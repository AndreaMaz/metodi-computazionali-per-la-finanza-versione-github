package it.univr.montecarlo.ourproducts;

import net.finmath.exception.CalculationException;
import net.finmath.functions.AnalyticFormulas;
import net.finmath.montecarlo.assetderivativevaluation.AssetModelMonteCarloSimulationModel;
import net.finmath.montecarlo.assetderivativevaluation.MonteCarloAssetModel;
import net.finmath.montecarlo.assetderivativevaluation.models.BlackScholesModel;
import net.finmath.montecarlo.assetderivativevaluation.products.AbstractAssetMonteCarloProduct;
import net.finmath.stochastic.RandomVariable;
import net.finmath.time.TimeDiscretization;

/**
 * This class implements the valuation of a Cliquet option under the assumption that the underlying is the simulation
 * of a Black-Scholes model. Under this setting, the valuation of the option is performed using a control variate consisting
 * of the non truncated sum of truncated returns.
 * @author Andrea Mazzon
 */
public class CliquetOptionWithBSControlVariate extends AbstractAssetMonteCarloProduct {

	//all the parameters defining the option
	private final double localFloor;
	private final double localCap;
	private final double globalFloor;
	private final double globalCap;
	private final TimeDiscretization monitoringTimes;
	
	private final Integer underlyingIndex;

	/**
	 * It constructs a product representing a cliquet option on an asset X with dynamics following the log-normal process
	 * dX_t = r X_t dt + sigma X_t dW_t, 0 \le t \le T,
	 * where T is the maturity of the option.
	 *   
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
     * @param localCap :the cap for the single return in the option
     * @param globalFloor,the floor for the sum of returns in the option
     * @param globalCap :the cap for the sum of returns in the option
	 * @param monitoringTimes The times t_i used in the calculation of the sum of returns. The time discretization is supposed to be equally spaced
	 * @param underlyingIndex The index of the asset S to be fetched from the model
	 */
	public CliquetOptionWithBSControlVariate(final double globalFloor, final double globalCap, final double localFloor, final double localCap, final TimeDiscretization monitoringTimes, final Integer underlyingIndex) {
		super();
		this.localFloor = localFloor;
		this.localCap = localCap;
		this.globalFloor = globalFloor;
		this.globalCap = globalCap;
		this.monitoringTimes = monitoringTimes;
		this.underlyingIndex = underlyingIndex;
	}

	/**
	 * It constructs a product representing a cliquet option on an asset X with dynamics following the log-normal process
	 * dX_t = r X_t dt + sigma X_t dW_t, 0 \le t \le T,
	 * where T is the maturity of the option.
	 *   
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
     * @param localCap :the cap for the single return in the option
     * @param globalFloor,the floor for the sum of returns in the option
     * @param globalCap :the cap for the sum of returns in the option
	 * @param monitoringTimes The times t_i used in the calculation of the sum of returns. The time discretization is supposed to be equally spaced
	 */
	public CliquetOptionWithBSControlVariate(final double globalFloor, final double globalCap, final double localFloor, final double localCap, final TimeDiscretization monitoringTimes) {
		this(globalFloor, globalCap, localFloor, localCap, monitoringTimes, 0);
	}
	
	//FOR NOW, IN THIS CLASS THERE IS A BUG, AS YOU CAN NOTE TESTING IT. LET'S TRY TO FIND IT TOGETHER

	//private method used to compute the analytic value of the control variate, see the notes
	private double computeAnalyticValue(double riskFreeRate, double volatility) {
		
		double initialValueForAnalytic = 1;//the initial value of X_{t_n}/X_{t_{n-1}}
		int numberOfIntervals = monitoringTimes.getNumberOfTimeSteps();
		double maturityOfTheCallOptions = monitoringTimes.getTimeStep(0);//we suppose them to be all the same

		double firstStrike = localFloor + 1;
		double secondStrike = localCap + 1;
		
		double firstCallPrice = AnalyticFormulas.blackScholesOptionValue(initialValueForAnalytic, riskFreeRate, volatility,
					maturityOfTheCallOptions, firstStrike);

		double secondCallPrice = AnalyticFormulas.blackScholesOptionValue(initialValueForAnalytic, riskFreeRate, volatility,
					maturityOfTheCallOptions, secondStrike);
		
		// we repeat the same over all the time intervals, so we multiply by their number (we could not do that if times are not equally spaced, in general)
		return numberOfIntervals * (localFloor + firstCallPrice - secondCallPrice);
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

		//we first compute the payoff, without control variate
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
			sumOfTruncations = sumOfTruncations.add(currentReturnTruncated);//we update the sum. X (see notes)
			pastTime = currentTime;
			
		}

		
		RandomVariable payoffWithoutControlVariates = sumOfTruncations.floor(globalFloor).cap(globalCap);//Y (see notes)
		
		
		//now we apply the control variate
		
		//computation of the optimal beta
		double covariance = payoffWithoutControlVariates.covariance(sumOfTruncations).doubleValue();//the method covariance returns a RandomVariable
		double varianceOfControlVariate = sumOfTruncations.getVariance();
		
		double optimalBeta = covariance/varianceOfControlVariate;
		
		
		/*
		 * The assumption that the underlying is a Black-Scholes model comes here: we downcast the object of type
		 * ProcessModel returned by the method getModel() of MonteCarloAssetModel. In order to do this, we
		 * have to downcast also the object given in input by the method getValue of this class. 
		 * 
		 */
		BlackScholesModel processModel = (BlackScholesModel) ((MonteCarloAssetModel) model).getModel();
		
		//to be given by the method which computes the analytic value of the non truncated sum of truncated returns
		double riskFreeRate = processModel.getRiskFreeRate().doubleValue();
        double volatility = processModel.getVolatility().doubleValue();
		double analyticValueControlVariate = computeAnalyticValue(riskFreeRate,volatility);

		RandomVariable termToSubtract = sumOfTruncations.sub(analyticValueControlVariate).mult(optimalBeta);
				
		RandomVariable values = payoffWithoutControlVariates.sub(termToSubtract);
		
		//at this point, we have computed the payoff with the control variate, and we just have to discount
		
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
