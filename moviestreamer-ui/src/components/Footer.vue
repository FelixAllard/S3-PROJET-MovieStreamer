<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
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
  <footer class="movie-footer">
    <div class="container-fluid px-3 px-md-4">
      <div class="footer-content">
        <div>
          <RouterLink class="footer-brand" to="/">CinePulse</RouterLink>
          <p class="footer-text mb-0">Personal movie directory</p>
        </div>

        <nav class="footer-links" aria-label="Footer navigation">
          <RouterLink to="/">Home</RouterLink>
          <RouterLink to="/movies">Movies</RouterLink>
          <RouterLink v-if="loggedIn" to="/watchlist">Watchlist</RouterLink>
          <RouterLink v-else to="/login">Login</RouterLink>
        </nav>

        <p class="footer-credit mb-0">
          © 2026 CinePulse — University of Sherbrooke
        </p>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.movie-footer {
  margin-top: 3rem;
  padding: 1.5rem 0;
  background: linear-gradient(90deg, rgba(7, 4, 15, 0.96), rgba(24, 12, 44, 0.9), rgba(10, 6, 22, 0.96));
  border-top: 1px solid rgba(139, 92, 246, 0.18);
  box-shadow: 0 -8px 30px rgba(0, 0, 0, 0.25);
}

.footer-content {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto minmax(0, 1fr);
  align-items: center;
  gap: 1.5rem;
}

.footer-brand {
  display: inline-block;
  margin-bottom: 0.35rem;
  color: #ffffff;
  font-weight: 800;
  letter-spacing: 0.03em;
  text-transform: uppercase;
}

.footer-brand:hover {
  color: #d8b4fe;
}

.footer-text {
  color: var(--text-muted);
  font-size: 0.95rem;
}

.footer-links {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.footer-links a {
  color: var(--text-soft);
  font-weight: 600;
  transition: color 0.2s ease;
}

.footer-links a:hover,
.footer-links a.router-link-active {
  color: #ffffff;
}

.footer-credit {
  color: var(--text-muted);
  font-size: 0.9rem;
  text-align: right;
}

@media (max-width: 767.98px) {
  .footer-content {
    display: flex;
    align-items: flex-start;
    flex-direction: column;
  }

  .footer-links {
    justify-content: flex-start;
  }

  .footer-credit {
    text-align: left;
  }
}
</style>
