package main.java.jdbc;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import main.java.personnel.*;

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
	public GestionPersonnel getGestionPersonnel() {
        GestionPersonnel gestionPersonnel = new GestionPersonnel();
        try (Connection connection = DriverManager.getConnection(
                Credentials.getUrl(), Credentials.getUser(), Credentials.getPassword());
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Ligue")) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nom = resultSet.getString("nom");
                gestionPersonnel.addLigue(id, nom);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gestionPersonnel;
    }

	@Override
	public void sauvegarderGestionPersonnel(GestionPersonnel gestionPersonnel) throws SauvegardeImpossible 
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
		public int insert(Ligue ligue) throws SauvegardeImpossible {
	    String sql = "INSERT INTO Ligue (nom) VALUES (?)";
	    try (Connection connection = DriverManager.getConnection(
	            Credentials.getUrl(), Credentials.getUser(), Credentials.getPassword());
	         PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

	        statement.setString(1, ligue.getNom());
	        statement.executeUpdate();
	        
	        ResultSet rs = statement.getGeneratedKeys();
	        if (rs.next()) {
	            return rs.getInt(1);
	        }
	    } catch (SQLException e) {
	        throw new SauvegardeImpossible(e);
	    }
	    return -1;
	}

	@Override
	public int insert(Employe employe) throws SauvegardeImpossible 
	{
		try 
		{
			PreparedStatement instruction;
			instruction = connection.prepareStatement("insert into employe (nom) values(?)", Statement.RETURN_GENERATED_KEYS);
			instruction.setString(1, employe.getNom());		
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
}
