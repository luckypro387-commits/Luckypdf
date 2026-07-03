package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.Task
import com.example.data.TaskPriority
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.uiState.collectAsState()
    val categories by viewModel.categoriesState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val selectedSortBy by viewModel.selectedSortBy.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    var showAddTaskDialog by remember { mutableStateOf(false) }

    // Derive status/progress
    val totalTasks = tasks.size
    val completedTasks = tasks.count { it.isCompleted }
    val progressFraction = if (totalTasks > 0) completedTasks.toFloat() / totalTasks else 0f

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTaskDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .testTag("add_task_fab")
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "Add Task")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Task", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Elegant Header Area
            HeaderSection(
                completedCount = completedTasks,
                totalCount = totalTasks,
                progress = progressFraction,
                onClearCompleted = { viewModel.clearCompleted() }
            )

            // Search Bar & Filter Section
            SearchAndFilterSection(
                searchQuery = searchQuery,
                onSearchChange = { viewModel.setSearchQuery(it) },
                selectedSortBy = selectedSortBy,
                onSortChange = { viewModel.setSortBy(it) }
            )

            // Category scrolling list
            CategoryChipsSection(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelect = { viewModel.setCategory(it) }
            )

            // Status Filter row
            StatusFilterRow(
                selectedFilter = selectedFilter,
                onFilterSelect = { viewModel.setFilter(it) }
            )

            // Task List
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (tasks.isEmpty()) {
                    EmptyStateSection(
                        hasFilter = searchQuery.isNotEmpty() || selectedCategory != "All" || selectedFilter != TaskFilter.ALL,
                        onClearFilters = {
                            viewModel.setSearchQuery("")
                            viewModel.setCategory("All")
                            viewModel.setFilter(TaskFilter.ALL)
                        }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(tasks, key = { it.id }) { task ->
                            TaskItemRow(
                                task = task,
                                onToggle = { viewModel.toggleTaskCompletion(task) },
                                onDelete = { viewModel.deleteTask(task) }
                            )
                        }
                    }
                }
            }
        }

        // Add Task Dialog Modal
        if (showAddTaskDialog) {
            AddTaskDialog(
                categories = categories.filter { it != "All" },
                onDismiss = { showAddTaskDialog = false },
                onConfirm = { title, desc, priority, cat ->
                    viewModel.addTask(title, desc, priority, cat)
                    showAddTaskDialog = false
                }
            )
        }
    }
}

