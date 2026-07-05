package com.example.lingua.features.news.presentation.list

import com.example.lingua.features.news.domain.model.Article

data class NewsUiState(
    val articles: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedSection: String = "World"
)
