package com.sortingapp.sorting;

import com.sortingapp.model.SortingResult;
import java.util.Arrays;

/**
 * Implementation of the Insertion Sort algorithm.
 * Insertion Sort builds the final sorted array one item at a time.
 * Time Complexity: O(n^2) Worst/Average, O(n) Best. Space Complexity: O(1).
 * 
 * Group Assignment Contribution:
 * Member 2: Implementation of sorting algorithms.
 */
public class InsertionSort implements SortingAlgorithm {

    @Override
    public String getName() {
        return "Insertion Sort";
    }

    @Override
    public String getDescription() {
        return "Simple comparison sort that builds the final sorted array one item at a time by inserting elements into their correct position.";
    }

    @Override
    public String getTimeComplexity() {
        return "Best: O(n) | Avg: O(n²) | Worst: O(n²)";
    }

    @Override
    public SortingResult sort(double[] data, boolean ascending) {
        if (data == null) {
            return new SortingResult(getName(), new double[0], 0, 0, 0, ascending, true);
        }

        double[] arr = Arrays.copyOf(data, data.length);
        long comparisons = 0;
        long swaps = 0;
        int n = arr.length;

        long startTime = System.nanoTime();

        for (int i = 1; i < n; i++) {
            double key = arr[i];
            int j = i - 1;

            while (j >= 0) {
                comparisons++;
                boolean condition = ascending ? (arr[j] > key) : (arr[j] < key);
                if (condition) {
                    arr[j + 1] = arr[j];
                    swaps++;
                    j--;
                } else {
                    break;
                }
            }
            arr[j + 1] = key;
        }

        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        boolean verified = SortUtils.isSorted(arr, ascending);

        return new SortingResult(getName(), arr, duration, comparisons, swaps, ascending, verified);
    }
}
