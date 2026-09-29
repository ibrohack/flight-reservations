package flightreservations.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        this.customerDao = customerDao;
        this.flightDao = flightDao;
        this.bookingDao = bookingDao;
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
        Flight flight = flightDao.findById(flightId);
        if (flight == null) {
            throw new EntityNotFoundException(Flight.class, flightId);
        }
        if (!flight.getDepartureDate().isAfter(today)) {
            throw new FlightDepartedException(flightId, flight.getDepartureDate());
        }
        if (bookingDao.exists(customer, flightId)) {
            throw new DuplicateBookingException(customerId, flightId);
        }
        if (!flightDao.reserveSeat(flightId)) {
            throw new NoSeatsAvailableException(flightId);
        }
        try {
            bookingDao.insert(customer, new Booking(flightId, today));
        } catch (DataAccessException e) {
            // The file could not be written: give the seat back so the
            // database and the file stay consistent.
            flightDao.releaseSeat(flightId);
            throw e;
        }
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
        List<Flight> upcomingFlights = new ArrayList<>();
        for (Flight flight : findBookedFlights(customerId)) {
            if (!flight.getDepartureDate().isBefore(today)) {
                upcomingFlights.add(flight);
            }
        }
        return upcomingFlights;
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
        List<Flight> pastFlights = new ArrayList<>();
        for (Flight flight : findBookedFlights(customerId)) {
            if (flight.getDepartureDate().isBefore(today)) {
                pastFlights.add(flight);
            }
        }
        // The flights come soonest first, so reversing puts the most recent first.
        Collections.reverse(pastFlights);
        return pastFlights;
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
        Customer customer = customerDao.findById(customerId);
        if (customer == null) {
            throw new EntityNotFoundException(Customer.class, customerId);
        }
        return customer;
    }

    /**
     * Reads a customer's bookings from the customer's file and loads the
     * booked flights from the database in a single query, soonest first.
     * Bookings of flights that no longer exist are skipped.
     *
     * @param customerId the ID of the customer
     * @return the booked flights, ordered by departure date
     * @throws EntityNotFoundException if the customer does not exist
     * @throws DataAccessException     if the bookings or flights cannot be
     *                                 read
     */
    private List<Flight> findBookedFlights(int customerId) throws EntityNotFoundException, DataAccessException {
        Customer customer = findCustomer(customerId);
        List<Integer> flightIds = new ArrayList<>();
        for (Booking booking : bookingDao.findByCustomer(customer)) {
            flightIds.add(booking.getFlightId());
        }
        return flightDao.findByIds(flightIds);
    }
}
