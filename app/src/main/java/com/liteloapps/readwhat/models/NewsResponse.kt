package com.liteloapps.readwhat.models

import androidx.lifecycle.MutableLiveData
import com.liteloapps.readwhat.models.Article

data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)