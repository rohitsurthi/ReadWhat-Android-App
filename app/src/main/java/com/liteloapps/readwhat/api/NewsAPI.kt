package com.liteloapps.readwhat.api

import com.liteloapps.readwhat.models.NewsResponse
import com.liteloapps.readwhat.utils.Constants.Companion.API_KEY
import com.liteloapps.readwhat.utils.Constants.Companion.COUNTRY_CODE
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {

    @GET("v2/top-headlines")
    suspend fun getCurrentNews (

        @Query("country")
        countryCode: String = COUNTRY_CODE,
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY

    ): Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchNews (

        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY

    ): Response<NewsResponse>

}