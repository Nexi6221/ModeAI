package com.example.styleai.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.styleai.ui.screens.HomeScreen
import com.example.styleai.ui.screens.OutfitDetailScreen
import com.example.styleai.ui.screens.ProfileScreen
import com.example.styleai.ui.screens.SavedOutfitsScreen
import com.example.styleai.ui.screens.StylistScreen
import com.example.styleai.ui.viewmodel.MainTab
import com.example.styleai.ui.viewmodel.StyleViewModel
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyCharcoal
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SoftIvory
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class NavItem(
    val tab: MainTab,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun MainApp(
    viewModel: StyleViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val selectedDetailOutfit by viewModel.selectedDetailOutfit.collectAsStateWithLifecycle()
    val savedCount by viewModel.savedCount.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    val navItems = listOf(
        NavItem(
            tab = MainTab.HOME,
            label = "Home",
            selectedIcon = Icons.Default.Home,
            unselectedIcon = Icons.Outlined.Home,
            testTag = "nav_home"
        ),
        NavItem(
            tab = MainTab.STYLIST,
            label = "Stylist",
            selectedIcon = Icons.Default.AutoAwesome,
            unselectedIcon = Icons.Outlined.AutoAwesome,
            testTag = "nav_stylist"
        ),
        NavItem(
            tab = MainTab.SAVED,
            label = "Lookbook",
            selectedIcon = Icons.Default.Bookmark,
            unselectedIcon = Icons.Outlined.BookmarkBorder,
            testTag = "nav_saved"
        ),
        NavItem(
            tab = MainTab.PROFILE,
            label = "Profile",
            selectedIcon = Icons.Default.Person,
            unselectedIcon = Icons.Outlined.PersonOutline,
            testTag = "nav_profile"
        )
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ObsidianBlack,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 80.dp)
            )
        },
        bottomBar = {
            if (selectedDetailOutfit == null) {
                Surface(
                    color = NavyCharcoal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderSubtle, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                ) {
                    NavigationBar(
                        containerColor = NavyCharcoal,
                        tonalElevation = 0.dp,
                        modifier = Modifier.height(70.dp)
                    ) {
                        navItems.forEach { item ->
                            val isSelected = currentTab == item.tab
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { viewModel.setTab(item.tab) },
                                icon = {
                                    if (item.tab == MainTab.SAVED && savedCount > 0) {
                                        BadgedBox(
                                            badge = {
                                                Badge(
                                                    containerColor = GoldAccent,
                                                    contentColor = ObsidianBlack
                                                ) {
                                                    Text("$savedCount", fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                                contentDescription = item.label,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    } else {
                                        Icon(
                                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                            contentDescription = item.label,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = item.label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = GoldAccent,
                                    selectedTextColor = GoldAccent,
                                    indicatorColor = SurfaceCardElevated,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.testTag(item.testTag)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedDetailOutfit != null) {
                OutfitDetailScreen(
                    outfit = selectedDetailOutfit!!,
                    onBackClick = { viewModel.closeOutfitDetail() },
                    onOpenInStylist = {
                        val outfit = selectedDetailOutfit!!
                        viewModel.closeOutfitDetail()
                        viewModel.setTab(MainTab.STYLIST)
                    },
                    onDeleteClick = { id ->
                        viewModel.deleteOutfit(id)
                    }
                )
            } else {
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "TabContentAnimation"
                ) { tab ->
                    when (tab) {
                        MainTab.HOME -> HomeScreen(
                            viewModel = viewModel,
                            onNavigateToStylist = { viewModel.setTab(MainTab.STYLIST) },
                            onNavigateToSaved = { viewModel.setTab(MainTab.SAVED) },
                            onOpenOutfitDetail = { viewModel.openOutfitDetail(it) }
                        )
                        MainTab.STYLIST -> StylistScreen(
                            viewModel = viewModel
                        )
                        MainTab.SAVED -> SavedOutfitsScreen(
                            viewModel = viewModel,
                            onOpenOutfitDetail = { viewModel.openOutfitDetail(it) },
                            onNavigateToStylist = { viewModel.setTab(MainTab.STYLIST) }
                        )
                        MainTab.PROFILE -> ProfileScreen(
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}
