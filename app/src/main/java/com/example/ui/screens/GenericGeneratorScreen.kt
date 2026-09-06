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
fun GenericGeneratorScreen(
    title: String,
    viewModel: MainViewModel,
    inputLabel: String = "Topic/Keyword",
    onGenerate: (String, String) -> Unit
) {
    val aiState by viewModel.aiState.collectAsState()
    
    var input by remember { mutableStateOf("") }
    var platform by remember { mutableStateOf("YouTube") }
    var expandedPlatform by remember { mutableStateOf(false) }
    val platforms = listOf("YouTube", "Instagram", "TikTok", "Facebook", "X", "LinkedIn")

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
        Text(title, style = MaterialTheme.typography.titleLarge)
        
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text(inputLabel) },
            modifier = Modifier.fillMaxWidth()
        )
        
        ExposedDropdownMenuBox(
            expanded = expandedPlatform,
            onExpandedChange = { expandedPlatform = !expandedPlatform },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = platform,
                onValueChange = {},
                readOnly = true,
                label = { Text("Platform") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPlatform) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth()
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
        
        GradientButton(
            text = "Generate",
            enabled = input.isNotBlank() && aiState !is UiState.Loading,
            onClick = { onGenerate(input, platform) }
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
                            Button(onClick = { viewModel.saveContent(title, input, state.data) }) {
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
