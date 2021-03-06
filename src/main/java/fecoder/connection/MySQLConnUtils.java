package fecoder.connection;

import fecoder.utils.Configuration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnUtils {

    // Kết nối vào MySQL.
    public static Connection getMySQLConnection() throws SQLException,
            ClassNotFoundException, IOException {
//        String hostName = "localhost";
//        String port = "3306";
//
//        String dbName = "pmdb";
//        String userName = "root";
//        String password = "";

        return getMySQLConnection(Configuration.hostname(), Configuration.port(), Configuration.database(), Configuration.user(), Configuration.password());
    }

    public static Connection getMySQLConnection(String hostName, String port, String dbName,
                                                String userName, String password) throws SQLException,
            ClassNotFoundException {
        // Khai báo class Driver cho DB MySQL
        // Việc này cần thiết với Java 5
        // Java6 tự động tìm kiếm Driver thích hợp.
        // Nếu bạn dùng Java6, thì ko cần dòng này cũng được.
//        Class.forName("com.mysql.jdbc.Driver");

        // Cấu trúc URL Connection dành cho Oracle
        // Ví dụ: jdbc:mysql://localhost:3306/simplehr
        String connectionURL = "jdbc:mysql://" + hostName + ":" + port + "/" + dbName + "?characterEncoding=UTF-8&autoReconnect=true&useSSL=false";

        return DriverManager.getConnection(connectionURL, userName,
                password);
    }
}