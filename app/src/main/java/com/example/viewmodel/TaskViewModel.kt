package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.algorithms.GreedyScheduler
import com.example.algorithms.SearchingAlgorithms
import com.example.algorithms.SortingAlgorithms
import com.example.data.cloud.CloudSyncLog
import com.example.data.cloud.SupabaseCloudManager
import com.example.data.local.AppDatabase
import com.example.data.repository.TaskRepository
import com.example.model.AlgorithmResult
import com.example.model.AlgorithmType
import com.example.model.ScheduleReport
import com.example.model.Task
import com.example.model.TaskCategory
import com.example.model.TaskPriority
import com.example.model.TaskStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class SortKey(val label: String) {
    DEADLINE("Deadline"),
    PRIORITY("Priority"),
    DURATION("Duration"),
    CREATION_DATE("Creation Date"),
    TITLE("Title (Alphabetical)")
}

enum class DeadlineFilter(val label: String) {
    ALL("All"),
    DUE_TODAY("Due Today"),
    OVERDUE("Overdue"),
    UPCOMING("Upcoming")
}

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val cloudManager = SupabaseCloudManager()
    val repository = TaskRepository(db.taskDao(), cloudManager)

    // User Session
    private val _currentUserId = MutableStateFlow("student_2026_demo")
    val currentUserId: StateFlow<String> = _currentUserId.asStateFlow()

    // All raw tasks from Room
    private val _allTasks = MutableStateFlow<List<Task>>(emptyList())
    val allTasks: StateFlow<List<Task>> = _allTasks.asStateFlow()

    // Filtering & Sorting State
    val searchQuery = MutableStateFlow("")
    val searchAlgorithm = MutableStateFlow(AlgorithmType.LINEAR_SEARCH)
    val selectedCategory = MutableStateFlow<TaskCategory?>(null)
    val selectedPriority = MutableStateFlow<TaskPriority?>(null)
    val selectedStatus = MutableStateFlow<TaskStatus?>(null)
    val selectedDeadlineFilter = MutableStateFlow(DeadlineFilter.ALL)

    val activeSortKey = MutableStateFlow(SortKey.DEADLINE)
    val activeSortAlgorithm = MutableStateFlow(AlgorithmType.MERGE_SORT)

    // Algorithm execution telemetry
    private val _lastSortResult = MutableStateFlow<AlgorithmResult?>(null)
    val lastSortResult: StateFlow<AlgorithmResult?> = _lastSortResult.asStateFlow()

    private val _lastSearchResult = MutableStateFlow<AlgorithmResult?>(null)
    val lastSearchResult: StateFlow<AlgorithmResult?> = _lastSearchResult.asStateFlow()

    // Smart Schedule State
    val availableStudyMinutes = MutableStateFlow(180) // 3 hours
    private val _scheduleReport = MutableStateFlow<ScheduleReport?>(null)
    val scheduleReport: StateFlow<ScheduleReport?> = _scheduleReport.asStateFlow()

    // Algorithm Lab State
    val labAlgorithm = MutableStateFlow(AlgorithmType.MERGE_SORT)
    val labDatasetSize = MutableStateFlow(25)
    private val _labResult = MutableStateFlow<AlgorithmResult?>(null)
    val labResult: StateFlow<AlgorithmResult?> = _labResult.asStateFlow()
    val isLabRunning = MutableStateFlow(false)

    // Cloud logs
    private val _syncLogs = MutableStateFlow<List<CloudSyncLog>>(emptyList())
    val syncLogs: StateFlow<List<CloudSyncLog>> = _syncLogs.asStateFlow()

    // Displayed Tasks (filtered and sorted)
    val displayedTasks: StateFlow<List<Task>> = combine(
        _allTasks,
        searchQuery,
        selectedCategory,
        selectedPriority,
        selectedStatus,
        selectedDeadlineFilter,
        activeSortKey,
        activeSortAlgorithm
    ) { params ->
        val rawList = params[0] as List<Task>
        val query = params[1] as String
        val cat = params[2] as TaskCategory?
        val prio = params[3] as TaskPriority?
        val status = params[4] as TaskStatus?
        val deadlineF = params[5] as DeadlineFilter
        val sortKey = params[6] as SortKey
        val sortAlgo = params[7] as AlgorithmType

        var filtered = rawList

        // Apply Search
        if (query.isNotBlank()) {
            filtered = if (searchAlgorithm.value == AlgorithmType.BINARY_SEARCH) {
                val (exactMatch, result) = SearchingAlgorithms.binarySearch(filtered, query)
                _lastSearchResult.value = result
                if (exactMatch != null) listOf(exactMatch) else emptyList()
            } else {
                val (matches, result) = SearchingAlgorithms.linearSearch(filtered, query)
                _lastSearchResult.value = result
                matches
            }
        }

        // Apply Category Filter
        if (cat != null) {
            filtered = filtered.filter { it.category == cat }
        }

        // Apply Priority Filter
        if (prio != null) {
            filtered = filtered.filter { it.priority == prio }
        }

        // Apply Status Filter
        if (status != null) {
            filtered = filtered.filter { it.status == status }
        }

        // Apply Deadline Filter
        filtered = when (deadlineF) {
            DeadlineFilter.ALL -> filtered
            DeadlineFilter.DUE_TODAY -> filtered.filter { it.isDueToday }
            DeadlineFilter.OVERDUE -> filtered.filter { it.isOverdue }
            DeadlineFilter.UPCOMING -> filtered.filter { !it.isOverdue && !it.isDueToday }
        }

        // Apply Sorting using selected DAA algorithm
        if (filtered.isNotEmpty()) {
            val (sorted, result) = when (sortKey) {
                SortKey.DEADLINE -> {
                    if (sortAlgo == AlgorithmType.INSERTION_SORT) {
                        SortingAlgorithms.insertionSort(filtered, { it.deadlineEpochMillis }, true)
                    } else {
                        SortingAlgorithms.mergeSort(filtered, { it.deadlineEpochMillis }, true)
                    }
                }
                SortKey.PRIORITY -> {
                    // Highest priority (3) first
                    if (sortAlgo == AlgorithmType.INSERTION_SORT) {
                        SortingAlgorithms.insertionSort(filtered, { it.priority.level }, false)
                    } else {
                        SortingAlgorithms.mergeSort(filtered, { it.priority.level }, false)
                    }
                }
                SortKey.DURATION -> {
                    if (sortAlgo == AlgorithmType.INSERTION_SORT) {
                        SortingAlgorithms.insertionSort(filtered, { it.estimatedDurationMinutes }, true)
                    } else {
                        SortingAlgorithms.mergeSort(filtered, { it.estimatedDurationMinutes }, true)
                    }
                }
                SortKey.CREATION_DATE -> {
                    if (sortAlgo == AlgorithmType.INSERTION_SORT) {
                        SortingAlgorithms.insertionSort(filtered, { it.createdAtEpochMillis }, false)
                    } else {
                        SortingAlgorithms.mergeSort(filtered, { it.createdAtEpochMillis }, false)
                    }
                }
                SortKey.TITLE -> {
                    if (sortAlgo == AlgorithmType.INSERTION_SORT) {
                        SortingAlgorithms.insertionSort(filtered, { it.title.lowercase() }, true)
                    } else {
                        SortingAlgorithms.mergeSort(filtered, { it.title.lowercase() }, true)
                    }
                }
            }
            _lastSortResult.value = result
            sorted
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadTasksForCurrentUser()
    }

    fun loadTasksForCurrentUser() {
        viewModelScope.launch {
            repository.seedInitialAcademicDemoTasksIfEmpty(_currentUserId.value)
            repository.getTasksStream(_currentUserId.value).collect { tasks ->
                _allTasks.value = tasks
                refreshSchedule()
                _syncLogs.value = cloudManager.syncLogs
            }
        }
    }

    fun switchUser(userId: String) {
        _currentUserId.value = userId
        cloudManager.activeUserId = userId
        loadTasksForCurrentUser()
    }

    fun addTask(
        title: String,
        description: String,
        category: TaskCategory,
        deadlineMillis: Long,
        priority: TaskPriority,
        durationMinutes: Int
    ) {
        viewModelScope.launch {
            val task = Task(
                title = title,
                description = description,
                category = category,
                deadlineEpochMillis = deadlineMillis,
                priority = priority,
                estimatedDurationMinutes = durationMinutes,
                userId = _currentUserId.value
            )
            repository.addTask(task)
            _syncLogs.value = cloudManager.syncLogs
            refreshSchedule()
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
            _syncLogs.value = cloudManager.syncLogs
            refreshSchedule()
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(task)
            _syncLogs.value = cloudManager.syncLogs
            refreshSchedule()
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
            _syncLogs.value = cloudManager.syncLogs
            refreshSchedule()
        }
    }

    fun refreshSchedule(minutes: Int = availableStudyMinutes.value) {
        availableStudyMinutes.value = minutes
        val report = GreedyScheduler.generateSchedule(_allTasks.value, minutes)
        _scheduleReport.value = report
    }

    fun triggerManualCloudSync() {
        viewModelScope.launch {
            val current = _allTasks.value
            for (task in current) {
                cloudManager.syncTaskToCloud(task)
            }
            _syncLogs.value = cloudManager.syncLogs
        }
    }

    fun runLabExperiment() {
        val size = labDatasetSize.value
        val algo = labAlgorithm.value
        isLabRunning.value = true

        viewModelScope.launch {
            val result = withContext(Dispatchers.Default) {
                val sampleTasks = (1..size).map { i ->
                    Task(
                        title = "Sample Academic Task #${(size - i + 1)}",
                        description = "Synthetic workload element $i for benchmarking.",
                        category = TaskCategory.values()[i % TaskCategory.values().size],
                        deadlineEpochMillis = System.currentTimeMillis() + (i * 3600_000L),
                        priority = TaskPriority.values()[i % TaskPriority.values().size],
                        estimatedDurationMinutes = 15 + (i * 7) % 180,
                        userId = "benchmark"
                    )
                }

                when (algo) {
                    AlgorithmType.INSERTION_SORT -> {
                        val (_, res) = SortingAlgorithms.insertionSort(sampleTasks, { it.estimatedDurationMinutes }, true)
                        res
                    }
                    AlgorithmType.MERGE_SORT -> {
                        val (_, res) = SortingAlgorithms.mergeSort(sampleTasks, { it.estimatedDurationMinutes }, true)
                        res
                    }
                    AlgorithmType.LINEAR_SEARCH -> {
                        val (_, res) = SearchingAlgorithms.linearSearch(sampleTasks, "Task #${size / 2}")
                        res
                    }
                    AlgorithmType.BINARY_SEARCH -> {
                        val (_, res) = SearchingAlgorithms.binarySearch(sampleTasks, "Sample Academic Task #${size / 2}")
                        res
                    }
                    AlgorithmType.GREEDY_SCHEDULER -> {
                        val start = System.nanoTime()
                        val sched = GreedyScheduler.generateSchedule(sampleTasks, 360)
                        val elapsed = System.nanoTime() - start
                        AlgorithmResult(
                            algorithm = AlgorithmType.GREEDY_SCHEDULER,
                            inputSize = size,
                            executionTimeNanos = elapsed,
                            comparisonsCount = size.toLong() * 2,
                            operationsCount = sched.scheduledSlots.size.toLong(),
                            complexity = com.example.model.ComplexityInfo(
                                bestCase = "Ω(n log n)",
                                averageCase = "Θ(n log n)",
                                worstCase = "O(n log n)",
                                spaceComplexity = "O(n)",
                                description = "Greedy Knapsack scheduling prioritizes tasks with highest value-to-cost ratio.",
                                academicRationale = "Approximation ratio bounds optimal schedule within polynomial time."
                            ),
                            executionTrace = listOf(
                                "Generated schedule for $size tasks in ${elapsed / 1000.0} µs.",
                                "Slots scheduled: ${sched.scheduledSlots.size}",
                                "Allocated: ${sched.totalAllocatedMinutes}m / 360m available.",
                                "Efficiency: ${"%.1f".format(sched.efficiencyPercentage)}%"
                            ),
                            explanation = "Greedy heuristic scheduled ${sched.scheduledSlots.size} tasks within time constraints."
                        )
                    }
                }
            }
            _labResult.value = result
            isLabRunning.value = false
        }
    }
}
