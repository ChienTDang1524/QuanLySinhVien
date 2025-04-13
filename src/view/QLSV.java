/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import javax.swing.table.DefaultTableModel;
import repository.repository_login;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 *
 * @author trinh
 */
public class QLSV extends javax.swing.JFrame {
// Thêm một trường để lưu vai trò người dùng

    private String userRole;
    private repository_login rps = new repository_login();
    int index = -1;
    DefaultTableModel model = new DefaultTableModel();

    /**
     * Creates new form QLSV
     */
    public QLSV(String role) {
        initComponents();
        setLocationRelativeTo(null);
        this.userRole = role;

        // Chuẩn bị thư mục chứa hình ảnh
        prepareImageFolder();

        // Kiểm tra các file hình ảnh
        checkImageFiles();

        // Thiết lập sự kiện cho hình ảnh
        setupImageEvents();
        preloadImages();

        // Thêm sự kiện cho các nút
        addEvents();

        // Gán ngẫu nhiên hình ảnh cho sinh viên nếu chưa có
        assignRandomAvatars();

        // Load dữ liệu vào bảng
        filltoTable();

        // Cấu hình nút theo vai trò
        configureButtonsByRole();
    }
// Phương thức cấu hình nút theo vai trò

    private void configureButtonsByRole() {
        // Nếu không có role hoặc role không hợp lệ
        if (userRole == null || userRole.isEmpty()) {
            // Vô hiệu hóa tất cả các nút
            btn_new.setEnabled(false);
            btn_save.setEnabled(false);
            btn_delete.setEnabled(false);
            btn_update.setEnabled(false);

            // Thông báo và yêu cầu đăng nhập
            JOptionPane.showMessageDialog(this,
                    "Vui lòng đăng nhập để sử dụng chức năng!");

            // Đóng form và quay lại login
            dispose();
            new Login().setVisible(true);
            return;
        }

        // Phần code phân quyền như đã viết ở trước
        if ("GIANGVIEN".equals(userRole)) {
            // ...
        } else if ("CANBO_DAOTAO".equals(userRole)) {
            // ...
        }
    }

