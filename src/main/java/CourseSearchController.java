import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class CourseSearchController {

    @FXML
    private ListView<Course> courseListView;

    @FXML
    private TextField searchSubjectTextField;

    @FXML
    private TextField searchNumberTextField;

    @FXML
    private TextField searchTitleTextField;

    @FXML
    private TextField addSubjectTextField;

    @FXML
    private TextField addNumberTextField;

    @FXML
    private TextField addTitleTextField;

    private final CourseDAO courseDAO = new CourseDAO();
    private final UserDAO userDAO = new UserDAO();

    private User currUser;


    public void initialize() {
        refreshCourseList();
    }


    @FXML
    private void searchCourses() {
        String subject = searchSubjectTextField.getText().trim();
        Integer number = parseInt(searchNumberTextField);
        String title = searchTitleTextField.getText().trim();

        List<Course> searchResults = courseDAO.searchCourses(subject, number, title);

        if (searchResults != null) {
            courseListView.getItems().clear();
            courseListView.getItems().addAll(searchResults);
            addSubjectTextField.clear();
            addNumberTextField.clear();
            addTitleTextField.clear();
        }
        else {
            List<Course> allCourses = courseDAO.getAllCourses();
            courseListView.getItems().clear();
            courseListView.getItems().addAll(allCourses);
            searchSubjectTextField.clear();
            searchNumberTextField.clear();
            searchTitleTextField.clear();
        }
    }

    @FXML
    private void showCourses() {
        List<Course> allCourses = courseDAO.getAllCourses();
        courseListView.getItems().clear();
        courseListView.getItems().addAll(allCourses);
        searchSubjectTextField.clear();
        searchNumberTextField.clear();
        searchTitleTextField.clear();
    }


    @FXML
    private void addCourse() {
        String subject = addSubjectTextField.getText().trim();
        int number = parseInt(addNumberTextField);
        String title = addTitleTextField.getText().trim();
        if (subject.isEmpty() && number == 0 && title.isEmpty()) {
            showAlert("Invalid Course Input", "Please enter a subject, number, and title.");

        }
        else if (courseDAO.checkCourses(subject, number, title)) {
            showAlert("Invalid Course Input", "Course already exists.");
        }
        else if (!isValidString(subject) || !validateSubjectInput(subject)) {
            showAlert("Invalid Subject Input", "Subject must be two to four letters (no numbers/symbols).");
        }
        else if (!validateNumberInput(number)) {
            showAlert("Invalid Number Input", "Number must be between 1000 and 9999 (no letters/symbols).");
        }
        else if (!validateTitleInput(title)) {
            showAlert("Invalid Title Input", "Title must be between 1 and 50 characters.");
        }
        else {
            courseDAO.addCourse(subject.toUpperCase(), number, title);
            refreshCourseList();
            addSubjectTextField.clear();
            addNumberTextField.clear();
            addTitleTextField.clear();
        }
    }

    @FXML
    private void handleCourseSelection(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Course selectedCourse = courseListView.getSelectionModel().getSelectedItem();

            if (selectedCourse != null) {
                // Navigate to Course Review Screen for the selected course
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("course-reviews.fxml"));
                    Scene myReviewsScene = new Scene(loader.load());
                    CourseReviewsController pass = loader.getController();
                    pass.passCourse(selectedCourse);
                    pass.passUser(currUser);

                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(myReviewsScene);
                    pass.refreshLists();
                    stage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Error going to My Reviews Screen");
                }
            }
        }
    }


    private void refreshCourseList() {
        List<Course> courses = courseDAO.getAllCourses();
        courseListView.getItems().clear();
        courseListView.getItems().addAll(courses);
    }

    private Integer parseInt(TextField textField) {
        try {
            return Integer.parseInt(textField.getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private boolean validateSubjectInput(String subject) {
        return subject.length() >= 2 && subject.length() <= 4;
    }

    private boolean validateNumberInput(int number) {
        return number >= 1000 && number <= 9999;
    }

    private boolean validateTitleInput(String title) {
        return title.length() >= 1 && title.length() <= 50;
    }

    private boolean isValidString(String input) {
        for (char c: input.toCharArray()) {
            if (Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleMyReviews(ActionEvent event) {
        //Go to my reviews screen
        try {
            //Change to name of myreviews fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("reviews.fxml"));
            Scene myReviewsScene = new Scene(loader.load());

            MyReviewsController pass = loader.getController();
            pass.passUser(currUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(myReviewsScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error going to My Reviews Screen");
        }
    }

    @FXML
    private void handleLogOut(ActionEvent event) {
        //Logout of account and go to login screen
        try {
            //Change to name of login fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene loginScene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error going to Login Screen");
        }
    }

    public void passUser(String username) {
        List<User> allUsers = userDAO.getAllUsers();
        for (User user : allUsers) {
            if (user.getUsername().equals(username)) {
                currUser = user;
            }
        }
    }

    public void passUser(User username) {
        currUser = username;
    }

}