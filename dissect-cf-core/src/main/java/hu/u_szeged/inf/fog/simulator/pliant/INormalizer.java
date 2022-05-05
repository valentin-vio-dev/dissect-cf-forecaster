package hu.u_szeged.inf.fog.simulator.pliant;

import java.util.Vector;

/**
 * TODO: refactor
 */
public interface INormalizer {

	//if the higher value is better
	public Vector<?> normalizeincremental(Vector<?> source_vector);
		
	//if the lower value is better
	public Vector<?> normalizedecremental(Vector<?> source_vector);
}
