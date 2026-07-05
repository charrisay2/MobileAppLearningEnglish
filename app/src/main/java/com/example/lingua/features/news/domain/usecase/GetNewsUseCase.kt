package com.example.lingua.features.news.domain.usecase

import com.example.lingua.features.news.domain.model.Article
import com.example.lingua.features.news.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNewsUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke(query: String? = null, section: String? = null): Flow<List<Article>> {
        return repository.getNewsStream(query, section)
    }
}
