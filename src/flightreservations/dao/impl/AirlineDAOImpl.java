package flightreservations.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import flightreservations.config.ConnectionManager;
import flightreservations.dao.AirlineDAO;
import flightreservations.exception.DataAccessException;
import flightreservations.exception.DuplicateEntryException;
import flightreservations.model.Airline;

/**
 * JDBC implementation of {@link AirlineDAO} on the {@code airline} table.
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
final class AirlineDAOImpl implements AirlineDAO {
    private static final String SQL_INSERT =
            "INSERT INTO airline (airlineName, country, iataCode) VALUES (?, ?, ?)";
    private static final String SQL_SELECT =
            "SELECT airlineId, airlineName, country, iataCode FROM airline";
    private static final String SQL_FIND_BY_ID = SQL_SELECT + " WHERE airlineId = ?";
    private static final String SQL_FIND_ALL = SQL_SELECT + " ORDER BY airlineName";

    /** Only {@link DAOFactory} creates the instance. */
    AirlineDAOImpl() {
    }

    /** {@inheritDoc} */
    @Override
    public void insert(Airline airline) throws DataAccessException {
        try (Connection connection = ConnectionManager.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(SQL_INSERT,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, airline.getAirlineName());
            statement.setString(2, airline.getCountry());
            statement.setString(3, airline.getIataCode());
            statement.executeUpdate();
            airline.setAirlineId(ResultSetMapper.toGeneratedKey(statement));
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DuplicateEntryException(
                    "An airline with IATA code " + airline.getIataCode() + " already exists.", e);
        } catch (SQLException e) {
            throw new DataAccessException("Could not save the airline " + airline.getIataCode() + ".", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Airline> findById(int airlineId) throws DataAccessException {
        try (Connection connection = ConnectionManager.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(SQL_FIND_BY_ID)) {
            statement.setInt(1, airlineId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(ResultSetMapper.toAirline(resultSet)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not read the airline " + airlineId + ".", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<Airline> findAll() throws DataAccessException {
        try (Connection connection = ConnectionManager.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(SQL_FIND_ALL);
                ResultSet resultSet = statement.executeQuery()) {
            List<Airline> airlines = new ArrayList<>();
            while (resultSet.next()) {
                airlines.add(ResultSetMapper.toAirline(resultSet));
            }
            return airlines;
        } catch (SQLException e) {
            throw new DataAccessException("Could not read the airlines.", e);
        }
    }
}
