package fecoder.controllers;

import fecoder.DAO.WorkOrderDAO;
import fecoder.DAO.CustomerDAO;
import fecoder.DAO.YearDAO;
import fecoder.models.WorkOrder;
import fecoder.models.Customer;
import fecoder.models.Year;
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
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.converter.IntegerStringConverter;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

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

    private int currentRow;
    private String currentCell;
    private final Utils utils = new Utils();

    private boolean isEditableComboBox = false;
    private boolean isUpdating = false;

    private final WorkOrderDAO workOrderDAO = new WorkOrderDAO();
    private final YearDAO yearDAO = new YearDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

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
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Updating button action
     * */
    public void updateButton(ActionEvent actionEvent) {
        try {
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

        setContextMenu();

        utils.reloadTableViewOnChange(dataTable, currentRow, currentCell);

        setSearchField();
    }
}
