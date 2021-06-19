package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.Packaging;
import fecoder.models.Type;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PackagingDAO {

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
        } catch (SQLException ex) {
            jdbcDAO.printSQLException(ex);
        }
        return data;
    }

    public List<Packaging> getList() {
        List<Packaging> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll = "select * from packaging";
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

    public Packaging getDataByID(int value) {
        Packaging data = new Packaging();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select * from packaging where id=?");
            preparedStatement.setInt(1, value);
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

    public Packaging getDataByName(String value) {
        Packaging data = new Packaging();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select * from packaging where name=?");
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

    public void updateData(String column, String value, int id) {
        jdbcDAO.updateSingleData("packaging", column, value, id);
    }

    public void updateDataInteger(String column, int value, int id) {
        jdbcDAO.updateSingleDataInteger("packaging", column, value, id);
    }

    public void updateDataFloat(String column, float value, int id) {
        jdbcDAO.updateSingleDataFloat("packaging", column, value, id);
    }

    public void updateDataBoolean(String column, boolean value, int id) {
        jdbcDAO.updateSingleDataBoolean("packaging", column, value, id);
    }

    public void delete(int id) {
        jdbcDAO.delete("packaging", id);
    }

    public void update(String name, String specifications, String dimension, int suplier, int type, int minimum_order, boolean stamped, String code, boolean main, String note, float price, int id) {
        String updateQuery = "update packaging set name=?, specifications=?, dimension=?, suplier=?, type=?, minimum_order=?, stamped=?, code=?, main=?, note=?, price=? where id=?";
        preparedUpdateQuery(updateQuery, name, specifications, dimension, suplier, type, minimum_order, stamped, code, main, note, price, id);
    }

    public void insert(String name, String specifications, String dimension, int suplier, int type, int minimum_order, boolean stamped, String code, boolean main, String note, float price) {
        String insertQuery = "insert into packaging (name, specifications, dimension, suplier, type, minimum_order, stamped, code, main, note, price) values(?,?,?,?,?,?,?,?,?,?,?)";
        preparedInsertQuery(insertQuery, name, specifications, dimension, suplier, type, minimum_order, stamped, code, main, note, price);
    }

    public void preparedInsertQuery(String query, String name, String specifications, String dimension, int suplier, int type, int minimum_order, boolean stamped, String code, boolean main, String note, float price) {
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

            preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();
        } catch (Exception ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
    }

    public void preparedUpdateQuery(String query, String name, String specifications, String dimension, int suplier, int type, int minimum_order, boolean stamped, String code, boolean main, String note, float price, int id) {
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
            preparedStatement.setInt(12, id);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();
        } catch (Exception ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
    }
}
