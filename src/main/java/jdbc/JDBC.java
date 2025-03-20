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
	         ResultSet resultSet = statement.executeQuery("SELECT e.id AS employe_id, e.nom AS employe_nom, e.prenom, e.mail, e.password, e.dateArrivee, e.dateDepart, e.role, e.ligue_id, " +
	                 "l.id AS ligue_id, l.nom AS ligue_nom " +
	                 "FROM Employe e " +
	                 "JOIN Ligue l ON e.ligue_id = l.id")) {

	    	
	        while (resultSet.next()) {
	            int id = resultSet.getInt("id");
	            String nom = resultSet.getString("nom");
	            if (id <= 0) { // Vérifie que l'ID est valide
	    	        throw new SQLException("Erreur : Une ligue sans ID a été trouvée ");
	    	    }
	            gestionPersonnel.addLigue(id, nom); // Charge bien l’ID depuis la base

	            // Charger les employés de cette ligue
	            String requeteEmploye = "SELECT * FROM Employe WHERE ligue_id = ?";
	            try (PreparedStatement statementEmploye = connection.prepareStatement(requeteEmploye)) {
	                statementEmploye.setInt(1, id);
	                ResultSet employes = statementEmploye.executeQuery();

	                while (employes.next()) {
	                    int employeId = employes.getInt("id");
	                    String employeNom = employes.getString("nom");
	                    String employePrenom = employes.getString("prenom");
	                    String employeMail = employes.getString("mail");
	                    String employePassword = employes.getString("password");

	                    LocalDate dateArrivee = employes.getDate("dateArrivee") != null
	                            ? employes.getDate("dateArrivee").toLocalDate() : null;
	                    LocalDate dateDepart = employes.getDate("dateDepart") != null
	                            ? employes.getDate("dateDepart").toLocalDate() : null;

	                    String role = employes.getString("role");

	                    
	                    // Ajouter l'employé à la ligue
	                    Employe employe = null;
						Ligue ligue = null;
						try {
							employe = ligue.addEmploye(employeNom, employePrenom, employeMail, employePassword, dateArrivee, dateDepart);
						} catch (SauvegardeImpossible e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                    employe.setId(employeId);
	                    

	                    // Définir l'administrateur si c'est un admin
	                    if (role.equals("admin")) {
	                        ligue.setAdministrateur(employe);
	                    }
	                }
	            } catch (SauvegardeImpossible e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	        try {
	            String requeteLigues = "SELECT id, nom, administrateur FROM Ligue";
	            ResultSet resultSetLigues = statement.executeQuery(requeteLigues);

	            while (resultSetLigues.next()) {
	                int idLigue = resultSetLigues.getInt("id");
	                String nomLigue = resultSetLigues.getString("nom");
	                int idAdmin = resultSetLigues.getInt("administrateur");


	                Ligue ligue = gestionPersonnel.addLigue(idLigue, nomLigue);
	                
	                if (idAdmin > 0) {
	                    Employe admin = findEmployeById(idAdmin, gestionPersonnel);
	                    if (admin != null) {
	                        try {
								ligue.setAdministrateur(admin);
							} catch (SauvegardeImpossible e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	                    }
	                }

	                // Nouvelle requête pour récupérer les employés de cette ligue
	                String requeteEmployes = "SELECT * FROM Employe WHERE ligue_id = ?";
	                try (PreparedStatement statementEmployes = connection.prepareStatement(requeteEmployes)) {
	                    statementEmployes.setInt(1, idLigue);
	                    ResultSet resultSetEmployes = statementEmployes.executeQuery();

	                    while (resultSetEmployes.next()) {
	                        int idEmploye = resultSetEmployes.getInt("id");
	                        String nomEmploye = resultSetEmployes.getString("nom");
	                        String prenomEmploye = resultSetEmployes.getString("prenom");
	                        String mailEmploye = resultSetEmployes.getString("mail");
	                        String passwordEmploye = resultSetEmployes.getString("password");

	                        // Vérifier la gestion des dates pour éviter NullPointerException
	                        LocalDate dateArrivee = resultSetEmployes.getDate("dateArrivee") != null
	                                ? resultSetEmployes.getDate("dateArrivee").toLocalDate()
	                                : null;
	                        LocalDate dateDepart = resultSetEmployes.getDate("dateDepart") != null
	                                ? resultSetEmployes.getDate("dateDepart").toLocalDate()
	                                : null;
	                        
	                        // Instancier un objet Employe avec la surcharge du constructeur
	                        Employe employe = new Employe(gestionPersonnel, ligue, idEmploye, nomEmploye, prenomEmploye, mailEmploye, passwordEmploye, dateArrivee, dateDepart);

	                        // Ajouter l’employé à la ligue correspondante
	                        ligue.getEmployes().add(employe);
	                        }
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        // Charger le root
	        try (ResultSet rootLecturePoto = statement.executeQuery("SELECT * FROM Employe WHERE role = 'root'")) {
	            if (rootLecturePoto.next()) {
	                int rootId = rootLecturePoto.getInt("id");
	                String rootNom = rootLecturePoto.getString("nom");
	                String rootPrenom = rootLecturePoto.getString("prenom");
	                String rootMail = rootLecturePoto.getString("mail");
	                String rootPassword = rootLecturePoto.getString("password");

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

	private Employe findEmployeById(int idAdmin, GestionPersonnel gestionPersonnel) {
		// TODO Auto-generated method stub
		return null;
	}

	public int insertEmployeWithLigueName(Employe employe, String nomLigue) throws SauvegardeImpossible {
	    String sql = "INSERT INTO Employe (nom, prenom, mail, password, dateArrivee, dateDepart, ligue_id) " +
	                 "SELECT ?, ?, ?, ?, ?, ?, ligue.id " +
	                 "FROM Ligue ligue WHERE ligue.nom = ?";

	    try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	        statement.setString(1, employe.getNom());
	        statement.setString(2, employe.getPrenom());
	        statement.setString(3, employe.getMail());
	        statement.setString(4, employe.getPassword());
	        statement.setDate(5, employe.getDateArrivée() != null ? java.sql.Date.valueOf(employe.getDateArrivée()) : null);
	        statement.setDate(6, employe.getDateDepart() != null ? java.sql.Date.valueOf(employe.getDateDepart()) : null);
	        statement.setString(7, nomLigue); // Nom de la ligue pour la jointure

	        int affectedRows = statement.executeUpdate();
	        if (affectedRows == 0) {
	            throw new SauvegardeImpossible("L'insertion a échoué, aucune ligne modifiée (ligue non trouvée ).");
	        }

	        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	                int id = generatedKeys.getInt(1);
	                employe.setId(id);
	                return id;
	            } else {
	                throw new SauvegardeImpossible("L'insertion a réussi, mais aucun ID n'a été retourné.");
	            }
	        }
	    } catch (SQLException e) {
	        throw new SauvegardeImpossible(e);
	    }
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
            	int id = rs.getInt(1); // Récupère l’ID généré
                ligue.setId(id); // Affecte l’ID à l’objet
                return id;
            } else {
                throw new SauvegardeImpossible("Erreur : Impossible de récupérer l'ID de la ligue après insertion.");
            }
        } catch (SQLException e) {
            throw new SauvegardeImpossible(e);
        }
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
	  @Override
	  public void deleteLigueAndMoveEmployes(Ligue ligueASupprimer, Ligue ligueDestination) throws SauvegardeImpossible {
	      try {
	          connection.setAutoCommit(false); // Transaction SQL

	          // 1. Déplacer les employés vers une autre ligue
	          PreparedStatement updateEmployes = connection.prepareStatement(
	              "UPDATE Employe SET ligue_id = ? WHERE ligue_id = ?");
	          updateEmployes.setInt(1, ligueDestination.getId());
	          updateEmployes.setInt(2, ligueASupprimer.getId());
	          updateEmployes.executeUpdate();

	          // 2. Supprimer ensuite la ligue vide
	          PreparedStatement deleteLigue = connection.prepareStatement(
	              "DELETE FROM Ligue WHERE id = ?");
	          deleteLigue.setInt(1, ligueASupprimer.getId());
	          deleteLigue.executeUpdate();

	          connection.commit(); // Confirme les opérations

	      } catch (SQLException e) {
	          try {
	              connection.rollback(); // Annuler les opérations en cas d'erreur
	          } catch (SQLException ex) {
	              ex.printStackTrace();
	          }
	          throw new SauvegardeImpossible(e);
	      } finally {
	          try {
	              connection.setAutoCommit(true);
	          } catch (SQLException ex) {
	              ex.printStackTrace();
	          }
	      }
	  }

	  @Override
	  public void delete(Employe employe) throws SauvegardeImpossible {
	      String sql = "DELETE FROM Employe WHERE id = ?";
	      try (PreparedStatement statement = connection.prepareStatement(sql)) {
	          statement.setInt(1, employe.getId());

	          int rowsDeleted = statement.executeUpdate();
	          if (rowsDeleted == 0) {
	              throw new SauvegardeImpossible("Aucun employé trouvé avec l'ID : " + employe.getId());
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