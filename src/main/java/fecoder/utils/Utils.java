package fecoder.utils;

import fecoder.controllers.PackagingOwnerController;
import fecoder.models.Product;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utils {

    /**
     *
     * Handle the way how to escape from "click outside" event
     * whenever click on the other row or cell either
     * This method also support for "F5" and "ESC" KeyCode
     *
     * @param tableView - TableView ID
     * @param currentRow - current row for comparison
     * @param currentCell - current cell for comparison
     *
     * (this mean there are currentRow and currentCell in the controller)
     *
     * */
    public void reloadTableViewOnChange(TableView tableView, int currentRow, String currentCell) {
        tableView.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ESCAPE || event.getCode() == KeyCode.F5) {
                tableView.refresh();
            }
        });
        ObservableList<TablePosition> selectedCells = tableView.getSelectionModel().getSelectedCells();
        selectedCells.addListener(new ListChangeListener<TablePosition>() {
            @Override
            public void onChanged(Change change) {
                for (TablePosition pos : selectedCells) {
                    if(pos.getRow() != currentRow || !pos.getTableColumn().getText().equals(currentCell)) {
                        tableView.refresh();
                    }
                }
            }
        });
    }

    /**
     *
     * Handle an event for opening new Window with a Textarea
     *
     * @param owner the parent Window
     * @param currentValue the value before updating
     * @param commitHandler commit the new value back to its parent's Window
     * @param title a dynamic title for Window
     * */
    public void openTextareaWindow(Window owner, String currentValue, Consumer<String> commitHandler, String title) {
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);

        TextArea textArea = new TextArea(currentValue);

        Button okButton = new Button("ÁP DỤNG");
        okButton.setDefaultButton(true);
        okButton.setOnAction(e -> {
            commitHandler.accept(textArea.getText());
            stage.hide();
        });

        Button cancelButton = new Button("HỦY BỎ");
        cancelButton.setCancelButton(true);
        cancelButton.setOnAction(e -> stage.hide());

        HBox buttons = new HBox(5, okButton, cancelButton);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(5));

        BorderPane root = new BorderPane(textArea, null, null, buttons, null);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Formatting DatePicker
     *
     * @param datePicker a type of DatePicker
     * @param pattern pattern string of date
     *
     * @see <a href="https://www.baeldung.com/java-datetimeformatter"> Check this link to know how to format date </a>
     * */
    public void formatDate(DatePicker datePicker, String pattern) {
        datePicker.setConverter(new StringConverter<LocalDate>() {
            final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
            {
                datePicker.setPromptText(pattern.toLowerCase());
            }
            @Override
            public String toString(LocalDate localDate) {
                if (localDate != null) {
                    return dateTimeFormatter.format(localDate);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String s) {
                if (s != null && !s.isEmpty()) {
                    return LocalDate.parse(s, dateTimeFormatter);
                } else {
                    return null;
                }
            }
        });
    }

    /**
     * Handle on clearing editable comnbobox
     *
     * @param comboBox ComboBox id
     * @param value The specific value that gonna set
     * */
    public void setComboBoxValue(ComboBox comboBox, String value) {
        if(comboBox.isEditable()) {
            comboBox.getEditor().setText(value);
        } else {
            comboBox.getSelectionModel().select(value);
        }
        comboBox.hide();
    }

    /**
     * Separating date from string with the format YYYY-MM-DD
     * */
    public LocalDate getDate(String date) {
        int year = Integer.parseInt(date.split("-")[0]);
        int month = Integer.parseInt(date.split("-")[1]);
        int day = Integer.parseInt(date.split("-")[2]);

        return LocalDate.of(year, month, day);
    }

    /**
     * Disable press Enter on TextField
     *
     * @param id TextField id
     * */
    public void disableKeyEnterOnTextField(TextField id)
    {
        // The below will only consume an event without doing anything
//        id.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
//            if(event.getCode().equals(KeyCode.ENTER)) {
//                event.consume();
//            }
//        });

        // The below will fire an Enter event as Tab
        // @see <a href="https://stackoverflow.com/a/32830458"> A workaround </a>
        id.setOnAction((ActionEvent e) -> {
            boolean isThisField = false;
            for (Node child : id.getParent().getChildrenUnmodifiable()) {
                if (isThisField) {

                    //This code will only execute after the current Node
                    if (child.isFocusTraversable() && !child.isDisabled()) {
                        child.requestFocus();

                        //Reset check to prevent later Node from pulling focus
                        isThisField = false;
                    }
                } else {

                    //Check if this is the current Node
                    isThisField = child.equals(id);
                }
            }

            //Check if current Node still has focus
            boolean focusChanged = !id.isFocused();
            if (!focusChanged) {
                for (Node child : id.getParent().getChildrenUnmodifiable()) {
                    if (!focusChanged && child.isFocusTraversable() && !child.isDisabled()) {
                        child.requestFocus();

                        //Update to prevent later Node from pulling focus
                        focusChanged = true;
                    }
                }
            }
        });
    }

    /**
     * Disable press Enter on editable ComboBox
     *
     * @param id ComboBox id
     * */
    public void disableKeyEnterOnTextFieldComboBox(ComboBox id, boolean editable)
    {
        id.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if(t1) {
                    id.setEditable(editable);
                } else {
                    id.setEditable(false);
                }
            }
        });

        // The below will fire an Enter event as Tab
        // @see <a href="https://stackoverflow.com/a/32830458"> A workaround </a>
        id.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if(event.getCode().equals(KeyCode.ENTER) || event.getCode().equals(KeyCode.TAB)) {
                event.consume();
                if(!id.getItems().isEmpty()) {
                    boolean isThisField = false;
                    for (Node child : id.getParent().getChildrenUnmodifiable()) {
                        if (isThisField) {

                            //This code will only execute after the current Node
                            if (child.isFocusTraversable() && !child.isDisabled()) {
                                child.requestFocus();

                                //Reset check to prevent later Node from pulling focus
                                isThisField = false;
                            }
                        } else {

                            //Check if this is the current Node
                            isThisField = child.equals(id);
                        }
                    }

                    //Check if current Node still has focus
                    boolean focusChanged = !id.isFocused();

                    if (!focusChanged) {
                        for (Node child : id.getParent().getChildrenUnmodifiable()) {
                            if (!focusChanged && child.isFocusTraversable() && !child.isDisabled()) {
                                child.requestFocus();

                                //Update to prevent later Node from pulling focus
                                focusChanged = true;
                                id.setEditable(editable);
                            }
                        }
                    }

                    id.setEditable(false);
                }
            }
