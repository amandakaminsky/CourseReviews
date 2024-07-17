import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Integer.parseInt;

public class CourseReviewsController {

    @FXML
    private Label courseTitle;

    @FXML
    private Label averageCourseRating;

    private User user;

    private Course currentCourse;

    private final UserDAO userDAO = new UserDAO();

    private  final ReviewDAO reviewDAO = new ReviewDAO();
    private  final CourseDAO courseDAO = new CourseDAO();

    @FXML
    private ListView<Integer> ratingsList;

    @FXML
    private ListView<String> commentsList;

    @FXML
    private ListView<String> timestampList;

    @FXML
    private ChoiceBox<String> ratingChoiceBox;

    private ArrayList<String> ratingOptions = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5"));

    @FXML
    private TextField reviewCommentTextField;

    @FXML
    private Button cancelReviewButton;

    @FXML
    private Button postReviewButton;

    @FXML
    private Button deleteReviewButton;

    @FXML
    private Button editReviewButton;

    @FXML
    private Button addReviewButton;

    private Review originalReview;


    public void refreshLists(){
        List<Review> allReviews = reviewDAO.getAllReviews();

        ArrayList<Integer> ratings = new ArrayList<>();
        ArrayList<String> comments = new ArrayList<>();
        ArrayList<String> timestamps = new ArrayList<>();

        for(Review review: allReviews) {
            if(review.getCourse().equals(currentCourse)) {
                ratings.add(review.getRating());
                comments.add(review.getComment());
                SimpleDateFormat form = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                form.setTimeZone(TimeZone.getTimeZone("America/New_York"));
                String date = form.format(review.getTimestamp());
                timestamps.add(date);
            }
        }

        ratingsList.getItems().clear();
        commentsList.getItems().clear();
        timestampList.getItems().clear();

        ratingsList.getItems().setAll(ratings);
        commentsList.getItems().setAll(comments);
        timestampList.getItems().setAll(timestamps);

        addReviewButton.setVisible(true);

        if (userDAO.hasWrittenReview(currentCourse, user)){
            deleteReviewButton.setVisible(true);
            editReviewButton.setVisible(true);
            addReviewButton.setVisible(false);
        }
        else if (originalReview != null) {
            addReviewButton.setVisible(false);
        }
    }


    @FXML
    private void handleBackButton(ActionEvent event) {
        //Go to course search screen
        if (ratingChoiceBox.isVisible() && (!ratingChoiceBox.getValue().equals("Rating") || !reviewCommentTextField.getText().isEmpty())) {
            showUnfinishedReviewAlert(event);
        }
        else {
            try {
                //Change to name of myreviews fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("course-search.fxml"));
                Scene mySearchScene = new Scene(loader.load());
                CourseSearchController pass = loader.getController();
                pass.passUser(user);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(mySearchScene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error going to Course Search Screen");
            }
        }
    }

    private void showUnfinishedReviewAlert(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Unfinished Review");
        alert.setHeaderText("Your review is unfinished.");
        alert.setContentText("Your edits will be deleted if you return to the last page.");

        ButtonType continueEditingButton = new ButtonType("Continue Editing");
        ButtonType goBackButton = new ButtonType("Go Back");
        alert.getButtonTypes().setAll(continueEditingButton, goBackButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == continueEditingButton) {
            //Nothing happens (stay on the current page)
            ;
        } else {
            if (originalReview != null) {
                resubmitOldReview(event);
            }
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("course-search.fxml"));
                Scene mySearchScene = new Scene(loader.load());
                CourseSearchController pass = loader.getController();
                pass.passUser(user);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(mySearchScene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error going to Course Search Screen");
            }
        }
    }

    @FXML
    private void handleAddReviewButton(ActionEvent event) {
        if (userDAO.hasWrittenReview(currentCourse, user)) {
            showAlert("Error Adding Review", "User has written review for course already.");
        }
        else {
            ratingChoiceBox.setVisible(true);
            reviewCommentTextField.setVisible(true);
            cancelReviewButton.setVisible(true);
            postReviewButton.setVisible(true);
            addReviewButton.setVisible(false);
        }
    }


