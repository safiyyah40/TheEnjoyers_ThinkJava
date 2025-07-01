// File: LeaderboardEntry.kt
package com.example.theenjoyers_thinkjava

data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val score: Int,
    val avatarUrl: String, // Nanti bisa diisi URL gambar dari internet
    val isCurrentUser: Boolean = false // Penanda jika ini adalah user yang sedang login
)