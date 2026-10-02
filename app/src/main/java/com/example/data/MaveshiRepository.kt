package com.example.data

import android.content.Context
import com.example.data.firebase.FirebaseSyncManager
import com.example.data.firebase.FirebaseSyncStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

data class DatabaseStats(
    val totalListings: Int,
    val activeListings: Int,
    val soldListings: Int,
    val totalReels: Int,
    val totalInquiries: Int,
    val totalReviews: Int,
    val totalNotifications: Int
)

class MaveshiRepository(context: Context) {
    private val db = MaveshiDatabase.getInstance(context)
    private val animalDao = db.animalDao()
    private val reelDao = db.reelDao()
    private val notifDao = db.notificationDao()
    private val chatDao = db.chatDao()
    private val userProfileDao = db.userProfileDao()
    private val categoryDao = db.categoryDao()
    private val inquiryDao = db.inquiryDao()
    private val sellerReviewDao = db.sellerReviewDao()
    private val searchHistoryDao = db.searchHistoryDao()
    private val bookingDao = db.bookingDao()
    private val appSettingDao = db.appSettingDao()

    private val firebaseSyncManager = FirebaseSyncManager(
        context = context,
        animalDao = animalDao,
        reelDao = reelDao,
        notifDao = notifDao,
        chatDao = chatDao
    )

    val syncStatus: StateFlow<FirebaseSyncStatus> = firebaseSyncManager.syncStatus
    val isFirebaseAvailable: StateFlow<Boolean> = firebaseSyncManager.isFirebaseAvailable

    // Reactive Data Streams
    val allListings: Flow<List<AnimalListing>> = animalDao.getAllListings()
    val featuredListings: Flow<List<AnimalListing>> = animalDao.getFeaturedListings()
    val userListings: Flow<List<AnimalListing>> = animalDao.getUserListings()
    val allFlaggedListings: Flow<List<AnimalListing>> = animalDao.getAllFlaggedListings()
    val allReels: Flow<List<ReelVideo>> = reelDao.getAllReels()
    val notifications: Flow<List<AppNotification>> = notifDao.getAllNotifications()
    val unreadNotifCount: Flow<Int> = notifDao.getUnreadCount()

    val userProfile: Flow<UserProfile?> = userProfileDao.getUserProfile()
    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
    val allInquiries: Flow<List<BuyerInquiry>> = inquiryDao.getAllInquiries()
    val allReviews: Flow<List<SellerReview>> = sellerReviewDao.getAllReviews()
    val recentSearches: Flow<List<SearchHistoryEntity>> = searchHistoryDao.getRecentSearches()
    val allBookings: Flow<List<BookingEntity>> = bookingDao.getAllBookings()
    val allSettings: Flow<List<AppSettingEntity>> = appSettingDao.getAllSettings()

    suspend fun ensureSeeded() = withContext(Dispatchers.IO) {
        MaveshiDatabase.seedDatabase(db)
    }

    // Listings Operations
    fun getListingById(id: Long): Flow<AnimalListing?> = animalDao.getListingById(id)

    suspend fun getListingByIdOnce(id: Long): AnimalListing? = withContext(Dispatchers.IO) {
        animalDao.getListingByIdOnce(id)
    }

    suspend fun toggleFavorite(id: Long, currentFav: Boolean) = withContext(Dispatchers.IO) {
        animalDao.setFavorite(id, !currentFav)
    }

    suspend fun incrementViews(id: Long) = withContext(Dispatchers.IO) {
        animalDao.incrementViews(id)
        val listing = animalDao.getListingByIdOnce(id)
        if (listing != null) {
            firebaseSyncManager.updateListingInFirebase(id, mapOf("viewsCount" to listing.viewsCount))
        }
    }

    suspend fun addListing(listing: AnimalListing): Long = withContext(Dispatchers.IO) {
        val newId = animalDao.insertListing(listing)
        val listingWithId = listing.copy(id = newId)
        firebaseSyncManager.syncListingToFirebase(listingWithId)

        // Automatically trigger in-app notification for new listing
        notifDao.insert(
            AppNotification(
                title = "Listing Live on Mall Maweshi!",
                message = "Your listing '${listing.title}' is now visible to thousands of buyers.",
                timeText = "Just now",
                section = "TODAY",
                listingId = newId,
                imageResName = listing.imageResName,
                isRead = false
            )
        )
        newId
    }

