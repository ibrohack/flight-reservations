package flightreservations.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import flightreservations.exception.DataAccessException;

/**
 * Singleton that manages the access to the MySQL database.
 * <p>
 * The only instance loads the connection settings from
 * {@value #SETTINGS_FILE} and checks the JDBC driver once. Each call to
 * {@link #getConnection()} opens a new, short-lived connection that the
 * caller must close with try-with-resources. No credentials are written in
 * the code.
 * </p>
 * <p>
 * Singleton pattern: {@code final} class, {@code private} constructor, one
 * {@code private static} instance and a {@code synchronized}
 * {@link #getInstance()} as the only access point. It is created lazily
 * because loading the settings can fail; in that case no instance is kept
 * and the next call tries again, so the settings can be fixed without
 * restarting the application.
 * </p>
 *
 * @author Brayan
 * @author Ekaitz
 * @author Aritz
 */
public final class ConnectionManager {
    private static final String SETTINGS_FILE = "config/db.properties";
    private static final String SETTINGS_TEMPLATE_FILE = "config/db.properties.example";
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static final String URL_KEY = "db.url";
    private static final String USER_KEY = "db.user";
    private static final String PASSWORD_KEY = "db.password";

    /** The only instance, created on the first successful call. */
    private static ConnectionManager instance;

    private final String url;
    private final String user;
    private final String password;

    /**
     * Loads the settings and checks the driver.
     *
     * @throws DataAccessException if the settings or the driver are missing
     */
    private ConnectionManager() throws DataAccessException {
        Properties settings = loadSettings();
        url = requireSetting(settings, URL_KEY);
        user = requireSetting(settings, USER_KEY);
        password = settings.getProperty(PASSWORD_KEY, "");
        loadDriver();
    }

    /**
     * Returns the only instance, creating it on the first call.
     *
     * @return the connection manager
     * @throws DataAccessException if the settings file is missing or
     *                             incomplete, or the JDBC driver is not in
     *                             the classpath
     */
    public static synchronized ConnectionManager getInstance() throws DataAccessException {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }

    /**
     * Opens a new connection to the database. The caller owns the connection
     * and must close it, ideally with try-with-resources.
     *
     * @return an open connection
     * @throws DataAccessException if the database cannot be reached or the
     *                             credentials are wrong
     */
    public Connection getConnection() throws DataAccessException {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new DataAccessException("Cannot connect to the database. Check that MySQL is running and that "
                    + SETTINGS_FILE + " has the right URL and credentials.", e);
        }
    }

    /**
     * Reads the settings file.
     *
     * @return the loaded settings
     * @throws DataAccessException if the file does not exist or cannot be read
     */
    private static Properties loadSettings() throws DataAccessException {
        File file = new File(SETTINGS_FILE);
        if (!file.isFile()) {
            throw new DataAccessException("Database settings not found. Copy " + SETTINGS_TEMPLATE_FILE
                    + " to " + SETTINGS_FILE + " and set your MySQL credentials.");
        }
        Properties settings = new Properties();
        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            settings.load(reader);
        } catch (IOException e) {
            throw new DataAccessException("Cannot read the database settings in " + SETTINGS_FILE + ".", e);
        }
        return settings;
    }

    /**
     * Gets a setting that must have a value.
     *
     * @param settings the loaded settings
     * @param key      the setting key
     * @return the trimmed value
     * @throws DataAccessException if the setting is missing or empty
     */
    private static String requireSetting(Properties settings, String key) throws DataAccessException {
        String value = settings.getProperty(key, "").trim();
        if (value.isEmpty()) {
            throw new DataAccessException("The setting '" + key + "' is missing in " + SETTINGS_FILE + ".");
        }
        return value;
    }

    /**
     * Checks that the MySQL JDBC driver is available.
     *
     * @throws DataAccessException if the driver class is not in the classpath
     */
    private static void loadDriver() throws DataAccessException {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            throw new DataAccessException("The MySQL JDBC driver was not found. Add the "
                    + "mysql-connector-j jar from lib/ to the classpath.", e);
        }
    }
}
