package hu.u_szeged.inf.fog.simulator.demo;

import java.util.HashMap;
import java.util.Map;
import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.u_szeged.inf.fog.simulator.application.Application;
import hu.u_szeged.inf.fog.simulator.iot.Station;
import hu.u_szeged.inf.fog.simulator.physical.ComputingAppliance;
import hu.u_szeged.inf.fog.simulator.providers.Instance;
import hu.u_szeged.inf.fog.simulator.providers.Provider;
import hu.u_szeged.inf.fog.simulator.util.EnergyChartGenerator;
import hu.u_szeged.inf.fog.simulator.util.TimelineGenerator;

public class XMLSimulation {
	
	public static void main(String[] args) throws Exception {

		String cloudfile = ScenarioBase.resourcePath + "LPDS_original.xml";
		String fogfile1 = ScenarioBase.resourcePath + "XML_examples/LPDS_32.xml"; 
		String fogfile2 = ScenarioBase.resourcePath + "XML_examples/LPDS_16.xml"; 
		
		String instancefile, appliancefile, devicefile, providerfile;
		
		if(args.length>0) {
			instancefile = args[0];
			appliancefile = args[1];
			devicefile = args[2];
			providerfile = ScenarioBase.resourcePath + "XML_examples/providers.xml";
			
		}else {
			instancefile = ScenarioBase.resourcePath + "XML_examples/instances.xml";
			appliancefile = ScenarioBase.resourcePath + "XML_examples/applications.xml";
			devicefile = ScenarioBase.resourcePath + "XML_examples/devices.xml";
			providerfile = ScenarioBase.resourcePath + "XML_examples/providers.xml";
		}

		Map<String, String> iaasloaders = new HashMap<String, String>();
		iaasloaders.put("LPDS_original", cloudfile);
		iaasloaders.put("LPDS_32", fogfile1);
		iaasloaders.put("LPDS_16", fogfile2);
		
		Instance.loadInstancesXML(instancefile);
		ComputingAppliance.loadAppliancesXML(appliancefile, iaasloaders);
		Station.loadDevicesXML(devicefile);
		Provider.loadProvidersXML(providerfile); 
				
		// Start the simulation
		long starttime = System.nanoTime();
		Timed.simulateUntilLastEvent();
		long stopttime = System.nanoTime();
		
		// args[2]
		if(args.length>0) {
			TimelineGenerator.generate(args[3]);
		    EnergyChartGenerator.generateForDevices(args[3]);
		    EnergyChartGenerator.generateForNodes(args[3]);
		}else {
			TimelineGenerator.generate(null);
		    EnergyChartGenerator.generateForDevices(null);
		    EnergyChartGenerator.generateForNodes(null);
		}
		
	    ScenarioBase.printInformation(stopttime - starttime);
	    
	}
}