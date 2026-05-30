package ca.usherbrooke.fgen.api.Data;

import ca.usherbrooke.fgen.api.Entities.User;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class UserDataTest {


    @Inject
    UserData userData;

    @Test
    public void testGetAllUsers() {
        System.out.println("=================================");
        System.out.println(" TEST SANS MOCK : Vraie base de données ");
        System.out.println("=================================");


        List<User> resultat = userData.getAllUsers();
        assertNotNull(resultat, "La liste ne doit pas être nulle");


        assertEquals(5, resultat.size(), "Il devrait y avoir 5 utilisateurs dans la base de données");

        System.out.println(" TEST RÉUSSI ✅ ");
        System.out.println("=================================");
    }
}