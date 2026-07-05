package com.example.lingua.features.news.data.mapper

import com.example.lingua.features.news.data.local.entities.ArticleEntity
import com.example.lingua.features.news.data.remote.dto.ArticleDto
import com.example.lingua.features.news.domain.model.Article

fun ArticleDto.toArticle(): Article {
    val imageUrl = multimedia.firstOrNull { it.type == "image" }?.let {
        "https://www.nytimes.com/${it.url}"
    }
    
    // Simple heuristic for difficulty and read time
    val wordCount = (leadParagraph ?: abstract ?: "").split(" ").size
    val difficulty = when {
        wordCount < 50 -> "A2"
        wordCount < 100 -> "B1"
        else -> "B2"
    }
    val timeToRead = "${(wordCount / 100).coerceAtLeast(1)} min"

    return Article(
        id = id,
        title = headline?.main ?: "",
        description = snippet ?: "",
        content = leadParagraph ?: abstract ?: "",
        url = webUrl ?: "",
        imageUrl = imageUrl,
        author = byline?.original ?: "New York Times",
        publishedDate = pubDate ?: "",
        section = sectionName ?: "General",
        difficulty = difficulty,
        timeToRead = timeToRead,
        tags = listOfNotNull(sectionName, "NYT")
    )
}

fun Article.toEntity(page: Int): ArticleEntity {
    return ArticleEntity(
        id = id,
        title = title,
        description = description,
        content = content,
        url = url,
        imageUrl = imageUrl,
        author = author,
        publishedDate = publishedDate,
        section = section,
        difficulty = difficulty,
        timeToRead = timeToRead,
        tags = tags.joinToString(","),
        page = page
    )
}

fun ArticleEntity.toArticle(): Article {
    return Article(
        id = id,
        title = title,
        description = description,
        content = content,
        url = url,
        imageUrl = imageUrl,
        author = author,
        publishedDate = publishedDate,
        section = section,
        difficulty = difficulty,
        timeToRead = timeToRead,
        tags = tags.split(",").filter { it.isNotBlank() }
    )
}
