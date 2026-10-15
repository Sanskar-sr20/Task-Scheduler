package com.example.algorithms

import com.example.model.AlgorithmResult
import com.example.model.AlgorithmType
import com.example.model.ComplexityInfo
import com.example.model.Task

object SearchingAlgorithms {

    val LINEAR_SEARCH_COMPLEXITY = ComplexityInfo(
        bestCase = "Ω(1) (Found at first element)",
        averageCase = "Θ(n) (Average n/2 comparisons)",
        worstCase = "O(n) (At end or not found)",
        spaceComplexity = "O(1) (Auxiliary memory)",
        description = "Sequentially checks each element of the list until a match is found or the whole list has been searched.",
        academicRationale = "Linear Search works on unordered datasets with no preprocessing overhead, making it preferred when data is small or unsorted."
    )

    val BINARY_SEARCH_COMPLEXITY = ComplexityInfo(
        bestCase = "Ω(1) (Target is mid element)",
        averageCase = "Θ(log n) (Logarithmic halving)",
        worstCase = "O(log n) (Logarithmic height)",
        spaceComplexity = "O(1) (Iterative)",
        description = "Repeatedly targets the middle element of a sorted sequence, eliminating half of the search interval each step.",
        academicRationale = "Binary Search delivers extremely fast O(log n) lookup, but requires the invariant that data is pre-sorted (O(n log n) setup cost)."
    )

    /**
     * Linear Search by title or description substring with step counters.
     */
    fun linearSearch(
        tasks: List<Task>,
        query: String
    ): Pair<List<Task>, AlgorithmResult> {
        val trace = mutableListOf<String>()
        val matches = mutableListOf<Task>()
        val n = tasks.size
        var comparisons = 0L
        val cleanQuery = query.trim().lowercase()

        val startTime = System.nanoTime()
        trace.add("Starting Linear Search for '$query' across $n tasks.")

        for (i in 0 until n) {
            comparisons++
            val task = tasks[i]
            val matchesTitle = task.title.lowercase().contains(cleanQuery)
            val matchesDesc = task.description.lowercase().contains(cleanQuery)

            if (matchesTitle || matchesDesc) {
                matches.add(task)
                trace.add("Match found at index $i: '${task.title}'")
            }
        }

        val elapsedNanos = System.nanoTime() - startTime
        trace.add("Completed scan of all $n elements in ${elapsedNanos / 1000.0} µs. Found ${matches.size} matches.")

        val result = AlgorithmResult(
            algorithm = AlgorithmType.LINEAR_SEARCH,
            inputSize = n,
            executionTimeNanos = elapsedNanos,
            comparisonsCount = comparisons,
            operationsCount = comparisons,
            complexity = LINEAR_SEARCH_COMPLEXITY,
            executionTrace = trace.take(50),
            explanation = "Linear Search examined all $n elements sequentially in O(n) time."
        )

        return Pair(matches, result)
    }

    /**
     * Binary Search on sorted tasks by title.
     * Enforces pre-sorting prerequisite and tracks search intervals.
     */
    fun binarySearch(
        tasks: List<Task>,
        targetExactTitle: String
    ): Pair<Task?, AlgorithmResult> {
        val trace = mutableListOf<String>()
        val n = tasks.size
        var comparisons = 0L
        var iterations = 0L
        val target = targetExactTitle.trim().lowercase()

        val startTime = System.nanoTime()
        trace.add("Binary Search initiated for '$targetExactTitle'. Checking sorted invariant...")

        // Verify sorted invariant
        var isSorted = true
        for (i in 0 until n - 1) {
            if (tasks[i].title.lowercase() > tasks[i + 1].title.lowercase()) {
                isSorted = false
                break
            }
        }

        val workingList: List<Task>
        if (!isSorted) {
            trace.add("Warning: Input was unsorted! Applying prerequisite Merge Sort first.")
            val (sorted, _) = SortingAlgorithms.mergeSort(tasks, { it.title.lowercase() }, true)
            workingList = sorted
        } else {
            trace.add("Invariant validated: Input tasks are already sorted by title.")
            workingList = tasks
        }

        var low = 0
        var high = workingList.size - 1
        var foundTask: Task? = null

        while (low <= high) {
            iterations++
            val mid = low + (high - low) / 2
            val midTask = workingList[mid]
            val midTitle = midTask.title.lowercase()

            trace.add("Iteration $iterations: window [$low..$high], mid index $mid ('${midTask.title}')")

            comparisons++
            val cmp = midTitle.compareTo(target)

            if (cmp == 0) {
                foundTask = midTask
                trace.add("SUCCESS: Exact match found at index $mid ('${midTask.title}')")
                break
            } else if (cmp < 0) {
                trace.add("Target '$target' is greater than '$midTitle' -> discard left half")
                low = mid + 1
            } else {
                trace.add("Target '$target' is less than '$midTitle' -> discard right half")
                high = mid - 1
            }
        }

        val elapsedNanos = System.nanoTime() - startTime
        if (foundTask == null) {
            trace.add("Binary search exhausted without finding exact match '$targetExactTitle'.")
        }

        val result = AlgorithmResult(
            algorithm = AlgorithmType.BINARY_SEARCH,
            inputSize = n,
            executionTimeNanos = elapsedNanos,
            comparisonsCount = comparisons,
            operationsCount = iterations,
            complexity = BINARY_SEARCH_COMPLEXITY,
            executionTrace = trace.take(50),
            explanation = if (foundTask != null) {
                "Target located in $iterations iterations using binary search O(log n)."
            } else {
                "Search concluded in $iterations iterations without a match."
            }
        )

        return Pair(foundTask, result)
    }
}
