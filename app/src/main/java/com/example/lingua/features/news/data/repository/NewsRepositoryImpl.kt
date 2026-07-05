package com.example.lingua.features.news.data.repository

import com.example.lingua.features.news.data.local.dao.NewsDao
import com.example.lingua.features.news.data.mapper.toArticle
import com.example.lingua.features.news.data.mapper.toEntity
import com.example.lingua.features.news.data.remote.NewsApiService
import com.example.lingua.features.news.domain.model.Article
import com.example.lingua.features.news.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val apiService: NewsApiService,
    private val newsDao: NewsDao,
    private val apiKey: String
) : NewsRepository {

    override fun getNewsStream(query: String?, section: String?): Flow<List<Article>> = flow {
        // Simple implementation: fetch from network and save to DB, then emit from DB
        try {
            val filterQuery = section?.let { "section_name:(\"$it\")" }
            val response = apiService.searchArticles(query, filterQuery, 0, apiKey)
            val articles = response.response?.docs?.map { it.toArticle() } ?: emptyList()
            
            if (articles.isNotEmpty()) {
                newsDao.insertArticles(articles.map { it.toEntity(0) })
            }
        } catch (e: Exception) {
            // Log error or handle offline
            android.util.Log.e("NewsRepository", "Error fetching from NYT: ${e.message}")
        }

        val dbFlow = if (!query.isNullOrBlank()) {
            newsDao.searchArticles(query)
        } else if (section != null) {
            newsDao.getArticlesBySection(section)
        } else {
            // Default: show everything or World
            newsDao.getArticlesBySection("World")
        }
        
        emitAll(dbFlow.map { entities -> entities.map { it.toArticle() } })
    }
}
