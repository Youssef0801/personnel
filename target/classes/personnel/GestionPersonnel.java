package personnel;

import java.io.Serializable;
import java.sql.*;
import java.time.LocalDate;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Gestion du personnel. Un seul objet de cette classe existe.
 * Il n'est pas possible d'instancier directement cette classe, 
 * la méthode {@link #getGestionPersonnel getGestionPersonnel} 
 * le fait automatiquement et retourne toujours le même objet.
 * Dans le cas où {@link #sauvegarder()} a été appelé lors 
 * d'une exécution précédente, c'est l'objet sauvegardé qui est
 * retourné.
 */

public class GestionPersonnel implements Serializable
{
	private static final long serialVersionUID = -105283113987886425L;
	private static GestionPersonnel gestionPersonnel = null;
	private SortedSet<Ligue> ligues;
	private Employe root = addRoot("root","toor");
	public final static int SERIALIZATION = 1, JDBC = 2, 
			TYPE_PASSERELLE = JDBC;  
	private static Passerelle passerelle = TYPE_PASSERELLE == JDBC ? new jdbc.JDBC() : new serialisation.Serialization();	
	private Connection connection; // Assuming you have a connection object
	
	/**
	 * Retourne l'unique instance de cette classe.
	 * Crée cet objet s'il n'existe déjà.
	 * @return l'unique objet de type {@link GestionPersonnel}.
	 */
	public Employe addRoot(String nom, String password) {
	    try {
	        root = new Employe(this, 0, null, nom, "", "", password, null, null);
	        passerelle.insert(root);
	    } catch (SauvegardeImpossible e) {
	        e.printStackTrace();
	    }
	    return root;
	}
	
	public void addRootDB(int id, String nom, String password) {
	    root = new Employe(this, id, null, nom, "", "", password, null, null);
	}
	
	public GestionPersonnel getGestionPersonnel() {
	    GestionPersonnel gestionPersonnel = new GestionPersonnel();
	    try {
	        // Charger les informations du root depuis la base de données
	        String requeteRoot = "SELECT id, nom, mdp FROM employe WHERE id = 1"; // Supposons que le root a toujours l'ID 1
	        Statement instructionRoot = connection.createStatement();
	        ResultSet rootResult = instructionRoot.executeQuery(requeteRoot);
	        if (rootResult.next()) {
	            int id = rootResult.getInt("id");
	            String nom = rootResult.getString("nom");
	            String password = rootResult.getString("mdp");
	            gestionPersonnel.addRootDB(id, nom, password); // Met à jour la variable root
	        }

	        // Charger les ligues depuis la base de données
	        String requeteLigues = "SELECT * FROM ligue";
	        Statement instructionLigues = connection.createStatement();
	        ResultSet ligues = instructionLigues.executeQuery(requeteLigues);
	        while (ligues.next()) {
	            int ligueId = ligues.getInt("id");
	            String nomLigue = ligues.getString("nom");
	            Ligue ligue = gestionPersonnel.addLigue(ligueId, nomLigue);

	            // Charger les employés de la ligue
	            String requeteEmployes = "SELECT * FROM employe WHERE id_ligue = ?";
	            PreparedStatement instructionEmployes = connection.prepareStatement(requeteEmployes);
	            instructionEmployes.setInt(1, ligueId);
	            ResultSet employes = instructionEmployes.executeQuery();
	            while (employes.next()) {
	                int employeId = employes.getInt("id");
	                String nomEmploye = employes.getString("nom");
	                String prenomEmploye = employes.getString("prenom");
	                String mail = employes.getString("mail");
	                String password = employes.getString("mdp");
	                LocalDate arrive = employes.getDate("arrive") != null ? employes.getDate("arrive").toLocalDate() : null;
	                LocalDate depart = employes.getDate("depart") != null ? employes.getDate("depart").toLocalDate() : null;

	                ligue.addEmploye(nomEmploye, prenomEmploye, mail, password, arrive, depart);
	            }
	        }
	    } catch (SQLException e) {
	        System.out.println(e);
	    }
	    return gestionPersonnel;
	}

	public GestionPersonnel()
	{
		if (gestionPersonnel != null)
			throw new RuntimeException("Vous ne pouvez créer qu'une seuls instance de cet objet.");
		ligues = new TreeSet<>();
		gestionPersonnel = this;
	}
	
	public void sauvegarder() throws SauvegardeImpossible
	{
		passerelle.saveGestionPersonnel(this);
	}
	
	/**
	 * Retourne la ligue dont administrateur est l'administrateur,
	 * null s'il n'est pas un administrateur.
	 * @param administrateur l'administrateur de la ligue recherchée.
	 * @return la ligue dont administrateur est l'administrateur.
	 */
	
	public Ligue getLigue(Employe administrateur)
	{
		if (administrateur.estAdmin(administrateur.getLigue()))
			return administrateur.getLigue();
		else
			return null;
	}

	/**
	 * Retourne toutes les ligues enregistrées.
	 * @return toutes les ligues enregistrées.
	 */
	
	public SortedSet<Ligue> getLigues()
	{
		return Collections.unmodifiableSortedSet(ligues);
	}

	public Ligue addLigue(String nom) throws SauvegardeImpossible
	{
		Ligue ligue = new Ligue(this, nom); 
		ligues.add(ligue);
		return ligue;
	}
	
	public Ligue addLigue(int id, String nom)
	{
		Ligue ligue = new Ligue(this, id, nom);
		ligues.add(ligue);
		return ligue;
	}

	void remove(Ligue ligue)
	{
		ligues.remove(ligue);
	}
	
	int insert(Ligue ligue) throws SauvegardeImpossible
	{
		return passerelle.insert(ligue);
	}
	int insert(Employe employe) throws SauvegardeImpossible
	{
		return passerelle.insert(employe);
	}

	public void update(Ligue ligue) throws SauvegardeImpossible {
	    passerelle.update(ligue);
	}

	/**
	 * Retourne le root (super-utilisateur).
	 * @return le root.
	 */
	
	public Employe getRoot()
	{
		return root;
	}

	public int insertEmploye(Employe employe) throws SauvegardeImpossible {
	    return passerelle.insert(employe);
	}

	public void deleteEmploye(Employe employe) throws SauvegardeImpossible {
	    passerelle.delete(employe);
	}

	public Passerelle getPasserelle() {
		return passerelle;
	}
}
