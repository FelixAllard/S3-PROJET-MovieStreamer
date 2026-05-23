package ca.usherbrooke.fgen.api.Buisness;


import ca.usherbrooke.fgen.api.Data.UserData;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserBuisness {
    private final UserData userData;


    @Inject
    public UserBuisness(UserData userData) {
        this.userData = userData;
    }


    public String ping() {
        return userData.ping();
    }

}
