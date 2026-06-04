package ca.usherbrooke.fgen.api.business;

import ca.usherbrooke.fgen.api.Business.TagBusiness;
import ca.usherbrooke.fgen.api.Data.TagData;
import ca.usherbrooke.fgen.api.Entities.Tag;
import io.smallrye.openapi.runtime.util.ModelUtil;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


public class TagBusinessTest {
    @Test
    public void Test(){
        System.out.println("=================================");
        System.out.println(" TAG BUISNESS TEST START ");
        System.out.println("=================================");
    }

    @Test
    public void addTag_newTag_success() {
        TagData mockData = mock(TagData.class); // Objet de test (faux tag)
        TagBusiness business = new TagBusiness(mockData);

        Tag tag = new Tag();
        tag.name = "Thriller";

        // Simulation du tag qui n'existe pas
        when(mockData.existsByName("Thriller")).thenReturn(false);
        // Simulation d'une insertion réussite
        when(mockData.postTag(tag)).thenReturn(tag);

        Tag result = business.postTag(tag);
        assertEquals("Thriller", result.name);
        verify(mockData).postTag(tag); // Vérification du business test bel et bien appelé
    }

    @Test
    public void addTag_duplicate_shouldThrow409() {
        TagData mockData = mock(TagData.class);
        TagBusiness business = new TagBusiness(mockData);

        Tag tag = new Tag();
        tag.name = "Action";

        when(mockData.existsByName("Action")).thenReturn(true);
        WebApplicationException ex = assertThrows( // Pas trop catch
                WebApplicationException.class,
                () -> business.postTag(tag)
        );
        assertEquals(409, ex.getResponse().getStatus());
        verify(mockData, never()).postTag(any()); // Vérification d'une insertion bel et bien bloquée
    }
}
