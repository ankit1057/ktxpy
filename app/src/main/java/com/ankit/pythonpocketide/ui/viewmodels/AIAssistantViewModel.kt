package com.ankit.pythonpocketide.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ankit.pythonpocketide.ai.GeminiAIService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for AI Assistant functionality
 * Manages AI service interactions and UI state
 * 
 * Created by: Ankit (SDE 2 Mobile at Dhwani RIS)
 * For: Python Pocket IDE - Major Project
 */

data class ChatMessage(
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val suggestions: List<String> = emptyList(),
    val isError: Boolean = false
)

class AIAssistantViewModel(private val context: Context) : ViewModel() {
    
    private var aiService: GeminiAIService? = null
    
    // UI State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _currentModel = MutableStateFlow(GeminiAIService.MODEL_GEMINI_FLASH)
    val currentModel: StateFlow<String> = _currentModel.asStateFlow()
    
    private val _aiConfig = MutableStateFlow(GeminiAIService.AIConfig())
    val aiConfig: StateFlow<GeminiAIService.AIConfig> = _aiConfig.asStateFlow()
    
    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()
    
    // Code context
    private val _currentCodeContext = MutableStateFlow(GeminiAIService.CodeContext())
    val currentCodeContext: StateFlow<GeminiAIService.CodeContext> = _currentCodeContext.asStateFlow()
    
    init {
        aiService = GeminiAIService.getInstance(context)
    }
    
    fun initializeAIService(context: Context) {
        if (aiService == null) {
            aiService = GeminiAIService.getInstance(context)
            loadConfig()
            addWelcomeMessage()
        }
    }
    
    private fun loadConfig() {
        aiService?.let { service ->
            val config = service.getConfig()
            _aiConfig.value = config
            _currentModel.value = config.model
        }
    }
    
    private fun addWelcomeMessage() {
        val welcomeMessage = ChatMessage(
            content = """
                👋 **Welcome to AI Assistant!**
                
                I'm here to help you with your Python code. I can:
                
                • **Explain code** - Help you understand complex logic
                • **Fix bugs** - Debug and suggest solutions
                • **Optimize code** - Improve performance and readability  
                • **Generate docs** - Create comprehensive documentation
                • **Review code** - Best practices and suggestions
                • **Answer questions** - General Python and programming help
                
                💡 **Quick Actions**: Select any code and use the buttons above for instant help!
                
                🤖 **Current Model**: ${_currentModel.value}
                
                Just ask me anything about your Python code!
            """.trimIndent(),
            isUser = false
        )
        
        _chatMessages.value = listOf(welcomeMessage)
    }
    
    fun updateCodeContext(
        currentCode: String,
        selectedText: String,
        currentFile: String,
        currentLine: Int
    ) {
        val context = _currentCodeContext.value.copy(
            currentFile = currentFile,
            currentLine = currentLine,
            selectedText = selectedText,
            surroundingCode = extractSurroundingCode(currentCode, currentLine),
            imports = extractImports(currentCode),
            variables = extractVariables(currentCode),
            functions = extractFunctions(currentCode)
        )
        _currentCodeContext.value = context
    }
    
    private fun extractSurroundingCode(code: String, currentLine: Int): String {
        val lines = code.split("\n")
        val start = maxOf(0, currentLine - 10)
        val end = minOf(lines.size, currentLine + 10)
        return lines.subList(start, end).joinToString("\n")
    }
    
    private fun extractImports(code: String): List<String> {
        val importRegex = Regex("""^(?:import|from)\s+([a-zA-Z_][a-zA-Z0-9_.]*(?:\s+as\s+[a-zA-Z_][a-zA-Z0-9_]*)?)\s*(?:import.*)?$""", RegexOption.MULTILINE)
        return importRegex.findAll(code).map { it.groupValues[1] }.toList()
    }
    
    private fun extractVariables(code: String): List<String> {
        val varRegex = Regex("""^([a-zA-Z_][a-zA-Z0-9_]*)\s*=.*$""", RegexOption.MULTILINE)
        return varRegex.findAll(code).map { it.groupValues[1] }.toList().distinct()
    }
    
