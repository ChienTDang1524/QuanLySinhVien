/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.Diem;
import repository.repository_login;
// Thêm một trường để lưu vai trò người dùng

/**
 *
 * @author trinh
 */
public class QLdiemSV extends javax.swing.JFrame {

    private repository_login diemRepo = new repository_login();
    private DefaultTableModel model = new DefaultTableModel();
    private int currentIndex = -1;
    private List<Diem> listDiem;
    private String userRole;

    /**
     * Creates new form QLdiemSV
     */
    public QLdiemSV() {
        this(""); // Gọi constructor có tham số với role rỗng
    }

    public QLdiemSV(String role) {
        initComponents();
        setLocationRelativeTo(null);
        this.userRole = role;
        init();
        loadTop3Students();

        // Vô hiệu hóa các nút cho Giảng viên
        configureButtonsByRole();
        txt_ten.setEnabled(false);
        txt_ma.setEnabled(false);
        txt_diemTB.setEnabled(false);
               
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
//    private void init() {
//
//        list.add(new Student("SV001", "Nguyen Van A", 8.5, 9.0, 7.5));
//        list.add(new Student("SV002", "Tran Thi B", 7.0, 8.5, 8.0));
//        list.add(new Student("SV003", "Le Van C", 9.0, 7.5, 8.5));
//        list.add(new Student("SV004", "Pham Thi D", 6.5, 8.0, 9.0));
//        list.add(new Student("SV005", "Hoang Van E", 8.0, 7.0, 7.5));
//        list.add(new Student("SV006", "Vo Thi F", 7.5, 9.5, 8.0));
//        list.add(new Student("SV007", "Dang Van G", 9.5, 8.0, 6.5));
//        list.add(new Student("SV008", "Bui Thi H", 8.0, 8.0, 8.0));
//        list.add(new Student("SV009", "Truong Van I", 7.5, 6.5, 9.5));
//        list.add(new Student("SV010", "Nguyen Thi K", 9.0, 9.0, 7.0));
//        model = (DefaultTableModel) tb_view.getModel();
//    }
//    public void fillTable() {
//        Comparator c = new Comparator<Student>() {
//            @Override
//            public int compare(Student o1, Student o2) {
//                if (o1.getDiemTB() < o2.getDiemTB()) {
//                    return 1;
//                } else {
//                    return -1;
//                }
//            }
//        };
//        Collections.sort(list, c);
//        model.setRowCount(0);
//        for (int i = 0; i < 3; i++) {
//            model.addRow(list.get(i).toRow());
//        }
//    }

    private void init() {
        model = (DefaultTableModel) tb_view.getModel();
        loadAllDiem();

        // Thêm sự kiện cho các nút điều hướng
        btn_back.addActionListener(e -> navigateRecord(-1));
        btn_backb.addActionListener(e -> navigateRecord(-listDiem.size()));
        btn_next.addActionListener(e -> navigateRecord(1));
        btn_nextn.addActionListener(e -> navigateRecord(listDiem.size()));
    }

    // Tải danh sách tất cả điểm
    private void loadAllDiem() {
        try {
            listDiem = diemRepo.getAllDiem();
            currentIndex = 0;
            if (!listDiem.isEmpty()) {
                showRecord(currentIndex);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Hiển thị top 3 sinh viên lên bảng
    private void loadTop3Students() {
        try {
            model.setRowCount(0);
            List<Diem> top3 = diemRepo.getTop3Students();

            for (Diem diem : top3) {
                model.addRow(diem.toRowtable());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải top 3 sinh viên: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Hiển thị thông tin của sinh viên tại vị trí index
    private void showRecord(int index) {
        if (index < 0 || index >= listDiem.size() || listDiem.isEmpty()) {
            return;
        }

        currentIndex = index;
        Diem diem = listDiem.get(index);

        txt_ma.setText(diem.getMa());
        txt_ten.setText(diem.getTen());
        txt_tienganh.setText(String.valueOf(diem.getDiemTiengAnh()));
        txt_tinhoc.setText(String.valueOf(diem.getDiemTinHoc()));
        txt_gdtc.setText(String.valueOf(diem.getDiemGDTC()));
        txt_diemTB.setText(String.format("%.2f", diem.getdiemTB()));
    }

    // Di chuyển giữa các record
    private void navigateRecord(int steps) {
        int newIndex = currentIndex + steps;

        if (steps < 0) {
            // Đi lùi
            newIndex = Math.max(0, newIndex);
        } else {
            // Đi tới
            newIndex = Math.min(listDiem.size() - 1, newIndex);
        }

        if (newIndex != currentIndex) {
            showRecord(newIndex);
        }
    }

    // Xóa trắng form
    private void clearForm() {
        txt_searchMSV.setText("");
        txt_diemTB.setText("");
        txt_ma.setText("");
        txt_gdtc.setText("");
        txt_ten.setText("");
        txt_tienganh.setText("");
        txt_tinhoc.setText("");
    }

    // Lấy dữ liệu từ form
    private Diem getDiemFromForm() {
        Diem diem = new Diem();

        try {
            diem.setMa(txt_ma.getText().trim());
            diem.setTen(txt_ten.getText().trim());
            diem.setDiemTiengAnh(Float.parseFloat(txt_tienganh.getText().trim()));
            diem.setDiemTinHoc(Float.parseFloat(txt_tinhoc.getText().trim()));
            diem.setDiemGDTC(Float.parseFloat(txt_gdtc.getText().trim()));

            return diem;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Điểm phải là số!");
            return null;
        }
    }

    // Kiểm tra dữ liệu đầu vào
    private boolean validateInput() {
        if (txt_ma.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã sinh viên không được để trống!");
            return false;
        }

        try {
            float tiengAnh = Float.parseFloat(txt_tienganh.getText().trim());
            float tinHoc = Float.parseFloat(txt_tinhoc.getText().trim());
            float gdtc = Float.parseFloat(txt_gdtc.getText().trim());

            if (tiengAnh < 0 || tiengAnh > 10
                    || tinHoc < 0 || tinHoc > 10
                    || gdtc < 0 || gdtc > 10) {
                JOptionPane.showMessageDialog(this, "Điểm phải từ 0 đến 10!");
                return false;
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Điểm phải là số!");
            return false;
        }

        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txt_searchMSV = new javax.swing.JTextField();
        btn_search = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txt_ten = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txt_ma = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txt_tienganh = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txt_tinhoc = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txt_gdtc = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txt_diemTB = new javax.swing.JTextField();
        btn_new = new javax.swing.JButton();
        btn_save = new javax.swing.JButton();
        btn_delete = new javax.swing.JButton();
        btn_update = new javax.swing.JButton();
        btn_backb = new javax.swing.JButton();
        btn_back = new javax.swing.JButton();
        btn_next = new javax.swing.JButton();
        btn_nextn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tb_view = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Quản lý điểm sinh viên");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 51, 255));
        jLabel1.setText("Quản Lý Điểm Sinh Viên");

        jLabel2.setText("Tìm kiếm");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setText("MÃ SV:");

        btn_search.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_search.setForeground(new java.awt.Color(51, 51, 255));
        btn_search.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon Application/Search.png"))); // NOI18N
        btn_search.setText("Search");
        btn_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_searchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(txt_searchMSV, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(btn_search)
                .addGap(27, 27, 27))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txt_searchMSV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_search))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel4.setText("Họ tên SV:");

        txt_ten.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_tenActionPerformed(evt);
            }
        });

        jLabel5.setText("Mã SV:");

        txt_ma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_maActionPerformed(evt);
            }
        });

        jLabel6.setText("Tiếng anh:");

        jLabel7.setText("Tin học:");

        jLabel8.setText("Giáo dục TC:");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(51, 0, 255));
        jLabel9.setText("Điểm TB:");

