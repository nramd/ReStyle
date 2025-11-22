package com.example.restyle.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Model data untuk user aplikasi ReStyle.
 * 
 * @property id Unique identifier untuk user
 * @property name Nama lengkap user
 * @property email Email address user
 * @property profileImageUrl URL foto profil user
 * @property phoneNumber Nomor telepon user
 * @property address Alamat user
 * @property impactPoints Total impact points yang dikumpulkan user
 * @property itemsCollection Total items dalam koleksi user
 * @property itemsSold Total items yang berhasil dijual
 * @property itemsDonated Total items yang berhasil didonasikan
 * @property itemsRecycled Total items yang berhasil di-recycle
 * @property createdAt Timestamp saat user register
 * @property lastLogin Timestamp saat user terakhir login
 */
@Parcelize
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val profileImageUrl: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val impactPoints: Int = 0,
    val itemsCollection: Int = 0,
    val itemsSold: Int = 0,
    val itemsDonated: Int = 0,
    val itemsRecycled: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLogin: Long = System.currentTimeMillis()
) : Parcelable

/**
 * Extension function untuk menghitung total items yang sudah diproses
 */
fun User.getTotalProcessedItems(): Int {
    return itemsSold + itemsDonated + itemsRecycled
}

/**
 * Extension function untuk menghitung total CO2 yang berhasil dikurangi
 * Asumsi: setiap item = 2.5kg CO2 yang dikurangi
 */
fun User.getCO2Reduction(): Double {
    return getTotalProcessedItems() * 2.5
}

/**
 * Extension function untuk mendapatkan level user berdasarkan impact points
 */
fun User.getUserLevel(): UserLevel {
    return when {
        impactPoints < 1000 -> UserLevel.BEGINNER
        impactPoints < 5000 -> UserLevel.INTERMEDIATE
        impactPoints < 10000 -> UserLevel.ADVANCED
        else -> UserLevel.EXPERT
    }
}

/**
 * Enum untuk level user
 */
enum class UserLevel(val displayName: String, val emoji: String, val minPoints: Int) {
    BEGINNER("Eco Beginner", "🌱", 0),
    INTERMEDIATE("Eco Warrior", "🌿", 1000),
    ADVANCED("Eco Champion", "🌳", 5000),
    EXPERT("Eco Master", "🌎", 10000)
}
