package fecoder;

import fecoder.login.LoginManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Scene scene = new Scene(new StackPane());
        scene.getStylesheets().add("style.css");

        primaryStage.getIcons().add(new Image("/images/icon.png"));

        LoginManager loginManager = new LoginManager(scene);
        loginManager.showLoginScreen();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Xác thực");
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
