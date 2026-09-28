package flightreservations.exception;

/**
 * Base class for errors caused by a user action that breaks a business rule.
 * The message is meant to be shown to the user as it is.
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
public class BusinessException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception with a message.
     *
     * @param message the description of the broken rule
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * Creates the exception with a message and the original cause.
     *
     * @param message the description of the broken rule
     * @param cause   the original exception
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
