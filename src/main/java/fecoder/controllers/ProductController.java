package fecoder.controllers;

import fecoder.DAO.ProductDAO;
import fecoder.models.Product;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

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
    public TableView<Product> dataTable;
    public TableColumn<Product, Integer> idColumn;
    public TableColumn<Product, String> nameColumn;
    public TableColumn<Product, String> descriptionColumn;
    public TableColumn<Product, String> specificationColumn;
    public TableColumn<Product, String> noteColumn;
    public Label anchorLabel;
    public Label anchorData;

    private final ProductDAO productDAO = new ProductDAO();

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
        descriptionField.setText(null);
        specificationField.setText(null);
        noteField.setText(null);
        anchorLabel.setText(null);
        anchorData.setText(null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadView();
    }

    private void getData(Product p) {
        nameField.setText(p.getName());
        descriptionField.setText(p.getDescription());
        specificationField.setText(p.getSpecification());
        noteField.setText(p.getNote());
        anchorLabel.setText("Current ID: ");
        anchorData.setText(""+p.getId());
    }

    private void showEditingWindow(Window owner, String currentValue, Consumer<String> commitHandler, String title) {
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);

        TextArea textArea = new TextArea(currentValue);

        Button okButton = new Button("ÁP DỤNG");
        okButton.setDefaultButton(true);
        okButton.setOnAction(e -> {
            commitHandler.accept(textArea.getText());
            stage.hide();
        });

        Button cancelButton = new Button("HỦY BỎ");
        cancelButton.setCancelButton(true);
        cancelButton.setOnAction(e -> stage.hide());

        HBox buttons = new HBox(5, okButton, cancelButton);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(5));

        BorderPane root = new BorderPane(textArea, null, null, buttons, null);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.setResizable(false);
        stage.show();
    }

    private void loadView() {
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

        searchComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal != null) {
                searchField.setText(null);
            }
        });

        SortedList<Product> productSortedList = new SortedList<>(productFilteredList);
        productSortedList.comparatorProperty().bind(dataTable.comparatorProperty());

        dataTable.setEditable(true);

        TableView.TableViewSelectionModel<Product> productTableViewSelectionModel = dataTable.getSelectionModel();
        productTableViewSelectionModel.setSelectionMode(SelectionMode.SINGLE);

        ObservableList<Product> productObservableList1 = productTableViewSelectionModel.getSelectedItems();

        productObservableList1.addListener(new ListChangeListener<Product>() {
            @Override
            public void onChanged(Change<? extends Product> change) {
//                System.out.println("Selection changed: " + change.getList());
            }
        });

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
                    showEditingWindow(dataTable.getScene().getWindow(), cell.getItem(), newValue -> {
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
                    showEditingWindow(dataTable.getScene().getWindow(), cell.getItem(), newValue -> {
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
                    showEditingWindow(dataTable.getScene().getWindow(), cell.getItem(), newValue -> {
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
                    showEditingWindow(dataTable.getScene().getWindow(), cell.getItem(), newValue -> {
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

        dataTable.setItems(productSortedList);
    }
}
