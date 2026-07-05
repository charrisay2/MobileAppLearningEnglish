package com.example.domain.news

import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getArticles(category: String? = null): Flow<List<Article>>
    suspend fun refreshArticles(category: String? = null)
}
