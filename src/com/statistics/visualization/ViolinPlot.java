package com.statistics.visualization;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Collections;

import com.statistics.JStatsLib;

public class ViolinPlot extends JPanel {
   
   private JStatsLib stats;
   private double median;
   private double iqr;
   private double[] percentiles; // [Q1, Q3]
   private ArrayList<Double> values;
   private double min;
   private double max;
   private String title;
   private Color[] theme;
   private int kernelPoints = 100; // Number of points to use for KDE
   private double bandwidth; // Bandwidth for KDE
   private double maxViolinWidth = 0.35; // Controls max width of violin as proportion of panel
   
   public ViolinPlot(JStatsLib stats, String title, Color[] theme) {
      this.stats = stats;
      this.values = stats.toList();
      this.median = stats.median();
      this.iqr = stats.iqr();
      this.percentiles = stats.findQuarterPercentiles();
      this.min = stats.min();
      this.max = stats.max();
      this.title = title;
      this.theme = theme;
      
      // Auto-calculate bandwidth based on Scott's rule
      // modification of Silverman's rule
      double stdDev = stats.stddev();
      int n = values.size();
      this.bandwidth = 1.06 * stdDev * Math.pow(n, -0.2);
      
      // Ensure bandwidth is not too small
      double range = max - min;
      double minBandwidth = range * 0.01; // At least 1% of the range
      this.bandwidth = Math.max(bandwidth, minBandwidth);
      
      // Debug
      System.out.println("Data range: " + min + " to " + max);
      System.out.println("Auto bandwidth: " + bandwidth);
      System.out.println("Data size: " + n);
   }
   
   /**
    * Set custom bandwidth for kernel density estimation
    * @param bandwidth The bandwidth value to use
    */
   public void setBandwidth(double bandwidth) {
      if (bandwidth <= 0) {
         throw new IllegalArgumentException("Bandwidth must be positive");
      }
      this.bandwidth = bandwidth;
      System.out.println("Manual bandwidth set to: " + bandwidth);
   }
   
   /**
    * Set the maximum width of the violin
    * @param proportion Value between 0 and 1
    */
   public void setMaxViolinWidth(double proportion) {
      if (proportion <= 0 || proportion > 1) {
         throw new IllegalArgumentException("Width proportion must be between 0 and 1");
      }
      this.maxViolinWidth = proportion;
   }
   
   /**
    * Set number of points to use in kernel density estimation
    * @param points Number of points
    */
   public void setKernelPoints(int points) {
      if (points < 10) {
         throw new IllegalArgumentException("Kernel points must be at least 10");
      }
      this.kernelPoints = points;
   }
   
   /**
    * Calculate kernel density estimation for the dataset
    * @return Array of [x, y] points representing the density curve
    */
   private double[][] calculateKDE() {
      // Sort values for more efficient calculation
      Collections.sort(values);
      
      // Prepare the grid of points for KDE calculation
      double range = max - min;
      double padding = range * 0.2; // Add padding to avoid cutoffs
      
      // If range is very small, add more padding
      if (range < 0.001) {
         padding = Math.max(1.0, values.get(0) * 0.5);
      }
      
      double realMin = min - padding;
      double realMax = max + padding;
      double adjustedRange = realMax - realMin;
      double step = adjustedRange / (kernelPoints - 1);
      
      double[] x = new double[kernelPoints];
      double[] density = new double[kernelPoints];
      
      // Generate x values
      for (int i = 0; i < kernelPoints; i++) {
         x[i] = realMin + (i * step);
      }
      
      // Calculate density for each x value
      for (int i = 0; i < kernelPoints; i++) {
         double sum = 0;
         for (Double value : values) {
            // Gaussian kernel function
            double z = (x[i] - value) / bandwidth;
            sum += Math.exp(-0.5 * z * z) / (bandwidth * Math.sqrt(2 * Math.PI));
         }
         density[i] = sum / values.size();
      }
      
      // Handle case when all density values are the same
      boolean allSame = true;
      double firstDensity = density[0];
      for (int i = 1; i < kernelPoints; i++) {
         if (Math.abs(density[i] - firstDensity) > 1e-10) {
            allSame = false;
            break;
         }
      }
      
      if (allSame) {
         System.out.println("Warning: All density values are the same. Try adjusting bandwidth.");
         // If all densities are the same, create an artificial shape
         for (int i = 0; i < kernelPoints; i++) {
            double pos = (double) i / (kernelPoints - 1);
            double adjustedPos = Math.abs(pos - 0.5) * 2.0;
            density[i] = Math.max(0.1, 1.0 - adjustedPos * adjustedPos);
         }
      }
      
      // Normalize density to have maximum width equal to 1
      double maxDensity = 0;
      for (int i = 0; i < kernelPoints; i++) {
         maxDensity = Math.max(maxDensity, density[i]);
      }
      
      if (maxDensity > 0) {
         for (int i = 0; i < kernelPoints; i++) {
            density[i] /= maxDensity;
         }
      }
      
      // Return [x, density] pairs
      double[][] result = new double[2][kernelPoints];
      result[0] = x;
      result[1] = density;
      return result;
   }
   
