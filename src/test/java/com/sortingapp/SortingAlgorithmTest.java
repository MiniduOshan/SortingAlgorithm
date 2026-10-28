package com.sortingapp;

import com.sortingapp.model.SortingResult;
import com.sortingapp.sorting.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests verifying correctness, edge cases, stability, and duplicate handling
 * across all 5 sorting algorithms.
 */
class SortingAlgorithmTest {

    private double[] randomData;
    private double[] sortedDataAscending;
    private double[] sortedDataDescending;

    @BeforeEach
    void setUp() {
        randomData = new double[]{42.5, 12.0, -5.3, 100.1, 0.0, 12.0, 88.8, 3.14159, -100.0};
        sortedDataAscending = Arrays.copyOf(randomData, randomData.length);
        Arrays.sort(sortedDataAscending);

        sortedDataDescending = new double[sortedDataAscending.length];
        for (int i = 0; i < sortedDataAscending.length; i++) {
            sortedDataDescending[i] = sortedDataAscending[sortedDataAscending.length - 1 - i];
        }
    }

    static Stream<SortingAlgorithm> algorithmProvider() {
        return SortFactory.getAllAlgorithms().stream();
    }

    // =========================================================================
    // Preserved 20 Original Unit Tests
    // =========================================================================

    @ParameterizedTest
    @MethodSource("algorithmProvider")
    @DisplayName("Test Ascending Order Sorting")
    void testAscendingSort(SortingAlgorithm algorithm) {
        SortingResult result = algorithm.sort(randomData, true);

        assertTrue(result.isSortedCorrectly(), algorithm.getName() + " failed ascending sort verification.");
        assertArrayEquals(sortedDataAscending, result.getSortedData(), 1e-6,
                algorithm.getName() + " produced incorrect array output.");
    }

    @ParameterizedTest
    @MethodSource("algorithmProvider")
    @DisplayName("Test Descending Order Sorting")
    void testDescendingSort(SortingAlgorithm algorithm) {
        SortingResult result = algorithm.sort(randomData, false);

        assertTrue(result.isSortedCorrectly(), algorithm.getName() + " failed descending sort verification.");
        assertArrayEquals(sortedDataDescending, result.getSortedData(), 1e-6,
                algorithm.getName() + " produced incorrect descending output.");
    }

    @ParameterizedTest
    @MethodSource("algorithmProvider")
    @DisplayName("Test Empty and Single Element Arrays")
    void testEdgeCases(SortingAlgorithm algorithm) {
        // Empty array
        SortingResult emptyResult = algorithm.sort(new double[0], true);
        assertTrue(emptyResult.isSortedCorrectly());
        assertEquals(0, emptyResult.getSortedData().length);

        // Single element array
        double[] single = new double[]{42.0};
        SortingResult singleResult = algorithm.sort(single, true);
        assertTrue(singleResult.isSortedCorrectly());
        assertArrayEquals(single, singleResult.getSortedData(), 1e-6);
    }

    @ParameterizedTest
    @MethodSource("algorithmProvider")
    @DisplayName("Test Large Array Sorting Correctness")
    void testLargeArray(SortingAlgorithm algorithm) {
        Random rnd = new Random(12345);
        double[] largeArray = new double[1000];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = rnd.nextDouble() * 1000.0 - 500.0;
        }

        double[] expected = Arrays.copyOf(largeArray, largeArray.length);
        Arrays.sort(expected);

        SortingResult result = algorithm.sort(largeArray, true);
        assertTrue(result.isSortedCorrectly(), algorithm.getName() + " failed on large array.");
        assertArrayEquals(expected, result.getSortedData(), 1e-6);
    }

    // =========================================================================
    // Additional Comprehensive Algorithm Tests
    // =========================================================================

    @ParameterizedTest
    @MethodSource("algorithmProvider")
    @DisplayName("Test Preservation of Original Input Array (No In-Place Mutation)")
    void testPreservationOfOriginalInput(SortingAlgorithm algorithm) {
        double[] original = new double[]{5.5, -2.1, 99.9, 0.0, -100.5};
        double[] copyBeforeSort = Arrays.copyOf(original, original.length);

        algorithm.sort(original, true);
        assertArrayEquals(copyBeforeSort, original, 1e-6,
                algorithm.getName() + " mutated the original input array in-place!");

        algorithm.sort(original, false);
        assertArrayEquals(copyBeforeSort, original, 1e-6,
                algorithm.getName() + " mutated the original input array in descending sort!");
    }

    @ParameterizedTest
    @MethodSource("algorithmProvider")
    @DisplayName("Test Already Sorted and Reverse Sorted Arrays")
    void testAlreadySortedAndReverseSorted(SortingAlgorithm algorithm) {
        double[] sorted = new double[]{-50.0, -10.5, 0.0, 1.25, 42.0, 100.0};
        SortingResult ascRes = algorithm.sort(sorted, true);
        assertTrue(ascRes.isSortedCorrectly());
        assertArrayEquals(sorted, ascRes.getSortedData(), 1e-6);

        double[] reverse = new double[]{100.0, 42.0, 1.25, 0.0, -10.5, -50.0};
        SortingResult descRes = algorithm.sort(reverse, false);
        assertTrue(descRes.isSortedCorrectly());
        assertArrayEquals(reverse, descRes.getSortedData(), 1e-6);
    }

    @ParameterizedTest
    @MethodSource("algorithmProvider")
    @DisplayName("Test Datasets with Many Identical/Duplicate Values (No Stack Overflow)")
    void testManyIdenticalValues(SortingAlgorithm algorithm) {
        int size = 5000;
        double[] duplicates = new double[size];
        Arrays.fill(duplicates, 7.77);

        SortingResult result = algorithm.sort(duplicates, true);
        assertTrue(result.isSortedCorrectly(), algorithm.getName() + " failed on all-identical array.");
        assertEquals(size, result.getSortedData().length);
        assertEquals(7.77, result.getSortedData()[0], 1e-6);
        assertEquals(7.77, result.getSortedData()[size - 1], 1e-6);
    }

    @ParameterizedTest
    @MethodSource("algorithmProvider")
    @DisplayName("Test Decimal Values and Negative Numbers")
    void testDecimalAndNegativeValues(SortingAlgorithm algorithm) {
        double[] data = new double[]{-0.0001, -999.999, 0.00001, -0.0002, 1000.5, -999.998};
        double[] expectedAsc = Arrays.copyOf(data, data.length);
        Arrays.sort(expectedAsc);

        SortingResult res = algorithm.sort(data, true);
        assertTrue(res.isSortedCorrectly());
        assertArrayEquals(expectedAsc, res.getSortedData(), 1e-9);
    }

    @Test
    @DisplayName("Test SortUtils isSorted Rejection of Non-Finite Values (NaN / Infinity)")
    void testSortUtilsRejectsNonFiniteValues() {
        assertFalse(SortUtils.isSorted(new double[]{1.0, Double.NaN, 3.0}, true));
        assertFalse(SortUtils.isSorted(new double[]{1.0, 2.0, Double.POSITIVE_INFINITY}, true));
        assertFalse(SortUtils.isSorted(new double[]{Double.NEGATIVE_INFINITY, 2.0, 3.0}, true));
        assertFalse(SortUtils.isSorted(new double[]{Double.NaN}, true));
        assertFalse(SortUtils.isSorted(null, true));
    }
}
