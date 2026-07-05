package com.example.presentation.reading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.LinguaMasterApplication
import com.example.domain.news.Article
import com.example.domain.news.NewsRepository
import com.example.domain.vocabulary.VocabularyFolder
import com.example.domain.vocabulary.VocabularyRepository
import com.example.data.vocabulary.remote.DictionaryApiService
import com.example.data.vocabulary.remote.DictionaryEntry
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ArticleDetailViewModel(
    private val repository: NewsRepository,
    private val vocabularyRepository: VocabularyRepository,
    private val articleId: String
) : ViewModel() {

    private val _article = MutableStateFlow<Article?>(null)
    val article: StateFlow<Article?> = _article.asStateFlow()

    private val _selectedWordEntry = MutableStateFlow<DictionaryEntry?>(null)
    val selectedWordEntry: StateFlow<DictionaryEntry?> = _selectedWordEntry.asStateFlow()

    private val _selectedWordExplanation = MutableStateFlow<String?>(null)
    val selectedWordExplanation: StateFlow<String?> = _selectedWordExplanation.asStateFlow()

    private val _isLoadingExplanation = MutableStateFlow(false)
    val isLoadingExplanation: StateFlow<Boolean> = _isLoadingExplanation.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    val folders: StateFlow<List<VocabularyFolder>> = vocabularyRepository.getAllFolders()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            repository.getArticles().collect { articles ->
                _article.value = articles.find { it.id == articleId }
            }
        }
    }

    private val dictionaryApi: DictionaryApiService by lazy {
        val json = Json { ignoreUnknownKeys = true }
        Retrofit.Builder()
            .baseUrl("https://api.dictionaryapi.dev/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(DictionaryApiService::class.java)
    }

    fun explainWord(word: String, contextSentence: String) {
        viewModelScope.launch {
            _isLoadingExplanation.value = true
            _selectedWordExplanation.value = null
            _selectedWordEntry.value = null
            try {
                val results = dictionaryApi.getDefinition(word.trim().lowercase())
                val entry = results.firstOrNull()
                
                if (entry != null) {
                    _selectedWordEntry.value = entry
                    val explanation = StringBuilder()
                    explanation.append("Word: ${entry.word}\n")
                    entry.phonetic?.let { explanation.append("Phonetic: $it\n") }
                    
                    entry.meanings.forEach { meaning ->
                        explanation.append("\n[${meaning.partOfSpeech}]\n")
                        meaning.definitions.take(2).forEachIndexed { index, def ->
                            explanation.append("${index + 1}. ${def.definition}\n")
                            def.example?.let { explanation.append("   Ex: $it\n") }
                        }
                    }
                    _selectedWordExplanation.value = explanation.toString()
                } else {
                    _selectedWordExplanation.value = "No definition found for '$word'."
                }
            } catch (e: Exception) {
                _selectedWordExplanation.value = "Failed to fetch definition: ${e.message}"
            } finally {
                _isLoadingExplanation.value = false
            }
        }
    }

    fun saveToVocabulary(folderId: Int? = null) {
        if (_isSaving.value) return
        val entry = _selectedWordEntry.value ?: return
        
        viewModelScope.launch {
            _isSaving.value = true
            try {
                val firstMeaning = entry.meanings.firstOrNull()
                val firstDef = firstMeaning?.definitions?.firstOrNull()
                
                val wordToSave = com.example.domain.vocabulary.VocabularyWord(
                    word = entry.word,
                    meaning = firstDef?.definition ?: "",
                    pronunciation = entry.phonetic ?: "",
                    ipa = entry.phonetic ?: "",
                    partOfSpeech = firstMeaning?.partOfSpeech ?: "",
                    example = firstDef?.example ?: "",
                    folderId = folderId
                )
                vocabularyRepository.insertWord(wordToSave)
                _selectedWordExplanation.value = "Saved '${entry.word}' to vocabulary!"
            } catch (e: Exception) {
                // Handle error if needed
            } finally {
                _isSaving.value = false
            }
        }
    }
    
    fun clearExplanation() {
        _selectedWordExplanation.value = null
        _selectedWordEntry.value = null
    }

    companion object {
        fun factory(articleId: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as LinguaMasterApplication)
                ArticleDetailViewModel(
                    application.container.newsRepository,
                    application.container.vocabularyRepository,
                    articleId
                )
            }
        }
    }
}
