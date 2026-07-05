package com.example.data.vocabulary.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.vocabulary.VocabularyWord

@Entity(tableName = "vocabulary_words")
data class VocabularyEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val word: String,
    val meaning: String,
    val pronunciation: String,
    val ipa: String = "",
    val audioUrl: String = "",
    val englishDefinition: String = "",
    val example: String = "",
    val partOfSpeech: String = "",
    val synonyms: String = "",
    val antonyms: String = "",
    val progress: Int = 0,
    val level: String = "",
    val isFavorite: Boolean,
    val folderId: Int? = null
)

fun VocabularyEntity.toDomain() = VocabularyWord(
    id = id,
    word = word,
    meaning = meaning,
    pronunciation = pronunciation,
    ipa = ipa,
    audioUrl = audioUrl,
    englishDefinition = englishDefinition,
    example = example,
    partOfSpeech = partOfSpeech,
    synonyms = synonyms,
    antonyms = antonyms,
    progress = progress,
    level = level,
    isFavorite = isFavorite,
    folderId = folderId
)

fun VocabularyWord.toEntity() = VocabularyEntity(
    id = id,
    word = word,
    meaning = meaning,
    pronunciation = pronunciation,
    ipa = ipa,
    audioUrl = audioUrl,
    englishDefinition = englishDefinition,
    example = example,
    partOfSpeech = partOfSpeech,
    synonyms = synonyms,
    antonyms = antonyms,
    progress = progress,
    level = level,
    isFavorite = isFavorite,
    folderId = folderId
)
