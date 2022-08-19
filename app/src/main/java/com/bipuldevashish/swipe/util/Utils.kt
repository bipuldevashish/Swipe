package com.bipuldevashish.swipe.util

import android.content.Context
import android.widget.ImageView
import android.widget.Toast
import com.bipuldevashish.swipe.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

object Utils {

    fun ImageView.loadImage(uri: String?) {
        val options = RequestOptions()
            .placeholder(R.drawable.loading)
            .circleCrop()
            .error(R.drawable.product)
        Glide.with(this.context)
            .setDefaultRequestOptions(options)
            .load(uri)
            .into(this)
    }

    fun show(message: String, context: Context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}