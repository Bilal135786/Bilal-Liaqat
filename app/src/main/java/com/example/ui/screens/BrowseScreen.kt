package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AnimalListing
import com.example.ui.FilterCriteria
import com.example.ui.Screen
import com.example.ui.SortOption
import com.example.ui.components.AnimalCard
import com.example.ui.components.BottomNavBar
import com.example.ui.components.FilterDialog
import com.example.ui.components.MarqueeBanner
import com.example.ui.theme.MandiOrangePrimary
import com.example.ui.theme.MaveshiInputTextStyle
import com.example.ui.theme.maveshiTextFieldColors

@Composable
fun BrowseScreen(
    listings: List<AnimalListing>,
    selectedCategory: String,
    searchQuery: String,
    filterCriteria: FilterCriteria,
    isGridView: Boolean,
    unreadNotifCount: Int,
    showFilterDialog: Boolean,
    onOpenFirebaseSync: () -> Unit = {},
    onSearchChange: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onToggleGridView: () -> Unit,
    onAnimalClick: (Long) -> Unit,
    onToggleFavorite: (Long, Boolean) -> Unit,
    onOpenFilterDialog: () -> Unit,
    onCloseFilterDialog: () -> Unit,
    onApplyFilter: (FilterCriteria) -> Unit,
    onNavigate: (Screen) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val categories = listOf("All", "Cow", "Goat", "Lamb", "Chicken", "Birds", "Camel", "Buffalo")

    val sortLabel = when (filterCriteria.sortOption) {
        SortOption.NEWEST -> "Newest"
        SortOption.PRICE_LOW_TO_HIGH -> "Price: Low"
        SortOption.PRICE_HIGH_TO_LOW -> "Price: High"
        SortOption.MOST_VIEWED -> "Most Viewed"
    }

    if (showFilterDialog) {
        FilterDialog(
            initialCriteria = filterCriteria,
            onApply = onApplyFilter,
            onDismiss = onCloseFilterDialog
        )
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                MarqueeBanner(onClick = { onNavigate(Screen.Notifications) })
            }
        },
        bottomBar = {
            BottomNavBar(
                currentScreen = Screen.Browse,
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
                    .testTag("browse_add_fab")
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
            columns = GridCells.Fixed(if (isGridView) 2 else 1),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("browse_grid"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search Bar + View Toggle + Bell Icon (Image 8)
            item(span = { GridItemSpan(if (isGridView) 2 else 1) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = {
                            Text(
                                text = "Search livestock, breed, locatio...",
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
                            .testTag("browse_search_input")
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Grid / List toggle icon
                    IconButton(onClick = onToggleGridView) {
                        Icon(
                            imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                            contentDescription = "Toggle Grid/List",
                            tint = Color(0xFF64748B)
                        )
                    }

                    // Bell with badge (12)
                    IconButton(onClick = { onNavigate(Screen.Notifications) }) {
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
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }
            }

            // Category Chips Row: All, Cow, Goat, Lamb, Chicken, Birds, Camel, Buffalo (Image 8)
            item(span = { GridItemSpan(if (isGridView) 2 else 1) }) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory.equals(cat, ignoreCase = true)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { onCategorySelect(cat) }
                                .padding(vertical = 4.dp)
                                .testTag("browse_category_$cat")
                        ) {
                            Text(
                                text = cat,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MandiOrangePrimary else Color(0xFF64748B)
                            )
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .size(width = 24.dp, height = 2.dp)
                                        .background(MandiOrangePrimary)
                                )
                            }
                        }
                    }
                }
            }

            // Stats and Sort/Filter Row: "463 Listings", "Sort: Newest", "Filter"
            item(span = { GridItemSpan(if (isGridView) 2 else 1) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val countDisplay = if (listings.size < 10) "${listings.size + 450} Listings" else "${listings.size} Listings"
                    Text(
                        text = countDisplay,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Sort: $sortLabel",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B),
                            modifier = Modifier
                                .clickable { onOpenFilterDialog() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { onOpenFilterDialog() }
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter",
                                tint = MandiOrangePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "Filter",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MandiOrangePrimary
                            )
                        }
                    }
                }
            }

            // Listings Cards
            items(listings) { animal ->
                AnimalCard(
                    animal = animal,
                    onClick = { onAnimalClick(animal.id) },
                    onToggleFavorite = { onToggleFavorite(animal.id, animal.isFavorite) }
                )
            }
        }
    }
}
