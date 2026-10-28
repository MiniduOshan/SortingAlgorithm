# IC4302 – Programming III Group Assignment
## Java Application for Sorting Algorithm Performance Evaluation
**Faculty of Technology | Department of ICT | University of Colombo**

---

### 📌 Project Overview
This project is a high-performance Java Swing application built for evaluating and benchmarking the execution time and efficiency of five fundamental sorting algorithms:
1. **Insertion Sort** — $O(n^2)$ comparison sort.
2. **Shell Sort** — $O(n \log n)$ to $O(n^{1.5})$ diminishing gap sort.
3. **Merge Sort** — $O(n \log n)$ divide-and-conquer sort.
4. **Quick Sort** — $O(n \log n)$ median-of-three partitioned sort.
5. **Heap Sort** — $O(n \log n)$ binary max/min heap sort.

Users can upload any custom dataset in `.csv` format (including Kaggle datasets), dynamically select any numeric column, choose sorting order (Ascending or Descending), evaluate algorithm execution times down to nanosecond precision, visualize results via custom bar charts, and export sorted datasets.

---

### 👥 Group Guidelines & Task Delegation (Presentation Structure)

In accordance with assignment guidelines, the codebase is modularized across 3 primary areas of contribution for presentation recording:

| Group Member | Primary Responsibilities | Java Packages / Classes |
| :--- | :--- | :--- |
| **Member 1** | CSV Dataset Upload, Parsing & Data Model | `com.sortingapp.model.CSVData`<br>`com.sortingapp.csv.CSVReader`<br>`com.sortingapp.csv.CSVWriter` |
| **Member 2** | Implementation of 5 Sorting Algorithms & Verification | `com.sortingapp.sorting.SortingAlgorithm`<br>`InsertionSort`, `ShellSort`, `MergeSort`, `QuickSort`, `HeapSort`, `SortUtils` |
| **Member 3** | Performance Evaluation Engine & Swing GUI Interface | `com.sortingapp.performance.PerformanceEvaluator`<br>`com.sortingapp.gui.MainFrame`<br>`BarChartPanel`, `PerformanceTablePanel`, `DataTablePanel` |

---

### 🚀 Key Features

- **Dataset Upload & Column Auto-Detection**:
  - Uploads `.csv` files with validation for empty or invalid formats.
  - Automatically identifies numeric columns (integers, doubles, floating point numbers).
- **5 High-Performance Sorting Algorithms**:
  - Fully implemented with operation counter tracking (Comparisons and Swaps/Shifts).
  - Works on exact deep copies of the unsorted column to ensure unbiased benchmarks.
  - Supports both **Ascending** and **Descending** sort order.
- **Performance Evaluation & Winner Detection**:
  - High-resolution timing using `System.nanoTime()`.
  - Automatically highlights the **🏆 Best Performing Algorithm** based on shortest execution time.
  - Generates relative speedup factors (e.g. Merge Sort is $14.1\times$ faster than Insertion Sort).
- **Interactive Swing GUI & Visualizations**:
  - Multi-tabbed layout: Performance Metrics, Raw Data Preview, Sorted Data Preview, and Documentation.
  - Custom `BarChartPanel` rendering horizontal comparative bar chart with color-coded winner highlights.
  - Full dataset preview and sorted CSV exporter.

---

### 📁 Project Structure

```
SortingAlgorithm/
├── pom.xml
├── README.md
├── test-data/
│   ├── sample_students.csv       (Small dataset: 20 rows)
│   ├── sample_sales_500.csv      (Medium dataset: 500 rows)
│   └── sample_housing_2000.csv   (Large dataset: 2,000 rows)
└── src/
    ├── main/
    │   └── java/
    │       └── com/
    │           └── sortingapp/
    │               ├── Main.java
    │               ├── model/
    │               │   ├── CSVData.java
    │               │   └── SortingResult.java
    │               ├── csv/
    │               │   ├── CSVReader.java
    │               │   └── CSVWriter.java
    │               ├── sorting/
    │               │   ├── SortingAlgorithm.java
    │               │   ├── InsertionSort.java
    │               │   ├── ShellSort.java
    │               │   ├── MergeSort.java
    │               │   ├── QuickSort.java
    │               │   ├── HeapSort.java
    │               │   ├── SortFactory.java
    │               │   └── SortUtils.java
    │               ├── performance/
    │               │   └── PerformanceEvaluator.java
    │               └── gui/
    │                   ├── MainFrame.java
    │                   └── components/
    │                       ├── BarChartPanel.java
    │                       ├── DataTablePanel.java
    │                       └── PerformanceTablePanel.java
    └── test/
        └── java/
            └── com/
                └── sortingapp/
                    └── SortingAlgorithmTest.java
```

---

### 💻 How to Compile & Run

#### Option 1: Using Standard `javac` & `java` (Command Line)
1. **Compile all Java source files**:
   ```bash
   javac -d target/classes -sourcepath src/main/java src/main/java/com/sortingapp/Main.java src/main/java/com/sortingapp/**/*.java
   ```
2. **Run the Application**:
   ```bash
   java -cp target/classes com.sortingapp.Main
   ```

#### Option 2: Using Maven
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.sortingapp.Main"
```

---

### 🎤 Presentation Guide (Zoom Recording Script)

When recording your Zoom presentation:
1. **Self-Introduction**:
   - Each member introduces themselves: *"I'm [Name], my student number is [T085XX]."*
2. **Member 1 Speech (Dataset Upload)**:
   - Demonstrate clicking **📁 Upload CSV** and choosing a file from `test-data/`.
   - Explain `CSVReader.java` line-by-line: reading file buffers, parsing quotes, and filtering numeric columns in `CSVData.java`.
3. **Member 2 Speech (Sorting Algorithms)**:
   - Demonstrate selecting a column (e.g. `GPA` or `TotalRevenue`) and selecting Ascending/Descending.
   - Explain the implementations of Insertion Sort, Shell Sort, Merge Sort, Quick Sort, and Heap Sort in `com.sortingapp.sorting`.
4. **Member 3 Speech (Performance & GUI)**:
   - Click **⚡ Evaluate All**.
   - Point out the winner card 🏆, execution times in milliseconds/microseconds, operation counts, and relative speedups.
   - Show the custom `BarChartPanel` visual rendering and export the sorted dataset with **💾 Export CSV**.
