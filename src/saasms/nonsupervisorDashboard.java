/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package saasms;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author adisa
 */
public class nonsupervisorDashboard extends javax.swing.JFrame {

    /**
     * Creates new form nonsupervisorDashboard
     */
    public nonsupervisorDashboard() {
        initComponents();
        loadOtherUnitStaff();
                 loadProfileData();
// On page init
jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        int row = jTable1.getSelectedRow();
        if (row != -1) {
            selectedStaffId = jTable1.getValueAt(row, 0).toString();
        }
    }
});

        jTableMyShifts.addMouseListener(new java.awt.event.MouseAdapter() {
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        int row = jTableMyShifts.getSelectedRow();
        if (row != -1) {
            selectedStaffId = jTableMyShifts.getValueAt(row, 0).toString(); // Staff ID is in column 0
            
        }
    }
});
       



    }
    private String selectedStaffId = null;
private String myShift = null;
private String targetShift = null;

private void loadProfileData() {
    int staffId = UserSession1.getStaffID();
    System.out.println("Fetching profile for staff ID: " + staffId);

    try (Connection con = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/staffattendanceandshiftmanagementschema",
            "root", 
            "Adesokan2310*")) {

        String sql = "SELECT * FROM users_tb WHERE staff_id = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, staffId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            System.out.println("User found: " + rs.getString("name"));
            // Debug
            jLabel2.setText(rs.getString("name"));
            jLabel11.setText(rs.getString("name"));
            jLabel12.setText(rs.getString("dob"));
            jLabel15.setText(rs.getString("email"));
            jLabel13.setText(rs.getString("gender"));
            jLabel14.setText(rs.getString("unit"));
        } else {
            System.out.println("No user found with that ID.");
        }

        rs.close();
        ps.close();

    } catch (Exception ex) {
        ex.printStackTrace();
    }
}
private void loadMyRosterTable() {
    int staffId = UserSession1.getStaffID();
    DefaultTableModel model = (DefaultTableModel) jTable2.getModel(); // Make sure jTable2 has 2 columns: "Day", "Shift"
    model.setRowCount(0); // Clear old rows

    try (Connection con = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/staffattendanceandshiftmanagementschema",
            "root", "Adesokan2310*")) {

        String sql = """
            SELECT sr.shift_date, s.shift_type
            FROM staff_rosters sr
            JOIN shifts s ON sr.shift_id = s.shift_id
            WHERE sr.staff_id = ?
            ORDER BY sr.shift_date
        """;

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, staffId);
        ResultSet rs = ps.executeQuery();

        java.text.SimpleDateFormat dayFormat = new java.text.SimpleDateFormat("EEEE");

        while (rs.next()) {
            java.sql.Date shiftDate = rs.getDate("shift_date");
            String day = dayFormat.format(shiftDate);
            String shift = rs.getString("shift_type");

            model.addRow(new Object[]{day, shift});
        }

        rs.close();
        ps.close();

    } catch (Exception ex) {
        ex.printStackTrace();
        javax.swing.JOptionPane.showMessageDialog(this, "Error loading roster.");
    }
}

