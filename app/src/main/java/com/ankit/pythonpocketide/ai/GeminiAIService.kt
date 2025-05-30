package com.ankit.pythonpocketide.ai

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Gemini AI Service for Python Pocket IDE
 * Provides context-aware AI assistance for code editing
 * 
 * Created by: Ankit (SDE 2 Mobile at Dhwani RIS)
 * For: Python Pocket IDE - Major Project
 */
class GeminiAIService private constructor(private val context: Context) {
    
    companion object {
        @Volatile
        private var INSTANCE: GeminiAIService? = null
        
        fun getInstance(context: Context): GeminiAIService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: GeminiAIService(context.applicationContext).also { INSTANCE = it }
            }
        }
        
        // Model configurations
        const val MODEL_GEMINI_PRO = "gemini-pro"
        const val MODEL_GEMINI_PRO_VISION = "gemini-pro-vision"
        const val MODEL_GEMINI_FLASH = "gemini-1.5-flash"
        
        private const val PREF_NAME = "ai_assistant_prefs"
        private const val KEY_API_KEY = "gemini_api_key"
        private const val KEY_SELECTED_MODEL = "selected_model"
        private const val KEY_CONTEXT_ENABLED = "context_enabled"
        private const val KEY_AUTO_COMPLETE = "auto_complete_enabled"
        private const val KEY_CODE_REVIEW = "code_review_enabled"
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // AI Assistant Configuration
    data class AIConfig(
        val model: String = MODEL_GEMINI_FLASH,
        val contextEnabled: Boolean = true,
        val autoCompleteEnabled: Boolean = true,
        val codeReviewEnabled: Boolean = true,
        val temperature: Float = 0.7f,
        val maxTokens: Int = 2048
    )
    
    // Code Context for AI assistance
    data class CodeContext(
        val currentFile: String = "",
        val currentLine: Int = 0,
        val selectedText: String = "",
        val surroundingCode: String = "",
        val projectType: String = "python",
        val imports: List<String> = emptyList(),
        val variables: List<String> = emptyList(),
        val functions: List<String> = emptyList()
    )
    
    // AI Response types
    sealed class AIResponse {
        data class Success(val content: String, val suggestions: List<String> = emptyList()) : AIResponse()
        data class Error(val message: String, val code: Int? = null) : AIResponse()
        object Loading : AIResponse()
    }
    
    // AI Request types
    sealed class AIRequest {
        data class CodeCompletion(val context: CodeContext, val partialCode: String) : AIRequest()
        data class CodeExplanation(val context: CodeContext, val code: String) : AIRequest()
        data class CodeReview(val context: CodeContext, val code: String) : AIRequest()
        data class BugFix(val context: CodeContext, val errorMessage: String) : AIRequest()
        data class CodeOptimization(val context: CodeContext, val code: String) : AIRequest()
        data class GenerateDocstring(val context: CodeContext, val function: String) : AIRequest()
        data class CustomQuery(val context: CodeContext, val query: String) : AIRequest()
    }
    
    // Configuration methods
    fun saveAPIKey(apiKey: String) {
        prefs.edit().putString(KEY_API_KEY, apiKey).apply()
    }
    
    fun getAPIKey(): String? = prefs.getString(KEY_API_KEY, null)
    
    fun saveConfig(config: AIConfig) {
        prefs.edit().apply {
            putString(KEY_SELECTED_MODEL, config.model)
            putBoolean(KEY_CONTEXT_ENABLED, config.contextEnabled)
            putBoolean(KEY_AUTO_COMPLETE, config.autoCompleteEnabled)
            putBoolean(KEY_CODE_REVIEW, config.codeReviewEnabled)
        }.apply()
    }
    
    fun getConfig(): AIConfig {
        return AIConfig(
            model = prefs.getString(KEY_SELECTED_MODEL, MODEL_GEMINI_FLASH) ?: MODEL_GEMINI_FLASH,
            contextEnabled = prefs.getBoolean(KEY_CONTEXT_ENABLED, true),
            autoCompleteEnabled = prefs.getBoolean(KEY_AUTO_COMPLETE, true),
            codeReviewEnabled = prefs.getBoolean(KEY_CODE_REVIEW, true)
        )
    }
    
    // Main AI request method
    suspend fun processAIRequest(request: AIRequest): AIResponse = withContext(Dispatchers.IO) {
        try {
            val apiKey = getAPIKey()
            if (apiKey.isNullOrBlank()) {
                return@withContext AIResponse.Error("API key not configured. Please set your Gemini API key in settings.")
            }
            
            val config = getConfig()
            val prompt = buildPrompt(request, config)
            
            val response = makeGeminiRequest(apiKey, config.model, prompt, config)
            parseGeminiResponse(response)
            
        } catch (e: Exception) {
            AIResponse.Error("AI request failed: ${e.message}")
        }
    }
    
    private fun buildPrompt(request: AIRequest, config: AIConfig): String {
        val systemPrompt = buildSystemPrompt()
        val contextPrompt = if (config.contextEnabled) buildContextPrompt(request) else ""
        val taskPrompt = buildTaskPrompt(request)
        
        return """
            $systemPrompt
            
            $contextPrompt
            
            $taskPrompt
            
            Please provide a helpful, accurate, and concise response focused on Python development for mobile/educational use.
        """.trimIndent()
    }
    
    private fun buildSystemPrompt(): String = """
        You are an expert Python programming assistant for the Python Pocket IDE mobile application.
        
        Context:
        - This is a mobile Python IDE for educational use
        - Target audience: MCA students and Python learners
        - Focus on Python 3.11.5 compatibility
        - Emphasize educational best practices
        - Consider mobile device constraints (memory, processing)
        - Support both beginners and advanced users
        
        Expertise areas:
        - Python programming (all levels)
        - Mobile app development
        - Educational programming concepts
        - Code optimization for mobile devices
        - Debugging and error resolution
        - Best practices and clean code
        
        Response style:
        - Clear and educational explanations
        - Include code examples when helpful
        - Suggest multiple approaches when applicable
        - Highlight potential issues or improvements
        - Use markdown formatting for code blocks
    """.trimIndent()
    
    private fun buildContextPrompt(request: AIRequest): String {
        val context = when (request) {
            is AIRequest.CodeCompletion -> request.context
            is AIRequest.CodeExplanation -> request.context
            is AIRequest.CodeReview -> request.context
            is AIRequest.BugFix -> request.context
            is AIRequest.CodeOptimization -> request.context
            is AIRequest.GenerateDocstring -> request.context
            is AIRequest.CustomQuery -> request.context
        }
        
        if (!getConfig().contextEnabled) return ""
        
        return """
            Current code context:
            - File: ${context.currentFile}
            - Line: ${context.currentLine}
            - Project type: ${context.projectType}
            - Available imports: ${context.imports.joinToString(", ")}
            - Variables in scope: ${context.variables.joinToString(", ")}
            - Functions in scope: ${context.functions.joinToString(", ")}
            
            Surrounding code:
            ```python
            ${context.surroundingCode}
            ```
            
            Selected/Focus text:
            ```python
            ${context.selectedText}
            ```
        """.trimIndent()
    }
    
    private fun buildTaskPrompt(request: AIRequest): String = when (request) {
        is AIRequest.CodeCompletion -> """
            Task: Code Completion
            
            The user is typing the following code and needs intelligent completion suggestions:
            ```python
            ${request.partialCode}
            ```
            
            Please provide:
            1. Most likely completion(s) for the current line
            2. Brief explanation of what the completion does
            3. Alternative suggestions if applicable
            
            Focus on Python 3.11.5 compatible suggestions that work well on mobile devices.
        """.trimIndent()
        
        is AIRequest.CodeExplanation -> """
            Task: Code Explanation
            
            Please explain the following Python code in educational terms:
            ```python
            ${request.code}
            ```
            
            Provide:
            1. Line-by-line explanation for complex parts
            2. Overall purpose and functionality
            3. Key concepts or patterns used
            4. Potential improvements or alternatives
            
            Tailor explanation for MCA students learning Python.
        """.trimIndent()
        
        is AIRequest.CodeReview -> """
            Task: Code Review
            
            Please review the following Python code for best practices, potential issues, and improvements:
            ```python
            ${request.code}
            ```
            
            Focus on:
            1. Code quality and readability
            2. Performance considerations for mobile
            3. Python best practices
            4. Potential bugs or edge cases
            5. Educational value and learning opportunities
            
            Provide specific suggestions with examples.
        """.trimIndent()
        
        is AIRequest.BugFix -> """
            Task: Bug Fix Assistance
            
            The user encountered this error:
            ```
            ${request.errorMessage}
            ```
            
            In the context of their code. Please help by:
            1. Explaining what likely caused this error
            2. Providing specific fix suggestions
            3. Showing corrected code examples
            4. Suggesting ways to prevent similar errors
            
            Consider mobile Python environment limitations.
        """.trimIndent()
        
        is AIRequest.CodeOptimization -> """
            Task: Code Optimization
            
            Please optimize the following Python code for better performance, especially considering mobile device constraints:
            ```python
            ${request.code}
            ```
            
            Focus on:
            1. Memory efficiency
            2. Processing speed
            3. Battery usage considerations
            4. Maintainability
            5. Educational clarity
            
            Provide optimized version with explanations.
        """.trimIndent()
        
        is AIRequest.GenerateDocstring -> """
            Task: Generate Documentation
            
            Please generate comprehensive docstring for this Python function:
            ```python
            ${request.function}
            ```
            
            Include:
            1. Clear description of purpose
            2. Parameter descriptions with types
            3. Return value description
            4. Usage examples
            5. Any important notes or warnings
            
            Follow Google/NumPy docstring style.
        """.trimIndent()
        
        is AIRequest.CustomQuery -> """
            Task: Custom Query
            
            User question: ${request.query}
            
            Please provide a helpful response considering:
            1. The current code context
            2. Python 3.11.5 compatibility
            3. Mobile development constraints
            4. Educational value for MCA students
            
            Be specific and provide code examples when relevant.
        """.trimIndent()
    }
    
    private suspend fun makeGeminiRequest(
        apiKey: String,
        model: String,
        prompt: String,
        config: AIConfig
    ): Response = withContext(Dispatchers.IO) {
        
        val requestBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", config.temperature)
                put("maxOutputTokens", config.maxTokens)
                put("topP", 0.95)
                put("topK", 40)
            })
            put("safetySettings", JSONArray().apply {
                // Add safety settings for educational content
                val categories = listOf(
                    "HARM_CATEGORY_HARASSMENT",
                    "HARM_CATEGORY_HATE_SPEECH", 
                    "HARM_CATEGORY_SEXUALLY_EXPLICIT",
                    "HARM_CATEGORY_DANGEROUS_CONTENT"
                )
                categories.forEach { category ->
                    put(JSONObject().apply {
                        put("category", category)
                        put("threshold", "BLOCK_MEDIUM_AND_ABOVE")
                    })
                }
            })
        }
        
        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey")
            .post(RequestBody.create("application/json".toMediaType(), requestBody.toString()))
            .addHeader("Content-Type", "application/json")
            .build()
        
        httpClient.newCall(request).execute()
    }
    
    private fun parseGeminiResponse(response: Response): AIResponse {
        if (!response.isSuccessful) {
            return AIResponse.Error("API request failed: ${response.code} ${response.message}")
        }
        
        val responseBody = response.body?.string() ?: return AIResponse.Error("Empty response")
        
        try {
            val json = JSONObject(responseBody)
            
            if (json.has("error")) {
                val error = json.getJSONObject("error")
                return AIResponse.Error("API Error: ${error.getString("message")}")
            }
            
            val candidates = json.getJSONArray("candidates")
            if (candidates.length() == 0) {
                return AIResponse.Error("No response generated")
            }
            
            val candidate = candidates.getJSONObject(0)
            val content = candidate.getJSONObject("content")
            val parts = content.getJSONArray("parts")
            
            if (parts.length() == 0) {
                return AIResponse.Error("Empty content in response")
            }
            
            val text = parts.getJSONObject(0).getString("text")
            
            // Extract suggestions if present in the response
            val suggestions = extractSuggestions(text)
            
            return AIResponse.Success(text, suggestions)
            
        } catch (e: Exception) {
            return AIResponse.Error("Failed to parse response: ${e.message}")
        }
    }
    
    private fun extractSuggestions(text: String): List<String> {
        // Extract code suggestions from the AI response
        val suggestions = mutableListOf<String>()
        
        // Look for numbered suggestions or bullet points
        val suggestionPatterns = listOf(
            "\\d+\\. (.+?)(?=\\d+\\.|$)".toRegex(RegexOption.MULTILINE),
            "- (.+?)(?=- |$)".toRegex(RegexOption.MULTILINE),
            "\\* (.+?)(?=\\* |$)".toRegex(RegexOption.MULTILINE)
        )
        
        suggestionPatterns.forEach { pattern ->
            pattern.findAll(text).forEach { match ->
                val suggestion = match.groupValues[1].trim()
                if (suggestion.isNotBlank() && suggestion.length < 200) {
                    suggestions.add(suggestion)
                }
            }
        }
        
        return suggestions.take(5) // Limit to 5 suggestions
    }
    
    // Quick completion for real-time suggestions
    suspend fun getQuickCompletion(
        context: CodeContext,
        partialCode: String,
        maxSuggestions: Int = 3
    ): List<String> = withContext(Dispatchers.IO) {
        try {
            if (!getConfig().autoCompleteEnabled) return@withContext emptyList()
            
            val response = processAIRequest(AIRequest.CodeCompletion(context, partialCode))
            when (response) {
                is AIResponse.Success -> response.suggestions.take(maxSuggestions)
                else -> emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    // Clean up resources
    fun cleanup() {
        scope.cancel()
    }
} 