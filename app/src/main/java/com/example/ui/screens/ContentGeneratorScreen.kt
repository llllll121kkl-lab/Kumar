package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.components.GradientButton
import com.example.ui.components.LoadingState
import com.example.ui.components.ErrorState
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentGeneratorScreen(viewModel: MainViewModel) {
    val aiState by viewModel.aiState.collectAsState()
    
    var topic by remember { mutableStateOf("") }
    var platform by remember { mutableStateOf("YouTube") }
    var contentType by remember { mutableStateOf("Long video") }
    var tone by remember { mutableStateOf("Professional") }
    var expandedPlatform by remember { mutableStateOf(false) }
    var expandedType by remember { mutableStateOf(false) }
    
    val platforms = listOf("YouTube", "Instagram", "TikTok", "Facebook", "X", "LinkedIn")
    val contentTypes = listOf("Short video", "Reel", "YouTube Short", "Long video", "Carousel", "Post")

    DisposableEffect(Unit) {
        onDispose { viewModel.resetAiState() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Content Generator", style = MaterialTheme.typography.titleLarge)
        
        OutlinedTextField(
            value = topic,
            onValueChange = { topic = it },
            label = { Text("Topic or Keyword") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ExposedDropdownMenuBox(
                expanded = expandedPlatform,
                onExpandedChange = { expandedPlatform = !expandedPlatform },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = platform,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Platform") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPlatform) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                )
                ExposedDropdownMenu(
                    expanded = expandedPlatform,
                    onDismissRequest = { expandedPlatform = false }
                ) {
                    platforms.forEach { p ->
                        DropdownMenuItem(
                            text = { Text(p) },
                            onClick = { platform = p; expandedPlatform = false }
                        )
                    }
                }
            }
            
            ExposedDropdownMenuBox(
                expanded = expandedType,
                onExpandedChange = { expandedType = !expandedType },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = contentType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Format") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                )
                ExposedDropdownMenu(
                    expanded = expandedType,
                    onDismissRequest = { expandedType = false }
                ) {
                    contentTypes.forEach { ct ->
                        DropdownMenuItem(
                            text = { Text(ct) },
                            onClick = { contentType = ct; expandedType = false }
                        )
                    }
                }
            }
        }
        
        GradientButton(
            text = "Generate Content",
            enabled = topic.isNotBlank() && aiState !is UiState.Loading,
            onClick = {
                val prompt = "Create content for $platform. Format: $contentType. Topic: $topic. Tone: $tone. Include Hook, Content concept, Outline, Main content, CTA, Caption, Title ideas, Hashtags, Keywords."
                val systemPrompt = "You are a professional social media content creator and strategist."
                viewModel.generateContent(prompt, systemPrompt)
            }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        when (val state = aiState) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(state.message) { viewModel.resetAiState() }
            is UiState.Success -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(state.data, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { viewModel.saveContent("Generated Content", topic, state.data) }) {
                                Text("Save")
                            }
                        }
                    }
                }
            }
            else -> {}
        }
    }
}
