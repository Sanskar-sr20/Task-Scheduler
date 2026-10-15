package com.example

import com.example.algorithms.GreedyScheduler
import com.example.algorithms.SearchingAlgorithms
import com.example.algorithms.SortingAlgorithms
import com.example.model.Task
import com.example.model.TaskCategory
import com.example.model.TaskPriority
import com.example.model.TaskStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    private fun createSampleTasks(): List<Task> {
        val now = System.currentTimeMillis()
        return listOf(
            Task(title = "Compiler Practical", priority = TaskPriority.MEDIUM, estimatedDurationMinutes = 60, deadlineEpochMillis = now + 86400000L),
            Task(title = "Algorithms Assignment", priority = TaskPriority.HIGH, estimatedDurationMinutes = 90, deadlineEpochMillis = now + 43200000L),
            Task(title = "Database Exam Prep", priority = TaskPriority.HIGH, estimatedDurationMinutes = 45, deadlineEpochMillis = now + 120000000L),
            Task(title = "Hostel Clearance", priority = TaskPriority.LOW, estimatedDurationMinutes = 30, deadlineEpochMillis = now + 200000000L)
        )
    }

    @Test
    fun testInsertionSortAscendingDuration() {
        val tasks = createSampleTasks()
        val (sorted, result) = SortingAlgorithms.insertionSort(tasks, { it.estimatedDurationMinutes }, true)

        assertEquals(4, sorted.size)
        assertEquals(30, sorted[0].estimatedDurationMinutes)
        assertEquals(45, sorted[1].estimatedDurationMinutes)
        assertEquals(60, sorted[2].estimatedDurationMinutes)
        assertEquals(90, sorted[3].estimatedDurationMinutes)
        assertTrue(result.comparisonsCount > 0)
    }

    @Test
    fun testMergeSortDescendingPriority() {
        val tasks = createSampleTasks()
        val (sorted, result) = SortingAlgorithms.mergeSort(tasks, { it.priority.level }, false)

        assertEquals(4, sorted.size)
        assertTrue(sorted[0].priority.level >= sorted[1].priority.level)
        assertTrue(sorted[1].priority.level >= sorted[2].priority.level)
        assertTrue(sorted[2].priority.level >= sorted[3].priority.level)
        assertEquals("Merge Sort", result.algorithm.displayName)
    }

    @Test
    fun testLinearSearch() {
        val tasks = createSampleTasks()
        val (matches, result) = SearchingAlgorithms.linearSearch(tasks, "Algorithms")

        assertEquals(1, matches.size)
        assertEquals("Algorithms Assignment", matches[0].title)
        assertEquals(4L, result.comparisonsCount)
    }

    @Test
    fun testBinarySearchWithAutomaticSorting() {
        val tasks = createSampleTasks() // Unsorted titles: Compiler, Algorithms, Database, Hostel
        val (found, result) = SearchingAlgorithms.binarySearch(tasks, "Database Exam Prep")

        assertNotNull(found)
        assertEquals("Database Exam Prep", found?.title)
        assertTrue(result.operationsCount > 0)
    }

    @Test
    fun testBinarySearchNotFound() {
        val tasks = createSampleTasks()
        val (found, _) = SearchingAlgorithms.binarySearch(tasks, "Non-existent Task")
        assertNull(found)
    }

    @Test
    fun testGreedySchedulerRespectsCapacity() {
        val tasks = createSampleTasks()
        // Available budget: 120 minutes
        val report = GreedyScheduler.generateSchedule(tasks, 120)

        assertTrue("Allocated time cannot exceed available budget", report.totalAllocatedMinutes <= 120)
        assertTrue(report.scheduledSlots.isNotEmpty())
        // Top density task (High priority, shorter duration or closer deadline) should be scheduled
        val scheduledTitles = report.scheduledSlots.map { it.task.title }
        assertTrue(scheduledTitles.contains("Algorithms Assignment") || scheduledTitles.contains("Database Exam Prep"))
    }

    @Test
    fun testGreedySchedulerEdgeCases() {
        // Edge Case 1: Empty list
        val emptyReport = GreedyScheduler.generateSchedule(emptyList(), 180)
        assertEquals(0, emptyReport.totalAllocatedMinutes)
        assertTrue(emptyReport.scheduledSlots.isEmpty())

        // Edge Case 2: Zero available time
        val tasks = createSampleTasks()
        val zeroReport = GreedyScheduler.generateSchedule(tasks, 0)
        assertEquals(0, zeroReport.totalAllocatedMinutes)
        assertEquals(4, zeroReport.unscheduledTasks.size)

        // Edge Case 3: Available time smaller than smallest task (30 mins)
        val tightReport = GreedyScheduler.generateSchedule(tasks, 20)
        assertEquals(0, tightReport.totalAllocatedMinutes)
        assertEquals(4, tightReport.unscheduledTasks.size)
    }
}
