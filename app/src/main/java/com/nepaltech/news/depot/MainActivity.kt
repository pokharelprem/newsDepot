package com.nepaltech.news.depot

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.nepaltech.news.depot.adapter.FragmentAdapter
import com.nepaltech.news.depot.api.NewsPost
import com.nepaltech.news.depot.databinding.ActivityMainBinding
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

    private lateinit var binding: ActivityMainBinding

    private val newsCategories = arrayOf(
        HOME, BUSINESS,
        ENTERTAINMENT, SCIENCE,
        SPORTS, TECHNOLOGY, HEALTH
    )

    //private var viewModel: MainViewModel by viewModels()

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

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set Action Bar
        //val toolbar: Toolbar = findViewById(R.id.toolbar)
        //setSupportActionBar(toolbar)



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
        viewModel.getNews("us", category = newsCategory).observe(this) {
            if(it!=null) {newsData.addAll(it)}
            totalRequestCount += 1

            // If main fragment loaded then attach the fragment to viewPager
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

    private fun setViewPager() {
        if (!apiRequestError) {
            viewPager.visibility = View.VISIBLE
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = newsCategories[position]
            }.attach()
        } else {
            val showError: TextView = findViewById(R.id.display_error)
            showError.text = errorMessage
            showError.visibility = View.VISIBLE
        }
    }

    // Check internet connection
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // For 29 api or above
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
            // For below 29 api
            if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnectedOrConnecting) {
                return true
            }
        }
        return false
    }
}