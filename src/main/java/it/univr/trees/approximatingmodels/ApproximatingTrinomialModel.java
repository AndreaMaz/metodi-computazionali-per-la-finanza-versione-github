package it.univr.trees.approximatingmodels;

import java.util.function.DoubleUnaryOperator;

import it.univr.trees.simpletrinomialmodel.TrinomialModel;


/**
 * This class implements a discrete trinomial model which approximates the continuous Black-Scholes model
 * for "small" length of the time discretization 0=t_0<t_1<..<t_n=T.
 * 
 * @author Andrea Mazzon
 *
 */
public abstract class ApproximatingTrinomialModel {

	//parameters describing the model
	private double initialPrice;
	private double riskFreeRate;
	private double volatility;

	//parameters of the time discretization
	private double timeStep;
	private double lastTime;
	private int numberOfTimes;

	/*
	 * This is of fundamental importance in our implementation: we give it the value of the possible up 
	 * movements and of the probability to move up according to the specific model we consider (that is,
	 * these are computed in derived classes of this abstract one), and then we delegate to it the
	 * implementation of the methods where we want to get values, the probabilities of the states etc..
	 */
	private TrinomialModel ourTrinomialModel;
	/**
	 * It constructs an object of type ApproximatingTrinomialModel.
	 * 
	 * @param initialPrice, the initial price of the asset modeled by the process
	 * @param riskFreeRate, the number r such that the value of a risk-free bond at time T is e^(rT)
	 * @param volatility, the log-volatility of the Black-Scholes model
	 * @param lastTime, the last time T in the time discretization 0=t_0<t_1<..<t_n=T
	 * @param timeStep, the length t_k-t_{k-1} of the equally spaced time steps that we take for the approximating
	 * time discretization 0=t_0<t_1<..<t_n=T
	 */
	public ApproximatingTrinomialModel(double initialPrice, double riskFreeRate, double volatility, 
			double lastTime, double timeStep) {
		this.initialPrice = initialPrice;
		this.riskFreeRate = riskFreeRate;
		this.volatility = volatility;
		this.lastTime = lastTime;
		this.timeStep = timeStep;
		numberOfTimes = (int) (Math.round(lastTime/timeStep) + 1);//the number of times comes from the number of times steps
	}


	/**
	 * It constructs an object of type ApproximatingTrinomialModel.
	 * 
	 * @param initialPrice, the initial price of the asset modeled by the process
	 * @param riskFreeRate, the number r such that the value of a risk-free bond at time T is e^(rT)
	 * @param volatility, the log-volatility of the Black-Scholes model
	 * @param lastTime, the last time T in the time discretization 0=t_0<t_1<..<t_n=T
	 * @param numberOfTimes, the number of times in the equally spaced time steps that we take for the approximating
	 * time discretization 0=t_0<t_1<..<t_n=T
	 */
	public ApproximatingTrinomialModel(double initialPrice, double riskFreeRate, double volatility,
			double lastTime, int numberOfTimes) {
		this.initialPrice = initialPrice;
		this.riskFreeRate = riskFreeRate;
		this.volatility = volatility;
		this.lastTime = lastTime;
		this.numberOfTimes = numberOfTimes;
		timeStep = lastTime/(numberOfTimes-1);//the times step comes from the number of times

	}


	/*
	 * This is an abstract method which gets implemented in the derived classes and sets the up factor and the
	 * up probability: they characterize the specific model. The difference with respect to the Binomial models
	 * is indeed that here we have freedom choosing the probability to go up, and that on the other hand, in
	 * order for the tree to be recombing, we want d=1/u.
	 */
	protected abstract double[] getUpFactorAndProbabilityUpOfTrinomialModel();

	/*
	 * In this method we generate the TrinomialModel. Note that, once we know the up factor and up probability,
	 * the implementation is the same for any approximation method.
	 */
	private void generateTrinomialModel() {
		//the first entry of the vector returned by the method
		double probabilityUp = getUpFactorAndProbabilityUpOfTrinomialModel()[0];
		
		//the second entry of the vector returned by the method
		double upFactor = getUpFactorAndProbabilityUpOfTrinomialModel()[1];

		double riskFreeFactorForTrinomialModel = Math.exp(riskFreeRate * timeStep) - 1;

		ourTrinomialModel = new TrinomialModel(probabilityUp, upFactor,  riskFreeFactorForTrinomialModel, initialPrice, numberOfTimes);
	}


	//all next methods are pure delegation to the TrinomialModel object
	/**
	 * It returns all the possible values of the approximating Trinomial model at the given time index.
	 * The entries of the array are ordered from the biggest one to the smallest one.
	 * @param timeIndex, the given time index
	 * @return an array of doubles representing all the possible values of the Trinomial model at timeIndex.
	 * 		   The entries of the array are ordered from the biggest one to the smallest one.
	 */
	public double[] getValuesAtGivenTimeIndex(int timeIndex) {
		//we want to generate ourTrinomialModel only once! So we check if it is null: if yes, we have to generate it
		if (ourTrinomialModel==null) {
			generateTrinomialModel();
		}
		//pure delegation
		return ourTrinomialModel.getValuesAtGivenTimeIndex(timeIndex);
	}

