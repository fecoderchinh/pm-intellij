package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.Packaging;
import fecoder.models.PackagingOwner;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PackagingOwnerDAO {
    private PackagingOwner createData(ResultSet resultSet) {
        PackagingOwner data = new PackagingOwner();
        try {
            data.setId(resultSet.getInt("id"));
            data.setProduct_id(resultSet.getInt("product_id"));
            data.setSize_id(resultSet.getInt("size_id"));
            data.setPackaging_id(resultSet.getInt("packaging_id"));
            data.setPack_qty(resultSet.getInt("pack_qty"));
        } catch (SQLException ex) {
            jdbcDAO.printSQLException(ex);
        }
        return data;
    }

    public List<PackagingOwner> getList() {
        List<PackagingOwner> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll = "select * from packaging_product_size order by product_id";
            ResultSet resultSet = statement.executeQuery(selectAll);
            while(resultSet.next()) {
                PackagingOwner data = createData(resultSet);
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

    public PackagingOwner getDataByColumnIntegerValue(String column, int value) {
        PackagingOwner data = new PackagingOwner();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select * from packaging_product_size where "+column+"=?");
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

    public PackagingOwner getDataByID(int value) {
        PackagingOwner data = new PackagingOwner();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select * from packaging_product_size where id=?");
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

    public void delete(int id) {
        jdbcDAO.delete("packaging_product_size", id);
    }

    public void updateDataInteger(String column, int value, int id) {
        jdbcDAO.updateSingleDataInteger("packaging_product_size", column, value, id);
    }

    public void update(int productID, int sizeID, int packagingID, int packQty, int id) {
        String updateQuery = "update packaging_product_size set product_id=?, size_id=?, packaging_id=?, pack_qty=? where id=?";
        preparedUpdateQuery(updateQuery, productID, sizeID, packagingID, packQty, id);
    }

    public void insert(int productID, int sizeID, int packagingID, int packQty) {
        String insertQuery = "insert into packaging_product_size (product_id, size_id, packaging_id, pack_qty) values(?,?,?,?)";
        preparedInsertQuery(insertQuery, productID, sizeID, packagingID, packQty);
    }

    public void preparedInsertQuery(String query, int productID, int sizeID, int packagingID, int packQty) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, productID);
            preparedStatement.setInt(2, sizeID);
            preparedStatement.setInt(3, packagingID);
            preparedStatement.setInt(4, packQty);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();
        } catch (Exception ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
    }

    public void preparedUpdateQuery(String query, int productID, int sizeID, int packagingID, int packQty, int id) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, productID);
            preparedStatement.setInt(2, sizeID);
            preparedStatement.setInt(3, packagingID);
            preparedStatement.setInt(4, packQty);
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
