package hu.u_szeged.inf.fog.simulator.pliant;

import java.util.Collections;
import java.util.Vector;

/**
 * TODO: refactor
 */
public class Linear implements INormalizer {
	@Override
	public Vector<Double> normalizeincremental(Vector  source_vector) {
		
		Vector<Double> result = new Vector<Double>(source_vector.size());
		
		if(source_vector.get(0) instanceof Double)
		{
			double max = (Double) Collections.max(source_vector) + 1.0;
			double min = (Double) Collections.min(source_vector) - 1.0;
			double dist =(double)max-min;
			for(int i=0;i<source_vector.size();i++)
			{			
				result.add(((Double) source_vector.get(i) - min) / (dist));				
			}
		}		
		if(source_vector.get(0) instanceof Long)
		{
			long max = (Long) Collections.max(source_vector) + 1;
			long min = (Long) Collections.min(source_vector) - 1;
			double dist = (double) max-min;		
			for(int i=0;i<source_vector.size();i++)
			{			
				result.add((double)((double)((Long) source_vector.get(i) - min)/dist));		
			}
		}
		
		return result;
	}

	public Vector<Double> normalizedecremental(Vector source_vector) {
		
		Vector<Double> result = new Vector<Double>(source_vector.size());
		
		if(source_vector.get(0) instanceof Double)
		{
			double max = (Double) Collections.max(source_vector) + 1.0;
			double min = (Double) Collections.min(source_vector) - 1.0;
			double dist =(double)min-max;
			for(int i=0;i<source_vector.size();i++)
			{			
				result.add(((Double) source_vector.get(i) - max) / (dist));				
			}
		}		
		if(source_vector.get(0) instanceof Long)
		{
			long max = (Long) Collections.max(source_vector) + 1;
			long min = (Long) Collections.min(source_vector) - 1;
			double dist =(double)min-max;		
			for(int i=0;i<source_vector.size();i++)
			{			
				result.add((double)((double)((Long) source_vector.get(i) - max)/dist));		
			}
		}		
		return result;
	}

	
	
}
