package com.example.domain.news

data class Article(
    val id: String,
    val title: String,
    val source: String,
    val content: String,
    val timeToRead: String,
    val difficulty: String,
    val tags: List<String>,
    val articleUrl: String,
    val publishDate: String
)
