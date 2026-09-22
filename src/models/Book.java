public class Book {
    private int customerId;
    private int flightId;
    public Book(int customerId, int flightId) {
        this.customerId = customerId;
        this.flightId = flightId;
    }
    public int getCustomerId() {
        return customerId;
    }
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    public int getFlightId() {
        return flightId;
    }
    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }
    @Override
    public String toString() {
        return "Book [customerId=" + customerId + ", flightId=" + flightId + "]";
    }
}
