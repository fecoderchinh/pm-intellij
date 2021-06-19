package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.Type;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TypeDAO {

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

    public List<Type> getList() {
        List<Type> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll = "select * from types";
            ResultSet resultSet = statement.executeQuery(selectAll);
            while(resultSet.next()) {
                Type data = createData(resultSet);
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

    public Type getDataByID(int value) {
        Type type = new Type();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select * from types where id=?");
            preparedStatement.setInt(1, value);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                type = createData(resultSet);
            }
            resultSet.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
        return type;
    }

    public Type getDataByName(String value) {
        Type type = new Type();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select * from types where name=?");
            preparedStatement.setString(1, value);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                type = createData(resultSet);
            }
            resultSet.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
        return type;
    }

    public boolean hasName(String value) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select * from types where name=?");
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

    public void updateData(String column, String value, int id) {
        jdbcDAO.updateSingleData("types", column, value, id);
    }

    public void delete(int id) {
        jdbcDAO.delete("types", id);
    }

    public void update(String name, String unit, int id) {
        String updateQuery = "update types set name=?, unit=? where id=?";
        preparedUpdateQuery(updateQuery, name, unit, id);
    }

    public void insert(String name, String unit) {
        String insertQuery = "insert into types (name, unit) values(?,?)";
        preparedInsertQuery(insertQuery, name, unit);
    }

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
