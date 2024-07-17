
import java.sql.*;

public class DatabaseDriver {
    private static final String DATABASE_URL = "jdbc:sqlite:course_reviews.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }

    public static void createTables() {
        try (Connection connection = connect()) {
            Statement statement = connection.createStatement();

            //Users table
            statement.execute("CREATE TABLE IF NOT EXISTS Users ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "username TEXT UNIQUE, "
                    + "password TEXT)");

            //Courses table
            statement.execute("CREATE TABLE IF NOT EXISTS Courses ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "subject TEXT, "
                    + "number INTEGER, "
                    + "title TEXT, "
                    + "averageRating DOUBLE,"
                    + "numRatings INTEGER, "
                    + "sumRatings INTEGER, "
                    + "UNIQUE(subject, number, title))");

            //Reviews table
            statement.execute("CREATE TABLE IF NOT EXISTS Reviews ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "user_id INTEGER, "
                    + "course_id INTEGER, "
                    + "rating INTEGER, "
                    + "comment TEXT, "
                    + "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + "FOREIGN KEY(user_id) REFERENCES Users(id), "
                    + "FOREIGN KEY(course_id) REFERENCES Courses(id))");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}