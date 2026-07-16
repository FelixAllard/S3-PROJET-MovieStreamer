<script setup>
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap'
import { useRouter } from 'vue-router'


import { ref, onMounted, computed, nextTick } from 'vue'
import { animate } from 'motion-v'
import apiClient from '/src/services/ApiClient.js'
import { Movie } from '/src/entities/Movie.js'

const router = useRouter()
const movies = ref([])
const allMovies = ref([])
const cardRefs = ref([])
const textRefs = ref([])
const imageRefs = ref([])
const bgRef = ref(null)
const resultsPanelRef = ref(null)

const searchQuery = ref('')
const loading = ref(false)
const errorMessage = ref('')
const isSearching = ref(false)

const searchFxActive = ref(false)
const searchButtonPressed = ref(false)
const resultsPulse = ref(false)

const transitioningCard = ref(null)

const deleteModalMovie = ref(null)
const deleteLoading = ref(false)

const isAdmin = ref(false)

function parseTokenData() {
  const token = localStorage.getItem('token')
  if (!token) { isAdmin.value = false; return }
  try {
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(
        atob(base64).split('').map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)).join('')
    )
    const payload = JSON.parse(jsonPayload)
    isAdmin.value = payload.realm_access?.roles?.includes('admin') || false
  } catch (error) {
    console.error('Invalid token:', error)
    isAdmin.value = false
  }
}

onMounted(async () => {
  parseTokenData()
  await fetchAllMovies()
  await fetchAllTags()
  animate(
      bgRef.value,
      { backgroundPosition: ['0% 50%', '100% 50%', '0% 50%'] },
      { duration: 8, repeat: Infinity, easing: 'linear' }
  )
})

async function fetchAllMovies() {
  try {
    loading.value = true
    errorMessage.value = ''

    const data = await apiClient.get('/movie/all')
    const mapped = Array.isArray(data) ? data.map(m => new Movie(m)) : []

    allMovies.value = mapped
    movies.value = mapped
  } catch (err) {
    console.error('Failed to fetch movies:', err)
    errorMessage.value = 'Failed to load movies.'
  } finally {
    loading.value = false
  }
}

function normalizeMovieResponse(data) {
  if (Array.isArray(data)) return data.map(m => new Movie(m))
  if (data && typeof data === 'object') return [new Movie(data)]
  return []
}


function triggerSearchImpact() {
  searchFxActive.value = false
  resultsPulse.value = false

  requestAnimationFrame(() => {
    searchFxActive.value = true
    resultsPulse.value = true
    setTimeout(() => { searchFxActive.value = false }, 1100)
    setTimeout(() => { resultsPulse.value = false }, 900)
  })
}

async function searchMovies() {
  const query = searchQuery.value.trim()

  searchButtonPressed.value = true
  setTimeout(() => { searchButtonPressed.value = false }, 180)
  triggerSearchImpact()

  if (!query) { clearSearch(); return }

  try {
    loading.value = true
    isSearching.value = true
    errorMessage.value = ''

    const data = await apiClient.get(`/movie/partial-title/${encodeURIComponent(query)}`)
    movies.value = normalizeMovieResponse(data)

    if (movies.value.length === 0)
      errorMessage.value = 'No movies found for that search.'

  } catch (err) {
    console.error('Search failed:', err)
    errorMessage.value = 'Search failed. Please try again.'
  } finally {
    loading.value = false

    await nextTick()

    if (resultsPanelRef.value) {
      animate(
          resultsPanelRef.value,
          { boxShadow: ['0 0 0 rgba(139,92,246,0)', '0 0 35px rgba(139,92,246,0.28)', '0 0 0 rgba(139,92,246,0)'] },
          { duration: 0.8, easing: 'ease-out' }
      )
    }
  }
}

function clearSearch() {
  searchQuery.value = ''
  errorMessage.value = ''
  isSearching.value = false
  movies.value = allMovies.value
}

