package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.Packaging;
import fecoder.models.Type;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TypeDAO {

    private final String tableName = "types";

    /**
     * Representing a database
     *
     * @param resultSet - A table of data representing a database result set
     * @return data
     * */
    private Type createData(ResultSet resultSet) {
        Type data = new Type();
        try {
            data.setId(resultSet.getInt("id"));
            data.setName(resultSet.getString("name"));
            data.setUnit(resultSet.getString("unit"));
        } catch (SQLException ex) {
            fecoder.DAO.jdbcDAO.printSQLException(ex);
        }
        return data;
    }

    /**
     * Getting all records of table
     *
     * @return list
     * */
    public List<Type> getList() {
        List<Type> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll = "select * from "+tableName+ " order by name DESC";
            ResultSet resultSet = statement.executeQuery(selectAll);
            while(resultSet.next()) {
                Type data = createData(resultSet);
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
    public Type getDataByID(int id) {
        Type type = new Type();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select * from "+ tableName +" where id=?");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                type = createData(resultSet);
            }
            resultSet.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException | IOException ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
        return type;
    }

    /**
     * Getting lastest record data
     *
     * @return data
     * */
    public Type getLastestData() {
        Type data = new Type();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select * from "+ tableName +" order by id asc limit 1");
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
     * Getting record data by its name
     *
     * @param value - record's name
     * @return data
     * */
    public Type getDataByName(String value) {
        Type type = new Type();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select * from "+ tableName +" where name=?");
            preparedStatement.setString(1, value);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                type = createData(resultSet);
            }
            resultSet.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException | IOException ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
        return type;
    }

    /**
     * Determine the name exists
     *
     * @param value - the record's value
     * @return boolean
     * */
    public boolean hasName(String value) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select * from "+ tableName +" where name=?");
            preparedStatement.setString(1, value);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                return true;
            }
            resultSet.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException | IOException ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
        return false;
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
     * @param unit - column unit
     * @param id - column id
     * */
    public void update(String name, String unit, int id) {
        String updateQuery = "update "+ tableName +" set name=?, unit=? where id=?";
        preparedUpdateQuery(updateQuery, name, unit, id);
    }

    /**
     * Inserting all columns
     *
     * @param name - column name
     * @param unit - column unit
     * */
    public void insert(String name, String unit) {
        String insertQuery = "insert into "+ tableName +" (name, unit) values(?,?)";
        preparedInsertQuery(insertQuery, name, unit);
    }

    /**
     * Preparing Insert Query before action
     *
     * @param query - SQL query
     * @param name - column name
     * @param unit - column unit
     * */
    public void preparedInsertQuery(String query, String name, String unit) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, unit);

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
     * @param unit - column unit
     * @param id - column id
     * */
    public void preparedUpdateQuery(String query, String name, String unit, int id) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, unit);
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
