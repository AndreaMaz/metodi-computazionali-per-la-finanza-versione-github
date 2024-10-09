package it.univr.trees.simplebinomialmodel;

import java.util.Arrays;
import java.util.function.DoubleUnaryOperator;

import it.univr.usefulmethodsarrays.UsefulMethodsForArrays;

/**
 * This class tests the implementation of BinomialModel
 * 
 * @author Andrea Mazzon
 *
 */
public class BinomialModelTest {

	
	public static void main(String[] strings) {

		double upFactor = 2;
		double downFactor = 0.5;
		int numberOfTimes = 8;
		double initialValue = 100;
		double riskFreeFactor = 0.0;
		
		BinomialModel myBinomialModel = new BinomialModel(upFactor,  downFactor, riskFreeFactor, initialValue,  numberOfTimes);
				
		//possible value at time index 2
		
		double[] valuesAtTimeIndexTwo = myBinomialModel.getValuesAtGivenTimeIndex(2);
		System.out.println("Possible values at time index 2 are:");
		System.out.println(Arrays.toString(valuesAtTimeIndexTwo));
		System.out.println();
		
		//probabilities at time index 2
		
		double[] probabilitiesAtTimeIndexTwo = myBinomialModel.getValuesProbabilitiesAtGivenTimeIndex(2);
		
		System.out.println("Probabilities at time index 2 are:");
		System.out.println(Arrays.toString(probabilitiesAtTimeIndexTwo));
		System.out.println();
		
		//all values
		
		System.out.println("All realizations for all times are:");
		for (int timeIndex = 0; timeIndex < numberOfTimes; timeIndex ++) {
			double[] values = myBinomialModel.getValuesAtGivenTimeIndex(timeIndex);
			System.out.println(Arrays.toString(values));	
		}
		
		
		System.out.println();
		
		//all probabilities
		
		System.out.println("All probabilities for all times are:");
		for (int timeIndex = 0; timeIndex < numberOfTimes; timeIndex ++) {
			double[] probabilities = myBinomialModel.getValuesProbabilitiesAtGivenTimeIndex(timeIndex);
			System.out.println(Arrays.toString(probabilities));	
		}
		
		
		System.out.println();
		System.out.println();
		
		double[] valuesAtFinalTime = myBinomialModel.getValuesAtGivenTimeIndex(numberOfTimes-1);
		
		//application of function to realizations
		
		double strike = initialValue;
		
		DoubleUnaryOperator ourFunction = (x) -> Math.max(x-strike, 0);//call
		
		double[] transformedValuesAtFinalTime = myBinomialModel.getTransformedValuesAtGivenTimeIndex(numberOfTimes-1, ourFunction);
		
		DoubleUnaryOperator ourSecondFunction = (x) -> Math.max(strike - x, 0);//put
		
		double[] secondTransformedValuesAtFinalTime = myBinomialModel.getTransformedValuesAtGivenTimeIndex(numberOfTimes-1, ourSecondFunction);
		
		System.out.println("Values at final time:");
		System.out.println(Arrays.toString(valuesAtFinalTime));
		System.out.println();
		
		System.out.println("Call values at final time:");
		System.out.println(Arrays.toString(transformedValuesAtFinalTime));
		System.out.println();
		
		System.out.println("Put values at final time:");
		System.out.println(Arrays.toString(secondTransformedValuesAtFinalTime));
		System.out.println();
		
		double[] probabilitiesAtFinalTime = myBinomialModel.getValuesProbabilitiesAtGivenTimeIndex(numberOfTimes-1);
		
		double expectedValueOfCallOption = UsefulMethodsForArrays.getScalarProductTwoArrays(probabilitiesAtFinalTime, transformedValuesAtFinalTime);
		
		System.out.println("Expected value of call: " + expectedValueOfCallOption);
		System.out.println();
		System.out.println();
		
		System.out.println(ourFunction.applyAsDouble(101));
	}
}
