package it.univr.trees.approximatingmodels;


import java.util.Arrays;
import java.util.function.DoubleUnaryOperator;

import it.univr.trees.assetderivativevaluation.products.EuropeanNonPathDependentOption;
import it.univr.usefulmethodsarrays.UsefulMethodsForArrays;
import net.finmath.functions.AnalyticFormulas;
import net.finmath.plots.Named;
import net.finmath.plots.Plot2D;


public class ApproximatingModelsTest {


	public static void main(String[] strings) throws Exception {

		double spotPrice = 100;
		double riskFreeRate = 0.0;
		double volatility = 0.2;
		double lastTime = 1.0;
		int numberOfTimes = 100;

		double strike = 80;
		
		//first test for Cox-Ross-Rubinstein: we just look at its expected value
		CoxRossRubinsteinModel firstApproximatingModel = new CoxRossRubinsteinModel(
				spotPrice, riskFreeRate, volatility,  lastTime, numberOfTimes);
		
	
		//da qua andremo avanti insieme
	}
}