    @FXML
    private void handleDeleteReviewButton(ActionEvent event) {
        //Get current users' review
        Review currentReview = reviewDAO.findReview(user,currentCourse);
        if (currentReview == null) {
            showAlert("Error deleting", "User doesn't have a review posted.");
        }
        else {
            int currentRating = currentReview.getRating();

            //Update average rating
            courseDAO.updateNumRatings(currentCourse, false);
            courseDAO.updateSumRatings(currentCourse, currentRating, false);
            courseDAO.updateAverageRating(currentCourse);
            currentCourse.setAverageRating(courseDAO.getCurrentAverageRating(currentCourse));

            double doubleRating = currentCourse.getAverageRating();
            String formatted = String.format("%.2f", doubleRating);
            averageCourseRating.setText("Average Rating: " + formatted);

            reviewCommentTextField.clear();
            ratingChoiceBox.setValue("Rating");

            reviewDAO.deleteReview(user, currentCourse);
            refreshLists();

            deleteReviewButton.setVisible(false);
            editReviewButton.setVisible(false);
            addReviewButton.setVisible(true);
        }
    }

    //write this method
    @FXML
    private void handleEditReviewButton(ActionEvent event) {
        //Set og review to user's review so we can resubmit it if necessary
        originalReview = reviewDAO.findReview(user, currentCourse);

        //Don't show edit and delete buttons while editing
        deleteReviewButton.setVisible(false);
        editReviewButton.setVisible(false);
        addReviewButton.setVisible(false);

        //Make sure add review is displayed
        ratingChoiceBox.setVisible(true);
        reviewCommentTextField.setVisible(true);
        cancelReviewButton.setVisible(true);
        postReviewButton.setVisible(true);

        //Get current users' review
        Review currentReview = reviewDAO.findReview(user, currentCourse);
        int currentRating = currentReview.getRating();
        ratingChoiceBox.setValue("" + currentRating);
        reviewCommentTextField.setText(currentReview.getComment());

        //Delete review
        currentCourse.getAverageRating();
        courseDAO.updateNumRatings(currentCourse, false);
        courseDAO.updateSumRatings(currentCourse, currentRating, false);
        courseDAO.updateAverageRating(currentCourse);
        currentCourse.setAverageRating(courseDAO.getCurrentAverageRating(currentCourse));

        double doubleRating = currentCourse.getAverageRating();
        String formatted = String.format("%.2f", doubleRating);
        averageCourseRating.setText("Average Rating: " + formatted);

        reviewDAO.deleteReview(user, currentCourse);
        refreshLists();
    }

    @FXML
    private void handleCancelReviewButton(ActionEvent event) {
        if (originalReview != null) {
            resubmitOldReview(event);
            originalReview = null;
        }
        else {
            addReviewButton.setVisible(true);
        }
        ratingChoiceBox.setVisible(false);
        reviewCommentTextField.setVisible(false);
        cancelReviewButton.setVisible(false);
        postReviewButton.setVisible(false);
        ratingChoiceBox.setValue("Rating");
        reviewCommentTextField.setText("");
    }

