package src;

import src.ui.MainMenu;

import javax.swing.*;

public class CampusApp {

    public static void main(String args[]){
        SwingUtilities.invokeLater(() -> {
            new MainMenu().setVisible(true);
        });
    }
}
