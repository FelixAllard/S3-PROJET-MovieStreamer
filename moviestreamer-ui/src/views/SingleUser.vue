<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { animate } from 'motion-v'
import apiClient from '/src/services/ApiClient.js'
import { getLoggedUser } from '/src/utils/auth.js'
import { User } from '/src/entities/User.js' 

const route = useRoute()
const router = useRouter()

const userProfile = ref(null)
const profileRoleDisplay = ref('user') 
const isViewingSelf = ref(false)
const loading = ref(true)
const errorMessage = ref('')

const cardRef = ref(null)

async function fetchUserProfile() {
  try {
    loading.value = true
    errorMessage.value = ''
    
    const targetId = route.params.id
    isViewingSelf.value = (targetId === 'me')

    const endpointUrl = isViewingSelf.value ? '/user/me' : `/user/${targetId}`
    const data = await apiClient.get(endpointUrl)
    userProfile.value = data

    if (isViewingSelf.value) {
      const token = localStorage.getItem('token')
      if (token) {
        const base64Url = token.split('.')[1]
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
        const payload = JSON.parse(atob(base64))
        
        const roles = payload.realm_access?.roles || []
        profileRoleDisplay.value = roles.includes('admin') ? 'admin' : 'user'
      }
    } else {
        if (data.realmRoles?.includes('admin') || data.username?.toLowerCase().includes('admin')) {
            profileRoleDisplay.value = 'admin'
        } else {
            profileRoleDisplay.value = 'user'
        }
    }

  } catch (err) {
    console.error('Failed fetching profile view record:', err)
    errorMessage.value = 'Security Intercept: Access denied or record not found.'
  } finally {
    loading.value = false
  }
}

watch(() => route.params.id, () => {
  fetchUserProfile()
})

onMounted(() => {
  fetchUserProfile()
  if (cardRef.value) {
    animate(cardRef.value, { opacity: [0, 1], y: [30, 0] }, { duration: 0.5, easing: 'ease-out' })
  }
})
</script>

<template>
  <div class="profile-page-wrapper py-5">
    <div class="background-overlay"></div>

    <div class="container d-flex justify-content-center align-items-center position-relative z-2 min-vh-75">
      <div v-if="loading" class="text-center text-white my-5">
        <div class="spinner-border text-purple mb-3" role="status"></div>
        <p class="text-muted-custom">Assembling profile metrics...</p>
      </div>

      <div v-else-if="errorMessage" class="error-panel text-center p-5">
        <h3 class="text-danger-custom mb-3">🔒 Security Intercept</h3>
        <p class="text-muted-custom mb-4">{{ errorMessage }}</p>
        <button class="btn btn-outline-purple btn-sm" @click="router.back()">Return</button>
      </div>

      <div v-else ref="cardRef" class="profile-glass-card p-4 p-md-5 w-100">
        <div class="d-flex align-items-center gap-3 mb-4 border-bottom-purple pb-3">
          <div class="avatar-glow d-flex align-items-center justify-content-center">
            <span class="fs-3 text-white fw-bold">{{ userProfile.username?.charAt(0).toUpperCase() }}</span>
          </div>
          <div>
            <p class="eyebrow mb-0">Identity Profile Card</p>
            <h2 class="profile-title mb-0">@{{ userProfile.username }}</h2>
          </div>
        </div>

        <div class="row g-4">
          <div v-if="!isViewingSelf" class="col-12 col-md-6">
            <div class="meta-label">Database Identifier Key</div>
            <div class="meta-value font-monospace text-purple-light">{{ userProfile.id }}</div>
          </div>

          <div class="col-12 col-md-6">
            <div class="meta-label">Primary Email Address</div>
            <div class="meta-value text-white">{{ userProfile.email || 'No email attached' }}</div>
          </div>

          <div class="col-12 col-md-6">
            <div class="meta-label">System Authorization Role Access</div>
            <div class="mt-2">
              <span class="badge role-badge capitalize">{{ profileRoleDisplay }}</span>
            </div>
          </div>
        </div>

        <div class="mt-5 pt-3 d-flex gap-3">
          <button class="btn back-btn btn-sm" @click="router.back()">← Return</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.profile-page-wrapper {
  position: relative;
  min-height: 100vh;
  background: #05030a;
}

.background-overlay {
  position: absolute;
  inset: 0;
  background: 
    radial-gradient(circle at 50% 30%, rgba(139, 92, 246, 0.1), transparent 50%);
  pointer-events: none;
}

.profile-glass-card {
  max-width: 680px;
  background: rgba(12, 8, 24, 0.75);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 28px;
  backdrop-filter: blur(20px);
  box-shadow: 
    0 30px 70px rgba(0, 0, 0, 0.6),
    0 0 50px rgba(139, 92, 246, 0.06);
}

.avatar-glow {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #7c3aed, #4c1d95);
  border-radius: 18px;
  box-shadow: 0 0 20px rgba(124, 58, 237, 0.4);
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.16em;
  font-size: 0.75rem;
  color: #c4b5fd;
  font-weight: 700;
}

.profile-title {
  font-size: 1.8rem;
  font-weight: 800;
  color: #fff;
}

.border-bottom-purple {
  border-bottom: 1px solid rgba(139, 92, 246, 0.15);
}

.meta-label {
  font-size: 0.78rem;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  color: rgba(255, 255, 255, 0.45);
  font-weight: 600;
  margin-bottom: 0.25rem;
}

.meta-value {
  font-size: 1.05rem;
  font-weight: 500;
}

.text-purple-light {
  color: #d8b4fe !important;
}

.role-badge {
  background: rgba(139, 92, 246, 0.15) !important;
  border: 1px solid rgba(139, 92, 246, 0.3) !important;
  color: #c4b5fd !important;
  padding: 0.5rem 1rem;
  font-weight: 700;
  font-size: 0.8rem;
  border-radius: 10px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.back-btn {
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.7);
  border-radius: 12px;
  padding: 0.6rem 1.2rem;
  font-weight: 600;
  transition: all 0.2s ease;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.08);
  color: #fff;
}

.error-panel {
  background: rgba(12, 8, 24, 0.72);
  border-radius: 24px;
  border: 1px solid rgba(239, 68, 68, 0.2);
}

.text-danger-custom {
  color: #fca5a5 !important;
}

.text-muted-custom {
  color: rgba(255, 255, 255, 0.5) !important;
}

.text-purple {
  color: #a78bfa !important;
}
</style>