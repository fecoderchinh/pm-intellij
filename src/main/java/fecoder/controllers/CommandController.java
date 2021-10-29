package fecoder.controllers;

import fecoder.DAO.CommandDAO;
import fecoder.DAO.CustomerDAO;
import fecoder.DAO.YearDAO;
import fecoder.models.Command;
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

public class CommandController implements Initializable {

    public TextField commandName;
    public TextField commandLotNumber;
    public TextField commandPONumber;
    public ComboBox<Year> commandYear;
    public ComboBox<Customer> commandCustomer;
    public DatePicker commandSendDate;
    public DatePicker commandShippingDate;
    public TextField commandDestination;
    public TextField commandNote;
    public Button insertButton;
    public Button updateButton;
    public Button clearButton;
    public ComboBox<String> searchComboBox;
    public TextField searchField;
    public Button reloadData;
    public Label anchorLabel;
    public Label anchorData;

    public TableView<Command> dataTable;
    public TableColumn<Command, Integer> idColumn;
    public TableColumn<Command, String> commandSendDateColumn;
    public TableColumn<Command, String> commandNumberColumn;
    public TableColumn<Command, String> commandLotColumn;
    public TableColumn<Command, String> commandPOColumn;
    public TableColumn<Command, Integer> commandYearColumn;
    public TableColumn<Command, Integer> commandCustomerColumn;
    public TableColumn<Command, String> commandShippingDateColumn;
    public TableColumn<Command, String> commandDestinationColumn;
    public TableColumn<Command, String> commandNoteColumn;

    private int currentRow;
    private String currentCell;
    private final Utils utils = new Utils();

    private boolean isEditableComboBox = false;
    private boolean isUpdating = false;

    private final CommandDAO commandDAO = new CommandDAO();
    private final YearDAO yearDAO = new YearDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

    private final ObservableList<Year> yearObservableList = FXCollections.observableArrayList(yearDAO.getList());
    private final ObservableList<Customer> customerObservableList = FXCollections.observableArrayList(customerDAO.getList());
    private final ObservableList<Command> commandObservableList = FXCollections.observableArrayList(commandDAO.getList());

