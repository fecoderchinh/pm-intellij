package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.Order;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    /**
     * Representing a database
     *
     * @param resultSet - A table of data representing a database result set
     * @return data
     * */
    private Order createData(ResultSet resultSet) {
        Order data = new Order();
        try {
            data.setRowNumber(resultSet.getInt("rowNumber"));
            data.setPackagingName(resultSet.getString("packagingName"));
            data.setWorkOrderName(resultSet.getString("workOrderName"));
            data.setSpecs(resultSet.getString("specs"));
            data.setDimension(resultSet.getString("dimension"));
            data.setUnit(resultSet.getString("unit"));
            data.setTotal(resultSet.getFloat("total"));
            data.setTotalResidualQuantity(resultSet.getFloat("totalResidualQuantity"));
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
    public List<Order> getList(String idList) {
        List<Order> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll =  "select " +
                    "distinct (@count := @count + 1) AS rowNumber, " +
                    "wo.name as workOrderName, " +
                    "p.name as packagingName, " +
                    "p.specifications as specs, " +
                    "p.dimension as dimension, " +
                    "t.unit as unit, " +
                    "wopp.actual_qty as total, " +
                    "if((wopp.actual_qty - wopp.residual_qty + wopp.stock - wopp.work_order_qty) > 0, (wopp.actual_qty - wopp.residual_qty + wopp.stock - wopp.work_order_qty), \"\") as totalResidualQuantity " +
                    "from " +
                    "work_order_product_packaging wopp, " +
                    "packaging p, " +
                    "supliers s, " +
                    "years y," +
                    "work_order wo," +
                    "types t," +
                    "packaging_product_size pps," +
                    "work_order_product wop " +
                    "CROSS JOIN (SELECT @count := 0) AS dummy " +
                    "where " +
                    "wopp.packaging_id = p.id " +
                    "and wopp.work_order_id = wo.id " +
                    "and wo.`year` = y.id " +
                    "and p.suplier = s.id " +
                    "and p.`type` = t.id " +
                    "and pps.product_id = wopp.product_id " +
                    "and wopp.wop_id = wop.id " +
                    "and wopp.work_order_id in (" + idList + ") "+
//                    "and y.id = " + year +
                    " and wopp.actual_qty > 0 group by wopp.id";
            ResultSet resultSet = statement.executeQuery(selectAll);
            while(resultSet.next()) {
                Order data = createData(resultSet);
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
     * @param idList list of work_order_product_packaging.work_order_id
     *
     * @return list
     * */
    public List<Order> getListOfWorkOrder(String idList) {
        List<Order> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll =  "select " +
                    "distinct (@count := @count + 1) AS rowNumber, " +
                    "wo.name as workOrderName, " +
                    "p.name as packagingName, " +
                    "p.specifications as specs, " +
                    "p.dimension as dimension, " +
                    "t.unit as unit, " +
                    "wopp.actual_qty as total, " +
                    "if((wopp.actual_qty - wopp.residual_qty + wopp.stock - wopp.work_order_qty) > 0, (wopp.actual_qty - wopp.residual_qty + wopp.stock - wopp.work_order_qty), \"\") as totalResidualQuantity " +
                    "from " +
                    "work_order_product_packaging wopp, " +
                    "packaging p, " +
                    "supliers s, " +
                    "years y," +
                    "work_order wo," +
                    "types t," +
                    "packaging_product_size pps," +
                    "work_order_product wop " +
                    "CROSS JOIN (SELECT @count := 0) AS dummy " +
                    "where " +
                    "wopp.packaging_id = p.id " +
                    "and wopp.work_order_id = wo.id " +
                    "and wo.`year` = y.id " +
                    "and p.suplier = s.id " +
                    "and p.`type` = t.id " +
                    "and pps.product_id = wopp.product_id " +
                    "and wopp.wop_id = wop.id " +
                    "and wopp.work_order_id in (" + idList + ") "+
//                    "and y.id = " + year +
                    " and wopp.actual_qty > 0 " +
                    " group by wopp.work_order_id";
            ResultSet resultSet = statement.executeQuery(selectAll);
            while(resultSet.next()) {
                Order data = createData(resultSet);
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
}
