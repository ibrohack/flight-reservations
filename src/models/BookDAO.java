import java.util.List;

/**
 * Data Access Object interface for {@link Book} entities.
 * <p>
 * Defines the contract for all booking related database operations, mainly CRUD operations
 * </p>
 *
 * @author Brayan, Ekaitz, Aritz
 * 
 */
public interface BookDAO {

    /**
     * Checks whether a given booking exists in the database.
     *
     * @param book the {@link Book} to verify
     * @return {@code true} if the booking exists, {@code false} otherwise
     */
    public boolean checkBook(Book book);

    /**
     * Inserts a new booking into the database.
     *
     * @param book the {@link Book} to insert
     * @return {@code true} if the insertion was successful, {@code false} otherwise
     */
    public boolean insertBook(Book book);

    /**
     * Deletes a booking from the database.
     *
     * @param book the {@link Book} to delete
     * @return {@code true} if the deletion was successful, {@code false} otherwise
     */
    public boolean deleteBook(Book book);

    /**
     * Updates an existing booking in the database.
     *
     * @param book the {@link Book} with updated information
     * @return {@code true} if the update was successful, {@code false} otherwise
     */
    public boolean updateBook(Book book);

    /**
     * Retrieves all bookings from the database.
     *
     * @return a {@link List} of all {@link Book} objects
     */
    public List<Book> getAllBooks();

    /**
     * Retrieves a booking by its name.
     *
     * @param name the name of the book to search for
     * @return the matching {@link Book}, or {@code null} if not found
     */
    public Book getBookByName(String name);


    /**
     * Retrieves a booking by its unique identifier.
     *
     * @param bookId the ID of the booking to retrieve
     * @return the matching {@link Book}, or {@code null} if not found
     */
    public Book getBookById(int bookId);

}