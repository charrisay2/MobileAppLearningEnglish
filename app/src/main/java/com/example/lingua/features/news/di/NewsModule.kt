package com.example.lingua.features.news.di

import android.content.Context
import androidx.room.Room
import com.example.lingua.features.news.data.local.NewsDatabase
import com.example.lingua.features.news.data.local.dao.NewsDao
import com.example.lingua.features.news.data.remote.NewsApiService
import com.example.lingua.features.news.data.repository.NewsRepositoryImpl
import com.example.lingua.features.news.domain.repository.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NewsModule {

    @Provides
    @Singleton
    fun provideNewsApiService(okHttpClient: OkHttpClient): NewsApiService {
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .baseUrl(NewsApiService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(NewsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideNewsDatabase(@ApplicationContext context: Context): NewsDatabase {
        return Room.databaseBuilder(
            context,
            NewsDatabase::class.java,
            "news_db_v3"
        ).fallbackToDestructiveMigration(dropAllTables = true).build()
    }

    @Provides
    fun provideNewsDao(database: NewsDatabase): NewsDao = database.newsDao

    @Provides
    @Named("NYT_API_KEY")
    fun provideApiKey(): String = com.example.BuildConfig.NYT_API_KEY

    @Provides
    @Singleton
    fun provideNewsRepository(
        apiService: NewsApiService,
        newsDao: NewsDao,
        @Named("NYT_API_KEY") apiKey: String
    ): NewsRepository {
        return NewsRepositoryImpl(apiService, newsDao, apiKey)
    }
}
