package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.BuildConfig
import com.example.viewmodel.MainViewModel

@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.titleLarge)
        
        SettingsSection("API Configuration") {
            val keyStatus = if (BuildConfig.GEMINI_API_KEY.isNotEmpty() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY") {
                "Configured in AI Studio Secrets"
            } else {
                "Not configured. Please set GEMINI_API_KEY."
            }
            SettingsItem("Gemini API Key", keyStatus, onClick = null)
        }
        
        SettingsSection("Data Management") {
            SettingsItem("Clear All Saved Content", "Permanently delete all generated ideas and scripts", onClick = {
                viewModel.clearAllSavedContent()
            })
            SettingsItem("Clear Content Planner", "Permanently delete your content calendar", onClick = {
                viewModel.clearAllPlans()
            })
            SettingsItem("Clear AI Chat History", "Reset your conversation with Sarban AI", onClick = {
                viewModel.clearChatHistory()
            })
        }
        
        SettingsSection("About") {
            SettingsItem("About Sarban AI Studio", "Version 1.0.0", onClick = null)
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(title: String, subtitle: String, onClick: (() -> Unit)?) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
        .padding(16.dp)
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        if (subtitle.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.surface)
}
