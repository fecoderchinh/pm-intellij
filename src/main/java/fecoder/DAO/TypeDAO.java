package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.Type;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TypeDAO {

    private Type createType(ResultSet resultSet) {
        Type suplier = new Type();
        try {
            suplier.setId(resultSet.getInt("id"));
            suplier.setName(resultSet.getString("name"));
        } catch (SQLException ex) {
            fecoder.DAO.jdbcDAO.printSQLException(ex);
        }
        return suplier;
    }

    public List<Type> getTypes() {
        List<Type> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll = "select * from types";
            ResultSet resultSet = statement.executeQuery(selectAll);
            while(resultSet.next()) {
                Type type = createType(resultSet);
                list.add(type);
            }
            resultSet.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
        return list;
    }

    public void updateData(String column, String value, int id) {
        jdbcDAO.updateSingleData("types", column, value, id);
    }

    public void delete(int id) {
        jdbcDAO.delete("types", id);
    }

    public void update(String name, int id) {
        String updateQuery = "update types set name=? where id=?";
        preparedUpdateQuery(updateQuery, name, id);
    }

    public void insert(String name) {
        String insertQuery = "insert into types (name) values(?)";
        preparedInsertQuery(insertQuery, name);
    }

    public void preparedInsertQuery(String query, String name) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, name);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();
        } catch (Exception ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
    }

    public void preparedUpdateQuery(String query, String name, int id) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, name);
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
