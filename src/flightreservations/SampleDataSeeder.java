package flightreservations;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import flightreservations.dao.BookingDAO;
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
            // Booked flight IDs per customer. They must match the sample data
            // and seat counts in database/airlinedb_script.sql.
            writeBookings(1, new int[] {1, 4, 5});
            writeBookings(2, new int[] {2, 3, 8});
            writeBookings(3, new int[] {1, 7});
            System.out.println("Sample booking files written to " + CUSTOMERS_FOLDER.getPath() + ".");
        } catch (DataAccessException | IOException | IllegalStateException e) {
            System.err.println("Could not write the sample booking files: " + e.getMessage());
            System.exit(EXIT_FAILURE);
        }
    }

    /**
     * Writes the sample bookings of one sample customer.
     *
     * @param customerId the ID of the customer
     * @param flightIds  the IDs of the flights the customer booked
     * @throws DataAccessException   if the customer cannot be read or a
     *                               booking cannot be written
     * @throws IllegalStateException if the customer is not in the database
     */
    private static void writeBookings(int customerId, int[] flightIds) throws DataAccessException {
        Customer customer = DAOFactory.getInstance().getCustomerDao().findById(customerId);
        if (customer == null) {
            throw new IllegalStateException(
                    "Customer " + customerId + " not found. Run database/airlinedb_script.sql first.");
        }
        BookingDAO bookingDao = DAOFactory.getInstance().getBookingDao();
        LocalDate bookingDate = LocalDate.now().minusDays(BOOKING_DAYS_AGO);
        for (int flightId : flightIds) {
            bookingDao.insert(customer, new Booking(flightId, bookingDate));
        }
        System.out.println("Customer " + customerId + ": " + flightIds.length
                + " bookings -> " + customer.getPath());
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
