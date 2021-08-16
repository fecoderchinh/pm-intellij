package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    private final String tableName = "customers";

    /**
     * Representing a database
     *
     * @param resultSet - A table of data representing a database result set
     * @return data
     * */
    private Customer createData(ResultSet resultSet) {
        Customer data = new Customer();
        try {
            data.setId(resultSet.getInt("id"));
            data.setName(resultSet.getString("name"));
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
    public List<Customer> getList() {
        List<Customer> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll = "select * from "+ tableName;
            ResultSet resultSet = statement.executeQuery(selectAll);
            while(resultSet.next()) {
                Customer data = createData(resultSet);
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
     * Updating record data
     *
     * @param column - table's column
     * @param value - column's new value
     * @param id - record's id
     * */
    public void updateData(String column, String value, int id) {
        jdbcDAO.updateSingleData(tableName, column, value, id);
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
     * @param name - column name
     * @param note - column note
     * @param id - column id
     * */
    public void update(String name, String note, int id) {
        String updateQuery = "update "+ tableName +" set name=?, note=? where id=?";
        preparedUpdateQuery(updateQuery, name, note, id);
    }

    /**
     * Inserting all columns
     *
     * @param name - column name
     * @param note - column note
     * */
    public void insert(String name, String note) {
        String insertQuery = "insert into "+ tableName +" (name, note) values(?,?)";
        preparedInsertQuery(insertQuery, name, note);
    }

    /**
     * Preparing Insert Query before action
     *
     * @param query - SQL query
     * @param name - column name
     * @param note - column note
     * */
    public void preparedInsertQuery(String query, String name, String note) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, note);

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
     * @param name - column name
     * @param note - column note
     * @param id - column id
     * */
    public void preparedUpdateQuery(String query, String name, String note, int id) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, note);
            preparedStatement.setInt(3, id);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();
        } catch (Exception ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
    }
}
