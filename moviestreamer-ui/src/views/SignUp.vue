<script setup>
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap'

import { ref, onMounted } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { animate } from 'motion-v'
import apiClient from '/src/services/ApiClient.js'

const router = useRouter()

const username = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const loading = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

const pageRef = ref(null)
const contentRef = ref(null)

onMounted(() => {
  if (pageRef.value) {
    animate(pageRef.value, { opacity: [0, 1] }, { duration: 0.45, easing: 'ease-out' })
  }
  if (contentRef.value) {
    animate(contentRef.value, { opacity: [0, 1], y: [30, 0], scale: [0.98, 1] }, { duration: 0.55, easing: 'ease-out' })
  }
})

async function handleSignUp() {
  if (!username.value || !email.value || !password.value || !confirmPassword.value) {
    errorMessage.value = 'Please fill out all required fields.'
    return
  }

  if (password.value !== confirmPassword.value) {
    errorMessage.value = 'Passwords do not match. Please verify your inputs.'
    return
  }

  try {
    loading.value = true
    errorMessage.value = ''
    successMessage.value = ''

    await apiClient.post('/user/register', {
      username: username.value,
      email: email.value,
      keycloakId: password.value
    })

    successMessage.value = 'Account created successfully! Redirecting to login portal...'
    
    setTimeout(() => {
      router.push('/login')
    }, 2200)

  } catch (err) {
    console.error('Registration failure exception:', err)
    errorMessage.value = err.response?.data?.message || 'Failed to create profile. Username or Email might already be taken.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div ref="pageRef" class="login-page">
    <div class="background-layer"></div>
    <div class="background-overlay"></div>

    <div class="container d-flex align-items-center justify-content-center min-vh-100 px-3 position-relative">
      <div ref="contentRef" class="login-panel">
        <div class="text-center mb-4">
          <p class="eyebrow mb-2">Join CinePulse</p>
          <h1 class="login-heading mb-2">Create Account</h1>
          <p class="text-muted-custom small">Start tracking your custom entertainment catalogs</p>
        </div>

        <form @submit.prevent="handleSignUp">
          <div v-if="errorMessage" class="alert alert-danger-custom text-center mb-4" role="alert">
            {{ errorMessage }}
          </div>

          <div v-if="successMessage" class="alert alert-success-custom text-center mb-4" role="alert">
            {{ successMessage }}
          </div>

          <div class="mb-3">
            <label for="username" class="form-label-custom">Username</label>
            <input
                id="username"
                v-model="username"
                type="text"
                class="form-control form-control-custom"
                placeholder="Pick a unique username"
                required
                :disabled="loading"
            />
          </div>

          <div class="mb-3">
            <label for="email" class="form-label-custom">Email Address</label>
            <input
                id="email"
                v-model="email"
                type="email"
                class="form-control form-control-custom"
                placeholder="name@example.com"
                required
                :disabled="loading"
            />
          </div>

          <div class="mb-3">
            <label for="password" class="form-label-custom">Password</label>
            <input
                id="password"
                v-model="password"
                type="password"
                class="form-control form-control-custom"
                placeholder="••••••••"
                required
                :disabled="loading"
            />
          </div>

          <div class="mb-4">
            <label for="confirmPassword" class="form-label-custom">Confirm Password</label>
            <input
                id="confirmPassword"
                v-model="confirmPassword"
                type="password"
                class="form-control form-control-custom"
                placeholder="••••••••"
                required
                :disabled="loading"
            />
          </div>

          <button
              type="submit"
              class="btn btn-login w-100 d-flex align-items-center justify-content-center"
              :disabled="loading"
          >
            <span v-if="loading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
            {{ loading ? 'Registering user...' : 'Sign Up' }}
          </button>
        </form>

        <div class="text-center mt-4">
          <p class="mb-0 small text-muted-custom">
            Already have an account? <RouterLink to="/login" class="signup-link font-weight-bold">Sign In</RouterLink>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

.login-page {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background: #05030a;
}

.background-layer {
  position: absolute;
  inset: 0;
  background-image: url('https://images.unsplash.com/photo-1536440136628-849c177e76a1?q=80&w=1925');
  background-size: cover;
  background-position: center;
  filter: blur(40px) scale(1.1);
  opacity: 0.25;
}

.background-overlay {
  position: absolute;
  inset: 0;
  background:
      linear-gradient(180deg, rgba(5, 3, 10, 0.45), rgba(5, 3, 10, 0.92)),
      radial-gradient(circle at top left, rgba(139, 92, 246, 0.25), transparent 35%),
      radial-gradient(circle at bottom right, rgba(168, 85, 247, 0.16), transparent 30%);
}

.login-panel {
  position: relative;
  z-index: 2;
  width: 100%;
  max-width: 460px;
  background: rgba(12, 8, 24, 0.72);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 28px;
  backdrop-filter: blur(16px);
  padding: 2.5rem 2rem;
  box-shadow:
      0 20px 60px rgba(0, 0, 0, 0.55),
      0 0 40px rgba(139, 92, 246, 0.1);
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.16em;
  font-size: 0.78rem;
  color: #c4b5fd;
  font-weight: 700;
}

.login-heading {
  font-size: 2rem;
  font-weight: 800;
  color: #fff;
}

.text-muted-custom {
  color: rgba(255, 255, 255, 0.6);
}

.form-label-custom {
  font-size: 0.85rem;
  color: #ddd6fe;
  font-weight: 600;
  margin-bottom: 0.45rem;
}

.form-control-custom {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #fff !important;
  border-radius: 12px;
  padding: 0.75rem 1rem;
  transition: all 0.2s ease;
}

.form-control-custom:focus {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(139, 92, 246, 0.6);
  box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.25);
  outline: none;
}

.form-control-custom::placeholder {
  color: rgba(255, 255, 255, 0.3);
}

.btn-login {
  background: linear-gradient(135deg, #7c3aed, #6d28d9);
  border: none;
  color: white;
  border-radius: 12px;
  padding: 0.8rem;
  font-weight: 600;
  transition: transform 0.15s ease, opacity 0.2s ease;
}

.btn-login:hover {
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
  color: white;
}

.btn-login:active {
  transform: scale(0.98);
}

.btn-login:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.signup-link {
  color: #c4b5fd;
  text-decoration: none;
  font-weight: 500;
}

.signup-link:hover {
  color: #fff;
  text-decoration: underline;
}

.alert-danger-custom {
  background: rgba(239, 68, 68, 0.15);
  border: 1px solid rgba(239, 68, 68, 0.3);
  color: #fca5a5;
  border-radius: 12px;
  padding: 0.75rem;
  font-size: 0.9rem;
}

.alert-success-custom {
  background: rgba(16, 185, 129, 0.15);
  border: 1px solid rgba(16, 185, 129, 0.3);
  color: #a7f3d0;
  border-radius: 12px;
  padding: 0.75rem;
  font-size: 0.9rem;
}
</style>