import com.statistics.JStatsLib;
import com.statistics.visualization.*;
import com.statistics.utils.Themes;

public class Main {
    public static void main(String[] args) {
        JStatsLib stats = new JStatsLib("test_data.dat", "\n");
         
        double mean = stats.mean();
        double stddev = stats.stddev();
        
        BoxPlot bp = new BoxPlot(stats, "Cool Awesome Box Plot", Themes.LIGHT.theme);
        Histogram hg = new Histogram(stats, 10, "Cool Awesome Histogram", Themes.LIGHT.theme);
        PieChart pc = new PieChart(stats, "Cool Awesome Pie Chart", Themes.LIGHT.theme);
        CumulativeFrequencyGraph cf = new CumulativeFrequencyGraph(stats, "Cool Awesome Cumulative Frequency Graph", Themes.LIGHT.theme);
        DotPlot dp = new DotPlot(stats, "Cool Awesome Dot Plot", Themes.LIGHT.theme);
        ViolinPlot vp = new ViolinPlot(stats, "Cool Awesome Violin Plot", Themes.LIGHT.theme);
        
        // Basic Statistics
        System.out.println("Mean: " + mean);
        System.out.println("Mode: " + stats.mode());
        System.out.println("Median: " + stats.median());
        System.out.println("Standard Deviation: " + stddev);
        System.out.println("Tree Size: " + stats.size());
        System.out.println("IQR: " + stats.iqr());
        
        // Normal Model
        System.out.println("Normal PDF: " + stats.normPDF(2.0));
        System.out.println("Normal CDF: " + stats.normCDF(2.0));
        System.out.println("Inverse Normal (90%): " + stats.invNorm(0.9, mean, stddev));
        
        // Intervals
        double[] confidenceInterval = stats.zInterval(0.9, mean, stddev);
        System.out.println("Confidence Interval: [" + confidenceInterval[0] + ", " + confidenceInterval[1] + "]");
        
        // Moving Averages
        System.out.println("Moving Average: " + stats.movingAverage(2));
        System.out.println("Exponential Moving Average: " + stats.exponentialMovingAverage(0.5));
        System.out.println("Centered Moving Average: " + stats.centeredMovingAverage(2));
        
        // Show Graphs
        bp.showGraph();
        try {Thread.sleep(500);} catch (InterruptedException e) {}

        hg.showGraph();
        try {Thread.sleep(500);} catch (InterruptedException e) {}
        
        pc.showGraph();
        try {Thread.sleep(500);} catch (InterruptedException e) {}
        
        cf.showGraph(); 
        try {Thread.sleep(500);} catch (InterruptedException e) {}
     
        dp.showGraph();
        try {Thread.sleep(500);} catch (InterruptedException e) {}
     
        vp.showGraph();
    }
}