@Composable
fun HeaderSection(
    completedCount: Int,
    totalCount: Int,
    progress: Float,
    onClearCompleted: () -> Unit
) {
    val currentDate = remember {
        SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date())
    }

    val animatedProgress by animateFloatAsState(targetValue = progress, label = "Progress")

    Surface(
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "My Spaces",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "TaskFlow",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = currentDate,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Progress Indicator Ring
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(72.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        strokeWidth = 6.dp
                    )
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 6.dp
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (totalCount > 0) "${(progress * 100).toInt()}%" else "0%",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$completedCount/$totalCount",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            if (completedCount > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Clear completed tasks",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onClearCompleted() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SearchAndFilterSection(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedSortBy: TaskSortBy,
    onSortChange: (TaskSortBy) -> Unit
) {
    var sortMenuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search tasks...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box {
            IconButton(
                onClick = { sortMenuExpanded = true },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                modifier = Modifier.size(52.dp)
            ) {
                Icon(Icons.Rounded.Sort, contentDescription = "Sort Tasks")
            }

            DropdownMenu(
                expanded = sortMenuExpanded,
                onDismissRequest = { sortMenuExpanded = false }
            ) {
                Text(
                    text = "Sort By",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                DropdownMenuItem(
                    text = { Text("Priority") },
                    onClick = {
                        onSortChange(TaskSortBy.PRIORITY)
                        sortMenuExpanded = false
                    },
                    leadingIcon = {
                        if (selectedSortBy == TaskSortBy.PRIORITY) {
                            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                )
                DropdownMenuItem(
                    text = { Text("Creation Date") },
                    onClick = {
                        onSortChange(TaskSortBy.CREATION_DATE)
                        sortMenuExpanded = false
                    },
                    leadingIcon = {
                        if (selectedSortBy == TaskSortBy.CREATION_DATE) {
                            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                )
                DropdownMenuItem(
                    text = { Text("Alphabetical") },
                    onClick = {
                        onSortChange(TaskSortBy.ALPHABETICAL)
                        sortMenuExpanded = false
                    },
                    leadingIcon = {
                        if (selectedSortBy == TaskSortBy.ALPHABETICAL) {
                            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CategoryChipsSection(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 6.dp)) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                val isSelected = category == selectedCategory
                val chipColor = animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    label = "ChipColor"
                )
                val textColor = animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "TextColor"
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(chipColor.value)
                        .clickable { onCategorySelect(category) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isSelected) Icons.Rounded.FolderOpen else Icons.Rounded.Folder,
                        contentDescription = null,
                        tint = textColor.value,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = category,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = textColor.value
                    )
                }
            }
        }
    }
}

@Composable
fun StatusFilterRow(
    selectedFilter: TaskFilter,
    onFilterSelect: (TaskFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TaskFilter.values().forEach { filter ->
            val isSelected = filter == selectedFilter
            val containerColor = animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                label = "FilterContainer"
            )
            val textColor = animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.outline,
                label = "FilterText"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(containerColor.value)
                    .clickable { onFilterSelect(filter) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (filter) {
                        TaskFilter.ALL -> "All"
                        TaskFilter.ACTIVE -> "Active"
                        TaskFilter.COMPLETED -> "Completed"
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = textColor.value
                )
            }
        }
    }
}

@Composable
fun TaskItemRow(
    task: Task,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val cardBgColor = animateColorAsState(
        targetValue = if (task.isCompleted) {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        label = "CardBg"
    )

    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (task.isCompleted) 0.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = cardBgColor.value
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("task_item_card_${task.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox/Toggle Indicator
            IconButton(
                onClick = onToggle,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("task_checkbox_${task.id}")
            ) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                    contentDescription = "Toggle Complete",
                    tint = if (task.isCompleted) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline
                    },
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Text Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                    ),
                    color = if (task.isCompleted) {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                if (task.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Metadata tags (Category & Priority)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Chip
                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = task.category,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    // Priority Chip
                    val priorityColors = getPriorityColors(task.priority)
                    Box(
                        modifier = Modifier
                            .background(priorityColors.first, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = task.priority.name,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = priorityColors.second
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Delete action
            IconButton(
                onClick = onDelete,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                ),
                modifier = Modifier
                    .size(48.dp)
                    .testTag("delete_task_button_${task.id}")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Delete Task",
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyStateSection(
    hasFilter: Boolean,
    onClearFilters: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (hasFilter) Icons.Rounded.Info else Icons.Rounded.List,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
            modifier = Modifier.size(96.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (hasFilter) "No matches found" else "All caught up!",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (hasFilter) {
                "Try clearing your search filters or selecting another category."
            } else {
                "Start building your space by adding a new task using the button below."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (hasFilter) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = onClearFilters,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Clear Filters")
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    categories: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String, priority: TaskPriority, category: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull() ?: "Personal") }
    var customCategoryActive by remember { mutableStateOf(false) }
    var customCategoryText by remember { mutableStateOf("") }

    val activeCategory = if (customCategoryActive) customCategoryText else selectedCategory

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 500.dp)
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
            ) {
                Text(
                    text = "New Space Task",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title *") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_input_title")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Description Input
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Notes / Description") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Priority Selection Header
                Text(
                    text = "Select Priority",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Priority Row Segmented Style
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskPriority.values().forEach { priority ->
                        val isSelected = selectedPriority == priority
                        val colors = getPriorityColors(priority)

                        val containerColor = animateColorAsState(
                            targetValue = if (isSelected) colors.first else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            label = "PriorityContainer"
                        )
                        val textColor = animateColorAsState(
                            targetValue = if (isSelected) colors.second else MaterialTheme.colorScheme.onSurfaceVariant,
                            label = "PriorityText"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(containerColor.value)
                                .clickable { selectedPriority = priority }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = priority.name,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = textColor.value
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category selection header
                Text(
                    text = "Assign Category",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Category FlowRow selection
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    categories.forEach { cat ->
                        val isSelected = !customCategoryActive && selectedCategory == cat
                        InputChip(
                            selected = isSelected,
                            onClick = {
                                customCategoryActive = false
                                selectedCategory = cat
                            },
                            label = { Text(cat) },
                            colors = InputChipDefaults.inputChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }

                    // Custom tag chip
                    InputChip(
                        selected = customCategoryActive,
                        onClick = { customCategoryActive = true },
                        label = { Text("+ Custom") },
                        colors = InputChipDefaults.inputChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }

                if (customCategoryActive) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customCategoryText,
                        onValueChange = { customCategoryText = it },
                        placeholder = { Text("Enter category name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Actions row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onConfirm(title, description, selectedPriority, activeCategory)
                            }
                        },
                        enabled = title.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("submit_task_button")
                    ) {
                        Text("Save Task")
                    }
                }
            }
        }
    }
}

@Composable
fun getPriorityColors(priority: TaskPriority): Pair<Color, Color> {
    return when (priority) {
        TaskPriority.LOW -> Pair(
            Color(0xFFE2F0D9), // Light pastel green background
            Color(0xFF385723)  // Dark forest green text
        )
        TaskPriority.MEDIUM -> Pair(
            Color(0xFFFFF2CC), // Light pastel yellow background
            Color(0xFF7F6000)  // Dark gold text
        )
        TaskPriority.HIGH -> Pair(
            Color(0xFFFCE4D6), // Light pastel red background
            Color(0xFFC65911)  // Dark crimson text
        )
    }
}
