
package DatabaseUtilities;

import Utilities.Validation;
import worldcountriesdatabase.Country;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import worldcountriesdatabase.CountryInfo;
import worldcountriesdatabase.CountryVideoLink;

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Class        CountriesDBManager
 * File         CountriesDBManager.java
 * Description  This class manages database operations for the World Countries Database, 
 *              including CRUD operations for country information, video links, and additional 
 *              country details. It handles connections, queries, and updates to ensure data 
 *              consistency across related tables.
 * Platform     JDK 23.0.1; NetBeans IDE 23; macOS Sequoia 15.0.1
 * Date         11/5/2024
 * History log  11/1/2023, 4/14/2024, 10/30/2024, 11/5/2024
 * @version     2.0
 * </pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
public class CountriesDBManager
{

    private static final Logger logger = Logger.getLogger(CountriesDBManager.class.getName());
    private final Connection connection;
    private final String tableName;

    /**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Constructor     CountriesDBManager
 * File            CountriesDBManager.java
 * Description     Initializes the database connection and ensures the tables are created.
 * Platform        JDK 23.0.1; NetBeans IDE 23; macOS Sequoia 15.0.1
 * Date            11/5/2024
 * @param tableName the name of the table for country data.
 * @throws SQLException if a database access error occurs.
 * </pre>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

    public CountriesDBManager(String tableName) throws SQLException
    {
        this.tableName = tableName;
        CreateCountriesDB.setupDatabase();  // Ensures tables are created
        this.connection = MySQLConnection.connectToWorldDB();
    }

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        updateCountry
 * Description   Updates the details of an existing country in the database.
 * @param country the Country object containing updated data.
 * @throws SQLException if a database access error occurs or update fails.
 * </pre>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

    public void updateCountry(Country country) throws SQLException
    {
        country.setName(capitalizeEachWord(country.getName()));  // Ensure capitalization
        String query = "UPDATE " + tableName + " SET capital = ?, population = ?, river = ?, area = ? WHERE country_code = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query))
        {
            stmt.setString(1, country.getCapital());
            stmt.setDouble(2, country.getPopulation());
            stmt.setString(3, country.getRiver());
            stmt.setDouble(4, country.getArea());
            stmt.setString(5, country.getCountryCode());
            stmt.executeUpdate();
            logger.log(Level.INFO, "Country {0} updated successfully.", country.getName());
        }
        catch (SQLException e)
        {
            handleSQLException(e, "updating", country.getName());
        }
    }

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        addCountry
 * Description   Adds a new country to the database.
 * @param country the Country object to be added.
 * @throws SQLException if a database access error or constraint violation
 * occurs.
 * </pre>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

    public void addCountry(Country country) throws SQLException
    {
        Validation.validateCountry(country);
        country.setName(capitalizeEachWord(country.getName()));  // Ensure capitalization
        String query = "INSERT INTO " + tableName + " (country_Code, name, capital, population, river, area) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query))
        {
            stmt.setString(1, country.getCountryCode());
            stmt.setString(2, country.getName());
            stmt.setString(3, country.getCapital());
            stmt.setDouble(4, country.getPopulation());
            stmt.setString(5, country.getRiver());
            stmt.setDouble(6, country.getArea());
            stmt.executeUpdate();
        }
        catch (SQLIntegrityConstraintViolationException e)
        {
            logger.log(Level.WARNING, "Duplicate entry for country: " + country.getName(), e);
            throw new SQLException("Duplicate entry or constraint violation for country: " + country.getName(), e);
        }
        catch (SQLException e)
        {
            handleSQLException(e, "adding", country.getName());
        }
    }

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        capitalizeEachWord
 * Description   Capitalizes each word in the given string.
 * @param input the string to be capitalized.
 * @return the capitalized string.
 * </pre>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

    private String capitalizeEachWord(String input)
    {
        if (input == null || input.trim().isEmpty())
        {
            return input;
        }
        String[] words = input.toLowerCase().split("\s+");
        StringBuilder capitalized = new StringBuilder();
        for (String word : words)
        {
            if (!word.isEmpty())
            {
                capitalized.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
        }
        return capitalized.toString().trim();
    }

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        deleteCountry
 * Description   Deletes a country from the database and associated tables based on the country code.
 * @param countryCode the code of the country to delete.
 * @throws SQLException if a database access error occurs.
 * </pre>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

    public void deleteCountry(String countryCode) throws SQLException
    {
        if (countryCode == null || countryCode.trim().isEmpty())
        {
            throw new IllegalArgumentException("Country code cannot be null or empty.");
        }

        if (isInitialCountryCode(countryCode))
        {
            logger.log(Level.INFO, "Country with country_code {0} is an officially rcecognized standard international country code. Data will be deleted and the country code will be preserved.", countryCode);
        }

        String[] tables =
        {
            tableName, "country_info", "country_videos"
        };  // Exclude 'countriesabreviate' for preservation
        boolean deletionSuccess = false;

        try
        {
            for (String table : tables)
            {
                String query = "DELETE FROM " + table + " WHERE country_code = ?";
                try (PreparedStatement stmt = connection.prepareStatement(query))
                {
                    stmt.setString(1, countryCode);
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0)
                    {
                        logger.log(Level.INFO, "Country with country_code {0} deleted from table {1}.", new Object[]
                        {
                            countryCode, table
                        });
                        deletionSuccess = true;
                    }
                }
            }

            if (!deletionSuccess)
            {
                logger.log(Level.WARNING, "No country found with country_code: {0}", countryCode);
            }
        }
        catch (SQLException e)
        {
            handleSQLException(e, "deleting", countryCode);
        }
    }

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        isInitialCountryCode
 * Description   Checks if a given country code is an initial entry in the abbreviations table.
 * @param countryCode the country code to check.
 * @return true if the country code is an initial entry; false otherwise.
 * @throws SQLException if a database access error occurs.
 * </pre>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

    private boolean isInitialCountryCode(String countryCode) throws SQLException
    {
        String query = "SELECT initial_entry FROM countriesabreviate WHERE country_code = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query))
        {
            stmt.setString(1, countryCode);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
            {
                return rs.getBoolean("initial_entry");
            }
        }
        catch (SQLException e)
        {
            handleSQLException(e, "checking if country code is initial", countryCode);
        }
        return false;
    }

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        getCountryVideoLink
 * Description   Retrieves the video link associated with a country based on its code.
 * @param countryCode the code of the country.
 * @return an Optional containing the CountryVideoLink if found; empty otherwise.
 * @throws SQLException if a database access error occurs.
 * </pre>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

    public Optional<CountryVideoLink> getCountryVideoLink(String countryCode) throws SQLException
    {
        String query = "SELECT video_link FROM country_videos WHERE country_code = ?";
        CountryVideoLink videoLink = null;

        try (PreparedStatement stmt = connection.prepareStatement(query))
        {
            stmt.setString(1, countryCode);
            try (ResultSet resultSet = stmt.executeQuery())
            {
                if (resultSet.next())
                {
                    videoLink = new CountryVideoLink(countryCode, resultSet.getString("video_link"));
                }
            }
        }
        catch (SQLException e)
        {
            handleSQLException(e, "fetching video link for", countryCode);
        }

        return Optional.ofNullable(videoLink);
    }

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        getCountryInfo
 * Description   Retrieves detailed information for a country based on its code.
 * @param countryCode the code of the country.
 * @return an Optional containing the CountryInfo if found; empty otherwise.
 * @throws SQLException if a database access error occurs.
 * </pre>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

    public Optional<CountryInfo> getCountryInfo(String countryCode) throws SQLException
    {
        String query = "SELECT * FROM country_info WHERE country_code = ?";
        CountryInfo countryInfo = null;

        try (PreparedStatement stmt = connection.prepareStatement(query))
        {
            stmt.setString(1, countryCode);
            try (ResultSet resultSet = stmt.executeQuery())
            {
                if (resultSet.next())
                {
                    countryInfo = new CountryInfo(
                            resultSet.getString("country_code"),
                            resultSet.getString("name"),
                            resultSet.getString("description"),
                            resultSet.getString("common_fact"),
                            resultSet.getString("lesser_known_fact"),
                            resultSet.getString("historical_info")
                    );
                }
            }
        }
        catch (SQLException e)
        {
            handleSQLException(e, "fetching country info for", countryCode);
        }

        return Optional.ofNullable(countryInfo);
    }

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        saveCountryInfo
 * Description   Saves or updates the information for a country in the database.
 * @param countryInfo the CountryInfo object containing the information to be saved.
 * @throws SQLException if a database access error occurs.
 * </pre>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

    public void saveCountryInfo(CountryInfo countryInfo) throws SQLException
    {
        String query = "REPLACE INTO country_info (country_code, name, description, common_fact, lesser_known_fact, historical_info) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query))
        {
            stmt.setString(1, countryInfo.getCountryCode());
            stmt.setString(2, countryInfo.getName());
            stmt.setString(3, countryInfo.getDescription());
            stmt.setString(4, countryInfo.getCommonFact());
            stmt.setString(5, countryInfo.getLesserKnownFact());
            stmt.setString(6, countryInfo.getHistoricalInfo());
            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            handleSQLException(e, "saving country info for", countryInfo.getCountryCode());
        }
    }

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        searchCountryByName
 * Description   Searches for countries by name and returns either all matches or a single selected match.
 * @param name the name of the country to search for.
 * @param singleSelection if true, prompts the user to select a single
 * country when multiple matches are found.
 * @return an array of Country objects matching the search criteria.
 * @throws SQLException if a database access error occurs.
 * </pre>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

    public Country[] searchCountryByName(String name, boolean singleSelection) throws SQLException
    {
        if (name == null || name.trim().isEmpty())
        {
            throw new IllegalArgumentException("Country name cannot be null or empty.");
        }

        String query = "SELECT * FROM " + tableName + " WHERE name LIKE ?";
        ArrayList<Country> countryList = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(query))
        {
            stmt.setString(1, "%" + name + "%");
            try (ResultSet resultSet = stmt.executeQuery())
            {
                while (resultSet.next())
                {
                    Country country = new Country(
                            resultSet.getString("country_code"),
                            resultSet.getString("name"),
                            resultSet.getString("capital"),
                            resultSet.getDouble("population"),
                            resultSet.getString("river"),
                            resultSet.getDouble("area")
                    );
                    countryList.add(country);
                }
            }
        }
        catch (SQLException e)
        {
            handleSQLException(e, "searching country by name", name);
        }

        if (countryList.isEmpty())
        {
            logger.log(Level.WARNING, "No country found matching the name: {0}", name);
            return new Country[0];
        }
        else if (singleSelection && countryList.size() > 1)
        {
            // Use JOptionPane for user selection and return only the selected country in an array
            Country selectedCountry = (Country) JOptionPane.showInputDialog(
                    null,
                    "Multiple countries found. Please select one:",
                    "Country Selection",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    countryList.toArray(new Country[0]),
                    countryList.get(0)
            );

            return selectedCountry != null ? new Country[]
            {
                selectedCountry
            } : new Country[0];
        }

        return countryList.toArray(new Country[0]); // Return all results as an array if singleSelection is false or only one match
    }

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        getAllCountries
 * Description   Retrieves all countries from the database.
 * @return an array of Country objects representing all countries.
 * @throws SQLException if a database access error occurs.
 * </pre>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

    public Country[] getAllCountries() throws SQLException
    {
        String query = "SELECT * FROM " + tableName;
        ArrayList<Country> countryList = new ArrayList<>();

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query))
        {
            while (rs.next())
            {
                Country country = new Country(
                        rs.getString("country_code"),
                        rs.getString("name"),
                        rs.getString("capital"),
                        rs.getDouble("population"),
                        rs.getString("river"),
                        rs.getDouble("area")
                );
                countryList.add(country);
            }
            logger.log(Level.INFO, "All countries retrieved from table {0}", tableName);
        }
        catch (SQLException e)
        {
            handleSQLException(e, "retrieving countries from", tableName);
        }

        return countryList.toArray(new Country[0]);
    }

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        getAllCountryCodesNormalized
 * Description   Retrieves all country codes and names, with names normalized for case-insensitive searching.
 * @return a Map associating normalized country names with their codes.
 * @throws SQLException if a database access error occurs.
 * </pre>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

    public Map<String, String> getAllCountryCodesNormalized() throws SQLException
    {
        Map<String, String> countryNameToCodeMap = new HashMap<>();
        String query = "SELECT country_code, name FROM countriesAbreviate";

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query))
        {
            while (rs.next())
            {
                String countryCode = rs.getString("country_code");
                String countryName = rs.getString("name")
                        .toLowerCase()
                        .replaceAll("\\s+", "");
                countryNameToCodeMap.put(countryName, countryCode);
            }
        }
        catch (SQLException e)
        {
            handleSQLException(e, "retrieving all country codes", "countriesAbreviate");
        }
        return countryNameToCodeMap;
    }

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        updateVideoLink
 * Description   Updates or inserts the video link for a specified country in the database.
 * @param countryCode the code of the country.
 * @param name the name of the country.
 * @param videoLink the video link URL.
 * @throws SQLException if a database access error occurs.
 * </pre>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

    public void updateVideoLink(String countryCode, String name, String videoLink) throws SQLException
    {
        String query = "INSERT INTO country_videos (country_code, name, video_link) VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE name = ?, video_link = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query))
        {
            stmt.setString(1, countryCode);  // Set country code
            stmt.setString(2, name);         // Set name
            stmt.setString(3, videoLink);    // Set video link for insert
            stmt.setString(4, name);         // Set name for update
            stmt.setString(5, videoLink);    // Set video link for update
            stmt.executeUpdate();
        }
    }

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        countryExists
 * Description   Checks if a country exists in the database based on its code or name.
 * @param country_code the code of the country.
 * @param name the name of the country.
 * @return true if the country exists; false otherwise.
 * @throws SQLException if a database access error occurs.
 * </pre>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

    public boolean countryExists(String country_code, String name) throws SQLException
    {
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE country_Code = ? OR name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query))
        {
            stmt.setString(1, country_code);
            stmt.setString(2, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
            {
                return rs.getInt(1) > 0;
            }
        }
        catch (SQLException e)
        {
            handleSQLException(e, "checking if country exists", country_code);
        }
        return false;
    }

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        countryCodeExistsInAbbreviate
 * Description   Checks if a country code exists in the abbreviations table.
 * @param countryCode the code of the country.
 * @return true if the country code exists; false otherwise.
 * @throws SQLException if a database access error occurs.
 * </pre>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

    public boolean countryCodeExistsInAbbreviate(String countryCode) throws SQLException
    {
        String sql = "SELECT COUNT(*) FROM countriesabreviate WHERE country_code = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setString(1, countryCode);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
        catch (SQLException e)
        {
            handleSQLException(e, "checking if country code exists in Abbreviate", countryCode);
        }
        return false;
    }

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        isCountryCodeAndNameMatched
 * Description   Checks if a given country code and name match in the abbreviations table.
 * @param countryCode the code of the country.
 * @param name the name of the country.
 * @return true if the code and name match; false otherwise.
 * @throws SQLException if a database access error occurs.
 * </pre>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

    public boolean isCountryCodeAndNameMatched(String countryCode, String name) throws SQLException
    {
        String query = "SELECT COUNT(*) FROM countriesabreviate WHERE country_code = ? AND LOWER(name) = LOWER(?)";
        try (PreparedStatement stmt = connection.prepareStatement(query))
        {
            stmt.setString(1, countryCode.trim());
            stmt.setString(2, name.trim());
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
            {
                return rs.getInt(1) > 0;
            }
        }
        catch (SQLException e)
        {
            handleSQLException(e, "checking country code and name match", countryCode);
        }
        return false;
    }

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        addUniqueCountryCodeToAbbreviate
 * Description   Adds a new unique country code and name to the abbreviations table if it does not already exist.
 * @param countryCode the code of the country.
 * @param name the name of the country.
 * @throws SQLException if a database access error occurs.
 * </pre>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

    public void addUniqueCountryCodeToAbbreviate(String countryCode, String name) throws SQLException
    {
        if (!countryCodeExistsInAbbreviate(countryCode))
        {
            String query = "INSERT INTO countriesabreviate (country_code, name, initial_entry) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query))
            {
                stmt.setString(1, countryCode);
                stmt.setString(2, name);
                stmt.setBoolean(3, false);
                stmt.executeUpdate();
            }
            catch (SQLException e)
            {
                handleSQLException(e, "adding unique country code to Abbreviate", countryCode);
            }
        }
    }

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        close
 * Description   Closes the database connection if it is open.
 * @throws SQLException if a database access error occurs.
 * </pre>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

    public void close() throws SQLException
    {
        if (connection != null && !connection.isClosed())
        {
            connection.close();
            logger.info("Database connection closed.");
        }
    }

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        handleSQLException
 * Description   Logs and rethrows an SQLException with additional context about the action being performed.
 * @param e the SQLException to handle.
 * @param action the action being performed when the exception occurred.
 * @param entityName the name of the entity involved in the action.
 * @throws SQLException rethrows the provided SQLException.
 * </pre>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

    private void handleSQLException(SQLException e, String action, String entityName) throws SQLException
    {
        logger.log(Level.SEVERE, "Error " + action + " " + entityName, e);
        throw e;
    }

}
