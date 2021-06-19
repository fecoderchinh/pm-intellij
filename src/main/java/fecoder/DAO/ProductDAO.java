package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
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

    public List<Product> getList() {
        List<Product> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll = "select * from products";
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

    public void delete(int id) {
        jdbcDAO.delete("products", id);
    }

    public void updateData(String column, String value, int id) {
        jdbcDAO.updateSingleData("products", column, value, id);
    }

    public void updateDataInteger(String column, int value, int id) {
        jdbcDAO.updateSingleDataInteger("products", column, value, id);
    }

    public void update(String name, String description, String specification, String note, int id) {
        String updateQuery = "update products set name=?, description=?, specification=?, note=? where id=?";
        preparedUpdateQuery(updateQuery, name, description, specification, note, id);
    }

    public void insert(String name, String description, String specification, String note) {
        String insertQuery = "insert into products (name, description, specification, note) values(?,?,?,?)";
        preparedInsertQuery(insertQuery, name, description, specification, note);
    }

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
