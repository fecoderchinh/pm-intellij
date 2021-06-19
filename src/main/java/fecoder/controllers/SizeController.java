package fecoder.controllers;

import fecoder.DAO.SizeDAO;
import fecoder.models.Size;
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

public class SizeController implements Initializable {
    public TextField sizeField;
    public Button insertButton;
    public Button updateButton;
    public Button clearButton;
    public TextField searchField;
    public Button reloadData;
    public TableView<Size> dataTable;
    public TableColumn<Size, Integer> idColumn;
    public TableColumn<Size, String> sizeColumn;
    public Label anchorLabel;
    public Label anchorData;

    private final SizeDAO DAO = new SizeDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadView();
    }

    public void insertButton(ActionEvent actionEvent) {
        DAO.insert(sizeField.getText());
        clearFields();
        reload();
    }

    public void updateButton(ActionEvent actionEvent) {
        DAO.update(sizeField.getText(), Integer.parseInt(anchorData.getText()));
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

    private void getItem(String data, int id) {
        sizeField.setText(data);
        anchorLabel.setText("Current ID: ");
        anchorData.setText(""+id);
    }

    private void clearFields() {
        sizeField.setText(null);
        anchorLabel.setText(null);
        anchorData.setText(null);
    }

    public void loadView() {
        ObservableList<Size> list = FXCollections.observableArrayList(DAO.getList());
        FilteredList<Size> filteredList = new FilteredList<>(list, p -> true);

        searchField.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    filteredList.setPredicate(str -> {
                        if (newValue == null || newValue.isEmpty())
                            return true;
                        String lowerCaseFilter = newValue.toLowerCase();
                        return str.getSize().toLowerCase().contains
                                (lowerCaseFilter);
                    });
                });

        SortedList<Size> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(dataTable.comparatorProperty());

        dataTable.setEditable(true);

        TableView.TableViewSelectionModel<Size> selectionModel = dataTable.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        ObservableList<Size> getSelectedItems = selectionModel.getSelectedItems();

        getSelectedItems.addListener(new ListChangeListener<Size>() {
            @Override
            public void onChanged(Change<? extends Size> change) {
//                System.out.println("Selection changed: " + change.getList());
            }
        });

        idColumn.setSortable(false);
        idColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Integer>(dataTable.getItems().indexOf(column.getValue())+1));

        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        sizeColumn.setCellFactory(TextFieldTableCell.<Size>forTableColumn());
        sizeColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((Size) event.getTableView().getItems().get(event.getTablePosition().getRow())).setSize(data);
            DAO.updateData("size", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        dataTable.setRowFactory((TableView<Size> tableView) -> {
            final TableRow<Size> row = new TableRow<>();

            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem viewItem = new MenuItem("Chi tiết");
            final MenuItem editItem = new MenuItem("Cập nhật");
            final MenuItem removeItem = new MenuItem("Xóa dòng");

            viewItem.setOnAction((ActionEvent event) -> {
                Size data = dataTable.getSelectionModel().getSelectedItem();
                int rowIndex = dataTable.getSelectionModel().getSelectedIndex();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Chi tiết size");
                alert.setHeaderText(data.getSize());
                alert.setContentText(
                        "Size: " + data.getSize() +"\n"
                );

                alert.showAndWait();
            });
            contextMenu.getItems().add(viewItem);

            editItem.setOnAction((ActionEvent event) -> {
                Size data = dataTable.getSelectionModel().getSelectedItem();
                getItem(data.getSize(), data.getId());
            });
            contextMenu.getItems().add(editItem);

            removeItem.setOnAction((ActionEvent event) -> {
                Size data = dataTable.getSelectionModel().getSelectedItem();
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
