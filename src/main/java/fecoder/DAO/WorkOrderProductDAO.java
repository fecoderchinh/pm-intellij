package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.WorkOrderProduct;
import fecoder.models.WorkOrderProductPackaging;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkOrderProductDAO {
    private final String tableName = "work_order_product";

    /**
     * Representing a database
     *
     * @param resultSet - A table of data representing a database result set
     * @return data
     * */
    private static WorkOrderProduct createData(ResultSet resultSet) {
        WorkOrderProduct data = new WorkOrderProduct();
        try {
            data.setId(resultSet.getInt("id"));
            data.setWork_order_id(resultSet.getInt("work_order_id"));
            data.setOrdinal_num(resultSet.getFloat("ordinal_num"));
            data.setProduct_id(resultSet.getInt("product_id"));
            data.setQty(resultSet.getInt("qty"));
            data.setNote(resultSet.getString("note"));
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
    public List<WorkOrderProduct> getList() {
        List<WorkOrderProduct> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll = "Select * from "+tableName;
            ResultSet resultSet = statement.executeQuery(selectAll);
            while (resultSet.next()) {
                WorkOrderProduct data = createData(resultSet);
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
     * Getting all records of table
     *
     * @param work_order_id from work_order_product.work_order_id
     *
     * @return list
     * */
    public List<WorkOrderProduct> getListByID(int work_order_id) {
        List<WorkOrderProduct> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll = "Select * from "+tableName+" where work_order_id="+work_order_id;
            ResultSet resultSet = statement.executeQuery(selectAll);
            while (resultSet.next()) {
                WorkOrderProduct data = createData(resultSet);
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
    public WorkOrderProduct getDataByID(int id) {
        WorkOrderProduct data = new WorkOrderProduct();
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
     * Getting record data by its ID
     *
     * @param id - record id
     * @return data
     * */
    public WorkOrderProduct getDataByOrdinalNumber(String id) {
        WorkOrderProduct data = new WorkOrderProduct();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select * from "+ tableName +" where ordinal_num=?");
            preparedStatement.setString(1, id);
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
     * Inserting all columns
     *
     * @param workOrderID column work_order_id
     * @param ordinalNumber column ordinal_num
     * @param productID column product_id
     * @param quantity column qty
     * @param note column note
     * */
    public void insert(int workOrderID, String ordinalNumber, int productID, float quantity, String note) {
        String insertQuery = "insert into "+ tableName +" (work_order_id, ordinal_num, product_id, qty, note) values(?,?,?,?,?)";
        preparedInsertQuery(insertQuery, workOrderID, ordinalNumber, productID, quantity, note);
    }

    /**
     * Preparing Insert Query before action
     *
     * @param workOrderID column work_order_id
     * @param ordinalNumber column ordinal_num
     * @param productID column product_id
     * @param quantity column qty
     * @param note column note
     * */
    public void preparedInsertQuery(String query, int workOrderID, String ordinalNumber, int productID, float quantity, String note) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, workOrderID);
            preparedStatement.setString(2, ordinalNumber);
            preparedStatement.setInt(3, productID);
            preparedStatement.setFloat(4, quantity);
            preparedStatement.setString(5, note);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();
        } catch (Exception ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
    }

    /**
     * Preparing Update Query before action
     *
     * @param workOrderID column work_order_id
     * @param ordinalNumber column ordinal_num
     * @param productID column product_id
     * @param quantity column qty
     * @param note column note
     * @param id - column id
     * */
    public void update(int workOrderID, String ordinalNumber, int productID, float quantity, String note, int id) {
        String query = "update "+ tableName +" set work_order_id=?, ordinal_num=?, product_id=?, qty=?, note=? where id=?";
        preparedUpdateQuery(query, workOrderID, ordinalNumber, productID, quantity, note, id);
    }

    /**
     * Preparing Update Query before action
     *
     * @param workOrderID column work_order_id
     * @param ordinalNumber column ordinal_num
     * @param productID column product_id
     * @param quantity column qty
     * @param note column note
     * @param id - column id
     * */
    public void preparedUpdateQuery(String query, int workOrderID, String ordinalNumber, int productID, float quantity, String note, int id) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, workOrderID);
            preparedStatement.setString(2, ordinalNumber);
            preparedStatement.setInt(3, productID);
            preparedStatement.setFloat(4, quantity);
            preparedStatement.setString(5, note);
            preparedStatement.setInt(6, id);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();
        } catch (Exception ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
    }
}
