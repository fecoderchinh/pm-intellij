package fecoder.controllers;

import fecoder.DAO.SizeDAO;
import fecoder.models.Size;
import fecoder.utils.Utils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class SizeController implements Initializable {
    public TextField sizeField;
    public Button insertButton;
    public Button updateButton;
    public Button clearButton;
    public TextField searchField;
    public Button reloadData;
    public Label anchorLabel;
    public Label anchorData;

    public TableView<Size> dataTable;
    public TableColumn<Size, Integer> idColumn;
    public TableColumn<Size, String> sizeColumn;

    private final SizeDAO DAO = new SizeDAO();

    private final Utils utils = new Utils();

    /**
     * All needed to start controller
     * */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadView();
    }

    /**
     * Inserting button action
     * */
    public void insertButton(ActionEvent actionEvent) {
        DAO.insert(sizeField.getText());
        clearFields();
        reload();
    }

    /**
     * Updating button action
     * */
    public void updateButton(ActionEvent actionEvent) {
        DAO.update(sizeField.getText(), Integer.parseInt(anchorData.getText()));
        clearFields();
        reload();
    }

    /**
     * Handle event on clearing all inputs
     * */
    public void clearButton(ActionEvent actionEvent) {
        clearFields();
    }

    /**
     * Handle event on reloading Window
     * */
    public void reloadData(ActionEvent actionEvent) {
        reload();
    }

    /**
     * Reloading method
     * */
    private void reload() {
        loadView();
        clearFields();
    }

    /**
     * Setting data for inputs
     *
     * @param data - the record's data
     * @param id - the record's id
     * */
    private void getItem(String data, int id) {
        sizeField.setText(data);
        anchorLabel.setText("ID Selected");
        anchorData.setText(""+id);
    }

    /**
     * Handle on clearing specific inputs
     * */
    private void clearFields() {
        sizeField.setText(null);
        anchorLabel.setText("No ID Selected");
        anchorData.setText(null);
    }

    /**
     * Handle on searching data
     * */
    public void setSearchField() {
        ObservableList<Size> list = FXCollections.observableArrayList(DAO.getList());
        FilteredList<Size> filteredList = new FilteredList<>(list, p -> true);

        sizeField.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    searchField.setText("");
                    filteredList.setPredicate(str -> {
                        if (newValue == null || newValue.isEmpty())
                            return true;
                        String lowerCaseFilter = newValue.toLowerCase();
                        return str.getSize().toLowerCase().contains
                                (lowerCaseFilter);
                    });
                });

        searchField.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    filteredList.setPredicate(str -> {
                        if (newValue == null || newValue.isEmpty())
                            return true;
                        String lowerCaseFilter = newValue.toLowerCase();
                        return str.getSize().toLowerCase().contains
                                (lowerCaseFilter);
                    });
                });

        SortedList<Size> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(dataTable.comparatorProperty());

        dataTable.setItems(sortedList);
    }

    /**
     * Getting current row on click
     * */
    public void getCurrentRow() {
        TableView.TableViewSelectionModel<Size> selectionModel = dataTable.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        ObservableList<Size> getSelectedItems = selectionModel.getSelectedItems();

        getSelectedItems.addListener(new ListChangeListener<Size>() {
            @Override
            public void onChanged(Change<? extends Size> change) {
//                System.out.println("Selection changed: " + change.getList());
            }
        });

    }

    /**
     * Load the current view resources.
     * <br>
     * Contains: <br>
     * - getCurrentRow() <br>
     * - setSearchField() <br>
     * - Controlling columns view and actions <br>
     * - Implementing contextMenu on right click <br>
     * */
    public void loadView() {
        anchorLabel.setText("No ID Selected");

        dataTable.setEditable(true);

        getCurrentRow();

        idColumn.setSortable(false);
        idColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Integer>(dataTable.getItems().indexOf(column.getValue())+1));

        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        sizeColumn.setCellFactory(TextFieldTableCell.<Size>forTableColumn());
        sizeColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((Size) event.getTableView().getItems().get(event.getTablePosition().getRow())).setSize(data);
            DAO.updateData("size", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        dataTable.setRowFactory((TableView<Size> tableView) -> {
            final TableRow<Size> row = new TableRow<>();

            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem viewItem = new MenuItem("Chi tiết");
            final MenuItem editItem = new MenuItem("Cập nhật");
            final MenuItem removeItem = new MenuItem("Xóa dòng");

            removeItem.setOnAction((ActionEvent event) -> {
                Size data = dataTable.getSelectionModel().getSelectedItem();
                Alert alert = utils.alert("del", Alert.AlertType.CONFIRMATION, "Xóa: "+ data.getSize(), null);

                Optional<ButtonType> result = alert.showAndWait();
                if (alert.getAlertType() == Alert.AlertType.CONFIRMATION && result.get() == ButtonType.OK){
                    DAO.delete(data.getId());
                    reload();
                }
            });
            contextMenu.getItems().add(removeItem);
            // Set context menu on row, but use a binding to make it only show for non-empty rows:
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu)null)
                            .otherwise(contextMenu)
            );
            return row;
        });

        setSearchField();
    }
}
