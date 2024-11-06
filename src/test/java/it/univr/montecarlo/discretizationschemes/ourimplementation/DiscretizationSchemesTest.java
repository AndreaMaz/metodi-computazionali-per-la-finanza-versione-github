package it.univr.montecarlo.discretizationschemes.ourimplementation;

import java.util.Random;

import it.univr.usefulmethodsarrays.UsefulMethodsForArrays;
import net.finmath.stochastic.RandomVariable;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;


public class DiscretizationSchemesTest {

	
	public static void main(String[] args) {

		double initialValue = 100.0;
		double volatility = 0.3;
		double muDrift = 0.0;
		
		double finalTime = 1.0;
		double timeStep = 0.05;
		int numberOfTimesSteps = (int) (finalTime/timeStep);

		TimeDiscretization times = new TimeDiscretizationFromArray(0.0, numberOfTimesSteps, timeStep);

		//one time step it is enough: we simulate the exact solution!
		TimeDiscretization timesForLogarithm = new TimeDiscretizationFromArray(0.0, 1, finalTime);

		//this is what we save from the time discretization for the simulation of the logarithm: we use it for the simulations
		int ratioBetweenNumberOfTimeSteps = times.getNumberOfTimeSteps()/timesForLogarithm.getNumberOfTimeSteps();
		
		int numberOfSimulatedPaths = 10000;
		
		//we "use the gain" we get by simulating less times to simulate more paths
		int numberOfSimulatedPathsForLogarithm = numberOfSimulatedPaths*ratioBetweenNumberOfTimeSteps;
		
		int numberOfTests = 100;

		double[] errorsEulerMaruyama = new double[numberOfTests];
		double[] errorsMilstein = new double[numberOfTests];
		double[] errorsEulerMaruyamaForLogarithm = new double[numberOfTests];
	
		Random seedGenerator = new Random();
		
		for (int i = 0; i<numberOfTests; i++) {

			//for every test, we look at the expectation with a possibly different seed
			int seed = seedGenerator.nextInt();

			AbstractProcessSimulation simulatorEulerMaruyama = new EulerSchemeForBlackScholes(volatility, muDrift, initialValue,
					numberOfSimulatedPaths, seed, times);

			AbstractProcessSimulation simulatorMilstein = new MilsteinSchemeForBlackScholes(volatility, muDrift, initialValue,
					numberOfSimulatedPaths, seed, times);

			AbstractProcessSimulation simulatorLogEuler = new LogEulerSchemeForBlackScholes(volatility, muDrift, initialValue,
					numberOfSimulatedPathsForLogarithm, seed, timesForLogarithm);

			RandomVariable processvalueAtFinalTimeForEulerMaruyama = simulatorEulerMaruyama.getFinalValue();
			RandomVariable processvalueAtFinalTimeForMilstein = simulatorMilstein.getFinalValue();
			RandomVariable processvalueAtFinalTimeForLogEuler = simulatorLogEuler.getFinalValue();
			
			double averageAtFinalTimeForEulerMaruyama = processvalueAtFinalTimeForEulerMaruyama.getAverage();
			double averageAtFinalTimeForMilstein = processvalueAtFinalTimeForMilstein.getAverage();
			double averageAtFinalTimeForLogEuler = processvalueAtFinalTimeForLogEuler.getAverage();

			
			//modificheremo le prossime tre righe insieme
			errorsEulerMaruyama[i] = Math.abs(averageAtFinalTimeForEulerMaruyama-initialValue); 
			errorsMilstein[i] = Math.abs(averageAtFinalTimeForMilstein-initialValue); 
			errorsEulerMaruyamaForLogarithm[i] = Math.abs(averageAtFinalTimeForLogEuler-initialValue);		
		}

		System.out.println("Average error Euler Maruyama: = " + UsefulMethodsForArrays.getAverage(errorsEulerMaruyama));
		System.out.println("Average error Milstein: = " + UsefulMethodsForArrays.getAverage(errorsMilstein));
		System.out.println("Average error Log Euler Maruyama: = " + UsefulMethodsForArrays.getAverage(errorsEulerMaruyamaForLogarithm));
	}
}
