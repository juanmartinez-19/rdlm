package com.example.restaurantdelamente.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.restaurantdelamente.R
import com.example.restaurantdelamente.viewmodel.ProductCrudViewModel
import com.example.restaurantdelamente.data.ViewState
import java.io.ByteArrayOutputStream
import java.util.UUID

class ProductCrudFragment : Fragment() {

    lateinit var v : View
    private lateinit var viewModel: ProductCrudViewModel
    private val PICK_IMAGE_REQUEST = 1
    //Buttons
    lateinit var btnSave : Button
    lateinit var btnSelImg : Button
    //Inputs
    lateinit var productNameInput : EditText
    lateinit var authorInput : EditText
    lateinit var descInput : EditText
    lateinit var priceInput : EditText
    lateinit var image : ImageView
    //State
    lateinit var progressBar : ProgressBar
    lateinit var textViewError : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        v = inflater.inflate(R.layout.fragment_product_crud, container, false)

        btnSave = v.findViewById(R.id.btnSave)
        btnSelImg = v.findViewById(R.id.buttonSelectImage)
        productNameInput = v.findViewById(R.id.etProductName)
        authorInput = v.findViewById(R.id.etAuthor)
        descInput = v.findViewById(R.id.etDescription)
        priceInput = v.findViewById(R.id.etPrice)
        progressBar = v.findViewById(R.id.progressBarLoading2)
        textViewError = v.findViewById(R.id.textViewError2)
        image = v.findViewById(R.id.imageView)

        btnSelImg.setOnClickListener {
            openGallery()
        }


        progressBar.visibility = View.GONE
        textViewError.visibility = View.GONE

        return v
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            selectedImageUri?.let {
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, it)
                image.setImageBitmap(bitmap)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "Add Products"

        viewModel = ViewModelProvider(this).get(ProductCrudViewModel::class.java)

        btnSave.setOnClickListener {

            this.addProduct()
            //Observar el estado de la vista
            this.observeState()

        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun addProduct() {
        val name = productNameInput.text?.toString()
        val author = authorInput.text?.toString()
        val desc = descInput.text?.toString()
        val priceString = priceInput.text?.toString()
        val price = priceString?.toInt()
        val id = UUID.randomUUID().toString()
        val imageByteArray = viewModel.convertImageViewToByteArray(image)

        viewModel.addProduct(id,name,author,desc,price,imageByteArray)
    }

    private fun observeState() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            when (viewState) {
                is ViewState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    textViewError.visibility = View.GONE
                }
                is ViewState.Failure -> {
                    progressBar.visibility = View.GONE
                    textViewError.visibility = View.VISIBLE
                    textViewError.text = "Error loading product"
                }
                is ViewState.Idle -> {
                    progressBar.visibility = View.GONE
                    textViewError.visibility = View.GONE
                }
                is ViewState.InvalidParameters -> {
                    progressBar.visibility = View.GONE
                    textViewError.visibility = View.VISIBLE
                    textViewError.text = "Invalid parameters"
                }
                else -> {
                    progressBar.visibility = View.GONE
                    textViewError.visibility = View.GONE
                }
            }
        })
    }


}