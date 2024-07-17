import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class  UserDAO {
    private static final String DATABASE_URL = "jdbc:sqlite:course_reviews.db";

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Users")) {

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                int id = resultSet.getInt("id");

                User user = new User(username, password, id);
                users.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    //Method 2: Does username already exist in database? Used for new user.
    public boolean doesUserExist(int userId){
        List<User> allUsers = getAllUsers();
        for(User user: allUsers){
            if(user.getId() == userId){
                return true;
            }
        }
        return false;
    }

    public boolean doesUserExist(String username){
        List<User> allUsers = getAllUsers();
        for(User user: allUsers){
            if(user.getUsername().equals(username)){
                return true;
            }
        }
        return false;
    }

    //Method 3: Does user with that username and password (both) exist?
    public boolean realUser(String username, String password) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Users WHERE username = ? AND password = ?")) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //Method 4: add user if they don't already exist
    public void addUser(String username, String password) {
        String query = "INSERT INTO Users (username, password) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL)) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                //User was Added
                //System.out.println("User added");
            } else {
                //User was not added
                System.out.println("Failed to add user");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Method 5: has user written a review for specific class?
    public boolean hasWrittenReview(Course course, User user){
        ReviewDAO reviewDAO = new ReviewDAO();
        List<Review> allReviews = reviewDAO.getAllReviews();

        for(Review review: allReviews){
            if((review.getCourse().getCourseId() == course.getCourseId()) && (review.getUser().getId() == user.getId())){
                return true;
            }
        }
        return false;
    }


}