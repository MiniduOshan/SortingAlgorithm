package com.sortingapp.sorting;

/**
 * Utility helper methods for sorting algorithms (validation, verification).
 * 
 * Group Assignment Contribution:
 * Member 2: Implementation of sorting algorithms.
 */
public class SortUtils {

    /**
     * Verifies if an array is properly sorted in the given order.
     */
    public static boolean isSorted(double[] arr, boolean ascending) {
        if (arr == null || arr.length <= 1) {
            return true;
        }
        for (int i = 0; i < arr.length - 1; i++) {
            if (ascending) {
                if (arr[i] > arr[i + 1]) return false;
            } else {
                if (arr[i] < arr[i + 1]) return false;
            }
        }
        return true;
    }
}
