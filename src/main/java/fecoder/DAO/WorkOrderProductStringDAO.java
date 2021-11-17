package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.PackagingOwnerString;
import fecoder.models.WorkOrderProductString;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class WorkOrderProductStringDAO {

    /**
     * Representing a database
     *
     * @param resultSet - A table of data representing a database result set
     * @return data
     * */
    private WorkOrderProductString createData(ResultSet resultSet) {
        WorkOrderProductString data = new WorkOrderProductString();
        try {
            data.setId(resultSet.getInt("id"));
            data.setWorkOrderName(resultSet.getString("workOrderName"));
            data.setProductName(resultSet.getString("productName"));
            data.setProductOrdinalNumber(resultSet.getString("productOrdinalNumber"));
            data.setProductQuantity(resultSet.getFloat("productQuantity"));
            data.setProductNote(resultSet.getString("productNote"));
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
    public List<WorkOrderProductString> getList() {
        List<WorkOrderProductString> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll =  "select wop.id as id, wo.name as workOrderName, p.name as productName, wop.ordinal_num as productOrdinalNumber, wop.qty as productQuantity, wop.note as productNote" +
                    " from work_order_product wop, products p, work_order wo" +
                    " where wop.work_order_id  = wo.id and wop.product_id = p.id"+
                    " order by wop.ordinal_num";
            ResultSet resultSet = statement.executeQuery(selectAll);
            while(resultSet.next()) {
                WorkOrderProductString data = createData(resultSet);
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
     * @param workOrderID the name of work_order
     *
     * @return list
     * */
    public List<WorkOrderProductString> getListByWorkOrderID(int workOrderID) {
        List<WorkOrderProductString> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll =  "select wop.id as id, wo.name as workOrderName, p.name as productName, wop.ordinal_num as productOrdinalNumber, wop.qty as productQuantity, wop.note as productNote" +
                    " from work_order_product wop, products p, work_order wo" +
                    " where wop.work_order_id  = wo.id and wop.product_id = p.id and wo.id = '"+ workOrderID +"'"+
                    " order by wop.ordinal_num";
            ResultSet resultSet = statement.executeQuery(selectAll);
            while(resultSet.next()) {
                WorkOrderProductString data = createData(resultSet);
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
     * @param workOrderName the name of work_order
     *
     * @return list
     * */
    public WorkOrderProductString getDataByName(String workOrderName) {
        WorkOrderProductString data = new WorkOrderProductString();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll =  "select wop.id as id, wo.name as workOrderName, p.name as productName, wop.ordinal_num as productOrdinalNumber, wop.qty as productQuantity, wop.note as productNote" +
                    " from work_order_product wop, products p, work_order wo" +
                    " where wop.work_order_id  = wo.id and wop.product_id = p.id and wo.name = '"+ workOrderName +"'"+
                    " order by wop.ordinal_num";
            ResultSet resultSet = statement.executeQuery(selectAll);
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
}
