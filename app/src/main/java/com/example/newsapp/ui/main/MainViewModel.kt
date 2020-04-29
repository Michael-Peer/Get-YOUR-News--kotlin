package com.example.newsapp.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.newsapp.models.Article
import com.example.newsapp.models.News
import com.example.newsapp.models.getDatabase
import com.example.newsapp.models.repository.NewsRepository
import com.example.newsapp.network.NewsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val database = getDatabase(application)
    private val newsRepo = NewsRepository(database)

    private val _articles = MutableLiveData<List<Article>>()

    val articles: LiveData<List<Article>>
        get() = _articles

    private val _totalResults = MutableLiveData<Int>()

    val totalResults: LiveData<Int>
        get() = _totalResults

   val news = newsRepo.news


    init {
        coroutineScope.launch {
            newsRepo.refreshNews()
        }
//    getNewsFromApi()
//    refreshFromNetwork()
    }

     fun extractData(news: News) {
         Log.i("MainViewModel", "inside extractData function")
         Log.i("MainViewModel", "----------------------------")
         Log.i("MainViewModel", "$news")
         _totalResults.value = news.totalResults
         _articles.value = news.articles
         Log.i("MainViewModel", "----------------------------")
         Log.i("MainViewModel", "totalResults: $_totalResults")
         Log.i("MainViewModel", "----------------------------")
         Log.i("MainViewModel", "articles: $_articles")


    }






    fun getNewsFromApi() {
        Log.i("MainViewModel", "created")
        coroutineScope.launch {
            val getNewsDefered =
                NewsApi.retrofitService.getNews("us", "934de238d46c4c0c88825b1c653a56d8")
            try {
                val listResult = getNewsDefered.await()
                Log.i("MainViewModel", "$listResult")
            } catch (e: Exception) {
                Log.i("MainViewModel", "$e")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
