package it.univr.trees.assetderivativevaluation.products;

import java.util.function.DoubleUnaryOperator;

import it.univr.trees.approximatingmodels.ApproximatingTrinomialModel;

/**
 * This class implements the valuation of an European option non path dependent (that is, which pays only
 * according to the value of the underlying at maturity) via an approximation of a Black-Scholes process
 * with a Trinomial model.
 * 
 * @author Andrea Mazzon
 *
 */
public class NotTooNiceEuropeanNonPathDependentOptionForTrinomialModel {
	private double maturity;
	private DoubleUnaryOperator payoffFunction;

	/**
	 * It constructs an object which represents the implementation of the European, non path dependent option.
	 * @param maturity, the maturity of the option
	 * @param payoffFunction, the funtion which identifies the payoff. The payoff is f(S_T) for payoffFunction
	 * f and underlying value S_T at maturity. The payoffFunction is represented by a DoubleUnaryOperator.
	 */
	public NotTooNiceEuropeanNonPathDependentOptionForTrinomialModel(double maturity, DoubleUnaryOperator payoffFunction) {
		this.maturity = maturity;
		this.payoffFunction = payoffFunction;
	}

	/**
	 * It returns the discounted value of the option written on the Black-Scholes model approximated by
	 * the object of type ApproximatingTrinomialModel given in input. The value of the option is computed
	 * as the discounted expectation of the possible values at maturity. This expectation is computed by going backward
	 * from maturity to initial time and computing the iterative conditional expectation, see slides.
	 * 
	 * @param approximatingTrinomialModel, the underlying
	 * @return the value of the option written on the underlying
	 */
	public double getValue(ApproximatingTrinomialModel approximatingTrinomialModel) {
		//the vector representing all the possible values of the payoff at maturity
		double[] optionValues = approximatingTrinomialModel.getTransformedValuesAtGivenTime(maturity, payoffFunction);
		int numberOfTimes = (int) Math.round(maturity/approximatingTrinomialModel.getTimeStep());
		//we go backward and for any timeIndex we compute the conditional expectation of the value of the option at timeIndex + 1
		for (int timeIndex = numberOfTimes - 1; timeIndex >= 0; timeIndex--) {
			//delegation to approximatingTrinomialModel!
        	double[] conditionalExpectation = approximatingTrinomialModel.getConditionalExpectation(optionValues, timeIndex);
            optionValues = conditionalExpectation;   
        }
		return optionValues[0];
	}
}
