package src.ui;

import src.db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BookingForm extends JFrame {
    private JTextField equipmentIdField, userIdField, dateFromField, dateToField, returnBookingIdField;
    private JTable bookingTable, availabilityTable;
    private DefaultTableModel bookingModel, availabilityModel;

    public BookingForm() {
        setTitle("Manage Bookings");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Input Panel (North)
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(new JLabel("Equipment ID:"));
        equipmentIdField = new JTextField(8);
        row1.add(equipmentIdField);

        row1.add(new JLabel("User ID:"));
        userIdField = new JTextField(8);
        row1.add(userIdField);

        inputPanel.add(row1);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.add(new JLabel("Date From (YYYY-MM-DD):"));
        dateFromField = new JTextField(10);
        row2.add(dateFromField);

        row2.add(new JLabel("Date To (YYYY-MM-DD):"));
        dateToField = new JTextField(10);
        row2.add(dateToField);

        inputPanel.add(row2);

        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton bookButton = new JButton("Book Equipment");
        row3.add(bookButton);

        row3.add(new JLabel("Booking ID to Return:"));
        returnBookingIdField = new JTextField(8);
        row3.add(returnBookingIdField);

        JButton returnButton = new JButton("Return Equipment");
        row3.add(returnButton);

        JButton cleanupButton = new JButton("Cleanup Old Alerts");
        row3.add(cleanupButton);

        inputPanel.add(row3);
        add(inputPanel, BorderLayout.NORTH);

        // Booking Table (Center)
        bookingModel = new DefaultTableModel();
        bookingModel.setColumnIdentifiers(new String[]{"Booking ID", "Equip ID", "User ID", "From", "To", "Status"});
        bookingTable = new JTable(bookingModel);
        add(new JScrollPane(bookingTable), BorderLayout.CENTER);

        // Availability Table (South)
        availabilityModel = new DefaultTableModel();
        availabilityModel.setColumnIdentifiers(new String[]{"ID", "Type", "Condition", "Location", "Availability"});
        availabilityTable = new JTable(availabilityModel);
        add(new JScrollPane(availabilityTable), BorderLayout.SOUTH);

        // Button Actions
        bookButton.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection()) {
                CallableStatement stmt = conn.prepareCall("CALL SafeBookEquipment(?, ?, ?, ?)");
                stmt.setInt(1, Integer.parseInt(equipmentIdField.getText()));
                stmt.setInt(2, Integer.parseInt(userIdField.getText()));
                stmt.setDate(3, Date.valueOf(dateFromField.getText()));
                stmt.setDate(4, Date.valueOf(dateToField.getText()));
                stmt.execute();
                JOptionPane.showMessageDialog(this, "Booking attempted (check for availability and conflicts)");
                loadBookings();
                loadAvailability();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        returnButton.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection()) {
                CallableStatement stmt = conn.prepareCall("CALL ReturnEquipment(?)");
                stmt.setInt(1, Integer.parseInt(returnBookingIdField.getText()));
                stmt.execute();
                JOptionPane.showMessageDialog(this, "Equipment returned and usage updated.");
                loadBookings();
                loadAvailability();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        cleanupButton.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection()) {
                CallableStatement stmt = conn.prepareCall("CALL CleanupOldOverdueAlerts()");
                stmt.execute();
                JOptionPane.showMessageDialog(this, "Old overdue alerts cleaned up.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        loadBookings();
        loadAvailability();
    }

    private void loadBookings() {
        bookingModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT booking_id, equipment_id, user_id, date_from, date_to, status1 FROM Bookings");
            while (rs.next()) {
                bookingModel.addRow(new Object[]{
                        rs.getInt("booking_id"),
                        rs.getInt("equipment_id"),
                        rs.getInt("user_id"),
                        rs.getDate("date_from"),
                        rs.getDate("date_to"),
                        rs.getString("status1")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadAvailability() {
        availabilityModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT equipment_id, type1, condition1, location, availability FROM Equipment");
            while (rs.next()) {
                availabilityModel.addRow(new Object[]{
                        rs.getInt("equipment_id"),
                        rs.getString("type1"),
                        rs.getString("condition1"),
                        rs.getString("location"),
                        rs.getString("availability")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}