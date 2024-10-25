package it.univr.trees.approximatingmodels;


import java.util.Arrays;



public class ApproximatingModelsTest {


	public static void main(String[] strings) throws Exception {

		double spotPrice = 100;
		double riskFreeRate = 0.0;
		double volatility = 0.2;
		double lastTime = 1.0;
		int numberOfTimes = 11;
		
		//test for Cox-Ross-Rubinstein: we just look at its expected value
		CoxRossRubinsteinModel firstApproximatingModel = new CoxRossRubinsteinModel(
				spotPrice, riskFreeRate, volatility,  lastTime, numberOfTimes);
		
		double[] valuesForCRR = firstApproximatingModel.getValuesAtGivenTime(lastTime);
		
		System.out.println("Values for Cox Ross Rubinstein:");
		System.out.println(Arrays.toString(valuesForCRR));
		
		//test for Jarrow Rudd: we just look at its expected value
		JarrowRuddModel secondApproximatingModel = new JarrowRuddModel(
				spotPrice, riskFreeRate, volatility,  lastTime, numberOfTimes);
		
		double[] valuesForJR = secondApproximatingModel.getValuesAtGivenTime(lastTime);
		
		System.out.println("Values for Jarrow Rudd:");
		System.out.println(Arrays.toString(valuesForJR));
		
		double strikeForLeisenReimer = spotPrice;
		
		//test for Jarrow Rudd: we just look at its expected value
		LeisenReimerModel thirdApproximatingModel = new LeisenReimerModel(
				spotPrice, riskFreeRate, volatility,  lastTime, numberOfTimes, strikeForLeisenReimer);
		
		double[] valuesForLR = thirdApproximatingModel.getValuesAtGivenTime(lastTime);
		
		System.out.println("Values for Leisen-Reimer:");
		System.out.println(Arrays.toString(valuesForLR));
		
	}
}
