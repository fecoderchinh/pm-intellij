package fecoder.login;

import java.sql.SQLException;

import javafx.event.ActionEvent;
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
    private TextField accountField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button submitButton;

    @FXML
    public void login(ActionEvent event) throws SQLException {

        Window owner = submitButton.getScene().getWindow();

//        System.out.println(accountField.getText());
//        System.out.println(passwordField.getText());

        if (accountField.getText().isEmpty()) {
            showAlert(owner,
                    "Tài khoản không thể bỏ trống");
            return;
        }
        if (passwordField.getText().isEmpty()) {
            showAlert(owner,
                    "Mật khẩu không thể bỏ trống");
            return;
        }

        String emailId = accountField.getText();
        String password = passwordField.getText();

        jdbcDAO connect = new jdbcDAO();
        boolean flag = connect.validate(emailId, password);

        if (!flag) {
            infoBox("Vui lòng nhập đúng Tài khoản và Mật khẩu", null, "Thông báo");
        } else {
            infoBox("Đăng nhập thành công!", null, "Thông báo");
        }
    }

    public static void infoBox(String infoMessage, String headerText, String title) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setContentText(infoMessage);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }

    private static void showAlert(Window owner, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
    }
}