    private fun extractFunctions(code: String): List<String> {
        val funcRegex = Regex("""^def\s+([a-zA-Z_][a-zA-Z0-9_]*)\s*\(.*\):.*$""", RegexOption.MULTILINE)
        return funcRegex.findAll(code).map { it.groupValues[1] }.toList()
    }
    
    fun sendMessage(
        message: String,
        requestType: AIRequestType = AIRequestType.CUSTOM_QUERY,
        codeContext: String = "",
        selectedCode: String = ""
    ) {
        // Add user message
        val userMessage = ChatMessage(
            content = message,
            isUser = true,
            timestamp = System.currentTimeMillis()
        )
        _chatMessages.value = _chatMessages.value + userMessage
        
        // Start loading
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                val context = _currentCodeContext.value.copy(
                    surroundingCode = codeContext,
                    selectedText = selectedCode
                )
                
                val aiRequest = when (requestType) {
                    AIRequestType.CODE_COMPLETION -> GeminiAIService.AIRequest.CodeCompletion(context, selectedCode)
                    AIRequestType.CODE_EXPLANATION -> GeminiAIService.AIRequest.CodeExplanation(context, selectedCode)
                    AIRequestType.CODE_REVIEW -> GeminiAIService.AIRequest.CodeReview(context, selectedCode)
                    AIRequestType.BUG_FIX -> GeminiAIService.AIRequest.BugFix(context, message)
                    AIRequestType.CODE_OPTIMIZATION -> GeminiAIService.AIRequest.CodeOptimization(context, selectedCode)
                    AIRequestType.GENERATE_DOCSTRING -> GeminiAIService.AIRequest.GenerateDocstring(context, selectedCode)
                    AIRequestType.CUSTOM_QUERY -> GeminiAIService.AIRequest.CustomQuery(context, message)
                }
                
                val response = aiService?.processAIRequest(aiRequest)
                
                when (response) {
                    is GeminiAIService.AIResponse.Success -> {
                        val aiMessage = ChatMessage(
                            content = response.content,
                            isUser = false,
                            timestamp = System.currentTimeMillis(),
                            suggestions = response.suggestions
                        )
                        _chatMessages.value = _chatMessages.value + aiMessage
                    }
                    is GeminiAIService.AIResponse.Error -> {
                        val errorMessage = ChatMessage(
                            content = "❌ **Error**: ${response.message}\n\nPlease check your API key in settings or try again.",
                            isUser = false,
                            timestamp = System.currentTimeMillis(),
                            isError = true
                        )
                        _chatMessages.value = _chatMessages.value + errorMessage
                    }
                    else -> {
                        val errorMessage = ChatMessage(
                            content = "❌ **Unexpected error occurred**. Please try again.",
                            isUser = false,
                            timestamp = System.currentTimeMillis(),
                            isError = true
                        )
                        _chatMessages.value = _chatMessages.value + errorMessage
                    }
                }
                
            } catch (e: Exception) {
                // Add error message
                val errorMessage = ChatMessage(
                    content = "❌ **Network error**: ${e.message}\n\nPlease check your internet connection and try again.",
                    isUser = false,
                    timestamp = System.currentTimeMillis(),
                    isError = true
                )
                _chatMessages.value = _chatMessages.value + errorMessage
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun performQuickAction(action: String, selectedText: String, fullCode: String) {
        viewModelScope.launch {
            if (selectedText.isBlank()) {
                val errorMessage = ChatMessage(
                    content = "⚠️ **No code selected**. Please select some code first to use quick actions.",
                    isUser = false
                )
                _chatMessages.value = _chatMessages.value + errorMessage
                return@launch
            }
            
            // Add user action message
            val actionMessage = ChatMessage(
                content = "🔧 **${action.replaceFirstChar { it.uppercase() }}**: `${selectedText.take(50)}${if (selectedText.length > 50) "..." else ""}`",
                isUser = true
            )
            _chatMessages.value = _chatMessages.value + actionMessage
            
            _isLoading.value = true
            
            try {
                val aiRequest = when (action.lowercase()) {
                    "explain" -> GeminiAIService.AIRequest.CodeExplanation(
                        context = _currentCodeContext.value,
                        code = selectedText
                    )
                    "fix" -> GeminiAIService.AIRequest.BugFix(
                        context = _currentCodeContext.value,
                        errorMessage = "Please analyze this code for potential issues: $selectedText"
                    )
                    "optimize" -> GeminiAIService.AIRequest.CodeOptimization(
                        context = _currentCodeContext.value,
                        code = selectedText
                    )
                    "review" -> GeminiAIService.AIRequest.CodeReview(
                        context = _currentCodeContext.value,
                        code = selectedText
                    )
                    "document" -> GeminiAIService.AIRequest.GenerateDocstring(
                        context = _currentCodeContext.value,
                        function = selectedText
                    )
                    else -> GeminiAIService.AIRequest.CustomQuery(
                        context = _currentCodeContext.value,
                        query = "Please help with this code: $selectedText"
                    )
                }
                
                val response = aiService?.processAIRequest(aiRequest)
                
                when (response) {
                    is GeminiAIService.AIResponse.Success -> {
                        val aiMessage = ChatMessage(
                            content = response.content,
                            isUser = false,
                            suggestions = response.suggestions
                        )
                        _chatMessages.value = _chatMessages.value + aiMessage
                    }
                    is GeminiAIService.AIResponse.Error -> {
                        val errorMessage = ChatMessage(
                            content = "❌ **Error**: ${response.message}",
                            isUser = false
                        )
                        _chatMessages.value = _chatMessages.value + errorMessage
                    }
                    else -> {
                        val errorMessage = ChatMessage(
                            content = "❌ **Unexpected error occurred**. Please try again.",
                            isUser = false
                        )
                        _chatMessages.value = _chatMessages.value + errorMessage
                    }
                }
            } catch (e: Exception) {
                val errorMessage = ChatMessage(
                    content = "❌ **Error**: ${e.message}",
                    isUser = false
                )
                _chatMessages.value = _chatMessages.value + errorMessage
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateConfig(config: GeminiAIService.AIConfig) {
        aiService?.saveConfig(config)
        _aiConfig.value = config
        _currentModel.value = config.model
        
        // Add confirmation message
        val confirmMessage = ChatMessage(
            content = "✅ **Settings Updated**\n\n• Model: ${config.model}\n• Context Awareness: ${if (config.contextEnabled) "Enabled" else "Disabled"}\n• Auto-completion: ${if (config.autoCompleteEnabled) "Enabled" else "Disabled"}\n• Code Review: ${if (config.codeReviewEnabled) "Enabled" else "Disabled"}",
            isUser = false
        )
        _chatMessages.value = _chatMessages.value + confirmMessage
    }
    
    fun showSettings() {
        _showSettingsDialog.value = true
    }
    
    fun hideSettings() {
        _showSettingsDialog.value = false
    }
    
    fun clearChat() {
        _chatMessages.value = emptyList()
        addWelcomeMessage()
    }
    
    // Quick completion for real-time suggestions
    suspend fun getQuickCompletions(partialCode: String): List<String> {
        return try {
            aiService?.getQuickCompletion(
                context = _currentCodeContext.value,
                partialCode = partialCode,
                maxSuggestions = 3
            ) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun setSelectedModel(model: String) {
        _currentModel.value = model
        val config = aiService?.getConfig()?.copy(model = model)
        if (config != null) {
            aiService?.saveConfig(config)
        }
    }
    
    fun updateCodeContext(context: GeminiAIService.CodeContext) {
        _currentCodeContext.value = context
    }
    
    fun clearMessages() {
        _chatMessages.value = emptyList()
    }
    
    fun saveAPIKey(apiKey: String) {
        aiService?.saveAPIKey(apiKey)
    }
    
    fun getAPIKey(): String? = aiService?.getAPIKey()
    
    override fun onCleared() {
        super.onCleared()
        aiService?.cleanup()
    }
} 