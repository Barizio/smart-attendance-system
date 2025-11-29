/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package saasms;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JFrame;

/**
 *
 * @author adisa
 */
public class supervisorDashboard extends javax.swing.JFrame {

    /**
     * Creates new form supervisorDashboard
     */
    private final String unit = UserSession1.getStaffUnit();

    public supervisorDashboard() {

        initComponents();
                loadAttendanceLogs("None", null);
                loadStaffInUnit();

//         new java.util.Timer().schedule(
//                new java.util.TimerTask() {
//            @Override
//            public void run() {
//                SwingUtilities.invokeLater(() -> loadStaffInUnit());
//            }
//        },
//                0, // initial delay
//                10000 // repeat every 10 seconds (in milliseconds)
//        );
        // Periodically refresh shift swap table every 10 seconds
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> loadPendingShiftSwaps());
            }
        },
                0, // initial delay
                10000 // repeat every 10 seconds (in milliseconds)
        );
//        new java.util.Timer().schedule(
//                new java.util.TimerTask() {
//            @Override
//            public void run() {
//                SwingUtilities.invokeLater(() -> loadAttendanceLogs("None", null));
//            }
//        },
//                0, // Start immediately
//                10000 // Refresh every 10 seconds (10000 ms)
//        );
        String[] shiftOptions = {"Morning", "Afternoon", "Evening", "Off"};
        JComboBox<String> comboBox = new JComboBox<>(shiftOptions);

// Step 2: Add it as an editor to specific columns
// Assuming your JTable is named tblRoster
// Columns: 0 = Staff ID, 1 = Name, 2 = Role, 3–9 = Days of Week
        for (int col = 3; col <= 9; col++) {
            tblRoster.getColumnModel().getColumn(col).setCellEditor(new DefaultCellEditor(comboBox));
        }

        jTextField2.setVisible(false);
        jDateChooser1.setVisible(false);
        jLabel6.setVisible(false);
        jButton10.setVisible(false);

        jLabel9.setText("Unit Supervisor – " + unit); // if you have a label at the top
        jLabel10.setText("Unit Supervisor – " + unit); // if you have a label at the top
        jLabel8.setText("Unit Supervisor – " + unit); // if you have a label at the top
        jLabel11.setText("Unit Supervisor – " + unit); // if you have a label at the top

        loadRosterTable();
    }

    

    public void loadStaffInUnit() {
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/staffattendanceandshiftmanagementschema",
                "root", "Adesokan2310*")) {

            String sql = "SELECT staff_id, name, email, phone_number FROM users_tb WHERE unit = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, unit); // the supervisor's unit
            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0); // clear previous rows

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("staff_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone_number")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading staff list: " + e.getMessage());
        }
    }

