package ca.usherbrooke.fgen.api.Utils;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;

public class ExceptionUtils {

    public static void throwException(int status, String message) {
        Map<String, String> errorResponse = Map.of("error", message);

        throw new WebApplicationException(
                Response.status(status)
                        .entity(errorResponse)
                        .type(MediaType.APPLICATION_JSON)
                        .build()
        );
    }
}