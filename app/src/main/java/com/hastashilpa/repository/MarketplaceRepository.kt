package com.hastashilpa.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.hastashilpa.model.Product
import kotlinx.coroutines.tasks.await
import java.util.UUID

class MarketplaceRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val productsCollection = firestore.collection("products")

    suspend fun uploadProduct(
        name: String, 
        price: Double, 
        category: String, 
        description: String, 
        imageUri: Uri?
    ) {
        val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
        
        var imageUrl = ""
        
        // 1. Upload Image to Firebase Storage
        imageUri?.let { uri ->
            val fileName = "prod_${System.currentTimeMillis()}.jpg"
            val storageRef = storage.reference.child("products/$userId/$fileName")
            
            // Fixed: Standard way to upload and get URL
            storageRef.putFile(uri).await()
            imageUrl = storageRef.downloadUrl.await().toString()
        }

        // 2. Save Product Data to Firestore
        val product = Product(
            id = productsCollection.document().id,
            name = name,
            price = price,
            category = category,
            description = description,
            imageUrl = imageUrl,
            artisanId = userId,
            timestamp = System.currentTimeMillis()
        )
        productsCollection.document(product.id).set(product).await()
    }

    suspend fun getTrendingProducts(): List<Product> {
        return try {
            productsCollection
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .await()
                .toObjects(Product::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
