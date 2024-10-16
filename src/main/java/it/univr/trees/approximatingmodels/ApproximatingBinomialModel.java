package it.univr.trees.approximatingmodels;

import java.util.function.DoubleUnaryOperator;

import it.univr.trees.simplebinomialmodel.BinomialModel;

/**
 * This class implements a discrete binomial model which approximates the continuous Black-Scholes model
 * for "small" length of the time discretization 0=t_0<t_1<..<t_n=T.
 * In particular, this is an abstract class which get extended by three classes that represent
 * three possible approximation schemes: Cox Ross Rubinstein (the most well known), Jarrow Rudd
 * and Leisen Reimer. The only abstract method, which gets implemented in the derived classes, takes
 * care of computing the up and down factors with which we construct an object of type BinomialModel.
 * 
 * @author Andrea Mazzon
 *
 */
public abstract class ApproximatingBinomialModel {

	//parameters describing the model
	private double initialPrice;
	private double riskFreeRate;
	private double volatility;

	//parameters of the time discretization
	private double timeStep;
	private double lastTime;
	private int numberOfTimes;

	/*
	 * This is of fundamental importance in our implementation: we contruct it giving the value of the possible up and down
	 * movements computed in derived classes of this abstract one according to the specific model we consider and then we
	 * delegate to it the implementation of the methods where we want to get values, the probabilities of the states etc..
	 */
	private BinomialModel ourBinomialModel;


	/**
	 * It constructs an object of type ApproximatingBinomialModel.
	 * 
	 * @param initialPrice, the initial price of the asset modeled by the process
	 * @param riskFreeRate, the number r such that the value of a risk-free bond at time T is e^(rT)
	 * @param volatility, the log-volatility of the Black-Scholes model
	 * @param lastTime, the last time T in the time discretization 0=t_0<t_1<..<t_n=T
	 * @param timeStep, the length t_k-t_{k-1} of the equally spaced time steps that we take for the approximating
	 * time discretization 0=t_0<t_1<..<t_n=T
	 */
	public ApproximatingBinomialModel(double initialPrice, double riskFreeRate, double volatility, 
			double lastTime, double timeStep) {
		this.initialPrice = initialPrice;
		this.riskFreeRate = riskFreeRate;
		this.volatility = volatility;
		this.lastTime = lastTime;
		this.timeStep = timeStep;
		numberOfTimes = (int) (Math.round(lastTime/timeStep) + 1);//the number of times comes from the number of times steps
	}


	/**
	 * It constructs an object of type ApproximatingBinomialModel.
	 * 
	 * @param initialPrice, the initial price of the asset modeled by the process
	 * @param riskFreeRate, the number r such that the value of a risk-free bond at time T is e^(rT)
	 * @param volatility, the log-volatility of the Black-Scholes model
	 * @param lastTime, the last time T in the time discretization 0=t_0<t_1<..<t_n=T
	 * @param numberOfTimes, the number of times in the equally spaced time steps that we take for the approximating
	 * time discretization 0=t_0<t_1<..<t_n=T
	 */
	public ApproximatingBinomialModel(double initialPrice, double riskFreeRate, double volatility,
			double lastTime, int numberOfTimes) {
		this.initialPrice = initialPrice;
		this.riskFreeRate = riskFreeRate;
		this.volatility = volatility;
		this.lastTime = lastTime;
		this.numberOfTimes = numberOfTimes;
		timeStep = lastTime/(numberOfTimes-1);//the times step comes from the number of times
	}

	//this is an abstract method which gets implemented in the derived classes : it sets the up and down factors
	protected abstract double[] getUpAndDownFactorsOfBinomialModel();

	/*
	 * In this method we generate the binomialModel. Note that, once we know the up and down factors, the
	 * implementation is the same for any approximation method.
	 */
	private void generateBinomialModel() {
		double[] upAndDownFactors = getUpAndDownFactorsOfBinomialModel();//[u_n, d_n]
		double riskFreeFactorForBinomialModel = Math.exp(riskFreeRate * timeStep) - 1;

		ourBinomialModel = new BinomialModel(upAndDownFactors[0], upAndDownFactors[1],  riskFreeFactorForBinomialModel, initialPrice, numberOfTimes);
	}

	//all next methods are pure delegation to the BinomialModel object
	/**
	 * It returns all the possible values of the approximating binomial model at the given time index. The element in
	 * position i is the one where the underlying has gone down i times.
	 * @param timeIndex, the given time index
	 * @return an array of doubles representing all the possible values of the binomial model at timeIndex.
	 * 		   The value in position i is B(0)*u^(timeIndex-i)*d^i
	 */
	public double[] getValuesAtGivenTimeIndex(int timeIndex) {
		//we want to generate ourBinomialModel only once! So we check if it is null: if yes, we have to generate it
		if (ourBinomialModel==null) {
			generateBinomialModel();
		}
		//pure delegation
		return ourBinomialModel.getValuesAtGivenTimeIndex(timeIndex);
	}

