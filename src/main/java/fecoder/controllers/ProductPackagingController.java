package fecoder.controllers;

import fecoder.DAO.*;
import fecoder.models.*;
import fecoder.utils.Utils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProductPackagingController implements Initializable {
    public Label mainLabel;
    public ComboBox<Size> sizeComboBox;
    public ComboBox<Packaging> packagingComboBox;
    public TextField packQtyField;
    public Button insertButton;
    public Button updateButton;
    public Button clearButton;
    public ComboBox<String> searchComboBox;
    public TextField searchField;
    public Button reloadData;
    public TableView<PackagingOwnerString> dataTable;
    public TableColumn<PackagingOwnerString, Integer> idColumn;
    public TableColumn<PackagingOwnerString, Size> sizeColumn;
    public TableColumn<PackagingOwnerString, String> packagingColumn;
    public TableColumn<PackagingOwnerString, Integer> packingQtyColumn;
    public Label anchorLabel;
    public Label anchorData;

    private boolean isEditableComboBox = false;
    private boolean isUpdating = false;

    private final PackagingOwnerDAO packagingOwnerDAO = new PackagingOwnerDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final SizeDAO sizeDAO = new SizeDAO();
    private final PackagingDAO packagingDAO = new PackagingDAO();
    private final PackagingOwnerStringDAO packagingOwnerStringDAO = new PackagingOwnerStringDAO();

    private final ObservableList<Size> sizeObservableList = FXCollections.observableArrayList(sizeDAO.getList());
    private final ObservableList<Packaging> packagingObservableList = FXCollections.observableArrayList(packagingDAO.getList());

    private int currentRow;
    private String currentCell;
    private final Utils utils = new Utils();

    private Product product;

    /**
     * All needed to start controller
     * */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        filterComboBox();
        loadView();
    }

    public void insertButton(ActionEvent actionEvent) {
        String sizeValue = utils.getComboBoxValue(sizeComboBox);
        String packagingValue = utils.getComboBoxValue(packagingComboBox);
        Size sizeInsertData = sizeDAO.getDataByName(sizeValue);
        Packaging packagingInsertData = packagingDAO.getDataByName(packagingValue);
        try {
            packagingOwnerDAO.insert(this.product.getId(), sizeInsertData.getId(), packagingInsertData.getId(), Integer.parseInt(packQtyField.getText()));
            clearFields();
            reload();
        } catch (NumberFormatException e) {
            utils.alert("err", Alert.AlertType.ERROR, "Đã xảy ra lỗi!", "Số lượng đóng gói không được để trống hoặc nhỏ hơn 1");
        }
    }

    public void updateButton(ActionEvent actionEvent) {
        String sizeValue = utils.getComboBoxValue(sizeComboBox);
        String packagingValue = utils.getComboBoxValue(packagingComboBox);
        Size sizeInsertData = sizeDAO.getDataByName(sizeValue);
        Packaging packagingInsertData = packagingDAO.getDataByName(packagingValue);
        try {
            packagingOwnerDAO.update(this.product.getId(), sizeInsertData.getId(), packagingInsertData.getId(), Integer.parseInt(packQtyField.getText()), Integer.parseInt(anchorData.getText()));
            clearFields();
            reload();
        } catch (NumberFormatException e) {
            utils.alert("err", Alert.AlertType.ERROR, "Đã xảy ra lỗi!", "Số lượng đóng gói không được để trống hoặc nhỏ hơn 1");
        }
    }

    public void clearButton(ActionEvent actionEvent) {
        clearFields();
    }

    public void reloadData(ActionEvent actionEvent) {
        reload();
    }

    /**
     * Handle on filter Combobox
     * */
    private void filterComboBox() {
        sizeComboBox.getItems().addAll(sizeObservableList);
        filterSizeComboBox();
        packagingComboBox.getItems().addAll(packagingObservableList);
        filterPackagingComboBox();
    }

    /**
     * Handle on filter Size Combobox
     * */
    private void filterSizeComboBox() {
        // Create the listener to filter the list as user enters search terms
        FilteredList<Size> dataFilteredList = new FilteredList<>(sizeObservableList, p-> true);

        // Add listener to our ComboBox textfield to filter the list
        sizeComboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            dataFilteredList.setPredicate(item -> {

                isUpdating = false;

                // Check if the search term is contained anywhere in our list
                if (item.getSize().toLowerCase().contains(newValue.toLowerCase().trim()) && !isUpdating) {
                    sizeComboBox.show();
                    return true;
                }

                // No matches found
                return false;
            });

        });

        sizeComboBox.getItems().removeAll(sizeObservableList);
        sizeComboBox.getItems().addAll(dataFilteredList);
    }

    /**
     * Handle on filter Packaging Combobox
     * */
    private void filterPackagingComboBox() {
        // Create the listener to filter the list as user enters search terms
        FilteredList<Packaging> dataFilteredList = new FilteredList<>(packagingObservableList, p-> true);

        // Add listener to our ComboBox textfield to filter the list
        packagingComboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            dataFilteredList.setPredicate(item -> {

                isUpdating = false;

                // Check if the search term is contained anywhere in our list
                if (item.getName().toLowerCase().contains(newValue.toLowerCase().trim()) && !isUpdating) {
                    packagingComboBox.show();
                    return true;
                }

                // No matches found
                return false;
            });

        });

        packagingComboBox.getItems().removeAll(packagingObservableList);
        packagingComboBox.getItems().addAll(dataFilteredList);
    }

    /**
     * Reset fields
     * This will handle an enter event and mouse leave for these Nodes
     * */
    private void resetFields() {
        utils.disableKeyEnterOnTextFieldComboBox(sizeComboBox, false);
        utils.disableKeyEnterOnTextFieldComboBox(packagingComboBox, true);

        utils.disableKeyEnterOnTextField(packQtyField);
    }

    /**
     * Handle on resetting ComboBox
     * */
    private void resetComboBox() {
        Size sizeData = sizeDAO.getDataByName("16/20");
        utils.setComboBoxValue(sizeComboBox, sizeData.getSize());

        Packaging packagingData = packagingDAO.getDataByID(1);
        utils.setComboBoxValue(packagingComboBox, packagingData.getName());
    }

    /**
     * Handle event on clearing all inputs
     * */
    private void clearFields() {
        resetComboBox();
        anchorLabel.setText("");
        anchorData.setText("");

        packQtyField.setText("");

        searchField.setText("");

        setProductData(this.product);
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
     * Setting context menu for table row
     * */
    private void setContextMenu() {
        dataTable.setRowFactory((TableView<PackagingOwnerString> tableView) -> {
            final TableRow<PackagingOwnerString> row = new TableRow<>();

            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem viewItem = new MenuItem("Chi tiết");
            final MenuItem editItem = new MenuItem("Cập nhật");
            final MenuItem removeItem = new MenuItem("Xóa dòng");

            viewItem.setOnAction((ActionEvent event) -> {
                PackagingOwnerString packagingOwnerString = dataTable.getSelectionModel().getSelectedItem();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Chi tiết mặt hàng");
                alert.setHeaderText(packagingOwnerString.getProductName());
                alert.setContentText(
                        "Tên mặt hàng: " + packagingOwnerString.getProductName() + "\n" +
                                "Bao bì: " + packagingOwnerString.getPackagingName() + "\n" +
                                "Size: " + packagingOwnerString.getSize() + "\n" +
                                "id: " + packagingOwnerString.getId() + "\n"
                );

                alert.showAndWait();
            });
            contextMenu.getItems().add(viewItem);

            editItem.setOnAction((ActionEvent event) -> {
                PackagingOwnerString packagingOwnerString = dataTable.getSelectionModel().getSelectedItem();
                PackagingOwner packagingOwnerData = packagingOwnerDAO.getDataByID(packagingOwnerString.getId());
                getData(packagingOwnerData);
                hideComboBoxForUpdatingData();
            });
            contextMenu.getItems().add(editItem);

            removeItem.setOnAction((ActionEvent event) -> {
                PackagingOwnerString packagingOwnerString = dataTable.getSelectionModel().getSelectedItem();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Thông báo!");
                alert.setHeaderText("Xóa: "+packagingOwnerString.getProductName());
                alert.setContentText("Lưu ý: Dữ liệu sẽ không thể khôi phục lại sau khi xóa, bạn có chắc muốn tiếp tục?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    packagingOwnerDAO.delete(packagingOwnerString.getId());
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
     * Setting data for inputs
     *
     * @param packagingOwner - the packaging data
     * */
    private void getData(PackagingOwner packagingOwner) {
        if(!isEditableComboBox) {
            sizeComboBox.getSelectionModel().select(packagingOwner.getSize_id());
            packagingComboBox.getSelectionModel().select(packagingOwner.getPackaging_id());
        } else {
            Size sizeData = sizeDAO.getDataByID(packagingOwner.getSize_id());
            sizeComboBox.getEditor().setText(sizeData.getSize());
            Packaging packagingData = packagingDAO.getDataByID(packagingOwner.getPackaging_id());
            packagingComboBox.getEditor().setText(packagingData.getName());
        }

        packQtyField.setText(packagingOwner.getPack_qty()+"");
        anchorLabel.setText("Current ID: ");
        anchorData.setText(""+packagingOwner.getId());
        this.isUpdating = true;
    }

    /**
     * Hiding the Combobox while on updating stage.
     * */
    private void hideComboBoxForUpdatingData() {
        if(isUpdating) {
            sizeComboBox.hide();
            packagingComboBox.hide();
        }
    }

    /**
     * Passing data between ProductController and ProductPackagingController
     * */
    public void setProductData(Product product) {
        this.product = product;
        mainLabel.setText("List bao bì: "+this.product.getName());
        setSearchField(this.product.getName());
    }

    /**
     * Handle on searching data
     * */
    private void setSearchField(String defaultData) {
        ObservableList<PackagingOwnerString> modelObservableList = FXCollections.observableArrayList(packagingOwnerStringDAO.getListByName(defaultData));
        FilteredList<PackagingOwnerString> modelFilteredList = new FilteredList<>(modelObservableList, p -> true);

        searchField.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    switch (searchComboBox.getValue()) {
                        case "Size":
                            modelFilteredList.setPredicate(str -> {
                                if (newValue == null || newValue.isEmpty())
                                    return true;
                                String lowerCaseFilter = newValue.toLowerCase();

                                return str.getSize().toLowerCase().contains
                                        (lowerCaseFilter);
                            });
                            break;
                        case "Bao bì":
                            modelFilteredList.setPredicate(str -> {
                                if (newValue == null || newValue.isEmpty())
                                    return true;
                                String lowerCaseFilter = newValue.toLowerCase();

                                return str.getPackagingName().toLowerCase().contains
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

        SortedList<PackagingOwnerString> modelSortedList = new SortedList<>(modelFilteredList);
        modelSortedList.comparatorProperty().bind(dataTable.comparatorProperty());

        dataTable.setItems(modelSortedList);
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
        resetFields();

        resetComboBox();

        dataTable.setEditable(true);

        idColumn.setSortable(false);
        idColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Integer>(dataTable.getItems().indexOf(column.getValue())+1));

        sizeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PackagingOwnerString, Size>, ObservableValue<Size>>() {
            @Override
            public ObservableValue<Size> call(TableColumn.CellDataFeatures<PackagingOwnerString, Size> sizeCellDataFeatures) {
                Size data = sizeDAO.getDataByName(sizeCellDataFeatures.getValue().getSize());
                return new SimpleObjectProperty<>(data);
            }
        });
        sizeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(sizeObservableList));

        packagingColumn.setCellValueFactory(new PropertyValueFactory<PackagingOwnerString, String>("packagingName"));
        packagingColumn.setCellFactory(tc -> {

            TableCell<PackagingOwnerString, String> cell = new TableCell<>();

            currentRow = cell.getIndex();
            currentCell = cell.getText();

            Text text = new Text();
            ComboBox<Packaging> packagingComboBoxTableCell = new ComboBox<>();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(packagingColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());

            cell.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && ! cell.isEmpty()) {
                    cell.setGraphic(packagingComboBoxTableCell);
                    cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
                    packagingComboBoxTableCell.setMaxWidth(Control.USE_COMPUTED_SIZE);
                    packagingComboBoxTableCell.setEditable(true);
                    packagingComboBoxTableCell.getEditor().setText(cell.itemProperty().getValue());

                    FilteredList<Packaging> dataFilteredList = new FilteredList<>(packagingObservableList, p-> true);

                    packagingComboBoxTableCell.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
                        dataFilteredList.setPredicate(item -> {

                            // If the TextField is empty, return all items in the original list
                            if (newValue == null || newValue.isEmpty()) {
                                packagingComboBoxTableCell.hide();
                                return true;
                            }

                            // Check if the search term is contained anywhere in our list
                            if (item.getName().trim().toLowerCase().contains(newValue.toLowerCase().trim())) {
                                packagingComboBoxTableCell.show();
                                return true;
                            }

                            // No matches found
                            return false;
                        });
                        SortedList<Packaging> dataSortedList = new SortedList<>(dataFilteredList);
                        packagingComboBoxTableCell.getItems().removeAll(packagingObservableList);
                        packagingComboBoxTableCell.getItems().addAll(dataSortedList);
                    });
//
                    packagingComboBoxTableCell.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            packagingComboBoxTableCell.setEditable(true);
                        }
                    });

                    packagingComboBoxTableCell.setOnKeyPressed(event -> {
                        if (event.getCode() == KeyCode.ENTER) {
                            TablePosition pos = (TablePosition) dataTable.getSelectionModel().getSelectedCells().get(0);
                            int selectedRow = dataTable.getItems().get(pos.getRow()).getId();
                            Packaging packagingInsertData = packagingDAO.getDataByName(packagingComboBoxTableCell.getEditor().getText());
                            packagingOwnerDAO.updateDataInteger("packaging_id", packagingInsertData.getId(), selectedRow);
                            clearFields();
                            reload();
                        }
                    });
                }
            });

            return cell ;
        });

        packingQtyColumn.setCellValueFactory(new PropertyValueFactory<>("pack_qty"));
        packingQtyColumn.setCellFactory(TextFieldTableCell.<PackagingOwnerString, Integer>forTableColumn(new IntegerStringConverter()));
        packingQtyColumn.setOnEditCommit(event -> {
            final Integer data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((PackagingOwnerString) event.getTableView().getItems().get(event.getTablePosition().getRow())).setPack_qty(data);
            packagingOwnerDAO.updateDataInteger("pack_qty", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        dataTable.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ESCAPE || event.getCode() == KeyCode.F5) {
                dataTable.refresh();
            }
        });

        setContextMenu();

        utils.reloadTableViewOnChange(dataTable, currentRow, currentCell);
    }

}
