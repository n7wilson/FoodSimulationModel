package twoagentmodel.relogo;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;

public class ClimateODE implements FirstOrderDifferentialEquations{
	private double sigma;
	private double rho;
	private double beta;
	
	public ClimateODE(double sigma, double rho, double beta){
		this.sigma = sigma;
		this.rho = rho;
		this.beta = beta;
	}
	
	public ClimateODE(){
		sigma = 10;
		rho = 28;
		beta = 8/3;
	}
	
	public int getDimension(){
		return 3;
	}
	
	public void computeDerivatives(double t, double[] xyz, double[] xyzDot){
		xyzDot[0] = sigma * (xyz[1] - xyz[0]);
		xyzDot[1] = xyz[0] * (rho - xyz[2]) - xyz[1];
		xyzDot[2] = (xyz[0] * xyz[1]) - (beta * xyz[2]);
	}
}
