package models;

/**
 * Model for {@link Book} entities.
 * <p>
 * Defines the structure and behavior of booking objects, including their
 * attributes and methods.
 * </p>
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 * 
 */
public class Book {
    /** The ID of the customer associated with the booking. */
    private int customerId;
    /** The ID of the flight associated with the booking. */
    private int flightId;

    /**
     * Constructs a new {@link Book} object with default values.
     */
    public Book() {
        this.customerId = 0;
        this.flightId = 0;
    }

    /**
     * Constructs a new {@link Book} object with the specified attributes.
     *
     * @param customerId the ID of the customer
     * @param flightId   the ID of the flight
     */
    public Book(int customerId, int flightId) {
        this.customerId = customerId;
        this.flightId = flightId;
    }

    /**
     * <p>
     * Gets the {@link Book}'s customer ID.
     * </p>
     * 
     * @return int
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * <p>
     * Sets the {@link Book}'s customer ID.
     * </p>
     * 
     * @param customerId
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     * <p>
     * Gets the {@link Book}'s flight ID.
     * </p>
     * 
     * @return int
     */
    public int getFlightId() {
        return flightId;
    }

    /**
     * <p>
     * Sets the {@link Book}'s flight ID.
     * </p>
     * 
     * @param flightId
     */
    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    /**
     * <p>
     * Returns a pretty printed string of the {@link Book} object.
     * </p>
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "Book { \n" +
                "  customerId : " + customerId + "\n" +
                "  flightId   : " + flightId + "\n" +
                "}";
    }
}
