# 📊 JStatsLib

**JStatsLib** is a Java library for performing statistical analysis and generating data visualizations. It allows you to import 1D data from a file, analyze it using methods like mean, median, mode, standard deviation, and more, and visualize it with graphs like box plots, histograms, pie charts, and others.

---

## ✨ Features

- 📂 Import data from `.dat`, `.txt`, and other delimited text files
- 📈 Calculate statistical metrics: mean, median, mode, range, variance, standard deviation, IQR, percentiles
- 🧠 Advanced analysis: normal distribution PDF/CDF, inverse normal, z-intervals, moving averages
- 📊 Graph support: Box Plot, Histogram, Pie Chart, Cumulative Frequency Graph, Dot Plot, Violin Plot
- 🌈 Custom themes for graphs

---

## 📦 Installation
Download the `.jar` file and add to your Java project directory or build system (e.g., Maven, Gradle).

---

## 🚀 Quick Start

```java
import com.statistics.JStatsLib;
import com.statistics.visualization.*;
import com.statistics.utils.Themes;

public class Main {
    public static void main(String[] args) {
        // Load data from a file (delimiter can be \n, ,, \t, etc.)
        JStatsLib stats = new JStatsLib("src/egg/data.dat", "\n");

        // Basic statistics
        System.out.println("Mean: " + stats.mean());
        System.out.println("Median: " + stats.median());
        System.out.println("Mode: " + stats.mode());
        System.out.println("Standard Deviation: " + stats.stddev());
        System.out.println("IQR: " + stats.iqr());

        // Normal distribution
        System.out.println("Normal PDF at x=2.0: " + stats.normPDF(2.0));
        System.out.println("Normal CDF at x=2.0: " + stats.normCDF(2.0));
        System.out.println("Inverse Normal (90%): " + stats.invNorm(0.9, stats.mean(), stats.stddev()));

        // Confidence interval
        double[] ci = stats.zInterval(0.9, stats.mean(), stats.stddev());
        System.out.println("Confidence Interval: [" + ci[0] + ", " + ci[1] + "]");

        // Moving averages
        System.out.println("Moving Average (window=2): " + stats.movingAverage(2));
        System.out.println("Exponential Moving Average (alpha=0.5): " + stats.exponentialMovingAverage(0.5));
        System.out.println("Centered Moving Average (window=2): " + stats.centeredMovingAverage(2));

        // Visualizations
        BoxPlot bp = new BoxPlot(stats, "Box Plot", Themes.YELLOWORANGE.theme);
        Histogram hg = new Histogram(stats, 10, "Histogram", Themes.OCEANBLUES.theme);
        PieChart pc = new PieChart(stats, "Pie Chart", Themes.GRAYSCALE.theme);
        CumulativeFrequencyGraph cf = new CumulativeFrequencyGraph(stats, "Cumulative Frequency", Themes.LSD.theme);
        DotPlot dp = new DotPlot(stats, "Dot Plot", Themes.CAMO.theme);
        ViolinPlot vp = new ViolinPlot(stats, "Violin Plot", Themes.NAVY.theme);

        bp.showGraph();
        hg.showGraph();
        pc.showGraph();
        cf.showGraph();
        dp.showGraph();
        vp.showGraph();
    }
}

---

```
## 📂 File Format
Input files should contain one data value per line or separated by a custom delimiter.

Example (\n delimited)
```txt
12.5
15.0
13.3
16.8
14.2
```

Example (comma delimited)
```txt
12.5,15.0,13.3,16.8,14.2
```

---

# 📚 API Highlights

| TOOLS/METHODS               | USAGE                                     | REASON                                                                 |
|----------------------------|-------------------------------------------|------------------------------------------------------------------------|
| `mean()`                   | Used to calculate the average             | To find the central tendency of the dataset                            |
| `median()`                 | Used to find the middle value             | Helps reduce the effect of outliers compared to mean                   |
| `mode()`                   | Used to find the most frequent value      | Useful for identifying common values in categorical/numeric datasets  |
| `stddev()`                 | Used to compute standard deviation        | Measures the spread/variability of the dataset                         |
| `variance()`               | Used to calculate variance                | Quantifies how much the data varies from the mean                      |
| `range()`                  | Finds the difference between max and min  | Provides a basic idea of spread                                        |
| `iqr()`                    | Computes interquartile range              | Helps detect outliers and understand data spread                       |
| `normPDF(x)`               | Computes normal distribution PDF at x     | Models real-world data assuming a bell-shaped curve                   |
| `normCDF(x)`               | Computes cumulative probability at x      | Finds probability up to a value in a normal distribution               |
| `invNorm(p, μ, σ)`         | Finds value at a given probability         | Useful in hypothesis testing and z-score to value conversion           |
| `zInterval(c, μ, σ)`       | Calculates confidence interval            | Useful for estimating population parameters from a sample              |
| `movingAverage(n)`         | Computes moving average with window `n`   | Smooths out short-term fluctuations in time series                     |
| `exponentialMovingAverage(α)` | Computes weighted average over time     | Prioritizes recent data for trends                                     |
| `centeredMovingAverage(n)`| Centers the moving average around middle  | Helps eliminate lag in moving average visualization                   |
| `toList()`                 | Returns all data as an ArrayList          | Useful for accessing raw data programmatically                         |
| `toFrequencyMap()`         | Returns map of values to frequencies      | Helps with mode calculation and visualizing frequency distributions    |

---

## 🎨 Themes
JStatsLib supports a variety of pre-defined color themes for graphs. Each theme is a set of 6 java.awt.Color values representing box, median, outlier, line, text, and background colors.

Available built-in themes:
Themes.LSD
Themes.LIGHT
Themes.DARK
Themes.GRAYSCALE
Themes.CAMO
Themes.RED
Themes.BLACKGREEN
Themes.REDWHITE
Themes.REDGREENBLUE
Themes.NAVY
Themes.CHRISTMAS
Themes.HALLOWEEN
Themes.PITCHBLACK
Themes.OCEANBLUES
Themes.YELLOWORANGE
Themes.PURPLE
Themes.PUKE
Themes.VERYBRIGHT
Themes.FLASHBANG
Themes.AMERICA

You can also define your own theme:

```java
Color[] customTheme = new Color[] {
    new Color(255, 200, 200), // Box
    new Color(100, 100, 255), // Median
    new Color(0, 255, 100),   // Outlier
    new Color(0, 0, 0),       // Line
    new Color(50, 50, 50),    // Text
    new Color(255, 255, 255)  // Background
};
BoxPlot bp = new BoxPlot(stats, "Custom Theme", customTheme);
```

---

## 📝 License
MIT License. See LICENSE for details.
