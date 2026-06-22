<script setup>
import NewMoviesCaroussel from "../components/NewMoviesCaroussel.vue";
import AnimatedTitle from "../components/AnimatedTitle.vue";
</script>

<template>
  <div class="home">

    <!-- HERO SECTION -->
    <section class="hero container text-center">

      <AnimatedTitle
          text="Welcome to CinePulse"
          size="display-2"
          :showCursor="false"
          glow
      />

      <p class="subtitle mt-3">
        A modern movie streaming experience built for discovery, tracking, and pure cinematic immersion.
      </p>

      <div class="cta mt-4">

        <RouterLink to="/movies">
          <button class="btn btn-primary btn-lg px-4">
            Explore Movies
          </button>
        </RouterLink>

        <RouterLink to="/">
          <button class="btn btn-outline-light btn-lg px-4 ms-2">
            Your Watchlist
          </button>
        </RouterLink>

      </div>

    </section>

    <!-- FEATURE STRIP -->
    <section class="features container mt-5">

      <div class="row g-4 text-center">

        <div class="col-md-4">
          <div class="feature-card">
            <h3>🎬 Discover</h3>
            <p>Find new movies instantly with smart search and curated recommendations.</p>
          </div>
        </div>

        <div class="col-md-4">
          <div class="feature-card">
            <h3>📺 Stream</h3>
            <p>Enjoy a smooth, cinematic browsing experience designed for movie lovers.</p>
          </div>
        </div>

        <div class="col-md-4">
          <div class="feature-card">
            <h3>⭐ Track</h3>
            <p>Keep your watchlist organized and never lose track of what you love.</p>
          </div>
        </div>

      </div>

    </section>

    <!-- NEW MOVIES -->
    <section class="container mt-5">

      <NewMoviesCaroussel />

    </section>

    <!-- ABOUT STRIP -->
    <section class="about mt-5 py-5">

      <div class="container text-center">

        <h2 class="text-white fw-bold">Built for Movie Lovers</h2>

        <p class="mt-3 text-light opacity-75">
          CinePulse is a next-generation streaming companion designed to bring together discovery,
          personalization, and a seamless watchlist experience.
          We’re a new company focused on making movie exploration feel effortless and immersive.
        </p>

      </div>

    </section>

  </div>
</template>
<style scoped>
.home {
  min-height: 100vh;
  background: radial-gradient(circle at top, #1a0f2e, #05020a);
  color: white;
}

/* HERO */
.hero {
  padding-top: 80px;
  padding-bottom: 40px;
}

.subtitle {
  font-size: 1.2rem;
  max-width: 700px;
  margin: 0 auto;
  opacity: 0.8;
}

/* BUTTONS */
.cta .btn {
  border-radius: 12px;
}

/* FEATURES */
.features {
  margin-top: 60px;
}

.feature-card {
  background: rgba(255,255,255,0.06);
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 18px;
  padding: 24px;
  backdrop-filter: blur(10px);
  transition: 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 20px 40px rgba(139,92,246,0.2);
}

/* SECTION TITLE */
.section-title h2 {
  font-size: 2rem;
}

/* ABOUT */
.about {
  background: linear-gradient(180deg, transparent, rgba(139,92,246,0.08));
}
</style>