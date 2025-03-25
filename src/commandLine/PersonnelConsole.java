package commandLine;

import personnel.*;
import commandLineMenus.*;
import static commandLineMenus.rendering.examples.util.InOut.*;

public class PersonnelConsole
{
	private GestionPersonnel gestionPersonnel;
	LigueConsole ligueConsole;
	EmployeConsole employeConsole;
	
	public PersonnelConsole(GestionPersonnel gestionPersonnel)
	{
		this.gestionPersonnel = gestionPersonnel;
		this.employeConsole = new EmployeConsole();
		this.ligueConsole = new LigueConsole(gestionPersonnel, employeConsole);
	}
	
	public void start()
	{
		mainMenu().start();
	}
	
	private Menu mainMenu()
	{
		Menu menu = new Menu("Gestion du personnel des ligues");
		menu.add(employeConsole.editEmploye(gestionPersonnel.getRoot()));
		menu.add(ligueConsole.liguesMenu());
		menu.add(quitMenu());
		return menu;
	}

	private Menu quitMenu()
	{
		Menu menu = new Menu("Quitter", "q");
		menu.add(quitAndSave());
		menu.add(quitWithoutSaving());
		menu.addBack("r");
		return menu;
	}
	
	private Option quitAndSave()
	{
		return new Option("Quitter et enregistrer", "q", 
				() -> 
				{
					try
					{
						gestionPersonnel.sauvegarder();
						Action.QUIT.optionSelected();
					} 
					catch (SauvegardeImpossible e)
					{
						System.out.println("Impossible d'effectuer la sauvegarde");
					}
				}
			);
	}
	
	private Option quitWithoutSaving()
	{
		return new Option("Quitter sans enregistrer", "a", Action.QUIT);
	}
	
	private boolean verifyPassword()
	{
		boolean ok = gestionPersonnel.getRoot().checkPassword(getString("password : "));
		if (!ok)
			System.out.println("Password incorrect.");
		return ok;
	}
	
	public static void main(String[] args)
	{
		PersonnelConsole personnelConsole = 
				new PersonnelConsole(new GestionPersonnel().getGestionPersonnel());
		if (personnelConsole.verifyPassword())
			personnelConsole.start();
	}
}
