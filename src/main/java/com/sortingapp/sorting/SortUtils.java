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
     * Rejects arrays containing non-finite values (NaN or Infinities).
     */
    public static boolean isSorted(double[] arr, boolean ascending) {
        if (arr == null) {
            return false;
        }
        if (arr.length == 0) {
            return true;
        }
        if (arr.length == 1) {
            return Double.isFinite(arr[0]);
        }
        for (int i = 0; i < arr.length - 1; i++) {
            if (!Double.isFinite(arr[i])) {
                return false;
            }
            if (ascending) {
                if (arr[i] > arr[i + 1]) return false;
            } else {
                if (arr[i] < arr[i + 1]) return false;
            }
        }
        return Double.isFinite(arr[arr.length - 1]);
    }
}
