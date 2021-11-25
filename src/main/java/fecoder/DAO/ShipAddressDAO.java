package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.ShipAddress;
import fecoder.models.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShipAddressDAO {

    private final String tableName = "ship_address";

    /**
     * Representing a database
     *
     * @param resultSet - A table of data representing a database result set
     * @return data
     * */
    private static ShipAddress createData(ResultSet resultSet) {
        ShipAddress data = new ShipAddress();
        try {
            data.setId(resultSet.getInt("id"));
            data.setName(resultSet.getString("name"));
            data.setAddress(resultSet.getString("address"));
            data.setCode_address(resultSet.getString("code_address"));
            data.setStocker(resultSet.getString("stocker"));
            data.setStocker_phone(resultSet.getString("stocker_phone"));
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
    public List<ShipAddress> getList() {
        List<ShipAddress> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll = "Select * from "+tableName+" order by id ASC";
            ResultSet resultSet = statement.executeQuery(selectAll);
            while (resultSet.next()) {
                ShipAddress data = createData(resultSet);
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
    public ShipAddress getDataByID(int id) {
        ShipAddress data = new ShipAddress();
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
     * @param code ship_address.code_address
     * @return data
     * */
    public ShipAddress getDataByCode(String code) {
        ShipAddress data = new ShipAddress();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select * from "+ tableName +" where code_address=?");
            preparedStatement.setString(1, code);
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
     * Updating record data
     *
     * @param column - table's column
     * @param value - column's new value
     * @param id - record's id
     * */
    public void updateData(String column, String value, int id) {
        jdbcDAO.updateSingleData(tableName, column, value, id+"");
    }

    /**
     * Updating record data
     *
     * @param column - table's column
     * @param value - column's new value
     * @param id - record's id
     * */
    public void updateDataInteger(String column, int value, int id) {
        jdbcDAO.updateSingleDataInteger(tableName, column, value, id);
    }

    /**
     * Updating record data
     *
     * @param column - table's column
     * @param value - column's new value
     * @param id - record's id
     * */
    public void updateDataBoolean(String column, boolean value, int id) {
        jdbcDAO.updateSingleDataBoolean(tableName, column, value, id);
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
     * Updating all columns
     *
     * @param name ship_address.name
     * @param address ship_address.address
     * @param code_address ship_address.code_address
     * @param stocker ship_address.stocker
     * @param stocker_phone ship_address.stocker_phone
     * @param id ship_address.id
     * */
    public void update(String name, String address, String code_address, String stocker, String stocker_phone, int id) {
        String updateQuery = "update "+ tableName +" set name=?, address=?, code_address=?, stocker=?, stocker_phone=? where id=?";
        preparedUpdateQuery(updateQuery, name, address, code_address, stocker, stocker_phone, id);
    }

    /**
     * Inserting all columns
     *
     * @param name ship_address.name
     * @param address ship_address.address
     * @param code_address ship_address.code_address
     * @param stocker ship_address.stocker
     * @param stocker_phone ship_address.stocker_phone
     * */
    public void insert(String name, String address, String code_address, String stocker, String stocker_phone) {
        String insertQuery = "insert into "+ tableName +" (name, address, code_address, stocker, stocker_phone) values(?,?,?,?,?)";
        preparedInsertQuery(insertQuery, name, address, code_address, stocker, stocker_phone);
    }

    /**
     * Preparing Insert Query before action
     *
     * @param query - SQL query
     * @param name ship_address.name
     * @param address ship_address.address
     * @param code_address ship_address.code_address
     * @param stocker ship_address.stocker
     * @param stocker_phone ship_address.stocker_phone
     * */
    public void preparedInsertQuery(String query, String name, String address, String code_address, String stocker, String stocker_phone) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, address);
            preparedStatement.setString(3, code_address);
            preparedStatement.setString(4, stocker);
            preparedStatement.setString(5, stocker_phone);

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
     * @param query - SQL query
     * @param name ship_address.name
     * @param address ship_address.address
     * @param code_address ship_address.code_address
     * @param stocker ship_address.stocker
     * @param stocker_phone ship_address.stocker_phone
     * @param id ship_address.id
     * */
    public void preparedUpdateQuery(String query, String name, String address, String code_address, String stocker, String stocker_phone, int id) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, address);
            preparedStatement.setString(3, code_address);
            preparedStatement.setString(4, stocker);
            preparedStatement.setString(5, stocker_phone);
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
