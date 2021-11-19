package fecoder.controllers;

import fecoder.DAO.TypeDAO;
import fecoder.models.Type;
import fecoder.utils.Utils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class TypeController implements Initializable {

    public TableView<Type> dataTable;
    public TableColumn<Type, Integer> idColumn;
    public TableColumn<Type, String> nameColumn;
    public TableColumn<Type, String> unitColumn;

    public Button insertButton;
    public Button updateButton;
    public Button clearButton;
    public TextField nameField;
    public TextField searchField;
    public Button reloadData;
    public Label anchorLabel;
    public Label anchorData;
    public TextField unitField;

    private final TypeDAO typeDAO = new TypeDAO();
    Utils utils = new Utils();

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
    @FXML
    public void insertButton(ActionEvent actionEvent) {
        typeDAO.insert(nameField.getText(), unitField.getText());
        clearFields();
        reload();
    }

    /**
     * Updating button action
     * */
    @FXML
    public void updateButton(ActionEvent actionEvent) {
        typeDAO.update(nameField.getText(), unitField.getText(), Integer.parseInt(anchorData.getText()));
        clearFields();
        reload();
    }

    /**
     * Handle event on clearing all inputs
     * */
    @FXML
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
     * Handle on searching data
     * */
    public void setSearchField() {
        ObservableList<Type> list = FXCollections.observableArrayList(typeDAO.getList());
        FilteredList<Type> filteredList = new FilteredList<>(list, p -> true);

        searchField.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    filteredList.setPredicate(str -> {
                        if (newValue == null || newValue.isEmpty())
                            return true;
                        String lowerCaseFilter = newValue.toLowerCase();
                        return str.getName().toLowerCase().contains
                                (lowerCaseFilter);
                    });
                });

        SortedList<Type> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(dataTable.comparatorProperty());

        dataTable.setItems(sortedList);
    }

    /**
     * Getting current row on click
     * */
    public void getCurrentRow() {
        TableView.TableViewSelectionModel<Type> selectionModel = dataTable.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        ObservableList<Type> getSelectedItems = selectionModel.getSelectedItems();

        getSelectedItems.addListener(new ListChangeListener<Type>() {
            @Override
            public void onChanged(Change<? extends Type> change) {
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

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellFactory(TextFieldTableCell.<Type>forTableColumn());
        nameColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((Type) event.getTableView().getItems().get(event.getTablePosition().getRow())).setName(data);
            typeDAO.updateData("name", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        unitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));
        unitColumn.setCellFactory(TextFieldTableCell.<Type>forTableColumn());
        unitColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((Type) event.getTableView().getItems().get(event.getTablePosition().getRow())).setName(data);
            typeDAO.updateData("unit", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        dataTable.setRowFactory((TableView<Type> tableView) -> {
            final TableRow<Type> row = new TableRow<>();

            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem viewItem = new MenuItem("Chi tiết");
            final MenuItem editItem = new MenuItem("Cập nhật");
            final MenuItem removeItem = new MenuItem("Xóa dòng");

            viewItem.setOnAction((ActionEvent event) -> {
                Type data = dataTable.getSelectionModel().getSelectedItem();
                int rowIndex = dataTable.getSelectionModel().getSelectedIndex();

                utils.alert("info", Alert.AlertType.INFORMATION, "Chi tiết "+data.getName(),
                                "Tên loại: " + data.getName() + "\n" +
                                "Đơn vị tính: " + data.getUnit()
                ).showAndWait();
            });
            contextMenu.getItems().add(viewItem);

            editItem.setOnAction((ActionEvent event) -> {
                Type data = dataTable.getSelectionModel().getSelectedItem();
                getItem(data);
            });
            contextMenu.getItems().add(editItem);

            removeItem.setOnAction((ActionEvent event) -> {
                Type data = dataTable.getSelectionModel().getSelectedItem();
                Alert alert = utils.alert("del", Alert.AlertType.CONFIRMATION, "Xóa: "+ data.getName(), null);

                Optional<ButtonType> result = alert.showAndWait();
                if (alert.getAlertType() == Alert.AlertType.CONFIRMATION && result.get() == ButtonType.OK){
                    typeDAO.delete(data.getId());
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
     * @param t Type model
     * */
    private void getItem(Type t) {
        nameField.setText(t.getName());
        unitField.setText(t.getUnit());
        anchorLabel.setText("ID Selected");
        anchorData.setText(""+t.getId());
    }

    /**
     * Handle on clearing specific inputs
     * */
    private void clearFields() {
        nameField.setText(null);
        unitField.setText(null);
        anchorLabel.setText("No ID Selected");
        anchorData.setText(null);
    }
}
