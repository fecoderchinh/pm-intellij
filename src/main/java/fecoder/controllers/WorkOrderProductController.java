package fecoder.controllers;

import fecoder.DAO.*;
import fecoder.models.*;
import fecoder.utils.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.Units;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    public Button exportData;
    public Button importData;

    public TableView<WorkOrderProductString> productTableView;
    public TableColumn<WorkOrderProductString, String> productIdColumn;
    public TableColumn<WorkOrderProductString, String> productNameColumn;
    public TableColumn<WorkOrderProductString, Integer> productOrderTimesColumn;
    public TableColumn<WorkOrderProductString, Integer> productQuantityColumn;

    public TreeTableView<WorkProduction> dataTable;
    
    public Label anchorLabel;
    public Label anchorData;

    public TableView<OrderBySupllier> supplierShipToAddressTableView;
    public TableColumn<OrderBySupllier, Integer> ss2aIdColumn;
    public TableColumn<OrderBySupllier, String> suplierCodeColumn;
    public TableColumn<OrderBySupllier, ShipAddress> addressColumn;
    public TextField orderTimesField;
    public ComboBox<WorkOrderProduct> orderTimesComboBox;

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
    OrderBySupplierDAO orderBySupplierDAO = new OrderBySupplierDAO();
    SupplierDAO supplierDAO = new SupplierDAO();
    ShipAddressDAO shipAddressDAO = new ShipAddressDAO();

    private final ObservableList<Product> productObservableList = FXCollections.observableArrayList(productDAO.getList());
    private final ObservableList<ShipAddress> shipAddresses = FXCollections.observableArrayList(shipAddressDAO.getList());


    private WorkOrderToString innerData;

    TreeItem<WorkProduction> root;

    public WorkOrderProductController() {
    }

    public void insertButton(ActionEvent actionEvent) {
        Product productInsertData = productDAO.getDataByName(utils.getComboBoxValue(productComboBox));
        int fakeAutoIncrementID = workOrderProductDAO.getFakeAutoIncrementID().getId();
//        System.out.println(
//                fakeAutoIncrementID+1 + "\n" +
//                this.innerData.getId() + "\n" +
//                ordinalNumberField.getText()+""+ "\n" +
//                productInsertData.getId()+ "\n" +
//                Float.parseFloat(qtyField.getText())+ "\n" +
//                noteField.getText()+""+ "\n" +
//                shipAddressDAO.getDataByID(1).getId()+ "\n" +
//                orderTimesField.getText()
//        );
        if(emptyFieldDetected()) {
            utils.alert("err", Alert.AlertType.ERROR, "X???y ra l???i!", "Kh??ng ???????c b??? tr???ng c??c tr?????ng b???t bu???c (*)").showAndWait();
        } else {
            try {
                workOrderProductDAO.insert_wopp_children(fakeAutoIncrementID+1, this.innerData.getId(), ordinalNumberField.getText()+"", productInsertData.getId(), Float.parseFloat(qtyField.getText()), noteField.getText()+"", shipAddressDAO.getDataByID(1).getId(), !orderTimesField.getText().isEmpty() ? Integer.parseInt(orderTimesField.getText()) : 1);
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
            utils.alert("err", Alert.AlertType.ERROR, "X???y ra l???i!", "Kh??ng ???????c b??? tr???ng c??c tr?????ng b???t bu???c (*)").showAndWait();
        } else {
//            System.out.println(this.innerData.getId());
//            System.out.println(ordinalNumberField.getText()+"");
//            System.out.println(product.getId());
//            System.out.println(Float.parseFloat(qtyField.getText()));
//            System.out.println(workOrderProductPackaging.getWop_id());
            try {
                workOrderProductDAO.update_wopp_children(
                        Integer.parseInt(anchorData.getText()), // work_order_product.id
                        this.innerData.getId(), // work_order_product.work_order_id
                        ordinalNumberField.getText()+"", // work_order_product.ordinal_num
                        product.getId(), // work_order_product.product_id
                        Float.parseFloat(qtyField.getText()), // work_order_product.qty
                        noteField.getText()+"", // work_order_product.note
                        !orderTimesField.getText().isEmpty() ? Integer.parseInt(orderTimesField.getText()) : 1 // work_order_product.order_times
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

    public void setData(WorkOrderToString workOrder) {
        this.innerData = workOrder;
        mainLabel.setText(this.innerData.getName());
        mainLabel.setWrapText(true);
        root = TreeTableUtil.getModel(this.innerData.getId());
        loadViewProduct(this.innerData.getId());
        loadViewWOPP();
        loadSuppliers(this.innerData.getId());
        final ObservableList<WorkOrderProduct> obs_order_times = FXCollections.observableArrayList(workOrderProductDAO.getListByIDAndOrderTimes(this.innerData.getId()));
        if(orderTimesComboBox.getItems().size() == 0) {
            orderTimesComboBox.getItems().addAll(obs_order_times);
        }
        if(workOrderProductDAO.getListByID(this.innerData.getId()).size() > 0) {
            utils.setComboBoxValue(orderTimesComboBox, workOrderProductDAO.getListByID(this.innerData.getId()).get(0).getOrder_times() + "");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        filterComboBox();
        new AutoCompleteComboBoxListener<>(productComboBox, true, productDAO.getLastestData().getName());
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
//        filterProductComboBox();
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
        utils.disableKeyEnterOnTextFieldComboBox(productComboBox, true, "M???t h??ng");

        utils.disableKeyEnterOnTextField(qtyField);

        utils.inputNumberOnly(qtyField);
    }

    /**
     * Handle on resetting ComboBox
     * */
    private void resetComboBox() {
        Product productData = productDAO.getDataByID(3);
        utils.setComboBoxValue(productComboBox, productData.getName());
    }

    /**
     * Handle event on clearing all inputs
     * */
    private void clearFields() {
        resetComboBox();
        anchorLabel.setText("No ID Selected");
        anchorData.setText("");

        qtyField.setText("");

        orderTimesField.setText("");

        searchField.setText("");

        setData(this.innerData);
    }

    /**
     * Reloading method
     * */
    private void reload() {
        utils.reloadTreeTableViewOnChange(dataTable, currentRow, currentCell);
        utils.reloadTableViewOnChange(productTableView, currentRow, currentCell);
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
            final MenuItem viewItem = new MenuItem("Chi ti???t");
            final MenuItem editItem = new MenuItem("C???p nh???t");
            final MenuItem removeItem = new MenuItem("X??a d??ng");

            viewItem.setOnAction((ActionEvent event) -> {
                WorkOrderProductString workOrderProductString = productTableView.getSelectionModel().getSelectedItem();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Chi ti???t m???t h??ng");
                alert.setHeaderText(workOrderProductString.getProductName());
                alert.setContentText(
                        "STT: " + workOrderProductString.getProductOrdinalNumber() + "\n" +
                                "M???t h??ng: " + workOrderProductString.getProductName() + "\n" +
                                "S??? l?????ng: " + workOrderProductString.getProductQuantity() + "\n"
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
                Alert alert = utils.alert("info", Alert.AlertType.CONFIRMATION, "X??a m???t h??ng: "+workOrderProductString.getProductName(), null);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    workOrderProductDAO.delete(workOrderProductString.getId());
                    workOrderProductPackagingDAO.deleteWOPPChildren(workOrderProductString.getId());
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
        orderTimesField.setText(data.getOrder_times()+"");
        noteField.setText(data.getNote());

        qtyField.setText(data.getQty()+"");
        anchorLabel.setText("ID Selected: ");
        anchorData.setText(""+data.getId());
        this.isUpdating = true;
    }

    /**
     * Loading Product Table
     *
     * @param workOrderID name of work_order
     * */
    private void loadViewProduct(int workOrderID) {
        ObservableList<WorkOrderProductString> workProductionObservableListByName = FXCollections.observableArrayList(workOrderProductStringDAO.getListByWorkOrderID(workOrderID));

        productTableView.setEditable(true);
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productOrdinalNumber"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productOrderTimesColumn.setCellValueFactory(new PropertyValueFactory<>("orderTimes"));
        productQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("productQuantity"));
        productTableView.setItems(workProductionObservableListByName);

        setContextMenuProductTable();

        productTableView.setOnMouseClicked((MouseEvent event) -> {
            if(event.getButton().equals(MouseButton.PRIMARY) && productTableView.getSelectionModel().getSelectedItem() != null){
//                mainLabel.setText(this.innerData.getName() + " - ("+ productTableView.getSelectionModel().getSelectedItem().getProductOrdinalNumber() +") - "+productTableView.getSelectionModel().getSelectedItem().getProductName());
//                mainLabel.setWrapText(true);
            }
        });
    }

    private void filter(TreeItem<WorkProduction> root, String filter, TreeItem<WorkProduction> filteredRoot) {
        for (TreeItem<WorkProduction> child : root.getChildren()) {
            TreeItem<WorkProduction> filteredChild = new TreeItem<>();
            filteredChild.setValue(child.getValue());
            filteredChild.setExpanded(true);
            filter(child, filter, filteredChild );
            if (!filteredChild.getChildren().isEmpty() || isMatch(filteredChild.getValue(), filter)) {
//                System.out.println(filteredChild.getValue() + " matches.");
                filteredRoot.getChildren().add(filteredChild);
            }
        }
    }

    private boolean isMatch(WorkProduction value, String filter) {
        switch (searchComboBox.getValue()) {
            case "T??n bao b??":
                return value.getPackagingName().toLowerCase().contains
                        (filter.toLowerCase());
            case "K??ch th?????c":
                return value.getPackagingDimension().toLowerCase().trim().replace(" ", "").contains
                        (filter.toLowerCase().trim().replace(" ", ""));
        }
        return false;
    }

    private void filterChanged(String filter) {
        if (filter.isEmpty()) {
            dataTable.setRoot(root);
        } else {
            TreeItem<WorkProduction> filteredRoot = new TreeItem<>();
            filteredRoot.setExpanded(true);
            filter(root, filter, filteredRoot);
            dataTable.setRoot(filteredRoot);
        }
    }

    /**
     * Loading Work Order TreeTableView
     *
     * */
    private void loadViewWOPP() {
        setSearchFieldTreeItem();

        root.setExpanded(true);
        dataTable.setTableMenuButtonVisible(false);

        dataTable.setRoot(root);
        dataTable.setEditable(true);

        dataTable.getColumns().clear();

//        dataTable.getColumns().add(TreeTableUtil.getIdColumn());
        dataTable.getColumns().add(TreeTableUtil.getOrdinalNumberColumn());
//        dataTable.getColumns().add(TreeTableUtil.getWOIDColumn());
//        dataTable.getColumns().add(TreeTableUtil.getWorkOrderNameColumn());
//        dataTable.getColumns().add(TreeTableUtil.getProductNameColumn(dataTable));
        dataTable.getColumns().add(TreeTableUtil.getPackagingNameColumn(dataTable));
//        dataTable.getColumns().add(TreeTableUtil.getPackagingSpecificationColumn(dataTable));
        dataTable.getColumns().add(TreeTableUtil.getPackagingDimensionColumn(dataTable));
        dataTable.getColumns().add(TreeTableUtil.getPackagingSuplierColumn());
        dataTable.getColumns().add(TreeTableUtil.getPackagingCodeColumn());
        dataTable.getColumns().add(TreeTableUtil.getWorkOrderQuantityColumn());
        dataTable.getColumns().add(TreeTableUtil.getStockColumn(dataTable, this, innerData));
        dataTable.getColumns().add(TreeTableUtil.getActualQuantityColumn(dataTable, this, innerData));
        dataTable.getColumns().add(TreeTableUtil.getResidualQuantityColumn(dataTable, this, innerData));
//        dataTable.getColumns().add(TreeTableUtil.getTotalResidualQuantityColumn(dataTable));
        dataTable.getColumns().add(TreeTableUtil.getPrintStatusColumn(dataTable, this, innerData));
        dataTable.getColumns().add(TreeTableUtil.getPackagingCustomCodeColumn(dataTable, this, innerData));

//        setContextMenu();

        final PseudoClass topNode = PseudoClass.getPseudoClass("top-node");
        dataTable.setRowFactory(t -> {
            final TreeTableRow<WorkProduction> row = new TreeTableRow<>();

            // every time the TreeItem changes, check, if the new item is a
            // child of the root and set the pseudoclass accordingly
            row.treeItemProperty().addListener((o, oldValue, newValue) -> {
                boolean tn = false;
                if (newValue != null) {
                    tn = newValue.getParent() == root;
                }
                row.pseudoClassStateChanged(topNode, tn);
            });
            return row;
        });

        utils.reloadTreeTableViewOnChange(dataTable, currentRow, currentCell);
    }

    /**
     * Handle on searching data
     * */
    public void setSearchField() {
        ObservableList<WorkProduction> list = FXCollections.observableArrayList(workProductionDAO.getListByID(this.innerData.getId()));
        FilteredList<WorkProduction> filteredList = new FilteredList<>(list, p -> true);

        searchField.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    switch (searchComboBox.getValue()) {
                        case "T??n bao b??":
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
//        sortedList.comparatorProperty().bind(dataTable.comparatorProperty());
//
//        dataTable.setItems(sortedList);
    }

    /**
     * Handle on searching data
     * */
    public void setSearchFieldTreeItem() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterChanged(newValue));
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
        anchorLabel.setText("No ID Select");

        resetFields();

        resetComboBox();

//        dataTable.setEditable(true);

//        ObservableList<WorkProduction> workOrderList = FXCollections.observableArrayList(workProductionDAO.getWorkOrderList(workOrderDAO.getDataByName(mainLabel.getText()).getId()));
//        for (WorkProduction _workOrder : workOrderList) {
//            System.out.println(_workOrder.getWorkOrderName());
//            if(_workOrder.getWorkOrderName() != null) {
//                ObservableList<WorkProduction> productList = FXCollections.observableArrayList(workProductionDAO.getProductList(workOrderDAO.getDataByName(_workOrder.getWorkOrderName()).getId()));
//                for (WorkProduction _product : productList) {
//                    System.out.println(_product.getProductName());
//                    ObservableList<WorkProduction> packagingList = FXCollections.observableArrayList(workProductionDAO.getPackagingList(productDAO.getDataByName(_product.getProductName()).getId()));
//                    for (WorkProduction _packaging : packagingList) {
//                        System.out.println(_packaging.getPackagingName() + " | " + _packaging.getPackagingSuplier());
//                    }
//                }
//            }
//        }
    }

    private void loadSuppliers(int id) {

        ObservableList<OrderBySupllier> orderObservableList = FXCollections.observableArrayList(orderBySupplierDAO.getList(id+""));

        supplierShipToAddressTableView.setEditable(true);

        ss2aIdColumn.setSortable(false);
        ss2aIdColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Integer>(supplierShipToAddressTableView.getItems().indexOf(column.getValue())+1));

        suplierCodeColumn.setCellValueFactory(new PropertyValueFactory<>("sCode"));
        suplierCodeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<OrderBySupllier, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<OrderBySupllier, String> orderBySupllierStringCellDataFeatures) {
                return new SimpleStringProperty(orderBySupllierStringCellDataFeatures.getValue().getsCode());
            }
        });

        addressColumn.setCellValueFactory(new PropertyValueFactory<>("shipAddress"));
        addressColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<OrderBySupllier, ShipAddress>, ObservableValue<ShipAddress>>() {
            @Override
            public ObservableValue<ShipAddress> call(TableColumn.CellDataFeatures<OrderBySupllier, ShipAddress> orderBySupllierShipAddressCellDataFeatures) {
                return new SimpleObjectProperty<>(shipAddressDAO.getDataByCode(orderBySupllierShipAddressCellDataFeatures.getValue().getShipAddress()));
            }
        });
        addressColumn.setCellFactory(ComboBoxTableCell.forTableColumn(shipAddresses));
        addressColumn.setOnEditCommit(event -> {
            final ShipAddress data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((OrderBySupllier) event.getTableView().getItems().get(event.getTablePosition().getRow())).setShipAddress(data.getCode_address());
            workOrderProductPackagingDAO.updateDataInteger("ship_address", data.getId(), event.getRowValue().getWoppID());
            dataTable.refresh();
        });

        supplierShipToAddressTableView.setItems(orderObservableList);
    }

    public void exportData(ActionEvent actionEvent) throws IOException {
//        data2DocOfOrderList((Stage)((Node) actionEvent.getSource()).getScene().getWindow());

        utils.openListCheckboxWindow((Stage)((Node) actionEvent.getSource()).getScene().getWindow(), nv -> {
            try {
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Word document (*.docx)", "*.docx");
                fileChooser.getExtensionFilters().add(extensionFilter);

                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Select folder");

//            File file = fileChooser.showSaveDialog(window);
                File file = directoryChooser.showDialog((Stage)((Node) actionEvent.getSource()).getScene().getWindow());

                if(file != null) {
                    ObservableList<OrderBySupllier> orderBySuplliers = FXCollections.observableArrayList(orderBySupplierDAO.getList(this.innerData.getId()+""));

                    LocalDateTime now = LocalDateTime.now();
                    workOrderDAO.updateData("order_date", now.getDayOfMonth()+"/"+now.getMonthValue()+"/"+now.getYear(), this.innerData.getId()+"");

                    if(nv.get(1)) {
                        for (OrderBySupllier orderBySupllier : orderBySuplliers) {
                            ExportWordDocument.data2DocOfOrderBySupplier(file, this.innerData.getId()+"", orderBySupllier.getsCode(), orderBySupllier.getShipAddress(), now.toString(), Integer.parseInt(utils.getComboBoxValue(orderTimesComboBox)));
                        }
                    }

                    WorkOrder workOrder = workOrderDAO.getDataByID(this.innerData.getId());
                    if(nv.get(0)) ExportWordDocument.data2WorksheetOfOrderListDraft(file, workOrder, now.toString());

                    if(nv.get(2)) ExportWordDocument.data2DocOfOrderList(file, this.innerData.getId()+"", now.toString(), Integer.parseInt(utils.getComboBoxValue(orderTimesComboBox)));
                    utils.alert("info", Alert.AlertType.INFORMATION, "Xu???t file th??nh c??ng!", "File ???? ???????c l??u v??o ???????ng d???n " +file.getPath()).showAndWait();
                }

            } catch (Exception ex){
                utils.alert("err", Alert.AlertType.ERROR, "L???i", ex.getMessage()).showAndWait();
            }
        }, "T??y ch???n");
    }

    private void helloWord() {
        String fileName = "e:\\java_platform\\helloWord.docx";

        try {

            XWPFDocument doc = new XWPFDocument();

            // create a paragraph
            XWPFParagraph p1 = doc.createParagraph();
            p1.setAlignment(ParagraphAlignment.CENTER);

            // set font
            XWPFRun r1 = p1.createRun();
            r1.setBold(true);
            r1.setItalic(true);
            r1.setFontSize(22);
            r1.setFontFamily("New Roman");
            r1.setText("I am first paragraph.");

            // save it to .docx file
            try (FileOutputStream out = new FileOutputStream(fileName)) {
                doc.write(out);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void docStyle() {
        String fileName = "e:\\java_platform\\docStyle.docx";

        try {

            XWPFDocument doc = new XWPFDocument();

            XWPFParagraph p1 = doc.createParagraph();
            p1.setAlignment(ParagraphAlignment.CENTER);

            // Set Text to Bold and font size to 22 for first paragraph
            XWPFRun r1 = p1.createRun();
            r1.setBold(true);
            r1.setItalic(true);
            r1.setFontSize(22);
            r1.setText("I am first paragraph. My Text is bold, italic, Courier and capitalized");
            r1.setFontFamily("Courier");

            XWPFParagraph p2 = doc.createParagraph();
            //Set color for second paragraph
            XWPFRun r2 = p2.createRun();
            r2.setText("I am second paragraph. My Text is Red in color and is embossed");
            r2.setColor("ff0000");
            r2.setEmbossed(true);

            XWPFParagraph p3 = doc.createParagraph();
            //Set strike for third paragraph and capitalization
            XWPFRun r3 = p3.createRun();
            r3.setStrikeThrough(true);
            r3.setCapitalized(true);
            r3.setText("I am third paragraph. My Text is strike through and is capitalized");

            XWPFParagraph p4 = doc.createParagraph();
            p4.setWordWrapped(true);
            p4.setPageBreak(true);  // new page break
            p4.setIndentationFirstLine(600);

            XWPFRun r4 = p4.createRun();
            r4.setFontSize(40);
            r4.setItalic(true);
            //r4.setTextPosition(100);
            r4.setText("Line 1");
            r4.addBreak();
            r4.setText("Line 2");
            r4.addBreak();
            r4.setText("Line 3");

            // save it to .docx file
            try (FileOutputStream out = new FileOutputStream(fileName)) {
                doc.write(out);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void docHeader() {
        String fileName = "e:\\java_platform\\docHeader.docx";

        try {

            XWPFDocument doc = new XWPFDocument();

            XWPFParagraph p = doc.createParagraph();
            XWPFRun r = p.createRun();
            r.setBold(true);
            r.setFontSize(30);
            r.setText("Create document header and footer!");

            // next page
            XWPFParagraph p2 = doc.createParagraph();
            p2.setWordWrapped(true);
            p2.setPageBreak(true);  // new page break

            XWPFRun r2 = p2.createRun();
            r2.setFontSize(40);
            r2.setItalic(true);
            r2.setText("New Page");

            // document header and footer
            XWPFHeader head = doc.createHeader(HeaderFooterType.DEFAULT);
            head.createParagraph()
                    .createRun()
                    .setText("This is document header");

            XWPFFooter foot = doc.createFooter(HeaderFooterType.DEFAULT);
            foot.createParagraph()
                    .createRun()
                    .setText("This is document footer");

            // save it to .docx file
            try (FileOutputStream out = new FileOutputStream(fileName)) {
                doc.write(out);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void docImage() {
        String imgFile = "e:\\java_platform\\google.png";
        String fileName = "e:\\java_platform\\docImage.docx";

        try {

            XWPFDocument doc = new XWPFDocument();

            XWPFParagraph p = doc.createParagraph();
            XWPFRun r = p.createRun();
            r.setText(imgFile);
            r.addBreak();

            // add png image
            try (FileInputStream is = new FileInputStream(imgFile)) {
                r.addPicture(is,
                        Document.PICTURE_TYPE_PNG,    // png file
                        imgFile,
                        Units.toEMU(400),
                        Units.toEMU(200));            // 400x200 pixels
            }

            // save it to .docx file
            try (FileOutputStream out = new FileOutputStream(fileName)) {
                doc.write(out);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void docTable() {
        String imgFile = "e:\\java_platform\\google.png";
        String fileName = "e:\\java_platform\\docTable.docx";

        try {

            XWPFDocument doc = new XWPFDocument();

            XWPFTable table = doc.createTable();

            //Creating first Row
            XWPFTableRow row1 = table.getRow(0);
            row1.getCell(0).setText("First Row, First Column");
            row1.addNewTableCell().setText("First Row, Second Column");
            row1.addNewTableCell().setText("First Row, Third Column");

            //Creating second Row
            XWPFTableRow row2 = table.createRow();
            row2.getCell(0).setText("Second Row, First Column");
            row2.getCell(1).setText("Second Row, Second Column");
            row2.getCell(2).setText("Second Row, Third Column");

            //create third row
            XWPFTableRow row3 = table.createRow();
            row3.getCell(0).setText("Third Row, First Column");
            row3.getCell(1).setText("Third Row, Second Column");
            row3.getCell(2).setText("Third Row, Third Column");

            // save it to .docx file
            try (FileOutputStream out = new FileOutputStream(fileName)) {
                doc.write(out);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void docRead() {
        String fileName = "e:\\java_platform\\test.docx";

        try (XWPFDocument doc = new XWPFDocument(
                Files.newInputStream(Paths.get(fileName)))) {

            XWPFWordExtractor xwpfWordExtractor = new XWPFWordExtractor(doc);
            String docText = xwpfWordExtractor.getText();
            System.out.println(docText);

            // find number of words in the document
            long count = Arrays.stream(docText.split("\\s+")).count();
            System.out.println("Total words: " + count);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void docUpdate() throws IOException {
        UpdateDocument obj = new UpdateDocument();

        obj.updateDocument(
                "e:\\java_platform\\test_input.docx",
                "e:\\java_platform\\test_output.docx",
                new String[]{"chinh", "123", "aaa"});
    }

    private void data2Doc(File file) {
        String imgFile = "e:\\java_platform\\docs-data\\logo.jpg";
        String _fontFamily = "Arial";

        OrderBySupplierDAO orderBySupplierDAO = new OrderBySupplierDAO();
        ObservableList<OrderBySupllier> orderObservableList = FXCollections.observableArrayList(orderBySupplierDAO.getList(this.innerData.getId()+""));

        try {
            XWPFDocument doc = new XWPFDocument();
            XWPFParagraph paragraph;
            XWPFRun run;
            XWPFTable table;
            XWPFTableRow row;

            /*
            * Word Header
            * */
            XWPFHeader header = doc.createHeader(HeaderFooterType.DEFAULT);

            table = header.createTable(1, 3);
            table.setWidth("100%");
            table.removeBorders();
            row = table.getRow(0);
            row.getCell(0).setWidth("25%");
            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(1).setWidth("55%");
            row.getCell(1).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);
            row.getCell(2).setWidth("20%");
            row.getCell(1).getParagraphs().get(0).setAlignment(ParagraphAlignment.RIGHT);

            paragraph = row.getCell(0).getParagraphArray(0);
            paragraph.setVerticalAlignment(TextAlignment.CENTER);
            run = paragraph.createRun();
            // add png image
            try (FileInputStream is = new FileInputStream(imgFile)) {
                run.addPicture(is,
                        Document.PICTURE_TYPE_PNG,    // png file
                        imgFile,
                        Units.toEMU(100),
                        Units.toEMU(45));            // 100x35 pixels
            }
            utils.setHeaderRowforSingleCell(row.getCell(1), "C??ng ty CP SEAVINA", 10, true, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
//            utils.setHeaderRowforSingleCell(row.getCell(1), "L?? 16A-18, KCN Tr?? N??c I,P.Tr?? N??c, Q. B??nh Th???y, TP. C???n Th?? ,Vi???t Nam", 10, true, true, ParagraphAlignment.CENTER);
//            utils.setHeaderRowforSingleCell(row.getCell(1), "1801141886", 10, true, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(1), "Tel: 0292.3744950  Fax: 0292.3743678", 10, true, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
//            utils.setHeaderRowforSingleCell(row.getCell(1), "(Ms. Nhung: 0946.886 868, Ms Trinh: 0918.755729)", 10, false, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(2), "TT5.6.1/ KD2-BM3", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);

            /*
            * Word content title
            * */

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.RIGHT);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            run.setText("SEAVINA, ng??y " + now.getDayOfMonth() + ", th??ng " + now.getMonthValue() + ", n??m "+ now.getYear());
            run.addBreak();

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
            run.setBold(true);
            run.setFontSize(14);
            run.setText("????? NGH??? BAO B?? "+this.innerData.getName());
            run.addBreak();

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            paragraph.setSpacingLineRule(LineSpacingRule.AUTO);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
//            run.setBold(true);
            run.setFontSize(10);
            run.setText("K??nh g???i: Ph??ng Mua H??ng Minh T??m");
            run.addBreak();
            run.setText("??i???n tho???i: 02923. 3600063");
            run.addBreak();
            run.setText("C??ng ty CP SEAVINA (Ph??ng Kinh Doanh) xin g???i Phi???u ????? ngh??? mua h??ng v???i chi ti???t nh?? sau:");

            table = doc.createTable(1, 7);
            table.setWidth("100%");

            row = table.getRow(0);
            row.getCell(0).setWidth("5%");
            row.getCell(1).setWidth("35%");
            row.getCell(2).setWidth("15%");
            row.getCell(3).setWidth("8%");
            row.getCell(4).setWidth("12%");
            row.getCell(5).setWidth("10%");
            row.getCell(6).setWidth("15%");
            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(3).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(4).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(5).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(6).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

            utils.setHeaderRowforSingleCell(row.getCell(0), "STT", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), "T??n Bao B??", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(2), "Qui c??ch (CM)", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(3), "??VT", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(4), "SL ?????t", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(5), "Ghi ch??", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(6), "LSX", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);

            DecimalFormat formatter = new DecimalFormat("#,###");

            table = doc.createTable(orderObservableList.size(), 7);
            table.setWidth("100%");

            for(int i=0;i<orderObservableList.size();i++) {
                row = table.getRow(i);
                row.getCell(0).setWidth("5%");
                row.getCell(1).setWidth("35%");
                row.getCell(2).setWidth("15%");
                row.getCell(3).setWidth("8%");
                row.getCell(4).setWidth("12%");
                row.getCell(5).setWidth("10%");
                row.getCell(6).setWidth("15%");
                row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(3).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(4).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(5).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(6).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                float residualNumber = Float.parseFloat(orderObservableList.get(i).getpTotal()+"") + Float.parseFloat(orderObservableList.get(i).getpStock()+"") - Float.parseFloat(orderObservableList.get(i).getpResidualQuantity()+"") - Float.parseFloat(orderObservableList.get(i).getpDesireQuantity()+"");

                utils.setHeaderRowforSingleCell(row.getCell(0), (i+1)+"", 10, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                utils.setHeaderRowforSingleCell(row.getCell(1), orderObservableList.get(i).getpName() + " ("+ orderObservableList.get(i).getpSpecs() +")", 10, false, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
                utils.setHeaderRowforSingleCell(row.getCell(2), orderObservableList.get(i).getpDimension(), 10, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                utils.setHeaderRowforSingleCell(row.getCell(3), orderObservableList.get(i).getpUnit(), 10, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                utils.setHeaderRowforSingleCell(row.getCell(4), formatter.format(Float.parseFloat(orderObservableList.get(i).getpTotal()+""))+"", 10, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                utils.setHeaderRowforSingleCell(row.getCell(5), residualNumber > 0 ? "D?? "+formatter.format(residualNumber) : "", 10, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                utils.setHeaderRowforSingleCell(row.getCell(6), orderObservableList.get(i).getWoName(), 10, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            }

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            paragraph.setSpacingLineRule(LineSpacingRule.AUTO);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
            run.setFontSize(10);
            run.setText("L??u ??: M???i thay ?????i hay c?? v???n ????? ch??a r?? ph???i ph???n h???i l???i v???i b??? ph???n li??n quan tr?????c khi th???c hi???n ????? tr??nh sai s??t c?? th??? x???y ra.");
            run.addBreak();

            table = doc.createTable(1, 2);
            table.setWidth("100%");
            table.removeBorders();
            row = table.getRow(0);
            row.getCell(0).setWidth("50%");
            row.getCell(1).setWidth("50%");
            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

            utils.setHeaderRowforSingleCell(row.getCell(0), "Ph??ng Kinh Doanh", 10, false, true, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), "Ng?????i L???p Bi???u", 10, false, true, ParagraphAlignment.RIGHT, UnderlinePatterns.NONE);

            if(orderObservableList.size() > 0) {
                if(file != null) {
                    // save it to .docx file
//                try (FileOutputStream out = new FileOutputStream(file.getPath())) // for fileChooser
                    try (FileOutputStream out = new FileOutputStream(file.getPath()+"/DE NGHI BB "+ this.innerData.getName() +".docx"))
                    {
                        doc.write(out);
                        utils.alert("info", Alert.AlertType.INFORMATION, "Xu???t file th??nh c??ng!", "File ???? ???????c l??u v??o ??? ????a!").showAndWait();
                    }
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void importData(ActionEvent actionEvent) {
        try {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Excel Workbook (*.xls)", "*.xls");
            fileChooser.getExtensionFilters().add(extensionFilter);

            File file = fileChooser.showOpenDialog((Stage)((Node) actionEvent.getSource()).getScene().getWindow());

            if(file != null) {
                FileInputStream fis = new FileInputStream(file.getPath());
                HSSFWorkbook wb = new HSSFWorkbook(fis);
                HSSFSheet sheet = wb.getSheetAt(0);
                Row row;
                Row top = sheet.getRow(0);

                if((int) Double.parseDouble(String.valueOf(top.getCell(25))) == this.innerData.getId()) {
                    for(int i=2; i<=sheet.getLastRowNum(); i++) {
                        row = sheet.getRow(i);

                        boolean cell_0_null = (row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK);
                        boolean cell_1_null = (row.getCell(1) == null || row.getCell(1).getCellType() == CellType.BLANK);
                        boolean cell_7_null = (row.getCell(7) == null || row.getCell(7).getCellType() == CellType.BLANK);
                        boolean cell_8_null = (row.getCell(8) == null || row.getCell(8).getCellType() == CellType.BLANK);
                        boolean cell_9_null = (row.getCell(9) == null || row.getCell(9).getCellType() == CellType.BLANK);

                        boolean str_cell_7_empty = String.valueOf(row.getCell(7)).isEmpty();
                        boolean str_cell_8_empty = String.valueOf(row.getCell(8)).isEmpty();
                        boolean str_cell_9_empty = String.valueOf(row.getCell(9)).isEmpty();

                        float stock = cell_7_null ? 0 : (str_cell_7_empty ? 0 : (float) Double.parseDouble(String.valueOf(row.getCell(7)).replaceAll(",", "")) );
                        float actual_qty = cell_8_null ? 0 : (str_cell_8_empty ? 0 : (float) Double.parseDouble(String.valueOf(row.getCell(8)).replaceAll(",", "")) );
                        String printed = cell_9_null ? "" : (str_cell_9_empty ? "" : String.valueOf(row.getCell(9)) );

                        if(!cell_0_null || cell_1_null) {
                            continue;
                        }

                        int id = (int) Double.parseDouble(String.valueOf(row.getCell(1)));

//                        System.out.println(
//                            "KT ID: "+cell_1_null + " => "+ id +"\n"+
//                            "KT T???n: "+ cell_7_null + " => "+ stock +"\n"+
//                            "KT Th???c ?????t: "+ cell_8_null + " => "+ actual_qty +"\n"+
//                            "KT In s???n: "+ cell_9_null + " => "+ printed
//                        );

//                        float stock = !cell_7_null ? (float) Double.parseDouble(String.valueOf(row.getCell(7)).replaceAll(",", "")) : 0;
//                        float actual_qty = !cell_8_null ? (float) Double.parseDouble(String.valueOf(row.getCell(8)).replaceAll(",", "")) : 0;
//                        String printed = !cell_9_null ? String.valueOf(row.getCell(9)) : "";
//                        int wopp_id = (int) Double.parseDouble(String.valueOf(row.getCell(1)));
//
//                        System.out.println(stock +", "+actual_qty+", "+printed+", "+wopp_id);

                        workOrderProductPackagingDAO.updateDataFromSheet(stock, actual_qty, printed, id);

//                        System.out.println("----------------------------------------------------------------");
                    }
                } else {
                    utils.alert("err", Alert.AlertType.ERROR, "L???i", "L???i, nh???p kh??ng ????ng ?????nh d???ng! H??? th???ng ch??? h??? tr??? nh???p Data t??? file nh??p (KIEM TRA BB "+ this.innerData.getName() +".xls) ???? xu???t tr?????c ????. Vui l??ng gi??? nguy??n ?????nh d???ng v?? th??? l???i.").showAndWait();
                }
                reload();
            }
        } catch (Exception ex){
            utils.alert("err", Alert.AlertType.ERROR, "L???i", ex.getMessage()).showAndWait();
        }
    }

//    private void data2DocOfOrderBySupplier(File file, String code) {
//        String imgFile = "e:\\java_platform\\docs-data\\logo.jpg";
//        String _fontFamily = "Arial";
//        ObservableList<OrderBySupllier> orderObservableList = FXCollections.observableArrayList(orderBySupplierDAO.getListBySupplierCode(this.innerData.getId()+"", code));
//        Supplier supplier = supplierDAO.getDataByCode(code);
//
//        try {
//            XWPFDocument doc = new XWPFDocument();
//            XWPFParagraph paragraph;
//            XWPFRun run;
//            XWPFTable table;
//            XWPFTableRow row;
//
//            /*
//             * Word Header
//             * */
//            XWPFHeader header = doc.createHeader(HeaderFooterType.DEFAULT);
//
//            table = header.createTable(1, 3);
//            table.setWidth("100%");
//            table.removeBorders();
//            row = table.getRow(0);
//            row.getCell(0).setWidth("22%");
//            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
//            row.getCell(1).setWidth("55%");
//            row.getCell(1).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);
//            row.getCell(2).setWidth("23%");
//            row.getCell(1).getParagraphs().get(0).setAlignment(ParagraphAlignment.RIGHT);
//
//            paragraph = row.getCell(0).getParagraphArray(0);
//            paragraph.setVerticalAlignment(TextAlignment.CENTER);
//            run = paragraph.createRun();
//            // add png image
//            try (FileInputStream is = new FileInputStream(imgFile)) {
//                run.addPicture(is,
//                        Document.PICTURE_TYPE_PNG,    // png file
//                        imgFile,
//                        Units.toEMU(100),
//                        Units.toEMU(45));            // 100x35 pixels
//            }
//            utils.setHeaderRowforSingleCell(row.getCell(1), "C??ng ty CP SEAVINA", 10, true, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
//            utils.setHeaderRowforSingleCell(row.getCell(1), "L?? 16A-18, KCN Tr?? N??c I,P.Tr?? N??c, Q. B??nh Th???y, TP. C???n Th?? ,Vi???t Nam", 10, true, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
//            utils.setHeaderRowforSingleCell(row.getCell(1), "1801141886", 10, true, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
//            utils.setHeaderRowforSingleCell(row.getCell(1), "Tel: 0292.3744950  Fax: 0292.3743678", 10, true, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
//            utils.setHeaderRowforSingleCell(row.getCell(1), "(Ms. Nhung: 0946.886 868, Ms Trinh: 0918.755729)", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
//            utils.setHeaderRowforSingleCell(row.getCell(2), "TT5.6.1/ KD2-BM3", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
//
//            /*
//             * Word content title
//             * */
//
//            paragraph = doc.createParagraph();
//            paragraph.setAlignment(ParagraphAlignment.RIGHT);
//            run = paragraph.createRun();
//            run.setFontFamily(_fontFamily);
//
//            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
//            LocalDateTime now = LocalDateTime.now();
//
//            run.setText("SEAVINA, ng??y " + now.getDayOfMonth() + ", th??ng " + now.getMonthValue() + ", n??m "+ now.getYear());
//            run.addBreak();
//
//            paragraph = doc.createParagraph();
//            paragraph.setAlignment(ParagraphAlignment.CENTER);
//            run = paragraph.createRun();
//            run.setFontFamily(_fontFamily);
//            run.setBold(true);
//            run.setFontSize(10);
//            run.setText("????N ?????T H??NG");
//            run.addBreak();
//            run.setText("(S???: "+code+".\t\t/"+now.getYear()+")");
//            run.addBreak();
//
//            paragraph = doc.createParagraph();
//            paragraph.setAlignment(ParagraphAlignment.LEFT);
//            paragraph.setSpacingLineRule(LineSpacingRule.AUTO);
//            run = paragraph.createRun();
//            run.setFontFamily(_fontFamily);
////            run.setBold(true);
//            run.setFontSize(10);
//            run.setText("K??nh g???i: "+supplier.getName());
//            run.addBreak();
//            run.setText("?????i di???n: "+supplier.getDeputy());
//            run.addBreak();
//            run.setText("?????a ch???: "+supplier.getAddress());
//            run.addBreak();
//            run.setText("??i???n tho???i: "+supplier.getPhone() + "\t\t"+"Fax: "+supplier.getFax());
//            run.addBreak();
//            run.setText("C??ng ty CP SEAVINA xin g???i ????N ?????T H??NG ?????n Qu?? C??ng Ty v???i chi ti???t nh?? sau:");
//
//            table = doc.createTable(1, 7);
//            table.setWidth("100%");
//
//            row = table.getRow(0);
//            row.getCell(0).setWidth("6%");
//            row.getCell(1).setWidth("30%");
//            row.getCell(2).setWidth("15%");
//            row.getCell(3).setWidth("10%");
//            row.getCell(4).setWidth("12%");
//            row.getCell(5).setWidth("12%");
//            row.getCell(6).setWidth("15%");
//            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
//            row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
//            row.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
//            row.getCell(3).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
//            row.getCell(4).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
//            row.getCell(5).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
//            row.getCell(6).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
//
//            utils.setHeaderRowforSingleCell(row.getCell(0), "STT", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
//            utils.setHeaderRowforSingleCell(row.getCell(1), "T??n Bao B??", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
//            utils.setHeaderRowforSingleCell(row.getCell(2), "Qui c??ch (CM)", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
//            utils.setHeaderRowforSingleCell(row.getCell(3), "??VT", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
//            utils.setHeaderRowforSingleCell(row.getCell(4), "SL ?????t", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
//            utils.setHeaderRowforSingleCell(row.getCell(5), "M??", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
//            utils.setHeaderRowforSingleCell(row.getCell(6), "LSX", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
//
//            DecimalFormat formatter = new DecimalFormat("#,###");
//
//            table = doc.createTable(orderObservableList.size(), 7);
//            table.setWidth("100%");
//
//            for(int i=0;i<orderObservableList.size();i++) {
//                row = table.getRow(i);
//                row.getCell(0).setWidth("6%");
//                row.getCell(1).setWidth("30%");
//                row.getCell(2).setWidth("15%");
//                row.getCell(3).setWidth("10%");
//                row.getCell(4).setWidth("12%");
//                row.getCell(5).setWidth("12%");
//                row.getCell(6).setWidth("15%");
//                row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
//                row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
//                row.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
//                row.getCell(3).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
//                row.getCell(4).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
//                row.getCell(5).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
//                row.getCell(6).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
//
//                utils.setHeaderRowforSingleCell(row.getCell(0), (i+1)+"", 10, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
//                utils.setHeaderRowforSingleCell(row.getCell(1), orderObservableList.get(i).getpName(), 10, false, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
//                utils.setHeaderRowforSingleCell(row.getCell(1), " ("+ orderObservableList.get(i).getpSpecs() +")", 10, false, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
//                utils.setHeaderRowforSingleCell(row.getCell(2), orderObservableList.get(i).getpDimension(), 10, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
//                utils.setHeaderRowforSingleCell(row.getCell(3), orderObservableList.get(i).getpUnit(), 10, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
//                utils.setHeaderRowforSingleCell(row.getCell(4), formatter.format(Float.parseFloat(orderObservableList.get(i).getpTotal()+""))+"", 10, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
//                utils.setHeaderRowforSingleCell(row.getCell(5), orderObservableList.get(i).getpCode(), 10, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
//                utils.setHeaderRowforSingleCell(row.getCell(6), orderObservableList.get(i).getWoName(), 10, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
//            }
//
//            table = doc.createTable(4, 2);
//            table.setWidth("100%");
//            utils.spanCellsAcrossRow(table, 0,0,2);
//            utils.spanCellsAcrossRow(table, 0,1,6);
//            utils.spanCellsAcrossRow(table, 1,0,2);
//            utils.spanCellsAcrossRow(table, 1,1,6);
//            utils.spanCellsAcrossRow(table, 2,0,2);
//            utils.spanCellsAcrossRow(table, 2,1,6);
//            utils.spanCellsAcrossRow(table, 3,0,2);
//            utils.spanCellsAcrossRow(table, 3,1,6);
//            row = table.getRow(0);
//            row.getCell(0).setWidth("40%");
//            row.getCell(1).setWidth("60%");
//            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
//            row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
//
//            utils.setHeaderRowforSingleCell(row.getCell(0), "?????c ??i???m v?? qui c??ch", 10, false, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
//            utils.setHeaderRowforSingleCell(row.getCell(1), "- Ch???t l?????ng: ?????m b???o ????ng ch???t l?????ng v?? ?????nh l?????ng bao b?? nh?? m???u ch??o h??ng.", 10, true, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
//            utils.setHeaderRowforSingleCell(row.getCell(1), "- S???n xu???t theo m???u x??c nh???n ng??y: " + now.getDayOfMonth() + "/" + now.getMonthValue() + "/"+ now.getYear(), 10, true, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
//            utils.setHeaderRowforSingleCell(row.getCell(1), "- Bao b?? ph???i l??m ????ng k??ch th?????c, m??u s???c, th??ng tin nh?? ???? x??c nh???n.", 10, true, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
//            utils.setHeaderRowforSingleCell(row.getCell(1), "- Bao b?? ph???i ?????t ti??u chu???n h??ng Th???y s???n xu???t kh???u. S??? l?????ng l??m ?????, kh??ng th???a, kh??ng thi???u.", 10, true, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
//            utils.setHeaderRowforSingleCell(row.getCell(1), "- Ch??ng t??i s??? tr??? l???i c??c l?? h??ng l??m sai qui c??ch v?? kh??ng ????ng c??c y??u c???u tr??n.", 10, true, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
//            utils.setHeaderRowforSingleCell(row.getCell(1), "- Khi giao h??ng vui l??ng li??n h???: Mr. Duy (0932.830.900).", 10, false, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
//
//            row = table.getRow(1);
//            utils.setHeaderRowforSingleCell(row.getCell(0), "Ng??y giao h??ng", 10, false, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
//
//            row = table.getRow(2);
//            utils.setHeaderRowforSingleCell(row.getCell(0), "?????a ch??? giao h??ng", 10, false, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
//            utils.setHeaderRowforSingleCell(row.getCell(1), "C??ng ty CP SEAVINA", 10, true, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
//            utils.setHeaderRowforSingleCell(row.getCell(1), "L?? 16A-18, KCN Tr?? N??c 1, P. Tr?? N??c, Q. B??nh Th???y, TP. C???n Th??", 10, false, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
//
//            row = table.getRow(3);
//            utils.setHeaderRowforSingleCell(row.getCell(0), "Ch?? ??", 10, false, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
//            utils.setHeaderRowforSingleCell(row.getCell(1), "M???i thay ?????i ho???c c?? v???n ????? g?? ch??a r?? ph???i b??o l???i ngay v???i C??ng Ty CP SEAVINA tr?????c khi ti???n h??nh.", 10, false, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
//
//            paragraph = doc.createParagraph();
//            paragraph.setAlignment(ParagraphAlignment.LEFT);
//            paragraph.setSpacingLineRule(LineSpacingRule.AUTO);
//            run = paragraph.createRun();
//            run.setFontFamily(_fontFamily);
//            run.setFontSize(10);
//            run.addBreak();
//
//            table = doc.createTable(1, 2);
//            table.setWidth("100%");
//            table.removeBorders();
//            row = table.getRow(0);
//            row.getCell(0).setWidth("50%");
//            row.getCell(1).setWidth("50%");
//            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
//            row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
//
//            utils.setHeaderRowforSingleCell(row.getCell(0), "B??n b??n h??ng", 10, false, true, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
//            utils.setHeaderRowforSingleCell(row.getCell(1), "B??n mua h??ng", 10, false, true, ParagraphAlignment.RIGHT, UnderlinePatterns.NONE);
//
//
//            if(file != null) {
//                // save it to .docx file
////                try (FileOutputStream out = new FileOutputStream(file.getPath())) // for fileChooser
//                try (FileOutputStream out = new FileOutputStream(file.getPath()+"/"+ code + "-"+this.innerData.getName()+".docx"))
//                {
//                    doc.write(out);
////                    utils.alert("info", Alert.AlertType.INFORMATION, "Xu???t file th??nh c??ng!", "File ???? ???????c l??u v??o ??? ????a!").showAndWait();
//                }
//            }
//
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//    }
}