public void loadAttendanceLogs(String filterType, String filterValue) {
    try (Connection con = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/staffattendanceandshiftmanagementschema",
            "root", "Adesokan2310*")) {

        String sql = "SELECT a.staff_id, u.name, a.log_date, a.sign_in_time, a.sign_out_time "
                   + "FROM attendance_logs a JOIN users_tb u ON a.staff_id = u.staff_id "
                   + "WHERE a.unit = ?";

        if (filterType.equals("Date")) {
            sql += " AND a.log_date = ?";
        } else if (filterType.equals("Staff ID")) {
            sql += " AND a.staff_id = ?";
        }

        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, unit); // supervisor's unit

        if (filterType.equals("Date")) {
            java.util.Date date = jDateChooser1.getDate();
            pst.setDate(2, new java.sql.Date(date.getTime()));
        } else if (filterType.equals("Staff ID")) {
            pst.setInt(2, Integer.parseInt(filterValue));
        }

        ResultSet rs = pst.executeQuery();
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        model.setRowCount(0);

        while (rs.next()) {
            int staffId = rs.getInt("staff_id");
            String staffName = rs.getString("name");
            java.sql.Date logDate = rs.getDate("log_date");
            java.sql.Time signIn = rs.getTime("sign_in_time");
            java.sql.Time signOut = rs.getTime("sign_out_time");

            // Get assigned shift start and end time for that day
            PreparedStatement shiftStmt = con.prepareStatement(
                    "SELECT s.start_time, s.end_time FROM staff_rosters r "
                  + "JOIN shifts s ON r.shift_id = s.shift_id "
                  + "WHERE r.staff_id = ? AND r.shift_date = ?"
            );
            shiftStmt.setInt(1, staffId);
            shiftStmt.setDate(2, logDate);
            ResultSet shiftRS = shiftStmt.executeQuery();

            java.sql.Time shiftStart = null;
            java.sql.Time shiftEnd = null;
            if (shiftRS.next()) {
                shiftStart = shiftRS.getTime("start_time");
                shiftEnd = shiftRS.getTime("end_time");
            }

            String status;

            // Determine attendance status
            if (signIn == null && signOut == null) {
                status = "Absent";
            } else if (shiftStart != null && signIn != null && signIn.after(shiftStart)) {
                status = "Late";
            } else if (shiftEnd != null && signOut != null && signOut.before(shiftEnd)) {
                status = "Left Early";
            } else {
                status = "Present";
            }

            model.addRow(new Object[]{
                staffId,
                staffName,
                logDate,
                signIn,
                signOut,
                status
            });

            shiftRS.close();
            shiftStmt.close();
        }

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading attendance logs: " + e.getMessage());
    }
}


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox2 = new javax.swing.JComboBox<>();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        tblRoster = new javax.swing.JTable();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jButton22 = new javax.swing.JButton();
        weekPicker = new com.toedter.calendar.JDateChooser();
        jLabel12 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jButton10 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jButton11 = new javax.swing.JButton();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel6 = new javax.swing.JLabel();
        jButton19 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jButton12 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton16 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jButton18 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jButton20 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jButton38 = new javax.swing.JButton();

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(30, 30, 46));

        jPanel2.setBackground(new java.awt.Color(30, 30, 46));

        tblRoster.setBackground(new java.awt.Color(255, 255, 255));
        tblRoster.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        tblRoster.setForeground(new java.awt.Color(0, 0, 0));
        tblRoster.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Staff ID", "Name", "Role", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, true, true, true, true, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane7.setViewportView(tblRoster);

        jButton13.setBackground(new java.awt.Color(74, 144, 226));
        jButton13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton13.setForeground(new java.awt.Color(255, 255, 255));
        jButton13.setText("Upload Roster");
        jButton13.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton13.setBorderPainted(false);
        jButton13.setFocusPainted(false);
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton14.setBackground(new java.awt.Color(74, 144, 226));
        jButton14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton14.setForeground(new java.awt.Color(255, 255, 255));
        jButton14.setText("Download Roster");
        jButton14.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton14.setBorderPainted(false);
        jButton14.setFocusPainted(false);
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton15.setBackground(new java.awt.Color(74, 144, 226));
        jButton15.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton15.setForeground(new java.awt.Color(255, 255, 255));
        jButton15.setText("Send Roster to Staff");
        jButton15.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton15.setBorderPainted(false);
        jButton15.setFocusPainted(false);
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jButton17.setBackground(new java.awt.Color(255, 0, 0));
        jButton17.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton17.setForeground(new java.awt.Color(255, 255, 255));
        jButton17.setText("Log out");
        jButton17.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton17.setBorderPainted(false);
        jButton17.setFocusPainted(false);
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("Insert descrip");

        jButton22.setBackground(new java.awt.Color(74, 144, 226));
        jButton22.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton22.setForeground(new java.awt.Color(255, 255, 255));
        jButton22.setText("Reload Roster");
        jButton22.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton22.setBorderPainted(false);
        jButton22.setFocusPainted(false);
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Select week Starting From:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(51, 51, 51))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(weekPicker, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(283, 283, 283))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(301, 301, 301)
                        .addComponent(jButton13)
                        .addGap(18, 18, 18)
                        .addComponent(jButton14)
                        .addGap(102, 102, 102)
                        .addComponent(jButton22))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(350, 350, 350)
                        .addComponent(jButton15))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 1340, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(94, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton17)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(weekPicker, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(59, 59, 59)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton13)
                    .addComponent(jButton14)
                    .addComponent(jButton22))
                .addGap(39, 39, 39)
                .addComponent(jButton15)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Roster", jPanel2);

        jPanel5.setBackground(new java.awt.Color(30, 30, 46));

        jButton10.setBackground(new java.awt.Color(74, 144, 226));
        jButton10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton10.setForeground(new java.awt.Color(255, 255, 255));
        jButton10.setText("Filter");
        jButton10.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton10.setBorderPainted(false);
        jButton10.setFocusPainted(false);
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Filter By");

        jTable2.setBackground(new java.awt.Color(30, 30, 46));
        jTable2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTable2.setForeground(new java.awt.Color(255, 255, 255));
        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Staff ID", "Name", "Log Date", "Sign-In Time ", "Sign-out Time", "Status "
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        jScrollPane3.setViewportView(jScrollPane2);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Attendance Logs");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Criteria", "Date", "Staff ID" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jButton11.setBackground(new java.awt.Color(74, 144, 226));
        jButton11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton11.setForeground(new java.awt.Color(255, 255, 255));
        jButton11.setText("Generate Report");
        jButton11.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton11.setBorderPainted(false);
        jButton11.setFocusPainted(false);
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Insert");

        jButton19.setBackground(new java.awt.Color(255, 0, 0));
        jButton19.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton19.setForeground(new java.awt.Color(255, 255, 255));
        jButton19.setText("Log out");
        jButton19.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton19.setBorderPainted(false);
        jButton19.setFocusPainted(false);
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Insert descrip");

        jButton12.setBackground(new java.awt.Color(74, 144, 226));
        jButton12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton12.setForeground(new java.awt.Color(255, 255, 255));
        jButton12.setText("Show All");
        jButton12.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton12.setBorderPainted(false);
        jButton12.setFocusPainted(false);
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(132, 132, 132)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 959, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(38, 38, 38)))
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jDateChooser1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(90, 90, 90)
                        .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(47, 47, 47))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButton19, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(584, 584, 584)
                        .addComponent(jButton11))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 337, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(43, 43, 43)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4))
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addGap(34, 34, 34)
                                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(100, 100, 100)
                                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(55, 55, 55)
                        .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE)
                        .addComponent(jButton11))))
        );

        jTabbedPane1.addTab("Attendance", jPanel5);

        jPanel4.setBackground(new java.awt.Color(30, 30, 47));
        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(30, 30, 30, 30));
        jPanel4.setForeground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Staff Management");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Staff Name");

        jTextField1.setBackground(new java.awt.Color(42, 42, 64));
        jTextField1.setForeground(new java.awt.Color(220, 220, 220));
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jButton8.setBackground(new java.awt.Color(74, 144, 226));
        jButton8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton8.setForeground(new java.awt.Color(255, 255, 255));
        jButton8.setText("Find Staff");
        jButton8.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton8.setBorderPainted(false);
        jButton8.setFocusPainted(false);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setBackground(new java.awt.Color(74, 144, 226));
        jButton9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton9.setForeground(new java.awt.Color(255, 255, 255));
        jButton9.setText("View All");
        jButton9.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton9.setBorderPainted(false);
        jButton9.setFocusPainted(false);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Staff");

        jScrollPane4.setToolTipText("");

        jTable1.setBackground(new java.awt.Color(255, 255, 255));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Staff ID", "name", "email", "phone_number"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setShowGrid(true);
        jScrollPane4.setViewportView(jTable1);

        jScrollPane1.setViewportView(jScrollPane4);

        jButton16.setBackground(new java.awt.Color(255, 0, 0));
        jButton16.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton16.setForeground(new java.awt.Color(255, 255, 255));
        jButton16.setText("Log out");
        jButton16.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton16.setBorderPainted(false);
        jButton16.setFocusPainted(false);
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Insert unit descript");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 509, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addComponent(jLabel1))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(84, 84, 84)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(315, 315, 315)
                        .addComponent(jLabel3))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(121, 121, 121)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1182, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(118, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton16)
                    .addComponent(jLabel8))
                .addGap(18, 18, 18)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Staff", jPanel4);

        jPanel3.setBackground(new java.awt.Color(30, 30, 46));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Shift Swap");

        jTable3.setBackground(new java.awt.Color(30, 30, 46));
        jTable3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTable3.setForeground(new java.awt.Color(255, 255, 255));
        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Request Id", "Requester", "Target", "Shift Date", "Status"
            }
        ));
        jScrollPane5.setViewportView(jTable3);

        jButton18.setBackground(new java.awt.Color(255, 0, 0));
        jButton18.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton18.setForeground(new java.awt.Color(255, 255, 255));
        jButton18.setText("Log out");
        jButton18.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton18.setBorderPainted(false);
        jButton18.setFocusPainted(false);
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Insert descrip");

        jButton20.setBackground(new java.awt.Color(74, 144, 226));
        jButton20.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton20.setForeground(new java.awt.Color(255, 255, 255));
        jButton20.setText("Decline Shift Request");
        jButton20.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton20.setBorderPainted(false);
        jButton20.setFocusPainted(false);
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        jButton21.setBackground(new java.awt.Color(74, 144, 226));
        jButton21.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton21.setForeground(new java.awt.Color(255, 255, 255));
        jButton21.setText("Approve Shift Request");
        jButton21.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton21.setBorderPainted(false);
        jButton21.setFocusPainted(false);
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 1251, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 123, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton21)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(421, 421, 421)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton20)
                        .addGap(294, 294, 294))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jButton18)
                        .addGap(30, 30, 30))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton20)
                    .addComponent(jButton21))
                .addGap(36, 36, 36))
        );

        jTabbedPane1.addTab("Shift Swap", jPanel3);

        jPanel1.setBackground(new java.awt.Color(30, 30, 47));

        jButton38.setBackground(new java.awt.Color(74, 144, 226));
        jButton38.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jButton38.setForeground(new java.awt.Color(255, 255, 255));
        jButton38.setText("Reset Password");
        jButton38.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton38.setBorderPainted(false);
        jButton38.setFocusPainted(false);
        jButton38.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton38ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(448, 448, 448)
                .addComponent(jButton38, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(735, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(232, 232, 232)
                .addComponent(jButton38, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(190, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Reset Password", jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1486, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 563, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void loadRosterTable() {
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/staffattendanceandshiftmanagementschema",
                "root", "Adesokan2310*")) {

            String sql = "SELECT staff_id, name FROM users_tb WHERE unit = ? AND role = 'non-supervisor'";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, unit); // your supervisor's unit
            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = (DefaultTableModel) tblRoster.getModel();
            model.setRowCount(0); // Clear any old data

            while (rs.next()) {
                Object[] row = new Object[10];
                row[0] = rs.getInt("staff_id");      // Column 0
                row[1] = rs.getString("name");       // Column 1
                row[2] = "Staff";                    // Column 2 (Role, optional)

                // Columns 3–9 for Monday to Sunday
                for (int i = 3; i <= 9; i++) {
                    row[i] = "Off"; // Default shift
                }

                model.addRow(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading roster table: " + e.getMessage());
        }
    }

    private void exportAttendanceToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Attendance Log As CSV");
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
             if (!fileToSave.getAbsolutePath().toLowerCase().endsWith(".csv")) {
        fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
    }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileToSave))) {
                // Header row
                for (int i = 0; i < jTable2.getColumnCount(); i++) {
                    bw.write(jTable2.getColumnName(i));
                    if (i < jTable2.getColumnCount() - 1) {
                        bw.write(",");
                    }
                }
                bw.newLine();

                // Data rows
                for (int i = 0; i < jTable2.getRowCount(); i++) {
                    for (int j = 0; j < jTable2.getColumnCount(); j++) {
                        Object val = jTable2.getValueAt(i, j);
                        bw.write(val != null ? val.toString() : "");
                        if (j < jTable2.getColumnCount() - 1) {
                            bw.write(",");
                        }
                    }
                    bw.newLine();
                }

                JOptionPane.showMessageDialog(this, "Attendance log exported successfully.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage());
            }
        }
    }


    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        // TODO add your handling code here:Filter Button
        String selected = jComboBox1.getSelectedItem().toString();

        if (selected.equals("Date")) {
            if (jDateChooser1.getDate() != null) {
                loadAttendanceLogs("Date", null);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a date.");
            }
        } else if (selected.equals("Staff ID")) {
            String staffId = jTextField2.getText().trim();
            if (!staffId.isEmpty()) {
                loadAttendanceLogs("Staff ID", staffId);
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a staff ID.");
            }
        } else {
            // Load all
            loadAttendanceLogs("None", null);
        }

    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        // TODO add your handling code here:
        exportAttendanceToCSV();
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:Select by criteria combo box
        String selected = jComboBox1.getSelectedItem().toString();
        if (selected.equals("Date")) {
            jDateChooser1.setVisible(true);
            jTextField2.setVisible(false);
            jButton10.setVisible(true);
        } else if (selected.equals("Staff ID")) {
            jDateChooser1.setVisible(false);
            jTextField2.setVisible(true);
            jButton10.setVisible(true);
        } else {
            jDateChooser1.setVisible(false);
            jTextField2.setVisible(false);
            jButton10.setVisible(false);
        }

    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        // TODO add your handling code here:
        JFileChooser fileChooser = new JFileChooser();
