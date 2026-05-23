package ca.usherbrooke.fgen.api;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

public class ExampleTest {

    @Test
    void testEnvironmentStarted() {

        System.out.println("=================================");
        System.out.println(" QUARKUS TEST ENVIRONMENT START ");
        System.out.println("=================================");

        // Example assertions:
        // assertEquals(expected, actual);
        // assertTrue(condition);
        // assertNotNull(object);

        // Example REST test:
        //
        // given()
        //   .when().get("/hello")
        //   .then()
        //      .statusCode(200);

        // Example injection:
        //
        // @Inject
        // MyService service;

        System.out.println("Example test executed successfully.");
    }
}
