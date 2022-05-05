package hu.u_szeged.inf.fog.simulator.pliant;

/**
 * TODO: refactor
 */
public class Kappa {

	public Kappa()
	{
		lambda = 1.0;
		nu = 0.5;
	}
	
	public Kappa(double lambda, double nu)
	{
		this.lambda = lambda;
		this.nu = nu;
	}
	
	public Double getAt(Double x)
	{		
		return 1.0 / (1.0 + Math.pow(( (nu/(1.0-nu)) * ((1.0-x) / x)), lambda) );
	}
	
	private double lambda;
	private double nu;
	
}
