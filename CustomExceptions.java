package BankingSystemApp;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.math.BigDecimal;

public class CustomExceptions {
    public static class UserNotFoundException extends Exception {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public static int getIntCheckException(Scanner scanner) {
        while (true) {
            try {
                int option = scanner.nextInt();
                return option;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer number.");
                scanner.nextLine(); // Clear the invalid input
                continue; // Skip to the next iteration
            }
        }
    }

    public static double getDoubleCheckException(Scanner scanner) {
        while (true) {
            try {
                double option = scanner.nextDouble();
                return option;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine();
                continue;
            }
        }
    }

    public static BigDecimal getDecimalCheckException(Scanner scanner) {
        while (true) {
            try {
                String inputString = scanner.next();
                BigDecimal option = new BigDecimal(inputString);
                return option;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine();
                continue;
            }
        }
    }

    public static class CurrencyNotSupportedException extends Exception {
        public CurrencyNotSupportedException(String message) {
            super(message);
        }
    }

}
