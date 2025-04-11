package com.statistics.visualization;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;
import java.util.ArrayList;

import com.statistics.JStatsLib;

public class BoxPlot extends JPanel {
   
   private JStatsLib stats;
   private double median;
   private double iqr;
   private double[] percentiles; // [Q1, Q3]
   private ArrayList<Double> values;
   private double min;
   private double max;
   private String title;
   private Color[] theme;
   
   public BoxPlot(JStatsLib stats, String title, Color[] theme) {
      this.stats = stats;
      this.values = stats.toList();
      this.median = stats.median();
      this.iqr = stats.iqr();
      this.percentiles = stats.findQuarterPercentiles();
      this.min = stats.min();
      this.max = stats.max();
      this.title = title;
      this.theme = theme;
   }
   
   @Override
   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
      GraphUtil.setupRenderingHints(g2d);
      g2d.setStroke(new BasicStroke(3));
      
      int width = getWidth();
      int height = getHeight();
      
      // Use GraphUtil for background and title.
      GraphUtil.drawBackgroundAndTitle(g2d, width, height, title, theme);
      
      int drawingWidth = width - (2 * GraphUtil.LEFT_PADDING);
      double range = max - min;
      
      // Use GraphUtil.valueToX to convert values.
      int q1Pos = GraphUtil.valueToX(percentiles[0], min, range, drawingWidth, GraphUtil.LEFT_PADDING);
      int q3Pos = GraphUtil.valueToX(percentiles[1], min, range, drawingWidth, GraphUtil.LEFT_PADDING);
      int boxWidth = q3Pos - q1Pos;
      int boxHeight = 40;
      int yPosition = height / 2 - boxHeight / 2;
      
      // Draw the box and median.
      g2d.setColor(theme[0]);
      g2d.fillRect(q1Pos, yPosition, boxWidth, boxHeight);
      g2d.setColor(Color.BLACK);
      g2d.drawRect(q1Pos, yPosition, boxWidth, boxHeight);
      int medianPos = GraphUtil.valueToX(median, min, range, drawingWidth, GraphUtil.LEFT_PADDING);
      g2d.setColor(theme[1]);
      g2d.drawLine(medianPos, yPosition, medianPos, yPosition + boxHeight);
      
      // Determine whisker bounds.
      double lowerBound = percentiles[0] - (1.5 * iqr);
      double upperBound = percentiles[1] + (1.5 * iqr);
      double minNonOutlier = Double.MAX_VALUE;
      double maxNonOutlier = Double.MIN_VALUE;
      for (Double value : values) {
         if (value >= lowerBound && value <= upperBound) {
            minNonOutlier = Math.min(minNonOutlier, value);
            maxNonOutlier = Math.max(maxNonOutlier, value);
         }
      }
      int lbPos = GraphUtil.valueToX(minNonOutlier, min, range, drawingWidth, GraphUtil.LEFT_PADDING);
      int ubPos = GraphUtil.valueToX(maxNonOutlier, min, range, drawingWidth, GraphUtil.LEFT_PADDING);
      
      // Draw whiskers and caps.
      g2d.setColor(theme[3]);
      int midY = yPosition + (boxHeight / 2);
      g2d.drawLine(q1Pos, midY, lbPos, midY);
      g2d.drawLine(q3Pos, midY, ubPos, midY);
      int capHeight = 10;
      g2d.drawLine(lbPos, midY - (capHeight / 2), lbPos, midY + (capHeight / 2));
      g2d.drawLine(ubPos, midY - (capHeight / 2), ubPos, midY + (capHeight / 2));
      
      // Draw X-axis.
      int scaleY = height - GraphUtil.BOTTOM_PADDING;
      GraphUtil.drawXAxis(g2d, width, height, min, max, scaleY, GraphUtil.LEFT_PADDING, GraphUtil.RIGHT_PADDING, theme);
      
      // Draw outliers.
      g2d.setColor(theme[2]);
      for (Double value : values) {
         if (value < lowerBound || value > upperBound) {
            int xPos = GraphUtil.valueToX(value, min, range, drawingWidth, GraphUtil.LEFT_PADDING);
            g2d.fillOval(xPos - 3, midY - 3, 6, 6);
         }
      }
      
      // Draw statistics using GraphUtil.
      GraphUtil.drawStatistics(g2d, stats, width - GraphUtil.PADDING - 150, GraphUtil.TOP_PADDING, theme);
   }
   
   public void showGraph() {
      GraphUtil.showInFrame(this, title, 800, 400);
   }
}
