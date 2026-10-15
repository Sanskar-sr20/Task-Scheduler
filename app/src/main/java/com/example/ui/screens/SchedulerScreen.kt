package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CategoryBadge
import com.example.ui.components.PriorityBadge
import com.example.ui.theme.ForestBackground
import com.example.ui.theme.ForestPrimary
import com.example.ui.theme.ForestSecondary
import com.example.viewmodel.TaskViewModel

@Composable
fun SchedulerScreen(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val scheduleReport by viewModel.scheduleReport.collectAsState()
    val availableMinutes by viewModel.availableStudyMinutes.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ForestBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Smart Scheduling Engine",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = ForestPrimary
                )
                Text(
                    text = "DAA Greedy Paradigm: Value-to-Duration Density Optimization",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        // Available Study Time Configuration Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("scheduler_config_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Available Study Time",
                            fontWeight = FontWeight.Bold,
                            color = ForestPrimary
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ForestPrimary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "${availableMinutes / 60}h ${availableMinutes % 60}m ($availableMinutes mins)",
                                fontWeight = FontWeight.Bold,
                                color = ForestPrimary,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Slider(
                        value = availableMinutes.toFloat(),
                        onValueChange = { viewModel.refreshSchedule(it.toInt()) },
                        valueRange = 60f..480f,
                        steps = 13, // 30-min increments
                        colors = SliderDefaults.colors(
                            thumbColor = ForestPrimary,
                            activeTrackColor = ForestPrimary
                        ),
                        modifier = Modifier.testTag("study_time_slider")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(120, 180, 240, 300, 360).forEach { mins ->
                            FilterChip(
                                selected = availableMinutes == mins,
                                onClick = { viewModel.refreshSchedule(mins) },
                                label = { Text("${mins / 60}h", fontSize = 11.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.refreshSchedule(availableMinutes) },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("regenerate_schedule_button")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Re-compute Greedy Schedule")
                    }
                }
            }
        }

        // Efficiency & Capacity Progress
        item {
            scheduleReport?.let { report ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Study Budget Utilization",
                                fontWeight = FontWeight.Bold,
                                color = ForestPrimary,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "%.1f%% Allocated".format(report.efficiencyPercentage),
                                fontWeight = FontWeight.Bold,
                                color = ForestSecondary,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { (report.totalAllocatedMinutes.toFloat() / Math.max(1, report.availableMinutes)).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(10.dp),
                            color = ForestPrimary,
                            trackColor = ForestPrimary.copy(alpha = 0.15f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Allocated: ${report.totalAllocatedMinutes} mins (${report.scheduledSlots.size} tasks)",
                                fontSize = 12.sp,
                                color = Color.DarkGray
                            )
                            Text(
                                text = "Remaining: ${report.availableMinutes - report.totalAllocatedMinutes} mins",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }

        // Scheduled Timeline Slots
        item {
            Text(
                text = "Feasible Timeline Schedule",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ForestPrimary
            )
        }

        scheduleReport?.let { report ->
            if (report.scheduledSlots.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "No tasks could be scheduled. Increase study time or add pending tasks.",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(report.scheduledSlots) { slot ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Timeline Time Pill
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = ForestPrimary,
                                modifier = Modifier.width(76.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = slot.startTimeFormatted,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "to",
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        text = slot.endTimeFormatted,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = slot.task.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    CategoryBadge(category = slot.task.category)
                                    PriorityBadge(priority = slot.task.priority)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Greedy Ratio: %.2f | %s".format(slot.greedyScore, slot.rationale),
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            // Unscheduled / Deferred Tasks Section
            if (report.unscheduledTasks.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Deferred Tasks (Exceeded Daily Study Budget)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB45309)
                    )
                }

                items(report.unscheduledTasks) { task ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = task.title, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                Text(
                                    text = "Duration: ${task.estimatedDurationMinutes}m | Requires next available session",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                            PriorityBadge(priority = task.priority)
                        }
                    }
                }
            }

            // Academic DAA Greedy Concept Note
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEDF4EF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = ForestPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "DAA Academic Concept: Greedy Choice Property",
                                fontWeight = FontWeight.Bold,
                                color = ForestPrimary,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = report.greedyRuleExplanation,
                            fontSize = 12.sp,
                            color = Color(0xFF191C1A),
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = report.limitationsNote,
                            fontSize = 11.sp,
                            color = Color(0xFF6B4226),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
