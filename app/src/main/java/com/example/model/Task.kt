package com.example.model

enum class TaskCategory(val displayName: String) {
    ASSIGNMENT("Assignment"),
    PRACTICAL("Practical"),
    EXAM_PREPARATION("Exam Preparation"),
    PERSONAL("Personal"),
    OTHER("Other")
}

enum class TaskPriority(val level: Int, val label: String) {
    HIGH(3, "High Priority"),
    MEDIUM(2, "Medium Priority"),
    LOW(1, "Low Priority")
}

enum class TaskStatus(val label: String) {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed")
}

enum class SyncState {
    SYNCED,
    PENDING_UPLOAD,
    OFFLINE
}

data class Task(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val category: TaskCategory = TaskCategory.ASSIGNMENT,
    val deadlineEpochMillis: Long,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val estimatedDurationMinutes: Int = 60,
    val status: TaskStatus = TaskStatus.PENDING,
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val syncState: SyncState = SyncState.SYNCED,
    val userId: String = "student_2026_demo"
) {
    val isOverdue: Boolean
        get() = status != TaskStatus.COMPLETED && deadlineEpochMillis < System.currentTimeMillis()

    val isDueToday: Boolean
        get() {
            val now = System.currentTimeMillis()
            val oneDayMillis = 24 * 60 * 60 * 1000L
            return status != TaskStatus.COMPLETED &&
                    deadlineEpochMillis in now..(now + oneDayMillis)
        }

    val daysUntilDeadline: Long
        get() {
            val diff = deadlineEpochMillis - System.currentTimeMillis()
            return if (diff < 0) 0 else diff / (1000 * 60 * 60 * 24)
        }
}
