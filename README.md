# Sorting Algorithm Performance Analyzer

A Java GUI application developed for the **IC4302 – Programming III** group assignment.

The application allows users to upload a CSV dataset, select a numeric column, apply different sorting algorithms, and compare their execution times.

## Features

* Upload `.csv` datasets
* Display available CSV columns
* Select a numeric column for sorting
* Implement five sorting algorithms:

  * Insertion Sort
  * Shell Sort
  * Merge Sort
  * Quick Sort
  * Heap Sort
* Measure execution time for each algorithm
* Compare algorithm performance
* Identify the fastest algorithm
* User-friendly Java GUI
* Handle invalid files and invalid input

## Technologies

* Java
* Java Swing
* IntelliJ IDEA
* Git & GitHub

## Project Structure

```text
src/
└── main/
    └── java/
        └── com/
            └── sortingapp/
                ├── Main.java
                │
                ├── gui/
                │   └── MainFrame.java
                │
                ├── csv/
                │   └── CSVReader.java
                │
                ├── sorting/
                │   ├── SortingAlgorithm.java
                │   ├── InsertionSort.java
                │   ├── ShellSort.java
                │   ├── MergeSort.java
                │   ├── QuickSort.java
                │   └── HeapSort.java
                │
                └── performance/
                    └── PerformanceEvaluator.java
```

## How It Works

```text
Upload CSV
    ↓
Read CSV columns
    ↓
Select numeric column
    ↓
Extract numeric values
    ↓
Run sorting algorithms
    ↓
Measure execution time
    ↓
Compare results
    ↓
Display fastest algorithm
```

## Sorting Algorithms

| Algorithm      | Purpose                                                            |
| -------------- | ------------------------------------------------------------------ |
| Insertion Sort | Sorts elements by inserting each element into its correct position |
| Shell Sort     | Improves insertion sort using gap-based comparisons                |
| Merge Sort     | Uses divide-and-conquer to sort the dataset                        |
| Quick Sort     | Uses partitioning around a pivot                                   |
| Heap Sort      | Uses a heap data structure for sorting                             |

## Performance Evaluation

Each algorithm is executed using the same original dataset.

Execution time is measured using Java's high-resolution timer:

```java
long start = System.nanoTime();

algorithm.sort(data);

long end = System.nanoTime();
```

The application displays the execution time of all five algorithms and identifies the algorithm with the shortest execution time.

Example:

```text
Algorithm        Execution Time
--------------------------------
Insertion Sort   15.23 ms
Shell Sort        4.31 ms
Merge Sort        2.83 ms
Quick Sort        2.21 ms
Heap Sort         3.11 ms

Best Algorithm: Quick Sort
```

## Input Requirements

The application accepts `.csv` files.

The selected sorting column must contain numeric values.

Example:

```csv
Name,Age,Salary,Experience
John,25,45000,2
David,32,65000,7
Sarah,28,52000,4
Mike,41,85000,15
```

Numeric columns such as `Age`, `Salary`, and `Experience` can be selected for sorting.

## Error Handling

The application handles common invalid inputs, including:

* Invalid file type
* Empty CSV files
* No column selected
* Non-numeric columns
* Empty values
* Invalid CSV data

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/your-username/sorting-algorithm-performance-analyzer.git
```

### 2. Open the project

Open the project using **IntelliJ IDEA**.

### 3. Configure Java

Make sure a compatible JDK is installed.

Recommended:

```text
JDK 21
```

### 4. Run the application

Run:

```text
Main.java
```

## Testing

The application should be tested with different datasets, including:

* Small datasets
* Large datasets
* Already sorted data
* Reverse-sorted data
* Duplicate values
* Negative numbers
* Empty or invalid input

The sorting result should be verified before comparing performance.

## Team Contributions

| Member   | Responsibility                   |
| -------- | -------------------------------- |
| Member 1 | CSV upload and dataset handling  |
| Member 2 | Sorting algorithm implementation |
| Member 3 | GUI and performance evaluation   |

## Assignment

**Module:** IC4302 – Programming III
**Assignment:** Group Assignment – Java Application for Sorting Algorithm Performance
**Faculty:** Faculty of Technology, University of Colombo

## License

This project was developed for academic purposes.
