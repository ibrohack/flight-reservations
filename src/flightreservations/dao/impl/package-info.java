/**
 * Implementations of the Data Access Object interfaces.
 * <p>
 * Airlines, customers and flights are accessed with JDBC. Bookings are read
 * and written with Java I/O streams in one binary file per customer. The
 * implementation classes are package-private:
 * {@link flightreservations.dao.impl.DAOFactory} is a Singleton and the only
 * class that creates them, and it hands them out as their interface type.
 * </p>
 */
package flightreservations.dao.impl;
