package ca.usherbrooke.fgen.api.business;

import ca.usherbrooke.fgen.api.Business.TagBusiness;
import ca.usherbrooke.fgen.api.Data.TagData;
import ca.usherbrooke.fgen.api.Entities.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TagBusinessTest {
    private TagData tagData;
    private TagBusiness tagBusiness;

    @BeforeEach
    void setUp() {
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
}
