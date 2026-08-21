package com.example.styleai.data.datasource

import com.example.R
import com.example.styleai.data.model.AccessoryItem
import com.example.styleai.data.model.ColorSwatch
import com.example.styleai.data.model.InspirationLook
import com.example.styleai.data.model.StyleAnalysisResult

object FashionDataSource {

    val sampleInspirations: List<InspirationLook> = listOf(
        InspirationLook(
            id = "look_1",
            title = "Parisian Camel & Navy Tailored Look",
            subtitle = "Effortless high-low proportion balancing",
            category = "Timeless Smart Casual",
            drawableResId = R.drawable.outfit_look_1,
            tags = listOf("Tailoring", "Trench", "Neutrals", "Score 94"),
            sampleAnalysis = StyleAnalysisResult(
                title = "Parisian Camel & Navy Tailored Ensemble",
                imageResId = R.drawable.outfit_look_1,
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
        ),
        InspirationLook(
            id = "look_2",
            title = "Modern Charcoal & Olive Minimalist",
            subtitle = "Contemporary smart tailoring with relaxed ease",
            category = "Modern Smart Casual",
            drawableResId = R.drawable.outfit_look_2,
            tags = listOf("Blazer", "Olive Chino", "Clean Sneaker", "Score 92"),
            sampleAnalysis = StyleAnalysisResult(
                title = "Charcoal Blazer & Olive Chino Pairing",
                imageResId = R.drawable.outfit_look_2,
                styleCategory = "Modern Smart Casual",
                score = 92,
                season = "Spring / Summer 2026",
                overview = "An impeccably tailored smart casual look that seamlessly blends casual staples with sharp structure.",
                colorPalette = listOf(
                    ColorSwatch("Charcoal Heather", "#2D3748", "Structured blazer"),
                    ColorSwatch("Military Olive", "#556B2F", "Tonal contrast bottom"),
                    ColorSwatch("Crisp Optic White", "#FFFFFF", "Clean base tee"),
                    ColorSwatch("Saddle Tan", "#A0522D", "Watch strap & leather")
                ),
                colorCoordination = "Subtle earth-tone olive brings organic balance to technical charcoal, elevated by the high-contrast optic white base.",
                clothingCombination = "Unstructured lightweight wool-blend blazer over a heavyweight combed cotton crewneck and tapered pleat-front chinos.",
                stylingSuggestions = listOf(
                    "Keep the blazer unbuttoned to maintain an approachable, relaxed posture",
                    "Ensure sneakers are spotlessly clean to anchor the smart-casual balance",
                    "Add a low-profile stainless steel or leather watch"
                ),
                accessories = listOf(
                    AccessoryItem("Footwear", "Minimalist white leather tennis sneakers", "Modern Crispness"),
                    AccessoryItem("Timepiece", "Minimalist Bauhaus watch with olive leather strap", "Architectural Focus"),
                    AccessoryItem("Eyewear", "Keyhole bridge acetate sunglasses in black", "Classic Edge"),
                    AccessoryItem("Bag", "Waxed canvas and leather commuter messenger", "Functional Craft")
                ),
                suitableOccasions = listOf(
                    "Tech & Design Studio Work",
                    "Casual Business Dinners",
                    "Weekend Art Walk",
                    "Casual Conference Presentations"
                ),
                alternativeCombinations = listOf(
                    "Swap the white tee for a black merino polo knit for an evening transition",
                    "Switch chinos to dark raw selvedge denim with suede Chelsea boots",
                    "Layer with a lightweight utility overshirt instead of the blazer"
                )
            )
        ),
        InspirationLook(
            id = "look_3",
            title = "Cashmere Monochrome Luxe",
            subtitle = "Tonal cream textures with gold accents",
            category = "Monochrome Chic",
            drawableResId = R.drawable.outfit_look_3,
            tags = listOf("Cashmere", "Pleated", "Gold Jewelry", "Score 96"),
            sampleAnalysis = StyleAnalysisResult(
                title = "Cream Cashmere & Pleated Linen Monochrome",
                imageResId = R.drawable.outfit_look_3,
                styleCategory = "Monochrome Chic",
                score = 96,
                season = "Transitional Fall / Resort",
                overview = "The pinnacle of quiet luxury. Working within a single tonal family allows rich textural contrast between cashmere and flowing pleats to shine.",
                colorPalette = listOf(
                    ColorSwatch("Rich Cashmere Cream", "#F9F6F0", "Luxe knit top"),
                    ColorSwatch("Alabaster White", "#F1EBE1", "Draped bottom"),
                    ColorSwatch("Brushed 18K Gold", "#D4AF37", "Illuminating metals"),
                    ColorSwatch("Warm Sand", "#D7C4B7", "Subtle leather tone")
                ),
                colorCoordination = "Monochromatic warm whites create visual elongation and an aura of effortless elegance without looking sterile.",
                clothingCombination = "Plush mock-neck knit softly tucked into high-rise wide-leg pleated trousers with a subtle satin sheen.",
                stylingSuggestions = listOf(
                    "Layer varied textures (chunky knit + smooth weave) to create visual interest in a monochrome look",
                    "Stack delicate brushed gold rings and a sculptural cuff",
                    "Opt for a warm neutral lipstick shade to complement the cream palette"
                ),
                accessories = listOf(
                    AccessoryItem("Footwear", "Square-toe leather mules in warm taupe", "Sculptural Grace"),
                    AccessoryItem("Jewelry", "Chunky textured gold huggies and snake chain necklace", "Warm Radiance"),
                    AccessoryItem("Bag", "Soft slouchy leather dumpling pouch in almond", "Tactile Luxury"),
                    AccessoryItem("Belt", "Slim leather belt with brushed brass buckle", "Waist Definition")
                ),
                suitableOccasions = listOf(
                    "Intimate Wine Tasting & Dinner",
                    "Museum & Architectural Tours",
                    "Boutique Hotel Weekend Getaways",
                    "Private Brunch Gatherings"
                ),
                alternativeCombinations = listOf(
                    "Drape an oversized mocha wool trench over the shoulders for high-contrast drama",
                    "Swap trousers for a bias-cut cream satin slip skirt and heeled ankle boots",
                    "Add an espresso woven leather bag for rich grounded contrast"
                )
            )
        )
    )

    val quickStyleQuestions = listOf(
        "Which footwear elevates this outfit?",
        "What jacket or coat works best?",
        "How can I make this work-appropriate?",
        "What jewelry or accessories to add?",
        "Suggest a bold color accent to pair"
    )

    val fashionQuotes = listOf(
        "“Style is a way to say who you are without having to speak.” — Rachel Zoe",
        "“Fashion fades, only style remains the same.” — Coco Chanel",
        "“Elegance is not about being noticed, it's about being remembered.” — Giorgio Armani",
        "“Simplicity is the keynote of all true elegance.” — Coco Chanel"
    )
}
