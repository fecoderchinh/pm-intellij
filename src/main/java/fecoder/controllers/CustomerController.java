package fecoder.controllers;

import fecoder.DAO.CustomerDAO;
import fecoder.models.Customer;
import fecoder.models.Customer;
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
import java.util.ResourceBundle;

public class CustomerController implements Initializable {
    public TextField nameField;
    public TextField noteField;
    public Button insertButton;
    public Button updateButton;
    public Button clearButton;
    public TextField searchField;
    public Button reloadData;
    public TableView<Customer> dataTable;
    public TableColumn<Customer, Integer> idColumn;
    public TableColumn<Customer, String> nameColumn;
    public TableColumn<Customer, String> noteColumn;
    public Label anchorLabel;
    public Label anchorData;

    private final CustomerDAO DAO = new CustomerDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadView();
    }

    public void insertButton(ActionEvent actionEvent) {
        DAO.insert(nameField.getText(), noteField.getText());
        clearFields();
        reload();
    }

    public void updateButton(ActionEvent actionEvent) {
        DAO.update(nameField.getText(), noteField.getText(), Integer.parseInt(anchorData.getText()));
        clearFields();
        reload();
    }

    public void clearButton(ActionEvent actionEvent) {
        clearFields();
    }

    public void reloadData(ActionEvent actionEvent) {
        reload();
    }

    private void reload() {
        loadView();
        clearFields();
    }

    private void getItem(String name, String note, int id) {
        nameField.setText(name);
        noteField.setText(note);
        anchorLabel.setText("Current ID: ");
        anchorData.setText(""+id);
    }

    private void clearFields() {
        nameField.setText(null);
        noteField.setText(null);
        anchorLabel.setText(null);
        anchorData.setText(null);
    }

    public void loadView() {
        ObservableList<Customer> list = FXCollections.observableArrayList(DAO.getLists());
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

        SortedList<Customer> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(dataTable.comparatorProperty());

        dataTable.setEditable(true);

        TableView.TableViewSelectionModel<Customer> selectionModel = dataTable.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        ObservableList<Customer> getSelectedItems = selectionModel.getSelectedItems();

        getSelectedItems.addListener(new ListChangeListener<Customer>() {
            @Override
            public void onChanged(Change<? extends Customer> change) {
//                System.out.println("Selection changed: " + change.getList());
            }
        });

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
            ((Customer) event.getTableView().getItems().get(event.getTablePosition().getRow())).setName(data);
            DAO.updateData("note", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        dataTable.setRowFactory((TableView<Customer> tableView) -> {
            final TableRow<Customer> row = new TableRow<>();

            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem viewItem = new MenuItem("Chi tiết");
            final MenuItem editItem = new MenuItem("Cập nhật");
            final MenuItem removeItem = new MenuItem("Xóa dòng");

            viewItem.setOnAction((ActionEvent event) -> {
                Customer suplier = dataTable.getSelectionModel().getSelectedItem();
                int rowIndex = dataTable.getSelectionModel().getSelectedIndex();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Chi tiết loại bao bì");
                alert.setHeaderText(suplier.getName());
                alert.setContentText(
                        "Tên Khách Hàng: " + suplier.getName() + "\n" +
                                "Ghi chú: " + suplier.getNote()
                );

                alert.showAndWait();
            });
            contextMenu.getItems().add(viewItem);

            editItem.setOnAction((ActionEvent event) -> {
                Customer data = dataTable.getSelectionModel().getSelectedItem();
                getItem(data.getName(), data.getNote(), data.getId());
            });
            contextMenu.getItems().add(editItem);

            removeItem.setOnAction((ActionEvent event) -> {
                Customer data = dataTable.getSelectionModel().getSelectedItem();
                DAO.delete(data.getId());
                reload();
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

        dataTable.setItems(sortedList);
    }
}
