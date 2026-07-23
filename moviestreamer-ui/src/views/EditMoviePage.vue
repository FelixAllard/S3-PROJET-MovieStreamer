<script setup>
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap'

import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { animate } from 'motion-v'
import apiClient from '/src/services/ApiClient.js'
import { Movie } from '/src/entities/Movie.js'

const route = useRoute()
const router = useRouter()

const movie = ref(null)
const loading = ref(true)
const saving = ref(false)
const errorMessage = ref('')
const successMessage = ref('')
const pageRef = ref(null)
const contentRef = ref(null)
const allTags = ref([])

const movieId = computed(() => route.params.id)

// Editable form fields
const form = ref({
  title: '',
  description: '',
  director: '',
  writer: '',
  studio: '',
  language: '',
  year: '',
  movieLength: '',
  thumbnail: '',
  streamId: '',
  tagIds: []
})

onMounted(async () => {
  await Promise.all([fetchMovie(), fetchAllTags()])

  if (pageRef.value) {
    animate(pageRef.value, { opacity: [0, 1] }, { duration: 0.45, easing: 'ease-out' })
  }
  if (contentRef.value) {
    animate(contentRef.value, { opacity: [0, 1], y: [30, 0], scale: [0.98, 1] }, { duration: 0.55, easing: 'ease-out' })
  }
})

async function fetchMovie() {
  try {
    loading.value = true
    errorMessage.value = ''
    const data = await apiClient.get(`/movie/${movieId.value}`)
    movie.value = data ? new Movie(data) : null

    if (movie.value) {
      form.value = {
        title: movie.value.title || '',
        description: movie.value.description || '',
        director: movie.value.director || '',
        writer: movie.value.writer || '',
        studio: movie.value.studio || '',
        language: movie.value.language || '',
        year: movie.value.year || '',
        movieLength: movie.value.movieLength || '',
        thumbnail: movie.value.thumbnail || '',
        streamId: movie.value.streamId || '',
        tagIds: movie.value.tags?.map(t => t.id) || []
      }
    } else {
      errorMessage.value = 'Movie not found.'
    }
  } catch (err) {
    console.error('Failed to fetch movie:', err)
    errorMessage.value = 'Failed to load movie.'
  } finally {
    loading.value = false
  }
}

async function fetchAllTags() {
  try {
    const data = await apiClient.get('/tag/all')
    allTags.value = Array.isArray(data) ? data : []
  } catch (err) {
    console.warn('Failed to fetch tags:', err)
  }
}

function toggleTag(tagId) {
  const idx = form.value.tagIds.indexOf(tagId)
  if (idx === -1) {
    form.value.tagIds.push(tagId)
  } else {
    form.value.tagIds.splice(idx, 1)
  }
}

function isTagSelected(tagId) {
  return form.value.tagIds.includes(tagId)
}

async function saveMovie() {
  if (saving.value) return
  try {
    saving.value = true
    errorMessage.value = ''
    successMessage.value = ''

    const payload = {
      title: form.value.title,
      description: form.value.description,
      director: form.value.director,
      writer: form.value.writer,
      studio: form.value.studio,
      language: form.value.language,
      year: form.value.year ? Number(form.value.year) : null,
      movieLength: form.value.movieLength ? Number(form.value.movieLength) : null,
      thumbnail: form.value.thumbnail,
      streamId: form.value.streamId,
      // Pass the actual tag objects (or at least objects with an ID)
      tags: form.value.tagIds.map(id => ({ id: id }))
    }

    await apiClient.put(`/movie/${movieId.value}`, payload)
    successMessage.value = 'Movie updated successfully!'
  } catch (err) {
    console.error('Failed to update movie:', err)
    errorMessage.value = 'Failed to save changes. Please try again.'
  } finally {
    saving.value = false
  }
}

function goBack() {
  router.push(`/movies/${movieId.value}`)
}
</script>

