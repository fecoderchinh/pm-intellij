package fecoder.controllers;

import fecoder.DAO.*;
import fecoder.models.*;
import fecoder.utils.AutoCompleteComboBoxListener;
import fecoder.utils.AutoFill.AutoFillTextBox;
import fecoder.utils.Utils;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProductPackagingController implements Initializable {
    public Label mainLabel;
    public ComboBox<Size> sizeComboBox;
    public ComboBox<PackagingToString> packagingComboBox;
    public TextField packQtyField;
    public Button insertButton;
    public Button updateButton;
    public Button clearButton;
    public ComboBox<String> searchComboBox;
    public TextField searchField;
    public Button reloadData;
    public TableView<PackagingOwnerString> dataTable;
    public TableColumn<PackagingOwnerString, Integer> idColumn;
    public TableColumn<PackagingOwnerString, String> sizeColumn;
    public TableColumn<PackagingOwnerString, String> packagingColumn;
    public TableColumn<PackagingOwnerString, Float> packingQtyColumn;
    public TableColumn<PackagingOwnerString, String> noteColumn;
    public Label anchorLabel;
    public Label anchorData;
    public TextField noteField;

    private boolean isEditableComboBox = false;
    private boolean isUpdating = false;

    private final PackagingOwnerDAO packagingOwnerDAO = new PackagingOwnerDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final SizeDAO sizeDAO = new SizeDAO();
    private final PackagingDAO packagingDAO = new PackagingDAO();
    private final PackagingOwnerStringDAO packagingOwnerStringDAO = new PackagingOwnerStringDAO();
    private final PackagingToStringDAO packagingToStringDAO = new PackagingToStringDAO();

    private final ObservableList<Size> sizeObservableList = FXCollections.observableArrayList(sizeDAO.getList());
    private final ObservableList<PackagingToString> packagingObservableList = FXCollections.observableArrayList(packagingToStringDAO.getList());

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
        new AutoCompleteComboBoxListener<>(sizeComboBox, true, sizeDAO.getLastestData().getSize());
        new AutoCompleteComboBoxListener<>(packagingComboBox, true, packagingToStringDAO.getLastestData().getCustomName());
        loadView();
    }

    public void insertButton(ActionEvent actionEvent) {
        Size size = sizeDAO.getDataByName(utils.getComboBoxValue(sizeComboBox));
        PackagingToString packaging = packagingToStringDAO.getDataByName(utils.getComboBoxValue(packagingComboBox));
        try {
            packagingOwnerDAO.insert(this.product.getId(), size.getId(), packaging.getId(), Float.parseFloat(packQtyField.getText()), noteField.getText());
            clearFields();
            reload();
        } catch (NumberFormatException e) {
            utils.alert("err", Alert.AlertType.ERROR, "???? x???y ra l???i!", "S??? l?????ng ????ng g??i kh??ng ???????c ????? tr???ng ho???c nh??? h??n 1").showAndWait();
        }
    }

    public void updateButton(ActionEvent actionEvent) {
        Size size = sizeDAO.getDataByName(utils.getComboBoxValue(sizeComboBox));
        PackagingToString packaging = packagingToStringDAO.getDataByName(utils.getComboBoxValue(packagingComboBox));
        try {
            packagingOwnerDAO.update(this.product.getId(), size.getId(), packaging.getId(), Float.parseFloat(packQtyField.getText()), noteField.getText(),Integer.parseInt(anchorData.getText()));
            clearFields();
            reload();
        } catch (NumberFormatException e) {
            utils.alert("err", Alert.AlertType.ERROR, "???? x???y ra l???i!", "S??? l?????ng ????ng g??i kh??ng ???????c ????? tr???ng ho???c nh??? h??n 1");
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
//        filterSizeComboBox();
        packagingComboBox.getItems().addAll(packagingObservableList);
//        filterPackagingComboBox();
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

                if (newValue == null || newValue.isEmpty())
                    return true;

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
        packagingComboBox.setEditable(true);
        // Create the listener to filter the list as user enters search terms
        FilteredList<PackagingToString> dataFilteredList = new FilteredList<>(packagingObservableList, p-> true);

//        // Add listener to our ComboBox textfield to filter the list
        packagingComboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {

            String value = newValue;
            // If any item is selected we get the first word of that item.
            String selected = packagingComboBox.getSelectionModel().getSelectedItem() != null
                    ? packagingComboBox.getSelectionModel().getSelectedItem().getName() : null;

            dataFilteredList.setPredicate(item -> {

                isUpdating = false;

                if (newValue == null || newValue.isEmpty())
                    return true;

                // Check if the search term is contained anywhere in our list
                if (item.getName().toLowerCase().contains(newValue.toLowerCase()) && !isUpdating) {
                    packagingComboBox.show();
                    return true;
                }

                // No matches found
                return false;
            });

            SortedList<PackagingToString> sortedList = new SortedList<>(dataFilteredList);

            packagingComboBox.getItems().removeAll(packagingObservableList);
            packagingComboBox.getItems().addAll(sortedList);

        });
    }

    /**
     * Reset fields
     * This will handle an enter event and mouse leave for these Nodes
     * */
    private void resetFields() {
        utils.disableKeyEnterOnTextFieldComboBox(sizeComboBox, true, "Size");

        utils.disableKeyEnterOnTextFieldComboBox(packagingComboBox, true, "Lo???i BB");

        utils.disableKeyEnterOnTextField(packQtyField);
        utils.disableKeyEnterOnTextField(noteField);
    }

    /**
     * Handle on resetting ComboBox
     * */
    private void resetComboBox() {
        Size sizeData = sizeDAO.getLastestData();
        utils.setComboBoxValue(sizeComboBox, sizeData.getSize());

        PackagingToString packagingData = packagingToStringDAO.getLastestData();
        utils.setComboBoxValue(packagingComboBox, packagingData.getCustomName());
    }

    /**
     * Handle on clearing comnbobox
     * */
    private void getComboBoxData(PackagingOwner data) {
        if(!sizeComboBox.isEditable()) {
            sizeComboBox.getSelectionModel().select(sizeDAO.getDataByID(data.getSize_id()));
        } else {
            Size _sM = sizeDAO.getDataByID(data.getSize_id());
            sizeComboBox.getEditor().setText(_sM.getSize());
        }

        if(!packagingComboBox.isEditable()) {
            packagingComboBox.getSelectionModel().select(packagingToStringDAO.getDataByID(data.getPackaging_id()));
        } else {
            PackagingToString _pM = packagingToStringDAO.getDataByID(data.getPackaging_id());
            packagingComboBox.getEditor().setText(_pM.getCustomName());
        }
    }

    /**
     * Handle event on clearing all inputs
     * */
    private void clearFields() {
        resetComboBox();
        anchorLabel.setText("No ID selected");
        anchorData.setText("");

        packQtyField.setText("");

        searchField.setText("");

        setData(this.product);
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
            final MenuItem viewItem = new MenuItem("Chi ti???t");
            final MenuItem editItem = new MenuItem("C???p nh???t");
            final MenuItem removeItem = new MenuItem("X??a d??ng");

            viewItem.setOnAction((ActionEvent event) -> {
                PackagingOwnerString packagingOwnerString = dataTable.getSelectionModel().getSelectedItem();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Chi ti???t m???t h??ng");
                alert.setHeaderText(packagingOwnerString.getProductName());
                alert.setContentText(
                        "T??n m???t h??ng: " + packagingOwnerString.getProductName() + "\n" +
                                "Bao b??: " + packagingOwnerString.getPackagingName() + "\n" +
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
                alert.setTitle("Th??ng b??o!");
                alert.setHeaderText("X??a: "+packagingOwnerString.getProductName());
                alert.setContentText("L??u ??: D??? li???u s??? kh??ng th??? kh??i ph???c l???i sau khi x??a, b???n c?? ch???c mu???n ti???p t???c?");

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

            row.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if(mouseEvent.getClickCount() == 2 && !row.isEmpty()) {
                        PackagingOwnerString packagingOwnerString = dataTable.getSelectionModel().getSelectedItem();
                        PackagingOwner packagingOwnerData = packagingOwnerDAO.getDataByID(packagingOwnerString.getId());
                        getData(packagingOwnerData);
                        hideComboBoxForUpdatingData();
                    }
                }
            });
            return row ;
        });
    }

    /**
     * Setting data for inputs
     *
     * @param packagingOwner - the packaging data
     * */
    private void getData(PackagingOwner packagingOwner) {
        getComboBoxData(packagingOwner);

        packQtyField.setText(packagingOwner.getPack_qty()+"");
        noteField.setText(packagingOwner.getNote());
        anchorLabel.setText("ID Selected: ");
        anchorData.setText(""+packagingOwner.getId());
        this.isUpdating = true;
    }

    /**
     * Hiding the Combobox while on updating stage.
     * */
    private void hideComboBoxForUpdatingData() {
//        if(isUpdating) {
//            sizeComboBox.hide();
//            packagingComboBox.hide();
//        }
    }

    /**
     * Passing data between ProductController and ProductPackagingController
     * */
    public void setData(Product product) {
        this.product = product;
        mainLabel.setText("Danh s??ch BB: "+this.product.getName());
        mainLabel.setWrapText(true);
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
                        case "Bao b??":
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
        anchorLabel.setText("No ID Selected");

        resetFields();

        resetComboBox();

        dataTable.setEditable(true);

        idColumn.setSortable(false);
        idColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Integer>(dataTable.getItems().indexOf(column.getValue())+1));

