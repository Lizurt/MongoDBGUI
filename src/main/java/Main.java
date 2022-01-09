import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        String fxmlFileName = "/fxml/main.fxml";
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(fxmlFileName));
        System.out.println(fxmlLoader.getLocation());
        Parent root = fxmlLoader.load();
        stage.setTitle("MongoDBGUI");
        stage.setScene(new Scene(root));
        stage.setMaximized(true);
        stage.show();
    }
}