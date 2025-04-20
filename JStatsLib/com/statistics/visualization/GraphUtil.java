package com.statistics.visualization;

import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.BasicStroke;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;

import com.statistics.JStatsLib;

public class GraphUtil {

    // Padding constants
    public static final int PADDING = 50;
    public static final int BOTTOM_PADDING = 70;
    public static final int TOP_PADDING = 50;
    public static final int LEFT_PADDING = 70;
    public static final int RIGHT_PADDING = 30;

    // Common font settings
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 16);
    public static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 12);
    public static final Font ITALIC_FONT = new Font("Arial", Font.ITALIC, 12);
    public static final Font BOLD_FONT = new Font("Arial", Font.BOLD, 12);

    /**
     * Creates a default color theme for graphs.
     * @return Color array representing the theme.
     */
    public static Color[] createDefaultTheme() {
        return new Color[] {
            new Color(65, 105, 225),  // Primary (royal blue)
            new Color(220, 20, 60),   // Secondary (crimson)
            new Color(50, 205, 50),   // Tertiary (lime green)
            new Color(255, 165, 0),   // Quaternary (orange)
            new Color(0, 0, 0),       // Text (black)
            new Color(245, 245, 245)  // Background (whitesmoke)
        };
    }

    /**
     * Sets up rendering hints for quality graphics
     * @param g2d Graphics2D object to configure
     */
    public static void setupRenderingHints(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(2));
    }

    /**
     * Draws the background and centers the title at the top
     * @param g2d Graphics2D object
     * @param width Panel width
     * @param height Panel height
     * @param title Graph title
     * @param theme Color theme
     */
    public static void drawBackgroundAndTitle(Graphics2D g2d, int width, int height, String title, Color[] theme) {
        // Background
        g2d.setColor(theme[5]);
        g2d.fillRect(0, 0, width, height);
        
        // Title centered near top
        g2d.setColor(theme[4]);
        g2d.setFont(TITLE_FONT);
        FontMetrics metrics = g2d.getFontMetrics();
        int titleWidth = metrics.stringWidth(title);
        g2d.drawString(title, (width - titleWidth) / 2, 30);
    }

    /**
     * Draws the X-axis with ticks and labels
     * @param g2d Graphics2D object
     * @param width Panel width
     * @param height Panel height
     * @param min Minimum data value
     * @param max Maximum data value
     * @param axisY Y-coordinate to draw the axis
     * @param leftPadding Left padding
     * @param rightPadding Right padding
     * @param theme Color theme
     */
    public static void drawXAxis(Graphics2D g2d, int width, int height, 
                                 double min, double max, int axisY, 
                                 int leftPadding, int rightPadding, Color[] theme) {
        g2d.setColor(theme[4]);
        g2d.drawLine(leftPadding, axisY, width - rightPadding, axisY);

        int numTicks = 10;
        g2d.setFont(LABEL_FONT);
        double range = max - min;
        int drawingWidth = width - (leftPadding + rightPadding);
        
        for (int i = 0; i <= numTicks; i++) {
            double value = min + (range * i / numTicks);
            int xPos = valueToX(value, min, range, drawingWidth, leftPadding);
            g2d.drawLine(xPos, axisY, xPos, axisY + 5);
            String label = String.format("%.1f", value);
            FontMetrics fm = g2d.getFontMetrics();
            int labelWidth = fm.stringWidth(label);
            g2d.drawString(label, xPos - labelWidth / 2, axisY + 20);
        }
        
        // Draw zero marker if zero falls within the range
        if (min <= 0 && max >= 0) {
            int zeroPos = valueToX(0, min, range, drawingWidth, leftPadding);
            g2d.setColor(Color.GRAY);
            g2d.drawLine(zeroPos, TOP_PADDING, zeroPos, axisY);
            g2d.setColor(theme[4]);
        }
    }

    /**
     * Draws the Y-axis with ticks and labels
     * @param g2d Graphics2D object
     * @param width Panel width
     * @param height Panel height
     * @param maxValue Maximum frequency or value
     * @param axisX X-coordinate to draw the axis
     * @param bottomPadding Bottom padding
     * @param topPadding Top padding
     * @param rightPadding Right padding
     * @param theme Color theme
     * @param label Optional axis label
     */
    public static void drawYAxis(Graphics2D g2d, int width, int height, 
                                 int maxValue, int axisX, 
                                 int bottomPadding, int topPadding, int rightPadding,
                                 Color[] theme, String label) {
        g2d.setColor(theme[4]);
        g2d.drawLine(axisX, topPadding, axisX, height - bottomPadding);

        int numTicks = 5;
        int drawingHeight = height - (topPadding + bottomPadding);
        
        for (int i = 0; i <= numTicks; i++) {
            int value = (maxValue * i) / numTicks;
            int y = height - bottomPadding - (int)(((double)value / maxValue) * drawingHeight);
            g2d.drawLine(axisX - 5, y, axisX, y);
            String valueLabel = Integer.toString(value);
            FontMetrics fm = g2d.getFontMetrics();
            int labelWidth = fm.stringWidth(valueLabel);
            g2d.drawString(valueLabel, axisX - 10 - labelWidth, y + 4);
        }
        
        if (label != null && !label.isEmpty()) {
            g2d.translate(20, height / 2);
            g2d.rotate(-Math.PI / 2);
            g2d.setFont(BOLD_FONT);
            g2d.drawString(label, 0, 0);
            g2d.rotate(Math.PI / 2);
            g2d.translate(-20, -height / 2);
        }
    }

    /**
     * Converts a data value to its X coordinate in a drawing area
     * @param val Data value
     * @param min Minimum value of the dataset
     * @param range Range (max - min) of the dataset
     * @param drawingWidth Available drawing width
     * @param leftPadding Left padding
     * @return X coordinate
     */
    public static int valueToX(double val, double min, double range, int drawingWidth, int leftPadding) {
        double scaleFactor = drawingWidth / range;
        return leftPadding + (int)((val - min) * scaleFactor);
    }
    
    /**
     * Converts a frequency value to its Y coordinate for bar heights
     * @param val Frequency count
     * @param height Panel height
     * @param maxFreq Maximum frequency
     * @return Y coordinate
     */
    public static int freqToY(int val, int height, int maxFreq) {
        int availableHeight = height - TOP_PADDING - BOTTOM_PADDING;
        double scaleFactor = (double) availableHeight / maxFreq;
        return height - BOTTOM_PADDING - (int)(val * scaleFactor);
    }
    
    /**
     * Calculates an X coordinate for cumulative graphs
     * @param val Data value
     * @param min Minimum value
     * @param max Maximum value
     * @param leftPadding Left padding
     * @param rightPadding Right padding
     * @param width Total drawing width
     * @return X coordinate
     */
    public static int calcValueToX(double val, double min, double max, int leftPadding, int rightPadding, int width) {
        double range = max - min;
        int drawingWidth = width - (leftPadding + rightPadding);
        double scaleFactor = drawingWidth / range;
        return leftPadding + (int)((val - min) * scaleFactor);
    }
    
    /**
     * Converts a percentage (0 to 1) to a Y coordinate for cumulative frequency graphs
     * @param percent Cumulative percentage
     * @param height Panel height
     * @param topPadding Top padding
     * @param bottomPadding Bottom padding
     * @return Y coordinate
     */
    public static int percentToY(double percent, int height, int topPadding, int bottomPadding) {
        int drawingHeight = height - (topPadding + bottomPadding);
        return height - bottomPadding - (int)(percent * drawingHeight);
    }

    /**
     * Draws basic statistical values in the top-right corner
     * @param g2d Graphics2D object
     * @param stats JStatsLib instance
     * @param x Starting X position
     * @param y Starting Y position
     * @param theme Color theme
     */
    public static void drawStatistics(Graphics2D g2d, JStatsLib stats, int x, int y, Color[] theme) {
        g2d.setColor(theme[4]);
        g2d.setFont(LABEL_FONT);
        int lineHeight = 20;
        g2d.drawString("N = " + stats.size(), x, y);
        g2d.drawString("Mean: " + String.format("%.2f", stats.mean()), x, y + lineHeight);
        g2d.drawString("StdDev: " + String.format("%.2f", stats.stddev()), x, y + lineHeight * 2);
        g2d.drawString("Min: " + String.format("%.2f", stats.min()), x, y + lineHeight * 3);
        g2d.drawString("Max: " + String.format("%.2f", stats.max()), x, y + lineHeight * 4);
    }
    
    /**
     * Convenience method to display a JPanel in a JFrame
     * @param panel The JPanel to display
     * @param title Frame title
     * @param width Frame width
     * @param height Frame height
     */
    public static void showInFrame(JPanel panel, String title, int width, int height) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(width, height);
        frame.add(panel);
        frame.setVisible(true);
    }
}
   