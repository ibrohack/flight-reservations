package flightreservations.dao;

import java.util.List;

import flightreservations.exception.DataAccessException;
import flightreservations.exception.DuplicateEntryException;
import flightreservations.model.Customer;

/**
 * Data Access Object interface for {@link Customer} entities.
 * <p>
 * Defines the customer operations the business layer needs, hiding how the
 * customers are stored.
 * </p>
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
public interface CustomerDAO {

    /**
     * Saves a new customer and sets the ID and the bookings file path
     * generated for it.
     *
     * @param customer the {@link Customer} to save; its ID and path are set
     *                 on success
     * @throws DuplicateEntryException if a customer with the same email
     *                                 already exists
     * @throws DataAccessException     if the customer cannot be saved
     */
    void insert(Customer customer) throws DataAccessException;

    /**
     * Finds a customer by its ID.
     *
     * @param customerId the ID of the customer
     * @return the matching {@link Customer}, or {@code null} if it does not
     *         exist
     * @throws DataAccessException if the customer cannot be read
     */
    Customer findById(int customerId) throws DataAccessException;

    /**
     * Finds all customers, ordered by name.
     *
     * @return a {@link List} of all customers, empty if there are none
     * @throws DataAccessException if the customers cannot be read
     */
    List<Customer> findAll() throws DataAccessException;
}
