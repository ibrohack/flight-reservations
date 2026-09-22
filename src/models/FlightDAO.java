import java.util.List;

/**
 * Data Access Object interface for {@link Flight} entities.
 * <p>
 * Defines the contract for all flight related database operations, mainly CRUD operations
 * </p>
 *
 * @author Brayan, Ekaitz, Aritz
 * 
 */
public interface FlightDAO {

    /**
     * Checks whether a given flight exists in the database.
     *
     * @param flight the {@link Flight} to verify
     * @return {@code true} if the flight exists, {@code false} otherwise
     */
    public boolean checkFlight(Flight flight);

    /**
     * Inserts a new flight into the database.
     *
     * @param flight the {@link Flight} to insert
     * @return {@code true} if the insertion was successful, {@code false} otherwise
     */
    public boolean insertFlight(Flight flight);

    /**
     * Deletes a flight from the database.
     *
     * @param flight the {@link Flight} to delete
     * @return {@code true} if the deletion was successful, {@code false} otherwise
     */
    public boolean deleteFlight(Flight flight);

    /**
     * Updates an existing flight in the database.
     *
     * @param flight the {@link Flight} with updated information
     * @return {@code true} if the update was successful, {@code false} otherwise
     */
    public boolean updateFlight(Flight flight);

    /**
     * Retrieves all flights from the database.
     *
     * @return a {@link List} of all {@link Flight} objects
     */
    public List<Flight> getAllFlights();

    /**
     * Retrieves all flights from the database.
     *
     * @return a {@link List} of all {@link Flight} objects
     */
    public List<Flight> getAllFutureFlights();

    /**
     * Retrieves a flight by its name.
     *
     * @param name the name of the flight to search for
     * @return the matching {@link Flight}, or {@code null} if not found
     */
    public Flight getFlightByName(String name);


    /**
     * Retrieves a flight by its unique identifier.
     *
     * @param flightId the ID of the flight to retrieve
     * @return the matching {@link Flight}, or {@code null} if not found
     */
    public Flight getFlightById(int flightId);

}