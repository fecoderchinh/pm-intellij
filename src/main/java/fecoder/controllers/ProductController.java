package fecoder.controllers;

import fecoder.DAO.ProductDAO;
import fecoder.models.Product;
import fecoder.utils.Utils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductController implements Initializable {
    public TextField nameField;
    public TextField descriptionField;
    public TextField specificationField;
    public TextField noteField;
    public Button insertButton;
    public Button updateButton;
    public Button clearButton;
    public ComboBox<String> searchComboBox;
    public TextField searchField;
    public Button reloadData;
    public Label anchorLabel;
    public Label anchorData;

    public TableView<Product> dataTable;
    public TableColumn<Product, Integer> idColumn;
    public TableColumn<Product, String> nameColumn;
    public TableColumn<Product, String> descriptionColumn;
    public TableColumn<Product, String> specificationColumn;
    public TableColumn<Product, String> noteColumn;

    private final ProductDAO productDAO = new ProductDAO();

    private int currentRow;
    private String currentCell;
    private final Utils utils = new Utils();

    /**
     * All needed to start controller
     * */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadView();
    }

    /**
     * Inserting button action
     * */
    public void insertButton(ActionEvent actionEvent) {
        productDAO.insert(
                nameField.getText(),
                descriptionField.getText(),
                specificationField.getText(),
                noteField.getText()
        );
        clearFields();
        reload();
    }

    /**
     * Updating button action
     * */
    public void updateButton(ActionEvent actionEvent) {
        productDAO.update(
                nameField.getText(),
                descriptionField.getText(),
                specificationField.getText(),
                noteField.getText(),
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
        clearFields();
        loadView();
    }

    /**
     * Handle event on clearing all inputs
     * */
    private void clearFields() {
        nameField.setText(null);
        descriptionField.setText(null);
        specificationField.setText(null);
        noteField.setText(null);
        anchorLabel.setText("No ID Selected");
        anchorData.setText(null);
    }

    /**
     * Setting data for inputs
     *
     * @param product - the product data
     * */
    private void getData(Product product) {
        nameField.setText(product.getName());
        descriptionField.setText(product.getDescription());
        specificationField.setText(product.getSpecification());
        noteField.setText(product.getNote());
        anchorLabel.setText("ID Selected:");
        anchorData.setText(""+product.getId());
    }

    /**
     * Handle on searching data
     * */
    public void setSearchField() {
        ObservableList<Product> productObservableList = FXCollections.observableArrayList(productDAO.getList());
        FilteredList<Product> productFilteredList = new FilteredList<>(productObservableList, p -> true);

        searchField.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    switch (searchComboBox.getValue()) {
                        case "Tên mặt hàng":
                            productFilteredList.setPredicate(str -> {
                                if (newValue == null || newValue.isEmpty())
                                    return true;
                                String lowerCaseFilter = newValue.toLowerCase();
                                return str.getName().toLowerCase().contains
                                        (lowerCaseFilter);
                            });
                            break;
                        case "Mô tả":
                            productFilteredList.setPredicate(str -> {
                                if (newValue == null || newValue.isEmpty())
                                    return true;
                                String lowerCaseFilter = newValue.toLowerCase();
                                return str.getDescription().toLowerCase().contains
                                        (lowerCaseFilter);
                            });
                            break;
                        case "Qui cách":
                            productFilteredList.setPredicate(str -> {
                                if (newValue == null || newValue.isEmpty())
                                    return true;
                                String lowerCaseFilter = newValue.toLowerCase();
                                return str.getSpecification().toLowerCase().contains
                                        (lowerCaseFilter);
                            });
                            break;
                    }
                });

        nameField.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    productFilteredList.setPredicate(str -> {
                        if (newValue == null || newValue.isEmpty())
                            return true;
                        String lowerCaseFilter = newValue.toLowerCase();
                        return str.getName().toLowerCase().contains
                                (lowerCaseFilter);
                    });
                });

        searchComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal != null) {
                searchField.setText(null);
            }
        });

        SortedList<Product> productSortedList = new SortedList<>(productFilteredList);
        productSortedList.comparatorProperty().bind(dataTable.comparatorProperty());

        dataTable.setItems(productSortedList);
    }

    /**
     * Getting current row on click
     * */
    public void getCurrentRow() {
        TableView.TableViewSelectionModel<Product> productTableViewSelectionModel = dataTable.getSelectionModel();
        productTableViewSelectionModel.setSelectionMode(SelectionMode.SINGLE);

        ObservableList<Product> productObservableList1 = productTableViewSelectionModel.getSelectedItems();

        productObservableList1.addListener(new ListChangeListener<Product>() {
            @Override
            public void onChanged(Change<? extends Product> change) {
//                System.out.println("Selection changed: " + change.getList());
            }
        });
    }

    /**
     * Loading scene utility
     *
     * @param resource resource path
     * @param title scene title
     * @param product product data
     * */
    private void loadSingleProductScene(Stage stage, String resource, String title, Product product, int minWidth, int minHeight) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(resource));
            /*
             * if "fx:controller" is not set in fxml
             * fxmlLoader.setController(NewWindowController);
             */
            Scene scene = new Scene(fxmlLoader.load());
            Stage _stage = new Stage();
            _stage.setTitle(title);
            _stage.setScene(scene);
            _stage.initModality(Modality.WINDOW_MODAL);
