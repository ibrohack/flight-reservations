/**
 * Business logic of the application.
 * <p>
 * The services validate the input and apply the booking rules. They use only
 * the {@link flightreservations.dao} interfaces, and they report broken rules
 * with a {@link flightreservations.exception.BusinessException}.
 * </p>
 */
package flightreservations.service;
