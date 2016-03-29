package beans;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;


public class Histogram extends JFrame {

	private static final long serialVersionUID = -6963827701162177893L;

	public Histogram(double[] POS, double[] NEG, double min, double max, int bin) {
		JFreeChart chart = createChart(POS, NEG, min, max, 100);
		ChartPanel cpanel = new ChartPanel(chart);
		getContentPane().add(cpanel, BorderLayout.CENTER);
	}
	private JFreeChart createChart(double[] POS, double[] NEG, double min, double max, int bin){

		HistogramDataset dataset = new HistogramDataset();
		dataset.addSeries("NEG", NEG, bin, min-1, max+1);
		dataset.addSeries("POS", POS, bin, min-1, max+1);

		JFreeChart chart = ChartFactory.createHistogram("Histogram",null,null,
														dataset,PlotOrientation.VERTICAL,
														true,false,false);	

		chart.setBackgroundPaint(new Color(250,250,250));
		XYPlot xyplot = (XYPlot)chart.getPlot();
		xyplot.setForegroundAlpha(0.6F);
		xyplot.setBackgroundPaint(Color.WHITE);
		xyplot.setDomainGridlinePaint(new Color(150,150,150));
		xyplot.setRangeGridlinePaint(new Color(150,150,150));
		XYBarRenderer xybarrenderer = (XYBarRenderer)xyplot.getRenderer();
		xybarrenderer.setShadowVisible(false);
		xybarrenderer.setBarPainter(new StandardXYBarPainter()); 
		return chart;
	}
}

