<script setup>
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap'

import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { animate } from 'motion-v'
import apiClient from '/src/services/ApiClient.js'

const router = useRouter()

const loading = ref(true)
const saving = ref(false)
const errorMessage = ref('')
const successMessage = ref('')
const pageRef = ref(null)
const contentRef = ref(null)
const allTags = ref([])

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
  await fetchAllTags()
  loading.value = false

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

async function fetchAllTags() {
  try {
    const data = await apiClient.get('/tag/all')
    allTags.value = Array.isArray(data) ? data : []
  } catch (err) {
    console.warn('Failed to fetch tags:', err)
    errorMessage.value = 'Failed to load available tags.'
  }
}

function toggleTag(tagId) {
  const index = form.value.tagIds.indexOf(tagId)

  if (index === -1) {
    form.value.tagIds.push(tagId)
  } else {
    form.value.tagIds.splice(index, 1)
  }
}

function isTagSelected(tagId) {
  return form.value.tagIds.includes(tagId)
}

function validateForm() {
  if (!form.value.title.trim()) return 'Title is required.'
  if (!form.value.description.trim()) return 'Description is required.'
  if (!form.value.director.trim()) return 'Director is required.'
  if (!form.value.writer.trim()) return 'Writer is required.'
  if (!form.value.studio.trim()) return 'Studio is required.'
  if (!form.value.language.trim()) return 'Language is required.'
  if (!form.value.thumbnail.trim()) return 'Thumbnail URL is required.'
  if (!form.value.streamId.trim()) return 'Stream ID is required.'
  if (!form.value.year || Number(form.value.year) < 0) return 'A valid year is required.'
  if (!form.value.movieLength || Number(form.value.movieLength) < 0) {
    return 'A valid movie length is required.'
  }

  return ''
}

