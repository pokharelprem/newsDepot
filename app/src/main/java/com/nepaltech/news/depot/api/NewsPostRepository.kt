package com.nepaltech.news.depot.api

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.nepaltech.news.depot.MainActivity
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar.getInstance

class NewsPostRepository(private val newsApi: NewsApi) {


    private fun unpackPosts(response: AllNews): List<NewsPost> {
        // XXX Write me.
        return response.newsList
    }

    suspend fun getTopHeadlines(country: String, apiKey: String): List<NewsPost> {
        // XXX Write me.
        return unpackPosts(newsApi.getTopHeadlines(country, apiKey))
    }

    fun getTopHeadlines(
        country: String,
        category: String,
        apiKey: String
    ): MutableLiveData<List<NewsPost>> {
        // XXX Write me.
        val newsList = MutableLiveData<List<NewsPost>>()

        val call = RetrofitHelper.getInstance().create(NewsApi::class.java)
            .getTopHeadlines(country, category, apiKey)

        call.enqueue(object :
            Callback<AllNews> {
            override fun onResponse(
                call: Call<AllNews>,
                response: Response<AllNews>
            ) {

                if (response.isSuccessful) {

                    val body = response.body()

                    if (body != null) {
                        val tempNewsList = mutableListOf<NewsPost>()

                        body.newsList.forEach {
                            tempNewsList.add(
                                NewsPost(
                                    it.source,
                                    it.newsTitle,
                                    it.author,
                                    it.newsDescription,
                                    it.newsUrl,
                                    it.newsImage,
                                    it.newsPublishedDate,
                                    it.content
                                )
                            )
                        }
                        newsList.value = tempNewsList
                    }

                } else {

                    val jsonObj: JSONObject?

                    try {
                        jsonObj = response.errorBody()?.string()?.let { JSONObject(it) }
                        if (jsonObj != null) {
                            MainActivity.apiRequestError = true
                            MainActivity.errorMessage = jsonObj.getString("message")
                            val tempNewsList = mutableListOf<NewsPost>()
                            newsList.value = tempNewsList
                        }
                    } catch (e: JSONException) {
                        Log.d("JSONException", "" + e.message)
                    }

                }
            }

            override fun onFailure(call: Call<AllNews>, t: Throwable) {

                MainActivity.apiRequestError = true
                MainActivity.errorMessage = t.localizedMessage as String
                Log.d("err_msg", "msg" + t.localizedMessage)
            }
        })
        return newsList
    }
}
