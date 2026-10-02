package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey
    val id: Long = 1,
    val fullName: String = "Zeeshan Zamurd",
    val handle: String = "@zeeshanzamurd",
    val phone: String = "03038692236",
    val email: String = "zeeshan.zamurd@gmail.com",
    val city: String = "Lahore, Punjab",
    val memberSince: String = "Member since March 2024",
    val isVerified: Boolean = true,
    val rating: Float = 4.9f,
    val reviewCount: Int = 32,
    val followersCount: Int = 412,
    val followingCount: Int = 54,
    val bio: String = "Certified livestock breeder & dairy farmer in Lahore. Top quality Sahiwal cows, Makhi Cheeni goats, and Nili Ravi buffaloes.",
    val avatarResName: String = "goat_cheeni"
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val nameUrdu: String,
    val iconResName: String,
    val count: Int = 0,
    val displayOrder: Int = 0
)

@Entity(tableName = "buyer_inquiries")
data class BuyerInquiry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val listingId: Long,
    val listingTitle: String,
    val buyerName: String,
    val buyerPhone: String,
    val offerPrice: Long,
    val message: String,
    val status: String = "PENDING", // PENDING, ACCEPTED, REJECTED, COMPLETED
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "seller_reviews")
data class SellerReview(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sellerPhone: String,
    val reviewerName: String,
    val rating: Int = 5,
    val comment: String,
    val timeAgo: String = "Recent",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val query: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val listingId: Long,
    val listingTitle: String,
    val buyerName: String,
    val buyerPhone: String,
    val tokenAmount: Long,
    val deliveryOption: String = "Mandi Self Pickup",
    val status: String = "CONFIRMED", // CONFIRMED, IN_TRANSIT, DELIVERED, CANCELLED
    val timestamp: Long = System.currentTimeMillis()
)
