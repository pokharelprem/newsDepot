package com.nepaltech.news.depot.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.nepaltech.news.depot.R
import com.nepaltech.news.depot.api.NewsPost
import com.squareup.picasso.Picasso
import java.time.Instant
import java.time.ZoneId
import java.util.*


class CustomAdapter(private var newsList: List<NewsPost>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {


    private lateinit var context: Context
    private lateinit var mClickListener: OnItemClickListener
    private lateinit var mLongClickListener: OnItemLongClickListener

    init {
        this.notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(position: Int)
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        mLongClickListener = listener
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        context = parent.context
        return ViewHolder(view, mClickListener, mLongClickListener)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val newsData = newsList[holder.adapterPosition]

        holder.headLine.text = newsData.newsTitle
        val time: Date? = newsData.newsPublishedDate
        val imgUrl = newsData.newsImage

        if (imgUrl.isNullOrEmpty()) {
            Picasso.get()
                .load( R.drawable.samplenews)
                .fit()
                .centerCrop()
                .into(holder.image)
        } else {
            Picasso.get()
                .load(imgUrl)
                .fit()
                .centerCrop()
                .error(R.drawable.samplenews)
                .into(holder.image)
        }

        if (context.toString().contains("SavedNews")) {
            holder.newsPublicationTime.text = time.toString()
        } else {
            val currentTimeInHours = Instant.now().atZone(ZoneId.of("America/Chicago"))
            val calendar : Calendar = Calendar.getInstance()
            if (time != null) {
                calendar.time = time
            }
            val hoursDifference = currentTimeInHours.hour - calendar.get(Calendar.HOUR_OF_DAY)
            val hoursAgo = " $hoursDifference hour ago"
            holder.newsPublicationTime.text = hoursAgo
        }

    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    class ViewHolder(
        ItemView: View,
        listener: OnItemClickListener,
        listener2: OnItemLongClickListener
    ) : RecyclerView.ViewHolder(ItemView) {
        val image: ImageView = itemView.findViewById(R.id.img)
        val headLine: TextView = itemView.findViewById(R.id.news_title)
        val newsPublicationTime: TextView = itemView.findViewById(R.id.news_publication_time)

        init {
            ItemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }

            ItemView.setOnLongClickListener {
                listener2.onItemLongClick(adapterPosition)
                return@setOnLongClickListener true
            }
        }

    }

}
