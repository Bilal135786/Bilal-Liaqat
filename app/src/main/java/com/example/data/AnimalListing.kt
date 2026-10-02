package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "animal_listings")
data class AnimalListing(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String, // Cow, Goat, Lamb, Chicken, Birds, Camel, Buffalo
    val breed: String,
    val price: Long,
    val city: String,
    val description: String,
    val imageResName: String,
    val imageUri: String? = null,
    val additionalPhotos: String = "", // Separated by "||"
    val isFeatured: Boolean = false,
    val isNew: Boolean = false,
    val viewsCount: Int = 0,
    val postedTimeText: String = "14 hours ago",
    val sellerName: String = "Zeeshan Zamurd",
    val sellerPhone: String = "03038692236",
    val isUserListing: Boolean = false,
    val isSold: Boolean = false,
    val isFavorite: Boolean = false,
    val ageText: String = "1.5 Years",
    val teethCount: String = "2 Danda (2 Teeth)",
    val weightKg: String = "45 kg",
    val milkCapacity: String = "2-3 Litres/day",
    val isVaccinated: Boolean = true,
    val isFlaggedIllegal: Boolean = false,
    val flagReason: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun getAllPhotos(): List<String> {
        val list = mutableListOf<String>()
        val main = if (!imageUri.isNullOrBlank()) imageUri else "res:$imageResName"
        list.add(main)

        if (additionalPhotos.isNotBlank()) {
            val extras = additionalPhotos.split("||").filter { it.isNotBlank() }
            list.addAll(extras)
        }
        return list
    }
}
