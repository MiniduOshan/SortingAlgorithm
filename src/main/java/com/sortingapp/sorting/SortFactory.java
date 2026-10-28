package com.sortingapp.sorting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Factory class to instantiate and provide all available sorting algorithms.
 * 
 * Member 2: Implementation of sorting algorithms.
 */
public class SortFactory {

    /**
     * Returns a list of all implemented sorting algorithms required by the assignment.
     */
    public static List<SortingAlgorithm> getAllAlgorithms() {
        List<SortingAlgorithm> algorithms = new ArrayList<>();
        algorithms.add(new InsertionSort());
        algorithms.add(new ShellSort());
        algorithms.add(new MergeSort());
        algorithms.add(new QuickSort());
        algorithms.add(new HeapSort());
        return Collections.unmodifiableList(algorithms);
    }
}
