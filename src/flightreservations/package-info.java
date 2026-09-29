/**
 * Console application for managing airlines, customers, flights and bookings.
 * <p>
 * Airlines, customers and flights are stored in a MySQL database and accessed
 * with JDBC. The bookings of each customer are stored in a binary file whose
 * path is saved in the database. The layers depend on each other in one
 * direction only:
 * </p>
 * <pre>
 * views  -&gt;  service  -&gt;  dao (interfaces)  -&gt;  dao.impl  -&gt;  MySQL / flights.dat
 * </pre>
 * <p>
 * {@link flightreservations.App} is the entry point and wires the layers
 * together. {@link flightreservations.SampleDataSeeder} is a development tool
 * that rebuilds the sample booking files.
 * </p>
 */
package flightreservations;
