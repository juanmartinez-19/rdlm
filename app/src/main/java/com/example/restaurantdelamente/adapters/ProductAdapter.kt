package com.example.restaurantdelamente.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.restaurantdelamente.R
import com.example.restaurantdelamente.entities.Product
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    var products : MutableList<Product>,
    var onClick : (Int) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductHolder>() {

    class ProductHolder(view : View) : RecyclerView.ViewHolder(view)
    {

        private var v : View
        init {
            this.v = view
        }

        fun setPtoductName(name : String)  {
            val txtTitle : TextView = v.findViewById(R.id.tvProductName)
            txtTitle.text = name
        }

        fun setProductPrice(price: Int) {
            val txtPrice: TextView = v.findViewById(R.id.etProductPrice)

            val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())

            val formattedPrice = numberFormat.format(price)

            txtPrice.text = "$$formattedPrice"
        }


        fun setProductImage(imageUri: String) {
            val imgProduct: ImageView = v.findViewById(R.id.ivProductImage)

            Glide.with(v.context)
                .load(imageUri)
                .into(imgProduct)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product,parent,false)
        return (ProductHolder(view))
    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun getSelectedProduct(position: Int):Product {
        return products[position]
    }

    fun submitList(newProducts: MutableList<Product>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        products[position].getProductName()?.let { holder.setPtoductName(it) }
        products[position].getProductPrice()?.let { holder.setProductPrice(it) }
        products[position].getProductUrlImage()?.let { holder.setProductImage(it) }
        holder.itemView.setOnClickListener() {
            onClick(position)
        }
    }

}