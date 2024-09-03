package com.example.restaurantdelamente.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.restaurantdelamente.R
import com.example.restaurantdelamente.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar

class LoginFragment : androidx.fragment.app.Fragment() {

    lateinit var v : View
    private lateinit var viewModel: LoginViewModel
    lateinit var btnLogin : Button
    lateinit var username : EditText
    lateinit var password : EditText
    lateinit var loginValidator : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_login, container, false)

        btnLogin = v.findViewById(R.id.btnLogin)
        username = v.findViewById(R.id.inputUsername)
        password = v.findViewById(R.id.inputPassword)

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()

        btnLogin.setOnClickListener {
            val username = username.text?.toString()
            val password = password.text?.toString()

            if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
                Snackbar.make(v, "Username or password cannot be empty", Snackbar.LENGTH_SHORT).show()
            } else {
                val loginValidator = viewModel.validateLogin(username, password)

                if (loginValidator != "OK") {
                    Snackbar.make(v, loginValidator, Snackbar.LENGTH_SHORT).show()
                } else {
                    val action = LoginFragmentDirections.actionLoginFragmentToHomeActivity()
                    findNavController().navigate(action)
                }

            }

        }

    }

}