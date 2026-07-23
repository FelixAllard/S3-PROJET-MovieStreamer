<script setup>
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap'

import {ref, onMounted, computed, onBeforeUnmount, nextTick} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {animate} from 'motion-v'
import Hls from 'hls.js'
import apiClient from '/src/services/ApiClient.js'
import {Movie} from '/src/entities/Movie.js'
import {isLoggedIn, resolveDbUserId} from '/src/utils/auth.js'

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

// Track-Selection States
const audioTracks = ref([])
const subtitleTracks = ref([])
const selectedAudioIndex = ref(null)
const selectedSubtitleIndex = ref(null) // null means 'Off'
const playSessionId = ref('')
const mediaSourceId = ref('')

// Streaming states
const isPlaying = ref(false)
const streamUrl = ref('')
const loadingStream = ref(false)
const isVideoBuffering = ref(false)

const videoPlayerRef = ref(null)
let hlsInstance = null

const movieId = computed(() => route.params.id)


// Auth & Rating
const userRating = ref(null)
const ratingSubmitting = ref(false)
const ratingSuccess = ref(false)
const ratingError = ref('')
const averageMovieRating = ref(null)
const hoveredRating = ref(0)

onMounted(async () => {
  await fetchMovie()
  await fetchSavedStatus()
  await fetchUserRating()
  await fetchMovieRating()

  if (pageRef.value) {
    animate(
        pageRef.value,
        {opacity: [0, 1]},
        {duration: 0.45, easing: 'ease-out'}
    )
  }

  if (contentRef.value) {
    animate(
        contentRef.value,
        {opacity: [0, 1], y: [30, 0], scale: [0.98, 1]},
        {duration: 0.55, easing: 'ease-out'}
    )
  }
})

onBeforeUnmount(() => {
  destroyHls()
})

function destroyHls() {
  if (hlsInstance) {
    hlsInstance.destroy()
    hlsInstance = null
  }
}

async function fetchMovie() {
  try {
    loading.value = true
    errorMessage.value = ''

    const encodedId = encodeURIComponent(movieId.value)
    const data = await apiClient.get(`/movie/${encodedId}`)
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

    const encodedId = encodeURIComponent(movieId.value)
    saved.value = await apiClient.get(`/watch-history/saved/${userId.value}/${encodedId}`)
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
    const encodedId = encodeURIComponent(movieId.value)
    const updated = await apiClient.put(`/watch-history/saved/${userId.value}/${encodedId}/${nextSaved}`, {})
    saved.value = Boolean(updated?.saved)
    watchlistMessage.value = saved.value ? 'Added to your watchlist.' : 'Removed from your watchlist.'
  } catch (err) {
    console.error('Failed to update saved movie status:', err)
    watchlistMessage.value = 'Unable to update your watchlist.'
  } finally {
    saving.value = false
  }
}

// 1. Fetch Tracks (PlaybackInfo) and Start Stream
async function startStreaming() {
  if (loadingStream.value || !movie.value) return

  loadingStream.value = true
  isVideoBuffering.value = true

  try {
    const encodedId = encodeURIComponent(movieId.value)

    // Step A: Fetch playback info containing audio/sub streams from your proxy backend or directly
    // This is mapping Jellyfin's GET /Items/{Id}/PlaybackInfo?UserId={UserId}
    const playbackInfo = await apiClient.get(`/movie/${encodedId}/playback-info`)

    if (playbackInfo && playbackInfo.MediaSources && playbackInfo.MediaSources.length > 0) {
      const source = playbackInfo.MediaSources[0]
      mediaSourceId.value = source.Id

      playSessionId.value = playbackInfo.PlaySessionId

      // Map streams from media source metadata
      const allStreams = source.MediaStreams || []

      // Parse Audio Tracks
      audioTracks.value = allStreams
          .filter(s => s.Type === 'Audio')
          .map(s => ({
            index: s.Index,
            label: s.DisplayTitle || s.Language || `Audio Track #${s.Index}`
          }))

      const savedAudio = localStorage.getItem(`preferred_audio_${movieId.value}`)
      if (savedAudio !== null) {
        selectedAudioIndex.value = Number(savedAudio)
      } else if (selectedAudioIndex.value === null && audioTracks.value.length > 0) {
        const defaultAudio = allStreams.find(s => s.Type === 'Audio' && s.IsDefault)
        selectedAudioIndex.value = defaultAudio ? defaultAudio.Index : audioTracks.value[0].index
      }

      // Select the default audio track if not set
      if (selectedAudioIndex.value === null && audioTracks.value.length > 0) {
        const defaultAudio = allStreams.find(s => s.Type === 'Audio' && s.IsDefault)
        selectedAudioIndex.value = defaultAudio ? defaultAudio.Index : audioTracks.value[0].index
      }

      // Parse Subtitle Tracks
      subtitleTracks.value = allStreams
          .filter(s => s.Type === 'Subtitle')
          .map(s => ({
            index: s.Index,
            label: s.DisplayTitle || s.Language || `Subtitle Track #${s.Index}`
          }))
    }

    // Step B: Ask your spring boot backend for the computed master stream url using our selected parameters
    await loadActiveTrackStream()

  } catch (err) {
    console.error('Streaming error:', err)
    alert('Failed to load playback parameters. Playing default profile instead.')
    isPlaying.value = true
    // Fallback URL mechanism if playbackInfo fails
    streamUrl.value = `https://jellyfin.seeroy.com/Videos/${movieId.value}/master.m3u8?api_key=bdc25e36b1ef4699acd7cada300310ae&MediaSourceId=${movieId.value}`
    setTimeout(() => {
      initPlayer()
    }, 50)
  } finally {
    loadingStream.value = false
  }
}

