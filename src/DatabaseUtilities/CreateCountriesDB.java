
package DatabaseUtilities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Class        CreateCountriesDB
 * File         CreateCountriesDB.java
 * Description  Manages the setup and population of the World Countries Database.
 *              This class handles the creation of database tables and the
 *              importation of CSV data for country abbreviations, details, and
 *              video links.
 * Platform     JDK 23.0.1; NetBeans IDE 23; macOS Sequoia 15.0.1
 * Date         10/25/2024
 * @version     1.0
 * </pre>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
public class CreateCountriesDB
{

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Constant      COUNTRIES_ABREVIATE_CSV
     * Description   Path to the CSV file containing country abbreviations.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private static final String COUNTRIES_ABREVIATE_CSV = "src/Data/countryAbreviate.csv";
    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Constant      COUNTRIES_A_CSV
     * Description   Path to the CSV file for the primary dataset of countries (Set A).
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private static final String COUNTRIES_A_CSV = "src/Data/countriesA.csv";
    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Constant      COUNTRIES_B_CSV
     * Description   Path to the CSV file for the secondary dataset of countries (Set B).
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private static final String COUNTRIES_B_CSV = "src/Data/countriesB.csv";
    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Constant      COUNTRY_INFO_CSV
     * Description   Path to the CSV file containing extended country information.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private static final String COUNTRY_INFO_CSV = "src/Data/countryInfo.csv";
    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Constant      COUNTRY_VIDEOS_CSV
     * Description   Path to the CSV file containing country video links.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private static final String COUNTRY_VIDEOS_CSV = "src/Data/countryVideos.csv";  // CSV for country videos

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        setupDatabase
     * Description   Ensures the required tables for the World Countries Database
     *               exist. If they don't, creates and populates them with data from CSV files.
     * @throws       SQLException if an error occurs during table creation or data import.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public static void setupDatabase() throws SQLException
    {
        MySQLConnection.createWorldDB();  // Create WorldDB if it doesn't exist yet
        Connection con = MySQLConnection.connectToWorldDB();
        con.setAutoCommit(false); // Disable auto-commit for transaction management
        Statement stmt = con.createStatement();

        try
        {
            // Check and create countriesAbreviate table (central reference table)
            if (!doesTableExist(con, "countriesAbreviate"))
            {
                stmt.executeUpdate("CREATE TABLE countriesAbreviate ("
                        + "country_code VARCHAR(2) NOT NULL, "
                        + "name VARCHAR(100) NOT NULL, "
                        + "initial_entry BOOLEAN NOT NULL DEFAULT TRUE, "
                        + "PRIMARY KEY (country_code))");
                System.out.println("Table countriesAbreviate created.");
                populateAbreviationTableFromCSV(con, "countriesAbreviate", COUNTRIES_ABREVIATE_CSV);
            }

            // Create dependent tables after `countriesAbreviate` is populated
            if (!doesTableExist(con, "countriesA"))
            {
                stmt.executeUpdate("CREATE TABLE countriesA ("
                        + "country_code VARCHAR(2) NOT NULL, "
                        + "name VARCHAR(100) NOT NULL, "
                        + "capital VARCHAR(100), "
                        + "population DOUBLE, "
                        + "river VARCHAR(100), "
                        + "area DOUBLE, "
                        + "PRIMARY KEY (country_code), "
                        + "FOREIGN KEY (country_code) REFERENCES countriesAbreviate(country_code))");
                System.out.println("Table countriesA created.");
                populateTableFromCSV(con, "countriesA", COUNTRIES_A_CSV);
            }

            if (!doesTableExist(con, "countriesB"))
            {
                stmt.executeUpdate("CREATE TABLE countriesB ("
                        + "country_code VARCHAR(2) NOT NULL, "
                        + "name VARCHAR(100) NOT NULL, "
                        + "capital VARCHAR(100), "
                        + "population DOUBLE, "
                        + "river VARCHAR(100), "
                        + "area DOUBLE, "
                        + "PRIMARY KEY (country_code), "
                        + "FOREIGN KEY (country_code) REFERENCES countriesAbreviate(country_code))");
                System.out.println("Table countriesB created.");
                populateTableFromCSV(con, "countriesB", COUNTRIES_B_CSV);
            }

            if (!doesTableExist(con, "country_info"))
            {
                stmt.executeUpdate("CREATE TABLE country_info ("
                        + "country_code VARCHAR(2) PRIMARY KEY, "
                        + "name TEXT, "
                        + "description TEXT, "
                        + "common_fact TEXT, "
                        + "lesser_known_fact TEXT, "
                        + "historical_info TEXT, "
                        + "FOREIGN KEY (country_code) REFERENCES countriesAbreviate(country_code))");
                System.out.println("Table country_info created.");
                populateCountryInfoFromCSV(con, COUNTRY_INFO_CSV);
            }

            if (!doesTableExist(con, "country_videos"))
            {
                stmt.executeUpdate("CREATE TABLE country_videos ("
                        + "country_code VARCHAR(2), "
                        + "name VARCHAR(100) NOT NULL, "
                        + "video_link TEXT NULL, " // Allow NULL values in video_link
                        + "FOREIGN KEY (country_code) REFERENCES countriesAbreviate(country_code))");
                System.out.println("Table country_videos created.");
                populateCountryVideosFromCSV(con, COUNTRY_VIDEOS_CSV);
            }

            con.commit();  // Commit transaction if no errors occurred
            //System.out.println("All tables created and populated successfully.");

        }
        catch (SQLException e)
        {
            con.rollback(); // Rollback in case of error
            System.out.println("Error setting up database: " + e.getMessage());
        }
        finally
        {
            stmt.close();
            con.close();
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        doesTableExist
     * Description   Checks if a specified table exists in the database.
     * @param        con Connection - The active database connection.
     * @param tableName String - Name of the table to check.
     * @return boolean - True if the table exists, false otherwise.
     * @throws SQLException if an error occurs during the metadata query.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private static boolean doesTableExist(Connection con, String tableName) throws SQLException
    {
        DatabaseMetaData dbm = con.getMetaData();
        ResultSet tables = dbm.getTables(null, null, tableName, null);
        return tables.next();
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        populateTableFromCSV
     * Description   Populates a specified table with data from a CSV file.
     * @param        con Connection - The active database connection.
     * @param tableName String - The table to populate.
     * @param csvFilePath String - Path to the CSV file.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private static void populateTableFromCSV(Connection con, String tableName, String csvFilePath)
    {
        String line;
        Pattern pattern = Pattern.compile(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");  // Regex to split CSV with quotes
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath)))
        {
            String insertSQL = "INSERT INTO " + tableName + " (country_code, name, capital, population, river, area) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(insertSQL);
            while ((line = br.readLine()) != null)
            {
                String[] data = splitCSVLine(line, pattern);  // Use the helper method to split correctly
                if (data.length == 6)
                {  // We expect 6 fields now (including country country_code)
                    pstmt.setString(1, data[0].replace("\"", ""));  // country_code (remove quotes if present)
                    pstmt.setString(2, data[1].replace("\"", ""));  // name (remove quotes if present)
                    pstmt.setString(3, data[2].replace("\"", ""));  // capital (remove quotes if present)
                    pstmt.setDouble(4, Double.parseDouble(data[3]));  // population
                    pstmt.setString(5, data[4].replace("\"", ""));  // river (remove quotes if present)
                    pstmt.setDouble(6, Double.parseDouble(data[5]));  // area
                    pstmt.executeUpdate();
                }
            }
            pstmt.close();
            System.out.println("Data populated for " + tableName + " from " + csvFilePath);
        }
        catch (IOException | SQLException e)
        {
            System.out.println("Error populating table " + tableName + " from CSV: " + e.getMessage());
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        populateCountryInfoFromCSV
     * Description   Populates the country_info table with extended country information
     *               from a CSV file.
     * @param        con Connection - The active database connection.
     * @param csvFilePath String - Path to the country info CSV file.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private static void populateCountryInfoFromCSV(Connection con, String csvFilePath)
    {
        String line;
        Pattern pattern = Pattern.compile(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");  // Regex to split by commas outside quotes

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath)))
        {
            String insertSQL = "INSERT INTO country_info (country_code, name, description, common_fact, lesser_known_fact, historical_info) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(insertSQL);

            while ((line = br.readLine()) != null)
            {
                // Split line by commas outside quotes
                String[] data = splitQuotedCSVLine(line, pattern);

                // Verify that there are exactly 5 fields
                if (data.length == 6)
                {
                    pstmt.setString(1, stripQuotes(data[0])); // country_code
                    pstmt.setString(2, stripQuotes(data[1])); // name
                    pstmt.setString(3, stripQuotes(data[2])); // description
                    pstmt.setString(4, stripQuotes(data[3])); // common_fact
                    pstmt.setString(5, stripQuotes(data[4])); // lesser_known_fact
                    pstmt.setString(6, stripQuotes(data[5])); // historical_info
                    pstmt.executeUpdate();
                }
                else
                {
                    System.out.println("Skipping line due to incorrect format: " + line);
                }
            }
            pstmt.close();
            System.out.println("Data populated for country_info from " + csvFilePath);

        }
        catch (IOException | SQLException e)
        {
            System.out.println("Error populating country_info table from CSV: " + e.getMessage());
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        splitQuotedCSVLine
     * Description   Helper method to split a CSV line, considering quoted fields with commas.
     * @param        line String - The CSV line to split.
     * @param pattern Pattern - The regex pattern for splitting by commas
     * outside quotes.
     * @return String[] - Array of fields from the CSV line.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private static String[] splitQuotedCSVLine(String line, Pattern pattern)
    {
        return pattern.split(line);
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        stripQuotes
     * Description   Helper method to strip quotes and sanitize a field.
     * @param        field String - The field to process.
     * @return String - Field with quotes removed and single quotes escaped.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private static String stripQuotes(String field)
    {
        return field.trim().replaceAll("^\"|\"$", "").replace("'", "''"); // Remove surrounding quotes and escape single quotes
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        populateCountryVideosFromCSV
     * Description   Populates the country_videos table with country video links from a CSV file.
     * @param        con Connection - The active database connection.
     * @param csvFilePath String - Path to the country videos CSV file.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private static void populateCountryVideosFromCSV(Connection con, String csvFilePath)
    {
        String line;
        Pattern pattern = Pattern.compile(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");  // Regex to split CSV with quotes
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath)))
        {
            String insertSQL = "INSERT INTO country_videos (country_code, name, video_link) VALUES (?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(insertSQL);
            while ((line = br.readLine()) != null)
            {
                String[] data = splitCSVLine(line, pattern);  // Use the helper method to split correctly
                if (data.length == 3)
                {
                    pstmt.setString(1, data[0].replace("\"", ""));  // country_code
                    pstmt.setString(2, data[1].replace("\"", ""));  // name (remove quotes if present)

                    // Check if video_link is empty and set it to NULL if so
                    String videoLink = data[2].replace("\"", "");
                    if (videoLink.isEmpty())
                    {
                        pstmt.setNull(3, Types.VARCHAR);  // Set NULL for empty video links
                    }
                    else
                    {
                        pstmt.setString(3, videoLink);  // Otherwise, set the actual video link
                    }

                    pstmt.executeUpdate();
                }
            }
            pstmt.close();
            System.out.println("Data populated for country_videos from " + csvFilePath);
        }
        catch (IOException | SQLException e)
        {
            System.out.println("Error populating country_videos table from CSV: " + e.getMessage());
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        splitCSVLine
     * Description   Helper method to split a CSV line, accounting for commas within quoted fields.
     * @param        line String - The line to split.
     * @param pattern Pattern - The regex pattern for splitting.
     * @return String[] - Array of split fields.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private static String[] splitCSVLine(String line, Pattern pattern)
    {
        Matcher matcher = pattern.matcher(line);
        return matcher.replaceAll("~").split("~");  // Replace commas inside quotes with a temp character and split
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        populateAbreviationTableFromCSV
     * Description   Populates the countriesAbreviate table with country abbreviations from a CSV file.
     * @param        con Connection - The active database connection.
     * @param tableName String - The abbreviation table to populate.
     * @param csvFilePath String - Path to the abbreviations CSV file.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private static void populateAbreviationTableFromCSV(Connection con, String tableName, String csvFilePath)
    {
        String line;
        Pattern pattern = Pattern.compile(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath)))
        {
            String insertSQL = "INSERT INTO " + tableName + " (country_code, name) VALUES (?,?)";
            PreparedStatement pstmt = con.prepareStatement(insertSQL);
            while ((line = br.readLine()) != null)
            {
                String[] data = splitCSVLine(line, pattern);
                if (data.length == 2)
                {
                    pstmt.setString(1, data[0].replace("\"", ""));
                    pstmt.setString(2, data[1].replace("\"", ""));
                    pstmt.executeUpdate();
                }
                else
                {
                    System.out.println("Skipping line in CSV due to incorrect format: " + line);
                }
            }
            pstmt.close();
            System.out.println("Data populated for " + tableName + " from " + csvFilePath);
        }
        catch (IOException | SQLException e)
        {
            System.out.println("Error populating table " + tableName + " from CSV: " + e.getMessage());
        }
    }

}
