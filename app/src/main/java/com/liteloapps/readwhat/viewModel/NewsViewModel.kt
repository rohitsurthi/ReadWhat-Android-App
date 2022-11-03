package com.liteloapps.readwhat.viewModel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liteloapps.readwhat.ReadWhatApplication
import com.liteloapps.readwhat.models.Article
import com.liteloapps.readwhat.models.NewsResponse
import com.liteloapps.readwhat.repository.NewsRepository
import com.liteloapps.readwhat.utils.Constants.Companion.COUNTRY_CODE
import com.liteloapps.readwhat.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(app:Application, val newsRepository: NewsRepository) : AndroidViewModel(app) {

    val currentNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var newsPage = 1
    var newsPageResponse: NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsPageResponse: NewsResponse? = null

    // Initializing with country_code
    init {
        getCurrentNews(COUNTRY_CODE)
    }

    // Making a safe network call to get current news with coroutine
    fun getCurrentNews(countryCode: String) = viewModelScope.launch {
        safeCurrentNewsCall(countryCode)
    }

    // Making a safe network call to get searched news with coroutine
    fun searchNews(searchQuery: String) = viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    }

    // Handling network response for current news
    private fun handleCurrentNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse> {
 
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->

                newsPage++
                if(newsPageResponse == null) {
                    newsPageResponse = resultResponse
                } else {
                    val previousPageArticles = newsPageResponse?.articles
                    val newArticles = resultResponse.articles
                    previousPageArticles?.addAll(newArticles)
                }
                return Resource.Success(newsPageResponse?: resultResponse)
            }
        }

        return Resource.Error(response.message())
    }

    // Handling network response for searched news
    private fun handleSearchNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse> {

        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->

                searchNewsPage++
                if(searchNewsPageResponse == null) {
                    searchNewsPageResponse = resultResponse
                } else {
                    val previousPageArticles = searchNewsPageResponse?.articles
                    val newArticles = resultResponse.articles
                    previousPageArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsPageResponse?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }


    // Getting the saved article from the RoomDataBase
    fun getSavedArticle() = newsRepository.getSavedArticles()

    // Inserting or Updating an Article in RoomDataBase
    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    // deleting article from RoomDataBase
    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    // Safe network call with try-catch and internet check for current news
    private suspend fun safeCurrentNewsCall(countryCode: String) {
        currentNews.postValue(Resource.Loading())
        try {
            if(doesUserHasInternet()) {
                val response = newsRepository.getCurrentNews(countryCode, newsPage)
                currentNews.postValue(handleCurrentNewsResponse(response))
            } else {
                currentNews.postValue(Resource.Error("Internet is not connected!"))
            }
        } catch(t: Throwable){
            when(t) {
                is IOException -> currentNews.postValue(Resource.Error("Network error"))
                else -> currentNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    // Safe network call with try-catch and internet check for searched news
    private suspend fun safeSearchNewsCall(searchQuery: String) {
        searchNews.postValue(Resource.Loading())
        try {
            if(doesUserHasInternet()) {
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            } else {
                searchNews.postValue(Resource.Error("Internet is not connected!"))
            }
        } catch(t: Throwable){
            when(t) {
                is IOException -> searchNews.postValue(Resource.Error("Network error"))
                else -> searchNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }



    // Checking if user has internet or not
    private fun doesUserHasInternet(): Boolean {

        val connectivityManager = getApplication<ReadWhatApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> return false
            }

        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }

            }
        }

        return false
    }
}