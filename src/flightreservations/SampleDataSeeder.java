package flightreservations;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import flightreservations.dao.BookingDAO;
import flightreservations.dao.CustomerDAO;
import flightreservations.dao.impl.DAOFactory;
import flightreservations.exception.DataAccessException;
import flightreservations.model.Booking;
import flightreservations.model.Customer;

/**
 * Development tool that rebuilds the sample booking files in
 * {@code data/customers/}.
 * <p>
 * The sample bookings include past flights, which cannot be booked through
 * the application, so they are written here directly with the
 * {@link BookingDAO}. Each file is opened from the path stored in the
 * database, exactly as the application does. Run
 * {@code database/airlinedb_script.sql} first, then run this class from the
 * project root.
 * </p>
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
public final class SampleDataSeeder {
    private static final File CUSTOMERS_FOLDER = new File("data/customers");
    private static final int BOOKING_DAYS_AGO = 90;
    private static final int EXIT_FAILURE = 1;

    /**
     * Booked flight IDs per customer ID. Must match the sample data and seat
     * counts in {@code database/airlinedb_script.sql}.
     */
    private static final Map<Integer, List<Integer>> SAMPLE_BOOKINGS = Map.of(
            1, List.of(1, 4, 5),
            2, List.of(2, 3, 8),
            3, List.of(1, 7));

    /** Tool class: not meant to be instantiated. */
    private SampleDataSeeder() {
    }

    /**
     * Deletes the existing booking files and writes the sample ones.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        try {
            deleteRecursively(CUSTOMERS_FOLDER);
            writeSampleBookings(DAOFactory.getInstance());
            System.out.println("Sample booking files written to " + CUSTOMERS_FOLDER.getPath() + ".");
        } catch (DataAccessException | IOException | IllegalStateException e) {
            System.err.println("Could not write the sample booking files: " + e.getMessage());
            System.exit(EXIT_FAILURE);
        }
    }

    /**
     * Writes the sample bookings of every sample customer.
     *
     * @param daoFactory the factory that provides the DAOs
     * @throws DataAccessException   if a customer cannot be read or a booking
     *                               cannot be written
     * @throws IllegalStateException if a sample customer is not in the
     *                               database
     */
    private static void writeSampleBookings(DAOFactory daoFactory) throws DataAccessException {
        CustomerDAO customerDao = daoFactory.getCustomerDao();
        BookingDAO bookingDao = daoFactory.getBookingDao();
        LocalDate bookingDate = LocalDate.now().minusDays(BOOKING_DAYS_AGO);
        for (Map.Entry<Integer, List<Integer>> entry : new TreeMap<>(SAMPLE_BOOKINGS).entrySet()) {
            int customerId = entry.getKey();
            Customer customer = customerDao.findById(customerId).orElseThrow(() -> new IllegalStateException(
                    "Customer " + customerId + " not found. Run database/airlinedb_script.sql first."));
            for (int flightId : entry.getValue()) {
                bookingDao.insert(customer, new Booking(flightId, bookingDate));
            }
            System.out.println("Customer " + customerId + ": " + entry.getValue().size()
                    + " bookings -> " + customer.getPath());
        }
    }

    /**
     * Deletes a file or a folder with all its content. Does nothing if it
     * does not exist.
     *
     * @param file the file or folder to delete
     * @throws IOException if something cannot be deleted
     */
    private static void deleteRecursively(File file) throws IOException {
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                deleteRecursively(child);
            }
        }
        if (file.exists() && !file.delete()) {
            throw new IOException("Could not delete " + file.getPath() + ".");
        }
    }
}
