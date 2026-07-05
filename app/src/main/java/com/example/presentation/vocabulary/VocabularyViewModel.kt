package com.example.presentation.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.LinguaMasterApplication
import com.example.data.premium.FirestoreManager
import com.example.domain.vocabulary.VocabularyFolder
import com.example.domain.vocabulary.VocabularyRepository
import com.example.domain.vocabulary.VocabularyWord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VocabularyViewModel(
    private val repository: VocabularyRepository,
    private val firestoreManager: FirestoreManager
) : ViewModel() {

    private val _selectedFolderId = MutableStateFlow<Int?>(null)
    val selectedFolderId = _selectedFolderId.asStateFlow()

    val folders: StateFlow<List<VocabularyFolder>> = repository.getAllFolders()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val uiState: StateFlow<List<VocabularyWord>> = combine(
        repository.getAllWords(),
        _selectedFolderId
    ) { allWords, folderId ->
        if (folderId == null) {
            allWords
        } else {
            allWords.filter { it.folderId == folderId }
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun selectFolder(folderId: Int?) {
        _selectedFolderId.value = folderId
    }

    private var isAddingFolder = false

    fun addFolder(name: String) {
        if (isAddingFolder) return
        viewModelScope.launch {
            isAddingFolder = true
            try {
                repository.insertFolder(VocabularyFolder(name = name))
            } finally {
                isAddingFolder = false
            }
        }
    }

    fun deleteFolder(folderId: Int) {
        viewModelScope.launch {
            repository.deleteFolder(folderId)
        }
    }

    fun deleteWord(wordId: Int) {
        viewModelScope.launch {
            repository.deleteWord(wordId)
        }
    }

    private var isAddingWord = false

    fun addWord(word: String, meaning: String, pronunciation: String, folderId: Int? = _selectedFolderId.value) {
        if (isAddingWord) return
        viewModelScope.launch {
            isAddingWord = true
            try {
                val vocabularyWord = VocabularyWord(
                    word = word,
                    meaning = meaning,
                    pronunciation = pronunciation,
                    isFavorite = true,
                    folderId = folderId
                )
                repository.insertWord(vocabularyWord)
                firestoreManager.incrementWordCount()
            } finally {
                isAddingWord = false
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as LinguaMasterApplication)
                val repository = application.container.vocabularyRepository
                val firestoreManager = application.container.firestoreManager
                VocabularyViewModel(repository, firestoreManager)
            }
        }
    }
}
