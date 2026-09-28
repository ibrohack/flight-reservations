package flightreservations.views;

import java.util.Arrays;
import java.util.Optional;

/**
 * Entries of the main menu, each with the number the user types and the
 * label shown.
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
public enum MenuOption {
    /** Register a new customer. */
    REGISTER_CUSTOMER(1, "Register a customer", Section.CUSTOMERS),
    /** Show the upcoming flights booked by a customer. */
    CUSTOMER_FLIGHTS(2, "Check a customer's flights", Section.CUSTOMERS),
    /** Show the past flights booked by a customer. */
    FLIGHT_HISTORY(3, "View a customer's flight history", Section.CUSTOMERS),
    /** Register a new airline. */
    REGISTER_AIRLINE(4, "Register an airline", Section.FLIGHTS),
    /** Register a new flight. */
    REGISTER_FLIGHT(5, "Register a flight", Section.FLIGHTS),
    /** Show the flights that depart after today. */
    FUTURE_FLIGHTS(6, "Check future flights", Section.FLIGHTS),
    /** Book a flight for a customer. */
    BOOK_FLIGHT(7, "Book a flight", Section.FLIGHTS),
    /** Close the application. */
    EXIT(0, "Exit", Section.SYSTEM);

    /** Groups of options, separated by a line in the menu. */
    public enum Section {
        /** Customer use cases. */
        CUSTOMERS,
        /** Airline, flight and booking use cases. */
        FLIGHTS,
        /** Application options. */
        SYSTEM
    }

    private final int number;
    private final String label;
    private final Section section;

    /**
     * Creates a menu option.
     *
     * @param number  the number the user types
     * @param label   the text shown in the menu
     * @param section the group the option belongs to
     */
    MenuOption(int number, String label, Section section) {
        this.number = number;
        this.label = label;
        this.section = section;
    }

    /**
     * Gets the number the user types to choose this option.
     *
     * @return the option number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Gets the text shown in the menu.
     *
     * @return the option label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Gets the group the option belongs to.
     *
     * @return the option section
     */
    public Section getSection() {
        return section;
    }

    /**
     * Finds the option for a number typed by the user.
     *
     * @param number the typed number
     * @return the matching option, or an empty {@link Optional} if no option
     *         has that number
     */
    public static Optional<MenuOption> fromNumber(int number) {
        return Arrays.stream(values())
                .filter(option -> option.number == number)
                .findFirst();
    }
}
