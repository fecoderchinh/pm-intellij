package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.WorkOrder;
import fecoder.utils.Utils;
import javafx.scene.control.Alert;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkOrderDAO {
    
    private final String tableName = "work_order";
    private final Utils utils = new Utils();

    /**
     * Representing a database
     *
     * @param resultSet - A table of data representing a database result set
     * @return data
     * */
    private static WorkOrder createData(ResultSet resultSet) {
        WorkOrder data = new WorkOrder();
        try {
            data.setId(resultSet.getInt("id"));
            data.setName(resultSet.getString("name"));
            data.setLotNumber(resultSet.getString("lot_number"));
            data.setPurchaseOrder(resultSet.getString("po_number"));
            data.setYear(resultSet.getInt("year"));
            data.setCustomerId(resultSet.getInt("customer_id"));
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
    public List<WorkOrder> getList() {
        List<WorkOrder> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll = "Select * from "+tableName;
            ResultSet resultSet = statement.executeQuery(selectAll);
            while (resultSet.next()) {
                WorkOrder data = createData(resultSet);
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
     * Getting record data by its ID
     *
     * @param id - record id
     * @return data
     * */
    public WorkOrder getDataByID(int id) {
        WorkOrder data = new WorkOrder();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select * from "+ tableName +" where id=?");
            preparedStatement.setInt(1, id);
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

    /**
     * Getting record data by its name
     *
     * @param value - record's name
     * @return data
     * */
    public WorkOrder getDataByName(String value) {
        WorkOrder data = new WorkOrder();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select * from "+ tableName +" where name=?");
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

    /**
     * Determine the name exists
     *
     * @param value - the record's value
     * @return boolean
     * */
    public boolean hasName(String value) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select * from "+ tableName +" where name=?");
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

    /**
     * Updating record data
     * 
     * @param column - table's column
     * @param value - column's new value
     * @param idList - record's id
     * */
    public void updateData(String column, String value, String idList) {
        jdbcDAO.updateSingleData(tableName, column, value, idList);
    }

    /**
     * Updating record integer data
     *
     * @param column - table's column
     * @param value - column's new value
     * @param id - record's id
     * */
    public void updateDataInteger(String column, int value, int id) {
        jdbcDAO.updateSingleDataInteger(tableName, column, value, id);
    }

    /**
     * Deleting record data
     *
     * @param id - record's id
     * */
    public void delete(int id) {
        jdbcDAO.delete(tableName, id);
    }

    /**
     * Getting all records by its id
     *
     * @param id - the record's id
     * @return list
     * */
    public List<WorkOrder> getRow(int id) {
        String query = "select * from "+ tableName +" where id=?";
        List<WorkOrder> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                WorkOrder data = createData(resultSet);
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
     * Updating all columns
     *
     * @param name - column name
     * @param lotNumber - column lotNumber
     * @param purchaseOrder - column purchaseOrder
     * @param year - column year
     * @param customerId - column customerId
     * @param sendDate - column sendDate
     * @param shippingDate - column shippingDate
     * @param destination - column destination
     * @param note - column note
     * @param id - column id
     * */
    public void update(String name, String lotNumber, String purchaseOrder, int year, int customerId, String sendDate, String shippingDate, String destination, String note, int id) {
        String updateQuery = "update "+ tableName +" set name=?, lot_number=?, po_number=?, year=?, customer_id=?, send_date=str_to_date(?,'%d-%m-%Y'), shipping_date=str_to_date(?,'%d-%m-%Y'), destination=?, note=? where id=?";
        preparedUpdateQuery(updateQuery, name, lotNumber, purchaseOrder, year, customerId, sendDate, shippingDate, destination, note, id);
    }

    /**
     * Inserting all columns
     *
     * @param name - column name
     * @param lotNumber - column lotNumber
     * @param purchaseOrder - column purchaseOrder
     * @param year - column year
     * @param customerId - column customerId
     * @param sendDate - column sendDate
     * @param shippingDate - column shippingDate
     * @param destination - column destination
     * @param note - column note
     * */
    public void insert(String name, String lotNumber, String purchaseOrder, int year, int customerId, String sendDate, String shippingDate, String destination, String note) {
        String insertQuery = "insert into "+ tableName +" (name, lot_number, po_number, year, customer_id, send_date, shipping_date, destination, note) values(REPLACE(?, ' ', ''),?,?,?,?,str_to_date(?,'%d-%m-%Y'),str_to_date(?,'%d-%m-%Y'),?,?)";
        preparedInsertQuery(insertQuery, name, lotNumber, purchaseOrder, year, customerId, sendDate, shippingDate, destination, note);
    }

    /**
     * Preparing Insert Query before action
     *
     * @param name - column name
     * @param lotNumber - column lotNumber
     * @param purchaseOrder - column purchaseOrder
     * @param year - column year
     * @param customerId - column customerId
     * @param sendDate - column sendDate
     * @param shippingDate - column shippingDate
     * @param destination - column destination
     * @param note - column note
     * */
    public void preparedInsertQuery(String query, String name, String lotNumber, String purchaseOrder, int year, int customerId, String sendDate, String shippingDate, String destination, String note) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, lotNumber);
            preparedStatement.setString(3, purchaseOrder);
            preparedStatement.setInt(4, year);
            preparedStatement.setInt(5, customerId);
            preparedStatement.setString(6, sendDate);
            preparedStatement.setString(7, shippingDate);
            preparedStatement.setString(8, destination);
            preparedStatement.setString(9, note);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();
        } catch (Exception ex) {
//            assert ex instanceof SQLException;
//            jdbcDAO.printSQLException((SQLException) ex);
            utils.alert("err", Alert.AlertType.ERROR, "Lỗi!", ex.getMessage()).showAndWait();
        }
    }

    /**
     * Preparing Update Query before action
     *
     * @param name - column name
     * @param lotNumber - column lotNumber
     * @param purchaseOrder - column purchaseOrder
     * @param year - column year
     * @param customerId - column customerId
     * @param sendDate - column sendDate
     * @param shippingDate - column shippingDate
     * @param destination - column destination
     * @param note - column note
     * @param id - column id
     * */
    public void preparedUpdateQuery(String query, String name, String lotNumber, String purchaseOrder, int year, int customerId, String sendDate, String shippingDate, String destination, String note, int id) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, lotNumber);
            preparedStatement.setString(3, purchaseOrder);
            preparedStatement.setInt(4, year);
            preparedStatement.setInt(5, customerId);
            preparedStatement.setString(6, sendDate);
            preparedStatement.setString(7, shippingDate);
            preparedStatement.setString(8, destination);
            preparedStatement.setString(9, note);
            preparedStatement.setInt(10, id);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();
        } catch (Exception ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
    }
}
