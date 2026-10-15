package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AlgorithmType
import com.example.ui.theme.ForestBackground
import com.example.ui.theme.ForestPrimary
import com.example.ui.theme.ForestSecondary
import com.example.viewmodel.TaskViewModel

@Composable
fun AlgorithmLabScreen(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val selectedAlgo by viewModel.labAlgorithm.collectAsState()
    val datasetSize by viewModel.labDatasetSize.collectAsState()
    val labResult by viewModel.labResult.collectAsState()
    val isRunning by viewModel.isLabRunning.collectAsState()

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
                    text = "Algorithm Lab & Benchmark",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = ForestPrimary
                )
                Text(
                    text = "Empirical analysis of Sorting, Searching, and Greedy Algorithms",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        // Experiment Configuration Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("lab_controls_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Select Algorithm to Test",
                        fontWeight = FontWeight.Bold,
                        color = ForestPrimary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AlgorithmType.values().forEach { algo ->
                            FilterChip(
                                selected = selectedAlgo == algo,
                                onClick = { viewModel.labAlgorithm.value = algo },
                                label = { Text(algo.displayName, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ForestPrimary.copy(alpha = 0.15f),
                                    selectedLabelColor = ForestPrimary
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "2. Input Workload Size (N elements)",
                        fontWeight = FontWeight.Bold,
                        color = ForestPrimary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(10, 25, 50, 100, 500).forEach { size ->
                            FilterChip(
                                selected = datasetSize == size,
                                onClick = { viewModel.labDatasetSize.value = size },
                                label = { Text("N = $size", fontSize = 11.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.runLabExperiment() },
                        enabled = !isRunning,
                        colors = ButtonDefaults.buttonColors(containerColor = ForestPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("run_lab_button")
                    ) {
                        if (isRunning) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Running JVM Benchmark...")
                        } else {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Run Benchmark Experiment")
                        }
                    }
                }
            }
        }

        // Live Benchmark Result Telemetry Card
        labResult?.let { result ->
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().testTag("lab_result_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = result.algorithm.displayName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = ForestPrimary
                                )
                                Text(
                                    text = "Workload: N = ${result.inputSize} items",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ForestSecondary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "${result.executionTimeMicros} µs",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = ForestSecondary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Metric Pills
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Comparisons", fontSize = 10.sp, color = Color.Gray)
                                    Text("${result.comparisonsCount}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Operations/Swaps", fontSize = 10.sp, color = Color.Gray)
                                    Text("${result.operationsCount}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Auxiliary Space", fontSize = 10.sp, color = Color.Gray)
                                    Text(result.complexity.spaceComplexity.split(" ").first(), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Complexity Table
                        Text("Theoretical Asymptotic Complexity", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ForestPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ComplexityCell(label = "Best (Ω)", notation = result.complexity.bestCase)
                            ComplexityCell(label = "Average (Θ)", notation = result.complexity.averageCase)
                            ComplexityCell(label = "Worst (O)", notation = result.complexity.worstCase)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = result.complexity.description,
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = result.complexity.academicRationale,
                            fontSize = 11.sp,
                            color = ForestSecondary,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Execution Trace Log
                        Text("Algorithm Execution Trace (First 5 steps):", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1E293B),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                result.executionTrace.take(6).forEach { line ->
                                    Text(
                                        text = "> $line",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = Color(0xFFE2E8F0)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "* Execution-time measurements are approximate and dependent on Android runtime/JVM environment.",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        // Comparative DAA Summary Table
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Master DAA Complexity Reference Table",
                        fontWeight = FontWeight.Bold,
                        color = ForestPrimary,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    ComplexityRow("Merge Sort", "O(n log n)", "O(n log n)", "O(n)", "Stable, Divide & Conquer")
                    ComplexityRow("Insertion Sort", "O(n)", "O(n²)", "O(1)", "Adaptive, In-place")
                    ComplexityRow("Binary Search", "O(1)", "O(log n)", "O(1)", "Requires sorted input")
                    ComplexityRow("Linear Search", "O(1)", "O(n)", "O(1)", "Unsorted inputs")
                    ComplexityRow("Greedy Scheduler", "O(n log n)", "O(n log n)", "O(n)", "Knapsack ratio heuristic")
                }
            }
        }
    }
}

@Composable
fun ComplexityCell(label: String, notation: String) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFFF1F5F9),
        modifier = Modifier.width(100.dp)
    ) {
        Column(modifier = Modifier.padding(6.dp)) {
            Text(label, fontSize = 10.sp, color = Color.Gray)
            Text(
                notation.split(" ").first(),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = ForestPrimary
            )
        }
    }
}

@Composable
fun ComplexityRow(
    name: String,
    best: String,
    worst: String,
    space: String,
    notes: String
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFF8FAFC),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1.2f)) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ForestPrimary)
                Text(notes, fontSize = 10.sp, color = Color.Gray)
            }
            Text("$best / $worst", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.width(8.dp))
            Surface(shape = RoundedCornerShape(4.dp), color = ForestPrimary.copy(alpha = 0.1f)) {
                Text(space, fontSize = 10.sp, color = ForestPrimary, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
            }
        }
    }
}
