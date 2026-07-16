<template>
  <div class="tags-page">
    <!-- Page Header -->
    <motion.div
        class="page-header"
        :initial="{ opacity: 0, y: -24 }"
        :animate="{ opacity: 1, y: 0 }"
        :transition="{ duration: 0.5, ease: 'easeOut' }"
    >
      <div class="header-content">
        <div class="header-title">
          <span class="header-icon">🏷️</span>
          <div>
            <h1>Tag Management</h1>
            <p class="subtitle">Create, edit and delete tags</p>
          </div>
        </div>
        <button class="btn-create" @click="openCreateModal">
          <span class="btn-icon">+</span>
          New Tag
        </button>
      </div>
    </motion.div>

    <!-- Search Bar -->
    <motion.div
        class="search-section"
        :initial="{ opacity: 0, y: 16 }"
        :animate="{ opacity: 1, y: 0 }"
        :transition="{ duration: 0.5, delay: 0.15, ease: 'easeOut' }"
    >
      <div class="search-wrapper">
        <span class="search-icon">🔍</span>
        <input
            v-model="searchQuery"
            type="text"
            class="search-input"
            placeholder="Search tags..."
        />
        <button v-if="searchQuery" class="search-clear" @click="clearSearch">✕</button>
      </div>
      <div class="results-count">
        <span>{{ filteredTags.length }} tag{{ filteredTags.length !== 1 ? 's' : '' }} found</span>
      </div>
    </motion.div>

    <!-- Tags Grid -->
    <motion.div
        class="tags-container"
        :initial="{ opacity: 0 }"
        :animate="{ opacity: 1 }"
        :transition="{ duration: 0.4, delay: 0.25 }"
    >
      <TransitionGroup name="tag-list" tag="div" class="tags-grid">
        <div
            v-for="tag in filteredTags"
            :key="tag.id"
            class="tag-card"
        >
          <!-- View Mode -->
          <template v-if="editingTagId !== tag.id">
            <div class="tag-card-body">
              <span class="tag-pill">{{ tag.name }}</span>
              <span class="tag-id">#{{ tag.id }}</span>
            </div>
            <div class="tag-card-actions">
              <button class="btn-action btn-edit" @click="startEdit(tag)" title="Edit">✏️</button>
              <button class="btn-action btn-delete" @click="confirmDelete(tag)" title="Delete">🗑️</button>
            </div>
          </template>

          <!-- Edit Mode -->
          <template v-else>
            <div class="tag-edit-mode">
              <input
                  v-model="editingName"
                  class="edit-input"
                  type="text"
                  maxlength="255"
                  @keyup.enter="saveEdit(tag)"
                  @keyup.escape="cancelEdit"
                  ref="editInput"
                  autofocus
              />
              <div class="edit-actions">
                <button class="btn-action btn-save" @click="saveEdit(tag)" title="Save">✔️</button>
                <button class="btn-action btn-cancel" @click="cancelEdit" title="Cancel">✕</button>
              </div>
            </div>
          </template>
        </div>
      </TransitionGroup>

      <!-- Empty State -->
      <div v-if="filteredTags.length === 0 && !loading" class="empty-state">
        <div class="empty-icon">🏷️</div>
        <p v-if="searchQuery">No tags matching <strong>"{{ searchQuery }}"</strong></p>
        <p v-else>No tags yet. Create your first one!</p>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Loading tags...</p>
      </div>
    </motion.div>

    <!-- ─── Create Modal ─── -->
    <Transition name="modal-fade">
      <div v-if="showCreateModal" class="modal-overlay" @click.self="closeCreateModal">
        <motion.div
            class="modal-box"
            :initial="{ opacity: 0, scale: 0.85, y: -30 }"
            :animate="{ opacity: 1, scale: 1, y: 0 }"
            :transition="{ duration: 0.3, ease: 'easeOut' }"
        >
          <div class="modal-header">
            <h2>✨ Create New Tag</h2>
            <button class="modal-close" @click="closeCreateModal">✕</button>
          </div>
          <div class="modal-body">
            <label class="form-label">Tag Name</label>
            <input
                v-model="newTagName"
                class="form-input"
                type="text"
                placeholder="Enter tag name..."
                maxlength="255"
                @keyup.enter="submitCreate"
                ref="createInput"
            />
            <div v-if="createError" class="error-msg">⚠️ {{ createError }}</div>
          </div>
          <div class="modal-footer">
            <button class="btn-secondary" @click="closeCreateModal">Cancel</button>
            <button class="btn-primary" @click="submitCreate" :disabled="createLoading">
              <span v-if="createLoading" class="btn-spinner"></span>
              <span v-else>Create Tag</span>
            </button>
          </div>
        </motion.div>
      </div>
    </Transition>

    <!-- ─── Delete Confirmation Modal ─── -->
    <Transition name="modal-fade">
      <div v-if="showDeleteModal" class="modal-overlay" @click.self="cancelDelete">
        <motion.div
            class="modal-box modal-danger"
            :initial="{ opacity: 0, scale: 0.85, y: -30 }"
            :animate="{ opacity: 1, scale: 1, y: 0 }"
            :transition="{ duration: 0.3, ease: 'easeOut' }"
        >
          <div class="modal-header">
            <h2>🗑️ Delete Tag</h2>
            <button class="modal-close" @click="cancelDelete">✕</button>
          </div>
          <div class="modal-body">
            <p>Are you sure you want to delete the tag:</p>
            <div class="delete-target">
              <span class="tag-pill">{{ tagToDelete?.name }}</span>
            </div>
            <p class="delete-warning">This will remove the tag from all movies it is associated with. This action cannot be undone.</p>
          </div>
          <div class="modal-footer">
            <button class="btn-secondary" @click="cancelDelete">Cancel</button>
            <button class="btn-danger" @click="executeDelete" :disabled="deleteLoading">
              <span v-if="deleteLoading" class="btn-spinner"></span>
              <span v-else>Yes, Delete</span>
            </button>
          </div>
        </motion.div>
      </div>
    </Transition>

    <!-- Toast Notification -->
    <Transition name="toast">
      <div v-if="toast.show" class="toast" :class="toast.type">
        {{ toast.message }}
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { motion } from 'motion-v'
import apiClient from '../services/ApiClient.js'

