package commandLine;

import static commandLineMenus.rendering.examples.util.InOut.getString;

import java.time.LocalDate;

import commandLineMenus.ListOption;
import commandLineMenus.Menu;
import commandLineMenus.Option;
import personnel.Employe;
import personnel.InvalideDate;

public class EmployeConsole 
{
    private Option display(final Employe employe)
    {
        return new Option("Afficher l'employé", "l", () -> {System.out.println(employe);});
    }

    ListOption<Employe> editEmploye()
    {
        return (employe) -> editEmploye(employe);		
    }

    Option editEmploye(Employe employe)
    {
            Menu menu = new Menu("Gérer le compte " + employe.getNom(), "c");
            menu.add(display(employe));
            menu.add(changeLastName(employe));
            menu.add(changeFirstName(employe));
            menu.add(changeEmail(employe));
            menu.add(changePassword(employe));
            menu.add(modifyStartDate(employe));
            menu.add(modifyEndDate(employe));
            menu.add(deleteEmployee(employe));
            menu.add(assignAdminRole(employe));
            menu.add(removeAdminRole(employe));
            menu.addBack("q");
            return menu;
    }

    private Option changeLastName(final Employe employe)
    {
        return new Option("Changer le nom", "n", 
                () -> {employe.setNom(getString("Nouveau nom : "));}
            );
    }
    
    private Option changeFirstName(final Employe employe)
    {
        return new Option("Changer le prénom", "p", () -> {employe.setPrenom(getString("Nouveau prénom : "));});
    }
    
    private Option changeEmail(final Employe employe)
    {
        return new Option("Changer le mail", "e", () -> {employe.setMail(getString("Nouveau mail : "));});
    }
    
    private Option changePassword(final Employe employe)
    {
        return new Option("Changer le password", "x", () -> {employe.setPassword(getString("Nouveau password : "));});
    }
    
    private Option deleteEmployee(final Employe employe)
    {
        return new Option("Supprimer l'employé", "d", () -> {employe.remove();});
    }
    private Option assignAdminRole(final Employe employe)
    {
        return new Option("Donner le rôle d'administrateur de la ligue", "a", () -> {employe.getLigue().setAdministrateur(employe);});
    }
    private Option removeAdminRole(final Employe employe)
    {
        return new Option("Réfuter les droits administrateurs de l'utilisateur", "r", () -> {employe.getLigue().deleteAdministrateur(employe);});
    }
    private Option modifyStartDate(final Employe employe) 
    {
        return new Option("Modifier la date d'arrivée", "m", () -> {
            LocalDate nouvelleDateArrivee = LocalDate.of(
                Integer.parseInt(getString("Année : ")),
                Integer.parseInt(getString("Mois : ")),
                Integer.parseInt(getString("Jour : "))
            );
            try {
                employe.setDebutDate(nouvelleDateArrivee); // Assurez-vous que la méthode s'appelle bien "setarrive"
            } catch (InvalideDate e) {
                e.printStackTrace();
            }
            System.out.println("Date d'arrivée modifiée avec succès.");
        });
    }
    private Option modifyEndDate(final Employe employe) 
    {
        return new Option("Modifier la date de départ", "z", () -> {
            LocalDate nouvelleDateDepart = LocalDate.of(
                Integer.parseInt(getString("Année : ")),
                Integer.parseInt(getString("Mois : ")),
                Integer.parseInt(getString("Jour : "))
            );
            try {
                employe.setEndDate(nouvelleDateDepart); // Assurez-vous que la méthode s'appelle bien "setarrive"
            } catch (InvalideDate e) {
                e.printStackTrace();
            }
            System.out.println("Date de départ modifiée avec succès.");
        });
    }
}
