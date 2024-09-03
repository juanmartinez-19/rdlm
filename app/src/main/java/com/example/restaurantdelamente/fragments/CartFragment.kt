package com.example.restaurantdelamente.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantdelamente.R
import com.example.restaurantdelamente.adapters.CartAdapter
import com.example.restaurantdelamente.data.ViewState
import com.example.restaurantdelamente.entities.ItemProduct
import com.example.restaurantdelamente.entities.ItemProductWithDetails
import com.example.restaurantdelamente.entities.Product
import com.example.restaurantdelamente.viewmodel.CartViewModel
import java.text.NumberFormat
import java.util.Locale

class CartFragment : Fragment() {

    private lateinit var viewModel: CartViewModel
    //Screen Vars
    private lateinit var recyclerCart: RecyclerView
    private lateinit var btnCheckout: Button
    lateinit var tvTotalValue : TextView

    private lateinit var cartAdapter: CartAdapter
    // State
    lateinit var progressBar : ProgressBar
    lateinit var textViewError : TextView
    lateinit var product : Product
    private val productMap = mutableMapOf<String?, Product>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_cart, container, false)

        recyclerCart = v.findViewById(R.id.recyclerCart)
        btnCheckout = v.findViewById(R.id.btnCheckout)
        progressBar = v.findViewById(R.id.progressBarLoading)
        textViewError = v.findViewById(R.id.textViewError)
        tvTotalValue = v.findViewById(R.id.tvTotalValue)


        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(CartViewModel::class.java)

        recyclerCart.layoutManager = LinearLayoutManager(context)

        cartAdapter = CartAdapter(
            mutableListOf(),
            { position ->
                val item = cartAdapter.getSelectedProduct(position)
                this.removeItemFromCart(item)
            },
            { position ->
                val item = cartAdapter.getSelectedProduct(position)
                val action = CartFragmentDirections.actionCartFragmentToProductDetailFragment(item.product)
                findNavController().navigate(action)
            },
            { position ->
                val item = cartAdapter.getSelectedProduct(position)
                this.increase(item)
            },
            { position ->
                val item = cartAdapter.getSelectedProduct(position)
                this.decrease(item)
            }
        )

        recyclerCart.adapter = cartAdapter

        viewModel.getProductsInCart()

        // Observar el LiveData
        this.observeCartListGet()

        // Observar el estado de la vista
       this.observeState()


        btnCheckout.setOnClickListener {
            println("Checkout")
        }
    }

    private fun increase(item : ItemProductWithDetails) {
        viewModel.increase(item.itemProduct.getProductId(),item.itemProduct.getQuantity(),item.itemProduct.getSubTotal())
        this.observeState()
        this.observeCartListRemove()
    }

    private fun decrease(item : ItemProductWithDetails) {
        viewModel.decrease(item.itemProduct.getProductId(),item.itemProduct.getQuantity(),item.itemProduct.getSubTotal())
        this.observeState()
        this.observeCartListRemove()
    }

    private fun removeItemFromCart(item : ItemProductWithDetails) {
        viewModel.removeItemFromCart(item.itemProduct.getProductId())
        this.observeState()
        this.observeCartListRemove()
    }

    private fun observeCartListRemove() {
        viewModel._cartList.observe(viewLifecycleOwner, Observer { _cartList ->
            this.updateItemProductsWithDetails()
        })
    }

    private fun observeCartListGet() {
        viewModel._cartList.observe(viewLifecycleOwner, Observer { _cartList ->
            if (_cartList != null) {

                for (item in _cartList) {
                    val productId = item.getProductId()
                    if (productId != null) {
                        if (!productMap.containsKey(productId)) {
                            viewModel.getProduct(productId)
                        }
                    }
                }
            }
        })

        viewModel.product.observe(viewLifecycleOwner, Observer { product ->
            if (product != null) {
                productMap[product.getProductId()] = product
                this.updateItemProductsWithDetails()
            }
        })
    }

    private fun updateItemProductsWithDetails() {
        val itemProductsWithDetails = mutableListOf<ItemProductWithDetails>()

        for (item in viewModel._cartList.value ?: emptyList()) {
            val productId = item.getProductId()
            val product = productId?.let { productMap[it] }
            if (product != null) {
                itemProductsWithDetails.add(ItemProductWithDetails(item, product))
            }
        }

        this.updateTotalValue()
        cartAdapter.submitList(itemProductsWithDetails)
    }

    private fun updateTotalValue() {
        var totalValue = 0

        viewModel._cartList.value?.forEach { item ->
            val itemSubTotal = item.getSubTotal()
            if (itemSubTotal != null) {
                totalValue += itemSubTotal
            }
        }

        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
        val formattedPrice = numberFormat.format(totalValue)

        tvTotalValue.text = "$$formattedPrice"
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
                is ViewState.Empty ->{
                    this.showEmpty()
                } else ->{
                    this.showError()
            }
            }
        })
    }

    private fun showEmpty() {
        progressBar.visibility = View.GONE
        recyclerCart.visibility = View.GONE
        textViewError.visibility = View.VISIBLE
        textViewError.text = "Cart is empty"
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerCart.visibility = View.GONE
        textViewError.visibility = View.GONE
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
        recyclerCart.visibility = View.VISIBLE
        textViewError.visibility = View.GONE
    }

    private fun showError() {
        progressBar.visibility = View.GONE
        recyclerCart.visibility = View.GONE
        textViewError.visibility = View.VISIBLE
    }

}
