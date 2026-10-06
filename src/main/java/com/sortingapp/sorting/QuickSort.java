package com.sortingapp.sorting;

import com.sortingapp.model.SortingResult;
import java.util.Arrays;

/**
 * Implementation of Quick Sort algorithm.
 * Quick Sort is a highly efficient divide-and-conquer sorting algorithm.
 * Uses Median-of-Three pivot selection for improved performance on pre-sorted arrays.
 * Time Complexity: O(n log n) Best/Avg, O(n^2) Worst. Space Complexity: O(log n).
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
        return "Divide-and-conquer algorithm that selects a pivot element and partitions the array into sub-arrays of smaller and larger elements.";
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
        if (low < high) {
            int pi = partition(arr, low, high, ascending);

            quickSort(arr, low, pi - 1, ascending);
            quickSort(arr, pi + 1, high, ascending);
        }
    }

    private int partition(double[] arr, int low, int high, boolean ascending) {
        // Median-of-three pivot selection to optimize performance
        int mid = low + (high - low) / 2;
        medianOfThree(arr, low, mid, high, ascending);

        double pivot = arr[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            comparisons++;
            boolean condition = ascending ? (arr[j] < pivot) : (arr[j] > pivot);
            if (condition) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
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
        swap(arr, mid, high);
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
