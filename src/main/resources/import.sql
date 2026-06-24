-- Tags
INSERT INTO app.tag (name) VALUES
('Sci-Fi'),
('Action'),
('Drama'),
('Comedy');

-- Movies
INSERT INTO app.movie (title, description, year, movieLength, thumbnail, director, writer, studio, language) VALUES
('Interstellar', 'A team of explorers travel through a wormhole in space.', 2014, 169.0, 'https://tse3.mm.bing.net/th/id/OIP.Z6aUuDLMpZtMYF1y9LYqSwHaLH?cb=thfvnextfalcon2&rs=1&pid=ImgDetMain&o=7&rm=3', 'Christopher Nolan', 'Jonathan Nolan', 'Paramount', 'English'),
('The Matrix', 'A computer hacker learns from mysterious rebels about the true nature of his reality.', 1999, 136.0, 'https://th.bing.com/th/id/R.d60d296c62dc75447bbafed62451f246?rik=sdz68QwhV9VbnQ&riu=http%3a%2f%2fshatthemovies.com%2fwp-content%2fuploads%2f2017%2f10%2fhEpWvX6Bp79eLxY1kX5ZZJcme5U.jpg&ehk=rCEZalvHRg9Hzt4V6IzDGmsbn2d8Oom5JJYSTQH3NNw%3d&risl=&pid=ImgRaw&r=0', 'Lana Wachowski', 'Lilly Wachowski', 'Warner Bros.', 'English'),
('Inception', 'A thief who steals corporate secrets through the use of dream-sharing technology.', 2010, 148.0, 'https://cdn.shopify.com/s/files/1/1416/8662/products/inception_2010_imax_original_film_art_2000x.jpg?v=1551890318', 'Christopher Nolan', 'Christopher Nolan', 'Warner Bros.', 'English'),
('The Dark Knight', 'When the menace known as the Joker wreaks havoc on Gotham.', 2008, 152.0, 'https://tse1.mm.bing.net/th/id/OIP.1AjXRjwX74DedUFn5sBnXgHaJ4?cb=thfvnextfalcon2&rs=1&pid=ImgDetMain&o=7&rm=3', 'Christopher Nolan', 'Jonathan Nolan', 'Warner Bros.', 'English'),
('Superbad', 'Two co-dependent high school seniors are forced to deal with separation anxiety.', 2007, 113.0, 'https://image.tmdb.org/t/p/original/2pcIeB50XjM8x0V5Wv6r6VAvArM.jpg', 'Greg Mottola', 'Seth Rogen', 'Columbia Pictures', 'English');

-- Map Movies to Tags (Movie/Tag Bridge Table)
INSERT INTO app.movie_tags (movie_id, tag_id) VALUES
(1, 1), -- Interstellar -> Sci-Fi
(1, 3), -- Interstellar -> Drama
(2, 1), -- The Matrix -> Sci-Fi
(2, 2), -- The Matrix -> Action
(3, 1), -- Inception -> Sci-Fi
(3, 2), -- Inception -> Action
(4, 2), -- The Dark Knight -> Action
(4, 3), -- The Dark Knight -> Drama
(5, 4); -- Superbad -> Comedy

-- Users
INSERT INTO app.app_user (username, email, keycloak_id) VALUES
('admin', 'admin1@admin1.com', '11111111-1111-1111-1111-111111111111'),
('user', 'user2@user2.com', '22222222-2222-2222-2222-222222222222');

-- User Watchlists & History (User/Movie Bridge Table)
INSERT INTO app.watch_movie_user (user_id, movie_id, status, saved, rating) VALUES
-- (User 1) Interactions
(1, 1, 'WATCHED', true, 5),       -- Watched Interstellar, on watchlist, rated 5
(1, 2, 'WATCHING', false, NULL),   -- Watching The Matrix, not on watchlist
(1, 3, 'WATCHED', false, 4),       -- Watched Inception, not on watchlist, rated 4
(1, 4, 'WATCHING', true, NULL),    -- Watching The Dark Knight, on watchlist
-- (User 2) Interactions
(2, 1, 'WATCHING', true, NULL),    -- Watching Interstellar, on watchlist
(2, 3, 'WATCHED', true, 5),        -- Watched Inception, on watchlist, rated 5
(2, 4, 'WATCHED', false, 4),       -- Watched The Dark Knight, not on watchlist, rated 4
(2, 5, 'WATCHING', true, NULL);    -- Watching Superbad, on watchlist