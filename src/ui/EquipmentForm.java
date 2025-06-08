package src.ui;

import src.db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class EquipmentForm extends JFrame {
    JTable table;
    DefaultTableModel model;
    JTextField typeField, conditionField, locationField;
    JComboBox<String> availabilityBox;

    public EquipmentForm() {
        setTitle("Equipment Management");
        setSize(700, 500);
        setLayout(null);

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID", "Type", "Condition", "Location", "Availability"});
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(10, 10, 660, 200);
        add(sp);

        JLabel l1 = new JLabel("Type:");
        l1.setBounds(10, 220, 50, 25);
        add(l1);
        typeField = new JTextField();
        typeField.setBounds(70, 220, 100, 25);
        add(typeField);

        JLabel l2 = new JLabel("Condition:");
        l2.setBounds(180, 220, 70, 25);
        add(l2);
        conditionField = new JTextField();
        conditionField.setBounds(260, 220, 100, 25);
        add(conditionField);

        JLabel l3 = new JLabel("Location:");
        l3.setBounds(370, 220, 60, 25);
        add(l3);
        locationField = new JTextField();
        locationField.setBounds(440, 220, 100, 25);
        add(locationField);

        JLabel l4 = new JLabel("Availability:");
        l4.setBounds(10, 260, 80, 25);
        add(l4);
        availabilityBox = new JComboBox<>(new String[]{"Available", "Unavailable"});
        availabilityBox.setBounds(100, 260, 100, 25);
        add(availabilityBox);

        JButton btnAdd = new JButton("Add Equipment");
        btnAdd.setBounds(220, 260, 140, 30);
        add(btnAdd);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setBounds(370, 260, 100, 30);
        add(btnRefresh);

        JButton btnDelete = new JButton("Delete");
        btnDelete.setBounds(480, 260, 100, 30);
        add(btnDelete);

        JButton btnBack = new JButton("Back");
        btnBack.setBounds(590, 260, 80, 30);
        add(btnBack);


        btnAdd.addActionListener(e -> addEquipment());
        btnRefresh.addActionListener(e -> loadEquipment());
        btnDelete.addActionListener(e -> deleteSelected());
        btnBack.addActionListener(e -> {
            this.dispose(); // closes the current window
            new MainMenu().setVisible(true); // returns to main menu
        });

        loadEquipment();
    }

    void loadEquipment() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Equipment");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
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

    void deleteSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row first");
            return;
        }

        int id = (int) model.getValueAt(row, 0);

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM Equipment WHERE equipment_id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Deleted.");
            loadEquipment();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void addEquipment() {
        String type = typeField.getText();
        String condition = conditionField.getText();
        String location = locationField.getText();
        String availability = availabilityBox.getSelectedItem().toString();

        if (type.isEmpty() || condition.isEmpty() || location.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO Equipment (type1, condition1, location, availability) VALUES (?, ?, ?, ?)"
            );
            ps.setString(1, type);
            ps.setString(2, condition);
            ps.setString(3, location);
            ps.setString(4, availability);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Equipment added successfully.");
            loadEquipment();

            typeField.setText("");
            conditionField.setText("");
            locationField.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
