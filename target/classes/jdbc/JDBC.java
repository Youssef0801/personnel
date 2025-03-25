package jdbc;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import personnel.*;

public class JDBC implements Passerelle 
{
	Connection connection;

	public JDBC()
	{
		try
		{
			Class.forName(Credentials.getDriverClassName());
			connection = DriverManager.getConnection(Credentials.getUrl(), Credentials.getUser(), Credentials.getPassword());
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("Pilote JDBC non installé.");
		}
		catch (SQLException e)
		{
			System.out.println(e);
		}
	}
	
	@Override
	public GestionPersonnel getGestionPersonnel() 
	{
	    GestionPersonnel gestionPersonnel = new GestionPersonnel();
	    try 
	    {
	        // Charger les informations du root depuis la base de données
	        String requeteRoot = "SELECT id, nom, mdp FROM employe WHERE id = 1"; // Supposons que le root a toujours l'ID 1
	        Statement instructionRoot = connection.createStatement();
	        ResultSet rootResult = instructionRoot.executeQuery(requeteRoot);
	        if (rootResult.next()) 
	        {
	            int id = rootResult.getInt("id");
	            String nom = rootResult.getString("nom");
	            String password = rootResult.getString("mdp");
	            gestionPersonnel.addRootDB(id, nom, password); // Met à jour la variable root
	        }

	        // Charger les ligues depuis la base de données
	        String requeteLigues = "SELECT * FROM ligue";
	        Statement instructionLigues = connection.createStatement();
	        ResultSet ligues = instructionLigues.executeQuery(requeteLigues);
	        while (ligues.next())
	        {
	            int ligueId = ligues.getInt("id");
	            String nomLigue = ligues.getString("nom");
	            Ligue ligue = gestionPersonnel.addLigue(ligueId, nomLigue);

	            // Charger les employés de la ligue
	            String requeteEmployes = "SELECT * FROM employe WHERE id_ligue = ?";
	            PreparedStatement instructionEmployes = connection.prepareStatement(requeteEmployes);
	            instructionEmployes.setInt(1, ligueId);
	            ResultSet employes = instructionEmployes.executeQuery();
	            while (employes.next()) 
	            {
	                int employeId = employes.getInt("id");
	                String nomEmploye = employes.getString("nom");
	                String prenomEmploye = employes.getString("prenom");
	                String mail = employes.getString("mail");
	                String password = employes.getString("mdp");
	                LocalDate arrive = employes.getDate("arrive") != null ? employes.getDate("arrive").toLocalDate() : null;
	                LocalDate depart = employes.getDate("depart") != null ? employes.getDate("depart").toLocalDate() : null;

	                Employe employe = new Employe(gestionPersonnel, employeId, ligue, nomEmploye, prenomEmploye, mail, password, arrive, depart);
	                ligue.addEmploye(employe);
	            }

	            // Charger l'administrateur de la ligue
	            String requeteAdmin = "SELECT id_administrateur FROM ligue WHERE id = ?";
	            PreparedStatement instructionAdmin = connection.prepareStatement(requeteAdmin);
	            instructionAdmin.setInt(1, ligueId);
	            ResultSet adminResult = instructionAdmin.executeQuery();
	            if (adminResult.next()) 
	            {
	                int adminId = adminResult.getInt("id_administrateur");
	                Employe administrateur = ligue.getEmployes().stream()
	                    .filter(e -> e.getId() == adminId)
	                    .findFirst()
	                    .orElse(null);
	                if (administrateur != null) 
	                {
	                    ligue.setAdministrateur(administrateur);
	                }
	            }
	        }
	    }
	    catch (SQLException e)
	    {
	        System.out.println(e);
	    }
	    return gestionPersonnel;
	}

	@Override
	public void saveGestionPersonnel(GestionPersonnel gestionPersonnel) throws SauvegardeImpossible 
	{
		close();
	}
	
	public void close() throws SauvegardeImpossible
	{
		try
		{
			if (connection != null)
				connection.close();
		}
		catch (SQLException e)
		{
			throw new SauvegardeImpossible(e);
		}
	}
	
	@Override
	public int insert(Ligue ligue) throws SauvegardeImpossible 
	{
		try 
		{
			PreparedStatement instruction;
			instruction = connection.prepareStatement(
					"insert into ligue (nom) values(?)", Statement.RETURN_GENERATED_KEYS);
			instruction.setString(1, ligue.getNom());		
			instruction.executeUpdate();
			ResultSet id = instruction.getGeneratedKeys();
			id.next();
			return id.getInt(1);
		} 
		catch (SQLException exception) 
		{
			exception.printStackTrace();
			throw new SauvegardeImpossible(exception);
		}		
	}
	