async function createMovie() {
  if (saving.value) return

  errorMessage.value = ''
  successMessage.value = ''

  const validationError = validateForm()
  if (validationError) {
    errorMessage.value = validationError
    return
  }

  try {
    saving.value = true

    const payload = {
      title: form.value.title.trim(),
      description: form.value.description.trim(),
      director: form.value.director.trim(),
      writer: form.value.writer.trim(),
      studio: form.value.studio.trim(),
      language: form.value.language.trim(),
      year: Number(form.value.year),
      movieLength: Number(form.value.movieLength),
      thumbnail: form.value.thumbnail.trim(),
      streamId: form.value.streamId.trim(),

      // Existing shared tag IDs only — this does not edit tag names.
      tags: form.value.tagIds.map(id => ({ id }))
    }

    const createdMovie = await apiClient.post('/movie', payload)

    successMessage.value = 'Movie created successfully!'

    if (createdMovie?.id) {
      setTimeout(() => {
        router.push(`/movies/${createdMovie.id}`)
      }, 700)
    }
  } catch (err) {
    console.error('Failed to create movie:', err)
    errorMessage.value = 'Failed to create the movie. Please try again.'
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
      class="create-movie-page"
      :style="form.thumbnail ? { '--movie-bg': `url(${form.thumbnail})` } : {}"
  >
    <div class="background-layer"></div>
    <div class="background-overlay"></div>

    <div class="container-fluid px-3 px-md-4 py-4 position-relative">
      <div class="mb-4">
        <button class="btn back-btn" @click="goBack">
          ← Back to Movies
        </button>
      </div>

      <div v-if="loading" class="state-box text-center text-white">
        Loading form...
      </div>

      <div v-else ref="contentRef" class="create-panel">
        <div class="create-header mb-4">
          <p class="eyebrow mb-1">Admin · Create Mode</p>
          <h1 class="create-heading">Add a New Movie</h1>
          <p class="create-subheading mb-0">
            Fill in the details below to add a movie to the catalog.
          </p>
        </div>

        <div v-if="successMessage" class="alert-success-custom mb-4">
          ✅ {{ successMessage }}
        </div>

        <div v-if="errorMessage" class="alert-error-custom mb-4">
          ⚠️ {{ errorMessage }}
        </div>

        <form @submit.prevent="createMovie">
          <div class="row g-4">
            <div class="col-12 col-lg-3">
              <div class="poster-wrap mb-3">
                <img
                    v-if="form.thumbnail"
                    :src="form.thumbnail"
                    :alt="form.title || 'Movie poster preview'"
                    class="poster-image"
                />

                <div v-else class="poster-placeholder">
                  <span>🎬</span>
                  <p>Poster Preview</p>
                </div>
              </div>

              <div class="form-group-dark">
                <label for="thumbnail" class="field-label">Thumbnail URL</label>
                <input
                    id="thumbnail"
                    v-model="form.thumbnail"
                    type="url"
                    class="field-input"
                    placeholder="https://..."
                />
              </div>
            </div>

            <div class="col-12 col-lg-9">
              <div class="row g-3">
                <div class="col-12">
                  <div class="form-group-dark">
                    <label for="title" class="field-label">Title</label>
                    <input
                        id="title"
                        v-model="form.title"
                        type="text"
                        class="field-input"
                        placeholder="Movie title"
                    />
                  </div>
                </div>

                <div class="col-12">
                  <div class="form-group-dark">
                    <label for="description" class="field-label">Description</label>
                    <textarea
                        id="description"
                        v-model="form.description"
                        class="field-input field-textarea"
                        rows="5"
                        placeholder="Movie description..."
                    ></textarea>
                  </div>
                </div>

                <div class="col-12 col-md-6">
                  <div class="form-group-dark">
                    <label for="director" class="field-label">Director</label>
                    <input
                        id="director"
                        v-model="form.director"
                        type="text"
                        class="field-input"
                        placeholder="Director name"
                    />
                  </div>
                </div>

                <div class="col-12 col-md-6">
                  <div class="form-group-dark">
                    <label for="writer" class="field-label">Writer</label>
                    <input
                        id="writer"
                        v-model="form.writer"
                        type="text"
                        class="field-input"
                        placeholder="Writer name"
                    />
                  </div>
                </div>

                <div class="col-12 col-md-4">
                  <div class="form-group-dark">
                    <label for="studio" class="field-label">Studio</label>
                    <input
                        id="studio"
                        v-model="form.studio"
                        type="text"
                        class="field-input"
                        placeholder="Studio name"
                    />
                  </div>
                </div>

                <div class="col-12 col-md-4">
                  <div class="form-group-dark">
                    <label for="language" class="field-label">Language</label>
                    <input
                        id="language"
                        v-model="form.language"
                        type="text"
                        class="field-input"
                        placeholder="e.g. English"
                    />
                  </div>
                </div>

                <div class="col-6 col-md-2">
                  <div class="form-group-dark">
                    <label for="year" class="field-label">Year</label>
                    <input
                        id="year"
                        v-model="form.year"
                        type="number"
                        min="0"
                        class="field-input"
                        placeholder="2024"
                    />
                  </div>
                </div>

                <div class="col-6 col-md-2">
                  <div class="form-group-dark">
                    <label for="movieLength" class="field-label">Length (min)</label>
                    <input
                        id="movieLength"
                        v-model="form.movieLength"
                        type="number"
                        min="0"
                        class="field-input"
                        placeholder="120"
                    />
                  </div>
                </div>

                <div class="col-12 col-md-6">
                  <div class="form-group-dark">
                    <label for="streamId" class="field-label">Stream ID</label>
                    <input
                        id="streamId"
                        v-model="form.streamId"
                        type="text"
                        class="field-input"
                        placeholder="Jellyfin Item ID / Stream ID"
                    />
                  </div>
                </div>

                <div v-if="allTags.length" class="col-12">
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

              <div class="d-flex gap-3 mt-4 flex-wrap">
                <button
                    type="submit"
                    class="btn create-btn"
                    :disabled="saving"
                >
                  {{ saving ? 'Creating...' : '＋ Create Movie' }}
                </button>

                <button
                    type="button"
                    class="btn cancel-btn"
                    :disabled="saving"
                    @click="goBack"
                >
                  Cancel
                </button>
              </div>
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.create-movie-page {
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
  background-position: center;
  background-size: cover;
  filter: blur(22px) scale(1.08);
  opacity: 0.35;
  transform: scale(1.06);
}

.background-overlay {
  position: absolute;
  inset: 0;
  background:
      linear-gradient(180deg, rgba(5, 3, 10, 0.45), rgba(5, 3, 10, 0.92)),
      radial-gradient(circle at top left, rgba(139, 92, 246, 0.22), transparent 30%),
      radial-gradient(circle at top right, rgba(168, 85, 247, 0.16), transparent 25%);
}

.create-panel {
  position: relative;
  z-index: 2;
  padding: 2rem;
  background: rgba(12, 8, 24, 0.72);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 28px;
  backdrop-filter: blur(14px);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.45), 0 0 40px rgba(139, 92, 246, 0.12);
}

.create-header {
  color: white;
}

.eyebrow {
  color: #c4b5fd;
  font-size: 0.78rem;
  font-weight: 700;
  letter-spacing: 0.16em;
  text-transform: uppercase;
}

.create-heading {
  color: #fff;
  font-size: clamp(1.6rem, 3vw, 2.8rem);
  font-weight: 800;
  line-height: 1.1;
}

.create-subheading {
  color: rgba(255, 255, 255, 0.6);
}

.poster-wrap {
  overflow: hidden;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.05);
  box-shadow: 0 18px 50px rgba(0, 0, 0, 0.45), 0 0 28px rgba(139, 92, 246, 0.18);
}

