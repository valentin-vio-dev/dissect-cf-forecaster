package hu.u_szeged.inf.fog.simulator.util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import hu.u_szeged.inf.fog.simulator.application.Application;
import hu.u_szeged.inf.fog.simulator.physical.ComputingAppliance;

/** This class is for visualizing the simulation (computing node, virtual machines and tasks).
 *  More information: https://developers.google.com/chart/interactive/docs/gallery/timeline
 */
public abstract class TimelineGenerator {

	/**
	 * It creates a HTML file in the root of the software.
	 */
	public static void generate(String path) throws FileNotFoundException, UnsupportedEncodingException {
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        PrintWriter writer;
        if(path==null) {
        	writer = new PrintWriter(sdf.format(cal.getTime())+"-Timeline"+".html", "UTF-8");
        }else {
        	writer = new PrintWriter(path+"/"+sdf.format(cal.getTime())+"-Timeline"+".html", "UTF-8");       	
        }

		writer.println("<!DOCTYPE html><html><head>");
		writer.println("<script type=\'text/javascript\' src=\'https://www.gstatic.com/charts/loader.js\'></script>");
		writer.println("<script type=\'text/javascript\'>");
		writer.println("google.charts.load(\'current\', {packages:[\'timeline\']});");
		writer.println("google.charts.setOnLoadCallback(drawChart);");
		writer.println("function drawChart(){");
		writer.println("var container = document.getElementById('example');");
		writer.println("var chart = new google.visualization.Timeline(container);");
		writer.println("var dataTable = new google.visualization.DataTable();");
		writer.println("dataTable.addColumn({ type: 'string', id: 'Application' });");
		writer.println("dataTable.addColumn({ type: 'string', id: 'VM' });");
		writer.println("dataTable.addColumn({ type: 'date', id: 'Start' });");
		writer.println("dataTable.addColumn({ type: 'date', id: 'End' });");
		writer.println("dataTable.addRows([");
		
		for (ComputingAppliance c : ComputingAppliance.allComputingAppliance) {
			for (Application a : c.applicationList) {
				for(TimelineCollector tc : a.timelineList) {
					writer.println("[ '"+a.name+"', '"+tc.vmId+"', new Date(0,0,0,0,0,0,"+tc.start +"), new Date(0,0,0,0,0,0,"+tc.stop+")],");
				}
			}
		}

		writer.println("]);");
		writer.println("chart.draw(dataTable);");
		writer.println("}</script>");
		writer.println("</head><body>");
		writer.println("<div id=\"example\" style=\"height: 1500px; width=100%;\"></div>");
		writer.println("</body></html>");
		writer.close();
	}
	
	/**
	 * Each bar in the time line is represented by an object of this class.
	 */
	public static class TimelineCollector{
		
		public TimelineCollector(long start, long stop, String vmId) {
			super();
			this.start = start;
			this.stop = stop;
			this.vmId = vmId;
		}
		
		public long start;
		public long stop;
		public String vmId;
	}
}
