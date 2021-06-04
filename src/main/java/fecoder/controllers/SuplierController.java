package fecoder.controllers;

import fecoder.DAO.SuplierDAO;
import fecoder.models.Suplier;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import javafx.scene.control.TableColumn;
import javafx.scene.input.PickResult;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class SuplierController implements Initializable {

    public TableView<Suplier> suplierTable;
    public TableColumn<Suplier, Integer> idColumn;
    public TableColumn<Suplier, String> codeColumn;
    public TableColumn<Suplier, String> nameColumn;
    public TableColumn<Suplier, String> addressColumn;
    public TableColumn<Suplier, String> emailColumn;
    public TableColumn<Suplier, String> deputyColumn;
    public TableColumn<Suplier, String> phoneColumn;
    public TableColumn<Suplier, String> faxColumn;
    public TextField codeField;
    public TextField nameField;
    public TextField addressField;
    public TextField emailField;
    public TextField deputyField;
    public TextField phoneField;
    public TextField faxField;
    public TextField searchField;


    private final SuplierDAO suplierDAO = new SuplierDAO();
    public Button insertButton;
    public Button updateButton;
    public Button reloadData;
    public Label anchorLabel;
    public Label anchorData;
    public Button clearButton;
    public ComboBox<String> comboBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadView();
    }

    @FXML
    private void reloadData() {
        reload();
    }

    @FXML
    private void clearButton() {
        clearFields();
    }

    private void reload() {
        loadView();
    }

    @FXML
    private void updateButton() {
        suplierDAO.update(
                nameField.getText(),
                addressField.getText(),
                emailField.getText(),
                deputyField.getText(),
                phoneField.getText(),
                faxField.getText(),
                codeField.getText().toUpperCase(),
                Integer.parseInt(anchorData.getText())
        );
        clearFields();
        reload();
    }

    @FXML
    private void insertButton() {
        suplierDAO.insert(
                nameField.getText(),
                addressField.getText(),
                emailField.getText(),
                deputyField.getText(),
                phoneField.getText(),
                faxField.getText(),
                codeField.getText().toUpperCase()
        );
        clearFields();
        reload();
    }

    private void getSuplier(String name, String address, String email, String deputy, String phone, String fax, String code, int id) {
        nameField.setText(name);
        addressField.setText(address);
        emailField.setText(email);
        deputyField.setText(deputy);
        phoneField.setText(phone);
        faxField.setText(fax);
        codeField.setText(code);
        anchorLabel.setText("Current ID: ");
        anchorData.setText(""+id);
    }

    private void clearFields() {
        nameField.setText(null);
        addressField.setText(null);
        emailField.setText(null);
        deputyField.setText(null);
        phoneField.setText(null);
        faxField.setText(null);
        codeField.setText(null);
        anchorLabel.setText(null);
        anchorData.setText(null);
    }

    private void showEditingWindow(Window owner, String currentValue, Consumer<String> commitHandler) {
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
        stage.setTitle("Cập nhật giá trị");
        stage.setResizable(false);
        stage.show();
    }

    public void loadView() {
        ObservableList<Suplier> list = FXCollections.observableArrayList(suplierDAO.getSupliers());
        FilteredList<Suplier> filteredList = new FilteredList<>(list, p -> true);

        searchField.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    switch (comboBox.getValue()) {
                        case "Mã":
                            filteredList.setPredicate(str -> {
                                if (newValue == null || newValue.isEmpty())
                                    return true;
                                String lowerCaseFilter = newValue.toLowerCase();
                                return str.getCode().toLowerCase().contains
                                        (lowerCaseFilter);
                            });
                            break;
                        case "Tên":
                            filteredList.setPredicate(str -> {
                                if (newValue == null || newValue.isEmpty())
                                    return true;
                                String lowerCaseFilter = newValue.toLowerCase();
                                return str.getName().toLowerCase().contains
                                        (lowerCaseFilter);
                            });
                            break;
                        case "Địa chỉ":
                            filteredList.setPredicate(str -> {
                                if (newValue == null || newValue.isEmpty())
                                    return true;
                                String lowerCaseFilter = newValue.toLowerCase();
                                return str.getAddress().toLowerCase().contains
                                        (lowerCaseFilter);
                            });
                            break;
                    }
                });

        comboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal != null) {
                searchField.setText(null);
            }
        });

        SortedList<Suplier> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(suplierTable.comparatorProperty());

        suplierTable.setEditable(true);

        TableView.TableViewSelectionModel<Suplier> selectionModel = suplierTable.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        ObservableList<Suplier> getSelectedItems = selectionModel.getSelectedItems();

        getSelectedItems.addListener(new ListChangeListener<Suplier>() {
            @Override
            public void onChanged(Change<? extends Suplier> change) {
//                System.out.println("Selection changed: " + change.getList());
            }
        });

