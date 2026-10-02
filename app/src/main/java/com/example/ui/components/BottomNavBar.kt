package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.Screen
import com.example.ui.theme.MandiOrangePrimary

@Composable
fun BottomNavBar(
    currentScreen: Screen,
    unreadNotifCount: Int,
    isDarkTheme: Boolean = false,
    onOpenFirebaseSync: () -> Unit = {},
    onNavigate: (Screen) -> Unit
) {
    val navBg = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White
    val selectedColor = MandiOrangePrimary
    val unselectedColor = if (isDarkTheme) Color(0xFF9E9E9E) else Color(0xFF9E9E9E)

    Surface(
        color = navBg,
        shadowElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Home
            val isHome = currentScreen is Screen.Home
            NavItem(
                isSelected = isHome,
                selectedColor = selectedColor,
                unselectedColor = unselectedColor,
                onClick = { onNavigate(Screen.Home) },
                testTag = "nav_home"
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = if (isHome) selectedColor else unselectedColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            // 2. Search / Browse
            val isBrowse = currentScreen is Screen.Browse
            NavItem(
                isSelected = isBrowse,
                selectedColor = selectedColor,
                unselectedColor = unselectedColor,
                onClick = { onNavigate(Screen.Browse) },
                testTag = "nav_search"
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = if (isBrowse) selectedColor else unselectedColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            // 3. Reels / Videos
            val isReels = currentScreen is Screen.Reels
            NavItem(
                isSelected = isReels,
                selectedColor = selectedColor,
                unselectedColor = unselectedColor,
                onClick = { onNavigate(Screen.Reels) },
                testTag = "nav_reels"
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isReels) MandiOrangePrimary else Color(0xFFB0B7C3)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Reels",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // 4. Notifications / Messages
            val isNotifications = currentScreen is Screen.Notifications
            NavItem(
                isSelected = isNotifications,
                selectedColor = selectedColor,
                unselectedColor = unselectedColor,
                onClick = { onNavigate(Screen.Notifications) },
                testTag = "nav_notifications"
            ) {
                BadgedBox(
                    badge = {
                        if (unreadNotifCount > 0) {
                            Badge(
                                containerColor = Color(0xFFEF4444),
                                contentColor = Color.White,
                                modifier = Modifier.offset(x = 4.dp, y = (-4).dp)
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
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Notifications",
                        tint = if (isNotifications) selectedColor else unselectedColor,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            // 5. Settings / Firebase Cloud Sync
            NavItem(
                isSelected = false,
                selectedColor = selectedColor,
                unselectedColor = unselectedColor,
                onClick = onOpenFirebaseSync,
                testTag = "nav_settings"
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = unselectedColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            // 6. Profile Avatar
            val isProfile = currentScreen is Screen.Profile
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .border(
                        width = if (isProfile) 2.dp else 1.dp,
                        color = if (isProfile) MandiOrangePrimary else Color.Transparent,
                        shape = CircleShape
                    )
                    .padding(2.dp)
                    .clip(CircleShape)
                    .clickable { onNavigate(Screen.Profile) }
                    .testTag("nav_profile"),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.nili_buffalo),
                    contentDescription = "Profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    isSelected: Boolean,
    selectedColor: Color,
    unselectedColor: Color,
    onClick: () -> Unit,
    testTag: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        content()
        if (isSelected) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(width = 16.dp, height = 3.dp)
                    .clip(CircleShape)
                    .background(selectedColor)
            )
        }
    }
}