//        sizeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PackagingOwnerString, String>, ObservableValue<Size>>() {
//            @Override
//            public ObservableValue<Size> call(TableColumn.CellDataFeatures<PackagingOwnerString, Size> sizeCellDataFeatures) {
//                Size data = sizeDAO.getDataByName(sizeCellDataFeatures.getValue().getSize());
//                return new SimpleObjectProperty<>(data);
//            }
//        });
//        sizeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(sizeObservableList));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<PackagingOwnerString, String>("size"));
//        sizeColumn.setCellFactory(tc -> {
//
//            TableCell<PackagingOwnerString, String> cell = new TableCell<>();
//
//            currentRow = cell.getIndex();
//            currentCell = cell.getText();
//
//            Text text = new Text();
//
//            ComboBox<Size> sizeComboBoxTableCell = new ComboBox<>();
//            sizeComboBoxTableCell.setConverter(new StringConverter<Size>() {
//                @Override
//                public String toString(Size size) {
//                    if (size == null) return null;
//                    return size.toString();
//                }
//
//                @Override
//                public Size fromString(String s) {
//                    return null;
//                }
//            });
//
//            sizeComboBoxTableCell.getItems().addAll(sizeObservableList);
//            new AutoCompleteComboBoxListener<>(sizeComboBoxTableCell, true, sizeDAO.getLastestData().getSize());
//
//            cell.setGraphic(text);
//            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
//            text.wrappingWidthProperty().bind(sizeColumn.widthProperty());
//
//            text.textProperty().bind(new StringBinding() {
//                { bind(cell.itemProperty()); }
//                @Override
//                protected String computeValue() {
//                    if(cell.itemProperty().getValue() != null) {
//                        return cell.itemProperty().getValue().toString();
//                    } else {
//                        return "";
//                    }
//                }
//            });
//
//            cell.setOnMouseClicked(e -> {
//                if (e.getClickCount() == 2 && ! cell.isEmpty()) {
//                    cell.setGraphic(sizeComboBoxTableCell);
//                    cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
//                    sizeComboBoxTableCell.setMaxWidth(Control.USE_COMPUTED_SIZE);
//                    sizeComboBoxTableCell.setEditable(true);
//                    sizeComboBoxTableCell.getEditor().setText(cell.itemProperty().getValue().toString());
//
//                    sizeComboBoxTableCell.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Size>() {
//                        @Override
//                        public void changed(ObservableValue<? extends Size> observableValue, Size size, Size t1) {
//                            if(t1 != null) {
//                                if(!cell.isEmpty()) {
//                                    TablePosition pos = (TablePosition) dataTable.getSelectionModel().getSelectedCells().get(0);
//                                    int selectedRow = dataTable.getItems().get(pos.getRow()).getId();
//                                    packagingOwnerDAO.updateDataInteger("size_id", t1.getId(), selectedRow);
////                                    clearFields();
////                                    reload();
//                                }
//                            }
//                        }
//                    });
//                }
//            });
//
//            return cell ;
//        });

        packagingColumn.setCellValueFactory(new PropertyValueFactory<PackagingOwnerString, String>("packagingName"));
