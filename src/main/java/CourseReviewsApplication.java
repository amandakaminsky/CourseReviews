import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;

public class CourseReviewsApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) throws IOException, SQLException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
            DatabaseDriver.createTables();
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Course Reviews Application");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading FXML");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Unexpected error");
        }
    }
}