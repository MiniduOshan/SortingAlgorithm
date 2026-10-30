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
 * Each algorithm is benchmarked over multiple runs, and the median-timed run
 * is selected to reduce the impact of short-lived JVM and OS timing fluctuations.
 * Execution times are measured by the sorting implementations.
 *
 * Group Assignment Contribution:
 * Member 3: Performance evaluation and reporting.
 */
public class PerformanceEvaluator {

    /**
     * Use an odd number of runs so there is a single middle (median) result.
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

        // Warm up the JVM before collecting benchmark results.
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
     * Evaluates a single sorting algorithm using median execution time.
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
     * Runs an algorithm five times using independent copies of the original
     * dataset and returns the median-timed result.
     */
    private static SortingResult runMedianBenchmark(
            SortingAlgorithm algorithm,
            double[] data,
            boolean ascending) {

        List<SortingResult> runs = new ArrayList<>(BENCHMARK_RUNS);

        for (int i = 0; i < BENCHMARK_RUNS; i++) {
            SortingResult result = algorithm.sort(data.clone(), ascending);

            if (!result.isSortedCorrectly()) {
                throw new IllegalStateException(
                        algorithm.getName() + " produced an incorrectly sorted result.");
            }

            runs.add(result);
        }

        // Sort the timings and select the middle result.
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

        // Perform two warm-up passes to reduce startup effects.
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
        for (double v : data) {
            if (!Double.isFinite(v)) {
                throw new IllegalArgumentException("Dataset contains non-finite numerical value: " + v);
            }
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

        /**
         * Generates a formatted report with the fastest algorithm, execution
         * times, and a ranked comparison of all benchmark results.
         */
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
