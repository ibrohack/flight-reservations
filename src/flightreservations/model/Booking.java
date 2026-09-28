package flightreservations.model;

import java.time.LocalDate;

/**
 * Model for {@link Booking} entities.
 * <p>
 * A booking links a {@link Customer} with a {@link Flight}. Bookings are
 * stored in the customer's own bookings file, so the owner is implied by
 * the file and is not repeated in each record.
 * </p>
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
public class Booking {
    /** The ID of the booked flight. */
    private final int flightId;
    /** The date the booking was made. */
    private final LocalDate bookingDate;

    /**
     * Constructs a new {@link Booking}.
     *
     * @param flightId    the ID of the booked flight
     * @param bookingDate the date the booking was made
     */
    public Booking(int flightId, LocalDate bookingDate) {
        this.flightId = flightId;
        this.bookingDate = bookingDate;
    }

    /**
     * Gets the ID of the booked flight.
     *
     * @return the flight ID
     */
    public int getFlightId() {
        return flightId;
    }

    /**
     * Gets the date the booking was made.
     *
     * @return the booking date
     */
    public LocalDate getBookingDate() {
        return bookingDate;
    }

    /**
     * Returns a pretty printed string of the {@link Booking} object.
     *
     * @return the booking as a multi-line string
     */
    @Override
    public String toString() {
        return "Booking {\n" +
                "  flightId    : " + flightId + "\n" +
                "  bookingDate : " + bookingDate + "\n" +
                "}";
    }
}
