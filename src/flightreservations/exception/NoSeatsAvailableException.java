package flightreservations.exception;

/**
 * Thrown when a customer tries to book a flight that has no seats left.
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
public class NoSeatsAvailableException extends BusinessException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception for a full flight.
     *
     * @param flightId the ID of the full flight
     */
    public NoSeatsAvailableException(int flightId) {
        super("Flight " + flightId + " has no seats left.");
    }
}