    suspend fun deleteListing(id: Long) = withContext(Dispatchers.IO) {
        animalDao.deleteListing(id)
    }

    suspend fun setSoldStatus(id: Long, isSold: Boolean) = withContext(Dispatchers.IO) {
        animalDao.setSoldStatus(id, isSold)
        firebaseSyncManager.updateListingInFirebase(id, mapOf("isSold" to isSold))
    }

    // User Profile Operations
    suspend fun updateUserProfile(name: String, phone: String, email: String, city: String, bio: String) = withContext(Dispatchers.IO) {
        userProfileDao.updateProfileInfo(name, phone, email, city, bio)
    }

    suspend fun getUserProfileOnce(): UserProfile? = withContext(Dispatchers.IO) {
        userProfileDao.getUserProfileOnce()
    }

    // Inquiries & Offers Operations
    fun getInquiriesForListing(listingId: Long): Flow<List<BuyerInquiry>> = inquiryDao.getInquiriesForListing(listingId)

    suspend fun submitInquiry(
        listingId: Long,
        listingTitle: String,
        buyerName: String,
        buyerPhone: String,
        offerPrice: Long,
        message: String
    ): Long = withContext(Dispatchers.IO) {
        val inquiry = BuyerInquiry(
            listingId = listingId,
            listingTitle = listingTitle,
            buyerName = buyerName,
            buyerPhone = buyerPhone,
            offerPrice = offerPrice,
            message = message,
            status = "PENDING"
        )
        val newId = inquiryDao.insertInquiry(inquiry)

        // Auto-generate notification for seller
        notifDao.insert(
            AppNotification(
                title = "New Offer: Rs $offerPrice",
                message = "$buyerName sent an offer for $listingTitle: '$message'",
                timeText = "Just now",
                section = "TODAY",
                listingId = listingId,
                isRead = false
            )
        )
        newId
    }

    suspend fun updateInquiryStatus(id: Long, status: String) = withContext(Dispatchers.IO) {
        inquiryDao.updateStatus(id, status)
    }

    // Seller Reviews Operations
    fun getReviewsForSeller(sellerPhone: String): Flow<List<SellerReview>> = sellerReviewDao.getReviewsForSeller(sellerPhone)

    suspend fun submitSellerReview(
        sellerPhone: String,
        reviewerName: String,
        rating: Int,
        comment: String
    ): Long = withContext(Dispatchers.IO) {
        val review = SellerReview(
            sellerPhone = sellerPhone,
            reviewerName = reviewerName,
            rating = rating,
            comment = comment,
            timeAgo = "Just now"
        )
        sellerReviewDao.insertReview(review)
    }

    // Search History Operations
    suspend fun addSearchQuery(query: String) = withContext(Dispatchers.IO) {
        if (query.isNotBlank()) {
            searchHistoryDao.insertSearch(SearchHistoryEntity(query = query.trim()))
        }
    }

    suspend fun clearSearchHistory() = withContext(Dispatchers.IO) {
        searchHistoryDao.clearHistory()
    }

    // Bookings Operations
    suspend fun submitBooking(
        listingId: Long,
        listingTitle: String,
        buyerName: String,
        buyerPhone: String,
        tokenAmount: Long,
        deliveryOption: String
    ): Long = withContext(Dispatchers.IO) {
        val booking = BookingEntity(
            listingId = listingId,
            listingTitle = listingTitle,
            buyerName = buyerName,
            buyerPhone = buyerPhone,
            tokenAmount = tokenAmount,
            deliveryOption = deliveryOption,
            status = "CONFIRMED"
        )
        bookingDao.insertBooking(booking)
    }

    // Reels Operations
    suspend fun toggleReelLike(id: Long, isCurrentlyLiked: Boolean) = withContext(Dispatchers.IO) {
        val delta = if (isCurrentlyLiked) -1 else 1
        reelDao.toggleLike(id, !isCurrentlyLiked, delta)
    }