//        packagingColumn.setCellFactory(tc -> {
//
//            TableCell<PackagingOwnerString, String> cell = new TableCell<>();
//
//            currentRow = cell.getIndex();
//            currentCell = cell.getText();
//
//            Text text = new Text();
//            ComboBox<PackagingToString> packagingComboBoxTableCell = new ComboBox<>();
//            packagingComboBoxTableCell.setConverter(new StringConverter<PackagingToString>() {
//                @Override
//                public String toString(PackagingToString packaging) {
//                    if (packaging == null) return null;
//                    return packaging.toString();
//                }
//
//                @Override
//                public PackagingToString fromString(String s) {
//                    return null;
//                }
//            });
//
//            packagingComboBoxTableCell.getItems().addAll(packagingObservableList);
//            new AutoCompleteComboBoxListener<>(packagingComboBoxTableCell, true, packagingDAO.getLastestData().getName());
//
//            cell.setGraphic(text);
//            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
//            text.wrappingWidthProperty().bind(packagingColumn.widthProperty());
//
//            text.textProperty().bind(new StringBinding() {
//                { bind(cell.itemProperty()); }
//                @Override
//                protected String computeValue() {
//                    if(cell.itemProperty().getValue() != null) {
//                        Packaging data = packagingDAO.getDataByName(cell.itemProperty().getValue());
//                        return data.getName();
//                    } else {
//                        return "";
//                    }
//                }
//            });
//
//            cell.setOnMouseClicked(e -> {
//                if (e.getClickCount() == 2 && ! cell.isEmpty()) {
//                    cell.setGraphic(packagingComboBoxTableCell);
//                    cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
//                    packagingComboBoxTableCell.setMaxWidth(Control.USE_COMPUTED_SIZE);
//                    packagingComboBoxTableCell.setEditable(true);
//                    packagingComboBoxTableCell.getEditor().setText(packagingDAO.getDataByName(cell.itemProperty().getValue()).getName());
//
//                    packagingComboBoxTableCell.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PackagingToString>() {
//                        @Override
//                        public void changed(ObservableValue<? extends PackagingToString> observableValue, PackagingToString packaging, PackagingToString t1) {
//                            if(t1 != null) {
//                                if(!cell.isEmpty()) {
//                                    TablePosition pos = (TablePosition) dataTable.getSelectionModel().getSelectedCells().get(0);
//                                    int selectedRow = dataTable.getItems().get(pos.getRow()).getId();
//                                    packagingOwnerDAO.updateDataInteger("packaging_id", t1.getId(), selectedRow);
////                                    clearFields();
////                                    reload();
//                                }
//                            }
//                        }
//                    });
//                    packagingComboBoxTableCell.addEventFilter(KeyEvent.ANY, ke -> {
//                        if(ke.getCode() == KeyCode.ENTER) {
//                            clearFields();
//                            reload();
//                        }
//                    });
//                }
//            });
//
//            return cell ;
//        });

        packingQtyColumn.setCellValueFactory(new PropertyValueFactory<>("pack_qty"));
