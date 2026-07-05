package com.example.data.premium

import android.util.Log
import com.example.domain.premium.Order
import com.example.domain.user.UserStats
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class FirestoreManager {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun getUserStats(): Flow<UserStats?> {
        val uid = auth.currentUser?.uid ?: return flowOf(null)
        return callbackFlow {
            val listener = firestore.collection("users").document(uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("FirestoreManager", "Error listening to user stats", error)
                        // Don't close the flow on error, just send null or keep waiting
                        trySend(null)
                        return@addSnapshotListener
                    }
                    try {
                        val stats = snapshot?.toObject(UserStats::class.java)
                        trySend(stats)
                    } catch (e: Exception) {
                        Log.e("FirestoreManager", "Error parsing UserStats", e)
                        trySend(null)
                    }
                }
            awaitClose { listener.remove() }
        }
    }

    suspend fun updateStreak() {
        val uid = auth.currentUser?.uid ?: return
        val userRef = firestore.collection("users").document(uid)
        
        try {
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val lastLogin = snapshot.getLong("lastLoginDate") ?: 0L
                val currentStreak = snapshot.getLong("streakCount") ?: 0L
                
                val now = Calendar.getInstance()
                val lastLoginCal = Calendar.getInstance().apply { timeInMillis = lastLogin }
                
                val isSameDay = now.get(Calendar.YEAR) == lastLoginCal.get(Calendar.YEAR) &&
                                now.get(Calendar.DAY_OF_YEAR) == lastLoginCal.get(Calendar.DAY_OF_YEAR)
                
                if (!isSameDay) {
                    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
                    val wasYesterday = yesterday.get(Calendar.YEAR) == lastLoginCal.get(Calendar.YEAR) &&
                                       yesterday.get(Calendar.DAY_OF_YEAR) == lastLoginCal.get(Calendar.DAY_OF_YEAR)
                    
                    val newStreak = if (wasYesterday) currentStreak + 1 else 1L
                    transaction.set(userRef, mapOf(
                        "streakCount" to newStreak,
                        "lastLoginDate" to System.currentTimeMillis()
                    ), SetOptions.merge())
                }
            }.await()
        } catch (e: Exception) {
            Log.e("FirestoreManager", "Failed to update streak", e)
        }
    }

    suspend fun incrementWordCount() {
        val uid = auth.currentUser?.uid ?: return
        val userRef = firestore.collection("users").document(uid)
        try {
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val currentCount = snapshot.getLong("totalWordsLearned") ?: 0L
                transaction.set(userRef, mapOf("totalWordsLearned" to currentCount + 1), SetOptions.merge())
            }.await()
        } catch (e: Exception) {
            Log.e("FirestoreManager", "Failed to increment word count", e)
        }
    }

    suspend fun recordOrder(order: Order) {
        try {
            firestore.collection("orders").document(order.orderId).set(order).await()
            
            val uid = auth.currentUser?.uid ?: return
            val expiryDate = if (order.productId == "premium_monthly") {
                System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000)
            } else {
                System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000)
            }
            
            firestore.collection("users").document(uid).set(
                mapOf(
                    "isPremium" to true,
                    "subscriptionType" to order.productId,
                    "expiryDate" to expiryDate
                ),
                SetOptions.merge()
            ).await()
        } catch (e: Exception) {
            Log.e("FirestoreManager", "Failed to record order", e)
        }
    }
}
