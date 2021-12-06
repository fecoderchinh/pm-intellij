package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.WorkOrder;
import fecoder.models.WorkOrderToString;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkOrderToStringDAO {

    private final String tableName = "work_order";

    /**
     * Representing a database
     *
     * @param resultSet - A table of data representing a database result set
     * @return data
     * */
    private static WorkOrderToString createData(ResultSet resultSet) {
        WorkOrderToString data = new WorkOrderToString();
        try {
            data.setId(resultSet.getInt("id"));
            data.setName(resultSet.getString("name"));
            data.setLotNumber(resultSet.getString("lot_number"));
            data.setPurchaseOrder(resultSet.getString("po_number"));
            data.setYear(resultSet.getString("year"));
            data.setCustomerId(resultSet.getString("customer_id"));
            data.setSendDate(resultSet.getString("send_date"));
            data.setShippingDate(resultSet.getString("shipping_date"));
            data.setDestination(resultSet.getString("destination"));
            data.setNote(resultSet.getString("note"));
            data.setOrder_date(resultSet.getString("order_date"));
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
    public List<WorkOrderToString> getList() {
        List<WorkOrderToString> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll = "select wo.id as id, wo.name as name, wo.lot_number as lot_number, wo.po_number as po_number, y.year as year, c.name as customer_id, wo.send_date as send_date, wo.shipping_date as shipping_date, wo.destination as destination, wo.note as note, wo.order_date as order_date from work_order as wo, customers as c, years as y where wo.year = y.id and wo.customer_id = c.id order by id DESC";
            ResultSet resultSet = statement.executeQuery(selectAll);
            while (resultSet.next()) {
                WorkOrderToString data = createData(resultSet);
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
     * Getting record data by its ID
     *
     * @param id - record id
     * @return data
     * */
    public WorkOrderToString getDataByID(int id) {
        WorkOrderToString data = new WorkOrderToString();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select wo.id as id, wo.name as name, wo.lot_number as lot_number, wo.po_number as po_number, y.year as year, c.name as customer_id, wo.send_date as send_date, wo.shipping_date as shipping_date, wo.destination as destination, wo.note as note, wo.order_date as order_date from work_order as wo, customers as c, years as y where wo.year = y.id and wo.customer_id = c.id and wo.id=?");
            preparedStatement.setInt(1, id);
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
    public WorkOrderToString getDataByName(String value) {
        WorkOrderToString data = new WorkOrderToString();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select wo.id as id, wo.name as name, wo.lot_number as lot_number, wo.po_number as po_number, y.year as year, c.name as customer_id, wo.send_date as send_date, wo.shipping_date as shipping_date, wo.destination as destination, wo.note as note, wo.order_date as order_date from work_order as wo, customers as c, years as y where wo.year = y.id and wo.customer_id = c.id and wo.name=?");
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
     * Determine the name exists
     *
     * @param value - the record's value
     * @return boolean
     * */
    public boolean hasName(String value) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select wo.id as id, wo.name as name, wo.lot_number as lot_number, wo.po_number as po_number, y.year as year, c.name as customer_id, wo.send_date as send_date, wo.shipping_date as shipping_date, wo.destination as destination, wo.note as note, wo.order_date as order_date from work_order as wo, customers as c, years as y where wo.year = y.id and wo.customer_id = c.id and wo.name=?");
            preparedStatement.setString(1, value);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                return true;
            }
            resultSet.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException | IOException ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
        return false;
    }

    /**
     * Getting all records by its id
     *
     * @param id - the record's id
     * @return list
     * */
    public List<WorkOrderToString> getRow(int id) {
        String query = "select wo.id as id, wo.name as name, wo.lot_number as lot_number, wo.po_number as po_number, y.year as year, c.name as customer_id, wo.send_date as send_date, wo.shipping_date as shipping_date, wo.destination as destination, wo.note as note, wo.order_date as order_date from work_order as wo, customers as c, years as y where wo.year = y.id and wo.customer_id = c.id and wo.id=?";
        List<WorkOrderToString> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                WorkOrderToString data = createData(resultSet);
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
}
