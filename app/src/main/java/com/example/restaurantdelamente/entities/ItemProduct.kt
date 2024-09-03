package com.example.restaurantdelamente.entities

class ItemProduct(
    private var quantity: Int?=0,
    private var subTotal: Int?=0,
    private var productId: String?=""
) {

    constructor() : this(null, null, null)

    fun setSubTotal(subTotal:Int?) {
        this.subTotal=subTotal
    }

    fun getQuantity(): Int? {
        return this.quantity
    }

    fun getSubTotal(): Int? {
        return this.subTotal
    }

    fun getProductId(): String? {
        return this.productId
    }

    fun decreaseQuantity() {
        this.quantity = this.quantity?.minus(1)
    }

    fun increaseQuantity() {
        this.quantity = this.quantity?.plus(1)
    }

}