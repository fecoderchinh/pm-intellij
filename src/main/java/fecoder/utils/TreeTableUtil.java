package fecoder.utils;

import fecoder.DAO.*;
import fecoder.controllers.WorkOrderProductController;
import fecoder.models.WorkOrder;
import fecoder.models.WorkOrderProductPackaging;
import fecoder.models.WorkOrderToString;
import fecoder.models.WorkProduction;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

import javax.swing.text.AbstractDocument;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class TreeTableUtil {

    public static TreeItem<WorkProduction> getModel(int workOrderID) {
        ProductDAO productDAO = new ProductDAO();
        WorkProductionDAO workProductionDAO = new WorkProductionDAO();
        WorkOrderDAO workOrderDAO = new WorkOrderDAO();
        WorkOrderProductStringDAO workOrderProductStringDAO = new WorkOrderProductStringDAO();
        WorkOrderProductDAO workOrderProductDAO = new WorkOrderProductDAO();
        YearDAO yearDAO = new YearDAO();
        WorkOrderProductPackagingDAO workOrderProductPackagingDAO = new WorkOrderProductPackagingDAO();
        OrderBySupplierDAO orderBySupplierDAO = new OrderBySupplierDAO();

        ObservableList<WorkProduction> workOrderList = FXCollections.observableArrayList(workProductionDAO.getWorkOrderList(workOrderID+""));

        Locale locale  = new Locale("en", "US");
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(locale);
        df.applyPattern("###,###.###");

//        System.out.println(workOrderList.size());
        TreeItem<WorkProduction> root = new TreeItem<>();

        if(workOrderList.size() > 0) {
            for(WorkProduction _workOrder : workOrderList) {
//                System.out.println(_workOrder.getWorkOrderName()); // work_order.name
//                System.out.println(workOrderProductStringDAO.getDataByName(_workOrder.getWorkOrderName()).getId()); // work_order_product.id
//                System.out.println(workOrderProductDAO.getDataByID(workOrderProductStringDAO.getDataByName(_workOrder.getWorkOrderName()).getId()).getWork_order_id()); // work_order_product.work_order_id
//                System.out.println(workOrderDAO.getDataByName(_workOrder.getWorkOrderName()).getYear());
                ObservableList<WorkProduction> productList = FXCollections.observableArrayList(
                        workProductionDAO.getProductList(
                                workOrderID+"" // work_order_product.work_order_id
                        )
                );
                TreeItem<WorkProduction> _workOrderNode = new TreeItem<>(new WorkProduction(0,"", "","","",_workOrder.getWorkOrderName(),"","","","","","",0, "",null,"","","","", "", 0, "", "", ""));
                if(productList.size() > 0) {
//                    System.out.println(productList.size());
                    for(WorkProduction _product : productList) {
//                        System.out.println("------------------------>");
//                        System.out.println(workOrderDAO.getDataByName(_product.getWorkOrderName()).getYear()); // years.id
//                        System.out.println(workOrderProductDAO.getDataByID(workOrderProductStringDAO.getDataByName(_product.getWorkOrderName()).getId()).getWork_order_id()); // work_order_product.work_order_id
//                        System.out.println(workOrderProductDAO.getDataByID(workOrderProductStringDAO.getDataByName(_product.getWorkOrderName()).getId()).getProduct_id()); // work_order_product.product_id
//                        System.out.println(_product.getOrdinalNumbers()); // work_order_product.ordinal_num
                        ObservableList<WorkProduction> packagingList = FXCollections.observableArrayList(
                                workProductionDAO.getPackagingList(
                                        workOrderID+"", // work_order_product.work_order_id
                                        workOrderProductPackagingDAO.getDataByID(_product.getId()).getProduct_id(), // work_order_product.product_id
                                        _product.getOrdinalNumbers(), // work_order_product.ordinal_num,
                                        _product.getOrderTimes() // work_order_product.order_times
                                )
                        );
//                        System.out.println(_product.getYear());
//                        System.out.println(workOrderProductDAO.getDataByID(workOrderProductStringDAO.getDataByName(_workOrder.getWorkOrderName()).getId()).getWork_order_id());
//                        System.out.println(productDAO.getDataByName(_product.getProductName()).getId());
//                        System.out.println(_product.getOrdinalNumbers());
                        TreeItem<WorkProduction> _productNode = new TreeItem<>(new WorkProduction(0,"", "","","", _product.getOrdinalNumbers() + "/ " + _product.getProductName() + " (L???n "+_product.getOrderTimes()+")","","","","","",null,0, null,"","","","","", "", 0, "", "", ""));
                        for(WorkProduction _packaging : packagingList) {
//                            System.out.println(_packaging.getOrdinalNumbers() + " | " + _packaging.getProductName() + " | " +_packaging.getPackagingName() + " | " + _packaging.getActualQuantity() + " | " + _packaging.getPackagingSuplier());
//                            _productNode.getChildren().add(new TreeItem<>(_packaging));
                            _productNode.getChildren().add(new TreeItem<>(new WorkProduction(_packaging.getId(),"", "","","",_packaging.getPackagingName(),"", _packaging.getPackagingDimension(), _packaging.getPackagingSuplier(), _packaging.getPackagingCode(), _packaging.getUnit(), _packaging.getPrintStatus(), 0, df.format(Float.parseFloat(_packaging.getWorkOrderQuantity())), df.format(Float.parseFloat(_packaging.getStock())), df.format(Integer.parseInt(_packaging.getActualQuantity())), _packaging.getResidualQuantity(), _packaging.getTotalResidualQuantity(), _packaging.getNoteProduct(), "", 0, "", _packaging.getPackagingCustomCode(), "")));
                            _productNode.setExpanded(true);
                        }
//                        System.out.println("---------------------------------------------");

                        _workOrderNode.getChildren().add(_productNode);
                        _workOrderNode.setExpanded(true);
                    }
                }
                root = _workOrderNode;
            }
        }

        return root;
    }

    public static TreeTableColumn<WorkProduction, Integer> getIdColumn()
    {
        TreeTableColumn<WorkProduction, Integer> column = new TreeTableColumn<>("id");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("id"));
        column.setCellFactory(new Callback<TreeTableColumn<WorkProduction, Integer>, TreeTableCell<WorkProduction, Integer>>() {
            @Override
            public TreeTableCell<WorkProduction, Integer> call(TreeTableColumn<WorkProduction, Integer> workProductionIntegerTreeTableColumn) {
                return new TreeTableCell<WorkProduction, Integer>() {
                    @Override
                    protected void updateItem(Integer integer, boolean b) {
                        if (integer != null) {
                            super.setText(integer == 0?"":integer.toString());
                        }
                    }
                };
            }
        });
        return column;
    }

    public static TreeTableColumn<WorkProduction, String> getOrdinalNumberColumn()
    {
        TreeTableColumn<WorkProduction, String> column = new TreeTableColumn<>("STT");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("ordinalNumbers"));
        return column;
    }

    public static TreeTableColumn<WorkProduction, String> getWOIDColumn()
    {
        TreeTableColumn<WorkProduction, String> column = new TreeTableColumn<>("ID_LSX");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("woID"));
        return column;
    }

    public static TreeTableColumn<WorkProduction, String> getWorkOrderNameColumn()
    {
        TreeTableColumn<WorkProduction, String> column = new TreeTableColumn<>("LSX");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("workOrderName"));
        return column;
    }

    public static TreeTableColumn<WorkProduction, String> getProductNameColumn(TreeTableView table)
    {
        TreeTableColumn<WorkProduction, String> column = new TreeTableColumn<>("M???t h??ng");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("productName"));
        column.prefWidthProperty().bind(table.widthProperty().multiply(0.3));
