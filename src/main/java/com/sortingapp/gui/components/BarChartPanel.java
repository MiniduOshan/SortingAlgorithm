package com.sortingapp.gui.components;

import com.sortingapp.model.SortingResult;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Displays sorting execution times in a light-themed bar chart.
 * Algorithms are ordered from fastest to slowest, with the winner in green.
 *
 * Member 3 Contribution:
 * Performance comparison chart and result visualization.
 */
public class BarChartPanel extends JPanel {

    private List<SortingResult> results = new ArrayList<>();
    private SortingResult bestResult;
    private static final Color BG = Color.WHITE;
    private static final Color TEXT = new Color(30, 41, 59);
    private static final Color MUTED = new Color(100, 116, 139);
    private static final Color GRID = new Color(218, 227, 240);
    private static final Color AXIS = new Color(29, 45, 75);

    private static final Color[] BAR_COLORS = {
            new Color(50, 214, 131),
            new Color(55, 145, 255),
            new Color(255, 181, 51),
            new Color(94, 169, 255),
            new Color(255, 75, 126)
    };

    public BarChartPanel() {
        setOpaque(true);
        setBackground(BG);
        setPreferredSize(new Dimension(560, 320));
        setMinimumSize(new Dimension(360, 150));
        setToolTipText("Execution Time Comparison - lower is better");
    }

    /**
     * Updates the chart with results ordered by execution time.
     *
     * @param results sorting performance results to display
     * @param bestResult fastest algorithm to highlight, or null if unavailable
     */
    public void setResults(List<SortingResult> results, SortingResult bestResult) {
        this.results = new ArrayList<>();
        this.bestResult = bestResult;
        if (results != null) {
            this.results.addAll(results);
            this.results.sort(Comparator.comparingLong(SortingResult::getExecutionTimeNs));
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            if (results == null || results.isEmpty()) {
                drawEmptyState(g2);
            } else {
                drawChart(g2);
            }
        } finally {
            g2.dispose();
        }
    }

