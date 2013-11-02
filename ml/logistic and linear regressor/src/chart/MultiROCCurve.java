package chart;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class MultiROCCurve  extends ApplicationFrame{
	private static final long serialVersionUID = 1L;	
    /** The plot. */
    private XYPlot plot;   
    /** The index of the last dataset added. */
    private int datasetIndex = 0;    
	public MultiROCCurve(String title) {
		super(title);
        final XYSeriesCollection data = null;
        final JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                "False Positive", 
                "True Positive", 
                data,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
            );
        chart.setBackgroundPaint(Color.white);
        
        this.plot = chart.getXYPlot();
        this.plot.setBackgroundPaint(Color.lightGray);
        this.plot.setDomainGridlinePaint(Color.white);
        this.plot.setRangeGridlinePaint(Color.white);
        final ValueAxis axis = this.plot.getDomainAxis();
        axis.setAutoRange(true);

        final NumberAxis rangeAxis2 = new NumberAxis("Range Axis 2");
        rangeAxis2.setAutoRangeIncludesZero(false);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
        pack();
        RefineryUtilities.centerFrameOnScreen(this);
        setVisible(true);        
	}
	public void addCurve(String level, XYSeries series){		
        this.datasetIndex++;
        this.plot.setDataset(this.datasetIndex,new XYSeriesCollection(series));
        this.plot.setRenderer(this.datasetIndex, new StandardXYItemRenderer());		
	}
}
