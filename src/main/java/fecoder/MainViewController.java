package fecoder;

import fecoder.login.LoginManager;
import fecoder.utils.Utils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MainViewController {
    public Button newWindowButton;
    public Button suplierManage;
    public Button packagingTypeManage;
    public Button yearManage;
    public Button sizeManage;
    public Button customerManage;
    public Button packagingManage;
    public Button productManage;
    public Button packagingOwner;
    public Button commands;
    @FXML private Button logoutButton;
    @FXML private Label sessionLabel;

    Utils utils = new Utils();

    public void initialize() {}

    public void initSessionID(final LoginManager loginManager, String sessionID) {
        sessionLabel.setText(sessionID);
        logoutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                loginManager.logout();
            }
        });

        suplierManage.setOnMouseClicked((event) -> {
            utils.loadScene("/fxml/suplier.fxml", "Quản Lý Danh Sách Nhà Cung Cấp");
        });

        packagingTypeManage.setOnMouseClicked((event) -> {
            utils.loadScene("/fxml/type.fxml", "Quản Lý Loại Bao Bì");
        });

        yearManage.setOnMouseClicked((event) -> {
            utils.loadScene("/fxml/year.fxml", "Quản Lý Loại Kích Thước");
        });

        sizeManage.setOnMouseClicked((event) -> {
            utils.loadScene("/fxml/size.fxml", "Quản lý size tôm");
        });

        customerManage.setOnMouseClicked((event) -> {
            utils.loadScene("/fxml/customer.fxml", "Quản lý khách hàng");
        });

        packagingManage.setOnMouseClicked((event) -> {
            utils.loadScene("/fxml/packaging.fxml", "Quản lý bao bì");
        });

        productManage.setOnMouseClicked((event) -> {
            utils.loadScene("/fxml/product.fxml", "Quản lý mặt hàng");
        });

        packagingOwner.setOnMouseClicked((event) -> {
            utils.loadScene("/fxml/packaging_owner.fxml", "Quản lý bao bì trong mặt hàng");
        });

        commands.setOnMouseClicked((event) -> {
            utils.loadScene("/fxml/commands.fxml", "Lệnh sản xuất");
        });
    }
}
