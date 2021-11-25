package fecoder.controllers;

import fecoder.DAO.ShipAddressDAO;
import fecoder.models.ShipAddress;
import fecoder.models.WorkOrder;
import fecoder.utils.Utils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ShipAddressController implements Initializable {
    public TextField codeField;
    public TextField nameField;
    public TextField addressField;
    public TextField stockerField;
    public TextField phoneField;
    public Button insertButton;
    public Button updateButton;
    public Button clearButton;
    public ComboBox<String> comboBox;
    public TextField searchField;
    public Button reloadData;
    public TableView<ShipAddress> dataTable;
    public TableColumn<ShipAddress, Integer> idColumn;
    public TableColumn<ShipAddress, String> nameColumn;
    public TableColumn<ShipAddress, String> codeColumn;
    public TableColumn<ShipAddress, String> addressColumn;
    public TableColumn<ShipAddress, String> stockerColumn;
    public TableColumn<ShipAddress, String> phoneColumn;
    public Label anchorLabel;
    public Label anchorData;

    private boolean isEditableComboBox = false;
    private boolean isUpdating = false;

    private int currentRow;
    private String currentCell;
    private final Utils utils = new Utils();

    private final ShipAddressDAO shipAddressDAO = new ShipAddressDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadView();
    }

    public void insertButton(ActionEvent actionEvent) {
        try {
            shipAddressDAO.insert(
                    nameField.getText(),
                    addressField.getText(),
                    codeField.getText(),
                    stockerField.getText(),
                    phoneField.getText()
            );
            reload();
        } catch (Exception ex) {
            utils.alert("err", Alert.AlertType.ERROR, "Error", ex.getMessage()).showAndWait();
        }
    }

    public void updateButton(ActionEvent actionEvent) {
        try {
            shipAddressDAO.update(
                    nameField.getText(),
                    addressField.getText(),
                    codeField.getText(),
                    stockerField.getText(),
                    phoneField.getText(),
                    Integer.parseInt(anchorData.getText())
            );
            reload();
        } catch (Exception ex) {
            utils.alert("err", Alert.AlertType.ERROR, "Error", ex.getMessage()).showAndWait();
        }
    }

    public void clearButton(ActionEvent actionEvent) {
        clearFields();
    }

    public void reloadData(ActionEvent actionEvent) {
        reload();
    }

    /**
     * Reloading method
     * */
    private void reload() {
        utils.reloadTableViewOnChange(dataTable, currentRow, currentCell);
        clearFields();
        loadView();
    }

    /**
     * Handle event on clearing all inputs
     * */
    private void clearFields() {
        anchorLabel.setText("No ID selected");
        anchorData.setText("");

        nameField.setText("");
        codeField.setText("");
        addressField.setText("");
        phoneField.setText("");
        stockerField.setText("");

        searchField.setText("");
    }

    /**
     * Reset fields
     * This will handle an enter event and mouse leave for these Nodes
     * */
    private void resetFields() {
        utils.disableKeyEnterOnTextField(nameField);
        utils.disableKeyEnterOnTextField(codeField);
        utils.disableKeyEnterOnTextField(addressField);
        utils.disableKeyEnterOnTextField(stockerField);
        utils.disableKeyEnterOnTextField(phoneField);
    }

    /**
     * Load the current view resources.
     * <br>
     * Contains: <br>
     * - resetFields() <br>
     * - resetComboBox() <br>
     * - setContextMenu() <br>
     * - setSearchField() <br>
     * - Controlling columns view and actions <br>
     * - Implementing contextMenu on right click <br>
     * */
    private void loadView(){
        anchorLabel.setText("No ID Selected");
        resetFields();

        dataTable.setEditable(true);

        idColumn.setSortable(false);
        idColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Integer>(dataTable.getItems().indexOf(column.getValue())+1));

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellFactory(TextFieldTableCell.<ShipAddress>forTableColumn());
        nameColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((ShipAddress) event.getTableView().getItems().get(event.getTablePosition().getRow())).setName(data);
            shipAddressDAO.updateData("name", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code_address"));
        codeColumn.setCellFactory(TextFieldTableCell.<ShipAddress>forTableColumn());
        codeColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((ShipAddress) event.getTableView().getItems().get(event.getTablePosition().getRow())).setCode_address(data);
            shipAddressDAO.updateData("code_address", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        addressColumn.setCellFactory(TextFieldTableCell.<ShipAddress>forTableColumn());
        addressColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((ShipAddress) event.getTableView().getItems().get(event.getTablePosition().getRow())).setAddress(data);
            shipAddressDAO.updateData("address", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        stockerColumn.setCellValueFactory(new PropertyValueFactory<>("stocker"));
        stockerColumn.setCellFactory(TextFieldTableCell.<ShipAddress>forTableColumn());
        stockerColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((ShipAddress) event.getTableView().getItems().get(event.getTablePosition().getRow())).setStocker(data);
            shipAddressDAO.updateData("stocker", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("stocker_phone"));
        phoneColumn.setCellFactory(TextFieldTableCell.<ShipAddress>forTableColumn());
        phoneColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((ShipAddress) event.getTableView().getItems().get(event.getTablePosition().getRow())).setStocker_phone(data);
            shipAddressDAO.updateData("stocker_phone", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        dataTable.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ESCAPE || event.getCode() == KeyCode.F5) {
                dataTable.refresh();
            }
        });

        setContextMenu();

        utils.reloadTableViewOnChange(dataTable, currentRow, currentCell);

        setSearchField();
    }

    /**
     * Handle on searching data
     * */
    public void setSearchField() {
        ObservableList<ShipAddress> list = FXCollections.observableArrayList(shipAddressDAO.getList());
        FilteredList<ShipAddress> filteredList = new FilteredList<>(list, p -> true);

        searchField.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    switch (comboBox.getValue()) {
                        case "Mã Cty":
                            filteredList.setPredicate(str -> {
                                if (newValue == null || newValue.isEmpty())
                                    return true;
                                String lowerCaseFilter = newValue.toLowerCase();
                                return str.getCode_address().toLowerCase().contains
                                        (lowerCaseFilter);
                            });
                            break;
                        case "Tên Cty":
                            filteredList.setPredicate(str -> {
                                if (newValue == null || newValue.isEmpty())
                                    return true;
                                String lowerCaseFilter = newValue.toLowerCase();
                                return str.getName().toLowerCase().contains
                                        (lowerCaseFilter);
                            });
                            break;
                        case "Địa chỉ":
                            filteredList.setPredicate(str -> {
                                if (newValue == null || newValue.isEmpty())
                                    return true;
                                String lowerCaseFilter = newValue.toLowerCase();
                                return str.getAddress().toLowerCase().contains
                                        (lowerCaseFilter);
                            });
                            break;
                    }
                });

        comboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal != null) {
                searchField.setText(null);
            }
        });

        SortedList<ShipAddress> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(dataTable.comparatorProperty());

        dataTable.setItems(sortedList);
    }

    /**
     * Setting context menu for table row
     * */
    private void setContextMenu() {
        dataTable.setRowFactory((TableView<ShipAddress> tableView) -> {
            final TableRow<ShipAddress> row = new TableRow<>();

            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem viewItem = new MenuItem("Chi tiết");
            final MenuItem editItem = new MenuItem("Cập nhật");
            final MenuItem removeItem = new MenuItem("Xóa dòng");

            ShipAddress data = dataTable.getSelectionModel().getSelectedItem();

            viewItem.setOnAction((ActionEvent event) -> {
                int rowIndex = dataTable.getSelectionModel().getSelectedIndex();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Chi tiết");
                alert.setHeaderText(data.getName());
                alert.setContentText(
                        "Tên Cty: " + data.getName() + "\n" +
                        "Địa chỉ: " + data.getAddress() + "\n" +
                        "Thủ kho: " + data.getStocker() + (data.getStocker_phone() != null ? " - " + data.getStocker_phone() : "") + "\n"
                );

                alert.showAndWait();
            });
            contextMenu.getItems().add(viewItem);

            editItem.setOnAction((ActionEvent event) -> {
                getData(data);
            });
            contextMenu.getItems().add(editItem);

            removeItem.setOnAction((ActionEvent event) -> {
                Alert alert = utils.alert("del", Alert.AlertType.CONFIRMATION, "Xóa: "+ data.getName(), null);

                Optional<ButtonType> result = alert.showAndWait();
                if (alert.getAlertType() == Alert.AlertType.CONFIRMATION && result.get() == ButtonType.OK){
                    shipAddressDAO.delete(data.getId());
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
            return row ;
        });
    }

    /**
     * Setting data for inputs
     *
     * @param data ShipAddress model
     * */
    private void getData(ShipAddress data) {
        clearFields();
        nameField.setText(data.getName());
        codeField.setText(data.getCode_address());
        addressField.setText(data.getAddress());
        stockerField.setText(data.getStocker());
        phoneField.setText(data.getStocker_phone());

        anchorLabel.setText("ID selected: ");
        anchorData.setText(""+ data.getId());

        isUpdating = true;
    }
}
