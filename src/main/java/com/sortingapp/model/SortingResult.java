package com.sortingapp.model;

/**
 * Encapsulates the evaluation metrics and outcome of a sorting algorithm execution.
 * 
 * Group Assignment Contribution:
 * Member 1 / Member 3: Dataset handling & Performance evaluation data model.
 */
public class SortingResult {
    private final String algorithmName;
    private final double[] sortedData;
    private final long executionTimeNs;
    private final long comparisonCount;
    private final long swapCount;
    private final boolean isAscending;
    private final boolean isSortedCorrectly;

    public SortingResult(String algorithmName, double[] sortedData, long executionTimeNs,
                         long comparisonCount, long swapCount, boolean isAscending, boolean isSortedCorrectly) {
        this.algorithmName = algorithmName;
        this.sortedData = sortedData;
        this.executionTimeNs = executionTimeNs;
        this.comparisonCount = comparisonCount;
        this.swapCount = swapCount;
        this.isAscending = isAscending;
        this.isSortedCorrectly = isSortedCorrectly;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public double[] getSortedData() {
        return sortedData;
    }

    public long getExecutionTimeNs() {
        return executionTimeNs;
    }

    /**
     * Returns execution time formatted in milliseconds with 3 decimal places.
     */
    public double getExecutionTimeMs() {
        return executionTimeNs / 1_000_000.0;
    }

    public String getFormattedExecutionTime() {
        if (executionTimeNs < 1_000) {
            return executionTimeNs + " ns";
        } else if (executionTimeNs < 1_000_000) {
            return String.format("%.2f µs", executionTimeNs / 1_000.0);
        } else {
            return String.format("%.3f ms", executionTimeNs / 1_000_000.0);
        }
    }

    public long getComparisonCount() {
        return comparisonCount;
    }

    public long getSwapCount() {
        return swapCount;
    }

    public boolean isAscending() {
        return isAscending;
    }

    public boolean isSortedCorrectly() {
        return isSortedCorrectly;
    }

    public int getItemCount() {
        return sortedData != null ? sortedData.length : 0;
    }
}
