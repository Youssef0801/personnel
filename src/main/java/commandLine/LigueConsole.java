package main.java.commandLine;

import static commandLineMenus.rendering.examples.util.InOut.getString;

import java.util.ArrayList;
import java.time.LocalDate;

import commandLineMenus.List;
import commandLineMenus.Menu;
import commandLineMenus.Option;

import main.java.personnel.*;

public class LigueConsole 
{
	private GestionPersonnel gestionPersonnel;
	private EmployeConsole employeConsole;

	public LigueConsole(GestionPersonnel gestionPersonnel, EmployeConsole employeConsole)
	{
		this.gestionPersonnel = gestionPersonnel;
		this.employeConsole = employeConsole;
	}

	Menu menuLigues()
	{
		Menu menu = new Menu("Gérer les ligues", "l");
		menu.add(afficherLigues());
		menu.add(ajouterLigue());
		menu.add(selectionnerLigue());
		menu.addBack("q");
		return menu;
	}

	private Option afficherLigues()
	{
		return new Option("Afficher les ligues", "l", () -> {System.out.println(gestionPersonnel.getLigues());});
	}

	private Option afficher(final Ligue ligue)
	{
		return new Option("Afficher la ligue", "l", 
				() -> 
				{
					System.out.println(ligue);
					System.out.println("administrée par " + ligue.getAdministrateur());
				}
		);
	}
	private Option afficherEmployes(final Ligue ligue)
	{
		return new Option("Afficher les employes", "l", () -> {System.out.println(ligue.getEmployes());});
	}

	private Option ajouterLigue()
	{
		return new Option("Ajouter une ligue", "a", () -> 
		{
			try
			{
				gestionPersonnel.addLigue(getString("nom : "));
			}
			catch(SauvegardeImpossible exception)
			{
				System.err.println("Impossible de sauvegarder cette ligue");
			}
		});
	}
	
	private Menu editerLigue(Ligue ligue)
	{
		Menu menu = new Menu("Editer " + ligue.getNom());
		menu.add(afficher(ligue));
		menu.add(gererEmployes(ligue));
		menu.add(changerAdministrateur(ligue));
		menu.add(changerNom(ligue));
		menu.add(supprimer(ligue));
		menu.addBack("q");
		return menu;
	}

	private Option changerNom(final Ligue ligue)
	{
		return new Option("Renommer", "r", 
				() -> {try {
					ligue.setNom(getString("Nouveau nom : "));
				} catch (SauvegardeImpossible e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}});
	}

	private List<Ligue> selectionnerLigue()
	{
		return new List<Ligue>("Sélectionner une ligue", "e", 
				() -> new ArrayList<>(gestionPersonnel.getLigues()),
				(element) -> editerLigue(element)
				);
	}
	
	private Option ajouterEmploye(final Ligue ligue)
	{
		return new Option("ajouter un employé", "a",
				() -> 
				{
					String nom = getString("nom : ");
					String prenom = getString("prenom : ");
					String mail = getString("mail : ");
					String password = getString("password : ");
					
					LocalDate dateDebut = null;
					LocalDate dateFin = null;

					// Validation de la date de début
					while (dateDebut == null) {
						try {
							String dateDebutStr = getString("date de début (yyyy-MM-dd) : ");
							dateDebut = LocalDate.parse(dateDebutStr);  // Essaye de parser la date
						} catch (Exception e) {
							System.out.println("Erreur : format de date invalide pour la date de début. Essayez encore (yyyy-MM-dd).");
						}
					}

					// Validation de la date de fin
					while (dateFin == null) {
						try {
							String dateFinStr = getString("date de fin (yyyy-MM-dd) : ");
							dateFin = LocalDate.parse(dateFinStr);  // Essaye de parser la date
						} catch (Exception e) {
							System.out.println("Erreur : format de date invalide pour la date de fin. Essayez encore (yyyy-MM-dd).");
						}
					}

					// Si la date de fin est avant la date de début, on l'ajuste
					if (dateFin.isBefore(dateDebut)) {
						System.out.println("La date de fin est avant la date de début... on va corriger ça !");
						dateFin = dateDebut.plusDays(1); // Fixela date de fin à un jour après la date de début
					}

					// Ajouter l'employé avec les dates validées
					try {
						ligue.addEmploye(nom, prenom, mail, password, dateDebut, dateFin);
					} catch (SauvegardeImpossible e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		);
	}


		
	private Menu gererEmployes(Ligue ligue)
	{
		Menu menu = new Menu("Gérer les employés de " + ligue.getNom(), "e");
		menu.add(afficherEmployes(ligue));
		menu.add(ajouterEmploye(ligue));
		menu.add(selectionnerEmploye(ligue));
		menu.addBack("q");
		return menu;
	}

	private List<Employe> selectionnerEmploye(final Ligue ligue)
	{
		return new List<>("Sélectionner un employé", "s", 
			() -> new ArrayList<>(ligue.getEmployes()),
			(employe) -> menuModifierOuSupprimerEmploye(employe)
		);
	}

	private Menu menuModifierOuSupprimerEmploye(Employe employe)
	{
		Menu menu = new Menu("Gérer " + employe.getNom(), "m");
		menu.add(modifierEmploye(employe));
		menu.add(supprimerEmploye(employe));
		menu.addBack("q");
		return menu;
	}

	private Option modifierEmploye(final Employe employe)
	{
		return new Option("Modifier", "m", () -> employeConsole.editerEmploye(employe));
	}

	private Option supprimerEmploye(final Employe employe)
	{
		return new Option("Supprimer", "d", () -> employe.remove());
	}
	
	private Option changerAdministrateur(final Ligue ligue)
	{
		return new Option("Changer l'administrateur", "a", 
			() -> {
				java.util.List<Employe> employesList = new ArrayList<>(ligue.getEmployes());
				Employe employe = employesList.get(Integer.parseInt(getString("Entrez l'index de l'employé à désigner administrateur : ")) - 1);
				try {
					ligue.setAdministrateur(employe);
				} catch (SauvegardeImpossible e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(employe.getNom() + " " + employe.getPrenom() + " est maintenant l'administrateur de la ligue " + ligue.getNom());
			});
	}


	private Option supprimer(Ligue ligue)
	{
		return new Option("Supprimer", "d", () -> {try {
			ligue.remove();
		} catch (SauvegardeImpossible e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}});
	}
	
}
