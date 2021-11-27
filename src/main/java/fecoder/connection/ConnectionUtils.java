package fecoder.connection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionUtils {

    public static Connection getMyConnection() throws SQLException,
            ClassNotFoundException, IOException {
        // Sử dụng Oracle.
        // Bạn có thể thay thế bởi Database nào đó.
        return MySQLConnUtils.getMySQLConnection();
    }

    //
    // Test Connection ...
    //
    public static void main(String[] args) throws SQLException,
            ClassNotFoundException, IOException {

        System.out.println("Get connection ... ");

        // Lấy ra đối tượng Connection kết nối vào database.
        Connection conn = ConnectionUtils.getMyConnection();

        System.out.println("Get connection " + conn);

        System.out.println("Done!");
    }

}