package it.univr.montecarlo.ourproducts;

import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionFromMersenneRandomNumbers;
import net.finmath.montecarlo.assetderivativevaluation.MonteCarloBlackScholesModel;
import net.finmath.montecarlo.assetderivativevaluation.products.AbstractAssetMonteCarloProduct;
import net.finmath.stochastic.RandomVariable;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;

public class CliquetOptionTest {

	public static void main(String[] args) throws CalculationException {
		
		
		double initialValue = 100;
		double riskFreeRate = 0.2;
		double volatility = 0.5;

		// option parameters
		double maturity = 4;

		//time discretization parameters
		double initialTime = 0.0;
		double lengthIntervalsBetweenMonitoringTimes = 0.25;
		int numberOfIntervalsBetweenMonitoringTimes = (int) (maturity/lengthIntervalsBetweenMonitoringTimes);
		
		TimeDiscretization monitoringTimes = new TimeDiscretizationFromArray(initialTime, numberOfIntervalsBetweenMonitoringTimes, lengthIntervalsBetweenMonitoringTimes);
		
		double localFloor = -0.05;
		double localCap = 0.3;
		
		double globalFloor = 0;
		double globalCap = numberOfIntervalsBetweenMonitoringTimes * 0.15;

		// Monte Carlo parameters:
		
		//simulation parameters
		int numberOfPaths = 100000;
		int seed = 1897;
				
		BrownianMotion ourDriver = new BrownianMotionFromMersenneRandomNumbers(monitoringTimes, 1 /* numberOfFactors */, numberOfPaths, seed);

		//we construct an object of type MonteCarloBlackScholesModel: it represents the simulation of a Black-Scholes process
		MonteCarloBlackScholesModel blackScholesProcess = new MonteCarloBlackScholesModel(initialValue, riskFreeRate, volatility, ourDriver);
		
		AbstractAssetMonteCarloProduct optionWithoutControlVariate = 
				new CliquetOption(globalFloor, globalCap, localFloor, localCap, monitoringTimes);
		RandomVariable payoffWithoutControlVariate = optionWithoutControlVariate.getValue(0.0, blackScholesProcess);
		
		System.out.println("Monte Carlo price without control variates: " + payoffWithoutControlVariate.getAverage());
		System.out.println("Monte Carlo variance without control variates: " + payoffWithoutControlVariate.getVariance());
		
		System.out.println();
		
		AbstractAssetMonteCarloProduct optionWithControlVariate = 
				new CliquetOptionWithBSControlVariate(globalFloor, globalCap, localFloor, localCap, monitoringTimes);
		RandomVariable payoffWithControlVariate = optionWithControlVariate.getValue(0.0, blackScholesProcess);
		
		System.out.println("Monte Carlo price with control variates: " + payoffWithControlVariate.getAverage());
		System.out.println("Monte Carlo variance with control variates: " + payoffWithControlVariate.getVariance());
		
	}

}
