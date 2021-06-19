package fecoder.controllers;

import fecoder.DAO.YearDAO;
import fecoder.models.Year;
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

public class YearController implements Initializable {
    public TextField yearField;
    public Button insertButton;
    public Button updateButton;
    public Button clearButton;
    public TextField searchField;
    public Button reloadData;
    public TableView<Year> dataTable;
    public TableColumn<Year, Integer> idColumn;
    public TableColumn<Year, String> yearColumn;
    public Label anchorLabel;
    public Label anchorData;

    private final YearDAO yearDAO = new YearDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadView();
    }

    public void insertButton(ActionEvent actionEvent) {
        yearDAO.insert(yearField.getText());
        clearFields();
        reload();
    }

    public void updateButton(ActionEvent actionEvent) {
        yearDAO.update(yearField.getText(), Integer.parseInt(anchorData.getText()));
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

    private void getItem(String year, int id) {
        yearField.setText(year);
        anchorLabel.setText("Current ID: ");
        anchorData.setText(""+id);
    }

    private void clearFields() {
        yearField.setText(null);
        anchorLabel.setText(null);
        anchorData.setText(null);
    }

    public void loadView() {
        ObservableList<Year> list = FXCollections.observableArrayList(yearDAO.getList());
        FilteredList<Year> filteredList = new FilteredList<>(list, p -> true);

        searchField.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    filteredList.setPredicate(str -> {
                        if (newValue == null || newValue.isEmpty())
                            return true;
                        String lowerCaseFilter = newValue.toLowerCase();
                        return str.getYear().toLowerCase().contains
                                (lowerCaseFilter);
                    });
                });

        SortedList<Year> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(dataTable.comparatorProperty());

        dataTable.setEditable(true);

        TableView.TableViewSelectionModel<Year> selectionModel = dataTable.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        ObservableList<Year> getSelectedItems = selectionModel.getSelectedItems();

        getSelectedItems.addListener(new ListChangeListener<Year>() {
            @Override
            public void onChanged(Change<? extends Year> change) {
//                System.out.println("Selection changed: " + change.getList());
            }
        });

        idColumn.setSortable(false);
        idColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Integer>(dataTable.getItems().indexOf(column.getValue())+1));

        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        yearColumn.setCellFactory(TextFieldTableCell.<Year>forTableColumn());
        yearColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((Year) event.getTableView().getItems().get(event.getTablePosition().getRow())).setYear(data);
            yearDAO.updateData("year", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        dataTable.setRowFactory((TableView<Year> tableView) -> {
            final TableRow<Year> row = new TableRow<>();

            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem viewItem = new MenuItem("Chi tiết");
            final MenuItem editItem = new MenuItem("Cập nhật");
            final MenuItem removeItem = new MenuItem("Xóa dòng");

            viewItem.setOnAction((ActionEvent event) -> {
                Year data = dataTable.getSelectionModel().getSelectedItem();
                int rowIndex = dataTable.getSelectionModel().getSelectedIndex();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Chi tiết kích thước");
                alert.setHeaderText(data.getYear());
                alert.setContentText(
                        "Năm phát hành: " + data.getYear() +"\n"
                );

                alert.showAndWait();
            });
            contextMenu.getItems().add(viewItem);

            editItem.setOnAction((ActionEvent event) -> {
                Year data = dataTable.getSelectionModel().getSelectedItem();
                getItem(data.getYear(), data.getId());
            });
            contextMenu.getItems().add(editItem);

            removeItem.setOnAction((ActionEvent event) -> {
                Year data = dataTable.getSelectionModel().getSelectedItem();
                yearDAO.delete(data.getId());
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