        txt_diemTB.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txt_diemTB.setForeground(new java.awt.Color(102, 0, 255));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(31, 31, 31)
                        .addComponent(txt_tinhoc, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txt_diemTB, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(txt_tienganh, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_gdtc, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(37, 37, 37)
                                .addComponent(txt_ma, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(txt_ten)))
                        .addGap(38, 38, 38)
                        .addComponent(jLabel9)))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txt_ten, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txt_ma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_tienganh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txt_diemTB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txt_tinhoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txt_gdtc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btn_new.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_new.setForeground(new java.awt.Color(51, 51, 255));
        btn_new.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon Application/Add.png"))); // NOI18N
        btn_new.setText("New");
        btn_new.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_newActionPerformed(evt);
            }
        });

        btn_save.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_save.setForeground(new java.awt.Color(51, 51, 255));
        btn_save.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon Application/Save.png"))); // NOI18N
        btn_save.setText("Save");
        btn_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_saveActionPerformed(evt);
            }
        });

        btn_delete.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_delete.setForeground(new java.awt.Color(51, 51, 255));
        btn_delete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon Application/Delete.png"))); // NOI18N
        btn_delete.setText("New");
        btn_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_deleteActionPerformed(evt);
            }
        });

        btn_update.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_update.setForeground(new java.awt.Color(51, 51, 255));
        btn_update.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon Application/Edit.png"))); // NOI18N
        btn_update.setText("Update");
        btn_update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_updateActionPerformed(evt);
            }
        });

        btn_backb.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon Application/ui.png"))); // NOI18N
        btn_backb.setToolTipText("");

        btn_back.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon Application/left-arrow.png"))); // NOI18N

        btn_next.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon Application/play-button.png"))); // NOI18N

        btn_nextn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon Application/next.png"))); // NOI18N

        tb_view.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Mã SV", "Họ tên SV", "Tiếng anh", "Tin học", "Giáo dục TC", "Điểm TB"
            }
        ));
        tb_view.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tb_viewMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tb_view);

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(51, 51, 255));
        jLabel10.setText("3 sinh viên có điểm cao nhất");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(189, 189, 189))
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn_backb, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_back)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_next)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_nextn)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_new)
                            .addComponent(btn_save)
                            .addComponent(btn_delete)
                            .addComponent(btn_update))
                        .addGap(23, 23, 23))))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel10)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_delete, btn_new, btn_save, btn_update});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_back, btn_backb, btn_next, btn_nextn});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btn_new)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn_save)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn_delete)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn_update)))
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btn_back)
                            .addComponent(btn_backb, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_next)))
                    .addComponent(btn_nextn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_delete, btn_new, btn_save, btn_update});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_back, btn_backb, btn_next, btn_nextn});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_searchActionPerformed
        // TODO add your handling code here:
