package personnel;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Employé d'une ligue hébergée par la M2L. Certains peuvent 
 * être administrateurs des employés de leur ligue.
 * Un seul employé, rattaché à aucune ligue, est le root.
 * Il est impossible d'instancier directement un employé, 
 * il faut passer la méthode {@link Ligue#addEmploye addEmploye}.
 */

public class Employe implements Serializable, Comparable<Employe>
{
    private static final long serialVersionUID = 4795721718037994734L;
    private String name, firstName, password, email;
    private Ligue league;
    private int id = -1;
    private LocalDate arrival, departure;
    private GestionPersonnel personnelManagement;
    private InvalideDate invalidArrival;
    private InvalideDate invalidDeparture;

    Employe(GestionPersonnel personnelManagement, Ligue league, String name, String firstName, String email, String password, LocalDate arrival, LocalDate departure) throws SauvegardeImpossible
    {
        this(personnelManagement, -1, league, name, firstName, email, password, arrival, departure);
        this.id = personnelManagement.insert(this);
    }
    
    public Employe(GestionPersonnel personnelManagement, int id, Ligue league, String name, String firstName, String email, String password, LocalDate arrival, LocalDate departure) {
        this.id = id;
        this.personnelManagement = personnelManagement;
        this.name = name;
        this.firstName = firstName;
        this.password = password;
        this.email = email;
        this.arrival = arrival;
        this.departure = departure;
        this.league = league;
    }
    
    public boolean isAdmin(Ligue league)
    {
        return league.getAdministrator() == this;
    }
    
    public boolean isRoot()
    {
        return personnelManagement.getRoot() == this;
    }
    
    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }
    
    public String getName()
    {
        return name;
    }

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

    public String getFirstName()
    {
        return firstName;
    }
    
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
        try 
        {
            personnelManagement.update(this);
        } 
        catch (SauvegardeImpossible e) 
        {
            e.printStackTrace();
        }
    }

    public String getEmail()
    {
        return email;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
        try 
        {
            personnelManagement.update(this);
        } 
        catch (SauvegardeImpossible e) 
        {
            e.printStackTrace();
        }
    }

    public boolean checkPassword(String password)
    {
        return this.password.equals(password);
    }

    public void setPassword(String password)
    {
        this.password = password;
        try 
        {
            personnelManagement.update(this);
        } 
        catch (SauvegardeImpossible e) 
        {
            e.printStackTrace();
        }
    }
    
    public String getPassword()
    {
        return password;
    }

    public Ligue getLeague()
    {
        return league;
    }

    public void remove()
    {
        Employe root = personnelManagement.getRoot();
        if (this != root)
        {
            if (isAdmin(getLeague()))
                getLeague().setAdministrator(root);
            getLeague().remove(this);
        }
        else
            throw new ImpossibleDeSupprimerRoot();
    }
    
    public LocalDate getArrival()
    {
        return arrival;
    }
    
    public void setArrival(LocalDate arrival) throws InvalideDate
    {
        if (this.departure != null && this.departure.isBefore(arrival)) {
            throw new InvalideDate(null);
        }
        this.arrival = arrival;
        try 
        {
            personnelManagement.update(this);
        } 
        catch (SauvegardeImpossible e) 
        {
            e.printStackTrace();
        }
    }
    
    public LocalDate getDeparture()
    {
        return departure;
    }
    
    public void setDeparture(LocalDate departure) throws InvalideDate
    {
        if (this.arrival != null && this.arrival.isAfter(departure)) {
            throw new InvalideDate(null);
        }
        this.departure = departure;
        try 
        {
            personnelManagement.update(this);
        } 
        catch (SauvegardeImpossible e) 
        {
            e.printStackTrace();
        }
    }   

    @Override
    public int compareTo(Employe other)
    {
        int cmp = getName().compareTo(other.getName());
        if (cmp != 0)
            return cmp;
        return getFirstName().compareTo(other.getFirstName());
    }
    
    @Override
    public String toString()
    {
        String res = name + " " + firstName + " " + email + " " + arrival;
        if (departure != null) {
            res += " " + departure;
        }
        res += " (";
        if (isRoot())
            res += "super-user";
        else if (isAdmin(league))
            res += "Administrator";
        else
            res += league.toString();
        return res + ")";
    }

}
