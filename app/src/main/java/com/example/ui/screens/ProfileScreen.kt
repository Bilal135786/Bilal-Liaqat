package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AnimalListing
import com.example.data.BuyerInquiry
import com.example.data.DatabaseStats
import com.example.data.ReelVideo
import com.example.data.UserProfile
import com.example.ui.Screen
import com.example.ui.components.AnimalCard
import com.example.ui.components.BottomNavBar
import com.example.ui.components.MarqueeBanner
import com.example.ui.theme.MandiOrangePrimary
import com.example.ui.theme.MaveshiInputTextStyle
import com.example.ui.theme.maveshiTextFieldColors
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.window.Dialog

@Composable
fun ProfileScreen(
    userListings: List<AnimalListing>,
    userReels: List<ReelVideo>,
    profileTab: Int,
    listingFilter: String,
    unreadNotifCount: Int,
    isUrdu: Boolean,
    showAddReelDialog: Boolean,
    userProfile: UserProfile? = null,
    databaseStats: DatabaseStats? = null,
    inquiries: List<BuyerInquiry> = emptyList(),
    onUpdateInquiryStatus: (Long, String) -> Unit = { _, _ -> },
    onEditProfile: (String, String, String, String, String) -> Unit = { _, _, _, _, _ -> },
    onOpenFirebaseSync: () -> Unit = {},
    onTabChange: (Int) -> Unit,
    onFilterChange: (String) -> Unit,
    onToggleUrdu: () -> Unit,
    onOpenAddReelDialog: () -> Unit,
    onCloseAddReelDialog: () -> Unit,
    onAddReel: (String) -> Unit,
    onAnimalClick: (Long) -> Unit,
    onToggleFavorite: (Long, Boolean) -> Unit,
    onNavigate: (Screen) -> Unit
) {
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(userProfile?.fullName ?: "Zeeshan Zamurd") }
    var editPhone by remember { mutableStateOf(userProfile?.phone ?: "03038692236") }
    var editEmail by remember { mutableStateOf(userProfile?.email ?: "zeeshan.zamurd@gmail.com") }
    var editCity by remember { mutableStateOf(userProfile?.city ?: "Lahore, Punjab") }
    var editBio by remember { mutableStateOf(userProfile?.bio ?: "Certified livestock breeder in Lahore.") }

    if (showEditProfileDialog) {
        Dialog(onDismissRequest = { showEditProfileDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Edit Profile / پروفائل تبدیل کریں",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaveshiInputTextStyle,
                        colors = maveshiTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaveshiInputTextStyle,
                        colors = maveshiTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaveshiInputTextStyle,
                        colors = maveshiTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = editCity,
                        onValueChange = { editCity = it },
                        label = { Text("City / Location") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaveshiInputTextStyle,
                        colors = maveshiTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("Bio / Description") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaveshiInputTextStyle,
                        colors = maveshiTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showEditProfileDialog = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                onEditProfile(editName, editPhone, editEmail, editCity, editBio)
                                showEditProfileDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MandiOrangePrimary),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Save Profile", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showAddReelDialog) {
        AddReelBottomSheet(
            onDismiss = onCloseAddReelDialog,
            onConfirmUpload = onAddReel,
            isUrdu = isUrdu
        )
    }

    val activeListings = userListings.filter { !it.isSold }
    val soldListings = userListings.filter { it.isSold }
    val displayedListings = if (listingFilter == "Active") activeListings else soldListings

    Scaffold(
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                MarqueeBanner(onClick = { onNavigate(Screen.Notifications) })
            }
        },
        bottomBar = {
            BottomNavBar(
                currentScreen = Screen.Profile,
                unreadNotifCount = unreadNotifCount,
                onOpenFirebaseSync = onOpenFirebaseSync,
                onNavigate = onNavigate
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("profile_screen_grid"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Profile Header Row with Name, Phone, Create Button, and Language Switcher
            item(span = { GridItemSpan(2) }) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Title or back
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { onNavigate(Screen.Home) }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color(0xFF1E293B)
                                )
                            }
                            Text(
                                text = if (isUrdu) "میری پروفائل" else "My Profile",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        }

                        // Language Toggle: EN / اردو
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFFFF3E0))
                                .clickable { onToggleUrdu() }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("language_toggle_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Translate,
                                    contentDescription = "Language",
                                    tint = MandiOrangePrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isUrdu) "English" else "اردو",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MandiOrangePrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Profile Card (Image 3 & Image 4)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .border(2.dp, MandiOrangePrimary, CircleShape)
                                .padding(3.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.nili_buffalo),
                                contentDescription = "Profile Picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = userProfile?.fullName ?: "Zeeshan Zamurd",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified Breeder",
                                    tint = Color(0xFF1E7F34),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = userProfile?.phone ?: "03038692236",
                                fontSize = 13.sp,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = userProfile?.city ?: "Lahore, Punjab",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        // Edit Profile Button
                        IconButton(
                            onClick = { showEditProfileDialog = true },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFFF1F5F9))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = MandiOrangePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))

                        // Orange "Create" Button (Image 3)
                        Button(
                            onClick = { onNavigate(Screen.AddListing) },
                            colors = ButtonDefaults.buttonColors(containerColor = MandiOrangePrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("profile_create_button")
                        ) {
                            Text(
                                text = if (isUrdu) "نئی لسٹنگ" else "Create",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action Button: "ریل شامل کریں" / "Add Reel" (Image 4)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onNavigate(Screen.AddListing) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (isUrdu) "نئی لسٹنگ" else "New Listing",
                                color = MandiOrangePrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = onOpenAddReelDialog,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF475569)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("profile_add_reel_button")
                        ) {
                            Text(
                                text = if (isUrdu) "ریل شامل کریں" else "Add Reel",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Firebase Cloud Sync Action Tile
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenFirebaseSync() }
                            .testTag("profile_firebase_sync_card")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Firebase Sync",
                                    tint = MandiOrangePrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isUrdu) "فائر بیس کلاؤڈ اور روم سِنک" else "Room & Firebase Cloud Sync",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF1E293B)
                                    )
                                    Text(
                                        text = if (isUrdu) "آف لائن لوکل ڈیٹا فائر بیس سے جڑا ہے" else "Offline local cache connected to Firestore",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Open",
                                tint = MandiOrangePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Owner Security & Control Center Tile
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate(Screen.DeveloperBackend) }
                            .testTag("profile_developer_backend_card")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE11D48).copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = "Owner Security",
                                        tint = MandiOrangePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (isUrdu) "مالک سکیورٹی اور کنٹرول سینٹر" else "Owner Security & Control Center",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFF22C55E).copy(alpha = 0.25f))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = "OWNER PIN",
                                                color = Color(0xFF4ADE80),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Text(
                                        text = if (isUrdu) "بلال کھچی (مالک) • مکمل کنٹرول اور گوگل پلے سیفٹی" else "Bilal Khichi (Owner) • Full security & Google Play control",
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Open",
                                tint = MandiOrangePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Privacy Policy & Google Play Data Safety Tile
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate(Screen.PrivacyPolicy) }
                            .testTag("profile_privacy_policy_card")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF0F766E).copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = "Privacy Policy",
                                        tint = Color(0xFF0F766E),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Privacy Policy & Data Safety",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFF0F172A)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFF16A34A).copy(alpha = 0.15f))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = "PLAY STORE",
                                                color = Color(0xFF16A34A),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Text(
                                        text = "Google Play Data Safety & User Rights disclosures",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Open",
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats Row: 9 Listings, 4 Videos, 7 SOLD, 0 Followers, 4 Following (Image 3)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem(
                            count = "9",
                            label = if (isUrdu) "فہرست" else "Listings"
                        )
                        StatItem(
                            count = "4",
                            label = if (isUrdu) "ویڈیوز" else "Videos"
                        )
                        StatItem(
                            count = "7",
                            label = if (isUrdu) "فروخت شدہ" else "SOLD"
                        )
                        StatItem(
                            count = "0",
                            label = "Followers"
                        )
                        StatItem(
                            count = "4",
                            label = "Following"
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tabs: Grid Icon vs Play Icon (Image 3)
                    TabRow(
                        selectedTabIndex = profileTab,
                        containerColor = Color.White,
                        contentColor = MandiOrangePrimary,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[profileTab]),
                                color = MandiOrangePrimary,
                                height = 3.dp
                            )
                        }
                    ) {
                        Tab(
                            selected = profileTab == 0,
                            onClick = { onTabChange(0) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.GridView,
                                    contentDescription = "Grid Listings",
                                    tint = if (profileTab == 0) MandiOrangePrimary else Color(0xFF94A3B8)
                                )
                            }
                        )
                        Tab(
                            selected = profileTab == 1,
                            onClick = { onTabChange(1) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Videos",
                                    tint = if (profileTab == 1) MandiOrangePrimary else Color(0xFF94A3B8)
                                )
                            }
                        )
                        Tab(
                            selected = profileTab == 2,
                            onClick = { onTabChange(2) },
                            icon = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocalOffer,
                                        contentDescription = "Offers & Inquiries",
                                        tint = if (profileTab == 2) MandiOrangePrimary else Color(0xFF94A3B8)
                                    )
                                    if (inquiries.isNotEmpty()) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = inquiries.size.toString(),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (profileTab == 2) MandiOrangePrimary else Color(0xFF94A3B8)
                                        )
                                    }
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Filter Pills: "Active Listings (2)" and "Sold Listings (7)" (Image 3)
                    if (profileTab == 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val isActive = listingFilter == "Active"
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isActive) MandiOrangePrimary else Color(0xFFF1F5F9))
                                    .clickable { onFilterChange("Active") }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = if (isUrdu) "فعال لسٹنگ (${activeListings.size})" else "Active Listings (${activeListings.size})",
                                    color = if (isActive) Color.White else Color(0xFF64748B),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            val isSold = listingFilter == "Sold"
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSold) MandiOrangePrimary else Color(0xFFF1F5F9))
                                    .clickable { onFilterChange("Sold") }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = if (isUrdu) "فروخت شدہ لسٹنگ (7)" else "Sold Listings (7)",
                                    color = if (isSold) Color.White else Color(0xFF64748B),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Tab 0: Listings Grid (Image 3: Cow Rs 300,000, Buffalo Rs 400,000)
            if (profileTab == 0) {
                if (displayedListings.isEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No listings found in this section.",
                                color = Color(0xFF94A3B8),
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    items(displayedListings) { animal ->
                        AnimalCard(
                            animal = animal,
                            onClick = { onAnimalClick(animal.id) },
                            onToggleFavorite = { onToggleFavorite(animal.id, animal.isFavorite) }
                        )
                    }
                }
            } else if (profileTab == 1) {
                // Tab 1: Video Reels Showcase
                items(userReels) { reel ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clickable { onNavigate(Screen.Reels) }
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Image(
                                painter = painterResource(id = R.drawable.irani_teeter),
                                contentDescription = "User Reel",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .align(Alignment.Center)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.6f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = Color.White
                                )
                            }
                            Text(
                                text = "${reel.viewsCount} views",
                                color = Color.White,
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            } else {
                // Tab 2: Buyer Inquiries & Offers (persisted in Room Database)
                if (inquiries.isEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No buyer offers or inquiries yet.",
                                color = Color(0xFF94A3B8),
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    items(inquiries) { inq ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = inq.buyerName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color(0xFF1E293B)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                when (inq.status) {
                                                    "ACCEPTED" -> Color(0xFFDCFCE7)
                                                    "REJECTED" -> Color(0xFFFEE2E2)
                                                    else -> Color(0xFFFEF3C7)
                                                }
                                            )
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = inq.status,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (inq.status) {
                                                "ACCEPTED" -> Color(0xFF15803D)
                                                "REJECTED" -> Color(0xFFB91C1C)
                                                else -> Color(0xFFB45309)
                                            }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Item: ${inq.listingTitle}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = "Offered: Rs ${java.text.NumberFormat.getNumberInstance(java.util.Locale.US).format(inq.offerPrice)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E7F34)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "\"${inq.message}\"",
                                    fontSize = 12.sp,
                                    color = Color(0xFF334155)
                                )

                                if (inq.status == "PENDING") {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { onUpdateInquiryStatus(inq.id, "ACCEPTED") },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E7F34)),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Accept", fontSize = 12.sp, color = Color.White)
                                        }
                                        OutlinedButton(
                                            onClick = { onUpdateInquiryStatus(inq.id, "REJECTED") },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Decline", fontSize = 12.sp, color = Color(0xFFDC2626))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Database & Backend Engine Status Card
            item(span = { GridItemSpan(2) }) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 24.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storage,
                                contentDescription = "Database",
                                tint = Color(0xFF1E7F34),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Complete Backend & Room Database",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• Room SQLite Database v2 with KSP persistence\n" +
                                   "• Realtime cloud sync with Firebase Firestore\n" +
                                   "• Total Listings: ${databaseStats?.totalListings ?: userListings.size} | Buyer Inquiries: ${inquiries.size}\n" +
                                   "• Seeded catalog across all Pakistani livestock breeds",
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = Color(0xFF475569)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF64748B)
        )
    }
}
