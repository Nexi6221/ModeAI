package com.example.styleai.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.styleai.data.model.AccessoryItem
import com.example.styleai.data.model.ColorSwatch
import com.example.styleai.data.model.StyleAnalysisResult

@Entity(tableName = "saved_outfits")
data class OutfitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val imageUri: String? = null,
    val imageResId: Int? = null,
    val styleCategory: String,
    val score: Int,
    val season: String,
    val overview: String,
    val colorPaletteJson: String,
    val colorCoordination: String,
    val clothingCombination: String,
    val stylingSuggestionsJson: String,
    val accessoriesJson: String,
    val suitableOccasionsJson: String,
    val alternativeCombinationsJson: String,
    val timestamp: Long = System.currentTimeMillis()
)

fun OutfitEntity.toModel(): StyleAnalysisResult {
    // Parse simple JSON / delimited lists cleanly
    val colors = parseColorSwatches(colorPaletteJson)
    val suggestions = parseStringList(stylingSuggestionsJson)
    val accList = parseAccessories(accessoriesJson)
    val occasions = parseStringList(suitableOccasionsJson)
    val altCombos = parseStringList(alternativeCombinationsJson)

    return StyleAnalysisResult(
        id = id,
        title = title,
        imageUri = imageUri,
        imageResId = imageResId,
        styleCategory = styleCategory,
        score = score,
        season = season,
        overview = overview,
        colorPalette = colors,
        colorCoordination = colorCoordination,
        clothingCombination = clothingCombination,
        stylingSuggestions = suggestions,
        accessories = accList,
        suitableOccasions = occasions,
        alternativeCombinations = altCombos,
        isSaved = true,
        timestamp = timestamp
    )
}

fun StyleAnalysisResult.toEntity(): OutfitEntity {
    return OutfitEntity(
        id = id,
        title = title,
        imageUri = imageUri,
        imageResId = imageResId,
        styleCategory = styleCategory,
        score = score,
        season = season,
        overview = overview,
        colorPaletteJson = serializeColorSwatches(colorPalette),
        colorCoordination = colorCoordination,
        clothingCombination = clothingCombination,
        stylingSuggestionsJson = serializeStringList(stylingSuggestions),
        accessoriesJson = serializeAccessories(accessories),
        suitableOccasionsJson = serializeStringList(suitableOccasions),
        alternativeCombinationsJson = serializeStringList(alternativeCombinations),
        timestamp = timestamp
    )
}

// Simple reliable serialization helpers
private fun serializeStringList(list: List<String>): String = list.joinToString("|||")
private fun parseStringList(str: String): List<String> {
    if (str.isBlank()) return emptyList()
    return str.split("|||").map { it.trim() }.filter { it.isNotEmpty() }
}

private fun serializeColorSwatches(list: List<ColorSwatch>): String {
    return list.joinToString(";;;") { "${it.name}:::${it.hexColor}:::${it.role}" }
}

private fun parseColorSwatches(str: String): List<ColorSwatch> {
    if (str.isBlank()) return emptyList()
    return str.split(";;;").mapNotNull { entry ->
        val parts = entry.split(":::")
        if (parts.size >= 3) {
            ColorSwatch(name = parts[0], hexColor = parts[1], role = parts[2])
        } else null
    }
}

private fun serializeAccessories(list: List<AccessoryItem>): String {
    return list.joinToString(";;;") { "${it.category}:::${it.recommendation}:::${it.vibe}" }
}

private fun parseAccessories(str: String): List<AccessoryItem> {
    if (str.isBlank()) return emptyList()
    return str.split(";;;").mapNotNull { entry ->
        val parts = entry.split(":::")
        if (parts.size >= 3) {
            AccessoryItem(category = parts[0], recommendation = parts[1], vibe = parts[2])
        } else null
    }
}
