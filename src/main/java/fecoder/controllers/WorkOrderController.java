package fecoder.controllers;

import fecoder.DAO.*;
import fecoder.models.*;
import fecoder.utils.AutoCompleteComboBoxListener;
import fecoder.utils.ExportWordDocument;
import fecoder.utils.Utils;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.*;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.apache.poi.util.Units;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.*;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkOrderController implements Initializable {

    public TextField workOrderName;
    public TextField workOrderLotNumber;
    public TextField workOrderPONumber;
    public ComboBox<Year> workOrderYear;
    public ComboBox<Customer> workOrderCustomer;
    public DatePicker workOrderSendDate;
    public DatePicker workOrderShippingDate;
    public TextField workOrderDestination;
    public TextField workOrderNote;
    public Button insertButton;
    public Button updateButton;
    public Button clearButton;
    public ComboBox<String> searchComboBox;
    public TextField searchField;
    public Button reloadData;
    public Label anchorLabel;
    public Label anchorData;

    public TableView<WorkOrderToString> dataTable;
    public TableColumn<WorkOrderToString, Integer> idColumn;
    public TableColumn<WorkOrderToString, String> workOrderSendDateColumn;
    public TableColumn<WorkOrderToString, String> workOrderNumberColumn;
    public TableColumn<WorkOrderToString, String> workOrderLotColumn;
    public TableColumn<WorkOrderToString, String> workOrderPOColumn;
    public TableColumn<WorkOrderToString, String> workOrderYearColumn;
    public TableColumn<WorkOrderToString, String> workOrderCustomerColumn;
    public TableColumn<WorkOrderToString, String> workOrderShippingDateColumn;
    public TableColumn<WorkOrderToString, String> workOrderDestinationColumn;
    public TableColumn<WorkOrderToString, String> workOrderNoteColumn;
    public Button exportData;

    private int currentRow;
    private String currentCell;
    private final Utils utils = new Utils();

    private boolean isEditableComboBox = false;
    private boolean isUpdating = false;

    private final WorkOrderDAO workOrderDAO = new WorkOrderDAO();
    private final YearDAO yearDAO = new YearDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderBySupplierDAO orderBySupplierDAO = new OrderBySupplierDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final WorkProductionDAO workProductionDAO = new WorkProductionDAO();
    private final WorkOrderToStringDAO workOrderToStringDAO = new WorkOrderToStringDAO();

    private final ObservableList<Year> yearObservableList = FXCollections.observableArrayList(yearDAO.getList());
    private final ObservableList<Customer> customerObservableList = FXCollections.observableArrayList(customerDAO.getList());
    private final ObservableList<WorkOrder> workOrderObservableList = FXCollections.observableArrayList(workOrderDAO.getList());

    /**
     * All needed to start controller
     * */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        workOrderYear.getItems().addAll(yearObservableList);
//        yearComboBoxFilter();
        workOrderCustomer.getItems().addAll(customerObservableList);
//        customerComboBoxFilter();
        new AutoCompleteComboBoxListener<>(workOrderCustomer, true, customerDAO.getLastestData().getName());
        resetComboBox();
        loadView();
    }

    /**
     * Inserting button action
     * */
    public void insertButton(ActionEvent actionEvent) {
        try {
            if(!workOrderDAO.hasName(workOrderName.getText().replace(" ", ""))) {
                workOrderDAO.insert(
                        workOrderName.getText(),
                        workOrderLotNumber.getText(),
                        workOrderPONumber.getText(),
                        yearDAO.getYear(utils.getComboBoxValue(workOrderYear)).getId(),
                        customerDAO.getCustomer(utils.getComboBoxValue(workOrderCustomer)).getId(),
                        workOrderSendDate.getEditor().getText(),
                        workOrderShippingDate.getEditor().getText(),
                        workOrderDestination.getText(),
                        workOrderNote.getText()
                );
                reload();
            } else {
                utils.alert("err", Alert.AlertType.ERROR, "Error", workOrderName.getText() + " already exists.").showAndWait();
            }
        } catch (Exception ex) {
            utils.alert("err", Alert.AlertType.ERROR, "Error", ex.getMessage()).showAndWait();
        }
    }

    /**
     * Updating button action
     * */
    public void updateButton(ActionEvent actionEvent) {
        try {
            if(!workOrderDAO.getDataByID(Integer.parseInt(anchorData.getText())).getName().equals(workOrderName.getText().replace(" ", "")) && workOrderDAO.hasName(workOrderName.getText().replace(" ", ""))) {
                utils.alert("err", Alert.AlertType.ERROR, "Error", workOrderName.getText() + " already exists.").showAndWait();
            } else {
                workOrderDAO.update(
                        workOrderName.getText(),
                        workOrderLotNumber.getText(),
                        workOrderPONumber.getText(),
                        yearDAO.getYear(utils.getComboBoxValue(workOrderYear)).getId(),
                        customerDAO.getCustomer(utils.getComboBoxValue(workOrderCustomer)).getId(),
                        workOrderSendDate.getEditor().getText(),
                        workOrderShippingDate.getEditor().getText(),
                        workOrderDestination.getText(),
                        workOrderNote.getText(),
                        Integer.parseInt(anchorData.getText())
                );
                reload();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
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
        utils.reloadTableViewOnChange(dataTable, currentRow, currentCell);
        clearFields();
        loadView();
    }

    /**
     * Handle event on clearing all inputs
     * */
    private void clearFields() {
        resetComboBox();
        anchorLabel.setText("No ID Selected");
        anchorData.setText("");

        workOrderName.setText("");
        workOrderLotNumber.setText("");
        workOrderPONumber.setText("");
        workOrderSendDate.setValue(null);
        workOrderShippingDate.setValue(null);
        workOrderDestination.setText("");
        workOrderNote.setText("");

        searchField.setText("");
    }

    /**
     * Handle on clearing comnbobox
     * */
    private void resetComboBox() {
        LocalDateTime now = LocalDateTime.now();
        utils.setComboBoxValue(workOrderYear, now.getYear()+"");

        Customer customerData = customerDAO.getDataByID(1);
        utils.setComboBoxValue(workOrderCustomer, customerData.getName());
    }

    /**
     * Determine the combobox is editable or not.
     *
     * @param isEditable state of editable
     * */
    private void isEditableComboBox(boolean isEditable) {
    }

    /**
     * Enable/Disable the filter for Combobox
     *
     * */
    private void filterComboBox(boolean filter) {
    }

    /**
     * Handle on filter Year Combobox
     *
     * */
    private void yearComboBoxFilter() {
    }

    /**
     * Handle on filter Customer Combobox
     * */
    private void customerComboBoxFilter() {
        // Create the listener to filter the list as user enters search terms
        FilteredList<Customer> dataFilteredList = new FilteredList<>(customerObservableList, p-> true);

        // Add listener to our ComboBox textfield to filter the list
        workOrderCustomer.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            dataFilteredList.setPredicate(item -> {

                this.isUpdating = false;

                // Check if the search term is contained anywhere in our list
                if (item.getName().toLowerCase().contains(newValue.toLowerCase().trim()) && !isUpdating) {
                    workOrderCustomer.show();
                    return true;
                }

                // No matches found
                return false;
            });

        });

        workOrderCustomer.getItems().removeAll(customerObservableList);
        workOrderCustomer.getItems().addAll(dataFilteredList);
    }

    /**
     * Hiding the ComboBox while on updating stage.
     * */
    private void hideComboBoxForUpdatingData() {
    }

    /**
     * Handle on clearing comnbobox
     * */
    private void getComboBoxData(WorkOrderToString data) {
        if(!isEditableComboBox) {
            workOrderYear.getSelectionModel().select(yearDAO.getYear(data.getYear()));
            workOrderCustomer.getSelectionModel().select(customerDAO.getCustomer(data.getCustomerId()));
        } else {
            workOrderYear.getEditor().setText(data.getYear());
            workOrderCustomer.getEditor().setText(data.getCustomerId());
        }
    }

    /**
     * Setting data for inputs
     *
     * @param workOrder - the product data
     * */
    private void getData(WorkOrderToString workOrder) {
        clearFields();
        workOrderName.setText(workOrder.getName());
        workOrderLotNumber.setText(workOrder.getLotNumber());
        workOrderPONumber.setText(workOrder.getPurchaseOrder());
        workOrderSendDate.setValue(utils.getDate(workOrder.getSendDate()));
        workOrderShippingDate.setValue(utils.getDate(workOrder.getShippingDate()));

        getComboBoxData(workOrder);

        workOrderDestination.setText(workOrder.getDestination());
        workOrderNote.setText(workOrder.getNote());

        anchorLabel.setText("ID selected: ");
        anchorData.setText(""+ workOrder.getId());

        isUpdating = true;
    }

    /**
     * Handle on searching data
     * */
    public void setSearchField() {
        ObservableList<WorkOrderToString> list = FXCollections.observableArrayList(workOrderToStringDAO.getList());
        FilteredList<WorkOrderToString> filteredList = new FilteredList<>(list, p -> true);

        searchField.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    switch (searchComboBox.getValue()) {
                        case "S??? l???nh":
                            filteredList.setPredicate(str -> {
                                if (newValue == null || newValue.isEmpty())
                                    return true;
                                String lowerCaseFilter = newValue.toLowerCase();
                                return str.getName().toLowerCase().contains
                                        (lowerCaseFilter);
                            });
                            break;
                        case "S??? Lot":
                            filteredList.setPredicate(str -> {
                                if (newValue == null || newValue.isEmpty())
                                    return true;
                                String lowerCaseFilter = newValue.toLowerCase();
                                return str.getLotNumber().toLowerCase().contains
                                        (lowerCaseFilter);
                            });
                            break;
                        case "S??? PO":
                            filteredList.setPredicate(str -> {
                                if (newValue == null || newValue.isEmpty())
                                    return true;
                                String lowerCaseFilter = newValue.toLowerCase();
                                return str.getPurchaseOrder().toLowerCase().contains
                                        (lowerCaseFilter);
                            });
                            break;
//                        case "Kh??ch":
//                            filteredList.setPredicate(str -> {
//                                if (newValue == null || newValue.isEmpty())
//                                    return true;
//                                String lowerCaseFilter = newValue.toLowerCase();
//                                return str.getCustomerId().toLowerCase().contains
//                                        (lowerCaseFilter);
//                            });
//                            break;
                        case "C???ng":
                            filteredList.setPredicate(str -> {
                                if (newValue == null || newValue.isEmpty())
                                    return true;
                                String lowerCaseFilter = newValue.toLowerCase();
                                return str.getDestination().toLowerCase().contains
                                        (lowerCaseFilter);
                            });
                            break;
                    }
                });

        workOrderName.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    filteredList.setPredicate(str -> {
                        if (newValue == null || newValue.isEmpty())
                            return true;
                        String lowerCaseFilter = newValue.toLowerCase();
                        return str.getName().toLowerCase().contains
                                (lowerCaseFilter);
                    });
                });

        workOrderName.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    switch (searchComboBox.getValue()) {
                        case "S??? l???nh":
                            filteredList.setPredicate(str -> {
                                if (newValue == null || newValue.isEmpty())
                                    return true;
                                String lowerCaseFilter = newValue.toLowerCase();
                                return str.getName().toLowerCase().contains
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

        SortedList<WorkOrderToString> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(dataTable.comparatorProperty());

        dataTable.setItems(sortedList);
    }

    /**
     * Getting current row on click
     * */
    public void getCurrentRow() {
    }

    /**
     * Setting context menu for table row
     * */
    private void setContextMenu() {
        dataTable.setRowFactory((TableView<WorkOrderToString> tableView) -> {
            final TableRow<WorkOrderToString> row = new TableRow<>();

            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem viewItem = new MenuItem("Chi ti???t");
            final MenuItem editItem = new MenuItem("C???p nh???t");
            final MenuItem manage = new MenuItem("List m???t h??ng");
            final MenuItem removeItem = new MenuItem("X??a d??ng");

            viewItem.setOnAction((ActionEvent event) -> {
                WorkOrderToString workOrder = dataTable.getSelectionModel().getSelectedItem();
                int rowIndex = dataTable.getSelectionModel().getSelectedIndex();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Chi ti???t L???nh s???n xu???t");
                alert.setHeaderText(workOrder.getName());
                alert.setContentText(
                        workOrder.getName() + "\n"
                );

                alert.showAndWait();
            });
//            contextMenu.getItems().add(viewItem);

            editItem.setOnAction((ActionEvent event) -> {
                WorkOrderToString workOrder = dataTable.getSelectionModel().getSelectedItem();
                getData(workOrder);
                hideComboBoxForUpdatingData();
            });
            contextMenu.getItems().add(editItem);

            manage.setOnAction((ActionEvent event) -> {
                WorkOrderToString workOrder = dataTable.getSelectionModel().getSelectedItem();
                loadSingleProductScene((Stage) manage.getParentPopup().getOwnerWindow(),"/fxml/work_order_product.fxml", workOrder.getName(), workOrder, 1152, 640);
            });
            contextMenu.getItems().add(manage);

            removeItem.setOnAction((ActionEvent event) -> {
                WorkOrderToString workOrder = dataTable.getSelectionModel().getSelectedItem();
                Alert alert = utils.alert("del", Alert.AlertType.CONFIRMATION, "X??a: "+ workOrder.getName(), null);

                Optional<ButtonType> result = alert.showAndWait();
                if (alert.getAlertType() == Alert.AlertType.CONFIRMATION && result.get() == ButtonType.OK){
                    workOrderDAO.delete_wopp_children(workOrder.getId());
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

            row.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if(mouseEvent.getClickCount() == 2 && !row.isEmpty()) {
                        WorkOrderToString workOrder = dataTable.getSelectionModel().getSelectedItem();
                        getData(workOrder);
                        hideComboBoxForUpdatingData();
                    }
                }
            });
            return row ;
        });
    }

    /**
     * Loading scene utility
     *
     * @param resource resource path
     * @param title scene title
     * @param workOrder data
     * */
    private void loadSingleProductScene(Stage stage, String resource, String title, WorkOrderToString workOrder, int minWidth, int minHeight) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(resource));
            /*
             * if "fx:controller" is not set in fxml
             * fxmlLoader.setController(NewWindowController);
             */
            Scene scene = new Scene(fxmlLoader.load());
            Stage _stage = new Stage();
            scene.getStylesheets().add("style.css");
            _stage.setTitle(title);
            _stage.setScene(scene);
            _stage.initModality(Modality.WINDOW_MODAL);

//            _stage.setResizable(false);
//            _stage.setMaximized(true);
            _stage.getIcons().add(new Image("/images/icon.png"));
            stage.setOpacity(0);

            _stage.setMinWidth(minWidth);
            _stage.setWidth(minWidth);
            _stage.setHeight(minHeight);
            _stage.setMinHeight(minHeight);

            ChangeListener<Number> widthListener = (observable, oldValue, newValue) -> {
                double stageWidth = newValue.doubleValue();
                _stage.setX(stage.getX() + stage.getWidth() / 2 - stageWidth / 2);
            };
            ChangeListener<Number> heightListener = (observable, oldValue, newValue) -> {
                double stageHeight = newValue.doubleValue();
                _stage.setY(stage.getY() + stage.getHeight() / 2 - stageHeight / 2);
            };

            _stage.widthProperty().addListener(widthListener);
            _stage.heightProperty().addListener(heightListener);

            _stage.setOnShown(e -> {
                _stage.widthProperty().removeListener(widthListener);
                _stage.heightProperty().removeListener(heightListener);
            });

            _stage.setMaximized(true);

            _stage.show();

            WorkOrderProductController controller = fxmlLoader.<WorkOrderProductController>getController();
            controller.setData(workOrder);

            _stage.setOnHiding(e -> {
                stage.setOpacity(1);
            });

        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        }
    }

    /**
     * Formatting date time
     * */
    private void formatDate() {
        utils.formatDate(workOrderSendDate, "dd-MM-yyyy");
        utils.formatDate(workOrderShippingDate, "dd-MM-yyyy");
    }

    /**
     * Reset fields
     * This will handle an enter event and mouse leave for these Nodes
     * */
    private void resetFields() {
        utils.disableKeyEnterOnTextField(workOrderName);
        utils.disableKeyEnterOnTextField(workOrderLotNumber);
        utils.disableKeyEnterOnTextField(workOrderPONumber);
        utils.disableKeyEnterOnTextFieldDatePicker(workOrderSendDate);
        utils.disableKeyEnterOnTextFieldDatePicker(workOrderShippingDate);

        utils.disableKeyEnterOnTextFieldComboBox(workOrderYear, false, "N??m");
        utils.disableKeyEnterOnTextFieldComboBox(workOrderCustomer, true, "Kh??ch h??ng");

        utils.disableKeyEnterOnTextField(workOrderDestination);
        utils.disableKeyEnterOnTextField(workOrderNote);
    }

    /**
     * Load the current view resources.
     * <br>
     * Contains: <br>
     * - formatDate() <br>
     * - resetFields() <br>
     * - resetComboBox() <br>
     * - getCurrentRow() <br>
     * - setSearchField() <br>
     * - Controlling columns view and actions <br>
     * - Implementing contextMenu on right click <br>
     * */
    private void loadView() {
        anchorLabel.setText("No ID Selected");
        formatDate();

        resetFields();

        resetComboBox();

        dataTable.setEditable(true);

        getCurrentRow();

        idColumn.setSortable(false);
        idColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Integer>(dataTable.getItems().indexOf(column.getValue())+1));

        workOrderNumberColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
