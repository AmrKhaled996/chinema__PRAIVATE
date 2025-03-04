package com.mycompany.movie;
import com.mycompany.movie.Book.Receipt;
import com.mycompany.movie.Hall.Hall;

import com.mycompany.movie.Movies.Movie;
import com.mycompany.movie.Movies.MovieLibrary;
import com.mycompany.movie.Movies.ScreenTime;
import com.mycompany.movie.Movies.enGenre;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {
    
    private static final String URL = "jdbc:mysql://localhost:3306/MovieTickets";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    /*
    
    *add Delete movie.  
    
    *add update hall seats. -done-
    
    *add list receipts. 
    
    */
    
    
    
    
    public static Date parseDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
           
    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
    
    
  public static void addScreenTime(ScreenTime screenTime, int movieID) throws ParseException {
    try (Connection conn = getConnection();
         PreparedStatement checkStmt = conn.prepareStatement(
             "SELECT * FROM screentime WHERE Hall_idHall = ? AND startDate = ? AND endDate = ?")) {

//        2024-11-21 00:00:00
        
       
        checkStmt.setByte(1, (byte) screenTime.getHall().getID());
        // to date from string
            checkStmt.setString(2, formatDate(new java.sql.Date(screenTime.getStartDate().getTime())));
            checkStmt.setString(3, formatDate(new java.sql.Date(screenTime.getEndDate().getTime())));
//        System.out.println(checkStmt.getString(2, new java.sql.Date(screenTime.getStartDate().getTime())));

        ResultSet rs = checkStmt.executeQuery();
        if (rs.next()) { 
            System.out.println("ScreenTime already exists. Skipping insertion.");
            return;
        }

        // Insert if no duplicate
        try (PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO screentime (Hall_idHall, startDate, endDate, Movies_movieID) VALUES (?, ?, ?, ?)")) {

            stmt.setByte(1, (byte) screenTime.getHall().getID());
            stmt.setString(2, formatDate(new java.sql.Date(screenTime.getStartDate().getTime())));
            stmt.setString(3, formatDate(new java.sql.Date(screenTime.getEndDate().getTime())));
            stmt.setInt(4, movieID);

            stmt.executeUpdate();
            System.out.println("ScreenTime added successfully.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}



  

    public static void addMovieWithScreenTimes(Movie movie) throws ParseException {
        String sql = "INSERT INTO movies (movieID,title, genre, duration) VALUES (?,?, ?,?)";
        try (Connection connection = Database.getConnection();
         PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
         
         stmt.setInt(1, movie.getID());
         stmt.setString(2, movie.getTitle());
         stmt.setString(3, movie.getGenre());
         stmt.setFloat(4, movie.getDuration());
       
        int affectedRows = stmt.executeUpdate();
        if (affectedRows > 0) {
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int movieId = generatedKeys.getInt(1);
                    
                    System.out.println("Movie added with ID: " + movieId);
                    MovieLibrary.getMovies();
                    // Add ScreenTimes
                    for (ScreenTime screenTime : movie.getScreenTimes()) {
                        addScreenTime(screenTime, movieId);
                    }
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
        }
    public static List<Movie> listMoviesWithScreenTimes() {
        String movieAndHallSql = "SELECT * FROM movies";
        String screenTimeSql = "SELECT st.startDate, st.endDate, h.idHall, h.numberOfRows, h.numberOfCols " +
                "FROM screentime st " +
                "JOIN hall h ON st.Hall_idHall = h.idHall " +
                "WHERE st.Movies_movieID = ?";
        String seatsSql = "SELECT isAvailable FROM Seats WHERE Hall_idHall = ?";

        List<Movie> movies = new ArrayList<>();

        try (Connection connection = Database.getConnection();
             PreparedStatement movieStmt = connection.prepareStatement(movieAndHallSql);
             ResultSet movieResultSet = movieStmt.executeQuery()) {

            while (movieResultSet.next()) {
                int movieId = movieResultSet.getInt("movieID");
                String title = movieResultSet.getString("title");
                enGenre genre = enGenre.valueOf(movieResultSet.getString("genre").toUpperCase());

                List<ScreenTime> screenTimes = new ArrayList<>();

                // Fetch associated screen times
                try (PreparedStatement screenTimeStmt = connection.prepareStatement(screenTimeSql)) {
                    screenTimeStmt.setInt(1, movieId);

                    try (ResultSet screenTimeResultSet = screenTimeStmt.executeQuery()) {
                        while (screenTimeResultSet.next()) {
                            Date startDate = parseDate(screenTimeResultSet.getString("startDate"));
                            Date endDate = parseDate(screenTimeResultSet.getString("endDate"));

                            int idHall = screenTimeResultSet.getInt("idHall");
                            int rows = screenTimeResultSet.getInt("numberOfRows");
                            int cols = screenTimeResultSet.getInt("numberOfCols");
                            int totalSeats = rows * cols;

                            // Create a 1D boolean array for seat availability
                            boolean[] seatAvailability = new boolean[totalSeats];
                            int index = 0;

                            // Fetch seat availability and populate the 1D array
                            try (PreparedStatement seatsStmt = connection.prepareStatement(seatsSql)) {
                                seatsStmt.setInt(1, idHall);
                                try (ResultSet seatsResultSet = seatsStmt.executeQuery()) {
                                    while (seatsResultSet.next() && index < totalSeats) {
                                        seatAvailability[index++] = seatsResultSet.getBoolean("isAvailable");
                                    }
                                }
                            }

                            // Create Hall object with seatAvailability
                            Hall hall = new Hall((byte) idHall, rows, cols);
                            screenTimes.add(new ScreenTime(hall, startDate, endDate));
                        }
                    }
                }

                // Add movie to the list
                if (!screenTimes.isEmpty()) {
                    movies.add(new Movie(movieId, title, genre, screenTimes));
                } else {
                 
                }
            }
        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
        }

        return movies;
    }


    public static List<ScreenTime> listMovieScreenTimes(Movie movie) { 

                List<ScreenTime> screenTimes = new ArrayList<>();
        String movieandHallSql = "SELECT * FROM movies , hall";

        String screenTimeSql = "SELECT st.startDate, st.endDate, h.idHall, h.numberOfRows, h.numberOfCols " +
                               "FROM screentime st " +
                               ", hall h " +
                               "WHERE st.Movies_movieID = "+ movie.getID() +"  AND st.Hall_idHall = ? ";


        try (Connection connection = Database.getConnection();
             PreparedStatement movieStmt = connection.prepareStatement(movieandHallSql);
             ResultSet movieResultSet = movieStmt.executeQuery()) {

            while (movieResultSet.next()) {
                int movieId = movieResultSet.getInt("movieID");
                int hallId=movieResultSet.getInt("idHall");


                try (PreparedStatement screenTimeStmt = connection.prepareStatement(screenTimeSql)) {
                    screenTimeStmt.setInt(1, movieId);
                    screenTimeStmt.setInt(2, hallId);

                    try (ResultSet screenTimeResultSet = screenTimeStmt.executeQuery()) {
                        while (screenTimeResultSet.next()) { 

                            Date startDate = parseDate(screenTimeResultSet.getString("startDate"));

                            Date endDate = parseDate(screenTimeResultSet.getString("endDate"));

                            Hall hall = new Hall(
                                (byte) screenTimeResultSet.getInt("idHall"),
                                screenTimeResultSet.getInt("numberOfRows"),
                                screenTimeResultSet.getInt("numberOfCols")
                            );

                            screenTimes.add(new ScreenTime(hall, startDate, endDate));

                        }
                    }
                }


                // Ensure screenTimes is not empty before creating the Movie object
                if (screenTimes.isEmpty()) {
                    
                    return null; // Skip this movie
                }



            }
        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
        }

        return screenTimes;
    }

        public static void deleteMovie(int movieId) {
        String deleteSql = "DELETE FROM movies WHERE movieID = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {

            // Step 1: Delete the movie
            deleteStmt.setInt(1, movieId);
            deleteStmt.executeUpdate();

            // Step 2: Get the new max movieID
            String maxIdSql = "SELECT MAX(movieID) AS maxId FROM movies";
            try (Statement stmt = connection.createStatement();
                 ResultSet resultSet = stmt.executeQuery(maxIdSql)) {

                int nextAutoIncrement = 1; // Default if table is empty
                if (resultSet.next()) {
                    int maxId = resultSet.getInt("maxId");
                    if (!resultSet.wasNull()) {
                        nextAutoIncrement = maxId + 1;
                    }
                }

                // Step 3: Reset the AUTO_INCREMENT value
                String resetAutoIncrementSql = "ALTER TABLE movies AUTO_INCREMENT = " + nextAutoIncrement;
                try (Statement resetStmt = connection.createStatement()) {
                    resetStmt.executeUpdate(resetAutoIncrementSql);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    public static Hall getHallById(byte hallId) {
        String sql = "SELECT * FROM hall WHERE idHall = ?";
        Hall hall = null;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, hallId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("idHall");
                    int numberOfRows = resultSet.getInt("numberOfRows");
                    int numberOfCols = resultSet.getInt("numberOfCols");
                    hall = new Hall((byte) id, numberOfRows, numberOfCols);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hall;
    }
    public static List<Hall> getAllHalls() {
        List<Hall> hallList = new ArrayList<>();
        String query = "SELECT idHall, numberOfRows, numberOfCols FROM hall";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                byte id = rs.getByte("idHall");
                int numberOfRows = rs.getInt("numberOfRows");
                int numberOfCols = rs.getInt("numberOfCols");

                Hall hall = new Hall(id, numberOfRows, numberOfCols);
                hallList.add(hall);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hallList;
    }
    
    
    
    
      public static int getSoldSeats(int movieID) {
       
        String query = """
            SELECT 
                COUNT(sr.Seats_idSeats) AS sold_seats
            FROM 
                Movies m
            JOIN 
                ScreenTime st ON m.movieID = st.Movies_movieID
            JOIN 
                receipt r ON st.Hall_idHall = r.ScreenTime_Hall_idHall 
                          AND st.Movies_movieID = r.ScreenTime_Movies_movieID
            JOIN 
                Seats_has_Receipt sr ON r.ReceiptID = sr.Receipt_ReceiptID
            WHERE 
                m.movieID = ?;
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set the parameter for movieID
            stmt.setInt(1, movieID);

            // Execute the query
            ResultSet rs = stmt.executeQuery();

            // Return the result
            if (rs.next()) {
                return rs.getInt("sold_seats");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // If no result or an error occurs, return 0
        return 0;
    }


      
      
    public static void insertSeatsIntoHall(Hall hall, Hall.Seat seats[][]) {
        //String insertSeatSql = "INSERT INTO Seats (idSeats, Hall_idHall, isAvailable, price, classType) VALUES (?, ?, ?, ?, ?)";
        String insertSeatSql = "INSERT INTO Seats (idSeats, Hall_idHall, isAvailable, price, classType) "
                + "VALUES (?, ?, ?, ?, ?)";

        
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSeatSql)) {

            // Loop through rows and columns to generate seat IDs
            for (int row = 0; row < hall.getNumberOfRows(); row++) { // 0-based indexing for rows
                char rowChar = (char) ('A' + row);         // Convert numeric row to character
                for (int col = 0; col < hall.getNumberOfCols(); col++) { // 0-based indexing for cols
                    String seatId = rowChar + String.valueOf(col + 1); // Generate seat ID (e.g., "A1", "B2")

                    // Set parameters for the SQL query
                    preparedStatement.setString(1, seatId);                   // idSeats
                    preparedStatement.setInt(2, hall.getID());                     // Hall_idHall
                    preparedStatement.setBoolean(3, true);                   // isAvailable (default true)
                    preparedStatement.setFloat(4, seats[row][col].getPrice()); // price
                    preparedStatement.setString(5, seats[row][col].getClassType()); // classType
                    //
                    var hallseats= hall.getSeats();
                    seats[row][col] =hallseats[row][col];
                    // Add to batch for efficiency
                    preparedStatement.addBatch();
                }
            }

            // Execute the batch
            preparedStatement.executeBatch();
            System.out.println("Seats successfully inserted for Hall ID: " + hall.getID());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void updateSeatAvailability(String seatId, byte hallId, boolean isAvailable) {
        String updateSql = "UPDATE Seats SET isAvailable = ? WHERE idSeats = ? AND Hall_idHall = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateSql)) {

            preparedStatement.setBoolean(1, isAvailable); // Set availability (true/false)
            preparedStatement.setString(2, seatId);       // Set the seat ID (e.g., "A1")
            preparedStatement.setInt(3, hallId);          // Set the hall ID

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Seat availability updated successfully.");
            } else {
                System.out.println("No matching seat found to update.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
   
     public static void addCustomer(String name , int age ) {
        String sql = "INSERT INTO customer ( name, age) VALUES (?, ?)";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.setInt(2, age);
            
            statement.executeUpdate();
            System.out.println("Customer added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void listCustomers() {
        String sql = "SELECT * FROM customer";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                System.out.println("ID: " + resultSet.getInt("CustomerID") +
                                   ", Name: " + resultSet.getString("name") +
                                   ", Age: " + resultSet.getString("age"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
     public static void addReceipt(int movieId, int customerId, float totalPrice, int hallId, List<String> seatIds) {
    String receiptSql = "INSERT INTO receipt (ScreenTime_Movies_movieID, Customer_CustomerID, totalPrice, ScreenTime_Hall_idHall) VALUES (?, ?, ?, ?)";
    String seatSql = "INSERT INTO Seats_has_Receipt (Receipt_ReceiptID, Seats_idSeats) VALUES (?, ?)";

    try (Connection connection = Database.getConnection()) {
        connection.setAutoCommit(false); // Begin transaction

        int receiptId = -1; // Declare receiptId outside the try block

        // Insert into receipts
        try (PreparedStatement receiptStmt = connection.prepareStatement(receiptSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            receiptStmt.setInt(1, movieId);
            receiptStmt.setInt(2, customerId);
            receiptStmt.setFloat(3, totalPrice);
            receiptStmt.setInt(4, hallId);
//            receiptStmt.setInt(5, screenTimeId);
            receiptStmt.executeUpdate();

            ResultSet keys = receiptStmt.getGeneratedKeys();
            if (keys.next()) {
                receiptId = keys.getInt(1);
            } else {
                throw new SQLException("Receipt insertion failed, no ID obtained.");
            }
        }

        // Insert into receipt_seats
        try (PreparedStatement seatStmt = connection.prepareStatement(seatSql)) {
            for (String seatId : seatIds) {
                seatStmt.setInt(1, receiptId);
                seatStmt.setString(2, seatId);
                seatStmt.addBatch();
            }
            seatStmt.executeBatch();
        }

        connection.commit(); // Commit transaction
        System.out.println("Receipt added successfully with ID: " + receiptId);
    } catch (SQLException e) {
        e.printStackTrace();
    }
}public static List<Receipt> getReceiptsForCustomer(int customerId) {
        String sql = "SELECT r.ReceiptID, r.totalPrice, r.ScreenTime_Hall_idHall, r.ScreenTime_Movies_movieID, r.Customer_CustomerID, " +
                     "s.startDate, s.endDate, m.title, c.name AS customerName, h.idHall, h.numberOfRows, h.numberOfCols " +
                     "FROM receipt r " +
                     "JOIN screentime s ON r.ScreenTime_Hall_idHall = s.Hall_idHall AND r.ScreenTime_Movies_movieID = s.Movies_movieID " +
                     "JOIN movies m ON r.ScreenTime_Movies_movieID = m.movieID " +
                     "JOIN customer c ON r.Customer_CustomerID = c.CustomerID " +
                     "JOIN hall h ON s.Hall_idHall = h.idHall " +
                     "WHERE r.Customer_CustomerID = ?";
        List<Receipt> receipts = new ArrayList<>();

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, customerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int receiptId = resultSet.getInt("ReceiptID");
                    float totalPrice = resultSet.getFloat("totalPrice");
                    int hallId = resultSet.getInt("ScreenTime_Hall_idHall");
                    int movieId = resultSet.getInt("ScreenTime_Movies_movieID");
                    String startDateStr = resultSet.getString("startDate");
                    String endDateStr = resultSet.getString("endDate");
                    String movieName = resultSet.getString("title");
                    String customerName = resultSet.getString("customerName");

                    // Fetching seat IDs for this receipt
                    List<String> seatIDs = getSeatIDsForReceipt(receiptId);

                    // Creating the Hall object
                    Hall hall = new Hall((byte)hallId, resultSet.getInt("numberOfRows"), resultSet.getInt("numberOfCols"));

                    // Converting string dates to java.sql.Date
                    Date startDate = parseDate(startDateStr);
                    Date endDate = parseDate(endDateStr);


                    // Creating the ScreenTime object
                    ScreenTime screenTime = new ScreenTime(hall, startDate, endDate);

                    // Creating and adding the Receipt object to the list
                    receipts.add(new Receipt(receiptId,customerName, movieName, totalPrice, hallId, seatIDs, screenTime));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return receipts;
    }

    // Method to list all receipts
   
public static List<Receipt> getAllReceipts() {
    String sql = "SELECT r.ReceiptID, r.totalPrice, r.ScreenTime_Hall_idHall, r.ScreenTime_Movies_movieID, r.Customer_CustomerID, " +
                 "s.startDate, s.endDate, m.title, c.name AS customerName, h.idHall, h.numberOfRows, h.numberOfCols " +
                 "FROM receipt r " +
                 "JOIN screentime s ON r.ScreenTime_Hall_idHall = s.Hall_idHall AND r.ScreenTime_Movies_movieID = s.Movies_movieID " +
                 "JOIN movies m ON r.ScreenTime_Movies_movieID = m.movieID " +
                 "JOIN customer c ON r.Customer_CustomerID = c.CustomerID " +
                 "JOIN hall h ON s.Hall_idHall = h.idHall";
    
    List<Receipt> receipts = new ArrayList<>();
    
    try (Connection connection = Database.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql);
         ResultSet resultSet = statement.executeQuery()) {

        // Iterating over the result set
        while (resultSet.next()) {
            // Fetching individual fields from the result set
            int receiptId = resultSet.getInt("ReceiptID");
            float totalPrice = resultSet.getFloat("totalPrice");
            int hallId = resultSet.getInt("ScreenTime_Hall_idHall");
            int movieId = resultSet.getInt("ScreenTime_Movies_movieID");
            int customerId = resultSet.getInt("Customer_CustomerID");
            String startDateStr = resultSet.getString("startDate");
            String endDateStr = resultSet.getString("endDate");
            String movieName = resultSet.getString("title");
            String customerName = resultSet.getString("customerName");

            // Fetching seat IDs for this receipt
            List<String> seatIDs = getSeatIDsForReceipt(receiptId);

            // Creating the Hall object
            Hall hall = new Hall((byte) hallId, resultSet.getInt("numberOfRows"), resultSet.getInt("numberOfCols"));

            // Converting string dates to java.sql.Date (handle null values or empty strings)
            Date startDate = parseDate(startDateStr);
            Date endDate = parseDate(endDateStr);

            // Debugging to verify the date parsing
           

            // Creating the ScreenTime object
            ScreenTime screenTime = new ScreenTime(hall, startDate, endDate);

            // Creating and adding the Receipt object to the list
            Receipt receipt = new Receipt(receiptId,customerName, movieName, totalPrice, hallId, seatIDs, screenTime);
            receipts.add(receipt);
        }

    } catch (SQLException e) {
        // Log SQL error with message
        System.err.println("SQL error occurred while fetching receipts: " + e.getMessage());
        e.printStackTrace();
    } catch (Exception e) {
        // Handle any other errors (like null pointer or conversion issues)
        System.err.println("Error occurred while processing the receipt data: " + e.getMessage());
        e.printStackTrace();
    }

    // Return the list of receipts (could be empty if no data was found)
    return receipts;
}

    private static List<String> getSeatIDsForReceipt(int receiptId) {
        List<String> seatIDs = new ArrayList<>();
        String sql = "SELECT Seats_idSeats FROM Seats_has_Receipt WHERE Receipt_ReceiptID = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, receiptId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    seatIDs.add(resultSet.getString("Seats_idSeats"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seatIDs;
    }
    

    public static int getSoldSeatsByDateRange(String startDate, String endDate) {
       
        String query = """
            SELECT 
                COUNT(sr.Seats_idSeats) AS sold_seats
            FROM 
                ScreenTime st
            JOIN 
                receipt r ON st.Hall_idHall = r.ScreenTime_Hall_idHall 
                          AND st.Movies_movieID = r.ScreenTime_Movies_movieID
            JOIN 
                Seats_has_Receipt sr ON r.ReceiptID = sr.Receipt_ReceiptID
            WHERE 
                st.startDate BETWEEN ? AND ?;
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set the parameters for the start and end dates
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);

            // Execute the query
            ResultSet rs = stmt.executeQuery();

            // Return the result
            if (rs.next()) {
                return rs.getInt("sold_seats");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // If no result or an error occurs, return 0
        return 0;
    }
}