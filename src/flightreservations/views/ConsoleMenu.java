package flightreservations.views;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

import flightreservations.exception.BusinessException;
import flightreservations.exception.DataAccessException;
import flightreservations.exception.InputClosedException;
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
 * runs inside {@link #runSafely(MenuAction)}, which shows business errors as
 * warnings, data errors as errors, and keeps the menu running in all cases.
 * </p>
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
public class ConsoleMenu {
    private static final String NO_FUTURE_FLIGHTS = "There are no future flights.";
    private static final String NO_UPCOMING_FLIGHTS = "This customer has no upcoming flights.";
    private static final String NO_PAST_FLIGHTS = "This customer has no past flights.";

    /** A use case run from the menu. */
    @FunctionalInterface
    private interface MenuAction {
        /**
         * Runs the use case.
         *
         * @throws BusinessException   if the user breaks a business rule
         * @throws DataAccessException if the data cannot be read or saved
         */
        void run() throws BusinessException, DataAccessException;
    }

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
        this.input = Objects.requireNonNull(input);
        this.printer = Objects.requireNonNull(printer);
        this.airlineService = Objects.requireNonNull(airlineService);
        this.customerService = Objects.requireNonNull(customerService);
        this.flightService = Objects.requireNonNull(flightService);
        this.bookingService = Objects.requireNonNull(bookingService);
    }

    /**
     * Shows the menu and runs the chosen use cases until the user exits or
     * the standard input is closed.
     */
    public void run() {
        printer.printBanner();
        try {
            MenuOption option = readOption();
            while (option != MenuOption.EXIT) {
                runSafely(actionFor(option));
                option = readOption();
            }
            printer.printMessage("Goodbye!");
        } catch (InputClosedException e) {
            printer.printMessage("\nInput closed. Goodbye!");
        }
    }

    /**
     * Shows the menu and reads a valid option.
     *
     * @return the chosen option
     */
    private MenuOption readOption() {
        printer.printMenu(MenuOption.values());
        Optional<MenuOption> option = MenuOption.fromNumber(input.readInt("Choose an option"));
        while (option.isEmpty()) {
            printer.printWarning("That option is not in the menu.");
            option = MenuOption.fromNumber(input.readInt("Choose an option"));
        }
        return option.get();
    }

    /**
     * Gets the use case of a menu option.
     *
     * @param option a menu option other than {@link MenuOption#EXIT}
     * @return the use case to run
     */
    private MenuAction actionFor(MenuOption option) {
        return switch (option) {
            case REGISTER_CUSTOMER -> this::registerCustomer;
            case CUSTOMER_FLIGHTS -> this::showCustomerFlights;
            case FLIGHT_HISTORY -> this::showFlightHistory;
            case REGISTER_AIRLINE -> this::registerAirline;
            case REGISTER_FLIGHT -> this::registerFlight;
            case FUTURE_FLIGHTS -> this::showFutureFlights;
            case BOOK_FLIGHT -> this::bookFlight;
            case EXIT -> throw new IllegalArgumentException("Exit has no action.");
        };
    }

    /**
     * Runs a use case and shows any error to the user without stopping the
     * application.
     *
     * @param action the use case
     */
    private void runSafely(MenuAction action) {
        try {
            action.run();
        } catch (BusinessException e) {
            printer.printWarning(e.getMessage());
        } catch (DataAccessException e) {
            printer.printError(describe(e));
        } catch (InputClosedException e) {
            throw e; // closing the input must end the program, not be reported as an error
        } catch (RuntimeException e) {
            printer.printError("Unexpected error: " + e);
        }
    }

    /**
     * Use case: register a customer.
     *
     * @throws BusinessException   if a value is invalid or already registered
     * @throws DataAccessException if the customer cannot be saved
     */
    private void registerCustomer() throws BusinessException, DataAccessException {
        printer.printTitle(MenuOption.REGISTER_CUSTOMER.getLabel());
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
        printer.printTitle(MenuOption.CUSTOMER_FLIGHTS.getLabel());
        OptionalInt customerId = chooseCustomer();
        if (customerId.isPresent()) {
            printer.printFlights(bookingService.getUpcomingFlights(customerId.getAsInt()), NO_UPCOMING_FLIGHTS);
        }
    }

    /**
     * Use case: view a customer's flight history.
     *
     * @throws BusinessException   if the customer does not exist
     * @throws DataAccessException if the data cannot be read
     */
    private void showFlightHistory() throws BusinessException, DataAccessException {
        printer.printTitle(MenuOption.FLIGHT_HISTORY.getLabel());
        OptionalInt customerId = chooseCustomer();
        if (customerId.isPresent()) {
            printer.printFlights(bookingService.getFlightHistory(customerId.getAsInt()), NO_PAST_FLIGHTS);
        }
    }

    /**
     * Use case: register an airline.
     *
     * @throws BusinessException   if a value is invalid or already registered
     * @throws DataAccessException if the airline cannot be saved
     */
    private void registerAirline() throws BusinessException, DataAccessException {
        printer.printTitle(MenuOption.REGISTER_AIRLINE.getLabel());
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
        printer.printTitle(MenuOption.REGISTER_FLIGHT.getLabel());
        List<Airline> airlines = airlineService.getAllAirlines();
        printer.printAirlines(airlines);
        if (airlines.isEmpty()) {
            return;
        }
        int airlineId = input.readId("Airline ID");
        String origin = input.readText("Origin", InputValidator::validateOrigin);
        String destination = input.readText("Destination", text -> {
            String validDestination = InputValidator.validateDestination(text);
            InputValidator.validateRoute(origin, validDestination);
            return validDestination;
        });
        LocalDate departureDate = input.readDate("Departure date (yyyy-MM-dd)",
                date -> InputValidator.validateDepartureDate(date, LocalDate.now()));
        int seatAmount = input.readInt("Seats", InputValidator.MIN_SEATS, InputValidator.MAX_SEATS);
        TravelClass travelClass = input.readChoice("Travel class", TravelClass.values());
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
        printer.printTitle(MenuOption.FUTURE_FLIGHTS.getLabel());
        printer.printFlights(flightService.getFutureFlights(), NO_FUTURE_FLIGHTS);
    }

    /**
     * Use case: book a flight.
     *
     * @throws BusinessException   if a booking rule is broken
     * @throws DataAccessException if the booking cannot be saved
     */
    private void bookFlight() throws BusinessException, DataAccessException {
        printer.printTitle(MenuOption.BOOK_FLIGHT.getLabel());
        OptionalInt customerId = chooseCustomer();
        if (customerId.isEmpty()) {
            return;
        }
        List<Flight> flights = flightService.getFutureFlights();
        printer.printFlights(flights, NO_FUTURE_FLIGHTS);
        if (flights.isEmpty()) {
            return;
        }
        int flightId = input.readId("Flight ID");
        bookingService.bookFlight(customerId.getAsInt(), flightId);
        printer.printSuccess("Flight " + flightId + " booked for customer " + customerId.getAsInt() + ".");
    }

    /**
     * Lists the customers and reads the ID of one of them.
     *
     * @return the typed customer ID, or empty if there are no customers
     * @throws DataAccessException if the customers cannot be read
     */
    private OptionalInt chooseCustomer() throws DataAccessException {
        List<Customer> customers = customerService.getAllCustomers();
        printer.printCustomers(customers);
        return customers.isEmpty() ? OptionalInt.empty() : OptionalInt.of(input.readId("Customer ID"));
    }

    /**
     * Builds a readable description of a data error, including its cause and
     * any error that happened while recovering from it.
     *
     * @param e the data error
     * @return the description to show
     */
    private static String describe(DataAccessException e) {
        StringBuilder description = new StringBuilder(e.getMessage());
        if (e.getCause() != null && e.getCause().getMessage() != null) {
            description.append(" Cause: ").append(e.getCause().getMessage());
        }
        for (Throwable suppressed : e.getSuppressed()) {
            description.append(" Also: ").append(suppressed.getMessage());
        }
        return description.toString();
    }
}
