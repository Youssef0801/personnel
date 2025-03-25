package commandLine;

import static commandLineMenus.rendering.examples.util.InOut.getString;

import java.time.LocalDate;
import java.util.ArrayList;

import commandLineMenus.List;
import commandLineMenus.Menu;
import commandLineMenus.Option;

import personnel.*;

public class LigueConsole 
{
	private GestionPersonnel gestionPersonnel;
	private EmployeConsole employeConsole;

	public LigueConsole(GestionPersonnel gestionPersonnel, EmployeConsole employeConsole)
	{
		this.gestionPersonnel = gestionPersonnel;
		this.employeConsole = employeConsole;
	}

	Menu liguesMenu()
	{
		Menu menu = new Menu("Gérer les ligues", "l");
		menu.add(showLigues());
		menu.add(addLigue());
		menu.add(selectionnerLigue());
		menu.addBack("q");
		return menu;
	}

	private Option showLigues()
	{
		return new Option("Afficher les ligues", "l", () -> {System.out.println(gestionPersonnel.getLigues());});
	}

	private Option show(final Ligue ligue)
	{
		return new Option("Afficher la ligue", "l", 
				() -> 
				{
					System.out.println(ligue);
					System.out.println("administrée par " + ligue.getAdministrateur());
				}
		);
	}
	private Option showEmployes(final Ligue ligue)
	{
		return new Option("Afficher les employes", "l", () -> {System.out.println(ligue.getEmployes());});
	}

	private Option addLigue()
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
	
	private Menu editLigue(Ligue ligue)
	{
		Menu menu = new Menu("Editer " + ligue.getNom());
		menu.add(show(ligue));
		menu.add(manageEmployes(ligue));
		//menu.add(changerAdministrateur(ligue));
		menu.add(changeName(ligue));
		menu.add(delete(ligue));
		menu.addBack("q");
		return menu;
	}

	private Option changeName(final Ligue ligue)
	{
		return new Option("Renommer", "r", 
				() -> {ligue.setNom(getString("Nouveau nom : "));});
	}

	private List<Ligue> selectionnerLigue()
	{
		return new List<Ligue>("Sélectionner une ligue", "e", 
				() -> new ArrayList<>(gestionPersonnel.getLigues()),
				(element) -> editLigue(element)
				);
	}
	private Option addEmploye(final Ligue ligue)
	{
		return new Option("Ajouter un employé", "a", () -> {
	        try {
	            ligue.addEmploye(
	                getString("Nom : "),
	                getString("Prénom : "),
	                getString("Mail : "),
	                getString("Mot de passe : "),
	                LocalDate.of(
	                    Integer.parseInt(getString("Année d'arrivée : ")),
	                    Integer.parseInt(getString("Mois d'arrivée : ")),
	                    Integer.parseInt(getString("Jour d'arrivée : "))
	                ),
	                null // Date de départ facultative
	            );
	            System.out.println("Employé ajouté avec succès !");
	        } catch (Exception e) {
	            System.err.println("Erreur lors de l'ajout de l'employé : " + e.getMessage());
	        }
	    });
	}
	

	private Menu manageEmployes(Ligue ligue)
	{
		Menu menu = new Menu("Gérer les employés de " + ligue.getNom(), "m");
		menu.add(showEmployes(ligue));
		menu.add(new List<>("Selectionner un employé", "s",
				() -> new ArrayList<>(ligue.getEmployes()),
				employeConsole.editEmploye()
				));
		menu.add(addEmploye(ligue));
		menu.addBack("q");
		return menu;
	}


	private List<Employe> deleteEmploye(final Ligue ligue)
	{
		return new List<>("Supprimer un employé", "s", 
				() -> new ArrayList<>(ligue.getEmployes()),
				(index, element) -> {element.remove();}
				);
	}
	
	private List<Employe> changeAdministrateur(final Ligue ligue)
	{
		return null;
	}		

	private List<Employe> editEmploye(final Ligue ligue)
	{
		return new List<>("Modifier un employé", "e", 
				() -> new ArrayList<>(ligue.getEmployes()),
				employeConsole.editEmploye()
				);
	}
	
	private Option delete(Ligue ligue) {
		return new Option("Supprimer", "d", () -> {
			ligue.remove();
			System.out.println("Ligue supprimée avec succès !");
		});
	}
	
}
