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
                    "Please enter your email id");
            return;
        }
        if (passwordField.getText().isEmpty()) {
            showAlert(owner,
                    "Please enter a password");
            return;
        }

        String emailId = accountField.getText();
        String password = passwordField.getText();

        jdbcDAO connect = new jdbcDAO();
        boolean flag = connect.validate(emailId, password);

        if (!flag) {
            infoBox("Please enter correct Email and Password", null, "Failed");
        } else {
            infoBox("Login Successful!", null, "Failed");
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
        alert.setTitle("Form Error!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
    }
}