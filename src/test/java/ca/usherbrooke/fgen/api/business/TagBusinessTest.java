package ca.usherbrooke.fgen.api.business;

import ca.usherbrooke.fgen.api.Business.TagBusiness;
import ca.usherbrooke.fgen.api.Data.TagData;
import ca.usherbrooke.fgen.api.Entities.Tag;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.mockito.Mockito;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class TagBusinessTest {

    private TagData tagData;
    private TagBusiness tagBusiness;

    @BeforeEach
    void setUp() {
        System.out.println("=================================");
        System.out.println(" TAG BUSINESS TEST START ");
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
    void getTagByName_delegueATagDataEtRetourneTag() {
        Tag tag = new Tag();
        tag.name = "Action";
        when(tagData.getTagByName("Action")).thenReturn(tag);

        Tag result = tagBusiness.getTagByName("Action");

        assertNotNull(result);
        assertEquals("Action", result.name);
        verify(tagData, times(1)).getTagByName("Action");
    }

    @Test
    void getTagByName_nameNullOuVideLanceException() {
        assertThrows(WebApplicationException.class, () -> tagBusiness.getTagByName(null));
        assertThrows(WebApplicationException.class, () -> tagBusiness.getTagByName(""));
    }

    @Test
    void getTagByName_tagInexistantLanceException() {
        when(tagData.getTagByName("Inexistant")).thenReturn(null);

        assertThrows(WebApplicationException.class, () -> tagBusiness.getTagByName("Inexistant"));
        verify(tagData, times(1)).getTagByName("Inexistant");
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
    void updateTagByTagId_delegueATagDataEtRetourneLeTag() {
        // Arrange
        Tag tag = new Tag();
        tag.name = "Horreur";
        when(tagData.updateTagByTagId(1, tag)).thenReturn(tag);

        // Act
        Tag result = tagBusiness.updateTagByTagId(1, tag);

        // Assert
        assertNotNull(result);
        assertEquals(result.name,tag.name);
        verify(tagData, times(1)).updateTagByTagId(1, tag);
    }

    @Test
    void updateTagByTagId_negativeIdTest(){
        // Arrange
        Tag tag = new Tag();

        assertThrows(WebApplicationException.class, () -> {
            Tag result = tagBusiness.updateTagByTagId(-1, tag);
        });
    }
    @Test
    void updateTagByTagId_negativeNullName(){
        // Arrange
        Tag tag = new Tag();


        assertThrows(WebApplicationException.class, () -> {
            Tag result = tagBusiness.updateTagByTagId(0, tag);
        });

        tag.name = "";
        assertThrows(WebApplicationException.class, () -> {
            Tag result = tagBusiness.updateTagByTagId(0, tag);
        });

        tag.name = "weoqriuqwpeoiruqpwoeiruasdlkjfhasdlkjfhqoiurye3dkjfhlksd" +
                "jfhaslkdjfyhqoieuwryqowieuryqowiuyqowieruyqoeiuryqwioeruyqo" +
                "wirueyqoiweuryqowieuryqowieuryqoiweuryqoiweuryweoqriuqwpeoi" +
                "ruqpwoeiruasdlkjfhasdlkjfhqoiurye3dkjfhlksdjfhaslkdjfyhqoie" +
                "uwryqowieuryqowiuyqowieruyqoeiuryqwioeruyqowirueyqoiweuryqo" +
                "wieuryqowieuryqoiweuryqoiweuryweoqriuqwpeoiruqpwoeiruasdlkj" +
                "fhasdlkjfhqoiurye3dkjfhlksdjfhaslkdjfyhqoieuwryqowieuryqowi" +
                "uyqowieruyqoeiuryqwioeruyqowirueyqoiweuryqowieuryqowieuryqo";

        assertThrows(WebApplicationException.class, () -> {
            Tag result = tagBusiness.updateTagByTagId(0, tag);
        });
    }

    @Test
    void updateTagByTagId_negativeNullTag(){
        // Assert
        assertThrows(WebApplicationException.class, () -> {
            Tag result = tagBusiness.updateTagByTagId(0, null);
        });
    }


    @Test
    void postTag_positiveTest()
    {
        Tag tag = new Tag();
        tag.name = "Test";
        tag.id = 0;
        when(tagData.postTag(tag)).thenReturn(tag);

        Tag result = tagBusiness.postTag(tag);
        assertNotNull(result);
        assertEquals(tag.id, result.id);
        assertEquals(tag.name, result.name);
    }

    @Test
    void postTag_nameEmptyTest()
    {
        Tag tag = new Tag();
        tag.name = "";
        tag.id = 0;
        when(tagData.postTag(tag)).thenReturn(tag);

        assertThrows(WebApplicationException.class, () -> {
            Tag result = tagBusiness.postTag(tag);
        });
    }


//    @Test
//    void postTag_nameNullTest()
//    {
//        Tag tag = new Tag();
//        tag.name = null;
//        tag.id = 0;
//        when(tagData.postTag(tag)).thenReturn(tag);
//
//        assertThrows(WebApplicationException.class, () -> {
//            Tag result = tagBusiness.postTag(tag);
//        });
//    }
}
