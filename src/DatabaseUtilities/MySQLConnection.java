
package DatabaseUtilities;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Class        MySQLConnection
 * File         MySQLConnection.java
 * Description  An interface representing the connection constants and methods
 *              required to connect to the MySQL database for the World Countries
 *              Database Application. It includes methods for establishing
 *              connections, loading the driver, and creating the database if
 *              it doesn't already exist.
 * Platform     JDK 23.0.1; NetBeans IDE 23; macOS Sequoia 15.0.1
 * Course       CS 143
 * Hours        17 minutes
 * Date         10/25/2024
 * History Log  7/18/2018, 5/7/2020, 10/25/2024
 * @version     1.0
 * @see javax.swing.JFrame
 * @see java.awt.Toolkit
 * </pre>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
public interface MySQLConnection
{

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Constant      SERVER_URL
     * Description   The URL for connecting to the MySQL server without specifying
     *               a database. Used for initial connection checks.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public static final String SERVER_URL = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC";
    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Constant      WORLD_DB_URL
     * Description   The URL for connecting to the "WorldDB" database. This is the
     *               main database used by the World Countries Database Application.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public static final String WORLD_DB_URL = "jdbc:mysql://localhost:3306/WorldDB?useSSL=false&serverTimezone=UTC";
    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Constant      DB_PROPERTIES_FILE
     * Description   Name of the (gitignored) properties file holding the MySQL
     *               credentials. Looked up in the working directory first, then
     *               on the classpath. See db.properties.example.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public static final String DB_PROPERTIES_FILE = "db.properties";
    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Constant      USER
     * Description   The username for authenticating with the MySQL server,
     *               read from the "db.user" key in db.properties.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public static final String USER = loadDbProperty("db.user");
    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Constant      PASS
     * Description   The password for authenticating with the MySQL server,
     *               read from the "db.password" key in db.properties.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public static final String PASS = loadDbProperty("db.password");
    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Constant      MYSQL_DRIVER
     * Description   The fully qualified class name of the MySQL JDBC driver.
     *               Loading this driver allows JDBC to connect to MySQL databases.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";  // Correct driver  

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        loadDbProperty
     * Description   Reads a single key from db.properties, checking the working
     *               directory first and then the classpath.
     * @param        key String - the property name to read.
     * @return       String - the property value.
     * @throws IllegalStateException if the file or key is missing.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private static String loadDbProperty(String key)
    {
        Properties props = new Properties();
        Path file = Path.of(DB_PROPERTIES_FILE);
        try (InputStream in = Files.exists(file)
                ? Files.newInputStream(file)
                : MySQLConnection.class.getClassLoader().getResourceAsStream(DB_PROPERTIES_FILE))
        {
            if (in == null)
            {
                throw new IllegalStateException("Missing " + DB_PROPERTIES_FILE
                        + ". Copy db.properties.example to " + DB_PROPERTIES_FILE
                        + " in the project root and fill in your MySQL credentials.");
            }
            props.load(in);
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Unable to read " + DB_PROPERTIES_FILE, e);
        }

        String value = props.getProperty(key);
        if (value == null)
        {
            throw new IllegalStateException("Missing \"" + key + "\" in " + DB_PROPERTIES_FILE);
        }
        return value.trim();
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        loadDriver
     * Description   Loads the MySQL JDBC driver class. This method is optional in
     *               modern JDBC but included as good practice for backwards compatibility.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    static void loadDriver()
    {
        try
        {
            Class.forName(MYSQL_DRIVER);
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            System.out.println("Error: Unable to load MySQL driver.");
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        connectToServer
     * Description   Establishes a connection to the MySQL server using the
     *               SERVER_URL, USER, and PASS constants. Returns a Connection object.
     * @return       Connection - The connection object to the MySQL server.
     * @throws SQLException if an error occurs while connecting.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    static Connection connectToServer() throws SQLException
    {
        return DriverManager.getConnection(SERVER_URL, USER, PASS);
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        connectToWorldDB
     * Description   Establishes a connection to the "WorldDB" database. This method
     *               assumes the database already exists. Returns a Connection object.
     * @return       Connection - The connection object to the WorldDB database.
     * @throws SQLException if an error occurs while connecting.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    static Connection connectToWorldDB() throws SQLException
    {
        return DriverManager.getConnection(WORLD_DB_URL, USER, PASS);
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        createWorldDB
     * Description   Loads the MySQL driver, establishes a connection to the server,
     *               and creates the "WorldDB" database if it does not already exist.
     * @throws       SQLException if an error occurs during the database creation process.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    static void createWorldDB() throws SQLException
    {
        loadDriver();  // Load the driver
        try (Connection connection = connectToServer(); Statement stmt = connection.createStatement())
        {

            // Check if the database already exists, if not create it
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS WorldDB");
        }
    }

}
