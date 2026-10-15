package com.example.model

data class ScheduledSlot(
    val slotIndex: Int,
    val task: Task,
    val startTimeFormatted: String,
    val endTimeFormatted: String,
    val durationMinutes: Int,
    val greedyScore: Double,
    val rationale: String
)

data class ScheduleReport(
    val availableMinutes: Int,
    val totalAllocatedMinutes: Int,
    val scheduledSlots: List<ScheduledSlot>,
    val unscheduledTasks: List<Task>,
    val efficiencyPercentage: Float,
    val greedyRuleExplanation: String,
    val limitationsNote: String
)
