import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JFrame;

public class NumberleSolver {
    private static int numberLength = 5;
    private static Set<Integer> excludedDigits;
    private static int[] finalNumber;
    private static List<Integer> foundInWrongSpot;
    private static Set<Integer>[] notHere;

    // Constants for test configurations
    private static final int REGULAR_TEST_NUM_TRIALS = 5;
    private static final int LENGTH_EFFECT_NUM_TRIALS = 100;
    private static final int LENGTH_EFFECT_MAX_LENGTH = 100;
    private static final int LENGTH_EFFECT_NUM_ITERATIONS = 3;



    public static void main(String[] args) {

        System.out.println("enter 1 for regular test, 2 for length effect test");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        if (choice == 1) {
            regularTest();
        } else if (choice == 2) {
            testLengthsEffect();
        }

        scanner.close();
    }

    public static void regularTest() {
        int totalGuesses = 0;
        int maxGuesses = 0;
        int minGuesses = Integer.MAX_VALUE;

        long startTime = System.nanoTime();

        for (int trial = 0; trial < REGULAR_TEST_NUM_TRIALS; trial++) {
            resetGameState();
            Numberle game = new Numberle(numberLength);

            int numGuesses = 0;
            while (true) {
                int[] guess = generateGuess(); // Your algorithm generates a guess
                numGuesses++;
                boolean correct = game.makeGuess(guess);

                if (correct) {
                    totalGuesses += numGuesses;
                    if (numGuesses > maxGuesses) {
                        maxGuesses = numGuesses;
                    }
                    if (numGuesses < minGuesses) {
                        minGuesses = numGuesses;
                    }
                    break;
                } else {
                    int[] feedback = game.checkGuess(guess);
                    processFeedback(feedback, guess); // Your algorithm processes the feedback
                }
            }
        }

        long endTime = System.nanoTime();
        double durationInSeconds = ((endTime - startTime) / 1_000_000_000.0);

        System.out.println("Execution time: " + durationInSeconds + " seconds");

        double averageGuesses = (double) totalGuesses / REGULAR_TEST_NUM_TRIALS;
        System.out.println("number of digits: " + numberLength);
        System.out.println("Average guesses: " + averageGuesses);
        System.out.println("Maximum guesses: " + maxGuesses);
        System.out.println("Minimum guesses: " + minGuesses);
    }

    /**
     * Tests the effect of changing the length of the number
     * 
     * @apiNote notice: this function overwrites "NUMBER_LENGTH" initial value
     */
    public static void testLengthsEffect() {
        List<Map<Double, Double>> dataPoints = new ArrayList<>();

        for (int iteration = 0; iteration < LENGTH_EFFECT_NUM_ITERATIONS; iteration++) {
            Map<Double, Double> avgGuessesPerDigit = new HashMap<>();

            for (int length = 1; length <= LENGTH_EFFECT_MAX_LENGTH; length++) {
                numberLength = length;

                int totalGuesses = 0;
                int maxGuesses = 0;
                int minGuesses = Integer.MAX_VALUE;

                long startTime = System.nanoTime();

                for (int trial = 0; trial < LENGTH_EFFECT_NUM_TRIALS; trial++) {
                    resetGameState();
                    Numberle game = new Numberle(numberLength);

                    int numGuesses = 0;
                    while (true) {
                        int[] guess = generateGuess(); // Your algorithm generates a guess
                        numGuesses++;
                        boolean correct = game.makeGuess(guess);

                        if (correct) {
                            totalGuesses += numGuesses;
                            if (numGuesses > maxGuesses) {
                                maxGuesses = numGuesses;
                            }
                            if (numGuesses < minGuesses) {
                                minGuesses = numGuesses;
                            }
                            break;
                        } else {
                            int[] feedback = game.checkGuess(guess);
                            processFeedback(feedback, guess); // Your algorithm processes the feedback
                        }
                    }
                }

                long endTime = System.nanoTime();
                double durationInSeconds = ((endTime - startTime) / 1_000_000_000.0);

                System.out.println("Execution time: " + durationInSeconds + " seconds");

                double averageGuesses = (double) totalGuesses / LENGTH_EFFECT_NUM_TRIALS;
                System.out.println("number of digits: " + numberLength);
                System.out.println("Average guesses: " + averageGuesses);
                System.out.println("Maximum guesses: " + maxGuesses);
                System.out.println("Minimum guesses: " + minGuesses);

                avgGuessesPerDigit.put(Double.valueOf(numberLength), averageGuesses);
            }

            dataPoints.add(avgGuessesPerDigit);
        }

        // Create JFrame and add graph panel
        GraphPanel graphPanel = new GraphPanel(dataPoints);
        JFrame frame = new JFrame("Graph Panel");
        frame.add(graphPanel);
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    private static void resetGameState() {
        excludedDigits = new HashSet<>();
        finalNumber = new int[numberLength];
        foundInWrongSpot = new ArrayList<>();
        notHere = new HashSet[numberLength];
        for (int i = 0; i < notHere.length; i++) {
            notHere[i] = new HashSet<>();
        }
    }

    private static int[] generateGuess() {
        Random rand = new Random();
        int[] guess = new int[numberLength];
        for (int i = 0; i < numberLength; i++) {
            if (finalNumber[i] != 0) {
                guess[i] = finalNumber[i];
                continue;
            }

            boolean foundFromWrongSpot = false;
            for (int j = 0; j < foundInWrongSpot.size(); j++) {
                if (!excludedDigits.contains(foundInWrongSpot.get(j))
                        && !notHere[i].contains(foundInWrongSpot.get(j))) {
                    guess[i] = foundInWrongSpot.get(j);
                    foundInWrongSpot.remove(j);
                    foundFromWrongSpot = true;
                    break;
                }
            }

            if (!foundFromWrongSpot) {
                do {
                    guess[i] = rand.nextInt(10);
                } while (excludedDigits.contains(guess[i]) || notHere[i].contains(guess[i]));
            }
        }
        return guess;
    }

    private static void processFeedback(int[] feedback, int[] guess) {
        for (int i = 0; i < feedback.length; i++) {
            if (feedback[i] == 0) {
                boolean foundInOtherPosition = false;

                for (int j = 0; j < feedback.length; j++) {
                    if (i != j && guess[i] == guess[j] && (feedback[j] == 1 || feedback[j] == 2)) {
                        notHere[i].add(guess[i]);
                        foundInOtherPosition = true;
                        break;
                    }
                }

                if (!foundInOtherPosition) {
                    excludedDigits.add(guess[i]);
                }
            } else if (feedback[i] == 1) {
                notHere[i].add(guess[i]);
                if (!foundInWrongSpot.contains(guess[i])) {
                    foundInWrongSpot.add(guess[i]);
                }
            } else if (feedback[i] == 2) {
                finalNumber[i] = guess[i];
            }
        }
    }
}
