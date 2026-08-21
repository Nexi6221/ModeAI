package com.example.styleai.data.model

data class StyleAnalysisResult(
    val id: Long = 0,
    val title: String,
    val imageUri: String? = null,
    val imageResId: Int? = null,
    val styleCategory: String,
    val score: Int,
    val season: String,
    val overview: String,
    val colorPalette: List<ColorSwatch>,
    val colorCoordination: String,
    val clothingCombination: String,
    val stylingSuggestions: List<String>,
    val accessories: List<AccessoryItem>,
    val suitableOccasions: List<String>,
    val alternativeCombinations: List<String>,
    val isSaved: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class ColorSwatch(
    val name: String,
    val hexColor: String,
    val role: String // e.g., "Primary base", "Complementary accent", "Neutral foundation"
)

data class AccessoryItem(
    val category: String, // e.g., "Footwear", "Jewelry", "Bag", "Outerwear", "Belt"
    val recommendation: String,
    val vibe: String
)

data class StylistChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class MessageSender {
    USER, STYLIST
}

data class InspirationLook(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val drawableResId: Int,
    val tags: List<String>,
    val sampleAnalysis: StyleAnalysisResult
)

data class UserStylePreferences(
    val name: String = "Sophia Vance",
    val archetype: String = "Minimalist Luxe & Tailored",
    val primaryPalette: String = "Warm Neutrals & Charcoal",
    val fitPreference: String = "Structured Relaxed",
    val preferredOccasions: List<String> = listOf("Smart Casual", "Creative Office", "Weekend Gallery"),
    val shoePreference: String = "Leather Loafers & Minimalist Clean Sneakers",
    val notificationsEnabled: Boolean = true
)
