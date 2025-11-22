package com.example.restyle.data.repository

import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class StorageRepository(private val context: Context) {

    private val storage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    private val storageRef: StorageReference by lazy {
        storage.reference
    }

    /**
     * Upload foto ke Firebase Storage
     * @param imageUri URI foto dari device
     * @param userId ID user yang upload
     * @param category Kategori foto (Resell/Donate/Recycle)
     * @return Flow yang emit progress dan download URL
     */
    fun uploadPhoto(
        imageUri: Uri,
        userId: String,
        category: String
    ): Flow<UploadResult> = callbackFlow {
        try {
            // Generate unique filename
            val fileName = "${UUID.randomUUID()}.jpg"
            val photoRef = storageRef
                .child("photos")
                .child(userId)
                .child(category)
                .child(fileName)

            // Start upload
            val uploadTask = photoRef.putFile(imageUri)

            // Listen to upload progress
            uploadTask.addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                trySend(UploadResult.Progress(progress))
            }

            // Wait for upload to complete
            uploadTask.await()

            // Get download URL
            val downloadUrl = photoRef.downloadUrl.await()

            // Send success result
            trySend(UploadResult.Success(downloadUrl.toString()))
            close()

        } catch (e: Exception) {
            trySend(UploadResult.Error(e.message ?: "Upload failed"))
            close(e)
        }

        awaitClose { }
    }

    suspend fun deletePhoto(photoUrl: String): Result<Unit> {
        return try {
            val photoRef = storage.getReferenceFromUrl(photoUrl)
            photoRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Sealed class untuk hasil upload
sealed class UploadResult {
    data class Progress(val progress: Int) : UploadResult()
    data class Success(val downloadUrl: String) : UploadResult()
    data class Error(val message: String) : UploadResult()
}