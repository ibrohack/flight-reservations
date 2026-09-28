package flightreservations.exception;

import java.time.LocalDate;

/**
 * Thrown when a customer tries to book a flight that departs today or has
 * already departed.
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
public class FlightDepartedException extends BusinessException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception for a flight that can no longer be booked.
     *
     * @param flightId      the ID of the flight
     * @param departureDate the departure date of the flight
     */
    public FlightDepartedException(int flightId, LocalDate departureDate) {
        super("Flight " + flightId + " departs on " + departureDate + " and can no longer be booked.");
    }
}
