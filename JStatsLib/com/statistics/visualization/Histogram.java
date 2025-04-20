package com.statistics.visualization;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.statistics.JStatsLib;

/**
 * <summary>
 * A histogram to represent your data
 * </summary>
 */

public class Histogram extends JPanel {
   
   private JStatsLib stats;
   private ArrayList<Double> values;
   private double min;
   private double max;
   private String title;
   private Color[] theme;
   private int numBins = 10; // Default number of bins
   
   public Histogram(JStatsLib stats, int bins, String title, Color[] theme) {
      this.stats = stats;
      this.values = stats.toList();
      this.min = stats.min();
      this.max = stats.max();
      this.title = title;
      this.theme = theme;
      if(bins < 1){
         System.out.println("HISTOGRAM ERROR: numBins must be >= 1. Setting numBins to 10");
         this.numBins = 10;
      } else {
         this.numBins = bins;
      }
   }
   
   public void setBinSize(int bins) {
      if (bins > 0) {
         this.numBins = bins;
         repaint();
      }
   }
   
   public int getBinSize() {
      return this.numBins;
   }
   
   private int freqToY(int val, int height, int maxFreq) {
      return GraphUtil.freqToY(val, height, maxFreq);
   }
   
   @Override
   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
      GraphUtil.setupRenderingHints(g2d);
      
      int width = getWidth();
      int height = getHeight();
      
      // Draw background and title
      GraphUtil.drawBackgroundAndTitle(g2d, width, height, title, theme);
      
      // Make Bins
      double range = max - min;
      double binWidth = range / numBins;
      
      Map<Integer, Integer> bins = new HashMap<>();
      for (int i = 0; i < numBins; i++) {
         bins.put(i, 0);
      }
      
      for (Double value : values) {
         if (value >= min && value <= max) {
            int binIndex = (int) Math.floor((value - min) / binWidth);
            if (binIndex == numBins) binIndex--;
            bins.put(binIndex, bins.get(binIndex) + 1);
         }
      }
      
      // Find tallest bin
      int maxFreq = 0;
      for (Integer freq : bins.values()) {
         maxFreq = Math.max(maxFreq, freq);
      }
      
      maxFreq = (int) (maxFreq * 1.1);
      if (maxFreq == 0) maxFreq = 1;
      
      int drawingWidth = width - GraphUtil.LEFT_PADDING - GraphUtil.PADDING;
      
      // Draw Axes
      int axisY = height - GraphUtil.BOTTOM_PADDING;
      GraphUtil.drawYAxis(g2d, width, height, maxFreq, GraphUtil.LEFT_PADDING, GraphUtil.BOTTOM_PADDING, GraphUtil.TOP_PADDING, GraphUtil.RIGHT_PADDING, theme, "Frequency");
      GraphUtil.drawXAxis(g2d, width, height, min, max, axisY, GraphUtil.LEFT_PADDING, GraphUtil.PADDING, theme);
      
      // Draw Bins
      for (int i = 0; i < numBins; i++) {
         double binStart = min + (i * binWidth);
         int binX = GraphUtil.valueToX(binStart, min, range, drawingWidth, GraphUtil.LEFT_PADDING);
         int nextX = GraphUtil.valueToX(binStart + binWidth, min, range, drawingWidth, GraphUtil.LEFT_PADDING);
         int barWidth = nextX - binX;
         
         int frequency = bins.get(i);
         int barHeight = axisY - freqToY(frequency, height, maxFreq);
         
         g2d.setColor(theme[0]);
         g2d.fillRect(binX, axisY - barHeight, barWidth, barHeight);
         g2d.setColor(theme[1]);
         g2d.drawRect(binX, axisY - barHeight, barWidth, barHeight);
         
         // Label each bar
         if (barHeight > 15) {
            g2d.setColor(theme[4]);
            String freqLabel = Integer.toString(frequency);
            FontMetrics fm = g2d.getFontMetrics();
            int labelWidth = fm.stringWidth(freqLabel);
            if (labelWidth < barWidth - 4) {
               g2d.drawString(freqLabel, binX + (barWidth - labelWidth) / 2, axisY - barHeight + 15);
            }
         }
      }
      
      // Draw Stats
      g2d.setColor(theme[4]);
      g2d.setFont(GraphUtil.ITALIC_FONT);
      g2d.drawString("Bins: " + numBins, GraphUtil.LEFT_PADDING, height - 20);
      g2d.drawString("Bin width: " + String.format("%.2f", binWidth), GraphUtil.LEFT_PADDING + 100, height - 20);
      g2d.drawString("Total values: " + values.size(), GraphUtil.LEFT_PADDING + 250, height - 20);
      
      GraphUtil.drawStatistics(g2d, stats, width - GraphUtil.PADDING - 150, GraphUtil.TOP_PADDING, theme);
   }
   
   public void showGraph() {
      GraphUtil.showInFrame(this, title, 800, 500);
   }
   
   public static Histogram createFromStats(JStatsLib stats, int bins, String title, Color[] theme) {
      return new Histogram(stats, bins, title, theme);
   }
}