    private void drawEmptyState(Graphics2D g2) {
        String title = "No performance data yet";
        String subtitle = "Run the sorting algorithms to display the comparison chart.";
        int centerY = getHeight() / 2;

        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2.setColor(TEXT);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, Math.max(12, (getWidth() - fm.stringWidth(title)) / 2), centerY - 8);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.setColor(MUTED);
        fm = g2.getFontMetrics();
        g2.drawString(subtitle, Math.max(12, (getWidth() - fm.stringWidth(subtitle)) / 2), centerY + 16);
    }

    private void drawChart(Graphics2D g2) {
        int width = getWidth();
        int height = getHeight();

        int left = 72;
        int right = 26;
        int top = 8;
        // Reserve enough space inside the component for full algorithm names.
        int bottom = 58;

        int plotWidth = Math.max(160, width - left - right);
        int plotHeight = Math.max(80, height - top - bottom);

        long maxNs = results.stream()
                .mapToLong(SortingResult::getExecutionTimeNs)
                .max()
                .orElse(1L);

        TimeScale scale = chooseScale(maxNs);
        double maxValue = maxNs / scale.divisor;
        double axisMax = niceUpperBound(maxValue);

        drawGridAndYAxis(g2, left, top, plotWidth, plotHeight, axisMax, scale.unit);
        drawBars(g2, left, top, plotWidth, plotHeight, axisMax, scale);

        g2.setColor(AXIS);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(left, top, left, top + plotHeight);
        g2.drawLine(left, top + plotHeight, left + plotWidth, top + plotHeight);
    }

    private void drawGridAndYAxis(Graphics2D g2, int left, int top, int plotWidth,
                                  int plotHeight, double axisMax, String unit) {
        int ticks = 4;
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        for (int i = 0; i <= ticks; i++) {
            double value = axisMax * i / ticks;
            int y = top + plotHeight - (int) Math.round(plotHeight * i / (double) ticks);

            g2.setColor(GRID);
            g2.setStroke(new BasicStroke(1f));
            g2.drawLine(left, y, left + plotWidth, y);

            String label = formatAxisValue(value);
            g2.setColor(TEXT);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(label, left - 12 - fm.stringWidth(label), y + fm.getAscent() / 2 - 2);
        }

        // Rotated y-axis title.
        String axisTitle = "Time (" + unit + ")";
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.setColor(TEXT);
        FontMetrics fm = g2.getFontMetrics();
        Graphics2D rotated = (Graphics2D) g2.create();
        rotated.rotate(-Math.PI / 2);
        int x = -(top + plotHeight / 2 + fm.stringWidth(axisTitle) / 2);
        rotated.drawString(axisTitle, x, 18);
        rotated.dispose();
    }

    private void drawBars(Graphics2D g2, int left, int top, int plotWidth,
                          int plotHeight, double axisMax, TimeScale scale) {
        int count = results.size();
        int slotWidth = plotWidth / Math.max(1, count);
        int barWidth = Math.max(38, Math.min(125, (int) (slotWidth * 0.42)));

        for (int i = 0; i < count; i++) {
            SortingResult result = results.get(i);
            double value = result.getExecutionTimeNs() / scale.divisor;
            int barHeight = (int) Math.round((value / axisMax) * plotHeight);
            barHeight = Math.max(3, Math.min(plotHeight, barHeight));

            int centerX = left + slotWidth * i + slotWidth / 2;
            int x = centerX - barWidth / 2;
            int y = top + plotHeight - barHeight;

            // Highlight the supplied winner; use the remaining colors for other bars.
            boolean isWinner = result == bestResult;
            Color barColor = isWinner ? BAR_COLORS[0]
                    : BAR_COLORS[(i % (BAR_COLORS.length - 1)) + 1];
            g2.setColor(barColor);
            g2.fill(new RoundRectangle2D.Double(x, y, barWidth, barHeight, 5, 5));

            String valueText = result.getFormattedExecutionTime();
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.setColor(TEXT);
            FontMetrics fm = g2.getFontMetrics();
            int valueX = centerX - fm.stringWidth(valueText) / 2;
            int valueY = y - 8;
            if (valueY < top + fm.getAscent()) {
                valueY = top + fm.getAscent();
            }
            g2.drawString(valueText, valueX, valueY);

            // Place the algorithm name below the x-axis.
            String label = result.getAlgorithmName();
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.setColor(TEXT);
            fm = g2.getFontMetrics();
            int labelX = centerX - fm.stringWidth(label) / 2;
            int labelY = top + plotHeight + 23;
            g2.drawString(label, labelX, labelY);
        }
    }

    /**
     * Chooses an appropriate time unit for the chart's y-axis.
     */
    private TimeScale chooseScale(long maxNs) {
        if (maxNs >= 1_000_000L) {
            return new TimeScale(1_000_000.0, "ms");
        }
        if (maxNs >= 1_000L) {
            return new TimeScale(1_000.0, "µs");
        }
        return new TimeScale(1.0, "ns");
    }

    /**
     * Rounds the axis limit upward to provide readable tick values and label space.
     */
    private double niceUpperBound(double maxValue) {
        if (maxValue <= 0) {
            return 1.0;
        }

        // Leave approximately 25% headroom for execution-time labels.
        double target = maxValue * 1.25;

        double power = Math.pow(10, Math.floor(Math.log10(target)));
        double normalized = target / power;

        double nice;
        if (normalized <= 1.0) nice = 1.0;
        else if (normalized <= 1.5) nice = 1.5;
        else if (normalized <= 2.0) nice = 2.0;
        else if (normalized <= 2.5) nice = 2.5;
        else if (normalized <= 3.0) nice = 3.0;
        else if (normalized <= 4.0) nice = 4.0;
        else if (normalized <= 5.0) nice = 5.0;
        else if (normalized <= 6.0) nice = 6.0;
        else if (normalized <= 8.0) nice = 8.0;
        else nice = 10.0;

        return nice * power;
    }

    private String formatAxisValue(double value) {
        if (Math.abs(value - Math.rint(value)) < 0.000001) {
            return String.format("%.0f", value);
        }
        if (value >= 10) return String.format("%.1f", value);
        return String.format("%.2f", value);
    }

    private static class TimeScale {
        final double divisor;
        final String unit;

        TimeScale(double divisor, String unit) {
            this.divisor = divisor;
            this.unit = unit;
        }
    }
}
