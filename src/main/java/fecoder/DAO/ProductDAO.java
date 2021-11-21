package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    private final String tableName = "products";

    /**
     * Representing a database
     *
     * @param resultSet - A table of data representing a database result set
     * @return data
     * */
    private Product createData(ResultSet resultSet) {
        Product data = new Product();
        try {
            data.setId(resultSet.getInt("id"));
            data.setName(resultSet.getString("name"));
            data.setDescription(resultSet.getString("description"));
            data.setSpecification(resultSet.getString("specification"));
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
    public List<Product> getList() {
        List<Product> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll = "select * from "+ tableName;
            ResultSet resultSet = statement.executeQuery(selectAll);
            while(resultSet.next()) {
                Product data = createData(resultSet);
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
     * Retrieving raw data
     *
     * @return data
     * */
    public Product getListRaw() {
        Product data = new Product();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll = "select * from "+tableName;
            ResultSet resultSet = statement.executeQuery(selectAll);
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
    public Product getDataByID(int id) {
        Product data = new Product();
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
     * Getting record data by its name
     *
     * @param value - record's name
     * @return data
     * */
    public Product getDataByName(String value) {
        Product data = new Product();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select * from "+ tableName +" where name=?");
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
     * Deleting record data
     *
     * @param id - record's id
     * */
    public void delete(int id) {
        jdbcDAO.delete(tableName, id);
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
     * Updating all columns
     *
     * @param name - column name
     * @param description - column description
     * @param specification - column specification
     * @param note - column note
     * @param id - column id
     * */
    public void update(String name, String description, String specification, String note, int id) {
        String updateQuery = "update "+ tableName +" set name=?, description=?, specification=?, note=? where id=?";
        preparedUpdateQuery(updateQuery, name, description, specification, note, id);
    }

    /**
     * Inserting all columns
     *
     * @param name - column name
     * @param description - column description
     * @param specification - column specification
     * @param note - column note
     * */
    public void insert(String name, String description, String specification, String note) {
        String insertQuery = "insert into "+ tableName +" (name, description, specification, note) values(?,?,?,?)";
        preparedInsertQuery(insertQuery, name, description, specification, note);
    }

    /**
     * Preparing Insert Query before action
     *
     * @param name - column name
     * @param description - column description
     * @param specification - column specification
     * @param note - column note
     * */
    public void preparedInsertQuery(String query, String name, String description, String specification, String note) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setString(3, specification);
            preparedStatement.setString(4, note);

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
     * @param name - column name
     * @param description - column description
     * @param specification - column specification
     * @param note - column note
     * @param id - column id
     * */
    public void preparedUpdateQuery(String query, String name, String description, String specification, String note, int id) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setString(3, specification);
            preparedStatement.setString(4, note);
            preparedStatement.setInt(5, id);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();
        } catch (Exception ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
    }
}
