package com.nepaltech.news.depot

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.nepaltech.news.depot.api.NewsPost
import com.nepaltech.news.depot.model.Constants.NEWS_AUTHOR
import com.nepaltech.news.depot.model.Constants.NEWS_CONTENT
import com.nepaltech.news.depot.model.Constants.NEWS_DESCRIPTION
import com.nepaltech.news.depot.model.Constants.NEWS_IMAGE_URL
import com.nepaltech.news.depot.model.Constants.NEWS_PUBLICATION_TIME
import com.nepaltech.news.depot.model.Constants.NEWS_SOURCE
import com.nepaltech.news.depot.model.Constants.NEWS_SOURCE_ID
import com.nepaltech.news.depot.model.Constants.NEWS_TITLE
import com.nepaltech.news.depot.model.Constants.NEWS_URL
import com.nepaltech.news.depot.model.MainViewModel
import java.util.*


class ReadNewsActivity : AppCompatActivity() {

    private lateinit var newsWebView: WebView
    private lateinit var viewModel: MainViewModel
    private lateinit var newsData: ArrayList<NewsPost>

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_news)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        newsWebView = findViewById(R.id.news_webview)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        //loading data into list
        newsData = ArrayList(1)
        val newsUrl = intent.getStringExtra(NEWS_URL)
        val newsContent =
            intent.getStringExtra(NEWS_CONTENT) + ". get paid version to hear full news. "
        val newsSource =
            newsData.add(
                NewsPost(
                    NewsPost.Source(
                        sourceName = intent.getStringExtra(NEWS_SOURCE), id = intent.getStringExtra(
                            NEWS_SOURCE_ID
                        )
                    ),
                    intent.getStringExtra(NEWS_TITLE)!!,
                    intent.getStringExtra(NEWS_AUTHOR),
                    intent.getStringExtra(NEWS_DESCRIPTION),
                    newsUrl,
                    intent.getStringExtra(NEWS_IMAGE_URL),
                    intent.getSerializableExtra(NEWS_PUBLICATION_TIME) as Date,
                    newsContent
                )
            )

        // Webview
        newsWebView.apply {
            settings.apply {
                domStorageEnabled = true
                loadsImagesAutomatically = true
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                javaScriptEnabled = true
            }
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
        }


        if (newsUrl != null) {
            newsWebView.loadUrl(newsUrl)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item_readnewsactivity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.share_news -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Hey, checkout this news : " + newsData[0].newsUrl
                )
                intent.type = "text/plain"
                startActivity(Intent.createChooser(intent, "Share with :"))
                return true
            }

            R.id.save_news -> {
                this.let { viewModel.insertNews(newsData[0]) }
                Toast.makeText(this, "News saved!", Toast.LENGTH_SHORT)
                    .show()
            }

            R.id.browse_news -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newsData[0].newsUrl))
                startActivity(intent)
            }
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}