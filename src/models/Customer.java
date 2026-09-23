package models;
/**
 * Model for {@link Customer} entities.
 * <p>
 * Defines the structure and behavior of customer objects, including their
 * attributes and methods.
 * </p>
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 * 
 */
public class Customer {
    /** The unique ID of the customer. */
    private int customerID;
    /** The name of the customer. */
    private String customerName;
    /** The email address of the customer. */
    private String email;
    /** The phone number of the customer. */
    private String phoneNumber;
    /** The profile picture file path associated with the customer. */
    private String path;

    /**
     * Constructs a new {@link Customer} object with default values.
     */
    public Customer() {
        this.customerID = 0;
        this.customerName = "";
        this.email = "";
        this.phoneNumber = "";
        this.path = "";
    }

    /**
     * Constructs a new {@link Customer} object with the specified attributes.
     *
     * @param customerID   the unique ID of the customer
     * @param customerName the name of the customer
     * @param email        the email address of the customer
     * @param phoneNumber  the phone number of the customer
     * @param path         the profile picture file path associated with the
     *                     customer
     */
    public Customer(int customerID, String customerName, String email, String phoneNumber, String path) {
        this.customerID = customerID;
        this.customerName = customerName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.path = path;
    }

    /**
     * Gets the unique ID of the customer.
     * 
     * @return int
     */
    public int getCustomerID() {
        return customerID;
    }

    /**
     * Sets the unique ID of the customer.
     * 
     * @param customerID
     */
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    /**
     * Gets the name of the customer.
     * 
     * @return String
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets the name of the customer.
     * 
     * @param customerName
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Gets the email address of the customer.
     * 
     * @return String
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the customer.
     * 
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the phone number of the customer.
     * 
     * @return String
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number of the customer.
     * 
     * @param phoneNumber
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the profile picture file path associated with the customer.
     * 
     * @return String
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the profile picture file path associated with the customer.
     * 
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 
     * <p>
     * Returns a pretty printed string of the {@link Customer} object.
     * </p>
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "Customer {\n" +
                "  customerID   : " + customerID + "\n" +
                "  customerName : " + customerName + "\n" +
                "  email        : " + email + "\n" +
                "  phoneNumber  : " + phoneNumber + "\n" +
                "  path        : " + path + "\n" +
                "}";
    }

}
