package ca.usherbrooke.fgen.api.Presentation;

import ca.usherbrooke.fgen.api.Business.TagBusiness;
import ca.usherbrooke.fgen.api.Business.UserService;
import ca.usherbrooke.fgen.api.Entities.Tag;
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
    private UserService userService;

    @BeforeEach
    void setUp() {
        System.out.println("=================================");
        System.out.println(" TAG PRESENTATION TEST ");
        System.out.println("=================================");

        tagBusiness = Mockito.mock(TagBusiness.class);
        tagPresentation = new TagPresentation(tagBusiness, userService);
    }

    @Test
    void getAllTags_retourneStatus200AvecListeTags() {
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

    @Test
    void getTagByName_retourneStatus200AvecTag() {
        Tag tag = new Tag();
        tag.id = 1;
        tag.name = "Action";
        when(tagBusiness.getTagByName("Action")).thenReturn(tag);

        Response response = tagPresentation.getTagByName("Action");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(tag, response.getEntity());
        verify(tagBusiness, times(1)).getTagByName("Action");
    }
  
    @Test
    void deleteTagByTagId_retourneStatus204SiTagSupprime() {
        when(tagBusiness.deleteTagByTagId(1)).thenReturn(true);

        Response response = tagPresentation.deleteTagByTagId(1);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(tagBusiness, times(1)).deleteTagByTagId(1);
    }

    @Test
    void deleteTagByTagId_retourneStatus404SiTagInexistant() {
        when(tagBusiness.deleteTagByTagId(99)).thenReturn(false);

        Response response = tagPresentation.deleteTagByTagId(99);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(tagBusiness, times(1)).deleteTagByTagId(99);
    }

    @Test
    void updateTagByTagId_retourneStatus204SiTagUpdated()
    {
        //Arrange
        Tag existingTag = new Tag();
        existingTag.id = 1;
        existingTag.name = "Action";

        Tag newTag = new Tag();
        newTag.name = "Horreur";

        when(tagBusiness.updateTagByTagId(1, newTag)).thenReturn(newTag);

        // Act
        Response response = tagPresentation.updateTagByTagId(1, newTag);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(newTag, response.getEntity());
        verify(tagBusiness, times(1)).updateTagByTagId(1, newTag);
    }




    @Test
    void postTag_positiveTest() {
        Tag tag = new Tag();
        tag.id = 0;
        tag.name = "test";
        when(tagBusiness.postTag(tag)).thenReturn(tag);

        Response response = tagPresentation.postTag(tag);

        assertEquals(201, response.getStatus());
        verify(tagBusiness, times(1)).postTag(tag);
    }
}
