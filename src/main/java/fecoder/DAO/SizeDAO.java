package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.Size;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SizeDAO {

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

    public List<Size> getList() {
        List<Size> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll = "select * from sizes order by size";
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

    public Size getDataByName(String value) {
        Size data = new Size();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select * from sizes where size=?");
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

    public Size getDataByID(int value) {
        Size data = new Size();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select * from sizes where id=?");
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

    public void updateData(String column, String size, int id) {
        jdbcDAO.updateSingleData("sizes", column, size, id);
    }

    public void delete(int id) {
        jdbcDAO.delete("sizes", id);
    }

    public void update(String size, int id) {
        String updateQuery = "update sizes set size=? where id=?";
        preparedUpdateQuery(updateQuery, size, id);
    }

    public void insert(String size) {
        String insertQuery = "insert into sizes (size) values(?)";
        preparedInsertQuery(insertQuery, size);
    }

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
