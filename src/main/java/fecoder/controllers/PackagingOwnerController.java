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
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class PackagingOwnerController implements Initializable {

    public Button insertButton;
    public Button updateButton;
    public Button clearButton;
    public ComboBox<String> searchComboBox;
    public TextField searchField;
    public Button reloadData;
    public Label anchorLabel;
    public Label anchorData;
    public ComboBox<Product> productComboBox;
    public ComboBox<Size> sizeComboBox;
    public ComboBox<Packaging> packagingComboBox;
    public TextField packQtyField;

    public TableView<PackagingOwnerString> dataTable;
    public TableColumn<PackagingOwnerString, Integer> idColumn;
    public TableColumn<PackagingOwnerString, String> productColumn;
    public TableColumn<PackagingOwnerString, Size> sizeColumn;
    public TableColumn<PackagingOwnerString, String> packagingColumn;
    public TableColumn<PackagingOwnerString, Integer> packingQtyColumn;

    private final PackagingOwnerDAO packagingOwnerDAO = new PackagingOwnerDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final SizeDAO sizeDAO = new SizeDAO();
    private final PackagingDAO packagingDAO = new PackagingDAO();
    private final PackagingOwnerStringDAO packagingOwnerStringDAO = new PackagingOwnerStringDAO();

    private final ObservableList<Product> productObservableList = FXCollections.observableArrayList(productDAO.getList());
    private final ObservableList<Size> sizeObservableList = FXCollections.observableArrayList(sizeDAO.getList());
    private final ObservableList<Packaging> packagingObservableList = FXCollections.observableArrayList(packagingDAO.getList());

    private boolean isEditableComboBox = false;
    private boolean isUpdating = false;

    private int currentRow;
    private String currentCell;
    private final Utils utils = new Utils();

    /**
     * All needed to start controller
     * */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.filterComboBox(true);
        loadView();
    }

    /**
     * Inserting button action
     * */
    public void insertButton(ActionEvent actionEvent) {
        Product productInsertData = productDAO.getDataByName(productComboBox.getEditor().getText());
        Size sizeInsertData = sizeDAO.getDataByName(sizeComboBox.getEditor().getText());
        Packaging packagingInsertData = packagingDAO.getDataByName(packagingComboBox.getEditor().getText());
        try {
            packagingOwnerDAO.insert(productInsertData.getId(), sizeInsertData.getId(), packagingInsertData.getId(), Integer.parseInt(packQtyField.getText()));
            clearFields();
            reload();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi!");
            alert.setHeaderText("Đã xảy ra lỗi!");
            alert.setContentText("Số lượng đóng gói không được để trống hoặc nhỏ hơn 1");
            alert.showAndWait();
        }
    }

    /**
     * Updating button action
     * */
    public void updateButton(ActionEvent actionEvent) {
        Product productInsertData = productDAO.getDataByName(productComboBox.getEditor().getText());
        Size sizeInsertData = sizeDAO.getDataByName(sizeComboBox.getEditor().getText());
        Packaging packagingInsertData = packagingDAO.getDataByName(packagingComboBox.getEditor().getText());
        try {
            packagingOwnerDAO.update(productInsertData.getId(), sizeInsertData.getId(), packagingInsertData.getId(), Integer.parseInt(packQtyField.getText()), Integer.parseInt(anchorData.getText()));
            clearFields();
            reload();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi!");
            alert.setHeaderText("Đã xảy ra lỗi!");
            alert.setContentText("Số lượng đóng gói không được để trống hoặc nhỏ hơn 1");
            alert.showAndWait();
        }
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
        clearFields();
        loadView();
    }

    /**
     * Handle on clearing specific inputs
     * */
    private void clearFields() {
        clearComboBox();
        packQtyField.setText(null);
        anchorLabel.setText(null);
        anchorData.setText(null);
    }

    /**
     * Handle on clearing comnbobox
     * */
    private void clearComboBox() {
        if(isEditableComboBox) {
            this.clearEditableComboBox(productComboBox);
            this.clearEditableComboBox(sizeComboBox);
            this.clearEditableComboBox(packagingComboBox);
        } else {
            this.clearNonEditableComboBox();
        }
    }

    /**
     * Setting data for inputs
     *
     * @param packagingOwner - the packaging data
     * */
    private void getData(PackagingOwner packagingOwner) {
        if(!isEditableComboBox) {
            productComboBox.getSelectionModel().select(packagingOwner.getProduct_id());
            sizeComboBox.getSelectionModel().select(packagingOwner.getSize_id());
            packagingComboBox.getSelectionModel().select(packagingOwner.getPackaging_id());
        } else {
            Product productData = productDAO.getDataByID(packagingOwner.getProduct_id());
            productComboBox.getEditor().setText(productData.getName());
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
     * Handle on clearing editable comnbobox
     * */
    private void clearEditableComboBox(ComboBox comboBox) {
        comboBox.getEditor().setText(null);
        comboBox.hide();
    }

    /**
     * Handle on clearing non-editable comnbobox
     * */
    private void clearNonEditableComboBox() {
        //        Product defaultComboBoxData = new Product(0, "Chưa chọn", "", "", "");
//        productComboBox.valueProperty().set(defaultComboBoxData);
//        productComboBox.getSelectionModel().clearSelection();
        if(!productComboBox.getItems().isEmpty()) {
//            sizeComboBox.getSelectionModel().clearSelection();
            productComboBox.getSelectionModel().selectFirst();
            productComboBox.hide();
        }
        if(!sizeComboBox.getItems().isEmpty()) {
//            sizeComboBox.getSelectionModel().clearSelection();
            sizeComboBox.getSelectionModel().selectFirst();
            sizeComboBox.hide();
        }
        if(!packagingComboBox.getItems().isEmpty()) {
//            packagingComboBox.getSelectionModel().clearSelection();
            packagingComboBox.getSelectionModel().selectFirst();
            packagingComboBox.hide();
        }
    }

    /**
     * Determine the combobox is editable or not.
     * */
    private void isEditableComboBox(boolean isEditable) {
        if(isEditable) {
            productComboBox.setEditable(isEditable);
            sizeComboBox.setEditable(isEditable);
            packagingComboBox.setEditable(isEditable);
        }

        this.isEditableComboBox = isEditable;
    }

    /**
     * Enable/Disable the filter for Combobox
     * */
    private void filterComboBox(boolean filter) {
        this.isEditableComboBox(filter);
        if(isEditableComboBox) {
            this.productComboBoxFilter(sizeComboBox);
            this.sizeComboBoxFilter(packagingComboBox);
            this.packagingComboBoxFilter(packQtyField);
        }
        productComboBox.getItems().addAll(productObservableList);
        sizeComboBox.getItems().addAll(sizeObservableList);
        packagingComboBox.getItems().addAll(packagingObservableList);
    }

    /**
     * Hiding the Combobox while on updating stage.
     * */
    private void hideComboBoxForUpdatingData() {
        if(isUpdating) {
            productComboBox.hide();
            sizeComboBox.hide();
            packagingComboBox.hide();
        }
    }

    /**
     * Handle on filter Product Combobox
     * */
    private void productComboBoxFilter(ComboBox comboBox) {
        // Create the listener to filter the list as user enters search terms
        FilteredList<Product> dataFilteredList = new FilteredList<>(productObservableList, p-> true);

        // Add listener to our ComboBox textfield to filter the list
        productComboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            dataFilteredList.setPredicate(item -> {

                this.isUpdating = false;

                // If the TextField is empty, return all items in the original list
                if (newValue == null || newValue.isEmpty()) {
                    productComboBox.hide();
                    return true;
                }

                // Check if the search term is contained anywhere in our list
                if (item.getName().toLowerCase().contains(newValue.toLowerCase().trim()) && !isUpdating) {
                    productComboBox.show();
                    return true;
                }

                // No matches found
                return false;
            });
            productComboBox.getItems().removeAll(productObservableList);
            productComboBox.getItems().addAll(dataFilteredList);
        });

        productComboBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                productComboBox.setEditable(true);
            }
        });

        productComboBox.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                productComboBox.setEditable(false);
                comboBox.requestFocus();
            }
        });
    }

    /**
     * Handle on filter Size Combobox
     * */
    private void sizeComboBoxFilter(ComboBox comboBox) {
        // Create the listener to filter the list as user enters search terms
        FilteredList<Size> dataFilteredList = new FilteredList<>(sizeObservableList, p-> true);

        // Add listener to our ComboBox textfield to filter the list
        sizeComboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            dataFilteredList.setPredicate(item -> {

                this.isUpdating = false;

                // If the TextField is empty, return all items in the original list
                if (newValue == null || newValue.isEmpty()) {
                    productComboBox.hide();
                    return true;
                }

                // Check if the search term is contained anywhere in our list
                if (item.getSize().toLowerCase().contains(newValue.toLowerCase().trim()) && !isUpdating) {
                    sizeComboBox.show();
                    return true;
                }

                // No matches found
                return false;
            });
            sizeComboBox.getItems().removeAll(sizeObservableList);
            sizeComboBox.getItems().addAll(dataFilteredList);
        });

        sizeComboBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                sizeComboBox.setEditable(true);
            }
        });

        sizeComboBox.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                sizeComboBox.setEditable(false);
                comboBox.requestFocus();
            }
        });
    }

    /**
     * Handle on filter Packaging Combobox
     * */
    private void packagingComboBoxFilter(TextField textField) {
        // Create the listener to filter the list as user enters search terms
        FilteredList<Packaging> dataFilteredList = new FilteredList<>(packagingObservableList, p-> true);

        // Add listener to our ComboBox textfield to filter the list
        packagingComboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            dataFilteredList.setPredicate(item -> {

                this.isUpdating = false;

                // If the TextField is empty, return all items in the original list
                if (newValue == null || newValue.isEmpty()) {
                    packagingComboBox.hide();
                    return true;
                }

                // Check if the search term is contained anywhere in our list
                if (item.getName().toLowerCase().contains(newValue.toLowerCase().trim()) && !isUpdating) {
                    packagingComboBox.show();
                    return true;
                }

                // No matches found
                return false;
            });
            packagingComboBox.getItems().removeAll(packagingObservableList);
            packagingComboBox.getItems().addAll(dataFilteredList);
        });

        packagingComboBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                packagingComboBox.setEditable(true);
            }
        });

        packagingComboBox.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                packagingComboBox.setEditable(false);
                textField.requestFocus();
            }
        });
    }

    /**
     * Handle on searching data
     * */
    public void setSearchField() {
        ObservableList<PackagingOwnerString> modelObservableList = FXCollections.observableArrayList(packagingOwnerStringDAO.getList());
        FilteredList<PackagingOwnerString> modelFilteredList = new FilteredList<>(modelObservableList, p -> true);

        searchField.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    switch (searchComboBox.getValue()) {
                        case "Tên mặt hàng":
                            modelFilteredList.setPredicate(str -> {
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

        SortedList<PackagingOwnerString> modelSortedList = new SortedList<>(modelFilteredList);
        modelSortedList.comparatorProperty().bind(dataTable.comparatorProperty());

        dataTable.setItems(modelSortedList);
    }

    /**
     * Getting current row on click
     * */
    public void getCurrentRow() {
        TableView.TableViewSelectionModel<PackagingOwnerString> modelTableViewSelectionModel = dataTable.getSelectionModel();
        modelTableViewSelectionModel.setSelectionMode(SelectionMode.SINGLE);

        ObservableList<PackagingOwnerString> modelObservableList1 = modelTableViewSelectionModel.getSelectedItems();

        modelObservableList1.addListener(new ListChangeListener<PackagingOwnerString>() {
            @Override
            public void onChanged(Change<? extends PackagingOwnerString> change) {
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
    private void loadView() {
        dataTable.setEditable(true);

        getCurrentRow();

        idColumn.setSortable(false);
        idColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Integer>(dataTable.getItems().indexOf(column.getValue())+1));

        productColumn.setCellValueFactory(new PropertyValueFactory<PackagingOwnerString, String>("productName"));
        productColumn.setCellFactory(tc -> {

            TableCell<PackagingOwnerString, String> cell = new TableCell<>();

            currentRow = cell.getIndex();
            currentCell = cell.getText();

            Text text = new Text();
            ComboBox<Product> productComboBoxTableCell = new ComboBox<>();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(productColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());

            cell.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && ! cell.isEmpty()) {
                    cell.setGraphic(productComboBoxTableCell);
                    cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
                    productComboBoxTableCell.setMaxWidth(Control.USE_COMPUTED_SIZE);
                    productComboBoxTableCell.setEditable(true);
                    productComboBoxTableCell.getEditor().setText(cell.itemProperty().getValue());

                    FilteredList<Product> dataFilteredList = new FilteredList<>(productObservableList, p-> true);

                    productComboBoxTableCell.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
                        dataFilteredList.setPredicate(item -> {

                            // If the TextField is empty, return all items in the original list
                            if (newValue == null || newValue.isEmpty()) {
                                productComboBoxTableCell.hide();
                                return true;
                            }

                            // Check if the search term is contained anywhere in our list
                            if (item.getName().trim().toLowerCase().contains(newValue.toLowerCase().trim())) {
                                productComboBoxTableCell.show();
                                return true;
                            }

                            // No matches found
                            return false;
                        });
                        SortedList<Product> dataSortedList = new SortedList<>(dataFilteredList);
                        productComboBoxTableCell.getItems().removeAll(productObservableList);
                        productComboBoxTableCell.getItems().addAll(dataSortedList);
                    });
//
                    productComboBoxTableCell.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            productComboBoxTableCell.setEditable(true);
                        }
                    });

                    productComboBoxTableCell.setOnKeyPressed(event -> {
                        if (event.getCode() == KeyCode.ENTER) {
                            TablePosition pos = (TablePosition) dataTable.getSelectionModel().getSelectedCells().get(0);
                            int selectedRow = dataTable.getItems().get(pos.getRow()).getId();
                            Product productInsertData = productDAO.getDataByName(productComboBoxTableCell.getEditor().getText());
                            packagingOwnerDAO.updateDataInteger("product_id", productInsertData.getId(), selectedRow);
                            clearFields();
                            reload();
                        }
                    });
                }
            });

            return cell ;
        });

