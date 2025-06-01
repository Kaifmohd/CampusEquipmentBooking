package src.ui;

import src.db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class EquipmentForm extends JFrame {
    JTable table;
    DefaultTableModel model;

    public EquipmentForm() {
        setTitle("Equipment Management");
        setSize(600, 400);
        setLayout(null);

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID", "Type", "Condition", "Location", "Availability"});
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(10, 10, 560, 200);
        add(sp);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setBounds(10, 220, 100, 30);
        add(btnRefresh);

        JButton btnDelete = new JButton("Delete");
        btnDelete.setBounds(120, 220, 100, 30);
        add(btnDelete);

        btnRefresh.addActionListener(e -> loadEquipment());
        btnDelete.addActionListener(e -> deleteSelected());

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
}
