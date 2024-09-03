package com.example.restaurantdelamente.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantdelamente.data.MyResult
import com.example.restaurantdelamente.entities.Product
import com.example.restaurantdelamente.data.ProductsRepository
import com.example.restaurantdelamente.data.ViewState
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> get() = _viewState
    var _productsList : MutableLiveData<MutableList<Product>?> = MutableLiveData()
    val getProductsUseCase : ProductsRepository = ProductsRepository()

    init {
        _viewState.value = ViewState.Idle
    }

    fun getProducts() {
        viewModelScope.launch {
            _viewState.value = ViewState.Loading
            when (val result = getProductsUseCase.getProductList()){
                is MyResult.Success -> {
                    if (result.data.isNotEmpty()) {
                        _productsList.value = result.data
                        _viewState.value = ViewState.Idle

                    } else {
                        _viewState.value = ViewState.Empty
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