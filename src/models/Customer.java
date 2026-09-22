public class Customer {
    private int customerID;
    private String customerName;
    private String email;
    private String phoneNumber;
    private String route;

    public Customer(int customerID, String customerName, String email, String phoneNumber, String route) {
        this.customerID = customerID;
        this.customerName = customerName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.route = route;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    @Override
    public String toString() {
        return "Customer {\n" +
                "  customerID   : " + customerID + "\n" +
                "  customerName : " + customerName + "\n" +
                "  email        : " + email + "\n" +
                "  phoneNumber  : " + phoneNumber + "\n" +
                "  route        : " + route + "\n" +
                "}";
    }

}
