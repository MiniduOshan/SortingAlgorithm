# Sorting Algorithm Performance Evaluator

**IC4302 – Programming III**  
**Department of ICT | Faculty of Technology | University of Colombo**

---

## 📌 Project Overview

The Sorting Algorithm Performance Evaluator is a Java Swing desktop application designed to compare the execution time and performance of five sorting algorithms using numerical data from CSV files.

The application implements the following algorithms:

1. **Insertion Sort** — Best: `O(n)`, Average: `O(n²)`, Worst: `O(n²)`. Sorts elements by inserting each value into its correct position.
2. **Shell Sort** — Best: `O(n log n)`, Worst: `O(n²)`. An improved insertion sort that uses progressively smaller gaps. Average-case complexity depends on the gap sequence.
3. **Merge Sort** — Best, Average and Worst: `O(n log n)`. Uses divide and conquer to split and merge sorted subarrays.
4. **Quick Sort** — Average: `O(n log n)`, Worst: `O(n²)`. Uses median-of-three pivot selection and three-way partitioning, with `O(n)` performance for all-equal elements.
5. **Heap Sort** — Best, Average and Worst: `O(n log n)`. Uses a binary max-heap to arrange elements in sorted order.

Users can upload CSV datasets, select numeric columns, execute all five algorithms, and compare their execution times. The application uses five benchmark runs per algorithm and selects the median execution time to reduce measurement fluctuations.

Results are displayed through a performance table, a comparison chart, and a fastest-algorithm summary. Users can also export the sorted dataset as a CSV file.

---

## 👥 Team Contributions

| Group Member | Responsibilities | Main Java Classes |
|---|---|---|
| **E.M. Pabasara** — 2022T01560 | CSV file uploading, parsing, numeric column validation, dataset management and exporting | `CSVData.java`, `CSVReader.java`, `CSVWriter.java` |
| **B.D.M.O.B. Wanigarathna** — 2022T01584 | Implementation and verification of the five sorting algorithms | `SortingAlgorithm.java`, `InsertionSort.java`, `ShellSort.java`, `MergeSort.java`, `QuickSort.java`, `HeapSort.java`, `SortUtils.java` |
| **M.M.D. Karunathilake** — 2022T01542 | Performance evaluation, execution-time comparison, results reporting and graphical user interface | `PerformanceEvaluator.java`, `MainFrame.java`, `BarChartPanel.java`, `PerformanceTablePanel.java` |

---

## 🚀 Key Features

### 1. CSV Dataset Management

- Upload CSV files containing numerical and non-numerical data.
- Automatically detect and display available columns.
- Identify valid numeric columns for sorting.
- Reject invalid numeric values, missing values, `NaN` and infinite values.
- Display clear error messages for invalid files or column selections.
- Support CSV files containing quoted fields and UTF-8 text.
- Export sorted data while preserving the corresponding row information.

### 2. Sorting Algorithms

- Implements Insertion Sort, Shell Sort, Merge Sort, Quick Sort and Heap Sort.
- Supports ascending and descending sorting within the algorithm implementations.
- Uses separate copies of the original dataset for each algorithm.
- Tracks comparisons and swaps or shifts during sorting.
- Checks whether the resulting arrays are correctly sorted.
- Includes three-way Quick Sort partitioning for improved handling of duplicate values.

### 3. Performance Evaluation

- Measures execution time using `System.nanoTime()`.
- Runs each algorithm five times and calculates the median execution time.
- Compares all five algorithms using the same original input values.
- Automatically identifies the fastest algorithm based on the lowest measured median time.
- Displays algorithm rankings, execution times, comparison counts and relative performance.
- Uses a warm-up process to reduce some Java runtime timing fluctuations.

### 4. Graphical User Interface

- Developed using Java Swing.
- Provides a simple dashboard for uploading datasets and selecting columns.
- Displays a performance results table and execution-time comparison chart.
- Highlights the fastest algorithm.
- Uses `SwingWorker` to keep the interface responsive during evaluation.
- Provides clear status messages and error notifications.
- Includes an option to export the sorted CSV dataset.

### 5. Testing and Validation

