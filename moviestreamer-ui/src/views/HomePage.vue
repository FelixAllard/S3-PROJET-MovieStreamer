<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import NewMoviesCaroussel from "../components/NewMoviesCaroussel.vue";
import AnimatedTitle from "../components/AnimatedTitle.vue";
import { isLoggedIn } from '/src/utils/auth.js'

const loggedIn = ref(isLoggedIn())

function refreshAuthState() {
  loggedIn.value = isLoggedIn()
}

onMounted(() => {
  window.addEventListener('auth-change', refreshAuthState)
})

onBeforeUnmount(() => {
  window.removeEventListener('auth-change', refreshAuthState)
})
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
          <button class="btn btn-primary btn-lg px-4">Explore Movies</button>
        </RouterLink>
        <RouterLink v-if="loggedIn" to="/watchlist">
          <button class="btn btn-outline-light btn-lg px-4 ms-2">Your Watchlist</button>
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

    <section class="container mt-5">
      <h2 class="section-heading text-center mb-4">🎥 New Releases</h2>
      <NewMoviesCaroussel />
    </section>

    <section class="stats-strip mt-5 py-4">
      <div class="container">
        <div class="row text-center g-4">
          <div class="col-md-4">
            <div class="stat-item">
              <h2 class="stat-number">10 000+</h2>
              <p class="stat-label">Films disponibles</p>
            </div>
          </div>
          <div class="col-md-4">
            <div class="stat-item">
              <h2 class="stat-number">50+</h2>
              <p class="stat-label">Genres couverts</p>
            </div>
          </div>
          <div class="col-md-4">
            <div class="stat-item">
              <h2 class="stat-number">100%</h2>
              <p class="stat-label">Conçu par des passionnés</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="about mt-5 py-5">
      <div class="container text-center">
        <h2 class="text-white fw-bold">Built for Movie Lovers</h2>
        <p class="mt-3 text-light opacity-75">
          CinePulse is a next-generation streaming companion designed to bring together discovery,
          personalization, and a seamless watchlist experience.
          We're a new company focused on making movie exploration feel effortless and immersive.
        </p>
      </div>
    </section>

    <section class="team-section py-5 mt-3">
      <div class="container text-center">

        <h2 class="text-white fw-bold mb-2">About us</h2>
        <p class="text-light opacity-75 mb-5">
          CinePulse was developed by a group of computer engineering students at
          Université de Sherbrooke, who are also hardcore fans of movies, series
          and technologies. Sharing our own libraries to friends and families is
          a dream come true within the context of this school project.
        </p>

        <div class="row g-4 justify-content-center">

          <div class="col-md-4">
            <div class="team-card">
              <div class="team-avatar">FA</div>
              <h5>Félix Allard</h5>
              <p class="team-role">Product Owner - Scrum Master</p>
            </div>
          </div>

          <div class="col-md-4">
            <div class="team-card">
              <div class="team-avatar">RB</div>
              <h5>Rayan Bellot</h5>
              <p class="team-role">Developer</p>
            </div>
          </div>

          <div class="col-md-4">
            <div class="team-card">
              <div class="team-avatar">EG</div>
              <h5>Émile Gagnon</h5>
              <p class="team-role">Developer</p>
            </div>
          </div>

          <div class="col-md-4">
            <div class="team-card">
              <div class="team-avatar">JM</div>
              <h5>Justin Morissette</h5>
              <p class="team-role">Developer</p>
            </div>
          </div>

          <div class="col-md-4">
            <div class="team-card">
              <div class="team-avatar">DS</div>
              <h5>Donavan Sirois</h5>
              <p class="team-role">Scrum Master - Developer</p>
            </div>
          </div>

          <div class="col-md-4">
            <div class="team-card">
              <div class="team-avatar">MV</div>
              <h5>Mathieu Vincent</h5>
              <p class="team-role">Developer</p>
            </div>
          </div>

        </div>
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

.cta .btn {
  border-radius: 12px;
}

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

.section-title h2 {
  font-size: 2rem;
}

.about {
  background: linear-gradient(180deg, transparent, rgba(139,92,246,0.08));
}

.stats-strip {
  background: rgba(139, 92, 246, 0.06);
  border-top: 1px solid rgba(255,255,255,0.05);
  border-bottom: 1px solid rgba(255,255,255,0.05);
}

.stat-number {
  font-size: 2.5rem;
  font-weight: 800;
  color: #a78bfa;
}

.stat-label {
  color: rgba(255,255,255,0.6);
  font-size: 0.95rem;
}

.team-section {
  background: linear-gradient(180deg, transparent, rgba(139,92,246,0.05));
}

.team-card {
  background: rgba(255,255,255,0.04);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 16px;
  padding: 28px 20px;
  transition: 0.3s ease;
}

.team-avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #7c3aed, #a78bfa);
  color: white;
  font-weight: 700;
  font-size: 1.1rem;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 12px auto;
}

.team-role {
  color: #a78bfa;
  font-size: 0.85rem;
  margin: 0;
}

.section-heading {
  color: white;
  font-weight: 700;
}

</style>
