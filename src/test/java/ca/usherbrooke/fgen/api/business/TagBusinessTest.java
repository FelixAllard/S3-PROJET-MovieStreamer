package ca.usherbrooke.fgen.api.business;

import ca.usherbrooke.fgen.api.Business.TagBusiness;
import ca.usherbrooke.fgen.api.Data.TagData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

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
}
