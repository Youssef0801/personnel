package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import jdbc.JDBC;
import personnel.*;

public class EmployePanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private GestionPersonnel gestionPersonnel;
    private JDBC jdbc;

    public EmployePanel() {
        setLayout(new BorderLayout());

        jdbc = new JDBC();
        gestionPersonnel = jdbc.getGestionPersonnel();

        model = new DefaultTableModel(new String[]{"ID", "Nom", "Prénom", "Email", "Ligue"}, 0);
        table = new JTable(model);

        JPanel buttonsPanel = new JPanel();
        JButton btnAjouter = new JButton("Ajouter");
        JButton btnSupprimer = new JButton("Supprimer");

        buttonsPanel.add(btnAjouter);
        buttonsPanel.add(btnSupprimer);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);

        chargerEmployes();

        // Actions
        btnAjouter.addActionListener(e -> ajouterEmploye());
        btnSupprimer.addActionListener(e -> supprimerEmploye());
    }

    private void chargerEmployes() {
        model.setRowCount(0);
        for (Ligue ligue : gestionPersonnel.getLeagues()) {
            for (Employe e : ligue.getEmployes()) {
                model.addRow(new Object[]{
                        e.getId(),
                        e.getName(),
                        e.getFirstName(),
                        e.getEmail(),
                        ligue.getName()
                });
            }
        }
    }

    private void ajouterEmploye() {
        JTextField nomField = new JTextField();
        JTextField prenomField = new JTextField();
        JTextField mailField = new JTextField();
        JTextField mdpField = new JTextField();

        // Choisir la ligue
        JComboBox<Ligue> ligueBox = new JComboBox<>();
        for (Ligue l : gestionPersonnel.getLeagues()) ligueBox.addItem(l);

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Nom:")); panel.add(nomField);
        panel.add(new JLabel("Prénom:")); panel.add(prenomField);
        panel.add(new JLabel("Mail:")); panel.add(mailField);
        panel.add(new JLabel("Mot de passe:")); panel.add(mdpField);
        panel.add(new JLabel("Ligue:")); panel.add(ligueBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Nouvel employé",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Ligue ligue = (Ligue) ligueBox.getSelectedItem();
                Employe emp = ligue.addEmploye(
                    nomField.getText(),
                    prenomField.getText(),
                    mailField.getText(),
                    mdpField.getText(),
                    null,
                    null
                );
            
                int id = jdbc.insert(emp);
                emp.setId(id);
            
                chargerEmployes();
            } catch (SauvegardeImpossible ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur d'insertion", "Erreur", JOptionPane.ERROR_MESSAGE);
            }            
        }
    }

    private void supprimerEmploye() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) model.getValueAt(selectedRow, 0);
            for (Ligue ligue : gestionPersonnel.getLeagues()) {
                for (Employe e : ligue.getEmployes()) {
                    if (e.getId() == id) {
                        try {
                            jdbc.delete(e);
                            ligue.remove(e);
                            chargerEmployes();
                        } catch (SauvegardeImpossible ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(this, "Erreur de suppression", "Erreur", JOptionPane.ERROR_MESSAGE);
                        }
                        return;
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Sélectionne un employé d'abord.");
        }
    }
}
