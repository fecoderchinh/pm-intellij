package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.WorkProduction;
import fecoder.utils.Utils;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class WorkProductionDAO {

    /**
     * Representing a database
     *
     * @param resultSet - A table of data representing a database result set
     * @return data
     * */
    private WorkProduction createData(ResultSet resultSet) {
        WorkProduction data = new WorkProduction();
        try {
            data.setId(resultSet.getInt("id"));
            data.setOrdinalNumbers(resultSet.getString("ordinalNumbers"));
            data.setWoID(resultSet.getString("woID"));
            data.setWorkOrderName(resultSet.getString("workOrderName"));
            data.setProductName(resultSet.getString("productName"));
            data.setPackagingName(resultSet.getString("packagingName"));
            data.setPackagingSpecification(resultSet.getString("packagingSpecification"));
            data.setPackagingDimension(resultSet.getString("packagingDimension"));
            data.setPackagingSuplier(resultSet.getString("packagingSuplier"));
            data.setPackagingCode(resultSet.getString("packagingCode"));
            data.setUnit(resultSet.getString("unit"));
            data.setPrintStatus(resultSet.getString("printStatus"));
            data.setPackQuantity(resultSet.getInt("packQuantity"));
            data.setWorkOrderQuantity(resultSet.getString("workOrderQuantity"));
            data.setStock(resultSet.getString("stock"));
            data.setActualQuantity(resultSet.getString("actualQuantity"));
            data.setResidualQuantity(resultSet.getString("residualQuantity"));
            data.setTotalResidualQuantity(resultSet.getString("totalResidualQuantity"));
            data.setNoteProduct(resultSet.getString("noteProduct"));
            data.setOrderDate(resultSet.getString("orderDate"));
            data.setPrice(resultSet.getFloat("price"));
            data.setOrderTimes(resultSet.getString("orderTimes"));
            data.setPackagingCustomCode(resultSet.getString("packagingCustomCode"));
        } catch (SQLException ex) {
            jdbcDAO.printSQLException(ex);
        }
        return data;
    }

    /**
     * Getting all records of table
     *
     * @return list
     * */
    public List<WorkProduction> getList() {
        List<WorkProduction> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll =  "select distinct " +
                    " wopp.id as id, " +
                    " wop.ordinal_num as ordinalNumbers, " +
                    " wop.id as woID, " + // new
                    " wo.name as workOrderName, " +
                    " p.name as productName, " +
                    " p2.name as packagingName, " +
                    " p2.specifications as packagingSpecification, " +
                    " p2.dimension as packagingDimension, " +
                    " s.code as packagingSuplier, " +
                    " p2.code as packagingCode, " +
                    " t.unit as unit, " +
                    " wopp.printed as printStatus, " +
                    " pps.pack_qty as packQuantity, " +
                    " (pps.pack_qty * wop.qty) as workOrderQuantity, " +
                    " wopp.stock as stock, " +
                    " wopp.actual_qty as actualQuantity, " +
                    " wopp.residual_qty as residualQuantity, " +
                    " (wopp.actual_qty + wopp.stock - wopp.residual_qty - (pps.pack_qty * wop.qty)) as totalResidualQuantity, " +
                    " wopp.note as noteProduct, " +
                    " wo.order_date as orderDate, " +
                    " p2.price as price, " +
                    " wop.order_times as orderTimes, " +
                    " wopp.packaging_custom_code as packagingCustomCode " +
                    "from " +
                    " work_order_product wop," +
                    " work_order wo," +
                    " products p," +
                    " packaging p2," +
                    " types t," +
                    " supliers s," +
                    " packaging_product_size pps," +
                    " work_order_product_packaging wopp " +
                    "where" +
                    " wop.work_order_id = wo.id" +
                    " and wop.product_id = p.id" +
                    " and p2.`type` = t.id" +
                    " and p2.suplier = s.id " +
                    " and pps.product_id = p.id " +
                    " and pps.packaging_id = p2.id" +
                    " and wopp.wop_id = wop.id" +
                    " and wopp.work_order_id = wo.id" +
                    " and wopp.product_id = p.id" +
                    " and wopp.packaging_id = p2.id" +
                    " group by wopp.id" +
                    " order by orderDate";
            ResultSet resultSet = statement.executeQuery(selectAll);
            while(resultSet.next()) {
                WorkProduction data = createData(resultSet);
                list.add(data);
            }
            resultSet.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException | IOException ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
        return list;
    }

    /**
     * Getting all records of table
     *
     * @return list
     * */
    public List<WorkProduction> getListForStats() {
        List<WorkProduction> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll =  "select distinct " +
                    " wopp.id as id, " +
                    " wop.ordinal_num as ordinalNumbers, " +
                    " wop.id as woID, " + // new
                    " wo.name as workOrderName, " +
                    " p.name as productName, " +
                    " concat(p2.name, if(wopp.printed is null, '', concat( '(',wopp.printed ,')' ) ) ) as packagingName, " +
                    " p2.specifications as packagingSpecification, " +
                    " p2.dimension as packagingDimension, " +
                    " s.code as packagingSuplier, " +
                    " p2.code as packagingCode, " +
                    " t.unit as unit, " +
                    " wopp.printed as printStatus, " +
                    " pps.pack_qty as packQuantity, " +
                    " (pps.pack_qty * wop.qty) as workOrderQuantity, " +
                    " wopp.stock as stock, " +
                    " wopp.actual_qty as actualQuantity, " +
                    " wopp.residual_qty as residualQuantity, " +
                    " (wopp.actual_qty + wopp.stock - wopp.residual_qty - (pps.pack_qty * wop.qty)) as totalResidualQuantity, " +
                    " wopp.note as noteProduct, " +
                    " wo.order_date as orderDate, " +
                    " p2.price as price, " +
                    " wop.order_times as orderTimes, " +
                    " wopp.packaging_custom_code as packagingCustomCode " +
                    "from " +
                    " work_order_product wop," +
                    " work_order wo," +
                    " products p," +
                    " packaging p2," +
                    " types t," +
                    " supliers s," +
                    " packaging_product_size pps," +
                    " work_order_product_packaging wopp " +
                    "where" +
                    " wop.work_order_id = wo.id" +
                    " and wop.product_id = p.id" +
                    " and p2.`type` = t.id" +
                    " and p2.suplier = s.id " +
                    " and pps.product_id = p.id " +
                    " and pps.packaging_id = p2.id" +
                    " and wopp.wop_id = wop.id" +
                    " and wopp.work_order_id = wo.id" +
                    " and wopp.product_id = p.id" +
                    " and wopp.packaging_id = p2.id" +
                    " group by wopp.id" +
                    " order by orderDate";
            ResultSet resultSet = statement.executeQuery(selectAll);
            while(resultSet.next()) {
                WorkProduction data = createData(resultSet);
                list.add(data);
            }
            resultSet.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException | IOException ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
        return list;
    }

    /**
     * Getting all records of table
     *
     * @param workOrderID the id of work order
     *
     * @return list
     * */
    public List<WorkProduction> getListByID(int workOrderID) {
        List<WorkProduction> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll =  "select distinct " +
                    " wopp.id as id, " +
                    " wop.ordinal_num as ordinalNumbers, " +
                    " wop.id as woID, " + // new
                    " wo.name as workOrderName, " +
                    " p.name as productName, " +
                    " p2.name as packagingName, " +
                    " p2.specifications as packagingSpecification, " +
                    " p2.dimension as packagingDimension, " +
                    " s.code as packagingSuplier, " +
                    " p2.code as packagingCode, " +
                    " t.unit as unit, " +
                    " wopp.printed as printStatus, " +
                    " pps.pack_qty as packQuantity, " +
                    " (pps.pack_qty * wop.qty) as workOrderQuantity, " +
                    " wopp.stock as stock, " +
                    " wopp.actual_qty as actualQuantity, " +
                    " wopp.residual_qty as residualQuantity, " +
                    " (wopp.actual_qty + wopp.stock - wopp.residual_qty - (pps.pack_qty * wop.qty)) as totalResidualQuantity, " +
                    " wopp.note as noteProduct, " +
                    " wo.order_date as orderDate, " +
                    " p2.price as price, " +
                    " wop.order_times as orderTimes, " +
                    " wopp.packaging_custom_code as packagingCustomCode " +
                    "from " +
                    " work_order_product wop," +
                    " work_order wo," +
                    " products p," +
                    " packaging p2," +
                    " types t," +
                    " supliers s," +
                    " packaging_product_size pps," +
                    " work_order_product_packaging wopp " +
                    "where" +
                    " wop.work_order_id = wo.id" +
                    " and wop.product_id = p.id" +
                    " and p2.`type` = t.id" +
                    " and p2.suplier = s.id " +
                    " and pps.product_id = p.id " +
                    " and pps.packaging_id = p2.id" +
                    " and wopp.wop_id = wop.id" +
                    " and wopp.work_order_id = wo.id" +
                    " and wopp.product_id = p.id" +
                    " and wopp.packaging_id = p2.id" +
                    " and wo.id = " + workOrderID +
                    " group by wopp.id" +
                    " order by wop.ordinal_num";
            ResultSet resultSet = statement.executeQuery(selectAll);
            while(resultSet.next()) {
                WorkProduction data = createData(resultSet);
                list.add(data);
            }
            resultSet.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException | IOException ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
        return list;
    }

    /**
     * Getting all records of table
     *
     * @param work_order_id the id of work_order_product.work_order_id
     *
     * @return list
     * */
    public List<WorkProduction> getWorkOrderList(String work_order_id) {
        List<WorkProduction> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll =  "select distinct " +
                    " wopp.id as id, " +
                    " wop.ordinal_num as ordinalNumbers, " +
                    " wop.id as woID, " + // new
                    " wo.name as workOrderName, " +
                    " p.name as productName, " +
                    " p2.name as packagingName, " +
                    " p2.specifications as packagingSpecification, " +
                    " p2.dimension as packagingDimension, " +
                    " s.code as packagingSuplier, " +
                    " p2.code as packagingCode, " +
                    " t.unit as unit, " +
                    " wopp.printed as printStatus, " +
                    " pps.pack_qty as packQuantity, " +
                    " (pps.pack_qty * wop.qty) as workOrderQuantity, " +
                    " wopp.stock as stock, " +
                    " wopp.actual_qty as actualQuantity, " +
                    " wopp.residual_qty as residualQuantity, " +
                    " (wopp.actual_qty + wopp.stock - wopp.residual_qty - (pps.pack_qty * wop.qty)) as totalResidualQuantity, " +
                    " wopp.note as noteProduct, " +
                    " wo.order_date as orderDate, " +
                    " p2.price as price, " +
                    " wop.order_times as orderTimes, " +
                    " wopp.packaging_custom_code as packagingCustomCode " +
                    "from " +
                    " work_order_product wop," +
                    " work_order wo," +
                    " products p," +
                    " packaging p2," +
                    " types t," +
                    " supliers s," +
                    " packaging_product_size pps," +
                    " work_order_product_packaging wopp " +
                    "where" +
                    " wop.work_order_id = wo.id" +
                    " and wop.product_id = p.id" +
                    " and p2.`type` = t.id" +
                    " and p2.suplier = s.id " +
                    " and pps.product_id = p.id " +
                    " and pps.packaging_id = p2.id" +
                    " and wopp.wop_id = wop.id" +
                    " and wopp.work_order_id = wo.id" +
                    " and wopp.product_id = p.id" +
                    " and wopp.packaging_id = p2.id" +
                    " and wo.id in (" + work_order_id + ")" +
                    " group by wo.id" +
                    " order by wop.ordinal_num";
            ResultSet resultSet = statement.executeQuery(selectAll);
            while(resultSet.next()) {
                WorkProduction data = createData(resultSet);
                list.add(data);
            }
            resultSet.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException | IOException ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
        return list;
    }

    /**
     * Getting all records of table
     *
     * @param work_order_id the id of work_order_product.work_order_id
     *
     * @return list
     * */
    public List<WorkProduction> getProductList(String work_order_id) {
        List<WorkProduction> list = new ArrayList<>();
        try {
            if(!work_order_id.isEmpty()) {
                Connection conn = ConnectionUtils.getMyConnection();
                Statement statement = conn.createStatement();
                String selectAll =  "select distinct " +
                        " wopp.id as id, " +
                        " wop.ordinal_num as ordinalNumbers, " +
                        " wop.id as woID, " + // new
                        " wo.name as workOrderName, " +
                        " p.name as productName, " +
                        " p2.name as packagingName, " +
                        " p2.specifications as packagingSpecification, " +
                        " p2.dimension as packagingDimension, " +
                        " s.code as packagingSuplier, " +
                        " p2.code as packagingCode, " +
                        " t.unit as unit, " +
                        " wopp.printed as printStatus, " +
                        " pps.pack_qty as packQuantity, " +
                        " (pps.pack_qty * wop.qty) as workOrderQuantity, " +
                        " wopp.stock as stock, " +
                        " wopp.actual_qty as actualQuantity, " +
                        " wopp.residual_qty as residualQuantity, " +
                        " (wopp.actual_qty + wopp.stock - wopp.residual_qty - (pps.pack_qty * wop.qty)) as totalResidualQuantity, " +
                        " wopp.note as noteProduct, " +
                        " wo.order_date as orderDate, " +
                        " p2.price as price, " +
                        " wop.order_times as orderTimes, " +
                        " wopp.packaging_custom_code as packagingCustomCode " +
                        "from " +
                        " work_order_product wop," +
                        " work_order wo," +
                        " products p," +
                        " packaging p2," +
                        " types t," +
                        " supliers s," +
                        " packaging_product_size pps," +
                        " work_order_product_packaging wopp " +
                        "where" +
                        " wop.work_order_id = wo.id" +
                        " and wop.product_id = p.id" +
                        " and p2.`type` = t.id" +
                        " and p2.suplier = s.id " +
                        " and pps.product_id = p.id " +
                        " and pps.packaging_id = p2.id" +
                        " and wopp.wop_id = wop.id" +
                        " and wopp.work_order_id = wo.id" +
                        " and wopp.product_id = p.id" +
                        " and wopp.packaging_id = p2.id" +
                        " and wo.id in (" + work_order_id + ")" +
                        " group by wop.id" +
                        " order by wop.ordinal_num";
                ResultSet resultSet = statement.executeQuery(selectAll);
                while(resultSet.next()) {
                    WorkProduction data = createData(resultSet);
                    list.add(data);
                }
                resultSet.close();
                conn.close();
            } else {
                Utils utils = new Utils();
                utils.alert("err", Alert.AlertType.ERROR, "Lỗi", "Chưa chọn LSX").showAndWait();
            }
        } catch (ClassNotFoundException | SQLException | IOException ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
        return list;
    }

    /**
     * Getting all records of table
     *
     * @param work_order_id the id of work_order_product.work_order_id
     * @param product_id the id of work_order_product.product_id
     * @param ordinal_num the id of work_order_product.ordinal_num
     * @param ordinal_times the id of work_order_product.ordinal_times
     *
     * @return list
     * */
    public List<WorkProduction> getPackagingList(String work_order_id, int product_id, String ordinal_num, String ordinal_times) {
        List<WorkProduction> list = new ArrayList<>();
        try {
            if(work_order_id.isEmpty() || ordinal_num.isEmpty() || product_id == 0) {
                Utils utils = new Utils();
                utils.alert("err", Alert.AlertType.ERROR, "Lỗi", "Không thể trích xuất dữ liệu.").showAndWait();
            } else {
                Connection conn = ConnectionUtils.getMyConnection();
                Statement statement = conn.createStatement();
                String selectAll =  "select distinct " +
                        " wopp.id as id, " +
                        " wop.ordinal_num as ordinalNumbers, " +
                        " wop.id as woID, " + // new
                        " wo.name as workOrderName, " +
                        " p.name as productName, " +
                        " p2.name as packagingName, " +
                        " p2.specifications as packagingSpecification, " +
                        " p2.dimension as packagingDimension, " +
                        " s.code as packagingSuplier, " +
                        " p2.code as packagingCode, " +
                        " t.unit as unit, " +
                        " wopp.printed as printStatus, " +
                        " pps.pack_qty as packQuantity, " +
                        " (pps.pack_qty * wop.qty) as workOrderQuantity, " +
                        " wopp.stock as stock, " +
                        " wopp.actual_qty as actualQuantity, " +
                        " wopp.residual_qty as residualQuantity, " +
                        " (wopp.actual_qty + wopp.stock - wopp.residual_qty - (pps.pack_qty * wop.qty)) as totalResidualQuantity, " +
                        " wopp.note as noteProduct, " +
                        " wo.order_date as orderDate, " +
                        " p2.price as price, " +
                        " wop.order_times as orderTimes, " +
                        " wopp.packaging_custom_code as packagingCustomCode " +
                        "from " +
                        " work_order_product wop," +
                        " work_order wo," +
                        " products p," +
                        " packaging p2," +
                        " types t," +
                        " supliers s," +
                        " packaging_product_size pps," +
                        " work_order_product_packaging wopp " +
                        "where" +
                        " wop.work_order_id = wo.id" +
                        " and wop.product_id = p.id" +
                        " and p2.`type` = t.id" +
                        " and p2.suplier = s.id " +
                        " and pps.product_id = p.id " +
                        " and pps.packaging_id = p2.id" +
                        " and wopp.wop_id = wop.id" +
                        " and wopp.work_order_id = wo.id" +
                        " and wopp.product_id = p.id" +
                        " and wopp.packaging_id = p2.id" +
                        " and wo.id in (" + work_order_id + ")" +
                        " and p.id = " + product_id +
                        " and wop.ordinal_num = " + ordinal_num +
                        " and wop.order_times = " + ordinal_times +
                        " group by wopp.id" +
                        " order by s.code ASC";
                ResultSet resultSet = statement.executeQuery(selectAll);
                while(resultSet.next()) {
                    WorkProduction data = createData(resultSet);
                    list.add(data);
                }
                resultSet.close();
                conn.close();
            }
        } catch (ClassNotFoundException | SQLException | IOException ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
        return list;
    }
}
