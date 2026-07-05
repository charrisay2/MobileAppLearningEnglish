package com.example.presentation.flashcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.LinguaMasterApplication
import com.example.domain.vocabulary.VocabularyRepository
import com.example.domain.vocabulary.VocabularyWord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FlashCardViewModel(private val repository: VocabularyRepository) : ViewModel() {
    val words: StateFlow<List<VocabularyWord>> = repository.getAllWords()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _pronunciationScore = MutableStateFlow<String?>(null)
    val pronunciationScore: StateFlow<String?> = _pronunciationScore.asStateFlow()
    
    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    fun nextCard() {
        if (words.value.isNotEmpty()) {
            _currentIndex.value = (_currentIndex.value + 1) % words.value.size
            _pronunciationScore.value = null
        }
    }

    fun previousCard() {
        if (words.value.isNotEmpty()) {
            val newIndex = _currentIndex.value - 1
            _currentIndex.value = if (newIndex < 0) words.value.size - 1 else newIndex
            _pronunciationScore.value = null
        }
    }

    fun shuffle() {
        _currentIndex.value = 0
        _pronunciationScore.value = null
    }

    fun toggleFavorite(word: VocabularyWord) {
        viewModelScope.launch {
            repository.insertWord(word.copy(isFavorite = !word.isFavorite))
        }
    }

    fun evaluatePronunciation(word: String, spokenText: String) {
        viewModelScope.launch {
            _isAnalyzing.value = true
            // Mock evaluation logic without AI
            kotlinx.coroutines.delay(1000)
            val isCorrect = word.equals(spokenText, ignoreCase = true)
            _pronunciationScore.value = if (isCorrect) {
                "Excellent! Your pronunciation of '$word' is perfect. Score: 100/100"
            } else {
                "Good effort. Try to emphasize the syllables in '$word' more clearly. Score: 75/100"
            }
            _isAnalyzing.value = false
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as LinguaMasterApplication)
                FlashCardViewModel(application.container.vocabularyRepository)
            }
        }
    }
}
