package flightreservations.dao.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import flightreservations.dao.BookingDAO;
import flightreservations.exception.DataAccessException;
import flightreservations.model.Booking;
import flightreservations.model.Customer;

/**
 * Binary file implementation of {@link BookingDAO}.
 * <p>
 * Each customer has its own bookings file, opened from the path stored in the
 * database ({@link Customer#getPath()}). The file is a sequence of fixed-size
 * records written with {@link DataOutputStream}:
 * </p>
 * <pre>
 * int  flightId     4 bytes
 * long bookingDate  8 bytes (days since 1970-01-01)
 * </pre>
 * <p>
 * The file and its folder are created on the first booking. A missing file
 * means the customer has no bookings yet.
 * </p>
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
final class BookingDAOImpl implements BookingDAO {
    /** Size in bytes of one booking record. */
    static final int RECORD_SIZE = Integer.BYTES + Long.BYTES;

    /** Only {@link DAOFactory} creates the instance. */
    BookingDAOImpl() {
    }

    /** {@inheritDoc} */
    @Override
    public void insert(Customer customer, Booking booking) throws DataAccessException {
        File file = bookingsFile(customer);
        createParentFolder(file);
        try (DataOutputStream output = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(file, true)))) {
            output.writeInt(booking.getFlightId());
            output.writeLong(booking.getBookingDate().toEpochDay());
        } catch (IOException e) {
            throw new DataAccessException("Could not save the booking in " + file.getPath() + ".", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<Booking> findByCustomer(Customer customer) throws DataAccessException {
        File file = bookingsFile(customer);
        List<Booking> bookings = new ArrayList<>();
        if (!file.exists()) {
            return bookings;
        }
        long recordCount = countRecords(file);
        try (DataInputStream input = new DataInputStream(
                new BufferedInputStream(new FileInputStream(file)))) {
            for (long record = 0; record < recordCount; record++) {
                int flightId = input.readInt();
                LocalDate bookingDate = LocalDate.ofEpochDay(input.readLong());
                bookings.add(new Booking(flightId, bookingDate));
            }
        } catch (IOException e) {
            throw new DataAccessException("Could not read the bookings in " + file.getPath() + ".", e);
        }
        return bookings;
    }

    /** {@inheritDoc} */
    @Override
    public boolean exists(Customer customer, int flightId) throws DataAccessException {
        return findByCustomer(customer).stream()
                .anyMatch(booking -> booking.getFlightId() == flightId);
    }

    /**
     * Gets the bookings file of a customer from the path stored in the
     * database.
     *
     * @param customer the customer loaded from the database
     * @return the customer's bookings file
     * @throws DataAccessException if the customer has no path
     */
    private static File bookingsFile(Customer customer) throws DataAccessException {
        String path = customer.getPath();
        if (path == null || path.isBlank()) {
            throw new DataAccessException("Customer " + customer.getCustomerId() + " has no bookings file path.");
        }
        return new File(path);
    }

    /**
     * Creates the folder of a file if it does not exist yet.
     *
     * @param file the file whose folder is needed
     * @throws DataAccessException if the folder cannot be created
     */
    private static void createParentFolder(File file) throws DataAccessException {
        File folder = file.getParentFile();
        if (folder != null && !folder.isDirectory() && !folder.mkdirs()) {
            throw new DataAccessException("Could not create the folder " + folder.getPath() + ".");
        }
    }

    /**
     * Counts the records of a bookings file from its size.
     *
     * @param file an existing bookings file
     * @return the number of records
     * @throws DataAccessException if the size is not a whole number of
     *                             records, which means the file is corrupted
     */
    private static long countRecords(File file) throws DataAccessException {
        long length = file.length();
        if (length % RECORD_SIZE != 0) {
            throw new DataAccessException("The bookings file " + file.getPath() + " is corrupted.");
        }
        return length / RECORD_SIZE;
    }
}