<template>
  <div
      ref="pageRef"
      class="edit-movie-page"
      :style="form.thumbnail ? { '--movie-bg': `url(${form.thumbnail})` } : {}"
  >
    <div class="background-layer"></div>
    <div class="background-overlay"></div>

    <div class="container-fluid px-3 px-md-4 py-4 position-relative">
      <div class="mb-4">
        <button class="btn back-btn" @click="goBack">
          ← Back to Movie
        </button>
      </div>

      <div v-if="loading" class="state-box text-center text-white">
        Loading movie...
      </div>

      <div v-else-if="errorMessage && !movie" class="state-box text-center text-white">
        <h2 class="mb-2">Something went wrong</h2>
        <p class="mb-0">{{ errorMessage }}</p>
      </div>

      <div v-else-if="movie" ref="contentRef" class="edit-panel">

        <!-- Header -->
        <div class="edit-header mb-4">
          <p class="eyebrow mb-1">Admin · Edit Mode</p>
          <h1 class="edit-heading">Editing: {{ movie.title }}</h1>
        </div>

        <!-- Alerts -->
        <div v-if="successMessage" class="alert-success-custom mb-4">
          ✅ {{ successMessage }}
        </div>
        <div v-if="errorMessage" class="alert-error-custom mb-4">
          ⚠️ {{ errorMessage }}
        </div>

        <div class="row g-4">

          <!-- Poster preview -->
          <div class="col-12 col-lg-3">
            <div class="poster-wrap mb-3">
              <img
                  :src="form.thumbnail || movie.thumbnail"
                  :alt="form.title"
                  class="poster-image"
              />
            </div>
            <div class="form-group-dark">
              <label class="field-label">Thumbnail URL</label>
              <input
                  v-model="form.thumbnail"
                  type="text"
                  class="field-input"
                  placeholder="https://..."
              />
            </div>
          </div>

          <!-- Form fields -->
          <div class="col-12 col-lg-9">
            <div class="row g-3">

              <div class="col-12">
                <div class="form-group-dark">
                  <label class="field-label">Title</label>
                  <input v-model="form.title" type="text" class="field-input" placeholder="Movie title" />
                </div>
              </div>

              <div class="col-12">
                <div class="form-group-dark">
                  <label class="field-label">Description</label>
                  <textarea
                      v-model="form.description"
                      class="field-input field-textarea"
                      rows="4"
                      placeholder="Movie description..."
                  ></textarea>
                </div>
              </div>

              <div class="col-12 col-md-6">
                <div class="form-group-dark">
                  <label class="field-label">Director</label>
                  <input v-model="form.director" type="text" class="field-input" placeholder="Director name" />
                </div>
              </div>

              <div class="col-12 col-md-6">
                <div class="form-group-dark">
                  <label class="field-label">Writer</label>
                  <input v-model="form.writer" type="text" class="field-input" placeholder="Writer name" />
                </div>
              </div>

              <div class="col-12 col-md-4">
                <div class="form-group-dark">
                  <label class="field-label">Studio</label>
                  <input v-model="form.studio" type="text" class="field-input" placeholder="Studio name" />
                </div>
              </div>

              <div class="col-12 col-md-4">
                <div class="form-group-dark">
                  <label class="field-label">Language</label>
                  <input v-model="form.language" type="text" class="field-input" placeholder="e.g. English" />
                </div>
              </div>

              <div class="col-6 col-md-2">
                <div class="form-group-dark">
                  <label class="field-label">Year</label>
                  <input v-model="form.year" type="number" class="field-input" placeholder="2024" />
                </div>
              </div>

              <div class="col-6 col-md-2">
                <div class="form-group-dark">
                  <label class="field-label">Length (min)</label>
                  <input v-model="form.movieLength" type="number" class="field-input" placeholder="120" />
                </div>
              </div>

              <div class="col-12 col-md-6">
                <div class="form-group-dark">
                  <label class="field-label">Stream ID</label>
                  <input
                      v-model="form.streamId"
                      type="text"
                      class="field-input"
                      placeholder="Jellyfin Item ID / Stream ID"
                  />
                </div>
              </div>

              <!-- Tags -->
              <div class="col-12" v-if="allTags.length">
                <div class="form-group-dark">
                  <label class="field-label">Genres / Tags</label>
                  <div class="tags-selector">
                    <button
                        v-for="tag in allTags"
                        :key="tag.id"
                        type="button"
                        class="tag-toggle"
                        :class="{ active: isTagSelected(tag.id) }"
                        @click="toggleTag(tag.id)"
                    >
                      {{ tag.name }}
                    </button>
                  </div>
                </div>
              </div>

            </div>

            <!-- Actions -->
            <div class="d-flex gap-3 mt-4 flex-wrap">
              <button
                  class="btn save-btn"
                  :disabled="saving"
                  @click="saveMovie"
              >
                {{ saving ? 'Saving...' : '💾 Save Changes' }}
              </button>
              <button class="btn cancel-btn" @click="goBack">
                Cancel
              </button>
            </div>
          </div>

        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.edit-movie-page {
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
  opacity: 0.35;
}

