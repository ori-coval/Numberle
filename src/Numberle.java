import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Numberle {

    // Reset
    public static final String RESET = "\033[0m"; // Text Reset

    // Background
    public static final String GREEN_BACKGROUND = "\033[42m"; // GREEN
    public static final String YELLOW_BACKGROUND = "\033[43m"; // YELLOW

    private static int NUMBER_LENGTH = 5; // Define the constant for number length
    private int[] secretNumber;
    private int[][] attempts;
    private int currentTry;

    public Numberle(int length) {
        NUMBER_LENGTH = length;
        secretNumber = generateSecretNumber(NUMBER_LENGTH); // Use the constant for secret number length
        attempts = new int[100][NUMBER_LENGTH]; // Assuming a maximum of 100 attempts for initialization
        currentTry = 0;
    }

    private int[] generateSecretNumber(int length) {
        Random rand = new Random();
        int[] number = new int[length];
        for (int i = 0; i < length; i++) {
            number[i] = rand.nextInt(10);
        }
        return number;
    }

    public boolean makeGuess(int[] guess) {
        attempts[currentTry] = guess;
        currentTry++;
        return Arrays.equals(secretNumber, guess);
    }

    public int[] checkGuess(int[] guess) {
        int[] result = new int[NUMBER_LENGTH];
        boolean[] matched = new boolean[NUMBER_LENGTH];

        // First pass: Check for correct digits in correct positions (green)
        for (int i = 0; i < NUMBER_LENGTH; i++) {
            if (guess[i] == secretNumber[i]) {
                result[i] = 2;
                matched[i] = true;
            }
        }

        // Second pass: Check for correct digits in incorrect positions (yellow)
        for (int i = 0; i < NUMBER_LENGTH; i++) {
            if (result[i] == 2)
                continue; // Skip already matched digits
            for (int j = 0; j < NUMBER_LENGTH; j++) {
                if (!matched[j] && guess[i] == secretNumber[j]) {
                    result[i] = 1;
                    matched[j] = true;
                    break;
                }
            }
        }

        return result;
    }

    public int getCurrentTry() {
        return currentTry;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Numberle game = new Numberle(NUMBER_LENGTH);

        System.out.println("Welcome to Numberle!");
        System.out.println("Try to guess the " + NUMBER_LENGTH + "-digit number.");

        while (true) {

            String guessInput = "";
            do {
                System.out.print("Enter your guess: ");
                guessInput = scanner.nextLine();
            } while (guessInput.length() != NUMBER_LENGTH);
            
            int[] guess = new int[NUMBER_LENGTH];

            for (int i = 0; i < NUMBER_LENGTH; i++) {
                guess[i] = Character.getNumericValue(guessInput.charAt(i));
            }

            if (game.makeGuess(guess)) {
                System.out.println("Congratulations! You guessed the number!");
                System.out.println("It took you " + game.getCurrentTry() + " tries.");
                break;
            } else {
                int[] feedback = game.checkGuess(guess);

                for (int i = 0; i < NUMBER_LENGTH; i++) {
                    if (feedback[i] == 0) {
                        System.out.print(guess[i]);
                    }
                    if (feedback[i] == 1) {
                        System.out.print(YELLOW_BACKGROUND + guess[i] + RESET);
                    }
                    if (feedback[i] == 2) {
                        System.out.print(GREEN_BACKGROUND + guess[i] + RESET);
                    }
                }
                System.out.println("");
            }
        }

        scanner.close();
    }
}
