
package Utilities;

import DatabaseUtilities.CountriesDBManager;
import java.awt.Color;
import java.net.MalformedURLException;
import java.util.logging.Logger;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTextField;
import worldcountriesdatabase.Country;

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Class        Validation
 * File         Validation.java
 * Description  A class that validates information passed to it for the World Countries Database.
 * Platform     JDK 23.0.1; NetBeans IDE 23; macOS Sequoia 15.0.1
 * Date         10/25/2024
 * History log  5/8/2016, 4/7/2020, 4/30/2021, 10/25/2024
 * @author      <i>Kellen Jones</i>
 * @version 2.0
 * </pre>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
public class Validation
{

    public static final double DOUBLE_RANGE = 1E-9;
    private static final Logger logger = Logger.getLogger(Validation.class.getName());

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method       isValidString()
     * Description  Validates that a string is not null or empty.
     * Date         10/25/2024
     * @param       input String
     * @return true/false boolean
     * @author      <i>Kellen Jones</i>
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public static boolean isValidString(String input)
    {
        return input != null && !input.trim().isEmpty();
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method       isValidCountryCode
     * Description  Validates that a country code is exactly two uppercase letters.
     * Date         10/25/2024
     * @param       code String - The country code to validate.
     * @return boolean - true if valid, false otherwise.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public static boolean isValidCountryCode(String code)
    {
        return code != null && code.matches("[A-Z]{2}");
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method       isValidCountryName()
     * Description  Validates the name of a country (allows letters and spaces).
     * Date         10/25/2024
     * @param       name String
     * @return true/false boolean
     * @author      <i>Kellen Jones</i>
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public static boolean isValidCountryName(String name)
    {
        return Pattern.matches("[a-zA-Z\\s]+", name);
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method       isValidFloat()
     * Description  Validates that a float value is positive.
     * Date         10/25/2024
     * @param       value float
     * @return true/false boolean
     * @author      <i>Kellen Jones</i>
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public static boolean isValidFloat(float value)
    {
        return value > 0;
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method       isValidPopulation()
     * Description  Validates the population (must be a positive float).
     * Date         10/25/2024
     * @param       population float
     * @return true/false boolean
     * @author      <i>Kellen Jones</i>
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public static boolean isValidPopulation(float population)
    {
        return population > 0;
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method       isLettersOnly()
     * Description  Returns true if the String is made of only letters and spaces.
     * Date         10/25/2024
     * @param       myString String
     * @return true/false boolean
     * @author      <i>Kellen Jones</i>
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public static boolean isLettersOnly(String myString)
    {
        if (myString.isEmpty())
        {
            return false;
        }
        char[] charArray = myString.toCharArray();
        for (char c : charArray)
        {
            if (!Character.isLetter(c) && !Character.isSpaceChar(c))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method       isNumeric()
     * Description  Returns true if the String is made of only numbers.
     * Date         10/25/2024
     * @param       myString String
     * @return true/false boolean
     * @author      <i>Kellen Jones</i>
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public static boolean isNumeric(String myString)
    {
        return myString.matches("-?\\d+(\\.\\d+)?");
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method       isInteger()
     * Description  Validates that entered value is an integer.
     * Date         10/25/2024
     * @param       fieldValue String
     * @return true/false boolean
     * @author      <i>Kellen Jones</i>
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public static boolean isInteger(String fieldValue)
    {
        Pattern pat = Pattern.compile("\\d+");
        Matcher mat = pat.matcher(fieldValue);
        return mat.matches();
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method       isInteger()
     * Description  Overloaded, validates that entered value is an integer
     *              within the required range.
     * Date         10/25/2024
     * @param       fieldValue String
     * @param lower int
     * @param upper int
     * @return true/false boolean
     * @author      <i>Kellen Jones</i>
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public static boolean isInteger(String fieldValue, int lower, int upper)
    {
        boolean result = true;
        Pattern pat = Pattern.compile("\\d+");
        Matcher mat = pat.matcher(fieldValue);
        if (mat.matches())
        {
            try
            {
                int num = Integer.parseInt(fieldValue);
                if (num < lower || num > upper)
                {
                    result = false;
                }
            }
            catch (NumberFormatException ex)
            {
                result = false;
            }
        }
        else
        {
            result = false;
        }
        return result;
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method       isCloseEnough()
     * Description  Determines if a double value is equal to another value
     *              considering the inaccuracy of a double value.
     * Date         10/25/2024
     * @param       checkedVal double
     * @param closeToVal double
     * @return true/false boolean
     * @author      <i>Kellen Jones</i>
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public static boolean isCloseEnough(double checkedVal, double closeToVal)
    {
        return (Math.abs(checkedVal - closeToVal) < DOUBLE_RANGE);
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        validateCountry
     * Description   Validates the Country object, checking that required fields are not null or empty.
     * @param country the Country object to validate.
     * @throws IllegalArgumentException if any required field is null or empty.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    public static void validateCountry(Country country)
    {
        if (country == null)
        {
            throw new IllegalArgumentException("Country object cannot be null.");
        }
        if (country.getCountryCode() == null || country.getCountryCode().trim().isEmpty())
        {
            throw new IllegalArgumentException("Country country_code cannot be null or empty.");
        }
        if (country.getName() == null || country.getName().trim().isEmpty())
        {
            throw new IllegalArgumentException("Country name cannot be null or empty.");
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        isValidURL
     * Description   Validates if the provided URL string is valid.
     * @param url    the URL string to validate.
     * @return true if the URL is valid; false otherwise.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public static boolean isValidURL(String url)
    {
        try
        {
            URI toURI = new java.net.URL(url).toURI();
            return true;
        }
        catch (MalformedURLException | URISyntaxException e)
        {
            return false;
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        validateCountryUniqueness
     * Description   Checks if a country with the specified code or name already exists in the database.
     * @param dbManager
     * @param countryCode the country code to check.
     * @param name the country name to check.
     * @throws SQLException if a database access error occurs.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public static void validateCountryUniqueness(CountriesDBManager dbManager, String countryCode, String name) throws SQLException
    {
        if (dbManager.countryExists(countryCode, name))
        {
            logger.log(Level.WARNING,
                    "Validation failed: Country with code {0} or name {1} already exists.",
                    new Object[]
                    {
                        countryCode, name
                    });

            throw new SQLException("Country with this code or name already exists.");
        }
    }
/**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        validateRequiredFields
     * Description   Validates that all required fields in the array are filled in.
     *               Highlights missing fields in red.
     * @param requiredFields an array of JTextField objects to validate.
     * @return       true if all required fields are valid; false otherwise.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public static boolean validateRequiredFields(JTextField[] requiredFields) {
        boolean valid = true;

        for (JTextField field : requiredFields) {
            if (field.getText().trim().isEmpty()) {
                field.setBackground(Color.RED);
                valid = false;
            } else {
                field.setBackground(Color.WHITE); // Reset to default color if valid
            }
        }

        return valid;
    }
}
