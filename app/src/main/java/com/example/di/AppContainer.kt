package com.example.di

import android.content.Context
import com.example.data.core.local.AppDatabase
import com.example.data.vocabulary.repository.VocabularyRepositoryImpl
import com.example.domain.news.NewsRepository
import com.example.domain.vocabulary.VocabularyRepository
import com.example.data.news.remote.NewsApiService
import com.example.data.news.repository.NewsRepositoryImpl
import com.example.data.premium.FirestoreManager
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

interface AppContainer {
    val vocabularyRepository: VocabularyRepository
    val newsRepository: NewsRepository
    val firestoreManager: FirestoreManager
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val vocabularyRepository: VocabularyRepository by lazy {
        VocabularyRepositoryImpl(AppDatabase.getDatabase(context).vocabularyDao())
    }

    private val newsApiService by lazy {
        val json = Json { ignoreUnknownKeys = true }
        Retrofit.Builder()
            .baseUrl(NewsApiService.BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(NewsApiService::class.java)
    }

    override val newsRepository: NewsRepository by lazy {
        NewsRepositoryImpl(apiService = newsApiService)
    }

    override val firestoreManager: FirestoreManager by lazy {
        FirestoreManager()
    }
}
