package flightreservations.service;

import java.util.List;
import java.util.Objects;

import flightreservations.dao.CustomerDAO;
import flightreservations.exception.DataAccessException;
import flightreservations.exception.DuplicateEntryException;
import flightreservations.exception.ValidationException;
import flightreservations.model.Customer;
import flightreservations.util.InputValidator;

/**
 * Business logic for customers.
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
public class CustomerService {
    private final CustomerDAO customerDao;

    /**
     * Creates the service.
     *
     * @param customerDao the customer data access object
     */
    public CustomerService(CustomerDAO customerDao) {
        this.customerDao = Objects.requireNonNull(customerDao);
    }

    /**
     * Validates and registers a new customer. On success the customer gets
     * its generated ID, its bookings file path and normalized values.
     *
     * @param customer the new {@link Customer}
     * @throws ValidationException if a value is invalid or the email is
     *                             already registered
     * @throws DataAccessException if the customer cannot be saved
     */
    public void registerCustomer(Customer customer) throws ValidationException, DataAccessException {
        customer.setCustomerName(InputValidator.validateCustomerName(customer.getCustomerName()));
        customer.setEmail(InputValidator.validateEmail(customer.getEmail()));
        customer.setPhoneNumber(InputValidator.validatePhoneNumber(customer.getPhoneNumber()));
        try {
            customerDao.insert(customer);
        } catch (DuplicateEntryException e) {
            throw new ValidationException(e.getMessage(), e);
        }
    }

    /**
     * Gets all registered customers, ordered by name.
     *
     * @return a {@link List} of customers, empty if there are none
     * @throws DataAccessException if the customers cannot be read
     */
    public List<Customer> getAllCustomers() throws DataAccessException {
        return customerDao.findAll();
    }
}