const movieCountLabel = computed(() => {
  if (loading.value) return 'Loading...'
  if (isSearching.value && searchQuery.value.trim()) return `${movies.value.length} search result(s)`
  return `${movies.value.length} movie(s)`
})

function onEnter(index) {
  const card = cardRefs.value[index]
  const text = textRefs.value[index]
  const image = imageRefs.value[index]

  if (!card || !text || !image) return
  animate(card, { scale: 1.11, y: -10, rotateZ: [-0.4, 0.4, 0] }, { duration: 0.32, easing: 'ease-out' })
  animate(image, { scale: 1.14 }, { duration: 0.45, easing: 'ease-out' })
  animate(card.querySelector('.overlay'), { opacity: 0.62 }, { duration: 0.25 })
  animate(text, { opacity: 1, y: 0 }, { duration: 0.32, easing: 'ease-out' })
  animate(card, {
    boxShadow: ['0 12px 30px rgba(0,0,0,0.28)', '0 22px 60px rgba(0,0,0,0.48), 0 0 28px rgba(139,92,246,0.32)']
  }, { duration: 0.28, easing: 'ease-out' })
}

async function openMovie(movie, index) {
  const card = cardRefs.value[index]
  const image = imageRefs.value[index]
  const text = textRefs.value[index]
  if (!card || !image) { router.push(`/movies/${movie.id}`); return }
  transitioningCard.value = movie.id
  animate(card, { scale: 1.22, y: -8 }, { duration: 0.22, easing: 'ease-out' })
  animate(image, { scale: 1.28, filter: ['blur(0px) contrast(1)', 'blur(1px) contrast(1.15)'] }, { duration: 0.22, easing: 'ease-out' })
  if (text) animate(text, { opacity: 0, y: 20 }, { duration: 0.18, easing: 'ease-out' })
  setTimeout(() => { router.push(`/movies/${movie.id}`) }, 260)
}

function onLeave(index) {
  const card = cardRefs.value[index]
  const text = textRefs.value[index]
  const image = imageRefs.value[index]
  if (!card || !text || !image) return
  animate(card, { scale: 1, y: 0, rotateZ: 0 }, { duration: 0.25, easing: 'ease-out' })
  animate(image, { scale: 1 }, { duration: 0.35, easing: 'ease-out' })
  animate(card.querySelector('.overlay'), { opacity: 0 }, { duration: 0.22 })
  animate(text, { opacity: 0, y: 28 }, { duration: 0.2 })
  animate(card, { boxShadow: '0 12px 30px rgba(0,0,0,0.28)' }, { duration: 0.25, easing: 'ease-out' })
}

// ── Admin actions ──────────────────────────────────────────
function navigateToCreate() {
  router.push('/movies/create')
}

function editMovie(movie, event) {
  event.stopPropagation()
  router.push(`/movies/${movie.id}/edit`)
}

function openDeleteModal(movie, event) {
  event.stopPropagation()
  deleteModalMovie.value = movie
}

function closeDeleteModal() {
  deleteModalMovie.value = null
}

async function confirmDelete() {
  if (!deleteModalMovie.value) return
  try {
    deleteLoading.value = true
    await apiClient.delete(`/movie/${deleteModalMovie.value.id}`)
    const id = deleteModalMovie.value.id
    allMovies.value = allMovies.value.filter(m => m.id !== id)
    movies.value = movies.value.filter(m => m.id !== id)
    closeDeleteModal()
  } catch (err) {
    console.error('Failed to delete movie:', err)
  } finally {
    deleteLoading.value = false
  }
}

// Advanced search
const showAdvancedSearch = ref(false)
const advancedFilters = ref({
  tags: [],
  yearMin: '',
  yearMax: '',
  language: '',
  director: '',
  studio: '',
  writer: '',
  title: ''
})

// Récupérer tous les tags disponibles
const allTags = ref([])