// ─── State ───────────────────────────────────────────────────
const tags = ref([])
const loading = ref(false)
const searchQuery = ref('')

const editingTagId = ref(null)
const editingName = ref('')
const editInput = ref(null)

const showCreateModal = ref(false)
const newTagName = ref('')
const createError = ref('')
const createLoading = ref(false)
const createInput = ref(null)

const showDeleteModal = ref(false)
const tagToDelete = ref(null)
const deleteLoading = ref(false)

const toast = ref({ show: false, message: '', type: 'success' })

// ─── Computed ─────────────────────────────────────────────────
const filteredTags = computed(() => {
  if (!searchQuery.value.trim()) return tags.value
  const q = searchQuery.value.toLowerCase()
  return tags.value.filter(t => t.name.toLowerCase().includes(q))
})

// ─── API ──────────────────────────────────────────────────────
const ENDPOINT = '/tag'

async function fetchTags() {
  loading.value = true
  try {
    tags.value = await apiClient.get(`${ENDPOINT}/all`)
  } catch {
    showToast('Failed to load tags', 'error')
  } finally {
    loading.value = false
  }
}

async function submitCreate() {
  createError.value = ''
  const name = newTagName.value.trim()
  if (!name) { createError.value = 'Tag name cannot be empty.'; return }
  if (name.length > 255) { createError.value = 'Tag name is too long (max 255 chars).'; return }

  createLoading.value = true
  try {
    const created = await apiClient.post(`${ENDPOINT}/`, { name })
    tags.value.push(created)
    closeCreateModal()
    showToast(`Tag "${created.name}" created!`, 'success')
  } catch (err) {
    if (err.status === 409) { createError.value = 'A tag with this name already exists.'; return }
    createError.value = 'Something went wrong. Please try again.'
  } finally {
    createLoading.value = false
  }
}

async function saveEdit(tag) {
  const name = editingName.value.trim()
  if (!name) { showToast('Tag name cannot be empty', 'error'); return }
  if (name === tag.name) { cancelEdit(); return }
  if (name.length > 255) { showToast('Tag name is too long', 'error'); return }

  try {
    const updated = await apiClient.put(`${ENDPOINT}/${tag.id}`, { name })
    const idx = tags.value.findIndex(t => t.id === tag.id)
    if (idx !== -1) tags.value[idx] = updated
    cancelEdit()
    showToast(`Tag updated to "${updated.name}"`, 'success')
  } catch (err) {
    if (err.status === 409) { showToast('A tag with this name already exists', 'error'); return }
    showToast('Failed to update tag', 'error')
  }
}

