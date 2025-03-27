package testsUnitaires;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import personnel.*;

class testLigue 
{
    GestionPersonnel personnelManagement = GestionPersonnel.getGestionPersonnel();
    
    @Test
    void createLeague() throws SauvegardeImpossible
    {
        Ligue league = personnelManagement.addLeague("Darts");
        assertEquals("Darts", league.getName());
    }

    @Test
    void addEmploye() throws SauvegardeImpossible
    {
        Ligue league = personnelManagement.addLeague("Darts");
        Employe employe = league.addEmploye("Bouchard", "Gérard", "g.bouchard@gmail.com", "azerty", LocalDate.now(), null); 
        assertEquals(employe, league.getEmployes().first());
    }
    
    @Test 
    void incorrectArrival() throws SauvegardeImpossible, Exception
    {
        Ligue league = personnelManagement.addLeague("Race");
        Employe employe = league.addEmploye("Babski", "Florian", "f@gmail.com", "azerty", LocalDate.now(), LocalDate.of(2022, 10, 1));
        employe.setArrival(LocalDate.of(2023, 5, 23));
        assertEquals(employe, league.getEmployes().first());
    }
    
    @Test
    void incorrectDeparture() throws Exception, SauvegardeImpossible
    {
        Ligue league = personnelManagement.addLeague("Race");
        Employe employe = league.addEmploye("Grondin", "Lucas", "gl@gmail.com", "azerty", LocalDate.now(), null);
        employe.setDeparture(LocalDate.of(2023, 5, 23));
        assertEquals(employe, league.getEmployes().first());
    }
    
    @Test
    void getSetLeague() throws SauvegardeImpossible
    {
        Ligue league = personnelManagement.addLeague("Race");
        league.setName("Throw");
        assertEquals("Throw", league.getName());
    }
    
    @Test
    void getSetEmploye() throws SauvegardeImpossible
    {
        Ligue league = personnelManagement.addLeague("Darts");
        Employe employe = league.addEmploye("Bouchard", "Gérard", "g.bouchard@gmail.com", "azerty", LocalDate.now(), null);
        employe.setName("Bruno");
        employe.setFirstName("Brunié");
        employe.setEmail("b.brunod@gmail.com");
        employe.setPassword("12345");
        assertEquals("Bruno", employe.getName());
        assertEquals("Brunié", employe.getFirstName());
        assertEquals("b.brunod@gmail.com", employe.getEmail());
    }
    
    @Test 
    void deleteLeague() throws SauvegardeImpossible
    {
        Ligue league = personnelManagement.addLeague("Darts");
        league.remove();
        assertEquals(league, personnelManagement.getLeagues().first());
    }
    
    @Test 
    void deleteEmploye() throws SauvegardeImpossible
    {
        Ligue league = personnelManagement.addLeague("Darts");
        Employe employe = league.addEmploye("Bouchard", "Gérard", "g.bouchard@gmail.com", "azerty", LocalDate.now(), null);
        league.getEmployes().first().remove();
        assertEquals(employe, league.getEmployes().first());
    }
    
    @Test 
    void changeAdministrator() throws SauvegardeImpossible
    {
        Ligue league = personnelManagement.addLeague("Darts");
        Employe employe = league.addEmploye("Bouchard", "Gérard", "g.bouchard@gmail.com", "azerty", LocalDate.now(), null);
        league.setAdministrator(employe);
        assertEquals(true, league.getEmployes().first().isAdmin(league));
    }
    
    @Test 
    void deleteAdministrator() throws SauvegardeImpossible
    {
        Ligue league = personnelManagement.addLeague("Darts");
        Employe employe = league.addEmploye("Bouchard", "Gérard", "g.bouchard@gmail.com", "azerty", LocalDate.now(), null);
        league.setAdministrator(employe);
        employe.remove();
        assertEquals(employe, league.getEmployes().first());
    }
}
