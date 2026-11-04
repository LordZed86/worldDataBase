# World Countries Database

A Java Swing desktop app for browsing, searching and editing information about the countries of the world, backed by a MySQL database. Built for CS 143.

For each country the app shows its flag, a photo, capital, population, land area, longest river, a description, a common fact, a lesser-known fact, some history and a link to a video.

## Features

- Browse every country in a list and view its details
- **Add**, **edit**, **search** and **delete** countries (Country menu)
- **Sort** by name or population, using sorting algorithms implemented by hand in `Utilities/SortingUtility.java`
- Switch between two datasets, `countriesA` and `countriesB` (File → Data Set)
- Print a country's details or the whole form
- Input validation for new and edited countries

## Requirements

- **Java 23** or newer (JDK)
- **MySQL 8** running locally on port 3306
- The MySQL Connector/J driver. It's included at `lib/mysql-connector-j-8.4.0/mysql-connector-j-8.4.0.jar`

## Setup

On first run the app creates the `WorldDB` database and its tables, then fills them from the CSV files in `src/Data`. You only need to give it your MySQL login.

1. **Add your MySQL credentials.** Copy the example file and fill in your username and password:

   ```sh
   cp db.properties.example db.properties
   ```

   ```properties
   db.user=root
   db.password=your-password
   ```

   `db.properties` is gitignored, so your password stays out of the repo.

## Build and run

Run these from the project root. The app reads `src/Data/*.csv` and `db.properties` using paths relative to the folder you start it from.

**macOS / Linux**

```sh
javac -cp lib/mysql-connector-j-8.4.0/mysql-connector-j-8.4.0.jar -d out $(find src -name "*.java")
java -cp "out:src:lib/mysql-connector-j-8.4.0/mysql-connector-j-8.4.0.jar" worldcountriesdatabase.WorldCountriesGUI
```

**Windows (PowerShell)**

```powershell
javac -cp lib\mysql-connector-j-8.4.0\mysql-connector-j-8.4.0.jar -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp "out;src;lib\mysql-connector-j-8.4.0\mysql-connector-j-8.4.0.jar" worldcountriesdatabase.WorldCountriesGUI
```

`src` is on the runtime classpath so the app can load the images in `src/CountryImages`, `src/FlagImages` and `src/OtherImages`.

## Project structure

```
src/
├── worldcountriesdatabase/   GUI: main window, add/edit dialog, about, splash screen
├── DatabaseUtilities/        MySQL connection, table setup and seeding, queries
├── Utilities/                Sorting algorithms, validation, printing
├── Data/                     CSV files used to fill the database on first run
├── CountryImages/            One photo per country
├── FlagImages/               Country flags
└── OtherImages/              Splash screen and other UI images
lib/                          MySQL JDBC driver
db.properties.example         Template for your local credentials
```

## Author

Kellen Jones