package it.univr.trees.assetderivativevaluation.products;

import java.util.function.DoubleUnaryOperator;

import it.univr.trees.approximatingmodels.ApproximatingTrinomialModel;
import it.univr.usefulmethodsarrays.UsefulMethodsForArrays;

/**
 * This class implements the valuation of an European option with double or single barrier for a trinomial model.
 * This is a path dependent option which pays the payoff only if the value of the underlying stays in an interval
 * [lowerBarrier, upperBarrier] for the whole path. We have single barrier if we only have lowerBarrier
 * or only have upperBarrier. The value is computed via an approximation of a Black-Scholes process
 * with a Trinomial model.
 * 
 * @author Andrea Mazzon
 *
 */
public class NotTooNiceEuropeanBarrierOptionForTrinomialModel {
	
	private double maturity;
	private DoubleUnaryOperator payoffFunction;
	private DoubleUnaryOperator barrierFunction;//this is defined in the costructor.
	

	/**
	 * It constructs an object which represents the implementation of the European option with barriers.
	 * @param maturity, the maturity of the option
	 * @param payoffFunction, the funtion which identifies the payoff. The payoff is f(S_T) for payoffFunction
	 * f and underlying value S_T at maturity. The payoffFunction is represented by a DoubleUnaryOperator
	 * @param lowerBarrier, the lower barrier: if the underlying goes below this value in its path until maturity,
	 * we get no payoff
	 * * @param upperBarrier, the upper barrier: if the underlying goes above this value in its path until maturity,
	 * we get no payoff
	 */
	public NotTooNiceEuropeanBarrierOptionForTrinomialModel(double maturity, DoubleUnaryOperator payoffFunction, double lowerBarrier,
			double upperBarrier) {
		this.maturity = maturity;
		this.payoffFunction = payoffFunction;
		//ternary operator!
		barrierFunction = (x) -> (x>lowerBarrier & x < upperBarrier ? 1 : 0);
	}
	
	/**
	 * It returns the discounted value of the option written on the Black-Scholes model approximated by
	 * the object of type ApproximatingTrinomialModel given in input. The value of the option is computed
	 * as the discounted expectation of the possible values at maturity. This expectation is computed by going backward
	 * from maturity to initial time and computing the iterative conditional expectation, see slides. The conditional
	 * expectations are multiplied at every time with a vector whose elements are 1 if the value of the underlying
	 * approximating Trinomial model is within the interval [lowerBarrier, upperBarrier] and 0 otherwise.
	 * 
	 * 
	 * @param approximatingTrinomialModel, the underlying
	 * @return the value of the option written on the underlying
	 */
	public double getValue(ApproximatingTrinomialModel approximatingTrinomialModel) {
		
		//the values of the option at maturity if this is not a barrier option
		double[] optionValuesWithoutBarrier = approximatingTrinomialModel.getTransformedValuesAtGivenTime(maturity, payoffFunction);
		
		//the values of the underlyings: we need them to check if they are inside the interval
		double[] underlyingValues = approximatingTrinomialModel.getValuesAtGivenTime(maturity);
		
		/*
		 * vector whose elements are 1 if the value of the underlying approximating Trinomial model is within the interval
		 * [lowerBarrier, upperBarrier] and 0 otherwise
		 */
		//(0,0,0,1,1,1...,0,0,0)
		double[] areTheUnderlyingValuesInsideInterval = UsefulMethodsForArrays.applyFunctionToArray(underlyingValues, barrierFunction);
		 
		//the values of the option at maturity, considering now the barrier
		double[] optionValues = UsefulMethodsForArrays.multArrays(optionValuesWithoutBarrier, areTheUnderlyingValuesInsideInterval);

		int numberOfTimes = (int) Math.round(maturity/approximatingTrinomialModel.getTimeStep());
		for (int timeIndex = numberOfTimes - 1; timeIndex >= 0; timeIndex--) {
			//now we repeat the same thing as above at any time.
			
			//the values of the option not considering the barrier
        	double[] conditionalExpectation = approximatingTrinomialModel.getConditionalExpectation(optionValues, timeIndex);
    		//the values of the underlyings: we need them to check if they are inside the interval
        	underlyingValues = approximatingTrinomialModel.getValuesAtGivenTimeIndex(timeIndex);
        	/*
    		 * vector whose elements are 1 if the value of the underlying approximating Trinomial model is within the interval
    		 * [lowerBarrier, upperBarrier] and 0 otherwise
    		 */
        	areTheUnderlyingValuesInsideInterval = UsefulMethodsForArrays.applyFunctionToArray(underlyingValues, barrierFunction);
        	
    		//the values of the option, considering now the barrier
        	double[] transformedConditionalExpectation = UsefulMethodsForArrays.multArrays(conditionalExpectation, areTheUnderlyingValuesInsideInterval);
        	optionValues = transformedConditionalExpectation;  

        }
		return optionValues[0];
	}

}
