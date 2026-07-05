package com.example.presentation.reading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.LinguaMasterApplication
import com.example.domain.news.Article
import com.example.domain.news.NewsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ReadingViewModel(private val repository: NewsRepository) : ViewModel() {

    private val _selectedCategory = MutableStateFlow("World")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val articles: StateFlow<List<Article>> = _selectedCategory
        .flatMapLatest { category ->
            repository.getArticles(category)
        }
        .catch { emit(emptyList()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun selectCategory(category: String) {
        _selectedCategory.value = category
        refreshArticles()
    }

    fun refreshArticles() {
        viewModelScope.launch {
            repository.refreshArticles(_selectedCategory.value)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as LinguaMasterApplication)
                val newsRepository = application.container.newsRepository
                ReadingViewModel(newsRepository)
            }
        }
    }
}
