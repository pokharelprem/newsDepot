package com.nepaltech.news.depot.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nepaltech.news.depot.ReadNewsActivity
import com.nepaltech.news.depot.SavedNewsActivity
import com.nepaltech.news.depot.api.NewsApi
import com.nepaltech.news.depot.api.NewsPost
import com.nepaltech.news.depot.api.NewsPostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val newsApi = NewsApi.create()
    private val newsPostRepository = NewsPostRepository(newsApi)

    private var newsLiveData = MutableLiveData<List<NewsPost>>().apply {
        value = listOf()
    }

    var savedNewsData = MutableLiveData<List<NewsPost>>().apply {

    }

    private var countryCode: String? = null
    private var apiKey: String? = null
    var fetchDone: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        netRefresh()
    }

    private fun netRefresh() {
        // XXX Write me.  This is where the network request is initiated.
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
        ) {
            // Update LiveData from IO dispatcher, use postValue
            newsLiveData.postValue(countryCode?.let {
                apiKey?.let { it1 ->
                    newsPostRepository.getTopHeadlines(it, it1)
                }
            })
        }
        fetchDone.postValue(true)
        Log.d(newsLiveData.value.toString(), "NewsList")
    }

    fun getNews(countryCd: String?, category: String?): MutableLiveData<List<NewsPost>> {

        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
        ) {
            // Update LiveData from IO dispatcher, use postValue
            newsLiveData.postValue(countryCd?.let {
                category?.let { it1 ->
                    apiKey?.let { it2 ->
                        newsPostRepository.getTopHeadlines(it, it1, it2)
                    }
                }
            })
        }
        fetchDone.postValue(true)
        Log.d(newsLiveData.value.toString(), "NewsList")
        return newsLiveData

    }

    fun setApiKey(s: String) {
        apiKey = s
    }

    fun setCountryCode(countryCd: String) {
        countryCode = countryCd
    }

    fun getSavedNewsFromDB(context: Context): LiveData<List<NewsPost>> {
        return savedNewsData
    }

    fun deleteNews(it: SavedNewsActivity, news: NewsPost) {
        savedNewsData.value?.minus(news)
    }

    fun insertNews(readNewsActivity: ReadNewsActivity, newsPost: NewsPost) {
        savedNewsData.value?.plus(newsPost)
    }
}