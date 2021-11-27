package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.PackagingOwner;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PackagingOwnerDAO {

    private final String tableName = "packaging_product_size";

    /**
     * Representing a database
     *
     * @param resultSet - A table of data representing a database result set
     * @return data
     * */
    private PackagingOwner createData(ResultSet resultSet) {
        PackagingOwner data = new PackagingOwner();
        try {
            data.setId(resultSet.getInt("id"));
            data.setProduct_id(resultSet.getInt("product_id"));
            data.setSize_id(resultSet.getInt("size_id"));
            data.setPackaging_id(resultSet.getInt("packaging_id"));
            data.setPack_qty(resultSet.getFloat("pack_qty"));
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
    public List<PackagingOwner> getList() {
        List<PackagingOwner> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll = "select * from "+ tableName +" order by product_id";
            ResultSet resultSet = statement.executeQuery(selectAll);
            while(resultSet.next()) {
                PackagingOwner data = createData(resultSet);
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
     * Retrieving column data by integer value
     *
     * @param column - specific column
     * @param value - value of column
     * @return data
     * */
    public PackagingOwner getDataByColumnIntegerValue(String column, int value) {
        PackagingOwner data = new PackagingOwner();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select * from "+ tableName +" where "+column+"=?");
            preparedStatement.setInt(1, value);
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
     * Getting record data by its ID
     *
     * @param id - record id
     * @return data
     * */
    public PackagingOwner getDataByID(int id) {
        PackagingOwner data = new PackagingOwner();
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
     * Updating record integer data
     *
     * @param column - table's column
     * @param value - column's new value
     * @param id - record's id
     * */
    public void updateDataInteger(String column, int value, int id) {
        jdbcDAO.updateSingleDataInteger(tableName, column, value, id);
    }

    /**
     * Updating record integer data
     *
     * @param column - table's column
     * @param value - column's new value
     * @param id - record's id
     * */
    public void updateDataFloat(String column, float value, int id) {
        jdbcDAO.updateSingleDataFloat(tableName, column, value, id);
    }

    /**
     * Updating record integer data
     *
     * @param column - table's column
     * @param value - column's new value
     * @param id - record's id
     * */
    public void updateData(String column, String value, int id) {
        jdbcDAO.updateSingleData(tableName, column, value, id+"");
    }

    /**
     * Updating all columns
     *
     * @param productID - column productID
     * @param sizeID - column sizeID
     * @param packagingID - column packagingID
     * @param packQty - column packQty
     * @param id - column id
     * */
    public void update(int productID, int sizeID, int packagingID, float packQty, String note, int id) {
        String updateQuery = "update "+ tableName +" set product_id=?, size_id=?, packaging_id=?, pack_qty=?, note=? where id=?";
        preparedUpdateQuery(updateQuery, productID, sizeID, packagingID, packQty, note, id);
    }

    /**
     * Inserting all columns
     *
     * @param productID - column productID
     * @param sizeID - column sizeID
     * @param packagingID - column packagingID
     * @param packQty - column packQty
     * */
    public void insert(int productID, int sizeID, int packagingID, float packQty, String note) {
        String insertQuery = "insert into "+ tableName +" (product_id, size_id, packaging_id, pack_qty, note) values(?,?,?,?,?)";
        preparedInsertQuery(insertQuery, productID, sizeID, packagingID, packQty, note);
    }

    /**
     * Preparing Insert Query before action
     *
     * @param productID - column productID
     * @param sizeID - column sizeID
     * @param packagingID - column packagingID
     * @param packQty - column packQty
     * */
    public void preparedInsertQuery(String query, int productID, int sizeID, int packagingID, float packQty, String note) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, productID);
            preparedStatement.setInt(2, sizeID);
            preparedStatement.setInt(3, packagingID);
            preparedStatement.setFloat(4, packQty);
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
     * @param productID - column productID
     * @param sizeID - column sizeID
     * @param packagingID - column packagingID
     * @param packQty - column packQty
     * @param id - column id
     * */
    public void preparedUpdateQuery(String query, int productID, int sizeID, int packagingID, float packQty, String note, int id) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, productID);
            preparedStatement.setInt(2, sizeID);
            preparedStatement.setInt(3, packagingID);
            preparedStatement.setFloat(4, packQty);
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
