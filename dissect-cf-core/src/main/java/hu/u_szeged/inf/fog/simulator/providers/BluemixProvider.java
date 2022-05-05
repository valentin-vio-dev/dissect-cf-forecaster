/*
 *  ========================================================================
 *  DIScrete event baSed Energy Consumption simulaTor 
 *    					             for Clouds and Federations (DISSECT-CF)
 *  ========================================================================
 *  
 *  This file is part of DISSECT-CF.
 *  
 *  DISSECT-CF is free software: you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or (at
 *  your option) any later version.
 *  
 *  DISSECT-CF is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 *  General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with DISSECT-CF.  If not, see <http://www.gnu.org/licenses/>.
 *  
 *  (C) Copyright 2019, Andras Markus (markusa@inf.u-szeged.hu)
 */

package hu.u_szeged.inf.fog.simulator.providers;

import java.util.ArrayList;

import hu.u_szeged.inf.fog.simulator.application.Application;

/**
 * This class represents the Bluemix IoT provider which 
 * follows the “pay as you go” approach. Bluemix only charges 
 * after the MB of data exchanged.
 */
public class BluemixProvider extends Provider{
	
	/**
	 * Helper class for managing Bluemix intervalls with the related costs.
	 */
	public static class Bluemix{
		
		/** 
		 * The second tag of the interval.
		 */
		double mbto;
		
		/**
		 * The lower bound of the interval.
		 */
		double mbfrom;
		
		/**
		 * The upper bound of the interval.
		 */
		double cost;
		
		/**
		 * Constructor for initializing an interval.
		 * @param mbto The lower bound of the interval.
		 * @param mbfrom The upper bound of the interval.
		 * @param cost The cost of the interval.
		 */
		public Bluemix(double mbfrom, double mbto, double cost) {
			this.mbto=mbto;
			this.mbfrom=mbfrom;
			this.cost=cost;
		}
	}
	
	/**
	 * It prints the actual cost of the provider.
	 */
	@Override
	public String toString() {
		return  "[BLUEMIX=" + cost +"]";
	}

	/**
	 * This constructor should be used only when initialization is done from XML file.
	 * @param app The application which is monitored by this provider.
	 */
	public BluemixProvider(Application app) {
		super();
		this.app=app;
	}
	
	/**
	 * It helps to create Bluemix provider without XML file.
	 * @param bmList List of the intervals
	 * @param app The application which is monitored by this provider.
	 */
	public BluemixProvider(ArrayList<Bluemix> bmList, Application app) {
		super(app);
		this.bmList=bmList;
		this.startProvider();
	}
	
	/**
	 * This method calculates the costs based on the frequency of the class.
	 */
	public void tick(long fires) {		
		
		if(this.bmList.size()!=0){
			double tmp= (double) this.app.sumOfProcessedData / 1048576; // 1 MB
			double cost=0.0;
			
 			for(Bluemix bm : this.bmList){
				if (tmp <= bm.mbto && tmp >= bm.mbfrom) {
					cost = bm.cost;
					
				}
			
			}
 			this.cost=tmp*cost;
		}
		if(this.needsToStop) {
			unsubscribe();
		}
	}

	/**
	 * This method starts the work of the provider with the given frequency.
	 */
	@Override
	public void startProvider() {
		subscribe(Integer.MAX_VALUE);
	}
}
