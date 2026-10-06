package com.sortingapp.sorting;

import com.sortingapp.model.SortingResult;
import java.util.Arrays;

/**
 * Implementation of Shell Sort algorithm.
 * Shell Sort generalizes insertion sort by allowing the exchange of items that are far apart.
 * Time Complexity: O(n log n) Best, O(n^1.5) Avg, O(n^2) Worst depending on gap sequence.
 * 
 * Group Assignment Contribution:
 * Member 2: Implementation of sorting algorithms.
 */
public class ShellSort implements SortingAlgorithm {

    @Override
    public String getName() {
        return "Shell Sort";
    }

    @Override
    public String getDescription() {
        return "In-place comparison sort that generalizes insertion sort by comparing elements separated by a diminishing gap sequence.";
    }

    @Override
    public String getTimeComplexity() {
        return "Best: O(n log n) | Avg: O(n^1.5) | Worst: O(n²)";
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

        // Start with a large gap, then reduce the gap
        for (int gap = n / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < n; i++) {
                double temp = arr[i];
                int j = i;

                while (j >= gap) {
                    comparisons++;
                    boolean condition = ascending ? (arr[j - gap] > temp) : (arr[j - gap] < temp);
                    if (condition) {
                        arr[j] = arr[j - gap];
                        swaps++;
                        j -= gap;
                    } else {
                        break;
                    }
                }
                arr[j] = temp;
            }
        }

        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        boolean verified = SortUtils.isSorted(arr, ascending);

        return new SortingResult(getName(), arr, duration, comparisons, swaps, ascending, verified);
    }
}
