package flightreservations.exception;

/**
 * Thrown by the console when the standard input is closed (for example with
 * Ctrl+Z or Ctrl+D), so the application can end cleanly instead of looping.
 * It is unchecked because it can happen on any read and always ends the
 * program.
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
public class InputClosedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception with the original cause.
     *
     * @param cause the exception raised by the input reader
     */
    public InputClosedException(Throwable cause) {
        super("The standard input was closed.", cause);
    }
}
