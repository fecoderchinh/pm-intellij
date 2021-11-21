package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.Packaging;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PackagingDAO {

    private final String tableName = "packaging";

    /**
     * Representing a database
     *
     * @param resultSet - A table of data representing a database result set
     * @return data
     * */
    private Packaging createData(ResultSet resultSet) {
        Packaging data = new Packaging();
        try {
            data.setId(resultSet.getInt("id"));
            data.setName(resultSet.getString("name"));
            data.setSpecifications(resultSet.getString("specifications"));
            data.setDimension(resultSet.getString("dimension"));
            data.setSuplier(resultSet.getInt("suplier"));
            data.setType(resultSet.getInt("type"));
            data.setMinimum_order(resultSet.getInt("minimum_order"));
            data.setStamped(resultSet.getBoolean("stamped"));
            data.setNote(resultSet.getString("note"));
            data.setMain(resultSet.getBoolean("main"));
            data.setCode(resultSet.getString("code"));
            data.setPrice(resultSet.getFloat("price"));
            data.setStock(resultSet.getFloat("stock"));
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
    public List<Packaging> getList() {
        List<Packaging> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll = "select * from "+tableName;
            ResultSet resultSet = statement.executeQuery(selectAll);
            while(resultSet.next()) {
                Packaging data = createData(resultSet);
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
    public Packaging getListRaw() {
        Packaging data = new Packaging();
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
    public Packaging getDataByID(int id) {
        Packaging data = new Packaging();
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
    public Packaging getDataByName(String value) {
        Packaging data = new Packaging();
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
     * Retrieving the data from specific column and string value
     *
     * @param column - the specific column
     * @param value - the value to lookup
     * @return boolean
     * */
    public boolean getDataByString(String column, String value) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select * from "+ tableName +" where "+column+"=?");
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
     * Updating record float data
     *
     * @param column - table's column
     * @param value - column's new value
     * @param id - record's id
     * */
    public void updateDataFloat(String column, float value, int id) {
        jdbcDAO.updateSingleDataFloat(tableName, column, value, id);
    }

    /**
     * Updating record boolean data
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
     * @param name - column name
     * @param specifications - column specifications
     * @param dimension - column dimension
     * @param suplier - column suplier
     * @param type - column type
     * @param minimum_order - column minimum_order
     * @param stamped - column stamped
     * @param code - column code
     * @param main - column main
     * @param note - column note
     * @param price - column price
     * @param id - column id
     * */
    public void update(String name, String specifications, String dimension, int suplier, int type, int minimum_order, boolean stamped, String code, boolean main, String note, float price, float stock, int id) {
        String updateQuery = "update "+ tableName +" set name=?, specifications=?, dimension=?, suplier=?, type=?, minimum_order=?, stamped=?, code=?, main=?, note=?, price=?, stock=? where id=?";
        preparedUpdateQuery(updateQuery, name, specifications, dimension, suplier, type, minimum_order, stamped, code, main, note, price, stock, id);
    }

    /**
     * Inserting all columns
     *
     * @param name - column name
     * @param specifications - column specifications
     * @param dimension - column dimension
     * @param suplier - column suplier
     * @param type - column type
     * @param minimum_order - column minimum_order
     * @param stamped - column stamped
     * @param code - column code
     * @param main - column main
     * @param note - column note
     * @param price - column price
     * @param stock - column stock
     * */
    public void insert(String name, String specifications, String dimension, int suplier, int type, int minimum_order, boolean stamped, String code, boolean main, String note, float price, float stock) {
        String insertQuery = "insert into "+ tableName +" (name, specifications, dimension, suplier, type, minimum_order, stamped, code, main, note, price, stock) values(?,?,?,?,?,?,?,?,?,?,?,?)";
        preparedInsertQuery(insertQuery, name, specifications, dimension, suplier, type, minimum_order, stamped, code, main, note, price, stock);
    }

    /**
     * Preparing Insert Query before action
     *
     * @param name - column name
     * @param specifications - column specifications
     * @param dimension - column dimension
     * @param suplier - column suplier
     * @param type - column type
     * @param minimum_order - column minimum_order
     * @param stamped - column stamped
     * @param code - column code
     * @param main - column main
     * @param note - column note
     * @param price - column price
     * @param stock - column stock
     * */
    public void preparedInsertQuery(String query, String name, String specifications, String dimension, int suplier, int type, int minimum_order, boolean stamped, String code, boolean main, String note, float price, float stock) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, specifications);
            preparedStatement.setString(3, dimension);
            preparedStatement.setInt(4, suplier);
            preparedStatement.setInt(5, type);
            preparedStatement.setInt(6, minimum_order);
            preparedStatement.setBoolean(7, stamped);
            preparedStatement.setString(8, code);
            preparedStatement.setBoolean(9, main);
            preparedStatement.setString(10, note);
            preparedStatement.setFloat(11, price);
            preparedStatement.setFloat(12, stock);

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
     * @param specifications - column specifications
     * @param dimension - column dimension
     * @param suplier - column suplier
     * @param type - column type
     * @param minimum_order - column minimum_order
     * @param stamped - column stamped
     * @param code - column code
     * @param main - column main
     * @param note - column note
     * @param price - column price
     * @param id - column id
     * */
    public void preparedUpdateQuery(String query, String name, String specifications, String dimension, int suplier, int type, int minimum_order, boolean stamped, String code, boolean main, String note, float price, float stock, int id) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, specifications);
            preparedStatement.setString(3, dimension);
            preparedStatement.setInt(4, suplier);
            preparedStatement.setInt(5, type);
            preparedStatement.setInt(6, minimum_order);
            preparedStatement.setBoolean(7, stamped);
            preparedStatement.setString(8, code);
            preparedStatement.setBoolean(9, main);
            preparedStatement.setString(10, note);
            preparedStatement.setFloat(11, price);
            preparedStatement.setFloat(12, stock);
            preparedStatement.setInt(13, id);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();
        } catch (Exception ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
    }
}
