package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.Suplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SuplierDAO {

    private static Suplier createData(ResultSet resultSet) {
        Suplier data = new Suplier();
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

    public List<Suplier> getList() {
        List<Suplier> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll = "Select * from supliers";
            ResultSet resultSet = statement.executeQuery(selectAll);
            while (resultSet.next()) {
                Suplier data = createData(resultSet);
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

    public Suplier getDataByID(int value) {
        Suplier data = new Suplier();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select * from supliers where id=?");
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

    public Suplier getDataByName(String value) {
        Suplier data = new Suplier();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select * from supliers where name=?");
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

    public boolean hasName(String value) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select * from supliers where name=?");
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
        jdbcDAO.updateSingleData("supliers", column, value, id);
    }

    public void delete(int id) {
        jdbcDAO.delete("supliers", id);
    }

    public List<Suplier> getRow(int id) {
        String query = "select * from supliers where id=?";
        List<Suplier> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                Suplier data = createData(resultSet);
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
