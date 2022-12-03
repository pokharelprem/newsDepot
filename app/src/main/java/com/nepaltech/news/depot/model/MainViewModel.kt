package com.nepaltech.news.depot.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nepaltech.news.depot.api.NewsApi
import com.nepaltech.news.depot.api.NewsPost
import com.nepaltech.news.depot.api.NewsPostRepository

class MainViewModel : ViewModel() {

    private val newsApi = NewsApi.create()
    private val newsPostRepository = NewsPostRepository(newsApi)
    private var newsLiveData: MutableLiveData<List<NewsPost>>? = null


    var savedNewsData = MutableLiveData<MutableList<NewsPost>>().apply {
        value = mutableListOf()
    }

    private var countryCode: String? = null
    private var apiKey: String? = null
    var fetchDone: MutableLiveData<Boolean> = MutableLiveData(false)

    fun getNews(countryCd: String?, category: String?): MutableLiveData<List<NewsPost>>? {

        newsLiveData = countryCd?.let {
            category?.let { it1 ->
                apiKey?.let { it2 ->
                    newsPostRepository.getTopHeadlines(it, it1, it2)
                }
            }
        }
        return newsLiveData
    }

    fun setApiKey(s: String) {
        apiKey = s
    }

    fun setCountryCode(countryCd: String) {
        countryCode = countryCd
    }

    fun getSavedNews(): MutableLiveData<MutableList<NewsPost>> {
        return savedNewsData
    }

    fun deleteNews(news: NewsPost) {
        savedNewsData.value?.remove(news)
    }

    fun insertNews(newsPost: NewsPost) {
        savedNewsData.value?.add(newsPost)
    }
}