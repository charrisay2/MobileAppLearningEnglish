package com.example.lingua.features.news.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.lingua.features.news.data.local.dao.NewsDao
import com.example.lingua.features.news.data.local.entities.ArticleEntity

@Database(entities = [ArticleEntity::class], version = 3, exportSchema = false)
abstract class NewsDatabase : RoomDatabase() {
    abstract val newsDao: NewsDao
}
