package lawson.lonchi.crossword;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lawson.lonchi.crossword.controller.CrosswordController;
import lawson.lonchi.crossword.controller.MainMenuController;
import lawson.lonchi.crossword.model.Crossword;

public class App extends Application {


    private static Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/lawson/lonchi/crossword/view/main_menu.fxml"));
        Parent root = loader.load();
        MainMenuController controller = loader.getController();
        controller.setPrimaryStage(primaryStage);

        primaryStage.setScene(new Scene(root, 640, 640));
        primaryStage.setTitle("Mots croisés");
        primaryStage.show();
    }

    public static void loadCrosswordView(Stage primaryStage, Crossword crossword) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/lawson/lonchi/crossword/view/crossword_view.fxml"));
        Parent root = loader.load();
        CrosswordController controller = loader.getController();
        controller.setCrossword(crossword);

        primaryStage.setScene(new Scene(root, 800, 800));
        primaryStage.setTitle("Grille de mots croisés");
        primaryStage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}