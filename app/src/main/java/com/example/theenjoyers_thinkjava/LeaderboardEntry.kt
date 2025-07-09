package com.example.theenjoyers_thinkjava

data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val score: Int,
    val avatarUrl: String,
    val isCurrentUser: Boolean = false
)