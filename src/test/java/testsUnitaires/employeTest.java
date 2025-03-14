package test.java.testsUnitaires;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.java.personnel.*;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

class employeTest {

    private Employe employe;

    @BeforeEach
    void setUp() {
        employe = new Employe(null, null, "Doe", "John", "john@example.com", "password", LocalDate.of(2022, 1, 1), LocalDate.of(2023, 1, 1));
    }

    @Test
    void testSetPrenom() {
        employe.setPrenom("Jane");
        assertEquals("Jane", employe.getPrenom());
    }

    @Test
    void testSetNom() {
        employe.setNom("Smith");
        assertEquals("Smith", employe.getNom());
    }

    @Test
    void testSetMail() {
        employe.setMail("jane@example.com");
        assertEquals("jane@example.com", employe.getMail());
    }

    @Test
    void testSetPassword() {
        employe.setPassword("newpassword");
        assertTrue(employe.checkPassword("newpassword"));
    }

    @Test
    void testSetDateArrivée() {
        LocalDate newDate = LocalDate.of(2023, 1, 1);
        employe.setDateArrivée(newDate);
        assertEquals(newDate, employe.getDateArrivée());
    }

    @Test
    void testSetDateDepart() {
        LocalDate newDate = LocalDate.of(2023, 1, 1);
        employe.setDateDepart(newDate);
        assertEquals(newDate, employe.getDateDepart());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            employe.setDateDepart(LocalDate.of(2021, 12, 31));
        });
        assertEquals("Date de départ doit être supérieure ou égale à la date d'arrivée.", exception.getMessage());
    }

    @Test
    void testRemoveEmploye() {
        GestionPersonnel gestionPersonnel = GestionPersonnel.getGestionPersonnel();
        Ligue ligue = createTestLigue(gestionPersonnel);
        Employe employe1 = ligue.addEmploye("Doe", "John", "john@example.com", "password", LocalDate.now(), null);
        Employe employe2 = ligue.addEmploye("Smith", "Jane", "jane@example.com", "password", LocalDate.now(), null);
        ligue.setAdministrateur(employe1);
        assertEquals(employe1, ligue.getAdministrateur());
        ligue.setAdministrateur(employe2);
        assertEquals(employe2, ligue.getAdministrateur());
    }

    @Test
    void testChangeAdministrateur() {
        GestionPersonnel gestionPersonnel = GestionPersonnel.getGestionPersonnel();
        Ligue ligue = createTestLigue(gestionPersonnel);
        Employe employe1 = ligue.addEmploye("Doe", "John", "john@example.com", "password", LocalDate.now(), null);
        Employe employe2 = ligue.addEmploye("Smith", "Jane", "jane@example.com", "password", LocalDate.now(), null);
        ligue.setAdministrateur(employe1);
        assertEquals(employe1, ligue.getAdministrateur());
        ligue.setAdministrateur(employe2);
        assertEquals(employe2, ligue.getAdministrateur());
    }

    private Ligue createTestLigue(GestionPersonnel gestionPersonnel) {
        try {
            return gestionPersonnel.addLigue("Test Ligue");
        } catch (SauvegardeImpossible e) {
            fail("Exception should not have been thrown");
            return null;
        }
    }
}