// 2. Load (or Re-load) Stream URL with track parameters
async function loadActiveTrackStream() {
  isVideoBuffering.value = true
  const encodedId = encodeURIComponent(movieId.value)

  // Ensure we explicitly pass the chosen audio stream index
  let queryParams = `?playSessionId=${playSessionId.value}&mediaSourceId=${mediaSourceId.value}`
  if (selectedAudioIndex.value !== null) {
    queryParams += `&audioStreamIndex=${selectedAudioIndex.value}`
  }
  if (selectedSubtitleIndex.value !== null) {
    queryParams += `&subtitleStreamIndex=${selectedSubtitleIndex.value}`
  }

  try {
    // Force a timestamp or unique param if your backend/proxy caches the stream URL response
    queryParams += `&t=${Date.now()}`

    const data = await apiClient.get(`/movie/${encodedId}/stream${queryParams}`)

    if (data && data.streamUrl) {
      streamUrl.value = data.streamUrl
      isPlaying.value = true

      await nextTick()
      initPlayer()
    } else {
      throw new Error('No stream URL received')
    }
  } catch (err) {
    console.error('Track change error:', err)
    isVideoBuffering.value = false
  }
}

async function onTrackChange() {
  if (selectedAudioIndex.value !== null) {
    localStorage.setItem(`preferred_audio_${movieId.value}`, selectedAudioIndex.value)
  }

  // Hard reload the entire browser page
  window.location.reload()
}

function initPlayer() {
  const video = videoPlayerRef.value
  if (!video) return

  destroyHls()

  if (streamUrl.value.includes('.m3u8')) {
    if (Hls.isSupported()) {
      // 1. Configure HLS.js to enable subtitles and render them natively in the DOM
      hlsInstance = new Hls({
        enableSubtitle: true,
        subtitlePreference: {
          shareTextFields: true
        }
      })

      hlsInstance.loadSource(streamUrl.value)
      hlsInstance.attachMedia(video)

      hlsInstance.on(Hls.Events.MANIFEST_PARSED, () => {
        video.play().catch(() => {
        })
      })

      hlsInstance.on(Hls.Events.MANIFEST_PARSED, () => {
        console.log("Audio:", hlsInstance.audioTracks)
        console.log("Subtitles:", hlsInstance.subtitleTracks)
      })

      // 2. Listen for subtitle tracks being loaded by HLS.js
      hlsInstance.on(Hls.Events.SUBTITLE_TRACKS_UPDATED, (event, data) => {
        console.log('Subtitle tracks found in HLS stream:', data.subtitleTracks)
      })

      // 3. Force HLS.js to switch to the selected subtitle stream when it changes
      if (selectedSubtitleIndex.value !== null) {
        hlsInstance.on(Hls.Events.SUBTITLE_TRACK_LOADED, () => {
          // Tell HLS.js to actively render the subtitle stream matching our index
          const hlsSubTracks = hlsInstance.subtitleTracks
          const matchIndex = hlsSubTracks.findIndex(t => t.id === selectedSubtitleIndex.value)
          if (matchIndex !== -1) {
            hlsInstance.subtitleTrack = matchIndex
          }
        })
      } else {
        // Turn subtitles off in HLS.js
        hlsInstance.subtitleTrack = -1
      }

      hlsInstance.on(Hls.Events.ERROR, (event, data) => {
        if (data.fatal) {
          console.error('HLS stream encountered a fatal decoder error:', data)
        }
      })
    } else if (video.canPlayType('application/vnd.apple.mpegurl')) {
      // For Safari (Native HLS handling)
      video.src = streamUrl.value
      video.addEventListener('loadedmetadata', () => {
        video.play().catch(() => {
        })
        // Enable native text tracks if Safari
        if (selectedSubtitleIndex.value !== null && video.textTracks) {
          for (let i = 0; i < video.textTracks.length; i++) {
            if (i === selectedSubtitleIndex.value) {
              video.textTracks[i].mode = 'showing'
            } else {
              video.textTracks[i].mode = 'disabled'
            }
          }
        }
      })
    }
  } else {
    video.src = streamUrl.value
    video.play().catch(() => {
    })
  }
}