private void loadWeeklyRoster(java.util.Date monday) {
    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
        protected Void doInBackground() throws Exception {
            int staffId = UserSession1.getStaffID();
            String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
            DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
            model.setRowCount(0); // clear previous data

            Calendar cal = Calendar.getInstance();
            cal.setTime(monday);

            try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/staffattendanceandshiftmanagementschema", 
                "root", 
                "Adesokan2310*")) {

                for (String day : days) {
                    java.sql.Date date = new java.sql.Date(cal.getTimeInMillis());

                    String query = """
                        SELECT s.shift_type 
                        FROM staff_rosters r
                        JOIN shifts s ON r.shift_id = s.shift_id
                        WHERE r.staff_id = ? AND r.shift_date = ?
                    """;
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setInt(1, staffId);
                    ps.setDate(2, date);
                    ResultSet rs = ps.executeQuery();

                    String shift = rs.next() ? rs.getString("shift_type") : "None";
                    model.addRow(new Object[]{day, shift});

                    cal.add(Calendar.DAY_OF_MONTH, 1); // next day
                    rs.close();
                    ps.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void done() {
            JOptionPane.showMessageDialog(nonsupervisorDashboard.this, "Weekly roster loaded.");
        }
    };

    worker.execute();
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jButton19 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jButton11 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jButton20 = new javax.swing.JButton();
        weekStartChooser = new com.toedter.calendar.JDateChooser();
        jPanel6 = new javax.swing.JPanel();
        jButton21 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        weekSwapChooser = new com.toedter.calendar.JDateChooser();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        btnCompareShifts = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableMyShifts = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTableTargetShifts = new javax.swing.JTable();
        jLabel16 = new javax.swing.JLabel();
        swapDayChooser = new com.toedter.calendar.JDateChooser();
        btnRequestSwap = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel4.setBackground(new java.awt.Color(30, 30, 47));
        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(30, 30, 30, 30));
        jPanel4.setForeground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText(" Welcome ,");

        jButton9.setBackground(new java.awt.Color(74, 144, 226));
        jButton9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton9.setForeground(new java.awt.Color(255, 255, 255));
        jButton9.setText("Reset Password");
        jButton9.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton9.setBorderPainted(false);
        jButton9.setFocusPainted(false);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("User Name");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Name");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Date of Birth");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Email");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Gender");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Unit");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("Insert Name");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Insert Dob");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Insert Gender");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("Insert Unit");

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("Insert Email");

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

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel8))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel2))
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel7)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel9)
                                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(88, 88, 88)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(91, 91, 91)
                                .addComponent(jButton9)))
                        .addGap(0, 799, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton19, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton9)
                .addGap(63, 63, 63))
        );

        jTabbedPane1.addTab("profiile", jPanel4);

        jPanel5.setBackground(new java.awt.Color(30, 30, 47));
        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(30, 30, 30, 30));
        jPanel5.setForeground(new java.awt.Color(255, 255, 255));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Shift Information");

        jButton11.setBackground(new java.awt.Color(74, 144, 226));
        jButton11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton11.setForeground(new java.awt.Color(255, 255, 255));
        jButton11.setText("Load My Roster");
        jButton11.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton11.setBorderPainted(false);
        jButton11.setFocusPainted(false);
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jScrollPane5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane5.setToolTipText("");

        jTable2.setBackground(new java.awt.Color(255, 255, 255));
        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Day", "Shift"
            }
        ));
        jTable2.setShowGrid(true);
        jScrollPane5.setViewportView(jTable2);

        jScrollPane2.setViewportView(jScrollPane5);

        jButton20.setBackground(new java.awt.Color(255, 0, 0));
        jButton20.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton20.setForeground(new java.awt.Color(255, 255, 255));
        jButton20.setText("Log out");
        jButton20.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton20.setBorderPainted(false);
        jButton20.setFocusPainted(false);
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton20, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 621, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(206, 206, 206)
                        .addComponent(weekStartChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(71, 71, 71)
                        .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 536, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton20))
                .addGap(39, 39, 39)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(weekStartChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(150, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Roster", jPanel5);

        jPanel6.setBackground(new java.awt.Color(30, 30, 47));
        jPanel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(30, 30, 30, 30));
        jPanel6.setForeground(new java.awt.Color(255, 255, 255));

        jButton21.setBackground(new java.awt.Color(255, 0, 0));
        jButton21.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton21.setForeground(new java.awt.Color(255, 255, 255));
        jButton21.setText("Log out");
        jButton21.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton21.setBorderPainted(false);
        jButton21.setFocusPainted(false);
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Select Week (Monday):");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Select Staff to Compare With:");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Staff ID", "Name", "Email"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        btnCompareShifts.setBackground(new java.awt.Color(74, 144, 226));
        btnCompareShifts.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCompareShifts.setForeground(new java.awt.Color(255, 255, 255));
        btnCompareShifts.setText("Compare Weekly Shifts");
        btnCompareShifts.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnCompareShifts.setBorderPainted(false);
        btnCompareShifts.setFocusPainted(false);
        btnCompareShifts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCompareShiftsActionPerformed(evt);
            }
        });

        jTableMyShifts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Day", "My Shift"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(jTableMyShifts);

        jTableTargetShifts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Day ", "Target's Shift"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(jTableTargetShifts);

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("Pick Day to Request Swap:");

        btnRequestSwap.setBackground(new java.awt.Color(74, 144, 226));
        btnRequestSwap.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnRequestSwap.setForeground(new java.awt.Color(255, 255, 255));
        btnRequestSwap.setText("Request Swap");
        btnRequestSwap.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnRequestSwap.setBorderPainted(false);
        btnRequestSwap.setFocusPainted(false);
        btnRequestSwap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRequestSwapActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel5))
                            .addComponent(jLabel6))
                        .addGap(59, 59, 59)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(124, 124, 124)
                                .addComponent(btnCompareShifts, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(weekSwapChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jScrollPane1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton21, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGap(225, 225, 225)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(289, 289, 289))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(119, 119, 119)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(swapDayChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(393, 393, 393)
                                .addComponent(btnRequestSwap, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton21)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(weekSwapChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCompareShifts, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(swapDayChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnRequestSwap, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Shift Swap", jPanel6);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        // TODO add your handling code here:
        Login loginform = new Login();
        loginform.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton19ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        // TODO add your handling code here:
        Login loginform = new Login();
        loginform.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton20ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        // TODO add your handling code here:
        Login loginform = new Login();
        loginform.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton21ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        // TODO add your handling code here:Load my roster button
        java.util.Date selectedDate = weekStartChooser.getDate();
    if (selectedDate == null) {
        JOptionPane.showMessageDialog(this, "Please select a start date.");
        return;
    }

    Calendar cal = Calendar.getInstance();
    cal.setTime(selectedDate);

    if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
        JOptionPane.showMessageDialog(this, "Please select a Monday.");
        return;
    }

    loadWeeklyRoster(selectedDate);
    }//GEN-LAST:event_jButton11ActionPerformed
private void loadOtherUnitStaff() {
    int staffId = UserSession1.getStaffID();
    String unit = UserSession1.getStaffUnit();

    DefaultTableModel model = new DefaultTableModel(new String[]{"Staff ID", "Name", "Role"}, 0);
    jTable1.setModel(model);

    try (Connection con = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/staffattendanceandshiftmanagementschema",
            "root", "Adesokan2310*")) {

        String sql = "SELECT staff_id, name, role FROM users_tb WHERE unit = ? AND staff_id != ? AND role != 'Supervisor'";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, unit);
        ps.setInt(2, staffId);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Object[] row = {
                rs.getInt("staff_id"),
                rs.getString("name"),
                rs.getString("role")
            };
            model.addRow(row);
        }

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No other staff found in your unit.");
        }

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading staff: " + ex.getMessage());
    }
}

    private void btnCompareShiftsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCompareShiftsActionPerformed
        // TODO add your handling code here:
        java.util.Date selectedDate = weekSwapChooser.getDate();

    if (selectedDate == null) {
        JOptionPane.showMessageDialog(this, "Please select the Monday of the week you are looking for.");
        return;
    }

    Calendar cal = Calendar.getInstance();
    cal.setTime(selectedDate);
    if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
        JOptionPane.showMessageDialog(this, "Selected date is not a Monday, please select a monday.");
        return;
    }

    if (selectedStaffId == null) {
        JOptionPane.showMessageDialog(this, "Please select a staff member to compare with.");
        return;
    }

    int myId = UserSession1.getStaffID();
    int targetId = Integer.parseInt(selectedStaffId);
    loadWeeklyShiftsForSwap(selectedDate, myId, targetId); // implement next
    }//GEN-LAST:event_btnCompareShiftsActionPerformed
