package flightreservations.dao;

import java.util.List;

import flightreservations.exception.DataAccessException;
import flightreservations.model.Booking;
import flightreservations.model.Customer;

/**
 * Data Access Object interface for {@link Booking} entities.
 * <p>
 * Defines the booking operations the business layer needs, hiding how the
 * bookings are stored. The bookings of a customer are located through the
 * {@link Customer} loaded from the database.
 * </p>
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
public interface BookingDAO {

    /**
     * Saves a new booking for a customer.
     *
     * @param customer the {@link Customer} who owns the booking, as loaded
     *                 from the database
     * @param booking  the {@link Booking} to save
     * @throws DataAccessException if the booking cannot be saved
     */
    void insert(Customer customer, Booking booking) throws DataAccessException;

    /**
     * Finds all bookings of a customer, in the order they were made.
     *
     * @param customer the {@link Customer} whose bookings are read, as loaded
     *                 from the database
     * @return a {@link List} of the customer's bookings, empty if there are
     *         none
     * @throws DataAccessException if the bookings cannot be read
     */
    List<Booking> findByCustomer(Customer customer) throws DataAccessException;

    /**
     * Checks whether a customer has already booked a flight.
     *
     * @param customer the {@link Customer} to check, as loaded from the
     *                 database
     * @param flightId the ID of the flight
     * @return {@code true} if the customer has booked the flight,
     *         {@code false} otherwise
     * @throws DataAccessException if the bookings cannot be read
     */
    boolean exists(Customer customer, int flightId) throws DataAccessException;
}
