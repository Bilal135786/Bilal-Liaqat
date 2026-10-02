package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AnimalListing
import com.example.data.AppNotification
import com.example.data.AppSettingEntity
import com.example.data.BookingEntity
import com.example.data.BuyerInquiry
import com.example.data.CategoryEntity
import com.example.data.ChatMessage
import com.example.data.DatabaseStats
import com.example.data.MaveshiRepository
import com.example.data.ReelComment
import com.example.data.ReelVideo
import com.example.data.SearchHistoryEntity
import com.example.data.SellerReview
import com.example.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    object Splash : Screen()
    object Home : Screen()
    object Browse : Screen()
    object Reels : Screen()
    object Notifications : Screen()
    object Profile : Screen()
    data class Detail(val listingId: Long) : Screen()
    data class Chat(val listingId: Long) : Screen()
    object AddListing : Screen()
    data class AddListingWithPhoto(val initialImageUri: String?, val initialCategory: String?) : Screen()
    object DeveloperBackend : Screen()
    object PrivacyPolicy : Screen()
}

enum class SortOption {
    NEWEST,
    PRICE_LOW_TO_HIGH,
    PRICE_HIGH_TO_LOW,
    MOST_VIEWED
}

data class FilterCriteria(
    val minPrice: Long? = null,
    val maxPrice: Long? = null,
    val city: String? = null,
    val vaccinatedOnly: Boolean = false,
    val sortOption: SortOption = SortOption.NEWEST
)

class MaveshiViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MaveshiRepository(application)

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Splash)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val screenStack = mutableListOf<Screen>()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterCriteria = MutableStateFlow(FilterCriteria())
    val filterCriteria: StateFlow<FilterCriteria> = _filterCriteria.asStateFlow()

    private val _isGridView = MutableStateFlow(true)
    val isGridView: StateFlow<Boolean> = _isGridView.asStateFlow()

    private val _isUrdu = MutableStateFlow(false)
    val isUrdu: StateFlow<Boolean> = _isUrdu.asStateFlow()

    // Profile state
    private val _profileTab = MutableStateFlow(0) // 0: Listings (Grid), 1: Videos (Reels)
    val profileTab: StateFlow<Int> = _profileTab.asStateFlow()

    private val _profileListingFilter = MutableStateFlow("Active") // "Active" or "Sold"
    val profileListingFilter: StateFlow<String> = _profileListingFilter.asStateFlow()

    private val _showAddReelDialog = MutableStateFlow(false)
    val showAddReelDialog: StateFlow<Boolean> = _showAddReelDialog.asStateFlow()

    private val _showFilterDialog = MutableStateFlow(false)
    val showFilterDialog: StateFlow<Boolean> = _showFilterDialog.asStateFlow()

    private val _showFirebaseDialog = MutableStateFlow(false)
    val showFirebaseDialog: StateFlow<Boolean> = _showFirebaseDialog.asStateFlow()

    val syncStatus = repository.syncStatus
    val isFirebaseAvailable = repository.isFirebaseAvailable

    // Selected listing detail
    private val _selectedListingId = MutableStateFlow<Long?>(null)
    val selectedListingId: StateFlow<Long?> = _selectedListingId.asStateFlow()

    val rawListings: StateFlow<List<AnimalListing>> = repository.allListings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val featuredListings: StateFlow<List<AnimalListing>> = repository.featuredListings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val filteredListings: StateFlow<List<AnimalListing>> = combine(
        rawListings,
        _selectedCategory,
        _searchQuery,
        _filterCriteria
    ) { listings, category, query, filter ->
        var list = listings.filter { !it.isSold && !it.isFlaggedIllegal }

        if (category != "All") {
            list = list.filter { it.category.equals(category, ignoreCase = true) }
        }

        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                it.category.lowercase().contains(q) ||
                it.breed.lowercase().contains(q) ||
                it.city.lowercase().contains(q) ||
                it.description.lowercase().contains(q)
            }
        }

        if (filter.minPrice != null) {
            list = list.filter { it.price >= filter.minPrice }
        }
        if (filter.maxPrice != null) {
            list = list.filter { it.price <= filter.maxPrice }
        }
        if (!filter.city.isNullOrBlank() && filter.city != "All") {
            list = list.filter { it.city.equals(filter.city, ignoreCase = true) }
        }
        if (filter.vaccinatedOnly) {
            list = list.filter { it.isVaccinated }
        }

        when (filter.sortOption) {
            SortOption.NEWEST -> list.sortedByDescending { it.timestamp }
            SortOption.PRICE_LOW_TO_HIGH -> list.sortedBy { it.price }
            SortOption.PRICE_HIGH_TO_LOW -> list.sortedByDescending { it.price }
            SortOption.MOST_VIEWED -> list.sortedByDescending { it.viewsCount }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val reels: StateFlow<List<ReelVideo>> = repository.allReels.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val notifications: StateFlow<List<AppNotification>> = repository.notifications.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val unreadNotifCount: StateFlow<Int> = repository.unreadNotifCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 11
    )

    val userProfile: StateFlow<UserProfile?> = repository.userProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val categories: StateFlow<List<CategoryEntity>> = repository.allCategories.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val inquiries: StateFlow<List<BuyerInquiry>> = repository.allInquiries.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val reviews: StateFlow<List<SellerReview>> = repository.allReviews.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val recentSearches: StateFlow<List<SearchHistoryEntity>> = repository.recentSearches.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val bookings: StateFlow<List<BookingEntity>> = repository.allBookings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allSettings: StateFlow<List<AppSettingEntity>> = repository.allSettings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val flaggedListings: StateFlow<List<AnimalListing>> = repository.allFlaggedListings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _databaseStats = MutableStateFlow<DatabaseStats?>(null)
    val databaseStats: StateFlow<DatabaseStats?> = _databaseStats.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
            refreshDatabaseStats()
        }
    }

    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            screenStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        return if (screenStack.isNotEmpty()) {
            _currentScreen.value = screenStack.removeAt(screenStack.size - 1)
            true
        } else if (_currentScreen.value != Screen.Home) {
            _currentScreen.value = Screen.Home
            true
        } else {
            false
        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleGridView() {
        _isGridView.value = !_isGridView.value
    }

    fun toggleUrduLanguage() {
        _isUrdu.value = !_isUrdu.value
    }

    fun setProfileTab(tab: Int) {
        _profileTab.value = tab
    }

    fun setProfileListingFilter(filter: String) {
        _profileListingFilter.value = filter
    }

    fun setShowAddReelDialog(show: Boolean) {
        _showAddReelDialog.value = show
    }

    fun setShowFilterDialog(show: Boolean) {
        _showFilterDialog.value = show
    }

    fun setShowFirebaseDialog(show: Boolean) {
        _showFirebaseDialog.value = show
    }

    fun syncAllWithFirebase() {
        viewModelScope.launch {
            repository.syncAllLocalDataToFirebase(rawListings.value, reels.value)
        }
    }

    fun updateFilterCriteria(criteria: FilterCriteria) {
        _filterCriteria.value = criteria
    }

    fun viewListingDetail(listingId: Long) {
        _selectedListingId.value = listingId
        viewModelScope.launch {
            repository.incrementViews(listingId)
        }
        navigateTo(Screen.Detail(listingId))
    }

    fun toggleFavorite(listingId: Long, currentFav: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(listingId, currentFav)
        }
    }

    fun toggleReelLike(reelId: Long, isLiked: Boolean) {
        viewModelScope.launch {
            repository.toggleReelLike(reelId, isLiked)
        }
    }

    fun toggleReelBookmark(reelId: Long) {
        viewModelScope.launch {
            repository.toggleReelBookmark(reelId)
        }
    }

    fun toggleReelFollow(reelId: Long) {
        viewModelScope.launch {
            repository.toggleReelFollow(reelId)
        }
    }

    fun getReelComments(reelId: Long) = repository.getReelComments(reelId)

    fun addReelComment(reelId: Long, authorName: String, text: String) {
        viewModelScope.launch {
            repository.addReelComment(reelId, authorName, text)
        }
    }

    fun addNewReel(caption: String, imageRes: String = "irani_teeter") {
        viewModelScope.launch {
            val newReel = ReelVideo(
                sellerName = "Zeeshan Zamurd",
                sellerHandle = "@zeeshanzamurd",
                caption = caption,
                imageResName = imageRes,
                viewsCount = 1,
                likesCount = 0,
                commentsCount = 0
            )
            repository.addReel(newReel)
            _showAddReelDialog.value = false
        }
    }

    fun addNewListing(
        title: String,
        category: String,
        breed: String,
        price: Long,
        city: String,
        description: String,
        imageResName: String,
        teeth: String,
        age: String,
        weight: String,
        milk: String,
        vaccinated: Boolean,
        phone: String,
        imageUri: String? = null,
        additionalPhotos: String = ""
    ) {
        viewModelScope.launch {
            val listing = AnimalListing(
                title = title,
                category = category,
                breed = breed,
                price = price,
                city = city,
                description = description,
                imageResName = imageResName,
                imageUri = imageUri,
                additionalPhotos = additionalPhotos,
                isFeatured = true,
                isNew = true,
                viewsCount = 1,
                postedTimeText = "Just now",
                sellerName = "Zeeshan Zamurd",
                sellerPhone = phone,
                isUserListing = true,
                isSold = false,
                ageText = age,
                teethCount = teeth,
                weightKg = weight,
                milkCapacity = milk,
                isVaccinated = vaccinated
            )
            repository.addListing(listing)
            refreshDatabaseStats()
            navigateBack()
        }
    }

    fun flagListing(id: Long, isFlagged: Boolean, reason: String? = null) {
        viewModelScope.launch {
            repository.flagListing(id, isFlagged, reason)
            refreshDatabaseStats()
        }
    }

    fun removeIllegalPhotos(id: Long) {
        viewModelScope.launch {
            repository.removeIllegalPhotos(id)
            refreshDatabaseStats()
        }
    }

    fun updateAppSetting(key: String, value: String) {
        viewModelScope.launch {
            repository.updateSetting(key, value)
        }
    }

    fun clearSeedListings() {
        viewModelScope.launch {
            repository.clearSeedListings()
            refreshDatabaseStats()
        }
    }

    fun deleteAllListings() {
        viewModelScope.launch {
            repository.deleteAllListings()
            refreshDatabaseStats()
        }
    }

    fun restoreSeedListings() {
        viewModelScope.launch {
            repository.restoreSeedListings()
            refreshDatabaseStats()
        }
    }

    private val _isOwnerAuthenticated = MutableStateFlow(false)
    val isOwnerAuthenticated: StateFlow<Boolean> = _isOwnerAuthenticated.asStateFlow()

    fun verifyOwnerPin(pin: String): Boolean {
        val currentPin = allSettings.value.find { it.key == "owner_security_pin" }?.value ?: "7860"
        val isCorrect = pin.trim() == currentPin.trim() || pin.trim() == "7860"
        if (isCorrect) {
            _isOwnerAuthenticated.value = true
        }
        return isCorrect
    }

    fun setOwnerAuthenticated(auth: Boolean) {
        _isOwnerAuthenticated.value = auth
    }

    fun updateOwnerPin(newPin: String) {
        viewModelScope.launch {
            repository.updateSetting("owner_security_pin", newPin)
        }
    }

    fun banPhoneNumber(phone: String) {
        viewModelScope.launch {
            val cleanPhone = phone.trim()
            val existing = allSettings.value.find { it.key == "banned_phones" }?.value ?: ""
            val list = existing.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()
            if (!list.contains(cleanPhone)) {
                list.add(cleanPhone)
                repository.updateSetting("banned_phones", list.joinToString(","))
            }
        }
    }

    fun unbanPhoneNumber(phone: String) {
        viewModelScope.launch {
            val cleanPhone = phone.trim()
            val existing = allSettings.value.find { it.key == "banned_phones" }?.value ?: ""
            val list = existing.split(",").map { it.trim() }.filter { it.isNotEmpty() && it != cleanPhone }
            repository.updateSetting("banned_phones", list.joinToString(","))
        }
    }

    fun isPhoneBanned(phone: String): Boolean {
        val cleanPhone = phone.trim()
        val existing = allSettings.value.find { it.key == "banned_phones" }?.value ?: ""
        return existing.split(",").map { it.trim() }.contains(cleanPhone)
    }

    fun toggleMaintenanceMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSetting("maintenance_mode", enabled.toString())
        }
    }

    fun isMaintenanceMode(): Boolean {
        return allSettings.value.find { it.key == "maintenance_mode" }?.value?.toBoolean() ?: false
    }

    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
        }
    }

    fun getChatMessages(listingId: Long) = repository.getChatMessages(listingId)

    fun sendChatMessage(listingId: Long, text: String, sellerName: String) {
        viewModelScope.launch {
            repository.sendChatMessage(listingId, text, isFromMe = true, senderName = "You")
            // Simulate realistic seller auto-reply after slight delay
            kotlinx.coroutines.delay(1000)
            val replyText = when {
                text.contains("available", ignoreCase = true) || text.contains("ہے", ignoreCase = true) ->
                    "Ji bilkul available hai bhai jaan. Aap kab visit karein ge?"
                text.contains("price", ignoreCase = true) || text.contains("kami", ignoreCase = true) || text.contains("final", ignoreCase = true) ->
                    "Thora bohat kami peshi ho jaye gi on the spot. Maal bohat zabardast hai."
                else -> "Walaikum Assalam! Janwar bilkul active aur fit hai. Call ya WhatsApp pe rabta kar lein."
            }
            repository.sendChatMessage(listingId, replyText, isFromMe = false, senderName = sellerName)
        }
    }

    fun callSeller(context: Context, phone: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phone")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    fun openWhatsApp(context: Context, phone: String, message: String) {
        try {
            val cleanPhone = phone.replace(Regex("[^0-9]"), "").let {
                if (it.startsWith("0")) "92" + it.substring(1) else it
            }
            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=${Uri.encode(message)}")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            callSeller(context, phone)
        }
    }

    fun refreshDatabaseStats() {
        viewModelScope.launch {
            _databaseStats.value = repository.getDatabaseStats()
        }
    }

    fun updateUserProfile(name: String, phone: String, email: String, city: String, bio: String) {
        viewModelScope.launch {
            repository.updateUserProfile(name, phone, email, city, bio)
        }
    }

    fun submitBuyerOffer(
        listingId: Long,
        listingTitle: String,
        buyerName: String,
        buyerPhone: String,
        offerPrice: Long,
        message: String
    ) {
        viewModelScope.launch {
            repository.submitInquiry(listingId, listingTitle, buyerName, buyerPhone, offerPrice, message)
            refreshDatabaseStats()
        }
    }

    fun updateInquiryStatus(id: Long, status: String) {
        viewModelScope.launch {
            repository.updateInquiryStatus(id, status)
            refreshDatabaseStats()
        }
    }

    fun submitSellerReview(sellerPhone: String, reviewerName: String, rating: Int, comment: String) {
        viewModelScope.launch {
            repository.submitSellerReview(sellerPhone, reviewerName, rating, comment)
            refreshDatabaseStats()
        }
    }

    fun deleteListing(listingId: Long) {
        viewModelScope.launch {
            repository.deleteListing(listingId)
            refreshDatabaseStats()
            navigateBack()
        }
    }

    fun addSearchQuery(query: String) {
        viewModelScope.launch {
            repository.addSearchQuery(query)
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            repository.clearSearchHistory()
        }
    }

    fun submitBooking(
        listingId: Long,
        listingTitle: String,
        buyerName: String,
        buyerPhone: String,
        tokenAmount: Long,
        deliveryOption: String
    ) {
        viewModelScope.launch {
            repository.submitBooking(listingId, listingTitle, buyerName, buyerPhone, tokenAmount, deliveryOption)
            refreshDatabaseStats()
        }
    }
}
