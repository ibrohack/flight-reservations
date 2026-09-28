package flightreservations.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import flightreservations.dao.BookingDAO;
import flightreservations.dao.CustomerDAO;
import flightreservations.dao.FlightDAO;
import flightreservations.exception.BusinessException;
import flightreservations.exception.DataAccessException;
import flightreservations.exception.DuplicateBookingException;
import flightreservations.exception.EntityNotFoundException;
import flightreservations.exception.FlightDepartedException;
import flightreservations.exception.NoSeatsAvailableException;
import flightreservations.model.Booking;
import flightreservations.model.Customer;
import flightreservations.model.Flight;

/**
 * Business logic for bookings: booking a flight and listing the flights a
 * customer has booked.
 * <p>
 * Customers and flights live in the database, while bookings live in each
 * customer's bookings file. When a flight is booked, a seat is taken in the
 * database first and the booking is then written to the file; if the file
 * cannot be written, the seat is given back so both stay consistent.
 * </p>
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
public class BookingService {
    private static final Comparator<Flight> BY_DEPARTURE =
            Comparator.comparing(Flight::getDepartureDate).thenComparing(Flight::getFlightId);

    private final CustomerDAO customerDao;
    private final FlightDAO flightDao;
    private final BookingDAO bookingDao;

    /**
     * Creates the service.
     *
     * @param customerDao the customer data access object
     * @param flightDao   the flight data access object
     * @param bookingDao  the booking data access object
     */
    public BookingService(CustomerDAO customerDao, FlightDAO flightDao, BookingDAO bookingDao) {
        this.customerDao = Objects.requireNonNull(customerDao);
        this.flightDao = Objects.requireNonNull(flightDao);
        this.bookingDao = Objects.requireNonNull(bookingDao);
    }

    /**
     * Books a flight for a customer. The flight must depart after today, the
     * customer must not have booked it already and it must have a seat left.
     *
     * @param customerId the ID of the customer
     * @param flightId   the ID of the flight
     * @throws EntityNotFoundException    if the customer or the flight does
     *                                    not exist
     * @throws FlightDepartedException    if the flight departs today or
     *                                    earlier
     * @throws DuplicateBookingException  if the customer already booked the
     *                                    flight
     * @throws NoSeatsAvailableException  if the flight is full
     * @throws DataAccessException        if the booking cannot be saved; the
     *                                    reserved seat is given back
     */
    public void bookFlight(int customerId, int flightId) throws BusinessException, DataAccessException {
        LocalDate today = LocalDate.now();
        Customer customer = findCustomer(customerId);
        Flight flight = flightDao.findById(flightId)
                .orElseThrow(() -> new EntityNotFoundException(Flight.class, flightId));
        if (!flight.getDepartureDate().isAfter(today)) {
            throw new FlightDepartedException(flightId, flight.getDepartureDate());
        }
        if (bookingDao.exists(customer, flightId)) {
            throw new DuplicateBookingException(customerId, flightId);
        }
        if (!flightDao.reserveSeat(flightId)) {
            throw new NoSeatsAvailableException(flightId);
        }
        saveBookingOrReleaseSeat(customer, new Booking(flightId, today));
    }

    /**
     * Gets the booked flights of a customer that depart today or later,
     * soonest first.
     *
     * @param customerId the ID of the customer
     * @return a {@link List} of upcoming flights, empty if there are none
     * @throws EntityNotFoundException if the customer does not exist
     * @throws DataAccessException     if the bookings or flights cannot be
     *                                 read
     */
    public List<Flight> getUpcomingFlights(int customerId) throws EntityNotFoundException, DataAccessException {
        LocalDate today = LocalDate.now();
        return findBookedFlights(customerId, flight -> !flight.getDepartureDate().isBefore(today), BY_DEPARTURE);
    }

    /**
     * Gets the booked flights of a customer that departed before today, most
     * recent first.
     *
     * @param customerId the ID of the customer
     * @return a {@link List} of past flights, empty if there are none
     * @throws EntityNotFoundException if the customer does not exist
     * @throws DataAccessException     if the bookings or flights cannot be
     *                                 read
     */
    public List<Flight> getFlightHistory(int customerId) throws EntityNotFoundException, DataAccessException {
        LocalDate today = LocalDate.now();
        return findBookedFlights(customerId, flight -> flight.getDepartureDate().isBefore(today),
                BY_DEPARTURE.reversed());
    }

    /**
     * Loads a customer or fails if it does not exist.
     *
     * @param customerId the ID of the customer
     * @return the customer
     * @throws EntityNotFoundException if the customer does not exist
     * @throws DataAccessException     if the customer cannot be read
     */
    private Customer findCustomer(int customerId) throws EntityNotFoundException, DataAccessException {
        return customerDao.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException(Customer.class, customerId));
    }

    /**
     * Reads a customer's bookings from the customer's file and loads the
     * booked flights from the database in a single query. Bookings of flights
     * that no longer exist are skipped.
     *
     * @param customerId the ID of the customer
     * @param filter     which flights to keep
     * @param order      how to sort the result
     * @return the matching booked flights
     * @throws EntityNotFoundException if the customer does not exist
     * @throws DataAccessException     if the bookings or flights cannot be
     *                                 read
     */
    private List<Flight> findBookedFlights(int customerId, Predicate<Flight> filter, Comparator<Flight> order)
            throws EntityNotFoundException, DataAccessException {
        Customer customer = findCustomer(customerId);
        List<Integer> flightIds = bookingDao.findByCustomer(customer).stream()
                .map(Booking::getFlightId)
                .toList();
        return flightDao.findByIds(flightIds).stream()
                .filter(filter)
                .sorted(order)
                .toList();
    }

    /**
     * Saves a booking in the customer's file. If that fails, the seat taken
     * for it is given back before the error is propagated.
     *
     * @param customer the customer who books
     * @param booking  the booking to save
     * @throws DataAccessException if the booking cannot be saved
     */
    private void saveBookingOrReleaseSeat(Customer customer, Booking booking) throws DataAccessException {
        try {
            bookingDao.insert(customer, booking);
        } catch (DataAccessException e) {
            releaseSeat(booking.getFlightId(), e);
            throw e;
        }
    }

    /**
     * Gives back a reserved seat. If that also fails, the failure is attached
     * to the original error so neither is lost.
     *
     * @param flightId the ID of the flight
     * @param cause    the error that made the booking fail
     */
    private void releaseSeat(int flightId, DataAccessException cause) {
        try {
            flightDao.releaseSeat(flightId);
        } catch (DataAccessException releaseError) {
            cause.addSuppressed(releaseError);
        }
    }
}
