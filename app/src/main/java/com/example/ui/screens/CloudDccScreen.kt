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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ForestBackground
import com.example.ui.theme.ForestPrimary
import com.example.ui.theme.ForestSecondary
import com.example.viewmodel.TaskViewModel

@Composable
fun CloudDccScreen(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val currentUserId by viewModel.currentUserId.collectAsState()
    val syncLogs by viewModel.syncLogs.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

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
                    text = "Cloud Integration (DCC)",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = ForestPrimary
                )
                Text(
                    text = "Supabase DBaaS, Row Level Security (RLS), and REST Architecture",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        // Tab Selector
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = ForestPrimary,
                modifier = Modifier.testTag("dcc_tab_row")
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Cloud Sync & RLS", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("PostgREST Logs", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("SQL Schema & DDL", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        when (selectedTab) {
            0 -> {
                // Tab 0: Cloud Sync, User Switching & Latency
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth().testTag("user_isolation_card")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = ForestPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Row Level Security (RLS) Tenant Isolation",
                                    fontWeight = FontWeight.Bold,
                                    color = ForestPrimary,
                                    fontSize = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "DCC Concept: Switch active user credentials to demonstrate cross-tenant database isolation. Each user only retrieves tasks where auth.uid() == user_id.",
                                fontSize = 12.sp,
                                color = Color.DarkGray
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text("Active User Profile:", fontSize = 11.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("student_2026_demo", "cs_examiner_demo", "guest_user").forEach { uid ->
                                    FilterChip(
                                        selected = currentUserId == uid,
                                        onClick = { viewModel.switchUser(uid) },
                                        label = { Text(uid, fontSize = 11.sp) },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Cloud Latency Simulator
                item {
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
                                    text = "Network Latency Simulation",
                                    fontWeight = FontWeight.Bold,
                                    color = ForestPrimary,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${viewModel.cloudManager.simulatedLatencyMs} ms",
                                    fontWeight = FontWeight.Bold,
                                    color = ForestSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Simulate cloud data-center round-trip latency (Mumbai / us-east-1).",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )

                            Slider(
                                value = viewModel.cloudManager.simulatedLatencyMs.toFloat(),
                                onValueChange = { viewModel.cloudManager.simulatedLatencyMs = it.toLong() },
                                valueRange = 0f..600f,
                                steps = 5,
                                colors = SliderDefaults.colors(thumbColor = ForestPrimary, activeTrackColor = ForestPrimary)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = { viewModel.triggerManualCloudSync() },
                                colors = ButtonDefaults.buttonColors(containerColor = ForestPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().testTag("manual_sync_button")
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Trigger Manual Cloud Sync")
                            }
                        }
                    }
                }

                // Cloud Architecture Concept Card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEDF4EF)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Distributed & Cloud Computing Architecture",
                                fontWeight = FontWeight.Bold,
                                color = ForestPrimary,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "1. Client Layer: Android Jetpack Compose app caching state in SQLite (Room).\n" +
                                        "2. REST Transport: Secure HTTPS PostgREST payloads with JWT tokens.\n" +
                                        "3. Cloud DB Layer: Hosted Supabase PostgreSQL with automated WAL replication.\n" +
                                        "4. Security: Zero service-role keys in client. Row Level Security guarantees tenant isolation.",
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = Color(0xFF191C1A)
                            )
                        }
                    }
                }
            }

            1 -> {
                // Tab 1: Live PostgREST Sync Logs
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PostgREST Cloud Transaction Logs",
                            fontWeight = FontWeight.Bold,
                            color = ForestPrimary,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${syncLogs.size} events",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                if (syncLogs.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                        ) {
                            Text(
                                text = "No cloud transactions yet. Add or update a task to trigger sync.",
                                color = Color.Gray,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(syncLogs.reversed()) { log ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (log.statusCode < 300) ForestPrimary.copy(alpha = 0.15f) else Color(0xFFFEE2E2)
                                    ) {
                                        Text(
                                            text = "${log.method} [${log.statusCode}]",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = if (log.statusCode < 300) ForestPrimary else Color.Red,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = "${log.latencyMs} ms | ${log.timestampFormatted}",
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = log.message,
                                    fontSize = 11.sp,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }
                }
            }

            2 -> {
                // Tab 2: SQL Schema & DDL
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Supabase PostgreSQL Schema (schema.sql)",
                                fontWeight = FontWeight.Bold,
                                color = ForestPrimary,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "DDL script and Row Level Security policies for project submission:",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1E293B),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = """
-- 1. Create Tasks Table
CREATE TABLE IF NOT EXISTS public.tasks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id TEXT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(50) DEFAULT 'ASSIGNMENT',
    priority INT NOT NULL CHECK (priority BETWEEN 1 AND 3),
    duration_minutes INT NOT NULL DEFAULT 60,
    deadline_epoch BIGINT NOT NULL,
    status VARCHAR(30) DEFAULT 'PENDING',
    created_at BIGINT DEFAULT EXTRACT(EPOCH FROM NOW()) * 1000
);

-- 2. Enable Row Level Security (RLS)
ALTER TABLE public.tasks ENABLE ROW LEVEL SECURITY;

-- 3. Policy: User can only access own tasks
CREATE POLICY "Users can manage own tasks"
ON public.tasks
FOR ALL
USING (auth.uid() = user_id OR user_id = current_setting('request.jwt.claim.sub', true));

-- 4. B-Tree Indexes for fast query retrieval
CREATE INDEX idx_tasks_user_deadline ON public.tasks (user_id, deadline_epoch);
CREATE INDEX idx_tasks_priority ON public.tasks (priority DESC);
                                    """.trimIndent(),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = Color(0xFFE2E8F0),
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
