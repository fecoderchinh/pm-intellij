package fecoder.controllers;

import fecoder.DAO.*;
import fecoder.models.*;
import fecoder.utils.Utils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class StatsController implements Initializable {

    public Label mainLabel;
    public ComboBox<String> searchComboBox;
    public TextField searchField;
    public Button reloadData;
    public TableView<WorkProduction> dataTable;
    public TableColumn<WorkProduction, String> datetimeCol;
    public TableColumn<WorkProduction, String> woNumberCol;
    public TableColumn<WorkProduction, String> pNameCol;
    public TableColumn<WorkProduction, String> pDimCol;
    public TableColumn<WorkProduction, Integer> pQuantityCol;
    public TableColumn<WorkProduction, Float> pStockCol;
    public TableColumn<WorkProduction, Integer> pActualQtyCol;
    public TableColumn<WorkProduction, Float> pPriceCol;
    public Label anchorLabel;
    public Label anchorData;

    private final WorkProductionDAO workProductionDAO = new WorkProductionDAO();

    /**
     * All needed to start controller
     * */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadView();
    }

    /**
     * Handle on searching data
     * */
    public void setSearchField() {
        ObservableList<WorkProduction> list = FXCollections.observableArrayList(workProductionDAO.getList());
        FilteredList<WorkProduction> filteredList = new FilteredList<>(list, p -> true);

        searchField.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    switch (searchComboBox.getValue()) {
                        case "Ngày đặt":
                            filteredList.setPredicate(str -> {
                                if (newValue == null || newValue.isEmpty())
                                    return true;
                                String lowerCaseFilter = newValue.toLowerCase();
                                return str.getOrderDate().toLowerCase().contains
                                        (lowerCaseFilter);
                            });
                            break;
                        case "Lệnh":
                            filteredList.setPredicate(str -> {
                                if (newValue == null || newValue.isEmpty())
                                    return true;
                                String lowerCaseFilter = newValue.toLowerCase();
                                return str.getWorkOrderName().toLowerCase().contains
                                        (lowerCaseFilter);
                            });
                            break;
                        case "Tên bao bì":
                            filteredList.setPredicate(str -> {
                                if (newValue == null || newValue.isEmpty())
                                    return true;
                                String lowerCaseFilter = newValue.toLowerCase();
                                return str.getPackagingName().toLowerCase().contains
                                        (lowerCaseFilter);
                            });
                            break;
                        case "Qui cách":
                            filteredList.setPredicate(str -> {
                                if (newValue == null || newValue.isEmpty())
                                    return true;
                                String lowerCaseFilter = newValue.toLowerCase();
                                return str.getProductName().toLowerCase().contains
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

        SortedList<WorkProduction> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(dataTable.comparatorProperty());

        dataTable.setItems(sortedList);
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
    private void loadView(){
        dataTable.setEditable(true);

        datetimeCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        woNumberCol.setCellValueFactory(new PropertyValueFactory<>("workOrderName"));
        pNameCol.setCellValueFactory(new PropertyValueFactory<>("packagingName"));
        pDimCol.setCellValueFactory(new PropertyValueFactory<>("packagingDimension"));
        pQuantityCol.setCellValueFactory(new PropertyValueFactory<>("packQuantity"));
        pStockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        pActualQtyCol.setCellValueFactory(new PropertyValueFactory<>("actualQuantity"));
        pPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        dataTable.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ESCAPE || event.getCode() == KeyCode.F5) {
                dataTable.refresh();
            }
        });

        dataTable.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );

        setSearchField();
    }

    public void reloadData(ActionEvent actionEvent) {
    }
}
