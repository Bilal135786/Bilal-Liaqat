package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AnimalListing
import com.example.ui.MaveshiViewModel
import com.example.ui.Screen
import com.example.ui.screens.AddListingScreen
import com.example.ui.screens.BrowseScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.DeveloperBackendScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ListingDetailScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.PrivacyPolicyScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReelsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MaveshiApp()
                }
            }
        }
    }
}

@Composable
fun MaveshiApp(viewModel: MaveshiViewModel = viewModel()) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsState()
    val rawListings by viewModel.rawListings.collectAsState()
    val featuredListings by viewModel.featuredListings.collectAsState()
    val filteredListings by viewModel.filteredListings.collectAsState()
    val reels by viewModel.reels.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val unreadNotifCount by viewModel.unreadNotifCount.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterCriteria by viewModel.filterCriteria.collectAsState()
    val isGridView by viewModel.isGridView.collectAsState()
    val isUrdu by viewModel.isUrdu.collectAsState()
    val profileTab by viewModel.profileTab.collectAsState()
    val profileListingFilter by viewModel.profileListingFilter.collectAsState()
    val showAddReelDialog by viewModel.showAddReelDialog.collectAsState()
    val showFilterDialog by viewModel.showFilterDialog.collectAsState()
    val showFirebaseDialog by viewModel.showFirebaseDialog.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()
    val isFirebaseAvailable by viewModel.isFirebaseAvailable.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val inquiries by viewModel.inquiries.collectAsState()
    val databaseStats by viewModel.databaseStats.collectAsState()

    if (showFirebaseDialog) {
        com.example.ui.components.FirebaseSyncDialog(
            syncStatus = syncStatus,
            isFirebaseAvailable = isFirebaseAvailable,
            onSyncNow = { viewModel.syncAllWithFirebase() },
            onDismiss = { viewModel.setShowFirebaseDialog(false) }
        )
    }

    // Handle Hardware / Gesture Back Press
    BackHandler(enabled = currentScreen !is Screen.Splash && currentScreen !is Screen.Home) {
        viewModel.navigateBack()
    }

    when (val screen = currentScreen) {
        is Screen.Splash -> {
            SplashScreen(
                onSplashFinished = {
                    viewModel.navigateTo(Screen.Home)
                }
            )
        }

        is Screen.Home -> {
            HomeScreen(
                featuredAnimals = if (featuredListings.isNotEmpty()) featuredListings else rawListings,
                selectedCategory = selectedCategory,
                searchQuery = searchQuery,
                unreadNotifCount = unreadNotifCount,
                onOpenFirebaseSync = { viewModel.setShowFirebaseDialog(true) },
                onSearchChange = { query ->
                    viewModel.setSearchQuery(query)
                    if (query.isNotEmpty()) {
                        viewModel.navigateTo(Screen.Browse)
                    }
                },
                onCategorySelect = { cat ->
                    viewModel.selectCategory(cat)
                    viewModel.navigateTo(Screen.Browse)
                },
                onAnimalClick = { id ->
                    viewModel.viewListingDetail(id)
                },
                onToggleFavorite = { id, currentFav ->
                    viewModel.toggleFavorite(id, currentFav)
                },
                onNavigate = { dest ->
                    viewModel.navigateTo(dest)
                }
            )
        }

        is Screen.Browse -> {
            BrowseScreen(
                listings = filteredListings,
                selectedCategory = selectedCategory,
                searchQuery = searchQuery,
                filterCriteria = filterCriteria,
                isGridView = isGridView,
                unreadNotifCount = unreadNotifCount,
                showFilterDialog = showFilterDialog,
                onOpenFirebaseSync = { viewModel.setShowFirebaseDialog(true) },
                onSearchChange = { viewModel.setSearchQuery(it) },
                onCategorySelect = { viewModel.selectCategory(it) },
                onToggleGridView = { viewModel.toggleGridView() },
                onAnimalClick = { id ->
                    viewModel.viewListingDetail(id)
                },
                onToggleFavorite = { id, currentFav ->
                    viewModel.toggleFavorite(id, currentFav)
                },
                onOpenFilterDialog = { viewModel.setShowFilterDialog(true) },
                onCloseFilterDialog = { viewModel.setShowFilterDialog(false) },
                onApplyFilter = { criteria ->
                    viewModel.updateFilterCriteria(criteria)
                },
                onNavigate = { dest ->
                    viewModel.navigateTo(dest)
                }
            )
        }

        is Screen.Detail -> {
            val animal = rawListings.find { it.id == screen.listingId } ?: rawListings.firstOrNull() ?: AnimalListing(
                title = "Goats",
                category = "Goat",
                breed = "Makhi Cheeni",
                price = 150000,
                city = "Lahore",
                description = "Makhi Cheeni Goat with 2 Kids For Sale",
                imageResName = "goat_cheeni"
            )

            ListingDetailScreen(
                animal = animal,
                onBack = { viewModel.navigateBack() },
                onChatWithSeller = {
                    viewModel.navigateTo(Screen.Chat(animal.id))
                },
                onCallSeller = { phone ->
                    viewModel.callSeller(context, phone)
                },
                onWhatsAppSeller = { phone, msg ->
                    viewModel.openWhatsApp(context, phone, msg)
                },
                onMakeOffer = { price, message, buyerName, buyerPhone ->
                    viewModel.submitBuyerOffer(animal.id, animal.title, buyerName, buyerPhone, price, message)
                },
                onDeleteListing = { id ->
                    viewModel.deleteListing(id)
                }
            )
        }

        is Screen.Reels -> {
            ReelsScreen(
                reels = reels,
                unreadNotifCount = unreadNotifCount,
                viewModel = viewModel,
                onBack = { viewModel.navigateBack() },
                onNavigate = { dest ->
                    viewModel.navigateTo(dest)
                }
            )
        }

        is Screen.Profile -> {
            val userListings = rawListings.filter { it.isUserListing }
            ProfileScreen(
                userListings = if (userListings.isNotEmpty()) userListings else rawListings.take(2),
                userReels = reels,
                profileTab = profileTab,
                listingFilter = profileListingFilter,
                unreadNotifCount = unreadNotifCount,
                isUrdu = isUrdu,
                showAddReelDialog = showAddReelDialog,
                userProfile = userProfile,
                databaseStats = databaseStats,
                inquiries = inquiries,
                onUpdateInquiryStatus = { id, status ->
                    viewModel.updateInquiryStatus(id, status)
                },
                onEditProfile = { name, phone, email, city, bio ->
                    viewModel.updateUserProfile(name, phone, email, city, bio)
                },
                onOpenFirebaseSync = { viewModel.setShowFirebaseDialog(true) },
                onTabChange = { viewModel.setProfileTab(it) },
                onFilterChange = { viewModel.setProfileListingFilter(it) },
                onToggleUrdu = { viewModel.toggleUrduLanguage() },
                onOpenAddReelDialog = { viewModel.setShowAddReelDialog(true) },
                onCloseAddReelDialog = { viewModel.setShowAddReelDialog(false) },
                onAddReel = { caption ->
                    viewModel.addNewReel(caption)
                },
                onAnimalClick = { id ->
                    viewModel.viewListingDetail(id)
                },
                onToggleFavorite = { id, currentFav ->
                    viewModel.toggleFavorite(id, currentFav)
                },
                onNavigate = { dest ->
                    viewModel.navigateTo(dest)
                }
            )
        }

        is Screen.Notifications -> {
            NotificationsScreen(
                notifications = notifications,
                unreadNotifCount = unreadNotifCount,
                searchQuery = searchQuery,
                onOpenFirebaseSync = { viewModel.setShowFirebaseDialog(true) },
                onSearchChange = { viewModel.setSearchQuery(it) },
                onMarkAllRead = { viewModel.markAllNotificationsRead() },
                onNotificationClick = { notif ->
                    viewModel.markNotificationRead(notif.id)
                    notif.listingId?.let { id ->
                        viewModel.viewListingDetail(id)
                    }
                },
                onNavigate = { dest ->
                    viewModel.navigateTo(dest)
                }
            )
        }

        is Screen.Chat -> {
            val animal = rawListings.find { it.id == screen.listingId } ?: rawListings.firstOrNull() ?: AnimalListing(
                title = "Goats",
                category = "Goat",
                breed = "Makhi Cheeni",
                price = 150000,
                city = "Lahore",
                description = "Makhi Cheeni Goat with 2 Kids For Sale",
                imageResName = "goat_cheeni"
            )

            ChatScreen(
                animal = animal,
                viewModel = viewModel,
                onBack = { viewModel.navigateBack() },
                onCallSeller = { phone ->
                    viewModel.callSeller(context, phone)
                }
            )
        }

        is Screen.AddListing -> {
            AddListingScreen(
                isMaintenanceMode = viewModel.isMaintenanceMode(),
                isPhoneBanned = { viewModel.isPhoneBanned(it) },
                onBack = { viewModel.navigateBack() },
                onSubmitListing = { title, category, breed, price, city, desc, imageRes, teeth, age, weight, milk, vacc, phone, imageUri, additionalPhotos ->
                    viewModel.addNewListing(
                        title, category, breed, price, city, desc, imageRes, teeth, age, weight, milk, vacc, phone, imageUri, additionalPhotos
                    )
                }
            )
        }

        is Screen.AddListingWithPhoto -> {
            AddListingScreen(
                initialImageUri = screen.initialImageUri,
                initialCategory = screen.initialCategory,
                isMaintenanceMode = viewModel.isMaintenanceMode(),
                isPhoneBanned = { viewModel.isPhoneBanned(it) },
                onBack = { viewModel.navigateBack() },
                onSubmitListing = { title, category, breed, price, city, desc, imageRes, teeth, age, weight, milk, vacc, phone, imageUri, additionalPhotos ->
                    viewModel.addNewListing(
                        title, category, breed, price, city, desc, imageRes, teeth, age, weight, milk, vacc, phone, imageUri, additionalPhotos
                    )
                }
            )
        }

        is Screen.DeveloperBackend -> {
            DeveloperBackendScreen(
                viewModel = viewModel,
                onBack = { viewModel.navigateBack() },
                onOpenPrivacyPolicy = { viewModel.navigateTo(Screen.PrivacyPolicy) }
            )
        }

        is Screen.PrivacyPolicy -> {
            PrivacyPolicyScreen(
                onBack = { viewModel.navigateBack() }
            )
        }
    }
}
