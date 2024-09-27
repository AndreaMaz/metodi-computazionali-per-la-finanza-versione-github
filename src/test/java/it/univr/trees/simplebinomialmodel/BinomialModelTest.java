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
		
		
		//da qua andremo avanti insieme

	}
}
