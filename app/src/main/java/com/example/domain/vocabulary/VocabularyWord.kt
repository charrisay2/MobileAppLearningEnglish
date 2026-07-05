package com.example.domain.vocabulary

data class VocabularyWord(
    val id: Int = 0,
    val word: String,
    val meaning: String, // Vietnamese meaning
    val pronunciation: String,
    val ipa: String = "",
    val audioUrl: String = "",
    val englishDefinition: String = "",
    val example: String = "",
    val partOfSpeech: String = "",
    val synonyms: String = "",
    val antonyms: String = "",
    val progress: Int = 0,
    val level: String = "", // A1, A2, B1, B2, C1, C2
    val isFavorite: Boolean = false,
    val folderId: Int? = null
)
