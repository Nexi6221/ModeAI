package com.example.styleai.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.R
import com.example.styleai.data.datasource.FashionDataSource
import com.example.styleai.data.model.InspirationLook
import com.example.styleai.data.model.StyleAnalysisResult
import com.example.styleai.ui.components.AccessoryCard
import com.example.styleai.ui.components.ChatBubble
import com.example.styleai.ui.components.ColorPaletteRow
import com.example.styleai.ui.components.LuxuryCard
import com.example.styleai.ui.components.SectionHeader
import com.example.styleai.ui.components.StyleScoreBadge
import com.example.styleai.ui.components.SuggestionBullet
import com.example.styleai.ui.viewmodel.AnalysisUiState
import com.example.styleai.ui.viewmodel.StyleViewModel
import com.example.ui.theme.BorderHighlight
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldMuted
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.NavyCharcoal
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.RoseError
import com.example.ui.theme.SoftIvory
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmChampagne

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun StylistScreen(
    viewModel: StyleViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val analysisState by viewModel.analysisState.collectAsStateWithLifecycle()
    val selectedImageUri by viewModel.selectedImageUri.collectAsStateWithLifecycle()
    val selectedResId by viewModel.selectedResId.collectAsStateWithLifecycle()
    val selectedBitmap by viewModel.selectedBitmap.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isChatLoading by viewModel.isChatLoading.collectAsStateWithLifecycle()

    var questionInput by remember { mutableStateOf("") }
    var showSampleSelectorDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onImageSelected(it, context) }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let { viewModel.onBitmapCaptured(it) }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .verticalScroll(scrollState)
            .imePadding()
            .padding(bottom = 100.dp)
    ) {
        // Stylist Screen Top Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "AI FASHION STYLIST",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GoldMuted,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Outfit Critique & Pairings",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(GoldAccent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Image Selection & Preview Card
        LuxuryCard(
            modifier = Modifier.padding(horizontal = 16.dp),
            borderGold = (analysisState is AnalysisUiState.Success)
        ) {
            val hasImage = selectedImageUri != null || selectedResId != null || selectedBitmap != null

            if (hasImage) {
                // Image is selected -> show preview with controls
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceDark)
                ) {
                    when {
                        selectedBitmap != null -> {
                            Image(
                                bitmap = selectedBitmap!!.asImageBitmap(),
                                contentDescription = "Outfit preview",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        selectedResId != null -> {
                            Image(
                                painter = painterResource(id = selectedResId!!),
                                contentDescription = "Outfit preview",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        selectedImageUri != null -> {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Outfit preview",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    // Change Photo Button
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ObsidianBlack.copy(alpha = 0.75f),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .clickable { showSampleSelectorDialog = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Change Outfit",
                                tint = SoftIvory,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Change",
                                style = MaterialTheme.typography.labelSmall.copy(color = SoftIvory)
                            )
                        }
                    }
                }
            } else {
                // Empty state -> Prompt user to select/upload/camera/choose sample
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(SurfaceDark)
                            .border(1.dp, BorderHighlight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = "Upload Outfit",
                            tint = GoldAccent,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Upload or Select an Outfit",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )

                    Text(
                        text = "Choose a photo from your gallery, capture one with your camera, or pick from our curated collection.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { photoPickerLauncher.launch("image/*") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldAccent,
                                contentColor = ObsidianBlack
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("stylist_pick_photo_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Gallery", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { cameraLauncher.launch(null) },
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderHighlight),
                            modifier = Modifier.testTag("stylist_camera_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = null,
                                tint = SoftIvory,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Camera", color = SoftIvory)
                        }

                        OutlinedButton(
                            onClick = { showSampleSelectorDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f)),
                            modifier = Modifier.testTag("stylist_samples_button")
                        ) {
                            Text("Lookbook", color = GoldAccent)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Button: "Analyze Outfit"
            val isAnalyzing = analysisState is AnalysisUiState.Analyzing
            Button(
                onClick = { viewModel.analyzeCurrentOutfit(context) },
                enabled = !isAnalyzing,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldAccent,
                    contentColor = ObsidianBlack,
                    disabledContainerColor = SurfaceCardElevated
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("stylist_analyze_button")
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = GoldAccent,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI Stylist Analyzing...",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (analysisState is AnalysisUiState.Success) "Re-Analyze Outfit" else "Analyze Outfit",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Analysis Results Display
        when (val state = analysisState) {
            is AnalysisUiState.Idle -> {
                // Curated Look Samples Horizontal Row
                Text(
                    text = "OR CHOOSE A CURATED LOOK TO CRITIQUE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GoldMuted,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(FashionDataSource.sampleInspirations) { look ->
                        CuratedLookThumbCard(
                            look = look,
                            onClick = { viewModel.selectSampleLook(look) }
                        )
                    }
                }
            }

            is AnalysisUiState.Analyzing -> {
                LuxuryCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = GoldAccent,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Consulting Fashion Intelligence Engine...",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Evaluating silhouette balance, color temperature harmony, garment pairings, and occasion versatility.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextSecondary,
                                fontSize = 12.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        )
                    }
                }
            }

            is AnalysisUiState.Error -> {
                LuxuryCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Analysis Notice",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = RoseError,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.analyzeCurrentOutfit(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Retry Analysis", color = SoftIvory)
                        }
                    }
                }
            }

            is AnalysisUiState.Success -> {
                val result = state.result
                OutfitAnalysisResultView(
                    result = result,
                    onToggleSave = { viewModel.toggleSaveCurrentOutfit() }
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Interactive AI Stylist Follow-up Q&A Section
        StylistChatSection(
            messages = chatMessages,
            isLoading = isChatLoading,
            questionInput = questionInput,
            onQuestionInputChange = { questionInput = it },
            onSendQuestion = {
                viewModel.askStylist(questionInput)
                questionInput = ""
                focusManager.clearFocus()
            },
            onQuickPromptClick = { prompt ->
                viewModel.askStylist(prompt)
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }

    // Curated Look Selector Modal Dialog
    if (showSampleSelectorDialog) {
        AlertDialog(
            onDismissRequest = { showSampleSelectorDialog = false },
            title = {
                Text(
                    text = "Select an Outfit",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = "Choose from one of our editorial looks to test the AI stylist immediately:",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    FashionDataSource.sampleInspirations.forEach { look ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.selectSampleLook(look)
                                    showSampleSelectorDialog = false
                                },
                            color = SurfaceDark
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = look.drawableResId),
                                    contentDescription = look.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = look.title,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    )
                                    Text(
                                        text = look.category,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = GoldMuted,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSampleSelectorDialog = false }) {
                    Text("Close", color = GoldAccent)
                }
            },
            containerColor = SurfaceCard,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun OutfitAnalysisResultView(
    result: StyleAnalysisResult,
    onToggleSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Header & Save Action Card
        LuxuryCard(
            modifier = Modifier.padding(horizontal = 16.dp),
            borderGold = true
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = result.styleCategory.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GoldMuted,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = result.title,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = result.season,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StyleScoreBadge(score = result.score)

                    IconButton(
                        onClick = onToggleSave,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (result.isSaved) GoldAccent else SurfaceCardElevated)
                            .testTag("toggle_save_outfit_button")
                    ) {
                        Icon(
                            imageVector = if (result.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (result.isSaved) "Saved" else "Save Outfit",
                            tint = if (result.isSaved) ObsidianBlack else SoftIvory,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // 1. Overview Section
        LuxuryCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionHeader(
                title = "Outfit Overview",
                icon = Icons.Default.Style,
                subtitle = "Silhouette balance & aesthetic cohesion"
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = result.overview,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = TextPrimary,
                    lineHeight = 22.sp
                )
            )
        }

        // 2. Color Coordination & Palette
        LuxuryCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionHeader(
                title = "Color Coordination",
                icon = Icons.Default.Palette,
                subtitle = "Harmonized tonal swatches & contrast balance"
            )
            Spacer(modifier = Modifier.height(12.dp))
            ColorPaletteRow(palette = result.colorPalette)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = result.colorCoordination,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            )
        }

        // 3. Clothing Combination & Proportions
        LuxuryCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionHeader(
                title = "Clothing Combination",
                icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                subtitle = "Garment dialogue & fabric drape"
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = result.clothingCombination,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextPrimary,
                    lineHeight = 20.sp
                )
            )
        }

        // 4. Styling Suggestions
        LuxuryCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionHeader(
                title = "Styling Suggestions",
                icon = Icons.Default.Lightbulb,
                subtitle = "High-impact adjustments to elevate the look"
            )
            Spacer(modifier = Modifier.height(10.dp))
            result.stylingSuggestions.forEach { suggestion ->
                SuggestionBullet(text = suggestion)
            }
        }

        // 5. Accessories Recommendations
        LuxuryCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionHeader(
                title = "Recommended Accessories",
                icon = Icons.Default.ShoppingBag,
                subtitle = "Footwear, bags, jewelry & finishing touches"
            )
            Spacer(modifier = Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                result.accessories.forEach { accessory ->
                    AccessoryCard(item = accessory)
                }
            }
        }

        // 6. Suitable Occasions
        LuxuryCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionHeader(
                title = "Suitable Occasions",
                icon = Icons.Default.Event,
                subtitle = "Where this outfit excels"
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                result.suitableOccasions.forEach { occasion ->
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

        // 7. Alternative Combinations
        LuxuryCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionHeader(
                title = "Alternative Combinations",
                icon = Icons.Default.SwapHoriz,
                subtitle = "Capsule wardrobe mix-and-match"
            )
            Spacer(modifier = Modifier.height(10.dp))
            result.alternativeCombinations.forEach { alternative ->
                SuggestionBullet(text = alternative)
            }
        }
    }
}

@Composable
fun StylistChatSection(
    messages: List<com.example.styleai.data.model.StylistChatMessage>,
    isLoading: Boolean,
    questionInput: String,
    onQuestionInputChange: (String) -> Unit,
    onSendQuestion: () -> Unit,
    onQuickPromptClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LuxuryCard(modifier = modifier) {
        SectionHeader(
            title = "Ask AI Stylist",
            icon = Icons.Default.AutoAwesome,
            subtitle = "Ask follow-up questions & get custom recommendations"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Suggestion Chips
        Text(
            text = "QUICK TOPICS",
            style = MaterialTheme.typography.labelSmall.copy(
                color = GoldMuted,
                letterSpacing = 1.sp,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FashionDataSource.quickStyleQuestions.forEach { prompt ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceDark,
                    modifier = Modifier
                        .border(1.dp, BorderHighlight, RoundedCornerShape(16.dp))
                        .clickable { onQuickPromptClick(prompt) }
                ) {
                    Text(
                        text = prompt,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SoftIvory,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Chat conversation history (shows last 4 messages)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            messages.takeLast(4).forEach { message ->
                ChatBubble(message = message)
            }

            if (isLoading) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = GoldAccent,
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = "Stylist is crafting your advice...",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Text input field + Send Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = questionInput,
                onValueChange = onQuestionInputChange,
                placeholder = {
                    Text(
                        text = "Ask about shoes, jewelry, layers...",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted, fontSize = 13.sp)
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("stylist_chat_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldAccent,
                    unfocusedBorderColor = BorderSubtle,
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSendQuestion() }),
                singleLine = true
            )

            IconButton(
                onClick = onSendQuestion,
                enabled = questionInput.isNotBlank() && !isLoading,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (questionInput.isNotBlank() && !isLoading) GoldAccent else SurfaceCardElevated)
                    .testTag("stylist_chat_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (questionInput.isNotBlank() && !isLoading) ObsidianBlack else TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun CuratedLookThumbCard(
    look: InspirationLook,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .width(130.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = SurfaceDark
    ) {
        Column {
            Image(
                painter = painterResource(id = look.drawableResId),
                contentDescription = look.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = look.title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 11.sp
                    ),
                    maxLines = 1
                )
                Text(
                    text = "Score ${look.sampleAnalysis.score}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GoldAccent,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}
