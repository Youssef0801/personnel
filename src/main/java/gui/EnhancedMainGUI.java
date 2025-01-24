package gui;

import personnel.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.time.LocalDate;

public class EnhancedMainGUI {
    private GestionPersonnel gestionPersonnel;
    private JFrame frame;
    private boolean isAdminLoggedIn = false;

    public EnhancedMainGUI(GestionPersonnel gestionPersonnel) {
        this.gestionPersonnel = gestionPersonnel;
        showInitialImage();
    }

    private void showInitialImage() {
        JFrame initialFrame = new JFrame("Évaluez ce visage sur 100");
        initialFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initialFrame.setSize(400, 400);
    
        // Use an absolute path for testing
        ImageIcon imageIcon = new ImageIcon("C:/Users/quali/OneDrive/Bureau/JVPROJECTecole/src/main/java/gui/image1.png");
        JLabel imageLabel = new JLabel(imageIcon);
        JTextField ratingField = new JTextField();
        JButton submitButton = new JButton("Soumettre");
    
        submitButton.addActionListener(e -> {
            String rating = ratingField.getText().trim();
            if ("100".equals(rating)) {
                initialFrame.dispose();
                setupUI();
            } else {
                JOptionPane.showMessageDialog(initialFrame, "Recommence");
            }
        });
    
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(imageLabel, BorderLayout.CENTER);
        panel.add(ratingField, BorderLayout.SOUTH);
        panel.add(submitButton, BorderLayout.EAST);
    
        initialFrame.add(panel);
        initialFrame.setVisible(true);
    }

    private void setupUI() {
        frame = new JFrame("Interface de piratage cybernétique");
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Accueil", createHomePanel());
        tabbedPane.addTab("Gestion des utilisateurs", createUserManagementPanel());
        tabbedPane.addTab("Gestion des ligues", createLeagueManagementPanel());
        tabbedPane.addTab("Journaux", createLogsPanel());

        frame.add(tabbedPane);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Bienvenue dans le système de gestion du personnel", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(welcomeLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3, 10, 10));
        JButton adminLoginButton = new JButton("Connexion administrateur");
        JButton quitButton = new JButton("Quitter");

        styleButton(adminLoginButton);
        styleButton(quitButton);

        adminLoginButton.addActionListener(e -> adminLogin());
        quitButton.addActionListener(e -> quitApplication());

        buttonPanel.add(adminLoginButton);
        buttonPanel.add(quitButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Gestion des utilisateurs", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        DefaultListModel<String> userListModel = new DefaultListModel<>();
        JList<String> userList = new JList<>(userListModel);
        JScrollPane scrollPane = new JScrollPane(userList);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3, 10, 10));
        JButton addUserButton = new JButton("Ajouter utilisateur");
        JButton updateUserButton = new JButton("Mettre à jour utilisateur");
        JButton deleteUserButton = new JButton("Supprimer utilisateur");

        styleButton(addUserButton);
        styleButton(updateUserButton);
        styleButton(deleteUserButton);

        addUserButton.addActionListener(e -> createUser(userListModel));
        updateUserButton.addActionListener(e -> updateUser(userList));
        deleteUserButton.addActionListener(e -> deleteUser(userListModel, userList.getSelectedValue()));

        buttonPanel.add(addUserButton);
        buttonPanel.add(updateUserButton);
        buttonPanel.add(deleteUserButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshUserList(userListModel);
        return panel;
    }