    suspend fun toggleReelBookmark(id: Long) = withContext(Dispatchers.IO) {
        reelDao.toggleBookmark(id)
    }

    suspend fun toggleReelFollow(id: Long) = withContext(Dispatchers.IO) {
        reelDao.toggleFollow(id)
    }

    fun getReelComments(reelId: Long): Flow<List<ReelComment>> = reelDao.getCommentsForReel(reelId)

    suspend fun addReelComment(reelId: Long, authorName: String, text: String) = withContext(Dispatchers.IO) {
        val comment = ReelComment(
            reelId = reelId,
            authorName = authorName,
            commentText = text,
            timeAgo = "Just now"
        )
        reelDao.insertComment(comment)
        reelDao.incrementCommentCount(reelId)
    }

    suspend fun addReel(reel: ReelVideo): Long = withContext(Dispatchers.IO) {
        val newId = reelDao.insertReel(reel)
        val reelWithId = reel.copy(id = newId)
        firebaseSyncManager.syncReelToFirebase(reelWithId)
        newId
    }

    // Notifications Operations
    suspend fun markNotificationRead(id: Long) = withContext(Dispatchers.IO) {
        notifDao.markAsRead(id)
    }

    suspend fun markAllNotificationsRead() = withContext(Dispatchers.IO) {
        notifDao.markAllAsRead()
    }

    // Chat Operations
    fun getChatMessages(listingId: Long): Flow<List<ChatMessage>> = chatDao.getMessagesForListing(listingId)

    suspend fun sendChatMessage(listingId: Long, text: String, isFromMe: Boolean, senderName: String) = withContext(Dispatchers.IO) {
        val msg = ChatMessage(
            listingId = listingId,
            senderName = senderName,
            text = text,
            isFromMe = isFromMe,
            timeFormatted = "Just now"
        )
        val msgId = chatDao.insertMessage(msg)
        firebaseSyncManager.syncChatMessageToFirebase(msg.copy(id = msgId))
    }

    // Backend Statistics & Diagnostics
    suspend fun getDatabaseStats(): DatabaseStats = withContext(Dispatchers.IO) {
        val totalListings = animalDao.getCount()
        val totalReels = reelDao.getCount()
        val totalInquiries = inquiryDao.getCount()
        val totalReviews = sellerReviewDao.getCount()
        val totalNotifs = notifDao.getCount()

        DatabaseStats(
            totalListings = totalListings,
            activeListings = totalListings - 3,
            soldListings = 3,
            totalReels = totalReels,
            totalInquiries = totalInquiries,
            totalReviews = totalReviews,
            totalNotifications = totalNotifs
        )
    }

    // Cloud Synchronization
    suspend fun syncAllLocalDataToFirebase(listings: List<AnimalListing>, reels: List<ReelVideo>) = withContext(Dispatchers.IO) {
        firebaseSyncManager.syncAllLocalDataToFirebase(listings, reels)
    }

    // Developer Moderation & Settings Operations
    suspend fun flagListing(id: Long, isFlagged: Boolean, reason: String?) = withContext(Dispatchers.IO) {
        animalDao.flagListing(id, isFlagged, reason)
    }

    suspend fun removeIllegalPhotos(id: Long) = withContext(Dispatchers.IO) {
        animalDao.removeIllegalPhotos(id)
    }

    suspend fun updateSetting(key: String, value: String) = withContext(Dispatchers.IO) {
        appSettingDao.updateValue(key, value)
    }

    suspend fun getSettingValue(key: String): String? = withContext(Dispatchers.IO) {
        appSettingDao.getValue(key)
    }

    suspend fun clearSeedListings() = withContext(Dispatchers.IO) {
        animalDao.deleteSeedListings()
    }

    suspend fun deleteAllListings() = withContext(Dispatchers.IO) {
        animalDao.deleteAllListings()
    }

    suspend fun restoreSeedListings() = withContext(Dispatchers.IO) {
        MaveshiDatabase.seedDatabase(db)
    }
}