//        workOrderNumberColumn.setCellFactory(TextFieldTableCell.<WorkOrder>forTableColumn());
//        workOrderNumberColumn.setOnEditCommit(event -> {
//            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
//            ((WorkOrder) event.getTableView().getItems().get(event.getTablePosition().getRow())).setName(data.trim().replace(" ", ""));
//            workOrderDAO.updateData("name", data.trim().replace(" ", ""), event.getRowValue().getId()+"");
//            dataTable.refresh();
//        });

        workOrderLotColumn.setCellValueFactory(new PropertyValueFactory<>("lotNumber"));
//        workOrderLotColumn.setCellFactory(TextFieldTableCell.<WorkOrder>forTableColumn());
//        workOrderLotColumn.setOnEditCommit(event -> {
//            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
//            ((WorkOrder) event.getTableView().getItems().get(event.getTablePosition().getRow())).setLotNumber(data);
//            workOrderDAO.updateData("lot_number", data, event.getRowValue().getId()+"");
//            dataTable.refresh();
//        });

        workOrderPOColumn.setCellValueFactory(new PropertyValueFactory<>("purchaseOrder"));
//        workOrderPOColumn.setCellFactory(TextFieldTableCell.<WorkOrder>forTableColumn());
//        workOrderPOColumn.setOnEditCommit(event -> {
//            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
//            ((WorkOrder) event.getTableView().getItems().get(event.getTablePosition().getRow())).setPurchaseOrder(data);
//            workOrderDAO.updateData("po_number", data, event.getRowValue().getId()+"");
//            dataTable.refresh();
//        });

        workOrderYearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
