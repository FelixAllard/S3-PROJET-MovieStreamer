package ca.usherbrooke.fgen.api.Data;


import ca.usherbrooke.fgen.api.DAO.TagRepository;
import ca.usherbrooke.fgen.api.Entities.Tag;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
}
