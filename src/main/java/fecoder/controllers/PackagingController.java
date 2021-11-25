package fecoder.controllers;

import fecoder.DAO.PackagingDAO;
import fecoder.DAO.SupplierDAO;
import fecoder.DAO.TypeDAO;
import fecoder.models.*;
import fecoder.utils.AutoFill.AutoFillTextBox;
import fecoder.utils.Utils;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.apache.poi.ss.formula.functions.T;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class PackagingController implements Initializable {
    public TextField nameField;
    public TextField specificationField;
    public TextField dimensionField;
    public ComboBox<Type> typeComboBox;
    public ComboBox<Supplier> suplierComboBox;
    public TextField minimumField;
    public TextField codeField;
    public TextField priceField;
    public TextField noteField;
    public Button insertButton;
    public Button updateButton;
    public Button clearButton;
    public ComboBox<String> searchComboBox;
    public TextField searchField;
    public Button reloadData;
    public CheckBox stampedField;
    public CheckBox mainField;
    public Label anchorLabel;
    public Label anchorData;
    public TextField stockField;

    public TableView<Packaging> dataTable;
    public TableColumn<Packaging, Integer> idColumn;
    public TableColumn<Packaging, Boolean> stampedColumn;
    public TableColumn<Packaging, Boolean> mainColumn;
    public TableColumn<Packaging, String> nameColumn;
    public TableColumn<Packaging, String> specificationColumn;
    public TableColumn<Packaging, String> dimensionColumn;
    public TableColumn<Packaging, Integer> minimumColumn;
    public TableColumn<Packaging, Integer> typeColumn;
    public TableColumn<Packaging, Integer> suplierColumn;
    public TableColumn<Packaging, String> codeColumn;
    public TableColumn<Packaging, Float> priceColumn;
    public TableColumn<Packaging, String> noteColumn;
    public TableColumn<Packaging, Float> stockColumn;

    private final TypeDAO typeDAO = new TypeDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final PackagingDAO packagingDAO = new PackagingDAO();

    private int currentRow;
    private String currentCell;
    private final Utils utils = new Utils();

    private boolean isEditableComboBox = false;
    private boolean isUpdating = false;

    private final  ObservableList<Type> type_obs = FXCollections.observableArrayList(typeDAO.getList());
    private final  ObservableList<Supplier> supplier_obs = FXCollections.observableArrayList(supplierDAO.getList());

    /**
     * All needed to start controller
     * */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeComboBox.getItems().addAll(type_obs);
        suplierComboBox.getItems().addAll(supplier_obs);
        supplierComboBoxFilter();
        loadView();
    }

    /**
     * Handle on filter Customer Combobox
     * */
    private void supplierComboBoxFilter() {
        // Create the listener to filter the list as user enters search terms
        FilteredList<Supplier> dataFilteredList = new FilteredList<>(supplier_obs, p-> true);

        // Add listener to our ComboBox textfield to filter the list
        suplierComboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            dataFilteredList.setPredicate(item -> {

                this.isUpdating = false;

                // Check if the search term is contained anywhere in our list
                if (item.getName().toLowerCase().contains(newValue.toLowerCase().trim()) && !isUpdating) {
                    suplierComboBox.show();
                    return true;
                }

                // No matches found
                return false;
            });

        });

        suplierComboBox.getItems().removeAll(supplier_obs);
        suplierComboBox.getItems().addAll(dataFilteredList);
    }

    /**
     * Inserting button action
     * */
    public void insertButton(ActionEvent actionEvent) {
        packagingDAO.insert(
                nameField.getText(),
                specificationField.getText(),
                dimensionField.getText(),
                supplierDAO.getDataByCode(suplierComboBox.getValue()+"").getId(),
                typeDAO.getDataByName(typeComboBox.getValue()+"").getId(),
                minimumField.getText().isEmpty() ? 0 : Integer.parseInt(minimumField.getText()),
                stampedField.isSelected(),
                codeField.getText(),
                mainField.isSelected(),
                noteField.getText(),
                priceField.getText().isEmpty() ? 0 : Float.parseFloat(priceField.getText()),
                stockField.getText().isEmpty() ? 0 : Float.parseFloat(stockField.getText())
        );
        clearFields();
        reload();
    }

    /**
     * Updating button action
     * */
    public void updateButton(ActionEvent actionEvent) {
        packagingDAO.update(
                nameField.getText(),
                specificationField.getText(),
                dimensionField.getText(),
                supplierDAO.getDataByCode(suplierComboBox.getValue()+"").getId(),
                typeDAO.getDataByName(typeComboBox.getValue()+"").getId(),
                Integer.parseInt(minimumField.getText()),
                stampedField.isSelected(),
                codeField.getText(),
                mainField.isSelected(),
                noteField.getText(),
                Float.parseFloat(priceField.getText()),
                Float.parseFloat(stockField.getText()),
                Integer.parseInt(anchorData.getText())
        );
        clearFields();
        reload();
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
        utils.reloadTableViewOnChange(dataTable, currentRow, currentCell);
        clearFields();
        loadView();
    }

    /**
     * Handle event on clearing all inputs
     * */
    private void clearFields() {
        nameField.setText("");
        specificationField.setText("");
        dimensionField.setText("");
        resetComboBox();
        minimumField.setText("");
        stampedField.setSelected(false);
        codeField.setText("");
        mainField.setSelected(false);
        noteField.setText("");
        priceField.setText("");
        stockField.setText("");
        anchorLabel.setText(null);
        anchorData.setText(null);
    }

    /**
     * Handle on clearing comnbobox
     * */
    private void resetComboBox() {
        utils.setComboBoxValue(typeComboBox, typeDAO.getDataByID(1).getName());
        utils.setComboBoxValue(suplierComboBox, supplierDAO.getDataByID(5).getCode());
    }

    /**
     * Handle on clearing comnbobox
     * */
    private void getComboBoxData(Packaging packaging) {
        if(!suplierComboBox.isEditable()) {
            suplierComboBox.getSelectionModel().select(supplierDAO.getDataByID(packaging.getSuplier()));
        } else {
            Supplier _supplierModel = supplierDAO.getDataByID(packaging.getSuplier());
            suplierComboBox.getEditor().setText(_supplierModel.getCode());
        }

        if(!typeComboBox.isEditable()) {
            typeComboBox.getSelectionModel().select(typeDAO.getDataByID(packaging.getType()));
        } else {
            Type _typeModel = typeDAO.getDataByID(packaging.getType());
            typeComboBox.getEditor().setText(_typeModel.getName());
        }
    }

    /**
     * Hiding the ComboBox while on updating stage.
     * */
    private void hideComboBoxForUpdatingData() {
    }

    /**
     * Setting data for inputs
     *
     * @param packaging - the packaging data
     * */
    private void getPackaging(Packaging packaging) {
        nameField.setText(packaging.getName());
        specificationField.setText(packaging.getSpecifications());
        dimensionField.setText(packaging.getDimension());

        getComboBoxData(packaging);

        minimumField.setText(packaging.getMinimum_order()+"");
        stampedField.selectedProperty().bindBidirectional(new SimpleBooleanProperty(packaging.isStamped()));
        codeField.setText(packaging.getCode());
        mainField.selectedProperty().bindBidirectional(new SimpleBooleanProperty(packaging.isMain()));
        noteField.setText(packaging.getNote());
        priceField.setText(packaging.getPrice()+"");
        stockField.setText(packaging.getStock()+"");
        anchorLabel.setText("ID selected: ");
        anchorData.setText(""+packaging.getId());

        isUpdating = true;
    }

    /**
     * Set an event for typeComboBox
     * */
    public void setTypeComboBoxEvent() {
        typeComboBox.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                Type type = typeComboBox.getSelectionModel().getSelectedItem();
                System.out.println(type.getId());
            }
        });
    }

    /**
     * Set an event for suplierComboBox
     * */
    public void setSuplierComboBoxEvent() {
        suplierComboBox.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                Supplier supplier = suplierComboBox.getSelectionModel().getSelectedItem();
                System.out.println(supplier.getId());
            }
        });
    }

    /**
     * Handle on searching data
     * */
    public void setSearchField() {
        ObservableList<Packaging> packagingObservableList = FXCollections.observableArrayList(packagingDAO.getList());
        FilteredList<Packaging> packagingFilteredList = new FilteredList<>(packagingObservableList, p -> true);

        searchField.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    switch (searchComboBox.getValue()) {
                        case "Mã BB":
                            packagingFilteredList.setPredicate(str -> {
                                if (newValue == null || newValue.isEmpty())
                                    return true;
                                String lowerCaseFilter = newValue.toLowerCase();
                                return str.getCode().toLowerCase().contains
                                        (lowerCaseFilter);
                            });
                            break;
                        case "Tên BB":
                            packagingFilteredList.setPredicate(str -> {
                                if (newValue == null || newValue.isEmpty())
                                    return true;
                                String lowerCaseFilter = newValue.toLowerCase();
                                return str.getName().toLowerCase().contains
                                        (lowerCaseFilter);
                            });
                            break;
                        case "Kích thước":
                            packagingFilteredList.setPredicate(str -> {
                                if (newValue == null || newValue.isEmpty())
                                    return true;
                                String lowerCaseFilter = newValue.toLowerCase();
                                return str.getDimension().toLowerCase().contains
                                        (lowerCaseFilter);
                            });
                            break;
                    }
                });

        searchComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal != null) {
                searchField.setText("");
            }
        });

        SortedList<Packaging> packagingSortedList = new SortedList<>(packagingFilteredList);
        packagingSortedList.comparatorProperty().bind(dataTable.comparatorProperty());

        dataTable.setItems(packagingSortedList);
    }

    /**
     * Getting current row on click
     * */
    public void getCurrentRow() {
        TableView.TableViewSelectionModel<Packaging> packagingTableViewSelectionModel = dataTable.getSelectionModel();
        packagingTableViewSelectionModel.setSelectionMode(SelectionMode.SINGLE);

        ObservableList<Packaging> packagingObservableList1 = packagingTableViewSelectionModel.getSelectedItems();

        packagingObservableList1.addListener(new ListChangeListener<Packaging>() {
            @Override
            public void onChanged(Change<? extends Packaging> change) {
//                System.out.println("Selection changed: " + change.getList());
            }
        });
    }

    /**
     * Reset fields
     * This will handle an enter event and mouse leave for these Nodes
     * */
    private void resetFields() {
        utils.disableKeyEnterOnTextField(nameField);
        utils.disableKeyEnterOnTextField(specificationField);
        utils.disableKeyEnterOnTextField(dimensionField);
        utils.disableKeyEnterOnTextField(minimumField);
        utils.disableKeyEnterOnTextField(priceField);
        utils.disableKeyEnterOnTextField(stockField);
        utils.disableKeyEnterOnTextField(noteField);

        utils.disableKeyEnterOnTextFieldComboBox(typeComboBox, true);
        utils.disableKeyEnterOnTextFieldComboBox(suplierComboBox, true);
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
    public void loadView(){
        resetComboBox();

        anchorLabel.setText("No ID Selected");
        resetFields();

        setTypeComboBoxEvent();

        setSuplierComboBoxEvent();

        dataTable.setEditable(true);

        getCurrentRow();

        idColumn.setSortable(false);
        idColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Integer>(dataTable.getItems().indexOf(column.getValue())+1));

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellFactory(tc -> {

            TableCell<Packaging, String> cell = new TableCell<>();

            currentRow = cell.getIndex();
            currentCell = cell.getText();

            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(nameColumn.widthProperty());
            text.textProperty().bind(new StringBinding() {
                { bind(cell.itemProperty()); }
                @Override
                protected String computeValue() {
                    if(cell.itemProperty().getValue() != null) {
                        Packaging item = packagingDAO.getDataByName(cell.itemProperty().getValue());
//                        return item.isStamped() ? cell.itemProperty().getValue()+" (Số LOT)" : cell.itemProperty().getValue()+"";
                        return cell.itemProperty().getValue()+"";
                    } else {
                        return "";
                    }
                }
            });

//            EDIT IN NEW WINDOW
            cell.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && ! cell.isEmpty()) {
                    utils.openTextareaWindow(dataTable.getScene().getWindow(), cell.getItem(), newValue -> {
                        Packaging item = dataTable.getItems().get(cell.getIndex());
                        item.setName(newValue);
                        packagingDAO.updateData("name", newValue, item.getId());
                        dataTable.refresh();
                    }, "Cập nhật tên bao bì");
                }
            });

            return cell ;
        });

        specificationColumn.setCellValueFactory(new PropertyValueFactory<>("specifications"));
        specificationColumn.setCellFactory(tc -> {

            TableCell<Packaging, String> cell = new TableCell<>();

            currentRow = cell.getIndex();
            currentCell = cell.getText();

            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(specificationColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());

//            EDIT IN NEW WINDOW
            cell.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && ! cell.isEmpty()) {
                    utils.openTextareaWindow(dataTable.getScene().getWindow(), cell.getItem(), newValue -> {
                        Packaging item = dataTable.getItems().get(cell.getIndex());
                        item.setName(newValue);
                        packagingDAO.updateData("specifications", newValue, item.getId());
                        dataTable.refresh();
                    }, "Cập nhật qui cách");
                }
            });

            return cell ;
        });

        dimensionColumn.setCellValueFactory(new PropertyValueFactory<>("dimension"));
        dimensionColumn.setCellFactory(TextFieldTableCell.<Packaging>forTableColumn());
        dimensionColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((Packaging) event.getTableView().getItems().get(event.getTablePosition().getRow())).setDimension(data);
            packagingDAO.updateData("dimension", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        minimumColumn.setCellValueFactory(new PropertyValueFactory<>("minimum_order"));
        minimumColumn.setCellFactory(TextFieldTableCell.<Packaging, Integer>forTableColumn(new IntegerStringConverter()));
        minimumColumn.setOnEditCommit(event -> {
            final int data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((Packaging) event.getTableView().getItems().get(event.getTablePosition().getRow())).setMinimum_order(data);
            packagingDAO.updateDataInteger("minimum_order", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        codeColumn.setCellFactory(TextFieldTableCell.<Packaging>forTableColumn());
        codeColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((Packaging) event.getTableView().getItems().get(event.getTablePosition().getRow())).setCode(data);
            packagingDAO.updateData("code", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceColumn.setCellFactory(TextFieldTableCell.<Packaging, Float>forTableColumn(new FloatStringConverter()));
        priceColumn.setOnEditCommit(event -> {
            final float data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((Packaging) event.getTableView().getItems().get(event.getTablePosition().getRow())).setPrice(data);
            packagingDAO.updateDataFloat("price", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        stockColumn.setCellFactory(TextFieldTableCell.<Packaging, Float>forTableColumn(new FloatStringConverter()));
        stockColumn.setOnEditCommit(event -> {
            final float data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((Packaging) event.getTableView().getItems().get(event.getTablePosition().getRow())).setStock(data);
            packagingDAO.updateDataFloat("stock", data, event.getRowValue().getId());
            dataTable.refresh();
        });

        noteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));
        noteColumn.setCellFactory(tc -> {

            TableCell<Packaging, String> cell = new TableCell<>();

            currentRow = cell.getIndex();
            currentCell = cell.getText();

            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(noteColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());

//            EDIT IN NEW WINDOW
            cell.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && ! cell.isEmpty()) {
                    utils.openTextareaWindow(dataTable.getScene().getWindow(), cell.getItem(), newValue -> {
                        Packaging item = dataTable.getItems().get(cell.getIndex());
                        item.setName(newValue);
                        packagingDAO.updateData("note", newValue, item.getId());
                        dataTable.refresh();
                    }, "Cập nhật ghi chú");
                }
            });

            return cell ;
        });

        stampedColumn.setCellFactory( CheckBoxTableCell.forTableColumn(stampedColumn) );
        stampedColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Packaging, Boolean>, ObservableValue<Boolean>>()
                {
                    @Override
                    public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Packaging, Boolean> param)
                    {
                        boolean check = param.getValue().isMain();

                        Packaging packaging = param.getValue();

                        SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(packaging.isStamped());


                        // Chú ý: singleCol.setOnEditCommit(): Không làm việc với
                        // CheckBoxTableCell.

                        // Khi cột "Single?" thay đổi
                        booleanProp.addListener(new ChangeListener<Boolean>() {

                            @Override
                            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                                                Boolean newValue) {
                                packagingDAO.updateDataBoolean("stamped", newValue, packaging.getId());
                                packaging.setStamped(newValue);
                            }
                        });
                        return booleanProp;
                    }
                });
        mainColumn.setCellFactory( CheckBoxTableCell.forTableColumn(mainColumn) );
        mainColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Packaging, Boolean>, ObservableValue<Boolean>>()
                {
                    //This callback tell the cell how to bind the data model 'Registered' property to
                    //the cell, itself.
                    @Override
                    public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Packaging, Boolean> param)
                    {
                        boolean check = param.getValue().isMain();
//                         int check = param.getValue().isMain() ? 1 : 0;
//                        return new SimpleBooleanProperty(check);

//                        Packaging packaging = param.getValue();
//                        SimpleBooleanProperty booleanProperty = new SimpleBooleanProperty(packaging.isMain());

//                        booleanProperty.addListener(new ChangeListener<Boolean>() {
//                            @Override
//                            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
//                                packagingDAO.updateDataInteger("main", check, param.getValue().getId());
//                            }
//                        });

//                        return new SimpleBooleanProperty(check);
//                        return booleanProperty;

                        Packaging packaging = param.getValue();

                        SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(packaging.isMain());


                        // Chú ý: singleCol.setOnEditCommit(): Không làm việc với
                        // CheckBoxTableCell.

                        // Khi cột "Single?" thay đổi
                        booleanProp.addListener(new ChangeListener<Boolean>() {

                            @Override
                            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                                                Boolean newValue) {
                                packagingDAO.updateDataBoolean("main", newValue, packaging.getId());
                                packaging.setMain(newValue);
                            }
                        });
                        return booleanProp;
                    }
                });

        ObservableList<Type> typeObservableList = FXCollections.observableArrayList(typeDAO.getList());
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
//        typeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(typeObservableList));
//        typeColumn.setCellFactory(new Callback<TableColumn<Packaging, Type>,TableCell<Packaging, Type>>() {
//
//            @Override
//            public TableCell<Packaging, Type> call(TableColumn<Packaging, Type> param) {
//
//                ComboBoxTableCell<Packaging, Type> ct= new ComboBoxTableCell<>();
////                ct.getItems().addAll(FXCollections.observableArrayList(typeDAO.getTypes()));
//
//                ct.setComboBoxEditable(true);
//
//                FilteredList<Type> typeFilteredList = new FilteredList<>(typeObservableList, p -> true);
//                ct.textProperty()
//                        .addListener((observable, oldValue, newValue) -> {
//                            typeFilteredList.setPredicate(str -> {
//                                if (newValue == null || newValue.isEmpty())
//                                    return true;
//                                String lowerCaseFilter = newValue.toLowerCase();
//                                return str.getName().toLowerCase().contains
//                                        (lowerCaseFilter);
//                            });
//
//                            System.out.println(typeFilteredList + " " +  oldValue + " " + newValue);
//                        });
//
//                SortedList<Type> typeSortedList = new SortedList<>(typeFilteredList);
//
//                ct.getItems().clear();
//                ct.getItems().addAll(typeSortedList);
//
//                return ct;
//            }
//
//        });