//        workOrderYearColumn.setCellFactory(TextFieldTableCell.<WorkOrder, Integer>forTableColumn(new IntegerStringConverter()));
//        workOrderYearColumn.setCellFactory(tc -> {
//
//            TableCell<WorkOrder, Integer> cell = new TableCell<>();
//
//            currentRow = cell.getIndex();
//            currentCell = cell.getText();
//
//            Text text = new Text();
//            ComboBox<Year> commandComboBoxTableCell = new ComboBox<>();
//            cell.setGraphic(text);
//            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
//            text.wrappingWidthProperty().bind(workOrderYearColumn.widthProperty());
//            text.textProperty().bind(new StringBinding() {
//                { bind(cell.itemProperty()); }
//                @Override
//                protected String computeValue() {
////                    return cell.itemProperty().getValue() != null ? cell.itemProperty().getValue()+"" : "";
//                    if(cell.itemProperty().getValue() != null) {
//                        Year data = yearDAO.getDataByID(cell.itemProperty().getValue());
//                        return data.getYear();
//                    } else {
//                        return "";
//                    }
//                }
//            });
//
//            cell.setOnMouseClicked(e -> {
//                if (e.getClickCount() == 2 && ! cell.isEmpty()) {
//                    cell.setGraphic(commandComboBoxTableCell);
//                    cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
//                    commandComboBoxTableCell.setMaxWidth(Control.USE_COMPUTED_SIZE);
//                    commandComboBoxTableCell.getEditor().setText(cell.itemProperty().getValue().toString());
//
//                    commandComboBoxTableCell.getItems().addAll(yearObservableList);
//
//                    Year yearData = yearDAO.getDataByID(cell.itemProperty().getValue());
//                    commandComboBoxTableCell.setValue(yearData);
//
//                    commandComboBoxTableCell.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Year>() {
//                        @Override
//                        public void changed(ObservableValue<? extends Year> observableValue, Year year, Year t1) {
//                            TablePosition pos = (TablePosition) dataTable.getSelectionModel().getSelectedCells().get(0);
//                            int selectedRow = dataTable.getItems().get(pos.getRow()).getId();
//                            workOrderDAO.updateDataInteger("year", t1.getId(), selectedRow);
//                            clearFields();
//                            reload();
//                        }
//                    });
//                }
//            });
//
//            return cell ;
//        });

        workOrderCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
