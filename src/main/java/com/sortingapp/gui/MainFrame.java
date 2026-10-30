package com.sortingapp.gui;

import com.sortingapp.csv.CSVReader;
import com.sortingapp.csv.CSVWriter;
import com.sortingapp.gui.components.BarChartPanel;
import com.sortingapp.gui.components.PerformanceTablePanel;
import com.sortingapp.model.CSVData;
import com.sortingapp.model.SortingResult;
import com.sortingapp.performance.PerformanceEvaluator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Main application window for the Sorting Algorithm Performance Evaluator.
 *
 * Provides CSV selection, sorting controls, performance visualization,
 * and CSV export using Java Swing.
 *
 * Member 3 Contribution:
 * - Graphical user interface development
 * - Performance evaluation integration
 * - Results table, chart, and fastest algorithm display
 * - Background execution using SwingWorker
 */
public class MainFrame extends JFrame {

    // Theme
    private static final Color APP_BG = new Color(246, 249, 253);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER = new Color(218, 227, 240);
    private static final Color TEXT_PRIMARY = new Color(20, 32, 67);
    private static final Color TEXT_SECONDARY = new Color(91, 108, 145);
    private static final Color BLUE = new Color(38, 132, 255);
    private static final Color PURPLE = new Color(125, 73, 255);
    private static final Color PINK = new Color(219, 57, 205);

