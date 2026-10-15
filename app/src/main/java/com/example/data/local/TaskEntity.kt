package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.SyncState
import com.example.model.Task
import com.example.model.TaskCategory
import com.example.model.TaskPriority
import com.example.model.TaskStatus

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val deadlineEpochMillis: Long,
    val priorityLevel: Int,
    val estimatedDurationMinutes: Int,
    val status: String,
    val createdAtEpochMillis: Long,
    val syncState: String,
    val userId: String
) {
    fun toDomain(): Task {
        val cat = try {
            TaskCategory.valueOf(category)
        } catch (e: Exception) {
            TaskCategory.ASSIGNMENT
        }
        val prio = when (priorityLevel) {
            3 -> TaskPriority.HIGH
            2 -> TaskPriority.MEDIUM
            else -> TaskPriority.LOW
        }
        val stat = try {
            TaskStatus.valueOf(status)
        } catch (e: Exception) {
            TaskStatus.PENDING
        }
        val sync = try {
            SyncState.valueOf(syncState)
        } catch (e: Exception) {
            SyncState.SYNCED
        }

        return Task(
            id = id,
            title = title,
            description = description,
            category = cat,
            deadlineEpochMillis = deadlineEpochMillis,
            priority = prio,
            estimatedDurationMinutes = estimatedDurationMinutes,
            status = stat,
            createdAtEpochMillis = createdAtEpochMillis,
            syncState = sync,
            userId = userId
        )
    }

    companion object {
        fun fromDomain(task: Task): TaskEntity {
            return TaskEntity(
                id = task.id,
                title = task.title,
                description = task.description,
                category = task.category.name,
                deadlineEpochMillis = task.deadlineEpochMillis,
                priorityLevel = task.priority.level,
                estimatedDurationMinutes = task.estimatedDurationMinutes,
                status = task.status.name,
                createdAtEpochMillis = task.createdAtEpochMillis,
                syncState = task.syncState.name,
                userId = task.userId
            )
        }
    }
}
