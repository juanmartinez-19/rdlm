package com.example.restaurantdelamente.data

import android.content.ContentValues
import android.util.Log
import com.example.restaurantdelamente.entities.ItemProduct
import com.example.restaurantdelamente.entities.ItemProductWithDetails
import com.example.restaurantdelamente.entities.Product
import com.google.firebase.Firebase
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.lang.reflect.InvocationTargetException

class ItemProductRepository {

    private val db = Firebase.firestore

    suspend fun updateItemQuantity(productId : String?, newQuantity : Int?, newSubTotal : Int) : MyResult<Unit>  {
        return try {
            val querySnapshot = db.collection("cart")
                .whereEqualTo("productId", productId ?: "")
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents.first()
                document.reference.update("quantity",newQuantity).await()
                document.reference.update("subTotal",newSubTotal).await()

                println("Producto actualizado con éxito")

                MyResult.Success(Unit)

            } else {
                println("El producto no existe")
                MyResult.Failure(Exception("No document found with productId $productId."))
            }
        } catch (e : Exception) {
            println("El producto no existe")
            MyResult.Failure(Exception("No document found with productId $productId."))
        }
    }

    suspend fun removeItemFromCart(productId : String?): MyResult<MutableList<ItemProduct>> {
        return try {
            val querySnapshot = db.collection("cart")
                .whereEqualTo("productId", productId ?: "")
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents.first()
                    document.reference.delete().await()
                    val documents = db.collection("cart").get().await()

                val cartList = documents.toObjects(ItemProduct::class.java)

                println("Producto eliminado con éxito")

                MyResult.Success(cartList)

            } else {
                println("El producto no existe")
                MyResult.Failure(Exception("No document found with productId $productId."))
            }


        } catch (e: Exception) {
            Log.e("FirestoreError", "Exception thrown: ${e.message}", e)
            MyResult.Failure(e)
        }
    }

    suspend fun getProduct(productId: String?): MyResult<Product?> {
        return try {
            val querySnapshot = db.collection("products")
                .whereEqualTo("productId", productId ?: "")
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val documentSnapshot = querySnapshot.documents.first()
                val product = documentSnapshot.toObject(Product::class.java)

                if (product != null) {
                    MyResult.Success(product)
                } else {
                    MyResult.Failure(Exception("Document could not be converted to Product."))
                }
            } else {
                MyResult.Failure(Exception("No document found with productId $productId."))
            }
        } catch (e: Exception) {
            Log.e("FirestoreError", "Exception thrown: ${e.message}", e)
            MyResult.Failure(e)
        }
    }

    suspend fun getProductsInCart () : MyResult<MutableList<ItemProduct>> {
        var cartList : MutableList<ItemProduct>

        return try {
            val documents =  db.collection("cart")
                .get()
                .await()
            cartList = documents.toObjects(ItemProduct::class.java)

            return MyResult.Success(cartList)

        } catch (e : Exception) {
            MyResult.Failure(e)
        }

    }

    suspend fun addProductToCart (itemQuantity:Int?,subTotal:Int?,productId:String?) : MyResult<Unit> {
        return try {
            val itemProduct = ItemProduct(itemQuantity,subTotal, productId)

            db.collection("cart")
                .add(itemProduct)
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