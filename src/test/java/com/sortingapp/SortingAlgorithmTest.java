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
 * Unit tests verifying correctness, edge cases, and stability of all 5 sorting algorithms.
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
}
