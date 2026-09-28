package flightreservations.exception;

/**
 * Thrown when a customer tries to book a flight they have already booked.
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
public class DuplicateBookingException extends BusinessException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception for a repeated booking.
     *
     * @param customerId the ID of the customer
     * @param flightId   the ID of the flight
     */
    public DuplicateBookingException(int customerId, int flightId) {
        super("Customer " + customerId + " has already booked flight " + flightId + ".");
    }
}
