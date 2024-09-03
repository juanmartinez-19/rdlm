package com.example.restaurantdelamente.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantdelamente.data.ItemProductRepository
import com.example.restaurantdelamente.data.MyResult
import com.example.restaurantdelamente.data.ViewState
import com.example.restaurantdelamente.entities.ItemProduct
import com.example.restaurantdelamente.entities.ItemProductWithDetails
import com.example.restaurantdelamente.entities.Product
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> get() = _viewState
    var _cartList: MutableLiveData<MutableList<ItemProduct>?> = MutableLiveData()
    private val getItemProductUseCase : ItemProductRepository = ItemProductRepository()
    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?>
        get() = _product

    init {
        _viewState.value = ViewState.Idle
    }

    fun increase (productId : String?, itemQuantity : Int?, itemSubTotal : Int?) {
        _viewState.value = ViewState.Loading

        viewModelScope.launch {
            val newQuantity = itemQuantity?.plus(1)
            val itemPrice = (itemSubTotal!! /itemQuantity!!)
            val newSubTotal = itemPrice * newQuantity!!

            when (getItemProductUseCase.updateItemQuantity(productId,newQuantity,newSubTotal)) {
                is MyResult.Success -> {
                    val currentList = _cartList.value?.toMutableList() ?: mutableListOf()

                    val product = currentList.find { it.getProductId() == productId }
                    product?.let {
                        it.increaseQuantity()
                        it.setSubTotal(newSubTotal)
                    }

                    _cartList.value = currentList

                    _viewState.value = ViewState.Idle
                }

                is MyResult.Failure -> {
                    _viewState.value = ViewState.Failure
                    Log.d("TEST", _viewState.value.toString())
                }
            }
        }
    }

    fun decrease (productId : String?, itemQuantity : Int?, itemSubTotal : Int?) {
        _viewState.value = ViewState.Loading

        viewModelScope.launch {
            val newQuantity = itemQuantity?.minus(1)
            val itemPrice = (itemSubTotal!! /itemQuantity!!)
            val newSubTotal = itemPrice * newQuantity!!

            when (getItemProductUseCase.updateItemQuantity(productId,newQuantity,newSubTotal)) {
                is MyResult.Success -> {
                    val currentList = _cartList.value?.toMutableList() ?: mutableListOf()

                    val product = currentList.find { it.getProductId() == productId }
                    product?.let {
                        if (it.getQuantity()!! > 1) {
                            it.decreaseQuantity()
                            it.setSubTotal(newSubTotal)
                        }
                    }

                    _cartList.value = currentList

                    _viewState.value = ViewState.Idle
                }

                is MyResult.Failure -> {
                    _viewState.value = ViewState.Failure
                    Log.d("TEST", _viewState.value.toString())
                }
            }
        }
    }

    fun removeItemFromCart(productId : String?) {
        _viewState.value = ViewState.Loading

        viewModelScope.launch {
            when (val result = getItemProductUseCase.removeItemFromCart(productId)) {
                is MyResult.Success -> {
                    _cartList.value = result.data
                    _viewState.value = ViewState.Idle
                }

                is MyResult.Failure -> {
                    _viewState.value = ViewState.Failure
                    Log.d("TEST", _viewState.value.toString())
                }
            }
        }
    }

    fun getProduct(productId: String?) {
        _viewState.value = ViewState.Loading

        viewModelScope.launch {
            when (val result = getItemProductUseCase.getProduct(productId)) {
                is MyResult.Success -> {
                    _product.value = result.data
                    _viewState.value = ViewState.Idle
                }

                is MyResult.Failure -> {
                    _product.value = null
                    _viewState.value = ViewState.Failure
                    Log.d("TEST", _viewState.value.toString())
                }
            }
        }
    }

    fun getProductsInCart() {
        _viewState.value = ViewState.Loading

        viewModelScope.launch {
            when (val result = getItemProductUseCase.getProductsInCart()){
                is MyResult.Success -> {
                    if (result.data.isNullOrEmpty()) {
                        _viewState.value = ViewState.Empty
                    } else {
                        _cartList.value = result.data
                        _viewState.value = ViewState.Idle
                    }
                }
                is MyResult.Failure -> {
                    _viewState.value = ViewState.Failure
                    Log.d("TEST", _viewState.value.toString())
                }
            }
        }
    }


}