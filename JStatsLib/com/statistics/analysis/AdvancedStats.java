package com.statistics.analysis;
import com.statistics.utils.RBTree;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <summary>
 * Provides advanced statistical calculations based on data stored in a Red-Black Tree structure.
 * This class builds upon BasicStats functionality and implements various statistical methods
 * for hypothesis testing, probability distributions, and confidence intervals.
 * </summary>
 */
public class AdvancedStats {
    private RBTree tree;
    private BasicStats basicStats;
    // Mathematical constants for optimization
    private static final double SQRT_2PI = Math.sqrt(2 * Math.PI);
    private static final double SQRT_2 = Math.sqrt(2);

    /**
     * Constructs an AdvancedStats object using the provided Red-Black Tree data structure.
     * 
     * @param tree The Red-Black Tree containing the data to analyze
     */
    public AdvancedStats(RBTree tree) {
        this.tree = tree;
        this.basicStats = new BasicStats(tree);
    }

    /**
     * Calculates the t-score (t-statistic) for the data against a known population mean.
     * Used for determining if sample mean differs significantly from the population mean.
     * 
     * @param populationMean The known mean of the population to test against
     * @return The t-score representing the standardized difference
     */
    public double tScore(double populationMean) {
        return (basicStats.mean() - populationMean) / (basicStats.stddev() / Math.sqrt(tree.getSize()));
    }

    /**
     * Computes the z-score (standard score) for a given value within the data distribution.
     * Represents how many standard deviations a value is from the mean.
     * 
     * @param number The value to calculate the z-score for
     * @return The z-score (number of standard deviations from the mean)
     */
    public double zScore(double number) {
        return (number - basicStats.mean()) / basicStats.stddev();
    }

    /**
     * Calculates the probability density function (PDF) value for a normal distribution
     * based on the dataset's mean and standard deviation.
     * 
     * @param number The point at which to evaluate the PDF
     * @return The probability density at the given point
     */
    public double normPDF(double number) {
        double sigma = basicStats.stddev();
        double mu = basicStats.mean();
        double z = (number - mu) / sigma;
        return (1 / (SQRT_2PI * sigma)) * Math.exp(-0.5 * z * z);
    }

    /**
     * Calculates the cumulative distribution function (CDF) value for a normal distribution
     * based on the dataset's mean and standard deviation.
     * 
     * @param number The point up to which to calculate the cumulative probability
     * @return The probability that a random variable is less than or equal to the given number
     */
    public double normCDF(double number) {
        double sigma = basicStats.stddev();
        double mu = basicStats.mean();
        double z = (number - mu) / (sigma * SQRT_2);
        return 0.5 * (1 + errorFunction(z));
    }

    /**
     * Calculates the error function (erf) using the Abramowitz and Stegun approximation.
     * This implementation provides a balance between accuracy and computational efficiency.
     * 
     * @param x The input value
     * @return The error function value at x, with accuracy < 1.5×10^-7
     */
    public double errorFunction(double x) {
        // Handle sign
        boolean negative = x < 0;
        double absX = Math.abs(x);
        
        // For large values of |x|, erf(x) approaches sign(x)
        if (absX > 5.0) {
            return negative ? -1.0 : 1.0;
        }
        
        // Abramowitz and Stegun approximation (error < 1.5×10^-7)
        double t = 1.0 / (1.0 + 0.3275911 * absX);
        double result = 1.0 - ((((1.061405429 * t - 1.453152027) * t + 1.421413741) * t 
                            - 0.284496736) * t + 0.254829592) * t * Math.exp(-absX * absX);
                            
        return negative ? -result : result;
    }

    /**
     * Implements the inverse normal cumulative distribution function (quantile function)
     * using a rational approximation algorithm for computational efficiency.
     * 
     * @param p The probability value (0 < p < 1)
     * @param mean The mean of the desired normal distribution
     * @param stddev The standard deviation of the desired normal distribution
     * @return The value x such that P(X ≤ x) = p for a normal random variable X
     */
    public double invNorm(double p, double mean, double stddev) {
        if (p <= 0 || p >= 1) {
            System.out.println("Please enter a confidence level between 0 and 1 (exclusive)");
            return 0;
        }
        
        // Rational approximation for normal quantile function
        double q = p - 0.5;
        double r;
        
        if (Math.abs(q) <= 0.425) {
            // Central region (|p - 0.5| ≤ 0.425)
            double r2 = q * q;
            r = q * ((((-0.007784894002430293 * r2 - 0.3223964580411365) * r2 - 2.400758277161838) * r2 - 2.549732539343734) * r2 + 4.5142647074103571) / 
                (((((0.007784695709041462 * r2 + 0.3224671290700398) * r2 + 2.445134137142996) * r2 + 3.754408661907416) * r2 + 1.0) * r2);
        } else {
            // Tail regions
            if (q <= 0) {
                r = p;
            } else {
                r = 1 - p;
            }
            
            r = Math.sqrt(-Math.log(r));
            
            // Approximation for lower and upper regions
            if (r <= 5.0) {
                // Region between central and extreme tail
                r = r - 1.6;
                r = (((((((0.0000736 * r + 0.0003617) * r - 0.0063904) * r + 0.0247856) * r
                        - 0.0838501) * r + 0.1776551) * r - 0.3280429) * r + 0.4863464) * r;
            } else {
                // Extreme tail region
                r = (((((((2.88e-7 * r + 1.702e-5) * r - 0.0003968) * r + 0.0059681) * r
                        - 0.0384288) * r + 0.1066453) * r - 0.1376678) * r + 0.2283857) * r;
            }
            
            if (q < 0) {
                r = -r;
            }
        }
        
        // Transform from standard normal to desired distribution
        return mean + stddev * r;
    }

    /**
     * Calculates a confidence interval for the mean using z-distribution.
     * For large sample sizes, the z-distribution closely approximates the t-distribution.
     * This method includes performance optimizations for common confidence levels.
     * 
     * @param cLevel The confidence level (between 0 and 1, e.g., 0.95 for 95% confidence)
     * @param mean The sample mean
     * @param stddev The sample standard deviation
     * @return An array containing the lower and upper bounds of the confidence interval
     */
    public double[] zInterval(double cLevel, double mean, double stddev) {
        double[] interval = new double[2];
        
        // For common confidence levels, use pre-computed z-values
        double z;
        if (Math.abs(cLevel - 0.90) < 1e-10) {
            z = 1.645;  // 90% confidence level
        } else if (Math.abs(cLevel - 0.95) < 1e-10) {
            z = 1.96;   // 95% confidence level
        } else if (Math.abs(cLevel - 0.99) < 1e-10) {
            z = 2.576;  // 99% confidence level
        } else {
            double alpha = 1 - cLevel;
            double pValue = 1 - alpha/2;  // For two-tailed test
            z = invNorm(pValue, 0, 1);
        }
        
        // Calculate margin of error
        double margin = z * (stddev / Math.sqrt(tree.getSize()));
        
        // Compute confidence interval bounds
        interval[0] = basicStats.mean() - margin;  // Lower bound
        interval[1] = basicStats.mean() + margin;  // Upper bound
        
        return interval;
    }
}