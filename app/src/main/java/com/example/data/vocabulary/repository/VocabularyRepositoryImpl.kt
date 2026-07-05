package com.example.data.vocabulary.repository

import com.example.data.vocabulary.local.VocabularyDao
import com.example.data.vocabulary.local.toDomain
import com.example.data.vocabulary.local.toEntity
import com.example.domain.vocabulary.VocabularyFolder
import com.example.domain.vocabulary.VocabularyRepository
import com.example.domain.vocabulary.VocabularyWord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VocabularyRepositoryImpl(
    private val dao: VocabularyDao
) : VocabularyRepository {
    override fun getAllWords(): Flow<List<VocabularyWord>> {
        return dao.getAllWords().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertWord(word: VocabularyWord) {
        dao.insertWord(word.toEntity())
    }

    override suspend fun deleteWord(wordId: Int) {
        dao.deleteWord(wordId)
    }

    override fun getWordsByFolder(folderId: Int): Flow<List<VocabularyWord>> {
        return dao.getWordsByFolder(folderId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getWordsWithoutFolder(): Flow<List<VocabularyWord>> {
        return dao.getWordsWithoutFolder().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllFolders(): Flow<List<VocabularyFolder>> {
        return dao.getAllFolders().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertFolder(folder: VocabularyFolder) {
        dao.insertFolder(folder.toEntity())
    }

    override suspend fun deleteFolder(folderId: Int) {
        dao.clearWordsFromFolder(folderId)
        dao.deleteFolder(folderId)
    }
}