//            if(!id.isFocused() || !id.getEditor().isFocused() || !id.isShowing()) {
//                id.setEditable(false);
//            }
        });
    }

    /**
     * Disable press Enter on editable ComboBox
     *
     * @param id DatePicker id
     * */
    public void disableKeyEnterOnTextFieldDatePicker(DatePicker id)
    {
        // The below will fire an Enter event as Tab
        // @see <a href="https://stackoverflow.com/a/32830458"> A workaround </a>
        id.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if(event.getCode().equals(KeyCode.ENTER)) {
                event.consume();
                boolean isThisField = false;
                for (Node child : id.getParent().getChildrenUnmodifiable()) {
                    if (isThisField) {

                        //This code will only execute after the current Node
                        if (child.isFocusTraversable() && !child.isDisabled()) {
                            child.requestFocus();

                            //Reset check to prevent later Node from pulling focus
                            isThisField = false;
                        }
                    } else {

                        //Check if this is the current Node
                        isThisField = child.equals(id);
                    }
                }

                //Check if current Node still has focus
                boolean focusChanged = !id.isFocused();
                if (!focusChanged) {
                    for (Node child : id.getParent().getChildrenUnmodifiable()) {
                        if (!focusChanged && child.isFocusTraversable() && !child.isDisabled()) {
                            child.requestFocus();

                            //Update to prevent later Node from pulling focus
                            focusChanged = true;
                        }
                    }
                }
            }
        });
    }

    /**
     * Managing alerts
     *
     * @param type type of alert
     * @param alertType style of alert (such as: CONFIRMATION, ERROR, etc)
     * @param data string data
     * */
    public Alert alert(String type, Alert.AlertType alertType, String data) {
        Alert alert;
        String typeLabel = null, typeDesc = null;

        switch (type) {
            case "del":
            case "delete":
                typeLabel = "Xóa ";
                typeDesc = "Lưu ý: Dữ liệu sẽ không thể khôi phục lại sau khi xóa, bạn có chắc muốn tiếp tục?";
                alert = new Alert(alertType);
                alert.setTitle("Thông báo!");
                alert.setHeaderText(typeLabel+data);
                alert.setContentText(typeDesc);
                                break;
            case "update":
            case "edit":
                typeLabel = "Cập nhật thông tin cho ";
                typeDesc = "Nhấn OK để xác nhận thực hiện hành động.";
                alert = new Alert(alertType);
                alert.setTitle("Thông báo!");
                alert.setHeaderText(typeLabel+data);
                alert.setContentText(typeDesc);
                break;
            default:
                typeLabel = "Không xác định được hành động.";
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thông báo!");
                alert.setHeaderText(typeLabel);
                alert.close();
                break;
        }

        return alert;
    }

    /**
     * Loading scene utility
     *
     * @param resource resource path
     * @param title scene title
     * */
    public void loadScene(String resource, String title) {
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

    /**
     * Loading scene utility
     *
     * @param resource resource path
     * @param title scene title
     * @param product product data
     * */
    public void loadSingleProductScene(String resource, String title, Product product) {
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
            PackagingOwnerController packagingOwnerController = fxmlLoader.<PackagingOwnerController>getController();
            packagingOwnerController.setProductData(product);

        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        }
    }
}
