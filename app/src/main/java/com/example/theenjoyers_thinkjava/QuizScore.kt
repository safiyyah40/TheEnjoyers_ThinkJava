package com.example.theenjoyers_thinkjava

import com.google.firebase.Timestamp

// Data class untuk menampung data skor dari Firestore
data class QuizScore(
    val category: String = "",
    val score: Long = 0,
    val timestamp: Timestamp? = null
)