	/**
	 * It returns an array whose elements are a function of all the possible values of the approximating
	 * Trinomial model at the given time index. The entries of the array are placed in decreasing order
	 * of the underlying approximating tree model (not of its transformed value).
	 * @param DoubleUnaryOperator transformFunction, the function
	 * @return an array of doubles representing a function all the possible values of the Trinomial model
	 * 		   at timeIndex. The entries of the array are placed in decreasing order of the underlying
	 * 		   approximating tree model (not of its transformed value).
	 */
	public double[] getTransformedValuesAtGivenTimeIndex(int timeIndex, DoubleUnaryOperator transformFunction) {
		//we want to generate ourTrinomialModel only once! So we check if it is null: if yes, we have to generate it
		if (ourTrinomialModel==null) {
			generateTrinomialModel();
		}
		//pure delegation
		return ourTrinomialModel.getTransformedValuesAtGivenTimeIndex(timeIndex, transformFunction);
	}



	/**
	 * It returns all the possible values of the approximating Trinomial model at the given time.
	 * The entries of the array are placed in decreasing order.
	 * @param time, the given time as double
	 * @return an array of doubles representing all the possible values of the Trinomial model at given time.
	 * 		   The entries of the array are ordered from the biggest one to the smallest one.
	 */
	public double[] getValuesAtGivenTime(double time) {
		int timeIndex = (int) Math.round(time/timeStep);
		//we want to generate ourTrinomialModel only once! So we check if it is null: if yes, we have to generate it
		if (ourTrinomialModel==null) {
			generateTrinomialModel();
		}
		//pure delegation
		return ourTrinomialModel.getValuesAtGivenTimeIndex(timeIndex);
	}

	/**
	 * It returns an array whose elements are a function of all the possible values of the Trinomial model at
	 * the given time. The entries of the array are placed in decreasing order of the underlying approximating
	 * tree model (not of its transformed value).
	 * @param time, the given time
	 * @param DoubleUnaryOperator transformFunction, the function
	 * @return an array of doubles representing a function all the possible values of the Trinomial model at timeIndex.
			  The entries of the array are placed in decreasing order of the underlying approximating tree model
			  (not of its transformed value).	
	 */
	public double[] getTransformedValuesAtGivenTime(double time, DoubleUnaryOperator transformFunction) {
		int timeIndex = (int) Math.round(time/timeStep);
		//we want to generate ourTrinomialModel only once! So we check if it is null: if yes, we have to generate it
		if (ourTrinomialModel==null) {
			generateTrinomialModel();
		}
		//pure delegation
		return ourTrinomialModel.getTransformedValuesAtGivenTimeIndex(timeIndex, transformFunction);
	}
	
	
	/**
	 * It returns an array representing the discounted conditional expectations at given timeIndex of the
	 * values of (possibly a function of) an approximating model at time timeIndex+1. 
	 * 
	 * @param optionValues, values of (possibly a function of) an approximating Trinomial model at time timeIndex+1
	 * @param timeIndex, the time index
	 * @return the array of the discounted conditional expectations at timeIndex of optionValues. 
	 */
	public double[] getConditionalExpectation(double[] optionValues,int timeIndex) {
		//we want to generate ourTrinomialModel only once! So we check if it is null: if yes, we have to generate it
		if (ourTrinomialModel==null) {
			generateTrinomialModel();
		}
		return ourTrinomialModel.getConditionalExpectation(optionValues, timeIndex);
	}


	/*
	 * Getters for the parameters of the Trinomial model. Some of them are used in the derived classes:
	 * in this way, we can set them private here (we prefer, because in this way they cannot be modified,
	 * note that there are no setters indeed)
	 */
	/**
	 * It returns the initial price of the approximated Black-Scholes model
	 * @return the initial price of the approximated Black-Scholes model
	 */
	public double getInitialPrice() {
		return initialPrice;
	}

	/**
	 * It returns the risk free rate of the approximated Black-Scholes model
	 * @return the risk free rate of the approximated Black-Scholes model
	 */
	public double getRiskFreeRate() {
		return riskFreeRate;
	}

	/**
	 * It returns the volatility of the approximated Black-Scholes model
	 * @return the volatility of the approximated Black-Scholes model
	 */
	public double getVolatility() {
		return volatility;
	}

	/**
	 * It returns the time step of the time discretization with which we approximate Black-Scholes model
	 * @return the time step of the time discretization with which we approximate Black-Scholes model
	 */
	public double getTimeStep() {
		return timeStep;
	}
	/**
	 * It returns the last time of the time discretization with which we approximate Black-Scholes model
	 * @return the last time of the time discretization with which we approximate Black-Scholes model
	 */
	public double getLastTime() {
		return lastTime;
	}

	/**
	 * It returns the number of times of the time discretization with which we approximate Black-Scholes model
	 * @return the numbr of times of the time discretization with which we approximate Black-Scholes model
	 */
	public int getNumberOfTimes() {
		return numberOfTimes;
	}	


}
