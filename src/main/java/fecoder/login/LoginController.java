package fecoder.login;

import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Window;

import fecoder.DAO.jdbcDAO;

public class LoginController {

    @FXML
    private TextField account;

    @FXML
    private PasswordField password;

    @FXML
    private Button submitButton;

    public void initialize() {}

    public void initManager(final LoginManager loginManager) {
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                if (account.getText().isEmpty()) {
                    showAlert("Tài khoản không thể bỏ trống");
                    return;
                }
                if (password.getText().isEmpty()) {
                    showAlert("Mật khẩu không thể bỏ trống");
                    return;
                }

                String user = account.getText();
                String pass = password.getText();

                jdbcDAO connect = new jdbcDAO();
                boolean flag = false;
                try {
                    flag = connect.validate(user, pass);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                if (!flag) {
                    infoBox("Vui lòng nhập đúng Tài khoản và Mật khẩu", null, "Thông báo");
                } else {
                    String sessionID = authorize(user,pass);
//                    infoBox("Đăng nhập thành công!", null, "Thông báo");
                    if (sessionID != null) {
                        loginManager.authenticated(sessionID);
                    }
                }
            }
        });
    }

    private String authorize(String user, String pass) {
        return user.equals(account.getText()) && pass.equals(password.getText())
                ? generateSessionID()
                : null;
    }

    private static int sessionID = 0;

    private String generateSessionID() {
        sessionID++;
        return ""+sessionID;
    }

    public static void infoBox(String infoMessage, String headerText, String title) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setContentText(infoMessage);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }

    private static void showAlert(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}