    private static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);

    // Application data
    private CSVData currentCSVData;
    private double[] currentColumnValues;
    private PerformanceEvaluator.EvaluationSummary currentSummary;

    // GUI components
    private JLabel fileNameLabel;
    private JLabel datasetInfoLabel;
    private JLabel columnHintLabel;
    private JComboBox<String> columnComboBox;
    private JButton uploadBtn;
    private JButton evaluateAllBtn;
    private JButton exportBtn;

    private PerformanceTablePanel performanceTablePanel;
    private BarChartPanel barChartPanel;

    private JLabel winnerNameLabel;
    private JLabel winnerTimeLabel;
    private JLabel winnerDetailsLabel;

    private boolean suppressSelectionEvents = false;

    public MainFrame() {
        configureFrame();
        initialiseComponents();
        buildLayout();
        attachEventHandlers();
        updateControlStates(false);
    }

    private void configureFrame() {
        setTitle("Sorting Algorithm Performance Evaluator");

        // Use the same application logo in the Windows title bar/taskbar.
        ImageIcon appIcon = loadScaledIcon("/icons/app_icon.png", 64, 64);
        if (appIcon != null) {
            setIconImage(appIcon.getImage());
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1440, 900);
        setMinimumSize(new Dimension(1180, 760));
        setLocationRelativeTo(null);
        getContentPane().setBackground(APP_BG);
        setLayout(new BorderLayout());
    }

    private void initialiseComponents() {
        performanceTablePanel = new PerformanceTablePanel();
        barChartPanel = new BarChartPanel();

        fileNameLabel = new JLabel("No CSV file selected");
        fileNameLabel.setFont(FONT_NORMAL);
        fileNameLabel.setForeground(TEXT_SECONDARY);

        datasetInfoLabel = new JLabel("Upload a dataset to begin");
        datasetInfoLabel.setFont(FONT_SMALL);
        datasetInfoLabel.setForeground(TEXT_SECONDARY);

        columnHintLabel = new JLabel("Choose the column to sort");
        columnHintLabel.setFont(FONT_SMALL);
        columnHintLabel.setForeground(TEXT_SECONDARY);

        columnComboBox = new JComboBox<>();
        styleComboBox(columnComboBox);
        columnComboBox.setPreferredSize(new Dimension(220, 38));

        uploadBtn = new GradientButton("Browse CSV", BLUE, new Color(30, 111, 230));
        uploadBtn.setPreferredSize(new Dimension(130, 40));

        evaluateAllBtn = new GradientButton("Run Sorting Algorithms", PURPLE, BLUE);
        evaluateAllBtn.setIcon(new PlayIcon(15, Color.WHITE));
        evaluateAllBtn.setIconTextGap(10);
        evaluateAllBtn.setHorizontalAlignment(SwingConstants.CENTER);
        evaluateAllBtn.setPreferredSize(new Dimension(260, 50));

        exportBtn = createSecondaryButton("Export Sorted CSV");
        exportBtn.setIcon(new ExportIcon(15, TEXT_PRIMARY));
        exportBtn.setIconTextGap(8);
        exportBtn.setPreferredSize(new Dimension(190, 40));

        winnerNameLabel = new JLabel("Waiting for evaluation");
        winnerNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 25));
        winnerNameLabel.setForeground(TEXT_PRIMARY);

        winnerTimeLabel = new JLabel("--");
        winnerTimeLabel.setFont(new Font("Segoe UI", Font.BOLD, 34));
        winnerTimeLabel.setForeground(PURPLE);

        winnerDetailsLabel = new JLabel("Run all five algorithms to identify the fastest one.");
        winnerDetailsLabel.setFont(FONT_NORMAL);
        winnerDetailsLabel.setForeground(TEXT_SECONDARY);

    }

    private void buildLayout() {
        JPanel root = new JPanel(new BorderLayout(0, 10));
        root.setBackground(APP_BG);
        root.setBorder(new EmptyBorder(12, 20, 8, 20));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(createHeaderPanel());
        top.add(Box.createVerticalStrut(10));
        top.add(createControlPanel());

        root.add(top, BorderLayout.NORTH);
        root.add(createDashboardPanel(), BorderLayout.CENTER);

        setContentPane(root);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(18, 0));
        panel.setOpaque(false);

        JComponent logo = createHeaderLogo();

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Sorting Algorithm Performance Evaluator");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(TEXT_PRIMARY);

        JLabel subtitle = new JLabel(
                "Upload a CSV file, select a numeric column and compare the performance of five sorting algorithms."
        );
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(TEXT_SECONDARY);

        textPanel.add(Box.createVerticalGlue());
        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subtitle);
        textPanel.add(Box.createVerticalGlue());

        JPanel coursePanel = new JPanel();
        coursePanel.setOpaque(false);
        coursePanel.setLayout(new BoxLayout(coursePanel, BoxLayout.Y_AXIS));

        JLabel course = new JLabel("IC4302 - Programming III", SwingConstants.RIGHT);
        course.setFont(new Font("Segoe UI", Font.BOLD, 13));
        course.setForeground(TEXT_PRIMARY);
        course.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel department = new JLabel("Department of ICT | Faculty of Technology", SwingConstants.RIGHT);
        department.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        department.setForeground(TEXT_SECONDARY);
        department.setAlignmentX(Component.RIGHT_ALIGNMENT);

        coursePanel.add(Box.createVerticalGlue());
        coursePanel.add(course);
        coursePanel.add(Box.createVerticalStrut(5));
        coursePanel.add(department);
        coursePanel.add(Box.createVerticalGlue());

        panel.add(logo, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);
        panel.add(coursePanel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createControlPanel() {
        RoundedPanel card = new RoundedPanel(CARD_BG, 20);
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel controls = new JPanel(new GridBagLayout());
        controls.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridx = 0;
        gbc.weightx = 1.25;
        controls.add(createFileSelectorBlock(), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.insets = new Insets(3, 16, 3, 16);
        controls.add(createVerticalSeparator(), gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.8;
        gbc.insets = new Insets(0, 0, 0, 0);
        controls.add(createColumnBlock(), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0;
        gbc.insets = new Insets(3, 16, 3, 16);
        controls.add(createVerticalSeparator(), gbc);

        gbc.gridx = 4;
        gbc.weightx = 0.9;
        gbc.insets = new Insets(0, 0, 0, 0);
        controls.add(createExportBlock(), gbc);

        card.add(controls, BorderLayout.CENTER);

        JPanel runButtonPanel = new JPanel(new BorderLayout());
        runButtonPanel.setOpaque(false);
        runButtonPanel.setBorder(new EmptyBorder(12, 0, 12, 0));
        runButtonPanel.add(evaluateAllBtn, BorderLayout.CENTER);

        card.add(runButtonPanel, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createFileSelectorBlock() {
        JPanel block = new JPanel(new BorderLayout(12, 0));
        block.setOpaque(false);

        ControlIconPanel icon = new ControlIconPanel(ControlIconPanel.IconType.FILE);
        icon.setPreferredSize(new Dimension(54, 54));
        block.add(icon, BorderLayout.WEST);

        JPanel content = new JPanel(new BorderLayout(10, 5));
        content.setOpaque(false);

        JLabel label = new JLabel("CSV File");
        label.setFont(FONT_LABEL);
        label.setForeground(TEXT_PRIMARY);

        JPanel fileRow = new JPanel(new BorderLayout(10, 0));
        fileRow.setOpaque(false);

        RoundedPanel fileDisplay = new RoundedPanel(new Color(249, 251, 255), 12);
        fileDisplay.setLayout(new BorderLayout());
        fileDisplay.setBorder(new EmptyBorder(8, 12, 8, 12));
        fileDisplay.add(fileNameLabel, BorderLayout.CENTER);

        fileRow.add(fileDisplay, BorderLayout.CENTER);
        fileRow.add(uploadBtn, BorderLayout.EAST);

        content.add(label, BorderLayout.NORTH);
        content.add(fileRow, BorderLayout.CENTER);
        content.add(datasetInfoLabel, BorderLayout.SOUTH);
        block.add(content, BorderLayout.CENTER);
        return block;
    }

    private JPanel createColumnBlock() {
        JPanel block = new JPanel(new BorderLayout(12, 0));
        block.setOpaque(false);

        ControlIconPanel icon = new ControlIconPanel(ControlIconPanel.IconType.DATABASE);
        icon.setPreferredSize(new Dimension(54, 54));
        block.add(icon, BorderLayout.WEST);

        JPanel content = new JPanel(new BorderLayout(0, 7));
        content.setOpaque(false);

        JLabel label = new JLabel("Numeric Column");
        label.setFont(FONT_LABEL);
        label.setForeground(TEXT_PRIMARY);

        content.add(label, BorderLayout.NORTH);
        content.add(columnComboBox, BorderLayout.CENTER);
        content.add(columnHintLabel, BorderLayout.SOUTH);
        block.add(content, BorderLayout.CENTER);
        return block;
    }

    private JPanel createExportBlock() {
        JPanel block = new JPanel(new BorderLayout(12, 0));
        block.setOpaque(false);

        ControlIconPanel icon = new ControlIconPanel(ControlIconPanel.IconType.EXPORT);
        icon.setPreferredSize(new Dimension(54, 54));
        block.add(icon, BorderLayout.WEST);

        JPanel content = new JPanel(new BorderLayout(0, 7));
        content.setOpaque(false);

        JLabel label = new JLabel("Export Results");
        label.setFont(FONT_LABEL);
        label.setForeground(TEXT_PRIMARY);

        JLabel hint = new JLabel("Available after running the algorithms");
        hint.setFont(FONT_SMALL);
        hint.setForeground(TEXT_SECONDARY);

        content.add(label, BorderLayout.NORTH);
        content.add(exportBtn, BorderLayout.CENTER);
        content.add(hint, BorderLayout.SOUTH);
        block.add(content, BorderLayout.CENTER);
        return block;
    }

    private JSeparator createVerticalSeparator() {
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setForeground(BORDER);
        separator.setPreferredSize(new Dimension(1, 58));
        return separator;
    }

    // Dashboard
    private JPanel createDashboardPanel() {
        JPanel dashboard = new JPanel(new GridBagLayout());
        dashboard.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // Top row: performance table and fastest-algorithm summary.
        gbc.gridy = 0;
        gbc.weighty = 0.0;

        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 1.08;
        gbc.insets = new Insets(0, 0, 8, 8);
        JPanel performanceCard = createPerformanceResultsCard();
        performanceCard.setPreferredSize(new Dimension(100, 235));
        performanceCard.setMinimumSize(new Dimension(100, 225));
        dashboard.add(performanceCard, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.92;
        gbc.insets = new Insets(0, 8, 8, 0);
        JPanel winnerCard = createWinnerCard();
        winnerCard.setPreferredSize(new Dimension(100, 235));
        winnerCard.setMinimumSize(new Dimension(100, 225));
        dashboard.add(winnerCard, gbc);

        // Display the performance chart across the full dashboard width.
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(8, 0, 0, 0);
        dashboard.add(createChartCard(), gbc);

        return dashboard;
    }

    private JPanel createPerformanceResultsCard() {
        RoundedPanel card = new RoundedPanel(CARD_BG, 20);
        card.setLayout(new BorderLayout(0, 6));
        card.setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel heading = createSectionHeading(
                "Performance Results",
                "Execution time and metrics for all five sorting algorithms."
        );

        performanceTablePanel.setBorder(null);
        performanceTablePanel.setBackground(CARD_BG);

        card.add(heading, BorderLayout.NORTH);
        card.add(performanceTablePanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createWinnerCard() {
        GradientWinnerPanel card = new GradientWinnerPanel();
        card.setLayout(new BorderLayout(18, 0));
        card.setBorder(new EmptyBorder(18, 22, 18, 22));

        TrophyGraphicPanel trophyGraphic = new TrophyGraphicPanel();
        trophyGraphic.setPreferredSize(new Dimension(175, 165));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel smallTitle = new JLabel("Fastest Algorithm");
        smallTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        smallTitle.setForeground(TEXT_SECONDARY);

        winnerNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 27));
        winnerTimeLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));

        text.add(Box.createVerticalGlue());
        text.add(smallTitle);
        text.add(Box.createVerticalStrut(5));
        text.add(winnerNameLabel);
        text.add(Box.createVerticalStrut(5));
        text.add(winnerTimeLabel);
        text.add(Box.createVerticalStrut(7));
        text.add(winnerDetailsLabel);
        text.add(Box.createVerticalGlue());

        card.add(trophyGraphic, BorderLayout.WEST);
        card.add(text, BorderLayout.CENTER);
        return card;
    }

    private JPanel createChartCard() {
        RoundedPanel card = new RoundedPanel(CARD_BG, 20);
        card.setLayout(new BorderLayout(0, 2));
        card.setBorder(new EmptyBorder(8, 10, 8, 10));

        barChartPanel.setBorder(null);
        barChartPanel.setBackground(CARD_BG);
        barChartPanel.setPreferredSize(new Dimension(1100, 410));

        JPanel heading = createSectionHeading(
                "Execution Time Comparison",
                "Lower execution time indicates better performance for this dataset."
        );

        card.add(heading, BorderLayout.NORTH);
        card.add(barChartPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createSectionHeading(String titleText, String subtitleText) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(titleText);
        title.setFont(FONT_SECTION);
        title.setForeground(TEXT_PRIMARY);

        JLabel subtitle = new JLabel(subtitleText);
        subtitle.setFont(FONT_SMALL);
        subtitle.setForeground(TEXT_SECONDARY);

        panel.add(title);
        panel.add(Box.createVerticalStrut(2));
        panel.add(subtitle);
        return panel;
    }

    // Event handling
    private void attachEventHandlers() {
        uploadBtn.addActionListener(e -> onUploadCSV());
        evaluateAllBtn.addActionListener(e -> onEvaluateAll());
        exportBtn.addActionListener(e -> onExportSortedCSV());

        columnComboBox.addActionListener(e -> {
            if (!suppressSelectionEvents) {
                onColumnSelected();
            }
        });
    }

    private void onUploadCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a CSV Dataset File");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));

        File testDir = new File("test-data");
        if (testDir.exists()) {
            fileChooser.setCurrentDirectory(testDir);
        }

        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selectedFile = fileChooser.getSelectedFile();

        try {
            CSVData data = CSVReader.readCSV(selectedFile);

            currentCSVData = data;
            currentSummary = null;

            suppressSelectionEvents = true;
            populateColumnDropdown();
            suppressSelectionEvents = false;

            fileNameLabel.setText(selectedFile.getName());
            datasetInfoLabel.setText(String.format(
                    "%,d rows  |  %d columns  |  %d valid numeric",
                    data.getRowCount(),
                    data.getColumnCount(),
                    data.getNumericColumnIndices().size()
            ));

            if (data.getNumericColumnIndices().isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "The selected CSV file does not contain any valid numeric columns suitable for sorting.\n\n"
                        + "All columns were inspected and contain non-numeric text, missing values, or non-finite data.",
                        "No Valid Numeric Columns",
                        JOptionPane.WARNING_MESSAGE
                );
            }

            onColumnSelected();
            resetEvaluationVisuals();
            updateControlStates(true);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error reading CSV file:\n" + ex.getMessage(),
                    "File Read Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void populateColumnDropdown() {
        columnComboBox.removeAllItems();

        if (currentCSVData != null) {
            for (int i = 0; i < currentCSVData.getColumnCount(); i++) {
                String header = currentCSVData.getHeaders().get(i);
                boolean isNumeric = currentCSVData.isColumnNumeric(i);
                columnComboBox.addItem(isNumeric ? header : header + " (Non-numeric)");
            }

            // Select the first valid numeric column by default if available
            List<Integer> numericIndices = currentCSVData.getNumericColumnIndices();
            if (!numericIndices.isEmpty()) {
                columnComboBox.setSelectedIndex(numericIndices.get(0));
            } else if (columnComboBox.getItemCount() > 0) {
                columnComboBox.setSelectedIndex(0);
            }
        }
    }

    private int getSelectedColumnIndex() {
        if (currentCSVData == null || columnComboBox.getSelectedIndex() < 0) {
            return -1;
        }
        return columnComboBox.getSelectedIndex();
    }

    /**
     * Validates the selected column and clears previous results
     * when the column selection changes.
     */
    private void onColumnSelected() {
        int colIndex = getSelectedColumnIndex();

        if (colIndex == -1 || currentCSVData == null) {
            columnHintLabel.setText("Choose the column to sort");
            columnHintLabel.setForeground(TEXT_SECONDARY);
            columnHintLabel.setToolTipText(null);
            updateControlStates(currentCSVData != null);
            return;
        }

        invalidatePreviousEvaluation();

        if (currentCSVData.isColumnNumeric(colIndex)) {
            currentColumnValues = currentCSVData.getColumnValuesAsDouble(colIndex);
            columnHintLabel.setText("Valid numeric column — ready to sort");
            columnHintLabel.setForeground(new Color(22, 128, 79));
            columnHintLabel.setToolTipText(null);
            evaluateAllBtn.setEnabled(true);
        } else {
            currentColumnValues = null;
            String reason = currentCSVData.getColumnValidationError(colIndex);
            String shortReason = (reason != null && reason.length() > 34) ? reason.substring(0, 31) + "..." : reason;
            columnHintLabel.setText(shortReason != null ? shortReason : "Not sortable");
            columnHintLabel.setForeground(new Color(220, 38, 38));
            columnHintLabel.setToolTipText(reason);
            evaluateAllBtn.setEnabled(false);

            String colName = currentCSVData.getHeaders().get(colIndex);
            JOptionPane.showMessageDialog(
                    this,
                    "The column '" + colName + "' cannot be selected for sorting.\n\nReason:\n" + reason,
                    "Column Not Sortable",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    /**
     * Executes all sorting algorithms in a background thread
     * using SwingWorker to keep the GUI responsive.
     */
    private void onEvaluateAll() {
        int colIndex = getSelectedColumnIndex();
        if (currentCSVData == null || colIndex < 0 || !currentCSVData.isColumnNumeric(colIndex) || currentColumnValues == null || currentColumnValues.length == 0) {
            String reason = (currentCSVData != null && colIndex >= 0)
                    ? currentCSVData.getColumnValidationError(colIndex)
                    : "Please upload a CSV dataset and select a valid numeric column first.";
            JOptionPane.showMessageDialog(
                    this,
                    "Cannot execute sorting on the selected column:\n" + reason,
                    "Invalid Column",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        boolean ascending = true;
        double[] valuesForEvaluation = currentColumnValues.clone();

        setEvaluationRunning(true);

        SwingWorker<PerformanceEvaluator.EvaluationSummary, Void> worker =
                new SwingWorker<>() {
                    @Override
                    protected PerformanceEvaluator.EvaluationSummary doInBackground() {
                        return PerformanceEvaluator.evaluateAll(valuesForEvaluation, ascending);
                    }

                    @Override
                    protected void done() {
                        try {
                            currentSummary = get();
                            displayEvaluationResults(currentSummary);
                        } catch (Exception ex) {
                            currentSummary = null;
                            resetEvaluationVisuals();
                            JOptionPane.showMessageDialog(
                                    MainFrame.this,
                                    "An error occurred while evaluating the algorithms:\n" + ex.getMessage(),
                                    "Evaluation Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        } finally {
                            setEvaluationRunning(false);
                        }
                    }
                };

        worker.execute();
    }

    /**
     * Updates the results table, comparison chart, and
     * fastest algorithm display after evaluation.
     */
    private void displayEvaluationResults(PerformanceEvaluator.EvaluationSummary summary) {
        if (summary == null) {
            resetEvaluationVisuals();
            return;
        }

        List<SortingResult> results = summary.getResults();
        SortingResult best = summary.getBestResult();

        performanceTablePanel.updateResults(results, best);
        barChartPanel.setResults(results, best);

        if (best != null) {
            winnerNameLabel.setText(best.getAlgorithmName());
            winnerTimeLabel.setText(best.getFormattedExecutionTime());
            winnerDetailsLabel.setText(String.format(
                    "Fastest result for %,d values in the selected column.",
                    best.getItemCount()
            ));

            exportBtn.setEnabled(true);
        } else {
            resetEvaluationVisuals();
        }
    }

    /**
     * Exports the dataset sorted by the fastest algorithm
     * to a user-selected CSV file.
     */
    private void onExportSortedCSV() {
        if (currentCSVData == null || currentSummary == null || currentSummary.getBestResult() == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "No current benchmark result is available to export.",
                    "Export Warning",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Sorted CSV Dataset");
        fileChooser.setSelectedFile(new File("sorted_" + currentCSVData.getFileName()));
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));

        int choice = fileChooser.showSaveDialog(this);
        if (choice != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File targetFile = fileChooser.getSelectedFile();
        if (!targetFile.getName().toLowerCase().endsWith(".csv")) {
            targetFile = new File(targetFile.getAbsolutePath() + ".csv");
        }

        try {
            int colIndex = getSelectedColumnIndex();
            double[] sortedValues = currentSummary.getBestResult().getSortedData();
            CSVWriter.exportSortedCSV(targetFile, currentCSVData, colIndex, sortedValues);

            JOptionPane.showMessageDialog(
                    this,
                    "Sorted dataset successfully exported to:\n" + targetFile.getAbsolutePath(),
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error saving file:\n" + ex.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Result/state management
    private void invalidatePreviousEvaluation() {
        currentSummary = null;
        resetEvaluationVisuals();
        exportBtn.setEnabled(false);
    }

    private void resetEvaluationVisuals() {
        performanceTablePanel.updateResults(null, null);
        barChartPanel.setResults(null, null);

        winnerNameLabel.setText("Waiting for evaluation");
        winnerTimeLabel.setText("--");
        winnerDetailsLabel.setText("Run all five algorithms to identify the fastest one.");

        exportBtn.setEnabled(false);
    }

    private void updateControlStates(boolean dataLoaded) {
        columnComboBox.setEnabled(dataLoaded);
        int colIndex = getSelectedColumnIndex();
        boolean canEvaluate = dataLoaded && currentCSVData != null && colIndex >= 0 && currentCSVData.isColumnNumeric(colIndex);
        evaluateAllBtn.setEnabled(canEvaluate);
        exportBtn.setEnabled(false);
    }

    private void setEvaluationRunning(boolean running) {
        uploadBtn.setEnabled(!running);
        columnComboBox.setEnabled(!running && currentCSVData != null);
        int colIndex = getSelectedColumnIndex();
        boolean canEvaluate = !running && currentCSVData != null && colIndex >= 0 && currentCSVData.isColumnNumeric(colIndex);
        evaluateAllBtn.setEnabled(canEvaluate);
        exportBtn.setEnabled(!running && currentSummary != null && currentSummary.getBestResult() != null);

        evaluateAllBtn.setText(running ? "Evaluating..." : "Run Sorting Algorithms");
    }

    /**
     * Loads the application logo or creates a fallback graphic if unavailable.
     */
    private JComponent createHeaderLogo() {
        ImageIcon icon = loadScaledIcon("/icons/app_icon.png", 78, 78);

        if (icon != null) {
            JLabel logoLabel = new JLabel(icon);
            logoLabel.setPreferredSize(new Dimension(78, 78));
            logoLabel.setMinimumSize(new Dimension(78, 78));
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            logoLabel.setVerticalAlignment(SwingConstants.CENTER);
            return logoLabel;
        }

        JPanel fallbackLogo = new GradientBadgePanel();
        fallbackLogo.setPreferredSize(new Dimension(78, 78));
        fallbackLogo.setMinimumSize(new Dimension(78, 78));
        return fallbackLogo;
    }

    /**
     * Loads and smoothly scales an image from src/main/resources.
     */
    private ImageIcon loadScaledIcon(String resourcePath, int width, int height) {
        java.net.URL resource = getClass().getResource(resourcePath);
        if (resource == null) {
            return null;
        }

        Image original = new ImageIcon(resource).getImage();
        Image scaled = original.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    // Styling helpers
    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(FONT_NORMAL);
        comboBox.setForeground(TEXT_PRIMARY);
        comboBox.setBackground(Color.WHITE);
        comboBox.setFocusable(false);
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_LABEL);
        button.setForeground(TEXT_PRIMARY);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(8, 14, 8, 14)
        ));
        return button;
    }

    // Small custom-painted components used by the light dashboard
    private static class ControlIconPanel extends JPanel {
        enum IconType { FILE, DATABASE, EXPORT }

        private final IconType type;

        ControlIconPanel(IconType type) {
            this.type = type;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight()) - 6;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            Color start;
            Color end;
            if (type == IconType.FILE) {
                start = new Color(29, 147, 255);
                end = new Color(36, 92, 235);
            } else if (type == IconType.DATABASE) {
                start = new Color(183, 62, 255);
                end = new Color(104, 56, 240);
            } else {
                start = new Color(44, 196, 125);
                end = new Color(27, 150, 96);
            }

            g2.setPaint(new GradientPaint(x, y, start, x + size, y + size, end));
            g2.fillOval(x, y, size, size);
            g2.setColor(new Color(255, 255, 255, 55));
            g2.fillOval(x + 4, y + 4, size - 8, size / 2);

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;

            if (type == IconType.FILE) {
                int fw = 18;
                int fh = 23;
                int fx = cx - fw / 2;
                int fy = cy - fh / 2;
                g2.drawRoundRect(fx, fy, fw, fh, 2, 2);
                g2.drawLine(fx + 11, fy, fx + 18, fy + 7);
                g2.drawLine(fx + 11, fy, fx + 11, fy + 7);
                g2.drawLine(fx + 11, fy + 7, fx + 18, fy + 7);
                g2.drawLine(fx + 5, fy + 13, fx + 13, fy + 13);
                g2.drawLine(fx + 5, fy + 17, fx + 13, fy + 17);
            } else if (type == IconType.DATABASE) {
                int dw = 22;
                int dh = 20;
                int dx = cx - dw / 2;
                int dy = cy - dh / 2;
                g2.drawOval(dx, dy, dw, 7);
                g2.drawArc(dx, dy + 5, dw, 7, 180, 180);
                g2.drawArc(dx, dy + 11, dw, 7, 180, 180);
                g2.drawLine(dx, dy + 3, dx, dy + 15);
                g2.drawLine(dx + dw, dy + 3, dx + dw, dy + 15);
            } else {
                // Export/download symbol.
                g2.drawLine(cx, cy - 12, cx, cy + 5);
                g2.drawLine(cx - 6, cy, cx, cy + 6);
                g2.drawLine(cx + 6, cy, cx, cy + 6);
                g2.drawRoundRect(cx - 10, cy + 9, 20, 5, 2, 2);
            }

            g2.dispose();
        }
    }

    private static class RoundedPanel extends JPanel {
        private final Color backgroundColor;
        private final int radius;

        RoundedPanel(Color backgroundColor, int radius) {
            this.backgroundColor = backgroundColor;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Soft shadow
            g2.setColor(new Color(20, 46, 90, 13));
            g2.fillRoundRect(2, 3, getWidth() - 4, getHeight() - 4, radius, radius);

            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, radius, radius);

            g2.setColor(BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 4, getHeight() - 4, radius, radius);
            g2.dispose();

            super.paintComponent(g);
        }
    }

    private static class GradientWinnerPanel extends JPanel {
        GradientWinnerPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(253, 249, 255),
                    getWidth(), getHeight(), Color.WHITE
            );
            g2.setPaint(gradient);
            g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 22, 22);

            // Decorative circuit traces on the right.
            Color circuit = new Color(171, 91, 255, 55);
            g2.setColor(circuit);
            g2.setStroke(new BasicStroke(2f));
            int w = getWidth();
            int h = getHeight();
            int x0 = Math.max(w - 190, w / 2 + 80);
            int cy = h / 2;
            g2.drawLine(x0, cy - 42, x0 + 34, cy - 42);
            g2.drawLine(x0 + 34, cy - 42, x0 + 52, cy - 60);
            g2.drawLine(x0 + 52, cy - 60, x0 + 100, cy - 60);
            g2.drawLine(x0, cy + 42, x0 + 34, cy + 42);
            g2.drawLine(x0 + 34, cy + 42, x0 + 52, cy + 60);
            g2.drawLine(x0 + 52, cy + 60, x0 + 105, cy + 60);
            for (int i = 0; i < 5; i++) {
                g2.fillOval(x0 + i * 15, cy - 5, 6, 6);
            }

            // Faint speedometer graphic.
            int sx = w - 92;
            int sy = cy - 5;
            g2.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawArc(sx - 32, sy - 32, 64, 64, 25, 130);
            g2.setStroke(new BasicStroke(3f));
            g2.drawLine(sx, sy, sx + 19, sy - 20);
            g2.fillOval(sx - 4, sy - 4, 8, 8);

            g2.setColor(new Color(189, 117, 255));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 22, 22);
            g2.dispose();

            super.paintComponent(g);
        }
    }

    private static class TrophyGraphicPanel extends JPanel {
        TrophyGraphicPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth() - 18, getHeight() - 18);
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            int x = cx - size / 2;
            int y = cy - size / 2;

            // Lavender halo and ring.
            g2.setColor(new Color(244, 231, 255));
            g2.fillOval(x, y, size, size);
            g2.setColor(new Color(166, 90, 245));
            g2.setStroke(new BasicStroke(5f));
            g2.drawOval(x + 3, y + 3, size - 6, size - 6);

            // Trophy bowl.
            int bowlW = size * 38 / 100;
            int bowlH = size * 25 / 100;
            int bx = cx - bowlW / 2;
            int by = cy - bowlH / 2 - 10;
            g2.setColor(new Color(255, 174, 0));
            g2.fillRoundRect(bx, by, bowlW, bowlH, 12, 12);

            g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawArc(bx - 16, by + 3, 28, 30, 85, 180);
            g2.drawArc(bx + bowlW - 12, by + 3, 28, 30, -85, 180);

            g2.fillRoundRect(cx - 5, by + bowlH - 1, 10, 28, 5, 5);
            g2.fillRoundRect(cx - 18, by + bowlH + 22, 36, 8, 5, 5);
            g2.fillRoundRect(cx - 25, by + bowlH + 29, 50, 9, 5, 5);

            g2.setColor(Color.WHITE);
            Polygon star = createStar(cx, by + bowlH / 2, 9, 4, 5);
            g2.fillPolygon(star);
            g2.dispose();
        }

        private Polygon createStar(int centerX, int centerY, int outerR, int innerR, int points) {
            Polygon p = new Polygon();
            for (int i = 0; i < points * 2; i++) {
                double angle = -Math.PI / 2 + i * Math.PI / points;
                int r = (i % 2 == 0) ? outerR : innerR;
                p.addPoint(
                        centerX + (int) Math.round(Math.cos(angle) * r),
                        centerY + (int) Math.round(Math.sin(angle) * r)
                );
            }
            return p;
        }
    }

    private static class GradientBadgePanel extends JPanel {
        GradientBadgePanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(232, 244, 255),
                    getWidth(), getHeight(), new Color(241, 232, 255)
            );
            g2.setPaint(gradient);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

            int baseY = getHeight() - 18;
            int startX = 18;
            int barWidth = 8;
            int gap = 5;
            int[] heights = {20, 33, 46, 57};
            Color[] colors = {BLUE, new Color(50, 157, 255), PURPLE, PINK};

            for (int i = 0; i < heights.length; i++) {
                g2.setColor(colors[i]);
                g2.fillRoundRect(
                        startX + i * (barWidth + gap),
                        baseY - heights[i],
                        barWidth,
                        heights[i],
                        5,
                        5
                );
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class PlayIcon implements Icon {
        private final int size;
        private final Color color;

        PlayIcon(int size, Color color) {
            this.size = size;
            this.color = color;
        }

        @Override
        public int getIconWidth() { return size; }

        @Override
        public int getIconHeight() { return size; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            Polygon triangle = new Polygon();
            triangle.addPoint(x + 2, y + 1);
            triangle.addPoint(x + 2, y + size - 1);
            triangle.addPoint(x + size - 1, y + size / 2);
            g2.fillPolygon(triangle);
            g2.dispose();
        }
    }

    private static class ExportIcon implements Icon {
        private final int size;
        private final Color color;

        ExportIcon(int size, Color color) {
            this.size = size;
            this.color = color;
        }

        @Override
        public int getIconWidth() { return size; }

        @Override
        public int getIconHeight() { return size; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(color);
            int cx = x + size / 2;
            g2.drawLine(cx, y + 1, cx, y + size - 6);
            g2.drawLine(cx - 4, y + size - 10, cx, y + size - 6);
            g2.drawLine(cx + 4, y + size - 10, cx, y + size - 6);
            g2.drawLine(x + 2, y + size - 3, x + size - 2, y + size - 3);
            g2.dispose();
        }
    }

    private static class GradientButton extends JButton {
        private final Color startColor;
        private final Color endColor;

        GradientButton(String text, Color startColor, Color endColor) {
            super(text);
            this.startColor = startColor;
            this.endColor = endColor;

            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(10, 18, 10, 18));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color first = isEnabled() ? startColor : new Color(185, 191, 205);
            Color second = isEnabled() ? endColor : new Color(165, 172, 188);

            if (getModel().isPressed()) {
                first = first.darker();
                second = second.darker();
            }

            GradientPaint gradient = new GradientPaint(0, 0, first, getWidth(), 0, second);
            g2.setPaint(gradient);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.dispose();

            super.paintComponent(g);
        }
    }
}
