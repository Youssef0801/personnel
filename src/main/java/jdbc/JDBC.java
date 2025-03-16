package main.java.jdbc;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import main.java.personnel.*;

public class JDBC implements Passerelle {
	private Connection connection;

	public JDBC() {
		try {
			Class.forName(Credentials.getDriverClassName());
			this.connection = DriverManager.getConnection(Credentials.getUrl(), Credentials.getUser(), Credentials.getPassword());
		} catch (ClassNotFoundException e) {
			System.out.println("Pilote JDBC non installé.");
		} catch (SQLException e) {
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

			// Lire les informations du root depuis la base de données
			try (ResultSet rootLecturePoto = statement.executeQuery("SELECT * FROM Employe WHERE role = 'root'")) {
				if (rootLecturePoto.next()) {
					int rootId = rootLecturePoto.getInt("id");
					String rootNom = rootLecturePoto.getString("nom");
					String rootPrenom = rootLecturePoto.getString("prenom");
					String rootMail = rootLecturePoto.getString("mail");
					String rootPassword = rootLecturePoto.getString("password");

					// Vérification pour éviter NullPointerException sur toLocalDate()
					LocalDate rootDateArrivee = rootLecturePoto.getDate("dateArrivee") != null 
							? rootLecturePoto.getDate("dateArrivee").toLocalDate() : null;
					LocalDate rootDateDepart = rootLecturePoto.getDate("dateDepart") != null 
							? rootLecturePoto.getDate("dateDepart").toLocalDate() : null;

					gestionPersonnel.addRoot(rootId, rootNom, rootPrenom, rootMail, rootPassword, rootDateArrivee, rootDateDepart);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return gestionPersonnel;
	}

	@Override
	public void sauvegarderGestionPersonnel(GestionPersonnel gestionPersonnel) throws SauvegardeImpossible {
		close();
	}

	
	@Override
    public int insert(Ligue ligue) throws SauvegardeImpossible {
        String sql = "INSERT INTO Ligue (nom) VALUES (?)";
        try (PreparedStatement statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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
	  public int insert(Employe employe) {
	        String sql = "INSERT INTO Employe (nom) VALUES (?)";
	        try (PreparedStatement instruction = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	            instruction.setString(1, employe.getNom());
	            instruction.executeUpdate();
	            ResultSet id = instruction.getGeneratedKeys();
	            if (id.next()) {
	                return id.getInt(1);
	            }
	        } catch (SQLException exception) {
	            exception.printStackTrace();
	        }
	        return -1;
	    }
	
	  

    @Override
    public void update(Ligue ligue) throws SauvegardeImpossible {
        if (ligue.getId() == 0) {
            throw new SauvegardeImpossible("❌ Impossible de mettre à jour une ligue sans ID.");
        }

        String sql = "UPDATE Ligue SET nom = ? WHERE id = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {

            statement.setString(1, ligue.getNom());
            statement.setInt(2, ligue.getId());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SauvegardeImpossible("❌ Aucune ligue trouvée avec cet ID.");
            }

        } catch (SQLException e) {
            throw new SauvegardeImpossible(e);
        }
    }
	  public void update(Employe employe) throws SauvegardeImpossible {
	      if (employe.getId() == 0) {
	          throw new SauvegardeImpossible("❌ Impossible de mettre à jour un employé sans ID.");
	      }

	      String sql = "UPDATE Employe SET nom = ?, prenom = ?, mail = ?, password = ?, dateArrivee = ?, dateDepart = ?, role = ?, ligue_id = ? WHERE id = ?";
	      
	      try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
	          statement.setString(1, employe.getNom());
	          statement.setString(2, employe.getPrenom());
	          statement.setString(3, employe.getMail());
	          statement.setString(4, employe.getPassword());

	          // Correction pour éviter une erreur si la date est null
	          if (employe.getDateArrivée() != null) {
	              statement.setDate(5, java.sql.Date.valueOf(employe.getDateArrivée()));
	          } else {
	              statement.setNull(5, java.sql.Types.DATE);
	          }

	          if (employe.getDateDepart() != null) {
	              statement.setDate(6, java.sql.Date.valueOf(employe.getDateDepart()));
	          } else {
	              statement.setNull(6, java.sql.Types.DATE);
	          }

	          // Vérification pour éviter une NullPointerException si la ligue est null
	          if (employe.getLigue() != null) {
	              statement.setInt(8, employe.getLigue().getId());
	          } else {
	              statement.setNull(8, java.sql.Types.INTEGER);
	          }

	          statement.setInt(9, employe.getId());

	          int rowsUpdated = statement.executeUpdate();
	          if (rowsUpdated == 0) {
	              throw new SauvegardeImpossible("❌ Aucune mise à jour effectuée, employé non trouvé.");
	          }

	      } catch (SQLException e) {
	          throw new SauvegardeImpossible(e);
	      }
	  }
  
    public void close() {
        try {
            if (this.connection != null) {
                this.connection.close();
                System.out.println("✅ Connexion MySQL fermée.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la fermeture de la connexion : " + e.getMessage());
        }
    }

	
}