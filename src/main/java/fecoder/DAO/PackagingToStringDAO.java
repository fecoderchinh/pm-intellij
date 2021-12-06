package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.Packaging;
import fecoder.models.PackagingToString;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PackagingToStringDAO {

    private final String tableName = "packaging";

    /**
     * Representing a database
     *
     * @param resultSet - A table of data representing a database result set
     * @return data
     * */
    private PackagingToString createData(ResultSet resultSet) {
        PackagingToString data = new PackagingToString();
        try {
            data.setId(resultSet.getInt("id"));
            data.setName(resultSet.getString("name"));
            data.setSpecifications(resultSet.getString("specifications"));
            data.setDimension(resultSet.getString("dimension"));
            data.setSuplier(resultSet.getString("suplier"));
            data.setType(resultSet.getString("type"));
            data.setMinimum_order(resultSet.getInt("minimum_order"));
            data.setStamped(resultSet.getBoolean("stamped"));
            data.setNote(resultSet.getString("note"));
            data.setMain(resultSet.getBoolean("main"));
            data.setCode(resultSet.getString("code"));
            data.setPrice(resultSet.getFloat("price"));
            data.setStock(resultSet.getFloat("stock"));
            data.setCustomName(resultSet.getString("customName"));
        } catch (SQLException ex) {
            jdbcDAO.printSQLException(ex);
        }
        return data;
    }

    /**
     * Getting all records of table
     *
     * @return list
     * */
    public List<PackagingToString> getList() {
        List<PackagingToString> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll = "select p.id as id, p.name as name, concat(p.name, ' (', s.code, ')') as customName, p.specifications as specifications, p.dimension as dimension, s.code as suplier, t.name as type, p.minimum_order as minimum_order, p.stamped as stamped, p.code as code, p.main as main, p.note as note, p.price as price, p.stock as stock from packaging as p, supliers as s, types as t where p.suplier = s.id and p.type = t.id order by id DESC";
            ResultSet resultSet = statement.executeQuery(selectAll);
            while(resultSet.next()) {
                PackagingToString data = createData(resultSet);
                list.add(data);
            }
            resultSet.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException | IOException ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
        return list;
    }

    /**
     * Getting lastest record data
     *
     * @return data
     * */
    public PackagingToString getLastestData() {
        PackagingToString data = new PackagingToString();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select p.id as id, p.name as name, concat(p.name, ' (', s.code, ')') as customName, p.specifications as specifications, p.dimension as dimension, s.code as suplier, t.name as type, p.minimum_order as minimum_order, p.stamped as stamped, p.code as code, p.main as main, p.note as note, p.price as price, p.stock as stock from packaging as p, supliers as s, types as t where p.suplier = s.id and p.type = t.id order by id asc limit 1");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                data = createData(resultSet);
            }
            resultSet.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException | IOException ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
        return data;
    }

    /**
     * Getting record data by its name
     *
     * @param value - record's name
     * @return data
     * */
    public PackagingToString getDataByName(String value) {
        PackagingToString data = new PackagingToString();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select p.id as id, p.name as name, concat(p.name, ' (', s.code, ')') as customName, p.specifications as specifications, p.dimension as dimension, s.code as suplier, t.name as type, p.minimum_order as minimum_order, p.stamped as stamped, p.code as code, p.main as main, p.note as note, p.price as price, p.stock as stock from packaging as p, supliers as s, types as t where p.suplier = s.id and p.type = t.id and concat(p.name, ' (', s.code, ')')=?");
            preparedStatement.setString(1, value);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                data = createData(resultSet);
            }
            resultSet.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException | IOException ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
        return data;
    }

    /**
     * Getting record data by its id
     *
     * @param value - record's id
     * @return data
     * */
    public PackagingToString getDataByID(int value) {
        PackagingToString data = new PackagingToString();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select p.id as id, p.name as name, concat(p.name, ' (', s.code, ')') as customName, p.specifications as specifications, p.dimension as dimension, s.code as suplier, t.name as type, p.minimum_order as minimum_order, p.stamped as stamped, p.code as code, p.main as main, p.note as note, p.price as price, p.stock as stock from packaging as p, supliers as s, types as t where p.suplier = s.id and p.type = t.id and p.id=?");
            preparedStatement.setInt(1, value);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                data = createData(resultSet);
            }
            resultSet.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException | IOException ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
        return data;
    }
}
