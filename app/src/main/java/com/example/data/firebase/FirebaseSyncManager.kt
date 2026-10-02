package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.example.data.AnimalDao
import com.example.data.AnimalListing
import com.example.data.AppNotification
import com.example.data.ChatDao
import com.example.data.ChatMessage
import com.example.data.NotificationDao
import com.example.data.ReelComment
import com.example.data.ReelDao
import com.example.data.ReelVideo
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

sealed class FirebaseSyncStatus {
    object Idle : FirebaseSyncStatus()
    object Syncing : FirebaseSyncStatus()
    data class Synced(val timestamp: Long, val message: String = "Connected & Synced with Firebase") : FirebaseSyncStatus()
    data class Offline(val reason: String = "Operating in offline Room cache mode") : FirebaseSyncStatus()
    data class Error(val errorMessage: String) : FirebaseSyncStatus()
}

class FirebaseSyncManager(
    private val context: Context,
    private val animalDao: AnimalDao,
    private val reelDao: ReelDao,
    private val notifDao: NotificationDao,
    private val chatDao: ChatDao
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private var firestore: FirebaseFirestore? = null
    private var listingListener: ListenerRegistration? = null
    private var reelListener: ListenerRegistration? = null
    private var chatListener: ListenerRegistration? = null

    private val _syncStatus = MutableStateFlow<FirebaseSyncStatus>(FirebaseSyncStatus.Idle)
    val syncStatus: StateFlow<FirebaseSyncStatus> = _syncStatus.asStateFlow()

    private val _isFirebaseAvailable = MutableStateFlow(false)
    val isFirebaseAvailable: StateFlow<Boolean> = _isFirebaseAvailable.asStateFlow()

    init {
        checkAndInitializeFirebase()
    }

    private fun checkAndInitializeFirebase() {
        try {
            val apps = FirebaseApp.getApps(context)
            if (apps.isNotEmpty()) {
                firestore = FirebaseFirestore.getInstance()
                _isFirebaseAvailable.value = true
                _syncStatus.value = FirebaseSyncStatus.Synced(System.currentTimeMillis(), "Firebase Firestore connected")
                startRealtimeListeners()
            } else {
                _isFirebaseAvailable.value = false
                _syncStatus.value = FirebaseSyncStatus.Offline("Add google-services.json to enable live Firebase cloud sync")
            }
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Firebase initialization note: ${e.message}")
            _isFirebaseAvailable.value = false
            _syncStatus.value = FirebaseSyncStatus.Offline("Room offline-first mode active. ${e.localizedMessage ?: ""}")
        }
    }

    /**
     * Start real-time Firestore synchronization into Room Database
     */
    fun startRealtimeListeners() {
        val db = firestore ?: return

        // 1. Listen to 'listings' collection in Firestore -> update Room
        listingListener?.remove()
        listingListener = db.collection("listings").addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("FirebaseSync", "Listen to listings failed: ${error.message}")
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                scope.launch {
                    val incomingListings = mutableListOf<AnimalListing>()
                    for (doc in snapshot.documents) {
                        try {
                            val id = doc.getLong("id") ?: (doc.id.toLongOrNull() ?: 0L)
                            val title = doc.getString("title") ?: "Livestock"
                            val category = doc.getString("category") ?: "Goat"
                            val breed = doc.getString("breed") ?: ""
                            val price = doc.getLong("price") ?: 0L
                            val city = doc.getString("city") ?: "Lahore"
                            val description = doc.getString("description") ?: ""
                            val imageResName = doc.getString("imageResName") ?: "maveshi_splash"
                            val isFeatured = doc.getBoolean("isFeatured") ?: false
                            val isNew = doc.getBoolean("isNew") ?: false
                            val viewsCount = doc.getLong("viewsCount")?.toInt() ?: 0
                            val postedTimeText = doc.getString("postedTimeText") ?: "Recently"
                            val sellerName = doc.getString("sellerName") ?: "Seller"
                            val sellerPhone = doc.getString("sellerPhone") ?: "03001234567"
                            val isUserListing = doc.getBoolean("isUserListing") ?: false
                            val isSold = doc.getBoolean("isSold") ?: false
                            val ageText = doc.getString("ageText") ?: "1 Year"
                            val teethCount = doc.getString("teethCount") ?: "2 Danda"
                            val weightKg = doc.getString("weightKg") ?: "40 kg"
                            val milkCapacity = doc.getString("milkCapacity") ?: ""
                            val isVaccinated = doc.getBoolean("isVaccinated") ?: true
                            val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()

                            val listing = AnimalListing(
                                id = id,
                                title = title,
                                category = category,
                                breed = breed,
                                price = price,
                                city = city,
                                description = description,
                                imageResName = imageResName,
                                isFeatured = isFeatured,
                                isNew = isNew,
                                viewsCount = viewsCount,
                                postedTimeText = postedTimeText,
                                sellerName = sellerName,
                                sellerPhone = sellerPhone,
                                isUserListing = isUserListing,
                                isSold = isSold,
                                ageText = ageText,
                                teethCount = teethCount,
                                weightKg = weightKg,
                                milkCapacity = milkCapacity,
                                isVaccinated = isVaccinated,
                                timestamp = timestamp
                            )
                            incomingListings.add(listing)
                        } catch (e: Exception) {
                            Log.e("FirebaseSync", "Error parsing listing from Firestore: ${e.message}")
                        }
                    }

                    if (incomingListings.isNotEmpty()) {
                        animalDao.insertAll(incomingListings)
                        _syncStatus.value = FirebaseSyncStatus.Synced(
                            System.currentTimeMillis(),
                            "Synced ${incomingListings.size} listings from Firebase"
                        )
                    }
                }
            }
        }

        // 2. Listen to 'reels' collection in Firestore -> update Room
        reelListener?.remove()
        reelListener = db.collection("reels").addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            if (snapshot != null && !snapshot.isEmpty) {
                scope.launch {
                    val incomingReels = mutableListOf<ReelVideo>()
                    for (doc in snapshot.documents) {
                        try {
                            val id = doc.getLong("id") ?: (doc.id.toLongOrNull() ?: 0L)
                            val sellerName = doc.getString("sellerName") ?: "User"
                            val sellerHandle = doc.getString("sellerHandle") ?: "@user"
                            val caption = doc.getString("caption") ?: ""
                            val imageResName = doc.getString("imageResName") ?: "irani_teeter"
                            val viewsCount = doc.getLong("viewsCount")?.toInt() ?: 0
                            val likesCount = doc.getLong("likesCount")?.toInt() ?: 0
                            val commentsCount = doc.getLong("commentsCount")?.toInt() ?: 0
                            val audioTitle = doc.getString("audioTitle") ?: "Original Sound"
                            val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()

                            incomingReels.add(
                                ReelVideo(
                                    id = id,
                                    sellerName = sellerName,
                                    sellerHandle = sellerHandle,
                                    caption = caption,
                                    imageResName = imageResName,
                                    viewsCount = viewsCount,
                                    likesCount = likesCount,
                                    commentsCount = commentsCount,
                                    audioTitle = audioTitle,
                                    timestamp = timestamp
                                )
                            )
                        } catch (e: Exception) {
                            Log.e("FirebaseSync", "Error parsing reel: ${e.message}")
                        }
                    }
                    if (incomingReels.isNotEmpty()) {
                        reelDao.insertAll(incomingReels)
                    }
                }
            }
        }
    }

    /**
     * Upload an animal listing from Room into Firebase Firestore
     */
    suspend fun syncListingToFirebase(listing: AnimalListing) = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext
        try {
            val map = hashMapOf(
                "id" to listing.id,
                "title" to listing.title,
                "category" to listing.category,
                "breed" to listing.breed,
                "price" to listing.price,
                "city" to listing.city,
                "description" to listing.description,
                "imageResName" to listing.imageResName,
                "isFeatured" to listing.isFeatured,
                "isNew" to listing.isNew,
                "viewsCount" to listing.viewsCount,
                "postedTimeText" to listing.postedTimeText,
                "sellerName" to listing.sellerName,
                "sellerPhone" to listing.sellerPhone,
                "isUserListing" to listing.isUserListing,
                "isSold" to listing.isSold,
                "ageText" to listing.ageText,
                "teethCount" to listing.teethCount,
                "weightKg" to listing.weightKg,
                "milkCapacity" to listing.milkCapacity,
                "isVaccinated" to listing.isVaccinated,
                "timestamp" to listing.timestamp
            )
            db.collection("listings").document(listing.id.toString())
                .set(map, SetOptions.merge())
                .await()
            _syncStatus.value = FirebaseSyncStatus.Synced(System.currentTimeMillis(), "Uploaded listing #${listing.id} to Firebase")
        } catch (e: Exception) {
            Log.w("FirebaseSync", "Could not upload listing to Firebase: ${e.message}")
        }
    }

    /**
     * Update listing status in Firebase
     */
    suspend fun updateListingInFirebase(listingId: Long, updates: Map<String, Any>) = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext
        try {
            db.collection("listings").document(listingId.toString())
                .update(updates)
                .await()
        } catch (e: Exception) {
            Log.w("FirebaseSync", "Could not update listing in Firebase: ${e.message}")
        }
    }

    /**
     * Upload a new reel into Firebase Firestore
     */
    suspend fun syncReelToFirebase(reel: ReelVideo) = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext
        try {
            val map = hashMapOf(
                "id" to reel.id,
                "sellerName" to reel.sellerName,
                "sellerHandle" to reel.sellerHandle,
                "caption" to reel.caption,
                "imageResName" to reel.imageResName,
                "viewsCount" to reel.viewsCount,
                "likesCount" to reel.likesCount,
                "commentsCount" to reel.commentsCount,
                "audioTitle" to reel.audioTitle,
                "timestamp" to reel.timestamp
            )
            db.collection("reels").document(reel.id.toString())
                .set(map, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            Log.w("FirebaseSync", "Could not upload reel to Firebase: ${e.message}")
        }
    }

    /**
     * Upload a chat message into Firebase Firestore
     */
    suspend fun syncChatMessageToFirebase(message: ChatMessage) = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext
        try {
            val map = hashMapOf(
                "id" to message.id,
                "listingId" to message.listingId,
                "senderName" to message.senderName,
                "text" to message.text,
                "isFromMe" to message.isFromMe,
                "timestamp" to message.timestamp,
                "timeFormatted" to message.timeFormatted
            )
            db.collection("chat_messages").document(message.id.toString())
                .set(map, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            Log.w("FirebaseSync", "Could not upload chat to Firebase: ${e.message}")
        }
    }

    /**
     * Manual Trigger: Sync all local Room entities to Firebase Firestore
     */
    suspend fun syncAllLocalDataToFirebase(
        listings: List<AnimalListing>,
        reels: List<ReelVideo>
    ) = withContext(Dispatchers.IO) {
        val db = firestore
        if (db == null) {
            _syncStatus.value = FirebaseSyncStatus.Offline("Firebase not configured. Add google-services.json to sync.")
            return@withContext
        }

        _syncStatus.value = FirebaseSyncStatus.Syncing
        try {
            for (listing in listings) {
                syncListingToFirebase(listing)
            }
            for (reel in reels) {
                syncReelToFirebase(reel)
            }
            _syncStatus.value = FirebaseSyncStatus.Synced(
                System.currentTimeMillis(),
                "Successfully synchronized all ${listings.size} listings & ${reels.size} reels with Firebase Firestore!"
            )
        } catch (e: Exception) {
            _syncStatus.value = FirebaseSyncStatus.Error("Sync error: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    fun stopListeners() {
        listingListener?.remove()
        reelListener?.remove()
        chatListener?.remove()
    }
}
