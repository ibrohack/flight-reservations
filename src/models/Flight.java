import java.time.LocalDate;

public class Flight {
    private int flightId;
    private String origin;
    private String destination;
    private LocalDate departureDate;
    private int seatAmount;
    private TravelClass travelClass;
    private int airlineId;

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

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public int getSeatAmount() {
        return seatAmount;
    }

    public void setSeatAmount(int seatAmount) {
        this.seatAmount = seatAmount;
    }

    public TravelClass getTravelClass() {
        return travelClass;
    }

    public void setTravelClass(TravelClass travelClass) {
        this.travelClass = travelClass;
    }

    public int getAirlineId() {
        return airlineId;
    }

    public void setAirlineId(int airlineId) {
        this.airlineId = airlineId;
    }

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
