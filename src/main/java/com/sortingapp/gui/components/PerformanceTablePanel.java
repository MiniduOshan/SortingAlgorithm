package com.sortingapp.gui.components;

import com.sortingapp.model.SortingResult;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Swing panel rendering tabular benchmark metrics for all evaluated sorting algorithms.
 * 
 * Group Assignment Contribution:
 * Member 3: Performance evaluation and GUI results table.
 */
public class PerformanceTablePanel extends JPanel {

    private final JTable table;
    private final DefaultTableModel model;
    private final JLabel winnerCardLabel;

    public PerformanceTablePanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // Winner Card Header
        winnerCardLabel = new JLabel("🏆 Select dataset and run evaluation to identify the best algorithm", SwingConstants.CENTER);
        winnerCardLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        winnerCardLabel.setForeground(new Color(44, 62, 80));
        winnerCardLabel.setOpaque(true);
        winnerCardLabel.setBackground(new Color(241, 248, 255));
        winnerCardLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 240), 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        add(winnerCardLabel, BorderLayout.NORTH);

        // Benchmark Table
        String[] columns = {"Rank", "Algorithm Name", "Execution Time", "Comparisons", "Swaps / Shifts", "Verified Sorted", "Speed Factor"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(236, 240, 241));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Center align rank and status columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateResults(List<SortingResult> results, SortingResult winner) {
        model.setRowCount(0);

        if (winner != null) {
            winnerCardLabel.setText(String.format("🏆 BEST ALGORITHM: %s | Execution Time: %s (%d elements)",
                    winner.getAlgorithmName().toUpperCase(), winner.getFormattedExecutionTime(), winner.getItemCount()));
            winnerCardLabel.setBackground(new Color(235, 247, 238));
            winnerCardLabel.setForeground(new Color(39, 174, 96));
        } else {
            winnerCardLabel.setText(" Select dataset and run evaluation to identify the best algorithm");
            winnerCardLabel.setBackground(new Color(241, 248, 255));
            winnerCardLabel.setForeground(new Color(44, 62, 80));
        }

        if (results == null || results.isEmpty()) return;

        long baselineNs = winner != null ? Math.max(1, winner.getExecutionTimeNs()) : 1;

        for (int i = 0; i < results.size(); i++) {
            SortingResult r = results.get(i);
            String rankStr = (winner != null && r.getAlgorithmName().equals(winner.getAlgorithmName())) ? "🥇 1st" : (i + 1) + "";
            double speedFactor = (double) r.getExecutionTimeNs() / baselineNs;
            String speedStr = String.format("%.2fx", speedFactor);

            model.addRow(new Object[]{
                    rankStr,
                    r.getAlgorithmName(),
                    r.getFormattedExecutionTime(),
                    String.format("%,d", r.getComparisonCount()),
                    String.format("%,d", r.getSwapCount()),
                    r.isSortedCorrectly() ? "✅ Passed" : "❌ Failed",
                    speedStr
            });
        }
    }
}
