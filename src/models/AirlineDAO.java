package models;

import java.util.List;

/**
 * Data Access Object interface for {@link Airline} entities.
 * <p>
 * Defines the contract for all airline related database operations, mainly CRUD operations
 * </p>
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 * 
 */
public interface AirlineDAO {

    /**
     * Checks whether a given airline exists in the database.
     *
     * @param airline the {@link Airline} to verify
     * @return {@code true} if the airline exists, {@code false} otherwise
     */
    public boolean checkAirline(Airline airline);

    /**
     * Inserts a new airline into the database.
     *
     * @param airline the {@link Airline} to insert
     * @return {@code true} if the insertion was successful, {@code false} otherwise
     */
    public boolean insertAirline(Airline airline);

    /**
     * Deletes a airline from the database.
     *
     * @param airline the {@link Airline} to delete
     * @return {@code true} if the deletion was successful, {@code false} otherwise
     */
    public boolean deleteAirline(Airline airline);

    /**
     * Updates an existing airline in the database.
     *
     * @param airline the {@link Airline} with updated information
     * @return {@code true} if the update was successful, {@code false} otherwise
     */
    public boolean updateAirline(Airline airline);

    /**
     * Retrieves all airlines from the database.
     *
     * @return a {@link List} of all {@link Airline} objects
     */
    public List<Airline> getAllAirlines();

    /**
     * Retrieves a airline by its name.
     *
     * @param name the name of the airline to search for
     * @return the matching {@link Airline}, or {@code null} if not found
     */
    public Airline getAirlineByName(String name);


    /**
     * Retrieves a airline by its unique identifier.
     *
     * @param airlineId the ID of the airline to retrieve
     * @return the matching {@link Airline}, or {@code null} if not found
     */
    public Airline getAirlineById(int airlineId);

}