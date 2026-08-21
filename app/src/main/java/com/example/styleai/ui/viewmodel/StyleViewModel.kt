package com.example.styleai.ui.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.styleai.data.api.GeminiStylistService
import com.example.styleai.data.datasource.FashionDataSource
import com.example.styleai.data.local.AppDatabase
import com.example.styleai.data.model.InspirationLook
import com.example.styleai.data.model.MessageSender
import com.example.styleai.data.model.StyleAnalysisResult
import com.example.styleai.data.model.StylistChatMessage
import com.example.styleai.data.model.UserStylePreferences
import com.example.styleai.data.repository.OutfitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.InputStream

sealed interface AnalysisUiState {
    object Idle : AnalysisUiState
    object Analyzing : AnalysisUiState
    data class Success(val result: StyleAnalysisResult) : AnalysisUiState
    data class Error(val message: String) : AnalysisUiState
}

enum class MainTab {
    HOME, STYLIST, SAVED, PROFILE
}

class StyleViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = OutfitRepository(database.outfitDao())
    private val stylistService = GeminiStylistService()

    // Navigation & Tabs
    private val _currentTab = MutableStateFlow(MainTab.HOME)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    // Saved outfits flow from Room
    val savedOutfits: StateFlow<List<StyleAnalysisResult>> = repository.allSavedOutfits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedCount: StateFlow<Int> = repository.savedCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Current Outfit Analysis State
    private val _analysisState = MutableStateFlow<AnalysisUiState>(AnalysisUiState.Idle)
    val analysisState: StateFlow<AnalysisUiState> = _analysisState.asStateFlow()

    // Selected image/look for analysis
    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    private val _selectedResId = MutableStateFlow<Int?>(null)
    val selectedResId: StateFlow<Int?> = _selectedResId.asStateFlow()

    private val _selectedBitmap = MutableStateFlow<Bitmap?>(null)
    val selectedBitmap: StateFlow<Bitmap?> = _selectedBitmap.asStateFlow()

    // Interactive Stylist Chat Messages
    private val _chatMessages = MutableStateFlow<List<StylistChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<StylistChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    // Selected Outfit for full detail preview modal/sheet
    private val _selectedDetailOutfit = MutableStateFlow<StyleAnalysisResult?>(null)
    val selectedDetailOutfit: StateFlow<StyleAnalysisResult?> = _selectedDetailOutfit.asStateFlow()

    // User preferences
    private val _userPreferences = MutableStateFlow(UserStylePreferences())
    val userPreferences: StateFlow<UserStylePreferences> = _userPreferences.asStateFlow()

    // Toast / Feedback message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        // Pre-populate with welcome message from AI stylist
        _chatMessages.value = listOf(
            StylistChatMessage(
                sender = MessageSender.STYLIST,
                text = "Welcome to StyleAI! I'm your dedicated fashion stylist. Select an outfit or upload a photo to receive tailored styling critiques, color harmonies, and silhouette pairings."
            )
        )

        // Seed initial curated looks to local database if empty so user has instant rich collection
        viewModelScope.launch {
            // Check if db is empty, if so insert one sample look to get started
            val initial = FashionDataSource.sampleInspirations.first().sampleAnalysis
            repository.saveOutfit(initial)
        }
    }

    fun setTab(tab: MainTab) {
        _currentTab.value = tab
    }

    fun selectSampleLook(look: InspirationLook) {
        _selectedResId.value = look.drawableResId
        _selectedImageUri.value = null
        _selectedBitmap.value = null
        _analysisState.value = AnalysisUiState.Success(look.sampleAnalysis)
        _currentTab.value = MainTab.STYLIST
        addStylistMessage("I've loaded the '${look.title}'. Feel free to ask me for custom pairing advice or tap to explore its breakdown!")
    }

    fun onImageSelected(uri: Uri, context: Context) {
        _selectedImageUri.value = uri
        _selectedResId.value = null
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            _selectedBitmap.value = bitmap
            _analysisState.value = AnalysisUiState.Idle
            _currentTab.value = MainTab.STYLIST
            addStylistMessage("Image received! Tap 'Analyze Outfit' whenever you're ready for your full editorial breakdown.")
        } catch (e: Exception) {
            _analysisState.value = AnalysisUiState.Error("Could not load image: ${e.localizedMessage}")
        }
    }

    fun onBitmapCaptured(bitmap: Bitmap) {
        _selectedBitmap.value = bitmap
        _selectedImageUri.value = null
        _selectedResId.value = null
        _analysisState.value = AnalysisUiState.Idle
        _currentTab.value = MainTab.STYLIST
        addStylistMessage("Outfit photo captured! Tap 'Analyze Outfit' to begin.")
    }

    fun analyzeCurrentOutfit(context: Context) {
        viewModelScope.launch {
            _analysisState.value = AnalysisUiState.Analyzing
            try {
                var bitmap = _selectedBitmap.value
                if (bitmap == null && _selectedResId.value != null) {
                    bitmap = BitmapFactory.decodeResource(context.resources, _selectedResId.value!!)
                }

                val promptContext = "User preference: ${_userPreferences.value.archetype}, Palette: ${_userPreferences.value.primaryPalette}"
                val result = stylistService.analyzeOutfit(bitmap, promptContext).copy(
                    imageUri = _selectedImageUri.value?.toString(),
                    imageResId = _selectedResId.value
                )

                _analysisState.value = AnalysisUiState.Success(result)
                addStylistMessage("Analysis complete for '${result.title}'! Score: ${result.score}/100. Ask me anything below to customize footwear, layers, or occasions.")
            } catch (e: Exception) {
                _analysisState.value = AnalysisUiState.Error("Stylist analysis encountered an issue: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }

    fun askStylist(question: String) {
        if (question.isBlank()) return
        val currentOutfit = (_analysisState.value as? AnalysisUiState.Success)?.result

        val userMsg = StylistChatMessage(
            sender = MessageSender.USER,
            text = question.trim()
        )
        _chatMessages.value = _chatMessages.value + userMsg

        viewModelScope.launch {
            _isChatLoading.value = true
            try {
                val history = _chatMessages.value.takeLast(6).map {
                    (if (it.sender == MessageSender.USER) "User" else "Stylist") to it.text
                }
                val answer = stylistService.askStylistQuestion(currentOutfit, question, history)
                addStylistMessage(answer)
            } catch (e: Exception) {
                addStylistMessage("I'm having trouble connecting right now, but I recommend leaning into clean tailored silhouettes and balancing warm neutral accents.")
            } finally {
                _isChatLoading.value = false
            }
        }
    }

    private fun addStylistMessage(text: String) {
        val botMsg = StylistChatMessage(
            sender = MessageSender.STYLIST,
            text = text
        )
        _chatMessages.value = _chatMessages.value + botMsg
    }

    fun toggleSaveCurrentOutfit() {
        val current = (_analysisState.value as? AnalysisUiState.Success)?.result ?: return
        viewModelScope.launch {
            if (current.isSaved) {
                repository.deleteOutfitByTitle(current.title)
                _analysisState.value = AnalysisUiState.Success(current.copy(isSaved = false))
                _userMessage.value = "Removed outfit from Saved Lookbook"
            } else {
                val updated = current.copy(isSaved = true)
                repository.saveOutfit(updated)
                _analysisState.value = AnalysisUiState.Success(updated)
                _userMessage.value = "Saved outfit to Lookbook!"
            }
        }
    }

    fun saveOutfit(outfit: StyleAnalysisResult) {
        viewModelScope.launch {
            repository.saveOutfit(outfit)
            _userMessage.value = "Outfit added to your Lookbook"
        }
    }

    fun deleteOutfit(id: Long) {
        viewModelScope.launch {
            repository.deleteOutfit(id)
            if (_selectedDetailOutfit.value?.id == id) {
                _selectedDetailOutfit.value = null
            }
            _userMessage.value = "Outfit removed from Lookbook"
        }
    }

    fun openOutfitDetail(outfit: StyleAnalysisResult) {
        _selectedDetailOutfit.value = outfit
    }

    fun closeOutfitDetail() {
        _selectedDetailOutfit.value = null
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun updatePreferences(newPrefs: UserStylePreferences) {
        _userPreferences.value = newPrefs
        _userMessage.value = "Styling profile updated"
    }

    fun hasApiKey(): Boolean = BuildConfig.GEMINI_API_KEY.isNotBlank()
}
