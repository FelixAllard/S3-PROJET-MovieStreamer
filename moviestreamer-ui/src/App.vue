<script setup>
import Navbar from '/src/views/Navbar.vue'
import CinemaShell from '/src/components/CinemaShell.vue'
import PixelField from '/src/components/PixelField.vue'
import Footer from '/src/components/Footer.vue'
</script>

<template>
  <Navbar />
  <CinemaShell contentMaxWidth="1200px">
    <template #left>
      <PixelField :columns="8" :rows="26" :intervalMs="140" :flipCount="5" />
    </template>

    <template #right>
      <PixelField :columns="8" :rows="26" :intervalMs="170" :flipCount="6" />
    </template>

    <div class="page-bg">
      <div class="container-fluid px-3 px-md-4 py-4">
        <router-view />
      </div>
      <Footer />
    </div>
  </CinemaShell>
</template>
