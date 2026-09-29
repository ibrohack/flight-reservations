package flightreservations.dao;

import java.time.LocalDate;
import java.util.List;

import flightreservations.exception.DataAccessException;
import flightreservations.model.Flight;

/**
 * Data Access Object interface for {@link Flight} entities.
 * <p>
 * Defines the flight operations the business layer needs, hiding how the
 * flights are stored. Every flight returned includes its operating airline.
 * </p>
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
public interface FlightDAO {

    /**
     * Saves a new flight and sets the ID generated for it.
     *
     * @param flight the {@link Flight} to save, with its airline set; its ID
     *               is set on success
     * @throws DataAccessException if the flight cannot be saved
     */
    void insert(Flight flight) throws DataAccessException;

    /**
     * Finds a flight by its ID.
     *
     * @param flightId the ID of the flight
     * @return the matching {@link Flight}, or {@code null} if it does not
     *         exist
     * @throws DataAccessException if the flight cannot be read
     */
    Flight findById(int flightId) throws DataAccessException;

    /**
     * Finds the flights that depart after a date, ordered by departure date.
     *
     * @param date the reference date (excluded)
     * @return a {@link List} of flights departing after {@code date}, empty
     *         if there are none
     * @throws DataAccessException if the flights cannot be read
     */
    List<Flight> findDepartingAfter(LocalDate date) throws DataAccessException;

    /**
     * Finds the flights with the given IDs in a single query, ordered by
     * departure date. IDs that do not exist are ignored.
     *
     * @param flightIds the IDs of the flights to find
     * @return a {@link List} of the flights found, empty if {@code flightIds}
     *         is empty
     * @throws DataAccessException if the flights cannot be read
     */
    List<Flight> findByIds(List<Integer> flightIds) throws DataAccessException;

    /**
     * Takes one available seat of a flight, only if there is one left. The
     * check and the update are a single atomic operation, so two bookings can
     * never take the same last seat.
     *
     * @param flightId the ID of the flight
     * @return {@code true} if a seat was taken, {@code false} if the flight
     *         has no seats left
     * @throws DataAccessException if the seat count cannot be updated
     */
    boolean reserveSeat(int flightId) throws DataAccessException;

    /**
     * Gives back one seat of a flight. Used to undo {@link #reserveSeat(int)}
     * when a booking cannot be completed.
     *
     * @param flightId the ID of the flight
     * @throws DataAccessException if the flight does not exist or the seat
     *                             count cannot be updated
     */
    void releaseSeat(int flightId) throws DataAccessException;
}
