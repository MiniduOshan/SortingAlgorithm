package com.sortingapp.sorting;

import com.sortingapp.model.SortingResult;
import java.util.Arrays;

/**
 * Implementation of Merge Sort algorithm.
 * Merge Sort is an efficient, comparison-based, divide-and-conquer algorithm.
 * Time Complexity: O(n log n) Best/Avg/Worst. Space Complexity: O(n).
 * 
 * Group Assignment Contribution:
 * Member 2: Implementation of sorting algorithms.
 */
public class MergeSort implements SortingAlgorithm {

    private long comparisons;
    private long swaps;

    @Override
    public String getName() {
        return "Merge Sort";
    }

    @Override
    public String getDescription() {
        return "Divide-and-conquer algorithm that recursively splits the array into halves, sorts them, and merges the sorted halves.";
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

        long startTime = System.nanoTime();

        if (arr.length > 1) {
            mergeSort(arr, 0, arr.length - 1, ascending);
        }

        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        boolean verified = SortUtils.isSorted(arr, ascending);

        return new SortingResult(getName(), arr, duration, comparisons, swaps, ascending, verified);
    }

    private void mergeSort(double[] arr, int left, int right, boolean ascending) {
        if (left < right) {
            int mid = left + (right - left) / 2;

            mergeSort(arr, left, mid, ascending);
            mergeSort(arr, mid + 1, right, ascending);

            merge(arr, left, mid, right, ascending);
        }
    }

    private void merge(double[] arr, int left, int mid, int right, boolean ascending) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        double[] L = new double[n1];
        double[] R = new double[n2];

        System.arraycopy(arr, left, L, 0, n1);
        System.arraycopy(arr, mid + 1, R, 0, n2);

        int i = 0, j = 0;
        int k = left;

        while (i < n1 && j < n2) {
            comparisons++;
            boolean condition = ascending ? (L[i] <= R[j]) : (L[i] >= R[j]);
            if (condition) {
                arr[k] = L[i];
                i++;
            } else {
                arr[k] = R[j];
                j++;
            }
            swaps++; // Count element placement
            k++;
        }

        while (i < n1) {
            arr[k] = L[i];
            i++;
            k++;
            swaps++;
        }

        while (j < n2) {
            arr[k] = R[j];
            j++;
            k++;
            swaps++;
        }
    }
}