	public int insert(Employe employe) throws SauvegardeImpossible {
	    try {
	        PreparedStatement instruction;
	        instruction = connection.prepareStatement(
	                "insert into employe (nom, prenom, mail, mdp, arrive, depart, id_ligue, id_niveau_acces ) VALUES (?, ?, ?, ?, ?, ?, ?, NULL )",
	                Statement.RETURN_GENERATED_KEYS);
	        instruction.setString(1, employe.getNom());
	        instruction.setString(2, employe.getPrenom());
	        instruction.setString(3, employe.getMail());
	        instruction.setString(4, employe.getPassword()); // Récupérer le mot de passe
	        instruction.setDate(5, employe.getDebutDate() != null ? java.sql.Date.valueOf(employe.getDebutDate()) : null); // Convertir LocalDate en Date SQL
	        instruction.setDate(6, employe.getEndDate() != null ? java.sql.Date.valueOf(employe.getEndDate()) : null); // Gérer les dates nulles
	        
	        if (employe.getLigue() != null) {
	            instruction.setInt(7, employe.getLigue().getId());
	        } else {
	            instruction.setNull(7, java.sql.Types.INTEGER);	        }
	        
	        instruction.executeUpdate();
	        ResultSet id = instruction.getGeneratedKeys();
	        id.next();
	        return id.getInt(1);
	    } catch (SQLException exception) {
	        exception.printStackTrace();
	        throw new SauvegardeImpossible(exception);
	    }
	}

	@Override
	public void update(Ligue ligue) throws SauvegardeImpossible {
	    try {
	        PreparedStatement instruction = connection.prepareStatement(
	            "UPDATE ligue SET nom = ? WHERE id = ?"
	        );
	        instruction.setString(1, ligue.getNom()); // Met à jour le nom de la ligue
	        instruction.setInt(2, ligue.getId());     // Identifie la ligue par son ID
	        instruction.executeUpdate();             // Exécute la requête de mise à jour
	    } catch (SQLException exception) {
	        exception.printStackTrace();
	        throw new SauvegardeImpossible(exception); // Lance une exception en cas d'erreur
	    }
	}

	@Override
	public void update(Employe employe) throws SauvegardeImpossible {
	    try {
	        PreparedStatement instruction = connection.prepareStatement(
	            "UPDATE employe SET nom = ?, prenom = ?, mail = ?, mdp = ?, arrive = ?, depart = ?, id_ligue = ? WHERE id = ?"
	        );
	        instruction.setString(1, employe.getNom()); // Update name
	        instruction.setString(2, employe.getPrenom()); // Update first name
	        instruction.setString(3, employe.getMail()); // Update email
	        instruction.setString(4, employe.getPassword()); // Update password
	        instruction.setDate(5, employe.getDebutDate() != null ? java.sql.Date.valueOf(employe.getDebutDate()) : null); // Update start date
	        instruction.setDate(6, employe.getEndDate() != null ? java.sql.Date.valueOf(employe.getEndDate()) : null); // Update end date
	        if (employe.getLigue() != null) {
	            instruction.setInt(7, employe.getLigue().getId()); // Update league ID
	        } else {
	            instruction.setNull(7, java.sql.Types.INTEGER); // Set league ID to null if not present
	        }
	        instruction.setInt(8, employe.getId()); // Identify the employee by ID
	        instruction.executeUpdate(); // Execute the update query
	    } catch (SQLException exception) {
	        exception.printStackTrace();
	        throw new SauvegardeImpossible(exception); // Throw exception if update fails
	    }
	}

	public void delete(Employe employe) {
	    try {
	        PreparedStatement instruction = connection.prepareStatement(
	            "DELETE FROM employe WHERE id = ?"
	        );
	        instruction.setInt(1, employe.getId());
	        instruction.executeUpdate();
	    } catch (SQLException exception) {
	        exception.printStackTrace();
		 }
	}

	@Override
	public void delete(Ligue ligue) throws SauvegardeImpossible {
		try {
			// Supprimer les employés de la ligue
			PreparedStatement deleteEmployes = connection.prepareStatement(
				"DELETE FROM employe WHERE id_ligue = ?"
			);
			deleteEmployes.setInt(1, ligue.getId());
			deleteEmployes.executeUpdate();

			// Supprimer la ligue
			PreparedStatement deleteLigue = connection.prepareStatement(
				"DELETE FROM ligue WHERE id = ?"
			);
			deleteLigue.setInt(1, ligue.getId());
			deleteLigue.executeUpdate();
		} catch (SQLException exception) {
			exception.printStackTrace();
			throw new SauvegardeImpossible(exception);
		}
	}
}
