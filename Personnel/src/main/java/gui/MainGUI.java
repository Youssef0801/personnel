package gui;

import personnel.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class MainGUI {
    private GestionPersonnel gestionPersonnel;
    private JFrame frame;
    private JPanel panel;
    private JButton adminLoginButton;
    private JButton createUserButton;
    private JButton quitButton;

    private boolean isAdminLoggedIn = false;

    public MainGUI(GestionPersonnel gestionPersonnel) {
        this.gestionPersonnel = gestionPersonnel;

        frame = new JFrame("Gestion du Personnel des Ligues");
        panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10));

        adminLoginButton = new JButton("Administrator Login");
        createUserButton = new JButton("Create User");
        quitButton = new JButton("Quit");

        adminLoginButton.addActionListener(e -> adminLogin());
        createUserButton.addActionListener(e -> createUser());
        quitButton.addActionListener(e -> quitApplication());

        panel.add(adminLoginButton);
        panel.add(createUserButton);
        panel.add(quitButton);

        frame.add(panel);
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void adminLogin() {
        JPasswordField passwordField = new JPasswordField();
        int result = JOptionPane.showConfirmDialog(frame, passwordField, "Enter Administrator Password", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String password = new String(passwordField.getPassword());
            if ("Mastabatata".equals(password)) {
                isAdminLoggedIn = true;
                JOptionPane.showMessageDialog(frame, "Administrator Login Successful.");
                showAdminMenu();
            } else {
                JOptionPane.showMessageDialog(frame, "Incorrect Password.");
            }
        }
    }

    private void showAdminMenu() {
        JFrame adminFrame = new JFrame("Administrator Menu");
        JPanel adminPanel = new JPanel();
        adminPanel.setLayout(new GridLayout(2, 1, 10, 10));

        JButton createLeagueButton = new JButton("Create League");
        JButton logoutButton = new JButton("Logout");

        createLeagueButton.addActionListener(e -> createLeague());
        logoutButton.addActionListener(e -> {
            isAdminLoggedIn = false;
            adminFrame.dispose();
            JOptionPane.showMessageDialog(frame, "Logged Out.");
        });

        adminPanel.add(createLeagueButton);
        adminPanel.add(logoutButton);

        adminFrame.add(adminPanel);
        adminFrame.setSize(400, 200);
        adminFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        adminFrame.setVisible(true);
    }

    private void createLeague() {
        String leagueName = JOptionPane.showInputDialog(frame, "Enter League Name:");
        if (leagueName != null && !leagueName.trim().isEmpty()) {
            try {
                gestionPersonnel.addLigue(leagueName);
                JOptionPane.showMessageDialog(frame, "League '" + leagueName + "' created successfully.");
            } catch (SauvegardeImpossible e) {
                JOptionPane.showMessageDialog(frame, "Failed to create league: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Invalid League Name.");
        }
    }

    private void createUser() {
        if (gestionPersonnel.getLigues().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No leagues available. Please ask the administrator to create a league first.");
            return;
        }

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField emailField = new JTextField();
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();

        List<String> leagueNames = new ArrayList<>();
        for (Ligue ligue : gestionPersonnel.getLigues()) {
            leagueNames.add(ligue.getNom());
        }
        JComboBox<String> leagueDropdown = new JComboBox<>(leagueNames.toArray(new String[0]));

        JPanel createUserPanel = new JPanel();
        createUserPanel.setLayout(new GridLayout(6, 2, 10, 10));
        createUserPanel.add(new JLabel("Username:"));
        createUserPanel.add(usernameField);
        createUserPanel.add(new JLabel("Password:"));
        createUserPanel.add(passwordField);
        createUserPanel.add(new JLabel("Email:"));
        createUserPanel.add(emailField);
        createUserPanel.add(new JLabel("First Name:"));
        createUserPanel.add(firstNameField);
        createUserPanel.add(new JLabel("Last Name:"));
        createUserPanel.add(lastNameField);
        createUserPanel.add(new JLabel("League:"));
        createUserPanel.add(leagueDropdown);

        int result = JOptionPane.showConfirmDialog(frame, createUserPanel, "Create User", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String email = emailField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String selectedLeague = (String) leagueDropdown.getSelectedItem();

            if (!username.isEmpty() && !password.isEmpty() && !email.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty()) {
                gestionPersonnel.addUser(username, password, email, firstName, lastName, selectedLeague);
                JOptionPane.showMessageDialog(frame, "User '" + username + "' created successfully and assigned to league '" + selectedLeague + "'.");
            } else {
                JOptionPane.showMessageDialog(frame, "All fields are required.");
            }
        }
    }

    private void quitApplication() {
        JOptionPane.showMessageDialog(frame, "Exiting application.");
        System.exit(0);
    }

    public static void main(String[] args) {
        GestionPersonnel gestionPersonnel = new GestionPersonnel();
        new MainGUI(gestionPersonnel);
    }
}