private void loadWeeklyShiftsForSwap(java.util.Date monday, int myId, int targetId) {
    SwingWorker<Void, Void> worker = new SwingWorker<>() {
        @Override
        protected Void doInBackground() throws Exception {
            String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
            DefaultTableModel myModel = (DefaultTableModel) jTableMyShifts.getModel();
            DefaultTableModel targetModel = (DefaultTableModel) jTableTargetShifts.getModel();
            myModel.setRowCount(0);
            targetModel.setRowCount(0);

            Calendar cal = Calendar.getInstance();
            cal.setTime(monday);

            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/staffattendanceandshiftmanagementschema", "root", "Adesokan2310*")) {
                for (String day : days) {
                    java.sql.Date sqlDate = new java.sql.Date(cal.getTimeInMillis());

                    // My shift
                    String myShift = "None";
                    PreparedStatement psMy = con.prepareStatement("""
                        SELECT s.shift_type FROM staff_rosters r
                        JOIN shifts s ON r.shift_id = s.shift_id
                        WHERE r.staff_id = ? AND r.shift_date = ?
                    """);
                    psMy.setInt(1, myId);
                    psMy.setDate(2, sqlDate);
                    ResultSet rsMy = psMy.executeQuery();
                    if (rsMy.next()) myShift = rsMy.getString("shift_type");
                    myModel.addRow(new Object[]{day, myShift});
                    rsMy.close(); psMy.close();

                    // Target shift
                    String targetShift = "None";
                    PreparedStatement psTarget = con.prepareStatement("""
                        SELECT s.shift_type FROM staff_rosters r
                        JOIN shifts s ON r.shift_id = s.shift_id
                        WHERE r.staff_id = ? AND r.shift_date = ?
                    """);
                    psTarget.setInt(1, targetId);
                    psTarget.setDate(2, sqlDate);
                    ResultSet rsTarget = psTarget.executeQuery();
                    if (rsTarget.next()) targetShift = rsTarget.getString("shift_type");
                    targetModel.addRow(new Object[]{day, targetShift});
                    rsTarget.close(); psTarget.close();

                    cal.add(Calendar.DATE, 1);
                }
            }

            return null;
        }

        @Override
        protected void done() {
            JOptionPane.showMessageDialog(nonsupervisorDashboard.this, "Weekly shifts loaded.");
        }
    };
    worker.execute();
}

    private void btnRequestSwapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRequestSwapActionPerformed
        // TODO add your handling code here:
    java.util.Date selectedDate = swapDayChooser.getDate();
