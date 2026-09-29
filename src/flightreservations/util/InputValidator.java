package flightreservations.util;

import java.time.LocalDate;
import java.util.Locale;

import flightreservations.exception.ValidationException;
import flightreservations.model.TravelClass;

/**
 * Validation rules for every value entered by the user.
 * <p>
 * Each method checks one value and returns it normalized (trimmed and, when
 * it applies, upper or lower cased), or throws a {@link ValidationException}
 * with a message ready to show to the user. The console uses these rules to
 * re-prompt field by field, and the services apply them again as the final
 * authority. The limits match the column sizes of the database.
 * </p>
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
public final class InputValidator {
    /** Maximum length of names, cities and countries ({@code VARCHAR(100)}). */
    public static final int NAME_MAX_LENGTH = 100;
    /** Maximum length of an email address ({@code VARCHAR(100)}). */
    public static final int EMAIL_MAX_LENGTH = 100;
    /** Minimum number of seats of a new flight. */
    public static final int MIN_SEATS = 1;
    /** Maximum number of seats of a new flight (largest airliner in service). */
    public static final int MAX_SEATS = 850;
    /** How many years ahead a new flight can be scheduled. */
    public static final int MAX_YEARS_AHEAD = 1;

    private static final String IATA_CODE_REGEX = "[A-Z0-9]{2}";
    private static final String EMAIL_REGEX = "[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+";
    private static final String PHONE_REGEX = "\\+?[0-9 ]{9,20}";

    /** Utility class: not meant to be instantiated. */
    private InputValidator() {
    }

    /**
     * Validates an airline name.
     *
     * @param airlineName the raw airline name
     * @return the trimmed airline name
     * @throws ValidationException if it is empty or too long
     */
    public static String validateAirlineName(String airlineName) throws ValidationException {
        return requireText(airlineName, "Airline name", NAME_MAX_LENGTH);
    }

    /**
     * Validates a customer name.
     *
     * @param customerName the raw customer name
     * @return the trimmed customer name
     * @throws ValidationException if it is empty or too long
     */
    public static String validateCustomerName(String customerName) throws ValidationException {
        return requireText(customerName, "Customer name", NAME_MAX_LENGTH);
    }

    /**
     * Validates a country name.
     *
     * @param country the raw country name
     * @return the trimmed country name
     * @throws ValidationException if it is empty or too long
     */
    public static String validateCountry(String country) throws ValidationException {
        return requireText(country, "Country", NAME_MAX_LENGTH);
    }

    /**
     * Validates an airline IATA code: exactly two letters or digits.
     *
     * @param iataCode the raw IATA code
     * @return the trimmed IATA code in upper case
     * @throws ValidationException if it does not have the IATA format
     */
    public static String validateIataCode(String iataCode) throws ValidationException {
        String normalized = trim(iataCode).toUpperCase(Locale.ROOT);
        if (!normalized.matches(IATA_CODE_REGEX)) {
            throw new ValidationException("The IATA code must be exactly 2 letters or digits (for example IB).");
        }
        return normalized;
    }

    /**
     * Validates an email address.
     *
     * @param email the raw email address
     * @return the trimmed email address in lower case
     * @throws ValidationException if it is empty, too long or malformed
     */
    public static String validateEmail(String email) throws ValidationException {
        String normalized = trim(email).toLowerCase(Locale.ROOT);
        if (normalized.length() > EMAIL_MAX_LENGTH) {
            throw new ValidationException("The email must have at most " + EMAIL_MAX_LENGTH + " characters.");
        }
        if (!normalized.matches(EMAIL_REGEX)) {
            throw new ValidationException("The email is not valid (for example name@example.com).");
        }
        return normalized;
    }

    /**
     * Validates a phone number: an optional {@code +} followed by 9 to 20
     * digits or spaces.
     *
     * @param phoneNumber the raw phone number
     * @return the trimmed phone number
     * @throws ValidationException if it does not have the phone format
     */
    public static String validatePhoneNumber(String phoneNumber) throws ValidationException {
        String normalized = trim(phoneNumber);
        if (!normalized.matches(PHONE_REGEX)) {
            throw new ValidationException("The phone number must have 9 to 20 digits or spaces, "
                    + "optionally starting with + (for example +34 600 111 222).");
        }
        return normalized;
    }

    /**
     * Validates the origin city of a flight.
     *
     * @param origin the raw origin city
     * @return the trimmed origin city
     * @throws ValidationException if it is empty or too long
     */
    public static String validateOrigin(String origin) throws ValidationException {
        return requireText(origin, "Origin", NAME_MAX_LENGTH);
    }

    /**
     * Validates the destination city of a flight.
     *
     * @param destination the raw destination city
     * @return the trimmed destination city
     * @throws ValidationException if it is empty or too long
     */
    public static String validateDestination(String destination) throws ValidationException {
        return requireText(destination, "Destination", NAME_MAX_LENGTH);
    }

    /**
     * Validates that the origin and the destination of a flight are
     * different cities, ignoring case.
     *
     * @param origin      the validated origin city
     * @param destination the validated destination city
     * @throws ValidationException if both cities are the same
     */
    public static void validateRoute(String origin, String destination) throws ValidationException {
        if (origin.equalsIgnoreCase(destination)) {
            throw new ValidationException("The origin and the destination must be different cities.");
        }
    }

    /**
     * Validates the departure date of a new flight: strictly after today and
     * at most {@value #MAX_YEARS_AHEAD} year ahead.
     *
     * @param departureDate the departure date
     * @param today         the current date
     * @return the same departure date
     * @throws ValidationException if the date is missing or out of range
     */
    public static LocalDate validateDepartureDate(LocalDate departureDate, LocalDate today)
            throws ValidationException {
        if (departureDate == null) {
            throw new ValidationException("The departure date is required.");
        }
        if (!departureDate.isAfter(today)) {
            throw new ValidationException("The departure date must be after today (" + today + ").");
        }
        LocalDate latestDate = today.plusYears(MAX_YEARS_AHEAD);
        if (departureDate.isAfter(latestDate)) {
            throw new ValidationException("The departure date cannot be later than " + latestDate + ".");
        }
        return departureDate;
    }

    /**
     * Validates the number of seats of a new flight.
     *
     * @param seatAmount the number of seats
     * @return the same number of seats
     * @throws ValidationException if it is outside {@value #MIN_SEATS} to
     *                             {@value #MAX_SEATS}
     */
    public static int validateSeatAmount(int seatAmount) throws ValidationException {
        if (seatAmount < MIN_SEATS || seatAmount > MAX_SEATS) {
            throw new ValidationException("The number of seats must be between " + MIN_SEATS
                    + " and " + MAX_SEATS + ".");
        }
        return seatAmount;
    }

    /**
     * Validates that a travel class was chosen.
     *
     * @param travelClass the travel class
     * @return the same travel class
     * @throws ValidationException if it is missing
     */
    public static TravelClass validateTravelClass(TravelClass travelClass) throws ValidationException {
        if (travelClass == null) {
            throw new ValidationException("The travel class is required.");
        }
        return travelClass;
    }

    /**
     * Checks that a text value is present and not longer than the limit.
     *
     * @param value     the raw value
     * @param fieldName the name of the field, used in the message
     * @param maxLength the maximum allowed length
     * @return the trimmed value
     * @throws ValidationException if it is empty or too long
     */
    private static String requireText(String value, String fieldName, int maxLength) throws ValidationException {
        String normalized = trim(value);
        if (normalized.isEmpty()) {
            throw new ValidationException(fieldName + " must not be empty.");
        }
        if (normalized.length() > maxLength) {
            throw new ValidationException(fieldName + " must have at most " + maxLength + " characters.");
        }
        return normalized;
    }

    /**
     * Trims a value, treating {@code null} as an empty text.
     *
     * @param value the raw value
     * @return the trimmed value, never {@code null}
     */
    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
