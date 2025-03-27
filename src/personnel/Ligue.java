package personnel;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Represents a league. Each league is linked to a list
 * of employees, including an administrator. Since it is not possible
 * to create an employee without assigning them to a league, the root is 
 * the administrator of the league until an administrator 
 * has been assigned using the {@link #setAdministrator} function.
 */

public class Ligue implements Serializable, Comparable<Ligue>
{
    private static final long serialVersionUID = 1L;
    private int id = -1;
    private String name;
    private SortedSet<Employe> employees;
    private Employe administrator;
    private GestionPersonnel personnelManagement;
    
    /**
     * Creates a league.
     * @param name the name of the league.
     */
    
    Ligue(GestionPersonnel personnelManagement, String name) throws SauvegardeImpossible
    {
        this(personnelManagement, -1, name);
        this.id = personnelManagement.insert(this); 
    }

    Ligue(GestionPersonnel personnelManagement, int id, String name)
    {
        this.name = name;
        employees = new TreeSet<>();
        this.personnelManagement = personnelManagement;
        administrator = personnelManagement.getRoot();
        this.id = id;
    }

    /**
     * Returns the name of the league.
     * @return the name of the league.
     */

    public String getName()
    {
        return name;
    }

    /**
     * Changes the name.
     * @param name the new name of the league.
     */

    public void setName(String name)
    {
        this.name = name;
        try 
        {
            personnelManagement.update(this);
        } 
        catch (SauvegardeImpossible e) 
        {
            e.printStackTrace();
        }
    }

    /**
     * Returns the administrator of the league.
     * @return the administrator of the league.
     */
    
    public Employe getAdministrator()
    {
        return administrator;
    }

    public int getId()
    {
        return id;
    }
    /**
     * Sets the given employee as the administrator of the league.
     * Throws InsufficientRights if the employee is not 
     * a member of the league or the root. Revokes the rights of the previous 
     * administrator.
     * @param administrator the new administrator of the league.
     */
    
    public void setAdministrator(Employe administrator)
    {
        Employe root = personnelManagement.getRoot();
        if (administrator != root && administrator.getLeague() != this)
            throw new DroitsInsuffisants();
        this.administrator = administrator;
        try 
        {
            personnelManagement.update(this);
        } 
        catch (SauvegardeImpossible e) 
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Removes administrator rights if the user
     * is the administrator.
     * @param administrator the administrator to remove.
     */
    public void deleteAdministrator(Employe administrator)
    {
        Employe root = personnelManagement.getRoot();
        if (administrator != root && administrator.getLeague().getAdministrator() == administrator)
            this.administrator = null;
    }

    /**
     * Returns the employees of the league.
     * @return the employees of the league in alphabetical order.
     */
    
    public SortedSet<Employe> getEmployes()
    {
        return Collections.unmodifiableSortedSet(employees);
    }

    /**
     * Adds an employee to the league. This method 
     * is the only way to create an employee.
     * @param name the name of the employee.
     * @param firstName the first name of the employee.
     * @param email the email address of the employee.
     * @param password the password of the employee.
     * @param arrival the arrival date of the employee.
     * @param departure the departure date of the employee.
     * @return the created employee. 
     */

    public Employe addEmploye(String name, String firstName, String email, String password, LocalDate arrival, LocalDate departure)
    {
        Employe employee = new Employe(this.personnelManagement, id, this, name, firstName, email, password, arrival, departure);
        employees.add(employee);
        return employee;
    }
    
    void remove(Employe employee)
    {
        employees.remove(employee);
    }
    
    /**
     * Deletes the league, which also deletes all employees
     * in the league.
     */
    
    public void remove()
    {
        personnelManagement.remove(this);
    }
    

    @Override
    public int compareTo(Ligue other)
    {
        return getName().compareTo(other.getName());
    }
    
    @Override
    public String toString()
    {
        return name;
    }
}
