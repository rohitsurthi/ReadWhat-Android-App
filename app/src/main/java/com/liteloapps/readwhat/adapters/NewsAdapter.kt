package com.liteloapps.readwhat.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.liteloapps.readwhat.R
import com.liteloapps.readwhat.models.Article

class NewsAdapter : androidx.recyclerview.widget.ListAdapter<Article, NewsAdapter.ArticleViewHolder>(DiffUtil()){

    val TAG = "NewsAdapter"

    class ArticleViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val title = view.findViewById<TextView>(R.id.news_view_title)
        val description = view.findViewById<TextView>(R.id.news_view_description)
        val source = view.findViewById<TextView>(R.id.news_view_source)
        val publistedAt = view.findViewById<TextView>(R.id.news_view_publisher)
        val bannerImage = view.findViewById<ImageView>(R.id.news_view_image)

        fun bind(item: Article) {
            title.text = item.title
            description.text = item.description
            source.text = item.source?.name
            publistedAt.text = item.publishedAt
            Glide.with(itemView.context).load(item.urlToImage).into(bannerImage)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.news_item_view_layout, parent, false)

        return ArticleViewHolder(view)
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(item)
        holder.itemView.apply {
            setOnClickListener {
                onItemClickListener?.let {
                    it(item)
                }
            }
        }

    }

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

    class DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<Article>(){

        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
}