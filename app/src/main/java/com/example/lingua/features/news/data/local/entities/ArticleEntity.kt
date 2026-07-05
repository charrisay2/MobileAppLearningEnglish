package com.example.lingua.features.news.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val content: String,
    val url: String,
    val imageUrl: String?,
    val author: String,
    val publishedDate: String,
    val section: String,
    val difficulty: String,
    val timeToRead: String,
    val tags: String,
    val page: Int
)
