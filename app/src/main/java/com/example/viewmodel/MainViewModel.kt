package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.BuildConfig
import com.example.api.Content
import com.example.api.GenerateContentRequest
import com.example.api.GenerationConfig
import com.example.api.Part
import com.example.api.RetrofitClient
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.data.ContentPlanEntity
import com.example.data.SavedContentEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(
        application.applicationContext,
        AppDatabase::class.java, "sarban-db"
    ).build()
    private val repository = AppRepository(db.appDao())

    val savedContents: StateFlow<List<SavedContentEntity>> = repository.savedContents.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val contentPlans: StateFlow<List<ContentPlanEntity>> = repository.contentPlans.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Common AI Generation State
    private val _aiState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val aiState: StateFlow<UiState<String>> = _aiState.asStateFlow()
    
    // Chat specific state
    private val _chatHistory = MutableStateFlow<List<Pair<String, String>>>(emptyList()) // User, AI
    val chatHistory: StateFlow<List<Pair<String, String>>> = _chatHistory.asStateFlow()

    fun resetAiState() {
        _aiState.value = UiState.Idle
    }

    private suspend fun callGemini(prompt: String, systemPrompt: String? = null): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            throw Exception("Gemini API key is not configured. Please add it to Settings / .env file.")
        }
        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            systemInstruction = systemPrompt?.let { Content(parts = listOf(Part(text = it))) }
        )
        val response = RetrofitClient.service.generateContent(apiKey, request)
        if (response.error != null) {
            throw Exception(response.error.message ?: "Unknown API error")
        }
        return response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No response from AI."
    }

    fun generateContent(prompt: String, systemPrompt: String? = null) {
        viewModelScope.launch {
            _aiState.value = UiState.Loading
            try {
                val result = withContext(Dispatchers.IO) { callGemini(prompt, systemPrompt) }
                _aiState.value = UiState.Success(result)
            } catch (e: Exception) {
                _aiState.value = UiState.Error(e.message ?: "Generation failed")
            }
        }
    }

    fun sendChatMessage(message: String) {
        val currentHistory = _chatHistory.value.toMutableList()
        currentHistory.add(Pair(message, "")) // Temporary empty response
        _chatHistory.value = currentHistory

        viewModelScope.launch {
            try {
                // Construct a conversation history prompt (simple approach for REST)
                val historyPrompt = currentHistory.dropLast(1).joinToString("\n") { "User: ${it.first}\nSarban AI: ${it.second}" }
                val fullPrompt = "$historyPrompt\nUser: $message\nSarban AI:"
                val systemPrompt = "You are Sarban AI, a helpful assistant for social media creators. Answer concisely."
                
                val response = withContext(Dispatchers.IO) { callGemini(fullPrompt, systemPrompt) }
                
                val updatedHistory = _chatHistory.value.toMutableList()
                updatedHistory[updatedHistory.size - 1] = Pair(message, response)
                _chatHistory.value = updatedHistory
            } catch (e: Exception) {
                 val updatedHistory = _chatHistory.value.toMutableList()
                 updatedHistory[updatedHistory.size - 1] = Pair(message, "Error: ${e.message}")
                 _chatHistory.value = updatedHistory
            }
        }
    }

    fun saveContent(type: String, title: String, content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveContent(SavedContentEntity(type = type, title = title, content = content))
        }
    }

    fun deleteSavedContent(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteContent(id)
        }
    }

    fun savePlan(dateMs: Long, platform: String, topic: String, title: String, status: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.savePlan(ContentPlanEntity(dateMs = dateMs, platform = platform, topic = topic, title = title, status = status))
        }
    }
    
    fun updatePlanStatus(plan: ContentPlanEntity, newStatus: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.savePlan(plan.copy(status = newStatus))
        }
    }

    fun deletePlan(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePlan(id)
        }
    }

    fun clearAllSavedContent() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAllSavedContent()
        }
    }

    fun clearAllPlans() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAllPlans()
        }
    }

    fun clearChatHistory() {
        _chatHistory.value = emptyList()
    }
}
