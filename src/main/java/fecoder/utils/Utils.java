package fecoder.utils;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.function.Consumer;

public class Utils {

    /**
     *
     * Handle the way how to escape from "click outside" event
     * whenever click on the other row or cell either
     * This method also support for "F5" and "ESC" KeyCode
     *
     * @param tv - TableView ID
     * @param currentRow - current row for comparison
     * @param currentCell - current cell for comparison
     *
     *
    * */
    public void updateTableOnChanged(TableView tv, int currentRow, String currentCell) {
        tv.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ESCAPE || event.getCode() == KeyCode.F5) {
                tv.refresh();
            }
        });
        ObservableList<TablePosition> selectedCells = tv.getSelectionModel().getSelectedCells();
        selectedCells.addListener(new ListChangeListener<TablePosition>() {
            @Override
            public void onChanged(Change change) {
                for (TablePosition pos : selectedCells) {
                    if(pos.getRow() != currentRow || !pos.getTableColumn().getText().equals(currentCell)) {
                        tv.refresh();
                    }
                }
            }
        });
    }

    /**
     *
     * Handle an event for opening new Window with a Textarea
     *
     * @param owner - the parent Window
     * @param currentValue - the value before updating
     * @param commitHandler - commit the new value back to its parent's Window
     * @param title - a dynamic title for Window
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
}
