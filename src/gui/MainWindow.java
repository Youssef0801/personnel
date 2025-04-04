
package gui;

import javax.swing.*;

public class MainWindow extends JFrame {
    public MainWindow() {
        setTitle("Gestion du personnel");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane onglets = new JTabbedPane();
        onglets.addTab("Employés", new EmployePanel());
        // onglets.addTab("Ligues", new LiguePanel()); à faire pareil

        add(onglets);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainWindow::new);
    }
}
