package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.Size;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SizeDAO {
    private final String tableName = "sizes";

    /**
     * Representing a database
     *
     * @param resultSet - A table of data representing a database result set
     * @return data
     * */
    private Size createData(ResultSet resultSet) {
        Size data = new Size();
        try {
            data.setId(resultSet.getInt("id"));
            data.setSize(resultSet.getString("size"));
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
    public List<Size> getList() {
        List<Size> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll = "select * from "+ tableName +" order by size";
            ResultSet resultSet = statement.executeQuery(selectAll);
            while(resultSet.next()) {
                Size data = createData(resultSet);
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
     * Getting record data by its name
     *
     * @param value - record's name
     * @return data
     * */
    public Size getDataByName(String value) {
        Size data = new Size();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select * from "+ tableName +" where size=?");
            preparedStatement.setString(1, value);
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
    public Size getDataByID(int id) {
        Size data = new Size();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select id,size from "+ tableName +" where id=?");
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
     * Updating record data
     *
     * @param column - table's column
     * @param size - column's new size
     * @param id - record's id
     * */
    public void updateData(String column, String size, int id) {
        jdbcDAO.updateSingleData(tableName, column, size, id);
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
     * @param size - column size
     * @param id - column id
     * */
    public void update(String size, int id) {
        String updateQuery = "update "+ tableName +" set size=? where id=?";
        preparedUpdateQuery(updateQuery, size, id);
    }

    /**
     * Inserting all columns
     *
     * @param size - column size
     * */
    public void insert(String size) {
        String insertQuery = "insert into "+ tableName +" (size) values(?)";
        preparedInsertQuery(insertQuery, size);
    }

    /**
     * Preparing Insert Query before action
     *
     * @param query - SQL query
     * @param size - column size
     * */
    public void preparedInsertQuery(String query, String size) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, size);

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
     * @param size - column size
     * @param id - column id
     * */
    public void preparedUpdateQuery(String query, String size, int id) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, size);
            preparedStatement.setInt(2, id);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();
        } catch (Exception ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
    }
}