   @Override
   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
      GraphUtil.setupRenderingHints(g2d);
      g2d.setStroke(new BasicStroke(2));
      
      int width = getWidth();
      int height = getHeight();
      
      // Use GraphUtil for background and title
      GraphUtil.drawBackgroundAndTitle(g2d, width, height, title, theme);
      
      int drawingWidth = width - (2 * GraphUtil.LEFT_PADDING);
      double range = max - min;
      
      // Calculate KDE
      double[][] kde = calculateKDE();
      double[] x = kde[0];
      double[] density = kde[1];
      
      // Draw violin shape
      int yCenter = height / 2;
      int maxWidth = (int)(Math.min(drawingWidth, height) * 0.25 * maxViolinWidth); // Maximum width of violin on each side
      
      // Create paths for the violin
      Path2D.Double violinPath = new Path2D.Double();
      
      // Start with moving to the bottom point
      int startX = GraphUtil.valueToX(x[0], min, range, drawingWidth, GraphUtil.LEFT_PADDING);
      violinPath.moveTo(startX, yCenter);
      
      // Draw the right side of the violin (top to bottom)
      for (int i = 0; i < kernelPoints; i++) {
         int xPos = GraphUtil.valueToX(x[i], min, range, drawingWidth, GraphUtil.LEFT_PADDING);
         int halfWidth = (int) (density[i] * maxWidth);
         
         violinPath.lineTo(xPos, yCenter - halfWidth);
      }
      
      // Draw the left side of the violin (bottom to top)
      for (int i = kernelPoints - 1; i >= 0; i--) {
         int xPos = GraphUtil.valueToX(x[i], min, range, drawingWidth, GraphUtil.LEFT_PADDING);
         int halfWidth = (int) (density[i] * maxWidth);
         
         violinPath.lineTo(xPos, yCenter + halfWidth);
      }
      
      violinPath.closePath();
      
      // Draw the violin
      g2d.setColor(new Color(theme[0].getRed(), theme[0].getGreen(), theme[0].getBlue(), 180));
      g2d.fill(violinPath);
      g2d.setColor(theme[1]);
      g2d.draw(violinPath);
      
      // Draw box plot elements inside the violin
      int q1Pos = GraphUtil.valueToX(percentiles[0], min, range, drawingWidth, GraphUtil.LEFT_PADDING);
      int q3Pos = GraphUtil.valueToX(percentiles[1], min, range, drawingWidth, GraphUtil.LEFT_PADDING);
      int medianPos = GraphUtil.valueToX(median, min, range, drawingWidth, GraphUtil.LEFT_PADDING);
      
      // Draw the quartile box
      int boxHeight = maxWidth / 2;
      g2d.setColor(new Color(theme[2].getRed(), theme[2].getGreen(), theme[2].getBlue(), 120));
      g2d.fillRect(q1Pos, yCenter - boxHeight/2, q3Pos - q1Pos, boxHeight);
      g2d.setColor(theme[2]);
      g2d.drawRect(q1Pos, yCenter - boxHeight/2, q3Pos - q1Pos, boxHeight);
      
      // Draw the median line
      g2d.setColor(theme[3]);
      g2d.setStroke(new BasicStroke(3));
      g2d.drawLine(medianPos, yCenter - boxHeight/2, medianPos, yCenter + boxHeight/2);
      
      // Draw data points as small circles
      g2d.setColor(new Color(0, 0, 0, 150));
      int dotSize = 4;
      for (Double value : values) {
         int xPos = GraphUtil.valueToX(value, min, range, drawingWidth, GraphUtil.LEFT_PADDING);
         // Jitter the y position to avoid overlap
         int yOffset = (int)(Math.random() * boxHeight) - boxHeight/2;
         g2d.fillOval(xPos - dotSize/2, yCenter + yOffset, dotSize, dotSize);
      }
      
      // Draw X-axis
      int scaleY = height - GraphUtil.BOTTOM_PADDING;
      GraphUtil.drawXAxis(g2d, width, height, min, max, scaleY, GraphUtil.LEFT_PADDING, GraphUtil.RIGHT_PADDING, theme);
      
      // Draw statistics using GraphUtil
      GraphUtil.drawStatistics(g2d, stats, width - GraphUtil.PADDING - 150, GraphUtil.TOP_PADDING, theme);
   }
   
   public void showGraph() {
      GraphUtil.showInFrame(this, title, 800, 400);
   }
}