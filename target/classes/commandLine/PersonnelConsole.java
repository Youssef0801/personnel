package commandLine;

import personnel.*;
import commandLineMenus.*;
import static commandLineMenus.rendering.examples.util.InOut.*;

public class PersonnelConsole
{
    private GestionPersonnel personnelManagement;
    LigueConsole leagueConsole;
    EmployeConsole employeeConsole;
    
    public PersonnelConsole(GestionPersonnel personnelManagement)
    {
        this.personnelManagement = personnelManagement;
        this.employeeConsole = new EmployeConsole();
        this.leagueConsole = new LigueConsole(personnelManagement, employeeConsole);
    }
    
    public void start()
    {
        mainMenu().start();
    }
    
    private Menu mainMenu()
    {
        Menu menu = new Menu("League Personnel Management");
        menu.add(employeeConsole.editEmployee(personnelManagement.getRoot()));
        menu.add(leagueConsole.manageLeagues());
        menu.add(exitMenu());
        return menu;
    }

    private Menu exitMenu()
    {
        Menu menu = new Menu("Exit", "q");
        menu.add(exitAndSave());
        menu.add(exitWithoutSaving());
        menu.addBack("r");
        return menu;
    }
    
    private Option exitAndSave()
    {
        return new Option("Exit and save", "q", 
                () -> 
                {
                    try
                    {
                        personnelManagement.save();
                        Action.QUIT.optionSelected();
                    } 
                    catch (SauvegardeImpossible e)
                    {
                        System.out.println("Unable to save");
                    }
                }
            );
    }
    
    private Option exitWithoutSaving()
    {
        return new Option("Exit without saving", "a", Action.QUIT);
    }
    
    private boolean verifyPassword()
    {
        boolean ok = personnelManagement.getRoot().checkPassword(getString("password: "));
        if (!ok)
            System.out.println("Incorrect password.");
        return ok;
    }
    
    public static void main(String[] args)
    {
        PersonnelConsole personnelConsole = 
                new PersonnelConsole(GestionPersonnel.getGestionPersonnel());
        if (personnelConsole.verifyPassword())
            personnelConsole.start();
    }
}
