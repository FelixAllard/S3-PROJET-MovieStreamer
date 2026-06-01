-- Tags
INSERT INTO app.tag (name) VALUES
('Sci-Fi'),
('Action'),
('Drama'),
('Comedy');

-- Movies
INSERT INTO app.movie (title, description, year, movieLength, thumbnail, director, writer, studio, language) VALUES
('Interstellar', 'A team of explorers travel through a wormhole in space.', 2014, 169.0, 'interstellar.jpg', 'Christopher Nolan', 'Jonathan Nolan', 'Paramount', 'English'),
('The Matrix', 'A computer hacker learns from mysterious rebels about the true nature of his reality.', 1999, 136.0, 'matrix.jpg', 'Lana Wachowski', 'Lilly Wachowski', 'Warner Bros.', 'English');

-- Map Movies to Tags (Many-to-Many Bridge Table)
INSERT INTO app.movie_tags (movie_id, tag_id) VALUES
(1, 1),
(1, 3),
(2, 1),
(2, 2);

-- Users
INSERT INTO app.app_user (name, surname, email) VALUES
('John', 'Doe', 'john.doe@example.com'),
('Jane', 'Smith', 'jane.smith@example.com');

-- User Watchlists (WatchMovieUser Bridge Table)
INSERT INTO app.watch_movie_user (user_id, movie_id, status, saved) VALUES
(1, 1, 'WATCHED', true),
(1, 2, 'WATCHING', false),
(2, 1, 'NOT_WATCHED', true);