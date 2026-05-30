package ca.usherbrooke.fgen.api.Presentation;

import ca.usherbrooke.fgen.api.Entities.User;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class UserPresentationTest {

    @Inject
    UserPresentation userPresentation;

    @Test
    public void testGetAllUsers() {
        System.out.println("=================================");
        System.out.println(" TEST PRESENTATION : Vraie base de données ");
        System.out.println("=================================");

        Response reponse = userPresentation.getAllUsers();

        assertEquals(200, reponse.getStatus(), "Le statut HTTP devrait être 200 (OK)");

        List<User> resultat = (List<User>) reponse.getEntity();

        assertNotNull(resultat, "La liste contenue dans la réponse ne doit pas être nulle");
        assertEquals(5, resultat.size(), "Le serveur devrait servir une liste de 5 utilisateurs");

        System.out.println(" TEST PRESENTATION RÉUSSI ✅ ");
        System.out.println("=================================");
    }
}