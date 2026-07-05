package com.example.domain.premium

sealed class SubscriptionPlan(val id: String, val name: String, val price: String) {
    object Monthly : SubscriptionPlan("premium_monthly", "Monthly Plan", "$9.99/mo")
    object Yearly : SubscriptionPlan("premium_yearly", "Yearly Plan", "$89.99/yr")
}

data class Order(
    val orderId: String = "",
    val uid: String = "",
    val productId: String = "",
    val purchaseToken: String = "",
    val purchaseTime: Long = 0,
    val amount: Double = 0.0,
    val currency: String = "USD",
    val status: String = "completed"
)

data class PremiumAnalytics(
    val totalRevenue: Double = 0.0,
    val monthlyRevenue: Double = 0.0,
    val totalOrders: Int = 0,
    val activeSubscribers: Int = 0
)