function onVideoWaiting() {
  isVideoBuffering.value = true
}

function onVideoPlaying() {
  isVideoBuffering.value = false
}

function goBack() {
  router.push('/movies')
}


async function fetchUserRating() {
  if (!loggedIn.value || !userId.value) return

  try {
    const rating = await apiClient.get(
        `/watch-history/rating/${userId.value}/${movieId.value}`
    )
    userRating.value = rating
  } catch (err) {
    console.log('No existing rating for this movie')
  }
}

async function submitRating(rating) {
  if (!loggedIn.value) return

  try {
    ratingSubmitting.value = true
    ratingError.value = ''
    ratingSuccess.value = false

    await apiClient.put(
        `/user/${movieId.value}/${userId.value}/${rating}`
    )

    userRating.value = rating
    ratingSuccess.value = true
    setTimeout(() => ratingSuccess.value = false, 3000)

  } catch (err) {
    console.error('Failed to submit rating:', err)
    if (err.status === 409) {
      ratingError.value = 'You have already rated this movie.'
    } else {
      ratingError.value = 'Failed to submit rating. Please try again.'
    }
  } finally {
    ratingSubmitting.value = false
  }
}

async function fetchMovieRating() {
  try {
    const data = await apiClient.get(`/movie/${movieId.value}/rating`)
    averageMovieRating.value = data
  } catch (err) {
    console.log('No ratings yet for this movie')
  }
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
        <div class="spinner-border text-primary spinner-purple mb-3" role="status"></div>
        <p class="mb-0">Loading movie...</p>
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
        <div class="row g-4 mb-5">
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
              <div v-if="averageMovieRating" class="average-rating-row mb-3">
                <span class="average-stars">
                    {{
                    '★'.repeat(Math.round(averageMovieRating.average))
                  }}{{ '☆'.repeat(10 - Math.round(averageMovieRating.average)) }}
                </span>
                <span class="average-label">
                    {{ averageMovieRating.average.toFixed(1) }}/10
                    <span class="average-count">
                        ({{ averageMovieRating.distribution.reduce((acc, d) => acc + d.count, 0) }} ratings)
                    </span>
                </span>
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

              <!-- Rating Section -->
              <div class="mt-4">
                <div v-if="loggedIn" class="rating-card">
                  <h3 class="info-label mb-3">Rate this movie</h3>

                  <div class="stars-row">
                    <button
                        v-for="star in 10"
                        :key="star"
                        class="star-btn"
                        :class="{
                          'star-active': star <= (hoveredRating || userRating || 0),
                          'star-hovered': star <= hoveredRating && hoveredRating > 0,
                          'star-disabled': ratingSubmitting
                        }"
                        @click="submitRating(star)"
                        @mouseenter="hoveredRating = star"
                        @mouseleave="hoveredRating = 0"
                        :disabled="ratingSubmitting"
                    >
                      {{ star <= (hoveredRating || userRating || 0) ? '★' : '☆' }}
                    </button>
                  </div>

                  <p v-if="userRating" class="rating-label mt-2">
                    Your rating: <strong>{{ userRating }}/10</strong>
                  </p>

                  <div v-if="ratingSuccess" class="rating-feedback success mt-2">
                    Rating submitted successfully!
                  </div>

                  <div v-if="ratingError" class="rating-feedback error mt-2">
                    {{ ratingError }}
                  </div>
                </div>

                <div v-else class="rating-card text-center">
                  <p class="mb-2" style="color: rgba(255,255,255,0.7)">
                    Want to rate this movie?
                  </p>
                  <button class="btn login-to-rate-btn" @click="router.push('/login')">
                    Sign in to rate
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="widescreen-player-wrapper">
          <div class="video-container">

            <video
                v-if="isPlaying"
                ref="videoPlayerRef"
                controls
                autoplay
                class="stream-player-active"
                @waiting="onVideoWaiting"
                @playing="onVideoPlaying"
                @canplay="onVideoPlaying"
            >
              Your browser does not support HTML5 video playback.
            </video>

            <div
                v-if="isPlaying && isVideoBuffering"
                class="video-buffering-overlay"
            >
              <div class="buffering-spinner-box">
                <div class="spinner-border custom-spinner-glow mb-3" role="status"></div>
                <p class="buffering-text">Buffering Stream...</p>
              </div>
            </div>

            <div
                v-else-if="!isPlaying"
                class="player-poster-overlay-active"
                :style="movie.thumbnail ? { backgroundImage: `url(${movie.thumbnail})` } : {}"
            >
              <div class="backdrop-blur-filter"></div>
              <button
                  class="central-play-btn"
                  :disabled="loadingStream"
                  @click="startStreaming"
              >
                <span v-if="loadingStream" class="spinner-border spinner-purple" role="status"></span>
                <span v-else class="play-arrow"></span>
              </button>
              <p class="overlay-play-text mt-3">
                {{ loadingStream ? 'Loading Player...' : 'Click to Stream' }}
              </p>
            </div>

          </div>
        </div>

        <!-- Track Selectors: Audio selector kept, Subtitles visually removed via v-if="false" -->
        <div v-if="isPlaying" class="track-selectors-panel mt-3">
          <div class="row g-3">
            <div class="col-12 col-md-6" v-if="audioTracks.length > 1">
              <label class="track-select-label">Audio Language</label>
              <select
                  class="form-select track-dropdown"
                  v-model="selectedAudioIndex"
                  @change="onTrackChange"
              >
                <option
                    v-for="track in audioTracks"
                    :key="track.index"
                    :value="track.index"
                >
                  {{ track.label }}
                </option>
              </select>
            </div>

            <!-- Subtitle UI hidden visually without removing data functionality -->
            <div class="col-12 col-md-6" v-if="false">
              <label class="track-select-label">Subtitles</label>
              <select
                  class="form-select track-dropdown"
                  v-model="selectedSubtitleIndex"
                  @change="onTrackChange"
              >
                <option :value="null">Off</option>
                <option
                    v-for="track in subtitleTracks"
                    :key="track.index"
                    :value="track.index"
                >
                  {{ track.label }}
                </option>
              </select>
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
  background: linear-gradient(180deg, rgba(5, 3, 10, 0.35), rgba(5, 3, 10, 0.88)),
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
  padding: 2rem;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.45),
  0 0 40px rgba(139, 92, 246, 0.12);
}

