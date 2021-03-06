package com.example.koheiando.twittervolleysample.views

import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.koheiando.twittervolleysample.R
import com.example.koheiando.twittervolleysample.model.tweet.Tweet

class TweetViewHolder(private val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
    val tweetTv = view.findViewById<TextView>(R.id.tweet_text_tv)
    val userNameTv = view.findViewById<TextView>(R.id.user_name_tv)

    fun bind(tweet: Tweet) {
        Log.d("TweetViewHolder", "bind")

        tweetTv.text = tweet.text
        userNameTv.text = tweet.user.name
    }

    fun setOnClickCallback(cb: (v: View) -> Unit) {
        view.setOnClickListener { v -> cb(v) }
    }
}
