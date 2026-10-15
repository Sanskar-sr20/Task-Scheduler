package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AcademicConceptsScreen
import com.example.ui.screens.AlgorithmLabScreen
import com.example.ui.screens.CloudDccScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.SchedulerScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.theme.ForestBackground
import com.example.ui.theme.ForestPrimary
import com.example.ui.theme.ForestSecondary
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.TaskViewModel

enum class AppScreen(val label: String, val icon: ImageVector) {
    DASHBOARD("Home", Icons.Default.Dashboard),
    TASKS("Tasks", Icons.Default.FormatListBulleted),
    SCHEDULER("Schedule", Icons.Default.AutoAwesome),
    LAB("Algo Lab", Icons.Default.Science),
    ACADEMICS("Academics", Icons.Default.School),
    CLOUD("Cloud (DCC)", Icons.Default.Cloud),
    SETTINGS("Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {

    private val viewModel: TaskViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                var currentScreen by remember { mutableStateOf(AppScreen.DASHBOARD) }

                // Handle system back navigation to return to Dashboard if on sub-screen
                if (currentScreen != AppScreen.DASHBOARD) {
                    BackHandler {
                        currentScreen = AppScreen.DASHBOARD
                    }
                }

                Scaffold(
                    contentWindowInsets = WindowInsets.safeDrawing,
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    text = when (currentScreen) {
                                        AppScreen.DASHBOARD -> "Task Scheduler"
                                        AppScreen.TASKS -> "My Tasks"
                                        AppScreen.SCHEDULER -> "Smart Scheduler"
                                        AppScreen.LAB -> "Algorithm Lab"
                                        AppScreen.ACADEMICS -> "Academic Concepts"
                                        AppScreen.CLOUD -> "Cloud Integration (DCC)"
                                        AppScreen.SETTINGS -> "Settings & Profile"
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = ForestPrimary
                                )
                            },
                            actions = {
                                IconButton(
                                    onClick = { currentScreen = AppScreen.CLOUD },
                                    modifier = Modifier.testTag("top_bar_cloud_button")
                                ) {
                                    Icon(
                                        Icons.Default.Cloud,
                                        contentDescription = "Cloud DCC",
                                        tint = if (currentScreen == AppScreen.CLOUD) ForestSecondary else Color.Gray,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { currentScreen = AppScreen.SETTINGS },
                                    modifier = Modifier.testTag("top_bar_settings_button")
                                ) {
                                    Icon(
                                        Icons.Default.Settings,
                                        contentDescription = "Settings",
                                        tint = if (currentScreen == AppScreen.SETTINGS) ForestSecondary else Color.Gray,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = ForestBackground
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = Color.White,
                            tonalElevation = 8.dp,
                            modifier = Modifier.testTag("main_bottom_nav")
                        ) {
                            val navItems = listOf(
                                AppScreen.DASHBOARD,
                                AppScreen.TASKS,
                                AppScreen.SCHEDULER,
                                AppScreen.LAB,
                                AppScreen.ACADEMICS
                            )
                            navItems.forEach { screen ->
                                val selected = currentScreen == screen
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { currentScreen = screen },
                                    icon = {
                                        Icon(
                                            screen.icon,
                                            contentDescription = screen.label,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = screen.label,
                                            fontSize = 10.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = ForestPrimary,
                                        selectedTextColor = ForestPrimary,
                                        indicatorColor = ForestPrimary.copy(alpha = 0.15f),
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray
                                    ),
                                    modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                        when (currentScreen) {
                            AppScreen.DASHBOARD -> DashboardScreen(
                                viewModel = viewModel,
                                onNavigateToTasks = { currentScreen = AppScreen.TASKS },
                                onNavigateToScheduler = { currentScreen = AppScreen.SCHEDULER },
                                onNavigateToLab = { currentScreen = AppScreen.LAB },
                                onNavigateToDcc = { currentScreen = AppScreen.CLOUD }
                            )
                            AppScreen.TASKS -> TasksScreen(viewModel = viewModel)
                            AppScreen.SCHEDULER -> SchedulerScreen(viewModel = viewModel)
                            AppScreen.LAB -> AlgorithmLabScreen(viewModel = viewModel)
                            AppScreen.ACADEMICS -> AcademicConceptsScreen()
                            AppScreen.CLOUD -> CloudDccScreen(viewModel = viewModel)
                            AppScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
