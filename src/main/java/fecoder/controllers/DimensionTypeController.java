package fecoder.controllers;

import fecoder.DAO.DimensionTypeDAO;
import fecoder.models.DimensionType;
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

public class DimensionTypeController implements Initializable {
    public TextField nameField;
    public TextField unitField;
    public Button insertButton;
    public Button updateButton;
    public Button clearButton;
    public TextField searchField;
    public Button reloadData;
    public TableView<DimensionType> dataTable;
    public TableColumn<DimensionType, Integer> idColumn;
    public TableColumn<DimensionType, String> nameColumn;
    public TableColumn<DimensionType, String> unitColumn;
    public Label anchorLabel;
    public Label anchorData;

    private final DimensionTypeDAO dimensionTypeDAO = new DimensionTypeDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadView();
    }

    public void insertButton(ActionEvent actionEvent) {
        dimensionTypeDAO.insert(nameField.getText(), unitField.getText());
        clearFields();
        reload();
    }

    public void updateButton(ActionEvent actionEvent) {
        dimensionTypeDAO.update(nameField.getText(), unitField.getText(), Integer.parseInt(anchorData.getText()));
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

    public void loadView() {
        ObservableList<DimensionType> list = FXCollections.observableArrayList(dimensionTypeDAO.getDimensionTypes());
        FilteredList<DimensionType> filteredList = new FilteredList<>(list, p -> true);

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

        SortedList<DimensionType> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(dataTable.comparatorProperty());

        dataTable.setEditable(true);

        TableView.TableViewSelectionModel<DimensionType> selectionModel = dataTable.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        ObservableList<DimensionType> getSelectedItems = selectionModel.getSelectedItems();

        getSelectedItems.addListener(new ListChangeListener<DimensionType>() {
            @Override
            public void onChanged(Change<? extends DimensionType> change) {
//                System.out.println("Selection changed: " + change.getList());
            }
        });

        idColumn.setSortable(false);
        idColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Integer>(dataTable.getItems().indexOf(column.getValue())+1));

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellFactory(TextFieldTableCell.<DimensionType>forTableColumn());
        nameColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((DimensionType) event.getTableView().getItems().get(event.getTablePosition().getRow())).setName(data);
            dimensionTypeDAO.updateData("name", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        unitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));
        unitColumn.setCellFactory(TextFieldTableCell.<DimensionType>forTableColumn());
        unitColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((DimensionType) event.getTableView().getItems().get(event.getTablePosition().getRow())).setUnit(data);
            dimensionTypeDAO.updateData("unit", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        dataTable.setRowFactory((TableView<DimensionType> tableView) -> {
            final TableRow<DimensionType> row = new TableRow<>();

            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem viewItem = new MenuItem("Chi tiết");
            final MenuItem editItem = new MenuItem("Cập nhật");
            final MenuItem removeItem = new MenuItem("Xóa dòng");

            viewItem.setOnAction((ActionEvent event) -> {
                DimensionType data = dataTable.getSelectionModel().getSelectedItem();
                int rowIndex = dataTable.getSelectionModel().getSelectedIndex();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Chi tiết kích thước");
                alert.setHeaderText(data.getName());
                alert.setContentText(
                        "Tên loại: " + data.getName() +"\n"+
                        "Đơn vị: " + data.getUnit()
                );

                alert.showAndWait();
            });
            contextMenu.getItems().add(viewItem);

            editItem.setOnAction((ActionEvent event) -> {
                DimensionType data = dataTable.getSelectionModel().getSelectedItem();
                getItem(data.getName(), data.getUnit(), data.getId());
            });
            contextMenu.getItems().add(editItem);

            removeItem.setOnAction((ActionEvent event) -> {
                DimensionType data = dataTable.getSelectionModel().getSelectedItem();
                dimensionTypeDAO.delete(data.getId());
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
