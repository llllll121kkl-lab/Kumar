package com.example.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.viewmodel.MainViewModel
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ContentGeneratorScreen
import com.example.ui.screens.GenericGeneratorScreen
import com.example.ui.screens.SavedContentScreen
import com.example.ui.screens.ContentPlannerScreen
import com.example.ui.screens.AiChatScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.SettingsScreen

@Composable
fun AppNavHost(navController: NavHostController, viewModel: MainViewModel, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) { HomeScreen(navController, viewModel) }
        composable(Screen.AiSearch.route) { 
            GenericGeneratorScreen("AI Search", viewModel, "Search Query") { input, platform ->
                viewModel.generateContent("Analyze the topic '$input' for $platform. Provide search intent, content opportunities, audience analysis, content angles, suggested titles, and keywords.", "You are an AI Search analyst for social media creators.")
            }
        }
        composable(Screen.ContentGenerator.route) { ContentGeneratorScreen(viewModel) }
        composable(Screen.ViralIdeas.route) { 
            GenericGeneratorScreen("Viral Ideas", viewModel, "Niche or Topic") { input, platform ->
                viewModel.generateContent("Generate high-potential content ideas for '$input' on $platform. Include idea title, hook, why it works, suggested format, duration, CTA, titles, and hashtags.", "You are an expert at creating viral social media concepts.")
            }
        }
        composable(Screen.ScriptGenerator.route) { 
            GenericGeneratorScreen("Script Generator", viewModel, "Topic") { input, platform ->
                viewModel.generateContent("Create a professional script for '$input' on $platform. Include opening hook, scene-by-scene structure, dialogue, narration, visual direction, ending, and CTA.", "You are an expert scriptwriter.")
            }
        }
        composable(Screen.TitleGenerator.route) { 
            GenericGeneratorScreen("Title Generator", viewModel, "Video Topic") { input, platform ->
                viewModel.generateContent("Generate 10 high-retention, non-clickbait titles for '$input' on $platform. Categorize by: Curiosity, Emotional, Short, Search-friendly.", "You are an expert copywriter for social media.")
            }
        }
        composable(Screen.HashtagGenerator.route) { 
            GenericGeneratorScreen("Hashtag Generator", viewModel, "Topic") { input, platform ->
                viewModel.generateContent("Generate relevant hashtags for '$input' on $platform. Organize them into: Broad, Niche, Topic-specific, Long-tail.", "You are a social media growth expert.")
            }
        }
        composable(Screen.CaptionGenerator.route) { 
            GenericGeneratorScreen("Caption Generator", viewModel, "Content Description") { input, platform ->
                viewModel.generateContent("Write engaging captions for '$input' on $platform. Provide variations: Short, Long, Storytelling, Engagement-focused, CTA-focused.", "You are an expert social media manager.")
            }
        }
        composable(Screen.ContentAnalyzer.route) { 
            GenericGeneratorScreen("Content Analyzer", viewModel, "Title, Hook or Script") { input, _ ->
                viewModel.generateContent("Analyze this content: '$input'. Evaluate hook strength, clarity, audience appeal, retention potential, CTA quality. Provide a simple score out of 10 and actionable improvements.", "You are a harsh but constructive content critic.")
            }
        }
        composable(Screen.ContentPlanner.route) { ContentPlannerScreen(viewModel) }
        composable(Screen.Analytics.route) { AnalyticsScreen(viewModel) }
        composable(Screen.AiChat.route) { AiChatScreen(viewModel) }
        composable(Screen.SavedContent.route) { SavedContentScreen(viewModel) }
        composable(Screen.Settings.route) { SettingsScreen(viewModel) }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title)
    }
}