	/**
	 * It returns an array whose elements are a function of all the possible values of the approximating
	 * binomial model at the given time index. The element in position i is the function of the value of the underlying in the
	 * case when it has gone down i times.
	 * @param timeIndex, the given time index
	 * @param DoubleUnaryOperator transformFunction, the function
	 * @return an array of doubles representing a function all the possible values of the binomial model at timeIndex.
	 * 		   The value in position i is transformFunction(B(0)*u^(timeIndex-i)*d^i)
	 */
	public double[] getTransformedValuesAtGivenTimeIndex(int timeIndex, DoubleUnaryOperator transformFunction) {
		//we want to generate ourBinomialModel only once! So we check if it is null: if yes, we have to generate it
		if (ourBinomialModel==null) {
			generateBinomialModel();
		}
		//pure delegation
		return ourBinomialModel.getTransformedValuesAtGivenTimeIndex(timeIndex, transformFunction);
	}

	/**
	 * It returns the probabilities of all the possible values of the approximating binomial model at the given time index.
	 * The element in position i is the probability of the value where the underlying has gone down i times.
	 * @param timeIndex, the given time index
	 * @return an array of doubles representing the probabilities of all the possible values of the binomial
	 * 		   model at timeIndex. The value in position i is the probability of B(0)*u^(timeIndex-i)*d^i
	 */
	public double[] getValuesProbabilitiesAtGivenTimeIndex(int timeIndex) {
		//we want to generate ourBinomialModel only once! So we check if it is null: if yes, we have to generate it
		if (ourBinomialModel==null) {
			generateBinomialModel();
		}
		//pure delegation
		return ourBinomialModel.getValuesProbabilitiesAtGivenTimeIndex(timeIndex);
	}


	/**
	 * It returns all the possible values of the approximating binomial model at the given time. The element in
	 * position i is the one where the underlying has gone down i times.
	 * @param time, the given time as double
	 * @return an array of doubles representing all the possible values of the binomial model at given time.
	 * 		   The value in position i is B(0)*u^(Math.round(time/timeStep)-i)*d^i
	 */
	public double[] getValuesAtGivenTime(double time) {
		int timeIndex = (int) Math.round(time/timeStep);
		//we want to generate ourBinomialModel only once! So we check if it is null: if yes, we have to generate it
		if (ourBinomialModel==null) {
			generateBinomialModel();
		}
		//pure delegation
		return ourBinomialModel.getValuesAtGivenTimeIndex(timeIndex);
	}

	/**
	 * It returns an array whose elements are a function of all the possible values of the binomial model at
	 * the given time. The element in position i is the function of the value of the underlying in the
	 * case when it has gone down i times.
	 * @param time, the given time
	 * @param DoubleUnaryOperator transformFunction, the function
	 * @return an array of doubles representing a function all the possible values of the binomial model at timeIndex.
	 * 		   The value in position i is transformFunction(B(0)*u^(Math.round(time/timeStep)-i)*d^i)
	 */
	public double[] getTransformedValuesAtGivenTime(double time, DoubleUnaryOperator transformFunction) {
		int timeIndex = (int) Math.round(time/timeStep);
		//we want to generate ourBinomialModel only once! So we check if it is null: if yes, we have to generate it
		if (ourBinomialModel==null) {
			generateBinomialModel();
		}
		//pure delegation
		return ourBinomialModel.getTransformedValuesAtGivenTimeIndex(timeIndex, transformFunction);
	}

	/**
	 * It returns the probabilities of all the possible values of the approximating  binomial model at the given time.
	 * The element in position i is the probability of the value where the underlying has gone down i times.
	 * @param time, the given time 
	 * @return an array of doubles representing the probabilities of all the possible values of the binomial
	 * 		   model at timeIndex. The value in position i is the probability of B(0)*u^(Math.round(time/timeStep-i)*d^i
	 */
	public double[] getValuesProbabilitiesAtGivenTime(double time) {
		int timeIndex = (int) Math.round(time/timeStep);
		//we want to generate ourBinomialModel only once! So we check if it is null: if yes, we have to generate it
		if (ourBinomialModel==null) {
			generateBinomialModel();
		}
		//pure delegation
		return ourBinomialModel.getValuesProbabilitiesAtGivenTimeIndex(timeIndex);
	}

	/**
	 * It returns the array whose two elements are the probability of an up movement and the probability
	 * of a down movement, respectively, for the approximating binomial model.
	 * @return the array whose two elements are the probability of an up movement and the probability
	 * of a down movement, respectively.
	 */
	public double[] getUpAndDownProbabilities() {
		//we want to generate ourBinomialModel only once! So we check if it is null: if yes, we have to generate it
		if (ourBinomialModel==null) {
			generateBinomialModel();
		}
		//pure delegation
		return ourBinomialModel.getUpAndDownProbabilities();
	}

	/**
	 * It returns an array representing the discounted conditional expectations at given timeIndex of the
	 * values of (possibly a function of) an approximating binomial model at time timeIndex+1. 
	 * 
	 * @param values, values of (possibly a function of) an approximating binomial model at time timeIndex+1
	 * @param timeIndex, the time index
	 * @return the array of the discounted conditional expectations at timeIndex of values. 
	 * 			The i-th element is the conditional expectation computed in the case when the underlying
	 * 			has gone down i times.
	 */
	public double[] getConditionalExpectation(double[] values,int timeIndex) {
		//we want to generate ourBinomialModel only once! So we check if it is null: if yes, we have to generate it
		if (ourBinomialModel==null) {
			generateBinomialModel();
		}
		return ourBinomialModel.getConditionalExpectation(values, timeIndex);
	}

	/*
	 * Getters for the parameters of the binomial model. Some of them are used in the derived classes:
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
