
package worldcountriesdatabase;

import DatabaseUtilities.CountriesDBManager;
import Utilities.PrintManager;
import Utilities.SortingUtility;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.*;

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Class        WorldCountriesGUI
 * Description  Main graphical user interface for the World Countries Database Application.
 *              Provides functionalities to view, search, edit, add, delete, sort,
 *              and print details of countries, along with switching datasets.
 * Platform     JDK 23.0.1; NetBeans IDE 23; macOS Sequoia 15.0.1
 * Course       CS 143
 * Hours        60 hours
 * Date         10/28/2024
 * History Log  7/18/2018, 5/7/2020, 10/28/2024
 * @author      Kellen Jones
 * @version     1.1
 * @see DatabaseUtilities.CountriesDBManager
 * @see Utilities.PrintManager
 * @see Utilities.SortingUtility
 * </pre>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
public class WorldCountriesGUI extends javax.swing.JFrame
{

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Variable      currentTableName
     * Description   Holds the name of the currently selected database table for
     *               displaying country data. Default is set to "countriesA".
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public static String currentTableName = "countriesA";
    private final List<String> availableTables = new ArrayList<>();
    private CountriesDBManager dbManager;

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Constructor   WorldCountriesGUI()
     * Description   Initializes the GUI, connects to the database, loads the initial
     *               list of countries, and sets up listeners for user interaction.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public WorldCountriesGUI()
    {
        initComponents();
        availableTables.add("countriesA");
        availableTables.add("countriesB");
        initializeDatabase();
        loadCountriesList();
        setupCountrySelectionListener();
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        initializeDatabase
     * Description   Initializes a connection to the current dataset and manages
     *               database connection errors.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void initializeDatabase()
    {
        try
        {
            dbManager = new CountriesDBManager(currentTableName);
        }
        catch (SQLException e)
        {
            showError("Database Connection Error", "Error connecting to the database: " + e.getMessage());
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        loadCountriesList
     * Description   Loads the list of country names from the current dataset into
     *               the display list component.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void loadCountriesList()
    {
        try
        {
            Country[] countries = dbManager.getAllCountries();
            DefaultListModel<String> model = new DefaultListModel<>();
            for (Country country : countries)
            {
                model.addElement(country.getName());
            }
            countriesJList.setModel(model);
        }
        catch (SQLException e)
        {
            showError("Data Loading Error", "Error loading countries from " + currentTableName + ": " + e.getMessage());
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        setupCountrySelectionListener
     * Description   Sets up a listener to display country details when a country is
     *               selected from the list.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void setupCountrySelectionListener()
    {
        countriesJList.addListSelectionListener(e ->
        {
            if (!e.getValueIsAdjusting())
            {
                String selectedCountryName = countriesJList.getSelectedValue();
                if (selectedCountryName != null)
                {
                    displayCountryDetails(selectedCountryName);
                }
            }
        });
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        displayCountryDetails
     * Description   Retrieves and displays detailed information about the selected
     *               country.
     * @param        selectedCountryName String - Name of the selected country.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void displayCountryDetails(String selectedCountryName)
    {
        try
        {
            Country[] countries = dbManager.searchCountryByName(selectedCountryName, false);
            if (countries.length > 0)
            {
                Country selectedCountry = countries[0];
                populateCountryDetails(selectedCountry);
            }
            else
            {
                showError("Country Not Found", "Country not found in database.");
            }
        }
        catch (SQLException e)
        {
            showError("Data Retrieval Error", "Error retrieving country details: " + e.getMessage());
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        populateCountryDetails
     * Description   Populates the GUI components with the provided country's data.
     * @param        country Country - The Country object containing details to display.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void populateCountryDetails(Country country)
    {
        countryCodeJLabel.setText(country.getCountryCode());
        countryJLabel.setText(country.getName());
        capitalJTextField.setText(country.getCapital());
        populationJTextField.setText(country.getFormattedPopulation());
        riverJTextField.setText(country.getRiver());
        areaJTextField.setText(String.valueOf(country.getArea()));
        setFlagImage(country);
        setMapImage(country);
        loadAdditionalCountryInfo(country.getCountryCode());
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        loadAdditionalCountryInfo
     * Description   Loads additional country information, such as historical facts
     *               and video links, if available.
     * @param        countryCode String - The code of the country for which to load additional info.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void loadAdditionalCountryInfo(String countryCode)
    {
        try
        {
            Optional<CountryInfo> optionalCountryInfo = dbManager.getCountryInfo(countryCode);
            if (optionalCountryInfo.isPresent())
            {
                CountryInfo countryInfo = optionalCountryInfo.get();
                descriptionJTextArea.setText(countryInfo.getDescription());
                commonFactsJTextArea.setText(countryInfo.getCommonFact());
                lesserKnownJTextArea.setText(countryInfo.getLesserKnownFact());
                historyJTextArea.setText(countryInfo.getHistoricalInfo());
            }
            else
            {
                clearInfoFields(); // Clear fields if no information is available
            }
            // Fetch and display video link using Optional
            Optional<CountryVideoLink> optionalVideoLink = dbManager.getCountryVideoLink(countryCode);
            if (optionalVideoLink.isPresent())
            {
                CountryVideoLink videoLink = optionalVideoLink.get();
                videoLinkJLabel1.setText("<html><a href=''>" + "Learn more about " + countryCode + "</a></html>");
                videoLinkJLabel1.setToolTipText(videoLink.getLink()); // Tooltip with the URL
            }
            else
            {
                videoLinkJLabel1.setText("No video available.");
                videoLinkJLabel1.setToolTipText("");
            }
        }
        catch (SQLException e)
        {
            showError("Additional Info Error", "Error loading additional info for country: " + countryCode);
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        clearInfoFields
     * Description   Clears the text fields for additional information when there
     *               is no data available for the selected country.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void clearInfoFields()
    {
        descriptionJTextArea.setText("No description available.");
        commonFactsJTextArea.setText("No common fact available.");
        lesserKnownJTextArea.setText("No lesser-known fact available.");
        historyJTextArea.setText("No historical info available.");
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        setFlagImage
     * Description   Loads and sets the flag image for the selected country.
     * @param        country Country - The Country object containing flag details.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void setFlagImage(Country country)
    {
        String[] imageNames =
        {
            country.getCountryCode(), country.getCountryCode().toLowerCase(), country.getName()
        };
        ImageIcon flagIcon = loadImage(imageNames, "/FlagImages/");
        flagImageJLabel.setIcon(flagIcon != null ? flagIcon : getDefaultIcon("No Flag Available"));
    }

    private void setMapImage(Country country)
    {
        String[] imageNames =
        {
            country.getCountryCode(), country.getName()
        };

        ImageIcon mapIcon = loadImage(imageNames, "/CountryImages/");
        mapImageJLabel.setIcon(mapIcon != null ? mapIcon : getDefaultIcon("No Map Available"));
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        editCountry
     * Description   Opens the edit dialog for the currently selected country, allowing
     *               users to modify country information.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void editCountry()
    {
        String selectedCountryName = countriesJList.getSelectedValue();
        if (selectedCountryName != null)
        {
            try
            {
                Country[] countries = dbManager.searchCountryByName(selectedCountryName, false);
                if (countries.length > 0)
                {
                    Country selectedCountry = countries[0];  // Get the first match
                    AddCountry editCountryDialog = new AddCountry(this, true, selectedCountry);
                    editCountryDialog.setVisible(true);
                    refreshCountryList();  // Refresh the list after closing the dialog
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "Country not found.", "Edit Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            catch (SQLException e)
            {
                JOptionPane.showMessageDialog(this, "Error loading country details for edit: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Please select a country to edit.", "Selection Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        deleteCountry
     * Description   Deletes the selected country from the current dataset and
     *               refreshes the list.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void deleteCountry()
    {
        String selectedCountryName = countriesJList.getSelectedValue();
        if (selectedCountryName != null)
        {
            try
            {
                CountriesDBManager dbManager = new CountriesDBManager(currentTableName);
                Country[] countries = dbManager.searchCountryByName(selectedCountryName, false);

                if (countries.length > 0)
                {  // Check if any countries were found
                    Country selectedCountry = countries[0];  // Use the first matched country
                    String countryCode = selectedCountry.getCountryCode();
                    // ToDo ask for confirmation
                    dbManager.deleteCountry(countryCode);
                    loadCountriesList();  // Refresh list after editing
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "Country not found.", "Delete Error", JOptionPane.ERROR_MESSAGE);
                }
                dbManager.close();

            }
            catch (SQLException e)
            {
                JOptionPane.showMessageDialog(this, "Error loading country details for deletion: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Please select a country to delete.", "Selection Error", JOptionPane.WARNING_MESSAGE);
        }

    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        loadImage
     * Description   Attempts to load an image from the specified path and file names
     *               with multiple possible file extensions.
     *               Returns the first successfully loaded image or null if no image
     *               is found.
     * @param        names String[] - Array of possible file names for the image.
     * @param path String - Path to the directory containing the images.
     * @return ImageIcon - The loaded image, or null if none is found.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private ImageIcon loadImage(String[] names, String path)
    {
        String[] extensions =
        {
            ".jpg", ".jpeg", ".png", ".bmp"
        }; // Add more extensions as needed

        for (String name : names)
        {
            for (String ext : extensions)
            {
                try
                {
                    ImageIcon icon = new ImageIcon(getClass().getResource(path + name + ext));
                    if (icon.getIconWidth() > 0)
                    {
                        return icon;
                    }
                }
                catch (Exception ignored)
                {
                }
            }
        }
        return null;
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        getDefaultIcon
     * Description   Provides a default icon with placeholder text if no image is
     *               available for a country.
     * @param        text String - Text to display as a placeholder.
     * @return ImageIcon - The default icon, or null if not applicable.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private ImageIcon getDefaultIcon(String text)
    {
        flagImageJLabel.setText(text);
        return null;
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        getCurrentTableName
     * Description   Returns the name of the currently selected database table used
     *               for displaying country data in the GUI.
     * @return       the name of the current table as a String.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public static String getCurrentTableName()
    {
        return WorldCountriesGUI.currentTableName;
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        swapDataset
     * Description   Switches the current dataset being displayed in the GUI and reloads
     *               the list of countries.
     * @param        newTableName String - The name of the new dataset table to display.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void swapDataset(String newTableName)
    {
        currentTableName = newTableName;
        initializeDatabase();
        loadCountriesList();
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        printForm
     * Description   Prints the entire form window.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void printForm()
    {
        PrintManager.printForm(this);
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        printCountryDetails
     * Description   Prints details of the selected country, if available.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void printCountryDetails()
    {
        String selectedCountryName = countriesJList.getSelectedValue();
        if (selectedCountryName != null)
        {
            try
            {
                // Pass the current table name when instantiating the DB manager
                CountriesDBManager dbManager = new CountriesDBManager(currentTableName);
                Country[] countries = dbManager.searchCountryByName(selectedCountryName, false);
                dbManager.close();

                if (countries.length > 0)  // Check if any countries were found
                {
                    // Use the new printCountryDetails method
                    PrintManager.printCountryDetails(countries[0]);
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "Country not found.", "Print Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            catch (SQLException e)
            {
                JOptionPane.showMessageDialog(this, "Error printing country details: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Please select a country to print.", "Selection Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        searchCountry
     * Description   Searches for a country based on user input and displays the details
     *               of the first match, if found.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void searchCountry()
    {
        String searchQuery = JOptionPane.showInputDialog(this, "Enter country name to search:");
        if (searchQuery != null && !searchQuery.trim().isEmpty())
        {
            try
            {
                Country[] countries = dbManager.searchCountryByName(searchQuery.trim(), true);
                if (countries.length > 0)
                {
                    Country selectedCountry = countries[0];

                    // Display the country details
                    displayCountryDetails(selectedCountry.getName());

                    // Highlight the country in countriesJList
                    int index = findCountryIndexInList(selectedCountry);
                    if (index != -1)
                    {
                        countriesJList.setSelectedIndex(index); // Selects and highlights the item in countriesJList
                        countriesJList.ensureIndexIsVisible(index); // Ensures the selected item is visible
                    }
                }
                else
                {
                    showError("No Results", "No country found matching the search term.");
                }
            }
            catch (SQLException e)
            {
                showError("Database Error", "An error occurred while searching: " + e.getMessage());
            }
        }
        else
        {
            showError("Input Error", "Please enter a valid country name.");
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        findCountryIndexInList
     * Description   Finds the index of a given country in the JList for highlighting.
     * @param        country Country - The country object to locate in the list.
     * @return int - The index of the country in the JList, or -1 if not found.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private int findCountryIndexInList(Country country)
    {
        ListModel<String> model = countriesJList.getModel();
        for (int i = 0; i < model.getSize(); i++)
        {
            if (model.getElementAt(i).equals(country))
            {
                return i;
            }
        }
        return -1; // Country not found in the list
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        refreshCountryList
     * Description   Reloads the list of countries from the database to reflect any
     *               changes made, such as additions or deletions.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    void refreshCountryList()
    {
        loadCountriesList();
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        showError
     * Description   Displays an error message dialog.
     * @param        title String - The title of the dialog.
     * @param message String - The message to display in the dialog.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void showError(String title, String message)
    {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        quitApplication
     * Description   Prompts the user for confirmation and exits the application.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void quitApplication()
    {
        int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to quit?", "Exit Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION)
        {
            System.exit(0);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        buttonGroup1 = new javax.swing.ButtonGroup();
        flagImageJLabel = new javax.swing.JLabel();
        infoJPanel = new javax.swing.JPanel();
        countriesJScrollPane = new javax.swing.JScrollPane();
        countriesJList = new javax.swing.JList<>();
        commonFactsJScrollPane = new javax.swing.JScrollPane();
        commonFactsJTextArea = new javax.swing.JTextArea();
        countryJLabel = new javax.swing.JLabel();
        titleJLabel = new javax.swing.JLabel();
        descriptionJScrollPane = new javax.swing.JScrollPane();
        descriptionJTextArea = new javax.swing.JTextArea();
        commonFactsJLabel = new javax.swing.JLabel();
        historyJLabel = new javax.swing.JLabel();
        videoLinkJLabel = new javax.swing.JLabel();
        capitalJTextField = new javax.swing.JTextField();
        areaJLabel = new javax.swing.JLabel();
        populationJTextField = new javax.swing.JTextField();
        populationJLabel = new javax.swing.JLabel();
        riverJLabel = new javax.swing.JLabel();
        descriptionJLabel = new javax.swing.JLabel();
        capitalJLabel = new javax.swing.JLabel();
        lessKnownJScrollPane = new javax.swing.JScrollPane();
        lesserKnownJTextArea = new javax.swing.JTextArea();
        riverJTextField = new javax.swing.JTextField();
        areaJTextField = new javax.swing.JTextField();
        lesserKnownJLabel = new javax.swing.JLabel();
        historyJScrollPane = new javax.swing.JScrollPane();
        historyJTextArea = new javax.swing.JTextArea();
        videoLinkJLabel1 = new javax.swing.JLabel();
        countryCodeJLabel = new javax.swing.JLabel();
        mapImageJLabel = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileJMenu = new javax.swing.JMenu();
        newJMenuItem = new javax.swing.JMenuItem();
        printDetailsJMenuItem = new javax.swing.JMenuItem();
        printFormJMenuItem = new javax.swing.JMenuItem();
        quitJMenuItem = new javax.swing.JMenuItem();
        countryJMenu = new javax.swing.JMenu();
        addJMenuItem = new javax.swing.JMenuItem();
        editJMenuItem = new javax.swing.JMenuItem();
        searchJMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        deleteJMenuItem = new javax.swing.JMenuItem();
        sortJMenu = new javax.swing.JMenu();
        alphaJRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        popJRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        helpJMenu = new javax.swing.JMenu();
        aboutJMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Countries of the World");
        setResizable(false);

        flagImageJLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        flagImageJLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/OtherImages/allCountries.jpg"))); // NOI18N
        flagImageJLabel.setToolTipText(null);
        flagImageJLabel.setPreferredSize(new java.awt.Dimension(300, 200));

        countriesJList.setFont(new java.awt.Font("Impact", 1, 24)); // NOI18N
        countriesJList.setModel(new javax.swing.AbstractListModel<String>()
        {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        countriesJList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        countriesJList.setToolTipText(null);
        countriesJScrollPane.setViewportView(countriesJList);

        commonFactsJTextArea.setEditable(false);
        commonFactsJTextArea.setColumns(20);
        commonFactsJTextArea.setLineWrap(true);
        commonFactsJTextArea.setRows(5);
        commonFactsJTextArea.setToolTipText("Common fact about the country");
        commonFactsJTextArea.setWrapStyleWord(true);
        commonFactsJTextArea.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        commonFactsJScrollPane.setViewportView(commonFactsJTextArea);

        countryJLabel.setFont(new java.awt.Font("Helvetica Neue", 0, 36)); // NOI18N
        countryJLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        countryJLabel.setText("<Country Name>");
        countryJLabel.setToolTipText(null);
        countryJLabel.setName(""); // NOI18N
        countryJLabel.setPreferredSize(new java.awt.Dimension(277, 359));

        titleJLabel.setFont(new java.awt.Font("Palatino", 1, 48)); // NOI18N
        titleJLabel.setForeground(new java.awt.Color(153, 0, 0));
        titleJLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleJLabel.setText("The Countries of the World");
        titleJLabel.setToolTipText(null);

        descriptionJTextArea.setEditable(false);
        descriptionJTextArea.setColumns(20);
        descriptionJTextArea.setLineWrap(true);
        descriptionJTextArea.setRows(5);
        descriptionJTextArea.setToolTipText("Description of the Country");
        descriptionJTextArea.setWrapStyleWord(true);
        descriptionJTextArea.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        descriptionJScrollPane.setViewportView(descriptionJTextArea);

        commonFactsJLabel.setText("Common Fact:");
        commonFactsJLabel.setToolTipText(null);

        historyJLabel.setText("History:");
        historyJLabel.setToolTipText(null);

        videoLinkJLabel.setText("Video Link:");
        videoLinkJLabel.setToolTipText(null);

        capitalJTextField.setEditable(false);
        capitalJTextField.setToolTipText("Country Capital");
        capitalJTextField.setPreferredSize(new java.awt.Dimension(200, 27));

        areaJLabel.setText("Land Area (Km):");
        areaJLabel.setToolTipText(null);

        populationJTextField.setEditable(false);
        populationJTextField.setToolTipText("Country Population");
        populationJTextField.setPreferredSize(new java.awt.Dimension(200, 27));

        populationJLabel.setText("Population:");
        populationJLabel.setToolTipText(null);

        riverJLabel.setText("Longest River:");
        riverJLabel.setToolTipText(null);

        descriptionJLabel.setText("Description:");
        descriptionJLabel.setToolTipText(null);

        capitalJLabel.setText("Capital:");
        capitalJLabel.setToolTipText(null);

        lesserKnownJTextArea.setEditable(false);
        lesserKnownJTextArea.setColumns(20);
        lesserKnownJTextArea.setLineWrap(true);
        lesserKnownJTextArea.setRows(5);
        lesserKnownJTextArea.setToolTipText("little known fact about the country");
        lesserKnownJTextArea.setWrapStyleWord(true);
        lesserKnownJTextArea.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        lessKnownJScrollPane.setViewportView(lesserKnownJTextArea);

        riverJTextField.setEditable(false);
        riverJTextField.setToolTipText("Longest or Most Significant River in the Country");
        riverJTextField.setPreferredSize(new java.awt.Dimension(200, 27));

        areaJTextField.setEditable(false);
        areaJTextField.setToolTipText("Countries Land Area in Square Kilometers");
        areaJTextField.setPreferredSize(new java.awt.Dimension(200, 27));

        lesserKnownJLabel.setText("Lesser Known Fact:");
        lesserKnownJLabel.setToolTipText(null);

        historyJTextArea.setEditable(false);
        historyJTextArea.setColumns(20);
        historyJTextArea.setLineWrap(true);
        historyJTextArea.setRows(5);
        historyJTextArea.setToolTipText("short historical tidbit of the country");
        historyJTextArea.setWrapStyleWord(true);
        historyJTextArea.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        historyJScrollPane.setViewportView(historyJTextArea);

        videoLinkJLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        videoLinkJLabel1.setToolTipText(videoLinkJLabel1.getText());
        videoLinkJLabel1.setPreferredSize(new java.awt.Dimension(350, 27));
        videoLinkJLabel1.setSize(new java.awt.Dimension(350, 27));
        videoLinkJLabel1.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                videoLinkJLabel1MouseClicked(evt);
            }
        });

        countryCodeJLabel.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        countryCodeJLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        countryCodeJLabel.setToolTipText("Alpha-2 ISO 3166 code ");
        countryCodeJLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Code", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.ABOVE_TOP));
        countryCodeJLabel.setPreferredSize(new java.awt.Dimension(42, 29));
        countryCodeJLabel.setSize(new java.awt.Dimension(42, 29));

        javax.swing.GroupLayout infoJPanelLayout = new javax.swing.GroupLayout(infoJPanel);
        infoJPanel.setLayout(infoJPanelLayout);
        infoJPanelLayout.setHorizontalGroup(
            infoJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(infoJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(infoJPanelLayout.createSequentialGroup()
                        .addComponent(countriesJScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(infoJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(infoJPanelLayout.createSequentialGroup()
                                .addGroup(infoJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(riverJLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(riverJTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(capitalJTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(capitalJLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                                .addGroup(infoJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(populationJLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(populationJTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(areaJLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(areaJTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(descriptionJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(descriptionJScrollPane)
                            .addComponent(commonFactsJScrollPane)
                            .addComponent(lessKnownJScrollPane)
                            .addComponent(historyJScrollPane)
                            .addComponent(historyJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lesserKnownJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(commonFactsJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(infoJPanelLayout.createSequentialGroup()
                                .addComponent(videoLinkJLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(videoLinkJLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(infoJPanelLayout.createSequentialGroup()
                                .addComponent(countryCodeJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(countryJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addComponent(titleJLabel))
                .addContainerGap())
        );
        infoJPanelLayout.setVerticalGroup(
            infoJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(infoJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(infoJPanelLayout.createSequentialGroup()
                        .addGroup(infoJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(countryJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(countryCodeJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(infoJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(capitalJLabel)
                            .addComponent(populationJLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(infoJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(capitalJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(populationJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(infoJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(riverJLabel)
                            .addComponent(areaJLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(infoJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(riverJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(areaJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(descriptionJLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(descriptionJScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(commonFactsJLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(commonFactsJScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lesserKnownJLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lessKnownJScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(historyJLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(historyJScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(infoJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(videoLinkJLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(videoLinkJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(countriesJScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 582, Short.MAX_VALUE))
                .addContainerGap())
        );

        mapImageJLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mapImageJLabel.setToolTipText(null);
        mapImageJLabel.setPreferredSize(new java.awt.Dimension(300, 300));

        fileJMenu.setText("File");

        newJMenuItem.setText("Data Set");
        newJMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                newJMenuItemActionPerformed(evt);
            }
        });
        fileJMenu.add(newJMenuItem);

        printDetailsJMenuItem.setText("Print Country Details");
        printDetailsJMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                printDetailsJMenuItemActionPerformed(evt);
            }
        });
        fileJMenu.add(printDetailsJMenuItem);

        printFormJMenuItem.setText("Print Form");
        printFormJMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                printFormJMenuItemActionPerformed(evt);
            }
        });
        fileJMenu.add(printFormJMenuItem);

        quitJMenuItem.setText("Quit");
        quitJMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                quitJMenuItemActionPerformed(evt);
            }
        });
        fileJMenu.add(quitJMenuItem);

        jMenuBar1.add(fileJMenu);

        countryJMenu.setText("Country");

        addJMenuItem.setText("Add Country");
        addJMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                addJMenuItemActionPerformed(evt);
            }
        });
        countryJMenu.add(addJMenuItem);

        editJMenuItem.setText("Edit Country");
        editJMenuItem.setToolTipText("");
        editJMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                editJMenuItemActionPerformed(evt);
            }
        });
        countryJMenu.add(editJMenuItem);

        searchJMenuItem.setText("Search Country");
        searchJMenuItem.setToolTipText("");
        searchJMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                searchJMenuItemActionPerformed(evt);
            }
        });
        countryJMenu.add(searchJMenuItem);
        countryJMenu.add(jSeparator1);

        deleteJMenuItem.setText("Delete Country");
        deleteJMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                deleteJMenuItemActionPerformed(evt);
            }
        });
        countryJMenu.add(deleteJMenuItem);

        jMenuBar1.add(countryJMenu);

        sortJMenu.setText("Sort");
        sortJMenu.setToolTipText("");

        buttonGroup1.add(alphaJRadioButtonMenuItem);
        alphaJRadioButtonMenuItem.setSelected(true);
        alphaJRadioButtonMenuItem.setText("Alphabetical");
        alphaJRadioButtonMenuItem.setToolTipText("");
        alphaJRadioButtonMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                alphaJRadioButtonMenuItemActionPerformed(evt);
            }
        });
        sortJMenu.add(alphaJRadioButtonMenuItem);

        buttonGroup1.add(popJRadioButtonMenuItem);
        popJRadioButtonMenuItem.setText("Population");
        popJRadioButtonMenuItem.setToolTipText("");
        popJRadioButtonMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                popJRadioButtonMenuItemActionPerformed(evt);
            }
        });
        sortJMenu.add(popJRadioButtonMenuItem);

        jMenuBar1.add(sortJMenu);

        helpJMenu.setText("Help");

        aboutJMenuItem.setText("About");
        aboutJMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                aboutJMenuItemActionPerformed(evt);
            }
        });
        helpJMenu.add(aboutJMenuItem);

        jMenuBar1.add(helpJMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(infoJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(flagImageJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mapImageJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(infoJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(flagImageJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mapImageJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void editJMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_editJMenuItemActionPerformed
    {//GEN-HEADEREND:event_editJMenuItemActionPerformed
        editCountry();
    }//GEN-LAST:event_editJMenuItemActionPerformed

    private void newJMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_newJMenuItemActionPerformed
    {//GEN-HEADEREND:event_newJMenuItemActionPerformed
        String[] tablesArray = availableTables.toArray(String[]::new);  // Convert list to array for selection
        String selectedTable = (String) JOptionPane.showInputDialog(this, "Choose dataset", "Swap Dataset",
                JOptionPane.QUESTION_MESSAGE, null, tablesArray, currentTableName);
        if (selectedTable != null)
        {
            swapDataset(selectedTable);  // Switch to the selected dataset and reload the list
            JOptionPane.showMessageDialog(this, "Switched to dataset: " + selectedTable, "Dataset Switch", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_newJMenuItemActionPerformed

    private void printDetailsJMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_printDetailsJMenuItemActionPerformed
    {//GEN-HEADEREND:event_printDetailsJMenuItemActionPerformed
        printCountryDetails();
    }//GEN-LAST:event_printDetailsJMenuItemActionPerformed

    private void printFormJMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_printFormJMenuItemActionPerformed
    {//GEN-HEADEREND:event_printFormJMenuItemActionPerformed
        printForm();
    }//GEN-LAST:event_printFormJMenuItemActionPerformed

    private void quitJMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_quitJMenuItemActionPerformed
    {//GEN-HEADEREND:event_quitJMenuItemActionPerformed
        quitApplication();
    }//GEN-LAST:event_quitJMenuItemActionPerformed

    private void addJMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addJMenuItemActionPerformed
    {//GEN-HEADEREND:event_addJMenuItemActionPerformed
        AddCountry addCountryDialog = new AddCountry(this, true, null);  // Pass null to indicate adding a new country
        addCountryDialog.setVisible(true);
        loadCountriesList();  // Refresh the country list after adding
    }//GEN-LAST:event_addJMenuItemActionPerformed

    private void searchJMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_searchJMenuItemActionPerformed
    {//GEN-HEADEREND:event_searchJMenuItemActionPerformed
        searchCountry();
    }//GEN-LAST:event_searchJMenuItemActionPerformed

    private void alphaJRadioButtonMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_alphaJRadioButtonMenuItemActionPerformed
    {//GEN-HEADEREND:event_alphaJRadioButtonMenuItemActionPerformed
        try
        {
            Country[] countries = dbManager.getAllCountries();
            SortingUtility.mergeSortByName(countries, 0, countries.length - 1);  // Sort alphabetically

            // Update the list after sorting
            DefaultListModel<String> model = new DefaultListModel<>();
            for (Country country : countries)
            {
                model.addElement(country.getName());
            }
            countriesJList.setModel(model);
            JOptionPane.showMessageDialog(this, "Countries sorted alphabetically.", "Sort Success", JOptionPane.INFORMATION_MESSAGE);
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog(this, "Error sorting countries: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_alphaJRadioButtonMenuItemActionPerformed

    private void popJRadioButtonMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_popJRadioButtonMenuItemActionPerformed
    {//GEN-HEADEREND:event_popJRadioButtonMenuItemActionPerformed
        try
        {
            Country[] countries = dbManager.getAllCountries();
            SortingUtility.quickSortByPopulation(countries, 0, countries.length - 1);  // Sort by population

            // Update the list after sorting
            DefaultListModel<String> model = new DefaultListModel<>();
            for (Country country : countries)
            {
                model.addElement(country.getName());
            }
            countriesJList.setModel(model);
            JOptionPane.showMessageDialog(this, "Countries sorted by population.", "Sort Success", JOptionPane.INFORMATION_MESSAGE);
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog(this, "Error sorting countries: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_popJRadioButtonMenuItemActionPerformed

    private void aboutJMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_aboutJMenuItemActionPerformed
    {//GEN-HEADEREND:event_aboutJMenuItemActionPerformed
        About aboutDialog = new About(this, true);  // Create an instance of the About dialog
        aboutDialog.setVisible(true);  // Display the About dialog
    }//GEN-LAST:event_aboutJMenuItemActionPerformed
    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        videoLinkJLabel1MouseClicked
     * Description   Opens the video link in the default browser when the video link
     *               label is clicked.
     * @param        evt java.awt.event.MouseEvent - The mouse click event.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    private void videoLinkJLabel1MouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_videoLinkJLabel1MouseClicked
    {//GEN-HEADEREND:event_videoLinkJLabel1MouseClicked
        if (evt.getClickCount() > 0)
        {
            String link = videoLinkJLabel1.getToolTipText();  // Retrieve the stored link
            try
            {
                // Open the link in the default browser
                java.awt.Desktop.getDesktop().browse(java.net.URI.create(link));
            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog(null, "Failed to open link: " + link, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_videoLinkJLabel1MouseClicked

    private void deleteJMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_deleteJMenuItemActionPerformed
    {//GEN-HEADEREND:event_deleteJMenuItemActionPerformed

        deleteCountry();
    }//GEN-LAST:event_deleteJMenuItemActionPerformed

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        main
     * Description   The entry point of the application. Displays a splash screen
     *               for 4 seconds, sets the Nimbus look and feel (if available),
     *               and initializes the WorldCountriesGUI with system look and feel.
     *               Creates and displays the main GUI form on the Event Dispatch
     *               Thread.
     * @param args   Command line arguments (not used).
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public static void main(String args[])
    {
        Splash mySplash = new Splash(4000);     // duration = 5 seconds
        mySplash.showSplash();                  // show splash screen 
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try
        {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
            {
                if ("Nimbus".equals(info.getName()))
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (ClassNotFoundException ex)
        {
            java.util.logging.Logger.getLogger(WorldCountriesGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (InstantiationException ex)
        {
            java.util.logging.Logger.getLogger(WorldCountriesGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (IllegalAccessException ex)
        {
            java.util.logging.Logger.getLogger(WorldCountriesGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (javax.swing.UnsupportedLookAndFeelException ex)
        {
            java.util.logging.Logger.getLogger(WorldCountriesGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        try
        {
            // Set System L&F
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        }
        catch (UnsupportedLookAndFeelException e)
        {
            // handle exception
        }
        catch (ClassNotFoundException e)
        {
            // handle exception
        }
        catch (InstantiationException e)
        {
            // handle exception
        }
        catch (IllegalAccessException e)
        {
            // handle exception
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                new WorldCountriesGUI().setVisible(true);
            }

        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutJMenuItem;
    private javax.swing.JMenuItem addJMenuItem;
    private javax.swing.JRadioButtonMenuItem alphaJRadioButtonMenuItem;
    private javax.swing.JLabel areaJLabel;
    private javax.swing.JTextField areaJTextField;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel capitalJLabel;
    private javax.swing.JTextField capitalJTextField;
    private javax.swing.JLabel commonFactsJLabel;
    private javax.swing.JScrollPane commonFactsJScrollPane;
    private javax.swing.JTextArea commonFactsJTextArea;
    private javax.swing.JList<String> countriesJList;
    private javax.swing.JScrollPane countriesJScrollPane;
    private javax.swing.JLabel countryCodeJLabel;
    private javax.swing.JLabel countryJLabel;
    private javax.swing.JMenu countryJMenu;
    private javax.swing.JMenuItem deleteJMenuItem;
    private javax.swing.JLabel descriptionJLabel;
    private javax.swing.JScrollPane descriptionJScrollPane;
    private javax.swing.JTextArea descriptionJTextArea;
    private javax.swing.JMenuItem editJMenuItem;
    private javax.swing.JMenu fileJMenu;
    private javax.swing.JLabel flagImageJLabel;
    private javax.swing.JMenu helpJMenu;
    private javax.swing.JLabel historyJLabel;
    private javax.swing.JScrollPane historyJScrollPane;
    private javax.swing.JTextArea historyJTextArea;
    private javax.swing.JPanel infoJPanel;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JScrollPane lessKnownJScrollPane;
    private javax.swing.JLabel lesserKnownJLabel;
    private javax.swing.JTextArea lesserKnownJTextArea;
    private javax.swing.JLabel mapImageJLabel;
    private javax.swing.JMenuItem newJMenuItem;
    private javax.swing.JRadioButtonMenuItem popJRadioButtonMenuItem;
    private javax.swing.JLabel populationJLabel;
    private javax.swing.JTextField populationJTextField;
    private javax.swing.JMenuItem printDetailsJMenuItem;
    private javax.swing.JMenuItem printFormJMenuItem;
    private javax.swing.JMenuItem quitJMenuItem;
    private javax.swing.JLabel riverJLabel;
    private javax.swing.JTextField riverJTextField;
    private javax.swing.JMenuItem searchJMenuItem;
    private javax.swing.JMenu sortJMenu;
    private javax.swing.JLabel titleJLabel;
    private javax.swing.JLabel videoLinkJLabel;
    private javax.swing.JLabel videoLinkJLabel1;
    // End of variables declaration//GEN-END:variables

}
