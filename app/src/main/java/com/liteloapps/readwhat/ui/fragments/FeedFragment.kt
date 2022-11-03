package com.liteloapps.readwhat.ui.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liteloapps.readwhat.R
import com.liteloapps.readwhat.adapters.NewsAdapter
import com.liteloapps.readwhat.databinding.FragmentFeedBinding
import com.liteloapps.readwhat.ui.MainActivity
import com.liteloapps.readwhat.utils.Constants.Companion.COUNTRY_CODE
import com.liteloapps.readwhat.utils.Constants.Companion.PAGE_SIZE
import com.liteloapps.readwhat.utils.Resource
import com.liteloapps.readwhat.viewModel.NewsViewModel

class FeedFragment : Fragment(R.layout.fragment_feed) {

    var TAG = "FeedFragment"

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel
        setUpRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
                Log.d("FeedFragment", "From Feed fragment----!!!")
            }
            findNavController().navigate(
                R.id.action_feedFragment_to_articleFragment,
                bundle
            )
        }

        viewModel.currentNews.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    hideProgressbar()
                    response.data?.let { newsResponse ->
                        newsAdapter.submitList(newsResponse.articles.toList())

                        val totalPages = newsResponse.totalResults / PAGE_SIZE + 2
                        isLastPage = viewModel.newsPage == totalPages
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
        binding.paginationProgressBar.visibility =View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility =View.VISIBLE
        isLoading = true
    }

    // Handling Pagination -------------------------------------------------------------------------

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
            val isTotalMoreThanVisible = totalItemCount >= PAGE_SIZE;
            val isPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning
                    && isTotalMoreThanVisible && isScrolling

            if(isPaginate) {
                viewModel.getCurrentNews(COUNTRY_CODE)
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

        binding.rvBreakingNews.layoutManager = LinearLayoutManager(activity)
        binding.rvBreakingNews.setHasFixedSize(true)
        binding.rvBreakingNews.adapter = newsAdapter

        binding.rvBreakingNews.addOnScrollListener(this@FeedFragment.scrollListener)
    }
}