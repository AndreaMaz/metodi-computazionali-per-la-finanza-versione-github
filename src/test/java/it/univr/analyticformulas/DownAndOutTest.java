package it.univr.analyticformulas;

import java.util.Arrays;
import java.util.function.DoubleUnaryOperator;

import net.finmath.functions.AnalyticFormulas;
import net.finmath.plots.Named;
import net.finmath.plots.Plot2D;

public class DownAndOutTest {

	public static void main(String[] args) {


		double spotPrice = 2;
		double riskFreeRate = 0.0;
		double volatility = 0.3;
		double lastTime = 3;
		
		double strike = 2;
		
		DoubleUnaryOperator downAndOutDifferenceFunction = (lowerBarrier) -> {
			return Math.pow(spotPrice/lowerBarrier,-(2*riskFreeRate/volatility*volatility - 1)) 
					* AnalyticFormulas.blackScholesOptionValue(lowerBarrier*lowerBarrier/spotPrice, riskFreeRate, volatility, lastTime, strike)
					// /AnalyticFormulas.blackScholesOptionValue(spotPrice, riskFreeRate, volatility, lastTime, strike)//uncomment if you want the relative difference
					;
		};		
		
		double minLowerBarrier = 0.2;
		double maxLowerBarrier = 2.0;
		
		int numberOfPlottedValues = 100;
				
		
		final Plot2D plotDifference = new Plot2D(minLowerBarrier, maxLowerBarrier,  numberOfPlottedValues, Arrays.asList(
				new Named<DoubleUnaryOperator>("Difference", downAndOutDifferenceFunction)));
		plotDifference.setXAxisLabel("Barrier");
		plotDifference.setYAxisLabel("Price difference");
		plotDifference.setTitle("Price difference between a European call option and a down-and-out European call option, spot price = " + spotPrice + ", risk free rate = " +
				riskFreeRate + ", volatility = " + volatility + ", maturity = " + lastTime + ", strike = " + strike);
		plotDifference.show();	
	}

}
