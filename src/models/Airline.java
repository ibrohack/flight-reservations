package models;

/**
 * Model for {@link Airline} entities.
 * <p>
 * Defines the structure and behavior of airline objects, including their
 * attributes and methods.
 * </p>
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 * 
 */
public class Airline {
    /** The unique ID of the airline. */
    private int airlineId;
    /** The name of the airline. */
    private String airlineName;
    /** The country of origin of the airline. */
    private String country;
    /** The IATA code of the airline. */
    private String iataCode;


    /**
     * Constructs a new {@link Airline} object with default values.
     */
    public Airline() {
        this.airlineId = 0;
        this.airlineName = "";
        this.country = "";
        this.iataCode = "";
    }

    /**
     * Constructs a new {@link Airline} object with the specified attributes.
     *
     * @param airlineId the unique ID of the airline
     * @param airlineName the name of the airline
     * @param country the country of origin of the airline
     * @param iataCode the IATA code of the airline
     */
    public Airline(int airlineId, String airlineName, String country, String iataCode) {
        this.airlineId = airlineId;
        this.airlineName = airlineName;
        this.country = country;
        this.iataCode = iataCode;
    }

    /**
     * <p>
     * Gets the {@link Airline}'s unique ID.
     * </p>
     * 
     * @return int
     */
    public int getAirlineId() {
        return airlineId;
    }

    /**
     * <p>
     * Gets the {@link Airline}'s name.
     * </p>
     * 
     * @return String
     */
    public String getAirlineName() {
        return airlineName;
    }

    /**
     * <p>
     * Sets the {@link Airline}'s name.
     * </p>
     * 
     * @param airlineName the name of the airline
     */
    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }

    /**
     * <p>
     * Gets the {@link Airline}'s country.
     * </p>
     * 
     * @return String
     */
    public String getCountry() {
        return country;
    }

    /**
     * 
     * <p>
     * Sets the {@link Airline}'s country.
     * </p>
     * 
     * @param country the country of the airline
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * 
     * <p>
     * Gets the {@link Airline}'s IATA code.
     * </p>
     * 
     * @return String
     */
    public String getIataCode() {
        return iataCode;
    }

    /**
     * 
     * <p>
     * Sets the {@link Airline}'s IATA code.
     * </p>
     * 
     * @param iataCode
     */
    public void setIataCode(String iataCode) {
        this.iataCode = iataCode;
    }

    /**
     * <p>
     * Returns a pretty printed string of the {@link Airline} object.
     * </p>
     * 
     * @return String
     */
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
