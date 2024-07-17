import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

public class MyReviewsController {
    @FXML
    private ListView<Course> reviewedCourseListView;

    @FXML
    private ListView<String> reviewInt;

    @FXML
    private ListView<Integer> courseRating;

    private final ReviewDAO reviewDAO = new ReviewDAO();
    private final UserDAO userDAO = new UserDAO();

    private User currUser;

    public List<Review> getAllReviewsForUser(int loggedInId){
        List<Review> allReviews = reviewDAO.getAllReviews();
        List<Review> userReviews = new ArrayList<>();


        for(Review review: allReviews){
            int userId = review.getUser().getId();
            if(userId == loggedInId){
                userReviews.add(review);
            }
        }

        return userReviews;
    }

    @FXML
    public void passUser(User user) {
        currUser = user;
        int currUserId = user.getId();
        List<Review> userReviews = getAllReviewsForUser(currUserId);
        List<String> ratingList = new ArrayList<>();
        List<Course> courseList = new ArrayList<>();

        for(Review review: userReviews){
            String subject = review.getCourse().getSubject();
            int number = review.getCourse().getNumber();

            int ratingInt = review.getRating();
            String rating = "Rating: " + ratingInt;
            ratingList.add(rating);

            Course course = review.getCourse();
            courseList.add(course);
        }

        reviewedCourseListView.getItems().setAll(courseList);
        reviewInt.getItems().setAll(ratingList);
    }


    @FXML
    private void handleCourseSelection(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Course selectedCourse = reviewedCourseListView.getSelectionModel().getSelectedItem();

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

    @FXML
    private void handleBack(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("course-search.fxml"));
            Scene myReviewsScene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            CourseSearchController pass = loader.getController();
            pass.passUser(currUser);

            stage.setScene(myReviewsScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error going to course search screen");
        }
    }

}