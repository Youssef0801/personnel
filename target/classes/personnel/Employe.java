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
	private String nom, prenom, password, mail;
	private Ligue ligue;
	private int id = -1;
	private LocalDate arrive, depart;
	private GestionPersonnel gestionPersonnel;
	private InvalideDate arr;
	private InvalideDate dep;


	Employe(GestionPersonnel gestionPersonnel,Ligue ligue, String nom, String prenom, String mail, String password, LocalDate arrive, LocalDate depart) throws SauvegardeImpossible
	{
		this(gestionPersonnel, -1, ligue, nom, prenom, mail, password, arrive, depart);
		this.id = gestionPersonnel.insert(this);
	}
	
	public Employe(GestionPersonnel gestionPersonnel, int id, Ligue ligue, String nom, String prenom, String mail, String password, LocalDate arrive, LocalDate depart) {
		this.id = id;
		this.gestionPersonnel = gestionPersonnel;
		this.nom = nom;
		this.prenom = prenom;
		this.password = password;
		this.mail = mail;
		this.arrive = arrive;
		this.depart = depart;
		this.ligue = ligue;
	}

	// Nouveau constructeur pour les données lues depuis MySQL
	Employe(GestionPersonnel gestionPersonnel, int id, int ligueId, String nom, String prenom, String mail, String password, LocalDate arrive, LocalDate depart) {
		this.id = id;
		this.gestionPersonnel = gestionPersonnel;
		this.nom = nom;
		this.prenom = prenom;
		this.password = password;
		this.mail = mail;
		this.arrive = arrive;
		this.depart = depart;
		this.ligue = gestionPersonnel.getLigues().stream()
			.filter(ligue -> ligue.getId() == ligueId)
			.findFirst()
			.orElse(null); // Associe la ligue correspondante à l'ID
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
	 * Retourne l'id de l'employé.
	 * @return l'id de l'employé. 
	 */
	
	public int getId()
	{
		return id;
	}

	/**
	* modifie l'id de l'employé.
	 * @return l'id de l'employé.
	 */
	
	public void setId(int id)
	{
		this.id = id;
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
	 */
	
	public void setNom(String nom)
	{
		this.nom = nom;
		try {
			gestionPersonnel.getPasserelle().update(this); // Met à jour l'employé dans la base de données
		} catch (SauvegardeImpossible e) {
			e.printStackTrace(); // Gère l'exception en cas d'échec de la sauvegarde
		}
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
	 */

	public void setPrenom(String prenom)
	{
		this.prenom = prenom;
		try {
			gestionPersonnel.getPasserelle().update(this);
		} catch (SauvegardeImpossible e) {
			e.printStackTrace();
		}
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
	 */

	public void setMail(String mail)
	{
		this.mail = mail;
		try {
			gestionPersonnel.getPasserelle().update(this);
		} catch (SauvegardeImpossible e) {
			e.printStackTrace();
		}
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
	 */
	
	public void setPassword(String password)
	{
		this.password= password;
		try {
			gestionPersonnel.getPasserelle().update(this);
		} catch (SauvegardeImpossible e) {
			e.printStackTrace();
		}
	}
	
	public String getPassword()
	{
		return password;
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
			{
				getLigue().setAdministrateur(root);
			}
			getLigue().remove(this);
			try {
				gestionPersonnel.deleteEmploye(this); // Supprime l'employé de la base de données
			} catch (SauvegardeImpossible e) {
				e.printStackTrace();
			}
		}
		else
			throw new ImpossibleDeSupprimerRoot();
	}
	
	/*
	 * Retourne la date d'arrivée de employé.
	 * @return la date d'arrivée de employé.
	 * 
	 * */
	
	public LocalDate getDebutDate()
	{
		return arrive;
	}
	
	/**
	 * Change la date d'arrivée de l'employé.
	 * @param la date d'arrivée de l'employé. 
	 */
	
	public void setDebutDate(LocalDate arrive) throws InvalideDate {
		try {
			if(this.depart != null) {
				if(this.depart.isAfter(arrive)) {
					this.arrive = arrive;
				}else {
					throw new InvalideDate(arr);
				}
			}else {
				this.arrive = arrive;
				gestionPersonnel.getPasserelle().update(this); // Met à jour la date d'arrivée dans la base de données
			}
		}catch(InvalideDate arr) {
			throw new InvalideDate(arr);
		} catch (SauvegardeImpossible e) {
			e.printStackTrace();
		}
		
		
	}
	
	/*
	 * Retourne la date de départ de employé.
	 * @return la date de départ de employé.
	 * 
	 * */
	
	public LocalDate getEndDate()
	{
		return depart;
	}
	
	/**
	 * Change la date départ de l'employé.
	 * @param la date départ de l'employé. 
	 */
	
	public void setEndDate(LocalDate depart) throws InvalideDate{
		try {
				if(this.arrive.isBefore(depart)) {
					this.depart = depart;
				}else {
					throw new InvalideDate(dep);
				}
				gestionPersonnel.getPasserelle().update(this); // Met à jour la date de départ dans la base de données
		}catch(InvalideDate dep) {
			throw new InvalideDate(dep);
		} catch (SauvegardeImpossible e) {
			e.printStackTrace();
		}
			
		
		
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
		String res = nom + " " + prenom + " " + mail + " "+ arrive;
		if(depart != null) {
			res += " " + depart;
		}
		res += " (";
		if (estRoot())
			res += "super-utilisateur";
		else if (estAdmin(ligue))
			res += "Administrateur";
		else
			res += ligue.toString();
		return res + ")";
	}

}