//        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setSortable(false);
        idColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Integer>(suplierTable.getItems().indexOf(column.getValue())+1));

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
//        nameColumn.setCellFactory(TextFieldTableCell.<Suplier>forTableColumn());
//        nameColumn.setOnEditCommit(event -> {
//            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
//            ((Suplier) event.getTableView().getItems().get(event.getTablePosition().getRow())).setName(data);
//            suplierDAO.updateData("name", data, event.getRowValue().getId());
//            suplierTable.refresh();
//        });
        nameColumn.setCellFactory(tc -> {

            TableCell<Suplier, String> cell = new TableCell<>();

            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(nameColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());

//            EDIT IN NEW WINDOW
            cell.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && ! cell.isEmpty()) {
                    showEditingWindow(suplierTable.getScene().getWindow(), cell.getItem(), newValue -> {
                        Suplier item = suplierTable.getItems().get(cell.getIndex());
                        item.setName(newValue);
                        suplierDAO.updateData("name", newValue, item.getId());
                        suplierTable.refresh();
                    });
                }
            });

            return cell ;
        });

        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        addressColumn.setCellFactory(TextFieldTableCell.<Suplier>forTableColumn());
        addressColumn.setCellFactory(tc -> {

            TableCell<Suplier, String> cell = new TableCell<>();

            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(nameColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());

//            EDIT IN NEW WINDOW
            cell.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && ! cell.isEmpty()) {
                    showEditingWindow(suplierTable.getScene().getWindow(), cell.getItem(), newValue -> {
                        Suplier item = suplierTable.getItems().get(cell.getIndex());
                        item.setName(newValue);
                        suplierDAO.updateData("address", newValue, item.getId());
                        suplierTable.refresh();
                    });
                }
            });

            return cell ;
        });

        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailColumn.setCellFactory(TextFieldTableCell.<Suplier>forTableColumn());
        emailColumn.setCellFactory(tc -> {

            TableCell<Suplier, String> cell = new TableCell<>();

            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(nameColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());

//            EDIT IN NEW WINDOW
            cell.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && ! cell.isEmpty()) {
                    showEditingWindow(suplierTable.getScene().getWindow(), cell.getItem(), newValue -> {
                        Suplier item = suplierTable.getItems().get(cell.getIndex());
                        item.setName(newValue);
                        suplierDAO.updateData("email", newValue, item.getId());
                        suplierTable.refresh();
                    });
                }
            });

            return cell ;
        });

        deputyColumn.setCellValueFactory(new PropertyValueFactory<>("deputy"));
        deputyColumn.setCellFactory(TextFieldTableCell.<Suplier>forTableColumn());
        deputyColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((Suplier) event.getTableView().getItems().get(event.getTablePosition().getRow())).setDeputy(data);
            suplierDAO.updateData("deputy", data, event.getRowValue().getId());
            suplierTable.refresh();
        });

        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneColumn.setCellFactory(TextFieldTableCell.<Suplier>forTableColumn());
        phoneColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((Suplier) event.getTableView().getItems().get(event.getTablePosition().getRow())).setPhone(data);
            suplierDAO.updateData("phone", data, event.getRowValue().getId());
            suplierTable.refresh();
        });

        faxColumn.setCellValueFactory(new PropertyValueFactory<>("fax"));
        faxColumn.setCellFactory(TextFieldTableCell.<Suplier>forTableColumn());
        faxColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((Suplier) event.getTableView().getItems().get(event.getTablePosition().getRow())).setFax(data);
            suplierDAO.updateData("fax", data, event.getRowValue().getId());
            suplierTable.refresh();
        });

        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        codeColumn.setCellFactory(TextFieldTableCell.<Suplier>forTableColumn());
        codeColumn.setOnEditCommit(event -> {
            final String data = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((Suplier) event.getTableView().getItems().get(event.getTablePosition().getRow())).setCode(data);
            suplierDAO.updateData("code", data, event.getRowValue().getId());
            suplierTable.refresh();
        });

        suplierTable.setRowFactory((TableView<Suplier> tableView) -> {
            final TableRow<Suplier> row = new TableRow<>();

            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem viewItem = new MenuItem("Chi tiết");
            final MenuItem editItem = new MenuItem("Cập nhật");
            final MenuItem removeItem = new MenuItem("Xóa dòng");

            viewItem.setOnAction((ActionEvent event) -> {
                Suplier suplier = suplierTable.getSelectionModel().getSelectedItem();
                int rowIndex = suplierTable.getSelectionModel().getSelectedIndex();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Chi tiết Nhà Cung Cấp");
                alert.setHeaderText(suplier.getName());
                alert.setContentText(
                        "Mã NCC: " + suplier.getCode() + "\n" +
                        "Tên NCC: " + suplier.getName() + "\n" +
                        "Địa chỉ: " + suplier.getAddress() + "\n" +
                        "Email: " + suplier.getEmail() + "\n" +
                        "Đại diện: " + suplier.getDeputy() + "\n" +
                        "Điện thoại: " + suplier.getPhone() + "\n" +
                        "Fax: " + suplier.getFax() + "\n"
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
                Suplier suplier = suplierTable.getSelectionModel().getSelectedItem();
                getSuplier(suplier.getName(), suplier.getAddress(), suplier.getEmail(), suplier.getDeputy(), suplier.getPhone(), suplier.getFax(), suplier.getCode(), suplier.getId());
            });
            contextMenu.getItems().add(editItem);

            removeItem.setOnAction((ActionEvent event) -> {
                Suplier suplier = suplierTable.getSelectionModel().getSelectedItem();
                suplierDAO.delete(suplier.getId());
                reload();
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

//        suplierTable.setItems(list);
        suplierTable.setItems(sortedList);
    }
}
