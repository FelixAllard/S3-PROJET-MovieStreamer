package ca.usherbrooke.fgen.api.business;

import ca.usherbrooke.fgen.api.Business.UserBusiness;
import ca.usherbrooke.fgen.api.Entities.User;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class UserBusinessTest {

    @Inject
    UserBusiness userBusiness;

    @Test
    public void testGetAllUsers() {
        System.out.println("=================================");
        System.out.println(" TEST BUSINESS : Vraie base de données ");
        System.out.println("=================================");

        List<User> resultat = userBusiness.getAllUsers();

        assertNotNull(resultat, "La liste ne doit pas être nulle");
        assertEquals(5, resultat.size(), "Le chef cuisinier devrait renvoyer les 5 utilisateurs de import.sql");

        System.out.println(" TEST BUSINESS RÉUSSI ✅ ");
        System.out.println("=================================");
    }
}