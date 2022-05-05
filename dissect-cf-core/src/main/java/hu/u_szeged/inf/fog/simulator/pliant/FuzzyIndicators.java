package hu.u_szeged.inf.fog.simulator.pliant;

import java.util.Vector;

/**
 * TODO: refactor
 */
public class FuzzyIndicators {

	private static double minmaxcorrigate = 0.00000005;
	
	public static Double getConjunction(Vector<Double> values)
	{
		Double result = 0.0;		
		for(int i=0;i<values.size();i++)
		{
			if(values.get(i) != 0.0 || values.get(i) != 1.0)
				result += ((1.0 - values.get(i)) / values.get(i));
			else
				result+=minmaxcorrigate;
		}		
		return (1.0 / (1.0 + result));
	}

	
	public static Double getAggregation(Vector<Double> values)
	{
		Double result = 1.0;		
		for(int i=0;i<values.size();i++)
		{
			if(values.get(i) != 0.0 || values.get(i) != 1.0)
				result *= ((1 - values.get(i)) / values.get(i));
			else
				result+=minmaxcorrigate;
		}		
		return (1 / (1 + result));
	}
}
