package ca.usherbrooke.fgen.api.Presentation;

import ca.usherbrooke.fgen.api.Business.TagBusiness;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

public class TagPresentationTest {

    private TagBusiness tagBusiness;
    private TagPresentation tagPresentation;

    @BeforeEach
    void setUp() {
        System.out.println("=================================");
        System.out.println(" TAG PRESENTATION TEST ");
        System.out.println("=================================");

        tagBusiness = Mockito.mock(TagBusiness.class);
        tagPresentation = new TagPresentation(tagBusiness);
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
}
