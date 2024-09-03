package com.example.restaurantdelamente.data

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.example.restaurantdelamente.entities.Product
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.lang.reflect.InvocationTargetException
import java.util.UUID


public class ProductsRepository {

    private val db = Firebase.firestore
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    val productImagesRef = storageRef.child("img").child("product").child(UUID.randomUUID().toString())

    suspend fun getProductList () : MyResult<MutableList<Product>> {
        var productsList : MutableList<Product> = mutableListOf()

        return try {
            val documents =  db.collection("products")
                .get()
                .await()
            productsList = documents.toObjects(Product::class.java)
            return MyResult.Success(productsList)
        } catch (e : Exception) {
            Log.e(ContentValues.TAG, "Exception thrown: ${e.message}")
            MyResult.Failure(e)
        }

    }

    suspend fun uploadImage (imageByteArray: ByteArray) : MyResult<Uri> {
        return try {
            var uploadTask = productImagesRef.putBytes(imageByteArray)
            uploadTask.await()

            val downloadUri = productImagesRef.downloadUrl.await()

            return MyResult.Success(downloadUri)
        } catch (e : Exception) {
            Log.e("FirestoreError", "Exception thrown: ${e.message}", e)
            MyResult.Failure(e)
        }
    }

    suspend fun addProduct (id:String?,name:String?,author:String?,desc:String?,price:Int?,downloadUri:Uri) : MyResult<Unit> {
        return try {
            val imageUrl = downloadUri.toString()

            val product = Product (id,name,author,desc,price,imageUrl)
            db.collection("products")
                .add(product)
                .await()
            return MyResult.Success(Unit)
        } catch (e: InvocationTargetException) {
            Log.e("FirestoreError", "InvocationTargetException: ${e.message}", e)
            MyResult.Failure(e)
        } catch (e: Exception) {
            Log.e("FirestoreError", "Exception thrown: ${e.message}", e)
            MyResult.Failure(e)
        }
    }




}