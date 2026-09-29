package flightreservations.service;

import java.util.List;

import flightreservations.dao.AirlineDAO;
import flightreservations.exception.DataAccessException;
import flightreservations.exception.DuplicateEntryException;
import flightreservations.exception.ValidationException;
import flightreservations.model.Airline;
import flightreservations.util.InputValidator;

/**
 * Business logic for airlines.
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
public class AirlineService {
    private final AirlineDAO airlineDao;

    /**
     * Creates the service.
     *
     * @param airlineDao the airline data access object
     */
    public AirlineService(AirlineDAO airlineDao) {
        this.airlineDao = airlineDao;
    }

    /**
     * Validates and registers a new airline. On success the airline gets its
     * generated ID and normalized values.
     *
     * @param airline the new {@link Airline}
     * @throws ValidationException if a value is invalid or the IATA code is
     *                             already registered
     * @throws DataAccessException if the airline cannot be saved
     */
    public void registerAirline(Airline airline) throws ValidationException, DataAccessException {
        airline.setAirlineName(InputValidator.validateAirlineName(airline.getAirlineName()));
        airline.setCountry(InputValidator.validateCountry(airline.getCountry()));
        airline.setIataCode(InputValidator.validateIataCode(airline.getIataCode()));
        try {
            airlineDao.insert(airline);
        } catch (DuplicateEntryException e) {
            throw new ValidationException(e.getMessage(), e);
        }
    }

    /**
     * Gets all registered airlines, ordered by name.
     *
     * @return a {@link List} of airlines, empty if there are none
     * @throws DataAccessException if the airlines cannot be read
     */
    public List<Airline> getAllAirlines() throws DataAccessException {
        return airlineDao.findAll();
    }
}
