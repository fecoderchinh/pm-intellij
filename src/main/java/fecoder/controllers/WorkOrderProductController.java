package fecoder.controllers;

import fecoder.DAO.*;
import fecoder.models.*;
import fecoder.utils.ExportWordDocument;
import fecoder.utils.TreeTableUtil;
import fecoder.utils.UpdateDocument;
import fecoder.utils.Utils;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.util.Units;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class WorkOrderProductController implements Initializable {
    public Label mainLabel;
    public TextField ordinalNumberField;
    public ComboBox<Product> productComboBox;
    public TextField qtyField;
    public TextField noteField;
    public Button insertButton;
    public Button updateButton;
    public Button clearButton;
    public ComboBox<String> searchComboBox;
    public TextField searchField;
    public Button reloadData;
    public Button exportData;

    public TableView<WorkOrderProductString> productTableView;
    public TableColumn<WorkOrderProductString, String> productIdColumn;
    public TableColumn<WorkOrderProductString, String> productNameColumn;

    public TreeTableView<WorkProduction> dataTable;
    
    public Label anchorLabel;
    public Label anchorData;

    private int currentRow;
    private String currentCell;
    private final Utils utils = new Utils();

    private boolean isEditableComboBox = false;
    private boolean isUpdating = false;

    ProductDAO productDAO = new ProductDAO();
    WorkOrderProductPackagingDAO workOrderProductPackagingDAO = new WorkOrderProductPackagingDAO();
    WorkProductionDAO workProductionDAO = new WorkProductionDAO();
    WorkOrderDAO workOrderDAO = new WorkOrderDAO();
    WorkOrderProductDAO workOrderProductDAO = new WorkOrderProductDAO();
    WorkOrderProductStringDAO workOrderProductStringDAO = new WorkOrderProductStringDAO();
    OrderBySupplierDAO orderBySupplierDAO = new OrderBySupplierDAO();
    SupplierDAO supplierDAO = new SupplierDAO();

    private final ObservableList<Product> productObservableList = FXCollections.observableArrayList(productDAO.getList());

    private WorkOrder innerData;

    public void insertButton(ActionEvent actionEvent) {
        Product productInsertData = productDAO.getDataByName(productComboBox.getValue()+"");
        int fakeAutoIncrementID = workOrderProductDAO.getFakeAutoIncrementID().getId();
//        System.out.println(fakeAutoIncrementID);
        if(emptyFieldDetected()) {
            utils.alert("err", Alert.AlertType.ERROR, "Xảy ra lỗi!", "Không được bỏ trống các trường bắt buộc (*)").showAndWait();
        } else {
            try {
                workOrderProductDAO.insert_wopp_children(fakeAutoIncrementID+1, this.innerData.getId(), ordinalNumberField.getText()+"", productInsertData.getId(), Float.parseFloat(qtyField.getText()), noteField.getText()+"");
                clearFields();
                reload();
                utils.alert("info", Alert.AlertType.INFORMATION, null, null).showAndWait();
            } catch (NumberFormatException e) {
                utils.alert("err", Alert.AlertType.ERROR, null, null).showAndWait();
            }
        }
    }

    public void updateButton(ActionEvent actionEvent) {
        String productComboBoxValue = utils.getComboBoxValue(productComboBox);
        Product product = productDAO.getDataByName(productComboBoxValue);
        WorkOrderProductPackaging workOrderProductPackaging = workOrderProductPackagingDAO.getDataByID(productTableView.getSelectionModel().getSelectedItem().getId());
        if(emptyFieldDetected()) {
            utils.alert("err", Alert.AlertType.ERROR, "Xảy ra lỗi!", "Không được bỏ trống các trường bắt buộc (*)").showAndWait();
        } else {
//            System.out.println(this.innerData.getId());
//            System.out.println(ordinalNumberField.getText()+"");
//            System.out.println(product.getId());
//            System.out.println(Float.parseFloat(qtyField.getText()));
//            System.out.println(workOrderProductPackaging.getWop_id());
            try {
                workOrderProductDAO.update(
                        this.innerData.getId(), // work_order_product.work_order_id
                        ordinalNumberField.getText()+"", // work_order_product.ordinal_num
                        product.getId(), // work_order_product.product_id
                        Float.parseFloat(qtyField.getText()), // work_order_product.qty
                        noteField.getText()+"", // work_order_product.note
                        Integer.parseInt(anchorData.getText()) // work_order_product.id
                );
                workOrderProductPackagingDAO.updateWOPPChildren(
                        product.getId(),
                        Float.parseFloat(qtyField.getText()),
                        Integer.parseInt(anchorData.getText())
                );
                clearFields();
                reload();
                utils.alert("info", Alert.AlertType.INFORMATION, null, null).showAndWait();
            } catch (NumberFormatException e) {
                utils.alert("err", Alert.AlertType.ERROR, null, null).showAndWait();
            }
        }
    }

    public void clearButton(ActionEvent actionEvent) {
        clearFields();
    }

    public void reloadData(ActionEvent actionEvent) {
        reload();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        filterComboBox();
        loadView();
    }

    /**
     * Handle validation on submitting
     * */
    private boolean emptyFieldDetected() {
        if(qtyField.getText().isEmpty()) {
            return true;
        }
        else if(ordinalNumberField.getText().isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Handle on filter Combobox
     * */
    private void filterComboBox() {
        productComboBox.getItems().addAll(productObservableList);
        filterProductComboBox();
    }

    /**
     * Handle on filter Product Combobox
     * */
    private void filterProductComboBox() {
        // Create the listener to filter the list as user enters search terms
        FilteredList<Product> dataFilteredList = new FilteredList<>(productObservableList, p-> true);

        // Add listener to our ComboBox textfield to filter the list
        productComboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            dataFilteredList.setPredicate(item -> {

                isUpdating = false;

                // Check if the search term is contained anywhere in our list
                if (item.getName().toLowerCase().contains(newValue.toLowerCase().trim()) && !isUpdating) {
                    productComboBox.show();
                    return true;
                }

                // No matches found
                return false;
            });

        });

        productComboBox.getItems().removeAll(productObservableList);
        productComboBox.getItems().addAll(dataFilteredList);
    }

    /**
     * Reset fields
     * This will handle an enter event and mouse leave for these Nodes
     * */
    private void resetFields() {
        utils.disableKeyEnterOnTextFieldComboBox(productComboBox, true);

        utils.disableKeyEnterOnTextField(qtyField);

        utils.inputNumberOnly(qtyField);
    }

    /**
     * Handle on resetting ComboBox
     * */
    private void resetComboBox() {
        Product productData = productDAO.getDataByID(1);
        utils.setComboBoxValue(productComboBox, productData.getName());
    }

    /**
     * Handle event on clearing all inputs
     * */
    private void clearFields() {
        resetComboBox();
        anchorLabel.setText("");
        anchorData.setText("");

        qtyField.setText("");

        searchField.setText("");

        setData(this.innerData);
    }

    /**
     * Reloading method
     * */
    private void reload() {
        utils.reloadTreeTableViewOnChange(dataTable, currentRow, currentCell);
        utils.reloadTableViewOnChange(productTableView, currentRow, currentCell);
        clearFields();
        loadView();
    }

    /**
     * Hiding the Combobox while on updating stage.
     * */
    private void hideComboBoxForUpdatingData() {
        if(isUpdating) {
            productComboBox.hide();
        }
    }

    /**
     * Setting context menu for table row
     * */
    private void setContextMenuProductTable() {
        productTableView.setRowFactory((TableView<WorkOrderProductString> tableView) -> {
            final TableRow<WorkOrderProductString> row = new TableRow<>();

            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem viewItem = new MenuItem("Chi tiết");
            final MenuItem editItem = new MenuItem("Cập nhật");
            final MenuItem removeItem = new MenuItem("Xóa dòng");

            viewItem.setOnAction((ActionEvent event) -> {
                WorkOrderProductString workOrderProductString = productTableView.getSelectionModel().getSelectedItem();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Chi tiết mặt hàng");
                alert.setHeaderText(workOrderProductString.getProductName());
                alert.setContentText(
                        "STT: " + workOrderProductString.getProductOrdinalNumber() + "\n" +
                                "Mặt hàng: " + workOrderProductString.getProductName() + "\n" +
                                "Số lượng: " + workOrderProductString.getProductQuantity() + "\n"
                );

                alert.showAndWait();
            });
            contextMenu.getItems().add(viewItem);

            editItem.setOnAction((ActionEvent event) -> {
                WorkOrderProductString workOrderProductString = productTableView.getSelectionModel().getSelectedItem();
                WorkOrderProduct workOrderProduct = workOrderProductDAO.getDataByID(workOrderProductString.getId());
                getData(workOrderProduct);
                hideComboBoxForUpdatingData();
            });
            contextMenu.getItems().add(editItem);

            removeItem.setOnAction((ActionEvent event) -> {
                WorkOrderProductString workOrderProductString = productTableView.getSelectionModel().getSelectedItem();
                Alert alert = utils.alert("info", Alert.AlertType.CONFIRMATION, "Xóa mặt hàng: "+workOrderProductString.getProductName(), null);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    workOrderProductDAO.delete(workOrderProductString.getId());
                    workOrderProductPackagingDAO.deleteWOPPChildren(workOrderProductString.getId());
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
     * @param data - the packaging data
     * */
    private void getData(WorkOrderProduct data) {
        if(!isEditableComboBox) {
            Product product = productDAO.getDataByID(data.getProduct_id());
            productComboBox.getSelectionModel().select(product);
        } else {
            Product product = productDAO.getDataByID(data.getProduct_id());
            productComboBox.getEditor().setText(product.getName());
        }

        ordinalNumberField.setText(data.getOrdinal_num()+"");

        qtyField.setText(data.getQty()+"");
        anchorLabel.setText("Current ID: ");
        anchorData.setText(""+data.getId());
        this.isUpdating = true;
    }

    /**
     * Loading Product Table
     *
     * @param workOrderID name of work_order
     * */
    private void loadViewProduct(int workOrderID) {
        ObservableList<WorkOrderProductString> workProductionObservableListByName = FXCollections.observableArrayList(workOrderProductStringDAO.getListByWorkOrderID(workOrderID));

        productTableView.setEditable(true);
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productOrdinalNumber"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productTableView.setItems(workProductionObservableListByName);

        setContextMenuProductTable();

        productTableView.setOnMouseClicked((MouseEvent event) -> {
            if(event.getButton().equals(MouseButton.PRIMARY) && productTableView.getSelectionModel().getSelectedItem() != null){
                mainLabel.setText(this.innerData.getName() + " - ("+ productTableView.getSelectionModel().getSelectedItem().getProductOrdinalNumber() +") - "+productTableView.getSelectionModel().getSelectedItem().getProductName());
            }
        });
    }

    /**
     * Loading Work Order TreeTableView
     *
     * @param workOrderID work_order.id
     * */
    private void loadViewWOPP(int workOrderID) {
        TreeItem<WorkProduction> root = TreeTableUtil.getModel(workOrderID);
        root.setExpanded(true);
        dataTable.setTableMenuButtonVisible(false);

        dataTable.setRoot(root);
        dataTable.setEditable(true);

        dataTable.getColumns().clear();

        dataTable.getColumns().add(TreeTableUtil.getIdColumn());
        dataTable.getColumns().add(TreeTableUtil.getOrdinalNumberColumn());
//        dataTable.getColumns().add(TreeTableUtil.getWOIDColumn());
//        dataTable.getColumns().add(TreeTableUtil.getWorkOrderNameColumn());
//        dataTable.getColumns().add(TreeTableUtil.getProductNameColumn(dataTable));
        dataTable.getColumns().add(TreeTableUtil.getPackagingNameColumn(dataTable));
//        dataTable.getColumns().add(TreeTableUtil.getPackagingSpecificationColumn(dataTable));
        dataTable.getColumns().add(TreeTableUtil.getPackagingDimensionColumn(dataTable));
        dataTable.getColumns().add(TreeTableUtil.getPackagingSuplierColumn());
        dataTable.getColumns().add(TreeTableUtil.getPackagingCodeColumn());
        dataTable.getColumns().add(TreeTableUtil.getWorkOrderQuantityColumn());
        dataTable.getColumns().add(TreeTableUtil.getStockColumn(dataTable));
        dataTable.getColumns().add(TreeTableUtil.getActualQuantityColumn(dataTable));
        dataTable.getColumns().add(TreeTableUtil.getResidualQuantityColumn(dataTable));
//        dataTable.getColumns().add(TreeTableUtil.getTotalResidualQuantityColumn(dataTable));
        dataTable.getColumns().add(TreeTableUtil.getPrintStatusColumn());

//        setContextMenu();

        final PseudoClass topNode = PseudoClass.getPseudoClass("top-node");
        dataTable.setRowFactory(t -> {
            final TreeTableRow<WorkProduction> row = new TreeTableRow<>();

            // every time the TreeItem changes, check, if the new item is a
            // child of the root and set the pseudoclass accordingly
            row.treeItemProperty().addListener((o, oldValue, newValue) -> {
                boolean tn = false;
                if (newValue != null) {
                    tn = newValue.getParent() == root;
                }
                row.pseudoClassStateChanged(topNode, tn);
            });
            return row;
        });

        utils.reloadTreeTableViewOnChange(dataTable, currentRow, currentCell);

//        setSearchField();
    }

    /**
     * Handle on searching data
     * */
    public void setSearchField() {
        ObservableList<WorkProduction> list = FXCollections.observableArrayList(workProductionDAO.getListByID(this.innerData.getId()));
        FilteredList<WorkProduction> filteredList = new FilteredList<>(list, p -> true);

        searchField.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    switch (searchComboBox.getValue()) {
                        case "Tên bao bì":
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
//        sortedList.comparatorProperty().bind(dataTable.comparatorProperty());
//
//        dataTable.setItems(sortedList);
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

//        dataTable.setEditable(true);

//        ObservableList<WorkProduction> workOrderList = FXCollections.observableArrayList(workProductionDAO.getWorkOrderList(workOrderDAO.getDataByName(mainLabel.getText()).getId()));
//        for (WorkProduction _workOrder : workOrderList) {
//            System.out.println(_workOrder.getWorkOrderName());
//            if(_workOrder.getWorkOrderName() != null) {
//                ObservableList<WorkProduction> productList = FXCollections.observableArrayList(workProductionDAO.getProductList(workOrderDAO.getDataByName(_workOrder.getWorkOrderName()).getId()));
//                for (WorkProduction _product : productList) {
//                    System.out.println(_product.getProductName());
//                    ObservableList<WorkProduction> packagingList = FXCollections.observableArrayList(workProductionDAO.getPackagingList(productDAO.getDataByName(_product.getProductName()).getId()));
//                    for (WorkProduction _packaging : packagingList) {
//                        System.out.println(_packaging.getPackagingName() + " | " + _packaging.getPackagingSuplier());
//                    }
//                }
//            }
//        }
    }

    public void setData(WorkOrder workOrder) {
        this.innerData = workOrder;
        mainLabel.setText(this.innerData.getName());
        loadViewProduct(this.innerData.getId());
        loadViewWOPP(this.innerData.getId());
    }

    public void exportData(ActionEvent actionEvent) throws IOException {
//        data2DocOfOrderList((Stage)((Node) actionEvent.getSource()).getScene().getWindow());

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Word document (*.docx)", "*.docx");
        fileChooser.getExtensionFilters().add(extensionFilter);

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select folder");

//            File file = fileChooser.showSaveDialog(window);
        File file = directoryChooser.showDialog((Stage)((Node) actionEvent.getSource()).getScene().getWindow());


        ObservableList<OrderBySupllier> orderBySuplliers = FXCollections.observableArrayList(orderBySupplierDAO.getList(this.innerData.getId()+""));

        for (OrderBySupllier orderBySupllier : orderBySuplliers) {
            data2DocOfOrderBySupplier(file, orderBySupllier.getsCode());
        }

        WorkOrder workOrder = workOrderDAO.getDataByID(this.innerData.getId());
        ExportWordDocument.data2DocOfOrderListDraft(file, workOrder);

        data2Doc(file);

    }

    private void helloWord() {
        String fileName = "e:\\java_platform\\helloWord.docx";

        try {

            XWPFDocument doc = new XWPFDocument();

            // create a paragraph
            XWPFParagraph p1 = doc.createParagraph();
            p1.setAlignment(ParagraphAlignment.CENTER);

            // set font
            XWPFRun r1 = p1.createRun();
            r1.setBold(true);
            r1.setItalic(true);
            r1.setFontSize(22);
            r1.setFontFamily("New Roman");
            r1.setText("I am first paragraph.");

            // save it to .docx file
            try (FileOutputStream out = new FileOutputStream(fileName)) {
                doc.write(out);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void docStyle() {
        String fileName = "e:\\java_platform\\docStyle.docx";

        try {

            XWPFDocument doc = new XWPFDocument();

            XWPFParagraph p1 = doc.createParagraph();
            p1.setAlignment(ParagraphAlignment.CENTER);

            // Set Text to Bold and font size to 22 for first paragraph
            XWPFRun r1 = p1.createRun();
            r1.setBold(true);
            r1.setItalic(true);
            r1.setFontSize(22);
            r1.setText("I am first paragraph. My Text is bold, italic, Courier and capitalized");
            r1.setFontFamily("Courier");

            XWPFParagraph p2 = doc.createParagraph();
            //Set color for second paragraph
            XWPFRun r2 = p2.createRun();
            r2.setText("I am second paragraph. My Text is Red in color and is embossed");
            r2.setColor("ff0000");
            r2.setEmbossed(true);

            XWPFParagraph p3 = doc.createParagraph();
            //Set strike for third paragraph and capitalization
            XWPFRun r3 = p3.createRun();
            r3.setStrikeThrough(true);
            r3.setCapitalized(true);
            r3.setText("I am third paragraph. My Text is strike through and is capitalized");

            XWPFParagraph p4 = doc.createParagraph();
            p4.setWordWrapped(true);
            p4.setPageBreak(true);  // new page break
            p4.setIndentationFirstLine(600);

            XWPFRun r4 = p4.createRun();
            r4.setFontSize(40);
            r4.setItalic(true);
            //r4.setTextPosition(100);
            r4.setText("Line 1");
            r4.addBreak();
            r4.setText("Line 2");
            r4.addBreak();
            r4.setText("Line 3");

            // save it to .docx file
            try (FileOutputStream out = new FileOutputStream(fileName)) {
                doc.write(out);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void docHeader() {
        String fileName = "e:\\java_platform\\docHeader.docx";

        try {

            XWPFDocument doc = new XWPFDocument();

            XWPFParagraph p = doc.createParagraph();
            XWPFRun r = p.createRun();
            r.setBold(true);
            r.setFontSize(30);
            r.setText("Create document header and footer!");

            // next page
            XWPFParagraph p2 = doc.createParagraph();
            p2.setWordWrapped(true);
            p2.setPageBreak(true);  // new page break

            XWPFRun r2 = p2.createRun();
            r2.setFontSize(40);
            r2.setItalic(true);
            r2.setText("New Page");

            // document header and footer
            XWPFHeader head = doc.createHeader(HeaderFooterType.DEFAULT);
            head.createParagraph()
                    .createRun()
                    .setText("This is document header");

            XWPFFooter foot = doc.createFooter(HeaderFooterType.DEFAULT);
            foot.createParagraph()
                    .createRun()
                    .setText("This is document footer");

            // save it to .docx file
            try (FileOutputStream out = new FileOutputStream(fileName)) {
                doc.write(out);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void docImage() {
        String imgFile = "e:\\java_platform\\google.png";
        String fileName = "e:\\java_platform\\docImage.docx";

        try {

            XWPFDocument doc = new XWPFDocument();

            XWPFParagraph p = doc.createParagraph();
            XWPFRun r = p.createRun();
            r.setText(imgFile);
            r.addBreak();

            // add png image
            try (FileInputStream is = new FileInputStream(imgFile)) {
                r.addPicture(is,
                        Document.PICTURE_TYPE_PNG,    // png file
                        imgFile,
                        Units.toEMU(400),
                        Units.toEMU(200));            // 400x200 pixels
            }

            // save it to .docx file
            try (FileOutputStream out = new FileOutputStream(fileName)) {
                doc.write(out);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void docTable() {
        String imgFile = "e:\\java_platform\\google.png";
        String fileName = "e:\\java_platform\\docTable.docx";

        try {

            XWPFDocument doc = new XWPFDocument();

            XWPFTable table = doc.createTable();

            //Creating first Row
            XWPFTableRow row1 = table.getRow(0);
            row1.getCell(0).setText("First Row, First Column");
            row1.addNewTableCell().setText("First Row, Second Column");
            row1.addNewTableCell().setText("First Row, Third Column");

            //Creating second Row
            XWPFTableRow row2 = table.createRow();
            row2.getCell(0).setText("Second Row, First Column");
            row2.getCell(1).setText("Second Row, Second Column");
            row2.getCell(2).setText("Second Row, Third Column");

            //create third row
            XWPFTableRow row3 = table.createRow();
            row3.getCell(0).setText("Third Row, First Column");
            row3.getCell(1).setText("Third Row, Second Column");
            row3.getCell(2).setText("Third Row, Third Column");

            // save it to .docx file
            try (FileOutputStream out = new FileOutputStream(fileName)) {
                doc.write(out);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void docRead() {
        String fileName = "e:\\java_platform\\test.docx";

        try (XWPFDocument doc = new XWPFDocument(
                Files.newInputStream(Paths.get(fileName)))) {

            XWPFWordExtractor xwpfWordExtractor = new XWPFWordExtractor(doc);
            String docText = xwpfWordExtractor.getText();
            System.out.println(docText);

            // find number of words in the document
            long count = Arrays.stream(docText.split("\\s+")).count();
            System.out.println("Total words: " + count);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void docUpdate() throws IOException {
        UpdateDocument obj = new UpdateDocument();

        obj.updateDocument(
                "e:\\java_platform\\test_input.docx",
                "e:\\java_platform\\test_output.docx",
                new String[]{"chinh", "123", "aaa"});
    }

    private void data2Doc(File file) {
        String imgFile = "e:\\java_platform\\docs-data\\logo.jpg";
        String _fontFamily = "Arial";

        OrderBySupplierDAO orderBySupplierDAO = new OrderBySupplierDAO();
        ObservableList<OrderBySupllier> orderObservableList = FXCollections.observableArrayList(orderBySupplierDAO.getList(this.innerData.getId()+""));

        try {
            XWPFDocument doc = new XWPFDocument();
            XWPFParagraph paragraph;
            XWPFRun run;
            XWPFTable table;
            XWPFTableRow row;

            /*
            * Word Header
            * */
            XWPFHeader header = doc.createHeader(HeaderFooterType.DEFAULT);

            table = header.createTable(1, 3);
            table.setWidth("100%");
            table.removeBorders();
            row = table.getRow(0);
            row.getCell(0).setWidth("25%");
            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(1).setWidth("55%");
            row.getCell(1).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);
            row.getCell(2).setWidth("20%");
            row.getCell(1).getParagraphs().get(0).setAlignment(ParagraphAlignment.RIGHT);

            paragraph = row.getCell(0).getParagraphArray(0);
            paragraph.setVerticalAlignment(TextAlignment.CENTER);
            run = paragraph.createRun();
            // add png image
            try (FileInputStream is = new FileInputStream(imgFile)) {
                run.addPicture(is,
                        Document.PICTURE_TYPE_PNG,    // png file
                        imgFile,
                        Units.toEMU(100),
                        Units.toEMU(45));            // 100x35 pixels
            }
            utils.setHeaderRowforSingleCell(row.getCell(1), "Công ty CP SEAVINA", 10, true, true, ParagraphAlignment.CENTER);
//            utils.setHeaderRowforSingleCell(row.getCell(1), "Lô 16A-18, KCN Trà Nóc I,P.Trà Nóc, Q. Bình Thủy, TP. Cần Thơ ,Việt Nam", 10, true, true, ParagraphAlignment.CENTER);
//            utils.setHeaderRowforSingleCell(row.getCell(1), "1801141886", 10, true, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(1), "Tel: 0292.3744950  Fax: 0292.3743678", 10, true, true, ParagraphAlignment.CENTER);
//            utils.setHeaderRowforSingleCell(row.getCell(1), "(Ms. Nhung: 0946.886 868, Ms Trinh: 0918.755729)", 10, false, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(2), "TT5.6.1/ KD2-BM3", 10, false, true, ParagraphAlignment.CENTER);

            /*
            * Word content title
            * */

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.RIGHT);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            run.setText("SEAVINA, ngày " + now.getDayOfMonth() + ", tháng " + now.getMonthValue() + ", năm "+ now.getYear());
            run.addBreak();

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
            run.setBold(true);
            run.setFontSize(14);
            run.setText("ĐỀ NGHỊ BAO BÌ "+this.innerData.getName());
            run.addBreak();

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            paragraph.setSpacingLineRule(LineSpacingRule.AUTO);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
//            run.setBold(true);
            run.setFontSize(10);
            run.setText("Kính gửi: Phòng Mua Hàng Minh Tâm");
            run.addBreak();
            run.setText("Điện thoại: 02923. 3600063");
            run.addBreak();
            run.setText("Công ty CP SEAVINA (Phòng Kinh Doanh) xin gửi Phiếu đề nghị mua hàng với chi tiết như sau:");

            table = doc.createTable(1, 7);
            table.setWidth("100%");

            row = table.getRow(0);
            row.getCell(0).setWidth("5%");
            row.getCell(1).setWidth("35%");
            row.getCell(2).setWidth("15%");
            row.getCell(3).setWidth("8%");
            row.getCell(4).setWidth("12%");
            row.getCell(5).setWidth("10%");
            row.getCell(6).setWidth("15%");
            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(3).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(4).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(5).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(6).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

            utils.setHeaderRowforSingleCell(row.getCell(0), "STT", 10, false, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(1), "Tên Bao Bì", 10, false, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(2), "Qui cách (CM)", 10, false, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(3), "ĐVT", 10, false, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(4), "SL Đặt", 10, false, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(5), "Ghi chú", 10, false, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(6), "LSX", 10, false, true, ParagraphAlignment.CENTER);

            DecimalFormat formatter = new DecimalFormat("#,###");

            table = doc.createTable(orderObservableList.size(), 7);
            table.setWidth("100%");

            for(int i=0;i<orderObservableList.size();i++) {
                row = table.getRow(i);
                row.getCell(0).setWidth("5%");
                row.getCell(1).setWidth("35%");
                row.getCell(2).setWidth("15%");
                row.getCell(3).setWidth("8%");
                row.getCell(4).setWidth("12%");
                row.getCell(5).setWidth("10%");
                row.getCell(6).setWidth("15%");
                row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(3).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(4).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(5).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(6).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                float residualNumber = Float.parseFloat(orderObservableList.get(i).getpTotal()+"") + Float.parseFloat(orderObservableList.get(i).getpStock()+"") - Float.parseFloat(orderObservableList.get(i).getpResidualQuantity()+"") - Float.parseFloat(orderObservableList.get(i).getpDesireQuantity()+"");

                utils.setHeaderRowforSingleCell(row.getCell(0), (i+1)+"", 10, false, false, ParagraphAlignment.CENTER);
                utils.setHeaderRowforSingleCell(row.getCell(1), orderObservableList.get(i).getpName() + " ("+ orderObservableList.get(i).getpSpecs() +")", 10, false, false, ParagraphAlignment.LEFT);
                utils.setHeaderRowforSingleCell(row.getCell(2), orderObservableList.get(i).getpDimension(), 10, false, false, ParagraphAlignment.CENTER);
                utils.setHeaderRowforSingleCell(row.getCell(3), orderObservableList.get(i).getpUnit(), 10, false, false, ParagraphAlignment.CENTER);
                utils.setHeaderRowforSingleCell(row.getCell(4), formatter.format(Float.parseFloat(orderObservableList.get(i).getpTotal()+""))+"", 10, false, false, ParagraphAlignment.CENTER);
                utils.setHeaderRowforSingleCell(row.getCell(5), residualNumber > 0 ? "Dư "+formatter.format(residualNumber) : "", 10, false, false, ParagraphAlignment.CENTER);
                utils.setHeaderRowforSingleCell(row.getCell(6), orderObservableList.get(i).getWoName(), 10, false, false, ParagraphAlignment.CENTER);
            }

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            paragraph.setSpacingLineRule(LineSpacingRule.AUTO);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
            run.setFontSize(10);
            run.setText("Lưu ý: Mọi thay đổi hay có vấn đề chưa rõ phải phản hồi lại với bộ phận liên quan trước khi thực hiện để tránh sai sót có thể xảy ra.");
            run.addBreak();

            table = doc.createTable(1, 2);
            table.setWidth("100%");
            table.removeBorders();
            row = table.getRow(0);
            row.getCell(0).setWidth("50%");
            row.getCell(1).setWidth("50%");
            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

            utils.setHeaderRowforSingleCell(row.getCell(0), "Phòng Kinh Doanh", 10, false, true, ParagraphAlignment.LEFT);
            utils.setHeaderRowforSingleCell(row.getCell(1), "Người Lập Biểu", 10, false, true, ParagraphAlignment.RIGHT);

            if(orderObservableList.size() > 0) {
                if(file != null) {
                    // save it to .docx file
//                try (FileOutputStream out = new FileOutputStream(file.getPath())) // for fileChooser
                    try (FileOutputStream out = new FileOutputStream(file.getPath()+"/DE NGHI BB "+ this.innerData.getName() +".docx"))
                    {
                        doc.write(out);
                        utils.alert("info", Alert.AlertType.INFORMATION, "Xuất file thành công!", "File đã được lưu vào ổ đĩa!").showAndWait();
                    }
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void data2DocOfOrderBySupplier(File file, String code) {
        String imgFile = "e:\\java_platform\\docs-data\\logo.jpg";
        String _fontFamily = "Arial";
        ObservableList<OrderBySupllier> orderObservableList = FXCollections.observableArrayList(orderBySupplierDAO.getListBySupplierCode(this.innerData.getId()+"", code));
        Supplier supplier = supplierDAO.getDataByCode(code);

        try {
            XWPFDocument doc = new XWPFDocument();
            XWPFParagraph paragraph;
            XWPFRun run;
            XWPFTable table;
            XWPFTableRow row;

            /*
             * Word Header
             * */
            XWPFHeader header = doc.createHeader(HeaderFooterType.DEFAULT);

            table = header.createTable(1, 3);
            table.setWidth("100%");
            table.removeBorders();
            row = table.getRow(0);
            row.getCell(0).setWidth("22%");
            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(1).setWidth("55%");
            row.getCell(1).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);
            row.getCell(2).setWidth("23%");
            row.getCell(1).getParagraphs().get(0).setAlignment(ParagraphAlignment.RIGHT);

            paragraph = row.getCell(0).getParagraphArray(0);
            paragraph.setVerticalAlignment(TextAlignment.CENTER);
            run = paragraph.createRun();
            // add png image
            try (FileInputStream is = new FileInputStream(imgFile)) {
                run.addPicture(is,
                        Document.PICTURE_TYPE_PNG,    // png file
                        imgFile,
                        Units.toEMU(100),
                        Units.toEMU(45));            // 100x35 pixels
            }
            utils.setHeaderRowforSingleCell(row.getCell(1), "Công ty CP SEAVINA", 10, true, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(1), "Lô 16A-18, KCN Trà Nóc I,P.Trà Nóc, Q. Bình Thủy, TP. Cần Thơ ,Việt Nam", 10, true, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(1), "1801141886", 10, true, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(1), "Tel: 0292.3744950  Fax: 0292.3743678", 10, true, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(1), "(Ms. Nhung: 0946.886 868, Ms Trinh: 0918.755729)", 10, false, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(2), "TT5.6.1/ KD2-BM3", 10, false, true, ParagraphAlignment.CENTER);

            /*
             * Word content title
             * */

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.RIGHT);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            run.setText("SEAVINA, ngày " + now.getDayOfMonth() + ", tháng " + now.getMonthValue() + ", năm "+ now.getYear());
            run.addBreak();

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
            run.setBold(true);
            run.setFontSize(10);
            run.setText("ĐƠN ĐẶT HÀNG");
            run.addBreak();
            run.setText("(Số: "+code+".\t\t/"+now.getYear()+")");
            run.addBreak();

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            paragraph.setSpacingLineRule(LineSpacingRule.AUTO);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
//            run.setBold(true);
            run.setFontSize(10);
            run.setText("Kính gửi: "+supplier.getName());
            run.addBreak();
            run.setText("Đại diện: "+supplier.getDeputy());
            run.addBreak();
            run.setText("Địa chỉ: "+supplier.getAddress());
            run.addBreak();
            run.setText("Điện thoại: "+supplier.getPhone() + "\t\t"+"Fax: "+supplier.getFax());
            run.addBreak();
            run.setText("Công ty CP SEAVINA xin gửi ĐƠN ĐẶT HÀNG đến Quý Công Ty với chi tiết như sau:");

            table = doc.createTable(1, 7);
            table.setWidth("100%");

            row = table.getRow(0);
            row.getCell(0).setWidth("6%");
            row.getCell(1).setWidth("30%");
            row.getCell(2).setWidth("15%");
            row.getCell(3).setWidth("10%");
            row.getCell(4).setWidth("12%");
            row.getCell(5).setWidth("12%");
            row.getCell(6).setWidth("15%");
            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(3).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(4).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(5).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(6).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

            utils.setHeaderRowforSingleCell(row.getCell(0), "STT", 10, false, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(1), "Tên Bao Bì", 10, false, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(2), "Qui cách (CM)", 10, false, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(3), "ĐVT", 10, false, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(4), "SL Đặt", 10, false, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(5), "Mã", 10, false, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(6), "LSX", 10, false, true, ParagraphAlignment.CENTER);

            DecimalFormat formatter = new DecimalFormat("#,###");

            table = doc.createTable(orderObservableList.size(), 7);
            table.setWidth("100%");

            for(int i=0;i<orderObservableList.size();i++) {
                row = table.getRow(i);
                row.getCell(0).setWidth("6%");
                row.getCell(1).setWidth("30%");
                row.getCell(2).setWidth("15%");
                row.getCell(3).setWidth("10%");
                row.getCell(4).setWidth("12%");
                row.getCell(5).setWidth("12%");
                row.getCell(6).setWidth("15%");
                row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(3).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(4).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(5).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(6).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                utils.setHeaderRowforSingleCell(row.getCell(0), (i+1)+"", 10, false, false, ParagraphAlignment.CENTER);
                utils.setHeaderRowforSingleCell(row.getCell(1), orderObservableList.get(i).getpName(), 10, false, false, ParagraphAlignment.LEFT);
                utils.setHeaderRowforSingleCell(row.getCell(1), " ("+ orderObservableList.get(i).getpSpecs() +")", 10, false, false, ParagraphAlignment.LEFT);
                utils.setHeaderRowforSingleCell(row.getCell(2), orderObservableList.get(i).getpDimension(), 10, false, false, ParagraphAlignment.CENTER);
                utils.setHeaderRowforSingleCell(row.getCell(3), orderObservableList.get(i).getpUnit(), 10, false, false, ParagraphAlignment.CENTER);
                utils.setHeaderRowforSingleCell(row.getCell(4), formatter.format(Float.parseFloat(orderObservableList.get(i).getpTotal()+""))+"", 10, false, false, ParagraphAlignment.CENTER);
                utils.setHeaderRowforSingleCell(row.getCell(5), orderObservableList.get(i).getpCode(), 10, false, false, ParagraphAlignment.CENTER);
                utils.setHeaderRowforSingleCell(row.getCell(6), orderObservableList.get(i).getWoName(), 10, false, false, ParagraphAlignment.CENTER);
            }

            table = doc.createTable(4, 2);
            table.setWidth("100%");
            utils.spanCellsAcrossRow(table, 0,0,2);
            utils.spanCellsAcrossRow(table, 0,1,6);
            utils.spanCellsAcrossRow(table, 1,0,2);
            utils.spanCellsAcrossRow(table, 1,1,6);
            utils.spanCellsAcrossRow(table, 2,0,2);
            utils.spanCellsAcrossRow(table, 2,1,6);
            utils.spanCellsAcrossRow(table, 3,0,2);
            utils.spanCellsAcrossRow(table, 3,1,6);
            row = table.getRow(0);
            row.getCell(0).setWidth("40%");
            row.getCell(1).setWidth("60%");
            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

            utils.setHeaderRowforSingleCell(row.getCell(0), "Đặc điểm và qui cách", 10, false, false, ParagraphAlignment.LEFT);
            utils.setHeaderRowforSingleCell(row.getCell(1), "- Chất lượng: Đảm bảo đúng chất lượng và định lượng bao bì như mẫu chào hàng.", 10, true, false, ParagraphAlignment.LEFT);
            utils.setHeaderRowforSingleCell(row.getCell(1), "- Sản xuất theo mẫu xác nhận ngày: " + now.getDayOfMonth() + "/" + now.getMonthValue() + "/"+ now.getYear(), 10, true, false, ParagraphAlignment.LEFT);
            utils.setHeaderRowforSingleCell(row.getCell(1), "- Bao bì phải làm đúng kích thước, màu sắc, thông tin như đã xác nhận.", 10, true, false, ParagraphAlignment.LEFT);
            utils.setHeaderRowforSingleCell(row.getCell(1), "- Bao bì phải đạt tiêu chuẩn hàng Thủy sản xuất khẩu. Số lượng làm đủ, không thừa, không thiếu.", 10, true, false, ParagraphAlignment.LEFT);
            utils.setHeaderRowforSingleCell(row.getCell(1), "- Chúng tôi sẽ trả lại các lô hàng làm sai qui cách và không đúng các yêu cầu trên.", 10, true, false, ParagraphAlignment.LEFT);
            utils.setHeaderRowforSingleCell(row.getCell(1), "- Khi giao hàng vui lòng liên hệ: Mr. Duy (0932.830.900).", 10, false, false, ParagraphAlignment.LEFT);

            row = table.getRow(1);
            utils.setHeaderRowforSingleCell(row.getCell(0), "Ngày giao hàng", 10, false, false, ParagraphAlignment.LEFT);

            row = table.getRow(2);
            utils.setHeaderRowforSingleCell(row.getCell(0), "Địa chỉ giao hàng", 10, false, false, ParagraphAlignment.LEFT);
            utils.setHeaderRowforSingleCell(row.getCell(1), "Công ty CP SEAVINA", 10, true, false, ParagraphAlignment.LEFT);
            utils.setHeaderRowforSingleCell(row.getCell(1), "Lô 16A-18, KCN Trà Nóc 1, P. Trà Nóc, Q. Bình Thủy, TP. Cần Thơ", 10, false, false, ParagraphAlignment.LEFT);

            row = table.getRow(3);
            utils.setHeaderRowforSingleCell(row.getCell(0), "Chú ý", 10, false, false, ParagraphAlignment.LEFT);
            utils.setHeaderRowforSingleCell(row.getCell(1), "Mọi thay đổi hoặc có vấn đề gì chưa rõ phải báo lại ngay với Công Ty CP SEAVINA trước khi tiến hành.", 10, false, false, ParagraphAlignment.LEFT);

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            paragraph.setSpacingLineRule(LineSpacingRule.AUTO);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
            run.setFontSize(10);
            run.addBreak();

            table = doc.createTable(1, 2);
            table.setWidth("100%");
            table.removeBorders();
            row = table.getRow(0);
            row.getCell(0).setWidth("50%");
            row.getCell(1).setWidth("50%");
            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

            utils.setHeaderRowforSingleCell(row.getCell(0), "Bên bán hàng", 10, false, true, ParagraphAlignment.LEFT);
            utils.setHeaderRowforSingleCell(row.getCell(1), "Bên mua hàng", 10, false, true, ParagraphAlignment.RIGHT);


            if(file != null) {
                // save it to .docx file
//                try (FileOutputStream out = new FileOutputStream(file.getPath())) // for fileChooser
                try (FileOutputStream out = new FileOutputStream(file.getPath()+"/"+ code + "-"+this.innerData.getName()+".docx"))
                {
                    doc.write(out);
//                    utils.alert("info", Alert.AlertType.INFORMATION, "Xuất file thành công!", "File đã được lưu vào ổ đĩa!").showAndWait();
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