//        for (Student student : list) {
//            if (txt_searchMSV.getText().equals(student.getMaSV())) {
//                txt_ma.setText(student.getMaSV());
//                txt_ten.setText(student.getHoTen());
//                txt_tienganh.setText(String.valueOf(student.getTiengAnh()));
//                txt_tinhoc.setText(String.valueOf(student.getTinHoc()));
//                txt_gdtc.setText(String.valueOf(student.getGdtc()));
//                txt_diemTB.setText(String.format("%.2f", student.getDiemTB()));
//            } else {
//            }
//        }
        String maSV = txt_searchMSV.getText().trim();

        if (maSV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã sinh viên để tìm kiếm!");
            return;
        }

        Diem diem = diemRepo.getDiemByMaSV(maSV);

        if (diem != null) {
            txt_ma.setText(diem.getMa());
            txt_ten.setText(diem.getTen());
            txt_tienganh.setText(String.valueOf(diem.getDiemTiengAnh()));
            txt_tinhoc.setText(String.valueOf(diem.getDiemTinHoc()));
            txt_gdtc.setText(String.valueOf(diem.getDiemGDTC()));
            txt_diemTB.setText(String.format("%.2f", diem.getdiemTB()));
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy sinh viên có mã: " + maSV);
        clearForm();
        }
    }//GEN-LAST:event_btn_searchActionPerformed

    private void txt_maActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_maActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_maActionPerformed

    private void btn_newActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_newActionPerformed
        // TODO add your handling code here:
        clearForm();
    }//GEN-LAST:event_btn_newActionPerformed

    private void btn_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_saveActionPerformed
//        // TODO add your handling code here:
//        Student s = new Student(txt_ma.getText(), txt_ten.getText(), Double.valueOf(txt_tienganh.getText()), Double.valueOf(txt_tinhoc.getText()), Double.valueOf(txt_gdtc.getText()));
//        int index = 0;
//        for (int i = 0; i < list.size(); i++) {
//            if (list.get(i).getMaSV().equals(txt_ma.getText())) {
//                index = i;
//                break;
//            }
//        }
//        txt_diemTB.setText(String.format("%.2f", s.getDiemTB()));
//
//        list.set(index, s);
//        fillTable();
        // Kiểm tra vai trò trước khi lưu
        if (!"GIANGVIEN".equals(userRole)) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền thực hiện chức năng này!");
            return;
        }

        if (!validateInput()) {
            return;
        }

        Diem diem = getDiemFromForm();

        if (diem == null) {
            return;
        }

        // Kiểm tra mã SV đã tồn tại trong bảng STUDENTS chưa
        repository_login studentRepo = new repository_login();
        model.Student student = studentRepo.getStudentByMaSV(diem.getMa());

        if (student == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy sinh viên có mã: " + diem.getMa());
            return;
        }

        // Kiểm tra sinh viên đã có điểm chưa
        Diem existingDiem = diemRepo.getDiemByMaSV(diem.getMa());

        if (existingDiem == null) {
            // Nếu chưa có điểm, thêm mới
            boolean result = diemRepo.addDiem(diem);

            if (result) {
                JOptionPane.showMessageDialog(this, "Thêm điểm thành công!");
                loadAllDiem();
                loadTop3Students();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm điểm thất bại!");
            }
        } else {
            // Nếu đã có điểm, luôn cập nhật (không cần hỏi)
            boolean result = diemRepo.updateDiem(diem);

            if (result) {
                JOptionPane.showMessageDialog(this, "Cập nhật điểm thành công!");
                loadAllDiem();
                loadTop3Students();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật điểm thất bại!");
            }
        }
    }//GEN-LAST:event_btn_saveActionPerformed

    private void btn_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_deleteActionPerformed
