package flightreservations.views;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;
import java.util.Objects;
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
    private static final int FIRST_CHOICE = 1;
    private static final int MIN_ID = 1;
    private static final String DATE_FORMAT_MESSAGE = "Enter a valid date as yyyy-MM-dd (for example 2027-03-15).";
    private static final String ID_MESSAGE = "Enter a positive whole number (an ID from the list).";
    private static final String NUMBER_MESSAGE = "Enter a whole number.";

    /**
     * A validation rule for a value typed by the user.
     *
     * @param <T> the type of the value
     */
    @FunctionalInterface
    public interface Validator<T> {
        /**
         * Checks a value.
         *
         * @param value the value to check
         * @return the value, possibly normalized
         * @throws ValidationException if the value is not valid
         */
        T validate(T value) throws ValidationException;
    }

    /**
     * Converts the typed text into a value.
     *
     * @param <T> the type of the value
     */
    @FunctionalInterface
    private interface Parser<T> {
        /**
         * Converts a text.
         *
         * @param text the typed text
         * @return the converted value
         * @throws ValidationException if the text is not valid
         */
        T parse(String text) throws ValidationException;
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
        this.scanner = Objects.requireNonNull(scanner);
        this.printer = Objects.requireNonNull(printer);
    }

    /**
     * Reads a text value that must pass a validation rule.
     *
     * @param prompt    the text asking for the value
     * @param validator the rule the value must pass
     * @return the validated (and normalized) text
     * @throws InputClosedException if the standard input is closed
     */
    public String readText(String prompt, Validator<String> validator) {
        return readUntilValid(prompt, validator::validate);
    }

    /**
     * Reads any whole number.
     *
     * @param prompt the text asking for the value
     * @return the number
     * @throws InputClosedException if the standard input is closed
     */
    public int readInt(String prompt) {
        return readUntilValid(prompt, text -> parseInt(text, Integer.MIN_VALUE, Integer.MAX_VALUE, NUMBER_MESSAGE));
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
        String message = "Enter a whole number from " + min + " to " + max + ".";
        return readUntilValid(prompt, text -> parseInt(text, min, max, message));
    }

    /**
     * Reads the ID of an entity: a positive whole number.
     *
     * @param prompt the text asking for the value
     * @return the ID
     * @throws InputClosedException if the standard input is closed
     */
    public int readId(String prompt) {
        return readUntilValid(prompt, text -> parseInt(text, MIN_ID, Integer.MAX_VALUE, ID_MESSAGE));
    }

    /**
     * Reads a date in ISO format ({@code yyyy-MM-dd}) that must pass a
     * validation rule.
     *
     * @param prompt    the text asking for the value
     * @param validator the rule the date must pass
     * @return the validated date
     * @throws InputClosedException if the standard input is closed
     */
    public LocalDate readDate(String prompt, Validator<LocalDate> validator) {
        return readUntilValid(prompt, text -> validator.validate(parseDate(text)));
    }

    /**
     * Shows a numbered list of choices and reads the chosen one.
     *
     * @param <E>     the enum type of the choices
     * @param prompt  the text asking for the choice
     * @param choices the available choices
     * @return the chosen value
     * @throws InputClosedException if the standard input is closed
     */
    public <E extends Enum<E>> E readChoice(String prompt, E[] choices) {
        printer.printChoices(choices);
        return choices[readInt(prompt, FIRST_CHOICE, choices.length) - FIRST_CHOICE];
    }

    /**
     * Prompts until the typed text can be converted into a valid value.
     *
     * @param <T>    the type of the value
     * @param prompt the text asking for the value
     * @param parser converts and validates the typed text
     * @return the valid value
     */
    private <T> T readUntilValid(String prompt, Parser<T> parser) {
        while (true) {
            String text = readLine(prompt);
            try {
                return parser.parse(text);
            } catch (ValidationException e) {
                printer.printWarning(e.getMessage());
            }
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

    /**
     * Converts a text into a whole number within a range.
     *
     * @param text    the typed text
     * @param min     the lowest accepted number
     * @param max     the highest accepted number
     * @param message the message shown if the text is not valid
     * @return the number
     * @throws ValidationException if the text is not a number in the range
     */
    private static int parseInt(String text, int min, int max, String message) throws ValidationException {
        int number;
        try {
            number = Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException(message, e);
        }
        if (number < min || number > max) {
            throw new ValidationException(message);
        }
        return number;
    }

    /**
     * Converts a text in {@code yyyy-MM-dd} format into a date.
     *
     * @param text the typed text
     * @return the date
     * @throws ValidationException if the text is not a valid date
     */
    private static LocalDate parseDate(String text) throws ValidationException {
        try {
            return LocalDate.parse(text.trim());
        } catch (DateTimeParseException e) {
            throw new ValidationException(DATE_FORMAT_MESSAGE, e);
        }
    }
}
