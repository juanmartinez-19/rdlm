package com.example.restaurantdelamente.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantdelamente.data.ItemProductRepository
import com.example.restaurantdelamente.data.MyResult
import com.example.restaurantdelamente.data.ProductsRepository
import com.example.restaurantdelamente.data.ViewState
import com.example.restaurantdelamente.entities.ItemProduct
import kotlinx.coroutines.launch

class ProductDetailViewModel : ViewModel() {

    private val getItemProductUseCase : ItemProductRepository = ItemProductRepository()
    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> get() = _viewState

    fun addProductToCart (itemQuantity:Int?,subTotal:Int?,productId:String?) {
        _viewState.value = ViewState.Loading

        if (itemQuantity==null||itemQuantity==0||subTotal==null||productId==null) {
            _viewState.value = ViewState.InvalidParameters
            Log.d("TEST", "Invalid parameters.")
        } else {
            viewModelScope.launch {
                when (val result = getItemProductUseCase.getProductsInCart()) {
                    is MyResult.Success -> {
                        val cartList = result.data
                        var productFound = false

                        cartList.forEach { item ->
                            if (item.getProductId() == productId) {
                                val newQuantity = item.getQuantity()?.plus(itemQuantity)
                                val itemPrice = (item.getSubTotal()!! /item.getQuantity()!!)
                                val newSubTotal = itemPrice * newQuantity!!
                                when (getItemProductUseCase.updateItemQuantity(productId, newQuantity, newSubTotal)) {
                                    is MyResult.Success -> {
                                        _viewState.value = ViewState.Idle
                                        Log.d("UpdateProductOK", "producto actualizado con exito")
                                    }
                                    is MyResult.Failure -> {
                                        _viewState.value = ViewState.Failure
                                        Log.d("UpdateProductError", _viewState.value.toString())
                                    }
                                }
                                productFound = true
                            }
                        }

                        if (!productFound) {
                            when (getItemProductUseCase.addProductToCart(itemQuantity,subTotal, productId)) {
                                is MyResult.Success -> {
                                    _viewState.value = ViewState.Idle
                                }
                                is MyResult.Failure -> {
                                    _viewState.value = ViewState.Failure
                                    Log.d("AddProductError", _viewState.value.toString())
                                }
                            }
                        }

                    }
                    is MyResult.Failure -> {
                        _viewState.value = ViewState.Failure
                        Log.d("AddProductError", _viewState.value.toString())
                    }
                }


            }

        }
    }


}

