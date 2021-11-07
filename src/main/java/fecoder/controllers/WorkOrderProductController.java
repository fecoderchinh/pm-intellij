package fecoder.controllers;

import fecoder.DAO.*;
import fecoder.models.*;
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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class WorkOrderProductController implements Initializable {
    public Label mainLabel;
    public TextField ordinalNumberField;
    public ComboBox<Product> productComboBox;
    public TextField qtyField;
    public TextField noteField;
    public Button insertButton;
    public Button updateButton;
    public Button clearButton;
    public ComboBox<String> searchComboBox;
    public TextField searchField;
    public Button reloadData;

    public TableView<WorkOrderProductString> productTableView;
    public TableColumn<WorkOrderProductString, String> productIdColumn;
    public TableColumn<WorkOrderProductString, String> productNameColumn;

    public TableView<WorkProduction> dataTable;
    public TableColumn<WorkProduction, Integer> idColumn;
    public TableColumn<WorkProduction, String> packagingNameColumn;
    public TableColumn<WorkProduction, String> packagingSpecificationColumn;
    public TableColumn<WorkProduction, String> packagingDimensionColumn;
    public TableColumn<WorkProduction, String> packagingSuplierColumn;
    public TableColumn<WorkProduction, String> packagingCodeColumn;
    public TableColumn<WorkProduction, String> unitColumn;
    public TableColumn<WorkProduction, String> printStatusColumn;
    public TableColumn<WorkProduction, Integer> packQuantityColumn;
    public TableColumn<WorkProduction, Float> workOrderQuantityColumn;
    public TableColumn<WorkProduction, Float> stockColumn;
    public TableColumn<WorkProduction, Float> actualQuantityColumn;
    public TableColumn<WorkProduction, Float> residualQuantityColumn;
    public TableColumn<WorkProduction, Float> totalResidualQuantityColumn;
    public TableColumn<WorkProduction, String> noteProductColumn;
    
    public Label anchorLabel;
    public Label anchorData;

    private int currentRow;
    private String currentCell;
    private final Utils utils = new Utils();

    private boolean isEditableComboBox = false;
    private boolean isUpdating = false;

    ProductDAO productDAO = new ProductDAO();
    WorkOrderProductPackagingDAO workOrderProductPackagingDAO = new WorkOrderProductPackagingDAO();
    WorkProductionDAO workProductionDAO = new WorkProductionDAO();
    WorkOrderDAO workOrderDAO = new WorkOrderDAO();
    WorkOrderProductDAO workOrderProductDAO = new WorkOrderProductDAO();
    WorkOrderProductStringDAO workOrderProductStringDAO = new WorkOrderProductStringDAO();

    private final ObservableList<Product> productObservableList = FXCollections.observableArrayList(productDAO.getList());

    private WorkOrder innerData;

    public void insertButton(ActionEvent actionEvent) {
        Product productInsertInsertData = productDAO.getDataByName(productComboBox.getValue()+"");
        WorkOrder workOrderInsertData = workOrderDAO.getDataByName(mainLabel.getText());
        if(emptyFieldDetected()) {
            utils.alert("err", Alert.AlertType.ERROR, "Xảy ra lỗi!", "Không được bỏ trống các trường bắt buộc (*)").showAndWait();
        } else {
            try {
                workOrderProductDAO.insert(workOrderInsertData.getId(), ordinalNumberField.getText()+"", productInsertInsertData.getId(), Float.parseFloat(qtyField.getText()), noteField.getText()+"");
                workOrderProductPackagingDAO.insertBasedOnWOPLatestData(workOrderInsertData.getId(), productInsertInsertData.getId(), Float.parseFloat(qtyField.getText()));
                clearFields();
                reload();
                utils.alert("info", Alert.AlertType.INFORMATION, null, null).showAndWait();
            } catch (NumberFormatException e) {
                utils.alert("err", Alert.AlertType.ERROR, null, null).showAndWait();
            }
        }
    }

    public void updateButton(ActionEvent actionEvent) {
        String productComboBoxValue = utils.getComboBoxValue(productComboBox);
        Product product = productDAO.getDataByName(productComboBoxValue);
        WorkOrderProductPackaging workOrderProductPackaging = workOrderProductPackagingDAO.getDataByID(productTableView.getSelectionModel().getSelectedItem().getId());
        if(emptyFieldDetected()) {
            utils.alert("err", Alert.AlertType.ERROR, "Xảy ra lỗi!", "Không được bỏ trống các trường bắt buộc (*)").showAndWait();
        } else {
            try {
                workOrderProductDAO.update(
                        this.innerData.getId(),
                        ordinalNumberField.getText()+"",
                        product.getId(),
                        Float.parseFloat(qtyField.getText()),
                        noteField.getText()+"",
                        workOrderProductPackaging.getWop_id()
                );
                clearFields();
                reload();
                utils.alert("info", Alert.AlertType.INFORMATION, null, null).showAndWait();
            } catch (NumberFormatException e) {
                utils.alert("err", Alert.AlertType.ERROR, null, null).showAndWait();
            }
        }
    }

    public void clearButton(ActionEvent actionEvent) {
        clearFields();
    }

    public void reloadData(ActionEvent actionEvent) {
        reload();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        filterComboBox();
        loadView();
    }

    /**
     * Handle validation on submitting
     * */
    private boolean emptyFieldDetected() {
        if(qtyField.getText().isEmpty()) {
            return true;
        }
        else if(ordinalNumberField.getText().isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Handle on filter Combobox
     * */
    private void filterComboBox() {
        productComboBox.getItems().addAll(productObservableList);
        filterProductComboBox();
    }

    /**
     * Handle on filter Product Combobox
     * */
    private void filterProductComboBox() {
        // Create the listener to filter the list as user enters search terms
        FilteredList<Product> dataFilteredList = new FilteredList<>(productObservableList, p-> true);

        // Add listener to our ComboBox textfield to filter the list
        productComboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            dataFilteredList.setPredicate(item -> {

                isUpdating = false;

                // Check if the search term is contained anywhere in our list
                if (item.getName().toLowerCase().contains(newValue.toLowerCase().trim()) && !isUpdating) {
                    productComboBox.show();
                    return true;
                }

                // No matches found
                return false;
            });

        });

        productComboBox.getItems().removeAll(productObservableList);
        productComboBox.getItems().addAll(dataFilteredList);
    }

    /**
     * Reset fields
     * This will handle an enter event and mouse leave for these Nodes
     * */
    private void resetFields() {
        utils.disableKeyEnterOnTextFieldComboBox(productComboBox, true);

        utils.disableKeyEnterOnTextField(qtyField);

        utils.inputNumberOnly(qtyField);
    }

    /**
     * Handle on resetting ComboBox
     * */
    private void resetComboBox() {
        Product productData = productDAO.getDataByID(1);
        utils.setComboBoxValue(productComboBox, productData.getName());
    }

    /**
     * Handle event on clearing all inputs
     * */
    private void clearFields() {
        resetComboBox();
        anchorLabel.setText("");
        anchorData.setText("");

        qtyField.setText("");

        searchField.setText("");

        setData(this.innerData);
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
     * Hiding the Combobox while on updating stage.
     * */
    private void hideComboBoxForUpdatingData() {
        if(isUpdating) {
            productComboBox.hide();
        }
    }

    /**
     * Setting context menu for table row
     * */
    private void setContextMenuProductTable() {
        productTableView.setRowFactory((TableView<WorkOrderProductString> tableView) -> {
            final TableRow<WorkOrderProductString> row = new TableRow<>();

            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem viewItem = new MenuItem("Chi tiết");
            final MenuItem editItem = new MenuItem("Cập nhật");
            final MenuItem removeItem = new MenuItem("Xóa dòng");

            viewItem.setOnAction((ActionEvent event) -> {
                WorkOrderProductString workOrderProductString = productTableView.getSelectionModel().getSelectedItem();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Chi tiết mặt hàng");
                alert.setHeaderText(workOrderProductString.getProductName());
                alert.setContentText(
                        "STT: " + workOrderProductString.getProductOrdinalNumber() + "\n" +
                                "Mặt hàng: " + workOrderProductString.getProductName() + "\n"
                );

                alert.showAndWait();
            });
            contextMenu.getItems().add(viewItem);

            editItem.setOnAction((ActionEvent event) -> {
                WorkOrderProductString workOrderProductString = productTableView.getSelectionModel().getSelectedItem();
                WorkOrderProduct workOrderProduct = workOrderProductDAO.getDataByID(workOrderProductString.getId());
                getData(workOrderProduct);
                hideComboBoxForUpdatingData();
            });
            contextMenu.getItems().add(editItem);

            removeItem.setOnAction((ActionEvent event) -> {
                WorkOrderProductString workOrderProductString = productTableView.getSelectionModel().getSelectedItem();
                Alert alert = utils.alert("info", Alert.AlertType.CONFIRMATION, "Xóa mặt hàng: "+workOrderProductString.getProductName(), null);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    workOrderProductDAO.delete(workOrderProductString.getId());
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
     * @param data - the packaging data
     * */
    private void getData(WorkOrderProduct data) {
        if(!isEditableComboBox) {
            Product product = productDAO.getDataByID(data.getProduct_id());
            productComboBox.getSelectionModel().select(product);
        } else {
            Product product = productDAO.getDataByID(data.getProduct_id());
            productComboBox.getEditor().setText(product.getName());
        }

        ordinalNumberField.setText(data.getOrdinal_num()+"");

        qtyField.setText(data.getQty()+"");
        anchorLabel.setText("Current ID: ");
        anchorData.setText(""+data.getId());
        this.isUpdating = true;
    }

    /**
     * Loading Product Table
     *
     * @param workOrderName name of work_order
     * */
    private void loadViewProduct(String workOrderName) {
        ObservableList<WorkOrderProductString> workProductionObservableListByName = FXCollections.observableArrayList(workOrderProductStringDAO.getListByName(workOrderName));

        productTableView.setEditable(true);
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productOrdinalNumber"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productTableView.setItems(workProductionObservableListByName);

        setContextMenuProductTable();

        productTableView.setOnMouseClicked((MouseEvent event) -> {
            if(event.getButton().equals(MouseButton.PRIMARY) && productTableView.getSelectionModel().getSelectedItem() != null){
                mainLabel.setText(this.innerData.getName() + " - ("+ productTableView.getSelectionModel().getSelectedItem().getProductOrdinalNumber() +") - "+productTableView.getSelectionModel().getSelectedItem().getProductName());
            }
        });
    }

    /**
     * Handle on searching data
     * */
    public void setSearchField() {
        ObservableList<WorkProduction> list = FXCollections.observableArrayList(workProductionDAO.getListByID(workOrderDAO.getDataByName(mainLabel.getText()).getId()));
        FilteredList<WorkProduction> filteredList = new FilteredList<>(list, p -> true);

        searchField.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    switch (searchComboBox.getValue()) {
                        case "Tên bao bì":
                            filteredList.setPredicate(str -> {
                                if (newValue == null || newValue.isEmpty())
                                    return true;
                                String lowerCaseFilter = newValue.toLowerCase();
                                return str.getProductName().toLowerCase().contains
                                        (lowerCaseFilter);
                            });
                            break;
                    }
                });

        searchComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal != null) {
                searchField.setText(null);
            }
        });

        SortedList<WorkProduction> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(dataTable.comparatorProperty());

        dataTable.setItems(sortedList);
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
    private void loadView() {
        resetFields();

        resetComboBox();

        dataTable.setEditable(true);

        idColumn.setSortable(false);
        idColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Integer>(dataTable.getItems().indexOf(column.getValue())+1));

        packagingNameColumn.setCellValueFactory(new PropertyValueFactory<>("packagingName"));
        packagingNameColumn.setCellValueFactory(new PropertyValueFactory<>("packagingName"));

//        setContextMenu();

        utils.reloadTableViewOnChange(dataTable, currentRow, currentCell);

        setSearchField();
    }

    public void setData(WorkOrder workOrder) {
        this.innerData = workOrder;
        mainLabel.setText(this.innerData.getName());
        loadViewProduct(this.innerData.getName());
        loadView();
    }
}
