package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reel_videos")
data class ReelVideo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sellerName: String = "Nouman Zamurd",
    val sellerHandle: String = "@noumanzamurd",
    val caption: String = "#song #music #bollywood #newsong #love #irani #irani teeter #kalateterkidunya",
    val imageResName: String = "irani_teeter",
    val videoUri: String? = null,
    val viewsCount: Int = 3,
    val likesCount: Int = 1,
    val commentsCount: Int = 2,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val isFollowing: Boolean = false,
    val audioTitle: String = "Original Sound - Nouman Zamurd",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "reel_comments")
data class ReelComment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val reelId: Long,
    val authorName: String,
    val commentText: String,
    val timeAgo: String = "Just now",
    val timestamp: Long = System.currentTimeMillis()
)
