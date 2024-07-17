import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {
    private static final String DATABASE_URL = "jdbc:sqlite:course_reviews.db";

    public List<Review> getAllReviews() {
        List<Review> reviews = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Reviews")) {

            while (resultSet.next()) {

                int rating = resultSet.getInt("rating");
                Timestamp timestamp = resultSet.getTimestamp("timestamp");
                String comment = resultSet.getString("comment");

                int user_id = resultSet.getInt("user_id");
                int course_id = resultSet.getInt("course_id");

                User user = null;
                Course course = null;

                //find course based on id in databaseDriver
                CourseDAO courseDAO = new CourseDAO();
                List<Course> allCourses = courseDAO.getAllCourses();

                for (int i = 0; i<allCourses.size(); i++){
                    if(allCourses.get(i).getCourseId() == course_id){
                        course = allCourses.get(i);
                    }
                }

                //find user based on id in databaseDriver
                UserDAO userDAO = new UserDAO();
                List<User> allUsers = userDAO.getAllUsers();

                for (int i = 0; i<allUsers.size(); i++){
                    if(allUsers.get(i).getId() == user_id){
                        user = allUsers.get(i);
                    }
                }

                Review review = new Review(rating, timestamp, comment, user, course);
                reviews.add(review);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;

    }

    //add review method with comment
    public static void addReview(int rating, Timestamp timestamp, User user, Course course, String comment) {
        int user_id = user.getId();
        int course_id = course.getCourseId();

        String query = "INSERT INTO Reviews (user_id, course_id, rating, comment, timestamp) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, user_id);
            preparedStatement.setInt(2, course_id);
            preparedStatement.setInt(3, rating);
            preparedStatement.setString(4, comment);
            preparedStatement.setTimestamp(5, timestamp);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //add review method without comment
    public void addReviewNoComm(int rating, Timestamp timestamp, User user, Course course) {
        int user_id = user.getId();
        int course_id = course.getCourseId();

        String query = "INSERT INTO Reviews (rating, timestamp, user_id, course_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, user_id);
            preparedStatement.setInt(2, course_id);
            preparedStatement.setInt(3, rating);
            preparedStatement.setTimestamp(5, timestamp);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //delete review method
    public void deleteReview(User user, Course course){
        int user_id = user.getId();
        int course_id = course.getCourseId();

        String query = "DELETE FROM Reviews WHERE user_id = ? AND course_id = ?";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, user_id);
            preparedStatement.setInt(2, course_id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Review findReview(User user, Course course){
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Reviews")) {

            while (resultSet.next()) {

                int rating = resultSet.getInt("rating");
                Timestamp timestamp = resultSet.getTimestamp("timestamp");
                String comment = resultSet.getString("comment");

                int user_id = resultSet.getInt("user_id");
                int course_id = resultSet.getInt("course_id");

                if (course.getCourseId() == course_id && user.getId() == user_id) {
                    return new Review(rating, timestamp, comment, user, course);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}
