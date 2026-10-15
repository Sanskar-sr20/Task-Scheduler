package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AlgorithmType
import com.example.model.Task
import com.example.model.TaskCategory
import com.example.model.TaskPriority
import com.example.model.TaskStatus
import com.example.ui.components.CategoryBadge
import com.example.ui.components.PriorityBadge
import com.example.ui.components.StatusBadge
import com.example.ui.components.TaskEditDialog
import com.example.ui.theme.ForestBackground
import com.example.ui.theme.ForestPrimary
import com.example.ui.theme.ForestSecondary
import com.example.ui.theme.PriorityHigh
import com.example.viewmodel.DeadlineFilter
import com.example.viewmodel.SortKey
import com.example.viewmodel.TaskViewModel

@Composable
fun TasksScreen(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.displayedTasks.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchAlgo by viewModel.searchAlgorithm.collectAsState()
    val sortKey by viewModel.activeSortKey.collectAsState()
    val sortAlgo by viewModel.activeSortAlgorithm.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedPriority by viewModel.selectedPriority.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()
    val deadlineFilter by viewModel.selectedDeadlineFilter.collectAsState()

    val lastSortResult by viewModel.lastSortResult.collectAsState()
    val lastSearchResult by viewModel.lastSearchResult.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    var showFilterSheet by remember { mutableStateOf(false) }

    if (showAddDialog) {
        TaskEditDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, desc, cat, deadline, prio, dur ->
                viewModel.addTask(title, desc, cat, deadline, prio, dur)
            }
        )
    }

    if (taskToEdit != null) {
        TaskEditDialog(
            initialTask = taskToEdit,
            onDismiss = { taskToEdit = null },
            onConfirm = { title, desc, cat, deadline, prio, dur ->
                taskToEdit?.let { existing ->
                    viewModel.updateTask(
                        existing.copy(
                            title = title,
                            description = desc,
                            category = cat,
                            deadlineEpochMillis = deadline,
                            priority = prio,
                            estimatedDurationMinutes = dur
                        )
                    )
                }
                taskToEdit = null
            }
        )
    }

    if (taskToDelete != null) {
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete '${taskToDelete?.title}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        taskToDelete?.id?.let { viewModel.deleteTask(it) }
                        taskToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(modifier = modifier.fillMaxSize().background(ForestBackground)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Screen Header & Search Box
            item {
                Column {
                    Text(
                        text = "Task Management",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = ForestPrimary
                    )
                    Text(
                        text = "Organize, sort, and search with DAA algorithms",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Search TextField
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.searchQuery.value = it },
                        modifier = Modifier.fillMaxWidth().testTag("task_search_field"),
                        placeholder = { Text("Search tasks...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ForestPrimary) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear search")
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Search Algorithm Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Search Algorithm:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ForestPrimary
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                selected = searchAlgo == AlgorithmType.LINEAR_SEARCH,
                                onClick = { viewModel.searchAlgorithm.value = AlgorithmType.LINEAR_SEARCH },
                                label = { Text("Linear Search O(n)", fontSize = 11.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                            FilterChip(
                                selected = searchAlgo == AlgorithmType.BINARY_SEARCH,
                                onClick = { viewModel.searchAlgorithm.value = AlgorithmType.BINARY_SEARCH },
                                label = { Text("Binary Search O(log n)", fontSize = 11.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }
            }

            // DAA Sorting Controls Row
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Sort, contentDescription = null, tint = ForestSecondary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Sort Key & Algorithm",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = ForestPrimary
                                )
                            }
                            // Toggle Merge Sort vs Insertion Sort
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                FilterChip(
                                    selected = sortAlgo == AlgorithmType.MERGE_SORT,
                                    onClick = { viewModel.activeSortAlgorithm.value = AlgorithmType.MERGE_SORT },
                                    label = { Text("Merge Sort O(n log n)", fontSize = 10.sp) },
                                    shape = RoundedCornerShape(6.dp)
                                )
                                FilterChip(
                                    selected = sortAlgo == AlgorithmType.INSERTION_SORT,
                                    onClick = { viewModel.activeSortAlgorithm.value = AlgorithmType.INSERTION_SORT },
                                    label = { Text("Insertion Sort O(n²)", fontSize = 10.sp) },
                                    shape = RoundedCornerShape(6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Sort keys
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            SortKey.values().forEach { key ->
                                FilterChip(
                                    selected = sortKey == key,
                                    onClick = { viewModel.activeSortKey.value = key },
                                    label = { Text(key.label, fontSize = 11.sp) },
                                    shape = RoundedCornerShape(6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Category Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { viewModel.selectedCategory.value = null },
                        label = { Text("All Categories", fontSize = 11.sp) },
                        shape = RoundedCornerShape(8.dp)
                    )
                    TaskCategory.values().forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = {
                                viewModel.selectedCategory.value = if (selectedCategory == cat) null else cat
                            },
                            label = { Text(cat.displayName, fontSize = 11.sp) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            // Real-time DAA Performance Banner
            item {
                lastSortResult?.let { res ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFE8F5E9),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = ForestSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${res.algorithm.displayName} ran in ${res.executionTimeMicros} µs (${res.comparisonsCount} comparisons, ${res.operationsCount} operations)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ForestPrimary
                            )
                        }
                    }
                }
            }

            // Task List
            if (tasks.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No tasks match your filters",
                                fontWeight = FontWeight.Bold,
                                color = ForestPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Try clearing the search query or category filters",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            } else {
                items(tasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onToggleDone = { viewModel.toggleTaskCompletion(task) },
                        onEdit = { taskToEdit = task },
                        onDelete = { taskToDelete = task }
                    )
                }
            }
        }

        // Floating Action Button to Add Task
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = ForestPrimary,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_add_task")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Task")
        }
    }
}

@Composable
fun TaskCard(
    task: Task,
    onToggleDone: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("task_card_${task.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Checkbox(
                        checked = task.status == TaskStatus.COMPLETED,
                        onCheckedChange = { onToggleDone() },
                        colors = CheckboxDefaults.colors(checkedColor = ForestPrimary),
                        modifier = Modifier.testTag("task_checkbox_${task.id}")
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = task.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textDecoration = if (task.status == TaskStatus.COMPLETED) TextDecoration.LineThrough else TextDecoration.None,
                            color = if (task.status == TaskStatus.COMPLETED) Color.Gray else ForestPrimary
                        )
                        if (task.description.isNotBlank()) {
                            Text(
                                text = task.description,
                                fontSize = 12.sp,
                                color = Color.DarkGray,
                                maxLines = 2
                            )
                        }
                    }
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Badges & Meta Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    CategoryBadge(category = task.category)
                    PriorityBadge(priority = task.priority)
                    StatusBadge(status = task.status)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${task.estimatedDurationMinutes}m",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestSecondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (task.isOverdue) "Overdue" else "${task.daysUntilDeadline}d left",
                        fontSize = 11.sp,
                        color = if (task.isOverdue) PriorityHigh else Color.Gray,
                        fontWeight = if (task.isOverdue) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
