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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AnimalListing
import com.example.ui.Screen
import com.example.ui.components.AnimalCard
import com.example.ui.components.AnimalPhotoStudioDialog
import com.example.ui.components.BottomNavBar
import com.example.ui.components.MarqueeBanner
import com.example.ui.theme.MandiGreenNew
import com.example.ui.theme.MandiOrangePrimary
import com.example.ui.theme.MaveshiInputTextStyle
import com.example.ui.theme.maveshiTextFieldColors

data class CategoryItem(
    val name: String,
    val iconRes: Int
)

@Composable
fun HomeScreen(
    featuredAnimals: List<AnimalListing>,
    selectedCategory: String,
    searchQuery: String,
    unreadNotifCount: Int,
    onOpenFirebaseSync: () -> Unit = {},
    onSearchChange: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onAnimalClick: (Long) -> Unit,
    onToggleFavorite: (Long, Boolean) -> Unit,
    onNavigate: (Screen) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var showPhotoStudio by remember { mutableStateOf(false) }

    if (showPhotoStudio) {
        AnimalPhotoStudioDialog(
            onDismiss = { showPhotoStudio = false },
            onProceedWithPhoto = { uri, category ->
                showPhotoStudio = false
                onNavigate(Screen.AddListingWithPhoto(initialImageUri = uri, initialCategory = category))
            }
        )
    }

    val categories = listOf(
        CategoryItem("Cow", R.drawable.sahiwal_cow),
        CategoryItem("Goat", R.drawable.goat_cheeni),
        CategoryItem("Lamb", R.drawable.maveshi_splash),
        CategoryItem("Chicken", R.drawable.chickens),
        CategoryItem("Birds", R.drawable.irani_teeter),
        CategoryItem("Buffalo", R.drawable.nili_buffalo),
        CategoryItem("Camel", R.drawable.maveshi_splash)
    )

    Scaffold(
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                MarqueeBanner(
                    onClick = { onNavigate(Screen.Notifications) }
                )
            }
        },
        bottomBar = {
            BottomNavBar(
                currentScreen = Screen.Home,
                unreadNotifCount = unreadNotifCount,
                onOpenFirebaseSync = onOpenFirebaseSync,
                onNavigate = onNavigate
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigate(Screen.AddListing) },
                containerColor = MandiOrangePrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .size(56.dp)
                    .testTag("home_add_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Listing",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        containerColor = Color(0xFFF8F9FA)
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("home_grid"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Mall Maweshi Brand Header Card
            item(span = { GridItemSpan(2) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            androidx.compose.ui.graphics.Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF1E7F34),
                                    Color(0xFF146025)
                                )
                            )
                        )
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .border(2.dp, Color(0x88FFFFFF), CircleShape)
                                    .padding(2.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.mall_maweshi_logo),
                                    contentDescription = "Mall Maweshi Logo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "Mall Maweshi",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Pakistan's online cattle & livestock marketplace",
                                    fontSize = 11.sp,
                                    color = Color(0xFFD1FAE5),
                                    maxLines = 1
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x33000000))
                                    .clickable { onNavigate(Screen.DeveloperBackend) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .testTag("home_owner_security_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = "Owner Security",
                                        tint = MandiOrangePrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Owner Security",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x33000000))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "مال مویشی",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F)
                                )
                            }
                        }
                    }
                }
            }

            // Search Bar & Notification Bell
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = {
                            Text(
                                text = "Search livestock, breed, location...",
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color(0xFF94A3B8)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchChange("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = Color(0xFF94A3B8)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaveshiInputTextStyle,
                        colors = maveshiTextFieldColors(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("search_input")
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Camera Action Icon Button to take or select animal photo
                    IconButton(
                        onClick = { showPhotoStudio = true },
                        modifier = Modifier.testTag("home_camera_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(MandiOrangePrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Take Animal Photo",
                                tint = MandiOrangePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Bell Icon with Red Badge (11) as in image 1
                    IconButton(
                        onClick = { onNavigate(Screen.Notifications) },
                        modifier = Modifier.testTag("home_bell_icon")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotifCount > 0) {
                                    Badge(
                                        containerColor = Color(0xFFEF4444),
                                        contentColor = Color.White,
                                        modifier = Modifier.offset(x = 2.dp, y = (-2).dp)
                                    ) {
                                        Text(
                                            text = if (unreadNotifCount > 99) "99+" else "$unreadNotifCount",
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color(0xFF1E293B),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onOpenFirebaseSync,
                        modifier = Modifier.testTag("home_cloud_sync_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Firebase Sync",
                            tint = MandiOrangePrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            // Quick Animal Camera & Photo Studio Banner Card
            item(span = { GridItemSpan(2) }) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFED7AA)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_animal_camera_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(MandiOrangePrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Animal Camera & Photos",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF9A3412)
                                )
                                Text(
                                    text = "Take live photo with camera or select from gallery",
                                    fontSize = 11.sp,
                                    color = Color(0xFFC2410C),
                                    maxLines = 1
                                )
                            }
                        }

                        Button(
                            onClick = { showPhotoStudio = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MandiOrangePrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("open_photo_studio_button")
                        ) {
                            Text("Open Studio", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            // Categories Row (Cow, Goat, Lamb, Chicken, Birds, etc.)
            item(span = { GridItemSpan(2) }) {
                Column(modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(categories) { cat ->
                            val isSelected = selectedCategory.equals(cat.name, ignoreCase = true)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable {
                                        if (isSelected) onCategorySelect("All") else onCategorySelect(cat.name)
                                    }
                                    .testTag("category_${cat.name.lowercase()}")
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.White)
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) MandiOrangePrimary else Color(0xFFE2E8F0),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = cat.iconRes),
                                        contentDescription = cat.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = cat.name,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MandiOrangePrimary else Color(0xFF1E293B)
                                )
                            }
                        }
                    }
                }
            }

            // Section Header: "Featured Animals" + "View all"
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Featured Animals",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "Discover premium livestock from trusted sellers",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    TextButton(
                        onClick = { onNavigate(Screen.Browse) },
                        modifier = Modifier.testTag("view_all_button")
                    ) {
                        Text(
                            text = "View all",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MandiOrangePrimary
                        )
                    }
                }
            }

            // Featured Animals Grid
            items(featuredAnimals) { animal ->
                AnimalCard(
                    animal = animal,
                    onClick = { onAnimalClick(animal.id) },
                    onToggleFavorite = { onToggleFavorite(animal.id, animal.isFavorite) }
                )
            }
        }
    }
}
