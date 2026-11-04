
package worldcountriesdatabase;

import DatabaseUtilities.CountriesDBManager;
import Utilities.Validation;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.sql.SQLException;
import java.util.logging.*;

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Class        AddCountry
 * File         AddCountry.java
 * Description  This class provides a dialog for adding or editing a country
 *              in the World Countries Database. It includes fields for
 *              country code, name, capital, population, and other attributes,
 *              as well as options to upload flags, maps, and link videos.
 * Platform     JDK 23.0.1; NetBeans IDE 23; macOS Sequoia 15.0.1
 * Date         11/5/2024
 * @version     1.0
 * </pre>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
public class AddCountry extends javax.swing.JDialog
{

    private static final Logger logger = Logger.getLogger(AddCountry.class.getName());
    private boolean isEditMode = false;
    private boolean isUserAddedCountry = false;
    private CountriesDBManager dbManager;
    private Country editingCountry;
    private BufferedImage tempFlagImage; // Temporarily holds the uploaded flag image
    private BufferedImage tempMapImage;  // Temporarily holds the uploaded map image
    private boolean isSaveConfirmed = false;

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Constructor   AddCountry
     * Description   Initializes the AddCountry dialog for either adding a new country
     *               or editing an existing one, and sets up initial configurations.
     * @param parent the parent frame for the dialog.
     * @param modal specifies if the dialog is modal.
     * @param country the Country object to edit, or null to add a new country.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public AddCountry(java.awt.Frame parent, boolean modal, Country country)
    {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);

        addEditorListeners(descriptionJTextField, "Description");
        addEditorListeners(commonJTextField, "Common Fact");
        addEditorListeners(lesserKnownJTextField, "Lesser Known Fact");
        addEditorListeners(historyJTextField, "History");

        setInitialFieldHighlight();
        addFieldFocusListeners();

        try
        {
            dbManager = new CountriesDBManager(WorldCountriesGUI.getCurrentTableName());
        }
        catch (SQLException e)
        {
            showErrorDialog("Database error: " + e.getMessage());
            logger.log(Level.SEVERE, "Database connection error", e);
            return;
        }

