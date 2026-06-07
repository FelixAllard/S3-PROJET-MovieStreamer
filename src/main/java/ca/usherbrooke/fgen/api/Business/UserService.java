package ca.usherbrooke.fgen.api.Business;

import ca.usherbrooke.fgen.api.DAO.UserRepository;
import ca.usherbrooke.fgen.api.Entities.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.jwt.JsonWebToken;

@ApplicationScoped
public class UserService {

    private final JsonWebToken jwt;
    private final UserRepository userRepository;

    @Inject
    public UserService(JsonWebToken jwt, UserRepository userRepository) {
        this.jwt = jwt;
        this.userRepository = userRepository;
    }

    @Transactional
    public User resolveCurrentUser() {
        String keycloakId = jwt.getSubject();

        User user = userRepository.find("keycloakId", keycloakId).firstResult();

        if (user == null) {
            user = new User();
            user.keycloakId = keycloakId;
            user.email = jwt.getClaim("email");
            user.name = jwt.getClaim("given_name");
            user.surname = jwt.getClaim("family_name");
            userRepository.persist(user);
        }

        return user;
    }
}