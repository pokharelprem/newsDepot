package com.nepaltech.news.depot.api

class NewsPostRepository(private val newsApi: NewsApi) {


    private fun unpackPosts(response: NewsApi.TotalNews): List<NewsPost> {
        // XXX Write me.
        return response.data.newsList
    }

    suspend fun getTopHeadlines(subreddit: String): List<NewsPost> {
        // XXX Write me.
        return unpackPosts(newsApi.getTopHeadlines("en", ""))
    }

}
