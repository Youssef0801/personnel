package main.java.personnel;

import java.io.Serializable;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import java.time.LocalDate;
import main.java.personnel.GestionPersonnel;



import main.java.jdbc.JDBC;

import java.util.List;

import java.util.ArrayList;

/**
 * Gestion du personnel. Un seul objet de cette classe existe.
 * Il n'est pas possible d'instancier directement cette classe,
 * la méthode {@link #getGestionPersonnel getGestionPersonnel}
 * le fait automatiquement et retourne toujours le même objet.
 * Dans le cas où {@link #sauvegarder()} a été appelé lors
 * d'une exécution précédente, c'est l'objet sauvegardé qui est
 * retourné.
 */

public class GestionPersonnel implements Serializable {
	private static final long serialVersionUID = -105283113987886425L;
	private static GestionPersonnel gestionPersonnel = null;
	private SortedSet<Ligue> ligues;
	private Employe root = new Employe(this, null, -1, "root", "", "", "toor", null, null);
	private SortedSet<Employe> employes;
	public final static int SERIALIZATION = 1, JDBC = 2,
			TYPE_PASSERELLE = JDBC;
	private static Passerelle passerelle = new JDBC();

	/**
	 * Retourne l'unique instance de cette classe.
	 * Crée cet objet s'il n'existe déjà.
	 * 
	 * @return l'unique objet de type {@link GestionPersonnel}.
	 */

	public static GestionPersonnel getGestionPersonnel() {
		if (gestionPersonnel == null) {
			gestionPersonnel = passerelle.getGestionPersonnel();
			if (gestionPersonnel == null)
				gestionPersonnel = new GestionPersonnel();
		}
		return gestionPersonnel;
	}

	public GestionPersonnel() {
		if (gestionPersonnel != null)
			throw new RuntimeException("Vous ne pouvez créer qu'une seuls instance de cet objet.");
		ligues = new TreeSet<>();
		gestionPersonnel = this;
	}
	public int insertEmployeWithLigueName(Employe employe, String nomLigue) throws SauvegardeImpossible {
	    return passerelle.insertEmployeWithLigueName(employe, nomLigue);
	}

	public void sauvegarder() throws SauvegardeImpossible {
		passerelle.sauvegarderGestionPersonnel(this);
	}

	/**
	 * Retourne la ligue dont administrateur est l'administrateur,
	 * null s'il n'est pas un administrateur.
	 * 
	 * @param administrateur l'administrateur de la ligue recherchée.
	 * @return la ligue dont administrateur est l'administrateur.
	 */

	public Ligue getLigue(Employe administrateur) {
		if (administrateur.estAdmin(administrateur.getLigue()))
			return administrateur.getLigue();
		else
			return null;
	}

	/**
	 * Retourne toutes les ligues enregistrées.
	 * 
	 * @return toutes les ligues enregistrées.
	 */

	public SortedSet<Ligue> getLigues() {
		ligues.clear(); // Vider l'ancienne liste pour éviter les doublons
		GestionPersonnel gestionPersonnel = passerelle.getGestionPersonnel();
		ligues.addAll(gestionPersonnel.getLigues());
		return Collections.unmodifiableSortedSet(ligues);
	}

	public static void main(String[] args) {
		GestionPersonnel gestion = GestionPersonnel.getGestionPersonnel();

		// Ajouter une nouvelle ligue
		try {
			Ligue nouvelleLigue = gestion.addLigue("Nouvelle Ligue JDBC");
			gestion.insert(nouvelleLigue);
		} catch (SauvegardeImpossible e) {
			e.printStackTrace();
		}

		// Vérifier l'ajout en affichant toutes les ligues
		System.out.println(" Ligues après ajout :");
		for (Ligue ligue : gestion.getLigues()) {
			System.out.println("- " + ligue.getNom());
		}
	}

	public Ligue addLigue(String nom) throws SauvegardeImpossible {
		Ligue ligue = new Ligue(this, nom);
		ligues.add(ligue);
		return ligue;
	}

	public Ligue addLigue(int id, String nom) {
		Ligue ligue = new Ligue(this, id, nom);
		ligues.add(ligue);
		return ligue;
	}
	public void updateLigue(Ligue ligue) throws SauvegardeImpossible {
	    passerelle.update(ligue);
	}
	// Surcharge : Mise à jour en passant directement l’ID et le nom
	public void update(int id, String nom) throws SauvegardeImpossible {
	    Ligue ligue = getLigueById(id); // Ajoute une méthode pour retrouver une ligue par ID
	    if (ligue != null) {
	        ligue.setNom(nom);
	    }
	}
	private Ligue getLigueById(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	void remove(Ligue ligue) {
		ligues.remove(ligue);
	}

	int insert(Ligue ligue) throws SauvegardeImpossible {
		return passerelle.insert(ligue);
	}
	int insert(Employe employe) throws SauvegardeImpossible {
		return passerelle.insert(employe);
	}
	// Mise à jour avec un objet Employe
	public void update(Employe employe) throws SauvegardeImpossible {
	    passerelle.update(employe);
	}

	// Surcharge : Mise à jour en passant directement l’ID et les nouvelles valeurs
	public void update(int id, String nom, String prenom, String mail, String password, LocalDate dateArrivee, LocalDate dateDepart, String role, Ligue ligue) throws SauvegardeImpossible {
	    Employe employe = getEmployeById(id); // Assure-toi d’avoir une méthode pour récupérer un employé par ID
	    if (employe != null) {
	        employe.setNom(nom);
	        employe.setPrenom(prenom);
	        employe.setMail(mail);
	        employe.setPassword(password);
	        employe.setDateArrivée(dateArrivee);
	        employe.setDateDepart(dateDepart);
	        employe.setLigue(ligue);
	    }
	}

	private Employe getEmployeById(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	void update(Ligue ligue) throws SauvegardeImpossible
	{
	    passerelle.update(ligue);
	}


	/**
	 * Retourne le root (super-utilisateur).
	 * 
	 * @return le root.
	 */

	public Employe getRoot() {
		return root;
	}

	public Employe addRoot(String nom, String password) {
		Employe employe = new Employe(this, null, -1, nom, "", "", password, null, null);
		employes.add(employe);
		return employe;
	}

	public void addUser(String username, String password, String email, String firstName, String lastName,
			String league) {

		// Implementation for adding a user

	}

	public void addRoot(int rootId, String rootNom, String rootPrenom, String rootMail, String rootPassword,
			LocalDate rootDateArrivee, LocalDate rootDateDepart) {
		// TODO Auto-generated method stub
		
	}

}