//        column.setCellFactory(new Callback<TreeTableColumn<WorkProduction, String>, TreeTableCell<WorkProduction, String>>() {
//            @Override
//            public TreeTableCell<WorkProduction, String> call(TreeTableColumn<WorkProduction, String> workProductionStringTreeTableColumn) {
//                TreeTableCell<WorkProduction, String> cell = new TreeTableCell<>();
//                Text text = new Text();
//                cell.setGraphic(text);
//                cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
//                text.wrappingWidthProperty().bind(cell.widthProperty());
//                text.textProperty().bind(cell.itemProperty());
//                return cell;
//            }
//        });
        column.setCellFactory(WRAPPING_CELL_FACTORY);
        return column;
    }

    public static TreeTableColumn<WorkProduction, String> getPackagingNameColumn(TreeTableView table)
    {
        TreeTableColumn<WorkProduction, String> column = new TreeTableColumn<>("Bao b??");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("packagingName"));
        column.prefWidthProperty().bind(table.widthProperty().multiply(0.45));
//        column.setResizable(false);
//        column.setCellFactory(new Callback<TreeTableColumn<WorkProduction, String>, TreeTableCell<WorkProduction, String>>() {
//            @Override
//            public TreeTableCell<WorkProduction, String> call(TreeTableColumn<WorkProduction, String> workProductionStringTreeTableColumn) {
//                TreeTableCell<WorkProduction, String> cell = new TreeTableCell<>();
//                Text text = new Text();
//                cell.setGraphic(text);
//                cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
//                text.wrappingWidthProperty().bind(cell.widthProperty());
//                text.textProperty().bind(cell.itemProperty());
//                return cell;
//            }
//        });
        column.setCellFactory(WRAPPING_CELL_FACTORY);
        return column;
    }

    public static TreeTableColumn<WorkProduction, String> getPackagingSpecificationColumn(TreeTableView table)
    {
        TreeTableColumn<WorkProduction, String> column = new TreeTableColumn<>("Qui c??ch");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("packagingSpecification"));
        column.prefWidthProperty().bind(table.widthProperty().multiply(0.3));
        column.setResizable(false);
