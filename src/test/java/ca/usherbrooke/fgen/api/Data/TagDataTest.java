package ca.usherbrooke.fgen.api.Data;


import ca.usherbrooke.fgen.api.Entities.Tag;
import jakarta.ws.rs.WebApplicationException;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import ca.usherbrooke.fgen.api.DAO.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TagDataTest {
    private TagRepository tagRepository;
    private TagData tagData;

    @BeforeEach
    void setUp() {
        tagRepository = Mockito.mock(TagRepository.class);
        tagData = new TagData(tagRepository);
    }

    @Test
    void getAllTags_delegueAuRepositoryEtRetourneListe() {
        // Arrange
        Tag m1 = new Tag();
        Tag m2 = new Tag();
        when(tagRepository.listAll()).thenReturn(List.of(m1, m2));

        // Act
        List<Tag> result = tagData.getAllTags();

        // Assert
        assertEquals(2, result.size());
        verify(tagRepository, times(1)).listAll();
    }

    @Test
    void getAllTags_retourneListeVideSiRepositoryVide() {
        // Arrange
        when(tagRepository.listAll()).thenReturn(List.of());

        // Act
        List<Tag> result = tagData.getAllTags();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(tagRepository, times(1)).listAll();
    }

    @Test
    void getTagByName_delegueAuRepositoryEtRetourneTag() {
        Tag tag = new Tag();
        tag.id = 1;
        tag.name = "Action";
        @SuppressWarnings("unchecked")
        PanacheQuery<Tag> query = Mockito.mock(PanacheQuery.class);
        when(tagRepository.find("name", "Action")).thenReturn(query);
        when(query.firstResult()).thenReturn(tag);

        Tag result = tagData.getTagByName("Action");

        assertNotNull(result);
        assertEquals("Action", result.name);
        verify(tagRepository, times(1)).find("name", "Action");
        verify(query, times(1)).firstResult();
    }

    @Test
    void getTagByName_retourneNullSiTagAbsent() {
        @SuppressWarnings("unchecked")
        PanacheQuery<Tag> query = Mockito.mock(PanacheQuery.class);
        when(tagRepository.find("name", "Inexistant")).thenReturn(query);
        when(query.firstResult()).thenReturn(null);

        Tag result = tagData.getTagByName("Inexistant");

        assertNull(result);
        verify(tagRepository, times(1)).find("name", "Inexistant");
        verify(query, times(1)).firstResult();
    }

    @Test
    void deleteTagByTagId_delegueAuRepositoryEtRetourneTrue() {
        when(tagRepository.deleteById(1)).thenReturn(true);

        boolean result = tagData.deleteTagByTagId(1);

        assertTrue(result);
        verify(tagRepository, times(1)).deleteMovieTagLinksByTagId(1);
        verify(tagRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteTagByTagId_retourneFalseSiRepositoryNeSupprimeRien() {
        when(tagRepository.deleteById(99)).thenReturn(false);

        boolean result = tagData.deleteTagByTagId(99);

        assertFalse(result);
        verify(tagRepository, times(1)).deleteMovieTagLinksByTagId(99);
        verify(tagRepository, times(1)).deleteById(99);
    }

    @Test
    void updateTagByTagId_modifieLeNomEtRetourneLeTag() {
        // Arrange
        Tag existingTag = new Tag();
        existingTag.id = 1;
        existingTag.name = "Action";

        Tag updatedTag = new Tag();
        updatedTag.name = "Horreur";

        when(tagRepository.findById(1)).thenReturn(existingTag);

        // Act
        Tag result = tagData.updateTagByTagId(1, updatedTag);

        // Assert
        assertEquals("Horreur", result.name);
        verify(tagRepository, times(1)).findById(1);
    }

    @Test
    void updateTagByTagId_retourneNullSiTagInexistant() {
        //Arrange
        when(tagRepository.findById(0)).thenReturn(null);

        // Assert
        assertThrows(WebApplicationException.class, () -> {
            Tag result = tagData.updateTagByTagId(0, new Tag());
        });

    }

    @Test
    void postTag_testPositive()
    {
        Tag m1 = new Tag();
        m1.id = 0;
        m1.name = "test";
        doNothing().when(tagRepository).persist(m1);
        when(tagRepository.count("name", m1.name)).thenReturn(0l);

        Tag result = tagData.postTag(m1);

        assertNotNull(result);
        verify(tagRepository, times(1)).persist(m1);

        assertEquals(m1.id, result.id);
        assertEquals(m1.name, m1.name);
    }

    @Test
    void postTag_testNameAlreadyExists()
    {
        Tag m1 = new Tag();
        m1.id = 0;
        m1.name = "name";
        doNothing().when(tagRepository).persist(m1);
        when(tagRepository.count("name", m1.name)).thenReturn(1l);

        assertThrows(WebApplicationException.class, () -> {
            Tag result = tagData.postTag(m1);
        });
    }
}
