<script setup>
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap'

import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { animate } from 'motion-v'
import apiClient from '/src/services/ApiClient.js'
import { Movie } from '/src/entities/Movie.js'
import { isLoggedIn, resolveDbUserId } from '/src/utils/auth.js'

const route = useRoute()
const router = useRouter()

const movie = ref(null)
const loading = ref(true)
const errorMessage = ref('')
const pageRef = ref(null)
const contentRef = ref(null)
const loggedIn = ref(isLoggedIn())
const userId = ref(null)
const saved = ref(false)
const saving = ref(false)
const watchlistMessage = ref('')

const movieId = computed(() => route.params.id)

onMounted(async () => {
  await fetchMovie()
  await fetchSavedStatus()

  if (pageRef.value) {
    animate(
        pageRef.value,
        { opacity: [0, 1] },
        { duration: 0.45, easing: 'ease-out' }
    )
  }

  if (contentRef.value) {
    animate(
        contentRef.value,
        { opacity: [0, 1], y: [30, 0], scale: [0.98, 1] },
        { duration: 0.55, easing: 'ease-out' }
    )
  }
})

async function fetchMovie() {
  try {
    loading.value = true
    errorMessage.value = ''

    const data = await apiClient.get(`/movie/${movieId.value}`)
    movie.value = data ? new Movie(data) : null

    if (!movie.value) {
      errorMessage.value = 'Movie not found.'
    }
  } catch (err) {
    console.error('Failed to fetch movie:', err)
    errorMessage.value = 'Failed to load movie.'
  } finally {
    loading.value = false
  }
}

async function fetchSavedStatus() {
  if (!loggedIn.value || !movie.value) return

  try {
    userId.value = await resolveDbUserId(apiClient)
    if (!userId.value) return

    saved.value = await apiClient.get(`/watch-history/saved/${userId.value}/${movieId.value}`)
  } catch (err) {
    console.error('Failed to fetch saved movie status:', err)
  }
}

async function toggleSavedStatus() {
  if (!loggedIn.value || !userId.value || saving.value) return

  try {
    saving.value = true
    watchlistMessage.value = ''

    const nextSaved = !saved.value
    const updated = await apiClient.put(`/watch-history/saved/${userId.value}/${movieId.value}/${nextSaved}`, {})
    saved.value = Boolean(updated?.saved)
    watchlistMessage.value = saved.value ? 'Added to your watchlist.' : 'Removed from your watchlist.'
  } catch (err) {
    console.error('Failed to update saved movie status:', err)
    watchlistMessage.value = 'Unable to update your watchlist.'
  } finally {
    saving.value = false
  }
}

function goBack() {
  router.push('/movies')
}
</script>