//        column.setCellFactory(new Callback<TreeTableColumn<WorkProduction, String>, TreeTableCell<WorkProduction, String>>() {
//            @Override
//            public TreeTableCell<WorkProduction, String> call(TreeTableColumn<WorkProduction, String> workProductionStringTreeTableColumn) {
//                TreeTableCell<WorkProduction, String> cell = new TreeTableCell<>();
//                Text text = new Text();
//                cell.setGraphic(text);
//                cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
//                text.wrappingWidthProperty().bind(cell.widthProperty());
//                text.textProperty().bind(cell.itemProperty());
//                return cell;
//            }
//        });
        column.setCellFactory(WRAPPING_CELL_FACTORY);
        return column;
    }

    public static TreeTableColumn<WorkProduction, String> getPackagingDimensionColumn(TreeTableView table)
    {
        TreeTableColumn<WorkProduction, String> column = new TreeTableColumn<>("K??ch th?????c");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("packagingDimension"));
        column.prefWidthProperty().bind(table.widthProperty().multiply(0.10));
        column.setResizable(false);
//        column.setCellFactory(new Callback<TreeTableColumn<WorkProduction, String>, TreeTableCell<WorkProduction, String>>() {
//            @Override
//            public TreeTableCell<WorkProduction, String> call(TreeTableColumn<WorkProduction, String> workProductionStringTreeTableColumn) {
//                TreeTableCell<WorkProduction, String> cell = new TreeTableCell<>();
//                Text text = new Text();
//                cell.setGraphic(text);
//                cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
//                text.wrappingWidthProperty().bind(cell.widthProperty());
//                text.textProperty().bind(cell.itemProperty());
//                return cell;
//            }
//        });
        column.setCellFactory(WRAPPING_CELL_FACTORY);
        return column;
    }

    public static TreeTableColumn<WorkProduction, String> getPackagingSuplierColumn()
    {
        TreeTableColumn<WorkProduction, String> column = new TreeTableColumn<>("NCC");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("packagingSuplier"));
        return column;
    }

    public static TreeTableColumn<WorkProduction, String> getPackagingCodeColumn()
    {
        TreeTableColumn<WorkProduction, String> column = new TreeTableColumn<>("M?? BB");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("packagingCode"));
