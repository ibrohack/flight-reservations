package flightreservations.exception;

/**
 * Thrown when a value entered by the user is not valid, for example an empty
 * name, a malformed email or a duplicated IATA code.
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
public class ValidationException extends BusinessException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception with a message.
     *
     * @param message the description of the invalid value
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Creates the exception with a message and the original cause.
     *
     * @param message the description of the invalid value
     * @param cause   the original exception
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
