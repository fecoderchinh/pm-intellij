package fecoder.utils;

import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
    private static final Properties prop=new Properties();
//    private static final File f = new File("C:\\Program Files\\Fecoder-Java\\connection.properties"); // for compiler
    private static final File f = new File("E:\\java_platform\\projects\\pmdb\\src\\main\\java\\fecoder\\config.properties");
    private static final Utils utils = new Utils();

    public Configuration() {

    }

    public static boolean isEmpty() throws IOException {
        return (!f.isFile() && !f.canRead());
    }
    
    private static void loadProperties() throws IOException {
        if(!isEmpty()) {
            try {
                FileInputStream fis = new FileInputStream(f);
                prop.load(fis);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = utils.alert("err", Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy file connection.properties!");
            alert.showAndWait();
            alert.close();
        }
    }

    public static String hostname() throws IOException {
        loadProperties();
        return prop.getProperty("hostname");
    }

    public static String port() throws IOException {
        loadProperties();
        return prop.getProperty("port");
    }

    public static String database() throws IOException {
        loadProperties();
        return prop.getProperty("database");
    }

    public static String user() throws IOException {
        loadProperties();
        return prop.getProperty("user");
    }

    public static String password() throws IOException {
        loadProperties();
        return prop.getProperty("password");
    }
}
