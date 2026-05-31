package ca.usherbrooke.fgen.api.Data;

import ca.usherbrooke.fgen.api.DAO.UserRepository;
import ca.usherbrooke.fgen.api.Entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserDataTest {

    private UserRepository userRepository;
    private UserData userData;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userData = new UserData(userRepository);
    }

    @Test
    void getAllUsers_delegueAuRepositoryEtRetourneListe() {
        User u1 = new User();
        User u2 = new User();
        when(userRepository.listAll()).thenReturn(List.of(u1, u2));

        List<User> result = userData.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepository, times(1)).listAll();
    }

    @Test
    void getAllUsers_retourneListeVideSiRepositoryVide() {
        when(userRepository.listAll()).thenReturn(List.of());

        List<User> result = userData.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).listAll();
    }
}