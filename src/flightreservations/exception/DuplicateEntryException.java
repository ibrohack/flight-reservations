package flightreservations.exception;

/**
 * Thrown by the data layer when an insert breaks a unique key, for example an
 * airline IATA code or a customer email that already exists.
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
public class DuplicateEntryException extends DataAccessException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception with a message and the original cause.
     *
     * @param message the description of the duplicated value
     * @param cause   the original exception
     */
    public DuplicateEntryException(String message, Throwable cause) {
        super(message, cause);
    }
}
