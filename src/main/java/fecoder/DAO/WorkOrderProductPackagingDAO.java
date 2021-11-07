package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.WorkOrder;
import fecoder.models.WorkOrderProductPackaging;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkOrderProductPackagingDAO {
    private final String tableName = "work_order_product_packaging";

    /**
     * Representing a database
     *
     * @param resultSet - A table of data representing a database result set
     * @return data
     * */
    private static WorkOrderProductPackaging createData(ResultSet resultSet) {
        WorkOrderProductPackaging data = new WorkOrderProductPackaging();
        try {
            data.setId(resultSet.getInt("id"));
            data.setWop_id(resultSet.getInt("wop_id"));
            data.setWork_order_id(resultSet.getInt("work_order_id"));
            data.setProduct_id(resultSet.getInt("product_id"));
            data.setPackaging_id(resultSet.getInt("packaging_id"));
            data.setWork_order_qty(resultSet.getFloat("work_order_qty"));
            data.setStock(resultSet.getFloat("stock"));
            data.setActual_qty(resultSet.getFloat("actual_qty"));
            data.setResidual_qty(resultSet.getFloat("residual_qty"));
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
    public List<WorkOrderProductPackaging> getList() {
        List<WorkOrderProductPackaging> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll = "Select * from "+tableName;
            ResultSet resultSet = statement.executeQuery(selectAll);
            while (resultSet.next()) {
                WorkOrderProductPackaging data = createData(resultSet);
                list.add(data);
            }
            resultSet.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }

        return list;
    }

    /**
     * Getting record data by its ID
     *
     * @param id - record id
     * @return data
     * */
    public WorkOrderProductPackaging getDataByID(int id) {
        WorkOrderProductPackaging data = new WorkOrderProductPackaging();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select * from "+ tableName +" where id=?");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                data = createData(resultSet);
            }
            resultSet.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
        return data;
    }

    /**
     * Deleting record data
     *
     * @param id - record's id
     * */
    public void delete(int id) {
        jdbcDAO.delete(tableName, id);
    }

    /**
     * Inserting data based on work_order_product table insertion
     * -- be careful whenever using this function without work_order_product inserted first
     * -- wop_id might not correct
     *
     * @param work_order_id work_order id
     * @param product_id product id
     * @param wop_qty work_order_product quantity
     * */
    public void insertBasedOnWOPLatestData(int work_order_id, int product_id, float wop_qty) {
        String query = "insert into work_order_product_packaging(wop_id, work_order_id, product_id, packaging_id, work_order_qty, stock, actual_qty, residual_qty)" +
                " select (select id from work_order_product where id = (SELECT LAST_INSERT_ID())), ?, ?, pps.packaging_id, pps.pack_qty * ?,0,0,0" +
                " from packaging_product_size pps" +
                " where pps.product_id = ?";
        preparedInsertQueryBasedOnWOPData(query, work_order_id, product_id, wop_qty);
    }

    /**
     * Preparing Insert Query before action
     *
     * @param query query string
     * @param work_order_id work_order id
     * @param product_id product id
     * @param wop_qty work_order_product quantity
     * */
    public void preparedInsertQueryBasedOnWOPData(String query, int work_order_id, int product_id, float wop_qty) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, work_order_id);
            preparedStatement.setInt(2, product_id);
            preparedStatement.setFloat(3, wop_qty);
            preparedStatement.setInt(4, product_id);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();
        } catch (Exception ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
    }
}
