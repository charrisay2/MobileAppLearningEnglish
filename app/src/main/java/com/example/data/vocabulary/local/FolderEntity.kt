package com.example.data.vocabulary.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vocabulary_folders")
data class FolderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

fun FolderEntity.toDomain() = com.example.domain.vocabulary.VocabularyFolder(
    id = id,
    name = name,
    description = description
)

fun com.example.domain.vocabulary.VocabularyFolder.toEntity() = FolderEntity(
    id = id,
    name = name,
    description = description
)
