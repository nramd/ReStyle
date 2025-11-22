package com.example.restyle.data.repository

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.example.restyle.data.model.Photo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PhotoRepository(private val context: Context? = null) {

    private val firestore: FirebaseFirestore by lazy {
        if (FirebaseApp.getApps(context ?: throw IllegalStateException("Context required")).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }
        FirebaseFirestore.getInstance()
    }

    private val photosCollection by lazy {
        firestore.collection("photos")
    }

    suspend fun savePhoto(photo: Photo): Result<String> {
        return try {
            val documentRef = photosCollection.add(photo).await()
            Result.success(documentRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Ambil semua foto
    fun getAllPhotos(): Flow<List<Photo>> = callbackFlow {
        val listener = photosCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val photos = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Photo::class.java)
                } ?: emptyList()

                trySend(photos)
            }

        awaitClose { listener.remove() }
    }

    // Ambil foto berdasarkan kategori
    fun getPhotosByCategory(category: String): Flow<List<Photo>> = callbackFlow {
        val listener = photosCollection
            .whereEqualTo("category", category)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val photos = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Photo::class.java)
                } ?: emptyList()

                trySend(photos)
            }

        awaitClose { listener.remove() }
    }

    // Ambil foto by userId
    fun getPhotosByUser(userId: String): Flow<List<Photo>> = callbackFlow {
        val listener = photosCollection
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val photos = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Photo::class.java)
                } ?: emptyList()

                trySend(photos)
            }

        awaitClose { listener.remove() }
    }

    // Ambil foto berdasarkan ID
    suspend fun getPhotoById(photoId: String): Result<Photo> {
        return try {
            val document = photosCollection.document(photoId).get().await()
            val photo = document.toObject(Photo::class.java)
            if (photo != null) {
                Result.success(photo)
            } else {
                Result.failure(Exception("Photo not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update foto
    suspend fun updatePhoto(photoId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            photosCollection.document(photoId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Delete foto
    suspend fun deletePhoto(photoId: String): Result<Unit> {
        return try {
            photosCollection.document(photoId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}