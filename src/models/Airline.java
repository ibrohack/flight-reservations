public class Airline {
    private int airlineId;
    private String airlineName;
    private String country;
    private String iataCode;

    public Airline(int airlineId, String airlineName, String country, String iataCode) {
        this.airlineId = airlineId;
        this.airlineName = airlineName;
        this.country = country;
        this.iataCode = iataCode;
    }

    public int getAirlineId() {
        return airlineId;
    }

    public void setAirlineId(int airlineId) {
        this.airlineId = airlineId;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIataCode() {
        return iataCode;
    }

    public void setIataCode(String iataCode) {
        this.iataCode = iataCode;
    }

    @Override
    public String toString() {
        return "Airline {\n" +
                "  airlineId   : " + airlineId + "\n" +
                "  airlineName : " + airlineName + "\n" +
                "  country     : " + country + "\n" +
                "  iataCode    : " + iataCode + "\n" +
                "}";
    }

}
