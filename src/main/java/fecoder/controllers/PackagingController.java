package fecoder.controllers;

import fecoder.DAO.PackagingDAO;
import fecoder.DAO.SupplierDAO;
import fecoder.DAO.TypeDAO;
import fecoder.models.Packaging;
import fecoder.models.Supplier;
import fecoder.models.Type;
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
    public TableView<Packaging> dataTable;
    public TableColumn<Packaging, Integer> idColumn;
    public CheckBox stampedField;
    public CheckBox mainField;
    public TableColumn<Packaging, Boolean> stampedColumn;
    public TableColumn<Packaging, Boolean> mainColumn;
    public TableColumn<Packaging, String> nameColumn;
    public TableColumn<Packaging, String> specificationColumn;
    public TableColumn<Packaging, String> dimensionColumn;
    public TableColumn<Packaging, Integer> minimumColumn;
//    public TableColumn<Packaging, Type> typeColumn;
    public TableColumn<Packaging, Integer> typeColumn;
    public TableColumn<Packaging, Integer> suplierColumn;
    public TableColumn<Packaging, String> codeColumn;
    public TableColumn<Packaging, Float> priceColumn;
    public TableColumn<Packaging, String> noteColumn;

    private final TypeDAO typeDAO = new TypeDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final PackagingDAO packagingDAO = new PackagingDAO();
    public Label anchorLabel;
    public Label anchorData;

    private final Utils utils = new Utils();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeComboBox.getItems().addAll(FXCollections.observableArrayList(typeDAO.getList()));
        suplierComboBox.getItems().addAll(FXCollections.observableArrayList(supplierDAO.getList()));
        loadView();
    }

    public void insertButton(ActionEvent actionEvent) {
        packagingDAO.insert(
                nameField.getText(),
                specificationField.getText(),
                dimensionField.getText(),
                suplierComboBox.getSelectionModel().getSelectedItem().getId(),
                typeComboBox.getSelectionModel().getSelectedItem().getId(),
                Integer.parseInt(minimumField.getText()),
                stampedField.isSelected(),
                codeField.getText(),
                mainField.isSelected(),
                noteField.getText(),
                Float.parseFloat(priceField.getText())
        );
        clearFields();
        reload();
    }

    public void updateButton(ActionEvent actionEvent) {
        packagingDAO.update(
                nameField.getText(),
                specificationField.getText(),
                dimensionField.getText(),
                suplierComboBox.getSelectionModel().getSelectedItem().getId(),
                typeComboBox.getSelectionModel().getSelectedItem().getId(),
                Integer.parseInt(minimumField.getText()),
                stampedField.isSelected(),
                codeField.getText(),
                mainField.isSelected(),
                noteField.getText(),
                Float.parseFloat(priceField.getText()),
                Integer.parseInt(anchorData.getText())
        );
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
        clearFields();
        loadView();
    }

    private void clearFields() {
        nameField.setText(null);
        specificationField.setText(null);
        dimensionField.setText(null);
//        suplierComboBox.setValue(null);
        if(!suplierComboBox.getItems().isEmpty()) {
            suplierComboBox.getSelectionModel().clearSelection();
        }
//        typeComboBox.setValue(null);
        if(!typeComboBox.getItems().isEmpty()) {
            typeComboBox.getSelectionModel().clearSelection();
        }
        minimumField.setText(null);
        stampedField.setSelected(false);
        codeField.setText(null);
        mainField.setSelected(false);
        noteField.setText(null);
        priceField.setText(null);
        anchorLabel.setText(null);
        anchorData.setText(null);
    }

    private void getPackaging(Packaging p) {
        nameField.setText(p.getName());
        specificationField.setText(p.getSpecifications());
        dimensionField.setText(p.getDimension());
        suplierComboBox.getSelectionModel().select(p.getSuplier());
        typeComboBox.getSelectionModel().select(p.getType());
        minimumField.setText(p.getMinimum_order()+"");
        stampedField.selectedProperty().bindBidirectional(new SimpleBooleanProperty(p.isStamped()));
        codeField.setText(p.getCode());
//        mainField.setSelected(p.getMain() == 1);
        mainField.selectedProperty().bindBidirectional(new SimpleBooleanProperty(p.isMain()));
        noteField.setText(p.getNote());
        priceField.setText(p.getPrice()+"");
        anchorLabel.setText("Current ID: ");
        anchorData.setText(""+p.getId());
    }

    public void loadView(){
//        typeComboBox.getItems().addAll(FXCollections.observableArrayList(typeDAO.getTypes()));
        typeComboBox.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                Type type = typeComboBox.getSelectionModel().getSelectedItem();
                System.out.println(type.getId());
            }
        });
//        suplierComboBox.getItems().addAll(FXCollections.observableArrayList(suplierDAO.getSupliers()));
        suplierComboBox.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                Supplier supplier = suplierComboBox.getSelectionModel().getSelectedItem();
                System.out.println(supplier.getId());
            }
        });

        ObservableList<Packaging> packagingObservableList = FXCollections.observableArrayList(packagingDAO.getList());
        FilteredList<Packaging> packagingFilteredList = new FilteredList<>(packagingObservableList, p -> true);

        searchField.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    switch (searchComboBox.getValue()) {
                        case "Mã":
                            packagingFilteredList.setPredicate(str -> {
                                if (newValue == null || newValue.isEmpty())
                                    return true;
                                String lowerCaseFilter = newValue.toLowerCase();
                                return str.getCode().toLowerCase().contains
                                        (lowerCaseFilter);
                            });
                            break;
                        case "Tên":
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
                searchField.setText(null);
            }
        });

        SortedList<Packaging> packagingSortedList = new SortedList<>(packagingFilteredList);
        packagingSortedList.comparatorProperty().bind(dataTable.comparatorProperty());

        dataTable.setEditable(true);

        TableView.TableViewSelectionModel<Packaging> packagingTableViewSelectionModel = dataTable.getSelectionModel();
        packagingTableViewSelectionModel.setSelectionMode(SelectionMode.SINGLE);

        ObservableList<Packaging> packagingObservableList1 = packagingTableViewSelectionModel.getSelectedItems();

        packagingObservableList1.addListener(new ListChangeListener<Packaging>() {
            @Override
            public void onChanged(Change<? extends Packaging> change) {
//                System.out.println("Selection changed: " + change.getList());
            }
        });

        idColumn.setSortable(false);
        idColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Integer>(dataTable.getItems().indexOf(column.getValue())+1));

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellFactory(tc -> {

            TableCell<Packaging, String> cell = new TableCell<>();

            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(nameColumn.widthProperty());
//            text.textProperty().bind(cell.itemProperty());
            text.textProperty().bind(new StringBinding() {
                { bind(cell.itemProperty()); }
                @Override
                protected String computeValue() {
                    if(cell.itemProperty().getValue() != null) {
                        Packaging item = packagingDAO.getDataByName(cell.itemProperty().getValue());
                        return item.isStamped() ? cell.itemProperty().getValue()+" (Số LOT)" : cell.itemProperty().getValue()+"";
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

        noteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));
        noteColumn.setCellFactory(tc -> {

            TableCell<Packaging, String> cell = new TableCell<>();

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
                getPackaging(packaging);
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

        dataTable.setItems(packagingSortedList);
    }
}
