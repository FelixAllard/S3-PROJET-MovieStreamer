-- Tags
INSERT INTO app.tag (name) VALUES
('Sci-Fi'),    -- ID 1
('Action'),    -- ID 2
('Drama'),     -- ID 3
('Comedy'),    -- ID 4
('Biography'), -- ID 5
('History'),   -- ID 6
('War'),       -- ID 7
('Adventure'), -- ID 8
('Thriller'),  -- ID 9
('Crime'),     -- ID 10
('Mystery');   -- ID 11

-- Movies
INSERT INTO app.movie (title, description, year, movieLength, thumbnail, director, writer, studio, language, streamId) VALUES
('October Sky', 'The true story of Homer Hickam, a coal miner''s son who was inspired by the first Sputnik launch to take up rocketry against his father''s wishes.', 1999, 107.0, 'https://image.tmdb.org/t/p/w600_and_h900_face/umWrXCIWdcYPf764ruvMRCpG3cA.jpg', 'Joe Johnston', 'Lewis Colick', 'Universal Pictures', 'English, Hindi', '503e200babbbbcddb3adb701b0f0698b&serverId=b30f92c5262844529f52a9c1f004358b'),
('Project Hail Mary', 'Alone on a tiny spaceship, a lone astronaut must save Earth from an extinction-level event.', 2026, 120.0, 'https://image.tmdb.org/t/p/w600_and_h900_face/yihdXomYb5kTeSivtFndMy5iDmf.jpg', 'Phil Lord', 'Drew Goddard', 'Metro-Goldwyn-Mayer', 'English, Hindi', '34565c3c98daeda82f930a4745095db4&serverId=b30f92c5262844529f52a9c1f004358b'),
('12 Angry Men', 'The jury in a New York City murder trial is frustrated by a single member whose skeptical caution forces them to more carefully consider the evidence.', 1957, 96.0, 'https://upload.wikimedia.org/wikipedia/commons/b/b5/12_Angry_Men_%281957_film_poster%29.jpg', 'Sidney Lumet', 'Reginald Rose', 'Orion-Nova Productions', 'English', '163fe62e8b6a6c8d27cb2efd0dcb8c5d&serverId=b30f92c5262844529f52a9c1f004358b'),
('Contact', 'Dr. Ellie Arroway, after years of searching, finds conclusive radio proof of extraterrestrial intelligence, sending her on a journey to decode their machine.', 1997, 150.0, 'https://image.tmdb.org/t/p/w600_and_h900_face/bCpMIywuNZeWt3i5UMLEIc0VSwM.jpg', 'Robert Zemeckis', 'James V. Hart', 'Warner Bros.', 'English', '50bfd6ff9728d15da56b97490128e04b&serverId=b30f92c5262844529f52a9c1f004358b');

-- Map Movies to Tags (Movie/Tag Bridge Table)
INSERT INTO app.movie_tags (movie_id, tag_id) VALUES
(1, 3),  -- October Sky -> Drama
(1, 5),  -- October Sky -> Biography
(1, 6),  -- October Sky -> History
(2, 1),  -- Project Hail Mary -> Sci-Fi
(2, 2),  -- Project Hail Mary -> Action
(2, 8),  -- Project Hail Mary -> Adventure
(2, 9),  -- Project Hail Mary -> Thriller
(3, 3),  -- 12 Angry Men -> Drama
(3, 10), -- 12 Angry Men -> Crime
(4, 1),  -- Contact -> Sci-Fi
(4, 3),  -- Contact -> Drama
(4, 11); -- Contact -> Mystery

-- Users
INSERT INTO app.app_user (username, email, keycloak_id) VALUES
('admin', 'admin1@admin1.com', '11111111-1111-1111-1111-111111111111'),
('user', 'user2@user2.com', '22222222-2222-2222-2222-222222222222');

-- User Watchlists & History (User/Movie Bridge Table)
INSERT INTO app.watch_movie_user (user_id, movie_id, status, saved, rating) VALUES
-- (User 1) Interactions
(1, 1, 'WATCHED', true, 5),       -- Watched October Sky, on watchlist, rated 5
(1, 2, 'WATCHED', false, 4),       -- Watched Project Hail Mary, not on watchlist, rated 4
(1, 3, 'WATCHING', true, NULL),    -- Watching 12 Angry Men, on watchlist
-- (User 2) Interactions
(2, 1, 'WATCHING', true, NULL),    -- Watching October Sky, on watchlist
(2, 2, 'WATCHED', true, 5),        -- Watched Project Hail Mary, on watchlist, rated 5
(2, 3, 'WATCHED', false, 4),       -- Watched 12 Angry Men, not on watchlist, rated 4
(2, 4, 'WATCHING', true, NULL);    -- Watching Contact, on watchlist