.poster-image {
  display: block;
  width: 100%;
  aspect-ratio: 2 / 3;
  object-fit: cover;
}

.poster-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 360px;
  color: rgba(255, 255, 255, 0.45);
  aspect-ratio: 2 / 3;
}

.poster-placeholder span {
  font-size: 3rem;
}

.poster-placeholder p {
  margin: 0.8rem 0 0;
  font-weight: 600;
}

.form-group-dark {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.field-label {
  color: #c4b5fd;
  font-size: 0.8rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.field-input {
  width: 100%;
  padding: 0.7rem 0.9rem;
  color: #fff;
  font-size: 0.95rem;
  outline: none;
  background: rgba(255, 255, 255, 0.07);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 12px;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.field-input::placeholder {
  color: rgba(255, 255, 255, 0.35);
}

.field-input:focus {
  border-color: rgba(139, 92, 246, 0.6);
  box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.18);
}

.field-textarea {
  min-height: 120px;
  resize: vertical;
}

.tags-selector {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.tag-toggle {
  padding: 0.4rem 0.85rem;
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 999px;
  transition: all 0.18s ease;
}

.tag-toggle:hover {
  color: #fff;
  background: rgba(139, 92, 246, 0.2);
  border-color: rgba(139, 92, 246, 0.4);
}

.tag-toggle.active {
  color: #fff;
  background: rgba(139, 92, 246, 0.75);
  border-color: rgba(196, 181, 253, 0.6);
}

.create-btn {
  padding: 0.75rem 1.6rem;
  color: #fff;
  font-size: 1rem;
  font-weight: 700;
  background: linear-gradient(90deg, #5b21b6, #7c3aed, #9333ea);
  border: none;
  border-radius: 14px;
  box-shadow: 0 8px 24px rgba(91, 33, 182, 0.35);
}

.create-btn:hover:not(:disabled) {
  color: #fff;
  transform: translateY(-2px);
  box-shadow: 0 12px 28px rgba(91, 33, 182, 0.45);
}

.create-btn:disabled,
.cancel-btn:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.cancel-btn,
.back-btn {
  color: white;
  background: rgba(20, 14, 35, 0.75);
  border: 1px solid rgba(196, 181, 253, 0.25);
  backdrop-filter: blur(10px);
}

.cancel-btn {
  padding: 0.75rem 1.4rem;
  font-weight: 600;
  border-radius: 14px;
}

.back-btn {
  position: relative;
  z-index: 2;
  padding: 0.65rem 1rem;
  border-radius: 999px;
}

.cancel-btn:hover,
.back-btn:hover {
  color: white;
  background: rgba(40, 24, 74, 0.85);
}

.alert-success-custom,
.alert-error-custom {
  padding: 0.85rem 1.2rem;
  font-weight: 600;
  border-radius: 14px;
}

.alert-success-custom {
  color: #bbf7d0;
  background: rgba(34, 197, 94, 0.15);
  border: 1px solid rgba(34, 197, 94, 0.35);
}

.alert-error-custom {
  color: #fecaca;
  background: rgba(239, 68, 68, 0.15);
  border: 1px solid rgba(239, 68, 68, 0.35);
}

.state-box {
  position: relative;
  z-index: 2;
  padding: 3rem 1rem;
  background: rgba(12, 8, 24, 0.68);
  border-radius: 24px;
  backdrop-filter: blur(12px);
}
</style>