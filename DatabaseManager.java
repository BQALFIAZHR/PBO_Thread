import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DatabaseManager {

 
    private static final String DB_URL = "jdbc:mysql://localhost:3306/db_rey?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = ""; 

    public void initDatabase() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement()) {

            String sqlCreate = "CREATE TABLE IF NOT EXISTS aktivitas (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "tipe_aktivitas VARCHAR(50) NOT NULL," +
                    "durasi_menit INT NOT NULL," +
                    "waktu_selesai DATETIME NOT NULL)";
            stmt.execute(sqlCreate);
            System.out.println("Koneksi DB dan tabel berhasil disiapkan.");

        } catch (SQLException e) {
            System.err.println("Gagal inisialisasi database: " + e.getMessage());
            throw e; 
        }
    }

    public void logActivityToDatabase(String type, int durationMinutes) throws SQLException {
        String sqlInsert = "INSERT INTO aktivitas(tipe_aktivitas, durasi_menit, waktu_selesai) VALUES(?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {

            pstmt.setString(1, type);
            pstmt.setInt(2, durationMinutes);
            pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis())); 
            pstmt.executeUpdate();
        }
        
    }

    public String loadLogs() throws SQLException {
        String sqlSelect = "SELECT * FROM aktivitas ORDER BY waktu_selesai DESC LIMIT 10"; 
        StringBuilder logs = new StringBuilder();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlSelect)) {

            while (rs.next()) {
                String type = rs.getString("tipe_aktivitas");
                int duration = rs.getInt("durasi_menit");
                Timestamp time = rs.getTimestamp("waktu_selesai");

                logs.append(String.format("[%s] Selesai %s selama %d menit.\n",
                        time.toString(), type, duration));
            }
        }
        return logs.toString(); 
    }
}