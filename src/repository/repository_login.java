/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import dbConnect.ConnectDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Diem;
import model.Student;

/**
 *
 * @author trinh
 */
public class repository_login {

    private Connection c = null;
    private ResultSet rs = null;
    private PreparedStatement ps = null;
    private String query = null;
    // Thêm phương thức mã hóa mật khẩu (ví dụ đơn giản)

    private String encryptPassword(String password) {
        // Đây chỉ là một phương thức mã hóa đơn giản, 
        // trong thực tế nên sử dụng các thuật toán mã hóa mạnh như BCrypt
        return new StringBuilder(password).reverse().toString();
    }

    private void logLogin(String username, String result) {
        // Trong một ứng dụng thực tế, bạn có thể ghi log vào file hoặc database
        System.out.println(java.time.LocalDateTime.now()
                + " - User: " + username
                + " - Login Result: " + result);
    }
    // Sửa phương thức getRole để sử dụng mã hóa

    public String getRole(String use, String pass) {
        query = "SELECT role FROM USERS WHERE username = ? AND password = ?";

        try {
            c = dbConnect.ConnectDB.getConnect();

            // Kiểm tra kết nối
            if (c == null) {
                System.out.println("Kết nối cơ sở dữ liệu thất bại");
                return "NO";
            }

            ps = c.prepareStatement(query);
            ps.setString(1, use);
            ps.setString(2, pass);

            rs = ps.executeQuery();

            String role = "NO";
            if (rs.next()) {
                role = rs.getString("role");
                System.out.println("Đăng nhập thành công - Role: " + role);
            } else {
                System.out.println("Không tìm thấy người dùng");
            }

            // Đóng các kết nối
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (c != null) {
                c.close();
            }

            return role;
        } catch (Exception e) {
            System.out.println("Lỗi truy vấn: " + e.getMessage());
            e.printStackTrace();
            return "NO";
        }
    }

    // quản lý sinh viên 
    public ArrayList<model.Student> getAll() {
        //1. Tạo danh sách sinh viên
        ArrayList<model.Student> arrStudent = new ArrayList<>();
        c = dbConnect.ConnectDB.getConnect();  //2. Kết nối DB   
        query = "select * from STUDENTS"; //3. Câu lệnh truy vấn  
        try {
            ps = c.prepareStatement(query);//4. Thực thi câu lệnh
            rs = ps.executeQuery();//5. Lấy DL sau khi thực thi            
            while (rs.next()) {
                model.Student student = new model.Student(); //5.1 Tạo đối tượng student                
                //5.2 set các giá trị cho đối tượng
                student.setMa(rs.getString(1));
                student.setTen(rs.getString(2));
                student.setEmail(rs.getString(3));
                student.setSdt(rs.getString(4));
                student.setGioiTinh(rs.getInt(5));

                student.setDiaChi(rs.getString(6));
                arrStudent.add(student);//Thêm đối tượng vào danh sách   
            }
            rs.close();
            ps.close();
            c.close();
            return arrStudent;
        } catch (Exception e) {
            return null;
        }
    }
    // Phương thức tìm kiếm sinh viên theo mã sinh viên

    public Student getStudentByMaSV(String maSV) {
        query = "SELECT * FROM STUDENTS WHERE MASV = ?";
        Student student = null;

        try {
            c = ConnectDB.getConnect();
            ps = c.prepareStatement(query);
            ps.setString(1, maSV);
            rs = ps.executeQuery();

            if (rs.next()) {
                student = new Student();
                student.setMa(rs.getString("MASV"));
                student.setTen(rs.getString("Hoten"));
                student.setEmail(rs.getString("Email"));
                student.setSdt(rs.getString("SoDT"));
                student.setGioiTinh(rs.getInt("Gioitinh"));
                student.setDiaChi(rs.getString("Diachi"));
                student.setHinh(rs.getString("Hinh"));
            }

            rs.close();
            ps.close();
            c.close();
        } catch (Exception e) {
            System.out.println("Lỗi tìm kiếm sinh viên: " + e.getMessage());
        }

        return student;
    }

