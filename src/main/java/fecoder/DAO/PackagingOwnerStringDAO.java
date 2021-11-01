package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.PackagingOwnerString;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PackagingOwnerStringDAO {

    /**
     * Representing a database
     *
     * @param resultSet - A table of data representing a database result set
     * @return data
     * */
    private PackagingOwnerString createData(ResultSet resultSet) {
        PackagingOwnerString data = new PackagingOwnerString();
        try {
            data.setId(resultSet.getInt("id"));
            data.setProductName(resultSet.getString("productName"));
            data.setSize(resultSet.getString("size"));
            data.setPackagingName(resultSet.getString("packagingName"));
            data.setPack_qty(resultSet.getInt("pack_qty"));
            data.setUnit(resultSet.getString("unit"));
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
    public List<PackagingOwnerString> getList() {
        List<PackagingOwnerString> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll =  "select a.id as id, b.name as packagingName, c.name as productName, d.size as size, a.pack_qty as pack_qty, e.unit as unit " +
                    "from packaging_product_size a, packaging b, products c, sizes d, types e " +
                    "where a.packaging_id = b.id and a.product_id = c.id and a.size_id = d.id and b.type = e.id " +
                    "order by c.name";
            ResultSet resultSet = statement.executeQuery(selectAll);
            while(resultSet.next()) {
                PackagingOwnerString data = createData(resultSet);
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

    /**
     * Getting all records of table
     *
     * @param productName the name of product
     *
     * @return list
     * */
    public List<PackagingOwnerString> getListByName(String productName) {
        List<PackagingOwnerString> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll =  "select a.id as id, b.name as packagingName, c.name as productName, d.size as size, a.pack_qty as pack_qty, e.unit as unit " +
                    "from packaging_product_size a, packaging b, products c, sizes d, types e " +
                    "where a.packaging_id = b.id and a.product_id = c.id and a.size_id = d.id and b.type = e.id  and c.name = '" + productName +"' " +
                    "order by c.name";
            ResultSet resultSet = statement.executeQuery(selectAll);
            while(resultSet.next()) {
                PackagingOwnerString data = createData(resultSet);
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
}
