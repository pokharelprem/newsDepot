package com.nepaltech.news.depot.api

class NewsPostRepository(private val newsApi: NewsApi) {


    private fun unpackPosts(response: AllNews): List<NewsPost> {
        // XXX Write me.
        return response.newsList
    }

    suspend fun getTopHeadlines(country: String, apiKey: String): List<NewsPost> {
        // XXX Write me.
        return unpackPosts(newsApi.getTopHeadlines(country, apiKey))
    }

}
