package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.ReelComment
import com.example.data.ReelVideo
import com.example.ui.MaveshiViewModel
import com.example.ui.Screen
import com.example.ui.components.BottomNavBar
import com.example.ui.theme.MaveshiInputTextStyle
import com.example.ui.theme.maveshiTextFieldColors
import com.example.ui.theme.MandiOrangePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReelsScreen(
    reels: List<ReelVideo>,
    unreadNotifCount: Int,
    viewModel: MaveshiViewModel,
    onBack: () -> Unit,
    onNavigate: (Screen) -> Unit
) {
    val context = LocalContext.current
    var currentReelIndex by remember { mutableIntStateOf(0) }
    var selectedTab by remember { mutableStateOf("For You") } // For You, Following, Discover
    var isExpandedDescription by remember { mutableStateOf(false) }
    var showCommentsSheet by remember { mutableStateOf(false) }
    var newCommentText by remember { mutableStateOf("") }

    val activeReel = reels.getOrNull(currentReelIndex) ?: ReelVideo(
        sellerName = "Nouman Zamurd",
        sellerHandle = "@noumanzamurd",
        caption = "#song #music #bollywood #newsong #love #irani #irani teeter #kalateterkidunya",
        imageResName = "irani_teeter"
    )

    val commentsState = if (activeReel.id > 0) {
        viewModel.getReelComments(activeReel.id).collectAsState(initial = emptyList()).value
    } else {
        emptyList()
    }

    val sheetState = rememberModalBottomSheetState()

    val drawableId = when (activeReel.imageResName) {
        "irani_teeter" -> R.drawable.irani_teeter
        "goat_cheeni" -> R.drawable.goat_cheeni
        "sahiwal_cow" -> R.drawable.sahiwal_cow
        else -> R.drawable.irani_teeter
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentScreen = Screen.Reels,
                unreadNotifCount = unreadNotifCount,
                isDarkTheme = true,
                onOpenFirebaseSync = { viewModel.setShowFirebaseDialog(true) },
                onNavigate = onNavigate
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("reels_screen")
        ) {
            // Fullscreen Reel Image/Video display
            Image(
                painter = painterResource(id = drawableId),
                contentDescription = "Reel Video",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        // Cycle to next reel if available
                        if (reels.isNotEmpty()) {
                            currentReelIndex = (currentReelIndex + 1) % reels.size
                        }
                    }
            )

            // Top gradient overlay for status readability
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)
                        )
                    )
            )

            // Bottom gradient overlay for caption readability
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                        )
                    )
            )

            // Top Navigation & Tabs: "For You", "Following", "Discover" (Image 5)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("For You", "Following", "Discover").forEach { tab ->
                            val isSelected = selectedTab == tab
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { selectedTab = tab }
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = tab,
                                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                                    fontSize = 16.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .size(width = 30.dp, height = 2.5.dp)
                                            .clip(CircleShape)
                                            .background(MandiOrangePrimary)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            // Right Action Sidebar (Image 5: Avatar with +, Heart, Comment, Star, Share)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 12.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // 1. Seller Avatar with Red/Orange "+" Follow badge
                Box(
                    modifier = Modifier.size(54.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.maveshi_splash),
                        contentDescription = "Seller Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, Color.White, CircleShape)
                    )

                    // Follow Plus Badge
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.BottomCenter)
                            .offset(y = 2.dp)
                            .clip(CircleShape)
                            .background(if (activeReel.isFollowing) Color(0xFF22C55E) else Color(0xFFEF4444))
                            .clickable { viewModel.toggleReelFollow(activeReel.id) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (activeReel.isFollowing) Icons.Default.Check else Icons.Default.Add,
                            contentDescription = "Follow",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // 2. Heart (Like) button with count (Image 5)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = { viewModel.toggleReelLike(activeReel.id, activeReel.isLiked) },
                        modifier = Modifier.testTag("reel_like_button")
                    ) {
                        Icon(
                            imageVector = if (activeReel.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (activeReel.isLiked) Color(0xFFEF4444) else Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                    Text(
                        text = "${activeReel.likesCount}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // 3. Comment icon with count (Image 5)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = { showCommentsSheet = true },
                        modifier = Modifier.testTag("reel_comment_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Comments",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Text(
                        text = "${activeReel.commentsCount}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // 4. Star / Bookmark icon
                IconButton(
                    onClick = { viewModel.toggleReelBookmark(activeReel.id) },
                    modifier = Modifier.testTag("reel_bookmark_button")
                ) {
                    Icon(
                        imageVector = if (activeReel.isBookmarked) Icons.Default.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (activeReel.isBookmarked) MandiOrangePrimary else Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // 5. Share icon
                IconButton(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Watch ${activeReel.sellerName}'s livestock reel on Mall Maweshi! ${activeReel.caption}"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Reel"))
                    },
                    modifier = Modifier.testTag("reel_share_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Bottom Info Overlay: Seller Name, Hashtags, "See more", Views count (Image 5)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth(0.78f)
                    .padding(start = 16.dp, bottom = 24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.maveshi_splash),
                        contentDescription = "Seller",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = activeReel.sellerName,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = activeReel.sellerHandle,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Hashtags
                Text(
                    text = activeReel.caption,
                    color = Color.White,
                    fontSize = 13.sp,
                    maxLines = if (isExpandedDescription) 6 else 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = if (isExpandedDescription) "See less" else "See more",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { isExpandedDescription = !isExpandedDescription }
                        .padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Views Count: "3 VIEWS" (Image 5)
                Text(
                    text = "${activeReel.viewsCount} VIEWS",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // Interactive Comments Sheet
    if (showCommentsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCommentsSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Comments (${commentsState.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    IconButton(onClick = { showCommentsSheet = false }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                ) {
                    if (commentsState.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No comments yet. Be the first to comment!",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else {
                        items(commentsState) { comment ->
                            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = comment.authorName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                    Text(
                                        text = comment.timeAgo,
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                                Text(
                                    text = comment.commentText,
                                    fontSize = 13.sp,
                                    color = Color(0xFF475569)
                                )
                            }
                        }
                    }
                }

                // Add comment row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        placeholder = { Text("Add a comment...", fontSize = 13.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        textStyle = MaveshiInputTextStyle,
                        colors = maveshiTextFieldColors()
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newCommentText.isNotBlank()) {
                                viewModel.addReelComment(activeReel.id, "You", newCommentText)
                                newCommentText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MandiOrangePrimary)
                    ) {
                        Text("Post")
                    }
                }
            }
        }
    }
}