//        column.setCellFactory(new Callback<TreeTableColumn<WorkProduction, String>, TreeTableCell<WorkProduction, String>>() {
//            @Override
//            public TreeTableCell<WorkProduction, String> call(TreeTableColumn<WorkProduction, String> workProductionStringTreeTableColumn) {
//                TreeTableCell<WorkProduction, String> cell = new TreeTableCell<>();
//                Text text = new Text();
//                cell.setGraphic(text);
//                cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
//                text.wrappingWidthProperty().bind(cell.widthProperty());
//                text.textProperty().bind(cell.itemProperty());
//                return cell;
//            }
//        });
        column.setCellFactory(WRAPPING_CELL_FACTORY);
        return column;
    }

    public static TreeTableColumn<WorkProduction, String> getUnitColumn()
    {
        TreeTableColumn<WorkProduction, String> column = new TreeTableColumn<>("??VT");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("unit"));
        return column;
    }

    public static TreeTableColumn<WorkProduction, String> getPrintStatusColumn(TreeTableView table, final WorkOrderProductController controller, WorkOrderToString data)
    {
        TreeTableColumn<WorkProduction, String> column = new TreeTableColumn<>("In s???n");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("printStatus"));
        column.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        column.setCellFactory(new Callback<TreeTableColumn<WorkProduction, String>, TreeTableCell<WorkProduction, String>>() {
            @Override
            public TreeTableCell<WorkProduction, String> call(TreeTableColumn<WorkProduction, String> workProductionStringTreeTableColumn) {
                return new TextFieldTreeTableCell<>();
            }
        });
        column.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        column.setOnEditCommit(new EventHandler<TreeTableColumn.CellEditEvent<WorkProduction, String>>() {
            @Override
            public void handle(TreeTableColumn.CellEditEvent<WorkProduction, String> workProductionStringCellEditEvent) {



                /*IMPORTANT: Do NOT forget to set ENOUGH data at your TreeItem<WorkProduction> leaves.*/



                TreeItem<WorkProduction> workProductionTreeItem = (TreeItem<WorkProduction>) table.getSelectionModel().getSelectedItem();
//                System.out.println(workProductionTreeItem.getValue().getId());

                TreeItem<WorkProduction> currentEditingWorkProductionTreeItem =  table.getTreeItem(workProductionStringCellEditEvent.getTreeTablePosition().getRow());

//                System.out.println(currentEditingWorkProductionTreeItem.getValue().getId());

                currentEditingWorkProductionTreeItem.getValue().setPrintStatus(workProductionStringCellEditEvent.getNewValue());
                WorkOrderProductPackagingDAO workOrderProductPackagingDAO = new WorkOrderProductPackagingDAO();

                workOrderProductPackagingDAO.updateData("printed", workProductionStringCellEditEvent.getNewValue(), workProductionTreeItem.getValue().getId());
                controller.setData(data);
            }
        });
        return column;
    }

    public static TreeTableColumn<WorkProduction, Integer> getPackQuantityColumn()
    {
        TreeTableColumn<WorkProduction, Integer> column = new TreeTableColumn<>("????ng g??i /th??ng");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("packQuantity"));
        column.setCellFactory(new Callback<TreeTableColumn<WorkProduction, Integer>, TreeTableCell<WorkProduction, Integer>>() {
            @Override
            public TreeTableCell<WorkProduction, Integer> call(TreeTableColumn<WorkProduction, Integer> workProductionIntegerTreeTableColumn) {
                return new TreeTableCell<WorkProduction, Integer>() {
                    @Override
                    protected void updateItem(Integer integer, boolean b) {
                        super.updateItem(integer, b);
                        setText(b || integer == 0 ? "" : integer.toString());
                    }
                };
            }
        });
        return column;
    }

    public static TreeTableColumn<WorkProduction, String> getWorkOrderQuantityColumn()
    {
        TreeTableColumn<WorkProduction, String> column = new TreeTableColumn<>("SL ?????t");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("workOrderQuantity"));
//        column.setCellFactory(new Callback<TreeTableColumn<WorkProduction, String>, TreeTableCell<WorkProduction, String>>() {
//            @Override
//            public TreeTableCell<WorkProduction, String> call(TreeTableColumn<WorkProduction, String> workProductionIntegerTreeTableColumn) {
//                return new TreeTableCell<WorkProduction, String>() {
//                    @Override
//                    protected void updateItem(String value, boolean b) {
//                        if (value != null) {
//                            Locale locale  = new Locale("vi", "VN");
//                            DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(locale);
//                            df.applyPattern("###,###.###");
//
//                            super.setText(df.format(Float.parseFloat(value)));
//                        }
//                    }
//                };
//            }
//        });
        return column;
    }

