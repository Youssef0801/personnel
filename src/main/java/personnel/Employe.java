package main.java.personnel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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
	private String nom, prenom, password, mail;
	private int id = -1 ;
	private Ligue ligue;
	private GestionPersonnel gestionPersonnel;
	private LocalDate dateArrivee;
    	private LocalDate dateDepart;
	
	public Employe(GestionPersonnel gestionPersonnel, Ligue ligue, String nom, String prenom, String mail, String password, LocalDate dateArrivee, LocalDate dateDepart) throws SauvegardeImpossible
    {
        this(gestionPersonnel, ligue, -1, nom, prenom, mail, password, dateArrivee, dateDepart);
        this.id = gestionPersonnel.insert(this);
    }
	
	public Employe(GestionPersonnel gestionPersonnel, Ligue ligue, int id, String nom, String prenom, String mail, String password, LocalDate dateArrivee, LocalDate dateDepart)
    {
        this.gestionPersonnel = gestionPersonnel;
        this.nom = nom;
        this.prenom = prenom;
        this.password = password;
        this.mail = mail;
        this.ligue = ligue;
        this.dateArrivee = dateArrivee;
        this.dateDepart = dateDepart;
        this.id = id;
    }

	

    public Employe(GestionPersonnel gestionPersonnel2, int int1, String string, String string2, String string3,
			String string4, LocalDate localDate, LocalDate localDate2, Ligue ligue2) {
    	
		// TODO Auto-generated constructor stub
	}
 // Getters and setters for dateArrivée and dateDepart
	public LocalDate getDateArrivée() {
        return dateArrivee;
    }

    public void setDateArrivée(LocalDate dateArrivée) throws SauvegardeImpossible {
        this.dateArrivee = dateArrivée;
        gestionPersonnel.update(this);

    }

    public LocalDate getDateDepart() {
        return dateDepart;
    }

    public void setDateDepart(LocalDate dateDepart) throws SauvegardeImpossible {
        if (dateDepart.isBefore(dateArrivee)) {
            throw new IllegalArgumentException("Date de départ doit être supérieure ou égale à la date d'arrivée.");
        }
        this.dateDepart = dateDepart;
        gestionPersonnel.update(this);

    }
	
	/**
	 * Retourne vrai ssi l'employé est administrateur de la ligue 
	 * passée en paramètre.
	 * @return vrai ssi l'employé est administrateur de la ligue 
	 * passée en paramètre.
	 * @param ligue la ligue pour laquelle on souhaite vérifier si this 
	 * est l'admininstrateur.
	 */
	
	public boolean estAdmin(Ligue ligue)
	{
		return ligue.getAdministrateur() == this;
	}
	
	/**
	 * Retourne vrai ssi l'employé est le root.
	 * @return vrai ssi l'employé est le root.
	 */
	
	public boolean estRoot()
	{
		return gestionPersonnel.getRoot() == this;
	}
	
	/**
	 * Retourne le nom de l'employé.
	 * @return le nom de l'employé. 
	 */
	
	public String getNom()
	{
		return nom;
	}

	/**
	 * Change le nom de l'employé.
	 * @param nom le nouveau nom.
	 * @throws SauvegardeImpossible 
	 */
	
	public void setNom(String nom) throws SauvegardeImpossible
	{
		this.nom = nom;
	    gestionPersonnel.update(this);

	}

	/**
	 * Retourne le prénom de l'employé.
	 * @return le prénom de l'employé.
	 */
	
	public String getPrenom()
	{
		return prenom;
	}
	
	/**
	 * Change le prénom de l'employé.
	 * @param prenom le nouveau prénom de l'employé. 
	 * @throws SauvegardeImpossible 
	 */

	public void setPrenom(String prenom) throws SauvegardeImpossible
	{
		this.prenom = prenom;
	    gestionPersonnel.update(this);

	}

	/**
	 * Retourne le mail de l'employé.
	 * @return le mail de l'employé.
	 */
	
	public String getMail()
	{
		return mail;
	}
	
	/**
	 * Change le mail de l'employé.
	 * @param mail le nouveau mail de l'employé.
	 * @throws SauvegardeImpossible 
	 */

	public void setMail(String mail) throws SauvegardeImpossible
	{
		this.mail = mail;
	    gestionPersonnel.update(this);

	}

	/**
	 * Retourne vrai ssi le password passé en paramètre est bien celui
	 * de l'employé.
	 * @return vrai ssi le password passé en paramètre est bien celui
	 * de l'employé.
	 * @param password le password auquel comparer celui de l'employé.
	 */
	
	public boolean checkPassword(String password)
	{
		return this.password.equals(password);
	}

	/**
	 * Change le password de l'employé.
	 * @param password le nouveau password de l'employé. 
	 * @throws SauvegardeImpossible 
	 */
	
	public void setPassword(String password) throws SauvegardeImpossible
	{
		this.password= password;
	    gestionPersonnel.update(this);

	}

	/**
	 * Retourne la ligue à laquelle l'employé est affecté.
	 * @return la ligue à laquelle l'employé est affecté.
	 */
	
	public Ligue getLigue()
	{
		return ligue;
	}

	/**
	 * Supprime l'employé. Si celui-ci est un administrateur, le root
	 * récupère les droits d'administration sur sa ligue.
	 */
	
	public void remove()
	{
		Employe root = gestionPersonnel.getRoot();
		if (this != root)
		{
			if (estAdmin(getLigue()))
				try {
					getLigue().setAdministrateur(root);
				} catch (SauvegardeImpossible e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			getLigue().remove(this);
		}
		else
			throw new ImpossibleDeSupprimerRoot();
	}

	@Override
	public int compareTo(Employe autre)
	{
		int cmp = getNom().compareTo(autre.getNom());
		if (cmp != 0)
			return cmp;
		return getPrenom().compareTo(autre.getPrenom());
	}
	
	@Override
	public String toString()
	{
		String res = nom + " " + prenom + " " + mail + " (";
		if (estRoot())
			res += "super-utilisateur";
		else
			res += ligue.toString();
		return res + ")";
	}

	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setLigue(Ligue ligue) throws SauvegardeImpossible {
		
		this.ligue= ligue;
		gestionPersonnel.update(this);
		
	}

	public void setId(int employeId) throws SauvegardeImpossible {
		
		this.id= id;
	    gestionPersonnel.update(this);

	}
	public void remove1() throws SauvegardeImpossible {
	    gestionPersonnel.delete(this);
	}

}
