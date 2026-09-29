/**
 * Database configuration.
 * <p>
 * {@link flightreservations.config.ConnectionManager} is a Singleton that
 * loads {@code config/db.properties} once and opens a new connection for each
 * operation. The caller closes every connection with try-with-resources.
 * </p>
 */
package flightreservations.config;
