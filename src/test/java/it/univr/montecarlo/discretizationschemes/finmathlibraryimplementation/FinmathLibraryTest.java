

package it.univr.montecarlo.discretizationschemes.finmathlibraryimplementation;



import net.finmath.exception.CalculationException;

import net.finmath.functions.AnalyticFormulas;

import net.finmath.montecarlo.BrownianMotionFromMersenneRandomNumbers;

import net.finmath.montecarlo.assetderivativevaluation.MonteCarloAssetModel;

import net.finmath.montecarlo.assetderivativevaluation.models.BachelierModel;

import net.finmath.montecarlo.assetderivativevaluation.products.EuropeanOption;

import net.finmath.stochastic.RandomVariable;

import net.finmath.time.TimeDiscretizationFromArray;



/**

 * In this class we test the Finmath library implementation of the Monte
Carlo method and discretization of a stochastic

 * process for the evaluation of an European call option. We want the underlying to be a
Bachelier model and use a classic

 * Euler-Maruyama scheme.

 *

 * @author Andrea Mazzon

 *

 */

public class FinmathLibraryTest {


	public static void main(String[] args) throws CalculationException {


		//parameters for the option


		double maturity = 1.0;

		double strike = 2.0;


		//parameters for the model (i.e., for the SDE)


		double initialValue = 2.0;

		double riskFreeRate = 0.0;

		double volatility = 0.2;


		//we compute and print the analytic value


		double analyticValue = AnalyticFormulas.bachelierOptionValue(initialValue, volatility, maturity, strike, 1.0);

		System.out.println("The analytic value of the option is " + analyticValue);


		EuropeanOption ourOption = new EuropeanOption(maturity, strike);


		//creazione modello

		BachelierModel ourModel = new BachelierModel(initialValue, riskFreeRate, volatility);


		//creazione della simulazione e discretizzazione del processo

		int numberOfFactors = 1;

		int numberOfSimulations = 100000;

		int seed = 1897;

		//creazione discretizzazione temporale


		double initialTime = 0.0;

		double timeStep = 0.05;

		int numberOfIntervals = (int) (maturity / timeStep);


		TimeDiscretizationFromArray ourTimeDiscretization = 

				new TimeDiscretizationFromArray(initialTime, numberOfIntervals, timeStep);


		BrownianMotionFromMersenneRandomNumbers ourStochasticDriver = 

				new BrownianMotionFromMersenneRandomNumbers(ourTimeDiscretization, numberOfFactors, numberOfSimulations, seed);


		MonteCarloAssetModel ourSimulatedUnderlying = new MonteCarloAssetModel(ourModel, ourStochasticDriver);


		//adesso possiamo calcolare il valore approssimato dell'opzione


		RandomVariable ourPayoff = ourOption.getValue(0.0, ourSimulatedUnderlying);


		double ourPrice = ourPayoff.getAverage();


		System.out.println("The Monte-Carlo value of the option is " + ourPrice);


		/*
		 * Modo alternativo e più diretto: il metodo ourOption.getValue(0.0, ourSimulatedUnderlying)
		 * viene chiamato nell'implementazione della Finmath library, e viene presa direttamente la average
		 */

		double ourDirectPrice =  ourOption.getValue(ourSimulatedUnderlying);


	}

}

