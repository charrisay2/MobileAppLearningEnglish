package com.example.data.news.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.lingua.features.news.data.local.NewsDatabase
import com.example.lingua.features.news.data.mapper.toArticle
import com.example.lingua.features.news.data.mapper.toEntity
import com.example.lingua.features.news.data.remote.NewsApiService
import androidx.room.Room
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class NewsSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Log.d("NewsSyncWorker", "Starting NYT news sync...")
            
            // Manual dependency injection as hilt-work is not available
            val apiKey = com.example.BuildConfig.NYT_API_KEY
            
            val json = Json { ignoreUnknownKeys = true }
            val apiService = Retrofit.Builder()
                .baseUrl(NewsApiService.BASE_URL)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(NewsApiService::class.java)
                
            val newsDb = Room.databaseBuilder(
                applicationContext,
                NewsDatabase::class.java,
                "news_db_v3"
            ).fallbackToDestructiveMigration(dropAllTables = true).build()
            
            val newsDao = newsDb.newsDao
            
            // Sync multiple sections for variety
            val sections = listOf("World", "Science", "Technology", "Education")
            var totalSynced = 0
            
            for (section in sections) {
                try {
                    val filterQuery = "section_name:(\"$section\")"
                    val response = apiService.searchArticles(null, filterQuery, 0, apiKey)
                    val articles = response.response?.docs?.map { it.toArticle() } ?: emptyList()
                    
                    if (articles.isNotEmpty()) {
                        newsDao.insertArticles(articles.map { it.toEntity(0) })
                        totalSynced += articles.size
                        Log.d("NewsSyncWorker", "Synced ${articles.size} articles for section: $section")
                    }
                } catch (e: Exception) {
                    Log.e("NewsSyncWorker", "Failed to sync section $section", e)
                }
            }
            
            Log.d("NewsSyncWorker", "Successfully synced $totalSynced total articles from NYT")
            
            // Note: newsDb.close() is not strictly required here as it's a singleton in the app 
            // but since we created a local instance, it's cleaner to close it if it were not a builder.
            // However, Room builders return the instance.

            Result.success()
        } catch (e: Exception) {
            Log.e("NewsSyncWorker", "Error during NYT news sync", e)
            Result.retry()
        }
    }
}
