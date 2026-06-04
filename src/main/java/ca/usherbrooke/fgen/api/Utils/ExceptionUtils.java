package ca.usherbrooke.fgen.api.Utils;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class ExceptionUtils {

    public static void throwException(int status, String message) {
        throw new WebApplicationException(
                Response.status(status)
                        .entity(message)
                        .type(MediaType.APPLICATION_JSON)
                        .build()
        );
    }
}