int option = fileChooser.showOpenDialog(this);
if (option == JFileChooser.APPROVE_OPTION) {
    File file = fileChooser.getSelectedFile();
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line;
        DefaultTableModel model = (DefaultTableModel) tblRoster.getModel();
        model.setRowCount(0); // Clear existing rows

        boolean skipHeader = true; // if your CSV has a header row
        while ((line = br.readLine()) != null) {
            if (skipHeader) {
                skipHeader = false;
                continue; // skip header
            }
            String[] values = line.split(",");

            // Make sure we have at least 10 values (Staff ID, Name, Role, 7 Days)
            if (values.length >= 10) {
                model.addRow(new Object[]{
                    values[0], values[1], values[2], // ID, Name, Role
                    values[3], values[4], values[5], values[6],
                    values[7], values[8], values[9]  // Mon–Sun
                });
            }
        }

        JOptionPane.showMessageDialog(this, "Roster uploaded to table successfully.");
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage());
    }
}


    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        // TODO add your handling code here:
      JFileChooser fileChooser = new JFileChooser();
fileChooser.setDialogTitle("Save Roster As");
int userSelection = fileChooser.showSaveDialog(this);

if (userSelection == JFileChooser.APPROVE_OPTION) {
    File fileToSave = fileChooser.getSelectedFile();

    // Ensure it ends with .csv
    if (!fileToSave.getAbsolutePath().toLowerCase().endsWith(".csv")) {
        fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
    }

    try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileToSave))) {
        // Write header
        for (int i = 0; i < tblRoster.getColumnCount(); i++) {
            bw.write(tblRoster.getColumnName(i));
            if (i < tblRoster.getColumnCount() - 1) {
                bw.write(",");
            }
        }
        bw.newLine();

        // Write data
        for (int i = 0; i < tblRoster.getRowCount(); i++) {
            for (int j = 0; j < tblRoster.getColumnCount(); j++) {
                Object val = tblRoster.getValueAt(i, j);
                bw.write(val != null ? val.toString() : "");
                if (j < tblRoster.getColumnCount() - 1) {
                    bw.write(",");
                }
            }
            bw.newLine();
        }

        JOptionPane.showMessageDialog(this, "Roster saved successfully as CSV.");
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage());
    }
}


    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        // TODO add your handling code here:Send roster button
