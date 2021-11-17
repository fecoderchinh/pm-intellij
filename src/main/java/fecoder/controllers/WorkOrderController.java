package fecoder.controllers;

import fecoder.DAO.*;
import fecoder.models.*;
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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.*;
import javafx.util.converter.IntegerStringConverter;
import org.apache.poi.util.Units;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public TableView<WorkOrder> dataTable;
    public TableColumn<WorkOrder, Integer> idColumn;
    public TableColumn<WorkOrder, String> workOrderSendDateColumn;
    public TableColumn<WorkOrder, String> workOrderNumberColumn;
    public TableColumn<WorkOrder, String> workOrderLotColumn;
    public TableColumn<WorkOrder, String> workOrderPOColumn;
    public TableColumn<WorkOrder, Integer> workOrderYearColumn;
    public TableColumn<WorkOrder, Integer> workOrderCustomerColumn;
    public TableColumn<WorkOrder, String> workOrderShippingDateColumn;
    public TableColumn<WorkOrder, String> workOrderDestinationColumn;
    public TableColumn<WorkOrder, String> workOrderNoteColumn;
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

    private final ObservableList<Year> yearObservableList = FXCollections.observableArrayList(yearDAO.getList());
    private final ObservableList<Customer> customerObservableList = FXCollections.observableArrayList(customerDAO.getList());
    private final ObservableList<WorkOrder> workOrderObservableList = FXCollections.observableArrayList(workOrderDAO.getList());

    /**
     * All needed to start controller
     * */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        workOrderYear.getItems().addAll(yearObservableList);
        yearComboBoxFilter();
        workOrderCustomer.getItems().addAll(customerObservableList);
        customerComboBoxFilter();
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
                        yearDAO.getYear(workOrderYear.getValue()+"").getId(),
                        customerDAO.getCustomer(workOrderCustomer.getValue()+"").getId(),
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
                        yearDAO.getYear(workOrderYear.getValue()+"").getId(),
                        customerDAO.getCustomer(workOrderCustomer.getValue()+"").getId(),
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
        anchorLabel.setText("");
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
        Year yearData = yearDAO.getDataByID(1);
        utils.setComboBoxValue(workOrderYear, yearData.getYear());

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
     * Setting data for inputs
     *
     * @param workOrder - the product data
     * */
    private void getData(WorkOrder workOrder) {
        clearFields();
        workOrderName.setText(workOrder.getName());
        workOrderLotNumber.setText(workOrder.getLotNumber());
        workOrderPONumber.setText(workOrder.getPurchaseOrder());
        workOrderSendDate.setValue(utils.getDate(workOrder.getSendDate()));
        workOrderShippingDate.setValue(utils.getDate(workOrder.getShippingDate()));

//        Year yearData = yearDAO.getDataByID(command.getYear());
//        utils.setComboBoxValue(commandYear, yearData.getYear());
//
//        Customer customerData = customerDAO.getDataByID(command.getCustomerId());
//        utils.setComboBoxValue(commandCustomer, customerData.getName());

        workOrderDestination.setText(workOrder.getDestination());
        workOrderNote.setText(workOrder.getNote());

        anchorLabel.setText("Current ID: ");
        anchorData.setText(""+ workOrder.getId());

        isUpdating = true;
    }

    /**
     * Handle on searching data
     * */
    public void setSearchField() {
        ObservableList<WorkOrder> list = FXCollections.observableArrayList(workOrderDAO.getList());
        FilteredList<WorkOrder> filteredList = new FilteredList<>(list, p -> true);

        searchField.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    switch (searchComboBox.getValue()) {
                        case "Số lệnh":
                            filteredList.setPredicate(str -> {
                                if (newValue == null || newValue.isEmpty())
                                    return true;
                                String lowerCaseFilter = newValue.toLowerCase();
                                return str.getName().toLowerCase().contains
                                        (lowerCaseFilter);
                            });
                            break;
                        case "Số Lot":
                            filteredList.setPredicate(str -> {
                                if (newValue == null || newValue.isEmpty())
                                    return true;
                                String lowerCaseFilter = newValue.toLowerCase();
                                return str.getLotNumber().toLowerCase().contains
                                        (lowerCaseFilter);
                            });
                            break;
                        case "Số PO":
                            filteredList.setPredicate(str -> {
                                if (newValue == null || newValue.isEmpty())
                                    return true;
                                String lowerCaseFilter = newValue.toLowerCase();
                                return str.getPurchaseOrder().toLowerCase().contains
                                        (lowerCaseFilter);
                            });
                            break;
//                        case "Khách":
//                            filteredList.setPredicate(str -> {
//                                if (newValue == null || newValue.isEmpty())
//                                    return true;
//                                String lowerCaseFilter = newValue.toLowerCase();
//                                return str.getCustomerId().toLowerCase().contains
//                                        (lowerCaseFilter);
//                            });
//                            break;
                        case "Cảng":
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

        searchComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal != null) {
                searchField.setText(null);
            }
        });

        SortedList<WorkOrder> sortedList = new SortedList<>(filteredList);
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
        dataTable.setRowFactory((TableView<WorkOrder> tableView) -> {
            final TableRow<WorkOrder> row = new TableRow<>();

            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem viewItem = new MenuItem("Chi tiết");
            final MenuItem editItem = new MenuItem("Cập nhật");
            final MenuItem manage = new MenuItem("List mặt hàng");
            final MenuItem removeItem = new MenuItem("Xóa dòng");

            viewItem.setOnAction((ActionEvent event) -> {
                WorkOrder workOrder = dataTable.getSelectionModel().getSelectedItem();
                int rowIndex = dataTable.getSelectionModel().getSelectedIndex();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Chi tiết Lệnh sản xuất");
                alert.setHeaderText(workOrder.getName());
                alert.setContentText(
                        "Số LSX: " + workOrder.getName() + "\n"
                );

                alert.showAndWait();
            });
            contextMenu.getItems().add(viewItem);

            editItem.setOnAction((ActionEvent event) -> {
                WorkOrder workOrder = dataTable.getSelectionModel().getSelectedItem();
                getData(workOrder);
                hideComboBoxForUpdatingData();
            });
            contextMenu.getItems().add(editItem);

            manage.setOnAction((ActionEvent event) -> {
                WorkOrder workOrder = dataTable.getSelectionModel().getSelectedItem();
                loadSingleProductScene("/fxml/work_order_product.fxml", workOrder.getName(), workOrder);
            });
            contextMenu.getItems().add(manage);

            removeItem.setOnAction((ActionEvent event) -> {
                WorkOrder workOrder = dataTable.getSelectionModel().getSelectedItem();
                Alert alert = utils.alert("del", Alert.AlertType.CONFIRMATION, "Xóa: "+ workOrder.getName(), null);

                Optional<ButtonType> result = alert.showAndWait();
                if (alert.getAlertType() == Alert.AlertType.CONFIRMATION && result.get() == ButtonType.OK){
                    workOrderDAO.delete(workOrder.getId());
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
     * Loading scene utility
     *
     * @param resource resource path
     * @param title scene title
     * @param workOrder data
     * */
    private void loadSingleProductScene(String resource, String title, WorkOrder workOrder) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(resource));
            /*
             * if "fx:controller" is not set in fxml
             * fxmlLoader.setController(NewWindowController);
             */
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            scene.getStylesheets().add("style.css");
            stage.setTitle(title);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.show();
            WorkOrderProductController controller = fxmlLoader.<WorkOrderProductController>getController();
            controller.setData(workOrder);

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

        utils.disableKeyEnterOnTextFieldComboBox(workOrderYear, false);
        utils.disableKeyEnterOnTextFieldComboBox(workOrderCustomer, true);

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
        formatDate();

        resetFields();

        resetComboBox();

        dataTable.setEditable(true);

        getCurrentRow();

        idColumn.setSortable(false);
        idColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Integer>(dataTable.getItems().indexOf(column.getValue())+1));

        workOrderNumberColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        workOrderNumberColumn.setCellFactory(TextFieldTableCell.<WorkOrder>forTableColumn());
        workOrderNumberColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((WorkOrder) event.getTableView().getItems().get(event.getTablePosition().getRow())).setName(data);
            workOrderDAO.updateData("name", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        workOrderLotColumn.setCellValueFactory(new PropertyValueFactory<>("lotNumber"));
        workOrderLotColumn.setCellFactory(TextFieldTableCell.<WorkOrder>forTableColumn());
        workOrderLotColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((WorkOrder) event.getTableView().getItems().get(event.getTablePosition().getRow())).setLotNumber(data);
            workOrderDAO.updateData("lot_number", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        workOrderPOColumn.setCellValueFactory(new PropertyValueFactory<>("purchaseOrder"));
        workOrderPOColumn.setCellFactory(TextFieldTableCell.<WorkOrder>forTableColumn());
        workOrderPOColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((WorkOrder) event.getTableView().getItems().get(event.getTablePosition().getRow())).setPurchaseOrder(data);
            workOrderDAO.updateData("po_number", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        workOrderYearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        workOrderYearColumn.setCellFactory(TextFieldTableCell.<WorkOrder, Integer>forTableColumn(new IntegerStringConverter()));
        workOrderYearColumn.setCellFactory(tc -> {

            TableCell<WorkOrder, Integer> cell = new TableCell<>();

            currentRow = cell.getIndex();
            currentCell = cell.getText();

            Text text = new Text();
            ComboBox<Year> commandComboBoxTableCell = new ComboBox<>();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(workOrderYearColumn.widthProperty());
            text.textProperty().bind(new StringBinding() {
                { bind(cell.itemProperty()); }
                @Override
                protected String computeValue() {
//                    return cell.itemProperty().getValue() != null ? cell.itemProperty().getValue()+"" : "";
                    if(cell.itemProperty().getValue() != null) {
                        Year data = yearDAO.getDataByID(cell.itemProperty().getValue());
                        return data.getYear();
                    } else {
                        return "";
                    }
                }
            });

            cell.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && ! cell.isEmpty()) {
                    cell.setGraphic(commandComboBoxTableCell);
                    cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
                    commandComboBoxTableCell.setMaxWidth(Control.USE_COMPUTED_SIZE);
                    commandComboBoxTableCell.getEditor().setText(cell.itemProperty().getValue().toString());

                    commandComboBoxTableCell.getItems().addAll(yearObservableList);

                    Year yearData = yearDAO.getDataByID(cell.itemProperty().getValue());
                    commandComboBoxTableCell.setValue(yearData);

                    commandComboBoxTableCell.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Year>() {
                        @Override
                        public void changed(ObservableValue<? extends Year> observableValue, Year year, Year t1) {
                            TablePosition pos = (TablePosition) dataTable.getSelectionModel().getSelectedCells().get(0);
                            int selectedRow = dataTable.getItems().get(pos.getRow()).getId();
                            workOrderDAO.updateDataInteger("year", t1.getId(), selectedRow);
                            clearFields();
                            reload();
                        }
                    });
                }
            });

            return cell ;
        });

        workOrderCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        workOrderCustomerColumn.setCellFactory(TextFieldTableCell.<WorkOrder, Integer>forTableColumn(new IntegerStringConverter()));
        workOrderCustomerColumn.setCellFactory(tc -> {

            TableCell<WorkOrder, Integer> cell = new TableCell<>();

            currentRow = cell.getIndex();
            currentCell = cell.getText();

            Text text = new Text();
            ComboBox<Customer> commandComboBoxTableCell = new ComboBox<>();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(workOrderCustomerColumn.widthProperty());
            text.textProperty().bind(new StringBinding() {
                { bind(cell.itemProperty()); }
                @Override
                protected String computeValue() {
//                    return cell.itemProperty().getValue() != null ? cell.itemProperty().getValue()+"" : "";
                    if(cell.itemProperty().getValue() != null) {
                        Customer data = customerDAO.getDataByID(cell.itemProperty().getValue());
                        return data.getName();
                    } else {
                        return "";
                    }
                }
            });

            cell.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && ! cell.isEmpty()) {
                    cell.setGraphic(commandComboBoxTableCell);
                    cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
                    commandComboBoxTableCell.setMaxWidth(Control.USE_COMPUTED_SIZE);

                    commandComboBoxTableCell.setEditable(true);
                    commandComboBoxTableCell.getEditor().setText(cell.itemProperty().getValue().toString());

                    Customer getCustomerByID = customerDAO.getDataByID(cell.itemProperty().getValue());
                    commandComboBoxTableCell.getEditor().setText(getCustomerByID.getName());

                    FilteredList<Customer> dataFilteredList = new FilteredList<>(customerObservableList, p-> true);

                    commandComboBoxTableCell.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
                        dataFilteredList.setPredicate(item -> {

                            // If the TextField is empty, return all items in the original list
                            if (newValue == null || newValue.isEmpty()) {
                                commandComboBoxTableCell.hide();
                                return true;
                            }

                            // Check if the search term is contained anywhere in our list
                            if (item.getName().trim().toLowerCase().contains(newValue.toLowerCase().trim())) {
                                commandComboBoxTableCell.show();
                                return true;
                            }

                            // No matches found
                            return false;
                        });
                        SortedList<Customer> dataSortedList = new SortedList<>(dataFilteredList);
                        commandComboBoxTableCell.getItems().removeAll(customerObservableList);
                        commandComboBoxTableCell.getItems().addAll(dataSortedList);
                    });
//
                    commandComboBoxTableCell.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            commandComboBoxTableCell.setEditable(true);
                        }
                    });

                    commandComboBoxTableCell.setOnKeyPressed(event -> {
                        if (event.getCode() == KeyCode.ENTER) {
                            TablePosition pos = (TablePosition) dataTable.getSelectionModel().getSelectedCells().get(0);
                            int selectedRow = dataTable.getItems().get(pos.getRow()).getId();
                            Customer getCustomerFromName = customerDAO.getCustomer(commandComboBoxTableCell.getEditor().getText());
                            if(!customerDAO.hasName(getCustomerFromName.getName())) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Lỗi!");
                                alert.setHeaderText("Đã xảy ra lỗi!");
                                alert.setContentText("Dữ liệu: "+ commandComboBoxTableCell.getEditor().getText() +" không tồn tài");
                                alert.showAndWait();
                            } else {
                                workOrderDAO.updateDataInteger("customer_id", getCustomerFromName.getId(), selectedRow);
                                clearFields();
                                reload();
                            }
                        }
                    });
                }
            });

            return cell ;
        });

        workOrderSendDateColumn.setCellValueFactory(new PropertyValueFactory<>("sendDate"));
        workOrderSendDateColumn.setCellFactory(TextFieldTableCell.<WorkOrder>forTableColumn());
        workOrderSendDateColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((WorkOrder) event.getTableView().getItems().get(event.getTablePosition().getRow())).setSendDate(data);
            workOrderDAO.updateData("send_date", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        workOrderShippingDateColumn.setCellValueFactory(new PropertyValueFactory<>("shippingDate"));
        workOrderShippingDateColumn.setCellFactory(TextFieldTableCell.<WorkOrder>forTableColumn());
        workOrderShippingDateColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((WorkOrder) event.getTableView().getItems().get(event.getTablePosition().getRow())).setShippingDate(data);
            workOrderDAO.updateData("shipping_date", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        workOrderDestinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        workOrderDestinationColumn.setCellFactory(TextFieldTableCell.<WorkOrder>forTableColumn());
        workOrderDestinationColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((WorkOrder) event.getTableView().getItems().get(event.getTablePosition().getRow())).setDestination(data);
            workOrderDAO.updateData("destination", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        workOrderNoteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));
        workOrderNoteColumn.setCellFactory(TextFieldTableCell.<WorkOrder>forTableColumn());
        workOrderNoteColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((WorkOrder) event.getTableView().getItems().get(event.getTablePosition().getRow())).setNote(data);
            workOrderDAO.updateData("note", data, event.getRowValue().getId());
            dataTable.refresh();
        });

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

        if(getListID() != "") {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Word document (*.docx)", "*.docx");
            fileChooser.getExtensionFilters().add(extensionFilter);

            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select folder");

//            File file = fileChooser.showSaveDialog(window);
            File file = directoryChooser.showDialog((Stage)((Node) actionEvent.getSource()).getScene().getWindow());

            ObservableList<OrderBySupllier> orderBySuplliers = FXCollections.observableArrayList(orderBySupplierDAO.getList(getListID()));

            for (OrderBySupllier orderBySupllier : orderBySuplliers) {
                data2DocOfOrderBySupplier(file, orderBySupllier.getsCode());
            }

            String[] _arrayListID = getListID().split(",");
            for(int i=0;i<_arrayListID.length; i++) {
                WorkOrder workOrder = workOrderDAO.getDataByID(Integer.parseInt(_arrayListID[i].trim()));
                data2DocOfOrderListDraft(file, workOrder);
            }

            data2DocOfOrderList(file);
        } else {
            utils.alert("err", Alert.AlertType.ERROR, "Lỗi", "Không có dữ liệu").showAndWait();
        }

    }
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

    private String getListWorkOrderName() {
        String idList = getListID();
        String listWorkOrderName = "";
        ObservableList<Order> listOfWorkOrder = FXCollections.observableArrayList(orderDAO.getListOfWorkOrder(idList));
        for(int i=0;i<listOfWorkOrder.size();i++) {
            listWorkOrderName += listOfWorkOrder.get(i).getWorkOrderName();
            listWorkOrderName += (i<listOfWorkOrder.size()-1) ? " + " : "";
        }

        return listWorkOrderName;
    }

    private void data2DocOfOrderList(File file) {
        String imgFile = "e:\\java_platform\\docs-data\\logo.jpg";
        String _fontFamily = "Arial";

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
            setHeaderRowforSingleCell(row.getCell(1), "Công ty CP SEAVINA", true, ParagraphAlignment.CENTER);
//            setHeaderRowforSingleCell(row.getCell(1), "Lô 16A-18, KCN Trà Nóc I,P.Trà Nóc, Q. Bình Thủy, TP. Cần Thơ ,Việt Nam", true, ParagraphAlignment.CENTER);
//            setHeaderRowforSingleCell(row.getCell(1), "1801141886", true, ParagraphAlignment.CENTER);
            setHeaderRowforSingleCell(row.getCell(1), "Tel: 0292.3744950  Fax: 0292.3743678", true, ParagraphAlignment.CENTER);
//            setHeaderRowforSingleCell(row.getCell(1), "(Ms. Nhung: 0946.886 868, Ms Trinh: 0918.755729)", false, ParagraphAlignment.CENTER);
            setHeaderRowforSingleCell(row.getCell(2), "TT5.6.1/ KD2-BM3", false, ParagraphAlignment.CENTER);

            /*
             * Word content title
             * */

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.RIGHT);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            run.setText("SEAVINA, ngày " + now.getDayOfMonth() + ", tháng " + now.getMonthValue() + ", năm "+ now.getYear());
            run.addBreak();

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
            run.setBold(true);
            run.setFontSize(14);
            run.setText("ĐỀ NGHỊ BAO BÌ "+getListWorkOrderName());
            run.addBreak();

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            paragraph.setSpacingLineRule(LineSpacingRule.AUTO);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
//            run.setBold(true);
            run.setFontSize(10);
            run.setText("Kính gửi: Phòng Mua Hàng Minh Tâm");
            run.addBreak();
            run.setText("Điện thoại: 02923. 3600063");
            run.addBreak();
            run.setText("Công ty CP SEAVINA (Phòng Kinh Doanh) xin gửi Phiếu đề nghị mua hàng với chi tiết như sau:");

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

            setHeaderRowforSingleCell(row.getCell(0), "STT", false, ParagraphAlignment.CENTER);
            setHeaderRowforSingleCell(row.getCell(1), "Tên Bao Bì", false, ParagraphAlignment.CENTER);
            setHeaderRowforSingleCell(row.getCell(2), "Qui cách (CM)", false, ParagraphAlignment.CENTER);
            setHeaderRowforSingleCell(row.getCell(3), "ĐVT", false, ParagraphAlignment.CENTER);
            setHeaderRowforSingleCell(row.getCell(4), "SL Đặt", false, ParagraphAlignment.CENTER);
            setHeaderRowforSingleCell(row.getCell(5), "Ghi chú", false, ParagraphAlignment.CENTER);
            setHeaderRowforSingleCell(row.getCell(6), "LSX", false, ParagraphAlignment.CENTER);

            ObservableList<OrderBySupllier> orderObservableList = FXCollections.observableArrayList(orderBySupplierDAO.getList(getListID()));
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

                setHeaderRowforSingleCell(row.getCell(0), (i+1)+"", false, ParagraphAlignment.CENTER);
                setHeaderRowforSingleCell(row.getCell(1), orderObservableList.get(i).getpName() + " ("+ orderObservableList.get(i).getpSpecs() +")", false, ParagraphAlignment.LEFT);
                setHeaderRowforSingleCell(row.getCell(2), orderObservableList.get(i).getpDimension(), false, ParagraphAlignment.CENTER);
                setHeaderRowforSingleCell(row.getCell(3), orderObservableList.get(i).getpUnit(), false, ParagraphAlignment.CENTER);
                setHeaderRowforSingleCell(row.getCell(4), formatter.format(Float.parseFloat(orderObservableList.get(i).getpTotal()+""))+"", false, ParagraphAlignment.CENTER);
                setHeaderRowforSingleCell(row.getCell(5), residualNumber > 0 ? "Dư "+formatter.format(residualNumber) : "", false, ParagraphAlignment.CENTER);
                setHeaderRowforSingleCell(row.getCell(6), orderObservableList.get(i).getWoName(), false, ParagraphAlignment.CENTER);
            }

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            paragraph.setSpacingLineRule(LineSpacingRule.AUTO);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
            run.setFontSize(10);
            run.setText("Lưu ý: Mọi thay đổi hay có vấn đề chưa rõ phải phản hồi lại với bộ phận liên quan trước khi thực hiện để tránh sai sót có thể xảy ra.");
            run.addBreak();

            table = doc.createTable(1, 2);
            table.setWidth("100%");
            table.removeBorders();
            row = table.getRow(0);
            row.getCell(0).setWidth("50%");
            row.getCell(1).setWidth("50%");
            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

            setHeaderRowforSingleCell(row.getCell(0), "Phòng Kinh Doanh", false, ParagraphAlignment.LEFT);
            setHeaderRowforSingleCell(row.getCell(1), "Người Lập Biểu", false, ParagraphAlignment.RIGHT);

            if(orderObservableList.size() > 0) {
                if(file != null) {
                    // save it to .docx file
//                try (FileOutputStream out = new FileOutputStream(file.getPath())) // for fileChooser
                    try (FileOutputStream out = new FileOutputStream(file.getPath()+"/DE NGHI BB "+ getListWorkOrderName() +".docx"))
                    {
                        doc.write(out);
                        utils.alert("info", Alert.AlertType.INFORMATION, "Xuất file thành công!", "File đã được lưu vào ổ đĩa!").showAndWait();
                    }
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void data2DocOfOrderBySupplier(File file, String code) {
        String imgFile = "e:\\java_platform\\docs-data\\logo.jpg";
        String _fontFamily = "Arial";
        ObservableList<OrderBySupllier> orderObservableList = FXCollections.observableArrayList(orderBySupplierDAO.getListBySupplierCode(getListID(), code));
        Supplier supplier = supplierDAO.getDataByCode(code);

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
            row.getCell(0).setWidth("22%");
            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(1).setWidth("55%");
            row.getCell(1).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);
            row.getCell(2).setWidth("23%");
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
            setHeaderRowforSingleCell(row.getCell(1), "Công ty CP SEAVINA", true, ParagraphAlignment.CENTER);
            setHeaderRowforSingleCell(row.getCell(1), "Lô 16A-18, KCN Trà Nóc I,P.Trà Nóc, Q. Bình Thủy, TP. Cần Thơ ,Việt Nam", true, ParagraphAlignment.CENTER);
            setHeaderRowforSingleCell(row.getCell(1), "1801141886", true, ParagraphAlignment.CENTER);
            setHeaderRowforSingleCell(row.getCell(1), "Tel: 0292.3744950  Fax: 0292.3743678", true, ParagraphAlignment.CENTER);
            setHeaderRowforSingleCell(row.getCell(1), "(Ms. Nhung: 0946.886 868, Ms Trinh: 0918.755729)", false, ParagraphAlignment.CENTER);
            setHeaderRowforSingleCell(row.getCell(2), "TT5.6.1/ KD2-BM3", false, ParagraphAlignment.CENTER);

            /*
             * Word content title
             * */

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.RIGHT);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            run.setText("SEAVINA, ngày " + now.getDayOfMonth() + ", tháng " + now.getMonthValue() + ", năm "+ now.getYear());
            run.addBreak();

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
            run.setBold(true);
            run.setFontSize(10);
            run.setText("ĐƠN ĐẶT HÀNG");
            run.addBreak();
            run.setText("(Số: "+code+".\t\t/"+now.getYear()+")");
            run.addBreak();

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            paragraph.setSpacingLineRule(LineSpacingRule.AUTO);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
//            run.setBold(true);
            run.setFontSize(10);
            run.setText("Kính gửi: "+supplier.getName());
            run.addBreak();
            run.setText("Đại diện: "+supplier.getDeputy());
            run.addBreak();
            run.setText("Địa chỉ: "+supplier.getAddress());
            run.addBreak();
            run.setText("Điện thoại: "+supplier.getPhone() + "\t\t"+"Fax: "+supplier.getFax());
            run.addBreak();
            run.setText("Công ty CP SEAVINA xin gửi ĐƠN ĐẶT HÀNG đến Quý Công Ty với chi tiết như sau:");

            table = doc.createTable(1, 6);
            table.setWidth("100%");

            row = table.getRow(0);
            row.getCell(0).setWidth("10%");
            row.getCell(1).setWidth("35%");
            row.getCell(2).setWidth("15%");
            row.getCell(3).setWidth("8%");
            row.getCell(4).setWidth("12%");
            row.getCell(5).setWidth("20%");
            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(3).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(4).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(5).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

            setHeaderRowforSingleCell(row.getCell(0), "STT", false, ParagraphAlignment.CENTER);
            setHeaderRowforSingleCell(row.getCell(1), "Tên Bao Bì", false, ParagraphAlignment.CENTER);
            setHeaderRowforSingleCell(row.getCell(2), "Qui cách (CM)", false, ParagraphAlignment.CENTER);
            setHeaderRowforSingleCell(row.getCell(3), "ĐVT", false, ParagraphAlignment.CENTER);
            setHeaderRowforSingleCell(row.getCell(4), "SL Đặt", false, ParagraphAlignment.CENTER);
            setHeaderRowforSingleCell(row.getCell(5), "LSX", false, ParagraphAlignment.CENTER);

            DecimalFormat formatter = new DecimalFormat("#,###");

            table = doc.createTable(orderObservableList.size(), 6);
            table.setWidth("100%");

            for(int i=0;i<orderObservableList.size();i++) {
                row = table.getRow(i);
                row.getCell(0).setWidth("10%");
                row.getCell(1).setWidth("35%");
                row.getCell(2).setWidth("15%");
                row.getCell(3).setWidth("8%");
                row.getCell(4).setWidth("12%");
                row.getCell(5).setWidth("20%");
                row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(3).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(4).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(5).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                setHeaderRowforSingleCell(row.getCell(0), (i+1)+"", false, ParagraphAlignment.CENTER);
                setHeaderRowforSingleCell(row.getCell(1), orderObservableList.get(i).getpName(), true, ParagraphAlignment.LEFT);
                setHeaderRowforSingleCell(row.getCell(1), "("+ orderObservableList.get(i).getpSpecs() +")", false, ParagraphAlignment.LEFT);
                setHeaderRowforSingleCell(row.getCell(2), orderObservableList.get(i).getpDimension(), false, ParagraphAlignment.CENTER);
                setHeaderRowforSingleCell(row.getCell(3), orderObservableList.get(i).getpUnit(), false, ParagraphAlignment.CENTER);
                setHeaderRowforSingleCell(row.getCell(4), formatter.format(Float.parseFloat(orderObservableList.get(i).getpTotal()+""))+"", false, ParagraphAlignment.CENTER);
                setHeaderRowforSingleCell(row.getCell(5), orderObservableList.get(i).getWoName(), false, ParagraphAlignment.CENTER);
            }

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            paragraph.setSpacingLineRule(LineSpacingRule.AUTO);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
            run.setFontSize(10);

            table = doc.createTable(4, 2);
            table.setWidth("100%");
            row = table.getRow(0);
            row.getCell(0).setWidth("40%");
            row.getCell(1).setWidth("60%");
            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

            setHeaderRowforSingleCell(row.getCell(0), "Đặc điểm và qui cách", false, ParagraphAlignment.LEFT);
            setHeaderRowforSingleCell(row.getCell(1), "- Chất lượng: Đảm bảo đúng chất lượng và định lượng bao bì như mẫu chào hàng.", true, ParagraphAlignment.LEFT);
            setHeaderRowforSingleCell(row.getCell(1), "- Sản xuất theo mẫu xác nhận ngày: " + now.getDayOfMonth() + "/" + now.getMonthValue() + "/"+ now.getYear(), true, ParagraphAlignment.LEFT);
            setHeaderRowforSingleCell(row.getCell(1), "- Bao bì phải làm đúng kích thước, màu sắc, thông tin như đã xác nhận.", true, ParagraphAlignment.LEFT);
            setHeaderRowforSingleCell(row.getCell(1), "- Bao bì phải đạt tiêu chuẩn hàng Thủy sản xuất khẩu. Số lượng làm đủ, không thừa, không thiếu.", true, ParagraphAlignment.LEFT);
            setHeaderRowforSingleCell(row.getCell(1), "- Chúng tôi sẽ trả lại các lô hàng làm sai qui cách và không đúng các yêu cầu trên.", true, ParagraphAlignment.LEFT);
            setHeaderRowforSingleCell(row.getCell(1), "- Khi giao hàng vui lòng liên hệ: Mr. Duy (0932.830.900).", false, ParagraphAlignment.LEFT);

            row = table.getRow(1);
            setHeaderRowforSingleCell(row.getCell(0), "Ngày giao hàng", false, ParagraphAlignment.LEFT);

            row = table.getRow(2);
            setHeaderRowforSingleCell(row.getCell(0), "Địa chỉ giao hàng", false, ParagraphAlignment.LEFT);
            setHeaderRowforSingleCell(row.getCell(1), "Công ty CP SEAVINA", true, ParagraphAlignment.LEFT);
            setHeaderRowforSingleCell(row.getCell(1), "Lô 16A-18, KCN Trà Nóc 1, P. Trà Nóc, Q. Bình Thủy, TP. Cần Thơ", false, ParagraphAlignment.LEFT);

            row = table.getRow(3);
            setHeaderRowforSingleCell(row.getCell(0), "Chú ý", false, ParagraphAlignment.LEFT);
            setHeaderRowforSingleCell(row.getCell(1), "Mọi thay đổi hoặc có vấn đề gì chưa rõ phải báo lại ngay với Công Ty CP SEAVINA trước khi tiến hành.", false, ParagraphAlignment.LEFT);

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            paragraph.setSpacingLineRule(LineSpacingRule.AUTO);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
            run.setFontSize(10);
            run.addBreak();

            table = doc.createTable(1, 2);
            table.setWidth("100%");
            table.removeBorders();
            row = table.getRow(0);
            row.getCell(0).setWidth("50%");
            row.getCell(1).setWidth("50%");
            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

            setHeaderRowforSingleCell(row.getCell(0), "Bên bán hàng", false, ParagraphAlignment.LEFT);
            setHeaderRowforSingleCell(row.getCell(1), "Bên mua hàng", false, ParagraphAlignment.RIGHT);


            if(file != null) {
                // save it to .docx file
//                try (FileOutputStream out = new FileOutputStream(file.getPath())) // for fileChooser
                try (FileOutputStream out = new FileOutputStream(file.getPath()+"/"+ code + "-"+getListWorkOrderName()+".docx"))
                {
                    doc.write(out);
//                    utils.alert("info", Alert.AlertType.INFORMATION, "Xuất file thành công!", "File đã được lưu vào ổ đĩa!").showAndWait();
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void data2DocOfOrderListDraft(File file, WorkOrder workOrder) {
        String imgFile = "e:\\java_platform\\docs-data\\logo.jpg";
        String _fontFamily = "Arial";
        ObservableList<WorkProduction> workProductionObservableList = FXCollections.observableArrayList(workProductionDAO.getListByID(workOrder.getId()));

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
            row.getCell(0).setWidth("22%");
            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(1).setWidth("55%");
            row.getCell(1).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);
            row.getCell(2).setWidth("23%");
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
            setHeaderRowforSingleCell(row.getCell(1), "Công ty CP SEAVINA", true, ParagraphAlignment.CENTER);
//            setHeaderRowforSingleCell(row.getCell(1), "Lô 16A-18, KCN Trà Nóc I,P.Trà Nóc, Q. Bình Thủy, TP. Cần Thơ ,Việt Nam", true, ParagraphAlignment.CENTER);
//            setHeaderRowforSingleCell(row.getCell(1), "1801141886", true, ParagraphAlignment.CENTER);
            setHeaderRowforSingleCell(row.getCell(1), "Tel: 0292.3744950  Fax: 0292.3743678", true, ParagraphAlignment.CENTER);
//            setHeaderRowforSingleCell(row.getCell(1), "(Ms. Nhung: 0946.886 868, Ms Trinh: 0918.755729)", false, ParagraphAlignment.CENTER);
            setHeaderRowforSingleCell(row.getCell(2), "TT5.6.1/ KD2-BM3", false, ParagraphAlignment.CENTER);

            /*
             * Word content title
             * */

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.RIGHT);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            run.setText("SEAVINA, ngày " + now.getDayOfMonth() + ", tháng " + now.getMonthValue() + ", năm "+ now.getYear());
            run.addBreak();

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
            run.setBold(true);
            run.setFontSize(16);
            run.setText("KIỂM TRA BAO BÌ "+workOrder.getName());
            run.addBreak();

            table = doc.createTable(1, 6);
            table.setWidth("100%");

            row = table.getRow(0);
            row.getCell(0).setWidth("32%");
            row.getCell(1).setWidth("15%");
            row.getCell(2).setWidth("8%");
            row.getCell(3).setWidth("15%");
            row.getCell(4).setWidth("15%");
            row.getCell(5).setWidth("15%");
            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(3).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(4).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(5).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

            setHeaderRowforSingleCell(row.getCell(0), "Tên Bao Bì", false, ParagraphAlignment.CENTER);
            setHeaderRowforSingleCell(row.getCell(1), "Qui cách (CM)", false, ParagraphAlignment.CENTER);
            setHeaderRowforSingleCell(row.getCell(2), "ĐVT", false, ParagraphAlignment.CENTER);
            setHeaderRowforSingleCell(row.getCell(3), "SL Đặt", false, ParagraphAlignment.CENTER);
            setHeaderRowforSingleCell(row.getCell(4), "Tồn", false, ParagraphAlignment.CENTER);
            setHeaderRowforSingleCell(row.getCell(5), "Thực tế", false, ParagraphAlignment.CENTER);

            DecimalFormat formatter = new DecimalFormat("#,###");

            table = doc.createTable(workProductionObservableList.size(), 6);
            table.setWidth("100%");

            for(int i=0;i<workProductionObservableList.size();i++) {
                row = table.getRow(i);
                row.getCell(0).setWidth("32%");
                row.getCell(1).setWidth("15%");
                row.getCell(2).setWidth("8%");
                row.getCell(3).setWidth("15%");
                row.getCell(4).setWidth("15%");
                row.getCell(5).setWidth("15%");
                row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(3).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(4).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(5).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                setHeaderRowforSingleCell(row.getCell(0), workProductionObservableList.get(i).getPackagingName(), true, ParagraphAlignment.LEFT);
                setHeaderRowforSingleCell(row.getCell(0), "("+ workProductionObservableList.get(i).getPackagingSpecification() +")", false, ParagraphAlignment.LEFT);
                setHeaderRowforSingleCell(row.getCell(1), workProductionObservableList.get(i).getPackagingDimension(), false, ParagraphAlignment.CENTER);
                setHeaderRowforSingleCell(row.getCell(2), workProductionObservableList.get(i).getUnit(), false, ParagraphAlignment.CENTER);
                setHeaderRowforSingleCell(row.getCell(3), formatter.format(Float.parseFloat(workProductionObservableList.get(i).getWorkOrderQuantity()+""))+"", false, ParagraphAlignment.CENTER);
                setHeaderRowforSingleCell(row.getCell(4), formatter.format(Float.parseFloat(workProductionObservableList.get(i).getStock()+""))+"", false, ParagraphAlignment.CENTER);
                setHeaderRowforSingleCell(row.getCell(5), formatter.format(Float.parseFloat(workProductionObservableList.get(i).getActualQuantity()+""))+"", false, ParagraphAlignment.CENTER);
            }

            if(file != null) {
                // save it to .docx file
//                try (FileOutputStream out = new FileOutputStream(file.getPath())) // for fileChooser
                try (FileOutputStream out = new FileOutputStream(file.getPath()+"/KIEM TRA BB "+ workOrder.getName() +".docx"))
                {
                    doc.write(out);
//                    utils.alert("info", Alert.AlertType.INFORMATION, "Xuất file thành công!", "File đã được lưu vào ổ đĩa!").showAndWait();
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void setHeaderRowforSingleCell(XWPFTableCell cell, String text, boolean addBreak, ParagraphAlignment paragraphAlignment) {
        XWPFParagraph tempParagraph = cell.getParagraphs().get(0);
        tempParagraph.setIndentationLeft(100);
        tempParagraph.setIndentationRight(100);
        tempParagraph.setAlignment(paragraphAlignment != null ? paragraphAlignment : ParagraphAlignment.LEFT);
        XWPFRun tempRun = tempParagraph.createRun();
        tempRun.setFontFamily("Arial");
        tempRun.setFontSize(10);
        tempRun.setColor("000000");
        tempRun.setText(text);
        if (addBreak) {
            tempRun.addBreak();
        }
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
    }
}