//        packingQtyColumn.setCellFactory(TextFieldTableCell.<PackagingOwnerString, Float>forTableColumn(new FloatStringConverter()));
        packingQtyColumn.setCellFactory(tc -> {
            TableCell<PackagingOwnerString, Float> cell = new TableCell<>();

            currentRow = cell.getIndex();
            currentCell = cell.getText();

            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(packingQtyColumn.widthProperty());
            text.textProperty().bind(new StringBinding() {
                { bind(cell.itemProperty()); }
                @Override
                protected String computeValue() {
                    Locale locale  = new Locale("en", "US");
                    DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(locale);
                    df.applyPattern("###,###.###");
                    return cell.itemProperty().getValue() != null ? df.format(cell.itemProperty().getValue()) : "";
                }
            });
            return cell;
        });
        packingQtyColumn.setOnEditCommit(event -> {
            final float data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((PackagingOwnerString) event.getTableView().getItems().get(event.getTablePosition().getRow())).setPack_qty(data);
            packagingOwnerDAO.updateDataFloat("pack_qty", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        noteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));
        noteColumn.setCellFactory(TextFieldTableCell.<PackagingOwnerString>forTableColumn());
        noteColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((PackagingOwnerString) event.getTableView().getItems().get(event.getTablePosition().getRow())).setNote(data);
            packagingOwnerDAO.updateData("note", data, event.getRowValue().getId());
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
