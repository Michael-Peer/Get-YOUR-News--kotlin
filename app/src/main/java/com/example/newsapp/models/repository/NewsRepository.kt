package com.example.newsapp.models.repository

import androidx.lifecycle.LiveData
import com.example.newsapp.models.News
import com.example.newsapp.models.NewsDatabase
import com.example.newsapp.network.NewsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NewsRepository (private val database: NewsDatabase){

    //get from dataBase
    val news: LiveData<News> = database.news.getNews()


    //insert to dataBase
    suspend fun refreshNews() {
        withContext(Dispatchers.IO) {
            val newsList = NewsApi.retrofitService.getNews("us", "934de238d46c4c0c88825b1c653a56d8").await()
            database.news.insertAll(newsList)
        }
    }

}