async function fetchAllTags() {
  try {
    const data = await apiClient.get('/tag/all')
    allTags.value = Array.isArray(data) ? data : []
  } catch (err) {
    console.error('Failed to fetch tags:', err)
  }
}

function toggleTag(tag) {
  const index = advancedFilters.value.tags.findIndex(t => t.id === tag.id)
  if (index === -1) {
    advancedFilters.value.tags.push(tag)
  } else {
    advancedFilters.value.tags.splice(index, 1)
  }
}

function isTagSelected(tag) {
  return advancedFilters.value.tags.some(t => t.id === tag.id)
}

async function applyAdvancedSearch() {
  try {
    loading.value = true
    errorMessage.value = ''
    isSearching.value = true

    const params = new URLSearchParams()

    if (advancedFilters.value.tags.length > 0)
      advancedFilters.value.tags.forEach(t => params.append('tags', t.id))
    if (advancedFilters.value.yearMin)
      params.append('yearMin', advancedFilters.value.yearMin)
    if (advancedFilters.value.yearMax)
      params.append('yearMax', advancedFilters.value.yearMax)
    if (advancedFilters.value.language)
      params.append('language', advancedFilters.value.language)
    if (advancedFilters.value.director)
      params.append('director', advancedFilters.value.director)
    if (advancedFilters.value.studio)
      params.append('studio', advancedFilters.value.studio)
    if (advancedFilters.value.writer)
      params.append('writer', advancedFilters.value.writer)
    if (advancedFilters.value.title)
      params.append('title', advancedFilters.value.title)

    const data = await apiClient.get(`/movie/search?${params.toString()}`)
    movies.value = Array.isArray(data) ? data.map(m => new Movie(m)) : []

    if (movies.value.length === 0)
      errorMessage.value = 'No movies found for those filters.'

  } catch (err) {
    console.error('Advanced search failed:', err)
    errorMessage.value = 'Search failed. Please try again.'
  } finally {
    loading.value = false
  }
}

function clearAdvancedSearch() {
  advancedFilters.value = {
    tags: [],
    yearMin: '',
    yearMax: '',
    language: '',
    director: '',
    studio: '',
    writer: ''
  }
  movies.value = allMovies.value
  isSearching.value = false
}
</script>

