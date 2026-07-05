package com.example.domain.user

data class UserStats(
    val streakCount: Int = 0,
    val lastLoginDate: Long = 0,
    val totalWordsLearned: Int = 0,
    val isPremium: Boolean = false,
    val subscriptionType: String = "free",
    val expiryDate: Long = 0
)
