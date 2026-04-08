package com.example.btlapp

data class Question(
    val id: Int,
    val content: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String?,
    val correctAnswer: Int, // 1, 2, 3, 4
    val explanation: String,
    val imageResId: Int? = null,
    val isCritical: Boolean = false // Câu hỏi điểm liệt
)
