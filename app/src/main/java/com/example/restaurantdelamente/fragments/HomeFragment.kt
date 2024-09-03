package com.example.restaurantdelamente.fragments

import android.content.ContentValues
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantdelamente.R
import com.example.restaurantdelamente.viewmodel.HomeViewModel
import com.example.restaurantdelamente.data.ViewState
import com.example.restaurantdelamente.adapters.ProductAdapter
import com.example.restaurantdelamente.entities.Product
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {

    //Vista
    lateinit var v : View
    //View Model
    private lateinit var viewModel: HomeViewModel
    //Recycler View
    lateinit var recyclerProducts : RecyclerView
    lateinit var productAdapter : ProductAdapter
    // State
    lateinit var progressBar : ProgressBar
    lateinit var textViewError : TextView
    lateinit var textViewTitle : TextView
    //Buttons
    lateinit var btnGoToCart : FloatingActionButton


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerProducts = v.findViewById(R.id.productsRecyclerView)
        progressBar = v.findViewById(R.id.progressBarLoading)
        textViewError = v.findViewById(R.id.textViewError)
        btnGoToCart = v.findViewById(R.id.fabGoToCart)
        textViewTitle = v.findViewById(R.id.tvHomeTitle)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        recyclerProducts.layoutManager = GridLayoutManager(context,2)
        productAdapter = ProductAdapter(mutableListOf()) {
            val product = productAdapter.getSelectedProduct(it)
            val action = HomeFragmentDirections.actionHomeFragmentToProductDetailFragment(product)
            findNavController().navigate(action)
        }

        recyclerProducts.adapter = productAdapter

        viewModel.getProducts()

        // Observar el LiveData
        this.observeProductsList()

        // Observar el estado de la vista
        this.observeState()

        btnGoToCart.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToCartFragment()
            findNavController().navigate(action)
        }


    }

    private fun observeProductsList() {
        viewModel._productsList.observe(viewLifecycleOwner, Observer { _productsList ->
            if (_productsList != null) {
                productAdapter.submitList(_productsList)
            }
        })
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
        recyclerProducts.visibility = View.GONE
        textViewError.visibility = View.VISIBLE
        textViewTitle.visibility = View.VISIBLE
        textViewError.text = "List of products is empty"
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerProducts.visibility = View.GONE
        textViewError.visibility = View.GONE
        textViewTitle.visibility = View.GONE
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
        recyclerProducts.visibility = View.VISIBLE
        textViewError.visibility = View.GONE
        textViewTitle.visibility = View.VISIBLE
    }

    private fun showError() {
        progressBar.visibility = View.GONE
        recyclerProducts.visibility = View.GONE
        textViewError.visibility = View.VISIBLE
        textViewTitle.visibility = View.VISIBLE
    }





}
