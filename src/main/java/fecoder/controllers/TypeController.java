package fecoder.controllers;

import fecoder.DAO.TypeDAO;
import fecoder.models.Type;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.net.URL;
import java.util.ResourceBundle;

public class TypeController implements Initializable {
    public Button insertButton;
    public Button updateButton;
    public Button clearButton;
    public TextField nameField;
    public TextField searchField;
    public Button reloadData;
    public TableView<Type> dataTable;
    public TableColumn<Type, Integer> idColumn;
    public TableColumn<Type, String> nameColumn;
    public TableColumn<Type, String> unitColumn;
    public Label anchorLabel;
    public Label anchorData;

    private final TypeDAO typeDAO = new TypeDAO();
    public TextField unitField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadView();
    }

    @FXML
    public void insertButton(ActionEvent actionEvent) {
        typeDAO.insert(nameField.getText(), unitField.getText());
        clearFields();
        reload();
    }

    @FXML
    public void updateButton(ActionEvent actionEvent) {
        typeDAO.update(nameField.getText(), unitField.getText(), Integer.parseInt(anchorData.getText()));
        clearFields();
        reload();
    }

    @FXML
    public void clearButton(ActionEvent actionEvent) {
        clearFields();
    }

    public void reloadData(ActionEvent actionEvent) {
        reload();
    }

    public void loadView() {
        ObservableList<Type> list = FXCollections.observableArrayList(typeDAO.getList());
        FilteredList<Type> filteredList = new FilteredList<>(list, p -> true);

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

        SortedList<Type> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(dataTable.comparatorProperty());

        dataTable.setEditable(true);

        TableView.TableViewSelectionModel<Type> selectionModel = dataTable.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        ObservableList<Type> getSelectedItems = selectionModel.getSelectedItems();

        getSelectedItems.addListener(new ListChangeListener<Type>() {
            @Override
            public void onChanged(Change<? extends Type> change) {
//                System.out.println("Selection changed: " + change.getList());
            }
        });

        idColumn.setSortable(false);
        idColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Integer>(dataTable.getItems().indexOf(column.getValue())+1));

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellFactory(TextFieldTableCell.<Type>forTableColumn());
        nameColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((Type) event.getTableView().getItems().get(event.getTablePosition().getRow())).setName(data);
            typeDAO.updateData("name", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        unitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));
        unitColumn.setCellFactory(TextFieldTableCell.<Type>forTableColumn());
        unitColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((Type) event.getTableView().getItems().get(event.getTablePosition().getRow())).setName(data);
            typeDAO.updateData("unit", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        dataTable.setRowFactory((TableView<Type> tableView) -> {
            final TableRow<Type> row = new TableRow<>();

            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem viewItem = new MenuItem("Chi tiết");
            final MenuItem editItem = new MenuItem("Cập nhật");
            final MenuItem removeItem = new MenuItem("Xóa dòng");

            viewItem.setOnAction((ActionEvent event) -> {
                Type suplier = dataTable.getSelectionModel().getSelectedItem();
                int rowIndex = dataTable.getSelectionModel().getSelectedIndex();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Chi tiết loại bao bì");
                alert.setHeaderText(suplier.getName());
                alert.setContentText(
                        "Tên loại: " + suplier.getName() + "\n" +
                        "Đơn vị tính: " + suplier.getUnit()
                );

                alert.showAndWait();
            });
            contextMenu.getItems().add(viewItem);

            editItem.setOnAction((ActionEvent event) -> {
                Type data = dataTable.getSelectionModel().getSelectedItem();
                getItem(data.getName(), data.getUnit(), data.getId());
            });
            contextMenu.getItems().add(editItem);

            removeItem.setOnAction((ActionEvent event) -> {
                Type data = dataTable.getSelectionModel().getSelectedItem();
                typeDAO.delete(data.getId());
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

    private void reload() {
        loadView();
        clearFields();
    }

    private void getItem(String name, String unit, int id) {
        nameField.setText(name);
        unitField.setText(unit);
        anchorLabel.setText("Current ID: ");
        anchorData.setText(""+id);
    }

    private void clearFields() {
        nameField.setText(null);
        unitField.setText(null);
        anchorLabel.setText(null);
        anchorData.setText(null);
    }
}
