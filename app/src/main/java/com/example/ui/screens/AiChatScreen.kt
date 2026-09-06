package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.viewmodel.MainViewModel

@Composable
fun AiChatScreen(viewModel: MainViewModel) {
    val chatHistory by viewModel.chatHistory.collectAsState()
    var currentMessage by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("AI Creator Chat", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
        
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (chatHistory.isEmpty()) {
                item {
                    Text(
                        text = "Hi! I'm Sarban AI. How can I help you create today?",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            items(chatHistory) { (userMsg, aiResponse) ->
                ChatBubble(message = userMsg, isUser = true)
                if (aiResponse.isNotEmpty()) {
                    ChatBubble(message = aiResponse, isUser = false)
                } else {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp).padding(start = 16.dp))
                }
            }
        }
        
        // Suggested Prompts
        val suggestedPrompts = listOf("Give me 3 Reel ideas", "My views dropped, what should I do?", "Write a hook for a tech video")
        if (chatHistory.isEmpty()) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                suggestedPrompts.forEach { prompt ->
                    SuggestionChip(
                        onClick = { viewModel.sendChatMessage(prompt) },
                        label = { Text(prompt, maxLines = 1) }
                    )
                }
            }
        }

        // Input Box
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = currentMessage,
                onValueChange = { currentMessage = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask Sarban AI...") },
                shape = RoundedCornerShape(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { 
                    if (currentMessage.isNotBlank()) {
                        viewModel.sendChatMessage(currentMessage)
                        currentMessage = ""
                    }
                },
                modifier = Modifier.background(MaterialTheme.colorScheme.primary, RoundedCornerShape(50))
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
            }
        }
    }
}

@Composable
fun ChatBubble(message: String, isUser: Boolean) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(12.dp),
                color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
