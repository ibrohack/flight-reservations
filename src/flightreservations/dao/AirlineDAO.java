package flightreservations.dao;

import java.util.List;
import java.util.Optional;

import flightreservations.exception.DataAccessException;
import flightreservations.exception.DuplicateEntryException;
import flightreservations.model.Airline;

/**
 * Data Access Object interface for {@link Airline} entities.
 * <p>
 * Defines the airline operations the business layer needs, hiding how the
 * airlines are stored.
 * </p>
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
public interface AirlineDAO {

    /**
     * Saves a new airline and sets the ID generated for it.
     *
     * @param airline the {@link Airline} to save; its ID is set on success
     * @throws DuplicateEntryException if an airline with the same IATA code
     *                                 already exists
     * @throws DataAccessException     if the airline cannot be saved
     */
    void insert(Airline airline) throws DataAccessException;

    /**
     * Finds an airline by its ID.
     *
     * @param airlineId the ID of the airline
     * @return the matching {@link Airline}, or an empty {@link Optional} if
     *         it does not exist
     * @throws DataAccessException if the airline cannot be read
     */
    Optional<Airline> findById(int airlineId) throws DataAccessException;

    /**
     * Finds all airlines, ordered by name.
     *
     * @return a {@link List} of all airlines, empty if there are none
     * @throws DataAccessException if the airlines cannot be read
     */
    List<Airline> findAll() throws DataAccessException;
}
