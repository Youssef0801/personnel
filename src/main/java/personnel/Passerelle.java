package main.java.personnel;

public interface Passerelle 
{
	public GestionPersonnel getGestionPersonnel();
	public void sauvegarderGestionPersonnel(GestionPersonnel gestionPersonnel)  throws SauvegardeImpossible;
	public int insert(Ligue ligue) throws SauvegardeImpossible;
    public void update(Ligue ligue) throws SauvegardeImpossible;
	public int insert(Employe employe) throws SauvegardeImpossible; 
	public void update(Employe employe) throws SauvegardeImpossible;
	public int insertEmployeWithLigueName(Employe employe, String nomLigue) throws SauvegardeImpossible;
	public void delete(Employe employe) throws SauvegardeImpossible;
	void deleteLigueAndMoveEmployes(Ligue ligueASupprimer, Ligue ligueDestination) throws SauvegardeImpossible;



}
