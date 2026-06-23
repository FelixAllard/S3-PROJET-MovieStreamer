<script setup>
import { motion } from 'motion-v'

const orbs = [
  { size: 180, top: '10%', left: '15%', delay: 0 },
  { size: 120, top: '42%', left: '55%', delay: 0.6 },
  { size: 220, top: '68%', left: '10%', delay: 1.1 },
  { size: 140, top: '20%', left: '60%', delay: 0.9 }
]
</script>

<template>
  <div class="ambient-wrap">
    <motion.div
        v-for="(orb, index) in orbs"
        :key="index"
        class="orb"
        :style="{
        width: `${orb.size}px`,
        height: `${orb.size}px`,
        top: orb.top,
        left: orb.left
      }"
        :initial="{ opacity: 0.35, y: 0, scale: 1 }"
        :animate="{
        opacity: [0.3, 0.55, 0.3],
        y: [0, -25, 0],
        scale: [1, 1.08, 1]
      }"
        :transition="{
        duration: 6,
        repeat: Infinity,
        ease: 'easeInOut',
        delay: orb.delay
      }"
    />
  </div>
</template>

<style scoped>
.ambient-wrap {
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 100vh;
}

.orb {
  position: absolute;
  border-radius: 999px;
  background: radial-gradient(circle, rgba(168, 85, 247, 0.35), rgba(139, 92, 246, 0.08), transparent 70%);
  filter: blur(8px);
  pointer-events: none;
}
</style>
