package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.PackagingType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PackagingTypeDAO {

    private PackagingType createType(ResultSet resultSet) {
        PackagingType data = new PackagingType();
        try {
            data.setId(resultSet.getInt("id"));
            data.setName(resultSet.getString("name"));
        } catch (SQLException ex) {
            fecoder.DAO.jdbcDAO.printSQLException(ex);
        }
        return data;
    }

    public List<PackagingType> getTypes() {
        List<PackagingType> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll = "select * from packaging_types";
            ResultSet resultSet = statement.executeQuery(selectAll);
            while(resultSet.next()) {
                PackagingType data = createType(resultSet);
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

    public void updateData(String column, String value, int id) {
        jdbcDAO.updateSingleData("packaging_types", column, value, id);
    }

    public void delete(int id) {
        jdbcDAO.delete("packaging_types", id);
    }

    public void update(String name, int id) {
        String updateQuery = "update packaging_types set name=? where id=?";
        preparedUpdateQuery(updateQuery, name, id);
    }

    public void insert(String name) {
        String insertQuery = "insert into packaging_types (name) values(?)";
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