async function executeDelete() {
  if (!tagToDelete.value) return
  deleteLoading.value = true
  try {
    await apiClient.delete(`${ENDPOINT}/${tagToDelete.value.id}`)
    tags.value = tags.value.filter(t => t.id !== tagToDelete.value.id)
    showToast(`Tag "${tagToDelete.value.name}" deleted`, 'success')
    cancelDelete()
  } catch {
    showToast('Failed to delete tag', 'error')
  } finally {
    deleteLoading.value = false
  }
}

// ─── UI helpers ───────────────────────────────────────────────
function startEdit(tag) {
  editingTagId.value = tag.id
  editingName.value = tag.name
  nextTick(() => editInput.value?.focus())
}
function cancelEdit() {
  editingTagId.value = null
  editingName.value = ''
}
function confirmDelete(tag) {
  tagToDelete.value = tag
  showDeleteModal.value = true
}
function cancelDelete() {
  showDeleteModal.value = false
  tagToDelete.value = null
}
function openCreateModal() {
  newTagName.value = ''
  createError.value = ''
  showCreateModal.value = true
  nextTick(() => createInput.value?.focus())
}
function closeCreateModal() {
  showCreateModal.value = false
  newTagName.value = ''
  createError.value = ''
}
function clearSearch() {
  searchQuery.value = ''
}

let toastTimer = null
function showToast(message, type = 'success') {
  clearTimeout(toastTimer)
  toast.value = { show: true, message, type }
  toastTimer = setTimeout(() => { toast.value.show = false }, 3000)
}

onMounted(fetchTags)
</script>

<style scoped>
/* ─── Base ──────────────────────────────────────────────────── */
.tags-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #0f0a1e 0%, #1a0f2e 50%, #0d0620 100%);
  padding: 2rem;
  font-family: 'Segoe UI', system-ui, sans-serif;
  color: #e2d9f3;
}

