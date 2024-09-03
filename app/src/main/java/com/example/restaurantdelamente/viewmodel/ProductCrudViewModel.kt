package com.example.restaurantdelamente.viewmodel

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantdelamente.data.MyResult
import com.example.restaurantdelamente.data.ProductsRepository
import com.example.restaurantdelamente.data.ViewState
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class ProductCrudViewModel : ViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> get() = _viewState
    private val getProductsUseCase : ProductsRepository = ProductsRepository()
    private lateinit var downloadUri : Uri

    fun convertImageViewToByteArray(image: ImageView): ByteArray {
        val bitmap = (image.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        return data
    }

    fun addProduct (id:String?,name:String?,author:String?,desc:String?,price:Int?,imageByteArray: ByteArray) {

       if (name.isNullOrEmpty()||id.isNullOrEmpty()||imageByteArray.isEmpty()) {
           _viewState.value = ViewState.InvalidParameters
           Log.d("TEST", "Invalid parameters.")
       } else {
           viewModelScope.launch {

               _viewState.value = ViewState.Loading

               when (val result = getProductsUseCase.uploadImage(imageByteArray)) {
                   is MyResult.Success -> {
                           downloadUri = result.data
                           _viewState.value = ViewState.Idle
                   }
                   is MyResult.Failure -> {
                       _viewState.value = ViewState.Failure
                       Log.d("updloadImageError", _viewState.value.toString())
                   }
               }

               when (getProductsUseCase.addProduct(id,name,author,desc,price,downloadUri)) {
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
    }



}