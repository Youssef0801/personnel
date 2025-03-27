package jdbc;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
            System.out.println("JDBC driver not installed.");
        }
        catch (SQLException e)
        {
            System.out.println(e);
        }
    }
    
    @Override
    public GestionPersonnel getGestionPersonnel() 
    {
        GestionPersonnel GestionPersonnel = new GestionPersonnel();
        try 
        {
            String query = "select * from ligue";
            Statement statement = connection.createStatement();
            ResultSet leagues = statement.executeQuery(query);
            while (leagues.next())
                GestionPersonnel.addLeague(leagues.getInt(1), leagues.getString(2));
        }
        catch (SQLException e)
        {
            System.out.println(e);
        }
        return GestionPersonnel;
    }

    @Override
    public void saveGestionPersonnel(GestionPersonnel GestionPersonnel) throws SauvegardeImpossible 
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
    public int insert(Ligue league) throws SauvegardeImpossible 
    {
        try 
        {
            PreparedStatement statement;
            statement = connection.prepareStatement(
                    "insert into ligue (nom) values(?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, league.getName());		
            statement.executeUpdate();
            ResultSet id = statement.getGeneratedKeys();
            id.next();
            return id.getInt(1);
        } 
        catch (SQLException exception) 
        {
            exception.printStackTrace();
            throw new SauvegardeImpossible(exception);
        }		
    }
    
    public int insert(Employe employee) throws SauvegardeImpossible {
        try {
            PreparedStatement statement;
            statement = connection.prepareStatement(
                    "insert into employe (nom, prenom, mail, mdp, arrive, depart, id_ligue, id_niveau_acces ) VALUES (?, ?, ?, ?, ?, ?, ?, NULL )",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, employee.getName());
            statement.setString(2, employee.getFirstName());
            statement.setString(3, employee.getEmail());
            statement.setString(4, employee.getPassword()); // Retrieve the password
            statement.setDate(5, employee.getArrival() != null ? java.sql.Date.valueOf(employee.getArrival()) : null); // Convert LocalDate to SQL Date
            statement.setDate(6, employee.getDeparture() != null ? java.sql.Date.valueOf(employee.getDeparture()) : null); // Handle null dates
            
            if (employee.getLeague() != null) {
                statement.setInt(7, employee.getLeague().getId());
            } else {
                statement.setNull(7, java.sql.Types.INTEGER);
            }
            
            statement.executeUpdate();
            ResultSet id = statement.getGeneratedKeys();
            id.next();
            return id.getInt(1);
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new SauvegardeImpossible(exception);
        }
    }

    @Override
    public void update(Ligue league) throws SauvegardeImpossible 
    {
        try 
        {
            PreparedStatement statement = connection.prepareStatement(
                "UPDATE ligue SET nom = ? WHERE id = ?");
            statement.setString(1, league.getName());
            statement.setInt(2, league.getId());
            statement.executeUpdate();
        } 
        catch (SQLException exception) 
        {
            exception.printStackTrace();
            throw new SauvegardeImpossible(exception);
        }
    }
}
