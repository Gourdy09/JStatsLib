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
import java.util.HashMap;
import java.util.Map;

import com.statistics.JStatsLib;

public class DotPlot extends JPanel {
	/**
	 * <summary>
	 * A dot plot to represent your data
	 * </summary>
	 */
   
   private JStatsLib stats;
   private java.util.ArrayList<Double> values;
   private double min;
   private double max;
   private String title;
   private Color[] theme;
   private int dotSize = 8;
   private int maxDotsInColumn = 20;
   
   private int padding = 50;
   private int bottomPadding = 70;
   private int topPadding = 50;
   
   public DotPlot(JStatsLib stats, String title, Color[] theme) {
      this.stats = stats;
      this.values = stats.toList();
      this.min = stats.min();
      this.max = stats.max();
      this.title = title;
      this.theme = theme;
   }
   
   // Use GraphUtil.calcValueToX if needed, but provide a simple conversion here
   private int valueToX(double val, double scaleFactor) {
      return padding + (int)((val - min) * scaleFactor);
   }
   
   private double roundToBin(double value, double binWidth) {
      return Math.round(value / binWidth) * binWidth;
   }
   
   @Override
   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
      GraphUtil.setupRenderingHints(g2d);
      
      int width = getWidth();
      int height = getHeight();
      
      // Use GraphUtil to draw background and title
      GraphUtil.drawBackgroundAndTitle(g2d, width, height, title, theme);
      
      int drawingWidth = width - (2 * padding);
      double range = max - min;
      double scaleFactor = drawingWidth / range;
      
      double suggestedBinCount = Math.min(50, Math.max(10, drawingWidth / (dotSize * 2)));
      double binWidth = range / suggestedBinCount;
      double magnitude = Math.pow(10, Math.floor(Math.log10(binWidth)));
      binWidth = Math.ceil(binWidth / magnitude) * magnitude;
      
      HashMap<Double, Integer> frequencyMap = stats.toFrequencyMap();
      Map<Double, Integer> binCounts = new java.util.HashMap<>();
      for (Map.Entry<Double, Integer> entry : frequencyMap.entrySet()) {
         double value = entry.getKey();
         int count = entry.getValue();
         double binValue = roundToBin(value, binWidth);
         binCounts.put(binValue, binCounts.getOrDefault(binValue, 0) + count);
      }
      
      int maxCount = 0;
      for (Integer count : binCounts.values()) {
         maxCount = Math.max(maxCount, count);
      }
      
      int availableHeight = height - topPadding - bottomPadding;
      if (maxCount > maxDotsInColumn) {
         dotSize = Math.max(4, Math.min(dotSize, availableHeight / maxDotsInColumn));
      }
      
      for (Map.Entry<Double, Integer> entry : binCounts.entrySet()) {
         double binValue = entry.getKey();
         int count = entry.getValue();
         int x = valueToX(binValue, scaleFactor);
         
         for (int i = 0; i < count; i++) {
            int yOffset;
            if (count <= maxDotsInColumn) {
               yOffset = i * (dotSize + 2);
            } else {
               double scaledHeight = availableHeight * ((double) i / count);
               yOffset = (int) scaledHeight;
            }
            int y = height - bottomPadding - yOffset - dotSize;
            
            g2d.setColor(theme[0]);
            g2d.fillOval(x - dotSize/2, y - dotSize/2, dotSize, dotSize);
            g2d.setColor(theme[4]);
            g2d.drawOval(x - dotSize/2, y - dotSize/2, dotSize, dotSize);
         }
      }
      
      // Draw X-axis using GraphUtil
      g2d.setColor(theme[4]);
      int axisY = height - bottomPadding;
      g2d.drawLine(padding, axisY, width - padding, axisY);
      
      int numTicks = 10;
      g2d.setFont(new Font("Arial", Font.PLAIN, 12));
      for (int i = 0; i <= numTicks; i++) {
         double value = min + (range * i / numTicks);
         int xPos = valueToX(value, scaleFactor);
         g2d.drawLine(xPos, axisY, xPos, axisY + 5);
         String label = String.format("%.1f", value);
         FontMetrics fm = g2d.getFontMetrics();
         int labelWidth = fm.stringWidth(label);
         g2d.drawString(label, xPos - labelWidth / 2, axisY + 20);
      }
      
      // Statistical markers
      double median = stats.median();
      double[] quartiles = stats.findQuarterPercentiles();
      double q1 = quartiles[0];
      double q3 = quartiles[1];
      int medianX = valueToX(median, scaleFactor);
      int q1X = valueToX(q1, scaleFactor);
      int q3X = valueToX(q3, scaleFactor);
      
      g2d.setColor(theme[1]);
      g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{10.0f}, 0.0f));
      g2d.drawLine(medianX, topPadding, medianX, height - bottomPadding);
      
      g2d.setColor(theme[2]);
      g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{5.0f}, 0.0f));
      g2d.drawLine(q1X, topPadding + 60, q1X, height - bottomPadding);
      g2d.drawLine(q3X, topPadding + 20, q3X, height - bottomPadding);
      
      g2d.setColor(theme[4]);
      g2d.setFont(new Font("Arial", Font.ITALIC, 12));
      g2d.drawString("Min: " + String.format("%.2f", min), padding, topPadding - 20);
      g2d.drawString("Q1: " + String.format("%.2f", q1), q1X - 20, topPadding + 60);
      g2d.drawString("Median: " + String.format("%.2f", median), medianX - 25, topPadding - 20);
      g2d.drawString("Q3: " + String.format("%.2f", q3), q3X - 20, topPadding + 20);
      g2d.drawString("Max: " + String.format("%.2f", max), width - padding - 40, topPadding + 20);
      
      // Draw count axis on the left
      int statsX = width - padding - 150;
      int statsY = topPadding;
      int lineHeight = 20;
      g2d.drawString("N = " + stats.size(), statsX, statsY);
      g2d.drawString("Mean: " + String.format("%.2f", stats.mean()), statsX, statsY + lineHeight);
   }
   
   public void showGraph() {
      JFrame frame = new JFrame(title);
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.setSize(800, 500);
      frame.add(this);
      frame.setVisible(true);
   }
}
