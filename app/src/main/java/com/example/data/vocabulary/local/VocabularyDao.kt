package com.example.data.vocabulary.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.vocabulary.local.FolderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabularyDao {
    @Query("SELECT * FROM vocabulary_words ORDER BY level ASC, word ASC")
    fun getAllWords(): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM vocabulary_words WHERE folderId = :folderId ORDER BY word ASC")
    fun getWordsByFolder(folderId: Int): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM vocabulary_words WHERE folderId IS NULL ORDER BY word ASC")
    fun getWordsWithoutFolder(): Flow<List<VocabularyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: VocabularyEntity)

    @Query("DELETE FROM vocabulary_words WHERE id = :wordId")
    suspend fun deleteWord(wordId: Int)

    @Query("SELECT * FROM vocabulary_folders ORDER BY name ASC")
    fun getAllFolders(): Flow<List<FolderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: FolderEntity)

    @Query("DELETE FROM vocabulary_folders WHERE id = :folderId")
    suspend fun deleteFolder(folderId: Int)

    @Query("UPDATE vocabulary_words SET folderId = NULL WHERE folderId = :folderId")
    suspend fun clearWordsFromFolder(folderId: Int)
}
