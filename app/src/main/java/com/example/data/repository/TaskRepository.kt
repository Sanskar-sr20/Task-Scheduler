package com.example.data.repository

import com.example.data.cloud.SupabaseCloudManager
import com.example.data.local.TaskDao
import com.example.data.local.TaskEntity
import com.example.model.SyncState
import com.example.model.Task
import com.example.model.TaskCategory
import com.example.model.TaskPriority
import com.example.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepository(
    private val taskDao: TaskDao,
    val cloudManager: SupabaseCloudManager
) {

    fun getTasksStream(userId: String): Flow<List<Task>> {
        return taskDao.getTasksByUser(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun addTask(task: Task) {
        val entity = TaskEntity.fromDomain(task.copy(syncState = SyncState.PENDING_UPLOAD))
        taskDao.insertTask(entity)

        // Attempt cloud sync asynchronously
        val cloudResult = cloudManager.syncTaskToCloud(task)
        if (cloudResult.isSuccess) {
            taskDao.insertTask(TaskEntity.fromDomain(task.copy(syncState = SyncState.SYNCED)))
        }
    }

    suspend fun updateTask(task: Task) {
        val entity = TaskEntity.fromDomain(task.copy(syncState = SyncState.PENDING_UPLOAD))
        taskDao.updateTask(entity)

        val cloudResult = cloudManager.syncTaskToCloud(task)
        if (cloudResult.isSuccess) {
            taskDao.updateTask(TaskEntity.fromDomain(task.copy(syncState = SyncState.SYNCED)))
        }
    }

    suspend fun deleteTask(taskId: String) {
        taskDao.deleteById(taskId)
        cloudManager.deleteTaskFromCloud(taskId)
    }

    suspend fun toggleTaskCompletion(task: Task) {
        val newStatus = if (task.status == TaskStatus.COMPLETED) TaskStatus.PENDING else TaskStatus.COMPLETED
        updateTask(task.copy(status = newStatus))
    }

    suspend fun seedInitialAcademicDemoTasksIfEmpty(userId: String) {
        val now = System.currentTimeMillis()
        val oneHour = 60 * 60 * 1000L
        val oneDay = 24 * oneHour

        val defaultTasks = listOf(
            Task(
                title = "DAA Lab Assignment 4: Greedy Scheduling",
                description = "Implement Fractional Knapsack and Job Sequencing with Deadlines in C++/Python.",
                category = TaskCategory.ASSIGNMENT,
                deadlineEpochMillis = now + 6 * oneHour, // Due today!
                priority = TaskPriority.HIGH,
                estimatedDurationMinutes = 90,
                status = TaskStatus.PENDING,
                userId = userId
            ),
            Task(
                title = "OS Practical: Process Synchronization Mutex",
                description = "Write Peterson's Algorithm and Semaphore simulation in Linux.",
                category = TaskCategory.PRACTICAL,
                deadlineEpochMillis = now + 18 * oneHour,
                priority = TaskPriority.HIGH,
                estimatedDurationMinutes = 60,
                status = TaskStatus.PENDING,
                userId = userId
            ),
            Task(
                title = "DCC Mini-Project Submission: Supabase RLS",
                description = "Configure Row Level Security policies and test cross-user isolation.",
                category = TaskCategory.ASSIGNMENT,
                deadlineEpochMillis = now + 2 * oneDay,
                priority = TaskPriority.MEDIUM,
                estimatedDurationMinutes = 120,
                status = TaskStatus.PENDING,
                userId = userId
            ),
            Task(
                title = "Algorithms Exam Prep: Master Theorem",
                description = "Revise Divide and Conquer recurrences: T(n) = aT(n/b) + f(n) case analysis.",
                category = TaskCategory.EXAM_PREPARATION,
                deadlineEpochMillis = now + 3 * oneDay,
                priority = TaskPriority.HIGH,
                estimatedDurationMinutes = 75,
                status = TaskStatus.PENDING,
                userId = userId
            ),
            Task(
                title = "Computer Networks: Socket Programming Practical",
                description = "Multi-client TCP chat server using select() and threading.",
                category = TaskCategory.PRACTICAL,
                deadlineEpochMillis = now + 4 * oneDay,
                priority = TaskPriority.MEDIUM,
                estimatedDurationMinutes = 60,
                status = TaskStatus.PENDING,
                userId = userId
            ),
            Task(
                title = "Hostel Fee Payment & Library Return",
                description = "Submit clearance slip before semester examination.",
                category = TaskCategory.PERSONAL,
                deadlineEpochMillis = now + 5 * oneDay,
                priority = TaskPriority.LOW,
                estimatedDurationMinutes = 30,
                status = TaskStatus.COMPLETED,
                userId = userId
            )
        )

        for (task in defaultTasks) {
            taskDao.insertTask(TaskEntity.fromDomain(task))
        }
    }
}
