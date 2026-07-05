package com.example.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.LinguaMasterApplication
import com.example.data.premium.FirestoreManager
import com.example.domain.user.UserStats
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val firestoreManager: FirestoreManager
) : ViewModel() {

    val userStats: StateFlow<UserStats?> = firestoreManager.getUserStats()
        .catch { e -> 
            Log.e("MainViewModel", "Error fetching user stats", e)
            emit(null) 
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        updateStreak()
    }

    private fun updateStreak() {
        viewModelScope.launch {
            try {
                firestoreManager.updateStreak()
            } catch (e: Exception) {
                Log.e("MainViewModel", "Streak update failed", e)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as LinguaMasterApplication)
                val firestoreManager = application.container.firestoreManager
                MainViewModel(firestoreManager)
            }
        }
    }
}
