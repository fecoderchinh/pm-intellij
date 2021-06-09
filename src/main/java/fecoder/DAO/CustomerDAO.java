package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    private Customer createData(ResultSet resultSet) {
        Customer data = new Customer();
        try {
            data.setId(resultSet.getInt("id"));
            data.setName(resultSet.getString("name"));
            data.setNote(resultSet.getString("note"));
        } catch (SQLException ex) {
            jdbcDAO.printSQLException(ex);
        }
        return data;
    }

    public List<Customer> getLists() {
        List<Customer> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll = "select * from customers";
            ResultSet resultSet = statement.executeQuery(selectAll);
            while(resultSet.next()) {
                Customer data = createData(resultSet);
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
        jdbcDAO.updateSingleData("customers", column, value, id);
    }

    public void delete(int id) {
        jdbcDAO.delete("customers", id);
    }

    public void update(String name, String note, int id) {
        String updateQuery = "update customers set name=?, note=? where id=?";
        preparedUpdateQuery(updateQuery, name, note, id);
    }

    public void insert(String name, String note) {
        String insertQuery = "insert into customers (name, note) values(?,?)";
        preparedInsertQuery(insertQuery, name, note);
    }

    public void preparedInsertQuery(String query, String name, String note) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, note);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();
        } catch (Exception ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
    }

    public void preparedUpdateQuery(String query, String name, String note, int id) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, note);
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