//            _stage.setResizable(false);
            _stage.getIcons().add(new Image("/images/icon.png"));
            stage.setOpacity(0);

            ProductPackagingController productPackagingController = fxmlLoader.<ProductPackagingController>getController();
            productPackagingController.setData(product);

            _stage.setMinWidth(minWidth);
            _stage.setWidth(minWidth);
            _stage.setHeight(minHeight);
            _stage.setMinHeight(minHeight);

            ChangeListener<Number> widthListener = (observable, oldValue, newValue) -> {
                double stageWidth = newValue.doubleValue();
                _stage.setX(stage.getX() + stage.getWidth() / 2 - stageWidth / 2);
            };
            ChangeListener<Number> heightListener = (observable, oldValue, newValue) -> {
                double stageHeight = newValue.doubleValue();
                _stage.setY(stage.getY() + stage.getHeight() / 2 - stageHeight / 2);
            };

            _stage.widthProperty().addListener(widthListener);
            _stage.heightProperty().addListener(heightListener);

            _stage.setOnShown(e -> {
                _stage.widthProperty().removeListener(widthListener);
                _stage.heightProperty().removeListener(heightListener);
            });

            _stage.setMaximized(true);
            _stage.show();

            _stage.setOnHiding(e -> {
                stage.setOpacity(1);
            });

        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        }
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

        anchorLabel.setText("No ID Selected");

        dataTable.setEditable(true);

        getCurrentRow();

        idColumn.setSortable(false);
        idColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Integer>(dataTable.getItems().indexOf(column.getValue())+1));

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellFactory(tc -> {

            TableCell<Product, String> cell = new TableCell<>();

            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(nameColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());

//            EDIT IN NEW WINDOW
            cell.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && ! cell.isEmpty()) {
                    utils.openTextareaWindow(dataTable.getScene().getWindow(), cell.getItem(), newValue -> {
                        Product item = dataTable.getItems().get(cell.getIndex());
                        item.setName(newValue);
                        productDAO.updateData("name", newValue, item.getId());
                        dataTable.refresh();
                    }, "Cập nhật tên mặt hàng");
                }
            });

            return cell ;
        });

        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setCellFactory(tc -> {

            TableCell<Product, String> cell = new TableCell<>();

            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(descriptionColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());

//            EDIT IN NEW WINDOW
            cell.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && ! cell.isEmpty()) {
                    utils.openTextareaWindow(dataTable.getScene().getWindow(), cell.getItem(), newValue -> {
                        Product item = dataTable.getItems().get(cell.getIndex());
                        item.setDescription(newValue);
                        productDAO.updateData("description", newValue, item.getId());
                        dataTable.refresh();
                    }, "Cập nhật mô tả");
                }
            });

            return cell ;
        });

        specificationColumn.setCellValueFactory(new PropertyValueFactory<>("specification"));
        specificationColumn.setCellFactory(tc -> {

            TableCell<Product, String> cell = new TableCell<>();

            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(specificationColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());

//            EDIT IN NEW WINDOW
            cell.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && ! cell.isEmpty()) {
                    utils.openTextareaWindow(dataTable.getScene().getWindow(), cell.getItem(), newValue -> {
                        Product item = dataTable.getItems().get(cell.getIndex());
                        item.setSpecification(newValue);
                        productDAO.updateData("specification", newValue, item.getId());
                        dataTable.refresh();
                    }, "Cập nhật qui cách");
                }
            });

            return cell ;
        });

        noteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));
        noteColumn.setCellFactory(tc -> {

            TableCell<Product, String> cell = new TableCell<>();

            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(noteColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());

//            EDIT IN NEW WINDOW
            cell.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && ! cell.isEmpty()) {
                    utils.openTextareaWindow(dataTable.getScene().getWindow(), cell.getItem(), newValue -> {
                        Product item = dataTable.getItems().get(cell.getIndex());
                        item.setNote(newValue);
                        productDAO.updateData("note", newValue, item.getId());
                        dataTable.refresh();
                    }, "Cập nhật ghi chú");
                }
            });

            return cell ;
        });

        dataTable.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ESCAPE || event.getCode() == KeyCode.F5) {
                dataTable.refresh();
            }
        });

        dataTable.setRowFactory((TableView<Product> tableView) -> {
            final TableRow<Product> row = new TableRow<>();

            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem viewItem = new MenuItem("Chi tiết");
            final MenuItem editItem = new MenuItem("Cập nhật");
            final MenuItem managePackaging = new MenuItem("List bao bì");
            final MenuItem removeItem = new MenuItem("Xóa dòng");

            viewItem.setOnAction((ActionEvent event) -> {
                Product product = dataTable.getSelectionModel().getSelectedItem();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Chi tiết mặt hàng");
                alert.setHeaderText(product.getName());
                alert.setContentText(
                        "Tên mặt hàng: " + product.getName() + "\n" +
                        "Mô tả: " + product.getDescription() + "\n" +
                        "Qui cách: " + product.getSpecification() + "\n" +
                        "Ghi chú: " + product.getNote() + "\n"
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
                Product product = dataTable.getSelectionModel().getSelectedItem();
                getData(product);
            });
            contextMenu.getItems().add(editItem);

            managePackaging.setOnAction((ActionEvent event) -> {
                Product product = dataTable.getSelectionModel().getSelectedItem();
                loadSingleProductScene((Stage) managePackaging.getParentPopup().getOwnerWindow(),"/fxml/product_packaging.fxml", "Chi tiết mặt hàng: "+product.getDescription(), product, 800, 640);
            });
            contextMenu.getItems().add(managePackaging);

            removeItem.setOnAction((ActionEvent event) -> {
                Product product = dataTable.getSelectionModel().getSelectedItem();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Thông báo!");
                alert.setHeaderText("Xóa: "+product.getName());
                alert.setContentText("Lưu ý: Dữ liệu sẽ không thể khôi phục lại sau khi xóa, bạn có chắc muốn tiếp tục?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    productDAO.delete(product.getId());
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
