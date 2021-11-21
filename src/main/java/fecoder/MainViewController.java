package fecoder;

import fecoder.login.LoginManager;
import fecoder.utils.Utils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

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
    public Button packagingOwner;
    @FXML private Button logoutButton;
    @FXML private Label sessionLabel;

    Utils utils = new Utils();

    public void initialize() {}

    public void initSessionID(final LoginManager loginManager, String sessionID) {
        sessionLabel.setText(sessionID);

        packagingOwner.setOnMouseClicked((event) -> {
            utils.loadSceneWithStage((Stage)suplierManage.getScene().getWindow(), "/fxml/statistics.fxml", "Thống kê bao bì");
        });
        
        logoutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                loginManager.logout();
            }
        });

        suplierManage.setGraphic(new ImageView(new Image(getClass().getResource("/images/suppliers.png").toString(), 200, 200, true, true)));
        suplierManage.setContentDisplay(ContentDisplay.TOP);
        suplierManage.setOnMouseClicked((event) -> {
            utils.loadSceneWithStage((Stage)suplierManage.getScene().getWindow(), "/fxml/suplier.fxml", "Danh Sách Nhà Cung Cấp");
        });

        packagingTypeManage.setGraphic(new ImageView(new Image(getClass().getResource("/images/package.png").toString(), 200, 200, true, true)));
        packagingTypeManage.setContentDisplay(ContentDisplay.TOP);
        packagingTypeManage.setOnMouseClicked((event) -> {
            utils.loadSceneWithStage((Stage)packagingTypeManage.getScene().getWindow(), "/fxml/type.fxml", "Loại Bao Bì");
        });

        yearManage.setGraphic(new ImageView(new Image(getClass().getResource("/images/calendar.png").toString(), 200, 200, true, true)));
        yearManage.setContentDisplay(ContentDisplay.TOP);
        yearManage.setOnMouseClicked((event) -> {
            utils.loadSceneWithStage((Stage)yearManage.getScene().getWindow(),"/fxml/year.fxml", "Năm phát hành");
        });

        sizeManage.setGraphic(new ImageView(new Image(getClass().getResource("/images/shrimp.png").toString(), 200, 200, true, true)));
        sizeManage.setContentDisplay(ContentDisplay.TOP);
        sizeManage.setOnMouseClicked((event) -> {
            utils.loadSceneWithStage((Stage)sizeManage.getScene().getWindow(), "/fxml/size.fxml", "Size tôm");
        });

        customerManage.setGraphic(new ImageView(new Image(getClass().getResource("/images/customer.png").toString(), 200, 200, true, true)));
        customerManage.setContentDisplay(ContentDisplay.TOP);
        customerManage.setOnMouseClicked((event) -> {
            utils.loadSceneWithStage((Stage)customerManage.getScene().getWindow(), "/fxml/customer.fxml", "Danh sách khách hàng");
        });

        packagingManage.setGraphic(new ImageView(new Image(getClass().getResource("/images/packages.png").toString(), 200, 200, true, true)));
        packagingManage.setContentDisplay(ContentDisplay.TOP);
        packagingManage.setOnMouseClicked((event) -> {
            utils.loadSceneWithStage((Stage)packagingManage.getScene().getWindow(), "/fxml/packaging.fxml", "Danh sách bao bì");
        });

        productManage.setGraphic(new ImageView(new Image(getClass().getResource("/images/tempura.png").toString(), 200, 200, true, true)));
        productManage.setContentDisplay(ContentDisplay.TOP);
        productManage.setOnMouseClicked((event) -> {
            utils.loadSceneWithStage((Stage)productManage.getScene().getWindow(),"/fxml/product.fxml", "Danh sách sản phẩm");
        });

        workOrder.setGraphic(new ImageView(new Image(getClass().getResource("/images/note.png").toString(), 200, 200, true, true)));
        workOrder.setContentDisplay(ContentDisplay.TOP);
        workOrder.setOnMouseClicked((event) -> {
            utils.loadSceneWithStage((Stage)workOrder.getScene().getWindow(), "/fxml/work_order.fxml", "Lệnh sản xuất");
        });
    }
}
