package src.ui;

import src.db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class MaintenanceForm extends JFrame {
    JTable table, logTable;
    DefaultTableModel model, logModel;
    JTextField techField, descField;

    public MaintenanceForm() {
        setTitle("Maintenance Management");
        setSize(800, 600);
        setLayout(null);

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID", "Type", "Condition", "Location"});
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(10, 10, 760, 120);
        add(sp);

        JLabel techLabel = new JLabel("Technician ID:");
        techLabel.setBounds(10, 140, 100, 25);
        add(techLabel);

        techField = new JTextField();
        techField.setBounds(120, 140, 100, 25);
        add(techField);

        JLabel descLabel = new JLabel("Description:");
        descLabel.setBounds(10, 180, 100, 25);
        add(descLabel);

        descField = new JTextField();
        descField.setBounds(120, 180, 400, 25);
        add(descField);

        JButton assignBtn = new JButton("Assign Technician");
        assignBtn.setBounds(120, 220, 200, 30);
        add(assignBtn);

        assignBtn.addActionListener(e -> assignTechnician());

        // Maintenance Logs Table
        logModel = new DefaultTableModel();
        logModel.setColumnIdentifiers(new String[]{"Log ID", "Equipment ID", "Technician ID", "Date", "Description"});
        logTable = new JTable(logModel);
        JScrollPane logPane = new JScrollPane(logTable);
        logPane.setBounds(10, 270, 760, 270);
        add(logPane);

        loadData();
        loadLogs();
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

    private void assignTechnician() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select equipment first.");
            return;
        }

        int equipmentId = (int) model.getValueAt(row, 0);
        String technicianId = techField.getText();
        String description = descField.getText();

        try (Connection conn = DBConnection.getConnection()) {
            CallableStatement cs = conn.prepareCall("CALL AssignTechnician(?, ?, ?)");
            cs.setInt(1, Integer.parseInt(technicianId));
            cs.setInt(2, equipmentId);
            cs.setString(3, description);
            cs.execute();

            JOptionPane.showMessageDialog(this, "Technician assigned successfully.");
            loadData();
            loadLogs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadLogs() {
        logModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM MaintenanceLogs");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                logModel.addRow(new Object[]{
                        rs.getInt("log_id"),
                        rs.getInt("equipment_id"),
                        rs.getInt("technician_id"),
                        rs.getDate("date1"),
                        rs.getString("description1")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
