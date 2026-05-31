package ca.usherbrooke.fgen.api.Business;


import ca.usherbrooke.fgen.api.Data.UserData;
import ca.usherbrooke.fgen.api.Entities.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

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

}
