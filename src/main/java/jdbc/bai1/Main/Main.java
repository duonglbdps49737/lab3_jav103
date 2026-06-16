package jdbc.bai1.Main;
import jdbc.bai1.Connection.DBConnection;

import java.sql.*;
public class Main {
    // ─────────────────────────────────────────────────────
    // 1. Hiển thị toàn bộ nhân viên
    // ─────────────────────────────────────────────────────
    public static void showAllEmployees() {
        String sql = "SELECT e.id, e.full_name, e.salary, d.dept_name " +
                "FROM employees e " +
                "LEFT JOIN departments d ON e.dept_id = d.dept_id " +
                "ORDER BY e.id";

        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║              DANH SÁCH NHÂN VIÊN                        ║");
        System.out.println("╠════╦══════════════════════════╦══════════════╦═══════════╣");
        System.out.printf("║%-4s║%-26s║%-14s║%-11s║%n", " ID", " Họ và Tên", " Lương", " Phòng Ban");
        System.out.println("╠════╬══════════════════════════╬══════════════╬═══════════╣");

        try (Connection conn = DBConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {

            int count = 0;
            while (rs.next()) {
                int    id       = rs.getInt("id");
                String name     = rs.getString("full_name");
                double salary   = rs.getDouble("salary");
                String deptName = rs.getString("dept_name");

                // AI Validation: kiểm tra dữ liệu trước khi hiển thị
                name     = validateString(name,     "N/A");
                deptName = validateString(deptName, "Chưa phân công");
                salary   = salary < 0 ? 0 : salary;

                System.out.printf("║%-4d║%-26s║%,14.0f║%-11s║%n",
                        id, truncate(name, 25), salary, truncate(deptName, 10));
                count++;
            }

            System.out.println("╚════╩══════════════════════════╩══════════════╩═══════════╝");
            System.out.printf("  Tổng: %d nhân viên%n", count);

        } catch (SQLException e) {
            System.err.println("[LỖI] Không thể truy vấn nhân viên: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────
    // 2. Lọc phòng ban có số nhân viên >= minCount
    // ─────────────────────────────────────────────────────
    public static void showDepartmentsWithMinEmployees(int minCount) {
        // AI Validation: kiểm tra tham số đầu vào
        if (minCount < 1) {
            System.err.println("[VALIDATION] minCount phải >= 1. Đặt lại = 1.");
            minCount = 1;
        }

        String sql = "SELECT d.dept_name, COUNT(e.id) AS emp_count " +
                "FROM departments d " +
                "LEFT JOIN employees e ON d.dept_id = e.dept_id " +
                "GROUP BY d.dept_id, d.dept_name " +
                "HAVING COUNT(e.id) >= " + minCount + " " +
                "ORDER BY emp_count DESC";

        System.out.printf("%n╔══════════════════════════════════════╗%n");
        System.out.printf("║  PHÒNG BAN CÓ SỐ NHÂN VIÊN >= %-3d   ║%n", minCount);
        System.out.println("╠══════════════════════════╦═══════════╣");
        System.out.printf("║%-26s║%-11s║%n", " Tên Phòng Ban", " Số NV");
        System.out.println("╠══════════════════════════╬═══════════╣");

        try (Connection conn = DBConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {

            int count = 0;
            while (rs.next()) {
                String deptName = validateString(rs.getString("dept_name"), "Không tên");
                int    empCount = rs.getInt("emp_count");
                System.out.printf("║%-26s║%-11d║%n", truncate(deptName, 25), empCount);
                count++;
            }

            System.out.println("╚══════════════════════════╩═══════════╝");
            if (count == 0) System.out.println("  Không có phòng ban nào thỏa điều kiện.");

        } catch (SQLException e) {
            System.err.println("[LỖI] Lọc phòng ban thất bại: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────
    // 3. Thống kê: Tên phòng ban | Số lượng nhân viên
    // ─────────────────────────────────────────────────────
    public static void showDepartmentStats() {
        String sql = "SELECT d.dept_name, COUNT(e.id) AS emp_count, " +
                "       ISNULL(AVG(e.salary), 0) AS avg_salary " +
                "FROM departments d " +
                "LEFT JOIN employees e ON d.dept_id = e.dept_id " +
                "GROUP BY d.dept_id, d.dept_name " +
                "ORDER BY emp_count DESC";

        System.out.println("\n╔═══════════════════════════════════════════════════════╗");
        System.out.println("║           THỐNG KÊ THEO PHÒNG BAN                    ║");
        System.out.println("╠══════════════════════════╦═══════════╦════════════════╣");
        System.out.printf( "║%-26s║%-11s║%-16s║%n", " Tên Phòng Ban", " Số NV", " Lương TB");
        System.out.println("╠══════════════════════════╬═══════════╬════════════════╣");

        try (Connection conn = DBConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String deptName  = validateString(rs.getString("dept_name"), "Không tên");
                int    empCount  = rs.getInt("emp_count");
                double avgSalary = rs.getDouble("avg_salary");

                // AI Validation
                avgSalary = avgSalary < 0 ? 0 : avgSalary;

                System.out.printf("║%-26s║%-11d║%,16.0f║%n",
                        truncate(deptName, 25), empCount, avgSalary);
            }

            System.out.println("╚══════════════════════════╩═══════════╩════════════════╝");

        } catch (SQLException e) {
            System.err.println("[LỖI] Thống kê thất bại: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────
    // AI Validation Helpers
    // ─────────────────────────────────────────────────────
    private static String validateString(String value, String defaultValue) {
        return (value == null || value.isBlank()) ? defaultValue : value.trim();
    }

    private static String truncate(String s, int maxLen) {
        if (s == null) return "";
        return s.length() <= maxLen ? s : s.substring(0, maxLen - 1) + "…";
    }

    // ─────────────────────────────────────────────────────
    // MAIN
    // ─────────────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  LAB 3 - BÀI 1: JDBC + Statement (SQL Server)  ");
        System.out.println("=================================================");

        // 1. Toàn bộ nhân viên
        showAllEmployees();

        // 2. Phòng ban có >= 2 nhân viên
        showDepartmentsWithMinEmployees(2);

        // 3. Thống kê phòng ban
        showDepartmentStats();
    }
}

