package com.hastashilpa.repository

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.google.firebase.firestore.FirebaseFirestore
import com.hastashilpa.model.Blueprint
import kotlinx.coroutines.tasks.await

class BlueprintRepository(private val context: Context) {
    private val firestore = FirebaseFirestore.getInstance()
    private val blueprintsCollection = firestore.collection("blueprints")

    suspend fun getBlueprints(): List<Blueprint> {
        return try {
            blueprintsCollection.get().await().toObjects(Blueprint::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun downloadBlueprintPdf(name: String, url: String) {
        if (url.isBlank() || !url.startsWith("http")) return

        try {
            // Sanitize filename: remove spaces and special characters that can cause download failure
            val sanitizedName = name.replace(Regex("[^a-zA-Z0-9]"), "_")
            val uri = Uri.parse(url)
            
            val request = DownloadManager.Request(uri)
                .setTitle("Downloading $name Guide")
                .setDescription("Saving your handicraft blueprint guide...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$sanitizedName.pdf")
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
