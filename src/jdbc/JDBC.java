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
        GestionPersonnel gestionPersonnel = new GestionPersonnel();
        try 
        {
            String queryLigue = "SELECT * FROM ligue";
            Statement statementLigue = connection.createStatement();
            ResultSet leagues = statementLigue.executeQuery(queryLigue);
            
            while (leagues.next())
            {
                int ligueId = leagues.getInt("id");
                String ligueName = leagues.getString("nom");
                int adminId = leagues.getInt("id_administrateur"); // Récupérer l'ID de l'administrateur
                Ligue ligue = gestionPersonnel.addLeague(ligueId, ligueName);

                // Charger les employés de cette ligue
                String queryEmploye = "SELECT * FROM employe WHERE id_ligue = ?";
                PreparedStatement statementEmploye = connection.prepareStatement(queryEmploye);
                statementEmploye.setInt(1, ligueId);
                ResultSet employes = statementEmploye.executeQuery();

                while (employes.next())
                {
                    int employeId = employes.getInt("id");
                    String nom = employes.getString("nom");
                    String prenom = employes.getString("prenom");
                    String email = employes.getString("mail");
                    String password = employes.getString("mdp");
                    LocalDate arrivee = employes.getDate("arrive") != null ? employes.getDate("arrive").toLocalDate() : null;
                    LocalDate depart = employes.getDate("depart") != null ? employes.getDate("depart").toLocalDate() : null;

                    // Instancier un objet Employe
                    Employe employe = new Employe(gestionPersonnel, employeId, ligue, nom, prenom, email, password, arrivee, depart);

                    // Vérifier si cet employé est l'administrateur de la ligue
                    if (employeId == adminId) {
                        ligue.setAdministrator(employe); // Définir l'administrateur de la ligue
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
    
    @Override
    public int insert(Employe employee) throws SauvegardeImpossible {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO employe (nom, prenom, mail, mdp, arrive, depart, id_ligue) VALUES (?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, employee.getName());
            statement.setString(2, employee.getFirstName());
            statement.setString(3, employee.getEmail());
            statement.setString(4, employee.getPassword());
            statement.setDate(5, employee.getArrival() != null ? java.sql.Date.valueOf(employee.getArrival()) : null);
            statement.setDate(6, employee.getDeparture() != null ? java.sql.Date.valueOf(employee.getDeparture()) : null);
            if (employee.getLeague() != null) {
                statement.setInt(7, employee.getLeague().getId());
            } else {
                statement.setNull(7, java.sql.Types.INTEGER);
            }

            statement.executeUpdate();
            ResultSet id = statement.getGeneratedKeys();
            if (id.next()) {
                return id.getInt(1); // Retourne l'ID généré
            } else {
                throw new SauvegardeImpossible(new Exception("Failed to retrieve generated ID."));
            }
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

    @Override
    public void update(Employe employee) throws SauvegardeImpossible 
    {
        try 
        {
            PreparedStatement statement = connection.prepareStatement(
                "UPDATE employe SET nom = ?, prenom = ?, mail = ?, mdp = ?, arrive = ?, depart = ?, id_ligue = ? WHERE id = ?");
            statement.setString(1, employee.getName());
            statement.setString(2, employee.getFirstName());
            statement.setString(3, employee.getEmail());
            statement.setString(4, employee.getPassword());
            statement.setDate(5, employee.getArrival() != null ? java.sql.Date.valueOf(employee.getArrival()) : null);
            statement.setDate(6, employee.getDeparture() != null ? java.sql.Date.valueOf(employee.getDeparture()) : null);
            if (employee.getLeague() != null) {
                statement.setInt(7, employee.getLeague().getId());
            } else {
                statement.setNull(7, java.sql.Types.INTEGER);
            }
            statement.setInt(8, employee.getId());
            statement.executeUpdate();
        } 
        catch (SQLException exception) 
        {
            exception.printStackTrace();
            throw new SauvegardeImpossible(exception);
        }
    }

    @Override
    public void delete(Employe employe) throws SauvegardeImpossible 
    {
        try 
        {
            PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM employe WHERE id = ?");
            statement.setInt(1, employe.getId());
            statement.executeUpdate();
        } 
        catch (SQLException exception) 
        {
            exception.printStackTrace();
            throw new SauvegardeImpossible(exception);
        }
    }

    @Override
    public void delete(Ligue league) throws SauvegardeImpossible 
    {
        try 
        {
            // Supprimer les employés associés à la ligue
            PreparedStatement deleteEmployees = connection.prepareStatement(
                "DELETE FROM employe WHERE id_ligue = ?");
            deleteEmployees.setInt(1, league.getId());
            deleteEmployees.executeUpdate();

            // Supprimer la ligue
            PreparedStatement deleteLeague = connection.prepareStatement(
                "DELETE FROM ligue WHERE id = ?");
            deleteLeague.setInt(1, league.getId());
            deleteLeague.executeUpdate();
        } 
        catch (SQLException exception) 
        {
            exception.printStackTrace();
            throw new SauvegardeImpossible(exception);
        }
    }
}
