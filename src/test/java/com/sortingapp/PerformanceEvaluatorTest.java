package com.sortingapp;

import com.sortingapp.model.SortingResult;
import com.sortingapp.performance.PerformanceEvaluator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests verifying the performance benchmarking engine, median calculation,
 * fastest algorithm selection, and data integrity.
 */
class PerformanceEvaluatorTest {

    @Test
    @DisplayName("Test EvaluateAll Returns Exactly Five Required Sorting Results")
    void testEvaluateAllReturnsFiveResults() {
        double[] sample = new double[]{10.5, -4.2, 88.0, 3.14, 0.0, -100.0, 42.0};
        PerformanceEvaluator.EvaluationSummary summary = PerformanceEvaluator.evaluateAll(sample, true);

        assertNotNull(summary);
        List<SortingResult> results = summary.getResults();
        assertEquals(5, results.size(), "Performance evaluator must evaluate all 5 algorithms");

        for (SortingResult res : results) {
            assertTrue(res.isSortedCorrectly());
            assertTrue(res.getExecutionTimeNs() > 0);
            assertNotNull(res.getAlgorithmName());
        }
    }

    @Test
    @DisplayName("Test Fastest Algorithm is Correctly Selected Using Lowest Execution Time")
    void testFastestAlgorithmSelection() {
        double[] sample = new double[]{15.0, 2.0, 99.0, -10.0, 4.5, 33.2};
        PerformanceEvaluator.EvaluationSummary summary = PerformanceEvaluator.evaluateAll(sample, true);

        SortingResult best = summary.getBestResult();
        assertNotNull(best, "Best result must not be null");

        // The best result must have executionTimeNs <= all other evaluated algorithms
        for (SortingResult r : summary.getResults()) {
            assertTrue(best.getExecutionTimeNs() <= r.getExecutionTimeNs(),
                    "Fastest algorithm must have the minimum median execution time");
        }
    }

    @Test
    @DisplayName("Test Evaluator Does Not Mutate the Input Array")
    void testInputArrayIsolation() {
        double[] input = new double[]{100.0, 50.0, 25.0, 12.5, 6.25};
        double[] inputCopy = Arrays.copyOf(input, input.length);

        PerformanceEvaluator.evaluateAll(input, true);
        assertArrayEquals(inputCopy, input, 1e-9, "Input dataset must not be modified by PerformanceEvaluator");
    }

    @Test
    @DisplayName("Test Evaluator Rejection of Null or Non-Finite Values")
    void testRejectionOfInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> PerformanceEvaluator.evaluateAll(null, true));

        double[] nanArray = new double[]{1.0, Double.NaN, 3.0};
        assertThrows(IllegalArgumentException.class, () -> PerformanceEvaluator.evaluateAll(nanArray, true));

        double[] infArray = new double[]{1.0, 2.0, Double.POSITIVE_INFINITY};
        assertThrows(IllegalArgumentException.class, () -> PerformanceEvaluator.evaluateAll(infArray, true));
    }

    @Test
    @DisplayName("Test Summary Text and Relative Speedup Formatter")
    void testSummaryTextGeneration() {
        double[] input = new double[]{5.0, 4.0, 3.0, 2.0, 1.0};
        PerformanceEvaluator.EvaluationSummary summary = PerformanceEvaluator.evaluateAll(input, true);

        String text = summary.getSummaryText();
        assertNotNull(text);
        assertTrue(text.contains("BEST PERFORMING ALGORITHM"));
        assertTrue(text.contains("Median Execution Time"));
        assertTrue(text.contains("Benchmark Runs per Algorithm: 5"));
        assertTrue(text.contains("Full Algorithm Comparison"));
    }
}
