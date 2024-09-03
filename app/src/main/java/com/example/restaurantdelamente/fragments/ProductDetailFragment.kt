package com.example.restaurantdelamente.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.restaurantdelamente.R
import com.example.restaurantdelamente.data.ViewState
import com.example.restaurantdelamente.entities.ItemProduct
import com.example.restaurantdelamente.entities.Product
import com.example.restaurantdelamente.viewmodel.ProductDetailViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID

class ProductDetailFragment : Fragment() {

    lateinit var v : View
    lateinit var mainImage : ImageView
    lateinit var productTitle : TextView
    lateinit var productPrice : TextView
    lateinit var productDesc : TextView
    lateinit var btnGoToCart : FloatingActionButton
    lateinit var btnAddToCart : Button
    lateinit var etItemQuantity : EditText
    lateinit var progressBar : ProgressBar
    lateinit var textViewError : TextView
    lateinit var product : Product

    companion object {
        fun newInstance() = ProductDetailFragment()
    }

    private lateinit var viewModel: ProductDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v= inflater.inflate(R.layout.fragment_product_detail, container, false)

        mainImage = v.findViewById(R.id.ivMainImage)
        productTitle = v.findViewById(R.id.tvTitle)
        productPrice = v.findViewById(R.id.tvPrice)
        productDesc = v.findViewById(R.id.tvDescription)
        btnGoToCart = v.findViewById(R.id.fabGoToCart)
        btnAddToCart = v.findViewById(R.id.btnAddToCart)
        etItemQuantity = v.findViewById(R.id.etQuantity)
        progressBar = v.findViewById(R.id.progressBar)
        textViewError = v.findViewById(R.id.tvError)
        product = ProductDetailFragmentArgs.fromBundle(requireArguments()).product

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productDesc.text = product.getProductDesc()

        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())

        val formattedPrice = numberFormat.format(product.getProductPrice())

        productPrice.text= "$$formattedPrice"

        productTitle.text = "${product.getProductAuthor()} - ${product.getProductName()}"

        Glide.with(v.context)
            .load(product.getProductUrlImage())
            .into(mainImage)

        btnGoToCart.setOnClickListener {
            val action = ProductDetailFragmentDirections.actionProductDetailFragmentToCartFragment()
            findNavController().navigate(action)
        }

        btnAddToCart.setOnClickListener {
            this.addProductToCart()
            this.observeState()
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProductDetailViewModel::class.java)


    }

    private fun addProductToCart() {
        val itemQuantityString = etItemQuantity.text.toString()

        if (itemQuantityString=="0"||itemQuantityString.isEmpty()) {
            this.showInvalidParameters()
        } else {

            val itemQuantity = itemQuantityString.toInt()
            val subTotal = (product.getProductPrice()?.times(itemQuantity))
            val productId = product.getProductId()

            viewModel.addProductToCart(itemQuantity,subTotal, productId)
        }
    }

    private fun observeState() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            when (viewState) {
                is ViewState.Loading -> {
                    this.showLoading()
                }
                is ViewState.Failure -> {
                    this.showError()
                }
                is ViewState.Idle -> {
                    this.hideLoading()
                }
                is ViewState.InvalidParameters -> {
                    this.showInvalidParameters()
                }
                else -> {
                    this.hideLoading()
                }
            }
        })
    }

    private fun showInvalidParameters() {
        progressBar.visibility = View.GONE
        textViewError.visibility = View.VISIBLE
        textViewError.text = "Invalid parameters"
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        textViewError.visibility = View.GONE
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
        textViewError.visibility = View.GONE
    }

    private fun showError() {
        progressBar.visibility = View.GONE
        textViewError.visibility = View.VISIBLE
        textViewError.text = "Error loading product"
    }

}

