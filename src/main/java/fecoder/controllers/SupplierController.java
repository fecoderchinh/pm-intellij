package fecoder.controllers;

import fecoder.DAO.SupplierDAO;
import fecoder.models.Packaging;
import fecoder.models.PackagingOwnerString;
import fecoder.models.Size;
import fecoder.models.Supplier;
import fecoder.utils.Utils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import javafx.scene.control.TableColumn;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class SupplierController implements Initializable {

    public TableView<Supplier> dataTable;
    public TableColumn<Supplier, Integer> idColumn;
    public TableColumn<Supplier, String> codeColumn;
    public TableColumn<Supplier, String> nameColumn;
    public TableColumn<Supplier, String> addressColumn;
    public TableColumn<Supplier, String> emailColumn;
    public TableColumn<Supplier, String> deputyColumn;
    public TableColumn<Supplier, String> phoneColumn;
    public TableColumn<Supplier, String> faxColumn;
//    public TableColumn<Supplier, String> fixedAddressColumn;

    public TextField codeField;
    public TextField nameField;
    public TextField addressField;
    public TextField emailField;
    public TextField deputyField;
    public TextField phoneField;
    public TextField faxField;
    public TextField searchField;
    public Button insertButton;
    public Button updateButton;
    public Button reloadData;
    public Label anchorLabel;
    public Label anchorData;
    public Button clearButton;
    public ComboBox<String> comboBox;

    private int currentRow;
    private String currentCell;
    private final Utils utils = new Utils();

    private final SupplierDAO supplierDAO = new SupplierDAO();

    /**
     * All needed to start controller
     * */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadView();
    }

    /**
     * Handle event on reloading Window
     * */
    @FXML
    private void reloadData() {
        reload();
    }

    /**
     * Handle event on clearing all inputs
     * */
    @FXML
    private void clearButton() {
        clearFields();
    }

    /**
     * Reloading method
     * */
    private void reload() {
        loadView();
        clearFields();
    }

    /**
     * Updating button action
     * */
    @FXML
    private void updateButton() {
        supplierDAO.update(
                nameField.getText(),
                addressField.getText(),
                emailField.getText(),
                deputyField.getText(),
                phoneField.getText(),
                faxField.getText(),
                codeField.getText().toUpperCase(),
                Integer.parseInt(anchorData.getText())
        );
        clearFields();
        reload();
    }

    /**
     * Inserting button action
     * */
    @FXML
    private void insertButton() {
        supplierDAO.insert(
                nameField.getText(),
                addressField.getText(),
                emailField.getText(),
                deputyField.getText(),
                phoneField.getText(),
                faxField.getText(),
                codeField.getText().toUpperCase()
        );
        clearFields();
        reload();
    }

    /**
     * Setting data for inputs
     *
     * @param supplier Supplier model
     * */
    private void getSupplier(Supplier supplier) {
        nameField.setText(supplier.getName());
        addressField.setText(supplier.getAddress());
        emailField.setText(supplier.getEmail());
        deputyField.setText(supplier.getDeputy());
        phoneField.setText(supplier.getPhone());
        faxField.setText(supplier.getFax());
        codeField.setText(supplier.getCode());
        anchorLabel.setText("ID Selected: ");
        anchorData.setText(""+ supplier.getId());
    }

    /**
     * Handle on clearing specific inputs
     * */
    private void clearFields() {
        nameField.setText(null);
        addressField.setText(null);
        emailField.setText(null);
        deputyField.setText(null);
        phoneField.setText(null);
        faxField.setText(null);
        codeField.setText(null);
        anchorLabel.setText("No ID Selected");
        anchorData.setText("");
    }

    /**
     * Handle on searching data
     * */
    public void setSearchField() {
        ObservableList<Supplier> list = FXCollections.observableArrayList(supplierDAO.getList());
        FilteredList<Supplier> filteredList = new FilteredList<>(list, p -> true);

        searchField.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    switch (comboBox.getValue()) {
                        case "Mã":
                            filteredList.setPredicate(str -> {
                                if (newValue == null || newValue.isEmpty())
                                    return true;
                                String lowerCaseFilter = newValue.toLowerCase();
                                return str.getCode().toLowerCase().contains
                                        (lowerCaseFilter);
                            });
                            break;
                        case "Tên":
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

        SortedList<Supplier> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(dataTable.comparatorProperty());

        dataTable.setItems(sortedList);
    }

    /**
     * Getting current row on click
     * */
    public void getCurrentRow() {
        TableView.TableViewSelectionModel<Supplier> selectionModel = dataTable.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        ObservableList<Supplier> getSelectedItems = selectionModel.getSelectedItems();

        getSelectedItems.addListener(new ListChangeListener<Supplier>() {
            @Override
            public void onChanged(Change<? extends Supplier> change) {
//                System.out.println("Selection changed: " + change.getList());
                dataTable.refresh();
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

        /*
         * Uncomment the below code to show the exact record ID
         * */
//        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        /*
         * The below code show auto increase number
         * */
        idColumn.setSortable(false);
        idColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Integer>(dataTable.getItems().indexOf(column.getValue())+1));

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellFactory(tc -> {

            TableCell<Supplier, String> cell = new TableCell<>();

            currentRow = cell.getIndex();
            currentCell = cell.getText();

            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(nameColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());

            cell.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && ! cell.isEmpty()) {
                    utils.openTextareaWindow(dataTable.getScene().getWindow(), cell.getItem(), newValue -> {
                        Supplier item = dataTable.getItems().get(cell.getIndex());
                        item.setName(newValue);
                        supplierDAO.updateData("name", newValue, item.getId());
                        dataTable.refresh();
                    }, "Cập nhật tên nhà cung cấp");
                }
            });

            return cell ;
        });

        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        addressColumn.setCellFactory(TextFieldTableCell.<Supplier>forTableColumn());
        addressColumn.setCellFactory(tc -> {

            TableCell<Supplier, String> cell = new TableCell<>();
            currentRow = cell.getIndex();
            currentCell = cell.getText();

            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(nameColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());

            cell.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && ! cell.isEmpty()) {
                    utils.openTextareaWindow(dataTable.getScene().getWindow(), cell.getItem(), newValue -> {
                        Supplier item = dataTable.getItems().get(cell.getIndex());
                        item.setName(newValue);
                        supplierDAO.updateData("address", newValue, item.getId());
                        dataTable.refresh();
                    }, "Cập nhật địa chỉ");
                }
            });

            return cell ;
        });

        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailColumn.setCellFactory(TextFieldTableCell.<Supplier>forTableColumn());
        emailColumn.setCellFactory(tc -> {

            TableCell<Supplier, String> cell = new TableCell<>();
            currentRow = cell.getIndex();
            currentCell = cell.getText();

            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(nameColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());

            cell.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && ! cell.isEmpty()) {
                    utils.openTextareaWindow(dataTable.getScene().getWindow(), cell.getItem(), newValue -> {
                        Supplier item = dataTable.getItems().get(cell.getIndex());
                        item.setName(newValue);
                        supplierDAO.updateData("email", newValue, item.getId());
                        dataTable.refresh();
                    }, "Cập nhật email");
                }
            });

            return cell ;
        });

        deputyColumn.setCellValueFactory(new PropertyValueFactory<>("deputy"));
        deputyColumn.setCellFactory(TextFieldTableCell.<Supplier>forTableColumn());
        deputyColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((Supplier) event.getTableView().getItems().get(event.getTablePosition().getRow())).setDeputy(data);
            supplierDAO.updateData("deputy", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneColumn.setCellFactory(TextFieldTableCell.<Supplier>forTableColumn());
        phoneColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((Supplier) event.getTableView().getItems().get(event.getTablePosition().getRow())).setPhone(data);
            supplierDAO.updateData("phone", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        faxColumn.setCellValueFactory(new PropertyValueFactory<>("fax"));
        faxColumn.setCellFactory(TextFieldTableCell.<Supplier>forTableColumn());
        faxColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((Supplier) event.getTableView().getItems().get(event.getTablePosition().getRow())).setFax(data);
            supplierDAO.updateData("fax", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        codeColumn.setCellFactory(TextFieldTableCell.<Supplier>forTableColumn());
        codeColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((Supplier) event.getTableView().getItems().get(event.getTablePosition().getRow())).setCode(data);
            supplierDAO.updateData("code", data, event.getRowValue().getId());
            dataTable.refresh();
        });

//        ObservableList<String> fixedAddressList = FXCollections.observableArrayList("Cố định", "Thay đổi");

//        fixedAddressColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Supplier, String>, ObservableValue<String>>() {
//            @Override
//            public ObservableValue<String> call(TableColumn.CellDataFeatures<Supplier, String> sizeCellDataFeatures) {
//                return new SimpleStringProperty(sizeCellDataFeatures.getValue().isFixed_address() ? "Cố định" : "Thay đổi");
//            }
//        });
//        fixedAddressColumn.setCellFactory(ComboBoxTableCell.forTableColumn(fixedAddressList));
//        fixedAddressColumn.setOnEditCommit(event -> {
//            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
//            ((Supplier) event.getTableView().getItems().get(event.getTablePosition().getRow())).setFixed_address(data.equals("Cố định"));
//            supplierDAO.updateDataBoolean("fixed_ship_address", data.equals("Cố định"), event.getRowValue().getId());
//            dataTable.refresh();
//        });

        dataTable.setRowFactory((TableView<Supplier> tableView) -> {
            final TableRow<Supplier> row = new TableRow<>();

            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem viewItem = new MenuItem("Chi tiết");
            final MenuItem editItem = new MenuItem("Cập nhật");
            final MenuItem removeItem = new MenuItem("Xóa dòng");

            viewItem.setOnAction((ActionEvent event) -> {
                Supplier supplier = dataTable.getSelectionModel().getSelectedItem();
                int rowIndex = dataTable.getSelectionModel().getSelectedIndex();

                utils.alert("info", Alert.AlertType.INFORMATION, "Chi tiết "+supplier.getName(),
                        "Mã NCC: " + supplier.getCode() + "\n" +
                                "Tên NCC: " + supplier.getName() + "\n" +
                                "Địa chỉ: " + supplier.getAddress() + "\n" +
                                "Email: " + supplier.getEmail() + "\n" +
                                "Đại diện: " + supplier.getDeputy() + "\n" +
                                "Điện thoại: " + supplier.getPhone() + "\n" +
                                "Fax: " + supplier.getFax() + "\n"
                        ).showAndWait();
            });
            contextMenu.getItems().add(viewItem);

            editItem.setOnAction((ActionEvent event) -> {
                Supplier supplier = dataTable.getSelectionModel().getSelectedItem();
                getSupplier(supplier);
            });
            contextMenu.getItems().add(editItem);

            removeItem.setOnAction((ActionEvent event) -> {
                Supplier supplier = dataTable.getSelectionModel().getSelectedItem();
                Alert alert = utils.alert("del", Alert.AlertType.CONFIRMATION, "Xóa: "+ supplier.getName(), null);

                Optional<ButtonType> result = alert.showAndWait();
                if (alert.getAlertType() == Alert.AlertType.CONFIRMATION && result.get() == ButtonType.OK){
                    supplierDAO.delete(supplier.getId());
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

//        suplierTable.setItems(list);
        utils.reloadTableViewOnChange(dataTable, currentRow, currentCell);
        setSearchField();
    }
}
