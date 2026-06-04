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

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class TagBusinessTest {

    private TagData tagData;
    private TagBusiness tagBusiness;

    @BeforeEach
    void setUp() {
        System.out.println("=================================");
        System.out.println(" TAG BUISNESS TEST START ");
        System.out.println("=================================");

        tagData = Mockito.mock(TagData.class);
        tagBusiness = new TagBusiness(tagData);
    }

    @Test
    void getAllTags_delegueATagDataEtRetourneListe() {
        // Arrange
        Tag t1 = new Tag();
        t1.name = "Action";
        Tag t2 = new Tag();
        t2.name = "Horreur";

        when(tagData.getAllTags()).thenReturn(List.of(t1, t2));

        // Act
        List<Tag> result = tagBusiness.getAllTags();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Action", result.get(0).name);
        verify(tagData, times(1)).getAllTags();
    }
  
    @Test
    void getAllTags_retourneListeVideSiAucunTag() {
        // Arrange
        when(tagData.getAllTags()).thenReturn(List.of());

        // Act
        List<Tag> result = tagBusiness.getAllTags();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(tagData, times(1)).getAllTags();
    }
  
    @Test
    void deleteTagByTagId_delegueATagDataEtRetourneTrue() {
        when(tagData.deleteTagByTagId(1)).thenReturn(true);

        boolean result = tagBusiness.deleteTagByTagId(1);

        assertTrue(result);
        verify(tagData, times(1)).deleteTagByTagId(1);
    }

    @Test
    void deleteTagByTagId_retourneFalseSiTagInexistant() {
        when(tagData.deleteTagByTagId(99)).thenReturn(false);

        boolean result = tagBusiness.deleteTagByTagId(99);

        assertFalse(result);
        verify(tagData, times(1)).deleteTagByTagId(99);
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
