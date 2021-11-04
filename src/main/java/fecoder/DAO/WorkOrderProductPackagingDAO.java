package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.WorkOrderProductPackaging;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class WorkOrderProductPackagingDAO {
    private final String tableName = "work_order_product_packaging";

    /**
     * Representing a database
     *
     * @param resultSet - A table of data representing a database result set
     * @return data
     * */
    private static WorkOrderProductPackaging createData(ResultSet resultSet) {
        WorkOrderProductPackaging data = new WorkOrderProductPackaging();
        try {
            data.setId(resultSet.getInt("id"));
            data.setWork_order_id(resultSet.getInt("work_order_id"));
            data.setProduct_id(resultSet.getInt("product_id"));
            data.setPackaging_id(resultSet.getInt("packaging_id"));
            data.setWork_order_qty(resultSet.getFloat("work_order_qty"));
            data.setStock(resultSet.getFloat("stock"));
            data.setActual_qty(resultSet.getFloat("actual_qty"));
            data.setResidual_qty(resultSet.getFloat("residual_qty"));
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
    public List<WorkOrderProductPackaging> getList() {
        List<WorkOrderProductPackaging> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll = "Select * from "+tableName;
            ResultSet resultSet = statement.executeQuery(selectAll);
            while (resultSet.next()) {
                WorkOrderProductPackaging data = createData(resultSet);
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