    /**
     * All needed to start controller
     * */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        commandYear.getItems().addAll(yearObservableList);
        yearComboBoxFilter();
        commandCustomer.getItems().addAll(customerObservableList);
        customerComboBoxFilter();
        loadView();
    }

    /**
     * Inserting button action
     * */
    public void insertButton(ActionEvent actionEvent) {
        try {
            commandDAO.insert(
                    commandName.getText(),
                    commandLotNumber.getText(),
                    commandPONumber.getText(),
                    yearDAO.getYear(commandYear.getValue()+"").getId(),
                    customerDAO.getCustomer(commandCustomer.getValue()+"").getId(),
                    commandSendDate.getEditor().getText(),
                    commandShippingDate.getEditor().getText(),
                    commandDestination.getText(),
                    commandNote.getText()
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
            commandDAO.update(
                    commandName.getText(),
                    commandLotNumber.getText(),
                    commandPONumber.getText(),
                    yearDAO.getYear(commandYear.getValue()+"").getId(),
                    customerDAO.getCustomer(commandCustomer.getValue()+"").getId(),
                    commandSendDate.getEditor().getText(),
                    commandShippingDate.getEditor().getText(),
                    commandDestination.getText(),
                    commandNote.getText(),
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

        commandName.setText("");
        commandLotNumber.setText("");
        commandPONumber.setText("");
        commandSendDate.setValue(null);
        commandShippingDate.setValue(null);
        commandDestination.setText("");
        commandNote.setText("");

        searchField.setText("");
    }

    /**
     * Handle on clearing comnbobox
     * */
    private void resetComboBox() {
        Year yearData = yearDAO.getDataByID(1);
        utils.setComboBoxValue(commandYear, yearData.getYear());

        Customer customerData = customerDAO.getDataByID(1);
        utils.setComboBoxValue(commandCustomer, customerData.getName());
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
        commandCustomer.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            dataFilteredList.setPredicate(item -> {

                this.isUpdating = false;

                // Check if the search term is contained anywhere in our list
                if (item.getName().toLowerCase().contains(newValue.toLowerCase().trim()) && !isUpdating) {
                    commandCustomer.show();
                    return true;
                }

                // No matches found
                return false;
            });

        });

        commandCustomer.getItems().removeAll(customerObservableList);
        commandCustomer.getItems().addAll(dataFilteredList);
    }

    /**
     * Hiding the ComboBox while on updating stage.
     * */
    private void hideComboBoxForUpdatingData() {
    }

    /**
     * Setting data for inputs
     *
     * @param command - the product data
     * */
    private void getData(Command command) {
        clearFields();
        commandName.setText(command.getName());
        commandLotNumber.setText(command.getLotNumber());
        commandPONumber.setText(command.getPurchaseOrder());
        commandSendDate.setValue(utils.getDate(command.getSendDate()));
        commandShippingDate.setValue(utils.getDate(command.getShippingDate()));

//        Year yearData = yearDAO.getDataByID(command.getYear());
//        utils.setComboBoxValue(commandYear, yearData.getYear());
//
//        Customer customerData = customerDAO.getDataByID(command.getCustomerId());
//        utils.setComboBoxValue(commandCustomer, customerData.getName());

        commandDestination.setText(command.getDestination());
        commandNote.setText(command.getNote());

        anchorLabel.setText("Current ID: ");
        anchorData.setText(""+command.getId());

        isUpdating = true;
    }

    /**
     * Handle on searching data
     * */
    public void setSearchField() {
        ObservableList<Command> list = FXCollections.observableArrayList(commandDAO.getList());
        FilteredList<Command> filteredList = new FilteredList<>(list, p -> true);

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

        SortedList<Command> sortedList = new SortedList<>(filteredList);
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
        dataTable.setRowFactory((TableView<Command> tableView) -> {
            final TableRow<Command> row = new TableRow<>();

            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem viewItem = new MenuItem("Chi tiết");
            final MenuItem editItem = new MenuItem("Cập nhật");
            final MenuItem removeItem = new MenuItem("Xóa dòng");

            viewItem.setOnAction((ActionEvent event) -> {
                Command command = dataTable.getSelectionModel().getSelectedItem();
                int rowIndex = dataTable.getSelectionModel().getSelectedIndex();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Chi tiết Lệnh sản xuất");
                alert.setHeaderText(command.getName());
                alert.setContentText(
                        "Số LSX: " + command.getName() + "\n"
                );

                alert.showAndWait();
            });
            contextMenu.getItems().add(viewItem);

            editItem.setOnAction((ActionEvent event) -> {
                Command command = dataTable.getSelectionModel().getSelectedItem();
                getData(command);
                hideComboBoxForUpdatingData();
            });
            contextMenu.getItems().add(editItem);

            removeItem.setOnAction((ActionEvent event) -> {
                Command command = dataTable.getSelectionModel().getSelectedItem();
                Alert alert = utils.alert("del", Alert.AlertType.CONFIRMATION, command.getName());

                Optional<ButtonType> result = alert.showAndWait();
                if (alert.getAlertType() == Alert.AlertType.CONFIRMATION && result.get() == ButtonType.OK){
                    commandDAO.delete(command.getId());
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
        utils.formatDate(commandSendDate, "dd-MM-yyyy");
        utils.formatDate(commandShippingDate, "dd-MM-yyyy");
    }

    /**
     * Reset fields
     * This will handle an enter event and mouse leave for these Nodes
     * */
    private void resetFields() {
        utils.disableKeyEnterOnTextField(commandName);
        utils.disableKeyEnterOnTextField(commandLotNumber);
        utils.disableKeyEnterOnTextField(commandPONumber);
        utils.disableKeyEnterOnTextFieldDatePicker(commandSendDate);
        utils.disableKeyEnterOnTextFieldDatePicker(commandShippingDate);

        utils.disableKeyEnterOnTextFieldComboBox(commandYear, false);
        utils.disableKeyEnterOnTextFieldComboBox(commandCustomer, true);

        utils.disableKeyEnterOnTextField(commandDestination);
        utils.disableKeyEnterOnTextField(commandNote);
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

        commandNumberColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        commandNumberColumn.setCellFactory(TextFieldTableCell.<Command>forTableColumn());
        commandNumberColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((Command) event.getTableView().getItems().get(event.getTablePosition().getRow())).setName(data);
            commandDAO.updateData("name", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        commandLotColumn.setCellValueFactory(new PropertyValueFactory<>("lotNumber"));
        commandLotColumn.setCellFactory(TextFieldTableCell.<Command>forTableColumn());
        commandLotColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((Command) event.getTableView().getItems().get(event.getTablePosition().getRow())).setLotNumber(data);
            commandDAO.updateData("lot_number", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        commandPOColumn.setCellValueFactory(new PropertyValueFactory<>("purchaseOrder"));
        commandPOColumn.setCellFactory(TextFieldTableCell.<Command>forTableColumn());
        commandPOColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((Command) event.getTableView().getItems().get(event.getTablePosition().getRow())).setPurchaseOrder(data);
            commandDAO.updateData("po_number", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        commandYearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        commandYearColumn.setCellFactory(TextFieldTableCell.<Command, Integer>forTableColumn(new IntegerStringConverter()));
        commandYearColumn.setCellFactory(tc -> {

            TableCell<Command, Integer> cell = new TableCell<>();

            currentRow = cell.getIndex();
            currentCell = cell.getText();

            Text text = new Text();
            ComboBox<Year> commandComboBoxTableCell = new ComboBox<>();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(commandYearColumn.widthProperty());
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
                            commandDAO.updateDataInteger("year", t1.getId(), selectedRow);
                            clearFields();
                            reload();
                        }
                    });
                }
            });

            return cell ;
        });

        commandCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        commandCustomerColumn.setCellFactory(TextFieldTableCell.<Command, Integer>forTableColumn(new IntegerStringConverter()));
        commandCustomerColumn.setCellFactory(tc -> {

            TableCell<Command, Integer> cell = new TableCell<>();

            currentRow = cell.getIndex();
            currentCell = cell.getText();

            Text text = new Text();
            ComboBox<Customer> commandComboBoxTableCell = new ComboBox<>();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(commandCustomerColumn.widthProperty());
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
                                commandDAO.updateDataInteger("customer_id", getCustomerFromName.getId(), selectedRow);
                                clearFields();
                                reload();
                            }
                        }
                    });
                }
            });

            return cell ;
        });

        commandSendDateColumn.setCellValueFactory(new PropertyValueFactory<>("sendDate"));
        commandSendDateColumn.setCellFactory(TextFieldTableCell.<Command>forTableColumn());
        commandSendDateColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((Command) event.getTableView().getItems().get(event.getTablePosition().getRow())).setSendDate(data);
            commandDAO.updateData("send_date", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        commandShippingDateColumn.setCellValueFactory(new PropertyValueFactory<>("shippingDate"));
        commandShippingDateColumn.setCellFactory(TextFieldTableCell.<Command>forTableColumn());
        commandShippingDateColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((Command) event.getTableView().getItems().get(event.getTablePosition().getRow())).setShippingDate(data);
            commandDAO.updateData("shipping_date", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        commandDestinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        commandDestinationColumn.setCellFactory(TextFieldTableCell.<Command>forTableColumn());
        commandDestinationColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((Command) event.getTableView().getItems().get(event.getTablePosition().getRow())).setDestination(data);
            commandDAO.updateData("destination", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        commandNoteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));
        commandNoteColumn.setCellFactory(TextFieldTableCell.<Command>forTableColumn());
        commandNoteColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((Command) event.getTableView().getItems().get(event.getTablePosition().getRow())).setNote(data);
            commandDAO.updateData("note", data, event.getRowValue().getId());
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
