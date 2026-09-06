package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.ContentPlanEntity
import com.example.ui.components.EmptyState
import com.example.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentPlannerScreen(viewModel: MainViewModel) {
    val plans by viewModel.contentPlans.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Plan")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            Text("Content Planner", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            
            if (plans.isEmpty()) {
                EmptyState("Empty Calendar", "No content planned yet.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(plans, key = { it.id }) { plan ->
                        PlanCard(
                            plan = plan,
                            onStatusChange = { newStatus -> viewModel.updatePlanStatus(plan, newStatus) },
                            onDelete = { viewModel.deletePlan(plan.id) }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        var topic by remember { mutableStateOf("") }
        var platform by remember { mutableStateOf("YouTube") }
        val platforms = listOf("YouTube", "Instagram", "TikTok", "Facebook", "X", "LinkedIn")
        var expandedPlatform by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add New Plan") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = topic, onValueChange = { topic = it }, label = { Text("Topic") }, modifier = Modifier.fillMaxWidth())
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
                        ExposedDropdownMenu(expanded = expandedPlatform, onDismissRequest = { expandedPlatform = false }) {
                            platforms.forEach { p ->
                                DropdownMenuItem(text = { Text(p) }, onClick = { platform = p; expandedPlatform = false })
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.savePlan(System.currentTimeMillis(), platform, topic, topic, "Idea")
                    showDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun PlanCard(plan: ContentPlanEntity, onStatusChange: (String) -> Unit, onDelete: () -> Unit) {
    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    val dateString = dateFormat.format(Date(plan.dateMs))
    val statuses = listOf("Idea", "Planned", "In Progress", "Published")

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(dateString, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
            Text(plan.topic, style = MaterialTheme.typography.titleMedium)
            Text(plan.platform, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            ScrollableTabRow(
                selectedTabIndex = statuses.indexOf(plan.status),
                edgePadding = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                statuses.forEachIndexed { index, status ->
                    Tab(
                        selected = plan.status == status,
                        onClick = { onStatusChange(status) },
                        text = { Text(status, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
        }
    }
}
