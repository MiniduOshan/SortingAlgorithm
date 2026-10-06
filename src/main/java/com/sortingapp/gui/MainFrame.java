package com.sortingapp.gui;

import com.sortingapp.csv.CSVReader;
import com.sortingapp.csv.CSVWriter;
import com.sortingapp.gui.components.BarChartPanel;
import com.sortingapp.gui.components.DataTablePanel;
import com.sortingapp.gui.components.PerformanceTablePanel;
import com.sortingapp.model.CSVData;

import com.sortingapp.model.SortingResult;
import com.sortingapp.performance.PerformanceEvaluator;
import com.sortingapp.sorting.SortFactory;
import com.sortingapp.sorting.SortingAlgorithm;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Main Application GUI Window for the Sorting Algorithm Performance Evaluator.
 * Combines CSV file loading, column selection, performance benchmarking,
 * visual chart rendering, and sorted dataset preview/export.
 * 
 * Group Assignment Contribution:
 * Member 3: User Interface Development & Application Integration.
 */
public class MainFrame extends JFrame {

    private CSVData currentCSVData;
    private double[] currentColumnValues;
    private PerformanceEvaluator.EvaluationSummary currentSummary;
    private File currentFile;

    // GUI Components
    private JLabel fileNameLabel;
    private JComboBox<String> columnComboBox;
    private JComboBox<String> orderComboBox;
    private JComboBox<String> singleAlgorithmComboBox;
    private JButton uploadBtn;
    private JButton evaluateAllBtn;
    private JButton runSingleBtn;
    private JButton exportBtn;

    private DataTablePanel rawDataTablePanel;
    private DataTablePanel sortedTablePanel;
    private PerformanceTablePanel performanceTablePanel;
    private BarChartPanel barChartPanel;
    private JLabel statusLabel;

