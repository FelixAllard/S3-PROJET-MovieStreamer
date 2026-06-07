package ca.usherbrooke.fgen.api.Business;

import ca.usherbrooke.fgen.api.Data.UserData;
import ca.usherbrooke.fgen.api.Entities.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@ApplicationScoped
public class UserBusiness {
    private final UserData userData;

    @Inject
    public UserBusiness(UserData userData) {
        this.userData = userData;
    }

    public String ping() {
        return userData.ping();
    }

    public List<User> getAllUsers(){
        return userData.getAllUsers();
    }

    public User getUserByUserId(long id)
    {
        return userData.getUserByUserId(id);
    }

    public boolean deleteUserByUserId(long id) {
        if (id < 0)
            throw new WebApplicationException(
                    Response.status(422)
                            .entity("Unprocessable ID: ID cannot be negative")
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );
        return userData.deleteUserByUserId(id);
    }

}
