package hu.u_szeged.inf.fog.simulator.util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import hu.u_szeged.inf.fog.simulator.iot.Device;
import hu.u_szeged.inf.fog.simulator.physical.ComputingAppliance;

/** This helper class is for visualizing the simulation in point of view energy consumption.
 *  More information: https://developers.google.com/chart/interactive/docs/gallery/columnchart
 */
public abstract class EnergyChartGenerator {
	
	/**
	 * It creates a HTML file in the root of the software.
	 */
	public static void generateForDevices(String path) throws FileNotFoundException, UnsupportedEncodingException {
		ArrayList<Double> consumptions = new ArrayList<Double>();
		//ArrayList<Double> CopyOfConsumptions = consumptions;
		
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        PrintWriter writer;
        if(path==null) {
        	writer = new PrintWriter(sdf.format(cal.getTime())+"-Devices-Energy"+".html", "UTF-8");
        }else {
        	writer = new PrintWriter(path+"/"+sdf.format(cal.getTime())+"-Devices-Energy"+".html", "UTF-8");
        }
		
		writer.println("<!DOCTYPE html><html><head>");
		writer.println("<script type=\'text/javascript\' src=\'https://www.gstatic.com/charts/loader.js\'></script>");
		writer.println("<script type=\'text/javascript\'>");
		writer.println("google.charts.load('current', {packages: ['corechart']});");
		writer.println("google.charts.setOnLoadCallback(drawChart);");

		writer.println("function drawChart() {");
		writer.println("var data = google.visualization.arrayToDataTable([");
		
		writer.println("['Consumption', 'Count', { role: 'style' }],");
				
		for (Device d : Device.allDevices) {
			consumptions.add(d.energyConsumption);
		}
		
		Collections.sort(consumptions);

		int length = removeMultipleElements(consumptions);
		
		for (int i=0; i<length; i++) {
			writer.println("["+String.valueOf(consumptions.get(i))+","+ Collections.frequency(consumptions, consumptions.get(i)) + ", '#f2a03d'],");
		}
		
		writer.println("]);");
		
		writer.println("var options = {title: 'Energy Consumption of IoT Devices', legend: { position: 'none' }};");
		
		

		writer.println("var chart = new google.visualization.ColumnChart(document.getElementById('container'));");
		writer.println("chart.draw(data, options);");
		writer.println("}");
		writer.println("</script>");
		writer.println("</head><body>");
		writer.println("<div id=\"container\" style=\"height: 800px; width=100%;\"></div>");
		writer.println("</body></html>");
		writer.close();
	}
	
	private static int removeMultipleElements(ArrayList<Double> al) {
		if (al.size()==0 || al.size()==1) {
			return al.size();
		}
		
		int j = 0;
		
		for (int i=0;i<al.size()-1;i++) {
			if (al.get(i) != al.get(i+1)) {
				al.set(j++, al.get(i));
			}
		}
		
		al.set(j++, al.get(al.size()-1));
		
		return j;
	}
	
	/**
	 * It creates a HTML file in the root of the software.
	 */
	public static void generateForNodes(String path) throws FileNotFoundException, UnsupportedEncodingException {
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        PrintWriter writer;
        if(path==null) {
        	writer = new PrintWriter(sdf.format(cal.getTime())+"-Nodes-Energy"+".html", "UTF-8");
        }else {
        	writer = new PrintWriter(path+"/"+sdf.format(cal.getTime())+"-Nodes-Energy"+".html", "UTF-8");
        }
		
		writer.println("<!DOCTYPE html><html><head>");
		writer.println("<script type=\'text/javascript\' src=\'https://www.gstatic.com/charts/loader.js\'></script>");
		writer.println("<script type=\'text/javascript\'>");
		writer.println("google.charts.load('current', {packages: ['corechart']});");
		writer.println("google.charts.setOnLoadCallback(drawChart);");
		writer.println("function drawChart() {");
		writer.println("var data = google.visualization.arrayToDataTable([");
		writer.println("['', 'Node', { role: 'style' }],");
		
		for (ComputingAppliance c : ComputingAppliance.allComputingAppliance) {
			writer.println("['" + c.name + "', " + c.energyConsumption + ", '#f2a03d'],");
		}
		
		writer.println("]);");
		writer.println("var options = {title: 'Energy Consumption of Computing Nodes', legend: { position: 'none' }};");
		writer.println("var chart = new google.visualization.ColumnChart(document.getElementById('container'));");
		writer.println("chart.draw(data, options);");
		writer.println("}");
		writer.println("</script>");
		writer.println("</head><body>");
		writer.println("<div id=\"container\" style=\"height: 800px; width=100%;\"></div>");
		writer.println(" </body></html>");
		writer.close();
	}

}
