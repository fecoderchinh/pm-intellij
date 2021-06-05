package fecoder.controllers;

import fecoder.DAO.PackagingTypeDAO;
import fecoder.models.PackagingType;
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

public class PackagingTypeController implements Initializable {
    public Button insertButton;
    public Button updateButton;
    public Button clearButton;
    public TextField nameField;
    public TextField searchField;
    public Button reloadData;
    public TableView<PackagingType> dataTable;
    public TableColumn<PackagingType, Integer> idColumn;
    public TableColumn<PackagingType, String> nameColumn;
    public Label anchorLabel;
    public Label anchorData;

    private final PackagingTypeDAO packagingTypeDAO = new PackagingTypeDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadView();
    }

    @FXML
    public void insertButton(ActionEvent actionEvent) {
        packagingTypeDAO.insert(nameField.getText());
        clearFields();
        reload();
    }

    @FXML
    public void updateButton(ActionEvent actionEvent) {
        packagingTypeDAO.update(nameField.getText(), Integer.parseInt(anchorData.getText()));
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
        ObservableList<PackagingType> list = FXCollections.observableArrayList(packagingTypeDAO.getTypes());
        FilteredList<PackagingType> filteredList = new FilteredList<>(list, p -> true);

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

        SortedList<PackagingType> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(dataTable.comparatorProperty());

        dataTable.setEditable(true);

        TableView.TableViewSelectionModel<PackagingType> selectionModel = dataTable.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        ObservableList<PackagingType> getSelectedItems = selectionModel.getSelectedItems();

        getSelectedItems.addListener(new ListChangeListener<PackagingType>() {
            @Override
            public void onChanged(Change<? extends PackagingType> change) {
//                System.out.println("Selection changed: " + change.getList());
            }
        });

        idColumn.setSortable(false);
        idColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Integer>(dataTable.getItems().indexOf(column.getValue())+1));

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellFactory(TextFieldTableCell.<PackagingType>forTableColumn());
        nameColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((PackagingType) event.getTableView().getItems().get(event.getTablePosition().getRow())).setName(data);
            packagingTypeDAO.updateData("name", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        dataTable.setRowFactory((TableView<PackagingType> tableView) -> {
            final TableRow<PackagingType> row = new TableRow<>();

            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem viewItem = new MenuItem("Chi tiết");
            final MenuItem editItem = new MenuItem("Cập nhật");
            final MenuItem removeItem = new MenuItem("Xóa dòng");

            viewItem.setOnAction((ActionEvent event) -> {
                PackagingType suplier = dataTable.getSelectionModel().getSelectedItem();
                int rowIndex = dataTable.getSelectionModel().getSelectedIndex();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Chi tiết loại bao bì");
                alert.setHeaderText(suplier.getName());
                alert.setContentText(
                        "Tên loại: " + suplier.getName()
                );

                alert.showAndWait();
            });
            contextMenu.getItems().add(viewItem);

            editItem.setOnAction((ActionEvent event) -> {
                PackagingType data = dataTable.getSelectionModel().getSelectedItem();
                getItem(data.getName(), data.getId());
            });
            contextMenu.getItems().add(editItem);

            removeItem.setOnAction((ActionEvent event) -> {
                PackagingType data = dataTable.getSelectionModel().getSelectedItem();
                packagingTypeDAO.delete(data.getId());
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

    private void getItem(String name, int id) {
        nameField.setText(name);
        anchorLabel.setText("Current ID: ");
        anchorData.setText(""+id);
    }

    private void clearFields() {
        nameField.setText(null);
        anchorLabel.setText(null);
        anchorData.setText(null);
    }
}
