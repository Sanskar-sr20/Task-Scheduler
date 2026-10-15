package com.example.algorithms

import com.example.model.ScheduleReport
import com.example.model.ScheduledSlot
import com.example.model.Task
import com.example.model.TaskStatus
import java.util.Calendar
import java.util.Locale

object GreedyScheduler {

    const val GREEDY_RULE_EXPLANATION =
        "The Greedy Choice Property ranks tasks by a composite Value-to-Duration Density ratio: " +
        "R(i) = [PriorityWeight(i) × (1 + UrgencyScore(i))] / Duration(i). " +
        "Tasks with the highest benefit-per-minute are greedily selected first until daily study time is exhausted."

    const val LIMITATIONS_NOTE =
        "Academic Limitation: Because tasks cannot be arbitrarily partitioned (0/1 Knapsack constraint), " +
        "a greedy strategy is an approximation heuristic O(n log n). While it solves Fractional Knapsack optimally, " +
        "discrete 0/1 Knapsack optimal solution requires Dynamic Programming O(n × W) or Branch-and-Bound."

    /**
     * Generates an optimal daily schedule based on available study time (in minutes).
     */
    fun generateSchedule(
        tasks: List<Task>,
        availableMinutes: Int,
        startHour: Int = 9,
        startMinute: Int = 0
    ): ScheduleReport {
        // Exclude already completed tasks
        val pendingTasks = tasks.filter { it.status != TaskStatus.COMPLETED }

        if (pendingTasks.isEmpty() || availableMinutes <= 0) {
            return ScheduleReport(
                availableMinutes = availableMinutes,
                totalAllocatedMinutes = 0,
                scheduledSlots = emptyList(),
                unscheduledTasks = pendingTasks,
                efficiencyPercentage = 0f,
                greedyRuleExplanation = GREEDY_RULE_EXPLANATION,
                limitationsNote = LIMITATIONS_NOTE
            )
        }

        val nowEpoch = System.currentTimeMillis()

        // Calculate greedy score for each task
        data class ScoredTask(val task: Task, val score: Double, val rationale: String)

        val scoredTasks = pendingTasks.map { task ->
            val priorityWeight = task.priority.level.toDouble() // High=3, Med=2, Low=1
            val hoursRemaining = (task.deadlineEpochMillis - nowEpoch).toDouble() / (1000.0 * 60 * 60)
            val daysRemaining = Math.max(0.1, hoursRemaining / 24.0)

            // Overdue tasks get an urgency multiplier
            val urgencyMultiplier = when {
                hoursRemaining < 0 -> 4.0
                hoursRemaining <= 24 -> 2.5
                hoursRemaining <= 72 -> 1.5
                else -> 1.0 / Math.log10(daysRemaining + 9)
            }

            // Greedy density: value / cost
            val duration = Math.max(15, task.estimatedDurationMinutes).toDouble()
            val rawScore = ((priorityWeight * 100.0) * urgencyMultiplier) / duration

            val rationale = "Priority=${task.priority.name} (w=$priorityWeight), " +
                    "UrgencyMult=%.1fx, Dur=${task.estimatedDurationMinutes}m".format(Locale.US, urgencyMultiplier)

            ScoredTask(task, rawScore, rationale)
        }

        // Sort descending by greedy score
        val sortedScored = scoredTasks.sortedByDescending { it.score }

        val scheduledSlots = mutableListOf<ScheduledSlot>()
        val unscheduled = mutableListOf<Task>()
        var remainingCapacity = availableMinutes
        var currentSlotMinutesFromStart = 0

        // Calendar for slot time calculation
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, startHour)
            set(Calendar.MINUTE, startMinute)
            set(Calendar.SECOND, 0)
        }

        var slotIndex = 1
        for (item in sortedScored) {
            val task = item.task
            val taskDuration = Math.max(15, task.estimatedDurationMinutes)

            if (taskDuration <= remainingCapacity) {
                // Task fits within remaining time budget
                val startCal = cal.clone() as Calendar
                startCal.add(Calendar.MINUTE, currentSlotMinutesFromStart)

                val endCal = startCal.clone() as Calendar
                endCal.add(Calendar.MINUTE, taskDuration)

                val startTimeStr = "%02d:%02d".format(
                    startCal.get(Calendar.HOUR_OF_DAY),
                    startCal.get(Calendar.MINUTE)
                )
                val endTimeStr = "%02d:%02d".format(
                    endCal.get(Calendar.HOUR_OF_DAY),
                    endCal.get(Calendar.MINUTE)
                )

                scheduledSlots.add(
                    ScheduledSlot(
                        slotIndex = slotIndex++,
                        task = task,
                        startTimeFormatted = startTimeStr,
                        endTimeFormatted = endTimeStr,
                        durationMinutes = taskDuration,
                        greedyScore = item.score,
                        rationale = item.rationale
                    )
                )

                remainingCapacity -= taskDuration
                // Add 10 minutes break between tasks if more than 30 mins capacity left
                val breakTime = if (remainingCapacity >= 40) 10 else 0
                currentSlotMinutesFromStart += (taskDuration + breakTime)
            } else {
                unscheduled.add(task)
            }
        }

        val allocated = availableMinutes - remainingCapacity
        val efficiency = if (availableMinutes > 0) (allocated.toFloat() / availableMinutes) * 100f else 0f

        return ScheduleReport(
            availableMinutes = availableMinutes,
            totalAllocatedMinutes = allocated,
            scheduledSlots = scheduledSlots,
            unscheduledTasks = unscheduled,
            efficiencyPercentage = efficiency,
            greedyRuleExplanation = GREEDY_RULE_EXPLANATION,
            limitationsNote = LIMITATIONS_NOTE
        )
    }
}
