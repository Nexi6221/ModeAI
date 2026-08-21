package com.example.styleai.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.styleai.data.model.StyleAnalysisResult
import com.example.styleai.ui.components.AccessoryCard
import com.example.styleai.ui.components.ColorPaletteRow
import com.example.styleai.ui.components.LuxuryCard
import com.example.styleai.ui.components.SectionHeader
import com.example.styleai.ui.components.StyleScoreBadge
import com.example.styleai.ui.components.SuggestionBullet
import com.example.ui.theme.BorderHighlight
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldMuted
import com.example.ui.theme.NavyCharcoal
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SoftIvory
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmChampagne

@Composable
fun OutfitDetailScreen(
    outfit: StyleAnalysisResult,
    onBackClick: () -> Unit,
    onOpenInStylist: () -> Unit,
    onDeleteClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .verticalScroll(scrollState)
            .padding(bottom = 60.dp)
    ) {
        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SurfaceCard)
                    .border(1.dp, BorderSubtle, CircleShape)
                    .testTag("outfit_detail_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = SoftIvory,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = "OUTFIT BREAKDOWN",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = GoldMuted,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            if (outfit.id > 0) {
                IconButton(
                    onClick = { onDeleteClick(outfit.id) },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SurfaceCard)
                        .border(1.dp, BorderSubtle, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = SoftIvory,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(40.dp))
            }
        }

        // Hero Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderHighlight, RoundedCornerShape(20.dp))
        ) {
            if (outfit.imageResId != null) {
                Image(
                    painter = painterResource(id = outfit.imageResId),
                    contentDescription = outfit.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (outfit.imageUri != null) {
                AsyncImage(
                    model = outfit.imageUri,
                    contentDescription = outfit.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Style,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            StyleScoreBadge(
                score = outfit.score,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title & Category Header Card
        LuxuryCard(
            modifier = Modifier.padding(horizontal = 16.dp),
            borderGold = true
        ) {
            Text(
                text = outfit.styleCategory.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = GoldMuted,
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = outfit.title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Season: ${outfit.season}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 1. Overview
        LuxuryCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionHeader(
                title = "Styling Overview",
                icon = Icons.Default.Style
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = outfit.overview,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = TextPrimary,
                    lineHeight = 22.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Color Palette & Harmony
        LuxuryCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionHeader(
                title = "Color Coordination",
                icon = Icons.Default.Palette
            )
            Spacer(modifier = Modifier.height(12.dp))
            ColorPaletteRow(palette = outfit.colorPalette)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = outfit.colorCoordination,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Clothing Proportions & Combinations
        LuxuryCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionHeader(
                title = "Clothing Combination",
                icon = Icons.AutoMirrored.Filled.DirectionsWalk
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = outfit.clothingCombination,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextPrimary,
                    lineHeight = 20.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Styling Suggestions
        LuxuryCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionHeader(
                title = "Styling Suggestions",
                icon = Icons.Default.Lightbulb
            )
            Spacer(modifier = Modifier.height(10.dp))
            outfit.stylingSuggestions.forEach { suggestion ->
                SuggestionBullet(text = suggestion)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 5. Recommended Accessories
        LuxuryCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionHeader(
                title = "Recommended Accessories",
                icon = Icons.Default.ShoppingBag
            )
            Spacer(modifier = Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                outfit.accessories.forEach { accessory ->
                    AccessoryCard(item = accessory)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 6. Occasions
        LuxuryCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionHeader(
                title = "Suitable Occasions",
                icon = Icons.Default.Event
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                outfit.suitableOccasions.forEach { occasion ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = SurfaceDark,
                        modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = occasion,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = TextPrimary,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 7. Alternative Combinations
        LuxuryCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionHeader(
                title = "Alternative Combinations",
                icon = Icons.Default.SwapHoriz
            )
            Spacer(modifier = Modifier.height(10.dp))
            outfit.alternativeCombinations.forEach { alternative ->
                SuggestionBullet(text = alternative)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action CTA to consult AI Stylist about this look
        Button(
            onClick = onOpenInStylist,
            colors = ButtonDefaults.buttonColors(
                containerColor = GoldAccent,
                contentColor = ObsidianBlack
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(48.dp)
                .testTag("detail_consult_stylist_button")
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Consult AI Stylist About This Look",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}
