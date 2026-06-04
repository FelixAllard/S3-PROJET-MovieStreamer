package ca.usherbrooke.fgen.api.Data;


import ca.usherbrooke.fgen.api.Entities.Tag;
import com.google.inject.Inject;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.openapi.internal.models.AbstractOpenAPI;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import ca.usherbrooke.fgen.api.Data.TagData;

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
}
