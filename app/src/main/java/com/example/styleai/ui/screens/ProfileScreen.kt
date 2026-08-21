package com.example.styleai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.styleai.data.model.UserStylePreferences
import com.example.styleai.ui.components.LuxuryCard
import com.example.styleai.ui.components.SectionHeader
import com.example.styleai.ui.viewmodel.StyleViewModel
import com.example.ui.theme.BorderHighlight
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldMuted
import com.example.ui.theme.NavyCharcoal
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SoftIvory
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmChampagne

@Composable
fun ProfileScreen(
    viewModel: StyleViewModel,
    modifier: Modifier = Modifier
) {
    val userPrefs by viewModel.userPreferences.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    var showEditProfileDialog by remember { mutableStateOf(false) }

    val archetypes = listOf(
        "Minimalist Luxe & Tailored",
        "Classic Contemporary",
        "Elevated Streetwear",
        "Monochromatic Avant-Garde"
    )

    val palettes = listOf(
        "Warm Neutrals & Charcoal",
        "Monochrome & Grayscale",
        "Earth & Terracotta Tones",
        "Deep Jewel & Navy"
    )

    val fits = listOf(
        "Structured Relaxed",
        "Slim Tailored",
        "Oversized Flowing",
        "Boxy Architectural"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .verticalScroll(scrollState)
            .padding(bottom = 90.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "STYLE PROFILE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GoldMuted,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Personal Fashion Identity",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            }
        }

        // Profile Card
        LuxuryCard(
            modifier = Modifier.padding(horizontal = 16.dp),
            borderGold = true
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Monogram Avatar
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(GoldAccent)
                        .border(2.dp, WarmChampagne, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userPrefs.name.take(2).uppercase(),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = ObsidianBlack
                        )
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = userPrefs.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = userPrefs.archetype,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = GoldMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = "Tailored styling recommendations active",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }

                IconButton(
                    onClick = { showEditProfileDialog = true },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(SurfaceDark)
                        .border(1.dp, BorderHighlight, CircleShape)
                        .testTag("edit_profile_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = SoftIvory,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Style Archetype Selector Card
        LuxuryCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionHeader(
                title = "Style Archetype",
                icon = Icons.Default.Style,
                subtitle = "Guides your AI recommendations"
            )
            Spacer(modifier = Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                archetypes.forEach { archetype ->
                    val isSelected = userPrefs.archetype == archetype
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) SurfaceCardElevated else SurfaceDark,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                if (isSelected) GoldAccent else BorderSubtle,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                viewModel.updatePreferences(userPrefs.copy(archetype = archetype))
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = archetype,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = GoldAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Color Palette Preferences
        LuxuryCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionHeader(
                title = "Primary Color Palette",
                icon = Icons.Default.Palette,
                subtitle = "Your default tonal universe"
            )
            Spacer(modifier = Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                palettes.forEach { palette ->
                    val isSelected = userPrefs.primaryPalette == palette
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) SurfaceCardElevated else SurfaceDark,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                if (isSelected) GoldAccent else BorderSubtle,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                viewModel.updatePreferences(userPrefs.copy(primaryPalette = palette))
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = palette,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = GoldAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Fit & Proportions
        LuxuryCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionHeader(
                title = "Fit & Silhouette",
                icon = Icons.Default.Settings,
                subtitle = "Preferred drape and garment cut"
            )
            Spacer(modifier = Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                fits.forEach { fit ->
                    val isSelected = userPrefs.fitPreference == fit
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) SurfaceCardElevated else SurfaceDark,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                if (isSelected) GoldAccent else BorderSubtle,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                viewModel.updatePreferences(userPrefs.copy(fitPreference = fit))
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = fit,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = GoldAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // AI Engine & Security Status
        LuxuryCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionHeader(
                title = "AI Stylist Engine",
                icon = Icons.Default.AutoAwesome
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Gemini Fashion Model",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = if (viewModel.hasApiKey()) "Active • Multimodal Gemini 3.5 Flash" else "Smart On-Device Fashion Engine",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (viewModel.hasApiKey()) EmeraldSuccess else GoldMuted,
                            fontSize = 12.sp
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (viewModel.hasApiKey()) EmeraldSuccess.copy(alpha = 0.15f) else GoldAccent.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (viewModel.hasApiKey()) "CONNECTED" else "STANDALONE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (viewModel.hasApiKey()) EmeraldSuccess else GoldAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // About StyleAI
        LuxuryCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionHeader(
                title = "About StyleAI",
                icon = Icons.Default.Info
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "StyleAI is a personal fashion stylist application built with modern Kotlin and Jetpack Compose. It empowers you to understand outfit dynamics, optimize proportions, balance colors, and assemble capsule wardrobes.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Version 1.0.0 • On-Device Room Persistence",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontSize = 11.sp
                )
            )
        }
    }

    // Edit Name Dialog
    if (showEditProfileDialog) {
        var tempName by remember { mutableStateOf(userPrefs.name) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Text(
                    text = "Edit Profile Name",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("Full Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_name_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempName.isNotBlank()) {
                            viewModel.updatePreferences(userPrefs.copy(name = tempName.trim()))
                        }
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = ObsidianBlack)
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceCard,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