    private void filltoTable() {
        try {
            // Lấy model của bảng
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            // Xóa tất cả dữ liệu hiện có
            model.setRowCount(0);

            // Lấy danh sách sinh viên từ cơ sở dữ liệu
            List<model.Student> students = rps.getAll();

            // Thêm từng sinh viên vào bảng
            for (model.Student student : students) {
                Object[] row = new Object[]{
                    student.getMa(),
                    student.getTen(),
                    student.getEmail(),
                    student.getSdt(),
                    student.getGioiTinh() == 1 ? "Nam" : "Nữ",
                    student.getDiaChi(),
                    student.getHinh()
                };
                model.addRow(row);
            }

            // Thêm sự kiện khi chọn dòng trong bảng
            jTable1.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting() && jTable1.getSelectedRow() >= 0) {
                    showDetail();
                }
            });
            // Hiển thị thông tin sinh viên đầu tiên sau khi load dữ liệu
            if (jTable1.getRowCount() > 0) {
                jTable1.setRowSelectionInterval(0, 0); // Chọn dòng đầu tiên
                showDetail(); // Hiển thị thông tin chi tiết
            }
        } catch (Exception e) {
            System.out.println("Lỗi fill dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Hiển thị chi tiết thông tin sinh viên khi chọn từ bảng
     */
    private void showDetail() {
        try {
            // Lấy dòng đang được chọn
            int selectedRow = jTable1.getSelectedRow();
            if (selectedRow >= 0) {
                // Lấy mã sinh viên từ dòng được chọn
                String maSV = jTable1.getValueAt(selectedRow, 0).toString();

                // Lấy thông tin sinh viên từ cơ sở dữ liệu
                model.Student student = rps.getStudentByMaSV(maSV);

                if (student != null) {
                    // Hiển thị thông tin lên form
                    jTextField1.setText(student.getMa());
                    jTextField2.setText(student.getTen());
                    jTextField3.setText(student.getEmail());
                    jTextField4.setText(student.getSdt());

                    // Set giới tính
                    if (student.getGioiTinh() == 1) {
                        rdo_nam.setSelected(true);
                    } else {
                        rdo_nu.setSelected(true);
                    }

                    // Set địa chỉ
                    jTextArea1.setText(student.getDiaChi());

                    // Trong phương thức showDetail()
// Thêm dòng này trước khi gọi displayImage()
                    jLabel2.setText("Hình");
                    jLabel2.setIcon(null);
                    jPanel1.validate();
                    jPanel1.repaint();

// Sau đó mới hiển thị hình ảnh
                    displayImage(student.getHinh());
                }
            }
        } catch (Exception e) {
            System.out.println("Lỗi hiển thị chi tiết: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Map<String, ImageIcon> preloadedImages = new HashMap<>();

    private void preloadImages() {
        try {
            File folder = new File("src/hinhAnh");
            File[] files = folder.listFiles();

            // Kích thước cố định cho tất cả hình ảnh
            int width = jPanel1.getWidth() - 12;
            int height = jPanel1.getHeight() - 12;

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && (file.getName().endsWith(".png")
                            || file.getName().endsWith(".jpg")
                            || file.getName().endsWith(".jpeg"))) {
                        try {
                            // Đọc hình ảnh
                            BufferedImage originalImage = ImageIO.read(file);

                            // Tạo hình ảnh mới với kích thước cố định
                            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                            Graphics2D g2d = resizedImage.createGraphics();

                            // Thiết lập chất lượng
                            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                            // Vẽ hình
                            g2d.drawImage(originalImage, 0, 0, width, height, null);
                            g2d.dispose();

                            // Lưu vào map
                            preloadedImages.put(file.getName(), new ImageIcon(resizedImage));

                            System.out.println("Đã load trước hình: " + file.getName());
                        } catch (Exception e) {
                            System.out.println("Lỗi khi load hình: " + file.getName() + " - " + e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Lỗi preload images: " + e.getMessage());
        }
    }

// Sau đó thay đổi displayImage
    private void displayImage(String fileName) {
        try {
            // Kiểm tra xem hình đã được load trước chưa
            if (preloadedImages.containsKey(fileName)) {
                // Sử dụng hình đã load trước
                jLabel2.setIcon(preloadedImages.get(fileName));
                jLabel2.setText("");
            } else {
                // Nếu chưa, load theo cách thông thường
                // ... (phần code cũ)
            }
        } catch (Exception e) {
            jLabel2.setText(fileName);
            jLabel2.setIcon(null);
            System.out.println("Lỗi hiển thị hình ảnh: " + e.getMessage());
        }
    }

    /**
     * Hiển thị hình ảnh trong JLabel
     *
     * @param fileName Tên file hình ảnh
     */
//    private void displayImage(String fileName) {
//        try {
//            // Đường dẫn đến thư mục chứa hình ảnh
//            String imagePath = "src/hinhAnh/" + fileName;
//
//            // Đọc hình ảnh gốc
//            BufferedImage originalImage = ImageIO.read(new File(imagePath));
//
//            if (originalImage == null) {
//                System.out.println("Không thể đọc hình từ: " + imagePath);
//                jLabel2.setText(fileName);
//                jLabel2.setIcon(null);
//                return;
//            }
//
//            // Kích thước cố định cho hiển thị
//            int targetWidth = jPanel1.getWidth() - 12;
//            int targetHeight = jPanel1.getHeight() - 12;
//
//            // Tạo một BufferedImage mới với kích thước cố định
//            BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
//
//            // Vẽ hình ảnh gốc lên hình mới với kích thước đã định
//            Graphics2D g2d = resizedImage.createGraphics();
//
//            // Xóa nền và thiết lập chất lượng rendering
//            g2d.setComposite(AlphaComposite.Src);
//            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//            // Tính toán kích thước và vị trí để giữ tỷ lệ
//            double originalRatio = (double) originalImage.getWidth() / originalImage.getHeight();
//            double targetRatio = (double) targetWidth / targetHeight;
//
//            int drawWidth, drawHeight, x, y;
//
//            if (originalRatio > targetRatio) {
//                // Hình gốc rộng hơn
//                drawWidth = targetWidth;
//                drawHeight = (int) (targetWidth / originalRatio);
//                x = 0;
//                y = (targetHeight - drawHeight) / 2;
//            } else {
//                // Hình gốc cao hơn
//                drawHeight = targetHeight;
//                drawWidth = (int) (targetHeight * originalRatio);
//                x = (targetWidth - drawWidth) / 2;
//                y = 0;
//            }
//
//            // Vẽ hình với kích thước và vị trí đã tính
//            g2d.drawImage(originalImage, x, y, drawWidth, drawHeight, null);
//            g2d.dispose();
//
//            // Tạo ImageIcon từ BufferedImage đã resize
//            ImageIcon resizedIcon = new ImageIcon(resizedImage);
//
//            // Reset jLabel2 trước khi đặt hình mới
//            jLabel2.setText("");
//            jLabel2.setIcon(null);
//
//            // Hiển thị hình mới
//            jLabel2.setIcon(resizedIcon);
//
//        } catch (Exception e) {
//            jLabel2.setText(fileName);
//            jLabel2.setIcon(null);
//            System.out.println("Lỗi hiển thị hình ảnh: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
    /**
     * Chuẩn bị thư mục chứa hình ảnh
     */
    private void prepareImageFolder() {
        try {
            // Tạo thư mục hinhAnh nếu chưa tồn tại
            File imagesFolder = new File("src/hinhAnh");
            if (!imagesFolder.exists()) {
                imagesFolder.mkdirs();
                System.out.println("Đã tạo thư mục hinhAnh");
            }
        } catch (Exception e) {
            System.out.println("Lỗi khi tạo thư mục hinhAnh: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Kiểm tra các file hình ảnh có tồn tại không
     */
    private void checkImageFiles() {
        File folder = new File("src/hinhAnh");
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                System.out.println("Các file trong thư mục src/hinhAnh:");
                for (File file : files) {
                    System.out.println(" - " + file.getName());
                }
            } else {
                System.out.println("Thư mục src/hinhAnh trống hoặc không thể đọc được");
            }
        } else {
            System.out.println("Thư mục src/hinhAnh không tồn tại");
        }
    }

    /**
     * Mở hộp thoại chọn hình ảnh
     */
    private void browseImage() {
        JFileChooser fileChooser = new JFileChooser();
        // Chỉ cho phép chọn các file hình ảnh
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Hình ảnh (JPG, PNG, GIF)", "jpg", "jpeg", "png", "gif");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String fileName = selectedFile.getName();

            try {
                // Tạo thư mục hinhAnh nếu chưa tồn tại
                File imagesFolder = new File("src/hinhAnh");
                if (!imagesFolder.exists()) {
                    imagesFolder.mkdirs();
                }

                // Đường dẫn đến file đích
                File destFile = new File(imagesFolder, fileName);

                // Sao chép file đã chọn vào thư mục hinhAnh
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Hiển thị hình ảnh đã chọn
                displayImage(fileName);

                // Lưu tên file để lưu vào database
                jLabel2.setText(fileName);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi sao chép file: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Thiết lập sự kiện cho hình ảnh
     */
    private void setupImageEvents() {
        // Thêm sự kiện click vào jLabel2 để chọn hình ảnh
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                browseImage();
            }
        });
        jLabel2.setToolTipText("Click để chọn hình ảnh");

        // Thêm sự kiện click vào jPanel1 để chọn hình ảnh
        jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                browseImage();
            }
        });
        jPanel1.setToolTipText("Click để chọn hình ảnh");
    }

    /**
     * Thêm sự kiện cho các nút
     */
    private void addEvents() {
        // Sự kiện cho nút New
        btn_new.addActionListener(e -> {
            clearForm();
        });

        // Sự kiện cho nút Save
        btn_save.addActionListener(e -> {
            saveStudent();
        });

        // Sự kiện cho nút Update
        btn_update.addActionListener(e -> {
            updateStudent();
        });

        // Sự kiện cho nút Delete
        btn_delete.addActionListener(e -> {
            deleteStudent();
        });
    }

    /**
     * Xóa form
     */
    private void clearForm() {
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
        rdo_nam.setSelected(true);
        jTextArea1.setText("");
        jLabel2.setIcon(null);
        jLabel2.setText("Hình");

        // Cho phép nhập mã mới
        jTextField1.setEnabled(true);
        jTextField1.requestFocus();
    }

    /**
     * Lưu sinh viên mới
     */
    private void saveStudent() {
        try {
            // Kiểm tra vai trò trước khi lưu
            if (!"CANBO_DAOTAO".equals(userRole)) {
                JOptionPane.showMessageDialog(this, "Bạn không có quyền thực hiện chức năng này!");
                return;
            }
            try {
                // Kiểm tra dữ liệu
                if (validateForm()) {
                    // Lấy dữ liệu từ form
                    model.Student student = getStudentFromForm();

                    // Kiểm tra trùng mã
                    if (rps.isDuplicateMaSV(student.getMa())) {
                        JOptionPane.showMessageDialog(this, "Mã sinh viên đã tồn tại!");
                        return;
                    }

                    // Thay thế dòng này:
                    // boolean result = rps.addStudent(student);
                    // Bằng dòng này:
                    boolean result = rps.addStudentWithDefaultGrade(student);

                    if (result) {
                        JOptionPane.showMessageDialog(this, "Thêm sinh viên thành công!");
                        filltoTable(); // Cập nhật lại bảng
                        clearForm(); // Xóa form
                    } else {
                        JOptionPane.showMessageDialog(this, "Thêm sinh viên thất bại!");
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Cập nhật sinh viên
     */
    private void updateStudent() {
        try {
            // Kiểm tra vai trò trước khi lưu
            if (!"CANBO_DAOTAO".equals(userRole)) {
                JOptionPane.showMessageDialog(this, "Bạn không có quyền thực hiện chức năng này!");
                return;
            }
            try {
                // Kiểm tra dữ liệu
                if (validateForm()) {
                    // Lấy dữ liệu từ form
                    model.Student student = getStudentFromForm();

                    // Cập nhật sinh viên
                    boolean result = rps.updateStudent(student);

                    if (result) {
                        JOptionPane.showMessageDialog(this, "Cập nhật sinh viên thành công!");
                        filltoTable(); // Cập nhật lại bảng
                    } else {
                        JOptionPane.showMessageDialog(this, "Cập nhật sinh viên thất bại!");
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Xóa sinh viên
     */
    private void deleteStudent() {
        try {
            // Kiểm tra vai trò trước khi lưu
            if (!"CANBO_DAOTAO".equals(userRole)) {
                JOptionPane.showMessageDialog(this, "Bạn không có quyền thực hiện chức năng này!");
                return;
            }
            try {
                // Lấy mã sinh viên
                String maSV = jTextField1.getText().trim();

                if (maSV.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn sinh viên cần xóa!");
                    return;
                }

                // Hỏi xác nhận
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Bạn có chắc muốn xóa sinh viên này?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    // Xóa sinh viên
                    boolean result = rps.deleteStudent(maSV);

                    if (result) {
                        JOptionPane.showMessageDialog(this, "Xóa sinh viên thành công!");
                        filltoTable(); // Cập nhật lại bảng
                        clearForm(); // Xóa form
                    } else {
                        JOptionPane.showMessageDialog(this, "Xóa sinh viên thất bại!");
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Kiểm tra dữ liệu nhập vào form
     */
    private boolean validateForm() {
        // Kiểm tra mã sinh viên
        if (jTextField1.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã sinh viên!");
            jTextField1.requestFocus();
            return false;
        }

        // Kiểm tra họ tên
        if (jTextField2.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên sinh viên!");
            jTextField2.requestFocus();
            return false;
        }

        // Kiểm tra email
        if (jTextField3.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập email sinh viên!");
            jTextField3.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Lấy dữ liệu từ form để tạo đối tượng Student
     */
    private model.Student getStudentFromForm() {
        model.Student student = new model.Student();
        student.setMa(jTextField1.getText().trim());
        student.setTen(jTextField2.getText().trim());
        student.setEmail(jTextField3.getText().trim());
        student.setSdt(jTextField4.getText().trim());
        student.setGioiTinh(rdo_nam.isSelected() ? 1 : 0);
        student.setDiaChi(jTextArea1.getText().trim());

        // Nếu không có hình, gán một hình mặc định
        String hinhStr = jLabel2.getText().trim();
        if (hinhStr.isEmpty() || hinhStr.equals("Hình")) {
            // Gán ngẫu nhiên một trong các hình avatar1.png đến avatar5.png
            int randomAvatar = (int) (Math.random() * 5) + 1;
            student.setHinh("avatar" + randomAvatar + ".png");
        } else {
            student.setHinh(hinhStr);
        }

        return student;
    }

    /**
     * Gán ngẫu nhiên hình ảnh avatar cho sinh viên
     */
    private void assignRandomAvatars() {
        try {
            List<model.Student> students = rps.getAll();
            for (int i = 0; i < students.size(); i++) {
                model.Student student = students.get(i);
                if (student.getHinh() == null || student.getHinh().isEmpty()) {
                    // Gán hình avatar ngẫu nhiên
                    int avatarNum = (i % 5) + 1;
                    String avatarName = "avatar" + avatarNum + ".png";
                    student.setHinh(avatarName);
                    rps.updateStudent(student);
                }
            }
        } catch (Exception e) {
            System.out.println("Lỗi gán avatar ngẫu nhiên: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Khởi tạo ứng dụng
     */
    private void initialize() {
        // Chuẩn bị thư mục chứa hình ảnh
        prepareImageFolder();

        // Kiểm tra các file hình ảnh
        checkImageFiles();

        // Thiết lập sự kiện cho hình ảnh
        setupImageEvents();
        preloadImages();

        // Thêm sự kiện cho các nút
        addEvents();

        // Gán ngẫu nhiên hình ảnh cho sinh viên nếu chưa có
        assignRandomAvatars();

        // Load dữ liệu vào bảng
        filltoTable();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_Group = new javax.swing.ButtonGroup();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        rdo_nam = new javax.swing.JRadioButton();
        rdo_nu = new javax.swing.JRadioButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        btn_new = new javax.swing.JButton();
        btn_save = new javax.swing.JButton();
        btn_delete = new javax.swing.JButton();
        btn_update = new javax.swing.JButton();

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane3.setViewportView(jTextArea2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Quản Lý Sinh Viên");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 0, 255));
        jLabel1.setText("Quản Lý Sinh Viên");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "MãSV", "Họ tên", "Email", "Số ĐT", "Giới tính", "Địa chỉ", "Hình"
            }
        ));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel2.setText("Hình");
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel2MouseEntered(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel3.setText("Mã SV:");

        jLabel4.setText("Họ tên:");

        jLabel5.setText("Email:");

        jLabel6.setText("Số ĐT:");

        jLabel7.setText("Giới tính:");

        jLabel8.setText("Địa chỉ");

        btn_Group.add(rdo_nam);
        rdo_nam.setText("Nam");

        btn_Group.add(rdo_nu);
        rdo_nu.setText("Nữ");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        btn_new.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon Application/Add.png"))); // NOI18N
        btn_new.setText("New");

        btn_save.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon Application/Save.png"))); // NOI18N
        btn_save.setText("Save");
        btn_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_saveActionPerformed(evt);
            }
        });

        btn_delete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon Application/Delete.png"))); // NOI18N
        btn_delete.setText("Delete");

        btn_update.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon Application/Edit.png"))); // NOI18N
        btn_update.setText("Update");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(185, 185, 185)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(rdo_nam, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(rdo_nu, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jTextField1)
                            .addComponent(jTextField2)
                            .addComponent(jTextField3)
                            .addComponent(jTextField4)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn_new)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_save))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn_delete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_update))
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_delete, btn_new, btn_save, btn_update});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel1)
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(rdo_nam)
                            .addComponent(rdo_nu)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 39, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_new)
                            .addComponent(btn_save))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_delete)
                            .addComponent(btn_update))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_delete, btn_new, btn_save, btn_update});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel2MouseEntered

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel2MouseClicked

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jTable1MouseClicked

    private void btn_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_saveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_saveActionPerformed
    public QLSV() {
        this(""); // Gọi constructor có tham số với role rỗng
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // KHÔNG nên tự động khởi tạo form ở đây
                // new QLSV().setVisible(true);

                // Thông báo người dùng phải đăng nhập
                JOptionPane.showMessageDialog(null,
                        "Vui lòng đăng nhập để sử dụng chức năng!");
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup btn_Group;
    private javax.swing.JButton btn_delete;
    private javax.swing.JButton btn_new;
    private javax.swing.JButton btn_save;
    private javax.swing.JButton btn_update;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JRadioButton rdo_nam;
    private javax.swing.JRadioButton rdo_nu;
    // End of variables declaration//GEN-END:variables

}
