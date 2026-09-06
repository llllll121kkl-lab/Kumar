package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Troubleshoot
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object AiSearch : Screen("ai_search", "AI Search", Icons.Default.Search)
    object ContentGenerator : Screen("content_generator", "Content Generator", Icons.Default.Create)
    object ViralIdeas : Screen("viral_ideas", "Viral Ideas", Icons.Default.Lightbulb)
    object ScriptGenerator : Screen("script_generator", "Script Generator", Icons.Default.Movie)
    object TitleGenerator : Screen("title_generator", "Title Generator", Icons.Default.Title)
    object HashtagGenerator : Screen("hashtag_generator", "Hashtag Generator", Icons.Default.Tag)
    object CaptionGenerator : Screen("caption_generator", "Caption Generator", Icons.Default.Create)
    object ContentAnalyzer : Screen("content_analyzer", "Content Analyzer", Icons.Default.Troubleshoot)
    object ContentPlanner : Screen("content_planner", "Content Planner", Icons.Default.CalendarMonth)
    object Analytics : Screen("analytics", "Analytics", Icons.Default.Analytics)
    object AiChat : Screen("ai_chat", "AI Creator Chat", Icons.AutoMirrored.Filled.Chat)
    object SavedContent : Screen("saved_content", "Saved Content", Icons.Default.Bookmark)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

val NAV_ITEMS = listOf(
    Screen.Home,
    Screen.AiSearch,
    Screen.ContentGenerator,
    Screen.ViralIdeas,
    Screen.ScriptGenerator,
    Screen.TitleGenerator,
    Screen.HashtagGenerator,
    Screen.CaptionGenerator,
    Screen.ContentAnalyzer,
    Screen.ContentPlanner,
    Screen.Analytics,
    Screen.AiChat,
    Screen.SavedContent,
    Screen.Settings
)
