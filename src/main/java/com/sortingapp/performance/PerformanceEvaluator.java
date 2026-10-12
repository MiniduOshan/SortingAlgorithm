package com.sortingapp.performance;

import com.sortingapp.model.SortingResult;
import com.sortingapp.sorting.SortFactory;
import com.sortingapp.sorting.SortingAlgorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Evaluates and compares performance across all required sorting algorithms.
 *
 * Benchmarking uses several timed runs for each algorithm and keeps the median
 * run. This reduces the effect of short-lived JVM/OS timing fluctuations while
 * still using the execution time measured inside each sorting implementation.
 *
 * Group Assignment Contribution:
 * Member 3: Performance evaluation and reporting.
 */
public class PerformanceEvaluator {

    /**
     * Use an odd number so a true middle (median) result always exists.
     */
    private static final int BENCHMARK_RUNS = 5;

    /**
     * Evaluates all implemented sorting algorithms on the same input dataset.
     *
     * @param data raw numerical data extracted from the selected CSV column
     * @param ascending true for ascending order, false for descending order
     * @return summary containing one representative result per algorithm and
     *         the fastest algorithm based on median execution time
     */
    public static EvaluationSummary evaluateAll(double[] data, boolean ascending) {
        validateData(data);

        List<SortingAlgorithm> algorithms = SortFactory.getAllAlgorithms();
        List<SortingResult> results = new ArrayList<>();

        // Small untimed warm-up helps reduce JIT start-up effects.
        warmupJIT(data, ascending);

        for (SortingAlgorithm algorithm : algorithms) {
            results.add(runMedianBenchmark(algorithm, data, ascending));
        }

        SortingResult bestResult = results.stream()
                .min(Comparator.comparingLong(SortingResult::getExecutionTimeNs))
                .orElse(null);

        return new EvaluationSummary(results, bestResult);
    }

    /**
     * Evaluates one algorithm. Kept compatible with the original application.
     */
    public static SortingResult evaluateSingle(
            SortingAlgorithm algorithm,
            double[] data,
            boolean ascending) {

        if (algorithm == null) {
            throw new IllegalArgumentException("Sorting algorithm cannot be null.");
        }

        validateData(data);
        warmupSingleAlgorithm(algorithm, data, ascending);
        return runMedianBenchmark(algorithm, data, ascending);
    }

    /**
     * Runs the same algorithm multiple times on the same original input and
     * returns the median-timed run.
     *
     * Each sorting implementation already creates its own copy of the input,
     * so no algorithm receives data that was previously sorted by another run.
     */
    private static SortingResult runMedianBenchmark(
            SortingAlgorithm algorithm,
            double[] data,
            boolean ascending) {

        List<SortingResult> runs = new ArrayList<>(BENCHMARK_RUNS);

        for (int i = 0; i < BENCHMARK_RUNS; i++) {
            SortingResult result = algorithm.sort(data, ascending);

            if (!result.isSortedCorrectly()) {
                throw new IllegalStateException(
                        algorithm.getName() + " produced an incorrectly sorted result.");
            }

            runs.add(result);
        }

        runs.sort(Comparator.comparingLong(SortingResult::getExecutionTimeNs));
        return runs.get(BENCHMARK_RUNS / 2);
    }

    /**
     * Performs a small warm-up for all algorithms before collecting timings.
     */
    private static void warmupJIT(double[] data, boolean ascending) {
        if (data.length == 0) {
            return;
        }

        double[] dummy = createWarmupArray(data);

        // Two passes give the JVM a chance to load/compile the hot methods.
        for (int pass = 0; pass < 2; pass++) {
            for (SortingAlgorithm algorithm : SortFactory.getAllAlgorithms()) {
                algorithm.sort(dummy, ascending);
            }
        }
    }

    private static void warmupSingleAlgorithm(
            SortingAlgorithm algorithm,
            double[] data,
            boolean ascending) {

        if (data.length == 0) {
            return;
        }

        double[] dummy = createWarmupArray(data);
        algorithm.sort(dummy, ascending);
        algorithm.sort(dummy, ascending);
    }

    private static double[] createWarmupArray(double[] data) {
        int size = Math.min(data.length, 100);
        double[] dummy = new double[size];
        System.arraycopy(data, 0, dummy, 0, size);
        return dummy;
    }

    private static void validateData(double[] data) {
        if (data == null) {
            throw new IllegalArgumentException("Dataset cannot be null.");
        }
    }

    /**
     * Holds all benchmark results and identifies the top performer.
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
            if (bestResult == null) {
                return "No results available.";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("BEST PERFORMING ALGORITHM: ")
                    .append(bestResult.getAlgorithmName())
                    .append("\n");
            sb.append("Median Execution Time: ")
                    .append(bestResult.getFormattedExecutionTime())
                    .append("\n");
            sb.append("Benchmark Runs per Algorithm: ")
                    .append(BENCHMARK_RUNS)
                    .append("\n");
            sb.append("Elements Sorted: ")
                    .append(bestResult.getItemCount())
                    .append("\n\n");
            sb.append("Full Algorithm Comparison:\n");
            sb.append("--------------------------------------------------\n");

            List<SortingResult> ranked = new ArrayList<>(results);
            ranked.sort(Comparator.comparingLong(SortingResult::getExecutionTimeNs));

            for (int i = 0; i < ranked.size(); i++) {
                SortingResult result = ranked.get(i);
                double speedRatio = (double) result.getExecutionTimeNs()
                        / Math.max(1L, bestResult.getExecutionTimeNs());

                sb.append(String.format(
                        "%d. %-15s: %10s | %d comparisons | %.2fx fastest%n",
                        i + 1,
                        result.getAlgorithmName(),
                        result.getFormattedExecutionTime(),
                        result.getComparisonCount(),
                        speedRatio));
            }

            return sb.toString();
        }
    }
}
