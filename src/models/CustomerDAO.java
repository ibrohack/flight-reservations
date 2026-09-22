import java.util.List;

/**
 * Data Access Object interface for {@link Customer} entities.
 * <p>
 * Defines the contract for all customer related database operations, mainly CRUD operations
 * </p>
 *
 * @author Brayan, Ekaitz, Aritz
 * 
 */
public interface CustomerDAO {

    /**
     * Checks whether a given customer exists in the database.
     *
     * @param customer the {@link Customer} to verify
     * @return {@code true} if the customer exists, {@code false} otherwise
     */
    public boolean checkCustomer(Customer customer);

    /**
     * Inserts a new customer into the database.
     *
     * @param customer the {@link Customer} to insert
     * @return {@code true} if the insertion was successful, {@code false} otherwise
     */
    public boolean insertCustomer(Customer customer);

    /**
     * Deletes a customer from the database.
     *
     * @param customer the {@link Customer} to delete
     * @return {@code true} if the deletion was successful, {@code false} otherwise
     */
    public boolean deleteCustomer(Customer customer);

    /**
     * Updates an existing customer in the database.
     *
     * @param customer the {@link Customer} with updated information
     * @return {@code true} if the update was successful, {@code false} otherwise
     */
    public boolean updateCustomer(Customer customer);

    /**
     * Retrieves all customers from the database.
     *
     * @return a {@link List} of all {@link Customer} objects
     */
    public List<Customer> getAllCustomers();

    /**
     * Retrieves a customer by its name.
     *
     * @param name the name of the customer to search for
     * @return the matching {@link Customer}, or {@code null} if not found
     */
    public Customer getCustomerByName(String name);


    /**
     * Retrieves a customer by its unique identifier.
     *
     * @param customerId the ID of the customer to retrieve
     * @return the matching {@link Customer}, or {@code null} if not found
     */
    public Customer getCustomerById(int customerId);

}