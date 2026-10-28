package com.sortingapp.sorting;

import com.sortingapp.model.SortingResult;
import java.util.Arrays;

/**
 * Implementation of Quick Sort algorithm.
 * Quick Sort is a highly efficient divide-and-conquer sorting algorithm.
 * Uses Median-of-Three pivot selection and 3-way partitioning (Dutch National Flag)
 * to handle large datasets containing many duplicate/identical values safely without
 * excessive recursion or stack overflow.
 * Time Complexity: O(n log n) Best/Avg, O(n^2) Worst (O(n) on identical keys). Space Complexity: O(log n).
 * 
 * Group Assignment Contribution:
 * Member 2: Implementation of sorting algorithms.
 */
public class QuickSort implements SortingAlgorithm {

    private long comparisons;
    private long swaps;

    @Override
    public String getName() {
        return "Quick Sort";
    }

    @Override
    public String getDescription() {
        return "Divide-and-conquer algorithm that selects a pivot element and partitions the array into sub-arrays of smaller, equal, and larger elements.";
    }

    @Override
    public String getTimeComplexity() {
        return "Best: O(n log n) | Avg: O(n log n) | Worst: O(n²)";
    }

    @Override
    public SortingResult sort(double[] data, boolean ascending) {
        if (data == null) {
            return new SortingResult(getName(), new double[0], 0, 0, 0, ascending, true);
        }

        double[] arr = Arrays.copyOf(data, data.length);
        comparisons = 0;
        swaps = 0;

        long startTime = System.nanoTime();

        if (arr.length > 1) {
            quickSort(arr, 0, arr.length - 1, ascending);
        }

        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        boolean verified = SortUtils.isSorted(arr, ascending);

        return new SortingResult(getName(), arr, duration, comparisons, swaps, ascending, verified);
    }

    private void quickSort(double[] arr, int low, int high, boolean ascending) {
        if (low >= high) {
            return;
        }

        // Median-of-three pivot selection to optimize performance
        int mid = low + (high - low) / 2;
        medianOfThree(arr, low, mid, high, ascending);

        double pivot = arr[mid];
        int lt = low;
        int gt = high;
        int i = low;

        // 3-way partitioning (Dutch National Flag)
        while (i <= gt) {
            comparisons++;
            int cmp;
            if (arr[i] == pivot) {
                cmp = 0;
            } else if (ascending ? (arr[i] < pivot) : (arr[i] > pivot)) {
                cmp = -1;
            } else {
                cmp = 1;
            }

            if (cmp < 0) {
                swap(arr, lt, i);
                lt++;
                i++;
            } else if (cmp > 0) {
                swap(arr, i, gt);
                gt--;
            } else {
                i++;
            }
        }

        quickSort(arr, low, lt - 1, ascending);
        quickSort(arr, gt + 1, high, ascending);
    }

    private void medianOfThree(double[] arr, int low, int mid, int high, boolean ascending) {
        comparisons++;
        if (ascending ? (arr[low] > arr[mid]) : (arr[low] < arr[mid])) {
            swap(arr, low, mid);
        }
        comparisons++;
        if (ascending ? (arr[low] > arr[high]) : (arr[low] < arr[high])) {
            swap(arr, low, high);
        }
        comparisons++;
        if (ascending ? (arr[mid] > arr[high]) : (arr[mid] < arr[high])) {
            swap(arr, mid, high);
        }
    }

    private void swap(double[] arr, int i, int j) {
        if (i != j) {
            double temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
            swaps++;
        }
    }
}
