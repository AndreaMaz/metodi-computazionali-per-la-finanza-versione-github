package it.univr.trees.approximatingmodels;

/**
 * This class represents the approximation of a Black-Scholes model via the Boyle model.
 * It extends ApproximatingTrinomialModel. The only method that is implemented here computes the values
 * of the up probability and up movements of the Trinomial model.
 * 
 * @author Andrea Mazzon
 *
 */
public class BoyleModel extends ApproximatingTrinomialModel {
	/**
	 * It constructs an object which represents the approximation of a Black-Scholes model via the Jarrow-Rudd model.
	 * 
	 * @param initialPrice, the initial price of the asset modeled by the process
	 * @param riskFreeRate, the number r such that the value of a risk-free bond at time T is e^(rT)
	 * @param volatility, the log-volatility of the Black-Scholes model
	 * @param lastTime, the last time T in the time discretization 0=t_0<t_1<..<t_n=T
	 * @param timeStep, the length t_k-t_{k-1} of the equally spaced time steps that we take for the approximating
	 * time discretization 0=t_0<t_1<..<t_n=T
	 */
	public BoyleModel(double spotPrice, double riskFreeRate, double volatility, 
			double lastTime, double timeStep) {
		super(spotPrice, riskFreeRate, volatility, lastTime, timeStep);
	}
	
	/**
	 * It constructs an object which represents the approximation of a Black-Scholes model via the Jarrow-Rudd model.
	 * 
	 * @param initialPrice, the initial price of the asset modeled by the process
	 * @param riskFreeRate, the number r such that the value of a risk-free bond at time T is e^(rT)
	 * @param volatility, the log-volatility of the Black-Scholes model
	 * @param lastTime, the last time T in the time discretization 0=t_0<t_1<..<t_n=T
	 * @param numberOfTimes, the number of times in the equally spaced time steps that we take for the approximating
	 * time discretization 0=t_0<t_1<..<t_n=T
	 */
	public BoyleModel(double spotPrice, double riskFreeRate, double volatility, 
			double lastTime, int numberOfTimes) {
		super(spotPrice, riskFreeRate, volatility, lastTime, numberOfTimes);
	}

	@Override
	protected double[] getUpFactorAndProbabilityUpOfTrinomialModel() {
		double volatility = getVolatility();
		double riskFreeRate = getRiskFreeRate();
		double timeStep = getTimeStep();
		double probabilityToGoUp = Math.pow(Math.exp(riskFreeRate * timeStep/2) - Math.exp(-volatility * Math.sqrt(timeStep/2)),2)
				/ Math.pow(Math.exp(volatility * Math.sqrt(timeStep/2)) - Math.exp(-volatility * Math.sqrt(timeStep/2)),2);
		double upFactor = Math.exp(volatility * Math.sqrt(2 * timeStep));
		
		return new double[] {probabilityToGoUp, upFactor};
	}
}
