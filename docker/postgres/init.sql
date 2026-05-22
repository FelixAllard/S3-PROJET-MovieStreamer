--
-- PostgreSQL database dump
--

-- Dumped from database version 13.6 (Debian 13.6-1.pgdg110+1)
-- Dumped by pg_dump version 14.1

-- Started on 2022-05-25 18:13:12 EDT

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 6 (class 2615 OID 16385)
-- Name: app; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA app;


ALTER SCHEMA app OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 201 (class 1259 OID 16431)
-- Name: message; Type: TABLE; Schema: app; Owner: postgres
--

CREATE TABLE app.user (
    id SERIAL PRIMARY KEY,
    name character varying(255) NOT NULL,
    email character varying(255) NOT NULL UNIQUE,
    password character varying(255) NOT NULL
);
ALTER TABLE app.user OWNER TO postgres;


CREATE TABLE app.tag(
                        id SERIAL PRIMARY KEY,
                        name character varying(255) NOT NULL
);
ALTER TABLE app.tag OWNER TO postgres;


CREATE TABLE app.movie(
                          id SERIAL PRIMARY KEY,
                          title character varying(255) NOT NULL,
                          description TEXT,
                          year integer,
                          movieLength integer NOT NULL, --en seconde my nigga
                          thumbnail_url TEXT,
                          director character varying(255) NOT NULL,
                          writer character varying(255) NOT NULL,
                          studio character varying(255) NOT NULL,
                          language character varying(255) NOT NULL

);
ALTER TABLE app.movie OWNER TO postgres;



CREATE TABLE app.user_savedMovies(
    user_id integer NOT NULL,
    movie_id integer NOT NULL,
    --saved_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, movie_id),
    FOREIGN KEY (user_id) REFERENCES app.user(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES app.movie(id) ON DELETE CASCADE
);
ALTER TABLE app.user_savedMovies OWNER TO postgres;


CREATE TABLE app.user_watchedMovies(
   user_id integer NOT NULL,
   movie_id integer NOT NULL,
   --saved_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
   PRIMARY KEY (user_id, movie_id),
   FOREIGN KEY (user_id) REFERENCES app.user(id) ON DELETE CASCADE,
   FOREIGN KEY (movie_id) REFERENCES app.movie(id) ON DELETE CASCADE
);
ALTER TABLE app.user_watchedMovies OWNER TO postgres;


CREATE TABLE app.movie_tag(
    tag_id integer NOT NULL,
    movie_id integer NOT NULL,
    --saved_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (tag_id, movie_id),
    FOREIGN KEY (tag_id) REFERENCES app.tag(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES app.movie(id) ON DELETE CASCADE
);
ALTER TABLE app.movie_tag OWNER TO postgres;



