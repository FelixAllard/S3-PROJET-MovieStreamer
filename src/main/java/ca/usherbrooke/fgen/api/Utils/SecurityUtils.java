package ca.usherbrooke.fgen.api.Utils;

import ca.usherbrooke.fgen.api.Business.UserBusiness;
import ca.usherbrooke.fgen.api.Entities.User;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;

public class SecurityUtils {

    /**
     * Reusable security check to ensure a user can only access their own data,
     * unless they are authenticated as an admin.
     */
    public static User verifyOwnershipOrAdmin(long pathUserId, UserBusiness userBusiness, JsonWebToken jwt, SecurityContext securityContext) {
        // Admin bypass
        if (securityContext != null && securityContext.isUserInRole("admin")) {
            User targetUser = userBusiness.getUserByUserId(pathUserId);
            if (targetUser == null) {
                ExceptionUtils.throwException(404, "User does not exist");
            }
            return targetUser;
        }

        User targetUser = userBusiness.getUserByUserId(pathUserId);
        if (targetUser == null) {
            ExceptionUtils.throwException(404, "User does not exist");
        }

        // Verifies Keycloak ID from JWT matches target user's profile
        String callerKeycloakId = jwt != null ? jwt.getSubject() : null;
        if (callerKeycloakId == null || !callerKeycloakId.equals(targetUser.getKeycloakId())) {
            ExceptionUtils.throwException(403, "Forbidden: You cannot access another user's data");
        }

        return targetUser;
    }
}