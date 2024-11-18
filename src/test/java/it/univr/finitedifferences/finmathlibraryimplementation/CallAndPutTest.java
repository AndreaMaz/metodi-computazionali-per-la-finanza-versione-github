package it.univr.finitedifferences.finmathlibraryimplementation;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import net.finmath.finitedifference.models.FDMBlackScholesModel;
import net.finmath.finitedifference.models.FiniteDifference1DModel;
import net.finmath.finitedifference.products.FDMEuropeanCallOption;
import net.finmath.finitedifference.products.FDMEuropeanPutOption;
import net.finmath.finitedifference.products.FiniteDifference1DProduct;
import net.finmath.functions.AnalyticFormulas;

/**
 * In this class we test the Finmath library implementation of finite difference methods to price call and put options.
 * The underlying is a Black-Scholes process. We print the prices for different initial values of the underlying and check,
 * via assertArrayEquals, if these are close to the analytic one.
 * 
 * @author Andrea Mazzon
 *
 */
public class CallAndPutTest {

	
	//option parameters
	double optionMaturity = 1;
	double optionStrike = 50;
	
	//model parameters
	double initialValue = 50;
	double riskFreeRate = 0.06;
	double volatility = 0.4;
	
	//discretization parameters
	int numberTimesteps = 120;
	int numberSpacesteps = 120;
	int numberStandardDeviations = 5;
	double theta = 0.5;

	FiniteDifference1DModel model = new FDMBlackScholesModel(
			numberTimesteps,//for the discretization of the time interval
			numberSpacesteps,//for the discretization of the space domain
			numberStandardDeviations,//this enters in the computation of the right and left end of the space domain
			optionStrike,//in the finmath library, there is written that this is the center of the space discretization, but this is not true: it has no effect
			theta,
			initialValue,//the center of the discretization in the class FDMThetaMethod is initialValue*exp(r*timeHorizon)
			riskFreeRate,
			volatility);
	
	double tolerance = 0.01;
	
	/**
	 * This method prints the prices of a call option for different initial values of the underlying, and checks
	 * if these are close to the analytic ones. This is done via assertArrayEquals, which causes a failure if, at least
	 * for an index i, there is a difference between the i-th elements of the array of analytic prices and of the prices we
	 * compute higher than tolerance.
	 */
	@Test
	void testCall() {
		
		//this is used to print only the first digits of the values
		DecimalFormat formatterValue = new DecimalFormat("#.##");
		formatterValue.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		
		//the object representing the option (like the classes implementing AbstractMonteCarloProduct)
		FiniteDifference1DProduct callOption = new FDMEuropeanCallOption(optionMaturity, optionStrike);
		/* 
		 * We don't really have to know that to call the method, but anyway: this method calls a method with the same name
		 * (but different argument list) defined in FiniteDifference1DModel. There, the boundary conditions are given according to
		 * the option: note that  FDMEuropeanCallOption has its own implementation of the methods giving the boundary
		 * conditions. 
		 */
		double[][] returnedValuesForCallOption = callOption.getValue(0.0, model);
		
		//these are the space variables (i.e., the initial values) for which the prices are computed
		double[] initialStockPricesForCall = returnedValuesForCallOption[0];
		
		//these are the corresponding prices
		double[] computedCallOptionValues = returnedValuesForCallOption[1];
		
		int numberOfPrices = computedCallOptionValues.length;
		
		//calculation of the analytic prices
		double[] analyticCallOptionValues = new double[numberOfPrices];
		for (int i =0; i < numberOfPrices; i++) {
			analyticCallOptionValues[i] = AnalyticFormulas.blackScholesOptionValue(initialStockPricesForCall[i], riskFreeRate,
					volatility, optionMaturity, optionStrike, true);
		}
		//there causes a failure if |computedCallOptionValues[i]-analyticCallOptionValues[i]|>tolerance for at least an index i
		Assert.assertArrayEquals(computedCallOptionValues, analyticCallOptionValues, tolerance);
	
		System.out.println("Call option:");
		System.out.println();
		
		for (int i =0; i < numberOfPrices; i++) {
			System.out.println("Initial value: " + formatterValue.format(initialStockPricesForCall[i])
				+ "; analytic price: " + formatterValue.format(analyticCallOptionValues[i])	+
					";  computed price: " + formatterValue.format(computedCallOptionValues[i]));
		}
		
	}
	
	/**
	 * This method prints the prices of a put option for different initial values of the underlying, and checks
	 * if these are close to the analytic ones. This is done via assertArrayEquals, which causes a failure if, at least
	 * for an index i, there is a difference between the i-th elements of the array of analytic prices and of the prices we
	 * compute higher than tolerance.
	 */
	@Test
	void testPut() {
		//this is used to print only the first digits of the values
		DecimalFormat formatterValue = new DecimalFormat("#.###");
		formatterValue.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		
		//the object representing the option (like the classes implementing AbstractMonteCarloProduct)
		FiniteDifference1DProduct putOption = new FDMEuropeanPutOption(optionMaturity, optionStrike);
		
		/* 
		 * We don't really have to know that to call the method, but anyway: this method calls a method with the same name
		 * (but different argument list) defined in FiniteDifference1DModel. There, the boundary conditions are given according to
		 * the option: note that  FDMEuropeanCallOption has its own implementation of the methods giving the boundary
		 * conditions. 
		 */
		double[][] returnedValuesForPutOption = putOption.getValue(0.0, model);
		
		//there are the space variables (i.e., the initial values) for which the prices are computed
		double[] initialStockPricesForPut = returnedValuesForPutOption[0];
		
		//these are the corresponding prices
		double[] computedPutOptionValues = returnedValuesForPutOption[1];
		
		int numberOfPrices = computedPutOptionValues.length;

		
		//calculation of the analytic prices
		double[] analyticPutOptionValues = new double[computedPutOptionValues.length];
		for (int i =0; i < analyticPutOptionValues.length; i++) {
			analyticPutOptionValues[i] = AnalyticFormulas.blackScholesOptionValue(initialStockPricesForPut[i], riskFreeRate,
					volatility, optionMaturity, optionStrike, false);
		}
		
		//there causes a failure if |computedPutOptionValues[i]-analyticPutOptionValues[i]|>tolerance for at least an index i
		Assert.assertArrayEquals(computedPutOptionValues, analyticPutOptionValues, tolerance);
		
		System.out.println("Put option:");
		System.out.println();
		
		for (int i =0; i < numberOfPrices; i++) {
			System.out.println("Initial value: " + formatterValue.format(initialStockPricesForPut[i])
				+ "; analytic price: " + formatterValue.format(analyticPutOptionValues[i])	+
					";  computed price: " + formatterValue.format(computedPutOptionValues[i]));
		}
		
		System.out.println();
		System.out.println();
	}

}
