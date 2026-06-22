<script setup>
import { ref } from 'vue'
import { motion } from 'motion-v'

const hoveredItem = ref(null)

const navItems = [
  { label: 'Home', to: '/' },
  { label: 'Movies', to: '/movies' }
]
</script>

<template>
  <motion.nav
      class="navbar navbar-expand-lg movie-navbar px-2 px-lg-3"
      :initial="{ y: -30, opacity: 0 }"
      :animate="{ y: 0, opacity: 1 }"
      :transition="{ duration: 0.7, ease: 'easeOut' }"
  >
    <div class="container-fluid position-relative">
      <motion.div
          class="brand-wrap"
          :animate="{
          boxShadow: [
            '0 0 0 rgba(139,92,246,0)',
            '0 0 22px rgba(139,92,246,0.22)',
            '0 0 0 rgba(139,92,246,0)'
          ]
        }"
          :transition="{ duration: 3.5, repeat: Infinity, ease: 'easeInOut' }"
      >
        <RouterLink class="navbar-brand movie-brand" to="/">
          <span class="brand-text">CinePulse</span>
        </RouterLink>
      </motion.div>

      <button
          class="navbar-toggler border-0 shadow-none"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarNav"
          aria-controls="navbarNav"
          aria-expanded="false"
          aria-label="Toggle navigation"
      >
        <span class="navbar-toggler-icon custom-toggler"></span>
      </button>

      <div class="collapse navbar-collapse justify-content-end" id="navbarNav">
        <ul class="navbar-nav align-items-lg-center gap-lg-2 mt-3 mt-lg-0">
          <li
              v-for="item in navItems"
              :key="item.to"
              class="nav-item position-relative"
              @mouseenter="hoveredItem = item.to"
              @mouseleave="hoveredItem = null"
          >
            <RouterLink
                :to="item.to"
                class="nav-link movie-link px-3 py-2"
            >
              <span class="link-label">{{ item.label }}</span>

              <motion.span
                  class="hover-glow"
                  :initial="{ opacity: 0, scaleX: 0.4 }"
                  :animate="hoveredItem === item.to
                  ? { opacity: 1, scaleX: 1 }
                  : { opacity: 0, scaleX: 0.4 }"
                  :transition="{ duration: 0.25, ease: 'easeOut' }"
              />

              <motion.span
                  class="hover-line"
                  :initial="{ width: '0%' }"
                  :animate="hoveredItem === item.to
                  ? { width: '100%' }
                  : { width: '0%' }"
                  :transition="{ duration: 0.3, ease: 'easeOut' }"
              />
            </RouterLink>
          </li>
        </ul>
      </div>
    </div>
  </motion.nav>
</template>

<style scoped>
.movie-navbar {
  position: sticky;
  top: 0;
  z-index: 1050;
  background:
      linear-gradient(90deg, rgba(7, 4, 15, 0.96), rgba(24, 12, 44, 0.92), rgba(10, 6, 22, 0.96));
  backdrop-filter: blur(14px);
  border-bottom: 1px solid rgba(139, 92, 246, 0.18);
  box-shadow:
      0 8px 30px rgba(0, 0, 0, 0.35),
      inset 0 -1px 0 rgba(255, 255, 255, 0.03);
  overflow: hidden;
}

.movie-navbar::before {
  content: '';
  position: absolute;
  inset: 0;
  background:
      linear-gradient(120deg, transparent 0%, rgba(139, 92, 246, 0.08) 35%, transparent 70%);
  pointer-events: none;
  animation: navbarSheen 6s linear infinite;
}

@keyframes navbarSheen {
  0% {
    transform: translateX(-35%);
  }
  100% {
    transform: translateX(35%);
  }
}

.brand-wrap {
  border-radius: 999px;
  padding: 0.15rem 0.6rem;
}

.movie-brand {
  position: relative;
  font-size: 1.35rem;
  font-weight: 800;
  letter-spacing: 0.03em;
  color: #ffffff !important;
  text-transform: uppercase;
}

.brand-text {
  background: linear-gradient(90deg, #ffffff, #d8b4fe, #a78bfa);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-size: 200% auto;
  animation: brandFlow 4s linear infinite;
}

@keyframes brandFlow {
  0% {
    background-position: 0% center;
  }
  100% {
    background-position: 200% center;
  }
}

.movie-link {
  position: relative;
  color: rgba(255, 255, 255, 0.84) !important;
  font-weight: 600;
  border-radius: 12px;
  transition:
      color 0.25s ease,
      background-color 0.25s ease,
      transform 0.2s ease;
  overflow: hidden;
}

.movie-link:hover {
  color: #ffffff !important;
  background: rgba(139, 92, 246, 0.08);
  transform: translateY(-1px);
}

.link-label {
  position: relative;
  z-index: 2;
}

.hover-line {
  position: absolute;
  left: 0;
  bottom: 0;
  height: 2px;
  background: linear-gradient(90deg, #7c3aed, #c084fc, #7c3aed);
  border-radius: 999px;
  z-index: 2;
}

.hover-glow {
  position: absolute;
  inset: 0;
  border-radius: 12px;
  background: radial-gradient(circle at center, rgba(139, 92, 246, 0.18), transparent 72%);
  z-index: 1;
  pointer-events: none;
}

.custom-toggler {
  filter: invert(1) brightness(2);
}

.navbar-toggler:focus {
  box-shadow: none;
}

.router-link-active.movie-link {
  color: #ffffff !important;
  background: rgba(139, 92, 246, 0.12);
  box-shadow:
      inset 0 0 0 1px rgba(192, 132, 252, 0.22),
      0 0 18px rgba(139, 92, 246, 0.12);
}

.router-link-active.movie-link .hover-line {
  width: 100% !important;
}

@media (max-width: 991.98px) {
  .movie-navbar {
    padding-top: 0.5rem;
    padding-bottom: 0.5rem;
  }

  .movie-link {
    text-align: center;
  }

  .navbar-collapse {
    margin-top: 0.75rem;
    padding: 0.75rem;
    border-radius: 16px;
    background: rgba(255, 255, 255, 0.04);
    border: 1px solid rgba(139, 92, 246, 0.12);
  }
}
</style>
