package com.example.traveljournal.imageloader

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.traveljournal.R

class GlideImageLoader(private val context : Context) {
    fun loadImage(imageUri : String, imageView : ImageView) {
        Glide.with(context)
            .load(imageUri)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .centerCrop()
            .into(imageView)
    }
}