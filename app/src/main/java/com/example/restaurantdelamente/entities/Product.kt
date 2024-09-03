package com.example.restaurantdelamente.entities

import android.media.Image
import android.net.Uri
import android.os.Parcelable
import android.widget.TextView
import com.example.restaurantdelamente.R
import kotlinx.parcelize.Parcelize

@Parcelize
class Product (
    private var productId : String?="",
    private var productName : String?="",
    private var productAuthor : String?="",
    private var productDesc : String?="",
    private var productPrice : Int?=0,
    private var productUrlImage : String?
) : Parcelable {

    constructor() : this(null, null,null,null,null,null)

    fun getProductName() : String? {
          return this.productName
     }

    fun getProductId() : String? {
        return this.productId
    }

    fun getProductAuthor() : String? {
        return this.productAuthor
    }

    fun getProductPrice() : Int? {
        return this.productPrice
    }

    fun getProductDesc() : String? {
        return this.productDesc
    }

    fun getProductUrlImage() : String? {
        return this.productUrlImage
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "productId" to productId,
            "productName" to productName,
            "productPrice" to productPrice,
            "productUrlImage" to productUrlImage,
            "productDesc" to productDesc,
            "productAuthor" to productAuthor
        )
    }

}