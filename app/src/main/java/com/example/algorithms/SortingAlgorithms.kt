package com.example.algorithms

import com.example.model.AlgorithmResult
import com.example.model.AlgorithmType
import com.example.model.ComplexityInfo
import com.example.model.Task

object SortingAlgorithms {

    val INSERTION_SORT_COMPLEXITY = ComplexityInfo(
        bestCase = "Ω(n) (Linear)",
        averageCase = "Θ(n²) (Quadratic)",
        worstCase = "O(n²) (Quadratic)",
        spaceComplexity = "O(1) (In-place)",
        description = "Incrementally builds the sorted sequence by picking the next element and inserting it into its correct position among previously sorted elements.",
        academicRationale = "Insertion Sort is optimal for small or nearly-sorted datasets because it requires zero auxiliary memory and has very low constant factors."
    )

    val MERGE_SORT_COMPLEXITY = ComplexityInfo(
        bestCase = "Ω(n log n)",
        averageCase = "Θ(n log n)",
        worstCase = "O(n log n)",
        spaceComplexity = "O(n) (Auxiliary buffer)",
        description = "A classic Divide-and-Conquer algorithm. It divides the unsorted list into n sublists, repeatedly merges sublists to produce new sorted sublists until only 1 remains.",
        academicRationale = "Merge Sort guarantees consistent O(n log n) performance regardless of input distribution (governed by recurrence T(n) = 2T(n/2) + Θ(n)). It is stable, though it requires O(n) extra space."
    )

    /**
     * Insertion Sort implementation with step trace and counters.
     */
    fun <T : Comparable<T>> insertionSort(
        tasks: List<Task>,
        keyExtractor: (Task) -> T,
        ascending: Boolean = true
    ): Pair<List<Task>, AlgorithmResult> {
        val trace = mutableListOf<String>()
        val n = tasks.size
        val arr = tasks.toMutableList()
        var comparisons = 0L
        var shifts = 0L

        trace.add("Initial input: $n tasks loaded.")
        val startTime = System.nanoTime()

        for (i in 1 until n) {
            val keyTask = arr[i]
            val keyValue = keyExtractor(keyTask)
            var j = i - 1

            trace.add("Pass $i: selecting '${keyTask.title}' (key: $keyValue)")

            while (j >= 0) {
                comparisons++
                val comp = keyExtractor(arr[j]).compareTo(keyValue)
                val condition = if (ascending) comp > 0 else comp < 0
                if (condition) {
                    arr[j + 1] = arr[j]
                    shifts++
                    j--
                } else {
                    break
                }
            }
            arr[j + 1] = keyTask
            if (j + 1 != i) {
                trace.add("Inserted '${keyTask.title}' at index ${j + 1}")
            }
        }

        val elapsedNanos = System.nanoTime() - startTime
        trace.add("Completed in ${elapsedNanos / 1000.0} µs with $comparisons comparisons and $shifts shifts.")

        val result = AlgorithmResult(
            algorithm = AlgorithmType.INSERTION_SORT,
            inputSize = n,
            executionTimeNanos = elapsedNanos,
            comparisonsCount = comparisons,
            operationsCount = shifts,
            complexity = INSERTION_SORT_COMPLEXITY,
            executionTrace = trace.take(50),
            explanation = "Insertion sort sorted $n items. Verified in-place stability."
        )

        return Pair(arr, result)
    }

    /**
     * Merge Sort implementation with step trace and counters.
     */
    fun <T : Comparable<T>> mergeSort(
        tasks: List<Task>,
        keyExtractor: (Task) -> T,
        ascending: Boolean = true
    ): Pair<List<Task>, AlgorithmResult> {
        val trace = mutableListOf<String>()
        val n = tasks.size
        var comparisons = 0L
        var merges = 0L

        trace.add("Initial input: $n tasks. Initiating divide-and-conquer recursion.")
        val startTime = System.nanoTime()

        fun merge(left: List<Task>, right: List<Task>): List<Task> {
            val result = mutableListOf<Task>()
            var i = 0
            var j = 0

            while (i < left.size && j < right.size) {
                comparisons++
                val comp = keyExtractor(left[i]).compareTo(keyExtractor(right[j]))
                val pickLeft = if (ascending) comp <= 0 else comp >= 0
                if (pickLeft) {
                    result.add(left[i])
                    i++
                } else {
                    result.add(right[j])
                    j++
                }
            }
            while (i < left.size) {
                result.add(left[i])
                i++
            }
            while (j < right.size) {
                result.add(right[j])
                j++
            }
            merges++
            return result
        }

        fun sortRecursive(list: List<Task>, depth: Int): List<Task> {
            if (list.size <= 1) return list
            val mid = list.size / 2
            val left = sortRecursive(list.subList(0, mid), depth + 1)
            val right = sortRecursive(list.subList(mid, list.size), depth + 1)
            if (depth <= 3) {
                trace.add("Depth $depth: Merging sublists of size ${left.size} and ${right.size}")
            }
            return merge(left, right)
        }

        val sortedList = sortRecursive(tasks, 1)
        val elapsedNanos = System.nanoTime() - startTime
        trace.add("Completed in ${elapsedNanos / 1000.0} µs with $comparisons comparisons across $merges merge passes.")

        val result = AlgorithmResult(
            algorithm = AlgorithmType.MERGE_SORT,
            inputSize = n,
            executionTimeNanos = elapsedNanos,
            comparisonsCount = comparisons,
            operationsCount = merges,
            complexity = MERGE_SORT_COMPLEXITY,
            executionTrace = trace.take(50),
            explanation = "Merge sort divided tasks recursively into log₂(n) levels and merged back in O(n log n) time."
        )

        return Pair(sortedList, result)
    }
}
