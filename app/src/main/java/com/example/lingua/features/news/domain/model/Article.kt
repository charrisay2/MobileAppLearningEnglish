package com.example.lingua.features.news.domain.model

data class Article(
    val id: String,
    val title: String,
    val description: String,
    val content: String,
    val url: String,
    val imageUrl: String?,
    val author: String,
    val publishedDate: String,
    val section: String,
    val difficulty: String = "B1",
    val timeToRead: String = "5 min",
    val tags: List<String> = listOf("News")
)
