package com.ankit.pythonpocketide.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ankit.pythonpocketide.ai.GeminiAIService
import com.ankit.pythonpocketide.ui.viewmodels.AIAssistantViewModel
import com.ankit.pythonpocketide.ui.viewmodels.ChatMessage
import com.ankit.pythonpocketide.ui.viewmodels.AIRequestType
import kotlinx.coroutines.launch

/**
 * AI Assistant Floating Window Component
 * Provides context-aware AI assistance for code editing
 * 
 * Features:
 * - Sliding panel interface
 * - Quick action buttons for common tasks
 * - Chat interface for natural conversation
 * - Settings for API configuration
 * - Code insertion capabilities
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAssistantFloat(
    isVisible: Boolean,
    onVisibilityChange: (Boolean) -> Unit,
    currentCode: String = "",
    selectedText: String = "",
    onCodeInsert: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    viewModel: AIAssistantViewModel = remember { AIAssistantViewModel(context) }
) {
    val scope = rememberCoroutineScope()
    
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showSettings by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf("") }
    
    // Update code context when current code changes
    LaunchedEffect(currentCode, selectedText) {
        val codeContext = GeminiAIService.CodeContext(
            currentFile = "main.py",
            selectedText = selectedText,
            surroundingCode = currentCode,
            projectType = "python"
        )
        viewModel.updateCodeContext(codeContext)
    }
    
    if (isVisible) {
        Popup(
            alignment = Alignment.CenterEnd,
            onDismissRequest = { onVisibilityChange(false) }
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300)),
                exit = slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            ) {
                Card(
                    modifier = modifier
                        .width(360.dp)
                        .height(600.dp)
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Header
                        AIAssistantHeader(
                            onClose = { onVisibilityChange(false) },
                            onSettings = { showSettings = true }
                        )
                        
                        Divider()
                        
                        // Quick Actions
                        QuickActions(
                            selectedText = selectedText,
                            onAction = { action ->
                                val requestType = when (action) {
                                    "explain" -> AIRequestType.CODE_EXPLANATION
                                    "fix" -> AIRequestType.BUG_FIX
                                    "optimize" -> AIRequestType.CODE_OPTIMIZATION
                                    "review" -> AIRequestType.CODE_REVIEW
                                    "document" -> AIRequestType.GENERATE_DOCSTRING
                                    else -> AIRequestType.CUSTOM_QUERY
                                }
                                
                                val message = when (action) {
                                    "explain" -> "Please explain this code"
                                    "fix" -> "Please help fix any issues in this code"
                                    "optimize" -> "Please optimize this code"
                                    "review" -> "Please review this code"
                                    "document" -> "Please generate documentation for this code"
                                    else -> action
                                }
                                
                                viewModel.sendMessage(
                                    message = message,
                                    requestType = requestType,
                                    codeContext = currentCode,
                                    selectedCode = selectedText
                                )
                            }
                        )
                        
                        Divider()
                        
                        // Chat Interface
                        AIChat(
                            messages = chatMessages,
                            isLoading = isLoading,
                            onSendMessage = { message ->
                                viewModel.sendMessage(
                                    message = message,
                                    requestType = AIRequestType.CUSTOM_QUERY,
                                    codeContext = currentCode,
                                    selectedCode = selectedText
                                )
                            },
                            onCodeInsert = onCodeInsert,
                            modifier = Modifier.weight(1f)
                        )
                        
                        Divider()
                        
                        // Input Field
                        ChatInput(
                            value = inputText,
                            onValueChange = { inputText = it },
                            onSend = { message ->
                                if (message.isNotBlank()) {
                                    viewModel.sendMessage(
                                        message = message,
                                        requestType = AIRequestType.CUSTOM_QUERY,
                                        codeContext = currentCode,
                                        selectedCode = selectedText
                                    )
                                    inputText = ""
                                }
                            },
                            enabled = !isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
    
    // Settings Dialog
    if (showSettings) {
        AISettingsDialog(
            onDismiss = { showSettings = false },
            viewModel = viewModel
        )
    }
}

@Composable
private fun AIAssistantHeader(
    onClose: () -> Unit,
    onSettings: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Star, // Using Star icon instead of Android
                contentDescription = "AI Assistant",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "AI Assistant",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        Row {
            IconButton(onClick = onSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun QuickActions(
    selectedText: String,
    onAction: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                QuickActionChip(
                    text = "Explain",
                    icon = Icons.Default.Info,
                    enabled = selectedText.isNotBlank(),
                    onClick = { onAction("explain") }
                )
            }
            item {
                QuickActionChip(
                    text = "Fix",
                    icon = Icons.Default.Build,
                    enabled = selectedText.isNotBlank(),
                    onClick = { onAction("fix") }
                )
            }
            item {
                QuickActionChip(
                    text = "Optimize",
                    icon = Icons.Default.Star, // Using Star instead of Bolt
                    enabled = selectedText.isNotBlank(),
                    onClick = { onAction("optimize") }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                QuickActionChip(
                    text = "Review",
                    icon = Icons.Default.CheckCircle,
                    enabled = selectedText.isNotBlank(),
                    onClick = { onAction("review") }
                )
            }
            item {
                QuickActionChip(
                    text = "Document",
                    icon = Icons.Default.Create, // Using Create instead of Article
                    enabled = selectedText.isNotBlank(),
                    onClick = { onAction("document") }
                )
            }
        }
    }
}

@Composable
fun QuickActionChip(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AssistChip(
        onClick = onClick,
        enabled = enabled,
        label = { 
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(16.dp)
            )
        },
        modifier = modifier
    )
}

@Composable
fun AIChat(
    messages: List<com.ankit.pythonpocketide.ui.viewmodels.ChatMessage>,
    isLoading: Boolean,
    onSendMessage: (String) -> Unit,
    onCodeInsert: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    
    Column(modifier = modifier) {
        // Messages
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message ->
                ChatMessageItem(
                    message = message,
                    onCodeInsert = onCodeInsert
                )
            }
            
            if (isLoading) {
                item {
                    LoadingMessage()
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Ask AI anything about your code...") },
                modifier = Modifier.weight(1f),
                enabled = !isLoading,
                maxLines = 3
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onSendMessage(inputText)
                        inputText = ""
                    }
                },
                enabled = !isLoading && inputText.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send"
                )
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: com.ankit.pythonpocketide.ui.viewmodels.ChatMessage,
    onCodeInsert: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (message.isUser) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (message.isUser) Icons.Default.Person else Icons.Default.Star,
                    contentDescription = if (message.isUser) "User" else "AI",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (message.isUser) "You" else "AI Assistant",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            SelectionContainer {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // Code suggestions
            if (message.suggestions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    message.suggestions.forEach { suggestion ->
                        SuggestionChip(
                            onClick = { onCodeInsert(suggestion) },
                            label = {
                                Text(
                                    text = suggestion,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingMessage(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "AI is thinking...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: (String) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(16.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("Ask AI anything about your code...") },
            modifier = Modifier.weight(1f),
            enabled = enabled,
            maxLines = 3,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = { 
                    if (value.isNotBlank()) {
                        onSend(value)
                    }
                }
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = {
                if (value.isNotBlank()) {
                    onSend(value)
                }
            },
            enabled = enabled && value.isNotBlank()
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AISettingsDialog(
    onDismiss: () -> Unit,
    viewModel: AIAssistantViewModel
) {
    var apiKey by remember { mutableStateOf(viewModel.getAPIKey() ?: "") }
    var selectedModel by remember { mutableStateOf(viewModel.currentModel.collectAsState().value) }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "AI Assistant Settings",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // API Key Input
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text("Gemini API Key") },
                    placeholder = { Text("Enter your Gemini API key") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Model Selection
                Text(
                    text = "AI Model",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                val models = listOf(
                    GeminiAIService.MODEL_GEMINI_FLASH to "Gemini Flash (Fast)",
                    GeminiAIService.MODEL_GEMINI_PRO to "Gemini Pro (Quality)",
                    GeminiAIService.MODEL_GEMINI_PRO_VISION to "Gemini Pro Vision"
                )
                
                models.forEach { (model, displayName) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedModel = model }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedModel == model,
                            onClick = { selectedModel = model }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = displayName,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (apiKey.isNotBlank()) {
                                viewModel.saveAPIKey(apiKey)
                            }
                            viewModel.setSelectedModel(selectedModel)
                            onDismiss()
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
} 