<template>
  <div
      ref="pageRef"
      class="single-movie-page"
      :style="movie?.thumbnail ? { '--movie-bg': `url(${movie.thumbnail})` } : {}"
  >
    <div class="background-layer"></div>
    <div class="background-overlay"></div>

    <div class="container-fluid px-3 px-md-4 py-4 position-relative">
      <div class="mb-4">
        <button class="btn back-btn" @click="goBack">
          Back to Movies
        </button>
      </div>

      <div v-if="loading" class="state-box text-center text-white">
        Loading movie...
      </div>

      <div v-else-if="errorMessage" class="state-box text-center text-white">
        <h2 class="mb-2">Something went wrong</h2>
        <p class="mb-0">{{ errorMessage }}</p>
      </div>

      <div
          v-else-if="movie"
          ref="contentRef"
          class="movie-details-panel"
      >
        <div class="row g-4 align-items-center">
          <div class="col-12 col-lg-4">
            <div class="poster-wrap">
              <img
                  :src="movie.thumbnail"
                  :alt="movie.title"
                  class="poster-image"
              />
            </div>
          </div>

          <div class="col-12 col-lg-8">
            <div class="movie-copy">
              <p class="eyebrow mb-2">Featured Movie</p>
              <h1 class="movie-heading mb-3">{{ movie.title }}</h1>

              <div class="meta-row mb-3">
                <span v-if="movie.year" class="meta-pill">{{ movie.year }}</span>
                <span v-if="movie.movieLength" class="meta-pill">{{ movie.movieLength }} min</span>
                <span v-if="movie.language" class="meta-pill">{{ movie.language }}</span>
              </div>

              <div v-if="loggedIn" class="watchlist-actions mb-3">
                <button
                    class="btn watchlist-btn"
                    :class="{ saved }"
                    :disabled="saving"
                    @click="toggleSavedStatus"
                >
                  {{ saving ? 'Saving...' : saved ? 'Remove from Watchlist' : 'Add to Watchlist' }}
                </button>
                <span v-if="watchlistMessage" class="watchlist-message">{{ watchlistMessage }}</span>
              </div>

              <p class="movie-description mb-4">
                {{ movie.description }}
              </p>

              <div v-if="movie.tags?.length" class="mb-4">
                <h2 class="section-label mb-2">Genres</h2>
                <div class="tags">
                  <span
                      v-for="tag in movie.tags"
                      :key="tag.id"
                      class="tag"
                  >
                    {{ tag.name }}
                  </span>
                </div>
              </div>

              <div class="row g-3">
                <div class="col-12 col-md-6">
                  <div class="info-card">
                    <h3 class="info-label">Director</h3>
                    <p class="info-value mb-0">{{ movie.director || 'Unknown' }}</p>
                  </div>
                </div>

                <div class="col-12 col-md-6">
                  <div class="info-card">
                    <h3 class="info-label">Writer</h3>
                    <p class="info-value mb-0">{{ movie.writer || 'Unknown' }}</p>
                  </div>
                </div>

                <div class="col-12 col-md-6">
                  <div class="info-card">
                    <h3 class="info-label">Studio</h3>
                    <p class="info-value mb-0">{{ movie.studio || 'Unknown' }}</p>
                  </div>
                </div>

                <div class="col-12 col-md-6">
                  <div class="info-card">
                    <h3 class="info-label">Language</h3>
                    <p class="info-value mb-0">{{ movie.language || 'Unknown' }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.single-movie-page {
  --movie-bg: none;
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background: #05030a;
}

.background-layer {
  position: absolute;
  inset: 0;
  background-image: var(--movie-bg);
  background-size: cover;
  background-position: center;
  filter: blur(22px) scale(1.08);
  transform: scale(1.06);
  opacity: 0.45;
}

.background-overlay {
  position: absolute;
  inset: 0;
  background:
      linear-gradient(180deg, rgba(5, 3, 10, 0.35), rgba(5, 3, 10, 0.88)),
      radial-gradient(circle at top left, rgba(139, 92, 246, 0.22), transparent 30%),
      radial-gradient(circle at top right, rgba(168, 85, 247, 0.16), transparent 25%);
}

.movie-details-panel {
  position: relative;
  z-index: 2;
  background: rgba(12, 8, 24, 0.68);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 28px;
  backdrop-filter: blur(14px);
  padding: 1.5rem;
  box-shadow:
      0 20px 60px rgba(0, 0, 0, 0.45),
      0 0 40px rgba(139, 92, 246, 0.12);
}

.poster-wrap {
  position: relative;
  overflow: hidden;
  border-radius: 24px;
  box-shadow:
      0 18px 50px rgba(0, 0, 0, 0.45),
      0 0 28px rgba(139, 92, 246, 0.18);
}

.poster-image {
  width: 100%;
  display: block;
  aspect-ratio: 2 / 3;
  object-fit: cover;
}

.movie-copy {
  color: white;
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.16em;
  font-size: 0.78rem;
  color: #c4b5fd;
  font-weight: 700;
}

.movie-heading {
  font-size: clamp(2.2rem, 4vw, 4.4rem);
  font-weight: 800;
  line-height: 1;
  color: #fff;
}

.meta-row {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.meta-pill {
  padding: 0.5rem 0.85rem;
  border-radius: 999px;
  background: rgba(139, 92, 246, 0.16);
  border: 1px solid rgba(196, 181, 253, 0.22);
  color: #f5f3ff;
  font-weight: 600;
}

.movie-description {
  font-size: 1rem;
  line-height: 1.75;
  color: rgba(255, 255, 255, 0.88);
  max-width: 60ch;
}

.watchlist-actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 0.8rem;
}

.watchlist-btn {
  border: 1px solid rgba(196, 181, 253, 0.35);
  background: rgba(255, 255, 255, 0.08);
  color: #fff;
  border-radius: 12px;
  padding: 0.7rem 1rem;
  font-weight: 700;
}

.watchlist-btn:hover {
  background: rgba(139, 92, 246, 0.22);
  color: #fff;
}

.watchlist-btn.saved {
  background: rgba(139, 92, 246, 0.82);
  border-color: rgba(196, 181, 253, 0.62);
}

.watchlist-btn:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.watchlist-message {
  color: rgba(255, 255, 255, 0.75);
  font-size: 0.92rem;
}

.section-label {
  font-size: 0.95rem;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #ddd6fe;
  font-weight: 700;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.65rem;
}

.tag {
  padding: 0.45rem 0.8rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #fff;
  font-size: 0.9rem;
}

.info-card {
  height: 100%;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 18px;
  padding: 1rem 1rem 0.95rem;
}

.info-label {
  font-size: 0.82rem;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #c4b5fd;
  margin-bottom: 0.45rem;
}

.info-value {
  color: #fff;
  font-size: 1rem;
  font-weight: 600;
}

.back-btn {
  position: relative;
  z-index: 2;
  border: 1px solid rgba(196, 181, 253, 0.25);
  background: rgba(20, 14, 35, 0.75);
  color: white;
  border-radius: 999px;
  padding: 0.65rem 1rem;
  backdrop-filter: blur(10px);
}

.back-btn:hover {
  background: rgba(40, 24, 74, 0.85);
  color: white;
}

.state-box {
  position: relative;
  z-index: 2;
  background: rgba(12, 8, 24, 0.68);
  border-radius: 24px;
  padding: 3rem 1rem;
  backdrop-filter: blur(12px);
}
</style>
