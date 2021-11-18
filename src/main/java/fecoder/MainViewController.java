package fecoder;

import fecoder.login.LoginManager;
import fecoder.utils.Utils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MainViewController {
    public Button newWindowButton;
    public Button suplierManage;
    public Button packagingTypeManage;
    public Button yearManage;
    public Button sizeManage;
    public Button customerManage;
    public Button packagingManage;
    public Button productManage;
//    public Button packagingOwner;
    public Button workOrder;
    @FXML private Button logoutButton;
    @FXML private Label sessionLabel;

    Utils utils = new Utils();

    public void initialize() {}

    public void initSessionID(final LoginManager loginManager, String sessionID) {
        sessionLabel.setText(sessionID);

//        newWindowButton.setGraphic(new ImageView(new Image(getClass().getResource("/images/logo.jpg").toString())));
//        newWindowButton.setContentDisplay(ContentDisplay.TOP);

        logoutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                loginManager.logout();
            }
        });

        suplierManage.setGraphic(new ImageView(new Image(getClass().getResource("/images/suppliers.png").toString(), 200, 200, true, true)));
        suplierManage.setContentDisplay(ContentDisplay.TOP);
        suplierManage.setOnMouseClicked((event) -> {
            utils.loadScene("/fxml/suplier.fxml", "Quản Lý Danh Sách Nhà Cung Cấp");
        });

        packagingTypeManage.setGraphic(new ImageView(new Image(getClass().getResource("/images/package.png").toString(), 200, 200, true, true)));
        packagingTypeManage.setContentDisplay(ContentDisplay.TOP);
        packagingTypeManage.setOnMouseClicked((event) -> {
            utils.loadScene("/fxml/type.fxml", "Quản Lý Loại Bao Bì");
        });

        yearManage.setGraphic(new ImageView(new Image(getClass().getResource("/images/calendar.png").toString(), 200, 200, true, true)));
        yearManage.setContentDisplay(ContentDisplay.TOP);
        yearManage.setOnMouseClicked((event) -> {
            utils.loadScene("/fxml/year.fxml", "Quản Lý Loại Kích Thước");
        });

        sizeManage.setGraphic(new ImageView(new Image(getClass().getResource("/images/shrimp.png").toString(), 200, 200, true, true)));
        sizeManage.setContentDisplay(ContentDisplay.TOP);
        sizeManage.setOnMouseClicked((event) -> {
            utils.loadScene("/fxml/size.fxml", "Quản lý size tôm");
        });

        customerManage.setGraphic(new ImageView(new Image(getClass().getResource("/images/customer.png").toString(), 200, 200, true, true)));
        customerManage.setContentDisplay(ContentDisplay.TOP);
        customerManage.setOnMouseClicked((event) -> {
            utils.loadScene("/fxml/customer.fxml", "Quản lý khách hàng");
        });

        packagingManage.setGraphic(new ImageView(new Image(getClass().getResource("/images/packages.png").toString(), 200, 200, true, true)));
        packagingManage.setContentDisplay(ContentDisplay.TOP);
        packagingManage.setOnMouseClicked((event) -> {
            utils.loadScene("/fxml/packaging.fxml", "Quản lý bao bì");
        });

        productManage.setGraphic(new ImageView(new Image(getClass().getResource("/images/tempura.png").toString(), 200, 200, true, true)));
        productManage.setContentDisplay(ContentDisplay.TOP);
        productManage.setOnMouseClicked((event) -> {
            utils.loadScene("/fxml/product.fxml", "Quản lý mặt hàng");
        });

//        packagingOwner.setOnMouseClicked((event) -> {
//            utils.loadScene("/fxml/packaging_owner.fxml", "Quản lý bao bì trong mặt hàng");
//        });

        workOrder.setGraphic(new ImageView(new Image(getClass().getResource("/images/note.png").toString(), 200, 200, true, true)));
        workOrder.setContentDisplay(ContentDisplay.TOP);
        workOrder.setOnMouseClicked((event) -> {
            utils.loadScene("/fxml/work_order.fxml", "Lệnh sản xuất");
        });
    }
}
