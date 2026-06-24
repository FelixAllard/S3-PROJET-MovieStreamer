<script setup>
import { ref, onMounted } from 'vue'
import { animate } from 'motion-v'
import apiClient from '/src/services/ApiClient.js'
import { useRouter } from 'vue-router'

const users = ref([])
const loading = ref(true)
const errorMessage = ref('')

const pageRef = ref(null)
const tableRef = ref(null)

const router = useRouter()

onMounted(async () => {
  if (pageRef.value) animate(pageRef.value, { opacity: [0, 1] }, { duration: 0.45 })
  if (tableRef.value) animate(tableRef.value, { opacity: [0, 1], y: [20, 0] }, { duration: 0.55, delay: 0.1 })
  
  await fetchAllUsers()
})

function goToUser(id) {
  router.push(`/user/${id}`)
}

async function fetchAllUsers() {
  try {
    loading.value = true
    errorMessage.value = ''
    
    const data = await apiClient.get('/user/all')
    users.value = data || []
  } catch (err) {
    console.error('Failed to load user catalog:', err)
    if (err.status === 403) {
      errorMessage.value = 'Access Denied: You do not have permission to view this resource.'
    } else {
      errorMessage.value = 'Failed to fetch user directory records from backend service.'
    }
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div ref="pageRef" class="admin-users-page py-5">
    <div class="background-overlay"></div>
    
    <div class="container position-relative z-2">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
          <p class="eyebrow mb-1">System Management</p>
          <h1 class="page-heading">User Directory</h1>
        </div>
        <button class="btn refresh-btn btn-sm d-flex align-items-center gap-2" @click="fetchAllUsers" :disabled="loading">
          <span v-if="loading" class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
          <span>{{ loading ? 'Updating...' : '🔄 Refresh' }}</span>
        </button>
      </div>

      <div v-if="loading && users.length === 0" class="state-panel text-center text-white">
        <div class="spinner-border text-purple mb-3" role="status"></div>
        <p class="mb-0 text-muted-custom">Retrieving protected profile catalogs...</p>
      </div>

      <div v-else-if="errorMessage" class="state-panel alert-panel text-center">
        <h3 class="text-danger-custom mb-2">Security Intercept</h3>
        <p class="text-muted-custom mb-0">{{ errorMessage }}</p>
      </div>

      <div v-else ref="tableRef" class="admin-panel-card table-responsive">
        <table class="table custom-admin-table align-middle mb-0">
          <thead>
            <tr>
              <th scope="col">ID</th>
              <th scope="col">Username</th>
              <th scope="col">Email Address</th>
              <th scope="col" class="text-center">Status</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.id" class="table-row-hover" style="cursor: pointer;" @click="goToUser(user.id)">
              <td class="text-muted-custom small font-monospace">{{ user.id }}</td>
              <td class="fw-bold text-white">{{ user.username || 'N/A' }}</td>
              <td class="text-purple-light">{{ user.email || 'N/A' }}</td>
              <td class="text-center">
                <span class="status-pill" :class="user.enabled !== false ? 'active' : 'disabled'">
                  {{ user.enabled !== false ? 'Active' : 'Suspended' }}
                </span>
              </td>
            </tr>
            <tr v-if="users.length === 0">
              <td colspan="4" class="text-center py-4 text-muted-custom">No users found inside the database.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<style scoped>
.admin-users-page {
  position: relative;
  min-height: 100vh;
  background: #05030a;
}

.background-overlay {
  position: absolute;
  inset: 0;
  background: 
    radial-gradient(circle at 80% 20%, rgba(139, 92, 246, 0.12), transparent 45%),
    radial-gradient(circle at 20% 80%, rgba(124, 58, 237, 0.06), transparent 50%);
  pointer-events: none;
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.16em;
  font-size: 0.78rem;
  color: #c4b5fd;
  font-weight: 700;
}

.page-heading {
  font-size: 2.2rem;
  font-weight: 800;
  color: #fff;
}

/* Glassmorphic Container Fixes Bootstrap Resets */
.admin-panel-card {
  background: rgba(12, 8, 24, 0.72) !important;
  border: 1px solid rgba(255, 255, 255, 0.08) !important;
  border-radius: 24px;
  backdrop-filter: blur(16px);
  padding: 1rem;
  box-shadow: 
    0 20px 50px rgba(0, 0, 0, 0.55),
    0 0 40px rgba(139, 92, 246, 0.05);
}

/* Clear out Bootstrap default properties */
.custom-admin-table {
  background: transparent !important;
  --bs-table-bg: transparent !important;
  --bs-table-color: #fff !important;
  border-collapse: separate;
  border-spacing: 0 4px; /* Creates clean row gaps */
}

.custom-admin-table th {
  color: #c4b5fd !important;
  font-weight: 700;
  text-transform: uppercase;
  font-size: 0.8rem;
  letter-spacing: 0.08em;
  padding: 1.2rem 1rem;
  border-bottom: 2px solid rgba(139, 92, 246, 0.25) !important;
  background: transparent !important;
}

.custom-admin-table td {
  padding: 1.2rem 1rem;
  border: none !important;
  background: transparent !important;
  color: #fff !important;
}

/* Modern Glowing Row Effect */
.table-row-hover {
  transition: background-color 0.25s ease, transform 0.2s ease;
  border-radius: 12px;
}

.table-row-hover:hover {
  background: rgba(139, 92, 246, 0.08) !important;
  backdrop-filter: blur(4px);
}

.table-row-hover td {
  border-top: 1px solid rgba(255, 255, 255, 0.02) !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.02) !important;
}

.table-row-hover td:first-child {
  border-left: 1px solid rgba(255, 255, 255, 0.02) !important;
  border-top-left-radius: 12px;
  border-bottom-left-radius: 12px;
}

.table-row-hover td:last-child {
  border-right: 1px solid rgba(255, 255, 255, 0.02) !important;
  border-top-right-radius: 12px;
  border-bottom-right-radius: 12px;
}

/* Context Elements */
.text-muted-custom {
  color: rgba(255, 255, 255, 0.5) !important;
}

.text-purple-light {
  color: #d8b4fe !important;
}

.text-danger-custom {
  color: #fca5a5 !important;
}

.text-purple {
  color: #a78bfa !important;
}

/* Status Pill Designs */
.status-pill {
  padding: 0.4rem 0.85rem;
  border-radius: 10px;
  font-size: 0.78rem;
  font-weight: 700;
  display: inline-block;
  letter-spacing: 0.03em;
}

.status-pill.active {
  background: rgba(16, 185, 129, 0.12) !important;
  border: 1px solid rgba(16, 185, 129, 0.3) !important;
  color: #34d399 !important;
}

.status-pill.disabled {
  background: rgba(239, 68, 68, 0.12) !important;
  border: 1px solid rgba(239, 68, 68, 0.3) !important;
  color: #fca5a5 !important;
}

/* Refresh Button Design */
.refresh-btn {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #fff;
  border-radius: 12px;
  padding: 0.6rem 1.1rem;
  font-weight: 600;
  transition: all 0.2s ease;
}

.refresh-btn:hover:not(:disabled) {
  background: rgba(139, 92, 246, 0.16);
  border-color: rgba(139, 92, 246, 0.4);
  box-shadow: 0 0 15px rgba(139, 92, 246, 0.15);
}

/* State Message Panels */
.state-panel {
  background: rgba(12, 8, 24, 0.72);
  border-radius: 24px;
  padding: 5rem 2rem;
  border: 1px solid rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(16px);
}

.alert-panel {
  border-color: rgba(239, 68, 68, 0.25);
  box-shadow: 0 0 40px rgba(239, 68, 68, 0.08);
}
</style>