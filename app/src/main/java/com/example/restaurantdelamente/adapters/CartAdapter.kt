package com.example.restaurantdelamente.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.restaurantdelamente.R
import com.example.restaurantdelamente.entities.ItemProductWithDetails
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    private val cartItems: MutableList<ItemProductWithDetails>,
    var removeItem : (Int) -> Unit,
    var navigate : (Int) -> Unit,
    var increase : (Int) -> Unit,
    var decrease : (Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var v : View
        init {
            this.v = view
        }

        fun setProductImage(imageUri: String) {
            val imgProduct: ImageView = v.findViewById(R.id.ivProductImage)

            Glide.with(v.context)
                .load(imageUri)
                .into(imgProduct)
        }

        fun setProductName(name : String)  {
            val txtTitle : TextView = v.findViewById(R.id.tvProductName)
            txtTitle.text = name
        }

        fun setProductSubtotal (subTotal : Int)  {
            val txtPrice: TextView = v.findViewById(R.id.tvProductSubTotal)

            val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())

            val formattedPrice = numberFormat.format(subTotal)

            txtPrice.text = "$$formattedPrice"
        }

        fun setProductQuantity (quantity : Int) {
            val txtQuantity : TextView = v.findViewById(R.id.tvProductQuantity)

            txtQuantity.text = quantity.toString()

        }

        fun getBtnRemove(): ImageButton {
            return v.findViewById(R.id.btnRemove)
        }

        fun getProductImageView(): ImageView {
            return v.findViewById(R.id.ivProductImage)
        }

        fun getProductNameTextView(): TextView {
            return v.findViewById(R.id.tvProductName)
        }

        fun getBtnIncrease() : ImageButton {
            return v.findViewById(R.id.btnIncrease)
        }

        fun getBtnDecrease() : ImageButton {
            return v.findViewById(R.id.btnDecrease)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart_product, parent, false)
        return CartViewHolder(view)
    }

    fun submitList(newItems: MutableList<ItemProductWithDetails>) {
        cartItems.clear()
        cartItems.addAll(newItems)
        notifyDataSetChanged()
    }

    fun getSelectedProduct(position: Int): ItemProductWithDetails {
        return cartItems[position]
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]

        item.product.getProductName()?.let { holder.setProductName(it) }
        item.product.getProductUrlImage()?.let { holder.setProductImage(it) }
        item.itemProduct.getSubTotal()?.let { holder.setProductSubtotal(it) }
        item.itemProduct.getQuantity()?.let { holder.setProductQuantity(it) }

        holder.getBtnRemove().setOnClickListener {
            removeItem(position)
        }
        holder.getBtnIncrease().setOnClickListener {
            increase(position)
        }
        holder.getBtnDecrease().setOnClickListener {
            decrease(position)
        }
        holder.getProductImageView().setOnClickListener{
            navigate(position)
        }
        holder.getProductNameTextView().setOnClickListener{
            navigate(position)
        }

     }

    override fun getItemCount(): Int = cartItems.size

}









