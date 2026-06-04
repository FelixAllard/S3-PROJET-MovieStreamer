package ca.usherbrooke.fgen.api.Presentation;


import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


public class TagPresentationTest {
    @Test
    public void Test(){
        System.out.println("=================================");
        System.out.println(" TAG PRESENTATION TEST ");
        System.out.println("=================================");
    }

    @Test
    public void addTag_validRequest_returns201() { // Test de validité lors de l'insertion
        given()
                .contentType(ContentType.JSON)
                .body("{\"name\": \"Comédie\"}")
                .when()
                .post("/public/tag")
                .then()
                .statusCode(201)
                .body("name", equalTo("Comédie"))
                .body("id", notNullValue());
    }

    @Test
    public void addTag_emptyName_returns400() { // Test de refus car nom vide
        given()
                .contentType(ContentType.JSON)
                .body("{\"name\": \"\"}")
                .when()
                .post("/public/tag")
                .then()
                .statusCode(400);
    }
}
