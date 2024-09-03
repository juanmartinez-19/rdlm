package com.example.restaurantdelamente.entities

class User (
    private var email : String,
    private var password : String,
    private var cart : MutableList<ItemProduct> = mutableListOf(),
    private var favProducts : MutableList<Product> = mutableListOf()
) {
}