//        workOrderCustomerColumn.setCellFactory(tc -> {
//
//            TableCell<WorkOrder, Integer> cell = new TableCell<>();
//
//            currentRow = cell.getIndex();
//            currentCell = cell.getText();
//
//            Text text = new Text();
//
//            ComboBox<Customer> comboBoxTableCell = new ComboBox<>();
//            comboBoxTableCell.setConverter(new StringConverter<Customer>() {
//                @Override
//                public String toString(Customer data) {
//                    if (data == null) return null;
//                    return data.getName();
//                }
//
//                @Override
//                public Customer fromString(String s) {
//                    return null;
//                }
//            });
//
//            comboBoxTableCell.getItems().addAll(customerObservableList);
//            new AutoCompleteComboBoxListener<>(comboBoxTableCell, true, customerDAO.getLastestData().getName());
//
//            cell.setGraphic(text);
//            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
//            text.wrappingWidthProperty().bind(workOrderCustomerColumn.widthProperty());
//
//
//            text.textProperty().bind(new StringBinding() {
//                { bind(cell.itemProperty()); }
//                @Override
//                protected String computeValue() {
////                    return cell.itemProperty().getValue() != null ? cell.itemProperty().getValue()+"" : "";
//                    if(cell.itemProperty().getValue() != null) {
//                        Customer data = customerDAO.getDataByID(cell.itemProperty().getValue());
//                        return data.toString();
//                    } else {
//                        return "";
//                    }
//                }
//            });
//
//            cell.setOnMouseClicked(e -> {
//                if (e.getClickCount() == 2 && ! cell.isEmpty()) {
//                    cell.setGraphic(comboBoxTableCell);
//                    cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
//                    comboBoxTableCell.setMaxWidth(Control.USE_COMPUTED_SIZE);
//                    comboBoxTableCell.setEditable(true);
//                    comboBoxTableCell.getEditor().setText(supplierDAO.getDataByID(cell.itemProperty().getValue()).getCode());
//
//                    comboBoxTableCell.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Customer>() {
//                        @Override
//                        public void changed(ObservableValue<? extends Customer> observableValue, Customer data, Customer t1) {
//                            if(t1 != null) {
//                                if(!cell.isEmpty()) {
//                                    TablePosition pos = (TablePosition) dataTable.getSelectionModel().getSelectedCells().get(0);
//                                    int selectedRow = dataTable.getItems().get(pos.getRow()).getId();
//                                    workOrderDAO.updateDataInteger("customer_id", t1.getId(), selectedRow);
////                                    clearFields();
////                                    reload();
//                                }
//                            }
//                        }
//                    });
//                    comboBoxTableCell.addEventFilter(KeyEvent.ANY, ke -> {
//                        if(ke.getCode() == KeyCode.ENTER) {
//                            clearFields();
//                            reload();
//                        }
//                    });
//                }
//            });
//
//            return cell ;
//        });

        workOrderSendDateColumn.setCellValueFactory(new PropertyValueFactory<>("sendDate"));
