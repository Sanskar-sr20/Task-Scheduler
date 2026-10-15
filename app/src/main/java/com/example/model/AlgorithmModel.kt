package com.example.model

enum class AlgorithmType(val displayName: String, val category: String) {
    INSERTION_SORT("Insertion Sort", "Sorting"),
    MERGE_SORT("Merge Sort", "Sorting"),
    LINEAR_SEARCH("Linear Search", "Searching"),
    BINARY_SEARCH("Binary Search", "Searching"),
    GREEDY_SCHEDULER("Greedy Interval/Knapsack", "Greedy Paradigm")
}

data class ComplexityInfo(
    val bestCase: String,
    val averageCase: String,
    val worstCase: String,
    val spaceComplexity: String,
    val description: String,
    val academicRationale: String
)

data class AlgorithmResult(
    val algorithm: AlgorithmType,
    val inputSize: Int,
    val executionTimeNanos: Long,
    val comparisonsCount: Long,
    val operationsCount: Long, // swaps or iterations
    val complexity: ComplexityInfo,
    val executionTrace: List<String>,
    val explanation: String
) {
    val executionTimeMicros: Double
        get() = executionTimeNanos / 1_000.0

    val executionTimeMillis: Double
        get() = executionTimeNanos / 1_000_000.0
}
