/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dbConnect;

/**
 *
 * @author trinh
 */
import java.sql.SQLException;
import java.sql.*;

public class ConnectDB {

    // tạo các thông số
    private static String SERVER = "localhost";
    private static String PATH = "1433";
    private static String DBNAME = "asmHoanthien";
    private static String USER = "sa";
    private static String PASS = "123";

    private static boolean SSL = true;// mặc định kt kết nối ssl = true
    private static String URL = null; // biến chứa đường liên kết 
    
    // thực hiện load driver + tạo url
    static {
        try {
            //1 load driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
           

            //2.1 đói tượng chưa liên kết
            StringBuilder sdbURL = new StringBuilder();
           
            sdbURL.append("jdbc:sqlserver://")
                    .append(SERVER).append(":").append(PATH).append(";")
                    .append("databasename = ").append(DBNAME).append(";")
                    .append("user = ").append(USER).append(";")
                    .append("password = ").append(PASS).append(";");
        
            //2.2 kiem tra ket noi SSL
            // thuc hien ma hoa dl va bo qua việc xác minh chứng chỉ
            if (SSL) {
                sdbURL.append("encrypt = true; trustServerCertificate = true");
            }
            //2.3 dưa liên kết vào chuỗi URL
            URL = sdbURL.toString();
            System.out.println("URL: " + URL);
            

        } catch (Exception e) {
            System.out.println("Load driver ko thành công");
           
        }

    }

    // thực hiện kết nối
    public static Connection getConnect() {
        try {
            Connection c = DriverManager.getConnection(URL);
            System.out.println("kết nối  thành công");
            return c;
        } catch (Exception e) {
            System.out.println("kết nối không thành công" + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) throws SQLException {
        // thực hiện kết nối 
        Connection conn = ConnectDB.getConnect();
        // lấy thông tin db
        DatabaseMetaData data = conn.getMetaData();
        // hiển thị thông tin
        System.err.println("tên jdbc :" + data.getDriverName());
        System.err.println("phiên bản jdbc :" + data.getDriverVersion());
        System.err.println("tên csdl :" + data.getDatabaseProductName());
        System.err.println("phiên bản csdl :" + data.getDatabaseProductVersion());
    }
}
