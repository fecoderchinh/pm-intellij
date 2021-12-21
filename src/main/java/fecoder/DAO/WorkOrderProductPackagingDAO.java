package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.WorkOrder;
import fecoder.models.WorkOrderProductPackaging;
import fecoder.utils.Utils;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkOrderProductPackagingDAO {
    private final String tableName = "work_order_product_packaging";
    private final Utils utils = new Utils();

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
            data.setPrinted(resultSet.getString("printed"));
            data.setShip_address(resultSet.getInt("ship_address"));
            data.setOrder_times(resultSet.getInt("order_times"));
            data.setPackaging_custom_code(resultSet.getString("packaging_custom_code"));
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
        } catch (ClassNotFoundException | SQLException | IOException ex) {
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
        } catch (ClassNotFoundException | SQLException | IOException ex) {
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
     * Delete method, this will remove all data belong to product id
     * Run this method immediately after deleting work_order_product.id
     * -- do not use this method elsewhere without above action
     *
     * @param id work_order_product_packaging.wop_id
     * */
    public void deleteWOPPChildren(int id) {
        String query = "delete from work_order_product_packaging where wop_id in (?)";
        preparedDeleteWOPPChildren(query, id);
    }

    /**
     * Update method, this will update all data belong to product id
     * Run this method immediately after updating work_order_product.id
     * -- do not use this method elsewhere without above action
     *
     * @param product_id product_id
     * @param work_order_qty work_order_product.work_order_qty
     * @param wop_id work_order_product.id
     * */
    public void updateWOPPChildren(int product_id, float work_order_qty, int order_times, int wop_id) {
        String query = "update work_order_product_packaging wopp" +
                " join (" +
                " select pps.packaging_id as _id, pps.pack_qty as _pack_qty" +
                " from packaging_product_size pps" +
                " where pps.product_id=?" +
                " ) pps on wopp.packaging_id = pps._id" +
                " set wopp.work_order_qty = pps._pack_qty * ?, wopp.order_times=? where wopp.wop_id = ?";
        preparedUpdateWOPPChildren(query, product_id, work_order_qty, order_times, wop_id);
    }

    /**
     * */
    public void updateDataFromSheet(float stock, float actual_qty, String printed, int id) {
        String query = "update "+tableName+
                " set stock=?,actual_qty=?,printed=?"+
                " where id=?";
        preparedUpdateDataFromSheet(query, stock, actual_qty, printed, id);
    }

    /**
     * */
    private void preparedUpdateDataFromSheet(String query, float stock, float actual_qty, String printed, int id) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setFloat(1, stock);
            preparedStatement.setFloat(2, actual_qty);
            preparedStatement.setString(3, printed);
            preparedStatement.setInt(4, id);

            preparedStatement.executeUpdate();

//            System.out.println(preparedStatement);

            preparedStatement.close();
            conn.close();
        } catch (Exception ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
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

            System.out.println(preparedStatement);

            preparedStatement.close();
            conn.close();
        } catch (Exception ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
    }

    /**
     * Preparing Insert Query before action
     *
     * @param query query string
     * @param id work_order_product_packaging.wop_id
     * */
    public void preparedDeleteWOPPChildren(String query, int id) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();
        } catch (Exception ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
    }

    /**
     * Preparing Insert Query before action
     *
     * @param query query string
     * @param v1 work_order id
     * @param v2 work_order_product quantity
     * @param v3 product id
     * */
    public void preparedUpdateWOPPChildren(String query, int v1, float v2, int v3, int v4) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, v1);
            preparedStatement.setFloat(2, v2);
            preparedStatement.setInt(3, v3);
            preparedStatement.setInt(4, v4);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();
        } catch (Exception ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
    }

    /**
     * Updating record string data
     *
     * @param column work_order_product_packaging 's column
     * @param value work_order_product_packaging 's column > value
     * @param id work_order_product_packaging.id
     * */
    public void updateQuantity(String column, float value, int id) {
        jdbcDAO.updateSingleDataFloat(tableName, column, value, id);
    }

    /**
     *
     * Handle on updating specific Float property
     *
     * @param table work_order_product_packaging
     * @param column work_order_product_packaging 's column
     * @param value work_order_product_packaging 's column > value
     * @param wop_id work_order_product_packaging.wop_id
     * @param packaging_id work_order_product_packaging.packaging_id
     * @param id work_order_product_packaging.id
     * */
    public static void updateQuantityPrepareStatement(String table, String column, float value, int wop_id, int packaging_id, int id) {
        String query = "update "+ table +" set "+ column +"=? where wop_id=? and packaging_id=? and id=?";
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setFloat(1, value);
            preparedStatement.setInt(2, wop_id);
            preparedStatement.setInt(3, packaging_id);
            preparedStatement.setInt(4, id);
            preparedStatement.execute();
        } catch (Exception ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
    }

    /**
     * Updating record string data
     *
     * @param column - table's column
     * @param value - column's new value
     * @param id - record's id
     * */
    public void updateData(String column, String value, int id) {
        jdbcDAO.updateSingleData(tableName, column, value, id+"");
    }

    /**
     * Updating record string data
     *
     * @param column - table's column
     * @param value - column's new value
     * @param id - record's id
     * */
    public void updateDataInteger(String column, int value, int id) {
        jdbcDAO.updateSingleDataInteger(tableName, column, value, id);
    }
}
