package com.focuscode.data.model

data class Challenge(
    val id: String,
    val category: String,
    val difficulty: String,
    val type: String,
    val question: String,
    val code: String?,
    val answers: List<String>,
    val correctAnswer: Int,
    val explanation: String?
)
