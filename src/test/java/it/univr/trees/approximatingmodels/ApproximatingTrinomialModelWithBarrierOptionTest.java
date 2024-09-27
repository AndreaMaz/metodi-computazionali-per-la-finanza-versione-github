package it.univr.trees.approximatingmodels;

import java.util.Arrays;
import java.util.function.DoubleUnaryOperator;

import it.univr.analyticformulas.OurAnalyticFormulas;
import it.univr.trees.assetderivativevaluation.products.EuropeanBarrierOption;
import it.univr.trees.assetderivativevaluation.products.NotTooNiceEuropeanBarrierOptionForTrinomialModel;
import net.finmath.plots.Named;
import net.finmath.plots.Plot2D;

public class ApproximatingTrinomialModelWithBarrierOptionTest {
	public static void main(String[] strings) throws Exception {

		double spotPrice = 2;
		double riskFreeRate = 0.0;
		double volatility = 0.7;
		double lastTime = 3;
		
		double strike = 2;
		
		double lowerBarrier = 1.7;
		
		
		DoubleUnaryOperator payoffFunction = (x) -> (x - strike > 0 ? x - strike : 0.0);
		
		NotTooNiceEuropeanBarrierOptionForTrinomialModel ourOption = new NotTooNiceEuropeanBarrierOptionForTrinomialModel(lastTime, payoffFunction, lowerBarrier, Double.MAX_VALUE);
		EuropeanBarrierOption otherOption = new EuropeanBarrierOption(lastTime, payoffFunction, lowerBarrier, Double.MAX_VALUE);

		
		/*
		 * We want to plot the results we get for our approximating models when we increase the number of times.
		 * In the same plot, we want to show also the analyic price of the option, as a benchmark.
		 * We use the Plot2D class of finmath-lib-plot-extensions. In order to do that, we have to define the
		 * functions to plot as objects of type DoubleUnaryOperator.
		 * In our case, we want these functions to take the number of times and return the prices approximated
		 * with this number of times. For us numberOfTimesForFunction should be an int, but in order to define
		 * a DoubleUnaryOperator one should take a double. So we first treat it as a double and then we downcast
		 * it when passing it to the getValue of EuropeanNonPathDependentOption.
		 */		

		DoubleUnaryOperator numberOfTimesToPriceBoyleModel = (numberOfTimesForFunction) -> {
		BoyleModel ourModelForFunction = new BoyleModel(spotPrice, riskFreeRate, volatility, lastTime, (int) numberOfTimesForFunction);		
		return ourOption.getValue(ourModelForFunction);
		};		
		
		DoubleUnaryOperator numberOfTimesToPriceCoxRossRubinsteinModel = (numberOfTimesForFunction) -> {
			CoxRossRubinsteinModel ourModelForFunction = new CoxRossRubinsteinModel(spotPrice, riskFreeRate, volatility, lastTime, (int) numberOfTimesForFunction);		
			return otherOption.getValue(ourModelForFunction);
		};
		
		DoubleUnaryOperator numberOfTimesToPriceJarrowRuddModel = (numberOfTimesForFunction) -> {
			JarrowRuddModel ourModelForFunction = new JarrowRuddModel(spotPrice, riskFreeRate, volatility, lastTime, (int) numberOfTimesForFunction);		
			return otherOption.getValue(ourModelForFunction);
		};
		
		
		DoubleUnaryOperator numberOfTimesToPriceLeisenReimerModel = (numberOfTimesForFunction) -> {
			LeisenReimerModel ourModelForFunction = new LeisenReimerModel(spotPrice, riskFreeRate, volatility, lastTime, (int) numberOfTimesForFunction, strike);		
			return otherOption.getValue(ourModelForFunction);
		};
		
		/*
		 * This is the DoubleUnaryOperator to plot the analytic price. "Dummy" in the sense that it is a function
		 * that always gives the same value.
		 */
		DoubleUnaryOperator dummyFunctionBlackScholesPrice = (numberOfTimesForFunction) -> {
			return OurAnalyticFormulas.blackScholesDownAndOut(spotPrice, riskFreeRate, volatility, lastTime, strike, lowerBarrier);
		};
		
		int maxNumberOfTimes = 1500;
		int minNumberOfTimes = 10;
		
		//plots
		
		final Plot2D plot = new Plot2D(minNumberOfTimes, maxNumberOfTimes, maxNumberOfTimes-minNumberOfTimes+1, Arrays.asList(
				new Named<DoubleUnaryOperator>("Boyle", numberOfTimesToPriceBoyleModel),
				new Named<DoubleUnaryOperator>("Cox Ross Rubinstein", numberOfTimesToPriceCoxRossRubinsteinModel),
				new Named<DoubleUnaryOperator>("Jarrow-Rudd", numberOfTimesToPriceJarrowRuddModel),
				new Named<DoubleUnaryOperator>("Leisen-Reimer", numberOfTimesToPriceLeisenReimerModel),
				new Named<DoubleUnaryOperator>("Analytic price", dummyFunctionBlackScholesPrice)));
		
		plot.setXAxisLabel("Number of discretized times");
		plot.setYAxisLabel("Price");
		plot.setIsLegendVisible(true);
		plot.show();		
	}
}
