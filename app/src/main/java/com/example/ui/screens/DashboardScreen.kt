package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Task
import com.example.model.TaskPriority
import com.example.model.TaskStatus
import com.example.ui.components.CategoryBadge
import com.example.ui.components.PriorityBadge
import com.example.ui.components.TaskEditDialog
import com.example.ui.theme.ForestBackground
import com.example.ui.theme.ForestPrimary
import com.example.ui.theme.ForestSecondary
import com.example.ui.theme.PriorityHigh
import com.example.viewmodel.TaskViewModel

@Composable
fun DashboardScreen(
    viewModel: TaskViewModel,
    onNavigateToTasks: () -> Unit,
    onNavigateToScheduler: () -> Unit,
    onNavigateToLab: () -> Unit,
    onNavigateToDcc: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allTasks by viewModel.allTasks.collectAsState()
    val scheduleReport by viewModel.scheduleReport.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    val totalCount = allTasks.size
    val pendingCount = allTasks.count { it.status == TaskStatus.PENDING }
    val completedCount = allTasks.count { it.status == TaskStatus.COMPLETED }
    val overdueCount = allTasks.count { it.isOverdue }
    val dueTodayCount = allTasks.count { it.isDueToday }

    val upcomingTasks = remember(allTasks) {
        allTasks.filter { it.status != TaskStatus.COMPLETED }
            .sortedBy { it.deadlineEpochMillis }
            .take(3)
    }

    if (showAddDialog) {
        TaskEditDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, desc, cat, deadline, prio, dur ->
                viewModel.addTask(title, desc, cat, deadline, prio, dur)
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ForestBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header Banner
        item {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = ForestPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_header_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Academic Task Scheduler",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "DAA Algorithm Engine & DCC Cloud Sync",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier
                                .clickable { onNavigateToDcc() }
                                .testTag("cloud_status_badge")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CloudDone,
                                    contentDescription = "Cloud Connected",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Supabase RLS",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Logged in as: $currentUserId",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp
                        )
                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("add_task_header_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = ForestPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Task", color = ForestPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Summary Metric Cards Grid
        item {
            Text(
                text = "Academic Task Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ForestPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Total",
                    count = totalCount,
                    icon = Icons.Default.PendingActions,
                    color = ForestPrimary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Pending",
                    count = pendingCount,
                    icon = Icons.Default.Schedule,
                    color = Color(0xFFD97706),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Done",
                    count = completedCount,
                    icon = Icons.Default.CheckCircle,
                    color = Color(0xFF2D6A4F),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Due Today",
                    count = dueTodayCount,
                    icon = Icons.Default.AutoAwesome,
                    color = Color(0xFF028090),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Overdue",
                    count = overdueCount,
                    icon = Icons.Default.Warning,
                    color = PriorityHigh,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Smart Daily Schedule Preview (DAA Greedy)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_schedule_preview_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ForestSecondary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Today's Greedy Schedule",
                                fontWeight = FontWeight.Bold,
                                color = ForestPrimary,
                                fontSize = 16.sp
                            )
                        }
                        Text(
                            text = "${scheduleReport?.totalAllocatedMinutes ?: 0}m / ${scheduleReport?.availableMinutes ?: 180}m",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ForestSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (scheduleReport?.scheduledSlots.isNullOrEmpty()) {
                        Text(
                            text = "No pending tasks or available study time set.",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        scheduleReport?.scheduledSlots?.take(2)?.forEach { slot ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = ForestPrimary.copy(alpha = 0.1f)
                                    ) {
                                        Text(
                                            text = "${slot.startTimeFormatted} - ${slot.endTimeFormatted}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ForestPrimary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = slot.task.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1
                                    )
                                }
                                PriorityBadge(priority = slot.task.priority)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onNavigateToScheduler,
                        colors = ButtonDefaults.buttonColors(containerColor = ForestSecondary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_scheduler_button")
                    ) {
                        Text("Customize Daily Schedule & DAA Analysis", fontSize = 12.sp)
                    }
                }
            }
        }

        // Upcoming Deadlines Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Upcoming Deadlines",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ForestPrimary
                )
                Text(
                    text = "View All Tasks",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestSecondary,
                    modifier = Modifier
                        .clickable { onNavigateToTasks() }
                        .padding(4.dp)
                )
            }
        }

        if (upcomingTasks.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No pending tasks. You're all caught up!",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(upcomingTasks) { task ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                CategoryBadge(category = task.category)
                                PriorityBadge(priority = task.priority)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${task.estimatedDurationMinutes} mins",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ForestPrimary
                            )
                            Text(
                                text = if (task.isOverdue) "Overdue!" else "${task.daysUntilDeadline}d left",
                                fontSize = 11.sp,
                                color = if (task.isOverdue) PriorityHigh else Color.Gray,
                                fontWeight = if (task.isOverdue) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // Quick Lab Jump
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToLab() }
                    .testTag("dashboard_lab_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = ForestPrimary,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Science, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Algorithm Demonstration Lab",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = ForestPrimary
                            )
                            Text(
                                text = "Test Merge Sort, Insertion Sort, Binary & Linear Search",
                                fontSize = 11.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    count: Int,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            }
            Text(
                text = "$count",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
