import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button closeButton;

    @FXML
    private Button createAccountButton;

    private final UserDAO userDAO = new UserDAO();



    @FXML
    private void loginButtonClicked(javafx.event.ActionEvent actionEvent) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (userDAO.realUser(username, password)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("course-search.fxml"));
                Scene myReviewsScene = new Scene(loader.load());

                CourseSearchController pass = loader.getController();
                pass.passUser(username);

                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                stage.setScene(myReviewsScene);
                stage.show();


            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error going to Course Search Screen");
            }
        } else {
            showAlert("Login Failed", "Invalid username or password.");
        }
    }


    @FXML
    private void createAccountButtonClicked() {
        TextInputDialog usernameDialog = new TextInputDialog();
        usernameDialog.setTitle("Create Account");
        usernameDialog.setHeaderText("Enter your username:");
        usernameDialog.setContentText("Username:");

        Optional<String> usernameResult = usernameDialog.showAndWait();
        if (!usernameResult.isPresent()) {
            //User clicked cancel in the username dialog
            return;
        }
        if (usernameResult.isPresent()) {
            String username = usernameResult.get();

            if (userDAO.doesUserExist(username)) {
                showRetryDialog("Account Creation Failed", "Username already exists.", true, username);
                return;
            }
            if (username.equals("")) {
                showRetryDialog("Account Creation Failed", "Please enter a username.", true, username);
                return;
            }

            TextInputDialog passwordDialog = new TextInputDialog();
            passwordDialog.setTitle("Create Account");
            passwordDialog.setHeaderText("Enter your password:");
            passwordDialog.setContentText("Password:");

            Optional<String> passwordResult = passwordDialog.showAndWait();
            if (!passwordResult.isPresent()) {
                // User clicked cancel in the password dialog
                return;
            }
            if (passwordResult.isPresent()) {
                String password = passwordResult.get();

                if (password.length() <= 7) {
                    showRetryDialog("Account Creation Failed", "Password must be at least 8 characters.", false, username);
                } else {
                    userDAO.addUser(username, password);
                    showSuccessfulAlert("Account Created", "Your account has been created successfully!");
                }
            } else {
                showRetryDialog("Account Creation Failed", "Password cannot be empty.", false, username);
            }
        } else {
            showRetryDialog("Account Creation Failed", "Username cannot be empty.", true, "");
        }
    }

    private void createPassword(String username) {
        if (username != "") {
            TextInputDialog passwordDialog = new TextInputDialog();
            passwordDialog.setTitle("Create Account");
            passwordDialog.setHeaderText("Enter your password:");
            passwordDialog.setContentText("Password:");

            Optional<String> passwordResult = passwordDialog.showAndWait();
            if (!passwordResult.isPresent()) {
                // User clicked cancel in the password dialog
                return;
            }
            if (passwordResult.isPresent()) {
                String password = passwordResult.get();

                if (password.length() <= 7) {
                    showRetryDialog("Account Creation Failed", "Password must be at least 8 characters.", false, username);
                } else {
                    userDAO.addUser(username, password);
                    showSuccessfulAlert("Account Created", "Your account has been created successfully!");
                }
            } else {
                showRetryDialog("Account Creation Failed", "Password cannot be empty.", false, username);
            }
        }
    }

    private void showRetryDialog(String title, String content, boolean usernameError, String username) {
        Alert retryAlert = new Alert(Alert.AlertType.ERROR);
        retryAlert.setTitle(title);
        retryAlert.setContentText(content);

        ButtonType retryButton = new ButtonType("Retry");
        ButtonType cancelButton = new ButtonType("Cancel");
        retryAlert.getButtonTypes().setAll(retryButton, cancelButton);

        Optional<ButtonType> result = retryAlert.showAndWait();

        if (result.isPresent() && result.get() == retryButton) {
            if (usernameError) {
                createAccountButtonClicked();
            }
            else {
                createPassword(username);
            }
        }
    }



    @FXML
    private void closeLoginWindow() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showSuccessfulAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}