package fecoder.utils;

import fecoder.DAO.*;
import fecoder.models.Supplier;
import fecoder.models.WorkOrderProduct;
import fecoder.models.WorkProduction;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class TreeTableUtil {

//    public static TreeItem<WorkProduction> getModel(String workOrderName) {
//        ProductDAO productDAO = new ProductDAO();
//        WorkProductionDAO workProductionDAO = new WorkProductionDAO();
//        WorkOrderDAO workOrderDAO = new WorkOrderDAO();
//        WorkOrderProductStringDAO workOrderProductStringDAO = new WorkOrderProductStringDAO();
//        WorkOrderProductDAO workOrderProductDAO = new WorkOrderProductDAO();
//        YearDAO yearDAO = new YearDAO();
//
//        ObservableList<WorkProduction> workOrderList = FXCollections.observableArrayList(workProductionDAO.getWorkOrderList(workOrderDAO.getDataByName(workOrderName).getId(), workOrderDAO.getDataByName(workOrderName).getYear()));
//
//        System.out.println(workOrderList.size());
//        TreeItem<WorkProduction> root = new TreeItem<>();
//        if(workOrderList.size() != 0) {
//
//            for (WorkProduction _workOrder : workOrderList) {
//                System.out.println(workOrderProductDAO.getDataByID(workOrderProductStringDAO.getDataByName(_workOrder.getWorkOrderName()).getId()).getWork_order_id());
//                System.out.println(_workOrder.getYear());
//                System.out.println(workOrderProductDAO.getDataByID(workOrderProductStringDAO.getDataByName(_workOrder.getWorkOrderName()).getId()).getProduct_id());
//                System.out.println(workOrderProductDAO.getDataByID(workOrderProductStringDAO.getDataByName(_workOrder.getWorkOrderName()).getId()).getOrdinal_num());
//                TreeItem<WorkProduction> _root = new TreeItem<>(new WorkProduction(0, "", null, null, _workOrder.getWorkOrderName(), null, null, 0, null, null, null, 0, 0, 0, 0, 0, 0, "", 0));
//                if(_workOrder.getWorkOrderName() != null) {
//                    System.out.println(workOrderProductDAO.getDataByID(workOrderProductStringDAO.getDataByName(_workOrder.getWorkOrderName()).getId()).getWork_order_id());
//                    System.out.println(_workOrder.getYear());
//                    System.out.println(workOrderProductDAO.getDataByID(workOrderProductStringDAO.getDataByName(_workOrder.getWorkOrderName()).getId()).getProduct_id());
//                    System.out.println(workOrderProductDAO.getDataByID(workOrderProductStringDAO.getDataByName(_workOrder.getWorkOrderName()).getId()).getOrdinal_num());
//
//
//
//                    ObservableList<WorkProduction> productList = FXCollections.observableArrayList(workProductionDAO.getPackagingList(
//                            workOrderProductDAO.getDataByID(workOrderProductStringDAO.getDataByName(_workOrder.getWorkOrderName()).getId()).getWork_order_id(),
//                            _workOrder.getYear(),
//                            workOrderProductDAO.getDataByID(workOrderProductStringDAO.getDataByName(_workOrder.getWorkOrderName()).getId()).getProduct_id(),
//                            workOrderProductDAO.getDataByID(workOrderProductStringDAO.getDataByName(_workOrder.getWorkOrderName()).getId()).getOrdinal_num()
//                    ));
//                    if(productList.size() != 0) {
////                        System.out.println(productList.size());
//                        for(WorkProduction _product : productList) {
//                            TreeItem<WorkProduction> _productNode = new TreeItem<>(new WorkProduction(0, _product.getOrdinalNumbers(), null, null, _product.getProductName(), null, null, 0, null, null, null, 0, 0, 0, 0, 0, 0, "", 0));
//                            if(_product.getProductName() != null) {
//                                ObservableList<WorkProduction> packagingList = FXCollections.observableArrayList(workProductionDAO.getPackagingList(
//                                        workOrderProductDAO.getDataByID(workOrderProductStringDAO.getDataByName(_workOrder.getWorkOrderName()).getId()).getWork_order_id(),
//                                        _product.getYear(),
//                                        workOrderProductDAO.getDataByID(workOrderProductStringDAO.getDataByName(_workOrder.getWorkOrderName()).getId()).getProduct_id(),
//                                        workOrderProductDAO.getDataByID(workOrderProductStringDAO.getDataByName(_workOrder.getWorkOrderName()).getId()).getOrdinal_num()
//                                ));
//                                System.out.println(packagingList.size());
//                                for(WorkProduction _packaging : packagingList) {
//                                    TreeItem<WorkProduction> _packagingLeaf = new TreeItem<>(new WorkProduction(0, null, null, null, _packaging.getPackagingName(), null, null, 0, null, null, null, 0, 0, 0, 0, 0, 0, "", 0));
//                                    _productNode.getChildren().add(_packagingLeaf);
//                                }
//                            }
//                            _root.getChildren().add(_productNode);
//                        }
//                    }
//                }
//                root = _root;
//            }
//        }
//
//        return root;
//    }

    public static TreeItem<WorkProduction> getModel(String workOrderName) {
        ProductDAO productDAO = new ProductDAO();
        WorkProductionDAO workProductionDAO = new WorkProductionDAO();
        WorkOrderDAO workOrderDAO = new WorkOrderDAO();
        WorkOrderProductStringDAO workOrderProductStringDAO = new WorkOrderProductStringDAO();
        WorkOrderProductDAO workOrderProductDAO = new WorkOrderProductDAO();
        YearDAO yearDAO = new YearDAO();

        ObservableList<WorkProduction> workOrderList = FXCollections.observableArrayList(workProductionDAO.getWorkOrderList(workOrderDAO.getDataByName(workOrderName).getId(), workOrderDAO.getDataByName(workOrderName).getYear()));

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
                                workOrderProductDAO.getDataByID(workOrderProductStringDAO.getDataByName(_workOrder.getWorkOrderName()).getId()).getWork_order_id(), // work_order_product.work_order_id
                                workOrderDAO.getDataByName(_workOrder.getWorkOrderName()).getYear() // years.id
                        )
                );
                TreeItem<WorkProduction> _workOrderNode = new TreeItem<>(new WorkProduction(0, 0, null, null, _workOrder.getWorkOrderName(), null, null, 0, null, null, null, 0, 0, 0, 0, 0, 0, "", ""));
                if(productList.size() > 0) {
//                    System.out.println(productList.size());
                    for(WorkProduction _product : productList) {
//                        System.out.println("------------------------>" + j);
//                        System.out.println(workOrderDAO.getDataByName(_product.getWorkOrderName()).getYear()); // years.id
//                        System.out.println(workOrderProductDAO.getDataByID(workOrderProductStringDAO.getDataByName(_product.getWorkOrderName()).getId()).getWork_order_id()); // work_order_product.work_order_id
//                        System.out.println(workOrderProductDAO.getDataByID(workOrderProductStringDAO.getDataByName(_product.getWorkOrderName()).getId()).getProduct_id()); // work_order_product.product_id
//                        System.out.println(_product.getOrdinalNumbers()); // work_order_product.ordinal_num
                        ObservableList<WorkProduction> packagingList = FXCollections.observableArrayList(
                                workProductionDAO.getPackagingList(
                                        yearDAO.getYear(_product.getYear()).getId(), // years.id
                                        workOrderProductDAO.getDataByID(workOrderProductStringDAO.getDataByName(_workOrder.getWorkOrderName()).getId()).getWork_order_id(), // work_order_product.work_order_id
                                        productDAO.getDataByName(_product.getProductName()).getId(), // work_order_product.product_id
                                        _product.getOrdinalNumbers() // work_order_product.ordinal_num
                                )
                        );
//                        System.out.println(_product.getYear());
//                        System.out.println(workOrderProductDAO.getDataByID(workOrderProductStringDAO.getDataByName(_workOrder.getWorkOrderName()).getId()).getWork_order_id());
//                        System.out.println(productDAO.getDataByName(_product.getProductName()).getId());
//                        System.out.println(_product.getOrdinalNumbers());
                        TreeItem<WorkProduction> _productNode = new TreeItem<>(new WorkProduction(0, 0, null, null, "#"+_product.getOrdinalNumbers() + ": " + _product.getProductName(), null, null, 0, null, null, null, 0, 0, 0, 0, 0, 0, "", ""));
                        for(WorkProduction _packaging : packagingList) {
//                            System.out.println(_packaging.getOrdinalNumbers() + "<->" +_packaging.getPackagingName());
                            _productNode.getChildren().add(new TreeItem<>(_packaging));
                        }
//                        System.out.println("---------------------------------------------");

                        _workOrderNode.getChildren().add(_productNode);
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

    public static TreeTableColumn<WorkProduction, String> getWorkOrderNameColumn()
    {
        TreeTableColumn<WorkProduction, String> column = new TreeTableColumn<>("LSX");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("workOrderName"));
        return column;
    }

    public static TreeTableColumn<WorkProduction, String> getProductNameColumn()
    {
        TreeTableColumn<WorkProduction, String> column = new TreeTableColumn<>("Mặt hàng");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("productName"));
        column.setCellFactory(new Callback<TreeTableColumn<WorkProduction, String>, TreeTableCell<WorkProduction, String>>() {
            @Override
            public TreeTableCell<WorkProduction, String> call(TreeTableColumn<WorkProduction, String> workProductionStringTreeTableColumn) {
                TreeTableCell<WorkProduction, String> cell = new TreeTableCell<>();
                Text text = new Text();
                cell.setGraphic(text);
                cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
                text.wrappingWidthProperty().bind(cell.widthProperty());
                text.textProperty().bind(cell.itemProperty());
                return cell;
            }
        });
        return column;
    }

    public static TreeTableColumn<WorkProduction, String> getPackagingNameColumn(TreeTableView table)
    {
        TreeTableColumn<WorkProduction, String> column = new TreeTableColumn<>("Bao bì");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("packagingName"));
        column.prefWidthProperty().bind(table.widthProperty().multiply(0.3));
        column.setResizable(false);
        column.setCellFactory(new Callback<TreeTableColumn<WorkProduction, String>, TreeTableCell<WorkProduction, String>>() {
            @Override
            public TreeTableCell<WorkProduction, String> call(TreeTableColumn<WorkProduction, String> workProductionStringTreeTableColumn) {
                TreeTableCell<WorkProduction, String> cell = new TreeTableCell<>();
                Text text = new Text();
                cell.setGraphic(text);
                cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
                text.wrappingWidthProperty().bind(cell.widthProperty());
                text.textProperty().bind(cell.itemProperty());
                return cell;
            }
        });
        return column;
    }

    public static TreeTableColumn<WorkProduction, String> getPackagingSpecificationColumn(TreeTableView table)
    {
        TreeTableColumn<WorkProduction, String> column = new TreeTableColumn<>("Qui cách");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("packagingSpecification"));
        column.prefWidthProperty().bind(table.widthProperty().multiply(0.3));
        column.setResizable(false);
        column.setCellFactory(new Callback<TreeTableColumn<WorkProduction, String>, TreeTableCell<WorkProduction, String>>() {
            @Override
            public TreeTableCell<WorkProduction, String> call(TreeTableColumn<WorkProduction, String> workProductionStringTreeTableColumn) {
                TreeTableCell<WorkProduction, String> cell = new TreeTableCell<>();
                Text text = new Text();
                cell.setGraphic(text);
                cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
                text.wrappingWidthProperty().bind(cell.widthProperty());
                text.textProperty().bind(cell.itemProperty());
                return cell;
            }
        });
        return column;
    }

    public static TreeTableColumn<WorkProduction, String> getPackagingDimensionColumn(TreeTableView table)
    {
        TreeTableColumn<WorkProduction, String> column = new TreeTableColumn<>("Kích thước");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("packagingDimension"));
        column.prefWidthProperty().bind(table.widthProperty().multiply(0.10));
        column.setResizable(false);
        column.setCellFactory(new Callback<TreeTableColumn<WorkProduction, String>, TreeTableCell<WorkProduction, String>>() {
            @Override
            public TreeTableCell<WorkProduction, String> call(TreeTableColumn<WorkProduction, String> workProductionStringTreeTableColumn) {
                TreeTableCell<WorkProduction, String> cell = new TreeTableCell<>();
                Text text = new Text();
                cell.setGraphic(text);
                cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
                text.wrappingWidthProperty().bind(cell.widthProperty());
                text.textProperty().bind(cell.itemProperty());
                return cell;
            }
        });
        return column;
    }

    public static TreeTableColumn<WorkProduction, Supplier> getPackagingSuplierColumn()
    {
        TreeTableColumn<WorkProduction, Supplier> column = new TreeTableColumn<>("NCC");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("packagingSuplier"));
        return column;
    }

    public static TreeTableColumn<WorkProduction, String> getPackagingCodeColumn()
    {
        TreeTableColumn<WorkProduction, String> column = new TreeTableColumn<>("Mã BB");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("packagingCode"));
        column.setCellFactory(new Callback<TreeTableColumn<WorkProduction, String>, TreeTableCell<WorkProduction, String>>() {
            @Override
            public TreeTableCell<WorkProduction, String> call(TreeTableColumn<WorkProduction, String> workProductionStringTreeTableColumn) {
                TreeTableCell<WorkProduction, String> cell = new TreeTableCell<>();
                Text text = new Text();
                cell.setGraphic(text);
                cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
                text.wrappingWidthProperty().bind(cell.widthProperty());
                text.textProperty().bind(cell.itemProperty());
                return cell;
            }
        });
        return column;
    }

    public static TreeTableColumn<WorkProduction, String> getUnitColumn()
    {
        TreeTableColumn<WorkProduction, String> column = new TreeTableColumn<>("ĐVT");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("unit"));
        return column;
    }

    public static TreeTableColumn<WorkProduction, String> getPrintStatusColumn()
    {
        TreeTableColumn<WorkProduction, String> column = new TreeTableColumn<>("In sẵn");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("printStatus"));
        return column;
    }

    public static TreeTableColumn<WorkProduction, Integer> getPackQuantityColumn()
    {
        TreeTableColumn<WorkProduction, Integer> column = new TreeTableColumn<>("Đóng gói /thùng");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("packQuantity"));
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

    public static TreeTableColumn<WorkProduction, Float> getWorkOrderQuantityColumn()
    {
        TreeTableColumn<WorkProduction, Float> column = new TreeTableColumn<>("SL đặt");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("workOrderQuantity"));
        column.setCellFactory(new Callback<TreeTableColumn<WorkProduction, Float>, TreeTableCell<WorkProduction, Float>>() {
            @Override
            public TreeTableCell<WorkProduction, Float> call(TreeTableColumn<WorkProduction, Float> workProductionIntegerTreeTableColumn) {
                return new TreeTableCell<WorkProduction, Float>() {
                    @Override
                    protected void updateItem(Float value, boolean b) {
                        if (value != null) {
                            super.setText(value == 0?"":value.toString());
                        }
                    }
                };
            }
        });
        return column;
    }

    public static TreeTableColumn<WorkProduction, Float> getStockColumn()
    {
        TreeTableColumn<WorkProduction, Float> column = new TreeTableColumn<>("Tồn");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("Stock"));
        column.setCellFactory(new Callback<TreeTableColumn<WorkProduction, Float>, TreeTableCell<WorkProduction, Float>>() {
            @Override
            public TreeTableCell<WorkProduction, Float> call(TreeTableColumn<WorkProduction, Float> workProductionIntegerTreeTableColumn) {
                return new TreeTableCell<WorkProduction, Float>() {
                    @Override
                    protected void updateItem(Float value, boolean b) {
                        if (value != null) {
                            super.setText(value == 0?"":value.toString());
                        }
                    }
                };
            }
        });
        return column;
    }

    public static TreeTableColumn<WorkProduction, Float> getActualQuantityColumn()
    {
        TreeTableColumn<WorkProduction, Float> column = new TreeTableColumn<>("Thực đặt");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("actualQuantity"));
        column.setCellFactory(new Callback<TreeTableColumn<WorkProduction, Float>, TreeTableCell<WorkProduction, Float>>() {
            @Override
            public TreeTableCell<WorkProduction, Float> call(TreeTableColumn<WorkProduction, Float> workProductionIntegerTreeTableColumn) {
                return new TreeTableCell<WorkProduction, Float>() {
                    @Override
                    protected void updateItem(Float value, boolean b) {
                        if (value != null) {
                            super.setText(value == 0?"":value.toString());
                        }
                    }
                };
            }
        });
        return column;
    }

    public static TreeTableColumn<WorkProduction, Float> getResidualQuantityColumn()
    {
        TreeTableColumn<WorkProduction, Float> column = new TreeTableColumn<>("Hao hụt");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("residualQuantity"));
        column.setCellFactory(new Callback<TreeTableColumn<WorkProduction, Float>, TreeTableCell<WorkProduction, Float>>() {
            @Override
            public TreeTableCell<WorkProduction, Float> call(TreeTableColumn<WorkProduction, Float> workProductionIntegerTreeTableColumn) {
                return new TreeTableCell<WorkProduction, Float>() {
                    @Override
                    protected void updateItem(Float value, boolean b) {
                        if (value != null) {
                            super.setText(value == 0?"":value.toString());
                        }
                    }
                };
            }
        });
        return column;
    }

    public static TreeTableColumn<WorkProduction, Float> getTotalResidualQuantityColumn()
    {
        TreeTableColumn<WorkProduction, Float> column = new TreeTableColumn<>("Tổng đặt");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("totalResidualQuantity"));
        column.setCellFactory(new Callback<TreeTableColumn<WorkProduction, Float>, TreeTableCell<WorkProduction, Float>>() {
            @Override
            public TreeTableCell<WorkProduction, Float> call(TreeTableColumn<WorkProduction, Float> workProductionIntegerTreeTableColumn) {
                return new TreeTableCell<WorkProduction, Float>() {
                    @Override
                    protected void updateItem(Float value, boolean b) {
                        if (value != null) {
                            super.setText(value == 0?"":value.toString());
                        }
                    }
                };
            }
        });
        return column;
    }

    public static TreeTableColumn<WorkProduction, String> getNoteProductColumn()
    {
        TreeTableColumn<WorkProduction, String> column = new TreeTableColumn<>("Ghi chú");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("noteProduct"));
        return column;
    }

    public static TreeTableColumn<WorkProduction, Integer> getYearColumn()
    {
        TreeTableColumn<WorkProduction, Integer> column = new TreeTableColumn<>("Ghi chú");
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>("year"));
        return column;
    }
}