        if (country != null)
        {
            enableEditMode(country);
        }
        else
        {
            prepareForNewCountry();
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        setInitialFieldHighlight
     * Description   Sets the initial background color for certain fields to highlight them.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void setInitialFieldHighlight()
    {
        Color initialHighlightColor = new Color(173, 216, 230);
        JTextField[] fields =
        {
            nameJTextField, capitalJTextField, populationJTextField
        };
        for (JTextField field : fields)
        {
            field.setBackground(initialHighlightColor);
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        enableEditMode
     * Description   Enables edit mode for the dialog and populates fields with data
     *               from the given Country object.
     * @param country the Country object to edit.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void enableEditMode(Country country)
    {
        isEditMode = true;
        editingCountry = country;
        saveJButton.setText("Update");
        titleJLabel.setText("Edit Country");
        setTitle("Editing " + country.getName());
        populateFieldsForEdit();
        highlightRequiredFieldsInEdit();
        setCountryCodeEditableStatus();
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        prepareForNewCountry
     * Description   Prepares the dialog for adding a new country by disabling certain fields.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void prepareForNewCountry()
    {
        countryCodeJTextField.setEnabled(false);
        addNameChangeListener();
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        populateFieldsForEdit
     * Description   Populates input fields with data from the CountryInfo of the country being edited.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void populateFieldsForEdit()
    {
        countryCodeJTextField.setText(editingCountry.getCountryCode());
        nameJTextField.setText(editingCountry.getName());
        capitalJTextField.setText(editingCountry.getCapital());
        populationJTextField.setText(String.valueOf(editingCountry.getPopulation()));
        riverJTextField.setText(editingCountry.getRiver());
        areaJTextField.setText(String.valueOf(editingCountry.getArea()));

        try
        {
            dbManager.getCountryInfo(editingCountry.getCountryCode()).ifPresent(countryInfo ->
            {
                descriptionJTextField.setText(countryInfo.getDescription());
                commonJTextField.setText(countryInfo.getCommonFact());
                lesserKnownJTextField.setText(countryInfo.getLesserKnownFact());
                historyJTextField.setText(countryInfo.getHistoricalInfo());
            });
        }
        catch (SQLException e)
        {
            showErrorDialog("Error loading country information.");
            logger.log(Level.SEVERE, "Error loading country information for " + editingCountry.getCountryCode(), e);
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        addFieldFocusListeners
     * Description   Adds listeners to highlight and validate required fields when interacted with.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void addFieldFocusListeners()
    {
        JTextField[] fields =
        {
            nameJTextField, capitalJTextField, populationJTextField
        };
        for (JTextField field : fields)
        {
            final boolean[] hasInteracted =
            {
                false
            };
            field.addKeyListener(new KeyAdapter()
            {
                @Override
                public void keyTyped(KeyEvent e)
                {
                    hasInteracted[0] = true;
                    field.setBackground(Color.WHITE);
                }

            });
            field.addFocusListener(new FocusAdapter()
            {
                @Override
                public void focusLost(FocusEvent e)
                {
                    if (hasInteracted[0] && field.getText().trim().isEmpty())
                    {
                        field.setBackground(Color.RED);
                        logger.log(Level.INFO, "Field {0} left empty after interaction", field.getName());
                    }
                    else if (hasInteracted[0])
                    {
                        field.setBackground(Color.WHITE);
                    }
                }

            });
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        setCountryCodeEditableStatus
     * Description   Sets the editability of the country code field based on whether the
     *               country code already exists in the abbreviations table.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void setCountryCodeEditableStatus()
    {
        if (isEditMode)
        {
            countryCodeJTextField.setEnabled(false);
        }
        else
        {
            try
            {
                isUserAddedCountry = !dbManager.countryCodeExistsInAbbreviate(editingCountry.getCountryCode());
                countryCodeJTextField.setEnabled(isUserAddedCountry);
                logger.log(Level.INFO, "Country code editable status set to {0}", isUserAddedCountry);
            }
            catch (SQLException e)
            {
                showErrorDialog("Error checking country code.");
                logger.log(Level.SEVERE, "Error checking country code", e);
            }
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        addNameChangeListener
     * Description   Adds a listener to the name field to automatically suggest a country code
     *               based on the entered name.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void addNameChangeListener()
    {
        nameJTextField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyReleased(KeyEvent e)
            {
                updateCountryCodeSuggestion();
            }

        });
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        updateCountryCodeSuggestion
     * Description   Generates and updates a suggested country code based on the entered country name.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void updateCountryCodeSuggestion()
    {
        try
        {
            String name = nameJTextField.getText().trim();
            if (!name.isEmpty())
            {
                String suggestedCode = generateUniqueCode(name);
                countryCodeJTextField.setText(suggestedCode);
                countryCodeJTextField.setEnabled(false);
                logger.log(Level.INFO, "Suggested country code {0} for name {1}", new Object[]
                {
                    suggestedCode, name
                });
            }
            else
            {
                countryCodeJTextField.setText("");
                countryCodeJTextField.setEnabled(false);
                logger.log(Level.INFO, "Country code field cleared as name field is empty");
            }
        }
        catch (SQLException ex)
        {
            showErrorDialog("Error generating unique country code.");
            logger.log(Level.SEVERE, "Error generating unique country code", ex);
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        showErrorDialog
     * Description   Displays an error dialog with the specified message.
     * @param message the error message to display.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void showErrorDialog(String message)
    {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        highlightRequiredFieldsInEdit
     * Description   Highlights required fields in red if they are empty in edit mode.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void highlightRequiredFieldsInEdit()
    {
        JTextField[] requiredFields =
        {
            nameJTextField, capitalJTextField, populationJTextField
        };

        for (JTextField field : requiredFields)
        {
            if (field.getText().trim().isEmpty())
            {
                field.setBackground(Color.RED); // Highlight in red if empty during edit
                logger.log(Level.INFO, "Field {0} highlighted in red as it is empty", field.getName());
            }
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        saveCountry
     * Description   Saves the country to the database, adding or editing as needed, and saves any
     *               uploaded images.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void saveCountry()
    {

        boolean allFieldsValid = Validation.validateRequiredFields(new JTextField[]
        {
            countryCodeJTextField, nameJTextField, capitalJTextField, populationJTextField
        });
        if (allFieldsValid)
        {
            if (isEditMode)
            {
                editCountry();
            }
            else
            {
                addCountry();
            }
            if (tempFlagImage != null)
            {
                saveFlagImage();
            }
            if (tempMapImage != null)
            {
                saveMapImage();
            }
            ((WorldCountriesGUI) getParent()).refreshCountryList();
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        saveFlagImage
     * Description   Saves the uploaded flag image to the appropriate directory.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void saveFlagImage()
    {
        try
        {
            Path flagImagesPath = Paths.get("src/FlagImages");
            if (Files.notExists(flagImagesPath))
            {
                Files.createDirectories(flagImagesPath);
            }
            String countryName = capitalizeEachWord(nameJTextField.getText().trim());
            File outputFile = new File(flagImagesPath.toFile(), countryName + ".jpg");
            ImageIO.write(tempFlagImage, "jpg", outputFile);
            JOptionPane.showMessageDialog(this, "Flag saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(this, "Failed to save flag: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        saveMapImage
     * Description   Saves the uploaded map image to the appropriate directory.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void saveMapImage()
    {
        try
        {
            Path mapImagesPath = Paths.get("src/MapImages");
            if (Files.notExists(mapImagesPath))
            {
                Files.createDirectories(mapImagesPath);
            }
            String countryName = capitalizeEachWord(nameJTextField.getText().trim());
            File outputFile = new File(mapImagesPath.toFile(), countryName + ".jpg");
            ImageIO.write(tempMapImage, "jpg", outputFile);
            JOptionPane.showMessageDialog(this, "Map saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(this, "Failed to save map: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        addCountry
     * Description   Adds a new country and its information to the database.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void addCountry()
    {
        try
        {
            if (!isSaveConfirmed)
            {
                return; // Don't proceed if save isn't confirmed
            }
            String countryCode = countryCodeJTextField.getText().trim();
            String countryName = nameJTextField.getText().trim();

            Validation.validateCountryUniqueness(dbManager, countryCode, countryName);

            if (!dbManager.countryCodeExistsInAbbreviate(countryCode))
            {
                dbManager.addUniqueCountryCodeToAbbreviate(countryCode, countryName);
                logger.log(Level.INFO, "Added country code {0} with name {1} to countriesAbreviate table", new Object[]
                {
                    countryCode, countryName
                });
            }

            if (!dbManager.isCountryCodeAndNameMatched(countryCode, countryName))
            {
                throw new SQLException("The provided country code does not match the associated country name.");
            }

            Country newCountry = new Country(
                    countryCode,
                    countryName,
                    capitalJTextField.getText().trim(),
                    Double.parseDouble(populationJTextField.getText().trim()),
                    riverJTextField.getText().trim(),
                    areaJTextField.getText().isEmpty() ? 0 : Double.parseDouble(areaJTextField.getText().trim())
            );
            CountryInfo newCountryInfo = new CountryInfo(
                    countryCode,
                    countryName,
                    descriptionJTextField.getText().trim(),
                    commonJTextField.getText().trim(),
                    lesserKnownJTextField.getText().trim(),
                    historyJTextField.getText().trim()
            );

            dbManager.addCountry(newCountry);
            dbManager.saveCountryInfo(newCountryInfo);
            JOptionPane.showMessageDialog(this, "Country added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        }
        catch (SQLException e)
        {
            logger.log(Level.SEVERE, "Database error while adding country: {0}", e.getMessage());
            showErrorDialog("Database error: " + e.getMessage());
        }
        catch (NumberFormatException e)
        {
            logger.log(Level.SEVERE, "Invalid number format for population or area: {0}", e.getMessage());
            showErrorDialog("Invalid number format for population or area.");
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        editCountry
     * Description   Updates an existing country and its information in the database.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void editCountry()
    {
        try
        {
            if (!isSaveConfirmed)
            {
                return; // Don't proceed if save isn't confirmed
            }
            if (!dbManager.isCountryCodeAndNameMatched(editingCountry.getCountryCode(), nameJTextField.getText().trim()))
            {
                throw new SQLException("The provided country code does not match the associated country name.");
            }

            editingCountry.setCapital(capitalJTextField.getText().trim());
            editingCountry.setPopulation(Double.parseDouble(populationJTextField.getText().trim()));
            editingCountry.setRiver(riverJTextField.getText().trim());
            editingCountry.setArea(areaJTextField.getText().isEmpty() ? 0 : Double.parseDouble(areaJTextField.getText().trim()));

            dbManager.updateCountry(editingCountry);

            CountryInfo updatedInfo = new CountryInfo(
                    editingCountry.getCountryCode(),
                    nameJTextField.getText(),
                    descriptionJTextField.getText().trim(),
                    commonJTextField.getText().trim(),
                    lesserKnownJTextField.getText().trim(),
                    historyJTextField.getText().trim()
            );

            dbManager.saveCountryInfo(updatedInfo);

            JOptionPane.showMessageDialog(this, "Country updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        }
        catch (SQLException e)
        {
            showErrorDialog("Database error: " + e.getMessage());
            logger.log(Level.SEVERE, "Database error while editing country: {0}", e.getMessage());
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        generateUniqueCode
     * Description   Generates a unique country code based on the country's name.
     * @param countryName the name of the country.
     * @return a unique code for the country.
     * @throws SQLException if a database access error occurs.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private String generateUniqueCode(String countryName) throws SQLException
    {
        String baseCode = ("" + countryName.charAt(0) + countryName.charAt(countryName.length() - 1)).toUpperCase();
        int suffix = 0;
        String suggestedCode = baseCode;

        while (dbManager.countryCodeExistsInAbbreviate(suggestedCode))
        {
            logger.log(Level.INFO, "Code {0} already exists. Trying alternative strategy", suggestedCode);
            if (suffix == 0)
            {
                suggestedCode = getFirstTwoConsonants(countryName).toUpperCase();
            }
            else if (suffix == 1)
            {
                suggestedCode = getAlternativeTwoLetters(countryName).toUpperCase();
            }
            else
            {
                suggestedCode = baseCode.substring(0, 1) + (char) ('A' + suffix - 2);
            }
            suffix++;
        }
        return suggestedCode;
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        getFirstTwoConsonants
     * Description   Retrieves the first two consonants from the country's name.
     * @param name   the name of the country.
     * @return the first two consonants or a fallback string if less than two
     * are found.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private String getFirstTwoConsonants(String name)
    {
        String consonants = name.replaceAll("[aeiou\s]", "");
        return consonants.length() >= 2 ? consonants.substring(0, 2) : (consonants + "X").substring(0, 2);
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        getAlternativeTwoLetters
     * Description   Retrieves an alternative two-letter combination from the country's name,
     *               using vowels as fallback if necessary.
     * @param name   the name of the country.
     * @return a two-letter alternative code.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private String getAlternativeTwoLetters(String name)
    {
        String vowels = name.replaceAll("[^aeiou]", "");
        return vowels.length() >= 2 ? vowels.substring(0, 2) : (vowels + "Z").substring(0, 2);
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        capitalizeEachWord
     * Description   Capitalizes each word in a given string.
     * @param input  the string to capitalize.
     * @return the capitalized string.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private String capitalizeEachWord(String input)
    {
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
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        uploadFlag
     * Description   Opens a file chooser to upload a flag image and resizes it.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void uploadFlag()
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "bmp"));

        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION)
        {
            File selectedFile = fileChooser.getSelectedFile();
            try
            {
                BufferedImage originalImage = ImageIO.read(selectedFile);
                tempFlagImage = resizeImage(originalImage, 300, 200);
                JOptionPane.showMessageDialog(this, "Flag uploaded successfully! It will be saved when you save the country.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            catch (IOException e)
            {
                JOptionPane.showMessageDialog(this, "Failed to upload flag: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        uploadMap
     * Description   Opens a file chooser to upload a map image and resizes it.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void uploadMap()
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "bmp"));

        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION)
        {
            File selectedFile = fileChooser.getSelectedFile();
            try
            {
                BufferedImage originalImage = ImageIO.read(selectedFile);
                tempMapImage = resizeImage(originalImage, 300, 300);
                JOptionPane.showMessageDialog(this, "Map uploaded successfully! It will be saved when you save the country.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            catch (IOException e)
            {
                JOptionPane.showMessageDialog(this, "Failed to upload map: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        linkVideo
     * Description   Prompts the user to enter a video link URL and saves it to the database.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void linkVideo()
    {
        String videoLink = JOptionPane.showInputDialog(this, "Enter the video link URL:", "Link to Video", JOptionPane.PLAIN_MESSAGE);

        if (videoLink != null && !videoLink.trim().isEmpty() && Validation.isValidURL(videoLink))
        {
            try
            {
                String countryCode = countryCodeJTextField.getText().trim();
                if (countryCode.isEmpty())
                {
                    showErrorDialog("Country code is required to link a video.");
                    return;
                }
                String countryName = nameJTextField.getText().trim();
                if (countryName.isEmpty())
                {
                    showErrorDialog("Country name is required to link a video.");
                    return;
                }
                dbManager.updateVideoLink(countryCode, countryName, videoLink);
                JOptionPane.showMessageDialog(this, "Video link saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            catch (SQLException e)
            {
                showErrorDialog("Failed to save video link: " + e.getMessage());
            }
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Invalid or empty URL.", "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        resizeImage
     * Description   Resizes an image to the specified width and height.
     * @param originalImage the original image to resize.
     * @param targetWidth the target width.
     * @param targetHeight the target height.
     * @return the resized BufferedImage.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight)
    {
        Image scaledImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        resizedImage.getGraphics().drawImage(scaledImage, 0, 0, null);
        return resizedImage;
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        openEditorDialog
     * Description   Opens a dialog for editing text in the specified field.
     * @param textField the JTextField to edit.
     * @param fieldName the name of the field.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void openEditorDialog(JTextField textField, String fieldName)
    {
        JDialog editorDialog = new JDialog(this, "Edit " + fieldName, true);
        editorDialog.setSize(400, 300);
        editorDialog.setLocationRelativeTo(this);

        JTextArea editorArea = new JTextArea(5, 40);
        editorArea.setText(textField.getText());
        editorArea.setLineWrap(true);
        editorArea.setWrapStyleWord(true);
        editorArea.setFont(textField.getFont());
        JScrollPane scrollPane = new JScrollPane(editorArea);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e ->
        {
            textField.setText(editorArea.getText());
            editorDialog.dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> editorDialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        editorDialog.setLayout(new BorderLayout());
        editorDialog.add(scrollPane, BorderLayout.CENTER);
        editorDialog.add(buttonPanel, BorderLayout.SOUTH);

        editorDialog.setVisible(true);
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        addEditorListeners
     * Description   Adds listeners for double-click and F2 key press to open an editor dialog.
     * @param textField the JTextField to add listeners to.
     * @param fieldName the name of the field.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void addEditorListeners(JTextField textField, String fieldName)
    {
        textField.addMouseListener(new java.awt.event.MouseAdapter()
        {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                if (evt.getClickCount() == 2)
                {
                    openEditorDialog(textField, fieldName);
                }
            }

        });

        textField.addKeyListener(new java.awt.event.KeyAdapter()
        {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt)
            {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_F2)
                {
                    openEditorDialog(textField, fieldName);
                }
            }

        });
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        generateAIInfo
     * Description   Displays a message that AI-generated information functionality is coming soon.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private void generateAIInfo()
    {
        JOptionPane.showMessageDialog(this, "AI-generated info functionality coming soon!", "AI Assistance", JOptionPane.INFORMATION_MESSAGE);
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

        titleJLabel = new javax.swing.JLabel();
        displayJPanel = new javax.swing.JPanel();
        countryCodeJLabel = new javax.swing.JLabel();
        countryCodeJTextField = new javax.swing.JTextField();
        nameJLabel = new javax.swing.JLabel();
        nameJTextField = new javax.swing.JTextField();
        capitalJLabel = new javax.swing.JLabel();
        capitalJTextField = new javax.swing.JTextField();
        populationJLabel = new javax.swing.JLabel();
        populationJTextField = new javax.swing.JTextField();
        riverJLabel = new javax.swing.JLabel();
        riverJTextField = new javax.swing.JTextField();
        areaJLabel = new javax.swing.JLabel();
        areaJTextField = new javax.swing.JTextField();
        descriptionJLabel = new javax.swing.JLabel();
        descriptionJTextField = new javax.swing.JTextField();
        commonJLabel = new javax.swing.JLabel();
        commonJTextField = new javax.swing.JTextField();
        lesserKnownJLabel = new javax.swing.JLabel();
        lesserKnownJTextField = new javax.swing.JTextField();
        historyJLabel = new javax.swing.JLabel();
        historyJTextField = new javax.swing.JTextField();
        controlJPanel = new javax.swing.JPanel();
        saveJButton = new javax.swing.JButton();
        cancelJButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        flagJButton = new javax.swing.JButton();
        mapJButton = new javax.swing.JButton();
        linkJButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        fillerJLabel1 = new javax.swing.JLabel();
        aiJButton = new javax.swing.JButton();
        fillerJLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Adding New Country");
        setAlwaysOnTop(true);
        setType(java.awt.Window.Type.UTILITY);

        titleJLabel.setFont(new java.awt.Font("Tempus Sans ITC", 2, 30)); // NOI18N
        titleJLabel.setForeground(new java.awt.Color(0, 102, 102));
        titleJLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleJLabel.setText("Add Country");

        displayJPanel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        displayJPanel.setVerifyInputWhenFocusTarget(false);
        displayJPanel.setLayout(new java.awt.GridLayout(10, 2, 3, 3));

        countryCodeJLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        countryCodeJLabel.setText("Country Code:");
        countryCodeJLabel.setToolTipText("");
        displayJPanel.add(countryCodeJLabel);

        countryCodeJTextField.setColumns(9);
        countryCodeJTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        countryCodeJTextField.setToolTipText("");
        displayJPanel.add(countryCodeJTextField);

        nameJLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        nameJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        nameJLabel.setText("Country Name:");
        displayJPanel.add(nameJLabel);

        nameJTextField.setColumns(9);
        nameJTextField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        nameJTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        displayJPanel.add(nameJTextField);

        capitalJLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        capitalJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        capitalJLabel.setText("Capital:");
        displayJPanel.add(capitalJLabel);

        capitalJTextField.setColumns(9);
        capitalJTextField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        capitalJTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        displayJPanel.add(capitalJTextField);

        populationJLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        populationJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        populationJLabel.setText("Population:");
        displayJPanel.add(populationJLabel);

        populationJTextField.setColumns(9);
        populationJTextField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        populationJTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        displayJPanel.add(populationJTextField);

        riverJLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        riverJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        riverJLabel.setText("Longest River:");
        displayJPanel.add(riverJLabel);

        riverJTextField.setColumns(9);
        riverJTextField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        riverJTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        displayJPanel.add(riverJTextField);

        areaJLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        areaJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        areaJLabel.setText("Land Area (km):");
        displayJPanel.add(areaJLabel);

        areaJTextField.setColumns(9);
        displayJPanel.add(areaJTextField);

        descriptionJLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        descriptionJLabel.setText("Description:");
        displayJPanel.add(descriptionJLabel);

        descriptionJTextField.setColumns(9);
        displayJPanel.add(descriptionJTextField);

        commonJLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        commonJLabel.setText("Common Fact:");
        displayJPanel.add(commonJLabel);

        commonJTextField.setColumns(9);
        displayJPanel.add(commonJTextField);

        lesserKnownJLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lesserKnownJLabel.setText("Lesser Known Fact:");
        displayJPanel.add(lesserKnownJLabel);

        lesserKnownJTextField.setColumns(9);
        displayJPanel.add(lesserKnownJTextField);

        historyJLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        historyJLabel.setText("History:");
        displayJPanel.add(historyJLabel);

        historyJTextField.setColumns(9);
        displayJPanel.add(historyJTextField);

        controlJPanel.setLayout(new java.awt.GridLayout(1, 2, 3, 3));

        saveJButton.setBackground(new java.awt.Color(255, 255, 204));
        saveJButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        saveJButton.setMnemonic('A');
        saveJButton.setText("Add");
        saveJButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                saveJButtonActionPerformed(evt);
            }
        });
        controlJPanel.add(saveJButton);

        cancelJButton.setBackground(new java.awt.Color(255, 255, 204));
        cancelJButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cancelJButton.setMnemonic('C');
        cancelJButton.setText("Cancel");
        cancelJButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cancelJButtonActionPerformed(evt);
            }
        });
        controlJPanel.add(cancelJButton);

        jPanel1.setLayout(new java.awt.GridLayout(1, 0));

        flagJButton.setText("Upload Flag");
        flagJButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                flagJButtonActionPerformed(evt);
            }
        });
        jPanel1.add(flagJButton);

        mapJButton.setText("Upload Map");
        mapJButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                mapJButtonActionPerformed(evt);
            }
        });
        jPanel1.add(mapJButton);

        linkJButton.setText("Link to Video");
        linkJButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                linkJButtonActionPerformed(evt);
            }
        });
        jPanel1.add(linkJButton);

        jPanel2.setLayout(new java.awt.GridLayout(1, 3));
        jPanel2.add(fillerJLabel1);

        aiJButton.setText("AI Assistance");
        aiJButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                aiJButtonActionPerformed(evt);
            }
        });
        jPanel2.add(aiJButton);
        jPanel2.add(fillerJLabel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(titleJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(controlJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(displayJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(titleJLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(displayJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(controlJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void cancelJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cancelJButtonActionPerformed
    {//GEN-HEADEREND:event_cancelJButtonActionPerformed
        isSaveConfirmed = false; // Ensures nothing gets saved
        this.dispose();
    }//GEN-LAST:event_cancelJButtonActionPerformed

    private void saveJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_saveJButtonActionPerformed
    {//GEN-HEADEREND:event_saveJButtonActionPerformed
        isSaveConfirmed = true;
        saveCountry();
        this.dispose();
    }//GEN-LAST:event_saveJButtonActionPerformed

    private void flagJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_flagJButtonActionPerformed
    {//GEN-HEADEREND:event_flagJButtonActionPerformed
        uploadFlag();
    }//GEN-LAST:event_flagJButtonActionPerformed

    private void mapJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_mapJButtonActionPerformed
    {//GEN-HEADEREND:event_mapJButtonActionPerformed
        uploadMap();
    }//GEN-LAST:event_mapJButtonActionPerformed

    private void linkJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_linkJButtonActionPerformed
    {//GEN-HEADEREND:event_linkJButtonActionPerformed
        linkVideo();
    }//GEN-LAST:event_linkJButtonActionPerformed

    private void aiJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_aiJButtonActionPerformed
    {//GEN-HEADEREND:event_aiJButtonActionPerformed
        generateAIInfo();
    }//GEN-LAST:event_aiJButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aiJButton;
    private javax.swing.JLabel areaJLabel;
    private javax.swing.JTextField areaJTextField;
    private javax.swing.JButton cancelJButton;
    private javax.swing.JLabel capitalJLabel;
    private javax.swing.JTextField capitalJTextField;
    private javax.swing.JLabel commonJLabel;
    private javax.swing.JTextField commonJTextField;
    private javax.swing.JPanel controlJPanel;
    private javax.swing.JLabel countryCodeJLabel;
    private javax.swing.JTextField countryCodeJTextField;
    private javax.swing.JLabel descriptionJLabel;
    private javax.swing.JTextField descriptionJTextField;
    private javax.swing.JPanel displayJPanel;
    private javax.swing.JLabel fillerJLabel1;
    private javax.swing.JLabel fillerJLabel2;
    private javax.swing.JButton flagJButton;
    private javax.swing.JLabel historyJLabel;
    private javax.swing.JTextField historyJTextField;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lesserKnownJLabel;
    private javax.swing.JTextField lesserKnownJTextField;
    private javax.swing.JButton linkJButton;
    private javax.swing.JButton mapJButton;
    private javax.swing.JLabel nameJLabel;
    private javax.swing.JTextField nameJTextField;
    private javax.swing.JLabel populationJLabel;
    private javax.swing.JTextField populationJTextField;
    private javax.swing.JLabel riverJLabel;
    private javax.swing.JTextField riverJTextField;
    private javax.swing.JButton saveJButton;
    private javax.swing.JLabel titleJLabel;
    // End of variables declaration//GEN-END:variables
}
