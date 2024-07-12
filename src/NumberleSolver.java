import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.swing.JFrame;

public class NumberleSolver {
    private static  int NUMBER_LENGTH = 250000;
    private static Set<Integer> excludedDigits;
    private static int[] finalNumber;
    private static List<Integer> foundInWrongSpot;
    private static Set<Integer>[] notHere;

    private static Map<Double, Double> avgGussesPerDigit = new HashMap<>();

    public static void main(String[] args) {
        // regularTest();
        testLengthsEffect();
    }

    public static void regularTest() {

        int numTrials = 5;
        int totalGuesses = 0;
        int maxGuesses = 0;
        int minGuesses = Integer.MAX_VALUE;

        long startTime = System.nanoTime();

        for (int trial = 0; trial < numTrials; trial++) {
            resetGameState();
            Numberle game = new Numberle(NUMBER_LENGTH);

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

        double averageGuesses = (double) totalGuesses / numTrials;
        System.out.println("Average guesses: " + averageGuesses);
        System.out.println("Maximum guesses: " + maxGuesses);
        System.out.println("Minimum guesses: " + minGuesses);

        avgGussesPerDigit.put(Double.valueOf(NUMBER_LENGTH), averageGuesses);
    }

    /**
     * Tests the effect of changing the length of the number
     * @apiNote notice: this function overwrites "NUMBER_LENGTH" initial value
     */
    public static void testLengthsEffect() {
        for (int i = 1; i <= 100; i++) {
            NUMBER_LENGTH = i;

            int numTrials = 100;
            int totalGuesses = 0;
            int maxGuesses = 0;
            int minGuesses = Integer.MAX_VALUE;

            long startTime = System.nanoTime();

            for (int trial = 0; trial < numTrials; trial++) {
                resetGameState();
                Numberle game = new Numberle(NUMBER_LENGTH);

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

            double averageGuesses = (double) totalGuesses / numTrials;
            System.out.println("Average guesses: " + averageGuesses);
            System.out.println("Maximum guesses: " + maxGuesses);
            System.out.println("Minimum guesses: " + minGuesses);

            avgGussesPerDigit.put(Double.valueOf(NUMBER_LENGTH), averageGuesses);
        }
        // Create JFrame and add graph panel
        GraphPanel graphPanel = new GraphPanel(avgGussesPerDigit);
        JFrame frame = new JFrame("Graph Panel");
        frame.add(graphPanel);
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    private static void resetGameState() {
        excludedDigits = new HashSet<>();
        finalNumber = new int[NUMBER_LENGTH];
        foundInWrongSpot = new ArrayList<>();
        notHere = new HashSet[NUMBER_LENGTH];
        for (int i = 0; i < notHere.length; i++) {
            notHere[i] = new HashSet<>();
        }
    }

    private static int[] generateGuess() {
        Random rand = new Random();
        int[] guess = new int[NUMBER_LENGTH];
        for (int i = 0; i < NUMBER_LENGTH; i++) {
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
