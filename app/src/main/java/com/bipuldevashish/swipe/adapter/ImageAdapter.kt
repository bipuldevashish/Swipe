package com.bipuldevashish.swipe.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bipuldevashish.swipe.R

class ImageAdapter(
    var context: Context,
    var items: List<Uri>
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_image_item, parent, false)
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = items[position]
        val bitmap = BitmapFactory.decodeStream(
            item.let { context.applicationContext.contentResolver!!.openInputStream(it) })
        (holder as? ImageViewHolder)?.bind(item = bitmap)
    }

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val imageView = view.findViewById<ImageView>(R.id.imageView)

        fun bind(item: Bitmap){
            imageView.setImageBitmap(item)
        }
    }

}