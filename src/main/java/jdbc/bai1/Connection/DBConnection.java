package jdbc.bai1.Connection;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DBConnection {
    private static final String SERVER   = "localhost/SQLEXPRESS";
    private static final String PORT     = "1433";
    private static final String DATABASE = "lab3_db";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "root123"; // đổi lại

    private static final String URL =
            "jdbc:sqlserver://" + SERVER + ":" + PORT
                    + ";databaseName=" + DATABASE
                    + ";encrypt=false"
                    + ";trustServerCertificate=true";

    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Không tìm thấy JDBC Driver cho SQL Server!", e);
        }
    }

    /**
     * Trả về Connection tới SQL Server
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /**
     * Đóng connection an toàn
     */
    public static void close(AutoCloseable... resources) {
        for (AutoCloseable r : resources) {
            if (r != null) {
                try { r.close(); } catch (Exception ignored) {}
            }
        }
    }
}
