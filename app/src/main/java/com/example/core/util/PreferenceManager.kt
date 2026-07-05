package com.example.core.util

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("lingua_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TRIAL_START_DATE = "trial_start_date"
        private const val KEY_IS_PREMIUM = "is_premium"
        private const val TRIAL_DAYS = 5
    }

    var trialStartDate: Long
        get() = prefs.getLong(KEY_TRIAL_START_DATE, 0L)
        set(value) = prefs.edit().putLong(KEY_TRIAL_START_DATE, value).apply()

    var isPremium: Boolean
        get() = prefs.getBoolean(KEY_IS_PREMIUM, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_PREMIUM, value).apply()

    fun isTrialExpired(): Boolean {
        if (isPremium) return false
        if (trialStartDate == 0L) {
            trialStartDate = System.currentTimeMillis()
            return false
        }
        val diff = System.currentTimeMillis() - trialStartDate
        val days = diff / (1000 * 60 * 60 * 24)
        return days >= TRIAL_DAYS
    }
    
    fun getRemainingTrialDays(): Int {
        if (isPremium) return 0
        if (trialStartDate == 0L) return TRIAL_DAYS
        val diff = System.currentTimeMillis() - trialStartDate
        val days = (diff / (1000 * 60 * 60 * 24)).toInt()
        return (TRIAL_DAYS - days).coerceAtLeast(0)
    }
}
