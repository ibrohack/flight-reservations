package flightreservations.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import flightreservations.config.ConnectionManager;
import flightreservations.dao.FlightDAO;
import flightreservations.exception.DataAccessException;
import flightreservations.model.Flight;

/**
 * JDBC implementation of {@link FlightDAO} on the {@code flight} table.
 * <p>
 * Every query joins the {@code airline} table, so each {@link Flight} is
 * returned with its airline in a single round trip.
 * </p>
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
final class FlightDAOImpl implements FlightDAO {
    private static final String SQL_INSERT = "INSERT INTO flight "
            + "(origin, destination, departureDate, seatAmount, travelClass, airlineId) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_SELECT = "SELECT f.flightId, f.origin, f.destination, f.departureDate, "
            + "f.seatAmount, f.travelClass, a.airlineId, a.airlineName, a.country, a.iataCode "
            + "FROM flight f JOIN airline a ON a.airlineId = f.airlineId";
    private static final String ORDER_BY_DEPARTURE = " ORDER BY f.departureDate, f.flightId";
    private static final String SQL_FIND_BY_ID = SQL_SELECT + " WHERE f.flightId = ?";
    private static final String SQL_FIND_DEPARTING_AFTER =
            SQL_SELECT + " WHERE f.departureDate > ?" + ORDER_BY_DEPARTURE;
    private static final String SQL_FIND_BY_IDS = SQL_SELECT + " WHERE f.flightId IN (%s)" + ORDER_BY_DEPARTURE;
    private static final String SQL_RESERVE_SEAT =
            "UPDATE flight SET seatAmount = seatAmount - 1 WHERE flightId = ? AND seatAmount > 0";
    private static final String SQL_RELEASE_SEAT =
            "UPDATE flight SET seatAmount = seatAmount + 1 WHERE flightId = ?";
    private static final String PLACEHOLDER = "?";
    private static final String PLACEHOLDER_SEPARATOR = ", ";

    /** Only {@link DAOFactory} creates the instance. */
    FlightDAOImpl() {
    }

    /** {@inheritDoc} */
    @Override
    public void insert(Flight flight) throws DataAccessException {
        try (Connection connection = ConnectionManager.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(SQL_INSERT,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, flight.getOrigin());
            statement.setString(2, flight.getDestination());
            statement.setObject(3, flight.getDepartureDate());
            statement.setInt(4, flight.getSeatAmount());
            statement.setString(5, flight.getTravelClass().name());
            statement.setInt(6, flight.getAirline().getAirlineId());
            statement.executeUpdate();
            flight.setFlightId(ResultSetMapper.toGeneratedKey(statement));
        } catch (SQLException e) {
            throw new DataAccessException("Could not save the flight " + flight.getOrigin()
                    + " - " + flight.getDestination() + ".", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Flight> findById(int flightId) throws DataAccessException {
        try (Connection connection = ConnectionManager.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(SQL_FIND_BY_ID)) {
            statement.setInt(1, flightId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(ResultSetMapper.toFlight(resultSet)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not read the flight " + flightId + ".", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<Flight> findDepartingAfter(LocalDate date) throws DataAccessException {
        try (Connection connection = ConnectionManager.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(SQL_FIND_DEPARTING_AFTER)) {
            statement.setObject(1, date);
            return readFlights(statement);
        } catch (SQLException e) {
            throw new DataAccessException("Could not read the flights departing after " + date + ".", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<Flight> findByIds(Collection<Integer> flightIds) throws DataAccessException {
        if (flightIds.isEmpty()) {
            return new ArrayList<>();
        }
        // Only "?" placeholders are added to the SQL; every ID is still bound as a parameter.
        String placeholders = String.join(PLACEHOLDER_SEPARATOR, Collections.nCopies(flightIds.size(), PLACEHOLDER));
        String sql = String.format(SQL_FIND_BY_IDS, placeholders);
        try (Connection connection = ConnectionManager.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            int parameterIndex = 1;
            for (int flightId : flightIds) {
                statement.setInt(parameterIndex++, flightId);
            }
            return readFlights(statement);
        } catch (SQLException e) {
            throw new DataAccessException("Could not read the flights " + flightIds + ".", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean reserveSeat(int flightId) throws DataAccessException {
        return updateSeats(SQL_RESERVE_SEAT, flightId) > 0;
    }

    /** {@inheritDoc} */
    @Override
    public void releaseSeat(int flightId) throws DataAccessException {
        if (updateSeats(SQL_RELEASE_SEAT, flightId) == 0) {
            throw new DataAccessException("Could not give back the seat: flight " + flightId + " was not found.");
        }
    }

    /**
     * Runs a seat update on one flight.
     *
     * @param sql      the update statement, with the flight ID as its only
     *                 parameter
     * @param flightId the ID of the flight
     * @return the number of rows updated
     * @throws DataAccessException if the update fails
     */
    private static int updateSeats(String sql, int flightId) throws DataAccessException {
        try (Connection connection = ConnectionManager.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, flightId);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not update the seats of flight " + flightId + ".", e);
        }
    }

    /**
     * Runs a prepared flight query and maps every row.
     *
     * @param statement the query, with its parameters already set
     * @return the flights returned by the query
     * @throws SQLException if the query fails
     */
    private static List<Flight> readFlights(PreparedStatement statement) throws SQLException {
        try (ResultSet resultSet = statement.executeQuery()) {
            List<Flight> flights = new ArrayList<>();
            while (resultSet.next()) {
                flights.add(ResultSetMapper.toFlight(resultSet));
            }
            return flights;
        }
    }
}
