package com.example.restyle.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Photo(
    @DocumentId
    val id: String = "",
    val imageUrl: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "", // "Resell", "Donate", "Recycle"
    val price: Long = 0, // Harga untuk Resell (IDR)
    val pickupLocation: String = "", // Lokasi untuk Donate/Recycle
    val pickupLat: Double = 0.0, // Latitude
    val pickupLng: Double = 0.0, // Longitude
    val status: String = "active",
    @ServerTimestamp
    val timestamp: Date? = null,
    val userId: String = ""
)

data class PhotoUploadState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val uploadProgress: Int = 0
)