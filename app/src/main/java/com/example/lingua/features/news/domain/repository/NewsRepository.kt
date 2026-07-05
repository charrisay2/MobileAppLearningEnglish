package com.example.lingua.features.news.domain.repository

import com.example.lingua.features.news.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getNewsStream(query: String?, section: String?): Flow<List<Article>>
}
