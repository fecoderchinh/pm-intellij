package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.User;

import java.sql.*;

public class jdbcDAO {
    private User createUser(ResultSet rs) {
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

    public boolean validate(String account, String password) throws SQLException {
        String sql = "Select * from user where account = ? and password = ?";
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

        } catch (SQLException | ClassNotFoundException ex) {
            assert ex instanceof SQLException;
            printSQLException((SQLException) ex); }
        return false;
    }

    public static void printSQLException(SQLException ex) {
        for (Throwable e: ex) {
            if( e instanceof SQLException ) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }
}
