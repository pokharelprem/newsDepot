package com.nepaltech.news.depot.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nepaltech.news.depot.ReadNewsActivity
import com.nepaltech.news.depot.adapter.CustomAdapter
import com.nepaltech.news.depot.MainActivity
import com.nepaltech.news.depot.R
import com.nepaltech.news.depot.api.NewsPost
import com.nepaltech.news.depot.model.Constants.NEWS_CONTENT
import com.nepaltech.news.depot.model.Constants.NEWS_DESCRIPTION
import com.nepaltech.news.depot.model.Constants.NEWS_IMAGE_URL
import com.nepaltech.news.depot.model.Constants.NEWS_PUBLICATION_TIME
import com.nepaltech.news.depot.model.Constants.NEWS_SOURCE
import com.nepaltech.news.depot.model.Constants.NEWS_TITLE
import com.nepaltech.news.depot.model.Constants.NEWS_URL

class TechFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_tech, container, false)
        val newsData: MutableList<NewsPost> = MainActivity.techNews
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val adapter = CustomAdapter(newsData)
        recyclerView.adapter = adapter

        adapter.setOnItemClickListener(object : CustomAdapter.OnItemClickListener {

            override fun onItemClick(position: Int) {
                val intent = Intent(context, ReadNewsActivity::class.java).apply {
                    putExtra(NEWS_URL, newsData[position].newsUrl)
                    putExtra(NEWS_TITLE, newsData[position].newsTitle)
                    putExtra(NEWS_IMAGE_URL, newsData[position].newsImage)
                    putExtra(NEWS_DESCRIPTION, newsData[position].newsDescription)
                    putExtra(NEWS_SOURCE, newsData[position].source.sourceName)
                    putExtra(NEWS_PUBLICATION_TIME, newsData[position].newsPublishedDate)
                    putExtra(NEWS_CONTENT, newsData[position].content)
                }

                startActivity(intent)
            }
        })

        // Ignore
        adapter.setOnItemLongClickListener(object : CustomAdapter.OnItemLongClickListener {
            override fun onItemLongClick(position: Int) = Unit
        })

        return view
    }

}