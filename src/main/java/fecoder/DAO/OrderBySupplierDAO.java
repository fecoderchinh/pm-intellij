package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.Order;
import fecoder.models.OrderBySupllier;
import fecoder.utils.Utils;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrderBySupplierDAO {
    /**
     * Representing a database
     *
     * @param resultSet - A table of data representing a database result set
     * @return data
     * */
    private OrderBySupllier createData(ResultSet resultSet) {
        OrderBySupllier data = new OrderBySupllier();
        try {
            data.setWoppID(resultSet.getInt("woppID"));
            data.setWoID(resultSet.getInt("woID"));
            data.setWoName(resultSet.getString("woName"));
            data.setpName(resultSet.getString("pName"));
            data.setpIsPrinted(resultSet.getString("pIsPrinted"));
            data.setpSpecs(resultSet.getString("pSpecs"));
            data.setpDimension(resultSet.getString("pDimension"));
            data.setpUnit(resultSet.getString("pUnit"));
            data.setpDesireQuantity(resultSet.getFloat("pDesireQuantity"));
            data.setpTotal(resultSet.getFloat("pTotal"));
            data.setpStock(resultSet.getFloat("pStock"));
            data.setpResidualQuantity(resultSet.getFloat("pResidualQuantity"));
            data.setsCode(resultSet.getString("sCode"));
            data.setpCode(resultSet.getString("pCode"));
            data.setsName(resultSet.getString("sName"));
            data.setsAddress(resultSet.getString("sAddress"));
            data.setsDeputy(resultSet.getString("sDeputy"));
            data.setsPhone(resultSet.getString("sPhone"));
            data.setsFax(resultSet.getString("sFax"));
            data.setShipAddress(resultSet.getString("shipAddress"));
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
     * @param idList list of work_order_product_packaging.work_order_id
     *
     * @return list
     * */
    public List<OrderBySupllier> getList(String idList) {
        List<OrderBySupllier> list = new ArrayList<>();
        try {
            if(!idList.isEmpty()) {
                Connection conn = ConnectionUtils.getMyConnection();
                Statement statement = conn.createStatement();
                String selectAll =  "select" +
                        " wopp.id as woppID," +
                        " wo.id as woID," +
                        " group_concat(distinct wo.name separator \" +\") as woName, " +
                        " p.name as pName," +
                        " wopp.printed as pIsPrinted," +
                        " p.specifications as pSpecs," +
                        " p.dimension as pDimension," +
                        " t.unit as pUnit," +
                        " sum(wopp.work_order_qty) as pDesireQuantity," +
                        " sum(wopp.actual_qty) as pTotal," +
                        " sum(wopp.stock) as pStock," +
                        " sum(wopp.residual_qty) as pResidualQuantity," +
                        " s.code as sCode," +
                        " p.code as pCode," +
                        " s.address as sAddress," +
                        " s.deputy as sDeputy," +
                        " s.name as sName," +
                        " s.phone as sPhone," +
                        " s.fax as sFax, " +
                        " sa.code_address as shipAddress, " +
                        " wopp.order_times as orderTimes, " +
                        " wopp.packaging_custom_code as packagingCustomCode " +
                        "from " +
                        " work_order_product_packaging wopp," +
                        " work_order wo," +
                        " packaging p," +
                        " types t ," +
                        " supliers s, " +
                        " ship_address sa " +
                        "where " +
                        " wopp.work_order_id = wo.id" +
                        " and wopp.packaging_id = p.id" +
                        " and p.`type` = t.id" +
                        " and p.suplier = s.id" +
                        " and wopp.ship_address = sa.id" +
                        " and wopp.actual_qty > 0 " +
                        "and wopp.work_order_id in (" + idList + ") "+
                        "group by " +
                        " wopp.packaging_id";
                ResultSet resultSet = statement.executeQuery(selectAll);
                while(resultSet.next()) {
                    OrderBySupllier data = createData(resultSet);
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
     * @param idList list of work_order_product_packaging.work_order_id
     * @param order_times list of work_order_product_packaging.order_times
     *
     * @return list
     * */
    public List<OrderBySupllier> getListByOrderTimes(String idList, int order_times) {
        List<OrderBySupllier> list = new ArrayList<>();
        try {
            if(!idList.isEmpty()) {
                Connection conn = ConnectionUtils.getMyConnection();
                Statement statement = conn.createStatement();
                String selectAll =  "select" +
                        " wopp.id as woppID," +
                        " wo.id as woID," +
                        " group_concat(distinct wo.name separator \" +\") as woName, " +
                        " p.name as pName," +
                        " wopp.printed as pIsPrinted," +
                        " p.specifications as pSpecs," +
                        " p.dimension as pDimension," +
                        " t.unit as pUnit," +
                        " sum(wopp.work_order_qty) as pDesireQuantity," +
                        " sum(wopp.actual_qty) as pTotal," +
                        " sum(wopp.stock) as pStock," +
                        " sum(wopp.residual_qty) as pResidualQuantity," +
                        " s.code as sCode," +
                        " p.code as pCode," +
                        " s.address as sAddress," +
                        " s.deputy as sDeputy," +
                        " s.name as sName," +
                        " s.phone as sPhone," +
                        " s.fax as sFax, " +
                        " sa.code_address as shipAddress, " +
                        " wopp.order_times as orderTimes, " +
                        " wopp.packaging_custom_code as packagingCustomCode " +
                        "from " +
                        " work_order_product_packaging wopp," +
                        " work_order wo," +
                        " packaging p," +
                        " types t ," +
                        " supliers s, " +
                        " ship_address sa " +
                        "where " +
                        " wopp.work_order_id = wo.id" +
                        " and wopp.packaging_id = p.id" +
                        " and p.`type` = t.id" +
                        " and p.suplier = s.id" +
                        " and wopp.ship_address = sa.id" +
                        " and wopp.actual_qty > 0 " +
                        " and wopp.work_order_id in (" + idList + ") "+
                        " and wopp.order_times = " + order_times + " " +
                        "group by " +
                        " wopp.packaging_id, wopp.printed, wopp.packaging_custom_code";
                ResultSet resultSet = statement.executeQuery(selectAll);
                while(resultSet.next()) {
                    OrderBySupllier data = createData(resultSet);
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
     * @param idList list of work_order_product_packaging.work_order_id
     *
     * @return list
     * */
    public List<OrderBySupllier> getListGroupByShippingAddress(String idList) {
        List<OrderBySupllier> list = new ArrayList<>();
        try {
            if(!idList.isEmpty()) {
                Connection conn = ConnectionUtils.getMyConnection();
                Statement statement = conn.createStatement();
                String selectAll =  "select" +
                        " wopp.id as woppID," +
                        " wo.id as woID," +
                        " group_concat(distinct wo.name separator \" +\") as woName, " +
                        " p.name as pName," +
                        " wopp.printed as pIsPrinted," +
                        " p.specifications as pSpecs," +
                        " p.dimension as pDimension," +
                        " t.unit as pUnit," +
                        " sum(wopp.work_order_qty) as pDesireQuantity," +
                        " sum(wopp.actual_qty) as pTotal," +
                        " sum(wopp.stock) as pStock," +
                        " sum(wopp.residual_qty) as pResidualQuantity," +
                        " s.code as sCode," +
                        " p.code as pCode," +
                        " s.address as sAddress," +
                        " s.deputy as sDeputy," +
                        " s.name as sName," +
                        " s.phone as sPhone," +
                        " s.fax as sFax, " +
                        " sa.code_address as shipAddress, " +
                        " wopp.order_times as orderTimes, " +
                        " wopp.packaging_custom_code as packagingCustomCode " +
                        "from " +
                        " work_order_product_packaging wopp," +
                        " work_order wo," +
                        " packaging p," +
                        " types t ," +
                        " supliers s, " +
                        " ship_address sa " +
                        "where " +
                        " wopp.work_order_id = wo.id" +
                        " and wopp.packaging_id = p.id" +
                        " and p.`type` = t.id" +
                        " and p.suplier = s.id" +
                        " and wopp.ship_address = sa.id" +
                        " and wopp.actual_qty > 0 " +
                        "and wopp.work_order_id in (" + idList + ") "+
                        "group by " +
                        " sa.code_address";
                ResultSet resultSet = statement.executeQuery(selectAll);
                while(resultSet.next()) {
                    OrderBySupllier data = createData(resultSet);
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
     * @param idList list of work_order_product_packaging.work_order_id
     * @param code suppliers.code
     * @param shippingCode ship_address.code_address
     * @param order_times work_order_product_packaging.order_times
     *
     * @return list
     * */
    public List<OrderBySupllier> getListBySupplierCode(String idList, String code, String shippingCode, int order_times) {
        List<OrderBySupllier> list = new ArrayList<>();
        try {
            if(!idList.isEmpty()) {
                Connection conn = ConnectionUtils.getMyConnection();
                Statement statement = conn.createStatement();
                String selectAll =  "select" +
                        " wopp.id as woppID," +
                        " wo.id as woID," +
                        " group_concat(distinct wo.name separator \" +\") as woName, " +
                        " p.name as pName," +
                        " wopp.printed as pIsPrinted," +
                        " p.specifications as pSpecs," +
                        " p.dimension as pDimension," +
                        " t.unit as pUnit," +
                        " sum(wopp.work_order_qty) as pDesireQuantity," +
                        " sum(wopp.actual_qty) as pTotal," +
                        " sum(wopp.stock) as pStock," +
                        " sum(wopp.residual_qty) as pResidualQuantity," +
                        " s.code as sCode," +
                        " p.code as pCode," +
                        " s.address as sAddress," +
                        " s.deputy as sDeputy," +
                        " s.name as sName," +
                        " s.phone as sPhone," +
                        " s.fax as sFax, " +
                        " sa.code_address as shipAddress, " +
                        " wopp.order_times as orderTimes, " +
                        " wopp.packaging_custom_code as packagingCustomCode " +
                        "from " +
                        " work_order_product_packaging wopp," +
                        " work_order wo," +
                        " packaging p," +
                        " types t ," +
                        " supliers s, " +
                        " ship_address sa " +
                        "where " +
                        " wopp.work_order_id = wo.id" +
                        " and wopp.packaging_id = p.id" +
                        " and p.`type` = t.id" +
                        " and p.suplier = s.id" +
                        " and wopp.ship_address = sa.id" +
                        " and wopp.actual_qty > 0 " +
                        "and wopp.work_order_id in (" + idList + ") "+
                        " and s.code = '" + code + "' " +
                        " and sa.code_address = '" + shippingCode + "' " +
                        " and wopp.order_times = " + order_times + " " +
                        "group by " +
                        " wopp.packaging_id, sa.code_address, wopp.printed, wopp.packaging_custom_code";
                ResultSet resultSet = statement.executeQuery(selectAll);
                while(resultSet.next()) {
                    OrderBySupllier data = createData(resultSet);
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
}
