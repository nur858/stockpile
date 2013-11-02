package chart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;


public class RocCurve extends ApplicationFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String title;
	public RocCurve(String title) {
	      super(title);
	      this.title = title;
	}
	public void display(XYSeries points){
        final XYSeries series = points;
        final XYSeriesCollection data = new XYSeriesCollection(series);
        final JFreeChart chart = ChartFactory.createXYLineChart(
            this.title,
            "False Positive", 
            "True Positive", 
            data,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
        pack();
        RefineryUtilities.centerFrameOnScreen(this);
        setVisible(true);        
	}
}