/* ─── Header ─────────────────────────────────────────────────── */
.page-header { margin-bottom: 2rem; }
.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 1rem;
}
.header-title {
  display: flex;
  align-items: center;
  gap: 1rem;
}
.header-icon {
  font-size: 2.5rem;
  filter: drop-shadow(0 0 12px rgba(167, 93, 255, 0.6));
}
h1 {
  margin: 0;
  font-size: 2rem;
  font-weight: 700;
  background: linear-gradient(135deg, #c084fc, #a855f7, #7c3aed);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.subtitle {
  margin: 0.2rem 0 0;
  color: #9d7ec4;
  font-size: 0.9rem;
}

/* ─── Create Button ───────────────────────────────────────────── */
.btn-create {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.65rem 1.4rem;
  background: linear-gradient(135deg, #7c3aed, #a855f7);
  color: #fff;
  border: none;
  border-radius: 12px;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s ease;
  box-shadow: 0 4px 20px rgba(124, 58, 237, 0.4);
}
.btn-create:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 28px rgba(124, 58, 237, 0.6);
  background: linear-gradient(135deg, #8b5cf6, #c084fc);
}
.btn-icon { font-size: 1.3rem; line-height: 1; }

/* ─── Search ──────────────────────────────────────────────────── */
.search-section { margin-bottom: 1.5rem; }
.search-wrapper {
  position: relative;
  max-width: 480px;
}
.search-icon {
  position: absolute;
  left: 1rem;
  top: 50%;
  transform: translateY(-50%);
  font-size: 1.1rem;
  pointer-events: none;
}
.search-input {
  width: 100%;
  padding: 0.75rem 2.8rem 0.75rem 2.8rem;
  background: rgba(255,255,255,0.06);
  border: 1px solid rgba(167,93,255,0.3);
  border-radius: 12px;
  color: #e2d9f3;
  font-size: 0.95rem;
  outline: none;
  transition: all 0.2s;
  box-sizing: border-box;
}
.search-input::placeholder { color: #7a5fa8; }
.search-input:focus {
  border-color: #a855f7;
  background: rgba(255,255,255,0.09);
  box-shadow: 0 0 0 3px rgba(168,85,247,0.2);
}
.search-clear {
  position: absolute;
  right: 0.8rem;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #9d7ec4;
  cursor: pointer;
  font-size: 0.9rem;
  padding: 0.2rem;
  transition: color 0.2s;
}
.search-clear:hover { color: #e2d9f3; }
.results-count {
  margin-top: 0.5rem;
  font-size: 0.8rem;
  color: #7a5fa8;
}

/* ─── Tags Grid ───────────────────────────────────────────────── */
.tags-container { min-height: 200px; }
.tags-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 1rem;
}

/* ─── Tag Card ────────────────────────────────────────────────── */
.tag-card {
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(167,93,255,0.2);
  border-radius: 14px;
  padding: 1rem 1.1rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
  transition: border-color 0.2s, background 0.2s, transform 0.2s, box-shadow 0.2s;
  backdrop-filter: blur(6px);
}
.tag-card:hover {
  border-color: rgba(167,93,255,0.5);
  background: rgba(255,255,255,0.09);
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(124,58,237,0.25);
}
.tag-card-body {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  min-width: 0;
}
.tag-pill {
  display: inline-block;
  padding: 0.3rem 0.8rem;
  background: linear-gradient(135deg, rgba(124,58,237,0.35), rgba(168,85,247,0.2));
  border: 1px solid rgba(167,93,255,0.4);
  border-radius: 999px;
  font-size: 0.88rem;
  font-weight: 600;
  color: #d8b4fe;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 180px;
}
.tag-id { font-size: 0.72rem; color: #5c4a7a; }
.tag-card-actions { display: flex; gap: 0.4rem; flex-shrink: 0; }

/* ─── Action Buttons ──────────────────────────────────────────── */
.btn-action {
  width: 34px;
  height: 34px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  font-size: 1rem;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}
.btn-edit { background: rgba(124,58,237,0.18); }
.btn-edit:hover { background: rgba(124,58,237,0.4); transform: scale(1.1); }
.btn-delete { background: rgba(220,38,38,0.15); }
.btn-delete:hover { background: rgba(220,38,38,0.35); transform: scale(1.1); }
.btn-save { background: rgba(34,197,94,0.2); }
.btn-save:hover { background: rgba(34,197,94,0.4); transform: scale(1.1); }
.btn-cancel { background: rgba(255,255,255,0.08); color: #9d7ec4; font-size: 0.85rem; }
.btn-cancel:hover { background: rgba(255,255,255,0.15); transform: scale(1.1); }

/* ─── Edit Mode ───────────────────────────────────────────────── */
.tag-edit-mode { display: flex; align-items: center; gap: 0.5rem; width: 100%; }
.edit-input {
  flex: 1;
  padding: 0.45rem 0.8rem;
  background: rgba(255,255,255,0.08);
  border: 1px solid rgba(167,93,255,0.5);
  border-radius: 8px;
  color: #e2d9f3;
  font-size: 0.9rem;
  outline: none;
  transition: border-color 0.2s, box-shadow 0.2s;
}
.edit-input:focus {
  border-color: #a855f7;
  box-shadow: 0 0 0 3px rgba(168,85,247,0.2);
}
.edit-actions { display: flex; gap: 0.35rem; }

/* ─── Empty / Loading ─────────────────────────────────────────── */
.empty-state, .loading-state {
  text-align: center;
  padding: 4rem 2rem;
  color: #7a5fa8;
}
.empty-icon { font-size: 3rem; margin-bottom: 1rem; }
.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(167,93,255,0.2);
  border-top-color: #a855f7;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 1rem;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* ─── Modals ──────────────────────────────────────────────────── */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.65);
  backdrop-filter: blur(6px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.modal-box {
  background: linear-gradient(145deg, #1c1030, #150d28);
  border: 1px solid rgba(167,93,255,0.35);
  border-radius: 20px;
  padding: 2rem;
  width: 100%;
  max-width: 420px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.6), 0 0 40px rgba(124,58,237,0.2);
}
.modal-danger {
  border-color: rgba(220,38,38,0.35);
  box-shadow: 0 20px 60px rgba(0,0,0,0.6), 0 0 40px rgba(220,38,38,0.15);
}
.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1.5rem;
}
.modal-header h2 { margin: 0; font-size: 1.25rem; color: #e2d9f3; }
.modal-close {
  background: rgba(255,255,255,0.07);
  border: none;
  color: #9d7ec4;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s;
}
.modal-close:hover { background: rgba(255,255,255,0.14); color: #e2d9f3; }
.modal-body { margin-bottom: 1.5rem; }
.modal-body p { margin: 0 0 0.75rem; color: #b09cd0; }
.form-label {
  display: block;
  margin-bottom: 0.5rem;
  font-size: 0.85rem;
  color: #a78bca;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}
.form-input {
  width: 100%;
  padding: 0.75rem 1rem;
  background: rgba(255,255,255,0.07);
  border: 1px solid rgba(167,93,255,0.3);
  border-radius: 10px;
  color: #e2d9f3;
  font-size: 0.95rem;
  outline: none;
  transition: all 0.2s;
  box-sizing: border-box;
}
.form-input::placeholder { color: #6b4f8a; }
.form-input:focus {
  border-color: #a855f7;
  box-shadow: 0 0 0 3px rgba(168,85,247,0.2);
}
.error-msg { margin-top: 0.6rem; color: #f87171; font-size: 0.85rem; }
.delete-target { text-align: center; margin: 1rem 0; }
.delete-warning { font-size: 0.82rem; color: #f87171; margin-top: 0.75rem; }
.modal-footer { display: flex; gap: 0.75rem; justify-content: flex-end; }

/* ─── Footer Buttons ──────────────────────────────────────────── */
.btn-primary {
  padding: 0.6rem 1.4rem;
  background: linear-gradient(135deg, #7c3aed, #a855f7);
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  min-width: 110px;
  justify-content: center;
}
.btn-primary:hover:not(:disabled) {
  background: linear-gradient(135deg, #8b5cf6, #c084fc);
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(124,58,237,0.45);
}
.btn-primary:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-secondary {
  padding: 0.6rem 1.2rem;
  background: rgba(255,255,255,0.07);
  color: #c4aee0;
  border: 1px solid rgba(167,93,255,0.2);
  border-radius: 10px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-secondary:hover { background: rgba(255,255,255,0.12); border-color: rgba(167,93,255,0.4); }
.btn-danger {
  padding: 0.6rem 1.4rem;
  background: linear-gradient(135deg, #b91c1c, #ef4444);
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  min-width: 110px;
  justify-content: center;
}
.btn-danger:hover:not(:disabled) {
  background: linear-gradient(135deg, #dc2626, #f87171);
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(220,38,38,0.4);
}
.btn-danger:disabled { opacity: 0.5; cursor: not-allowed; }

/* ─── Button Spinner ──────────────────────────────────────────── */
.btn-spinner {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

/* ─── Toast ───────────────────────────────────────────────────── */
.toast {
  position: fixed;
  bottom: 2rem;
  right: 2rem;
  padding: 0.85rem 1.4rem;
  border-radius: 12px;
  font-weight: 600;
  font-size: 0.9rem;
  z-index: 9999;
  box-shadow: 0 8px 30px rgba(0,0,0,0.4);
  backdrop-filter: blur(8px);
}
.toast.success {
  background: rgba(21,128,61,0.85);
  border: 1px solid rgba(34,197,94,0.4);
  color: #86efac;
}
.toast.error {
  background: rgba(153,27,27,0.85);
  border: 1px solid rgba(239,68,68,0.4);
  color: #fca5a5;
}

/* ─── Transitions ─────────────────────────────────────────────── */
.modal-fade-enter-active,
.modal-fade-leave-active { transition: opacity 0.25s ease; }
.modal-fade-enter-from,
.modal-fade-leave-to { opacity: 0; }

.tag-list-enter-active { transition: all 0.3s ease; }
.tag-list-leave-active { transition: all 0.25s ease; position: absolute; }
.tag-list-enter-from { opacity: 0; transform: translateY(12px) scale(0.96); }
.tag-list-leave-to { opacity: 0; transform: scale(0.9); }

.toast-enter-active,
.toast-leave-active { transition: all 0.3s ease; }
.toast-enter-from,
.toast-leave-to { opacity: 0; transform: translateY(12px); }
</style>
