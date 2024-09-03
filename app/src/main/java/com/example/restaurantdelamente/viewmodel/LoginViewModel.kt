package com.example.restaurantdelamente.viewmodel

import android.view.View
import android.widget.Button
import androidx.lifecycle.ViewModel


class LoginViewModel : ViewModel() {

    lateinit var v : View
    lateinit var btnLogin : Button

    fun validateLogin(username: String, password: String) : String {
        var result : String = "OK"

        /*
        if (username.isEmpty()) {
           result =  "Enter your username to continue."
        } else if (password.isEmpty()) {
          result =  "Enter your password to continue."
        } else if (username.length < 5) {
           result =  "Username is too short."
        } else if (password.length < 5) {
          result ="Password is too short."
        }
        */


        return result;
    }

}