package com.statistics.analysis;

import com.statistics.utils.RBTree;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides fundamental statistical calculations for data stored in a Red-Black Tree structure.
 * This class implements core descriptive statistics methods and serves as a foundation
 * for more advanced statistical analyses.
 */
public class BasicStats {
    private RBTree tree;
    private HashMap<Double, Integer> map;
    private ArrayList<Double> arrayList;

    /**
     * Constructs a BasicStats object using the provided Red-Black Tree data structure.
     * Initializes frequency map and sorted array list for efficient statistical calculations.
     * 
     * @param tree The Red-Black Tree containing the dataset to analyze
     */
    public BasicStats(RBTree tree) {
        this.tree = tree;
        this.map = tree.toFrequencyMap();
        this.arrayList = tree.toArrayList();
    }

    /**
     * Calculates the arithmetic mean (average) of the dataset.
     * Takes into account the frequency of each value for weighted calculation.
     * 
     * @return The arithmetic mean of all values in the dataset
     */
    public double mean() {
        double sum = 0;
        for (Map.Entry<Double, Integer> entry : map.entrySet()) {
            sum += entry.getKey() * entry.getValue();
        }
        return sum / tree.getSize();
    }

    /**
     * Determines the mode of the dataset - the value that appears most frequently.
     * If multiple values have the same highest frequency, returns the first one found.
     * 
     * @return The most frequently occurring value in the dataset
     */
    public Double mode() {
        double mode = 0.0;
        int maxCount = 0;
        for (Map.Entry<Double, Integer> entry : map.entrySet()) {
            if (entry.getValue() > maxCount) {
                mode = entry.getKey();
                maxCount = entry.getValue();
            }
        }
        return mode;
    }

    /**
     * Calculates the median of the dataset - the middle value when data is sorted.
     * For even-sized datasets, returns the average of the two middle values.
     * 
     * @return The median value of the dataset
     */
    public double median() {
        int n = tree.getSize();
        if (n % 2 == 0) {
            return (arrayList.get(n / 2 - 1) + arrayList.get(n / 2)) / 2;
        } else {
            return arrayList.get(n / 2);
        }
    }

    /**
     * Calculates the standard deviation of the dataset.
     * Standard deviation measures the amount of variation or dispersion in the dataset.
     * 
     * @return The standard deviation of the dataset
     */
    public double stddev() {
        return Math.sqrt(variance());
    }

    /**
     * Calculates the variance of the dataset using Bessel's correction (n-1 denominator).
     * Variance measures how far the values are spread out from their average value.
     * 
     * @return The variance of the dataset
     */
    public double variance() {
        double sum = 0;
        double mean = mean();

        for (Map.Entry<Double, Integer> entry : map.entrySet()) {
            sum += Math.pow(entry.getKey() - mean, 2) * entry.getValue();
        }
        return sum / (tree.getSize() - 1);
    }

    /**
     * Calculates the range of the dataset - the difference between the maximum and minimum values.
     * Range provides a simple measure of dispersion or spread.
     * 
     * @return The range of the dataset
     */
    public double range() {
        return arrayList.get(arrayList.size() - 1) - arrayList.get(0);
    }

    /**
     * Returns the minimum value in the dataset.
     * 
     * @return The smallest value in the dataset
     */
    public double min() {
        return arrayList.get(0);
    }

    /**
     * Returns the maximum value in the dataset.
     * 
     * @return The largest value in the dataset
     */
    public double max() {
        return arrayList.get(arrayList.size() - 1);
    }

    /**
     * Calculates the interquartile range (IQR) of the dataset.
     * IQR is the difference between the third quartile (Q3) and first quartile (Q1),
     * representing the middle 50% of the data.
     * 
     * @return The interquartile range of the dataset
     */
    public double iqr() {
        double[] percentiles = findQuarterPercentiles();
        return percentiles[1] - percentiles[0];
    }

    /**
     * Calculates the first (25th percentile) and third (75th percentile) quartiles.
     * Uses linear interpolation for positions that fall between data points.
     * 
     * @return An array containing [Q1, Q3] values
     */
    public double[] findQuarterPercentiles() {
        double q1Index = 0.25 * (arrayList.size() - 1);
        double q3Index = 0.75 * (arrayList.size() - 1);

        double q1 = interpolate(arrayList, q1Index);
        double q3 = interpolate(arrayList, q3Index);

        return new double[]{q1, q3};
    }

