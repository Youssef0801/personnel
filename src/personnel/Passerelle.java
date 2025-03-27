package personnel;

public interface Passerelle 
{
    public GestionPersonnel getGestionPersonnel();
    public void saveGestionPersonnel(GestionPersonnel gestionPersonnel) throws SauvegardeImpossible;
    public int insert(Ligue ligue) throws SauvegardeImpossible;
    public int insert(Employe employe) throws SauvegardeImpossible;
    public void update(Ligue ligue) throws SauvegardeImpossible;
    public void update(Employe employe) throws SauvegardeImpossible; // New method
}