// Ensure a Monday is selected
    java.util.Date selectedDate = weekPicker.getDate(); // Replace with your actual JDateChooser variable

    if (selectedDate == null) {
        JOptionPane.showMessageDialog(this, "Please select a week (starting Monday).");
        return;
    }

    Calendar cal = Calendar.getInstance();
    cal.setTime(selectedDate);
    int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
    if (dayOfWeek != Calendar.MONDAY) {
        JOptionPane.showMessageDialog(this, "Please select a Monday to start the week.");
        return;
    }

    // Show loader (modal dialog)
    final javax.swing.JDialog loading = new javax.swing.JDialog(this, "Sending Rosters...", true);
    javax.swing.JLabel lbl = new javax.swing.JLabel("Sending roster emails. Please wait...");
    lbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    loading.add(lbl);
    loading.setSize(300, 100);
    loading.setLocationRelativeTo(this);

    new Thread(() -> {
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/staffattendanceandshiftmanagementschema",
                "root", "Adesokan2310*")) {

            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
            SimpleDateFormat fullDateFormat = new SimpleDateFormat("EEEE, dd MMM yyyy");
            Calendar weekCal = Calendar.getInstance();

            for (int row = 0; row < tblRoster.getRowCount(); row++) {
                String staffId = tblRoster.getValueAt(row, 0).toString();
                String name = tblRoster.getValueAt(row, 1).toString();

                // Get staff email and confirm they're non-supervisor in same unit
                PreparedStatement ps = con.prepareStatement(
                        "SELECT email, role, unit FROM users_tb WHERE staff_id = ?");
                ps.setString(1, staffId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String email = rs.getString("email");
                    String role = rs.getString("role");
                    String staffUnit = rs.getString("unit");

                    if (role.equalsIgnoreCase("non-supervisor") && staffUnit.equalsIgnoreCase(unit)) {
                        String[][] weeklyRoster = new String[7][3]; // day, shift, date
                        weekCal.setTime(selectedDate);

                        for (int i = 0; i < 7; i++) {
                            Date shiftDate = weekCal.getTime();
                            String shiftType = tblRoster.getValueAt(row, i + 3).toString(); // Columns 3-9

                            // Fetch shift_id
                            PreparedStatement shiftStmt = con.prepareStatement(
                                    "SELECT shift_id FROM shifts WHERE shift_type = ? AND unit = ?");
                            shiftStmt.setString(1, shiftType);
                            shiftStmt.setString(2, unit);
                            ResultSet shiftRS = shiftStmt.executeQuery();

                            if (shiftRS.next()) {
                                int shiftId = shiftRS.getInt("shift_id");

                                // Insert into staff_rosters
                                PreparedStatement insert = con.prepareStatement(
                                        "INSERT INTO staff_rosters (staff_id, shift_id, shift_date) VALUES (?, ?, ?)");
                                insert.setInt(1, Integer.parseInt(staffId));
                                insert.setInt(2, shiftId);
                                insert.setDate(3, new java.sql.Date(shiftDate.getTime()));
                                insert.executeUpdate();
                            }

                            weeklyRoster[i][0] = dayFormat.format(shiftDate);
                            weeklyRoster[i][1] = shiftType;
                            weeklyRoster[i][2] = fullDateFormat.format(shiftDate);

                            shiftRS.close();
                            shiftStmt.close();
                            weekCal.add(Calendar.DATE, 1); // Next day
                        }

                        // Send email
                        EmailSender.sendRosterToStaff(email, name, weeklyRoster);
                    }
                }

                rs.close();
                ps.close();
            }

            SwingUtilities.invokeLater(() -> {
                loading.dispose();
                JOptionPane.showMessageDialog(this, "Roster sent and saved successfully.");
            });

        } catch (Exception e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> {
                loading.dispose();
                JOptionPane.showMessageDialog(this, "Error sending roster: " + e.getMessage());
            });
        }
    }).start();

    loading.setVisible(true);
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        // TODO add your handling code here:
         Login loginform = new Login();
        loginform.setVisible(true);
        loginform.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.dispose();
    }//GEN-LAST:event_jButton16ActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        // TODO add your handling code here:Logout Button
        Login loginform = new Login();
        loginform.setVisible(true);
        loginform.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.dispose();
    }//GEN-LAST:event_jButton17ActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        // TODO add your handling code here:
         Login loginform = new Login();
        loginform.setVisible(true);
        loginform.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.dispose();
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        // TODO add your handling code here:
         Login loginform = new Login();
        loginform.setVisible(true);
        loginform.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.dispose();
    }//GEN-LAST:event_jButton19ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        // TODO add your handling code here:
       int row = jTable3.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Select a request to decline.");
        return;
    }

    int requestId = Integer.parseInt(jTable3.getValueAt(row, 0).toString());

    try (Connection con = DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/staffattendanceandshiftmanagementschema",
        "root", "Adesokan2310*")) {

        // 1. Get requester and target info
        PreparedStatement ps = con.prepareStatement("""
            SELECT requester_id, target_staff_id, shift_date 
            FROM shift_swap_requests 
            WHERE request_id = ?
        """);
        ps.setInt(1, requestId);
        ResultSet rs = ps.executeQuery();

        if (!rs.next()) {
            JOptionPane.showMessageDialog(this, "Request not found.");
            return;
        }

        int requesterId = rs.getInt("requester_id");
        int targetId = rs.getInt("target_staff_id");
        java.sql.Date shiftDate = rs.getDate("shift_date");

        // 2. Mark as Declined
        PreparedStatement decline = con.prepareStatement("""
            UPDATE shift_swap_requests SET status = 'Declined' WHERE request_id = ?
        """);
        decline.setInt(1, requestId);
        decline.executeUpdate();

        // 3. Get emails and names
        PreparedStatement info = con.prepareStatement("""
            SELECT staff_id, name, email FROM users_tb WHERE staff_id IN (?, ?)
        """);
        info.setInt(1, requesterId);
        info.setInt(2, targetId);
        ResultSet rinfo = info.executeQuery();

        String requesterName = "", requesterEmail = "", targetName = "", targetEmail = "";
        while (rinfo.next()) {
            int id = rinfo.getInt("staff_id");
            String name = rinfo.getString("name");
            String email = rinfo.getString("email");
            if (id == requesterId) {
                requesterName = name;
                requesterEmail = email;
            } else {
                targetName = name;
                targetEmail = email;
            }
        }

        EmailSender.sendShiftSwapNotification(
            requesterEmail, targetEmail,
            requesterName, targetName, "declined", shiftDate
        );

        JOptionPane.showMessageDialog(this, "Swap request declined.");
        loadPendingShiftSwaps();

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Failed to decline request.");
    }




    }//GEN-LAST:event_jButton20ActionPerformed

   private void loadPendingShiftSwaps() {
    try (Connection con = DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/staffattendanceandshiftmanagementschema",
        "root", "Adesokan2310*")) {

        String sql = """
            SELECT s.request_id, u1.name AS requester, u2.name AS target, 
                   s.shift_date, s.status
            FROM shift_swap_requests s
            JOIN users_tb u1 ON s.requester_id = u1.staff_id
            JOIN users_tb u2 ON s.target_staff_id = u2.staff_id
            WHERE s.status = 'Pending'
        """;
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
        model.setRowCount(0); // clear table

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("request_id"),
                rs.getString("requester"),
                rs.getString("target"),
                rs.getDate("shift_date"),
                rs.getString("status")
            });
        }

    } catch (Exception ex) {
        ex.printStackTrace();
    }
}


    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        // TODO add your handling code here:APPROVE REQUEST
      int row = jTable3.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Select a request to approve.");
        return;
    }

    int requestId = Integer.parseInt(jTable3.getValueAt(row, 0).toString());

    try (Connection con = DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/staffattendanceandshiftmanagementschema",
        "root", "Adesokan2310*")) {

        // Get staff IDs and date using request_id
        PreparedStatement ps = con.prepareStatement("""
            SELECT requester_id, target_staff_id, shift_date 
            FROM shift_swap_requests 
            WHERE request_id = ?
        """);
        ps.setInt(1, requestId);
        ResultSet rs = ps.executeQuery();

        if (!rs.next()) {
            JOptionPane.showMessageDialog(this, "Shift swap request not found.");
            return;
        }

        int requesterId = rs.getInt("requester_id");
        int targetId = rs.getInt("target_staff_id");
        java.sql.Date shiftDate = rs.getDate("shift_date");

        // Get shifts for both
        PreparedStatement getShift = con.prepareStatement("""
            SELECT staff_id, shift_id FROM staff_rosters 
            WHERE shift_date = ? AND staff_id IN (?, ?)
        """);
        getShift.setDate(1, shiftDate);
        getShift.setInt(2, requesterId);
        getShift.setInt(3, targetId);
        ResultSet shiftRs = getShift.executeQuery();

        Integer requesterShift = null, targetShift = null;
        while (shiftRs.next()) {
            int sid = shiftRs.getInt("staff_id");
            int shiftId = shiftRs.getInt("shift_id");
            if (sid == requesterId) requesterShift = shiftId;
            else if (sid == targetId) targetShift = shiftId;
        }

        if (requesterShift == null || targetShift == null) {
            JOptionPane.showMessageDialog(this, "One or both staff have no shift on the selected date.");
            return;
        }

        // Swap
        PreparedStatement updateShift = con.prepareStatement("""
            UPDATE staff_rosters SET shift_id = ? 
            WHERE staff_id = ? AND shift_date = ?
        """);

        updateShift.setInt(1, targetShift);
        updateShift.setInt(2, requesterId);
        updateShift.setDate(3, shiftDate);
        updateShift.executeUpdate();

        updateShift.setInt(1, requesterShift);
        updateShift.setInt(2, targetId);
        updateShift.setDate(3, shiftDate);
        updateShift.executeUpdate();

        // Mark Approved
        PreparedStatement mark = con.prepareStatement("""
            UPDATE shift_swap_requests SET status = 'Approved' WHERE request_id = ?
        """);
        mark.setInt(1, requestId);
        mark.executeUpdate();

        // Get emails and names
        PreparedStatement info = con.prepareStatement("""
            SELECT staff_id, name, email FROM users_tb WHERE staff_id IN (?, ?)
        """);
        info.setInt(1, requesterId);
        info.setInt(2, targetId);
        ResultSet rinfo = info.executeQuery();

        String requesterName = "", requesterEmail = "", targetName = "", targetEmail = "";
        while (rinfo.next()) {
            int id = rinfo.getInt("staff_id");
            String name = rinfo.getString("name");
            String email = rinfo.getString("email");
            if (id == requesterId) {
                requesterName = name;
                requesterEmail = email;
            } else {
                targetName = name;
                targetEmail = email;
            }
        }

        EmailSender.sendShiftSwapNotification(
            requesterEmail, targetEmail,
            requesterName, targetName, "approved", shiftDate
        );

        JOptionPane.showMessageDialog(this, "Swap approved.");
        loadPendingShiftSwaps();

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Swap approval failed.");
    }

    }//GEN-LAST:event_jButton21ActionPerformed
