package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimalDao {
    @Query("SELECT * FROM animal_listings ORDER BY timestamp DESC")
    fun getAllListings(): Flow<List<AnimalListing>>

    @Query("SELECT * FROM animal_listings WHERE isFeatured = 1 ORDER BY timestamp DESC")
    fun getFeaturedListings(): Flow<List<AnimalListing>>

    @Query("SELECT * FROM animal_listings WHERE category = :category ORDER BY timestamp DESC")
    fun getListingsByCategory(category: String): Flow<List<AnimalListing>>

    @Query("SELECT * FROM animal_listings WHERE isUserListing = 1 ORDER BY timestamp DESC")
    fun getUserListings(): Flow<List<AnimalListing>>

    @Query("SELECT * FROM animal_listings WHERE id = :id")
    fun getListingById(id: Long): Flow<AnimalListing?>

    @Query("SELECT * FROM animal_listings WHERE id = :id")
    suspend fun getListingByIdOnce(id: Long): AnimalListing?

    @Query("SELECT COUNT(*) FROM animal_listings")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListing(listing: AnimalListing): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(listings: List<AnimalListing>)

    @Update
    suspend fun updateListing(listing: AnimalListing)

    @Query("UPDATE animal_listings SET isFavorite = :isFav WHERE id = :id")
    suspend fun setFavorite(id: Long, isFav: Boolean)

    @Query("UPDATE animal_listings SET viewsCount = viewsCount + 1 WHERE id = :id")
    suspend fun incrementViews(id: Long)

    @Query("UPDATE animal_listings SET isSold = :isSold WHERE id = :id")
    suspend fun setSoldStatus(id: Long, isSold: Boolean)

    @Query("DELETE FROM animal_listings WHERE id = :id")
    suspend fun deleteListing(id: Long)

    @Query("DELETE FROM animal_listings WHERE isUserListing = 0")
    suspend fun deleteSeedListings()

    @Query("DELETE FROM animal_listings")
    suspend fun deleteAllListings()

    @Query("UPDATE animal_listings SET isFlaggedIllegal = :isFlagged, flagReason = :reason WHERE id = :id")
    suspend fun flagListing(id: Long, isFlagged: Boolean, reason: String?)

    @Query("UPDATE animal_listings SET imageUri = NULL, additionalPhotos = '', isFlaggedIllegal = 1, flagReason = 'Removed by Developer for violating photo guidelines' WHERE id = :id")
    suspend fun removeIllegalPhotos(id: Long)

    @Query("SELECT * FROM animal_listings WHERE isFlaggedIllegal = 1")
    fun getAllFlaggedListings(): Flow<List<AnimalListing>>

    @Query("SELECT COUNT(*) FROM animal_listings WHERE category = :category AND isSold = 0")
    suspend fun getCountByCategory(category: String): Int

    @Query("SELECT DISTINCT city FROM animal_listings WHERE isSold = 0")
    suspend fun getAllCities(): List<String>
}

@Dao
interface ReelDao {
    @Query("SELECT * FROM reel_videos ORDER BY id ASC")
    fun getAllReels(): Flow<List<ReelVideo>>

    @Query("SELECT COUNT(*) FROM reel_videos")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReel(reel: ReelVideo): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reels: List<ReelVideo>)

    @Update
    suspend fun updateReel(reel: ReelVideo)

    @Query("UPDATE reel_videos SET isLiked = :isLiked, likesCount = likesCount + :delta WHERE id = :id")
    suspend fun toggleLike(id: Long, isLiked: Boolean, delta: Int)

    @Query("UPDATE reel_videos SET isBookmarked = NOT isBookmarked WHERE id = :id")
    suspend fun toggleBookmark(id: Long)

    @Query("UPDATE reel_videos SET isFollowing = NOT isFollowing WHERE id = :id")
    suspend fun toggleFollow(id: Long)

    @Query("SELECT * FROM reel_comments WHERE reelId = :reelId ORDER BY timestamp DESC")
    fun getCommentsForReel(reelId: Long): Flow<List<ReelComment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: ReelComment)

    @Query("UPDATE reel_videos SET commentsCount = commentsCount + 1 WHERE id = :reelId")
    suspend fun incrementCommentCount(reelId: Long)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM app_notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<AppNotification>>

    @Query("SELECT COUNT(*) FROM app_notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM app_notifications")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notifications: List<AppNotification>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: AppNotification)

    @Query("UPDATE app_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE app_notifications SET isRead = 1")
    suspend fun markAllAsRead()
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages WHERE listingId = :listingId ORDER BY timestamp ASC")
    fun getMessagesForListing(listingId: Long): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage): Long
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile)

    @Query("UPDATE user_profiles SET fullName = :name, phone = :phone, email = :email, city = :city, bio = :bio WHERE id = 1")
    suspend fun updateProfileInfo(name: String, phone: String, email: String, city: String, bio: String)

    @Query("SELECT COUNT(*) FROM user_profiles")
    suspend fun getCount(): Int
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY displayOrder ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Query("UPDATE categories SET count = :count WHERE id = :id")
    suspend fun updateCount(id: String, count: Int)

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCount(): Int
}

@Dao
interface InquiryDao {
    @Query("SELECT * FROM buyer_inquiries ORDER BY timestamp DESC")
    fun getAllInquiries(): Flow<List<BuyerInquiry>>

    @Query("SELECT * FROM buyer_inquiries WHERE listingId = :listingId ORDER BY timestamp DESC")
    fun getInquiriesForListing(listingId: Long): Flow<List<BuyerInquiry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInquiry(inquiry: BuyerInquiry): Long

    @Query("UPDATE buyer_inquiries SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("DELETE FROM buyer_inquiries WHERE id = :id")
    suspend fun deleteInquiry(id: Long)

    @Query("SELECT COUNT(*) FROM buyer_inquiries")
    suspend fun getCount(): Int
}

@Dao
interface SellerReviewDao {
    @Query("SELECT * FROM seller_reviews WHERE sellerPhone = :sellerPhone ORDER BY timestamp DESC")
    fun getReviewsForSeller(sellerPhone: String): Flow<List<SellerReview>>

    @Query("SELECT * FROM seller_reviews ORDER BY timestamp DESC")
    fun getAllReviews(): Flow<List<SellerReview>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: SellerReview): Long

    @Query("SELECT COUNT(*) FROM seller_reviews")
    suspend fun getCount(): Int
}

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 10")
    fun getRecentSearches(): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(search: SearchHistoryEntity): Long

    @Query("DELETE FROM search_history WHERE id = :id")
    suspend fun deleteSearch(id: Long)

    @Query("DELETE FROM search_history")
    suspend fun clearHistory()
}

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings ORDER BY timestamp DESC")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE id = :id LIMIT 1")
    fun getBookingById(id: Long): Flow<BookingEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity): Long

    @Query("UPDATE bookings SET status = :status WHERE id = :id")
    suspend fun updateBookingStatus(id: Long, status: String)

    @Query("SELECT COUNT(*) FROM bookings")
    suspend fun getCount(): Int
}
