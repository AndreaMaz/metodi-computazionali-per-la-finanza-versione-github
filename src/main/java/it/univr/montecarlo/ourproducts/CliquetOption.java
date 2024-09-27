package it.univr.montecarlo.ourproducts;

import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.assetderivativevaluation.AssetModelMonteCarloSimulationModel;
import net.finmath.montecarlo.assetderivativevaluation.products.AbstractAssetMonteCarloProduct;
import net.finmath.stochastic.RandomVariable;
import net.finmath.time.TimeDiscretization;

/**
 * This class implements the valuation of a Cliquet option.
 */
public class CliquetOption extends AbstractAssetMonteCarloProduct {

	private final double localFloor;
	private final double localCap;
	private final double globalFloor;
	private final double globalCap;
	private final TimeDiscretization monitoringTimes;
	private final Integer underlyingIndex;

	/**
	 * It constructs a product representing a cliquet option on an asset X.
	 * The payoff of the Cliquet option with local floor localFloor, local cap localCap, global floor globalFloor, global
	 * cap globalCap and partition of the interval given by the TimeDiscretization object representing 0=t_0 <= t_1 <=..<= t_N =T
	 * is
	 *   
	 * min(max(R_1^*+R_2^*+..+R_N^*, globalFloor), globalCap),
	 *   
	 * where 
	 *   
	 * R_k^* = min(max(R_k, localFloor), localCap)
	 *	   
	 * with
	 *   
	 * R_k = X_{t_k}/X_{t_{k-1}} - 1.
	 *
	 *
	 * @param localFloor,the floor for the single return in the option
     * @param localCap, the cap for the single return in the option
     * @param globalFloor,the floor for the sum of returns in the option
     * @param globalCap, the cap for the sum of returns in the option
	 * @param monitoringTimes The times t_i used in the calculation of the sum of returns
	 * @param underlyingIndex The index of the asset S to be fetched from the model
	 */
	public CliquetOption(final double globalFloor, final double globalCap, final double localFloor, final double localCap, final TimeDiscretization monitoringTimes, final Integer underlyingIndex) {
		super();
		this.localFloor = localFloor;
		this.localCap = localCap;
		this.globalFloor = globalFloor;
		this.globalCap = globalCap;
		this.monitoringTimes = monitoringTimes;
		this.underlyingIndex = underlyingIndex;
	}

	/**
	 * It constructs a product representing a cliquet option on an asset X.
	 * The payoff of the Cliquet option with local floor localFloor, local cap localCap, global floor globalFloor, global
	 * cap globalCap and partition of the interval given by the TimeDiscretization object representing 0=t_0 <= t_1 <=..<= t_N =T
	 * is
	 *   
	 * min(max(R_1^*+R_2^*+..+R_N^*, globalFloor), globalCap),
	 *   
	 * where 
	 *   
	 * R_k^* = min(max(R_k, localFloor), localCap)
	 *	   
	 * with
	 *   
	 * R_k = X_{t_k}/X_{t_{k-1}} - 1.
	 *
	 *
	 * @param localFloor,the floor for the single return in the option
     * @param localCap, the cap for the single return in the option
     * @param globalFloor,the floor for the sum of returns in the option
     * @param globalCap, the cap for the sum of returns in the option
	 * @param monitoringTimes The times t_i used in the calculation of the sum of returns
	 */
	public CliquetOption(final double globalFloor, final double globalCap, final double localFloor, final double localCap, final TimeDiscretization monitoringTimes) {
		this(globalFloor, globalCap, localFloor, localCap, monitoringTimes, 0);
	}

	/**
	 * This method returns the value random variable of the product within the specified model, evaluated at a given evalutationTime.
	 * Note: For a lattice this is often the value conditional to evalutationTime, for a Monte-Carlo simulation this is the (sum of) value discounted to evaluation time.
	 * Cashflows prior evaluationTime are not considered.
	 *
	 * @param evaluationTime The time on which this products value should be observed.
	 * @param model The model used to price the product.
	 * @return The random variable representing the value of the product discounted to evaluation time
	 * @throws net.finmath.exception.CalculationException Thrown if the valuation fails, specific cause may be available via the <code>cause()</code> method.
	 */
	@Override
	public RandomVariable getValue(final double evaluationTime, final AssetModelMonteCarloSimulationModel model) throws CalculationException {
		RandomVariable sumOfTruncations = model.getRandomVariableForConstant(0.0);//we will update this sum
		
		double pastTime = 0;//we will divide by the value of the underlying at this time
		double currentTime;
		
		//they will be updated at every iteration of the for loop
		RandomVariable underlyingAtCurrentTime;
		RandomVariable underlyingAtPastTime;
		RandomVariable currentReturn;
		RandomVariable currentReturnTruncated;
		
		
		//in this for loop we compute the sum of the truncated returns
		for(int currentTimeIndex =1; currentTimeIndex< monitoringTimes.getNumberOfTimes(); currentTimeIndex++) {
			
			currentTime = monitoringTimes.getTime(currentTimeIndex);
			underlyingAtPastTime	= model.getAssetValue(pastTime, underlyingIndex);//X_{t_{n-1}}
			underlyingAtCurrentTime	= model.getAssetValue(currentTime, underlyingIndex);//X_{t_n}
			currentReturn = underlyingAtCurrentTime.div(underlyingAtPastTime).sub(1);//X_{t_n}/X_{t_{n-1}}-1
			currentReturnTruncated = currentReturn.floor(localFloor).cap(localCap);//min(max(X_{t_n}/X_{t_{n-1}}-1,floor),cap)
			sumOfTruncations = sumOfTruncations.add(currentReturnTruncated);//we update the sum
			pastTime = currentTime;
		}


		// The payoff: values = max(underlying - strike, 0)
		RandomVariable values = sumOfTruncations.floor(globalFloor).cap(globalCap);

		double maturity = monitoringTimes.getTime(monitoringTimes.getNumberOfTimeSteps());
		// Discounting...
		final RandomVariable numeraireAtMaturity	= model.getNumeraire(maturity);
		final RandomVariable monteCarloWeights		= model.getMonteCarloWeights(maturity);
		values = values.div(numeraireAtMaturity).mult(monteCarloWeights);

		// ...to evaluation time.
		final RandomVariable	numeraireAtEvalTime			= model.getNumeraire(evaluationTime);
		final RandomVariable	monteCarloWeightsAtEvalTime	= model.getMonteCarloWeights(evaluationTime);
		values = values.mult(numeraireAtEvalTime).div(monteCarloWeightsAtEvalTime);

		return values;
	}

}
