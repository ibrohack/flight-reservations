package flightreservations.views;

import java.time.LocalDate;
import java.util.List;

import flightreservations.exception.BusinessException;
import flightreservations.exception.DataAccessException;
import flightreservations.exception.InputClosedException;
import flightreservations.exception.ValidationException;
import flightreservations.model.Airline;
import flightreservations.model.Customer;
import flightreservations.model.Flight;
import flightreservations.model.TravelClass;
import flightreservations.service.AirlineService;
import flightreservations.service.BookingService;
import flightreservations.service.CustomerService;
import flightreservations.service.FlightService;
import flightreservations.util.InputValidator;

/**
 * Console user interface: shows the main menu and runs the chosen use case.
 * <p>
 * This is where the exceptions of the lower layers end up. Every use case
 * runs inside the try/catch of {@link #run()}, which shows business errors
 * as warnings, data errors as errors, and keeps the menu running in all
 * cases.
 * </p>
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
public class ConsoleMenu {
    private static final int EXIT = 0;
    private static final int LAST_OPTION = 7;
    /** Returned by {@link #chooseCustomer()} when there is no customer to choose. */
    private static final int NO_CUSTOMER = 0;
    private static final String NO_FUTURE_FLIGHTS = "There are no future flights.";

    private final ConsoleInput input;
    private final ConsolePrinter printer;
    private final AirlineService airlineService;
    private final CustomerService customerService;
    private final FlightService flightService;
    private final BookingService bookingService;

    /**
     * Creates the menu.
     *
     * @param input           reads the user's input
     * @param printer         writes the output
     * @param airlineService  the airline business logic
     * @param customerService the customer business logic
     * @param flightService   the flight business logic
     * @param bookingService  the booking business logic
     */
    public ConsoleMenu(ConsoleInput input, ConsolePrinter printer, AirlineService airlineService,
            CustomerService customerService, FlightService flightService, BookingService bookingService) {
        this.input = input;
        this.printer = printer;
        this.airlineService = airlineService;
        this.customerService = customerService;
        this.flightService = flightService;
        this.bookingService = bookingService;
    }

    /**
     * Shows the menu and runs the chosen use cases until the user exits or
     * the standard input is closed. Any error is shown to the user without
     * stopping the application.
     */
    public void run() {
        printer.printBanner();
        int option = -1;
        while (option != EXIT) {
            try {
                printer.printMenu();
                option = input.readInt("Choose an option", EXIT, LAST_OPTION);
                switch (option) {
                    case 1 -> registerCustomer();
                    case 2 -> showCustomerFlights();
                    case 3 -> showFlightHistory();
                    case 4 -> registerAirline();
                    case 5 -> registerFlight();
                    case 6 -> showFutureFlights();
                    case 7 -> bookFlight();
                    case EXIT -> printer.printMessage("Goodbye!");
                }
            } catch (BusinessException e) {
                printer.printWarning(e.getMessage());
            } catch (DataAccessException e) {
                printer.printError(describe(e));
            } catch (InputClosedException e) {
                // Caught before RuntimeException because it is a subclass of it:
                // closing the input ends the program, it is not an error.
                printer.printMessage("\nInput closed. Goodbye!");
                option = EXIT;
            } catch (RuntimeException e) {
                printer.printError("Unexpected error: " + e);
            }
        }
    }

    /**
     * Use case: register a customer.
     *
     * @throws BusinessException   if a value is invalid or already registered
     * @throws DataAccessException if the customer cannot be saved
     */
    private void registerCustomer() throws BusinessException, DataAccessException {
        printer.printTitle("Register a customer");
        String name = input.readText("Name", InputValidator::validateCustomerName);
        String email = input.readText("Email", InputValidator::validateEmail);
        String phoneNumber = input.readText("Phone number", InputValidator::validatePhoneNumber);
        Customer customer = new Customer(name, email, phoneNumber);
        customerService.registerCustomer(customer);
        printer.printSuccess("Customer registered with ID " + customer.getCustomerId()
                + ". Bookings file: " + customer.getPath());
    }

    /**
     * Use case: check a customer's upcoming flights.
     *
     * @throws BusinessException   if the customer does not exist
     * @throws DataAccessException if the data cannot be read
     */
    private void showCustomerFlights() throws BusinessException, DataAccessException {
        printer.printTitle("Check a customer's flights");
        int customerId = chooseCustomer();
        if (customerId != NO_CUSTOMER) {
            List<Flight> flights = bookingService.getUpcomingFlights(customerId);
            printer.printFlights(flights, "This customer has no upcoming flights.");
        }
    }

    /**
     * Use case: view a customer's flight history.
     *
     * @throws BusinessException   if the customer does not exist
     * @throws DataAccessException if the data cannot be read
     */
    private void showFlightHistory() throws BusinessException, DataAccessException {
        printer.printTitle("View a customer's flight history");
        int customerId = chooseCustomer();
        if (customerId != NO_CUSTOMER) {
            List<Flight> flights = bookingService.getFlightHistory(customerId);
            printer.printFlights(flights, "This customer has no past flights.");
        }
    }

    /**
     * Use case: register an airline.
     *
     * @throws BusinessException   if a value is invalid or already registered
     * @throws DataAccessException if the airline cannot be saved
     */
    private void registerAirline() throws BusinessException, DataAccessException {
        printer.printTitle("Register an airline");
        String name = input.readText("Name", InputValidator::validateAirlineName);
        String country = input.readText("Country", InputValidator::validateCountry);
        String iataCode = input.readText("IATA code (2 characters)", InputValidator::validateIataCode);
        Airline airline = new Airline(name, country, iataCode);
        airlineService.registerAirline(airline);
        printer.printSuccess("Airline registered with ID " + airline.getAirlineId() + ".");
    }

    /**
     * Use case: register a flight.
     *
     * @throws BusinessException   if a value is invalid or the airline does
     *                             not exist
     * @throws DataAccessException if the flight cannot be saved
     */
    private void registerFlight() throws BusinessException, DataAccessException {
        printer.printTitle("Register a flight");
        List<Airline> airlines = airlineService.getAllAirlines();
        printer.printAirlines(airlines);
        if (airlines.isEmpty()) {
            return;
        }
        int airlineId = input.readId("Airline ID");
        String origin = input.readText("Origin", InputValidator::validateOrigin);
        String destination = readDestination(origin);
        LocalDate departureDate = readDepartureDate();
        int seatAmount = input.readInt("Seats", InputValidator.MIN_SEATS, InputValidator.MAX_SEATS);
        TravelClass[] travelClasses = TravelClass.values();
        printer.printChoices(travelClasses);
        int choice = input.readInt("Travel class", 1, travelClasses.length);
        TravelClass travelClass = travelClasses[choice - 1];
        Flight flight = new Flight(origin, destination, departureDate, seatAmount, travelClass);
        flightService.registerFlight(flight, airlineId);
        printer.printSuccess("Flight registered with ID " + flight.getFlightId() + ".");
    }

    /**
     * Use case: check future flights.
     *
     * @throws DataAccessException if the flights cannot be read
     */
    private void showFutureFlights() throws DataAccessException {
        printer.printTitle("Check future flights");
        printer.printFlights(flightService.getFutureFlights(), NO_FUTURE_FLIGHTS);
    }

    /**
     * Use case: book a flight.
     *
     * @throws BusinessException   if a booking rule is broken
     * @throws DataAccessException if the booking cannot be saved
     */
    private void bookFlight() throws BusinessException, DataAccessException {
        printer.printTitle("Book a flight");
        int customerId = chooseCustomer();
        if (customerId == NO_CUSTOMER) {
            return;
        }
        List<Flight> flights = flightService.getFutureFlights();
        printer.printFlights(flights, NO_FUTURE_FLIGHTS);
        if (flights.isEmpty()) {
            return;
        }
        int flightId = input.readId("Flight ID");
        bookingService.bookFlight(customerId, flightId);
        printer.printSuccess("Flight " + flightId + " booked for customer " + customerId + ".");
    }

    /**
     * Lists the customers and reads the ID of one of them.
     *
     * @return the typed customer ID, or {@link #NO_CUSTOMER} if there are no
     *         customers
     * @throws DataAccessException if the customers cannot be read
     */
    private int chooseCustomer() throws DataAccessException {
        List<Customer> customers = customerService.getAllCustomers();
        printer.printCustomers(customers);
        if (customers.isEmpty()) {
            return NO_CUSTOMER;
        }
        return input.readId("Customer ID");
    }

    /**
     * Reads the destination of a flight until it is valid and different
     * from the origin.
     *
     * @param origin the validated origin city
     * @return the validated destination city
     */
    private String readDestination(String origin) {
        while (true) {
            String destination = input.readText("Destination", InputValidator::validateDestination);
            try {
                InputValidator.validateRoute(origin, destination);
                return destination;
            } catch (ValidationException e) {
                printer.printWarning(e.getMessage());
            }
        }
    }

    /**
     * Reads the departure date of a flight until it is after today and not
     * too far ahead.
     *
     * @return the validated departure date
     */
    private LocalDate readDepartureDate() {
        while (true) {
            LocalDate date = input.readDate("Departure date (yyyy-MM-dd)");
            try {
                return InputValidator.validateDepartureDate(date, LocalDate.now());
            } catch (ValidationException e) {
                printer.printWarning(e.getMessage());
            }
        }
    }

    /**
     * Builds a readable description of a data error, including its cause.
     *
     * @param e the data error
     * @return the description to show
     */
    private static String describe(DataAccessException e) {
        String description = e.getMessage();
        if (e.getCause() != null && e.getCause().getMessage() != null) {
            description += " Cause: " + e.getCause().getMessage();
        }
        return description;
    }
}
