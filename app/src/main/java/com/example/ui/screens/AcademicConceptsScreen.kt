package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
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

data class VivaItem(val question: String, val answer: String, val category: String)

@Composable
fun AcademicConceptsScreen(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val expandedViva = remember { mutableStateMapOf<Int, Boolean>() }

    val vivaQuestions = remember {
        listOf(
            VivaItem(
                question = "1. What is the Greedy Choice Property in our scheduling algorithm?",
                answer = "The greedy choice property means a globally near-optimal solution is assembled by making locally optimal choices at each step. In our scheduler, tasks are ordered by their Value-to-Duration Density ratio: R(i) = [PriorityWeight(i) × (1 + UrgencyScore(i))] / Duration(i). The task with the highest return per unit of study time is greedily scheduled first.",
                category = "DAA"
            ),
            VivaItem(
                question = "2. Why is our Task Scheduling not guaranteed to be strictly optimal (0/1 Knapsack limitation)?",
                answer = "Because tasks are discrete and cannot be split into fractional pieces (0/1 constraint). The Fractional Knapsack problem is solvable with 100% optimality via greedy choice in O(n log n), but 0/1 Knapsack is NP-Complete and requires Dynamic Programming O(n × W) to find exact optimum. Greedy scheduling is a fast polynomial approximation heuristic.",
                category = "DAA"
            ),
            VivaItem(
                question = "3. What is the recurrence relation and complexity for Merge Sort?",
                answer = "T(n) = 2T(n/2) + Θ(n). By the Master Theorem (Case 2: a = 2, b = 2, f(n) = n^1 = n^(log_b a)), T(n) = Θ(n log n) in best, average, and worst cases. Its limitation is requiring O(n) auxiliary memory for merging.",
                category = "DAA"
            ),
            VivaItem(
                question = "4. When does Insertion Sort outperform Merge Sort?",
                answer = "Insertion Sort runs in linear time Ω(n) when the input array is already sorted or nearly sorted, requiring zero auxiliary memory O(1). For small datasets (typically N < 30), Insertion Sort's low constant factors make it faster than Merge Sort.",
                category = "DAA"
            ),
            VivaItem(
                question = "5. Why does Binary Search require pre-sorted data?",
                answer = "Binary Search relies on the invariant that if the target is less than the middle element, it must exist strictly in the left half, and vice versa. If data is unsorted, this invariant is violated, yielding false negatives. Preserving this invariant costs O(n log n) for sorting once, followed by O(log n) lookups.",
                category = "DAA"
            ),
            VivaItem(
                question = "6. How is Row Level Security (RLS) implemented in Supabase / PostgreSQL?",
                answer = "Row Level Security enforces security policies directly in the database engine. In Supabase, the policy evaluates `auth.uid() = user_id`. Even if a malicious client issues `SELECT * FROM tasks`, PostgreSQL automatically filters rows to only return those matching the verified JWT subject claim.",
                category = "DCC"
            ),
            VivaItem(
                question = "7. What is PostgREST and how does Supabase use it?",
                answer = "PostgREST is an open-source web server that automatically translates RESTful HTTP requests directly into SQL queries inside PostgreSQL. This eliminates boilerplate CRUD backend code and provides sub-millisecond response times.",
                category = "DCC"
            ),
            VivaItem(
                question = "8. Explain the 3-Tier Client-Server Architecture of this system.",
                answer = "1. Presentation Layer (Client): Native Android Jetpack Compose app.\n2. Application / Logic Layer: Scheduling engine & REST API endpoints with DAA algorithms.\n3. Data Storage Layer: Supabase PostgreSQL cloud database with local SQLite (Room) offline cache.",
                category = "DCC"
            ),
            VivaItem(
                question = "9. How does our app handle network partition or offline status (CAP theorem)?",
                answer = "The app implements an Offline-First cache pattern: changes are written synchronously to local Room SQLite storage and queued. When network connectivity is re-established, the sync manager replays mutations to Supabase via idempotent upserts (merge-duplicates).",
                category = "DCC"
            ),
            VivaItem(
                question = "10. Why should Supabase service-role keys never be exposed on client devices?",
                answer = "The service-role key bypasses Row Level Security completely with administrative root privileges. Exposing it in client-side code would allow any user or reverse-engineer to alter, delete, or read all other users' private database records.",
                category = "DCC"
            )
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
        // Header
        item {
            Column {
                Text(
                    text = "Academic Concepts & Viva Prep",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = ForestPrimary
                )
                Text(
                    text = "Curriculum mapping for DAA, DCC, Viva Q&A, and Project Report",
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
                contentColor = ForestPrimary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Viva Questions", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("DAA Concepts", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("DCC Concepts", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("Report Outline", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        when (selectedTab) {
            0 -> {
                // Tab 0: Viva Question Bank
                item {
                    Text(
                        text = "College Viva Voce Question Bank (DAA & DCC)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ForestPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap on any question to expand detailed examiner answers.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                items(vivaQuestions.indices.toList()) { index ->
                    val item = vivaQuestions[index]
                    val isExpanded = expandedViva[index] == true

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedViva[index] = !isExpanded }
                            .testTag("viva_card_$index")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (item.category == "DAA") ForestPrimary.copy(alpha = 0.15f) else Color(0xFF028090).copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = item.category,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            color = if (item.category == "DAA") ForestPrimary else Color(0xFF028090),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = item.question,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = ForestPrimary
                                    )
                                }
                                Icon(
                                    if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = Color.Gray
                                )
                            }

                            AnimatedVisibility(visible = isExpanded) {
                                Column(modifier = Modifier.padding(top = 10.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFF8FAFC),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = item.answer,
                                            fontSize = 12.sp,
                                            lineHeight = 18.sp,
                                            color = Color(0xFF1E293B),
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Tab 1: DAA Syllabus Concepts
                item {
                    AcademicCard(
                        title = "1. Greedy Algorithm Paradigm",
                        content = "• Principle: Makes locally optimal choice at each stage with hope of finding global optimum.\n" +
                                "• Heuristic: Rank tasks by benefit ratio R = (Priority × Urgency) / Duration.\n" +
                                "• Asymptotic Time: O(n log n) dominated by sorting the ratio list.\n" +
                                "• Theoretical Limitation: For 0/1 tasks with discrete durations, greedy does not guarantee absolute global optimum (unlike Fractional Knapsack). Exact optimum requires Dynamic Programming O(n × W)."
                    )
                }
                item {
                    AcademicCard(
                        title = "2. Divide and Conquer: Merge Sort",
                        content = "• Recurrence Relation: T(n) = 2T(n/2) + Θ(n).\n" +
                                "• Solution: Master Theorem Case 2 gives Θ(n log n).\n" +
                                "• Stability: Maintains relative order of tasks with identical deadlines.\n" +
                                "• Space Complexity: O(n) auxiliary buffer required for merging."
                    )
                }
                item {
                    AcademicCard(
                        title = "3. Incremental Approach: Insertion Sort",
                        content = "• Best Case: Ω(n) on pre-sorted tasks (only 1 comparison per element, 0 shifts).\n" +
                                "• Worst Case: O(n²) when reverse-ordered.\n" +
                                "• Space Complexity: O(1) in-place without heap allocations."
                    )
                }
                item {
                    AcademicCard(
                        title = "4. Searching Paradigm: Linear vs Binary Search",
                        content = "• Linear Search: O(n) sequential scan. No prerequisite on data ordering.\n" +
                                "• Binary Search: O(log n) logarithmic reduction. Halves search interval each step.\n" +
                                "• Invariant Requirement: Strictly requires sorted keys. Initial sorting costs O(n log n)."
                    )
                }
            }

            2 -> {
                // Tab 2: DCC Syllabus Concepts
                item {
                    AcademicCard(
                        title = "1. Cloud Database-as-a-Service (DBaaS)",
                        content = "• Provider: Supabase (managed PostgreSQL cloud instance).\n" +
                                "• Capabilities: Automated backup, WAL replication, PostgREST API generation.\n" +
                                "• Benefit: Eliminates physical server provisioning and maintenance."
                    )
                }
                item {
                    AcademicCard(
                        title = "2. Security & Multi-Tenancy (Row Level Security)",
                        content = "• Zero-Trust Client: Client uses public anonymous key (`anon_key`).\n" +
                                "• RLS Policy: `USING (auth.uid() = user_id)` guarantees tenant data isolation.\n" +
                                "• Principle of Least Privilege: Never bundle the `service_role` administrative key in frontend code."
                    )
                }
                item {
                    AcademicCard(
                        title = "3. Distributed Consistency & Offline Toleration",
                        content = "• Local Storage: SQLite via Android Room acts as client-side cache.\n" +
                                "• Replication: Sync requests asynchronously pushed to Supabase cloud.\n" +
                                "• CAP Theorem Trade-off: High availability (A) and partition tolerance (P) maintained offline; eventual consistency achieved upon reconnection."
                    )
                }
            }

            3 -> {
                // Tab 3: Mini-Project Report Outline
                item {
                    AcademicCard(
                        title = "Mini-Project Report Synopsis & Outline",
                        content = "Chapter 1: Introduction & Problem Statement\n" +
                                "   - Challenges in student academic time management.\n" +
                                "   - Objective: Automate scheduling via DAA algorithms backed by DCC cloud storage.\n\n" +
                                "Chapter 2: Literature Survey & Theoretical Framework\n" +
                                "   - Analysis of Knapsack, Interval Scheduling, and Asymptotic Complexity.\n" +
                                "   - Comparison of Cloud architectures (Firebase vs Supabase DBaaS).\n\n" +
                                "Chapter 3: System Architecture & Design\n" +
                                "   - 3-Tier Architecture Diagram (Compose UI, Algorithm Engine, Supabase Cloud).\n" +
                                "   - Entity-Relationship (ER) Schema for Tasks & Row Level Security.\n\n" +
                                "Chapter 4: Implementation & Algorithms\n" +
                                "   - Merge Sort & Insertion Sort implementation.\n" +
                                "   - Greedy Density Scheduling heuristic code.\n\n" +
                                "Chapter 5: Empirical Benchmark & Results\n" +
                                "   - Microsecond execution measurements across N = 10, 50, 100, 500.\n" +
                                "   - Performance graphs and complexity verification.\n\n" +
                                "Chapter 6: Conclusion & Future Scope\n" +
                                "   - Distributed multi-device calendar sync and notifications."
                    )
                }
            }
        }
    }
}

@Composable
fun AcademicCard(title: String, content: String) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, color = ForestPrimary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = content, fontSize = 12.sp, lineHeight = 18.sp, color = Color.DarkGray)
        }
    }
}