    // Phương thức thêm sinh viên mới
    public boolean addStudent(Student student) {
        query = "INSERT INTO STUDENTS (MASV, Hoten, Email, SoDT, Gioitinh, Diachi, Hinh) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            c = ConnectDB.getConnect();
            ps = c.prepareStatement(query);

            ps.setString(1, student.getMa());
            ps.setString(2, student.getTen());
            ps.setString(3, student.getEmail());
            ps.setString(4, student.getSdt());
            ps.setInt(5, student.getGioiTinh());
            ps.setString(6, student.getDiaChi());
            ps.setString(7, student.getHinh());

            int result = ps.executeUpdate();
            ps.close();
            c.close();

            return result > 0;
        } catch (Exception e) {
            System.out.println("Lỗi thêm sinh viên: " + e.getMessage());
            return false;
        }
    }

    // Phương thức cập nhật thông tin sinh viên
    public boolean updateStudent(Student student) {
        query = "UPDATE STUDENTS SET Hoten = ?, Email = ?, SoDT = ?, Gioitinh = ?, Diachi = ?, Hinh = ? WHERE MASV = ?";

        try {
            c = ConnectDB.getConnect();
            ps = c.prepareStatement(query);

            ps.setString(1, student.getTen());
            ps.setString(2, student.getEmail());
            ps.setString(3, student.getSdt());
            ps.setInt(4, student.getGioiTinh());
            ps.setString(5, student.getDiaChi());
            ps.setString(6, student.getHinh());
            ps.setString(7, student.getMa());

            int result = ps.executeUpdate();
            ps.close();
            c.close();

            return result > 0;
        } catch (Exception e) {
            System.out.println("Lỗi cập nhật sinh viên: " + e.getMessage());
            return false;
        }
    }

    // Phương thức xóa sinh viên
    public boolean deleteStudent(String maSV) {
        // Xóa điểm của sinh viên trước (nếu có)
        String deleteGradeQuery = "DELETE FROM GRADE WHERE MASV = ?";
        // Sau đó xóa sinh viên
        String deleteStudentQuery = "DELETE FROM STUDENTS WHERE MASV = ?";

        try {
            c = ConnectDB.getConnect();

            // Xóa điểm trước
            ps = c.prepareStatement(deleteGradeQuery);
            ps.setString(1, maSV);
            ps.executeUpdate();
            ps.close();

            // Xóa sinh viên
            ps = c.prepareStatement(deleteStudentQuery);
            ps.setString(1, maSV);
            int result = ps.executeUpdate();
            ps.close();
            c.close();

            return result > 0;
        } catch (Exception e) {
            System.out.println("Lỗi xóa sinh viên: " + e.getMessage());
            return false;
        }
    }

    // Phương thức kiểm tra trùng mã sinh viên
    public boolean isDuplicateMaSV(String maSV) {
        query = "SELECT COUNT(*) FROM STUDENTS WHERE MASV = ?";
        boolean isDuplicate = false;

        try {
            c = ConnectDB.getConnect();
            ps = c.prepareStatement(query);
            ps.setString(1, maSV);
            rs = ps.executeQuery();

            if (rs.next()) {
                isDuplicate = rs.getInt(1) > 0;
            }

            rs.close();
            ps.close();
            c.close();
        } catch (Exception e) {
            System.out.println("Lỗi kiểm tra trùng mã sinh viên: " + e.getMessage());
        }

        return isDuplicate;
    }

    // Phương thức tìm kiếm sinh viên theo tên
    public List<Student> searchStudentsByName(String name) {
        List<Student> list = new ArrayList<>();
        query = "SELECT * FROM STUDENTS WHERE Hoten LIKE ?";

        try {
            c = ConnectDB.getConnect();
            ps = c.prepareStatement(query);
            ps.setString(1, "%" + name + "%");
            rs = ps.executeQuery();

            while (rs.next()) {
                Student student = new Student();
                student.setMa(rs.getString("MASV"));
                student.setTen(rs.getString("Hoten"));
                student.setEmail(rs.getString("Email"));
                student.setSdt(rs.getString("SoDT"));
                student.setGioiTinh(rs.getInt("Gioitinh"));
                student.setDiaChi(rs.getString("Diachi"));
                student.setHinh(rs.getString("Hinh"));

                list.add(student);
            }

            rs.close();
            ps.close();
            c.close();
        } catch (Exception e) {
            System.out.println("Lỗi tìm kiếm sinh viên theo tên: " + e.getMessage());
        }

        return list;
    }
    // Lấy danh sách điểm của tất cả sinh viên

    public List<Diem> getAllDiem() {
        List<Diem> list = new ArrayList<>();
        query = "SELECT g.ID, s.MASV, s.Hoten, g.Tienganh, g.Tinhoc, g.GDTC "
                + "FROM GRADE g JOIN STUDENTS s ON g.MASV = s.MASV";

        try {
            c = ConnectDB.getConnect();
            ps = c.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                Diem diem = new Diem();
                diem.setMa(rs.getString("MASV"));
                diem.setTen(rs.getString("Hoten"));
                diem.setDiemTiengAnh(rs.getFloat("Tienganh"));
                diem.setDiemTinHoc(rs.getFloat("Tinhoc"));
                diem.setDiemGDTC(rs.getFloat("GDTC"));

                list.add(diem);
            }

            rs.close();
            ps.close();
            c.close();
        } catch (Exception e) {
            System.out.println("Lỗi truy vấn danh sách điểm: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    public boolean addStudentWithDefaultGrade(Student student) {
        boolean success = false;
        try {
            c = ConnectDB.getConnect();
            c.setAutoCommit(false); // Bắt đầu transaction

            // Thêm sinh viên
            String insertStudentQuery = "INSERT INTO STUDENTS (MASV, Hoten, Email, SoDT, Gioitinh, Diachi, Hinh) VALUES (?, ?, ?, ?, ?, ?, ?)";
            ps = c.prepareStatement(insertStudentQuery);
            ps.setString(1, student.getMa());
            ps.setString(2, student.getTen());
            ps.setString(3, student.getEmail());
            ps.setString(4, student.getSdt());
            ps.setInt(5, student.getGioiTinh());
            ps.setString(6, student.getDiaChi());
            ps.setString(7, student.getHinh());
            ps.executeUpdate();
            ps.close();

            // Lấy ID mới nhất trong bảng GRADE
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(ID) AS MaxId FROM GRADE");
            int newId = 1; // Mặc định nếu bảng trống
            if (rs.next()) {
                newId = rs.getInt("MaxId") + 1;
            }
            rs.close();
            stmt.close();

            // Thêm điểm mặc định
            String insertGradeQuery = "INSERT INTO GRADE (ID, MASV, Tienganh, Tinhoc, GDTC) VALUES (?, ?, 0, 0, 0)";
            ps = c.prepareStatement(insertGradeQuery);
            ps.setInt(1, newId);
            ps.setString(2, student.getMa());
            ps.executeUpdate();
            ps.close();

            c.commit(); // Hoàn tất transaction
            success = true;
        } catch (Exception e) {
            System.out.println("Lỗi thêm sinh viên và điểm mặc định: " + e.getMessage());
            e.printStackTrace();
            try {
                c.rollback(); // Rollback nếu có lỗi
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                c.setAutoCommit(true);
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    // Lấy top 3 sinh viên có điểm cao nhất
    public List<Diem> getTop3Students() {
        List<Diem> list = new ArrayList<>();
        query = "SELECT s.MASV, s.Hoten, g.Tienganh, g.Tinhoc, g.GDTC, "
                + "(COALESCE(g.Tienganh, 0) + COALESCE(g.Tinhoc, 0) + COALESCE(g.GDTC, 0)) / 3 AS DiemTB "
                + "FROM GRADE g JOIN STUDENTS s ON g.MASV = s.MASV "
                + "ORDER BY DiemTB DESC "
                + "OFFSET 0 ROWS FETCH NEXT 3 ROWS ONLY";

        try {
            c = ConnectDB.getConnect();
            ps = c.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                Diem diem = new Diem();
                diem.setMa(rs.getString("MASV"));
                diem.setTen(rs.getString("Hoten"));
                diem.setDiemTiengAnh(rs.getFloat("Tienganh"));
                diem.setDiemTinHoc(rs.getFloat("Tinhoc"));
                diem.setDiemGDTC(rs.getFloat("GDTC"));

                list.add(diem);
            }

            rs.close();
            ps.close();
            c.close();
        } catch (Exception e) {
            System.out.println("Lỗi truy vấn top 3 sinh viên: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    public Diem getDiemByMaSV(String maSV) {
        query = "SELECT s.MASV, s.Hoten, COALESCE(g.Tienganh, 0) as Tienganh, "
                + "COALESCE(g.Tinhoc, 0) as Tinhoc, COALESCE(g.GDTC, 0) as GDTC "
                + "FROM STUDENTS s LEFT JOIN GRADE g ON s.MASV = g.MASV "
                + "WHERE s.MASV = ?";
        Diem diem = null;

        try {
            c = ConnectDB.getConnect();
            ps = c.prepareStatement(query);
            ps.setString(1, maSV);
            rs = ps.executeQuery();

            if (rs.next()) {
                diem = new Diem();
                diem.setMa(rs.getString("MASV"));
                diem.setTen(rs.getString("Hoten"));
                diem.setDiemTiengAnh(rs.getFloat("Tienganh"));
                diem.setDiemTinHoc(rs.getFloat("Tinhoc"));
                diem.setDiemGDTC(rs.getFloat("GDTC"));
            }

            rs.close();
            ps.close();
            c.close();
        } catch (Exception e) {
            System.out.println("Lỗi tìm kiếm điểm: " + e.getMessage());
            e.printStackTrace();
        }

        return diem;
    }

    public boolean checkStudentInGrade(String maSV) {
        query = "SELECT COUNT(*) AS count FROM GRADE WHERE MASV = ?";

        try {
            c = ConnectDB.getConnect();
            ps = c.prepareStatement(query);
            ps.setString(1, maSV);

            rs = ps.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("Số lần xuất hiện của " + maSV + " trong bảng GRADE: " + count);
                return count > 0;
            }

            ps.close();
            c.close();

            return false;
        } catch (Exception e) {
            System.out.println("Lỗi kiểm tra sinh viên: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật điểm cho sinh viên
    public boolean updateDiem(Diem diem) {
        // Kiểm tra sinh viên có trong bảng GRADE chưa
        if (!checkStudentInGrade(diem.getMa())) {
            // Nếu chưa, thêm mới
            return addDiem(diem);
        }

        query = "UPDATE GRADE SET Tienganh = ?, Tinhoc = ?, GDTC = ? WHERE MASV = ?";

        try {
            c = ConnectDB.getConnect();
            ps = c.prepareStatement(query);

            ps.setFloat(1, diem.getDiemTiengAnh());
            ps.setFloat(2, diem.getDiemTinHoc());
            ps.setFloat(3, diem.getDiemGDTC());
            ps.setString(4, diem.getMa());

            int result = ps.executeUpdate();

            ps.close();
            c.close();

            return result > 0;
        } catch (Exception e) {
            System.out.println("Lỗi cập nhật điểm: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    // Thêm điểm mới cho sinh viên
    // Sửa phương thức addDiem để đảm bảo có ID

    public boolean addDiem(Diem diem) {
        query = "INSERT INTO GRADE (ID, MASV, Tienganh, Tinhoc, GDTC) VALUES (?, ?, ?, ?, ?)";

        try {
            c = ConnectDB.getConnect();

            // Lấy ID mới nhất
            int newId = getMaxId() + 1;

            ps = c.prepareStatement(query);
            ps.setInt(1, newId);
            ps.setString(2, diem.getMa());
            ps.setFloat(3, diem.getDiemTiengAnh());
            ps.setFloat(4, diem.getDiemTinHoc());
            ps.setFloat(5, diem.getDiemGDTC());

            int result = ps.executeUpdate();

            ps.close();
            c.close();

            return result > 0;
        } catch (Exception e) {
            System.out.println("Lỗi thêm điểm: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Xóa điểm của sinh viên
    public boolean deleteDiem(String maSV) {
        query = "DELETE FROM GRADE WHERE MASV = ?";

        try {
            c = ConnectDB.getConnect();
            ps = c.prepareStatement(query);
            ps.setString(1, maSV);

            int result = ps.executeUpdate();
            ps.close();
            c.close();

            return result > 0;
        } catch (Exception e) {
            System.out.println("Lỗi xóa điểm: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Lấy ID lớn nhất trong bảng GRADE
    private int getMaxId() {
        int maxId = 0;
        try {
            String queryMaxId = "SELECT MAX(ID) AS MaxId FROM GRADE";
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(queryMaxId);

            if (rs.next()) {
                maxId = rs.getInt("MaxId");
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.out.println("Lỗi lấy ID lớn nhất: " + e.getMessage());
            e.printStackTrace();
        }
        return maxId;
    }

}
