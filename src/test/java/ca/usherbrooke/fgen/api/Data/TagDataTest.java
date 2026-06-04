package ca.usherbrooke.fgen.api.Data;


import ca.usherbrooke.fgen.api.Entities.Tag;
import com.google.inject.Inject;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.openapi.internal.models.AbstractOpenAPI;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import ca.usherbrooke.fgen.api.Data.TagData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.wildfly.common.Assert.assertTrue;

public class TagDataTest {

    @Inject   // ← manquant — c'est ça qui crée la variable tagData
    TagData tagData;

    @Test
    public void Test(){
        System.out.println("=================================");
        System.out.println(" TAG DATA TEST START ");
        System.out.println("=================================");
    }

    @Test
    @Transactional // Vérifier si l'ajout d'un tag retourne un tag avec un ID et un nom valide
    public void addTag_success() {
        Tag tag = new Tag();
        tag.name = "Action";
        Tag result = tagData.postTag(tag);
        assertTrue(result.id > 0); // ID verification
        assertEquals("Action", result.name); // Name verification
    }

    @Test
    @Transactional
    public void addTag_nullName_shouldFail() { // Si le tag a un nom null, on retourne une erreur
        Tag tag = new Tag();
        tag.name = null;
        assertThrows(Exception.class, () -> tagData.postTag(tag));
    }
}
