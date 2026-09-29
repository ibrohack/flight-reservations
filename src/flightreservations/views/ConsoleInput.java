package flightreservations.views;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import flightreservations.exception.InputClosedException;
import flightreservations.exception.ValidationException;

/**
 * Reads values typed by the user on the console.
 * <p>
 * Every read method asks again until the value is valid: number and date
 * format errors, and the rules passed as a {@link Validator}, are handled
 * here by showing the reason and prompting again. So a single wrong field
 * never makes the user retype the whole form.
 * </p>
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
public class ConsoleInput {

    /**
     * A validation rule for a text typed by the user, for example
     * {@code InputValidator::validateEmail}.
     */
    public interface Validator {
        /**
         * Checks a text.
         *
         * @param text the text to check
         * @return the text, possibly normalized
         * @throws ValidationException if the text is not valid
         */
        String validate(String text) throws ValidationException;
    }

    private final Scanner scanner;
    private final ConsolePrinter printer;

    /**
     * Creates the console input.
     *
     * @param scanner the scanner to read lines from
     * @param printer the printer used for prompts and error messages
     */
    public ConsoleInput(Scanner scanner, ConsolePrinter printer) {
        this.scanner = scanner;
        this.printer = printer;
    }

    /**
     * Reads a text value that must pass a validation rule.
     *
     * @param prompt    the text asking for the value
     * @param validator the rule the value must pass
     * @return the validated (and normalized) text
     * @throws InputClosedException if the standard input is closed
     */
    public String readText(String prompt, Validator validator) {
        while (true) {
            String text = readLine(prompt);
            try {
                return validator.validate(text);
            } catch (ValidationException e) {
                printer.printWarning(e.getMessage());
            }
        }
    }

    /**
     * Reads a whole number within a range.
     *
     * @param prompt the text asking for the value
     * @param min    the lowest accepted number
     * @param max    the highest accepted number
     * @return the number
     * @throws InputClosedException if the standard input is closed
     */
    public int readInt(String prompt, int min, int max) {
        return readNumber(prompt, min, max, "Enter a whole number from " + min + " to " + max + ".");
    }

    /**
     * Reads the ID of an entity: a positive whole number.
     *
     * @param prompt the text asking for the value
     * @return the ID
     * @throws InputClosedException if the standard input is closed
     */
    public int readId(String prompt) {
        return readNumber(prompt, 1, Integer.MAX_VALUE, "Enter a positive whole number (an ID from the list).");
    }

    /**
     * Reads a date in ISO format ({@code yyyy-MM-dd}).
     *
     * @param prompt the text asking for the value
     * @return the date
     * @throws InputClosedException if the standard input is closed
     */
    public LocalDate readDate(String prompt) {
        while (true) {
            String text = readLine(prompt);
            try {
                return LocalDate.parse(text.trim());
            } catch (DateTimeParseException e) {
                printer.printWarning("Enter a valid date as yyyy-MM-dd (for example 2027-03-15).");
            }
        }
    }

    /**
     * Prompts until the user types a whole number within a range.
     *
     * @param prompt  the text asking for the value
     * @param min     the lowest accepted number
     * @param max     the highest accepted number
     * @param message the message shown if the text is not valid
     * @return the number
     * @throws InputClosedException if the standard input is closed
     */
    private int readNumber(String prompt, int min, int max, String message) {
        while (true) {
            String text = readLine(prompt);
            try {
                int number = Integer.parseInt(text.trim());
                if (number >= min && number <= max) {
                    return number;
                }
            } catch (NumberFormatException e) {
                // Not a number: the message below is shown.
            }
            printer.printWarning(message);
        }
    }

    /**
     * Shows a prompt and reads one line.
     *
     * @param prompt the text asking for the value
     * @return the typed line
     * @throws InputClosedException if the standard input is closed
     */
    private String readLine(String prompt) {
        printer.printPrompt(prompt);
        try {
            return scanner.nextLine();
        } catch (NoSuchElementException e) {
            throw new InputClosedException(e);
        }
    }
}
