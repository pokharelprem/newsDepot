package com.nepaltech.news.depot

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nepaltech.news.depot.adapter.CustomAdapter
import com.nepaltech.news.depot.api.NewsPost
import com.nepaltech.news.depot.model.Constants.NEWS_CONTENT
import com.nepaltech.news.depot.model.Constants.NEWS_DESCRIPTION
import com.nepaltech.news.depot.model.Constants.NEWS_IMAGE_URL
import com.nepaltech.news.depot.model.Constants.NEWS_PUBLICATION_TIME
import com.nepaltech.news.depot.model.Constants.NEWS_SOURCE
import com.nepaltech.news.depot.model.Constants.NEWS_TITLE
import com.nepaltech.news.depot.model.Constants.NEWS_URL
import com.nepaltech.news.depot.model.MainViewModel

class SavedNewsActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: MainViewModel
    private lateinit var newsData: MutableList<NewsPost>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_news)

        recyclerView = findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        newsData = mutableListOf()

        val adapter = CustomAdapter(newsData)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // Get Saved News
        viewModel.getSavedNews().observe(this) {
            val list = viewModel.getSavedNews().value
            newsData.clear()
            newsData.addAll(it)
            adapter.notifyDataSetChanged()
        }

        adapter.setOnItemClickListener(object : CustomAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {

                val intent = Intent(this@SavedNewsActivity, ReadNewsActivity::class.java).apply {
                    putExtra(NEWS_URL, newsData[position].newsUrl)
                    putExtra(NEWS_TITLE, newsData[position].newsTitle)
                    putExtra(NEWS_IMAGE_URL, newsData[position].newsImage)
                    putExtra(NEWS_DESCRIPTION, newsData[position].newsDescription)
                    putExtra(NEWS_SOURCE, newsData[position].source?.sourceName)
                    putExtra(NEWS_PUBLICATION_TIME, newsData[position].newsPublishedDate)
                    putExtra(NEWS_CONTENT, newsData[position].newsDescription)
                }

                startActivity(intent)
            }
        })

        adapter.setOnItemLongClickListener(object : CustomAdapter.OnItemLongClickListener {
            override fun onItemLongClick(position: Int) {
                // Delete saved news dialog
                recyclerView.findViewHolderForAdapterPosition(position)?.itemView?.setBackgroundColor(
                    getThemeColor(com.google.android.material.R.attr.colorPrimaryVariant)
                )

                val alertDialog = AlertDialog.Builder(this@SavedNewsActivity).apply {
                    setMessage("Delete this News?")
                    setTitle("Alert!")
                    setCancelable(false)

                    setPositiveButton(
                        "Yes"
                    ) { _, _ ->
                        this@SavedNewsActivity.let {
                            viewModel.deleteNews(
                                news = newsData[position]
                            )
                        }
                        adapter.notifyItemRemoved(position)
                        Toast.makeText(this@SavedNewsActivity, "Deleted!", Toast.LENGTH_SHORT).show()
                    }

                    setNegativeButton("No") { _, _ ->
                        recyclerView.findViewHolderForAdapterPosition(position)?.itemView?.setBackgroundColor(
                            getThemeColor(com.google.android.material.R.attr.colorPrimary)
                        )
                    }
                }.create()
                alertDialog.show()
            }
        })
        recyclerView.adapter = adapter
    }

    @ColorInt
    fun Context.getThemeColor(@AttrRes attribute: Int) = TypedValue().let {
        theme.resolveAttribute(attribute, it, true)
        it.data
    }

}