/* Widescreen Video Player Wrapper */
.widescreen-player-wrapper {
  width: 100%;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 15px 45px rgba(0, 0, 0, 0.6),
  0 0 30px rgba(139, 92, 246, 0.18);
  border: 1px solid rgba(255, 255, 255, 0.12);
  background-color: #000;
}

.video-container {
  position: relative;
  width: 100%;
  aspect-ratio: 16 / 9;
}

.stream-player {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

/* High Contrast Buffering Overlay */
.video-buffering-overlay {
  position: absolute;
  inset: 0;
  background: rgba(5, 3, 10, 0.7);
  backdrop-filter: blur(6px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 15;
  pointer-events: none;
}

.buffering-spinner-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.custom-spinner-glow {
  width: 4rem;
  height: 4rem;
  border-width: 0.35em;
  color: #a78bfa !important;
  filter: drop-shadow(0 0 12px rgba(167, 139, 250, 0.75));
  animation: spinner-border .75s linear infinite;
}

.buffering-text {
  color: #f5f3ff;
  font-weight: 700;
  font-size: 1.1rem;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  margin-top: 10px;
  text-shadow: 0 0 8px rgba(167, 139, 250, 0.5), 0 2px 4px rgba(0, 0, 0, 0.9);
}

.player-poster-overlay {
  position: absolute;
  inset: 0;
  background-size: cover;
  background-position: center;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  pointer-events: auto;
}

.backdrop-blur-filter {
  position: absolute;
  inset: 0;
  background: rgba(12, 8, 24, 0.55);
  backdrop-filter: blur(15px);
  z-index: 1;
}

.central-play-btn {
  position: relative;
  z-index: 2;
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #a78bfa 0%, #7c3aed 100%);
  border: none;
  box-shadow: 0 0 25px rgba(139, 92, 246, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.25s cubic-bezier(0.175, 0.885, 0.32, 1.275), box-shadow 0.25s;
}

.central-play-btn:hover {
  transform: scale(1.12);
  box-shadow: 0 0 35px rgba(139, 92, 246, 0.85);
}

.central-play-btn:disabled {
  background: rgba(45, 30, 80, 0.9);
  box-shadow: none;
  cursor: not-allowed;
}

.spinner-purple {
  color: #c4b5fd !important;
  width: 3rem;
  height: 3rem;
  border-width: 0.3em;
}

.play-arrow {
  display: block;
  width: 0;
  height: 0;
  border-top: 15px solid transparent;
  border-bottom: 15px solid transparent;
  border-left: 24px solid #fff;
  margin-left: 6px;
}

.overlay-play-text {
  position: relative;
  z-index: 2;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  font-size: 0.95rem;
  color: rgba(255, 255, 255, 0.9);
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.8);
}

.poster-wrap {
  position: relative;
  overflow: hidden;
  border-radius: 24px;
  box-shadow: 0 18px 50px rgba(0, 0, 0, 0.45),
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

.stream-player-active {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: contain;
  z-index: 10;
  pointer-events: auto !important;
}

.player-poster-overlay-active {
  position: absolute;
  inset: 0;
  background-size: cover;
  background-position: center;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  z-index: 5;
}

.track-selectors-panel {
  background: rgba(20, 14, 35, 0.85);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 16px;
  padding: 1.25rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.track-select-label {
  display: block;
  font-size: 0.8rem;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #c4b5fd;
  font-weight: 700;
  margin-bottom: 0.5rem;
}

.track-dropdown {
  background-color: rgba(12, 8, 24, 0.9) !important;
  border: 1px solid rgba(196, 181, 253, 0.25) !important;
  color: #f5f3ff !important;
  border-radius: 10px;
  padding: 0.6rem 1rem;
  font-weight: 600;
  font-size: 0.95rem;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.track-dropdown:focus {
  border-color: #a78bfa !important;
  box-shadow: 0 0 10px rgba(167, 139, 250, 0.25) !important;
  outline: none;
}

.track-dropdown option {
  background-color: #0c0818;
  color: #fff;
}

.rating-card {
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 18px;
  padding: 1rem 1rem 0.95rem;
}

.stars-row {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}

.star-btn {
  background: none;
  border: none;
  font-size: 1.6rem;
  color: rgba(255, 255, 255, 0.3);
  cursor: pointer;
  padding: 0 2px;
  transition: color 0.1s ease, transform 0.1s ease;
  line-height: 1;
}

.star-hovered {
  color: #f59e0b;
  transform: scale(1.2);
}

.star-active {
  color: #f59e0b;
}

.star-disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.rating-label {
  color: rgba(255, 255, 255, 0.8);
  font-size: 0.9rem;
}

.rating-feedback {
  font-size: 0.88rem;
  border-radius: 10px;
  padding: 0.5rem 0.75rem;
}

.rating-feedback.success {
  background: rgba(34, 197, 94, 0.15);
  border: 1px solid rgba(34, 197, 94, 0.3);
  color: #86efac;
}

.rating-feedback.error {
  background: rgba(239, 68, 68, 0.15);
  border: 1px solid rgba(239, 68, 68, 0.3);
  color: #fca5a5;
}

.login-to-rate-btn {
  border: 1px solid rgba(196, 181, 253, 0.25);
  background: rgba(139, 92, 246, 0.15);
  color: #c4b5fd;
  border-radius: 999px;
  padding: 0.55rem 1.2rem;
  font-size: 0.9rem;
  transition: all 0.2s ease;
}

.login-to-rate-btn:hover {
  background: rgba(139, 92, 246, 0.3);
  color: white;
}

.average-rating-row {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.average-stars {
  font-size: 1.2rem;
  color: #f59e0b;
  letter-spacing: 2px;
}

.average-label {
  color: rgba(255, 255, 255, 0.9);
  font-size: 1rem;
  font-weight: 600;
}

.average-count {
  color: rgba(255, 255, 255, 0.5);
  font-size: 0.85rem;
  font-weight: 400;
}
</style>