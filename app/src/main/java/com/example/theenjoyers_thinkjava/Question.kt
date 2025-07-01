package com.example.theenjoyers_thinkjava
import java.io.Serializable

data class Question(
    val id: Int,
    val questionText: String,
    val options: List<String>,
    val correctAnswerIndex: Int
) : Serializable