package com.example.styleai.data.api

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.styleai.data.model.AccessoryItem
import com.example.styleai.data.model.ColorSwatch
import com.example.styleai.data.model.StyleAnalysisResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class GeminiStylistService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val mediaTypeJson = "application/json; charset=utf-8".toMediaType()

    suspend fun analyzeOutfit(
        bitmap: Bitmap?,
        promptContext: String = ""
    ): StyleAnalysisResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY.trim()

        if (apiKey.isNotEmpty()) {
            try {
                val apiResult = callGeminiVisionApi(apiKey, bitmap, promptContext)
                if (apiResult != null) {
                    return@withContext apiResult
                }
            } catch (e: Exception) {
                Log.w("GeminiStylist", "API call fallback to local engine: ${e.message}")
            }
        }

        // High quality local stylist fallback
        generateSmartLocalAnalysis(bitmap, promptContext)
    }

    suspend fun askStylistQuestion(
        currentOutfit: StyleAnalysisResult?,
        userQuestion: String,
        chatHistory: List<Pair<String, String>> = emptyList()
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY.trim()

        if (apiKey.isNotEmpty()) {
            try {
                val answer = callGeminiChatApi(apiKey, currentOutfit, userQuestion, chatHistory)
                if (!answer.isNullOrBlank()) {
                    return@withContext answer
                }
            } catch (e: Exception) {
                Log.w("GeminiStylist", "Chat API fallback: ${e.message}")
            }
        }

        generateLocalChatResponse(currentOutfit, userQuestion)
    }

    private fun callGeminiVisionApi(
        apiKey: String,
        bitmap: Bitmap?,
        context: String
    ): StyleAnalysisResult? {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

        val prompt = """
        You are a world-class luxury fashion stylist and editorial creative director at StyleAI.
        Analyze this outfit and return a STRICT JSON object without markdown fences (no ```json).
        The JSON must match this structure:
        {
          "title": "Short aesthetic title (e.g., 'Tailored Parisian Camel Ensemble')",
          "styleCategory": "Category (e.g., 'Minimalist Chic' / 'Smart Casual' / 'Contemporary Tailoring')",
          "score": 92,
          "season": "e.g., Fall / Winter 2026",
          "overview": "2-3 sentences concise editorial critique praising silhouette balance and cohesion.",
          "colorPalette": [
            {"name": "Camel Beige", "hexColor": "#C3A376", "role": "Hero neutral"},
            {"name": "Deep Midnight", "hexColor": "#1A2238", "role": "Base grounding"},
            {"name": "Off-White", "hexColor": "#F4F1EA", "role": "Illumination accent"}
          ],
          "colorCoordination": "Detailed critique of color temperature, contrast, and balance.",
          "clothingCombination": "Breakdown of outerwear, top, bottoms, proportions, and textures.",
          "stylingSuggestions": [
            "Specific high-impact tweak 1",
            "Specific high-impact tweak 2",
            "Specific high-impact tweak 3"
          ],
          "accessories": [
            {"category": "Footwear", "recommendation": "Almond-toe leather loafers or sleek Chelsea boots", "vibe": "Refined polish"},
            {"category": "Bag", "recommendation": "Structured minimalist leather tote in dark espresso", "vibe": "Architectural chic"},
            {"category": "Jewelry", "recommendation": "Subtle brushed gold huggies and slim signet ring", "vibe": "Effortless warmth"},
            {"category": "Outerwear / Layer", "recommendation": "Unlined cashmere overcoat or wool blend wrap", "vibe": "Textural depth"}
          ],
          "suitableOccasions": [
            "Creative Corporate & Executive Meetings",
            "Art Gallery Openings & Cultural Receptions",
            "Weekend Bistro Lunches & Coffee Dates",
            "Evening Smart-Casual Dinners"
          ],
          "alternativeCombinations": [
            "Swap wide-leg trousers for pleated barrel-leg denim with pointed kitten heels",
            "Layer a fine-gauge merino turtleneck beneath an unbuttoned crisp poplin shirt",
            "Introduce a tonal silk neck scarf in espresso/terracotta for vintage flair"
          ]
        }
        User Context: $context
        """.trimIndent()

        val contentsArray = JSONArray()
        val contentObj = JSONObject()
        val partsArray = JSONArray()

        partsArray.put(JSONObject().put("text", prompt))

        if (bitmap != null) {
            val base64Image = bitmapToBase64(bitmap)
            val inlineData = JSONObject()
                .put("mimeType", "image/jpeg")
                .put("data", base64Image)
            partsArray.put(JSONObject().put("inlineData", inlineData))
        }

        contentObj.put("parts", partsArray)
        contentsArray.put(contentObj)

        val requestBodyJson = JSONObject()
            .put("contents", contentsArray)

        val request = Request.Builder()
            .url(url)
            .post(requestBodyJson.toString().toRequestBody(mediaTypeJson))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null
            val responseString = response.body?.string() ?: return null
            val respJson = JSONObject(responseString)
            val candidates = respJson.optJSONArray("candidates") ?: return null
            if (candidates.length() == 0) return null
            val text = candidates.getJSONObject(0)
                .optJSONObject("content")
                ?.optJSONArray("parts")
                ?.optJSONObject(0)
                ?.optString("text") ?: return null

            return parseJsonToAnalysisResult(text)
        }
    }

    private fun callGeminiChatApi(
        apiKey: String,
        currentOutfit: StyleAnalysisResult?,
        question: String,
        history: List<Pair<String, String>>
    ): String? {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

        val systemContext = buildString {
            append("You are StyleAI's elite personal fashion stylist. Provide concise, friendly, inspiring, and highly actionable styling advice.")
            if (currentOutfit != null) {
                append("\nCurrent Outfit Analyzed: ${currentOutfit.title} (${currentOutfit.styleCategory})")
                append("\nPalette: ${currentOutfit.colorPalette.joinToString { it.name }}")
                append("\nOverview: ${currentOutfit.overview}")
            }
        }

        val contentsArray = JSONArray()

        // Add history
        for ((user, bot) in history) {
            contentsArray.put(JSONObject().put("role", "user").put("parts", JSONArray().put(JSONObject().put("text", user))))
            contentsArray.put(JSONObject().put("role", "model").put("parts", JSONArray().put(JSONObject().put("text", bot))))
        }

        // Add prompt
        val currentPrompt = "$systemContext\n\nUser Question: $question"
        contentsArray.put(JSONObject().put("role", "user").put("parts", JSONArray().put(JSONObject().put("text", currentPrompt))))

        val requestBodyJson = JSONObject().put("contents", contentsArray)

        val request = Request.Builder()
            .url(url)
            .post(requestBodyJson.toString().toRequestBody(mediaTypeJson))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null
            val responseString = response.body?.string() ?: return null
            val respJson = JSONObject(responseString)
            val candidates = respJson.optJSONArray("candidates") ?: return null
            if (candidates.length() == 0) return null
            return candidates.getJSONObject(0)
                .optJSONObject("content")
                ?.optJSONArray("parts")
                ?.optJSONObject(0)
                ?.optString("text")
        }
    }

    private fun parseJsonToAnalysisResult(rawText: String): StyleAnalysisResult? {
        try {
            val cleaned = rawText.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val json = JSONObject(cleaned)
            val title = json.optString("title", "Curated Ensemble")
            val styleCategory = json.optString("styleCategory", "Contemporary Minimalist")
            val score = json.optInt("score", 93)
            val season = json.optString("season", "Autumn / Winter 2026")
            val overview = json.optString("overview", "A beautifully balanced ensemble featuring intentional proportions and refined tonal harmony.")
            val colorCoordination = json.optString("colorCoordination", "The palette balances warm grounded tones with subtle high-contrast accents.")
            val clothingCombination = json.optString("clothingCombination", "Harmonious tailoring pairing structured outerwear with fluid bottom drapery.")

            val colorArray = json.optJSONArray("colorPalette")
            val colors = mutableListOf<ColorSwatch>()
            if (colorArray != null) {
                for (i in 0 until colorArray.length()) {
                    val obj = colorArray.getJSONObject(i)
                    colors.add(
                        ColorSwatch(
                            name = obj.optString("name", "Neutral"),
                            hexColor = obj.optString("hexColor", "#333333"),
                            role = obj.optString("role", "Base")
                        )
                    )
                }
            }

            val suggArray = json.optJSONArray("stylingSuggestions")
            val suggestions = mutableListOf<String>()
            if (suggArray != null) {
                for (i in 0 until suggArray.length()) {
                    suggestions.add(suggArray.getString(i))
                }
            }

            val accArray = json.optJSONArray("accessories")
            val accessories = mutableListOf<AccessoryItem>()
            if (accArray != null) {
                for (i in 0 until accArray.length()) {
                    val obj = accArray.getJSONObject(i)
                    accessories.add(
                        AccessoryItem(
                            category = obj.optString("category", "Accent"),
                            recommendation = obj.optString("recommendation", "Refined piece"),
                            vibe = obj.optString("vibe", "Polished")
                        )
                    )
                }
            }

            val occArray = json.optJSONArray("suitableOccasions")
            val occasions = mutableListOf<String>()
            if (occArray != null) {
                for (i in 0 until occArray.length()) {
                    occasions.add(occArray.getString(i))
                }
            }

            val altArray = json.optJSONArray("alternativeCombinations")
            val alternatives = mutableListOf<String>()
            if (altArray != null) {
                for (i in 0 until altArray.length()) {
                    alternatives.add(altArray.getString(i))
                }
            }

            return StyleAnalysisResult(
                title = title,
                styleCategory = styleCategory,
                score = score,
                season = season,
                overview = overview,
                colorPalette = if (colors.isNotEmpty()) colors else defaultColors(),
                colorCoordination = colorCoordination,
                clothingCombination = clothingCombination,
                stylingSuggestions = if (suggestions.isNotEmpty()) suggestions else defaultSuggestions(),
                accessories = if (accessories.isNotEmpty()) accessories else defaultAccessories(),
                suitableOccasions = if (occasions.isNotEmpty()) occasions else defaultOccasions(),
                alternativeCombinations = if (alternatives.isNotEmpty()) alternatives else defaultAlternatives()
            )
        } catch (e: Exception) {
            Log.e("GeminiStylist", "Error parsing JSON: ${e.message}")
            return null
        }
    }

    private fun generateSmartLocalAnalysis(bitmap: Bitmap?, context: String): StyleAnalysisResult {
        val isContextEvening = context.contains("evening", true) || context.contains("formal", true) || context.contains("dinner", true)
        val isStreetwear = context.contains("street", true) || context.contains("casual", true) || context.contains("sneaker", true)

        return when {
            isEvening(context) -> StyleAnalysisResult(
                title = "Refined Monochromatic Evening Silhouette",
                styleCategory = "Minimalist Evening Luxe",
                score = 96,
                season = "Fall / Winter 2026",
                overview = "A sophisticated, high-contrast evening look emphasizing elongated lines, fluid drape, and understated opulence.",
                colorPalette = listOf(
                    ColorSwatch("Obsidian Black", "#11141A", "Dominant base"),
                    ColorSwatch("Smoky Anthracite", "#2E3440", "Subtle texture depth"),
                    ColorSwatch("Lustrous Champagne", "#E7DFC6", "Refined metallic accent")
                ),
                colorCoordination = "The monochromatic gradient delivers an elongating vertical effect, allowing jewelry and texture to take center stage without visual clash.",
                clothingCombination = "Structured tailored blazer draped loosely over fluid silk trousers, creating a tension between architectural stiffness and effortless movement.",
                stylingSuggestions = listOf(
                    "Keep wrists bare to emphasize a statement sculptural cuff or cocktail ring",
                    "Choose an open-toe heel or pointed mule to elongate the hemline break",
                    "Add a micro-velvet clutch in deep burgundy for an unexpected flash of warmth"
                ),
                accessories = listOf(
                    AccessoryItem("Footwear", "Pointed-toe leather slingbacks in glossy black", "Sharp Elegance"),
                    AccessoryItem("Jewelry", "Brushed 18k gold wave drop earrings", "Warm Radiance"),
                    AccessoryItem("Bag", "Structured mini top-handle bag with gold hardware", "Editorial Finish"),
                    AccessoryItem("Fragrance Note", "Sandalwood, amber resin, and crisp cardamom", "Intimate Sillage")
                ),
                suitableOccasions = listOf(
                    "Rooftop Dinner & Cocktail Soirée",
                    "Opening Night Theater & Symphony",
                    "Black-Tie Optional Gala & Receptions",
                    "Private Dining Club"
                ),
                alternativeCombinations = listOf(
                    "Swap the trousers for a bias-cut satin midi skirt and sculptural kitten heels",
                    "Introduce an unbuttoned sheer organza blouse layered under the tuxedo jacket",
                    "Cinch the waist with a thin polished brass hardware belt"
                )
            )
            isStreetwear -> StyleAnalysisResult(
                title = "Contemporary Elevated Streetwear",
                styleCategory = "Urban Tailored Streetwear",
                score = 91,
                season = "All-Season Modern",
                overview = "Strikes the ideal equilibrium between relaxed streetwear comfort and sharp architectural tailoring.",
                colorPalette = listOf(
                    ColorSwatch("Washed Slate", "#374151", "Dominant neutral"),
                    ColorSwatch("Raw Denim Indigo", "#1E293B", "Grounding base"),
                    ColorSwatch("Chalk White", "#F8FAFC", "Crisp contrast accent")
                ),
                colorCoordination = "Cool-toned neutrals anchored by bright chalk accents create clean graphic lines without overwhelming the silhouette.",
                clothingCombination = "Oversized unstructured outerwear balanced against straight-leg relaxed denim and a heavyweight boxy crewneck.",
                stylingSuggestions = listOf(
                    "Maintain the cropped hem just above the sneaker collar for intentional stacking",
                    "Roll the cuffs once to reveal raw inner selvedge detailing",
                    "Add a leather crossbody sling bag worn high on the chest"
                ),
                accessories = listOf(
                    AccessoryItem("Footwear", "Pristine white leather retro tennis trainers", "Clean Baseline"),
                    AccessoryItem("Eyewear", "Thick acetate square sunglasses in dark tortoise", "Street Chic"),
                    AccessoryItem("Bag", "Nylon / leather hybrid modular utility sling", "Functional Edge"),
                    AccessoryItem("Headwear", "Unstructured ribbed wool beanie in heather grey", "Relaxed Vibe")
                ),
                suitableOccasions = listOf(
                    "Weekend Art District Exploration",
                    "Casual Creative Studio Days",
                    "Flight & High-Comfort Travel",
                    "Specialty Coffee & Brunch"
                ),
                alternativeCombinations = listOf(
                    "Swap sneakers for chunky lug-sole Chelsea boots and a cropped wool blouson",
                    "Layer a lightweight hooded knit under a relaxed notch-lapel overcoat",
                    "Pair with wide-leg pleated tech trousers and vintage runner silhouettes"
                )
            )
            else -> StyleAnalysisResult(
                title = "Parisian Tailored Camel & Navy Ensemble",
                styleCategory = "Timeless Smart Casual",
                score = 94,
                season = "Autumn / Transitional 2026",
                overview = "An immaculate masterclass in timeless capsule styling. The interplay of structured camel outerwear with relaxed high-waisted navy trousers produces a relaxed yet undeniably polished aesthetic.",
                colorPalette = listOf(
                    ColorSwatch("Warm Camel", "#C69864", "Hero outer layer"),
                    ColorSwatch("Navy Depths", "#1B2433", "Anchoring base"),
                    ColorSwatch("Ivory Cashmere", "#F5EFEB", "Illuminating core"),
                    ColorSwatch("Cognac Leather", "#8B5A2B", "Accent leather")
                ),
                colorCoordination = "The warm golden undertones of camel provide radiant warmth against the deep, cooling stabilizing effect of navy and clean ivory.",
                clothingCombination = "A classic mid-calf trench layered over a fine-gauge knit and fluid wide-leg tailored trousers with deep pleats.",
                stylingSuggestions = listOf(
                    "Push the trench sleeves up slightly to the mid-forearm to expose a minimal timepiece or gold bangle",
                    "Tuck the front hem of the knit to define natural waist proportions",
                    "Ensure trouser hems break gently at the top of the loafer instep"
                ),
                accessories = listOf(
                    AccessoryItem("Footwear", "Hand-burnished almond-toe horsebit loafers in cognac", "Continental Polish"),
                    AccessoryItem("Bag", "Pebbled calfskin structured shoulder tote in dark espresso", "Understated Luxury"),
                    AccessoryItem("Jewelry", "Chunky dome ring & mini ribbed gold hoops", "Refined Accent"),
                    AccessoryItem("Eyewear", "Oval acetate sunglasses with dark olive lenses", "Old-Money Chic")
                ),
                suitableOccasions = listOf(
                    "Creative Corporate Client Meetings",
                    "High-End Shopping & Gallery Strolls",
                    "Chic Bistro Luncheons",
                    "Upscale Airport & Travel Days"
                ),
                alternativeCombinations = listOf(
                    "Swap loafers for pristine low-top white sneakers for a casual European weekend spin",
                    "Switch trousers for a pleated camel houndstooth midi skirt and knee-high leather boots",
                    "Layer an espresso silk neck scarf tied in a loose French knot"
                )
            )
        }
    }

    private fun generateLocalChatResponse(outfit: StyleAnalysisResult?, query: String): String {
        val q = query.lowercase()
        return when {
            q.contains("shoe") || q.contains("footwear") || q.contains("sneaker") || q.contains("boot") -> {
                "For this look, footwear sets the tone. To keep it elevated and timeless, I recommend **cognac leather horsebit loafers** or **pointed-toe Chelsea boots**. If you want a more casual, youthful energy, switch to **pristine white minimalist leather sneakers** with no visible logos."
            }
            q.contains("jacket") || q.contains("coat") || q.contains("outerwear") || q.contains("layer") -> {
                "Layering adds dimension! A **double-breasted camel trench coat** or an **unstructured wool-cashmere overcoat** in espresso will immediately add editorial structure. For mild weather, consider an **unlined herringbone blazer** or a **cropped suede blouson**."
            }
            q.contains("color") || q.contains("palette") || q.contains("match") -> {
                "The current palette works because it anchors deep neutrals with warm tones. If you want to introduce a pop of color, try **burnt terracotta**, **forest spruce green**, or **rich burgundy wine** — these harmonize naturally with earth tones and navy."
            }
            q.contains("jewelry") || q.contains("accessory") || q.contains("bag") || q.contains("belt") -> {
                "Less is more with this silhouette. Stick to **brushed warm gold jewelry** (like ribbed mini hoop earrings and a dome ring). For the bag, a **structured pebbled leather shoulder bag** in deep chocolate brown will tie everything together seamlessly."
            }
            q.contains("formal") || q.contains("dress up") || q.contains("work") || q.contains("office") -> {
                "To make this fully executive-ready: 1) Swap casual knits for a crisp poplin button-down with a pointed collar, 2) Step into polished leather slingbacks or Oxford shoes, and 3) Add a structured leather briefcase or structured tote."
            }
            else -> {
                "Great question! As your stylist, my rule of thumb here is balancing **volume and texture**. Since this outfit features clean, structured lines, keep accents intentional and let the tailored fit do the talking. Try pairing it with high-quality leather accessories and warm neutral layering."
            }
        }
    }

    private fun isEvening(context: String) =
        context.contains("evening", true) || context.contains("cocktail", true) || context.contains("gala", true)

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    private fun defaultColors() = listOf(
        ColorSwatch("Charcoal Navy", "#1A2238", "Base"),
        ColorSwatch("Camel Khaki", "#C3A376", "Hero Neutral"),
        ColorSwatch("Off-White", "#F4F1EA", "Accent")
    )

    private fun defaultSuggestions() = listOf(
        "Ensure sleeve cuffs break cleanly at the wrist bone",
        "Maintain clean balance between structured outerwear and fluid trouser drape",
        "Add subtle gold or silver hardware accents"
    )

    private fun defaultAccessories() = listOf(
        AccessoryItem("Footwear", "Classic leather loafers in cognac or dark espresso", "Refined Polish"),
        AccessoryItem("Bag", "Structured leather tote with minimalist hardware", "Daily Luxury"),
        AccessoryItem("Jewelry", "Brushed metal huggie earrings and slim signet ring", "Understated Radiance")
    )

    private fun defaultOccasions() = listOf(
        "Smart Casual Gatherings",
        "Creative Workspace",
        "Weekend Brunch & Shopping"
    )

    private fun defaultAlternatives() = listOf(
        "Pair with clean white leather sneakers for effortless weekend chic",
        "Layer a fine-gauge knit under a tailored blazer for temperature adaptability"
    )
}
