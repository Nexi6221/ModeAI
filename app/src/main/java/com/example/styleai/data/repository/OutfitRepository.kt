package com.example.styleai.data.repository

import com.example.styleai.data.local.OutfitDao
import com.example.styleai.data.local.toEntity
import com.example.styleai.data.local.toModel
import com.example.styleai.data.model.StyleAnalysisResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OutfitRepository(private val outfitDao: OutfitDao) {

    val allSavedOutfits: Flow<List<StyleAnalysisResult>> = outfitDao.getAllOutfits().map { entities ->
        entities.map { it.toModel() }
    }

    val savedCount: Flow<Int> = outfitDao.getSavedCount()

    suspend fun saveOutfit(outfit: StyleAnalysisResult): Long {
        return outfitDao.insertOutfit(outfit.copy(isSaved = true).toEntity())
    }

    suspend fun deleteOutfit(id: Long) {
        outfitDao.deleteOutfitById(id)
    }

    suspend fun deleteOutfitByTitle(title: String) {
        outfitDao.deleteOutfitByTitle(title)
    }

    suspend fun getOutfitById(id: Long): StyleAnalysisResult? {
        return outfitDao.getOutfitById(id)?.toModel()
    }
}