    private JPanel createLeagueManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Gestion des ligues", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        DefaultListModel<String> leagueListModel = new DefaultListModel<>();
        JList<String> leagueList = new JList<>(leagueListModel);
        JScrollPane scrollPane = new JScrollPane(leagueList);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3, 10, 10));
        JButton addLeagueButton = new JButton("Ajouter ligue");
        JButton updateLeagueButton = new JButton("Mettre à jour ligue");
        JButton deleteLeagueButton = new JButton("Supprimer ligue");

        styleButton(addLeagueButton);
        styleButton(updateLeagueButton);
        styleButton(deleteLeagueButton);

        addLeagueButton.addActionListener(e -> {
            if (isAdminLoggedIn) {
                createLeague(leagueListModel);
            } else {
                JOptionPane.showMessageDialog(frame, "Seul l'administrateur peut créer des ligues.");
            }
        });
        updateLeagueButton.addActionListener(e -> updateLeague(leagueList));
        deleteLeagueButton.addActionListener(e -> deleteLeague(leagueListModel, leagueList.getSelectedValue()));

        buttonPanel.add(addLeagueButton);
        buttonPanel.add(updateLeagueButton);
        buttonPanel.add(deleteLeagueButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshLeagueList(leagueListModel);
        return panel;
    }

    private JPanel createLogsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Journaux du système", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(Color.BLACK);
        logArea.setForeground(Color.GREEN);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshLogsButton = new JButton("Actualiser les journaux");
        styleButton(refreshLogsButton);
        refreshLogsButton.addActionListener(e -> refreshLogs(logArea));
        panel.add(refreshLogsButton, BorderLayout.SOUTH);

        return panel;
    }

    private void styleButton(JButton button) {
        button.setBackground(Color.BLACK);
        button.setForeground(Color.GREEN);
        button.setFont(new Font("Monospaced", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.GREEN));
    }

    private void refreshUserList(DefaultListModel<String> userListModel) {
        userListModel.clear();
        Ligue rootLigue = gestionPersonnel.getRoot().getLigue();
        if (rootLigue != null) {
            for (Employe user : rootLigue.getEmployes()) {
                userListModel.addElement(user.getNom() + " " + user.getPrenom());
            }
        }
    }

    private void refreshLeagueList(DefaultListModel<String> leagueListModel) {
        leagueListModel.clear();
        for (Ligue ligue : gestionPersonnel.getLigues()) {
            leagueListModel.addElement(ligue.getNom());
        }
    }

    private void refreshLogs(JTextArea logArea) {
        logArea.setText("Journaux actualisés à " + java.time.LocalTime.now());
    }

    private void adminLogin() {
        JPasswordField passwordField = new JPasswordField();
        int result = JOptionPane.showConfirmDialog(frame, passwordField, "Entrez le mot de passe administrateur", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String password = new String(passwordField.getPassword());
            if ("Mastabatata".equals(password)) {
                isAdminLoggedIn = true;
                JOptionPane.showMessageDialog(frame, "Connexion administrateur réussie.");
                showHackerAnimation("Connexion administrateur réussie...");
            } else {
                JOptionPane.showMessageDialog(frame, "Mot de passe incorrect.");
            }
        }
    }

    private void createUser(DefaultListModel<String> userListModel) {
        if (gestionPersonnel.getLigues().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Aucune ligue n'existe. Créez une ligue avant d'ajouter des utilisateurs.");
            return;
        }

        JTextField usernameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();

        JPanel userPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        userPanel.add(new JLabel("Nom d'utilisateur:"));
        userPanel.add(usernameField);
        userPanel.add(new JLabel("Email:"));
        userPanel.add(emailField);
        userPanel.add(new JLabel("Mot de passe:"));
        userPanel.add(passwordField);
        userPanel.add(new JLabel("Prénom:"));
        userPanel.add(firstNameField);
        userPanel.add(new JLabel("Nom:"));
        userPanel.add(lastNameField);

        int result = JOptionPane.showConfirmDialog(frame, userPanel, "Créer utilisateur", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();

            if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty()) {
                gestionPersonnel.getRoot().getLigue().addEmploye(username, firstName, email, password, LocalDate.now(), null);
                JOptionPane.showMessageDialog(frame, "Utilisateur créé avec succès");
                refreshUserList(userListModel);
                showHackerAnimation("Création de l'utilisateur...");
            } else {
                JOptionPane.showMessageDialog(frame, "Tous les champs sont obligatoires");
            }
        }
    }

    private void updateUser(JList<String> userList) {
        String selectedUser = userList.getSelectedValue();
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(frame, "Aucun utilisateur sélectionné");
            return;
        }

        Employe user = gestionPersonnel.getRoot().getLigue().getEmployes().stream()
                .filter(e -> (e.getNom() + " " + e.getPrenom()).equals(selectedUser))
                .findFirst().orElse(null);

        if (user == null) {
            JOptionPane.showMessageDialog(frame, "Utilisateur non trouvé");
            return;
        }

        JTextField emailField = new JTextField(user.getMail());
        JPasswordField passwordField = new JPasswordField();
        JTextField firstNameField = new JTextField(user.getPrenom());
        JTextField lastNameField = new JTextField(user.getNom());

        JPanel userPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        userPanel.add(new JLabel("Email:"));
        userPanel.add(emailField);
        userPanel.add(new JLabel("Mot de passe:"));
        userPanel.add(passwordField);
        userPanel.add(new JLabel("Prénom:"));
        userPanel.add(firstNameField);
        userPanel.add(new JLabel("Nom:"));
        userPanel.add(lastNameField);

        int result = JOptionPane.showConfirmDialog(frame, userPanel, "Mettre à jour utilisateur", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            user.setMail(emailField.getText().trim());
            user.setPassword(new String(passwordField.getPassword()));
            user.setPrenom(firstNameField.getText().trim());
            user.setNom(lastNameField.getText().trim());
            JOptionPane.showMessageDialog(frame, "Utilisateur mis à jour avec succès");
            showHackerAnimation("Mise à jour de l'utilisateur...");
        }
    }

    private void deleteUser(DefaultListModel<String> userListModel, String username) {
        if (username == null) {
            JOptionPane.showMessageDialog(frame, "Aucun utilisateur sélectionné");
            return;
        }

        Employe user = gestionPersonnel.getRoot().getLigue().getEmployes().stream()
                .filter(e -> (e.getNom() + " " + e.getPrenom()).equals(username))
                .findFirst().orElse(null);

        if (user != null) {
            user.remove();
            JOptionPane.showMessageDialog(frame, "Utilisateur supprimé avec succès");
            refreshUserList(userListModel);
            showHackerAnimation("Suppression de l'utilisateur...");
        } else {
            JOptionPane.showMessageDialog(frame, "Utilisateur non trouvé");
        }
    }

    private void createLeague(DefaultListModel<String> leagueListModel) {
        String leagueName = JOptionPane.showInputDialog(frame, "Entrez le nom de la ligue:");
        if (leagueName != null && !leagueName.trim().isEmpty()) {
            try {
                gestionPersonnel.addLigue(leagueName);
                JOptionPane.showMessageDialog(frame, "Ligue créée avec succès");
                refreshLeagueList(leagueListModel);
                showHackerAnimation("Création de la ligue...");
            } catch (SauvegardeImpossible e) {
                JOptionPane.showMessageDialog(frame, "Échec de la création de la ligue");
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Nom de ligue invalide");
        }
    }

    private void updateLeague(JList<String> leagueList) {
        String selectedLeague = leagueList.getSelectedValue();
        if (selectedLeague == null) {
            JOptionPane.showMessageDialog(frame, "Aucune ligue sélectionnée");
            return;
        }

        Ligue league = gestionPersonnel.getLigues().stream()
                .filter(l -> l.getNom().equals(selectedLeague))
                .findFirst().orElse(null);

        if (league == null) {
            JOptionPane.showMessageDialog(frame, "Ligue non trouvée");
            return;
        }

        String newName = JOptionPane.showInputDialog(frame, "Entrez le nouveau nom de la ligue:", selectedLeague);
        if (newName != null && !newName.trim().isEmpty()) {
            league.setNom(newName);
            JOptionPane.showMessageDialog(frame, "Ligue mise à jour avec succès");
            refreshLeagueList((DefaultListModel<String>) leagueList.getModel());
            showHackerAnimation("Mise à jour de la ligue...");
        } else {
            JOptionPane.showMessageDialog(frame, "Nom de ligue invalide");
        }
    }

    private void deleteLeague(DefaultListModel<String> leagueListModel, String leagueName) {
        if (leagueName == null) {
            JOptionPane.showMessageDialog(frame, "Aucune ligue sélectionnée");
            return;
        }

        Ligue league = gestionPersonnel.getLigues().stream()
                .filter(l -> l.getNom().equals(leagueName))
                .findFirst().orElse(null);

        if (league != null) {
            league.remove();
            JOptionPane.showMessageDialog(frame, "Ligue supprimée avec succès");
            refreshLeagueList(leagueListModel);
            showHackerAnimation("Suppression de la ligue...");
        } else {
            JOptionPane.showMessageDialog(frame, "Ligue non trouvée");
        }
    }

    private void quitApplication() {
        JOptionPane.showMessageDialog(frame, "Quitter l'application");
        System.exit(0);
    }

    private void showHackerAnimation(String message) {
        JFrame hackerFrame = new JFrame(message);
        JTextArea textArea = new JTextArea();
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.GREEN);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);

        hackerFrame.add(new JScrollPane(textArea));
        hackerFrame.setSize(600, 400);
        hackerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        hackerFrame.setVisible(true);

        new Thread(() -> {
            Random random = new Random();
            String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            for (int i = 0; i < 1000; i++) {
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < 80; j++) {
                    sb.append(characters.charAt(random.nextInt(characters.length())));
                }
                textArea.append(sb.toString() + "\n");
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        GestionPersonnel gestionPersonnel = GestionPersonnel.getGestionPersonnel();
        new EnhancedMainGUI(gestionPersonnel);
    }
}