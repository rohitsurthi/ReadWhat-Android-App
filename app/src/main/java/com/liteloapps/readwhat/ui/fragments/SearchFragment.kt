package com.liteloapps.readwhat.ui.fragments
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liteloapps.readwhat.R
import com.liteloapps.readwhat.adapters.NewsAdapter
import com.liteloapps.readwhat.databinding.FragmentFeedBinding
import com.liteloapps.readwhat.databinding.FragmentSearchBinding
import com.liteloapps.readwhat.ui.MainActivity
import com.liteloapps.readwhat.utils.Constants
import com.liteloapps.readwhat.utils.Constants.Companion.SEARCH_TEXT_DELAY
import com.liteloapps.readwhat.utils.Resource
import com.liteloapps.readwhat.viewModel.NewsViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment(R.layout.fragment_search) {

    var TAG = "SearchFragment"

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel
        setUpRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
                Log.d(TAG, "From Search Fragment")
            }
            findNavController().navigate(
                R.id.action_searchFragment_to_articleFragment,
                bundle
            )
        }

        var job: Job? = null
        binding.etSearch.addTextChangedListener { searchText ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_TEXT_DELAY)
                searchText?.let {
                    if(searchText.toString().isNotEmpty()) {
                        viewModel.searchNews(searchText.toString())
                    }
                }
            }
        }

        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    hideProgressbar()
                    response.data?.let { newsResponse ->
                        newsAdapter.submitList(newsResponse.articles.toList())

                        val totalPages = newsResponse.totalResults / Constants.PAGE_SIZE + 2
                        isLastPage = viewModel.searchNewsPage == totalPages

                        if(isLastPage) {
                            binding.rvSearchNews.setPadding(0,0,0,0)
                        }
                    }
                }

                is Resource.Error -> {
                    hideProgressbar()
                    response.message?.let { message ->
                        Log.d(TAG, "this happened : $message")
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        } )

    }

    private fun hideProgressbar() {
        binding.searchProgressBar.visibility =View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.searchProgressBar.visibility =View.VISIBLE
        isLoading = true
    }


    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object: RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)



            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.PAGE_SIZE;
            val isPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning
                    && isTotalMoreThanVisible && isScrolling

            if(isPaginate) {
                viewModel.searchNews(binding.etSearch.text.toString())
                isScrolling = false
            }else {
                // change in UI
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()

        binding.rvSearchNews.layoutManager = LinearLayoutManager(activity)
        binding.rvSearchNews.setHasFixedSize(true)
        binding.rvSearchNews.adapter = newsAdapter

        binding.rvSearchNews.addOnScrollListener(this@SearchFragment.scrollListener)
    }
}