package com.statistics.visualization;

import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.BasicStroke;
import java.awt.geom.Arc2D;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import com.statistics.JStatsLib;
import com.statistics.utils.Themes;

public class PieChart extends JPanel {
   
   private JStatsLib stats;
   private HashMap<Double, Integer> frequencyMap;
   private String title;
   private Color[] theme;
   private int totalCount;
   
   private int padding = 50;
   private int legendPadding = 200;
   
   public PieChart(JStatsLib stats, String title, Color[] theme) {
      this.stats = stats;
      this.frequencyMap = stats.toFrequencyMap();
      this.title = title;
      this.theme = theme;
      this.totalCount = stats.size();
   }
   
   @Override
   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
      GraphUtil.setupRenderingHints(g2d);
      
      int width = getWidth();
      int height = getHeight();
      
      // Use GraphUtil to draw background and title.
      GraphUtil.drawBackgroundAndTitle(g2d, width, height, title, theme);
      
      // The rest of the pie drawing (slices, legend, etc.) remains.
      int pieWidth = width - padding * 2 - legendPadding;
      int pieHeight = height - padding * 2;
      int diameter = Math.min(pieWidth, pieHeight);
      int x = padding;
      int y = (height - diameter) / 2;
      
      if (totalCount == 0) {
         g2d.setColor(Color.RED);
         g2d.setFont(new Font("Arial", Font.BOLD, 14));
         String noDataMsg = "No data to display";
         FontMetrics metrics = g2d.getFontMetrics();
         g2d.drawString(noDataMsg, (width - metrics.stringWidth(noDataMsg)) / 2, height / 2);
         return;
      }
      
      ArrayList<Map.Entry<Double, Integer>> sortedEntries = new ArrayList<>(frequencyMap.entrySet());
      sortedEntries.sort(Map.Entry.comparingByKey());
      
      double startAngle = 0;
      int legendY = padding;
      int legendSquareSize = 15;
      int legendTextOffset = 20;
      int colorIndex = 0;
      
      for (Map.Entry<Double, Integer> entry : sortedEntries) {
         double value = entry.getKey();
         int count = entry.getValue();
         double percentage = (double) count / totalCount;
         double arcAngle = 360.0 * percentage;
         
         Color sliceColor = theme[colorIndex % theme.length];
         colorIndex++;
         if (colorIndex >= theme.length) {
            colorIndex = 0;
         }
         
         g2d.setColor(sliceColor);
         Arc2D.Double arc = new Arc2D.Double(x, y, diameter, diameter, startAngle, arcAngle, Arc2D.PIE);
         g2d.fill(arc);
         g2d.setColor(Color.BLACK);
         g2d.draw(arc);
         
         // Draw the legend.
         g2d.setColor(sliceColor);
         g2d.fillRect(width - legendPadding + padding, legendY, legendSquareSize, legendSquareSize);
         g2d.setColor(Color.BLACK);
         g2d.drawRect(width - legendPadding + padding, legendY, legendSquareSize, legendSquareSize);
         g2d.setColor(theme[4]);
         g2d.setFont(new Font("Arial", Font.PLAIN, 12));
         String legendText = String.format("%.1f: %d (%.1f%%)", value, count, percentage * 100);
         g2d.drawString(legendText, width - legendPadding + padding + legendTextOffset, legendY + legendSquareSize - 2);
         
         startAngle += arcAngle;
         legendY += legendSquareSize + 10;
      }
      
      // Total count and basic statistics remain at the bottom.
      g2d.setColor(theme[4]);
      g2d.setFont(new Font("Arial", Font.BOLD, 12));
      g2d.drawString("Total count: " + totalCount, width - legendPadding + padding, legendY + 20);
      
      int statsY = legendY + 50;
      g2d.drawString("Statistics:", width - legendPadding + padding, statsY);
      g2d.drawString("Mean: " + String.format("%.2f", stats.mean()), width - legendPadding + padding, statsY + 20);
      g2d.drawString("Median: " + String.format("%.2f", stats.median()), width - legendPadding + padding, statsY + 40);
      g2d.drawString("StdDev: " + String.format("%.2f", stats.stddev()), width - legendPadding + padding, statsY + 60);
   }
   
   public void showGraph() {
      JFrame frame = new JFrame(title);
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.setSize(800, 500);
      frame.add(this);
      frame.setVisible(true);
   }
}