.background-overlay {
  position: absolute;
  inset: 0;
  background:
      linear-gradient(180deg, rgba(5,3,10,0.45), rgba(5,3,10,0.92)),
      radial-gradient(circle at top left, rgba(139,92,246,0.22), transparent 30%),
      radial-gradient(circle at top right, rgba(168,85,247,0.16), transparent 25%);
}

.edit-panel {
  position: relative;
  z-index: 2;
  background: rgba(12, 8, 24, 0.72);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 28px;
  backdrop-filter: blur(14px);
  padding: 2rem;
  box-shadow: 0 20px 60px rgba(0,0,0,0.45), 0 0 40px rgba(139,92,246,0.12);
}

.edit-header { color: white; }

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.16em;
  font-size: 0.78rem;
  color: #c4b5fd;
  font-weight: 700;
}

.edit-heading {
  font-size: clamp(1.6rem, 3vw, 2.8rem);
  font-weight: 800;
  color: #fff;
  line-height: 1.1;
}

/* Poster */
.poster-wrap {
  overflow: hidden;
  border-radius: 20px;
  box-shadow: 0 18px 50px rgba(0,0,0,0.45), 0 0 28px rgba(139,92,246,0.18);
}

.poster-image {
  width: 100%;
  display: block;
  aspect-ratio: 2 / 3;
  object-fit: cover;
}

/* Form fields */
.form-group-dark {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.field-label {
  font-size: 0.8rem;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #c4b5fd;
  font-weight: 700;
}

.field-input {
  background: rgba(255, 255, 255, 0.07);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 12px;
  padding: 0.7rem 0.9rem;
  color: #fff;
  font-size: 0.95rem;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
  outline: none;
  width: 100%;
}

.field-input::placeholder { color: rgba(255,255,255,0.35); }

.field-input:focus {
  border-color: rgba(139, 92, 246, 0.6);
  box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.18);
}

.field-textarea {
  resize: vertical;
  min-height: 100px;
}

/* Tag selector */
.tags-selector {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.tag-toggle {
  padding: 0.4rem 0.85rem;
  border-radius: 999px;
  font-size: 0.85rem;
  font-weight: 600;
  border: 1px solid rgba(255, 255, 255, 0.15);
  background: rgba(255, 255, 255, 0.06);
  color: rgba(255, 255, 255, 0.7);
  cursor: pointer;
  transition: all 0.18s ease;
}

.tag-toggle:hover {
  background: rgba(139, 92, 246, 0.2);
  border-color: rgba(139, 92, 246, 0.4);
  color: #fff;
}

.tag-toggle.active {
  background: rgba(139, 92, 246, 0.75);
  border-color: rgba(196, 181, 253, 0.6);
  color: #fff;
}

/* Action buttons */
.save-btn {
  background: linear-gradient(90deg, #5b21b6, #7c3aed, #9333ea);
  border: none;
  border-radius: 14px;
  padding: 0.75rem 1.6rem;
  color: #fff;
  font-weight: 700;
  font-size: 1rem;
  box-shadow: 0 8px 24px rgba(91,33,182,0.35);
  transition: transform 0.15s ease, box-shadow 0.2s ease;
}

.save-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 12px 28px rgba(91,33,182,0.45);
  color: #fff;
}

.save-btn:disabled { opacity: 0.6; cursor: not-allowed; }

.cancel-btn {
  border: 1px solid rgba(196, 181, 253, 0.25);
  background: rgba(20, 14, 35, 0.75);
  color: white;
  border-radius: 14px;
  padding: 0.75rem 1.4rem;
  font-weight: 600;
  backdrop-filter: blur(10px);
  transition: background 0.2s ease;
}

.cancel-btn:hover {
  background: rgba(40, 24, 74, 0.85);
  color: white;
}

/* Alerts */
.alert-success-custom {
  background: rgba(34, 197, 94, 0.15);
  border: 1px solid rgba(34, 197, 94, 0.35);
  border-radius: 14px;
  padding: 0.85rem 1.2rem;
  color: #bbf7d0;
  font-weight: 600;
}

.alert-error-custom {
  background: rgba(239, 68, 68, 0.15);
  border: 1px solid rgba(239, 68, 68, 0.35);
  border-radius: 14px;
  padding: 0.85rem 1.2rem;
  color: #fecaca;
  font-weight: 600;
}

/* Back button */
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

/* State box */
.state-box {
  position: relative;
  z-index: 2;
  background: rgba(12, 8, 24, 0.68);
  border-radius: 24px;
  padding: 3rem 1rem;
  backdrop-filter: blur(12px);
}
</style>