//        sizeColumn.setCellValueFactory(new PropertyValueFactory<PackagingOwnerString, String>("size"));
        sizeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PackagingOwnerString, Size>, ObservableValue<Size>>() {
            @Override
            public ObservableValue<Size> call(TableColumn.CellDataFeatures<PackagingOwnerString, Size> sizeCellDataFeatures) {
                Size data = sizeDAO.getDataByName(sizeCellDataFeatures.getValue().getSize());
                return new SimpleObjectProperty<>(data);
            }
        });
        sizeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(sizeObservableList));

//        packagingColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PackagingOwnerString, Packaging>, ObservableValue<Packaging>>() {
//            @Override
//            public ObservableValue<Packaging> call(TableColumn.CellDataFeatures<PackagingOwnerString, Packaging> packagingOwnerStringStringCellDataFeatures) {
//                Packaging packagingRawData = packagingDAO.getDataByName(packagingOwnerStringStringCellDataFeatures.getValue().getPackagingName());
//                return new SimpleObjectProperty<>(packagingRawData);
//            }
//        });
        packagingColumn.setCellValueFactory(new PropertyValueFactory<PackagingOwnerString, String>("packagingName"));
        packagingColumn.setCellFactory(tc -> {

            TableCell<PackagingOwnerString, String> cell = new TableCell<>();

            currentRow = cell.getIndex();
            currentCell = cell.getText();

            Text text = new Text();
            ComboBox<Packaging> packagingComboBoxTableCell = new ComboBox<>();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(productColumn.widthProperty());
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

//                // Traditional way to get the response value.
//                Optional<String> result = dialog.showAndWait();
//                if (result.isPresent()){
//                    suplier.setCode(result.get());
//                    tableView.getItems().set(rowIndex, suplier);
//                }
            });
            contextMenu.getItems().add(viewItem);

            editItem.setOnAction((ActionEvent event) -> {
                PackagingOwnerString packagingOwnerString = dataTable.getSelectionModel().getSelectedItem();
                System.out.println(packagingOwnerString.getId());
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

        utils.updateTableOnChanged(dataTable, currentRow, currentCell);

        setSearchField();
    }
}
