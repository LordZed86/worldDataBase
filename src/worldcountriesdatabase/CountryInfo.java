
package worldcountriesdatabase;

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Class        CountryInfo
 * File         CountryInfo.java
 * Description  This class represents information about a specific country,
 *              including a description, common fact, lesser-known fact, and
 *              historical information.
 * Platform     JDK 23.0.1; NetBeans IDE 23; macOS Sequoia 15.0.1
 * Date         11/5/2024
 * @version     1.0
 * </pre>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
public class CountryInfo
{

    private final String countryCode;
    private String name;
    private String description;
    private String commonFact;
    private String lesserKnownFact;
    private String historicalInfo;

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Constructor   CountryInfo
     * Description   Initializes a CountryInfo object with the specified details.
     * @param countryCode       the country code of the country.
     * @param description a description of the country.
     * @param commonFact a common fact about the country.
     * @param lesserKnownFact a lesser-known fact about the country.
     * @param historicalInfo historical information about the country.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public CountryInfo(String countryCode, String name, String description, String commonFact, String lesserKnownFact, String historicalInfo)
    {
        this.countryCode = countryCode;
        this.name = name;
        this.description = description;
        this.commonFact = commonFact;
        this.lesserKnownFact = lesserKnownFact;
        this.historicalInfo = historicalInfo;
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        getCountryCode
     * Description   Retrieves the country code.
     * @return       the country code.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public String getCountryCode()
    {
        return countryCode;
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        getName
     * Description   Retrieves the country name.
     * @return       the country name.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public String getName()
    {
        return name;
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        getDescription
     * Description   Retrieves the description of the country.
     * @return       the description of the country.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        getCommonFact
     * Description   Retrieves a common fact about the country.
     * @return       a common fact about the country.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public String getCommonFact()
    {
        return commonFact;
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        getLesserKnownFact
     * Description   Retrieves a lesser-known fact about the country.
     * @return       a lesser-known fact about the country.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public String getLesserKnownFact()
    {
        return lesserKnownFact;
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        getHistoricalInfo
     * Description   Retrieves historical information about the country.
     * @return       historical information about the country.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public String getHistoricalInfo()
    {
        return historicalInfo;
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        setName
     * Description   Sets the name of the country.
     * @param name the name to set for the country.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        setDescription
     * Description   Sets the description of the country.
     * @param description the description to set for the country.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        setCommonFact
     * Description   Sets a common fact about the country.
     * @param commonFact the common fact to set.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public void setCommonFact(String commonFact)
    {
        this.commonFact = commonFact;
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        setLesserKnownFact
     * Description   Sets a lesser-known fact about the country.
     * @param lesserKnownFact the lesser-known fact to set.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public void setLesserKnownFact(String lesserKnownFact)
    {
        this.lesserKnownFact = lesserKnownFact;
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        setHistoricalInfo
     * Description   Sets historical information about the country.
     * @param historicalInfo the historical information to set.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public void setHistoricalInfo(String historicalInfo)
    {
        this.historicalInfo = historicalInfo;
    }

}
