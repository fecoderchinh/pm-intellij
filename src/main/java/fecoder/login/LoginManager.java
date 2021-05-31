package fecoder.login;

import fecoder.MainViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginManager {
    private Scene scene;

    public LoginManager(Scene scene) {
        this.scene = scene;
    }

    public void authenticated(String sessionID) {
        showMainView(sessionID);
    }

    public void logout() {
        showLoginScreen();
    }

    public void showLoginScreen() {
        try {
            FXMLLoader loader =new FXMLLoader(
                    getClass().getResource("/fxml/login_form.fxml")
            );
            scene.setRoot((Parent) loader.load());
            LoginController controller = loader.<LoginController>getController();
            controller.initManager(this);
        } catch (IOException ex) {
            Logger.getLogger(LoginManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void showMainView(String sessionID) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/mainview.fxml")
            );
            scene.setRoot((Parent) loader.load());
            MainViewController controller = loader.<MainViewController>getController();
            controller.initSessionID(this, sessionID);
        } catch (IOException ex) {
            Logger.getLogger(LoginManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
