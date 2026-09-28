package flightreservations.dao.impl;

import flightreservations.dao.AirlineDAO;
import flightreservations.dao.BookingDAO;
import flightreservations.dao.CustomerDAO;
import flightreservations.dao.FlightDAO;

/**
 * Singleton factory that provides the Data Access Objects of the application.
 * <p>
 * It is the only class that knows the DAO implementations: their
 * constructors are package-private, so each DAO is created exactly once here
 * and handed out as its interface type. The rest of the application depends
 * only on the interfaces.
 * </p>
 * <p>
 * Singleton pattern: {@code final} class, {@code private} constructor and
 * one {@code private static final} instance created eagerly (the JVM makes
 * class initialization thread-safe), exposed by {@link #getInstance()}.
 * </p>
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
public final class DAOFactory {
    /** The only instance, created when the class is loaded. */
    private static final DAOFactory INSTANCE = new DAOFactory();

    private final AirlineDAO airlineDao = new AirlineDAOImpl();
    private final CustomerDAO customerDao = new CustomerDAOImpl();
    private final FlightDAO flightDao = new FlightDAOImpl();
    private final BookingDAO bookingDao = new BookingDAOImpl();

    /** Prevents instantiation from outside the class. */
    private DAOFactory() {
    }

    /**
     * Returns the only instance of the factory.
     *
     * @return the DAO factory
     */
    public static DAOFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Gets the airline DAO.
     *
     * @return the {@link AirlineDAO}
     */
    public AirlineDAO getAirlineDao() {
        return airlineDao;
    }

    /**
     * Gets the customer DAO.
     *
     * @return the {@link CustomerDAO}
     */
    public CustomerDAO getCustomerDao() {
        return customerDao;
    }

    /**
     * Gets the flight DAO.
     *
     * @return the {@link FlightDAO}
     */
    public FlightDAO getFlightDao() {
        return flightDao;
    }

    /**
     * Gets the booking DAO.
     *
     * @return the {@link BookingDAO}
     */
    public BookingDAO getBookingDao() {
        return bookingDao;
    }
}
