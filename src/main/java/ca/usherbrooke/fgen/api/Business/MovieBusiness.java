package ca.usherbrooke.fgen.api.Business;

import ca.usherbrooke.fgen.api.Data.MovieData;
import ca.usherbrooke.fgen.api.Data.TagData;
import ca.usherbrooke.fgen.api.Entities.Tag;
import ca.usherbrooke.fgen.api.Entities.Movie;
import ca.usherbrooke.fgen.api.Utils.ExceptionUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class MovieBusiness {
    private final MovieData movieData;
    private final TagData tagData;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @ConfigProperty(name = "jellyfin.server.url")
    String jellyfinUrl;

    @ConfigProperty(name = "jellyfin.username")
    String jellyfinUsername;

    @ConfigProperty(name = "jellyfin.password")
    String jellyfinPassword;

    private String cachedToken = null;
    private String cachedUserId = null;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Inject
    public MovieBusiness(MovieData movieData, TagData tagData) {
        this.movieData = movieData;
        this.tagData = tagData;
    }

    public String getPlaybackInfo(long movieId) {

        Movie movie = getMovieByMovieId(movieId);
        String streamId = movie.getStreamId();

        if (streamId == null || streamId.isEmpty()) {
            throw new WebApplicationException(
                    Response.status(404)
                            .entity("Streaming metadata is unavailable.")
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );
        }

        try {

            String token = getOrAuthenticateJellyfinToken();

            String cleanStreamId = streamId.split("&")[0];
            String encodedStreamId =
                    URLEncoder.encode(cleanStreamId, StandardCharsets.UTF_8);

            String payload = """
        {
          "UserId":"%s",
          "EnableDirectPlay":true,
          "EnableDirectStream":true,
          "EnableTranscoding":true,
          "MaxStreamingBitrate":120000000,
          "DeviceProfile":{
            "Name":"Chrome",
            "MaxStreamingBitrate":120000000
          }
        }
        """.formatted(cachedUserId);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(
                            URI.create(
                                    String.format(
                                            "%s/Items/%s/PlaybackInfo?UserId=%s&api_key=%s",
                                            jellyfinUrl,
                                            encodedStreamId,
                                            cachedUserId,
                                            token
                                    )
                            )
                    )
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException(response.body());
            }

            return response.body();

        } catch (Exception ex) {

            throw new WebApplicationException(
                    Response.status(502)
                            .entity("Failed to retrieve playback info.")
                            .build()
            );
        }
    }

    public Map<String, String> getStreamUrl(
            long movieId,
            Integer audioStreamIndex,
            Integer subtitleStreamIndex,
            String playSessionId,
            String mediaSourceId
    ) {

        Movie movie = getMovieByMovieId(movieId);

        String streamId = movie.getStreamId();

        if (streamId == null || streamId.isEmpty()) {
            throw new WebApplicationException(
                    Response.status(404)
                            .entity("Streaming unavailable.")
                            .build()
            );
        }

        try {

            String token = getOrAuthenticateJellyfinToken();

            String cleanStreamId = streamId.split("&")[0];

            String encodedStreamId =
                    URLEncoder.encode(cleanStreamId, StandardCharsets.UTF_8);

            StringBuilder url = new StringBuilder();

            url.append(
                    String.format(
                            "%s/Videos/%s/master.m3u8" +
                                    "?UserId=%s" +
                                    "&api_key=%s" +
                                    "&MediaSourceId=%s" +
                                    "&PlaySessionId=%s" +
                                    "&VideoCodec=h264" +
                                    "&AudioCodec=aac" +
                                    "&AudioChannels=2" +
                                    "&SegmentContainer=ts" +
                                    "&Context=Streaming" +
                                    "&EnableSubtitlesInManifest=true",
                            jellyfinUrl,
                            encodedStreamId,
                            cachedUserId,
                            token,
                            mediaSourceId,
                            playSessionId
                    )
            );

            if (audioStreamIndex != null) {
                url.append("&AudioStreamIndex=").append(audioStreamIndex);
            }

            if (subtitleStreamIndex != null) {
                url.append("&SubtitleStreamIndex=").append(subtitleStreamIndex);
            }

            Map<String, String> result = new HashMap<>();
            System.out.println(url.toString());
            result.put("streamUrl", url.toString());

            return result;

        } catch (Exception ex) {

            cachedToken = null;
            cachedUserId = null;

            throw new WebApplicationException(
                    Response.status(502)
                            .entity("Streaming server unavailable.")
                            .build()
            );
        }
    }

    private synchronized String getOrAuthenticateJellyfinToken() throws Exception {

        if (cachedToken != null && cachedUserId != null) {
            return cachedToken;
        }

        String authPayload = String.format(
                "{\"Username\":\"%s\",\"Pw\":\"%s\"}",
                jellyfinUsername,
                jellyfinPassword
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(jellyfinUrl + "/Users/AuthenticateByName"))
                .header("Content-Type", "application/json")
                .header(
                        "X-Emby-Authorization",
                        "MediaBrowser Client=\"MovieApp\", Device=\"Server\", DeviceId=\"backend-01\", Version=\"1.0.0\""
                )
                .POST(HttpRequest.BodyPublishers.ofString(authPayload))
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException(
                    "Authentication failed: " + response.statusCode()
            );
        }

        JsonNode root = objectMapper.readTree(response.body());

        cachedToken = root.get("AccessToken").asText();
        cachedUserId = root.get("User").get("Id").asText();

        return cachedToken;
    }

    public List<Movie> importNewMoviesFromJellyfin() {
        try {
            String token = getOrAuthenticateJellyfinToken();

            String url = String.format(
                    "%s/Users/%s/Items?IncludeItemTypes=Movie&Recursive=true&Fields=People,Studios,Overview,Genres&api_key=%s",
                    jellyfinUrl,
                    cachedUserId,
                    token
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed to fetch items from Jellyfin: " + response.statusCode());
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode items = root.get("Items");

            List<Movie> newlyImportedMovies = new ArrayList<>();

            if (items != null && items.isArray()) {
                for (JsonNode item : items) {
                    String jellyfinId = item.has("Id") ? item.get("Id").asText() : "";
                    String serverId = item.has("ServerId") ? item.get("ServerId").asText() : "";
                    String constructedStreamId = jellyfinId + (serverId.isEmpty() ? "" : "&serverId=" + serverId);

                    boolean exists = movieData.getAllMovies().stream()
                            .anyMatch(m -> m.getStreamId() != null && m.getStreamId().startsWith(jellyfinId));

                    if (!exists) {
                        Movie newMovie = new Movie();
                        newMovie.setTitle(item.has("Name") ? item.get("Name").asText() : "Unknown Title");
                        newMovie.setDescription(item.has("Overview") ? item.get("Overview").asText() : "No description available.");
                        newMovie.setYear(item.has("ProductionYear") ? item.get("ProductionYear").asInt() : 0);

                        if (item.has("RunTimeTicks")) {
                            long ticks = item.get("RunTimeTicks").asLong();
                            float minutes = (float) Math.ceil(ticks / 600000000.0);
                            newMovie.setMovieLength(minutes);
                        } else {
                            newMovie.setMovieLength(0.0f);
                        }

                        if (item.has("ImageTags") && item.has("ImageTags")) {
                            String primaryTag = item.get("ImageTags").get("Primary").asText();
                            newMovie.setThumbnail(String.format("%s/Items/%s/Images/Primary?tag=%s", jellyfinUrl, jellyfinId, primaryTag));
                        } else {
                            newMovie.setThumbnail("");
                        }

                        newMovie.setDirector(extractPeople(item, "Director"));
                        newMovie.setStudio(extractStudio(item));
                        newMovie.setWriter(extractPeople(item, "Writer"));
                        newMovie.setLanguage(item.has("OfficialRating") ? item.get("OfficialRating").asText() : "English");
                        newMovie.setStreamId(constructedStreamId);

                        // Extract genre names into a simple list of strings
                        List<String> genreNames = new ArrayList<>();
                        if (item.has("Genres") && item.get("Genres").isArray()) {
                            for (JsonNode genreNode : item.get("Genres")) {
                                String genreName = genreNode.asText().trim();
                                if (!genreName.isEmpty()) {
                                    genreNames.add(genreName);
                                }
                            }
                        }

                        // Delegate the persistence and tag handling entirely to MovieData
                        Movie savedMovie = movieData.importMovieFromJellyfinData(newMovie, genreNames);
                        if (savedMovie != null) {
                            newlyImportedMovies.add(savedMovie);
                        }
                    }
                }
            }

            return newlyImportedMovies;

        } catch (Exception ex) {
            throw new WebApplicationException(
                    Response.status(502)
                            .entity("Failed to synchronize movies from Jellyfin: " + ex.getMessage())
                            .build()
            );
        }
    }

    // Helper method to extract crew like Director or Writer from Jellyfin's 'People' array
    private String extractPeople(JsonNode item, String type) {
        if (item.has("People") && item.get("People").isArray()) {
            List<String> names = new ArrayList<>();
            for (JsonNode person : item.get("People")) {
                if (person.has("Type") && type.equalsIgnoreCase(person.get("Type").asText())) {
                    if (person.has("Name")) {
                        names.add(person.get("Name").asText());
                    }
                }
            }
            if (!names.isEmpty()) {
                return String.join(", ", names);
            }
        }
        return "Unknown";
    }

    private String extractStudio(JsonNode item) {
        if (item.has("Studios") && item.get("Studios").isArray()) {
            List<String> studioNames = new ArrayList<>();
            for (JsonNode studio : item.get("Studios")) {
                if (studio.has("Name")) {
                    studioNames.add(studio.get("Name").asText());
                }
            }
            if (!studioNames.isEmpty()) {
                return String.join(", ", studioNames);
            }
        }
        return "Unknown Studio";
    }

    public String ping() {
        return movieData.ping();
    }

    public List<Movie> getAllMovies(){
        return movieData.getAllMovies();
    }

    public Movie getMovieByMovieId(long id){
        if(id < 0){
            throw new WebApplicationException(
                    Response.status(422)
                            .entity("Unprocessable ID: ID cannot be negative")
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );
        }
        Movie movie = movieData.getMovieByMovieId(id);
        if(movie == null){
            throw new WebApplicationException(
                    Response.status(404)
                            .entity("Movie not found")
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );
        }
        return movie;
    }

    public boolean deleteMovieByMovieId(long id) {
        if (id < 0)
            throw new WebApplicationException(
                    Response.status(422)
                            .entity("Unprocessable ID: ID cannot be negative")
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );
        boolean isDeleted = movieData.deleteMovieByMovieId(id);
        if (!isDeleted) {
            throw new WebApplicationException(
                    Response.status(404)
                            .entity("Movie not found")
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );
        }
        return true;
    }

    public Movie getMovieByMovieName(String name) {
        if (name == null)
            ExceptionUtils.throwException(400, "Movie Name Null");
        if (name.isEmpty())
            ExceptionUtils.throwException(422, "Movie Name Cannot Be Empty");

        Movie movie = movieData.getMovieByMovieName(name);
        if (movie == null)
            ExceptionUtils.throwException(404, "Movie Not Found");

        return movie;
    }

    public Map<String, Object> getMovieRatingByMovieId(long id) {
        if(id <= 0)
            ExceptionUtils.throwException(422, "Unprocessable ID: ID cannot be negative");

        List<Object[]> rawRatings = movieData.getMovieRatingByMovieId(id);

        if(rawRatings.isEmpty())
            ExceptionUtils.throwException(404, "No ratings found for this movie");

        List<Map<String, Object>> distribution = new ArrayList<>();
        double totalRatings = 0;
        double totalCount = 0;

        for (Object[] r : rawRatings) {
            int rating = (int) r[0];
            long count = (long) r[1];

            Map<String, Object> row = new HashMap<>();
            row.put("rating", rating);
            row.put("count", count);
            distribution.add(row);

            totalRatings += rating * count;
            totalCount += count;
        }

        Map<String, Object> formattedRatings = new HashMap<>();
        formattedRatings.put("average", totalRatings / totalCount);
        formattedRatings.put("distribution", distribution);
        return formattedRatings;
    }
    public List<Movie> getNewMovies(int number){
        if(number <= 0)
            ExceptionUtils.throwException(422, "Unprocessable ID: ID cannot be negative");
        List<Movie> movies = movieData.getNewMovies(number);
        if(movies.isEmpty())
            ExceptionUtils.throwException(204, "No content");
        return movies;
    }
    public Movie postMovie(Movie movie) {
        if(movie == null)
            ExceptionUtils.throwException(422, "Movie Null");
        if(movie.description.isEmpty())
            ExceptionUtils.throwException(422, "Movie Description Empty");
        if(movie.title == null || movie.title.isEmpty())
            ExceptionUtils.throwException(422, "Movie Title Empty");
        if(movie.year < 0)
            ExceptionUtils.throwException(422, "Movie Year Invalid");
        if(movie.director == null || movie.director.isEmpty())
            ExceptionUtils.throwException(422, "Movie Director Empty");
        if(movie.studio ==null || movie.studio.isEmpty())
            ExceptionUtils.throwException(422, "Movie Studio Empty");
        if(movie.writer == null || movie.writer.isEmpty())
            ExceptionUtils.throwException(422, "Movie WriterEmpty");
        if(movie.movieLength < 0)
            ExceptionUtils.throwException(422, "Movie Length Invalid");
        if(movie.language == null || movie.language.isEmpty())
            ExceptionUtils.throwException(422, "Movie Language Empty");
        if(movie.thumbnail == null || movie.thumbnail.isEmpty())
            ExceptionUtils.throwException(422, "Movie Thumbnail Empty");
        if(movie.streamId == null || movie.streamId.isEmpty())
            ExceptionUtils.throwException(422, "Movie Stream ID Empty");

        return movieData.postMovie(movie);
    }

    public List<Movie> getMoviesByMovieTags(List<Integer> tagIds) {
        if (tagIds == null || tagIds.isEmpty())
            ExceptionUtils.throwException(422, "Tags list cannot be null or empty");

        List<Movie> movies = movieData.getMoviesByMovieTags(tagIds);
        if (movies.isEmpty())
            ExceptionUtils.throwException(204, "No content");

        return movies;
    }

    public Movie updateMovieByMovieId(int id, Movie updatedMovie) {
        if (id < 0)
            ExceptionUtils.throwException(422, "Unprocessable ID: ID cannot be negative");
        if (updatedMovie == null)
            ExceptionUtils.throwException(400, "Movie Null");
        if (updatedMovie.description == null || updatedMovie.description.isEmpty())
            ExceptionUtils.throwException(422, "Movie Description Empty");
        if (updatedMovie.title == null || updatedMovie.title.isEmpty())
            ExceptionUtils.throwException(422, "Movie Title Empty");
        if (updatedMovie.year < 0)
            ExceptionUtils.throwException(422, "Movie Year Invalid");
        if (updatedMovie.director == null || updatedMovie.director.isEmpty())
            ExceptionUtils.throwException(422, "Movie Director Empty");
        if (updatedMovie.studio == null || updatedMovie.studio.isEmpty())
            ExceptionUtils.throwException(422, "Movie Studio Empty");
        if (updatedMovie.writer == null || updatedMovie.writer.isEmpty())
            ExceptionUtils.throwException(422, "Movie Writer Empty");
        if (updatedMovie.movieLength < 0)
            ExceptionUtils.throwException(422, "Movie Length Invalid");
        if (updatedMovie.language == null || updatedMovie.language.isEmpty())
            ExceptionUtils.throwException(422, "Movie Language Empty");
        if (updatedMovie.thumbnail == null || updatedMovie.thumbnail.isEmpty())
            ExceptionUtils.throwException(422, "Movie Thumbnail Empty");
        if (updatedMovie.streamId == null || updatedMovie.streamId.isEmpty())
            ExceptionUtils.throwException(422, "Movie Stream ID Empty");

        return movieData.updateMovieByMovieId(id, updatedMovie);
    }

    public List<Movie> searchMovies(List<Integer> tags, Integer yearMin, Integer yearMax,
                                    String language, String director, String studio,
                                    String writer, String title) {
        if (yearMin != null && yearMin < 0)
            ExceptionUtils.throwException(422, "Year Min cannot be negative");
        if (yearMax != null && yearMax < 0)
            ExceptionUtils.throwException(422, "Year Max cannot be negative");
        if (yearMin != null && yearMax != null && yearMin > yearMax)
            ExceptionUtils.throwException(422, "Year Min cannot be greater than Year Max");

        return movieData.searchMovies(tags, yearMin, yearMax, language, director, studio, writer, title);
    }

    public List<Movie> getMoviesByPartialTitle(String title) {
        return movieData.getMoviesByPartialTitle(title);

    }
}
