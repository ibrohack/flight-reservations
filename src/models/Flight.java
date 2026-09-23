package models;

import java.time.LocalDate;

/**
 * Model for {@link Flight} entities.
 * <p>
 * Defines the structure and behavior of flight objects, including their
 * attributes and methods.
 * </p>
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 * 
 */
public class Flight {
    /** The unique ID of the flight. */
    private int flightId;
    /** The origin city of the flight. */
    private String origin;
    /** The destination city of the flight. */
    private String destination;
    /** The departure date of the flight. */
    private LocalDate departureDate;
    /** The number of available seats on the flight. */
    private int seatAmount;
    /** The travel class of the flight. */
    private TravelClass travelClass;
    /** The ID of the airline operating the flight. */
    private int airlineId;

    /** Constructs a new {@link Flight} object with default values. */
    public Flight() {
        this.flightId = 0;
        this.origin = "";
        this.destination = "";
        this.departureDate = LocalDate.now();
        this.seatAmount = 0;
        this.travelClass = TravelClass.ECONOMY;
        this.airlineId = 0;
    }

    /**
     * Constructs a new {@link Flight} object with the specified parameters.
     *
     * @param flightId      the unique ID of the flight
     * @param origin        the origin city of the flight
     * @param destination   the destination city of the flight
     * @param departureDate the departure date of the flight
     * @param seatAmount    the number of available seats on the flight
     * @param travelClass   the travel class of the flight
     * @param airlineId     the ID of the airline operating the flight
     */
    public Flight(int flightId, String origin, String destination, LocalDate departureDate, int seatAmount,
            TravelClass travelClass, int airlineId) {
        this.flightId = flightId;
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.seatAmount = seatAmount;
        this.travelClass = travelClass;
        this.airlineId = airlineId;
    }

    /**
     * Gets the unique ID of the flight.
     * 
     * @return int
     */
    public int getFlightId() {
        return flightId;
    }

    /**
     * Sets the unique ID of the flight.
     * 
     * @param flightId
     */
    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    /**
     * Gets the origin city of the flight.
     * 
     * @return String
     */
    public String getOrigin() {
        return origin;
    }

    /**
     * Sets the origin city of the flight.
     * 
     * @param origin
     */
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    /**
     * Gets the destination city of the flight.
     * 
     * @return String
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Sets the destination city of the flight.
     * 
     * @param destination
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * Gets the departure date of the flight.
     * 
     * @return LocalDate
     */
    public LocalDate getDepartureDate() {
        return departureDate;
    }

    /**
     * Sets the departure date of the flight.
     * 
     * @param departureDate
     */
    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    /**
     * Gets the number of available seats on the flight.
     * 
     * @return int
     */
    public int getSeatAmount() {
        return seatAmount;
    }

    /**
     * Sets the number of available seats on the flight.
     * 
     * @param seatAmount
     */
    public void setSeatAmount(int seatAmount) {
        this.seatAmount = seatAmount;
    }

    /**
     * Gets the travel class of the flight.
     * 
     * @return TravelClass
     */
    public TravelClass getTravelClass() {
        return travelClass;
    }

    /**
     * Sets the travel class of the flight.
     * 
     * @param travelClass
     */
    public void setTravelClass(TravelClass travelClass) {
        this.travelClass = travelClass;
    }

    /**
     * Gets the ID of the airline operating the flight.
     * 
     * @return int
     */
    public int getAirlineId() {
        return airlineId;
    }

    /**
     * Sets the ID of the airline operating the flight.
     * 
     * @param airlineId
     */
    public void setAirlineId(int airlineId) {
        this.airlineId = airlineId;
    }

    /**
     * <p>
     * Returns a pretty printed string of the {@link Flight} object.
     * </p>
     * * @return String
     */
    @Override
    public String toString() {
        return "Flight {\n" +
                "  flightId      : " + flightId + "\n" +
                "  origin        : " + origin + "\n" +
                "  destination   : " + destination + "\n" +
                "  departureDate : " + departureDate.toString() + "\n" +
                "  seatAmount    : " + seatAmount + "\n" +
                "  travelClass   : " + travelClass + "\n" +
                "  airlineId     : " + airlineId + "\n" +
                "}";
    }

}
