package src.ui;

import src.db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class UserForm extends JFrame {
    JTable table;
    DefaultTableModel model;
    JTextField nameField;
    JComboBox<String> roleBox;

    public UserForm() {
        setTitle("User Management");
        setSize(600, 400);
        setLayout(null);

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID", "Name", "Role"});
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(10, 10, 560, 180);
        add(sp);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(10, 200, 50, 25);
        add(nameLabel);
        nameField = new JTextField();
        nameField.setBounds(70, 200, 150, 25);
        add(nameField);

        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setBounds(240, 200, 40, 25);
        add(roleLabel);
        roleBox = new JComboBox<>(new String[]{"Student", "Admin", "Technician"});
        roleBox.setBounds(290, 200, 100, 25);
        add(roleBox);

        JButton addBtn = new JButton("Add User");
        addBtn.setBounds(400, 200, 100, 25);
        add(addBtn);

        JButton deleteBtn = new JButton("Delete User");
        deleteBtn.setBounds(400, 230, 120, 25);
        add(deleteBtn);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBounds(400, 260, 100, 25);
        add(refreshBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(10, 300, 80, 25);
        add(backBtn);

        addBtn.addActionListener(e -> addUser());
        deleteBtn.addActionListener(e -> deleteUser());
        refreshBtn.addActionListener(e -> loadUsers());
        backBtn.addActionListener(e -> {
            this.dispose();
            new MainMenu().setVisible(true);
        });

        loadUsers();
    }

    void loadUsers() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Users");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("role")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void addUser() {
        String name = nameField.getText();
        String role = roleBox.getSelectedItem().toString();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO Users (name, role) VALUES (?, ?)");
            ps.setString(1, name);
            ps.setString(2, role);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "User added successfully.");
            nameField.setText("");
            loadUsers();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    void deleteUser() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a user to delete.");
            return;
        }

        int userId = (int) model.getValueAt(row, 0);

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM Users WHERE user_id = ?");
            ps.setInt(1, userId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "User deleted.");
            loadUsers();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}