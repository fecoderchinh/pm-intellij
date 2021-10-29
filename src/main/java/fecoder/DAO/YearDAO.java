package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.Year;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class YearDAO {

    private final String tableName = "years";

    /**
     * Representing a database
     *
     * @param resultSet - A table of data representing a database result set
     * @return data
     * */
    private Year createData(ResultSet resultSet) {
        Year data = new Year();
        try {
            data.setId(resultSet.getInt("id"));
            data.setYear(resultSet.getString("year"));
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
    public List<Year> getList() {
        List<Year> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll = "select * from "+tableName;
            ResultSet resultSet = statement.executeQuery(selectAll);
            while(resultSet.next()) {
                Year data = createData(resultSet);
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
    public Year getDataByID(int id) {
        Year data = new Year();
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
     * Getting record data by its varchar
     *
     * @param year - record value
     * @return data
     * */
    public Year getYear(String year) {
        Year data = new Year();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select * from "+ tableName +" where year=?");
            preparedStatement.setString(1, year);
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
     * Check if year exist
     *
     * @param value - the record's value
     * @return boolean
     * */
    public boolean has(String value) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select * from "+ tableName +" where year=?");
            preparedStatement.setString(1, value);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                return true;
            }
            resultSet.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
        return false;
    }

    /**
     * Updating record data
     *
     * @param column - table's column
     * @param year - column's new year
     * @param id - record's id
     * */
    public void updateData(String column, String year, int id) {
        jdbcDAO.updateSingleData(tableName, column, year, id);
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
     * @param year - column year
     * @param id - column id
     * */
    public void update(String year, int id) {
        String updateQuery = "update "+ tableName +" set year=? where id=?";
        preparedUpdateQuery(updateQuery, year, id);
    }

    /**
     * Inserting all columns
     *
     * @param year - column year
     * */
    public void insert(String year) {
        String insertQuery = "insert into "+ tableName +" (year) values(?)";
        preparedInsertQuery(insertQuery, year);
    }

    /**
     * Preparing Insert Query before action
     *
     * @param query - SQL query
     * @param year - column year
     * */
    public void preparedInsertQuery(String query, String year) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, year);

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
     * @param year - column year
     * @param id - column id
     * */
    public void preparedUpdateQuery(String query, String year, int id) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, year);
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
