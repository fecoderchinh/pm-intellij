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
    public Button suplierManage;
    public Button packagingTypeManage;
    public Button yearManage;
    public Button sizeManage;
    public Button customerManage;
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

        suplierManage.setOnMouseClicked((event) -> {
            loadScene("/fxml/suplier.fxml", "Quản Lý Danh Sách Nhà Cung Cấp");
        });

        packagingTypeManage.setOnMouseClicked((event) -> {
            loadScene("/fxml/type.fxml", "Quản Lý Loại Bao Bì");
        });

        yearManage.setOnMouseClicked((event) -> {
            loadScene("/fxml/year.fxml", "Quản Lý Loại Kích Thước");
        });

        sizeManage.setOnMouseClicked((event) -> {
            loadScene("/fxml/size.fxml", "Quản lý size tôm");
        });

        customerManage.setOnMouseClicked((event) -> {
            loadScene("/fxml/customer.fxml", "Quản lý khách hàng");
        });
    }

    private void loadScene(String resource, String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(resource));
            /*
             * if "fx:controller" is not set in fxml
             * fxmlLoader.setController(NewWindowController);
             */
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        }
    }
}
