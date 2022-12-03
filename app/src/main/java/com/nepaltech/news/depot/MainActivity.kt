package com.nepaltech.news.depot

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.nepaltech.news.depot.adapter.FragmentAdapter
import com.nepaltech.news.depot.api.NewsPost
import com.nepaltech.news.depot.model.Constants.BUSINESS
import com.nepaltech.news.depot.model.Constants.ENTERTAINMENT
import com.nepaltech.news.depot.model.Constants.GENERAL
import com.nepaltech.news.depot.model.Constants.HEALTH
import com.nepaltech.news.depot.model.Constants.HOME
import com.nepaltech.news.depot.model.Constants.SCIENCE
import com.nepaltech.news.depot.model.Constants.SPORTS
import com.nepaltech.news.depot.model.Constants.TECHNOLOGY
import com.nepaltech.news.depot.model.Constants.TOTAL_NEWS_TAB
import com.nepaltech.news.depot.model.MainViewModel

class MainActivity : AppCompatActivity() {

    private val newsCategories = arrayOf(
        HOME, BUSINESS,
        ENTERTAINMENT, SCIENCE,
        SPORTS, TECHNOLOGY, HEALTH
    )

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var fragmentAdapter: FragmentAdapter
    private lateinit var shimmerLayout: ShimmerFrameLayout
    private var totalRequestCount = 0
    private lateinit var viewModel: MainViewModel

    companion object {
        var generalNews: ArrayList<NewsPost> = ArrayList()
        var entertainmentNews: MutableList<NewsPost> = mutableListOf()
        var businessNews: MutableList<NewsPost> = mutableListOf()
        var healthNews: MutableList<NewsPost> = mutableListOf()
        var scienceNews: MutableList<NewsPost> = mutableListOf()
        var sportsNews: MutableList<NewsPost> = mutableListOf()
        var techNews: MutableList<NewsPost> = mutableListOf()
        var apiRequestError = false
        var errorMessage = "error"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.view_pager)
        shimmerLayout = findViewById(R.id.shimmer_layout)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.setApiKey(getString(R.string.apiKey))
        viewModel.setCountryCode("us")

        if (!isNetworkAvailable(applicationContext)) {
            shimmerLayout.visibility = View.GONE
            val showError: TextView = findViewById(R.id.display_error)
            showError.text = getString(R.string.internet_warming)
            showError.visibility = View.VISIBLE
        }

        // Send request call for news data
        requestNews(GENERAL, generalNews)
        requestNews(BUSINESS, businessNews)
        requestNews(ENTERTAINMENT, entertainmentNews)
        requestNews(HEALTH, healthNews)
        requestNews(SCIENCE, scienceNews)
        requestNews(SPORTS, sportsNews)
        requestNews(TECHNOLOGY, techNews)
        fragmentAdapter = FragmentAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = fragmentAdapter
        viewPager.visibility = View.GONE

    }

    private fun requestNews(newsCategory: String, newsData: MutableList<NewsPost>) {
        viewModel.getNews("us", newsCategory)?.observe(this) {
            newsData.addAll(it)
            totalRequestCount += 1
            if (newsCategory == GENERAL) {
                shimmerLayout.stopShimmer()
                shimmerLayout.hideShimmer()
                shimmerLayout.visibility = View.GONE
                setViewPager()
            }
            if (totalRequestCount == TOTAL_NEWS_TAB) {
                viewPager.offscreenPageLimit = 7
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setViewPager() {
        if (!apiRequestError) {
            viewPager.visibility = View.VISIBLE
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = newsCategories[position]
                if (newsCategories[position] == HEALTH) {
                    tab.icon = resources.getDrawable(R.drawable.ic_health)
                } else if (newsCategories[position] == BUSINESS) {
                    tab.icon = resources.getDrawable(R.drawable.ic_business)
                } else if (newsCategories[position] == ENTERTAINMENT) {
                    tab.icon = resources.getDrawable(R.drawable.ic_entertainment)
                } else if (newsCategories[position] == HOME) {
                    tab.icon = resources.getDrawable(R.drawable.ic_home_black_24dp)
                } else if (newsCategories[position] == TECHNOLOGY) {
                    tab.icon = resources.getDrawable(R.drawable.ic_technology)
                } else if (newsCategories[position] == SCIENCE) {
                    tab.icon = resources.getDrawable(R.drawable.ic_science)
                } else if (newsCategories[position] == SPORTS) {
                    tab.icon = resources.getDrawable(R.drawable.ic_sports)
                }
            }.attach()
        } else {
            val showError: TextView = findViewById(R.id.display_error)
            showError.text = errorMessage
            showError.visibility = View.VISIBLE
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                    ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnectedOrConnecting) {
                return true
            }
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item_mainactivity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        intent = Intent(applicationContext, SavedNewsActivity::class.java)
        startActivity(intent)
        return super.onOptionsItemSelected(item)
    }
}