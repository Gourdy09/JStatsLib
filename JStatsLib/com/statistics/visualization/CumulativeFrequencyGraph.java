package com.statistics.visualization;

import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.BasicStroke;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.statistics.JStatsLib;
import com.statistics.utils.Themes;

/**
 * <summary>
 * A cumulative frequency graph to represent your data
 * </summary>
 */

public class CumulativeFrequencyGraph extends JPanel {
   
   private JStatsLib stats;
   private ArrayList<Double> values;
   private double min;
   private double max;
   private String title;
   private Color[] theme;
   
   private int leftPadding = GraphUtil.LEFT_PADDING;
   private int rightPadding = GraphUtil.RIGHT_PADDING;
   private int bottomPadding = GraphUtil.BOTTOM_PADDING;
   private int topPadding = GraphUtil.TOP_PADDING;
   
   public CumulativeFrequencyGraph(JStatsLib stats, String title, Color[] theme) {
      this.stats = stats;
      this.values = stats.toList();
      Collections.sort(this.values);
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
      g2d.setStroke(new BasicStroke(2));
      
      int width = getWidth();
      int height = getHeight();
      
      // Use GraphUtil to draw background and title
      GraphUtil.drawBackgroundAndTitle(g2d, width, height, title, theme);
      
      // Draw axes
      g2d.setColor(theme[4]);
      g2d.drawLine(leftPadding, topPadding, leftPadding, height - bottomPadding);
      g2d.drawLine(leftPadding, height - bottomPadding, width - rightPadding, height - bottomPadding);
      
      ArrayList<Double> uniqueValues = new ArrayList<>();
      for (Double val : stats.toList()) {
         if (!uniqueValues.contains(val)) {
             uniqueValues.add(val);
         }
      }
      Collections.sort(uniqueValues);
      
      // Add frequency to ArrayList of frequencies for each item in dataset
      int totalCount = stats.size();
      ArrayList<Double> cumulativePercentages = new ArrayList<>();
      int runningCount = 0;
      HashMap<Double, Integer> freqMap = stats.toFrequencyMap();
      for (Double value : uniqueValues) {
         int frequency = freqMap.get(value);
         runningCount += frequency;
         double percentage = (double) runningCount / totalCount;
         cumulativePercentages.add(percentage);
      }
      
      // Draw graph
      Path2D path = new Path2D.Double();
      boolean started = false;
      for (int i = 0; i < uniqueValues.size(); i++) {
         double value = uniqueValues.get(i);
         double percentage = cumulativePercentages.get(i);
         int x = GraphUtil.calcValueToX(value, min, max, leftPadding, rightPadding, width);
         int y = GraphUtil.percentToY(percentage, height, topPadding, bottomPadding);
         if (!started) {
            path.moveTo(x, y);
            started = true;
         } else {
            path.lineTo(x, y);
         }
      }
      
      g2d.setColor(theme[0]);
      g2d.setStroke(new BasicStroke(3));
      g2d.draw(path);
      
      Path2D fillPath = new Path2D.Double(path);
      if (!uniqueValues.isEmpty()) {
         fillPath.lineTo(GraphUtil.calcValueToX(uniqueValues.get(uniqueValues.size()-1), min, max, leftPadding, rightPadding, width), height - bottomPadding);
         fillPath.lineTo(leftPadding, height - bottomPadding);
      }
      fillPath.closePath();
      
      Color fillColor = new Color(theme[0].getRed(), theme[0].getGreen(), theme[0].getBlue(), 80);
      g2d.setColor(fillColor);
      g2d.fill(fillPath);
      
      g2d.setColor(theme[1]);
      for (int i = 0; i < uniqueValues.size(); i++) {
         double value = uniqueValues.get(i);
         double percentage = cumulativePercentages.get(i);
         int x = GraphUtil.calcValueToX(value, min, max, leftPadding, rightPadding, width);
         int y = GraphUtil.percentToY(percentage, height, topPadding, bottomPadding);
         g2d.fillOval(x - 4, y - 4, 8, 8);
      }
      
      // X-axis ticks and labels
      g2d.setColor(theme[4]);
      g2d.setFont(new Font("Arial", Font.PLAIN, 12));
      int numXTicks = 10;
      double xRange = max - min;
      for (int i = 0; i <= numXTicks; i++) {
         double value = min + (xRange * i / numXTicks);
         int x = GraphUtil.calcValueToX(value, min, max, leftPadding, rightPadding, width);
         g2d.drawLine(x, height - bottomPadding, x, height - bottomPadding + 5);
         String label = String.format("%.1f", value);
         FontMetrics fm = g2d.getFontMetrics();
         int labelWidth = fm.stringWidth(label);
         g2d.drawString(label, x - labelWidth / 2, height - bottomPadding + 20);
      }
      
      // Y-axis ticks and labels
      int numYTicks = 10;
      for (int i = 0; i <= numYTicks; i++) {
         double percentage = (double) i / numYTicks;
         int y = GraphUtil.percentToY(percentage, height, topPadding, bottomPadding);
         g2d.drawLine(leftPadding - 5, y, leftPadding, y);
         String label = String.format("%.0f%%", percentage * 100);
         FontMetrics fm = g2d.getFontMetrics();
         int labelWidth = fm.stringWidth(label);
         g2d.drawString(label, leftPadding - 10 - labelWidth, y + 5);
      }
      
      // Axis titles
      g2d.setFont(new Font("Arial", Font.BOLD, 14));
      g2d.drawString("Value", width / 2, height - 20);
      g2d.translate(20, height / 2);
      g2d.rotate(-Math.PI / 2);
      g2d.drawString("Cumulative Frequency (%)", 0, 0);
      g2d.rotate(Math.PI / 2);
      g2d.translate(-20, -height / 2);
      
      // Key statistics
      g2d.setFont(new Font("Arial", Font.PLAIN, 12));
      g2d.drawString("N = " + stats.size(), width - 150, topPadding + 20);
      g2d.drawString("Min: " + String.format("%.2f", min), width - 150, topPadding + 40);
      g2d.drawString("Max: " + String.format("%.2f", max), width - 150, topPadding + 60);
      g2d.drawString("Mean: " + String.format("%.2f", stats.mean()), width - 150, topPadding + 80);
      g2d.drawString("StdDev: " + String.format("%.2f", stats.stddev()), width - 150, topPadding + 100);
      
      // Median and quartile markers
      double median = stats.median();
      int medianX = GraphUtil.calcValueToX(median, min, max, leftPadding, rightPadding, width);
      int medianY = GraphUtil.percentToY(0.5, height, topPadding, bottomPadding);
      g2d.setColor(theme[2]);
      g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{5.0f}, 0.0f));
      g2d.drawLine(leftPadding, medianY, medianX, medianY);
      g2d.drawLine(medianX, medianY, medianX, height - bottomPadding);
      g2d.setColor(theme[3]);
      g2d.setFont(new Font("Arial", Font.ITALIC, 12));
      g2d.drawString("Median: " + String.format("%.2f", median), medianX - 30, medianY - 10);
      
      double[] quartiles = stats.findQuarterPercentiles();
      double q1 = quartiles[0];
      double q3 = quartiles[1];
      int q1X = GraphUtil.calcValueToX(q1, min, max, leftPadding, rightPadding, width);
      int q1Y = GraphUtil.percentToY(0.25, height, topPadding, bottomPadding);
      int q3X = GraphUtil.calcValueToX(q3, min, max, leftPadding, rightPadding, width);
      int q3Y = GraphUtil.percentToY(0.75, height, topPadding, bottomPadding);
      g2d.setColor(theme[2]);
      g2d.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{3.0f}, 0.0f));
      g2d.drawLine(leftPadding, q1Y, q1X, q1Y);
      g2d.drawLine(q1X, q1Y, q1X, height - bottomPadding);
      g2d.drawLine(leftPadding, q3Y, q3X, q3Y);
      g2d.drawLine(q3X, q3Y, q3X, height - bottomPadding);
      g2d.setColor(theme[3]);
      g2d.setFont(new Font("Arial", Font.ITALIC, 11));
      g2d.drawString("Q1: " + String.format("%.2f", q1), q1X - 30, q1Y - 5);
      g2d.drawString("Q3: " + String.format("%.2f", q3), q3X - 30, q3Y - 5);
   }
   
   public void showGraph() {
      JFrame frame = new JFrame(title);
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.setSize(800, 500);
      frame.add(this);
      frame.setVisible(true);
   }
}
