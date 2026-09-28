package flightreservations.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import flightreservations.config.ConnectionManager;
import flightreservations.dao.CustomerDAO;
import flightreservations.exception.DataAccessException;
import flightreservations.exception.DuplicateEntryException;
import flightreservations.model.Customer;

/**
 * JDBC implementation of {@link CustomerDAO} on the {@code customer} table.
 * <p>
 * When a customer is inserted, the path of its bookings file is built from
 * the generated ID and saved in the same transaction, so a customer is never
 * stored without a path.
 * </p>
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
final class CustomerDAOImpl implements CustomerDAO {
    /** Location of each customer's bookings file, relative to the project root. */
    static final String BOOKINGS_PATH_PATTERN = "data/customers/%d/flights.dat";

    private static final String SQL_INSERT =
            "INSERT INTO customer (customerName, email, phoneNumber) VALUES (?, ?, ?)";
    private static final String SQL_UPDATE_PATH = "UPDATE customer SET path = ? WHERE customerId = ?";
    private static final String SQL_SELECT =
            "SELECT customerId, customerName, email, phoneNumber, path FROM customer";
    private static final String SQL_FIND_BY_ID = SQL_SELECT + " WHERE customerId = ?";
    private static final String SQL_FIND_ALL = SQL_SELECT + " ORDER BY customerName";

    /** Only {@link DAOFactory} creates the instance. */
    CustomerDAOImpl() {
    }

    /** {@inheritDoc} */
    @Override
    public void insert(Customer customer) throws DataAccessException {
        try (Connection connection = ConnectionManager.getInstance().getConnection()) {
            connection.setAutoCommit(false);
            try {
                int customerId = insertRow(connection, customer);
                String path = String.format(Locale.ROOT, BOOKINGS_PATH_PATTERN, customerId);
                updatePath(connection, customerId, path);
                connection.commit();
                customer.setCustomerId(customerId);
                customer.setPath(path);
            } catch (SQLException e) {
                rollback(connection, e);
                throw e;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DuplicateEntryException(
                    "A customer with email " + customer.getEmail() + " already exists.", e);
        } catch (SQLException e) {
            throw new DataAccessException("Could not save the customer " + customer.getEmail() + ".", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Customer> findById(int customerId) throws DataAccessException {
        try (Connection connection = ConnectionManager.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(SQL_FIND_BY_ID)) {
            statement.setInt(1, customerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(ResultSetMapper.toCustomer(resultSet)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not read the customer " + customerId + ".", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<Customer> findAll() throws DataAccessException {
        try (Connection connection = ConnectionManager.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(SQL_FIND_ALL);
                ResultSet resultSet = statement.executeQuery()) {
            List<Customer> customers = new ArrayList<>();
            while (resultSet.next()) {
                customers.add(ResultSetMapper.toCustomer(resultSet));
            }
            return customers;
        } catch (SQLException e) {
            throw new DataAccessException("Could not read the customers.", e);
        }
    }

    /**
     * Inserts the customer row inside the current transaction.
     *
     * @param connection the connection with the open transaction
     * @param customer   the customer to insert
     * @return the generated customer ID
     * @throws SQLException if the insert fails
     */
    private static int insertRow(Connection connection, Customer customer) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT,
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, customer.getCustomerName());
            statement.setString(2, customer.getEmail());
            statement.setString(3, customer.getPhoneNumber());
            statement.executeUpdate();
            return ResultSetMapper.toGeneratedKey(statement);
        }
    }

    /**
     * Saves the bookings file path inside the current transaction.
     *
     * @param connection the connection with the open transaction
     * @param customerId the ID of the customer
     * @param path       the bookings file path
     * @throws SQLException if the update fails
     */
    private static void updatePath(Connection connection, int customerId, String path) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_PATH)) {
            statement.setString(1, path);
            statement.setInt(2, customerId);
            statement.executeUpdate();
        }
    }

    /**
     * Rolls back the current transaction. A rollback failure is attached to
     * the original error instead of hiding it.
     *
     * @param connection the connection with the open transaction
     * @param cause      the error that caused the rollback
     */
    private static void rollback(Connection connection, SQLException cause) {
        try {
            connection.rollback();
        } catch (SQLException rollbackError) {
            cause.addSuppressed(rollbackError);
        }
    }
}