//        workOrderSendDateColumn.setCellFactory(TextFieldTableCell.<WorkOrder>forTableColumn());
//        workOrderSendDateColumn.setOnEditCommit(event -> {
//            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
//            ((WorkOrder) event.getTableView().getItems().get(event.getTablePosition().getRow())).setSendDate(data);
//            workOrderDAO.updateData("send_date", data, event.getRowValue().getId()+"");
//            dataTable.refresh();
//        });

        workOrderShippingDateColumn.setCellValueFactory(new PropertyValueFactory<>("shippingDate"));
//        workOrderShippingDateColumn.setCellFactory(TextFieldTableCell.<WorkOrder>forTableColumn());
//        workOrderShippingDateColumn.setOnEditCommit(event -> {
//            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
//            ((WorkOrder) event.getTableView().getItems().get(event.getTablePosition().getRow())).setShippingDate(data);
//            workOrderDAO.updateData("shipping_date", data, event.getRowValue().getId()+"");
//            dataTable.refresh();
//        });

        workOrderDestinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
//        workOrderDestinationColumn.setCellFactory(TextFieldTableCell.<WorkOrder>forTableColumn());
//        workOrderDestinationColumn.setOnEditCommit(event -> {
//            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
//            ((WorkOrder) event.getTableView().getItems().get(event.getTablePosition().getRow())).setDestination(data);
//            workOrderDAO.updateData("destination", data, event.getRowValue().getId()+"");
//            dataTable.refresh();
//        });

        workOrderNoteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));
