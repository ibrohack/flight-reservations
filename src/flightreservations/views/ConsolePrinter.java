package flightreservations.views;

import java.io.PrintStream;
import java.util.List;

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
    private static final String AIRLINE_ROW = "%-4s %-25s %-20s %s%n";
    private static final String CUSTOMER_ROW = "%-4s %-22s %-32s %-18s %s%n";
    private static final String FLIGHT_ROW = "%-4s %-12s %-12s %-10s %-8s %5s  %s%n";

    private final PrintStream out;

    /**
     * Creates a printer.
     *
     * @param out the stream to write to, usually {@code System.out}
     */
    public ConsolePrinter(PrintStream out) {
        this.out = out;
    }

    /** Prints the application banner. */
    public void printBanner() {
        out.println(BANNER);
    }

    /** Prints the main menu, with a separator line between sections. */
    public void printMenu() {
        out.println(SEPARATOR);
        out.println("1. Register a customer");
        out.println("2. Check a customer's flights");
        out.println("3. View a customer's flight history");
        out.println(SEPARATOR);
        out.println("4. Register an airline");
        out.println("5. Register a flight");
        out.println("6. Check future flights");
        out.println("7. Book a flight");
        out.println(SEPARATOR);
        out.println("0. Exit");
        out.println(SEPARATOR);
    }

    /**
     * Prints a numbered list of choices, starting at 1.
     *
     * @param choices the choices to show
     */
    public void printChoices(Object[] choices) {
        for (int index = 0; index < choices.length; index++) {
            out.println("  " + (index + 1) + ") " + choices[index]);
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
        out.print(prompt + ": ");
    }

    /**
     * Prints a table of airlines, or a message if there are none.
     *
     * @param airlines the airlines to show
     */
    public void printAirlines(List<Airline> airlines) {
        if (airlines.isEmpty()) {
            out.println("There are no airlines yet.");
            return;
        }
        out.printf(AIRLINE_ROW, "ID", "Name", "Country", "IATA");
        for (Airline airline : airlines) {
            out.printf(AIRLINE_ROW, airline.getAirlineId(), airline.getAirlineName(),
                    airline.getCountry(), airline.getIataCode());
        }
    }

    /**
     * Prints a table of customers, or a message if there are none.
     *
     * @param customers the customers to show
     */
    public void printCustomers(List<Customer> customers) {
        if (customers.isEmpty()) {
            out.println("There are no customers yet.");
            return;
        }
        out.printf(CUSTOMER_ROW, "ID", "Name", "Email", "Phone", "Bookings file");
        for (Customer customer : customers) {
            out.printf(CUSTOMER_ROW, customer.getCustomerId(), customer.getCustomerName(),
                    customer.getEmail(), customer.getPhoneNumber(), customer.getPath());
        }
    }

    /**
     * Prints a table of flights, or a message if there are none.
     *
     * @param flights      the flights to show
     * @param emptyMessage the message shown when there are no flights
     */
    public void printFlights(List<Flight> flights, String emptyMessage) {
        if (flights.isEmpty()) {
            out.println(emptyMessage);
            return;
        }
        out.printf(FLIGHT_ROW, "ID", "Origin", "Destination", "Date", "Class", "Seats", "Airline");
        for (Flight flight : flights) {
            Airline airline = flight.getAirline();
            out.printf(FLIGHT_ROW, flight.getFlightId(), flight.getOrigin(), flight.getDestination(),
                    flight.getDepartureDate(), flight.getTravelClass(), flight.getSeatAmount(),
                    airline.getAirlineName() + " (" + airline.getIataCode() + ")");
        }
    }

    /**
     * Prints a message about an operation that worked.
     *
     * @param message the message
     */
    public void printSuccess(String message) {
        out.println("[OK] " + message);
    }

    /**
     * Prints a message about something the user can fix.
     *
     * @param message the message
     */
    public void printWarning(String message) {
        out.println("[!] " + message);
    }

    /**
     * Prints a message about a system failure.
     *
     * @param message the message
     */
    public void printError(String message) {
        out.println("[ERROR] " + message);
    }

    /**
     * Prints a plain message.
     *
     * @param message the message
     */
    public void printMessage(String message) {
        out.println(message);
    }
}
