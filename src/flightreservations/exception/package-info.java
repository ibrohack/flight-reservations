/**
 * Custom exceptions of the application.
 * <p>
 * There are two families, both checked, and the console shows each one
 * differently:
 * </p>
 * <ul>
 * <li>{@link flightreservations.exception.DataAccessException} (and its
 * subclass {@link flightreservations.exception.DuplicateEntryException}) for
 * failures of the data layer, such as a database, file or configuration
 * error.</li>
 * <li>{@link flightreservations.exception.BusinessException} and its
 * subclasses for user actions that break a rule, such as invalid input or a
 * flight with no seats left.</li>
 * </ul>
 * <p>
 * {@link flightreservations.exception.InputClosedException} is unchecked and
 * only ends the application when the standard input is closed.
 * </p>
 */
package flightreservations.exception;
