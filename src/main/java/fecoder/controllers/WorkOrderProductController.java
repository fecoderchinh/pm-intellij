package fecoder.controllers;

import fecoder.models.*;
import fecoder.utils.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

import java.net.URL;
import java.util.ResourceBundle;

public class WorkOrderProductController implements Initializable {
    public Label mainLabel;
    public TextField ordinalNumberField;
    public ComboBox<Product> productComboBox;
    public TextField qtyField;
    public Button insertButton;
    public Button updateButton;
    public Button clearButton;
    public ComboBox<String> searchComboBox;
    public TextField searchField;
    public Button reloadData;

    public TableView<Product> productTableView;
    public TableColumn<Product, String> productIdColumn;
    public TableColumn<Product, String> productNameColumn;

    public TableView<WorkProduction> dataTable;
    public TableColumn<WorkProduction, Integer> idColumn;
    public TableColumn<WorkProduction, String> packagingNameColumn;
    public TableColumn<WorkProduction, String> packagingSpecificationColumn;
    public TableColumn<WorkProduction, String> packagingDimensionColumn;
    public TableColumn<WorkProduction, String> packagingSuplierColumn;
    public TableColumn<WorkProduction, String> packagingCodeColumn;
    public TableColumn<WorkProduction, String> unitColumn;
    public TableColumn<WorkProduction, String> printStatusColumn;
    public TableColumn<WorkProduction, Integer> packQuantityColumn;
    public TableColumn<WorkProduction, Float> workOrderQuantityColumn;
    public TableColumn<WorkProduction, Float> stockColumn;
    public TableColumn<WorkProduction, Float> actualQuantityColumn;
    public TableColumn<WorkProduction, Float> residualQuantityColumn;
    public TableColumn<WorkProduction, Float> totalResidualQuantityColumn;
    public TableColumn<WorkProduction, String> noteProductColumn;
    
    public Label anchorLabel;
    public Label anchorData;

    private int currentRow;
    private String currentCell;
    private final Utils utils = new Utils();

    private WorkOrder innerData;

    public void insertButton(ActionEvent actionEvent) {
    }

    public void updateButton(ActionEvent actionEvent) {
    }

    public void clearButton(ActionEvent actionEvent) {
    }

    public void reloadData(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        filterComboBox();
        loadView();
    }

    /**
     * Load the current view resources.
     * <br>
     * Contains: <br>
     * - resetFields() <br>
     * - resetComboBox() <br>
     * - setContextMenu() <br>
     * - setSearchField() <br>
     * - Controlling columns view and actions <br>
     * - Implementing contextMenu on right click <br>
     * */
    private void loadView() {
//        resetFields();

//        resetComboBox();

        dataTable.setEditable(true);

        idColumn.setSortable(false);
        idColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Integer>(dataTable.getItems().indexOf(column.getValue())+1));

//        setContextMenu();

        utils.reloadTableViewOnChange(dataTable, currentRow, currentCell);
    }

    public void setData(WorkOrder workOrder) {
        this.innerData = workOrder;
        mainLabel.setText(this.innerData.getName());
    }
}
