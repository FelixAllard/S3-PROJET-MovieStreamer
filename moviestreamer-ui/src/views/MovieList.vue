<script setup>
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap'

import { ref, onMounted } from 'vue'
import { animate } from 'motion-v'
import apiClient from '/src/services/ApiClient.js'
import { Movie } from '/src/entities/Movie.js'

const movies = ref([])
const cardRefs = ref([])
const textRefs = ref([])
const bgRef = ref(null)

onMounted(async () => {
  try {
    const data = await apiClient.get('/movie/all')
    movies.value = data.map(m => new Movie(m))
  } catch (err) {
    console.error('Failed to fetch movies:', err)
  }

  animate(
      bgRef.value,
      { backgroundPosition: ['0% 50%', '100% 50%', '0% 50%'] },
      { duration: 8, repeat: Infinity, easing: 'linear' }
  )
})

function onEnter(index) {
  const card = cardRefs.value[index]
  const text = textRefs.value[index]

  animate(card, { scale: 1.05 }, { duration: 0.25, easing: 'ease-out' })
  animate(card.querySelector('.overlay'), { opacity: 0.55 }, { duration: 0.25 })
  animate(text, { opacity: 1, y: 0 }, { duration: 0.3 })
}

function onLeave(index) {
  const card = cardRefs.value[index]
  const text = textRefs.value[index]

  animate(card, { scale: 1 }, { duration: 0.25, easing: 'ease-out' })
  animate(card.querySelector('.overlay'), { opacity: 0 }, { duration: 0.25 })
  animate(text, { opacity: 0, y: 20 }, { duration: 0.2 })
}
</script>

<template>
  <div ref="bgRef" class="page-bg">
    <div class="container-fluid px-4 py-4">
      <div class="row g-3 justify-content-center">
        <div
            v-for="(movie, index) in movies.slice(0, 5)"
            :key="movie.id"
            class="col-6 col-sm-4 col-md-3 col-xl-2"
        >
          <div
              :ref="el => cardRefs[index] = el"
              class="movie-card"
              @mouseenter="onEnter(index)"
              @mouseleave="onLeave(index)"
          >
            <img
                class="image"
                :src="`${movie.thumbnail}`"
                :alt="movie.title"
            />
            <div class="overlay"></div>
            <div :ref="el => textRefs[index] = el" class="text">
              <h2>{{ movie.title }}</h2>
              <p>{{ movie.description }}</p>
              <div class="tags">
                <span v-for="tag in movie.tags" :key="tag.id" class="tag">
                  {{ tag.name }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-bg {
  min-height: 100vh;
  background: linear-gradient(270deg, #0f0c29, #302b63, #24243e, #1a1a2e, #16213e);
  background-size: 400% 400%;
}

.movie-card {
  position: relative;
  width: 100%;
  aspect-ratio: 2 / 3;
  overflow: hidden;
  border-radius: 16px;
  cursor: pointer;
  transform-origin: center;
  background: #111;
}

.image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.overlay {
  position: absolute;
  inset: 0;
  background: black;
  opacity: 0;
}

.text {
  position: absolute;
  bottom: 20px;
  left: 20px;
  right: 20px;
  color: white;
  opacity: 0;
  transform: translateY(20px);
}

.text h2 {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
}

.text p {
  margin: 5px 0 0;
  font-size: 12px;
  opacity: 0.8;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 8px;
}

.tag {
  background: rgba(255, 255, 255, 0.25);
  border-radius: 12px;
  padding: 2px 10px;
  font-size: 11px;
}
</style>