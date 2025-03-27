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
        return new Option("Display employee", "l", () -> {System.out.println(employe);});
    }

    ListOption<Employe> editEmployee()
    {
        return (employe) -> editEmployee(employe);		
    }

    Option editEmployee(Employe employe)
    {
            Menu menu = new Menu("Manage account " + employe.getName(), "c");
            menu.add(display(employe));
            menu.add(changeName(employe));
            menu.add(changeFirstName(employe));
            menu.add(changeEmail(employe));
            menu.add(changePassword(employe));
            menu.add(modifyArrivalDate(employe));
            menu.add(modifyDepartureDate(employe));
            menu.add(deleteEmployee(employe));
            menu.add(assignLeagueAdmin(employe));
            menu.add(removeAdminRights(employe));
            menu.addBack("q");
            return menu;
    }

    private Option changeName(final Employe employe)
    {
        return new Option("Change name", "n", 
                () -> {employe.setName(getString("New name: "));}
            );
    }
    
    private Option changeFirstName(final Employe employe)
    {
        return new Option("Change first name", "p", () -> {employe.setFirstName(getString("New first name: "));});
    }
    
    private Option changeEmail(final Employe employe)
    {
        return new Option("Change email", "e", () -> {employe.setEmail(getString("New email: "));});
    }
    
    private Option changePassword(final Employe employe)
    {
        return new Option("Change password", "x", () -> {employe.setPassword(getString("New password: "));});
    }
    
    private Option deleteEmployee(final Employe employe)
    {
        return new Option("Delete employee", "d", () -> {employe.remove();});
    }
    private Option assignLeagueAdmin(final Employe employe)
    {
        return new Option("Assign league administrator role", "a", () -> {employe.getLeague().setAdministrator(employe);});
    }
    private Option removeAdminRights(final Employe employe)
    {
        return new Option("Revoke administrator rights", "r", () -> {employe.getLeague().deleteAdministrator(employe);});
    }
    private Option modifyArrivalDate(final Employe employe) 
    {
        return new Option("Modify arrival date", "m", () -> {
            LocalDate newArrivalDate = LocalDate.of(
                Integer.parseInt(getString("Year: ")),
                Integer.parseInt(getString("Month: ")),
                Integer.parseInt(getString("Day: "))
            );
            try {
                employe.setArrival(newArrivalDate); // Ensure the method is named "setarrive"
            } catch (InvalideDate e) {
                e.printStackTrace();
            }
            System.out.println("Arrival date successfully modified.");
        });
    }
    private Option modifyDepartureDate(final Employe employe) 
    {
        return new Option("Modify departure date", "z", () -> {
            LocalDate newDepartureDate = LocalDate.of(
                Integer.parseInt(getString("Year: ")),
                Integer.parseInt(getString("Month: ")),
                Integer.parseInt(getString("Day: "))
            );
            try {
                employe.setDeparture(newDepartureDate); // Ensure the method is named "setdepart"
            } catch (InvalideDate e) {
                e.printStackTrace();
            }
            System.out.println("Departure date successfully modified.");
        });
    }
}
