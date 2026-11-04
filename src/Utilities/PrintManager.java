
package Utilities;

import java.awt.*;
import java.awt.print.*;
import javax.swing.*;
import worldcountriesdatabase.Country;

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <pre>
 * Class        PrintManager
 * File         PrintManager.java
 * Description  Provides methods to handle printing functionalities for
 *              components and country data, including dynamic scaling,
 *              error handling, and print quality improvements.
 * Platform     JDK 23.0.1; NetBeans IDE 23; macOS Sequoia 15.0.1
 * Date         10/28/2024
 * History log  10/28/2024 - Created PrintManager with printing utilities.
 * @version     1.0
 * </pre>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
public class PrintManager implements Printable
{

    private final Component componentToBePrinted;

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Constructor   PrintManager
     * Description   Constructs a new PrintManager that will print the specified
     *               component.
     * @param        component the component that will be printed
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public PrintManager(Component component)
    {
        this.componentToBePrinted = component;
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        disableDoubleBuffering
     * Description   Disables double buffering for the specified component to
     *               improve print quality.
     * @param        c the component for which double buffering will be disabled
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public static void disableDoubleBuffering(Component c)
    {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(false);
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        enableDoubleBuffering
     * Description   Re-enables double buffering for the specified component
     *               after printing.
     * @param        c the component for which double buffering will be re-enabled
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public static void enableDoubleBuffering(Component c)
    {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(true);
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        print
     * Description   Creates an instance of PrinterJob, sets the component to
     *               be printed, and displays the print dialog. Initiates the
     *               printing process if the user confirms.
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public void print()
    {
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(this);
        if (printJob.printDialog())
        {
            try
            {
                printJob.print();
            }
            catch (PrinterException pe)
            {
                JOptionPane.showMessageDialog(null, "Error occurred during printing: " + pe.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        print
     * Description   Prints the component on the specified page. Scales the
     *               component to fit within the printable area and disables
     *               double buffering to improve print quality.
     * @param        g the graphics object to render the component
     * @param pageFormat the page's size and margins
     * @param pageIndex the index of the page to be printed
     * @return {@code PAGE_EXISTS} if the page exists, otherwise
     * {@code NO_SUCH_PAGE}
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    @Override
    public int print(Graphics g, PageFormat pageFormat, int pageIndex)
    {
        if (pageIndex > 0)
        {
            return NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) g;
        double scaleFactor = Math.min(
                pageFormat.getImageableWidth() / componentToBePrinted.getWidth(),
                pageFormat.getImageableHeight() / componentToBePrinted.getHeight());
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        g2d.scale(scaleFactor, scaleFactor);

        disableDoubleBuffering(componentToBePrinted);
        componentToBePrinted.paint(g2d);
        enableDoubleBuffering(componentToBePrinted);

        return PAGE_EXISTS;
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        printCountry
     * Description   Prints the details of the specified Country object.
     * @param        country the country object whose details will be printed
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public static void printCountry(Country country)
    {
        if (country == null)
        {
            System.out.println("No country to print.");
            return;
        }

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(new Printable()
        {
            @Override
            public int print(Graphics g, PageFormat pf, int page) throws PrinterException
            {
                if (page > 0)
                {
                    return NO_SUCH_PAGE;
                }

                Graphics2D g2d = (Graphics2D) g;
                g2d.translate(pf.getImageableX(), pf.getImageableY());
                g2d.setFont(new Font("Serif", Font.PLAIN, 12));

                // Format the country details for printing
                String countryInfo = String.format(
                        "Country Details\n"
                        + "-----------------\n"
                        + "Country: %s\n"
                        + "Capital: %s\n"
                        + "Population: %s\n"
                        + "River: %s\n"
                        + "Area: %.2f km²\n"
                        + "Description: %s\n"
                        + "Common Fact: %s\n"
                        + "Lesser-Known Fact: %s\n"
                        + "History: %s\n",
                        country.getName(),
                        country.getCapital(),
                        country.getFormattedPopulation(),
                        country.getRiver(),
                        country.getArea(),
                        country.getDescription(),
                        country.getCommonFact(),
                        country.getLesserKnownFact(),
                        country.getHistory()
                );

                // Draw the formatted text
                int y = 20;
                for (String line : countryInfo.split("\n"))
                {
                    g2d.drawString(line, 0, y);
                    y += g2d.getFontMetrics().getHeight();
                }

                return PAGE_EXISTS;
            }

        });

        // Show the print dialog and proceed if the user confirms
        if (job.printDialog())
        {
            try
            {
                job.print();
            }
            catch (PrinterException e)
            {
                JOptionPane.showMessageDialog(null, "Printing error: " + e.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        printCountryDetails
     * Description   Displays the details of a specified Country object and
     *               provides an option to print them.
     * @param        country the country whose details will be displayed and
     *               optionally printed
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public static void printCountryDetails(Country country)
    {
        if (country == null)
        {
            JOptionPane.showMessageDialog(null, "No country selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Format the country details for display
        String countryInfo = String.format(
                "<html><body>"
                + "<h2>Country Details</h2>"
                + "<p><strong>Country:</strong> %s</p>"
                + "<p><strong>Capital:</strong> %s</p>"
                + "<p><strong>Population:</strong> %s</p>"
                + "<p><strong>River:</strong> %s</p>"
                + "<p><strong>Area:</strong> %.2f</p>"
                + "<p><strong>Description:</strong> %s</p>"
                + "<p><strong>Common Fact:</strong> %s</p>"
                + "<p><strong>Lesser-Known Fact:</strong> %s</p>"
                + "<p><strong>History:</strong> %s</p>"
                + "</body></html>",
                country.getName(),
                country.getCapital(),
                country.getFormattedPopulation(),
                country.getRiver(),
                country.getArea(),
                country.getDescription(),
                country.getCommonFact(),
                country.getLesserKnownFact(),
                country.getHistory()
        );

        // Show dialog with Print and Cancel options
        int option = JOptionPane.showOptionDialog(
                null,
                new JLabel(countryInfo),
                "Country Details",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                new Object[]
                {
                    "Print", "Cancel"
                },
                "Print");

        // If the user clicks "Print," proceed with printing
        if (option == JOptionPane.YES_OPTION)
        {
            printCountry(country);  // Call the method to print to a physical printer
        }
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        printAllCountries
     * Description   Displays the details of all countries in a dialog box.
     * @param        countries the array of Country objects whose details will
     *               be displayed
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public static void printAllCountries(Country[] countries)
    {
        if (countries == null || countries.length == 0)
        {
            System.out.println("No countries to print.");
            return;
        }

        StringBuilder allCountriesInfo = new StringBuilder("All Countries:\n");
        for (Country country : countries)
        {
            allCountriesInfo.append("Country: ").append(country.getName())
                    .append("\nCapital: ").append(country.getCapital())
                    .append("\nPopulation: ").append(country.getFormattedPopulation())
                    .append("\nRiver: ").append(country.getRiver())
                    .append("\nArea: ").append(country.getArea())
                    .append("\nDescription: ").append(country.getDescription())
                    .append("\nCommon Fact: ").append(country.getCommonFact())
                    .append("\nLesser-Known Fact: ").append(country.getLesserKnownFact())
                    .append("\nHistory: ").append(country.getHistory())
                    .append("\n--------------------------\n");
        }

        JOptionPane.showMessageDialog(null, allCountriesInfo.toString(), "All Country Details", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     * Method        printForm
     * Description   Prints the entire GUI form.
     * @param        form the form component to be printed
     * </pre>
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public static void printForm(Component form)
    {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(new Printable()
        {
            @Override
            public int print(Graphics g, PageFormat pf, int page) throws PrinterException
            {
                if (page > 0)
                {
                    return NO_SUCH_PAGE;
                }

                Graphics2D g2d = (Graphics2D) g;
                g2d.translate(pf.getImageableX(), pf.getImageableY());

                double scaleX = pf.getImageableWidth() / form.getWidth();
                double scaleY = pf.getImageableHeight() / form.getHeight();
                double scale = Math.min(scaleX, scaleY);
                g2d.scale(scale, scale);

                form.printAll(g);

                return PAGE_EXISTS;
            }

        });

        if (job.printDialog())
        {
            try
            {
                job.print();
            }
            catch (PrinterException e)
            {
                JOptionPane.showMessageDialog(null, "Printing error: " + e.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