    public MainFrame() {
        setTitle("IC4302 - Sorting Algorithm Performance Evaluation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);

        // Header Panel
        JPanel headerPanel = createHeaderPanel();

        // Control Panel
        JPanel controlPanel = createControlPanel();

        // Components initialization for tabs
        rawDataTablePanel = new DataTablePanel();
        sortedTablePanel = new DataTablePanel();
        performanceTablePanel = new PerformanceTablePanel();
        barChartPanel = new BarChartPanel();

        // Main Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Tab 1: Performance Evaluation
        JSplitPane perfSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, performanceTablePanel, barChartPanel);
        perfSplitPane.setResizeWeight(0.5);
        perfSplitPane.setDividerLocation(300);
        tabbedPane.addTab("📊 Performance Metrics & Chart", perfSplitPane);

        // Tab 2: Raw Dataset
        tabbedPane.addTab("📁 Raw Dataset Preview", rawDataTablePanel);

        // Tab 3: Sorted Data
        tabbedPane.addTab("🔢 Sorted Data Preview", sortedTablePanel);

        // Tab 4: Group Guidelines & Algorithm Details
        tabbedPane.addTab("ℹ️ Group & Algorithm Specs", createDocsPanel());

        // Status Bar
        statusLabel = new JLabel(" Ready. Please upload a dataset in .csv format.");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(210, 215, 220)),
                new EmptyBorder(6, 10, 6, 10)
        ));

        // Layout
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(controlPanel, BorderLayout.SOUTH);

        getContentPane().add(topContainer, BorderLayout.NORTH);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        getContentPane().add(statusLabel, BorderLayout.SOUTH);

        // Attach Event Handlers
        uploadBtn.addActionListener(e -> onUploadCSV());
        evaluateAllBtn.addActionListener(e -> onEvaluateAll());
        runSingleBtn.addActionListener(e -> onRunSingleAlgorithm());
        exportBtn.addActionListener(e -> onExportSortedCSV());
        columnComboBox.addActionListener(e -> onColumnSelected());

        // Initialize algorithm selector dropdown
        singleAlgorithmComboBox = new JComboBox<>();
        for (SortingAlgorithm algo : SortFactory.getAllAlgorithms()) {
            singleAlgorithmComboBox.addItem(algo.getName());
        }

        // Initialize state
        fileNameLabel = new JLabel("No file uploaded");
        fileNameLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        fileNameLabel.setForeground(new Color(100, 110, 120));

        updateControlStates(false);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(41, 128, 185)); // Deep Blue Header
        panel.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel titleLabel = new JLabel("University of Colombo - Faculty of Technology | Department of ICT");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(236, 240, 241));

        JLabel subtitleLabel = new JLabel("IC4302 – Programming III: Sorting Algorithm Performance Evaluator");
        subtitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        subtitleLabel.setForeground(Color.WHITE);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(subtitleLabel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createControlPanel() {
        JPanel mainControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        mainControlPanel.setBackground(new Color(245, 247, 250));
        mainControlPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 225, 230)));

        uploadBtn = new JButton("📁 Upload CSV");
        uploadBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        uploadBtn.setBackground(new Color(52, 152, 219));
        uploadBtn.setForeground(Color.BLACK);

        columnComboBox = new JComboBox<>();
        columnComboBox.setPreferredSize(new Dimension(180, 26));

        orderComboBox = new JComboBox<>(new String[]{"Ascending (Low to High)", "Descending (High to Low)"});
        orderComboBox.setPreferredSize(new Dimension(190, 26));

        evaluateAllBtn = new JButton("⚡ Evaluate All");
        evaluateAllBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        evaluateAllBtn.setBackground(new Color(46, 204, 113));
        evaluateAllBtn.setForeground(Color.BLACK);

        runSingleBtn = new JButton("▶ Run Single");
        runSingleBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        exportBtn = new JButton("💾 Export CSV");
        exportBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        mainControlPanel.add(uploadBtn);
        mainControlPanel.add(new JLabel("Numeric Column:"));
        mainControlPanel.add(columnComboBox);
        mainControlPanel.add(new JLabel("Order:"));
        mainControlPanel.add(orderComboBox);
        mainControlPanel.add(evaluateAllBtn);
        mainControlPanel.add(exportBtn);

        return mainControlPanel;
    }

    private JPanel createDocsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
        editorPane.setText("<html><body style='font-family: Segoe UI, sans-serif; font-size: 13px; color: #2c3e50;'>"
                + "<h2>IC4302 Group Assignment: Sorting Algorithm Performance Evaluation</h2>"
                + "<p>This Java Swing application benchmarks 5 fundamental sorting algorithms on numeric columns loaded from user CSV datasets.</p>"
                + "<h3>Member Task Delegation (Presentation Structure):</h3>"
                + "<ul>"
                + "<li><b>Member 1:</b> File Upload & CSV Dataset Handling (<code>com.sortingapp.csv</code>, <code>CSVData</code>, <code>CSVReader</code>)</li>"
                + "<li><b>Member 2:</b> Sorting Algorithm Implementations (Insertion, Shell, Merge, Quick, Heap Sort)</li>"
                + "<li><b>Member 3:</b> Performance Evaluation System & GUI Interface (<code>PerformanceEvaluator</code>, <code>BarChartPanel</code>, <code>MainFrame</code>)</li>"
                + "</ul>"
                + "<h3>Implemented Algorithms & Complexity:</h3>"
                + "<table border='1' cellpadding='6' cellspacing='0' style='border-collapse:collapse; border-color:#bdc3c7;'>"
                + "<tr bgcolor='#ecf0f1'><th>Algorithm</th><th>Best Time</th><th>Average Time</th><th>Worst Time</th><th>Space</th></tr>"
                + "<tr><td><b>Insertion Sort</b></td><td>O(n)</td><td>O(n²)</td><td>O(n²)</td><td>O(1)</td></tr>"
                + "<tr><td><b>Shell Sort</b></td><td>O(n log n)</td><td>O(n^1.5)</td><td>O(n²)</td><td>O(1)</td></tr>"
                + "<tr><td><b>Merge Sort</b></td><td>O(n log n)</td><td>O(n log n)</td><td>O(n log n)</td><td>O(n)</td></tr>"
                + "<tr><td><b>Quick Sort</b></td><td>O(n log n)</td><td>O(n log n)</td><td>O(n²)</td><td>O(log n)</td></tr>"
                + "<tr><td><b>Heap Sort</b></td><td>O(n log n)</td><td>O(n log n)</td><td>O(n log n)</td><td>O(1)</td></tr>"
                + "</table>"
                + "</body></html>");

        panel.add(new JScrollPane(editorPane), BorderLayout.CENTER);
        return panel;
    }

    private void onUploadCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a CSV Dataset File");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));

        // Default to project directory test-data if present
        File testDir = new File("test-data");
        if (testDir.exists()) {
            fileChooser.setCurrentDirectory(testDir);
        }

        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                CSVData data = CSVReader.readCSV(selectedFile);
                if (data.getNumericColumnIndices().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "The selected CSV file does not contain any numeric columns suitable for sorting.",
                            "No Numeric Columns", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                currentCSVData = data;
                currentFile = selectedFile;
                populateColumnDropdown();
                rawDataTablePanel.loadCSVData(currentCSVData, getSelectedColumnIndex());

                updateControlStates(true);
                statusLabel.setText(" Loaded file: " + selectedFile.getName() + " (" + data.getRowCount() + " rows, "
                        + data.getNumericColumnIndices().size() + " numeric columns available)");

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error reading CSV file:\n" + ex.getMessage(),
                        "File Read Error", JOptionPane.ERROR_MESSAGE);
                statusLabel.setText(" Error reading file: " + ex.getMessage());
            }
        }
    }

    private void populateColumnDropdown() {
        columnComboBox.removeAllItems();
        if (currentCSVData != null) {
            for (String colName : currentCSVData.getNumericColumnNames()) {
                columnComboBox.addItem(colName);
            }
        }
    }

    private int getSelectedColumnIndex() {
        if (currentCSVData == null || columnComboBox.getSelectedIndex() < 0) {
            return -1;
        }
        int numIndex = columnComboBox.getSelectedIndex();
        return currentCSVData.getNumericColumnIndices().get(numIndex);
    }

    private void onColumnSelected() {
        int colIndex = getSelectedColumnIndex();
        if (colIndex != -1 && currentCSVData != null) {
            currentColumnValues = currentCSVData.getColumnValuesAsDouble(colIndex);
            rawDataTablePanel.loadCSVData(currentCSVData, colIndex);
        }
    }

    private void onEvaluateAll() {
        if (currentCSVData == null || currentColumnValues == null) {
            JOptionPane.showMessageDialog(this, "Please upload a CSV dataset first.", "No Data", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ascending = orderComboBox.getSelectedIndex() == 0;
        statusLabel.setText(" Evaluating sorting algorithms...");

        // Perform benchmarks
        currentSummary = PerformanceEvaluator.evaluateAll(currentColumnValues, ascending);

        // Update Performance Table & Bar Chart
        List<SortingResult> results = currentSummary.getResults();
        SortingResult best = currentSummary.getBestResult();

        performanceTablePanel.updateResults(results, best);
        barChartPanel.setResults(results, best);

        // Update Sorted Data Preview tab with the winner's sorted data
        if (best != null && best.getSortedData() != null) {
            String colName = (String) columnComboBox.getSelectedItem();
            sortedTablePanel.loadSingleColumnData(colName, best.getSortedData());
        }

        exportBtn.setEnabled(true);
        statusLabel.setText(String.format(" Evaluation completed! Best Algorithm: %s (%s)",
                best.getAlgorithmName(), best.getFormattedExecutionTime()));
    }

    private void onRunSingleAlgorithm() {
        if (currentCSVData == null || currentColumnValues == null) {
            JOptionPane.showMessageDialog(this, "Please upload a CSV dataset first.", "No Data", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int algoIdx = singleAlgorithmComboBox.getSelectedIndex();
        if (algoIdx < 0) return;

        SortingAlgorithm algo = SortFactory.getAllAlgorithms().get(algoIdx);
        boolean ascending = orderComboBox.getSelectedIndex() == 0;

        SortingResult result = PerformanceEvaluator.evaluateSingle(algo, currentColumnValues, ascending);

        JOptionPane.showMessageDialog(this,
                String.format("Algorithm: %s\nExecution Time: %s\nComparisons: %,d\nSwaps/Shifts: %,d\nSorted Verified: %s",
                        result.getAlgorithmName(), result.getFormattedExecutionTime(),
                        result.getComparisonCount(), result.getSwapCount(),
                        result.isSortedCorrectly() ? "Yes" : "No"),
                "Single Algorithm Result", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onExportSortedCSV() {
        if (currentCSVData == null || currentSummary == null || currentSummary.getBestResult() == null) {
            JOptionPane.showMessageDialog(this, "No benchmark results available to export.", "Export Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Sorted CSV Dataset");
        fileChooser.setSelectedFile(new File("sorted_" + currentCSVData.getFileName()));
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));

        int choice = fileChooser.showSaveDialog(this);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File targetFile = fileChooser.getSelectedFile();
            if (!targetFile.getName().toLowerCase().endsWith(".csv")) {
                targetFile = new File(targetFile.getAbsolutePath() + ".csv");
            }

            try {
                int colIndex = getSelectedColumnIndex();
                double[] sortedValues = currentSummary.getBestResult().getSortedData();
                CSVWriter.exportSortedCSV(targetFile, currentCSVData, colIndex, sortedValues);

                JOptionPane.showMessageDialog(this,
                        "Sorted dataset successfully exported to:\n" + targetFile.getAbsolutePath(),
                        "Export Successful", JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText(" Exported sorted CSV to: " + targetFile.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error saving file:\n" + ex.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateControlStates(boolean dataLoaded) {
        columnComboBox.setEnabled(dataLoaded);
        orderComboBox.setEnabled(dataLoaded);
        evaluateAllBtn.setEnabled(dataLoaded);
        runSingleBtn.setEnabled(dataLoaded);
        exportBtn.setEnabled(false);
    }
}
