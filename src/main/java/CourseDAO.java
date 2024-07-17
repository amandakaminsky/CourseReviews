import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {
    private static final String DATABASE_URL = "jdbc:sqlite:course_reviews.db";

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DATABASE_URL)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Courses");
            while (resultSet.next()) {
                Course course = new Course(
                        resultSet.getInt("id"),
                        resultSet.getString("subject"),
                        resultSet.getInt("number"),
                        resultSet.getString("title"),
                        resultSet.getDouble("averageRating"),
                        resultSet.getInt("numRatings"),
                        resultSet.getInt("sumRatings")
                );
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }


    public List<Course> searchCourses(String subject, int number, String title) {
        if (subject == "" && number == 0 && title == "") {
            return null;
        }

        List<Course> courses = new ArrayList<>();
        String baseQuery = "SELECT * FROM Courses WHERE";
        List<String> conditions = new ArrayList<>();

        //Set the conditions we will search for
        //All case insensitive, subject and number must match exactly, title just checks substrings
        if (subject != null && !subject.isEmpty()) {
            conditions.add("LOWER(subject) = ?");
        }
        if (number > 0) {
            conditions.add("number = ?");
        }
        if (title != null && !title.isEmpty()) {
            conditions.add("LOWER(title) LIKE ?");
        }

        String conditionString = String.join(" AND ", conditions);
        String query = baseQuery + " " + conditionString;

        try (Connection connection = DriverManager.getConnection(DATABASE_URL)) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            int parameterIndex = 1;

            if (subject != null && !subject.isEmpty()) {
                preparedStatement.setString(parameterIndex++, subject.toLowerCase());
            }
            if (number > 0) {
                preparedStatement.setInt(parameterIndex++, number);
            }
            if (title != null && !title.isEmpty()) {
                preparedStatement.setString(parameterIndex, "%" + title.toLowerCase() + "%");
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Course course = new Course(
                        resultSet.getInt("id"),
                        resultSet.getString("subject"),
                        resultSet.getInt("number"),
                        resultSet.getString("title"),
                        resultSet.getDouble("averageRating"),
                        resultSet.getInt("numRatings"),
                        resultSet.getInt("sumRatings")
                );
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }


    public void addCourse(String subject, int number, String title) {
        String query = "INSERT INTO Courses (subject, number, title, averageRating, numRatings, sumRatings) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL)) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, subject);
            preparedStatement.setInt(2, number);
            preparedStatement.setString(3, title);
            preparedStatement.setDouble(4, 0.0);
            preparedStatement.setDouble(5, 0);
            preparedStatement.setDouble(6, 0);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double getCurrentAverageRating(Course course) {
        double rating;
        try (Connection connection = DriverManager.getConnection(DATABASE_URL)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Courses");

            while (resultSet.next()) {
                double averageRating = resultSet.getDouble("averageRating");
                int currentId = resultSet.getInt("id");
                if (currentId == course.getCourseId()) {
                    return averageRating;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void updateAverageRating(Course course) {
        double newAverageRating = 0;
        try (Connection connection = DriverManager.getConnection(DATABASE_URL)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Courses");

            while (resultSet.next()) {
                int currentId = resultSet.getInt("id");
                if (course.getCourseId() == currentId) {
                    int numRatings = resultSet.getInt("numRatings");
                    int sumRatings = resultSet.getInt("sumRatings");
                    if (numRatings == 0) {
                        newAverageRating = 0;
                    }
                    else {
                        newAverageRating = (double) sumRatings / numRatings;
                    }

                    //Update table
                    String updateQuery = "UPDATE Courses SET averageRating = ? WHERE id = ?";
                    try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                        updateStatement.setDouble(1, newAverageRating);
                        updateStatement.setInt(2, currentId);
                        updateStatement.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateSumRatings(Course course, int rating, boolean bool) {
        int currentSumRating = 0;
        try (Connection connection = DriverManager.getConnection(DATABASE_URL)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Courses");

            while (resultSet.next()) {
                int currentId = resultSet.getInt("id");
                if (course.getCourseId() == currentId) {
                    currentSumRating = resultSet.getInt("sumRatings");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int newCurrentSumRating = 0;

        if (bool){
            newCurrentSumRating = currentSumRating + rating;
        } else{
            newCurrentSumRating = currentSumRating - rating;
        }


        String updateQuery = "UPDATE Courses SET sumRatings = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL)) {
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);

            updateStatement.setInt(1, newCurrentSumRating);
            updateStatement.setInt(2, course.getCourseId());
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateNumRatings(Course course, Boolean subOrAdd) {
        int currentNumRatings = 0;
        try (Connection connection = DriverManager.getConnection(DATABASE_URL)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Courses");

            while (resultSet.next()) {
                int currentId = resultSet.getInt("id");
                if (course.getCourseId() == currentId)
                {
                    currentNumRatings = resultSet.getInt("numRatings");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int newCurrentNumRatings=0;
        if (subOrAdd == true) {
            newCurrentNumRatings = currentNumRatings + 1;
        } else{
            newCurrentNumRatings = currentNumRatings - 1;
        }

        String updateQuery = "UPDATE Courses SET numRatings = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL)) {
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);

            updateStatement.setInt(1, newCurrentNumRatings);
            updateStatement.setInt(2, course.getCourseId());
            updateStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public boolean checkCourses(String subject, int number, String title) {
        String query = "SELECT * FROM Courses WHERE subject = ? AND number = ? AND UPPER(title) = UPPER(?)";
        boolean courseExists = false;

        try (Connection connection = DriverManager.getConnection(DATABASE_URL)) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, subject.toUpperCase());
            preparedStatement.setInt(2, number);
            preparedStatement.setString(3, title.toUpperCase());

            ResultSet resultSet = preparedStatement.executeQuery();
            courseExists = resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courseExists;
    }

}