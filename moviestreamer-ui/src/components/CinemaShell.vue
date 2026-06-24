<script setup>
const props = defineProps({
  contentMaxWidth: {
    type: String,
    default: '1200px'
  }
})
</script>

<template>
  <div class="cinema-shell" :style="{ '--content-max-width': props.contentMaxWidth }">
    <aside class="side-rail left-rail">
      <slot name="left" />
    </aside>

    <main class="center-stage">
      <slot />
    </main>

    <aside class="side-rail right-rail">
      <slot name="right" />
    </aside>
  </div>
</template>

<style scoped>
.cinema-shell {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(0, var(--content-max-width)) minmax(0, 1fr);
  width: 100%;
  min-height: 100vh;
}

.center-stage {
  min-width: 0;
  width: 100%;
}

.side-rail {
  position: relative;
  overflow: hidden;
  min-width: 0;
}

@media (max-width: 1199.98px) {
  .cinema-shell {
    grid-template-columns: 1fr;
  }

  .side-rail {
    display: none;
  }
}
</style>