- Automated unit testing using JUnit 5.
- Tests sorting correctness with different input sizes and values.
- Validates numeric column detection and CSV file handling.
- Checks array integrity and correct sorting results.
- Verifies performance comparison and fastest-algorithm selection.
- Uses Maven for compilation, testing and building the application.

---

## 📁 Project Structure

```text
SortingAlgorithm/
├── pom.xml
├── README.md
├── test-data/
│   ├── iris.csv
│   ├── sample_students.csv
│   ├── sample_sales_500.csv
│   ├── sample_housing_2000.csv
│   └── students_performance.csv
│
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── sortingapp/
    │   │           ├── Main.java
    │   │           ├── model/
    │   │           │   ├── CSVData.java
    │   │           │   └── SortingResult.java
    │   │           ├── csv/
    │   │           │   ├── CSVReader.java
    │   │           │   └── CSVWriter.java
    │   │           ├── sorting/
    │   │           │   ├── SortingAlgorithm.java
    │   │           │   ├── InsertionSort.java
    │   │           │   ├── ShellSort.java
    │   │           │   ├── MergeSort.java
    │   │           │   ├── QuickSort.java
    │   │           │   ├── HeapSort.java
    │   │           │   ├── SortFactory.java
    │   │           │   └── SortUtils.java
    │   │           ├── performance/
    │   │           │   └── PerformanceEvaluator.java
    │   │           └── gui/
    │   │               ├── MainFrame.java
    │   │               └── components/
    │   │                   ├── BarChartPanel.java
    │   │                   └── PerformanceTablePanel.java
    │   └── resources/
    │       └── icons/
    │           └── app_icon.png
    └── test/
        └── java/
            └── com/
                └── sortingapp/
                    ├── SortingAlgorithmTest.java
                    ├── CSVValidationTest.java
                    └── PerformanceEvaluatorTest.java
```

---

## 💻 Installation and Execution

### Prerequisites

- Java Development Kit (JDK) 17 or later
- Apache Maven
- Windows, Linux or macOS

### Option 1: Using Maven (Recommended)

**Step 1 — Open the project directory**

```bash
cd SortingAlgorithm
```

**Step 2 — Compile the application**

```bash
mvn clean compile
```

**Step 3 — Run the application**

```bash
java -cp target/classes com.sortingapp.Main
```

### Option 2: Using Java Compiler

**Step 1 — Create the output directory**

```bash
mkdir -p target/classes
```

On Windows CMD, use:

```cmd
if not exist target\classes mkdir target\classes
```

**Step 2 — Compile the Java source files**

```bash
javac -d target/classes -sourcepath src/main/java src/main/java/com/sortingapp/Main.java
```

**Step 3 — Run the application**

```bash
java -cp target/classes com.sortingapp.Main
```

---

## 🧪 Unit Testing

The project uses JUnit 5 for automated testing.

Run all tests using Maven:

```bash
mvn clean test
```

The tests cover:

- Sorting algorithm correctness
- Ascending and descending sorting
- Duplicate values and edge cases
- CSV parsing and validation
- Original array preservation
- Performance evaluation and result ranking

Detailed test reports are generated in:

```text
target/surefire-reports/
```

To compile, test and package the application:

```bash
mvn clean package
```

---

## 📊 How to Use

1. **Launch the application** using one of the methods above.
2. **Upload a CSV file** using the Browse CSV button.
3. **Select a numeric column** from the dropdown list.
4. **Run the sorting algorithms** using the Run Sorting Algorithms button.
5. **View the results** in the performance table and comparison chart.
6. **Identify the fastest algorithm** from the performance summary.
7. **Export the sorted data** using the Export Sorted CSV button.

**Note:** Execution times may vary between runs due to system activity and Java runtime optimisation. The fastest algorithm is determined dynamically using the measured median execution times.

---

## 🛠 Technologies Used

| Technology | Purpose |
|---|---|
| Java | Core application development |
| Java Swing | Graphical user interface |
| Maven | Project build and dependency management |
| JUnit 5 | Automated unit testing |
| CSV | Dataset input and sorted-data export |
| Git | Version control |

---

## Conclusion

The Sorting Algorithm Performance Evaluator provides a practical way to execute and compare five sorting algorithms using CSV datasets. It combines sorting implementations, data validation, execution-time measurement and performance visualisation in a single Java desktop application.
