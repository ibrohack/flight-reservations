package flightreservations;

import java.io.Console;
import java.nio.charset.Charset;
import java.util.Scanner;

import flightreservations.dao.impl.DAOFactory;
import flightreservations.service.AirlineService;
import flightreservations.service.BookingService;
import flightreservations.service.CustomerService;
import flightreservations.service.FlightService;
import flightreservations.views.ConsoleInput;
import flightreservations.views.ConsoleMenu;
import flightreservations.views.ConsolePrinter;

/**
 * Entry point of the flight reservations application.
 * <p>
 * It wires the layers together: it takes the DAOs from the
 * {@link DAOFactory}, gives them to the services, and starts the console menu.
 * Run it from the project root so the relative paths ({@code config/},
 * {@code data/}) resolve correctly.
 * </p>
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
public final class App {

    /** Entry point class: not meant to be instantiated. */
    private App() {
    }

    /**
     * Starts the application.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        DAOFactory daoFactory = DAOFactory.getInstance();
        AirlineService airlineService = new AirlineService(daoFactory.getAirlineDao());
        CustomerService customerService = new CustomerService(daoFactory.getCustomerDao());
        FlightService flightService = new FlightService(daoFactory.getFlightDao(), daoFactory.getAirlineDao());
        BookingService bookingService = new BookingService(daoFactory.getCustomerDao(),
                daoFactory.getFlightDao(), daoFactory.getBookingDao());

        try (Scanner scanner = new Scanner(System.in, inputCharset())) {
            ConsolePrinter printer = new ConsolePrinter(System.out);
            ConsoleInput input = new ConsoleInput(scanner, printer);
            new ConsoleMenu(input, printer, airlineService, customerService, flightService, bookingService).run();
        }
    }

    /**
     * Gets the charset of the console, so typed accents (for example in
     * "García") are read correctly on Windows. Falls back to the default
     * charset when there is no console.
     *
     * @return the charset to read the standard input with
     */
    private static Charset inputCharset() {
        Console console = System.console();
        return console != null ? console.charset() : Charset.defaultCharset();
    }
}