//        // TODO add your handling code here:
//        int index = 0;
//        for (int i = 0; i < list.size(); i++) {
//            if (list.get(i).getMaSV().equals(txt_ma.getText())) {
//                index = i;
//                break;
//            }
//        }
//        list.remove(index);
//        fillTable();
        if (!"GIANGVIEN".equals(userRole)) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền thực hiện chức năng này!");
            return;
        }
        String maSV = txt_ma.getText().trim();

        if (maSV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sinh viên cần xóa điểm!");
            return;
        }

        int option = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa điểm của sinh viên này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            boolean result = diemRepo.deleteDiem(maSV);

            if (result) {
                JOptionPane.showMessageDialog(this, "Xóa điểm thành công!");
                clearForm();
                loadAllDiem();
                loadTop3Students();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa điểm thất bại!");
            }
        }


    }//GEN-LAST:event_btn_deleteActionPerformed

    private void btn_updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_updateActionPerformed
//        // TODO add your handling code here:
//         Student s = new Student(txt_ma.getText(), txt_ten.getText(), Double.valueOf(txt_tienganh.getText()), Double.valueOf(txt_tinhoc.getText()), Double.valueOf(txt_gdtc.getText()));
//        int index = 0;
//        for (int i = 0; i < list.size(); i++) {
//            if (list.get(i).getMaSV().equals(txt_ma.getText())) {
//                index = i;
//                break;
//            }
//        }
//        txt_diemTB.setText(String.format("%.2f", s.getDiemTB()));
//
//        list.set(index, s);
//        fillTable();
        if (!"GIANGVIEN".equals(userRole)) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền thực hiện chức năng này!");
            return;
        }
        if (!validateInput()) {
            return;
        }

        Diem diem = getDiemFromForm();

        if (diem == null) {
            return;
        }

        // Kiểm tra sinh viên có tồn tại không
        repository_login studentRepo = new repository_login();
        model.Student student = studentRepo.getStudentByMaSV(diem.getMa());

        if (student == null) {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy sinh viên có mã: " + diem.getMa());
            return;
        }

        boolean result = diemRepo.updateDiem(diem);

        if (result) {
            JOptionPane.showMessageDialog(this, "Cập nhật điểm thành công!");
            loadAllDiem();
            loadTop3Students();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật điểm thất bại! Vui lòng kiểm tra lại.");
        }
    }//GEN-LAST:event_btn_updateActionPerformed

    private void tb_viewMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tb_viewMouseClicked
        // TODO add your handling code here:
        int row = tb_view.rowAtPoint(evt.getPoint());
        if (row >= 0) {
            // Lấy dữ liệu từ dòng được chọn
            String maSV = tb_view.getValueAt(row, 0).toString();
            String ten = tb_view.getValueAt(row, 1).toString();
            float diemTiengAnh = Float.parseFloat(tb_view.getValueAt(row, 2).toString());
            float diemTinHoc = Float.parseFloat(tb_view.getValueAt(row, 3).toString());
            float diemGDTC = Float.parseFloat(tb_view.getValueAt(row, 4).toString());

            // Hiển thị dữ liệu lên form
            txt_ma.setText(maSV);
            txt_ten.setText(ten);
            txt_tienganh.setText(String.valueOf(diemTiengAnh));
            txt_tinhoc.setText(String.valueOf(diemTinHoc));
            txt_gdtc.setText(String.valueOf(diemGDTC));
            txt_diemTB.setText(String.format("%.2f",
                    (diemTiengAnh + diemTinHoc + diemGDTC) / 3));
        }
    }//GEN-LAST:event_tb_viewMouseClicked

    private void txt_tenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_tenActionPerformed
        // TODO add your handling code here:
        // Khóa và thay đổi màu nền của TextField

    }//GEN-LAST:event_txt_tenActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // KHÔNG nên tự động khởi tạo form ở đây
                // new QLdiemSV().setVisible(true);

                // Thông báo người dùng phải đăng nhập
                JOptionPane.showMessageDialog(null,
                        "Vui lòng đăng nhập để sử dụng chức năng!");
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_back;
    private javax.swing.JButton btn_backb;
    private javax.swing.JButton btn_delete;
    private javax.swing.JButton btn_new;
    private javax.swing.JButton btn_next;
    private javax.swing.JButton btn_nextn;
    private javax.swing.JButton btn_save;
    private javax.swing.JButton btn_search;
    private javax.swing.JButton btn_update;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tb_view;
    private javax.swing.JTextField txt_diemTB;
    private javax.swing.JTextField txt_gdtc;
    private javax.swing.JTextField txt_ma;
    private javax.swing.JTextField txt_searchMSV;
    private javax.swing.JTextField txt_ten;
    private javax.swing.JTextField txt_tienganh;
    private javax.swing.JTextField txt_tinhoc;
    // End of variables declaration//GEN-END:variables

}
