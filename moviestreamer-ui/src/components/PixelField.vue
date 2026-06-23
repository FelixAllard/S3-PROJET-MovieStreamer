<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'

const props = defineProps({
  columns: { type: Number, default: 10 },
  rows: { type: Number, default: 24 },
  intervalMs: { type: Number, default: 180 },
  flipCount: { type: Number, default: 6 }
})

const palette = [
  '#12091f',
  '#1a1030',
  '#24133f',
  '#2d1650',
  '#3a1d68',
  '#4c1d95',
  '#5b21b6',
  '#6d28d9',
  '#7c3aed',
  '#8b5cf6'
]

const pixels = ref([])
let timer = null

const total = computed(() => props.columns * props.rows)

function buildPixels() {
  pixels.value = Array.from({ length: total.value }, () => ({
    color: palette[Math.floor(Math.random() * 4)],
    glow: Math.random() > 0.88
  }))
}

function flipPixels() {
  if (!pixels.value.length) return

  for (let i = 0; i < props.flipCount; i++) {
    const index = Math.floor(Math.random() * pixels.value.length)
    const color = palette[Math.floor(Math.random() * palette.length)]

    pixels.value[index] = {
      color,
      glow: Math.random() > 0.8
    }
  }
}

onMounted(() => {
  buildPixels()
  timer = window.setInterval(flipPixels, props.intervalMs)
})

onBeforeUnmount(() => {
  if (timer) clearInterval(timer)
})
</script>

<template>
  <div
      class="pixel-field"
      :style="{
      gridTemplateColumns: `repeat(${columns}, 1fr)`,
      gridTemplateRows: `repeat(${rows}, 1fr)`
    }"
  >
    <div
        v-for="(pixel, index) in pixels"
        :key="index"
        class="pixel"
        :class="{ glow: pixel.glow }"
        :style="{ backgroundColor: pixel.color }"
    />
  </div>
</template>

<style scoped>
.pixel-field {
  width: 100%;
  height: 100%;
  min-height: 100vh;
  display: grid;
  gap: 6px;
  padding: 10px;
}

.pixel {
  aspect-ratio: 1 / 1;
  border-radius: 4px;
  opacity: 0.95;
  transition:
      background-color 240ms ease,
      transform 220ms ease,
      box-shadow 260ms ease,
      opacity 220ms ease;
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.03);
}

.pixel.glow {
  box-shadow:
      0 0 10px rgba(139, 92, 246, 0.18),
      0 0 18px rgba(139, 92, 246, 0.12),
      inset 0 0 0 1px rgba(255, 255, 255, 0.06);
  transform: scale(1.03);
}
</style>
