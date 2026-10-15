package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ForestBackground
import com.example.ui.theme.ForestPrimary
import com.example.ui.theme.ForestSecondary
import com.example.viewmodel.TaskViewModel

@Composable
fun SettingsScreen(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val currentUserId by viewModel.currentUserId.collectAsState()
    var inputUserId by remember { mutableStateOf(currentUserId) }
    var supabaseUrlInput by remember { mutableStateOf(viewModel.cloudManager.supabaseUrl) }
    var seedSuccessMessage by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ForestBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Project Settings & Profile",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = ForestPrimary
                )
                Text(
                    text = "Configure student identity, cloud backend, and demo environment",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        // Student Account / Login Simulation
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("student_profile_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = ForestPrimary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Student Authentication / Profile",
                            fontWeight = FontWeight.Bold,
                            color = ForestPrimary,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = inputUserId,
                        onValueChange = { inputUserId = it },
                        label = { Text("Student User ID / Email") },
                        placeholder = { Text("student_2026_demo") },
                        modifier = Modifier.fillMaxWidth().testTag("user_id_field"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.switchUser(inputUserId.trim()) },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("switch_user_button")
                    ) {
                        Text("Switch Active Student Account (RLS Test)")
                    }
                }
            }
        }

        // Cloud Database Configuration (DCC)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Cloud, contentDescription = null, tint = ForestSecondary, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Supabase Cloud Endpoint",
                            fontWeight = FontWeight.Bold,
                            color = ForestPrimary,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = supabaseUrlInput,
                        onValueChange = {
                            supabaseUrlInput = it
                            viewModel.cloudManager.supabaseUrl = it
                        },
                        label = { Text("Supabase Project URL") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Backend Status: Connected (DBaaS)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ForestSecondary
                        )
                    }
                }
            }
        }

        // Demo Data Re-seeder
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Demo Workload & Reset",
                        fontWeight = FontWeight.Bold,
                        color = ForestPrimary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Reset sample academic tasks (DAA assignments, OS practicals, Compiler prep) for viva presentation.",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {
                            viewModel.loadTasksForCurrentUser()
                            seedSuccessMessage = true
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("reseed_data_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Re-seed Academic Demo Tasks")
                    }

                    if (seedSuccessMessage) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Academic tasks reloaded successfully!",
                            color = ForestSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Project Info & Academic Metadata
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEDF4EF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.School, contentDescription = null, tint = ForestPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "College Mini-Project Information",
                            fontWeight = FontWeight.Bold,
                            color = ForestPrimary,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Project Title: Cloud-Based Task Scheduler and Priority Manager", fontSize = 12.sp)
                    Text("• Subjects: Design & Analysis of Algorithms (DAA) + Distributed & Cloud Computing (DCC)", fontSize = 12.sp)
                    Text("• Target: Computer Science & Engineering (B.E. / B.Tech)", fontSize = 12.sp)
                    Text("• Architecture: Client (Android Compose), DAA Logic, Cloud DB (Supabase RLS)", fontSize = 12.sp)
                    Text("• Version: 1.0.0 (Production Mini-Project Build)", fontSize = 12.sp)
                }
            }
        }
    }
}
