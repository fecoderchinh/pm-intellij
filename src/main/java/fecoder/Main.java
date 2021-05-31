package fecoder;

import fecoder.login.LoginManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Scene scene = new Scene(new StackPane());

        LoginManager loginManager = new LoginManager(scene);
        loginManager.showLoginScreen();

        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
