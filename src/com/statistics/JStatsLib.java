package com.statistics;
import com.statistics.utils.FileHandler;
import com.statistics.analysis.BasicStats;
import com.statistics.analysis.AdvancedStats;
import com.statistics.utils.RBTree;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <summary>
 * JStatsLib provides an interface for importing data and performing statistical operations.
 *
 * Usage:
 * - Create a JStatsLib object and load data from a file.
 * - Call statistical functions directly on the object.
 *
 * </summary>
 */
 
public class JStatsLib {
    private RBTree tree;
    private BasicStats basicStats;
    private AdvancedStats advancedStats;
    
    /**
     * Constructs a new JStatsLib instance and loads data from the specified path.
     * 
     * @param path The file path to load data from
     * @param delimiter The delimiter used in the file
     * @throws IllegalArgumentException if the file cannot be read or is empty
     */
    public JStatsLib(String path, String delimiter) {
        tree = new RBTree();
        importData(path, delimiter);
        
        if (tree.getSize() == 0) {
            throw new IllegalArgumentException("No data was imported. Please check your file path and format.");
        }
        
        basicStats = new BasicStats(tree);
        advancedStats = new AdvancedStats(tree);
    }
    
    /**
     * Import data from file
     * 
     * @param path The file path
     * @param delimiter The delimiter used in the file
     * @throws IllegalArgumentException if the file path is invalid
     */
    public void importData(String path, String delimiter) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        
        FileHandler fileHandler = new FileHandler(path, delimiter, tree);
        fileHandler.readFile();
    }
    
    /**
     * Checks if the data size is sufficient for statistical calculations
     * 
     * @param minSize The minimum required size
     * @throws IllegalStateException if the data size is insufficient
     */
    private void checkDataSize(int minSize) {
        if (tree.getSize() < minSize) {
            throw new IllegalStateException("Insufficient data points. At least " + minSize + " values required.");
        }
    }
    
    /**
     * Calculates the arithmetic mean of the dataset
     * 
     * @return The mean value
     * @throws IllegalStateException if there is no data
     */
    public double mean() {
        checkDataSize(1);
        return basicStats.mean();
    }
    
    /**
     * Finds the mode (most frequent value) in the dataset
     * 
     * @return The mode or null if no unique mode exists
     * @throws IllegalStateException if there is no data
     */
    public Double mode() {
        checkDataSize(1);
        return basicStats.mode();
    }
    
    /**
     * Calculates the median (middle value) of the dataset
     * 
     * @return The median value
     * @throws IllegalStateException if there is no data
     */
    public double median() {
        checkDataSize(1);
        return basicStats.median();
    }
    
    /**
     * Calculates the standard deviation of the dataset
     * 
     * @return The standard deviation
     * @throws IllegalStateException if there are fewer than 2 data points
     */
    public double stddev() {
        checkDataSize(2);
        double result = basicStats.stddev();
        if (Double.isNaN(result) || result < 0) {
            throw new IllegalStateException("Standard deviation calculation resulted in an invalid value: " + result);
        }
        return result;
    }
    
    /**
     * Calculates the variance of the dataset
     * 
     * @return The variance
     * @throws IllegalStateException if there are fewer than 2 data points
     */
    public double variance() {
        checkDataSize(2);
        double result = basicStats.variance();
        if (Double.isNaN(result) || result < 0) {
            throw new IllegalStateException("Variance calculation resulted in an invalid value: " + result);
        }
        return result;
    }
    
    /**
     * Calculates the range (max - min) of the dataset
     * 
     * @return The range
     * @throws IllegalStateException if there is no data
     */
    public double range() {
        checkDataSize(1);
        return basicStats.range();
    }
    
    /**
     * Finds the minimum value in the dataset
     * 
     * @return The minimum value
     * @throws IllegalStateException if there is no data
     */
    public double min() {
        checkDataSize(1);
        return basicStats.min();
    }
    
    /**
     * Finds the maximum value in the dataset
     * 
     * @return The maximum value
     * @throws IllegalStateException if there is no data
     */
    public double max() {
        checkDataSize(1);
        return basicStats.max();
    }
    
    /**
     * Calculates the interquartile range (IQR) of the dataset
     * 
     * @return The IQR
     * @throws IllegalStateException if there are fewer than 4 data points
     */
    public double iqr() {
        checkDataSize(4);
        double result = basicStats.iqr();
        if (Double.isNaN(result) || result < 0) {
            throw new IllegalStateException("IQR calculation resulted in an invalid value: " + result);
        }
        return result;
    }
    
    /**
     * Calculates the normal probability density function at the given point
     * 
     * @param number The point to calculate the PDF at
     * @return The PDF value
     * @throws IllegalStateException if standard deviation is invalid
     */
    public double normPDF(double number) {
        checkDataSize(2);
        try {
            double stdDev = basicStats.stddev();
            if (Double.isNaN(stdDev) || stdDev <= 0) {
                throw new IllegalStateException("Cannot calculate normal PDF: Standard deviation must be positive");
            }
            return advancedStats.normPDF(number);
        } catch (Exception e) {
            throw new IllegalStateException("Error calculating normal PDF: " + e.getMessage(), e);
        }
    }
    
    /**
     * Calculates the cumulative distribution function at the given point
     * 
     * @param number The point to calculate the CDF at
     * @return The CDF value
     * @throws IllegalStateException if standard deviation is invalid
     */
    public double normCDF(double number) {
        checkDataSize(2);
        try {
            double stdDev = basicStats.stddev();
            if (Double.isNaN(stdDev) || stdDev <= 0) {
                throw new IllegalStateException("Cannot calculate normal CDF: Standard deviation must be positive");
            }
            return advancedStats.normCDF(number);
        } catch (Exception e) {
            throw new IllegalStateException("Error calculating normal CDF: " + e.getMessage(), e);
        }
    }
    
    /**
     * Returns the size of the dataset
     * 
     * @return The number of data points
     */
    public int size() {
        return tree.getSize();
    }
    
    /**
     * Calculates the inverse normal CDF
     * 
     * @param value The probability value (0-1)
     * @param mean The mean of the normal distribution
     * @param stddev The standard deviation of the normal distribution
     * @return The x-value corresponding to the given probability
     * @throws IllegalArgumentException if parameters are invalid
     */
    public double invNorm(double value, double mean, double stddev) {
        if (value <= 0 || value >= 1) {
            throw new IllegalArgumentException("Probability value must be between 0 and 1 exclusive");
        }
        if (Double.isNaN(stddev) || stddev <= 0) {
            throw new IllegalArgumentException("Standard deviation must be positive");
        }
        return advancedStats.invNorm(value, mean, stddev);
    }
    
    /**
     * Calculates a z-interval for the dataset
     * 
     * @param cLevel The confidence level (0-1)
     * @param mean The mean of the distribution
     * @param stddev The standard deviation of the distribution
     * @return The confidence interval as a 2-element array
     * @throws IllegalArgumentException if parameters are invalid
     */
    public double[] zInterval(double cLevel, double mean, double stddev) {
        checkDataSize(2);
        if (cLevel <= 0 || cLevel >= 1) {
            throw new IllegalArgumentException("Confidence level must be between 0 and 1 exclusive");
        }
        if (Double.isNaN(stddev) || stddev <= 0) {
            throw new IllegalArgumentException("Standard deviation must be positive");
        }
        return advancedStats.zInterval(cLevel, mean, stddev);
    }
    
    /**
     * Finds the 25th, 50th, and 75th percentiles
     * 
     * @return An array containing the quartile values
     * @throws IllegalStateException if there is insufficient data
     */
    public double[] findQuarterPercentiles() {
        checkDataSize(4);
        double[] result = basicStats.findQuarterPercentiles();
        // Validate results
        for (double val : result) {
            if (Double.isNaN(val) || Double.isInfinite(val)) {
                throw new IllegalStateException("Invalid percentile calculation result");
            }
        }
        return result;
    }
    
    /**
     * Calculates the moving average with the specified window size
     * 
     * @param windowSize The size of the moving window
     * @return A list containing the moving averages
     * @throws IllegalArgumentException if the window size is invalid
     */
    public ArrayList<Double> movingAverage(int windowSize) {
        if (windowSize <= 0) {
            throw new IllegalArgumentException("Window size must be positive");
        }
        if (windowSize > tree.getSize()) {
            throw new IllegalArgumentException("Window size cannot be larger than the data size");
        }
        return basicStats.movingAverage(windowSize);
    }
    
    /**
     * Calculates the exponential moving average with the specified alpha
     * 
     * @param alpha The smoothing factor (0-1)
     * @return A list containing the exponential moving averages
     * @throws IllegalArgumentException if alpha is invalid
     */
    public ArrayList<Double> exponentialMovingAverage(double alpha) {
        if (alpha <= 0 || alpha >= 1) {
            throw new IllegalArgumentException("Alpha must be between 0 and 1 exclusive");
        }
        return basicStats.exponentialMovingAverage(alpha);
    }
    
    /**
     * Calculates the centered moving average with the specified window size
     * 
     * @param windowSize The size of the moving window
     * @return A list containing the centered moving averages
     */
    public ArrayList<Double> centeredMovingAverage(int windowSize) {
        return basicStats.centeredMovingAverage(windowSize);
    }
    
    /**
     * Converts the dataset to an ArrayList
     * 
     * @return An ArrayList containing all data points
     */
    public ArrayList<Double> toList() {
        return tree.toArrayList();
    }
    
    /**
     * Creates a frequency map of the dataset
     * 
     * @return A HashMap mapping values to their frequencies
     */
    public HashMap<Double, Integer> toFrequencyMap() {
        return tree.toFrequencyMap();
    }
}