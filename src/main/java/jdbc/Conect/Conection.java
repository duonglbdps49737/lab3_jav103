package jdbc.Conect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conection {
    public static final String HOSTNAME = "localhost";
    public static final String PORT = "3306";
    public static final String DBNAME = "";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "root123";

    public static Connection getConnection() {
        String ConnectUrl = "jdbc:mysql://localhost:3306/ql_khachsan";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(ConnectUrl, "root", "root123");
        } catch (SQLException | ClassNotFoundException e) {
            ((Exception)e).printStackTrace(System.out);
            return null;
        }
    }
}