//        typeColumn.setOnEditCommit(event -> {
//            event.getRowValue().setType(event.getNewValue().getId());
//            packagingDAO.updateDataInteger("type", event.getNewValue().getId(), event.getRowValue().getId());
//        });

        typeColumn.setCellFactory(TextFieldTableCell.<Packaging, Integer>forTableColumn(new IntegerStringConverter()));
        typeColumn.setCellFactory(tc -> {

            TableCell<Packaging, Integer> cell = new TableCell<>();

            currentRow = cell.getIndex();
            currentCell = cell.getText();

            Text text = new Text();
            AutoFillTextBox<Type> box = new AutoFillTextBox<Type>(typeObservableList);
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(typeColumn.widthProperty());
            text.textProperty().bind(new StringBinding() {
                { bind(cell.itemProperty()); }
                @Override
                protected String computeValue() {
//                    return cell.itemProperty().getValue() != null ? cell.itemProperty().getValue()+"" : "";
                    if(cell.itemProperty().getValue() != null) {
                        Type typeByName = typeDAO.getDataByID(cell.itemProperty().getValue());
                        return typeByName.getName();
                    } else {
                     return "";
                    }
                }
            });

            cell.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && ! cell.isEmpty()) {
                    TablePosition pos = (TablePosition) dataTable.getSelectionModel().getSelectedCells().get(0);
                    int selectedRow = dataTable.getItems().get(pos.getRow()).getId();
                    cell.setGraphic(box);
                    cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
                    box.getTextbox().setPromptText("Nhập từ để tìm");

                    box.getTextbox().setOnKeyReleased(event -> {
                        if(event.getCode() == KeyCode.ENTER && !typeDAO.hasName(box.getTextbox().textProperty().getValue())) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Lỗi!");
                            alert.setHeaderText("Đã có lỗi xảy ra!");
                            alert.setContentText(box.getText() + " không tồn tại! Xin thử lại!");

                            alert.showAndWait();
                        }
                    });

                    box.getTextbox().textProperty().addListener((obs, oldVal, newVal) -> {
                        if (!box.getText().isEmpty() && box.getItem() != null && box.getItem().getName().toLowerCase().trim().equals(newVal.toLowerCase().trim())) {
                            packagingDAO.updateDataInteger("type", box.getItem().getId(), selectedRow);
                            dataTable.refresh();
                            reload();
                        }
                    });

                    box.getTextbox().setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            Type typeByName = typeDAO.getDataByName(box.getTextbox().textProperty().getValue());

                            int newPackagingTypeID = typeByName.getId();

                            if (typeDAO.hasName(box.getTextbox().textProperty().getValue())) {
                                packagingDAO.updateDataInteger("type", newPackagingTypeID, selectedRow);
                                dataTable.refresh();
                                reload();
                            }
                        }
                    });
                }
            });

            cell.setOnKeyPressed(event -> {
                if(event.getCode() == KeyCode.ESCAPE) {
                    cell.setGraphic(text);
                }
            });

            return cell ;
        });

        ObservableList<Supplier> supplierObservableList = FXCollections.observableArrayList(supplierDAO.getList());
        suplierColumn.setCellValueFactory(new PropertyValueFactory<>("suplier"));
        suplierColumn.setCellFactory(TextFieldTableCell.<Packaging, Integer>forTableColumn(new IntegerStringConverter()));
        suplierColumn.setCellFactory(tc -> {

            TableCell<Packaging, Integer> cell = new TableCell<>();

            currentRow = cell.getIndex();
            currentCell = cell.getText();

            Text text = new Text();
            AutoFillTextBox<Supplier> box = new AutoFillTextBox<Supplier>(supplierObservableList);
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(suplierColumn.widthProperty());
            text.textProperty().bind(new StringBinding() {
                { bind(cell.itemProperty()); }
                @Override
                protected String computeValue() {
//                    return cell.itemProperty().getValue() != null ? cell.itemProperty().getValue()+"" : "";
                    if(cell.itemProperty().getValue() != null) {
                        Supplier dataByID = supplierDAO.getDataByID(cell.itemProperty().getValue());
                        return dataByID.getCode();
                    } else {
                        return "";
                    }
                }
            });

            cell.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && ! cell.isEmpty()) {
                    TablePosition pos = (TablePosition) dataTable.getSelectionModel().getSelectedCells().get(0);
                    int selectedRow = dataTable.getItems().get(pos.getRow()).getId();
                    cell.setGraphic(box);
                    cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
                    box.getTextbox().setPromptText("Nhập mã NCC");

                    box.getTextbox().setOnKeyReleased(event -> {
                        if(event.getCode() == KeyCode.ENTER && !supplierDAO.hasName(box.getTextbox().textProperty().getValue())) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Lỗi!");
                            alert.setHeaderText("Đã có lỗi xảy ra!");
                            alert.setContentText(box.getText() + " không tồn tại! Xin thử lại!");

                            alert.showAndWait();
                        }
                    });

                    box.getTextbox().textProperty().addListener((obs, oldVal, newVal) -> {
                        if (!box.getText().isEmpty() && box.getItem() != null && box.getItem().getCode().toLowerCase().trim().equals(newVal.toLowerCase().trim())) {
                            packagingDAO.updateDataInteger("suplier", box.getItem().getId(), selectedRow);
                            dataTable.refresh();
                            reload();
                        }
                    });

                    box.getTextbox().setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            Supplier dataByName = supplierDAO.getDataByName(box.getTextbox().textProperty().getValue());

                            int newPackagingSuplierID = dataByName.getId();

                            if (supplierDAO.hasName(box.getTextbox().textProperty().getValue())) {
                                packagingDAO.updateDataInteger("suplier", newPackagingSuplierID, selectedRow);
                                dataTable.refresh();
                                reload();
                            }
                        }
                    });
                }
            });

            cell.setOnKeyPressed(event -> {
                if(event.getCode() == KeyCode.ESCAPE) {
                    cell.setGraphic(text);
                }
            });

            return cell ;
        });

        dataTable.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ESCAPE || event.getCode() == KeyCode.F5) {
                dataTable.refresh();
            }
        });

        dataTable.setRowFactory((TableView<Packaging> tableView) -> {
            final TableRow<Packaging> row = new TableRow<>();

            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem viewItem = new MenuItem("Chi tiết");
            final MenuItem editItem = new MenuItem("Cập nhật");
            final MenuItem removeItem = new MenuItem("Xóa dòng");

            viewItem.setOnAction((ActionEvent event) -> {
                Packaging packaging = dataTable.getSelectionModel().getSelectedItem();
                int rowIndex = dataTable.getSelectionModel().getSelectedIndex();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Chi tiết bao bì");
                alert.setHeaderText(packaging.getName());
                Supplier supplier = supplierDAO.getDataByID(packaging.getSuplier());
                Type type = typeDAO.getDataByID(packaging.getType());
                String packagingCode = packaging.getCode() != null ? packaging.getCode() : "Không qui định";
                String stampStatus = packaging.isStamped() ? "In sẵn (Thông tin theo lô)" : "Không in";
                String mainStatus = packaging.isMain() ? "Bao gói chính" : "Bao bì bên trong";
                alert.setContentText(
                        "Mã bao bì: " + packagingCode + "\n" +
                        "Nhà cung cấp: " + supplier.getName() + "\n" +
                        "Tên bao bì: " + packaging.getName() + "\n" +
                        "Qui cách: " + packaging.getSpecifications() + "\n" +
                        "Kích thước: " + packaging.getDimension() + "\n" +
                        "Loại: " + type.getName() + "\n" +
                        "Đơn vị tính: (" + type.getUnit() + ") \n" +
                        "Loại in: " + stampStatus + "\n" +
                        "Loại đóng gói: " + mainStatus + "\n" +
                        "Đặt tối thiểu: " + packaging.getMinimum_order() + "\n" +
                        "Còn tồn: " + packaging.getStock() + "\n" +
                        "Đơn giá: " + packaging.getPrice() + "\n" +
                        "Ghi chú: " + packaging.getNote() + "\n"
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
                Packaging packaging = dataTable.getSelectionModel().getSelectedItem();
//                System.out.println(packaging.getId() + " " + packaging.getType() + " " + packaging.getSuplier());
                getPackaging(packaging);
                hideComboBoxForUpdatingData();
            });
            contextMenu.getItems().add(editItem);

            removeItem.setOnAction((ActionEvent event) -> {
                Packaging packaging = dataTable.getSelectionModel().getSelectedItem();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Thông báo!");
                alert.setHeaderText("Xóa: "+packaging.getName());
                alert.setContentText("Lưu ý: Dữ liệu sẽ không thể khôi phục lại sau khi xóa, bạn có chắc muốn tiếp tục?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    packagingDAO.delete(packaging.getId());
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

        utils.reloadTableViewOnChange(dataTable, currentRow, currentCell);
        setSearchField();
    }
}
