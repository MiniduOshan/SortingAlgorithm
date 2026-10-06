package com.sortingapp.sorting;

import com.sortingapp.model.SortingResult;

/**
 * Common interface for all sorting algorithm implementations.
 * Allows uniform execution and performance evaluation.
 * 
 * Group Assignment Contribution:
 * Member 2: Implementation of sorting algorithms.
 */
public interface SortingAlgorithm {

    /**
     * Returns the human-readable name of the sorting algorithm.
     */
    String getName();

    /**
     * Returns a brief description of how the algorithm works.
     */
    String getDescription();

    /**
     * Returns the asymptotic time complexity (Best, Average, Worst).
     */
    String getTimeComplexity();

    /**
     * Sorts the provided numerical array in ascending or descending order.
     * The input array must NOT be mutated in-place without creating a copy if needed,
     * or the algorithm works on a copy passed to it.
     * 
     * @param data Array of double values to sort.
     * @param ascending True for ascending order, false for descending order.
     * @return SortingResult containing metrics and sorted array.
     */
    SortingResult sort(double[] data, boolean ascending);
}
