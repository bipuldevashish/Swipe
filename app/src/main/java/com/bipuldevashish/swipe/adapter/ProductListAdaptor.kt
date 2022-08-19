package com.bipuldevashish.swipe.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bipuldevashish.swipe.R
import com.bipuldevashish.swipe.models.ProductItem
import com.bipuldevashish.swipe.util.Utils.loadImage

class ProductListAdaptor(var items: List<ProductItem>, val context: Context) :
    RecyclerView.Adapter<ProductListAdaptor.ProductListViewHolder>() {


    inner class ProductListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imageView = itemView.findViewById<ImageView>(R.id.circular_imageView)
        private val productName = itemView.findViewById<TextView>(R.id.tv_name)
        private val productType = itemView.findViewById<TextView>(R.id.tv_type)
        private val productPrice = itemView.findViewById<TextView>(R.id.tv_price)
        private val productTax = itemView.findViewById<TextView>(R.id.tv_tax)

        /**
         * Binds data with the ui
         */
        fun bind(item: ProductItem) {
            productName.text = item.product_name
            productType.text = item.product_type
            (context.getString(R.string.Rs) + item.price.toString()).also { productPrice.text = it }
            (context.getString(R.string.tax) +item.tax.toString()+context.getString(R.string.percent)).also { productTax.text = it }
            imageView.loadImage(item.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
        return ProductListViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_product_details, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {
        val item = items[position]
        (holder as? ProductListViewHolder)?.bind(item = item)
    }

    override fun getItemCount(): Int {
        return items.size
    }


}