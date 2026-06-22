<script setup>
import { ref, onMounted, watch, nextTick, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { animate } from 'motion-v'
import ApiClient from '../services/ApiClient.js'
import AnimatedTitle from './AnimatedTitle.vue'

const router = useRouter()

const movies = ref([])
const activeIndex = ref(0)
const isMobile = ref(false)

const centerCard = ref(null)

let interval = null

// -------------------------
// SCREEN DETECTION
// -------------------------
function checkScreen() {
  isMobile.value = window.innerWidth < 768
}

// -------------------------
// FETCH MOVIES
// -------------------------
async function loadMovies() {
  try {
    movies.value = await ApiClient.get('/movie/new/5')
  } catch (err) {
    console.error(err)
  }
}

// -------------------------
// ROTATION (DESKTOP ONLY)
// -------------------------
function startRotation() {
  interval = setInterval(() => {
    if (!movies.value.length || isMobile.value) return

    activeIndex.value =
        (activeIndex.value + 1) % movies.value.length
  }, 5000)
}

onMounted(async () => {
  checkScreen()
  window.addEventListener('resize', checkScreen)

  await loadMovies()
  startRotation()

  await nextTick()

  if (centerCard.value) {
    animate(centerCard.value, {
      opacity: [0, 1],
      y: [60, 0],
      scale: [0.9, 1.05],
      filter: ['blur(10px)', 'blur(0px)']
    }, {
      duration: 1,
      easing: 'ease-out'
    })
  }
})

onBeforeUnmount(() => {
  if (interval) clearInterval(interval)
  window.removeEventListener('resize', checkScreen)
})

// -------------------------
// SWITCH ANIMATION
// -------------------------
watch(activeIndex, async () => {
  if (isMobile.value) return

  await nextTick()

  if (!centerCard.value) return

  animate(centerCard.value, {
    opacity: [0, 1],
    x: [150, 0],
    scale: [0.9, 1.05],
    rotateY: [10, 0],
    filter: ['blur(8px)', 'blur(0px)']
  }, {
    duration: 0.8,
    easing: 'ease-out'
  })
})

// -------------------------
// OPEN MOVIE
// -------------------------
async function openMovie(movie) {
  if (!centerCard.value) return

  animate(centerCard.value, {
    scale: [1.05, 1.2],
    filter: ['brightness(1)', 'brightness(1.4)']
  }, {
    duration: 0.3
  })

  setTimeout(() => {
    router.push(`/movies/${movie.id}`)
  }, 300)
}
</script>

<template>
  <div class="cinema">

    <!-- TITLE -->
    <AnimatedTitle
        text="New Arrivals"
        size="medium"
        :showCursor="false"
    />

    <!-- ===================== -->
    <!-- DESKTOP CINEMATIC VIEW -->
    <!-- ===================== -->
    <div v-if="!isMobile" class="hero">

      <div class="side" @click="activeIndex = (activeIndex - 1 + movies.length) % movies.length">
        <img
            v-if="movies.length"
            :src="movies[(activeIndex - 1 + movies.length) % movies.length].thumbnail"
        />
      </div>

      <div
          v-if="movies.length"
          ref="centerCard"
          class="center"
          @click="openMovie(movies[activeIndex])"
      >
        <img :src="movies[activeIndex].thumbnail" />

        <div class="info">
          <h1>{{ movies[activeIndex].title }}</h1>
          <p>{{ movies[activeIndex].description }}</p>

          <button class="btn">▶ Watch Now</button>
        </div>
      </div>

      <div class="side" @click="activeIndex = (activeIndex + 1) % movies.length">
        <img
            v-if="movies.length"
            :src="movies[(activeIndex + 1) % movies.length].thumbnail"
        />
      </div>

    </div>

    <!-- ===================== -->
    <!-- MOBILE BOOTSTRAP VIEW -->
    <!-- ===================== -->
    <div v-else class="container mt-3">

      <div
          id="movieCarousel"
          class="carousel slide"
          data-bs-ride="carousel"
      >

        <div class="carousel-inner">

          <div
              v-for="(movie, index) in movies"
              :key="movie.id"
              class="carousel-item"
              :class="{ active: index === 0 }"
          >
            <div class="mobile-card" @click="openMovie(movie)">
              <img :src="movie.thumbnail" class="d-block w-100" />

              <div class="mobile-overlay">
                <h5>{{ movie.title }}</h5>
                <p>{{ movie.description }}</p>
              </div>
            </div>
          </div>

        </div>

        <button class="carousel-control-prev" type="button" data-bs-target="#movieCarousel" data-bs-slide="prev">
          <span class="carousel-control-prev-icon"></span>
        </button>

        <button class="carousel-control-next" type="button" data-bs-target="#movieCarousel" data-bs-slide="next">
          <span class="carousel-control-next-icon"></span>
        </button>

      </div>

    </div>

  </div>
</template>

<style scoped>
.cinema {
  height: 100vh;
  overflow: hidden;
  background: radial-gradient(circle at center, #120a24, #05020a);
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 10px;
}

/* ------------------- */
/* DESKTOP HERO */
/* ------------------- */

.hero {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 40px;
  max-height: 70vh;
}

.center {
  width: 340px;
  height: 460px;
  border-radius: 24px;
  overflow: hidden;
  cursor: pointer;
  position: relative;

  box-shadow:
      0 0 60px rgba(139,92,246,.4),
      0 25px 80px rgba(0,0,0,.7);
}

.center img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.info {
  position: absolute;
  bottom: 0;
  padding: 16px;
  color: white;
  background: linear-gradient(transparent, rgba(0,0,0,.95));
}

.info h1 {
  font-size: 1.4rem;
  font-weight: 900;
}

.info p {
  font-size: 0.85rem;
  opacity: 0.85;
}

.btn {
  margin-top: 8px;
  padding: 8px 14px;
  border: none;
  border-radius: 10px;
  background: linear-gradient(90deg, #7c3aed, #9333ea);
  color: white;
  font-weight: 700;
}

/* SIDE CARDS */
.side {
  width: 130px;
  height: 200px;
  opacity: 0.5;
  cursor: pointer;
  border-radius: 14px;
  overflow: hidden;
  transition: 0.3s;
}

.side:hover {
  opacity: 0.85;
  transform: scale(1.05);
}

.side img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* ------------------- */
/* MOBILE CAROUSEL */
/* ------------------- */

.mobile-card {
  position: relative;
  border-radius: 16px;
  overflow: hidden;
}

.mobile-card img {
  border-radius: 16px;
}

.mobile-overlay {
  position: absolute;
  bottom: 0;
  width: 100%;
  padding: 12px;
  color: white;
  background: linear-gradient(transparent, rgba(0,0,0,.9));
}

.mobile-overlay h5 {
  font-weight: 800;
}

/* hide weird bootstrap spacing */
.carousel {
  border-radius: 16px;
  overflow: hidden;
}
</style>