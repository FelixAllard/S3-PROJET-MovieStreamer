<script setup>
import { ref, onMounted, watch, nextTick } from 'vue'
import { animate } from 'motion-v'

const props = defineProps({
  text: {
    type: String,
    required: true
  },
  speed: {
    type: Number,
    default: 40
  },
  glow: {
    type: Boolean,
    default: true
  },
  size: {
    type: String,
    default: 'display-3'
    // Bootstrap: display-1 → display-6 OR fs-1 → fs-6
  },
  showCursor: {
    type: Boolean,
    default: true
  }
})

const displayText = ref('')
const containerRef = ref(null)

let index = 0
let interval = null

function startTyping() {
  displayText.value = ''
  index = 0

  clearInterval(interval)

  interval = setInterval(async () => {
    displayText.value += props.text[index]
    index++

    await nextTick()

    if (containerRef.value) {
      animate(containerRef.value, {
        scale: [1, 1.02, 1],
        filter: props.glow
            ? ['brightness(1)', 'brightness(1.3)', 'brightness(1)']
            : undefined
      }, {
        duration: 0.25
      })
    }

    if (index >= props.text.length) {
      clearInterval(interval)
      finishAnimation()
    }

  }, props.speed)
}

function finishAnimation() {
  if (!containerRef.value) return

  animate(containerRef.value, {
    scale: [1, 1.06, 1],
    textShadow: [
      '0 0 0px rgba(139,92,246,0)',
      '0 0 25px rgba(139,92,246,0.8)',
      '0 0 0px rgba(139,92,246,0)'
    ]
  }, {
    duration: 0.8,
    easing: 'ease-out'
  })
}

onMounted(() => {
  startTyping()
})

watch(() => props.text, () => {
  startTyping()
})
</script>

<template>
  <div class="title-wrapper text-center">

    <h1
        ref="containerRef"
        class="type-text text-white fw-black"
        :class="size"
    >
      {{ displayText }}

      <span v-if="showCursor" class="cursor">|</span>
    </h1>

  </div>
</template>

<style scoped>
.title-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
}

.type-text {
  letter-spacing: 1px;
  text-shadow: 0 0 15px rgba(139,92,246,0.35);
  font-weight: 900;
}

.cursor {
  margin-left: 4px;
  animation: blink 0.8s infinite;
  color: #a78bfa;
}

@keyframes blink {
  0%, 50% { opacity: 1; }
  51%, 100% { opacity: 0; }
}
</style>