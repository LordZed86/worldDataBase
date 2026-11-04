
package worldcountriesdatabase;

import java.util.Objects;
/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Class        Country
 * File         Country.java
 * Description  This class represents a country, with attributes for code, name, 
 *              capital, population, river, area, and additional details like 
 *              description, common facts, lesser-known facts, and historical info.
 * Platform     JDK 23.0.1; NetBeans IDE 23; macOS Sequoia 15.0.1
 * Date         11/5/2024
 * @version     1.0
 * </pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
public class Country implements Comparable<Country>
{

    private String countryCode;
    private String name;
    private String capital;
    private double population;
    private String river;
    private double area;
    private String description;      // New field for description
    private String commonFact;       // New field for common fact
    private String lesserKnownFact;  // New field for lesser-known fact
    private String history;          // New field for history

    /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Constructor   Country
 * Description   Default constructor for creating an empty Country object.
 * </pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    public Country()
    {
        // Default constructor
    }
/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Constructor   Country
 * Description   Initializes a Country object with basic details.
 * @param code          the country code.
 * @param name          the name of the country.
 * @param capital       the capital city of the country.
 * @param population    the population of the country.
 * @param river         the longest river in the country.
 * @param area          the area of the country.
 * </pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    public Country(String code, String name, String capital, double population, String river, double area)
    {
        this.countryCode = code;
        this.name = name;
        this.capital = capital;
        this.population = population;
        this.river = river;
        this.area = area;
    }

/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Constructor   Country
 * Description   Initializes a Country object with extended details.
 * @param code             the country code.
 * @param name             the name of the country.
 * @param capital          the capital city of the country.
 * @param population       the population of the country.
 * @param river            the longest river in the country.
 * @param area             the area of the country.
 * @param description      a description of the country.
 * @param commonFact       a common fact about the country.
 * @param lesserKnownFact  a lesser-known fact about the country.
 * @param history          historical information about the country.
 * </pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    public Country(String code, String name, String capital, double population, String river, double area,
            String description, String commonFact, String lesserKnownFact, String history)
    {
        this.countryCode = code;
        this.name = name;
        this.capital = capital;
        this.population = population;
        this.river = river;
        this.area = area;
        this.description = description;
        this.commonFact = commonFact;
        this.lesserKnownFact = lesserKnownFact;
        this.history = history;
    }

/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        getCountryCode
 * Description   Retrieves the country code.
 * @return       the country code.
 * </pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    public String getCountryCode()
    {
        return countryCode;
    }
/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        setCountryCode
 * Description   Sets the country code.
 * @param countryCode the code to set for the country.
 * </pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    public void setCountryCode(String countryCode)
    {
        this.countryCode = countryCode;
    }
/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        getName
 * Description   Retrieves the name of the country.
 * @return       the name of the country.
 * </pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    public String getName()
    {
        return name;
    }
/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        setName
 * Description   Sets the name of the country.
 * @param name   the name to set for the country.
 * </pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    public void setName(String name)
    {
        this.name = name;
    }
/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        getCapital
 * Description   Retrieves the capital city of the country.
 * @return       the capital city.
 * </pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    public String getCapital()
    {
        return capital;
    }
/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        setCapital
 * Description   Sets the capital city of the country.
 * @param capital the capital city to set.
 * </pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    public void setCapital(String capital)
    {
        this.capital = capital;
    }
/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        getPopulation
 * Description   Retrieves the population of the country.
 * @return       the population of the country.
 * </pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    public double getPopulation()
    {
        return population;
    }
/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        setPopulation
 * Description   Sets the population of the country.
 * @param population the population to set (must be non-negative).
 * @throws IllegalArgumentException if population is negative.
 * </pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    public void setPopulation(double population)
    {
        if (population >= 0)
        {
            this.population = population;
        }
        else
        {
            throw new IllegalArgumentException("Population must be non-negative.");
        }
    }
/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        getRiver
 * Description   Retrieves the name of the longest river in the country.
 * @return       the name of the river.
 * </pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    public String getRiver()
    {
        return river;
    }
/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        setRiver
 * Description   Sets the name of the longest river in the country.
 * @param river  the river to set.
 * </pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    public void setRiver(String river)
    {
        this.river = river;
    }
/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        getArea
 * Description   Retrieves the area of the country.
 * @return       the area of the country.
 * </pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    public double getArea()
    {
        return area;
    }
/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        setArea
 * Description   Sets the area of the country.
 * @param area   the area to set (must be non-negative).
 * @throws IllegalArgumentException if area is negative.
 * </pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    public void setArea(double area)
    {
        if (area >= 0)
        {
            this.area = area;
        }
        else
        {
            throw new IllegalArgumentException("Area must be non-negative.");
        }
    }
/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        getDescription
 * Description   Retrieves the description of the country.
 * @return       the description of the country.
 * </pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    public String getDescription()
    {
        return description;
    }
/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        setDescription
 * Description   Sets the description of the country.
 * @param description the description to set.
 * </pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    public void setDescription(String description)
    {
        this.description = description;
    }
/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        getCommonFact
 * Description   Retrieves a common fact about the country.
 * @return       a common fact about the country.
 * </pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    public String getCommonFact()
    {
        return commonFact;
    }
/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        setCommonFact
 * Description   Sets a common fact about the country.
 * @param commonFact the common fact to set.
 * </pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    public void setCommonFact(String commonFact)
    {
        this.commonFact = commonFact;
    }
/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        getLesserKnownFact
 * Description   Retrieves a lesser-known fact about the country.
 * @return       a lesser-known fact about the country.
 * </pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    public String getLesserKnownFact()
    {
        return lesserKnownFact;
    }
/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        setLesserKnownFact
 * Description   Sets a lesser-known fact about the country.
 * @param lesserKnownFact the lesser-known fact to set.
 * </pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    public void setLesserKnownFact(String lesserKnownFact)
    {
        this.lesserKnownFact = lesserKnownFact;
    }
/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        getHistory
 * Description   Retrieves historical information about the country.
 * @return       historical information about the country.
 * </pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    public String getHistory()
    {
        return history;
    }
/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        setHistory
 * Description   Sets historical information about the country.
 * @param history the historical information to set.
 * </pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    public void setHistory(String history)
    {
        this.history = history;
    }
/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        compareTo
 * Description   Compares this country to another country based on population.
 * @param other  the other Country to compare to.
 * @return       a negative integer, zero, or a positive integer as this country's
 *               population is less than, equal to, or greater than the specified country's population.
 * </pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    @Override
    public int compareTo(Country other)
    {
        return Double.compare(this.population, other.population);
    }
/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        getFormattedPopulation
 * Description   Formats the population for display, using "K" for thousands 
 *               and "M" for millions where appropriate.
 * @return       the formatted population string.
 * </pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    public String getFormattedPopulation()
    {
        if (population >= 1_000_000) {
            return String.format("%.2f M", population / 1_000_000);
        } else if (population >= 1_000) {
            return String.format("%.2f K", population / 1_000);
        } else {
            return String.format("%.0f", population);  // Display as-is for populations less than 1,000
        }
    }
/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        toString
 * Description   Returns a string representation of the country.
 * @return       a string representing the country's name, capital, formatted population, river, and area.
 * </pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    @Override
    public String toString()
    {
        return name + ", " + capital + ", Population: " + getFormattedPopulation() 
               + ", River: " + river + ", Area: " + area;
    }
/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        equals
 * Description   Compares this country to the specified object for equality.
 * @param obj    the object to compare to.
 * @return       true if the specified object is equal to this country; false otherwise.
 * </pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }
        Country country = (Country) obj;
        return Double.compare(country.population, population) == 0
                && Double.compare(country.area, area) == 0
                && Objects.equals(name, country.name)
                && Objects.equals(capital, country.capital)
                && Objects.equals(river, country.river);
    }
/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Method        hashCode
 * Description   Returns the hash code for this country.
 * @return       the hash code for this country.
 * </pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    @Override
    public int hashCode()
    {
        return Objects.hash(name, capital, population, river, area);
    }

}
