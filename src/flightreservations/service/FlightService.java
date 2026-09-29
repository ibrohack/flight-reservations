package flightreservations.service;

import java.time.LocalDate;
import java.util.List;

import flightreservations.dao.AirlineDAO;
import flightreservations.dao.FlightDAO;
import flightreservations.exception.BusinessException;
import flightreservations.exception.DataAccessException;
import flightreservations.exception.EntityNotFoundException;
import flightreservations.exception.ValidationException;
import flightreservations.model.Airline;
import flightreservations.model.Flight;
import flightreservations.util.InputValidator;

/**
 * Business logic for flights.
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
public class FlightService {
    private final FlightDAO flightDao;
    private final AirlineDAO airlineDao;

    /**
     * Creates the service.
     *
     * @param flightDao  the flight data access object
     * @param airlineDao the airline data access object
     */
    public FlightService(FlightDAO flightDao, AirlineDAO airlineDao) {
        this.flightDao = flightDao;
        this.airlineDao = airlineDao;
    }

    /**
     * Validates and registers a new flight operated by an existing airline.
     * On success the flight gets its generated ID, its airline and
     * normalized values.
     *
     * @param flight    the new {@link Flight}
     * @param airlineId the ID of the operating airline
     * @throws ValidationException     if a value is invalid
     * @throws EntityNotFoundException if the airline does not exist
     * @throws DataAccessException     if the flight cannot be saved
     */
    public void registerFlight(Flight flight, int airlineId) throws BusinessException, DataAccessException {
        validate(flight);
        Airline airline = airlineDao.findById(airlineId);
        if (airline == null) {
            throw new EntityNotFoundException(Airline.class, airlineId);
        }
        flight.setAirline(airline);
        flightDao.insert(flight);
    }

    /**
     * Gets the flights that depart after today, ordered by departure date.
     *
     * @return a {@link List} of future flights, empty if there are none
     * @throws DataAccessException if the flights cannot be read
     */
    public List<Flight> getFutureFlights() throws DataAccessException {
        return flightDao.findDepartingAfter(LocalDate.now());
    }

    /**
     * Validates and normalizes the values of a new flight.
     *
     * @param flight the new flight
     * @throws ValidationException if a value is invalid
     */
    private static void validate(Flight flight) throws ValidationException {
        String origin = InputValidator.validateOrigin(flight.getOrigin());
        String destination = InputValidator.validateDestination(flight.getDestination());
        InputValidator.validateRoute(origin, destination);
        flight.setOrigin(origin);
        flight.setDestination(destination);
        InputValidator.validateDepartureDate(flight.getDepartureDate(), LocalDate.now());
        InputValidator.validateSeatAmount(flight.getSeatAmount());
        InputValidator.validateTravelClass(flight.getTravelClass());
    }
}
