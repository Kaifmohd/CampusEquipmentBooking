package src.ui;

import src.db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class MaintenanceForm extends JFrame {
    JTable table;
    DefaultTableModel model;

    public MaintenanceForm() {
        setTitle("Maintenance Management");
        setSize(600, 400);
        setLayout(null);

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID", "Type", "Condition", "Location"});
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(10, 10, 560, 180);
        add(sp);

        JLabel techLabel = new JLabel("Technician ID:");
        techLabel.setBounds(10, 200, 100, 25);
        add(techLabel);

        JTextField techField = new JTextField();
        techField.setBounds(120, 200, 100, 25);
        add(techField);

        JLabel descLabel = new JLabel("Description:");
        descLabel.setBounds(10, 240, 100, 25);
        add(descLabel);

        JTextField descField = new JTextField();
        descField.setBounds(120, 240, 300, 25);
        add(descField);

        JButton assignBtn = new JButton("Assign Technician");
        assignBtn.setBounds(120, 280, 200, 30);
        add(assignBtn);

        assignBtn.addActionListener(e -> assignTechnician(techField.getText(), descField.getText()));
        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT equipment_id, type1, condition1, location FROM Equipment WHERE condition1 = 'Needs Maintenance'");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("equipment_id"),
                        rs.getString("type1"),
                        rs.getString("condition1"),
                        rs.getString("location")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void assignTechnician(String technicianId, String description) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select equipment first.");
            return;
        }

        int equipmentId = (int) model.getValueAt(row, 0);

        try (Connection conn = DBConnection.getConnection()) {
            CallableStatement cs = conn.prepareCall("{CALL AssignTechnician(?, ?, ?)}");
            cs.setInt(1, Integer.parseInt(technicianId));
            cs.setInt(2, equipmentId);
            cs.setString(3, description);
            cs.execute();

            JOptionPane.showMessageDialog(this, "Technician assigned successfully.");
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
