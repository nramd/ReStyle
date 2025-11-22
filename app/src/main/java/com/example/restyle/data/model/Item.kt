package com.example.restyle.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Model data untuk item pakaian yang akan di-resell, donate, atau recycle.
 * 
 * @property id Unique identifier untuk item
 * @property title Judul/nama item
 * @property description Deskripsi detail item
 * @property category Kategori item (e.g., "Shirt", "Pants", "Dress")
 * @property condition Kondisi item (e.g., "New", "Like New", "Good", "Fair")
 * @property size Ukuran item (e.g., "S", "M", "L", "XL")
 * @property brand Brand/merek item
 * @property imageUrls List URL gambar item
 * @property price Harga item (untuk resell), null jika donate/recycle
 * @property type Tipe listing: "resell", "donate", atau "recycle"
 * @property userId ID user yang meng-upload item
 * @property createdAt Timestamp saat item di-create
 * @property updatedAt Timestamp saat item terakhir di-update
 * @property status Status item: "pending", "approved", "sold", "donated", "recycled"
 */
@Parcelize
data class Item(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: ItemCategory = ItemCategory.OTHER,
    val condition: ItemCondition = ItemCondition.GOOD,
    val size: String = "",
    val brand: String = "",
    val imageUrls: List<String> = emptyList(),
    val price: Double? = null,
    val type: ItemType = ItemType.RESELL,
    val userId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val status: ItemStatus = ItemStatus.PENDING
) : Parcelable

/**
 * Enum untuk kategori item pakaian
 */
enum class ItemCategory(val displayName: String) {
    SHIRT("Shirt"),
    PANTS("Pants"),
    DRESS("Dress"),
    JACKET("Jacket"),
    SHOES("Shoes"),
    ACCESSORIES("Accessories"),
    OTHER("Other")
}

/**
 * Enum untuk kondisi item
 */
enum class ItemCondition(val displayName: String, val description: String) {
    NEW("New", "Brand new with tags"),
    LIKE_NEW("Like New", "Worn once or twice, no visible flaws"),
    GOOD("Good", "Gently used, minor signs of wear"),
    FAIR("Fair", "Obvious signs of wear, but still functional"),
    WORN("Worn", "Heavy signs of wear")
}

/**
 * Enum untuk tipe listing
 */
enum class ItemType(val displayName: String, val emoji: String) {
    RESELL("Resell", "💰"),
    DONATE("Donate", "🎁"),
    RECYCLE("Recycle", "♻️")
}

/**
 * Enum untuk status item
 */
enum class ItemStatus(val displayName: String) {
    PENDING("Pending Review"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    SOLD("Sold"),
    DONATED("Donated"),
    RECYCLED("Recycled"),
    REMOVED("Removed")
}

/**
 * Data class untuk hasil analisis AI terhadap item
 * 
 * @property condition Kondisi item hasil analisis AI
 * @property estimatedPrice Estimasi harga berdasarkan kondisi dan kategori
 * @property category Kategori item hasil analisis
 * @property confidence Level confidence analisis (0.0 - 1.0)
 * @property suggestions Saran untuk meningkatkan nilai item
 */
data class AIAnalysis(
    val condition: ItemCondition,
    val estimatedPrice: Double,
    val category: ItemCategory,
    val confidence: Float,
    val suggestions: List<String> = emptyList()
)
