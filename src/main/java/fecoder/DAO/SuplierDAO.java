package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.Suplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SuplierDAO {

    private Suplier createSuplier(ResultSet resultSet) {
        Suplier suplier = new Suplier();
        try {
            suplier.setId(resultSet.getInt("id"));
            suplier.setName(resultSet.getString("name"));
            suplier.setAddress(resultSet.getString("address"));
            suplier.setEmail(resultSet.getString("email"));
            suplier.setDeputy(resultSet.getString("deputy"));
            suplier.setPhone(resultSet.getString("phone"));
            suplier.setFax(resultSet.getString("fax"));
            suplier.setCode(resultSet.getString("code"));
        } catch (SQLException ex) {
            jdbcDAO.printSQLException(ex);
        }
        return suplier;
    }

    public List<Suplier> getSupliers() {
        List<Suplier> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll = "Select * from supliers";
            ResultSet resultSet = statement.executeQuery(selectAll);
            while (resultSet.next()) {
                Suplier suplier = createSuplier(resultSet);
                list.add(suplier);
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
        jdbcDAO.updateSingleData("supliers", column, value, id);
    }

    public void delete(int id) {
        jdbcDAO.delete("supliers", id);
    }

    public void update(String name, String address, String email, String deputy, String phone, String fax, String code, int id) {
        String updateQuery = "update supliers set name=?, address=?, email=?, deputy=?, phone=?, fax=?, code=? where id=?";
        preparedUpdateQuery(updateQuery, name, address, email, deputy, phone, fax, code, id);
    }

    public void insert(String name, String address, String email, String deputy, String phone, String fax, String code) {
        String insertQuery = "insert into supliers (name, address, email, deputy, phone, fax, code) values(?,?,?,?,?,?,?)";
        preparedInsertQuery(insertQuery, name, address, email, deputy, phone, fax, code);
    }

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
