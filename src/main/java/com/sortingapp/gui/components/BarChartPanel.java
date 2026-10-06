package com.sortingapp.gui.components;

import com.sortingapp.model.SortingResult;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Custom Swing component that renders a visual bar chart comparing algorithm execution times.
 * Highlights the fastest algorithm with a distinct gold/emerald color.
 * 
 * Group Assignment Contribution:
 * Member 3: User Interface & Performance Visualization.
 */
public class BarChartPanel extends JPanel {

    private List<SortingResult> results;
    private SortingResult bestResult;

    private static final Color WINNER_COLOR = new Color(46, 204, 113); // Emerald Green
    private static final Color REGULAR_COLOR = new Color(52, 152, 219); // Blue
    private static final Color SLOWEST_COLOR = new Color(231, 76, 60); // Flat Red
    private static final Color GRID_COLOR = new Color(220, 224, 230);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);

    public BarChartPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(500, 300));
        setToolTipText("Execution Time Comparison Chart");
    }

    public void setResults(List<SortingResult> results, SortingResult bestResult) {
        this.results = results;
        this.bestResult = bestResult;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        if (results == null || results.isEmpty()) {
            g2.setColor(new Color(127, 140, 141));
            g2.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            String msg = "Upload a CSV and click 'Evaluate Performance' to display performance chart.";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(msg, (width - fm.stringWidth(msg)) / 2, height / 2);
            return;
        }

        int paddingLeft = 140;
        int paddingRight = 120;
        int paddingTop = 40;
        int paddingBottom = 40;

        int chartWidth = width - paddingLeft - paddingRight;
        int chartHeight = height - paddingTop - paddingBottom;

        // Title
        g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        g2.setColor(TEXT_COLOR);
        g2.drawString("Execution Time Comparison (Lower is Better)", paddingLeft, 25);

        long maxTime = results.stream().mapToLong(SortingResult::getExecutionTimeNs).max().orElse(1);
        if (maxTime == 0) maxTime = 1;

        int barCount = results.size();
        int barGap = 12;
        int barHeight = Math.max(18, (chartHeight - (barCount - 1) * barGap) / barCount);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        for (int i = 0; i < barCount; i++) {
            SortingResult r = results.get(i);
            int y = paddingTop + i * (barHeight + barGap);

            // Draw Algorithm Name Label
            g2.setColor(TEXT_COLOR);
            FontMetrics fm = g2.getFontMetrics();
            String label = r.getAlgorithmName();
            g2.drawString(label, paddingLeft - fm.stringWidth(label) - 10, y + barHeight / 2 + 5);

            // Calculate bar length
            double ratio = (double) r.getExecutionTimeNs() / maxTime;
            int barLen = (int) (ratio * chartWidth);
            barLen = Math.max( barLen, 6); // minimum visible width

            // Bar Color
            boolean isWinner = bestResult != null && r.getAlgorithmName().equals(bestResult.getAlgorithmName());
            if (isWinner) {
                g2.setColor(WINNER_COLOR);
            } else if (r.getExecutionTimeNs() == maxTime && barCount > 1) {
                g2.setColor(SLOWEST_COLOR);
            } else {
                g2.setColor(REGULAR_COLOR);
            }

            // Draw Bar with rounded corners
            g2.fillRoundRect(paddingLeft, y, barLen, barHeight, 6, 6);

            // Draw Execution Time text next to bar
            g2.setColor(TEXT_COLOR);
            String timeText = r.getFormattedExecutionTime() + (isWinner ? " 🏆 Best" : "");
            if (isWinner) {
                g2.setFont(g2.getFont().deriveFont(Font.BOLD));
            } else {
                g2.setFont(g2.getFont().deriveFont(Font.PLAIN));
            }
            g2.drawString(timeText, paddingLeft + barLen + 8, y + barHeight / 2 + 5);
        }

        // Draw Axis Line
        g2.setColor(GRID_COLOR);
        g2.drawLine(paddingLeft, paddingTop - 5, paddingLeft, paddingTop + chartHeight + 5);
    }
}