//    public static TreeTableColumn<WorkProduction, Float> getStockColumn(TreeTableView table)
//    {
//        TreeTableColumn<WorkProduction, Float> column = new TreeTableColumn<>("T???n");
//        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("stock"));
//        column.setCellFactory(new Callback<TreeTableColumn<WorkProduction, Float>, TreeTableCell<WorkProduction, Float>>() {
//            @Override
//            public TreeTableCell<WorkProduction, Float> call(TreeTableColumn<WorkProduction, Float> workProductionFloatTreeTableColumn) {
//                return new TextFieldTreeTableCell<>();
//            }
//        });
//        column.setCellFactory(TextFieldTreeTableCell.<WorkProduction, Float>forTreeTableColumn(new FloatStringConverter()));
//        column.setOnEditCommit(new EventHandler<TreeTableColumn.CellEditEvent<WorkProduction, Float>>() {
//            @Override
//            public void handle(TreeTableColumn.CellEditEvent<WorkProduction, Float> workProductionFloatCellEditEvent) {
//                TreeItem<WorkProduction> workProductionTreeItem = (TreeItem<WorkProduction>) table.getSelectionModel().getSelectedItem();
////                System.out.println(workProductionTreeItem.getValue().getId());
//
//                TreeItem<WorkProduction> currentEditingWorkProductionTreeItem =  table.getTreeItem(workProductionFloatCellEditEvent.getTreeTablePosition().getRow());
////                System.out.println(workProductionFloatCellEditEvent.getNewValue());
//                currentEditingWorkProductionTreeItem.getValue().setStock(workProductionFloatCellEditEvent.getNewValue());
//                WorkOrderProductPackagingDAO workOrderProductPackagingDAO = new WorkOrderProductPackagingDAO();
//                workOrderProductPackagingDAO.updateQuantity("stock", workProductionFloatCellEditEvent.getNewValue(), workProductionTreeItem.getValue().getId());
//            }
//        });
//        return column;
//    }

    public static TreeTableColumn<WorkProduction, String> getStockColumn(TreeTableView table, final WorkOrderProductController controller, WorkOrderToString data)
    {
        TreeTableColumn<WorkProduction, String> column = new TreeTableColumn<>("T???n");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("stock"));
        column.setCellFactory(new Callback<TreeTableColumn<WorkProduction, String>, TreeTableCell<WorkProduction, String>>() {
            @Override
            public TreeTableCell<WorkProduction, String> call(TreeTableColumn<WorkProduction, String> workProductionStringTreeTableColumn) {
                return new TextFieldTreeTableCell<>();
            }
        });
        column.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        column.setOnEditCommit(new EventHandler<TreeTableColumn.CellEditEvent<WorkProduction, String>>() {
            @Override
            public void handle(TreeTableColumn.CellEditEvent<WorkProduction, String> workProductionStringCellEditEvent) {



                /*IMPORTANT: Do NOT forget to set ENOUGH data at your TreeItem<WorkProduction> leaves.*/



                TreeItem<WorkProduction> workProductionTreeItem = (TreeItem<WorkProduction>) table.getSelectionModel().getSelectedItem();
//                System.out.println(workProductionTreeItem.getValue().getId());

                TreeItem<WorkProduction> currentEditingWorkProductionTreeItem =  table.getTreeItem(workProductionStringCellEditEvent.getTreeTablePosition().getRow());

//                System.out.println(currentEditingWorkProductionTreeItem.getValue().getId());

                currentEditingWorkProductionTreeItem.getValue().setStock(workProductionStringCellEditEvent.getNewValue());
                WorkOrderProductPackagingDAO workOrderProductPackagingDAO = new WorkOrderProductPackagingDAO();

                workOrderProductPackagingDAO.updateQuantity("stock", Float.parseFloat(workProductionStringCellEditEvent.getNewValue()), workProductionTreeItem.getValue().getId());
                controller.setData(data);
            }
        });

        return column;
    }

    public static TreeTableColumn<WorkProduction, String> getActualQuantityColumn(TreeTableView table, final WorkOrderProductController controller, WorkOrderToString data)
    {
        TreeTableColumn<WorkProduction, String> column = new TreeTableColumn<>("Th???c ?????t");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("actualQuantity"));
        column.setCellFactory(new Callback<TreeTableColumn<WorkProduction, String>, TreeTableCell<WorkProduction, String>>() {
            @Override
            public TreeTableCell<WorkProduction, String> call(TreeTableColumn<WorkProduction, String> workProductionStringTreeTableColumn) {
                return new TextFieldTreeTableCell<>();
            }
        });
        column.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        column.setOnEditCommit(new EventHandler<TreeTableColumn.CellEditEvent<WorkProduction, String>>() {
            @Override
            public void handle(TreeTableColumn.CellEditEvent<WorkProduction, String> workProductionStringCellEditEvent) {
                TreeItem<WorkProduction> workProductionTreeItem = (TreeItem<WorkProduction>) table.getSelectionModel().getSelectedItem();
//                System.out.println(workProductionTreeItem.getValue().getId());

                TreeItem<WorkProduction> currentEditingWorkProductionTreeItem =  table.getTreeItem(workProductionStringCellEditEvent.getTreeTablePosition().getRow());
//                System.out.println(workProductionFloatCellEditEvent.getNewValue());
                currentEditingWorkProductionTreeItem.getValue().setActualQuantity(workProductionStringCellEditEvent.getNewValue());
                WorkOrderProductPackagingDAO workOrderProductPackagingDAO = new WorkOrderProductPackagingDAO();
                workOrderProductPackagingDAO.updateQuantity("actual_qty", Float.parseFloat(workProductionStringCellEditEvent.getNewValue()), workProductionTreeItem.getValue().getId());
                controller.setData(data);
            }
        });
        return column;
    }

    public static TreeTableColumn<WorkProduction, String> getResidualQuantityColumn(TreeTableView table, final WorkOrderProductController controller, WorkOrderToString data)
    {
        TreeTableColumn<WorkProduction, String> column = new TreeTableColumn<>("Hao h???t");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("residualQuantity"));
        column.setCellFactory(new Callback<TreeTableColumn<WorkProduction, String>, TreeTableCell<WorkProduction, String>>() {
            @Override
            public TreeTableCell<WorkProduction, String> call(TreeTableColumn<WorkProduction, String> workProductionStringTreeTableColumn) {
                return new TextFieldTreeTableCell<>();
            }
        });
        column.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        column.setOnEditCommit(new EventHandler<TreeTableColumn.CellEditEvent<WorkProduction, String>>() {
            @Override
            public void handle(TreeTableColumn.CellEditEvent<WorkProduction, String> workProductionStringCellEditEvent) {
                TreeItem<WorkProduction> workProductionTreeItem = (TreeItem<WorkProduction>) table.getSelectionModel().getSelectedItem();
//                System.out.println(workProductionTreeItem.getValue().getId());

                TreeItem<WorkProduction> currentEditingWorkProductionTreeItem =  table.getTreeItem(workProductionStringCellEditEvent.getTreeTablePosition().getRow());
//                System.out.println(workProductionFloatCellEditEvent.getNewValue());
                currentEditingWorkProductionTreeItem.getValue().setResidualQuantity(workProductionStringCellEditEvent.getNewValue());
                WorkOrderProductPackagingDAO workOrderProductPackagingDAO = new WorkOrderProductPackagingDAO();
                workOrderProductPackagingDAO.updateQuantity("residual_qty", Float.parseFloat(workProductionStringCellEditEvent.getNewValue()), workProductionTreeItem.getValue().getId());
                controller.setData(data);
            }
        });
        return column;
    }

    public static TreeTableColumn<WorkProduction, String> getPackagingCustomCodeColumn(TreeTableView table, final WorkOrderProductController controller, WorkOrderToString data)
    {
        TreeTableColumn<WorkProduction, String> column = new TreeTableColumn<>("M?? BB Theo ??H");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("packagingCustomCode"));
        column.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        column.setCellFactory(new Callback<TreeTableColumn<WorkProduction, String>, TreeTableCell<WorkProduction, String>>() {
            @Override
            public TreeTableCell<WorkProduction, String> call(TreeTableColumn<WorkProduction, String> workProductionStringTreeTableColumn) {
                return new TextFieldTreeTableCell<>();
            }
        });
        column.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        column.setOnEditCommit(new EventHandler<TreeTableColumn.CellEditEvent<WorkProduction, String>>() {
            @Override
            public void handle(TreeTableColumn.CellEditEvent<WorkProduction, String> workProductionStringCellEditEvent) {
                TreeItem<WorkProduction> workProductionTreeItem = (TreeItem<WorkProduction>) table.getSelectionModel().getSelectedItem();
//                System.out.println(workProductionTreeItem.getValue().getId());

                TreeItem<WorkProduction> currentEditingWorkProductionTreeItem =  table.getTreeItem(workProductionStringCellEditEvent.getTreeTablePosition().getRow());
//                System.out.println(workProductionFloatCellEditEvent.getNewValue());
                currentEditingWorkProductionTreeItem.getValue().setResidualQuantity(workProductionStringCellEditEvent.getNewValue());
                WorkOrderProductPackagingDAO workOrderProductPackagingDAO = new WorkOrderProductPackagingDAO();
                workOrderProductPackagingDAO.updateData("packaging_custom_code", workProductionStringCellEditEvent.getNewValue(), workProductionTreeItem.getValue().getId());
                controller.setData(data);
            }
        });
        return column;
    }

    public static TreeTableColumn<WorkProduction, String> getTotalResidualQuantityColumn(TreeTableView table)
    {
        TreeTableColumn<WorkProduction, String> column = new TreeTableColumn<>("C??n d??");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("totalResidualQuantity"));
