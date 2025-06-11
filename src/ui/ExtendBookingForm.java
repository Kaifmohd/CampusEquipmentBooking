package src.ui;

import src.db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class ExtendBookingForm extends JFrame {

    private JComboBox<Integer> bookingComboBox;
    private JTextField newDateToField;
    private JButton extendButton;

    public ExtendBookingForm() {
        setTitle("Extend Booking Period");
        setSize(400, 250);
        setLayout(new GridLayout(4, 1, 10, 10));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        bookingComboBox = new JComboBox<>();
        newDateToField = new JTextField("YYYY-MM-DD");
        extendButton = new JButton("Submit Extension");

        add(new JLabel("Select Booking ID:"));
        add(bookingComboBox);
        add(new JLabel("Enter New End Date (YYYY-MM-DD):"));
        add(newDateToField);
        add(extendButton);

        loadBookingIds();
        attachListeners();

        setVisible(true);
    }

    private void loadBookingIds() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT booking_id FROM Bookings WHERE status1 = 'Assigned'")) {

            while (rs.next()) {
                bookingComboBox.addItem(rs.getInt("booking_id"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading bookings: " + e.getMessage());
        }
    }


    private void attachListeners() {
        extendButton.addActionListener(e -> {
            Integer bookingId = (Integer) bookingComboBox.getSelectedItem();
            String dateStr = newDateToField.getText();

            try {
                java.util.Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
                java.sql.Date sqlNewDate = new java.sql.Date(utilDate.getTime());

                try (Connection conn = DBConnection.getConnection();
                     CallableStatement stmt = conn.prepareCall("{ CALL ExtendBookingPeriod(?, ?, ?) }")) {

                    stmt.setInt(1, bookingId);
                    stmt.setDate(2, sqlNewDate);
                    stmt.registerOutParameter(3, Types.VARCHAR);

                    stmt.execute();

                    String result = stmt.getString(3);
                    JOptionPane.showMessageDialog(this, result);

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Extension failed: " + ex.getMessage());
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
            }
        });
    }
}
