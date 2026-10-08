package com.sortingapp.gui.components;

import com.sortingapp.model.SortingResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Displays sorting performance results ranked from fastest to slowest.
 */
public class PerformanceTablePanel extends JPanel {

    private static final Color BACKGROUND = Color.WHITE;
    private static final Color HEADER_BG = new Color(239, 246, 255);
    private static final Color HEADER_TEXT = new Color(20, 32, 67);
    private static final Color TEXT_PRIMARY = new Color(32, 47, 78);
    private static final Color BORDER = new Color(218, 227, 240);
    private static final Color ROW_ALT = new Color(249, 251, 255);
    private static final Color WINNER_BG = new Color(236, 253, 245);
    private static final Color WINNER_TEXT = new Color(22, 128, 79);

    private final JTable table;
    private final DefaultTableModel model;

    public PerformanceTablePanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        setOpaque(true);

        String[] columns = {
                "Rank",
                "Algorithm",
                "Execution Time",
                "Comparisons",
                "Swaps / Shifts"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        configureTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(BACKGROUND);
        scrollPane.setBackground(BACKGROUND);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void configureTable() {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(TEXT_PRIMARY);
        table.setBackground(BACKGROUND);
        table.setRowHeight(26);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(224, 232, 243));
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(229, 241, 255));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setFocusable(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setForeground(HEADER_TEXT);
        header.setBackground(HEADER_BG);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 29));
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        ResultsCellRenderer renderer = new ResultsCellRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        table.getColumnModel().getColumn(0).setPreferredWidth(55);
        table.getColumnModel().getColumn(0).setMaxWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(190);
        table.getColumnModel().getColumn(2).setPreferredWidth(145);
        table.getColumnModel().getColumn(3).setPreferredWidth(130);
        table.getColumnModel().getColumn(4).setPreferredWidth(130);
    }

    /**
     * Rebuilds the table in execution-time order. Rank 1 is the fastest result.
     */
    public void updateResults(List<SortingResult> results, SortingResult winner) {
        model.setRowCount(0);

        if (results == null || results.isEmpty()) {
            return;
        }

        List<SortingResult> rankedResults = new ArrayList<>(results);
        rankedResults.sort(Comparator.comparingLong(SortingResult::getExecutionTimeNs));

        for (int i = 0; i < rankedResults.size(); i++) {
            SortingResult result = rankedResults.get(i);
            model.addRow(new Object[]{
                    i + 1,
                    result.getAlgorithmName(),
                    result.getFormattedExecutionTime(),
                    String.format("%,d", result.getComparisonCount()),
                    String.format("%,d", result.getSwapCount())
            });
        }
    }

    private class ResultsCellRenderer extends DefaultTableCellRenderer {

        ResultsCellRenderer() {
            setBorder(new EmptyBorder(0, 8, 0, 8));
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(TEXT_PRIMARY);

            if (!isSelected) {
                if (row == 0) {
                    setBackground(WINNER_BG);
                    setForeground(WINNER_TEXT);
                    setFont(new Font("Segoe UI", Font.BOLD, 13));
                } else {
                    setBackground(row % 2 == 0 ? BACKGROUND : ROW_ALT);
                }
            }

            setHorizontalAlignment(column == 1 ? SwingConstants.LEFT : SwingConstants.CENTER);
            return this;
        }
    }
}
