package fecoder.controllers;

import fecoder.DAO.CustomerDAO;
import fecoder.models.Customer;
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

public class CustomerController implements Initializable {
    public TextField nameField;
    public TextField noteField;
    public Button insertButton;
    public Button updateButton;
    public Button clearButton;
    public TextField searchField;
    public Button reloadData;
    public Label anchorLabel;
    public Label anchorData;

    public TableView<Customer> dataTable;
    public TableColumn<Customer, Integer> idColumn;
    public TableColumn<Customer, String> nameColumn;
    public TableColumn<Customer, String> noteColumn;

    private final CustomerDAO DAO = new CustomerDAO();

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
        DAO.insert(nameField.getText(), noteField.getText());
        clearFields();
        reload();
    }

    /**
     * Updating button action
     * */
    public void updateButton(ActionEvent actionEvent) {
        DAO.update(nameField.getText(), noteField.getText(), Integer.parseInt(anchorData.getText()));
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
     * @param name - the record's name
     * @param note - the record's note
     * @param id - the record's id
     * */
    private void getItem(String name, String note, int id) {
        nameField.setText(name);
        noteField.setText(note);
        anchorLabel.setText("ID Selected: ");
        anchorData.setText(""+id);
    }

    /**
     * Handle on clearing specific inputs
     * */
    private void clearFields() {
        nameField.setText(null);
        noteField.setText(null);
        anchorLabel.setText("No ID Selected");
        anchorData.setText(null);
    }

    /**
     * Handle on searching data
     * */
    public void setSearchField() {
        ObservableList<Customer> list = FXCollections.observableArrayList(DAO.getList());
        FilteredList<Customer> filteredList = new FilteredList<>(list, p -> true);

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

        nameField.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    filteredList.setPredicate(str -> {
                        if (newValue == null || newValue.isEmpty())
                            return true;
                        String lowerCaseFilter = newValue.toLowerCase();
                        return str.getName().toLowerCase().contains
                                (lowerCaseFilter);
                    });
                });

        SortedList<Customer> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(dataTable.comparatorProperty());

        dataTable.setItems(sortedList);
    }

    /**
     * Getting current row on click
     * */
    public void getCurrentRow() {
        TableView.TableViewSelectionModel<Customer> selectionModel = dataTable.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        ObservableList<Customer> getSelectedItems = selectionModel.getSelectedItems();

        getSelectedItems.addListener(new ListChangeListener<Customer>() {
            @Override
            public void onChanged(Change<? extends Customer> change) {
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
        nameColumn.setCellFactory(TextFieldTableCell.<Customer>forTableColumn());
        nameColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((Customer) event.getTableView().getItems().get(event.getTablePosition().getRow())).setName(data);
            DAO.updateData("name", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        noteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));
        noteColumn.setCellFactory(TextFieldTableCell.<Customer>forTableColumn());
        noteColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((Customer) event.getTableView().getItems().get(event.getTablePosition().getRow())).setNote(data);
            DAO.updateData("note", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        dataTable.setRowFactory((TableView<Customer> tableView) -> {
            final TableRow<Customer> row = new TableRow<>();

            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem viewItem = new MenuItem("Chi ti???t");
            final MenuItem editItem = new MenuItem("C???p nh???t");
            final MenuItem removeItem = new MenuItem("X??a d??ng");

            viewItem.setOnAction((ActionEvent event) -> {
                Customer data = dataTable.getSelectionModel().getSelectedItem();
                int rowIndex = dataTable.getSelectionModel().getSelectedIndex();

                utils.alert("info", Alert.AlertType.INFORMATION, "Chi ti???t "+data.getName(),
                        "T??n Kh??ch H??ng: " + data.getName() + "\n" +
                        "Ghi ch??: " + data.getNote()
                ).showAndWait();
            });
            contextMenu.getItems().add(viewItem);

            removeItem.setOnAction((ActionEvent event) -> {
                Customer data = dataTable.getSelectionModel().getSelectedItem();
                Alert alert = utils.alert("del", Alert.AlertType.CONFIRMATION, "X??a: "+ data.getName(), null);

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
