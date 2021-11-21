package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {

    private final String tableName = "supliers";

    /**
     * Representing a database
     *
     * @param resultSet - A table of data representing a database result set
     * @return data
     * */
    private static Supplier createData(ResultSet resultSet) {
        Supplier data = new Supplier();
        try {
            data.setId(resultSet.getInt("id"));
            data.setName(resultSet.getString("name"));
            data.setAddress(resultSet.getString("address"));
            data.setEmail(resultSet.getString("email"));
            data.setDeputy(resultSet.getString("deputy"));
            data.setPhone(resultSet.getString("phone"));
            data.setFax(resultSet.getString("fax"));
            data.setCode(resultSet.getString("code"));
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
    public List<Supplier> getList() {
        List<Supplier> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll = "Select * from "+tableName;
            ResultSet resultSet = statement.executeQuery(selectAll);
            while (resultSet.next()) {
                Supplier data = createData(resultSet);
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
    public Supplier getDataByID(int id) {
        Supplier data = new Supplier();
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
    public Supplier getDataByName(String value) {
        Supplier data = new Supplier();
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
     * Getting record data by its code
     *
     * @param code suppliers.code
     * @return data
     * */
    public Supplier getDataByCode(String code) {
        Supplier data = new Supplier();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select * from "+ tableName +" where code=? order by id desc limit 1");
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
     * Getting all records by its id
     *
     * @param id - the record's id
     * @return list
     * */
    public List<Supplier> getRow(int id) {
        String query = "select * from "+ tableName +" where id=?";
        List<Supplier> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                Supplier data = createData(resultSet);
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
     * Updating all columns
     *
     * @param name - column name
     * @param address - column address
     * @param email - column email
     * @param deputy - column deputy
     * @param phone - column phone
     * @param fax - column fax
     * @param code - column code
     * @param id - column id
     * */
    public void update(String name, String address, String email, String deputy, String phone, String fax, String code, int id) {
        String updateQuery = "update "+ tableName +" set name=?, address=?, email=?, deputy=?, phone=?, fax=?, code=? where id=?";
        preparedUpdateQuery(updateQuery, name, address, email, deputy, phone, fax, code, id);
    }

    /**
     * Inserting all columns
     *
     * @param name - column name
     * @param address - column address
     * @param email - column email
     * @param deputy - column deputy
     * @param phone - column phone
     * @param fax - column fax
     * @param code - column code
     * */
    public void insert(String name, String address, String email, String deputy, String phone, String fax, String code) {
        String insertQuery = "insert into "+ tableName +" (name, address, email, deputy, phone, fax, code) values(?,?,?,?,?,?,?)";
        preparedInsertQuery(insertQuery, name, address, email, deputy, phone, fax, code);
    }

    /**
     * Preparing Insert Query before action
     *
     * @param query - SQL query
     * @param name - column name
     * @param address - column address
     * @param email - column email
     * @param deputy - column deputy
     * @param phone - column phone
     * @param fax - column fax
     * @param code - column code
     * */
    public void preparedInsertQuery(String query, String name, String address, String email, String deputy, String phone, String fax, String code) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, address);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, deputy);
            preparedStatement.setString(5, phone);
            preparedStatement.setString(6, fax);
            preparedStatement.setString(7, code);

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
     * @param address - column address
     * @param email - column email
     * @param deputy - column deputy
     * @param phone - column phone
     * @param fax - column fax
     * @param code - column code
     * @param id - column id
     * */
    public void preparedUpdateQuery(String query, String name, String address, String email, String deputy, String phone, String fax, String code, int id) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, address);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, deputy);
            preparedStatement.setString(5, phone);
            preparedStatement.setString(6, fax);
            preparedStatement.setString(7, code);
            preparedStatement.setInt(8, id);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();
        } catch (Exception ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
    }
}
