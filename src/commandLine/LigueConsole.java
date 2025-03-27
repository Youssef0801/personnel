package commandLine;

import static commandLineMenus.rendering.examples.util.InOut.getString;

import java.time.LocalDate;
import java.util.ArrayList;

import commandLineMenus.List;
import commandLineMenus.Menu;
import commandLineMenus.Option;

import personnel.*;

public class LigueConsole 
{
    private GestionPersonnel gestionPersonnel;
    private EmployeConsole employeConsole;

    public LigueConsole(GestionPersonnel gestionPersonnel, EmployeConsole employeConsole)
    {
        this.gestionPersonnel = gestionPersonnel;
        this.employeConsole = employeConsole;
    }

    Menu manageLeagues()
    {
        Menu menu = new Menu("Manage leagues", "l");
        menu.add(displayLeagues());
        menu.add(addLeague());
        menu.add(selectLeague());
        menu.addBack("q");
        return menu;
    }

    private Option displayLeagues()
    {
        return new Option("Display leagues", "l", () -> {System.out.println(gestionPersonnel.getLeagues());});
    }

    private Option displayLeague(final Ligue league)
    {
        return new Option("Display league", "l", 
                () -> 
                {
                    System.out.println(league);
                    System.out.println("administered by " + league.getAdministrator());
                }
        );
    }
    private Option displayEmployees(final Ligue league)
    {
        return new Option("Display employees", "l", () -> {System.out.println(league.getEmployes());});
    }

    private Option addLeague()
    {
        return new Option("Add a league", "a", () -> 
        {
            try
            {
                gestionPersonnel.addLeague(getString("Name: "));
            }
            catch(SauvegardeImpossible exception)
            {
                System.err.println("Unable to save this league");
            }
        });
    }
    
    private Menu editLeague(Ligue league)
    {
        Menu menu = new Menu("Edit " + league.getName());
        menu.add(displayLeague(league));
        menu.add(manageEmployees(league));
        //menu.add(changeAdministrator(league));
        menu.add(renameLeague(league));
        menu.add(deleteLeague(league));
        menu.addBack("q");
        return menu;
    }

    private Option renameLeague(final Ligue league)
    {
        return new Option("Rename", "r", 
                () -> {league.setName(getString("New name: "));});
    }

    private List<Ligue> selectLeague()
    {
        return new List<Ligue>("Select a league", "e", 
                () -> new ArrayList<>(gestionPersonnel.getLeagues()),
                (element) -> editLeague(element)
                );
    }
    private Option addEmployee(final Ligue league)
    {
        return new Option("Add an employee", "a",
                () -> 
                {
                    league.addEmploye(getString("Name: "), 
                        getString("First name: "), getString("Email: "), 
                        getString("Password: "), LocalDate.of(Integer.parseInt(getString("Year:")),Integer.parseInt(getString("Month:")),Integer.parseInt(getString("Day:"))),LocalDate.of(Integer.parseInt(getString("Year:")),Integer.parseInt(getString("Month:")),Integer.parseInt(getString("Day:"))));
                }
        );
    }
    

    private Menu manageEmployees(Ligue league)
    {
        Menu menu = new Menu("Manage employees of " + league.getName(), "m");
        menu.add(displayEmployees(league));
        menu.add(new List<>("Select an employee", "s",
                () -> new ArrayList<>(league.getEmployes()),
                employeConsole.editEmployee()
                ));
        menu.add(addEmployee(league));
        menu.addBack("q");
        return menu;
    }


    private List<Employe> deleteEmployee(final Ligue league)
    {
        return new List<>("Delete an employee", "s", 
                () -> new ArrayList<>(league.getEmployes()),
                (index, element) -> {element.remove();}
                );
    }
    
    private List<Employe> modifyAdministrator(final Ligue league)
    {
        return null;
    }		

    private List<Employe> modifyEmployee(final Ligue league)
    {
        return new List<>("Modify an employee", "e", 
                () -> new ArrayList<>(league.getEmployes()),
                employeConsole.editEmployee()
                );
    }
    
    private Option deleteLeague(Ligue league)
    {
        return new Option("Delete", "d", () -> {league.remove();});
    }
    
    private Option changeAdministrator(final Ligue league)
    {
        return new Option("Change administrator", "a", () -> {
            new List<Employe>("Select a new administrator", "s",
                () -> new ArrayList<>(league.getEmployes()),
                (employe) -> {
                    league.setAdministrator(employe);
                    return new Option("Administrator changed successfully", "c", () -> {});
                }
            ).start();
            System.out.println("Administrator changed successfully.");
        });
    }
}
