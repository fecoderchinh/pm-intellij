package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.Year;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class YearDAO {

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

    public List<Year> getList() {
        List<Year> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll = "select * from years";
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

    public void updateData(String column, String year, int id) {
        jdbcDAO.updateSingleData("years", column, year, id);
    }

    public void delete(int id) {
        jdbcDAO.delete("years", id);
    }

    public void update(String year, int id) {
        String updateQuery = "update years set year=? where id=?";
        preparedUpdateQuery(updateQuery, year, id);
    }

    public void insert(String year) {
        String insertQuery = "insert into years (year) values(?)";
        preparedInsertQuery(insertQuery, year);
    }

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
