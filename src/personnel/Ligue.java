package personnel;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Représente une ligue. Chaque ligue est reliée à une liste
 * d'employés dont un administrateur. Comme il n'est pas possible
 * de créer un employé sans l'affecter à une ligue, le root est 
 * l'administrateur de la ligue jusqu'à ce qu'un administrateur 
 * lui ait été affecté avec la fonction {@link #setAdministrateur}.
 */

public class Ligue implements Serializable, Comparable<Ligue>
{
	private static final long serialVersionUID = 1L;
	private int id = -1;
	private String nom;
	private SortedSet<Employe> employes;
	private Employe administrateur;
	private GestionPersonnel gestionPersonnel;
	
	/**
	 * Crée une ligue.
	 * @param nom le nom de la ligue.
	 */
	
	Ligue(GestionPersonnel gestionPersonnel, String nom) throws SauvegardeImpossible
	{
		this(gestionPersonnel, -1, nom);
		this.id = gestionPersonnel.insert(this); 
	}

	Ligue(GestionPersonnel gestionPersonnel, int id, String nom)
	{
		this.nom = nom;
		employes = new TreeSet<>();
		this.gestionPersonnel = gestionPersonnel;
		administrateur = gestionPersonnel.getRoot();
		this.id = id;
	}

	/**
	 * Retourne le nom de la ligue.
	 * @return le nom de la ligue.
	 */

	public String getNom()
	{
		return nom;
	}

	/**
	 * Change le nom.
	 * @param nom le nouveau nom de la ligue.
	 */
	public void setNom(String nom) {
	    this.nom = nom;
	    try {
	        gestionPersonnel.update(this); // Met à jour la ligue dans la base de données
	    } catch (SauvegardeImpossible e) {
	        e.printStackTrace(); // Gère l'exception en cas d'échec de la sauvegarde
	    }
	}

	/**
	 * Retourne l'administrateur de la ligue.
	 * @return l'administrateur de la ligue.
	 */
	
	public Employe getAdministrateur()
	{
		return administrateur;
	}

	public int getId()
	{
		return id;
	}
	/**
	 * Fait de administrateur l'administrateur de la ligue.
	 * Lève DroitsInsuffisants si l'administrateur n'est pas 
	 * un employé de la ligue ou le root. Révoque les droits de l'ancien 
	 * administrateur.
	 * @param administrateur le nouvel administrateur de la ligue.
	 */
	public void setAdministrateur(Employe administrateur) {
	    Employe root = gestionPersonnel.getRoot();
	    if (administrateur != root && administrateur.getLigue() != this) {
	        throw new DroitsInsuffisants();
	    }
	    this.administrateur = administrateur;
	    try {
	        gestionPersonnel.getPasserelle().update(this); // Met à jour la ligue dans la base de données
	    } catch (SauvegardeImpossible e) {
	        e.printStackTrace(); // Gère l'exception en cas d'échec de la sauvegarde
	    }
	}
	
	/*Enlever le droit d'administrateur si l'utilisateur
	 * est administrateur
	 * 
	 * 
	 * */
	public void deleteAdministrateur(Employe administrateur)
	{
		Employe root = gestionPersonnel.getRoot();
		if (administrateur != root && administrateur.getLigue().getAdministrateur() == administrateur)
		
		this.administrateur = null;
	}

	/**
	 * Retourne les employés de la ligue.
	 * @return les employés de la ligue dans l'ordre alphabétique.
	 */
	
	public SortedSet<Employe> getEmployes()
	{
		return Collections.unmodifiableSortedSet(employes);
	}

	/**
	 * Ajoute un employé dans la ligue. Cette méthode 
	 * est le seul moyen de créer un employé.
	 * @param nom le nom de l'employé.
	 * @param prenom le prénom de l'employé.
	 * @param mail l'adresse mail de l'employé.
	 * @param password le password de l'employé.
	 * @return l'employé créé. 
	 */

	public Employe addEmploye(String nom, String prenom, String mail, String password, LocalDate arrive, LocalDate depart) {
	    Employe employe = new Employe(this.gestionPersonnel, -1, this, nom, prenom, mail, password, arrive, depart);
	    try {
	        int id = gestionPersonnel.insertEmploye(employe); // Insère l'employé dans la base de données
	        employe.setId(id); // Met à jour l'ID de l'employé avec celui généré par la base de données
	    } catch (SauvegardeImpossible e) {
	        e.printStackTrace();
	    }
	    employes.add(employe);
	    return employe;
	}
	
	public void addEmploye(Employe employe) {
	    employes.add(employe);
	}
	
	void remove(Employe employe)
	{
		employes.remove(employe);
	}
	
	/**
	 * Supprime la ligue, entraîne la suppression de tous les employés
	 * de la ligue.
	 */
	public void remove() {
		try {
			// Supprime la ligue de la base de données
			gestionPersonnel.getPasserelle().delete(this);
			// Supprime la ligue de la liste des ligues
			gestionPersonnel.remove(this);
		} catch (SauvegardeImpossible e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public int compareTo(Ligue autre)
	{
		return getNom().compareTo(autre.getNom());
	}
	
	@Override
	public String toString()
	{
		return nom;
	}
}
