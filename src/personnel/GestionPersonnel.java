package personnel;

import java.io.Serializable;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.time.LocalDate;

/**
 * Personnel management. Only one object of this class exists.
 * It is not possible to instantiate this class directly,
 * the method {@link #getGestionPersonnel getGestionPersonnel}
 * does it automatically and always returns the same object.
 * If {@link #save()} was called during a previous execution,
 * the saved object is returned.
 */

public class GestionPersonnel implements Serializable
{
    private static final long serialVersionUID = -105283113987886425L;
    private static GestionPersonnel GestionPersonnel = null;
    private SortedSet<Ligue> leagues;
    private Employe root = addRoot("root", "toor");
    public final static int SERIALIZATION = 1, JDBC = 2, 
            TYPE_GATEWAY = JDBC;  
    private static Passerelle gateway = TYPE_GATEWAY == JDBC ? new jdbc.JDBC() : new serialisation.Serialization();	
    
    /**
     * Returns the unique instance of this class.
     * Creates this object if it does not already exist.
     * @return the unique object of type {@link GestionPersonnel}.
     */
    public Employe addRoot(String name, String password) {
        Employe root = new Employe(this, 0, null, name, "", "", password, null, null);
        try {
            if (getRoot() != null) {
                gateway.insert(root);
            }
        } catch (SauvegardeImpossible e) {
            e.printStackTrace();
        }
        return this.root = root;
    }
    
    public Employe addRoot(int id, String name, String firstName, String email, String password, LocalDate arrival, LocalDate departure) {
        Employe root = new Employe(this, id, null, name, firstName, email, password, arrival, departure);
        this.root = root;
        return root;
    }
    
    public static GestionPersonnel getGestionPersonnel()
    {
        if (GestionPersonnel == null)
        {
            GestionPersonnel = gateway.getGestionPersonnel();
            if (GestionPersonnel == null)
                GestionPersonnel = new GestionPersonnel();
        }
        return GestionPersonnel;
    }

    public GestionPersonnel()
    {
        if (GestionPersonnel != null)
            throw new RuntimeException("You can only create one instance of this object.");
        leagues = new TreeSet<>();
        GestionPersonnel = this;
    }
    
    public void save() throws SauvegardeImpossible
    {
        gateway.saveGestionPersonnel(this);
    }
    
    /**
     * Returns the league whose administrator is the given administrator,
     * null if they are not an administrator.
     * @param administrator the administrator of the league being searched for.
     * @return the league whose administrator is the given administrator.
     */
    
    public Ligue getLeague(Employe administrator)
    {
        if (administrator.isAdmin(administrator.getLeague()))
            return administrator.getLeague();
        else
            return null;
    }

    /**
     * Returns all registered leagues.
     * @return all registered leagues.
     */
    
    public SortedSet<Ligue> getLeagues()
    {
        return Collections.unmodifiableSortedSet(leagues);
    }

    public Ligue addLeague(String name) throws SauvegardeImpossible
    {
        Ligue league = new Ligue(this, name); 
        leagues.add(league);
        return league;
    }
    
    public Ligue addLeague(int id, String name)
    {
        Ligue league = new Ligue(this, id, name);
        leagues.add(league);
        return league;
    }

    void remove(Ligue league)
    {
        leagues.remove(league);
    }
    
    int insert(Ligue league) throws SauvegardeImpossible
    {
        return gateway.insert(league);
    }
    int insert(Employe employe) throws SauvegardeImpossible
    {
        return gateway.insert(employe);
    }

    public void update(Ligue league) throws SauvegardeImpossible
    {
        gateway.update(league);
    }

    public void update(Employe employe) throws SauvegardeImpossible
    {
        gateway.update(employe);
    }

    public void delete(Employe employe) throws SauvegardeImpossible
    {
        gateway.delete(employe);
    }

    /**
     * Returns the root (super-user).
     * @return the root.
     */
    
    public Employe getRoot()
    {
        return root;
    }
}
