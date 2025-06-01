package src.ui;

import javax.swing.*;
import java.awt.event.*;

public class MainMenu extends JFrame {
    public MainMenu() {
        setTitle("Campus Equipment Booking System");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JButton btnEquipment = new JButton("Manage Equipment");
        btnEquipment.setBounds(100, 50, 200, 40);
        add(btnEquipment);

        JButton btnBooking = new JButton("Manage Bookings");
        btnBooking.setBounds(100, 100, 200, 40);
        add(btnBooking);

        JButton btnMaintenance = new JButton("Manage Maintenance");
        btnMaintenance.setBounds(100, 150, 200, 40);
        add(btnMaintenance);

        btnEquipment.addActionListener(e -> new EquipmentForm().setVisible(true));
        btnBooking.addActionListener(e -> new BookingForm().setVisible(true));
        btnMaintenance.addActionListener(e -> new MaintenanceForm().setVisible(true));
    }

    public static void main(String[] args) {
        new MainMenu().setVisible(true);
    }
}

