package com.example.styleai.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OutfitDao {
    @Query("SELECT * FROM saved_outfits ORDER BY timestamp DESC")
    fun getAllOutfits(): Flow<List<OutfitEntity>>

    @Query("SELECT * FROM saved_outfits WHERE id = :id LIMIT 1")
    suspend fun getOutfitById(id: Long): OutfitEntity?

    @Query("SELECT * FROM saved_outfits WHERE title = :title LIMIT 1")
    suspend fun getOutfitByTitle(title: String): OutfitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfit(outfit: OutfitEntity): Long

    @Query("DELETE FROM saved_outfits WHERE id = :id")
    suspend fun deleteOutfitById(id: Long)

    @Query("DELETE FROM saved_outfits WHERE title = :title")
    suspend fun deleteOutfitByTitle(title: String)

    @Query("SELECT COUNT(*) FROM saved_outfits")
    fun getSavedCount(): Flow<Int>
}