private void searchStaff() {
    String search = jTextField1.getText().trim();
    String supervisorUnit = UserSession1.getStaffUnit(); // Get supervisor's unit

    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
    model.setRowCount(0); // Clear table

    String sql = "SELECT staff_id, name, email, gender, unit, role, phone_number "
               + "FROM users_tb WHERE unit = ?";

    if (!search.isEmpty()) {
        sql += " AND name LIKE ?";
    }

    try (Connection con = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/staffattendanceandshiftmanagementschema",
            "root", "Adesokan2310*")) {

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, supervisorUnit);

        if (!search.isEmpty()) {
            ps.setString(2, "%" + search + "%");
        }

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("staff_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("gender"),
                rs.getString("unit"),
                rs.getString("role"),
                rs.getString("phone_number")
            });
        }

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading staff data: " + ex.getMessage());
    }
}

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:Find Staff
        searchStaff();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
        // TODO add your handling code here:
        loadRosterTable();
    }//GEN-LAST:event_jButton22ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        // TODO add your handling code here:
        loadAttendanceLogs("None", null);
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
        loadStaffInUnit();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton38ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton38ActionPerformed
        // TODO add your handling code here:
        ResetPassword resetpage = new ResetPassword();
        resetpage.setVisible(true);
        resetpage.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }//GEN-LAST:event_jButton38ActionPerformed
private int getStaffIdByName(String name) {
    try (Connection con = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/staffattendanceandshiftmanagementschema",
            "root", "Adesokan2310*")) {
        
        PreparedStatement ps = con.prepareStatement("SELECT staff_id FROM users_tb WHERE name = ?");
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("staff_id");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return -1;
}

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(supervisorDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(supervisorDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(supervisorDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(supervisorDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new supervisorDashboard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton38;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
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
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTable tblRoster;
    private com.toedter.calendar.JDateChooser weekPicker;
    // End of variables declaration//GEN-END:variables
}
