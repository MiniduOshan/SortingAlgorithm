package com.sortingapp.sorting;

import com.sortingapp.model.SortingResult;
import java.util.Arrays;

/**
 * Implementation of Heap Sort algorithm.
 * Heap Sort uses a binary heap data structure to sort elements.
 * Time Complexity: O(n log n) Best/Avg/Worst. Space Complexity: O(1).
 * 
 * Group Assignment Contribution:
 * Member 2: Implementation of sorting algorithms.
 */
public class HeapSort implements SortingAlgorithm {

    private long comparisons;
    private long swaps;

    @Override
    public String getName() {
        return "Heap Sort";
    }

    @Override
    public String getDescription() {
        return "Comparison-based sorting technique based on Binary Heap data structure. Builds a heap and repeatedly extracts the root.";
    }

    @Override
    public String getTimeComplexity() {
        return "Best: O(n log n) | Avg: O(n log n) | Worst: O(n log n)";
    }

    @Override
    public SortingResult sort(double[] data, boolean ascending) {
        if (data == null) {
            return new SortingResult(getName(), new double[0], 0, 0, 0, ascending, true);
        }

        double[] arr = Arrays.copyOf(data, data.length);
        comparisons = 0;
        swaps = 0;
        int n = arr.length;

        long startTime = System.nanoTime();

        // Build max heap for ascending, or min heap for descending
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i, ascending);
        }

        // One by one extract elements from heap
        for (int i = n - 1; i > 0; i--) {
            swap(arr, 0, i);
            heapify(arr, i, 0, ascending);
        }

        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        boolean verified = SortUtils.isSorted(arr, ascending);

        return new SortingResult(getName(), arr, duration, comparisons, swaps, ascending, verified);
    }

    private void heapify(double[] arr, int n, int i, boolean ascending) {
        int target = i; // Initialize largest/smallest as root
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < n) {
            comparisons++;
            boolean condition = ascending ? (arr[left] > arr[target]) : (arr[left] < arr[target]);
            if (condition) {
                target = left;
            }
        }

        if (right < n) {
            comparisons++;
            boolean condition = ascending ? (arr[right] > arr[target]) : (arr[right] < arr[target]);
            if (condition) {
                target = right;
            }
        }

        if (target != i) {
            swap(arr, i, target);
            heapify(arr, n, target, ascending);
        }
    }

    private void swap(double[] arr, int i, int j) {
        double temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
        swaps++;
    }
}
