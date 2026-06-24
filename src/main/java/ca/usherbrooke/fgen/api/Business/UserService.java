package ca.usherbrooke.fgen.api.Business;

import ca.usherbrooke.fgen.api.DAO.UserRepository;
import ca.usherbrooke.fgen.api.Entities.User;
import ca.usherbrooke.fgen.api.Utils.ExceptionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.Collections;

@ApplicationScoped
public class UserService {

    private final JsonWebToken jwt;
    private final UserRepository userRepository;
    private final Keycloak keycloak;

    @Inject
    public UserService(JsonWebToken jwt, UserRepository userRepository, Keycloak keycloak) {
        this.jwt = jwt;
        this.userRepository = userRepository;
        this.keycloak = keycloak;
    }

    @Transactional
    public User resolveCurrentUser() {
        String keycloakId = jwt.getSubject();

        User user = userRepository.find("keycloakId", keycloakId).firstResult();

        if (user == null) {
            user = new User();
            user.keycloakId = keycloakId;
            user.email = jwt.getClaim("email");
            userRepository.persist(user);
        }

        return user;
    }

    @Transactional
    public User registerNewUser(String username, String email, String password) {
        UserRepresentation keycloakUser = new UserRepresentation();
        keycloakUser.setUsername(username);
        keycloakUser.setEmail(email);
        keycloakUser.setEnabled(true);
        keycloakUser.setEmailVerified(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        keycloakUser.setCredentials(Collections.singletonList(credential));

        String realmName = "usager";
        UsersResource usersResource = keycloak.realm(realmName).users();
        Response response = usersResource.create(keycloakUser);

        if (response.getStatus() != 201) {
            ExceptionUtils.throwException(response.getStatus(), "Failed to provision target user in Keycloak identity system.");
        }

        String path = response.getLocation().getPath();
        String keycloakId = path.substring(path.lastIndexOf("/") + 1);

        try {
            RoleRepresentation userRole = keycloak.realm(realmName).roles().get("user").toRepresentation();

            usersResource.get(keycloakId).roles().realmLevel().add(Collections.singletonList(userRole));
        } catch (Exception e) {
            System.err.println("Failed to assign default 'user' role to Keycloak identity profile: " + e.getMessage());
        }

        User localUser = new User();
        localUser.setUsername(username);
        localUser.setEmail(email);
        localUser.setKeycloakId(keycloakId);
        userRepository.persist(localUser);

        return localUser;
    }
}