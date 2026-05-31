package ca.usherbrooke.fgen.api.Data;

import ca.usherbrooke.fgen.api.DAO.UserRepository;
import ca.usherbrooke.fgen.api.Entities.User;
import ca.usherbrooke.fgen.api.Entities.User;
import ca.usherbrooke.fgen.api.Entities.WatchMovieUser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.ArrayList;

import java.util.List;

@ApplicationScoped
public class UserData {
    private final UserRepository userRepository;

    public UserData(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //@Transactional NEEDED TO MODIFY DATABASE
    public String ping() {
        return "pong!";
    }

    public List<User> getAllUsers(){
        return userRepository.listAll();
    }
}
