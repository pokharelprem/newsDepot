package com.nepaltech.news.depot.api

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.text.clearSpans
import com.google.gson.annotations.SerializedName
import java.util.*

data class NewsPost(

    @SerializedName("source")
    private var source: Source,

    @SerializedName("title")
    private val newsTitle: SpannableString,

    @SerializedName("author")
    private val author: String,

    @SerializedName("description")
    private val newsDescription: SpannableString,

    @SerializedName("url")
    private val newsUrl: String,

    @SerializedName("urlToImage")
    private val newsImage: String,

    @SerializedName("publishedAt")
    private val newsPublishedDate: Date
) {
    //Added for Child JSON Object
    class Source {
        @SerializedName("name")
        var sourceName: String? = null

        @SerializedName("id")
        var id: String? = null
    }

    companion object {
        // NB: This only highlights the first match in a string
        private fun findAndSetSpan(fulltext: SpannableString, subtext: String): Boolean {
            if (subtext.isEmpty()) return true
            val i = fulltext.indexOf(subtext, ignoreCase = true)
            if (i == -1) return false
            fulltext.setSpan(
                ForegroundColorSpan(Color.CYAN), i, i + subtext.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return true
        }

        fun spannableStringsEqual(a: SpannableString?, b: SpannableString?): Boolean {
            if (a == null && b == null) return true
            if (a == null && b != null) return false
            if (a != null && b == null) return false
            val spA = a!!.getSpans(0, a.length, Any::class.java)
            val spB = b!!.getSpans(0, b.length, Any::class.java)
            return a.toString() == b.toString()
                    &&
                    spA.size == spB.size && spA.equals(spB)

        }
    }

    private fun clearSpan(str: SpannableString) {
        str.clearSpans()
        str.setSpan(
            ForegroundColorSpan(Color.GRAY), 0, 0,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    // clearSpans does not invalidate the textview
    // We have to assign a span to make sure text gets redrawn, so assign
    // a span that does nothing
    private fun removeAllCurrentSpans() {
        // Erase all spans
        clearSpan(newsTitle)
        clearSpan(newsDescription)
    }

    // Given a search string, look for it in the NewsPost.  If found,
    // highlight it and return true, otherwise return false.
    fun searchFor(searchTerm: String, subreddits: Boolean): Boolean {
        // XXX Write me, search both regular posts and subreddit listings
        removeAllCurrentSpans()
        val titleFound = findAndSetSpan(newsTitle, searchTerm);
        return titleFound
    }

    // NB: This changes the behavior of lists of RedditPosts.  I want posts fetched
    // at two different times to compare as equal.  By default, they will be different
    // objects with different hash codes.
    override fun equals(other: Any?): Boolean =
        if (other is NewsPost) {
            source == other.source && newsTitle == other.newsTitle
        } else {
            false
        }

    override fun hashCode(): Int {
        var result = source.hashCode()
        result = 31 * result + newsTitle.hashCode()
        result = 31 * result + author.hashCode()
        result = 31 * result + newsDescription.hashCode()
        result = 31 * result + newsUrl.hashCode()
        result = 31 * result + newsImage.hashCode()
        result = 31 * result + newsPublishedDate.hashCode()
        return result
    }
}
