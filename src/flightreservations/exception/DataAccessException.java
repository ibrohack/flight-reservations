package flightreservations.exception;

/**
 * Thrown when the data layer cannot complete an operation: a database error,
 * a file error or a missing configuration. It always carries a message that
 * explains the failure and, when available, the original cause.
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
public class DataAccessException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception with a message.
     *
     * @param message the description of the failure
     */
    public DataAccessException(String message) {
        super(message);
    }

    /**
     * Creates the exception with a message and the original cause.
     *
     * @param message the description of the failure
     * @param cause   the original exception
     */
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
