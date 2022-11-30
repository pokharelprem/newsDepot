package com.nepaltech.news.depot.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nepaltech.news.depot.api.NewsApi
import com.nepaltech.news.depot.api.NewsPost
import com.nepaltech.news.depot.api.NewsPostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val newsApi = NewsApi.create()
    private val newsPostRepository = NewsPostRepository(newsApi)

    private val newsLiveData = MutableLiveData<List<NewsPost>>().apply {
        value = listOf()
    }

    private val newsList: List<NewsPost>? = null
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
            newsLiveData.postValue(countryCode?.let { apiKey?.let { it1 ->
                newsPostRepository.getTopHeadlines(it, it1)
            } })
        }
        fetchDone.postValue(true)
        Log.d(newsLiveData.value.toString(), "NewsList")
    }

    fun setApiKey(s: String) {
        apiKey = s
    }

    fun setCountryCode(countryCd: String) {
        countryCode = countryCd
    }
}