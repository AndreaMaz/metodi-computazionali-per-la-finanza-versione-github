package it.univr.montecarlo.numbersgeneration;

import java.util.Arrays;

/**
 * This class contains one main method that calls method of the class LinearCongruentialGenerator.
 *
 * @author Andrea Mazzon
 *
 */

public class PseudoRandomNumbersTesting {
    public static void main(String[] args) {

        long firstSeed = 1897;// the seed is the first entry of the sequence of pseudo random numbers

        int numberOfPseudoRandomNumbers = 5;

        LinearCongruentialGenerator firstGenerator = new LinearCongruentialGenerator(numberOfPseudoRandomNumbers,
                firstSeed);

        long[] sequenceGeneratedByTheFirstObject = firstGenerator.getRandomNumberSequence();

        System.out.println("Simulation of " + numberOfPseudoRandomNumbers + " integers with seed " + firstSeed
                + " : " + Arrays.toString(sequenceGeneratedByTheFirstObject));

        System.out.println();

        System.out.println("First four number of the random sequence, excluded the seed:");

        // Maybe the user is not interested to have all the sequence, but only in the first numbers
        for (int i = 0; i < numberOfPseudoRandomNumbers; i++) {
            System.out.println(firstGenerator.getNextInteger());
        }

        System.out.println();

        LinearCongruentialGenerator secondGenerator = new LinearCongruentialGenerator(numberOfPseudoRandomNumbers);

        long[] sequenceGeneratedByTheSecondObject = secondGenerator.getRandomNumberSequence();

        System.out.println("Simulation of " + numberOfPseudoRandomNumbers + " integers with random seed "
                + " : " + Arrays.toString(sequenceGeneratedByTheSecondObject));


        System.out.println();
        long maxLong = Long.MAX_VALUE;
        System.out.println("Max long = " + maxLong);
        long maxLongPlusOne = maxLong + 1;
        System.out.println("Max long plus one = " + maxLongPlusOne);
        long minLong = Long.MIN_VALUE;
        System.out.println("Min long = " + minLong);
        long maxLongMinusOne = minLong - 1;
        System.out.println("Max long plus one = " + maxLongMinusOne);

    }
}