    @FXML
    private void resubmitOldReview(ActionEvent event) {
        if (user == null) {
            showAlert("Error Posting Review", "Can't find current user. Please logout and log back in.");
        }
        else if (currentCourse == null) {
            showAlert("Error Posting Review", "Can't find current course. Please select a different course.");
        }
        else if (userDAO.hasWrittenReview(currentCourse, user)) {
            showAlert("Error Posting Review", "User has written review for course already.");
            reviewCommentTextField.clear();
            ratingChoiceBox.setValue("Rating");
        }
        else {
            String ratingText = ratingChoiceBox.getValue();
            if (!ratingText.equals("Rating")) {
                int rating = parseInt(ratingText);
                String comment = reviewCommentTextField.getText();
                Timestamp timestamp = originalReview.getTimestamp();
                ReviewDAO.addReview(rating, timestamp, user, currentCourse, comment);
                refreshLists();

                courseDAO.updateNumRatings(currentCourse, true);
                courseDAO.updateSumRatings(currentCourse, rating, true);
                courseDAO.updateAverageRating(currentCourse);
                currentCourse.setAverageRating(courseDAO.getCurrentAverageRating(currentCourse));

                double doubleRating = currentCourse.getAverageRating();
                String formatted = String.format("%.2f", doubleRating);
                averageCourseRating.setText("Average Rating: " + formatted);

                reviewCommentTextField.clear();
                ratingChoiceBox.setValue("Rating");

                deleteReviewButton.setVisible(true);
                editReviewButton.setVisible(true);
                addReviewButton.setVisible(false);

                ratingChoiceBox.setVisible(false);
                reviewCommentTextField.setVisible(false);
                cancelReviewButton.setVisible(false);
                postReviewButton.setVisible(false);
            }
            else {
                showAlert("Error Posting Review", "Please select a rating.");
            }
        }
    }

    public void passCourse(Course selectedCourse) {
        currentCourse = selectedCourse;
        String courseName = selectedCourse.getSubject() + " " + selectedCourse.getNumber() + ": " + selectedCourse.getTitle();
        double doubleRating = currentCourse.getAverageRating();
        String formatted = String.format("%.2f", doubleRating);
        courseTitle.setText(courseName);
        averageCourseRating.setText("Average Rating: " + formatted);
        ratingChoiceBox.getItems().addAll(ratingOptions);
        ratingChoiceBox.setVisible(false);
        reviewCommentTextField.setVisible(false);
        cancelReviewButton.setVisible(false);
        postReviewButton.setVisible(false);

        boolean isVisible = false;

        deleteReviewButton.setVisible(isVisible);
        editReviewButton.setVisible(isVisible);

    }

    @FXML
    private void submitReview(ActionEvent event) {
        if (user == null) {
            showAlert("Error Posting Review", "Can't find current user. Please logout and log back in.");
        }
        else if (currentCourse == null) {
            showAlert("Error Posting Review", "Can't find current course. Please select a different course.");
        }
        else if (userDAO.hasWrittenReview(currentCourse, user)) {
            showAlert("Error Posting Review", "User has written review for course already.");
            reviewCommentTextField.clear();
            ratingChoiceBox.setValue("Rating");
        }
        else {
            String ratingText = ratingChoiceBox.getValue();
            if (!ratingText.equals("Rating")) {
                int rating = parseInt(ratingText);
                String comment = reviewCommentTextField.getText();

                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                ReviewDAO.addReview(rating, timestamp, user, currentCourse, comment);
                refreshLists();

                courseDAO.updateNumRatings(currentCourse, true);
                courseDAO.updateSumRatings(currentCourse, rating, true);
                courseDAO.updateAverageRating(currentCourse);
                currentCourse.setAverageRating(courseDAO.getCurrentAverageRating(currentCourse));

                double doubleRating = currentCourse.getAverageRating();
                String formatted = String.format("%.2f", doubleRating);
                averageCourseRating.setText("Average Rating: " + formatted);

                reviewCommentTextField.clear();
                ratingChoiceBox.setValue("Rating");

                deleteReviewButton.setVisible(true);
                editReviewButton.setVisible(true);
                addReviewButton.setVisible(false);

                ratingChoiceBox.setVisible(false);
                reviewCommentTextField.setVisible(false);
                cancelReviewButton.setVisible(false);
                postReviewButton.setVisible(false);
            }
            else {
                showAlert("Error Posting Review", "Please select a rating.");
            }
        }
    }

    @FXML
    public void passUser(User username) {
        user=username;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}