package flightreservations.exception;

/**
 * Thrown when an entity requested by its ID does not exist.
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
public class EntityNotFoundException extends BusinessException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception for a missing entity.
     *
     * @param entityType the class of the missing entity, used in the message
     * @param id         the ID that was not found
     */
    public EntityNotFoundException(Class<?> entityType, int id) {
        super(entityType.getSimpleName() + " " + id + " does not exist.");
    }
}
