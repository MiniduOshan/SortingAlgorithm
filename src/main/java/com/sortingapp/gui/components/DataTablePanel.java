package com.sortingapp.gui.components;

import com.sortingapp.model.CSVData;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Custom Swing panel for displaying CSV dataset tables (original data and sorted previews).
 * 
 * Group Assignment Contribution:
 * Member 3: GUI layout and data visualization.
 */
public class DataTablePanel extends JPanel {

    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JLabel statusLabel;
    private int highlightedColIndex = -1;

    public DataTablePanel() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel();
        table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table cells are read-only
            }
        };

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(236, 240, 241));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setGridColor(new Color(230, 230, 230));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        statusLabel = new JLabel(" No dataset loaded.");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        add(statusLabel, BorderLayout.SOUTH);
    }

    public void loadCSVData(CSVData csvData, int sortedColIndex) {
        this.highlightedColIndex = sortedColIndex;
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        if (csvData == null) {
            statusLabel.setText(" No dataset loaded.");
            return;
        }

        // Add headers
        for (String header : csvData.getHeaders()) {
            tableModel.addColumn(header);
        }

        // Add rows (up to 5000 rows for smooth UI rendering)
        int maxRows = Math.min(csvData.getRows().size(), 5000);
        for (int i = 0; i < maxRows; i++) {
            tableModel.addRow(csvData.getRows().get(i));
        }

        statusLabel.setText(String.format(" Displaying %d of %d total rows | Columns: %d",
                maxRows, csvData.getRowCount(), csvData.getColumnCount()));

        // Apply custom cell renderer to highlight the sorted column
        applyCustomRenderer();
    }

    public void loadSingleColumnData(String columnName, double[] values) {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        tableModel.addColumn("Row #");
        tableModel.addColumn(columnName + " (Sorted)");

        for (int i = 0; i < values.length; i++) {
            tableModel.addRow(new Object[]{i + 1, String.format("%.4f", values[i])});
        }

        statusLabel.setText(String.format(" Sorted column dataset: %d entries.", values.length));
    }

    private void applyCustomRenderer() {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    if (column == highlightedColIndex) {
                        c.setBackground(new Color(235, 245, 255)); // Soft highlight blue
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                        c.setFont(c.getFont().deriveFont(Font.PLAIN));
                    } else {
                        c.setBackground(new Color(248, 249, 250)); // Alternating row color
                        c.setFont(c.getFont().deriveFont(Font.PLAIN));
                    }
                }
                return c;
            }
        });
    }
}
