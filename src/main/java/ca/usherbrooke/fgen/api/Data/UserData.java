package ca.usherbrooke.fgen.api.Data;

import ca.usherbrooke.fgen.api.DAO.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserData {
    private final UserRepository userRepository;

    public UserData(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String ping(){
        return "pong!";
    }

}
