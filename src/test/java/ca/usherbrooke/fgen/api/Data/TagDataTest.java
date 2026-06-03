package ca.usherbrooke.fgen.api.Data;

import ca.usherbrooke.fgen.api.DAO.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

public class TagDataTest {

    private TagRepository tagRepository;
    private TagData tagData;

    @BeforeEach
    void setUp() {
        System.out.println("=================================");
        System.out.println(" TAG DATA TEST START ");
        System.out.println("=================================");

        tagRepository = Mockito.mock(TagRepository.class);
        tagData = new TagData(tagRepository);
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