//        workOrderNoteColumn.setCellFactory(TextFieldTableCell.<WorkOrder>forTableColumn());
//        workOrderNoteColumn.setOnEditCommit(event -> {
//            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
//            ((WorkOrder) event.getTableView().getItems().get(event.getTablePosition().getRow())).setNote(data);
//            workOrderDAO.updateData("note", data, event.getRowValue().getId()+"");
//            dataTable.refresh();
//        });

        dataTable.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ESCAPE || event.getCode() == KeyCode.F5) {
                dataTable.refresh();
            }
        });

        dataTable.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );

        setContextMenu();

        utils.reloadTableViewOnChange(dataTable, currentRow, currentCell);

        setSearchField();
    }

    public void exportData(ActionEvent actionEvent) throws IOException {
//        data2DocOfOrderList((Stage)((Node) actionEvent.getSource()).getScene().getWindow());

        if(dataTable.getSelectionModel().getSelectedItem() == null) {
            utils.alert("err", Alert.AlertType.ERROR, "L???i", "Ch??a ch???n LSX").showAndWait();
        } else {
            utils.openListCheckboxWindow((Stage)((Node) actionEvent.getSource()).getScene().getWindow(), nv -> {

                try {
                    FileChooser fileChooser = new FileChooser();
                    FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Word document (*.docx)", "*.docx");
                    fileChooser.getExtensionFilters().add(extensionFilter);

                    DirectoryChooser directoryChooser = new DirectoryChooser();
                    directoryChooser.setTitle("Select folder");

//            File file = fileChooser.showSaveDialog(window);
                    File file = directoryChooser.showDialog((Stage)((Node) actionEvent.getSource()).getScene().getWindow());
                    LocalDateTime now = LocalDateTime.now();

                    if(file != null) {
                        ObservableList<OrderBySupllier> orderBySuplliers = FXCollections.observableArrayList(orderBySupplierDAO.getList(getListID()));

                        if(nv.get(1)) {
                            for (OrderBySupllier orderBySupllier : orderBySuplliers) {
                                ObservableList<OrderBySupllier> _sa = FXCollections.observableArrayList(orderBySupplierDAO.getListGroupByShippingAddress(getListID()));
                                for(OrderBySupllier _saEach : _sa) {
                                    ExportWordDocument.data2DocOfOrderBySupplier(file, getListID(), orderBySupllier.getsCode(), _saEach.getShipAddress(), now.toString(), 1);
                                }
//                            ExportWordDocument.data2DocOfOrderBySupplier(file, getListID(), orderBySupllier.getsCode(), now.toString());
                            }
                        }

                        String[] _arrayListID = getListID().split(",");
                        for(int i=0;i<_arrayListID.length; i++) {
                            workOrderDAO.updateData("order_date", now.getDayOfMonth()+"/"+now.getMonthValue()+"/"+now.getYear(), _arrayListID[i].trim());
                            WorkOrder workOrder = workOrderDAO.getDataByID(Integer.parseInt(_arrayListID[i].trim()));

                            if(nv.get(0)) {
                                ExportWordDocument.data2WorksheetOfOrderListDraft(file, workOrder, now.toString());
                            }

                            if(nv.get(2)) {
                                ExportWordDocument.data2DocOfOrderList(file, workOrder.getId()+"", now.toString(), 1);
                            }
                        }
                        utils.alert("info", Alert.AlertType.INFORMATION, "Xu???t file th??nh c??ng!", "File ???? ???????c l??u v??o ???????ng d???n " +file.getPath()).showAndWait();
                    }
                } catch(Exception ex) {
                    utils.alert("err", Alert.AlertType.ERROR, "L???i", ex.getMessage()).showAndWait();
                }
            }, "T??y ch???n");
        }

    }

    /**
     * Getting a list from table's selected cells
     * */
    private String getListID() {
        String idList = "";
        for(int i=0;i<dataTable.getSelectionModel().getSelectedCells().size();i++) {
            TablePosition pos = (TablePosition) dataTable.getSelectionModel().getSelectedCells().get(i);
            int selectedRow = dataTable.getItems().get(pos.getRow()).getId();
            idList += selectedRow;
            idList += (i<dataTable.getSelectionModel().getSelectedCells().size()-1) ? ", " : "";
        }
        return idList;
    }
}
