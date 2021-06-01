package fecoder;

import fecoder.login.LoginManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainViewController {
    public Button newWindowButton;
    @FXML private Button logoutButton;
    @FXML private Label sessionLabel;

    public void initialize() {}

    public void initSessionID(final LoginManager loginManager, String sessionID) {
        sessionLabel.setText(sessionID);
        logoutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                loginManager.logout();
            }
        });

        newWindowButton.setOnMouseClicked((event) -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/NewWindow.fxml"));
                /*
                 * if "fx:controller" is not set in fxml
                 * fxmlLoader.setController(NewWindowController);
                 */
                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = new Stage();
                stage.setTitle("New Window");
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();
            } catch (IOException e) {
                Logger logger = Logger.getLogger(getClass().getName());
                logger.log(Level.SEVERE, "Failed to create new Window.", e);
            }
        });
    }
}