<template>
  <div ref="bgRef" class="page-bg">
    <div class="container-fluid px-3 px-md-4 py-4">
      <div class="row g-4 align-items-start">
        <div class="col-12 col-lg-9">
          <div
              ref="resultsPanelRef"
              class="content-panel p-3 p-md-4 h-100 position-relative overflow-hidden"
              :class="{ 'results-pulse': resultsPulse }"
          >
            <div v-if="searchFxActive" class="scan-line"></div>
            <div v-if="searchFxActive" class="impact-ring"></div>

            <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2 position-relative z-2">
              <h1 class="section-title display-6 fw-bold mb-0">Movies</h1>
              <div class="d-flex align-items-center gap-3">
                <span class="movie-count">{{ movieCountLabel }}</span>
                <button v-if="isAdmin" class="btn add-movie-btn" @click="navigateToCreate">
                  ＋ Add Movie
                </button>
              </div>
            </div>

            <div v-if="loading" class="text-white text-center py-5 position-relative z-2">
              Loading movies...
            </div>

            <div v-else-if="movies.length === 0" class="empty-state text-center position-relative z-2">
              <h4>No movies found</h4>
              <p class="mb-0">Try another movie name or clear the search.</p>
            </div>

            <div v-else class="row g-4 position-relative z-2">
              <div
                  v-for="(movie, index) in movies"
                  :key="movie.id"
                  class="col-6 col-sm-4 col-md-4 col-xl-3"
              >
                <div
                    :ref="el => cardRefs[index] = el"
                    class="movie-card"
                    :class="{ 'is-transitioning': transitioningCard === movie.id }"
                    @mouseenter="onEnter(index)"
                    @mouseleave="onLeave(index)"
                    @click="openMovie(movie, index)"
                >
                  <img
                      :ref="el => imageRefs[index] = el"
                      class="image"
                      :src="movie.thumbnail"
                      :alt="movie.title"
                  />
                  <div class="overlay"></div>

                  <div :ref="el => textRefs[index] = el" class="text p-3 p-sm-3 p-md-4">
                    <h2 class="movie-title mb-1">{{ movie.title }}</h2>
                    <p class="movie-description mb-0">{{ movie.description }}</p>

                    <div class="tags mt-2">
                      <span v-for="tag in movie.tags" :key="tag.id" class="tag">
                        {{ tag.name }}
                      </span>
                    </div>

                    <!-- Admin buttons (only visible to admins) -->
                    <div v-if="isAdmin" class="admin-actions mt-2 d-flex gap-2" @click.stop>
                      <button class="btn admin-btn edit-btn" @click="editMovie(movie, $event)" title="Edit movie">
                        ✏️ Edit
                      </button>
                      <button class="btn admin-btn delete-btn" @click="openDeleteModal(movie, $event)" title="Delete movie">
                        🗑️ Delete
                      </button>
                    </div>
                  </div>

                  <div class="card-shine"></div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="col-12 col-lg-3">
          <div class="search-panel p-3 p-md-4 h-100 position-relative overflow-hidden">
            <div class="search-panel-glow"></div>

            <h2 class="search-title fs-2 fw-bold text-center position-relative z-2">Search</h2>

            <p class="search-subtitle mb-3 text-center position-relative z-2">
              Search by movie name now, with support for future backend improvements.
            </p>

            <div class="mb-3 position-relative z-2">
              <label for="movieSearch" class="form-label text-white">Movie name</label>
              <input
                  id="movieSearch"
                  v-model="searchQuery"
                  type="text"
                  class="form-control search-input"
                  placeholder="Enter a movie name"
                  @keyup.enter="searchMovies"
              />
            </div>

            <div class="d-grid gap-2 position-relative z-2">
              <button
                  class="btn movie-search-btn"
                  :class="{ 'pressed': searchButtonPressed }"
                  @click="searchMovies"
                  :disabled="loading"
              >
                <span class="btn-energy"></span>
                <span class="btn-label">Search</span>
              </button>
              <button class="btn btn-outline-light clear-btn" @click="clearSearch" :disabled="loading && !searchQuery">
                Clear Search
              </button>

              <!-- Bouton Advanced Search -->
              <button
                  class="btn advanced-search-btn mt-1"
                  @click="showAdvancedSearch = !showAdvancedSearch"
                  :disabled="loading"
              >
                {{ showAdvancedSearch ? 'Hide Advanced Search' : 'Advanced Search' }}
              </button>

              <!-- Panneau Advanced Search -->
              <div v-if="showAdvancedSearch" class="advanced-panel mt-3 position-relative z-2">

                <!-- Tags -->
                <div class="mb-3">
                  <label class="form-label text-white">Tags</label>
                  <div class="tags-grid">
            <span
                v-for="tag in allTags"
                :key="tag.id"
                class="tag-filter"
                :class="{ 'tag-selected': isTagSelected(tag) }"
                @click="toggleTag(tag)"
            >
                {{ tag.name }}
            </span>
                  </div>
                </div>

                <div class="mb-3">
                  <label class="form-label text-white">Title</label>
                  <input
                      v-model="advancedFilters.title"
                      type="text"
                      class="form-control search-input"
                      placeholder="e.g. Inter"
                  />
                </div>

                <!-- Année -->
                <div class="mb-3">
                  <label class="form-label text-white">Year</label>
                  <div class="d-flex gap-2">
                    <input
                        v-model="advancedFilters.yearMin"
                        type="number"
                        class="form-control search-input"
                        placeholder="From"
                    />
                    <input
                        v-model="advancedFilters.yearMax"
                        type="number"
                        class="form-control search-input"
                        placeholder="To"
                    />
                  </div>
                </div>

                <!-- Language -->
                <div class="mb-3">
                  <label class="form-label text-white">Language</label>
                  <input
                      v-model="advancedFilters.language"
                      type="text"
                      class="form-control search-input"
                      placeholder="e.g. English"
                  />
                </div>

                <!-- Director -->
                <div class="mb-3">
                  <label class="form-label text-white">Director</label>
                  <input
                      v-model="advancedFilters.director"
                      type="text"
                      class="form-control search-input"
                      placeholder="e.g. Christopher Nolan"
                  />
                </div>

                <!-- Studio -->
                <div class="mb-3">
                  <label class="form-label text-white">Studio</label>
                  <input
                      v-model="advancedFilters.studio"
                      type="text"
                      class="form-control search-input"
                      placeholder="e.g. Warner Bros"
                  />
                </div>

                <!-- Writer -->
                <div class="mb-3">
                  <label class="form-label text-white">Writer</label>
                  <input
                      v-model="advancedFilters.writer"
                      type="text"
                      class="form-control search-input"
                      placeholder="e.g. Jonathan Nolan"
                  />
                </div>

                <!-- Boutons -->
                <div class="d-grid gap-2">
                  <button class="btn movie-search-btn" @click="applyAdvancedSearch">
                    <span class="btn-energy"></span>
                    <span class="btn-label">Apply Filters</span>
                  </button>
                  <button class="btn btn-outline-light clear-btn" @click="clearAdvancedSearch">
                    Clear Filters
                  </button>
                </div>
              </div>
            </div>

            <div v-if="searchQuery" class="search-hint mt-3 position-relative z-2">
              Current search: <strong>{{ searchQuery }}</strong>
            </div>

            <div v-if="errorMessage" class="alert alert-warning mt-3 mb-0 position-relative z-2">
              {{ errorMessage }}
            </div>

            <div v-if="searchFxActive" class="search-beam"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <Transition name="modal-fade">
      <div v-if="deleteModalMovie" class="delete-modal-backdrop" @click.self="closeDeleteModal">
        <div class="delete-modal">
          <div class="delete-modal-icon">🗑️</div>
          <h3 class="delete-modal-title">Delete Movie</h3>
          <p class="delete-modal-body">
            Are you sure you want to delete <strong>{{ deleteModalMovie.title }}</strong>?
            This action cannot be undone.
          </p>
          <div class="delete-modal-actions">
            <button class="btn delete-cancel-btn" @click="closeDeleteModal" :disabled="deleteLoading">
              Cancel
            </button>
            <button class="btn delete-confirm-btn" @click="confirmDelete" :disabled="deleteLoading">
              {{ deleteLoading ? 'Deleting...' : 'Yes, Delete' }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<style scoped>
.page-bg {
  min-height: 100vh;
  background: linear-gradient(270deg, #0b0718, #24163d, #16152c, #1a1a2e, #120f22);
  background-size: 400% 400%;
}

.content-panel,
.search-panel {
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 24px;
  backdrop-filter: blur(12px);
}

.content-panel { transition: box-shadow 0.45s ease; }

.section-title, .search-title { color: #fff; }

.movie-count, .search-subtitle, .search-hint, .empty-state h4, .empty-state p {
  color: rgba(255, 255, 255, 0.88);
}

.search-panel {
  box-shadow: 0 12px 30px rgba(0,0,0,0.22), inset 0 0 0 1px rgba(255,255,255,0.02);
}

.search-panel-glow {
  position: absolute;
  inset: -20% -10% auto -10%;
  height: 180px;
  background: radial-gradient(circle, rgba(139,92,246,0.25), transparent 70%);
  filter: blur(20px);
  pointer-events: none;
}

.search-input {
  border-radius: 14px;
  padding: 0.95rem 1rem;
  border: none;
  background: rgba(255, 255, 255, 0.95);
  transition: transform 0.2s ease, box-shadow 0.25s ease;
}

.search-input:focus {
  box-shadow: 0 0 0 0.25rem rgba(139,92,246,0.28);
  transform: scale(1.01);
}

.movie-search-btn {
  position: relative;
  overflow: hidden;
  border: none;
  border-radius: 14px;
  padding: 0.9rem 1rem;
  color: #fff;
  font-weight: 700;
  background: linear-gradient(90deg, #5b21b6, #7c3aed, #9333ea);
  box-shadow: 0 10px 24px rgba(91,33,182,0.35), 0 0 18px rgba(139,92,246,0.2);
  transition: transform 0.16s ease, box-shadow 0.22s ease;
}

.movie-search-btn:hover {
  transform: translateY(-2px) scale(1.015);
  box-shadow: 0 14px 28px rgba(91,33,182,0.42), 0 0 24px rgba(139,92,246,0.3);
}

.movie-search-btn.pressed { transform: scale(0.96); }

.btn-energy {
  position: absolute;
  inset: 0;
  background: linear-gradient(120deg, transparent 10%, rgba(255,255,255,0.25) 45%, transparent 80%);
  transform: translateX(-120%);
  animation: buttonSweep 2.4s linear infinite;
}

.btn-label { position: relative; z-index: 1; }
.clear-btn { border-radius: 14px; }

.movie-card {
  position: relative;
  width: 100%;
  aspect-ratio: 2 / 3;
  overflow: hidden;
  border-radius: 18px;
  cursor: pointer;
  transform-origin: center;
  background: #111;
  box-shadow: 0 12px 30px rgba(0,0,0,0.28);
  transition: z-index 0.2s ease;
}

.movie-card:hover { z-index: 3; }

.image { width: 100%; height: 100%; object-fit: cover; transform: scale(1); }

.overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(0,0,0,0.9), rgba(0,0,0,0.2) 45%, rgba(0,0,0,0.05));
  opacity: 0;
}

.text {
  position: absolute;
  inset: auto 0 0 0;
  color: white;
  opacity: 0;
  transform: translateY(20px);
}

.movie-title { font-size: 1.08rem; font-weight: 800; text-shadow: 0 2px 12px rgba(0,0,0,0.4); }

.movie-description {
  font-size: 0.78rem;
  opacity: 0.88;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.tags { display: flex; flex-wrap: wrap; gap: 6px; }

.tag {
  background: rgba(255,255,255,0.16);
  border: 1px solid rgba(255,255,255,0.1);
  backdrop-filter: blur(6px);
  border-radius: 999px;
  padding: 4px 10px;
  font-size: 11px;
}

/* ── Admin action buttons ── */
.admin-actions {
  position: relative;
  z-index: 10;
}

.admin-btn {
  font-size: 0.7rem;
  padding: 4px 10px;
  border-radius: 999px;
  font-weight: 600;
  border: none;
  backdrop-filter: blur(6px);
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.admin-btn:hover { transform: scale(1.06); }

.edit-btn {
  background: rgba(99,179,237,0.25);
  border: 1px solid rgba(99,179,237,0.4);
  color: #bee3f8;
}

.edit-btn:hover { background: rgba(99,179,237,0.4); }

.delete-btn {
  background: rgba(252,129,129,0.22);
  border: 1px solid rgba(252,129,129,0.4);
  color: #fed7d7;
}

.delete-btn:hover { background: rgba(252,129,129,0.38); }

/* ── Delete confirmation modal ── */
.delete-modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.72);
  backdrop-filter: blur(6px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.delete-modal {
  background: linear-gradient(145deg, #1e1535, #2a1f4a);
  border: 1px solid rgba(255,255,255,0.14);
  border-radius: 24px;
  padding: 2.5rem 2rem;
  max-width: 420px;
  width: 90%;
  text-align: center;
  box-shadow: 0 30px 80px rgba(0,0,0,0.6), 0 0 40px rgba(139,92,246,0.18);
}

.delete-modal-icon { font-size: 2.8rem; margin-bottom: 0.75rem; }
.delete-modal-title { color: #fff; font-weight: 800; margin-bottom: 0.75rem; }
.delete-modal-body { color: rgba(255,255,255,0.78); font-size: 0.95rem; margin-bottom: 1.75rem; }

.delete-modal-actions { display: flex; gap: 12px; justify-content: center; }

.delete-cancel-btn {
  border-radius: 14px;
  padding: 0.65rem 1.4rem;
  background: rgba(255,255,255,0.1);
  border: 1px solid rgba(255,255,255,0.2);
  color: #fff;
  font-weight: 600;
  transition: background 0.2s ease;
}

.delete-cancel-btn:hover { background: rgba(255,255,255,0.18); }

.delete-confirm-btn {
  border-radius: 14px;
  padding: 0.65rem 1.4rem;
  background: linear-gradient(90deg, #b91c1c, #dc2626);
  border: none;
  color: #fff;
  font-weight: 700;
  box-shadow: 0 8px 20px rgba(185,28,28,0.4);
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.delete-confirm-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 12px 24px rgba(185,28,28,0.5);
}

.delete-confirm-btn:disabled { opacity: 0.65; }

/* Modal transition */
.modal-fade-enter-active, .modal-fade-leave-active { transition: opacity 0.25s ease; }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; }

/* ── Card effects ── */
.card-shine {
  position: absolute;
  inset: 0;
  background: linear-gradient(120deg, transparent 25%, rgba(255,255,255,0.18) 50%, transparent 75%);
  transform: translateX(-140%);
  opacity: 0;
  pointer-events: none;
}

.movie-card:hover .card-shine { opacity: 1; animation: shinePass 0.9s ease; }

.empty-state {
  padding: 3rem 1rem;
  border-radius: 16px;
  background: rgba(255,255,255,0.05);
}

.search-beam {
  position: absolute;
  top: 55%; left: -10%;
  width: 140%; height: 2px;
  background: linear-gradient(90deg, transparent, rgba(192,132,252,0.95), transparent);
  box-shadow: 0 0 14px rgba(192,132,252,0.75);
  transform: rotate(-8deg);
  animation: beamBlast 0.8s ease-out forwards;
  pointer-events: none;
}

.scan-line {
  position: absolute;
  top: 0; left: -20%;
  width: 40%; height: 100%;
  background: linear-gradient(90deg, transparent, rgba(139,92,246,0.18), transparent);
  filter: blur(4px);
  animation: scanAcross 0.9s ease-out forwards;
  pointer-events: none;
}

.impact-ring {
  position: absolute;
  top: 50%; left: 50%;
  width: 120px; height: 120px;
  border: 2px solid rgba(192,132,252,0.45);
  border-radius: 999px;
  transform: translate(-50%, -50%);
  animation: ringPulse 0.8s ease-out forwards;
  pointer-events: none;
}

.results-pulse::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at center, rgba(139,92,246,0.14), transparent 60%);
  animation: resultsPulse 0.7s ease-out;
  pointer-events: none;
}

@keyframes shinePass {
  0% {
    transform: translateX(-140%);
  }
  100% {
    transform: translateX(140%);
  }
}

@keyframes buttonSweep {
  0% {
    transform: translateX(-120%);
  }
  100% {
    transform: translateX(120%);
  }
}

@keyframes beamBlast {
  0% {
    opacity: 0;
    transform: translateX(-40px) rotate(-8deg) scaleX(0.4);
  }
  35% {
    opacity: 1;
  }
  100% {
    opacity: 0;
    transform: translateX(40px) rotate(-8deg) scaleX(1.2);
  }
}

@keyframes scanAcross {
  0% {
    transform: translateX(0);
    opacity: 0;
  }
  15% {
    opacity: 1;
  }
  100% {
    transform: translateX(320%);
    opacity: 0;
  }
}

@keyframes ringPulse {
  0% {
    opacity: 0.7;
    transform: translate(-50%, -50%) scale(0.35);
  }
  100% {
    opacity: 0;
    transform: translate(-50%, -50%) scale(5);
  }
}

@keyframes resultsPulse {
  0% {
    opacity: 0.9;
  }
  100% {
    opacity: 0;
  }
}
.movie-card {
  position: relative;
}

.movie-card::after {
  content: '';
  position: absolute;
  inset: 0;
  opacity: 0;
  pointer-events: none;
  background-image:
      linear-gradient(rgba(255,255,255,0.08) 1px, transparent 1px),
      linear-gradient(90deg, rgba(255,255,255,0.08) 1px, transparent 1px);
  background-size: 10px 10px;
  mix-blend-mode: overlay;
  transition: opacity 0.18s ease;
}

.movie-card.is-transitioning::after { opacity: 1; animation: pixelFlash 0.25s ease-out forwards; }
.movie-card.is-transitioning .image { image-rendering: pixelated; }

.add-movie-btn {
  background: linear-gradient(90deg, #5b21b6, #7c3aed, #9333ea);
  border: none;
  border-radius: 999px;
  padding: 0.5rem 1.1rem;
  color: #fff;
  font-weight: 700;
  font-size: 0.88rem;
  box-shadow: 0 6px 18px rgba(91, 33, 182, 0.35);
  transition: transform 0.15s ease, box-shadow 0.2s ease;
}

.add-movie-btn:hover {
  color: #fff;
  transform: translateY(-2px);
  box-shadow: 0 10px 22px rgba(91, 33, 182, 0.45);
}


@keyframes shinePass {
  0% { transform: translateX(-140%); }
  100% { transform: translateX(140%); }
}
@keyframes buttonSweep {
  0% { transform: translateX(-120%); }
  100% { transform: translateX(120%); }
}
@keyframes beamBlast {
  0% { opacity: 0; transform: translateX(-40px) rotate(-8deg) scaleX(0.4); }
  35% { opacity: 1; }
  100% { opacity: 0; transform: translateX(40px) rotate(-8deg) scaleX(1.2); }
}
@keyframes scanAcross {
  0% { transform: translateX(0); opacity: 0; }
  15% { opacity: 1; }
  100% { transform: translateX(320%); opacity: 0; }
}
@keyframes ringPulse {
  0% { opacity: 0.7; transform: translate(-50%, -50%) scale(0.35); }
  100% { opacity: 0; transform: translate(-50%, -50%) scale(5); }
}
@keyframes resultsPulse {
  0% { opacity: 0.9; }
  100% { opacity: 0; }
}
@keyframes pixelFlash {
  0% { opacity: 0; transform: scale(1); }
  40% { opacity: 0.8; transform: scale(1.02); }
  100% { opacity: 0; transform: scale(1.08); }
}

.advanced-search-btn {
  border-radius: 14px;
  border: 1px solid rgba(139, 92, 246, 0.5);
  color: rgba(255, 255, 255, 0.85);
  background: rgba(139, 92, 246, 0.12);
  transition: all 0.2s ease;
}

.advanced-search-btn:hover {
  background: rgba(139, 92, 246, 0.25);
  border-color: rgba(139, 92, 246, 0.8);
  color: white;
}

.advanced-panel {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 1rem;
}

.tags-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.tag-filter {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 999px;
  padding: 4px 12px;
  font-size: 12px;
  color: white;
  cursor: pointer;
  transition: all 0.18s ease;
}

.tag-filter:hover {
  background: rgba(139, 92, 246, 0.3);
  border-color: rgba(139, 92, 246, 0.6);
}

.tag-selected {
  background: rgba(139, 92, 246, 0.55);
  border-color: rgba(139, 92, 246, 0.9);
  box-shadow: 0 0 10px rgba(139, 92, 246, 0.35);
}
</style>
