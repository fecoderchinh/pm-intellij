package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.Product;
import fecoder.models.User;
import fecoder.utils.Utils;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.sql.*;

public class jdbcDAO {

    private final String usersTable = "users";

    /**
     *
     * This method is on development
     * */
    private User createData(ResultSet rs) {
        User user = new User();
        try {
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setAccount(rs.getString("account"));
            user.setPassword(rs.getString("password"));
            user.setRoleId(rs.getInt("role_id"));
        } catch (SQLException ignored) {}

        return user;
    }

    /**
     * Getting record data by its name
     *
     * @param value - record's name
     * @return data
     * */
    public User getDataByName(String value) {
        User data = new User();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("select * from "+ usersTable +" where account=? order by account desc limit 1");
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
     *
     * Handle on validating user input
     *
     * @param account - user account
     * @param password - user password
     * @return boolean
     * */
    public boolean validate(String account, String password) throws SQLException {
        String sql = "Select * from users where account = ? and password = ?";
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, account);
            preparedStatement.setString(2, password);

//            System.out.println(preparedStatement);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }

        } catch (SQLException | ClassNotFoundException | IOException ex) {
            assert ex instanceof SQLException;
            printSQLException((SQLException) ex); }
        return false;
    }

    /**
     *
     * Handle on updating specific String property
     *
     * @param table - the selected table
     * @param column - the selected column
     * @param value - the string new value
     * @param idList - the list of ID
     * */
    public static void updateSingleData(String table, String column, String value, String idList) {
        String query = "update "+ table +" set "+ column +"=? where id in (?)";
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, value);
            preparedStatement.setString(2, idList);
            preparedStatement.execute();
        } catch (Exception ex) {
            assert ex instanceof SQLException;
            printSQLException((SQLException) ex);
        }
    }

    /**
     *
     * Handle on updating specific Integer property
     *
     * @param table - the selected table
     * @param column - the selected column
     * @param value - the integer new value
     * @param id - the record id
     * */
    public static void updateSingleDataInteger(String table, String column, int value, int id) {
        String query = "update "+ table +" set "+ column +"=? where id=?";
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, value);
            preparedStatement.setInt(2, id);
            preparedStatement.execute();
        } catch (Exception ex) {
            assert ex instanceof SQLException;
            printSQLException((SQLException) ex);
        }
    }

    /**
     *
     * Handle on updating specific Float property
     *
     * @param table - the selected table
     * @param column - the selected column
     * @param value - the float new value
     * @param id - the record id
     * */
    public static void updateSingleDataFloat(String table, String column, float value, int id) {
        String query = "update "+ table +" set "+ column +"=? where id=?";
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setFloat(1, value);
            preparedStatement.setInt(2, id);
            preparedStatement.execute();
        } catch (Exception ex) {
            assert ex instanceof SQLException;
            printSQLException((SQLException) ex);
        }
    }

    /**
     *
     * Handle on updating specific Boolean property
     *
     * @param table - the selected table
     * @param column - the selected column
     * @param value - the boolean new value
     * @param id - the record id
     * */
    public static void updateSingleDataBoolean(String table, String column, boolean value, int id) {
        String query = "update "+ table +" set "+ column +"=? where id=?";
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setBoolean(1, value);
            preparedStatement.setInt(2, id);
            preparedStatement.execute();
        } catch (Exception ex) {
            assert ex instanceof SQLException;
            printSQLException((SQLException) ex);
        }
    }

    /**
     *
     * Handle delete record data
     *
     * @param table - the selected table
     * @param id - the record id
     * */
    public static void delete(String table, int id) {
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("delete from "+ table + " where id=?");
            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();
        } catch (Exception ex) {
            assert ex instanceof SQLException;
            printSQLException((SQLException) ex);
        }
    }

    /**
     *
     * Controlling SQL Exceptions
     *
     * @param ex - the SQL Exception data
     * */
    public static void printSQLException(SQLException ex) {
        for (Throwable e: ex) {
            if( e instanceof SQLException ) {
                e.printStackTrace(System.err);
//                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
//                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
//                System.err.println("Message: " + e.getMessage());
                Utils utils = new Utils();
                utils.alert("err", Alert.AlertType.ERROR, "Error Code: " + ((SQLException) e).getErrorCode(), "Message: " + e.getMessage()).showAndWait();
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }
}