    /**
     * Performs linear interpolation to find values at non-integer indices.
     * This allows for more accurate percentile calculations.
     * 
     * @param array The sorted array of data points
     * @param index The position (potentially fractional) at which to interpolate
     * @return The interpolated value at the specified position
     */
    private double interpolate(ArrayList<Double> array, double index) {
        int idx = (int) index;
        double fraction = index - idx;

        if (idx >= array.size() - 1) {
            return array.get(array.size() - 1);
        }
        return array.get(idx) + fraction * (array.get(idx + 1) - array.get(idx));
    }
    
    /**
     * Calculates the rolling (moving) average of the data with a specified window size.
     * The function returns an ArrayList containing the rolling averages where each entry
     * corresponds to the average of the current element and the previous (windowSize-1) elements.
     * The first (windowSize-1) entries will contain averages of fewer elements as there
     * aren't enough previous elements available.
     * 
     * @param windowSize The number of elements to include in the rolling average window
     * @return ArrayList of rolling averages, same size as the original data
     * @throws IllegalArgumentException if windowSize is less than 1 or greater than data size
     */
    public ArrayList<Double> movingAverage(int windowSize) {
        if (windowSize < 1) {
            throw new IllegalArgumentException("Window size must be at least 1");
        }
        
        if (windowSize > arrayList.size()) {
            throw new IllegalArgumentException("Window size cannot be larger than the data size");
        }
        
        ArrayList<Double> result = new ArrayList<>(arrayList.size());
        double sum = 0;
        
        // Process first window elements
        for (int i = 0; i < windowSize; i++) {
            sum += arrayList.get(i);
            result.add(sum / (i + 1));  // Average of elements seen so far
        }
        
        // Process remaining elements using sliding window
        for (int i = windowSize; i < arrayList.size(); i++) {
            sum = sum - arrayList.get(i - windowSize) + arrayList.get(i);
            result.add(sum / windowSize);
        }
        
        return result;
    }
    
    /**
     * Calculates and returns the exponentially weighted moving average (EWMA) of the data.
     * In EWMA, more recent observations are given more weight than older observations,
     * with the weight decreasing exponentially as observations get older.
     * 
     * @param alpha The smoothing factor in the range [0,1]. Higher alpha gives more weight to recent observations.
     * @return ArrayList of exponentially weighted moving averages
     * @throws IllegalArgumentException if alpha is not between 0 and 1
     */
    public ArrayList<Double> exponentialMovingAverage(double alpha) {
        if (alpha < 0 || alpha > 1) {
            throw new IllegalArgumentException("Alpha must be between 0 and 1");
        }
        
        ArrayList<Double> result = new ArrayList<>(arrayList.size());
        
        if (arrayList.isEmpty()) {
            return result;
        }
        
        // Initialize with first value
        double currentEMA = arrayList.get(0);
        result.add(currentEMA);
        
        // Calculate EMA for remaining values
        for (int i = 1; i < arrayList.size(); i++) {
            currentEMA = alpha * arrayList.get(i) + (1 - alpha) * currentEMA;
            result.add(currentEMA);
        }
        
        return result;
    }
    
    /**
     * Calculates centered moving average (used often in time series analysis).
     * For odd window sizes, it's a symmetric window around the current point.
     * For even window sizes, it averages two simple moving averages to center the window.
     * 
     * @param windowSize The window size for the moving average (should be odd for true centering)
     * @return ArrayList of centered moving averages (will be shorter than original data)
     * @throws IllegalArgumentException if windowSize is less than 2 or greater than data size
     */
    public ArrayList<Double> centeredMovingAverage(int windowSize) {
        if (windowSize < 2) {
            throw new IllegalArgumentException("Window size must be at least 2");
        }
        
        if (windowSize > arrayList.size()) {
            throw new IllegalArgumentException("Window size cannot be larger than the data size");
        }
        
        ArrayList<Double> result = new ArrayList<>();
        int offset = windowSize / 2;
        
        // If window odd, center perfectly
        if (windowSize % 2 == 1) {
            for (int i = offset; i < arrayList.size() - offset; i++) {
                double sum = 0;
                for (int j = i - offset; j <= i + offset; j++) {
                    sum += arrayList.get(j);
                }
                result.add(sum / windowSize);
            }
        } 
        // If window even, average two consecutive centered values
        else {
            for (int i = offset; i < arrayList.size() - offset; i++) {
                double sum1 = 0;
                double sum2 = 0;
                
                // First average (i-offset to i+offset-1)
                for (int j = i - offset; j < i + offset; j++) {
                    sum1 += arrayList.get(j);
                }
                
                // Second average (i-offset+1 to i+offset)
                for (int j = i - offset + 1; j <= i + offset; j++) {
                    sum2 += arrayList.get(j);
                }
                
                // Average of the two averages
                result.add((sum1 / windowSize + sum2 / windowSize) / 2);
            }
        }
        
        return result;
    }
}