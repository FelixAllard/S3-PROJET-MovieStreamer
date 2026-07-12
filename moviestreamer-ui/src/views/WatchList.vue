<script setup>
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap'

import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { animate } from 'motion-v'
import apiClient from '/src/services/ApiClient.js'
import { Movie } from '/src/entities/Movie.js'
import { resolveDbUserId } from '/src/utils/auth.js'

const router = useRouter()

const movies = ref([])
const loading = ref(true)
const errorMessage = ref('')
const pageRef = ref(null)
const cardRefs = ref([])

onMounted(async () => {
  await fetchWatchlist()

  if (pageRef.value) {
    animate(
        pageRef.value,
        { opacity: [0, 1] },
        { duration: 0.45, easing: 'ease-out' }
    )
  }
})

async function fetchWatchlist() {
  try {
    loading.value = true
    errorMessage.value = ''

    const userId = await resolveDbUserId(apiClient)
    if (!userId) {
      errorMessage.value = 'Unable to load your account.'
      return
    }

    const data = await apiClient.get(`/watch-history/saved/${userId}/movies`)
    movies.value = Array.isArray(data) ? data.map(movie => new Movie(movie)) : []

    await nextTick()
    cardRefs.value.forEach((card, index) => {
      if (!card) return

      animate(
          card,
          { opacity: [0, 1], y: [24, 0] },
          { duration: 0.35, delay: index * 0.04, easing: 'ease-out' }
      )
    })
  } catch (err) {
    console.error('Failed to fetch watchlist:', err)
    errorMessage.value = 'Failed to load your watchlist.'
  } finally {
    loading.value = false
  }
}

function openMovie(movie) {
  router.push(`/movies/${movie.id}`)
}
</script>

<template>
  <div ref="pageRef" class="watchlist-page">
    <div class="container-fluid px-3 px-md-4 py-4">
      <div class="watchlist-header mb-4">
        <p class="eyebrow mb-2">Personal Collection</p>
        <h1 class="watchlist-heading mb-2">Your Watchlist</h1>
        <p class="watchlist-subtitle mb-0">Movies you saved for later.</p>
      </div>

      <div v-if="loading" class="state-box text-center">
        Loading your watchlist...
      </div>

      <div v-else-if="errorMessage" class="state-box text-center">
        <h2 class="mb-2">Something went wrong</h2>
        <p class="mb-0">{{ errorMessage }}</p>
      </div>

      <div v-else-if="movies.length === 0" class="state-box text-center">
        <h2 class="mb-2">Your watchlist is empty</h2>
        <p class="mb-4">Save a movie from its detail page to see it here.</p>
        <button class="btn browse-btn" @click="router.push('/movies')">
          Browse Movies
        </button>
      </div>

      <div v-else class="row g-4">
        <div
            v-for="(movie, index) in movies"
            :key="movie.id"
            class="col-6 col-sm-4 col-lg-3"
        >
          <button
              :ref="el => cardRefs[index] = el"
              class="watchlist-card"
              @click="openMovie(movie)"
          >
            <img
                :src="movie.thumbnail"
                :alt="movie.title"
                class="poster"
            />
            <span class="card-overlay"></span>
            <span class="card-copy">
              <span class="movie-title">{{ movie.title }}</span>
              <span class="movie-meta">{{ movie.year || 'Unknown year' }}</span>
            </span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.watchlist-page {
  min-height: 100vh;
  background:
      radial-gradient(circle at top left, rgba(139, 92, 246, 0.18), transparent 28%),
      linear-gradient(180deg, #080512, #12091f 55%, #05030a);
  color: #fff;
}

.watchlist-header {
  max-width: 760px;
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.16em;
  font-size: 0.78rem;
  color: #c4b5fd;
  font-weight: 700;
}

.watchlist-heading {
  font-size: clamp(2.2rem, 4vw, 4rem);
  font-weight: 800;
  line-height: 1;
}

.watchlist-subtitle,
.state-box p {
  color: rgba(255, 255, 255, 0.72);
}

.state-box {
  background: rgba(12, 8, 24, 0.72);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 24px;
  padding: 3rem 1rem;
  backdrop-filter: blur(12px);
}

.browse-btn {
  background: rgba(139, 92, 246, 0.82);
  color: #fff;
  border: 1px solid rgba(196, 181, 253, 0.42);
  border-radius: 12px;
  padding: 0.75rem 1.1rem;
  font-weight: 700;
}

.watchlist-card {
  position: relative;
  display: block;
  width: 100%;
  aspect-ratio: 2 / 3;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 18px;
  padding: 0;
  background: rgba(255, 255, 255, 0.06);
  box-shadow: 0 16px 34px rgba(0, 0, 0, 0.35);
  cursor: pointer;
  text-align: left;
}

.watchlist-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 46px rgba(0, 0, 0, 0.46), 0 0 24px rgba(139, 92, 246, 0.18);
}

.poster {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.25s ease;
}

.watchlist-card:hover .poster {
  transform: scale(1.06);
}

.card-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, transparent 48%, rgba(0, 0, 0, 0.88));
}

.card-copy {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  display: grid;
  gap: 0.25rem;
  padding: 1rem;
}

.movie-title {
  color: #fff;
  font-weight: 800;
  font-size: 1rem;
  line-height: 1.15;
}

.movie-meta {
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.86rem;
  font-weight: 600;
}
</style>
