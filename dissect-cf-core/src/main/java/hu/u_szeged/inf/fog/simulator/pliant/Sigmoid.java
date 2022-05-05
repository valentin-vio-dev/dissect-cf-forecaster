package hu.u_szeged.inf.fog.simulator.pliant;

import java.util.Vector;

/**
 * TODO: refactor
 */
public class Sigmoid<E> implements INormalizer{
	
	public Sigmoid()
	{
		shift = 0.0;
		lambda = 1;
	}
	
	public Sigmoid(Double lambda, Double shift)
	{
		this.shift = shift;
		this.lambda = lambda;
	}
	

	public Vector<Double> normalizeincremental(Vector<?> source_vector) {		
		
		Vector<Double> result = new Vector<Double>(source_vector.size());
		
		for(int i=0;i<source_vector.size();i++)
		{
			Double value = null;
			if(source_vector.get(i) instanceof Double)
				value = (Double)source_vector.get(i);
			if(source_vector.get(i) instanceof Long)
				value = (Double)source_vector.get(i);			
			result.add(getat(value));
		}
		return result;
	}

	public Vector<Double> normalizedecremental(Vector<?> source_vector) {
		Vector<Double> result = new Vector<Double>(source_vector.size());
		
		for(int i=0;i<source_vector.size();i++)
		{
			Double value = null;
			if(source_vector.get(i) instanceof Double)
				value = (Double)source_vector.get(i);
			if(source_vector.get(i) instanceof Long)
				value = (Double)source_vector.get(i);			
			result.add(getat((-1)*value));
		}
		return result;
	}	
	
	public Double getat(Double x)
	{
		return 1 / (1 + Math.pow(Math.E, (-1) * lambda * (x - shift)));		
	}	
	
	double lambda;
	double shift;
}
