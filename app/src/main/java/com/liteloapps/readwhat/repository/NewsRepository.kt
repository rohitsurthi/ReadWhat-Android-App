package com.liteloapps.readwhat.repository

import com.liteloapps.readwhat.api.RetroFitInstance
import com.liteloapps.readwhat.db.ArticleDatabase
import com.liteloapps.readwhat.models.Article

class NewsRepository (val db: ArticleDatabase) {

    suspend fun getCurrentNews(countryCode: String, pageNumber: Int) =
        RetroFitInstance.api.getCurrentNews(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetroFitInstance.api.searchNews(searchQuery, pageNumber)

    /*
    *  Room database operations to perform save article
    * */

    fun getSavedArticles() = db.getArticleDao().getAllArticles()

    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)

}