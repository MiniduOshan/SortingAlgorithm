# IC4302 – Programming III Group Assignment
## Java Application for Sorting Algorithm Performance Evaluation
**Faculty of Technology | Department of ICT | University of Colombo**

---

### 📌 Project Overview
This project is a high-performance Java Swing application built for evaluating and benchmarking the execution time and efficiency of five fundamental sorting algorithms:
1. **Insertion Sort** — $O(n^2)$ comparison sort.
2. **Shell Sort** — $O(n \log n)$ to $O(n^{1.5})$ diminishing gap sort.
3. **Merge Sort** — $O(n \log n)$ divide-and-conquer sort.
4. **Quick Sort** — $O(n \log n)$ median-of-three 3-way partitioned sort (Dutch National Flag).
5. **Heap Sort** — $O(n \log n)$ binary max/min heap sort.

Users can upload custom datasets in `.csv` format (including Kaggle datasets), dynamically select any valid numeric column, evaluate algorithm execution times down to nanosecond precision using median-of-5 benchmarking, visualize performance via custom bar charts, and export sorted datasets back to CSV.

---

### 👥 Group Guidelines & Task Delegation (Presentation Structure)

In accordance with assignment guidelines, the codebase is modularized across 3 primary areas of contribution for presentation recording:

| Group Member | Primary Responsibilities | Java Packages / Classes |
| :--- | :--- | :--- |
| **Member 1** | CSV Dataset Upload, Parsing & Data Model | `com.sortingapp.model.CSVData`<br>`com.sortingapp.csv.CSVReader`<br>`com.sortingapp.csv.CSVWriter` |
| **Member 2** | Implementation of 5 Sorting Algorithms & Verification | `com.sortingapp.sorting.SortingAlgorithm`<br>`InsertionSort`, `ShellSort`, `MergeSort`, `QuickSort`, `HeapSort`, `SortUtils` |
| **Member 3** | Performance Evaluation Engine & Swing GUI Interface | `com.sortingapp.performance.PerformanceEvaluator`<br>`com.sortingapp.gui.MainFrame`<br>`BarChartPanel`, `PerformanceTablePanel` |

---

### 🚀 Key Features

- **Dataset Upload & Column Auto-Detection**:
  - Uploads `.csv` files with validation for empty files, headerless data, and malformed CSV rows.
  - Strictly validates numeric columns across all rows, safely rejecting missing values, empty strings, non-numeric text, `NaN`, and infinite values without silent zero-filling.
  - Automatically identifies and indicates valid numeric columns in the column selector.
- **5 High-Performance Sorting Algorithms**:
  - Fully custom implementations with operation counter tracking (Comparisons and Swaps/Shifts).
  - Quick Sort implements median-of-three 3-way partitioning to safely handle datasets with identical/duplicate values without excessive recursion or stack overflow.
  - Works on exact independent copies of unsorted data to ensure unbiased benchmarks.
  - Algorithm engine cleanly supports both **Ascending** and **Descending** sort orders.
- **Performance Evaluation & Winner Detection**:
  - High-resolution timing using `System.nanoTime()`.
  - Executes 5 benchmark runs per algorithm and evaluates the median run time to minimize JVM/OS timing noise.
  - Automatically highlights the **🏆 Fastest Algorithm** based on shortest median execution time.
  - Ranks all algorithms from fastest to slowest with formatted times, comparison counts, and relative speedup ratios.
- **Interactive Swing GUI & Visualizations**:
  - Unified modern light dashboard layout featuring top control panel, performance summary card, and metrics table.
  - Custom `BarChartPanel` rendering responsive vertical comparative bar chart dynamically scaled in nanoseconds, microseconds, milliseconds, or seconds.
  - Responsive background benchmarking using `SwingWorker` to prevent UI freezing during large sorts.
  - RFC 4180 compliant CSV exporter with proper header escaping, field quoting, and complete row preservation.

---

### 📁 Project Structure

```
SortingAlgorithm/
├── pom.xml
├── README.md
├── test-data/
│   ├── iris.csv                  (Iris dataset: 150 rows)
│   ├── sample_students.csv       (Small dataset: 20 rows)
│   ├── sample_sales_500.csv      (Medium dataset: 500 rows)
│   ├── sample_housing_2000.csv   (Large dataset: 2,000 rows)
│   └── students_performance.csv  (1,000 rows with numeric scores)
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

### 💻 How to Compile & Run

#### Option 1: Using Standard `javac` & `java` (Command Line)
1. **Compile all Java source files**:
   ```bash
   javac -d target/classes -sourcepath src/main/java src/main/java/com/sortingapp/Main.java
   ```
2. **Run the Application**:
   ```bash
   java -cp target/classes com.sortingapp.Main
   ```

#### Option 2: Using Maven
```bash
mvn clean compile
mvn test
mvn exec:java -Dexec.mainClass="com.sortingapp.Main"
```

---

### 🎤 Presentation Guide (Zoom Recording Script)

When recording your Zoom presentation:
1. **Self-Introduction**:
   - Each member introduces themselves: *"I'm [Name], my student number is [T085XX]."*
2. **Member 1 Speech (Dataset Upload & Handling)**:
   - Demonstrate clicking **Browse CSV** and choosing a file from `test-data/` (e.g., `sample_students.csv` or `iris.csv`).
   - Explain `CSVReader.java` line-by-line: reading file buffers, parsing RFC 4180 quotes, and validating numeric columns in `CSVData.java`.
   - Show how non-numeric or missing data is handled safely without silent zero replacement.
3. **Member 2 Speech (Sorting Algorithms)**:
   - Demonstrate selecting a valid numeric column (e.g. `GPA` or `TotalRevenue`).
   - Explain the implementations of Insertion Sort, Shell Sort, Merge Sort, Quick Sort (including median-of-three 3-way partition for duplicate values), and Heap Sort in `com.sortingapp.sorting`.
   - Highlight array copy isolation and verification with `SortUtils.isSorted()`.
4. **Member 3 Speech (Performance & GUI)**:
   - Click **Run Sorting Algorithms**.
   - Point out the winner card 🏆, execution times in milliseconds/microseconds, operation counts (comparisons, swaps/shifts), and ranking.
   - Show the custom `BarChartPanel` visual rendering and export the sorted dataset with **Export Sorted CSV**.
