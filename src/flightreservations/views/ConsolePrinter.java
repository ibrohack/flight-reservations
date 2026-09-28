package flightreservations.views;

import java.io.PrintStream;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import flightreservations.model.Airline;
import flightreservations.model.Customer;
import flightreservations.model.Flight;

/**
 * Writes everything the user sees on the console: the menu, tables of data
 * and result messages. Keeping the formatting here keeps the models and the
 * menu logic free of presentation details.
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
public class ConsolePrinter {
    private static final String BANNER =
            "▄─▄ ▄   ─▄─ ▄─▄ ▄ ▄ ▄─▄─▄      ▄─▄ ▄─▄ ▄─▄ ▄─▄ ▄─▄ ▄ ▄ ▄─▄ ▄─▄─▄ ─▄─ ▄─▄ ▄─▄ ▄─▄\n" +
            "▓─  ▓    ▓  ▓─▄ ▓─▓   ▓        ▓─▀ ▓─  ▀─▄ ▓─  ▓─▀ ▓ ▓ ▓─▓   ▓    ▓  ▓ ▓ ▓ ▓ ▀─▄\n" +
            "▀   ▀─▀ ─▀─ ▀─▀ ▀ ▀   ▀        ▀ ▀ ▀─▀ ▀─▀ ▀─▀ ▀ ▀  ▀  ▀ ▀   ▀   ─▀─ ▀─▀ ▀ ▀ ▀─▀\n";
    private static final String SEPARATOR =
            "────────────────────────────────────────────────────────────────────────────────";
    private static final String MENU_ROW = "%d. %s%n";
    private static final String CHOICE_ROW = "  %d) %s%n";
    private static final String AIRLINE_ROW = "%-4s %-25s %-20s %s%n";
    private static final String CUSTOMER_ROW = "%-4s %-22s %-32s %-18s %s%n";
    private static final String FLIGHT_ROW = "%-4s %-12s %-12s %-10s %-8s %5s  %s%n";
    private static final String SUCCESS_PREFIX = "[OK] ";
    private static final String WARNING_PREFIX = "[!] ";
    private static final String ERROR_PREFIX = "[ERROR] ";
    private static final String PROMPT_SUFFIX = ": ";

    private final PrintStream out;

    /**
     * Creates a printer.
     *
     * @param out the stream to write to, usually {@code System.out}
     */
    public ConsolePrinter(PrintStream out) {
        this.out = Objects.requireNonNull(out);
    }

    /** Prints the application banner. */
    public void printBanner() {
        out.println(BANNER);
    }

    /**
     * Prints the main menu, with a separator line between sections.
     *
     * @param options the options to show, in order
     */
    public void printMenu(MenuOption[] options) {
        MenuOption.Section currentSection = null;
        for (MenuOption option : options) {
            if (option.getSection() != currentSection) {
                out.println(SEPARATOR);
                currentSection = option.getSection();
            }
            out.printf(MENU_ROW, option.getNumber(), option.getLabel());
        }
        out.println(SEPARATOR);
    }

    /**
     * Prints a numbered list of choices, starting at 1.
     *
     * @param choices the choices to show
     */
    public void printChoices(Object[] choices) {
        for (int index = 0; index < choices.length; index++) {
            out.printf(CHOICE_ROW, index + 1, choices[index]);
        }
    }

    /**
     * Prints the title of a use case.
     *
     * @param title the title
     */
    public void printTitle(String title) {
        out.println();
        out.println("== " + title + " ==");
    }

    /**
     * Prints a prompt without a line break, so the user types after it.
     *
     * @param prompt the text asking for a value
     */
    public void printPrompt(String prompt) {
        out.print(prompt + PROMPT_SUFFIX);
    }

    /**
     * Prints a table of airlines.
     *
     * @param airlines the airlines to show
     */
    public void printAirlines(List<Airline> airlines) {
        printTable(AIRLINE_ROW, new Object[] {"ID", "Name", "Country", "IATA"}, airlines,
                airline -> new Object[] {airline.getAirlineId(), airline.getAirlineName(),
                        airline.getCountry(), airline.getIataCode()},
                "There are no airlines yet.");
    }

    /**
     * Prints a table of customers.
     *
     * @param customers the customers to show
     */
    public void printCustomers(List<Customer> customers) {
        printTable(CUSTOMER_ROW, new Object[] {"ID", "Name", "Email", "Phone", "Bookings file"}, customers,
                customer -> new Object[] {customer.getCustomerId(), customer.getCustomerName(),
                        customer.getEmail(), customer.getPhoneNumber(), customer.getPath()},
                "There are no customers yet.");
    }

    /**
     * Prints a table of flights.
     *
     * @param flights      the flights to show
     * @param emptyMessage the message shown when there are no flights
     */
    public void printFlights(List<Flight> flights, String emptyMessage) {
        printTable(FLIGHT_ROW, new Object[] {"ID", "Origin", "Destination", "Date", "Class", "Seats", "Airline"},
                flights,
                flight -> new Object[] {flight.getFlightId(), flight.getOrigin(), flight.getDestination(),
                        flight.getDepartureDate(), flight.getTravelClass(), flight.getSeatAmount(),
                        flight.getAirline().getAirlineName() + " (" + flight.getAirline().getIataCode() + ")"},
                emptyMessage);
    }

    /**
     * Prints a message about an operation that worked.
     *
     * @param message the message
     */
    public void printSuccess(String message) {
        out.println(SUCCESS_PREFIX + message);
    }

    /**
     * Prints a message about something the user can fix.
     *
     * @param message the message
     */
    public void printWarning(String message) {
        out.println(WARNING_PREFIX + message);
    }

    /**
     * Prints a message about a system failure.
     *
     * @param message the message
     */
    public void printError(String message) {
        out.println(ERROR_PREFIX + message);
    }

    /**
     * Prints a plain message.
     *
     * @param message the message
     */
    public void printMessage(String message) {
        out.println(message);
    }

    /**
     * Prints rows as an aligned table with a header, or a message if there
     * are no rows.
     *
     * @param <T>          the type of the rows
     * @param rowFormat    the {@link String#format} pattern of one row
     * @param headers      the column headers
     * @param rows         the rows to show
     * @param columns      extracts the column values of a row
     * @param emptyMessage the message shown when there are no rows
     */
    private <T> void printTable(String rowFormat, Object[] headers, List<T> rows,
            Function<T, Object[]> columns, String emptyMessage) {
        if (rows.isEmpty()) {
            out.println(emptyMessage);
            return;
        }
        out.printf(rowFormat, headers);
        for (T row : rows) {
            out.printf(rowFormat, columns.apply(row));
        }
    }
}
