package com.liteloapps.readwhat.ui.fragments
import android.content.ClipData.Item
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.liteloapps.readwhat.R
import com.liteloapps.readwhat.adapters.NewsAdapter
import com.liteloapps.readwhat.databinding.FragmentSavedBinding
import com.liteloapps.readwhat.databinding.FragmentSearchBinding
import com.liteloapps.readwhat.ui.MainActivity
import com.liteloapps.readwhat.viewModel.NewsViewModel

class SavedFragment : Fragment(R.layout.fragment_saved) {


    var TAG = "SavedFragment"

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel
        setUpRecyclerView()


        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_savedFragment_to_articleFragment,
                bundle
            )
        }

        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val position = viewHolder.adapterPosition
                val article = newsAdapter.currentList[position]
                viewModel.deleteArticle(article)

                Snackbar.make(view, "Deleted the article", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        viewModel.saveArticle(article)
                    }
                    show()
                }
            }

        }
5
        ItemTouchHelper(itemTouchHelper).apply {
            attachToRecyclerView(binding.rvSavedNews)
        }

        viewModel.getSavedArticle().observe(viewLifecycleOwner, Observer { articles ->
            newsAdapter.submitList(articles)
        })
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()

        binding.rvSavedNews.layoutManager = LinearLayoutManager(activity)
        binding.rvSavedNews.setHasFixedSize(true)
        binding.rvSavedNews.adapter = newsAdapter
    }
}