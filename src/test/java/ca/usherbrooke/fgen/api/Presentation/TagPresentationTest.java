package ca.usherbrooke.fgen.api.Presentation;


import ca.usherbrooke.fgen.api.Business.TagBusiness;
import ca.usherbrooke.fgen.api.Entities.Tag;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class TagPresentationTest {
    private TagBusiness tagBusiness;
    private TagPresentation tagPresentation;

    @BeforeEach
    void setUp() {
        tagBusiness = Mockito.mock(TagBusiness.class);
        tagPresentation = new TagPresentation(tagBusiness);
    }

    @Test
    void getAllTags_retourneStatus200AvecListeFilms() {
        // Arrange
        Tag m1 = new Tag();
        Tag m2 = new Tag();
        when(tagBusiness.getAllTags()).thenReturn(List.of(m1, m2));

        // Act
        Response response = tagPresentation.getAllTags();

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(List.of(m1, m2), response.getEntity());
        verify(tagBusiness, times(1)).getAllTags();
    }

    @Test
    void getAllTags_retourneStatus200AvecListeVide() {
        // Arrange
        when(tagBusiness.getAllTags()).thenReturn(List.of());

        // Act
        Response response = tagPresentation.getAllTags();

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(((List<?>) response.getEntity()).isEmpty());
        verify(tagBusiness, times(1)).getAllTags();
    }
}
