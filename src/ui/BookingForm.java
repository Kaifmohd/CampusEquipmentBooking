package src.ui;

import src.db.DBConnection;

import javax.swing.*;
import java.sql.*;

public class BookingForm extends JFrame {
    public BookingForm() {
        setTitle("Book Equipment");
        setSize(400, 300);
        setLayout(null);

        JLabel l1 = new JLabel("Equipment ID:");
        l1.setBounds(20, 30, 100, 25);
        add(l1);

        JTextField t1 = new JTextField();
        t1.setBounds(130, 30, 100, 25);
        add(t1);

        JLabel l2 = new JLabel("User ID:");
        l2.setBounds(20, 70, 100, 25);
        add(l2);

        JTextField t2 = new JTextField();
        t2.setBounds(130, 70, 100, 25);
        add(t2);

        JLabel l3 = new JLabel("Date From (YYYY-MM-DD):");
        l3.setBounds(20, 110, 200, 25);
        add(l3);

        JTextField t3 = new JTextField();
        t3.setBounds(220, 110, 100, 25);
        add(t3);

        JLabel l4 = new JLabel("Date To (YYYY-MM-DD):");
        l4.setBounds(20, 150, 200, 25);
        add(l4);

        JTextField t4 = new JTextField();
        t4.setBounds(220, 150, 100, 25);
        add(t4);

        JButton bookBtn = new JButton("Book Equipment");
        bookBtn.setBounds(100, 200, 180, 30);
        add(bookBtn);

        bookBtn.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection()) {
                conn.setAutoCommit(false);

                int equipmentId = Integer.parseInt(t1.getText());
                int userId = Integer.parseInt(t2.getText());
                String from = t3.getText();
                String to = t4.getText();

                PreparedStatement check = conn.prepareStatement("SELECT availability FROM Equipment WHERE equipment_id = ?");
                check.setInt(1, equipmentId);
                ResultSet rs = check.executeQuery();

                if (rs.next() && "Available".equals(rs.getString("availability"))) {
                    PreparedStatement insert = conn.prepareStatement(
                            "INSERT INTO Bookings (equipment_id, user_id, date_from, date_to, status1) VALUES (?, ?, ?, ?, 'Pending')");
                    insert.setInt(1, equipmentId);
                    insert.setInt(2, userId);
                    insert.setString(3, from);
                    insert.setString(4, to);
                    insert.executeUpdate();
                    conn.commit();
                    JOptionPane.showMessageDialog(this, "Booking successful!");
                } else {
                    conn.rollback();
                    JOptionPane.showMessageDialog(this, "Equipment not available.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}
