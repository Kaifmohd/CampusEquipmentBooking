package src.ui;

import javax.swing.*;
import java.awt.event.*;

public class MainMenu extends JFrame {
    public MainMenu() {
        setTitle("Campus Equipment Booking System");
        setSize(400, 350);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        int btnWidth = 200;
        int btnHeight = 40;
        int startX = 100;
        int startY = 50;
        int gap = 15;

        JButton btnEquipment = new JButton("Manage Equipment");
        btnEquipment.setBounds(startX, startY, btnWidth, btnHeight);
        add(btnEquipment);

        JButton btnBooking = new JButton("Manage Bookings");
        btnBooking.setBounds(startX, startY + (btnHeight + gap), btnWidth, btnHeight);
        add(btnBooking);

        JButton btnMaintenance = new JButton("Manage Maintenance");
        btnMaintenance.setBounds(startX, startY + 2 * (btnHeight + gap), btnWidth, btnHeight);
        add(btnMaintenance);

        JButton btnUsers = new JButton("Manage Users");
        btnUsers.setBounds(startX, startY + 3 * (btnHeight + gap), btnWidth, btnHeight);
        add(btnUsers);

        btnEquipment.addActionListener(e -> {
            new EquipmentForm().setVisible(true);
            this.dispose();
        });
        btnBooking.addActionListener(e -> {
            new BookingForm().setVisible(true);
        });
        btnMaintenance.addActionListener(e -> {
            new MaintenanceForm().setVisible(true);
        });
        btnUsers.addActionListener(e -> {
            new UserForm().setVisible(true);
        });
    }

    public static void main(String[] args) {
        new MainMenu().setVisible(true);
    }
}