//        column.setCellFactory(new Callback<TreeTableColumn<WorkProduction, String>, TreeTableCell<WorkProduction, String>>() {
//            @Override
//            public TreeTableCell<WorkProduction, String> call(TreeTableColumn<WorkProduction, String> workProductionStringTreeTableColumn) {
//                return new TextFieldTreeTableCell<>();
//            }
//        });
//        column.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
//        column.setOnEditCommit(new EventHandler<TreeTableColumn.CellEditEvent<WorkProduction, String>>() {
//            @Override
//            public void handle(TreeTableColumn.CellEditEvent<WorkProduction, String> workProductionStringCellEditEvent) {
//                TreeItem<WorkProduction> workProductionTreeItem = (TreeItem<WorkProduction>) table.getSelectionModel().getSelectedItem();
////                System.out.println(workProductionTreeItem.getValue().getId());
//
//                TreeItem<WorkProduction> currentEditingWorkProductionTreeItem =  table.getTreeItem(workProductionStringCellEditEvent.getTreeTablePosition().getRow());
////                System.out.println(workProductionFloatCellEditEvent.getNewValue());
//                currentEditingWorkProductionTreeItem.getValue().setTotalResidualQuantity(
//                        (Float.parseFloat(workProductionTreeItem.getValue().getActualQuantity()) + Float.parseFloat(workProductionTreeItem.getValue().getStock()) - Float.parseFloat(workProductionTreeItem.getValue().getResidualQuantity()) - (workProductionTreeItem.getValue().getPackQuantity() * workProductionTreeItem.getValue().getWorkOrderQuantity())) +""
//                );
//            }
//        });
        return column;
    }

    public static TreeTableColumn<WorkProduction, String> getNoteProductColumn()
    {
        TreeTableColumn<WorkProduction, String> column = new TreeTableColumn<>("Ghi ch??");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("noteProduct"));
        column.setCellFactory(WRAPPING_CELL_FACTORY);
        return column;
    }

    public static final Callback<TreeTableColumn<WorkProduction,String>, TreeTableCell<WorkProduction,String>> WRAPPING_CELL_FACTORY =
            new Callback<TreeTableColumn<WorkProduction,String>, TreeTableCell<WorkProduction,String>>() {

                @Override public TreeTableCell<WorkProduction,String> call(TreeTableColumn<WorkProduction,String> param) {
                    TreeTableCell<WorkProduction,String> tableCell = new TreeTableCell<WorkProduction, String>() {
                        @Override protected void updateItem(String item, boolean empty) {
                            if (item == getItem()) return;

                            super.updateItem(item, empty);

                            if (item == null) {
                                super.setText(null);
                                super.setGraphic(null);
                            } else {
                                super.setText(null);
                                Label l = new Label(item);
                                l.setWrapText(false);
                                VBox box = new VBox(l);
                                l.heightProperty().addListener((observable,oldValue,newValue)-> {
                                    Platform.runLater(()->this.getTreeTableRow().requestLayout());
                                    box.setPrefHeight(newValue.intValue());
                                });
                                super.setGraphic(box);
                            }
                        }
                    };
                    return tableCell;
                }
            };
}
