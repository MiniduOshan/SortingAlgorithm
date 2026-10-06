package com.sortingapp.performance;

import com.sortingapp.model.SortingResult;
import com.sortingapp.sorting.SortFactory;
import com.sortingapp.sorting.SortingAlgorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Evaluates and compares performance across multiple sorting algorithms.
 * Identifies the best-performing algorithm based on shortest execution time.
 * 
 * Group Assignment Contribution:
 * Member 3: Performance evaluation and reporting.
 */
public class PerformanceEvaluator {

    /**
     * Evaluates all implemented sorting algorithms on the provided dataset.
     * 
     * @param data Raw numerical data array extracted from dataset column.
     * @param ascending True for ascending sort order, false for descending.
     * @return EvaluationSummary containing results for all algorithms and winner details.
     */
    public static EvaluationSummary evaluateAll(double[] data, boolean ascending) {
        List<SortingAlgorithm> algorithms = SortFactory.getAllAlgorithms();
        List<SortingResult> results = new ArrayList<>();

        // Warm up JIT compiler with small dummy array to standardize benchmark
        warmupJIT(data, ascending);

        for (SortingAlgorithm algo : algorithms) {
            SortingResult result = algo.sort(data, ascending);
            results.add(result);
        }

        // Find the best-performing algorithm (minimum execution time)
        SortingResult bestResult = results.stream()
                .min(Comparator.comparingLong(SortingResult::getExecutionTimeNs))
                .orElse(null);

        return new EvaluationSummary(results, bestResult);
    }

    /**
     * Evaluates a single algorithm on the dataset.
     */
    public static SortingResult evaluateSingle(SortingAlgorithm algorithm, double[] data, boolean ascending) {
        return algorithm.sort(data, ascending);
    }

    private static void warmupJIT(double[] data, boolean ascending) {
        if (data.length == 0) return;
        double[] dummy = new double[Math.min(data.length, 50)];
        System.arraycopy(data, 0, dummy, 0, dummy.length);

        for (SortingAlgorithm algo : SortFactory.getAllAlgorithms()) {
            algo.sort(dummy, ascending);
        }
    }

    /**
     * Class holding the collection of results and identifying the top performer.
     */
    public static class EvaluationSummary {
        private final List<SortingResult> results;
        private final SortingResult bestResult;

        public EvaluationSummary(List<SortingResult> results, SortingResult bestResult) {
            this.results = results;
            this.bestResult = bestResult;
        }

        public List<SortingResult> getResults() {
            return results;
        }

        public SortingResult getBestResult() {
            return bestResult;
        }

        public String getSummaryText() {
            if (bestResult == null) return "No results available.";
            StringBuilder sb = new StringBuilder();
            sb.append("🏆 BEST PERFORMING ALGORITHM: ").append(bestResult.getAlgorithmName()).append("\n");
            sb.append("⚡ Execution Time: ").append(bestResult.getFormattedExecutionTime()).append("\n");
            sb.append("📊 Elements Sorted: ").append(bestResult.getItemCount()).append("\n\n");
            sb.append("Full Algorithm Comparison:\n");
            sb.append("--------------------------------------------------\n");
            for (SortingResult r : results) {
                double speedRatio = (double) r.getExecutionTimeNs() / Math.max(1, bestResult.getExecutionTimeNs());
                sb.append(String.format("- %-15s: %10s | %d comparisons | %.2fx baseline\n",
                        r.getAlgorithmName(), r.getFormattedExecutionTime(), r.getComparisonCount(), speedRatio));
            }
            return sb.toString();
        }
    }
}