if (selectedDate == null) {
    JOptionPane.showMessageDialog(this, "Please select a valid swap date.");
    return;
}

int row = jTable1.getSelectedRow();
if (row == -1) {
    JOptionPane.showMessageDialog(this, "Please select a staff member.");
    return;
}

Object staffIdObj = jTable1.getValueAt(row, 0);
int selectedStaffId;
try {
    selectedStaffId = Integer.parseInt(staffIdObj.toString());
} catch (NumberFormatException e) {
    JOptionPane.showMessageDialog(this, "Invalid staff selected.");
    return;
}

int requesterId = UserSession1.getStaffID();
String unit = UserSession1.getStaffUnit();

try (Connection con = DriverManager.getConnection(
    "jdbc:mysql://localhost:3306/staffattendanceandshiftmanagementschema",
    "root", "Adesokan2310*")) {

    PreparedStatement ps = con.prepareStatement("""
        INSERT INTO shift_swap_requests (requester_id, target_staff_id, shift_date, status, unit)
        VALUES (?, ?, ?, 'Pending', ?)
    """);
    ps.setInt(1, requesterId);
    ps.setInt(2, selectedStaffId);
    ps.setDate(3, new java.sql.Date(selectedDate.getTime()));
    ps.setString(4, unit);
    ps.executeUpdate();

    // Fetch emails
    String requesterName = "", requesterEmail = "";
    String targetName = "", targetEmail = "";

    PreparedStatement infoStmt = con.prepareStatement("""
        SELECT staff_id, name, email FROM users_tb WHERE staff_id IN (?, ?)
    """);
    infoStmt.setInt(1, requesterId);
    infoStmt.setInt(2, selectedStaffId);
    ResultSet rs = infoStmt.executeQuery();

    while (rs.next()) {
        int id = rs.getInt("staff_id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        if (id == requesterId) {
            requesterName = name;
            requesterEmail = email;
        } else {
            targetName = name;
            targetEmail = email;
        }
    }

    // Notify both
    EmailSender.sendShiftSwapNotification(
        requesterEmail, targetEmail,
        requesterName, targetName,
        "requested", selectedDate
    );

    JOptionPane.showMessageDialog(this, "Shift swap request submitted.");
} catch (Exception ex) {
    ex.printStackTrace();
    JOptionPane.showMessageDialog(this, "Failed to submit shift swap request.");
}

    }//GEN-LAST:event_btnRequestSwapActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
         ResetPassword resetpage = new ResetPassword();
        resetpage.setVisible(true);
        resetpage.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }//GEN-LAST:event_jButton9ActionPerformed

    /**
     * @param args the command line arguments
     */
    private void loadUnitStaff() {
        int myId = UserSession1.getStaffID();
    String myUnit = UserSession1.getStaffUnit(); // store this in session

    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
    model.setRowCount(0);

    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/staffattendanceandshiftmanagementschema", "root", "Adesokan2310*")) {
        PreparedStatement ps = con.prepareStatement("SELECT staff_id, name, email FROM users_tb WHERE unit = ? AND staff_id != ?");
        ps.setString(1, myUnit);
        ps.setInt(2, myId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            model.addRow(new Object[]{rs.getString("staff_id"), rs.getString("name"), rs.getString("email")});
        }
    } catch (Exception ex) {
        ex.printStackTrace();
    }
}
  



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
            java.util.logging.Logger.getLogger(nonsupervisorDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(nonsupervisorDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(nonsupervisorDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(nonsupervisorDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new nonsupervisorDashboard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCompareShifts;
    private javax.swing.JButton btnRequestSwap;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTableMyShifts;
    private javax.swing.JTable jTableTargetShifts;
    private com.toedter.calendar.JDateChooser swapDayChooser;
    private com.toedter.calendar.JDateChooser weekStartChooser;
    private com.toedter.calendar.JDateChooser weekSwapChooser;
    // End of variables declaration//GEN-END:variables
}
