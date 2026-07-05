package com.example.domain.vocabulary

import kotlinx.coroutines.flow.Flow

interface VocabularyRepository {
    fun getAllWords(): Flow<List<VocabularyWord>>
    fun getWordsByFolder(folderId: Int): Flow<List<VocabularyWord>>
    fun getWordsWithoutFolder(): Flow<List<VocabularyWord>>
    suspend fun insertWord(word: VocabularyWord)
    suspend fun deleteWord(wordId: Int)
    
    fun getAllFolders(): Flow<List<VocabularyFolder>>
    suspend fun insertFolder(folder: VocabularyFolder)
    suspend fun deleteFolder(folderId: Int)
}
