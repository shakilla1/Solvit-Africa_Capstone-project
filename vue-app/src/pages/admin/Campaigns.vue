<template>
  <div class="space-y-5">
    <div class="flex justify-between items-center">
      <div>
        <h2 class="text-xl font-semibold text-[#0f172a]">Campaigns</h2>
        <p class="text-sm text-[#475569]">Manage your phishing simulation campaigns</p>
      </div>
      <RouterLink to="/admin/campaigns/new">
        <Button><template #icon><Plus :size="16" /></template>New Campaign</Button>
      </RouterLink>
    </div>

    <!-- Filters -->
    <Card>
      <div class="flex gap-3">
        <div class="flex-1">
          <Input placeholder="Search campaigns..." v-model="searchTerm" />
        </div>
        <select v-model="filterStatus" class="px-3 py-2 border border-[#e2e8f0] rounded-lg text-sm text-[#0f172a]">
          <option value="">All Status</option>
          <option value="active">Active</option>
          <option value="draft">Draft</option>
          <option value="completed">Completed</option>
          <option value="paused">Paused</option>
        </select>
        <select v-model="filterDifficulty" class="px-3 py-2 border border-[#e2e8f0] rounded-lg text-sm text-[#0f172a]">
          <option value="">All Difficulty</option>
          <option value="easy">Easy</option>
          <option value="medium">Medium</option>
          <option value="hard">Hard</option>
        </select>
        <select v-model="sortBy" class="px-3 py-2 border border-[#e2e8f0] rounded-lg text-sm text-[#0f172a]">
          <option value="newest">Newest First</option>
          <option value="oldest">Oldest First</option>
          <option value="targets">Most Targets</option>
          <option value="clickRate">Highest Click Rate</option>
        </select>
        <button v-if="searchTerm || filterStatus || filterDifficulty" @click="clearFilters"
          class="px-3 py-2 text-sm text-[#475569] hover:text-[#dc2626] border border-[#e2e8f0] rounded-lg flex items-center gap-1">
          <X :size="14" /> Clear
        </button>
      </div>
    </Card>

    <!-- Table -->
    <Card class="p-0 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-[#f8fafc] border-b border-[#e2e8f0]">
            <tr>
              <th class="text-left px-5 py-3 text-xs font-medium text-[#94a3b8] uppercase">Campaign</th>
              <th class="text-left px-5 py-3 text-xs font-medium text-[#94a3b8] uppercase">Difficulty</th>
              <th class="text-left px-5 py-3 text-xs font-medium text-[#94a3b8] uppercase">Status</th>
              <th class="text-left px-5 py-3 text-xs font-medium text-[#94a3b8] uppercase">Targets</th>
              <th class="text-left px-5 py-3 text-xs font-medium text-[#94a3b8] uppercase">Click Rate</th>
              <th class="text-left px-5 py-3 text-xs font-medium text-[#94a3b8] uppercase">Submission</th>
              <th class="text-left px-5 py-3 text-xs font-medium text-[#94a3b8] uppercase">Launched</th>
              <th class="text-left px-5 py-3 text-xs font-medium text-[#94a3b8] uppercase">Actions</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-[#e2e8f0]">
            <tr v-if="loading">
              <td colspan="8" class="px-5 py-10 text-center text-sm text-[#94a3b8]">Loading campaigns...</td>
            </tr>
            <tr v-else-if="filteredCampaigns.length === 0">
              <td colspan="8" class="px-5 py-10 text-center text-sm text-[#94a3b8]">No campaigns match your filters.</td>
            </tr>
            <tr v-for="campaign in filteredCampaigns" :key="campaign.id" class="hover:bg-[#f8fafc]">
              <td class="px-5 py-3">
                <RouterLink :to="`/admin/campaigns/${campaign.id}`" class="font-medium text-sm text-[#2563eb] hover:underline">{{ campaign.name }}</RouterLink>
              </td>
              <td class="px-5 py-3">
                <Badge :variant="difficultyColor(campaign.difficulty)">{{ capitalize(campaign.difficulty) }}</Badge>
              </td>
              <td class="px-5 py-3">
                <Badge :variant="statusColor(campaign.status)">{{ capitalize(campaign.status) }}</Badge>
              </td>
              <td class="px-5 py-3 text-sm text-[#0f172a]">{{ campaign.totalTargets }}</td>
              <td class="px-5 py-3">
                <div class="flex items-center gap-2">
                  <div class="w-14 h-1.5 bg-[#f1f5f9] rounded-full overflow-hidden">
                    <div class="h-full bg-[#d97706] rounded-full" :style="{ width: Math.round(campaign.clickRate) + '%' }"></div>
                  </div>
                  <span class="text-xs text-[#0f172a]">{{ Math.round(campaign.clickRate) }}%</span>
                </div>
              </td>
              <td class="px-5 py-3">
                <div class="flex items-center gap-2">
                  <div class="w-14 h-1.5 bg-[#f1f5f9] rounded-full overflow-hidden">
                    <div class="h-full bg-[#dc2626] rounded-full" :style="{ width: Math.round(campaign.submissionRate) + '%' }"></div>
                  </div>
                  <span class="text-xs text-[#0f172a]">{{ Math.round(campaign.submissionRate) }}%</span>
                </div>
              </td>
              <td class="px-5 py-3 text-xs text-[#475569]">{{ campaign.launchedAt ? new Date(campaign.launchedAt).toLocaleDateString() : 'Not launched' }}</td>
              <td class="px-5 py-3">
                <div class="flex items-center gap-1">
                  <RouterLink :to="`/admin/campaigns/${campaign.id}`">
                    <button class="p-1.5 hover:bg-[#f1f5f9] rounded" title="View"><Eye :size="15" class="text-[#475569]" /></button>
                  </RouterLink>
                  <button class="p-1.5 hover:bg-[#f1f5f9] rounded" title="Edit" @click="editCampaign(campaign)">
                    <Edit :size="15" class="text-[#475569]" />
                  </button>
                  <button
                    v-if="campaign.status !== 'DRAFT' && campaign.status !== 'COMPLETED'"
                    class="p-1.5 hover:bg-[#f1f5f9] rounded"
                    :title="campaign.status === 'paused' ? 'Resume' : 'Pause'"
                    @click="togglePause(campaign)"
                  >
                    <Play v-if="campaign.status === 'PAUSED'" :size="15" class="text-[#16a34a]" />
                    <Pause v-else :size="15" class="text-[#d97706]" />
                  </button>
                  <button class="p-1.5 hover:bg-[#fee2e2] rounded" title="Delete" @click="confirmDelete(campaign)">
                    <Trash2 :size="15" class="text-[#dc2626]" />
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="px-5 py-3 border-t border-[#e2e8f0] flex items-center justify-between">
        <div class="text-xs text-[#475569]">Showing {{ filteredCampaigns.length }} of {{ campaigns.length }} campaigns</div>
        <div class="flex gap-1.5">
          <button class="px-3 py-1 text-xs border border-[#e2e8f0] rounded hover:bg-[#f8fafc] disabled:opacity-40" disabled>Previous</button>
          <button class="px-3 py-1 text-xs border border-[#e2e8f0] rounded hover:bg-[#f8fafc] disabled:opacity-40" disabled>Next</button>
        </div>
      </div>
    </Card>

    <!-- Delete Confirm Modal -->
    <div v-if="deleteTarget" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50" @click.self="deleteTarget = null">
      <div class="bg-white rounded-xl p-6 w-full max-w-sm shadow-xl">
        <div class="w-12 h-12 rounded-full bg-[#fee2e2] flex items-center justify-center mx-auto mb-4">
          <Trash2 :size="24" class="text-[#dc2626]" />
        </div>
        <h3 class="text-lg font-semibold text-[#0f172a] text-center mb-2">Delete Campaign?</h3>
        <p class="text-sm text-[#475569] text-center mb-5">
          Are you sure you want to delete <strong>{{ deleteTarget.name }}</strong>? This action cannot be undone.
        </p>
        <div class="flex gap-3">
          <Button variant="secondary" class="flex-1" @click="deleteTarget = null">Cancel</Button>
          <Button variant="danger" class="flex-1" @click="doDelete">Delete</Button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Plus, Eye, Edit, Pause, Play, Trash2, X } from '@lucide/vue'
import { toast } from 'vue-sonner'
import Card from '../../components/Card.vue'
import Button from '../../components/Button.vue'
import Badge from '../../components/Badge.vue'
import Input from '../../components/Input.vue'
import { campaignApi } from '../../api'

const searchTerm = ref('')
const filterStatus = ref('')
const filterDifficulty = ref('')
const sortBy = ref('newest')
const deleteTarget = ref<any>(null)
const campaigns = ref<any[]>([])
const loading = ref(true)

async function load() {
  try {
    const res = await campaignApi.list()
    campaigns.value = res.data
  } catch (err: any) {
    toast.error('Failed to load campaigns')
  } finally {
    loading.value = false
  }
}

onMounted(load)

const filteredCampaigns = computed(() => {
  let list = campaigns.value.filter((c: any) => {
    const matchSearch = !searchTerm.value || c.name.toLowerCase().includes(searchTerm.value.toLowerCase())
    const matchStatus = !filterStatus.value || c.status?.toLowerCase() === filterStatus.value
    const matchDiff = !filterDifficulty.value || c.difficulty?.toLowerCase() === filterDifficulty.value
    return matchSearch && matchStatus && matchDiff
  })
  if (sortBy.value === 'oldest') list = [...list].reverse()
  if (sortBy.value === 'targets') list = [...list].sort((a: any, b: any) => b.totalTargets - a.totalTargets)
  if (sortBy.value === 'clickRate') list = [...list].sort((a: any, b: any) => b.clickRate - a.clickRate)
  return list
})

function clearFilters() { searchTerm.value = ''; filterStatus.value = ''; filterDifficulty.value = '' }

async function togglePause(campaign: any) {
  try {
    const res = await campaignApi.togglePause(campaign.id)
    const idx = campaigns.value.findIndex(c => c.id === campaign.id)
    if (idx >= 0) campaigns.value[idx] = res.data
    toast.success(`Campaign ${res.data.status === 'PAUSED' ? 'paused' : 'resumed'}`)
  } catch {
    toast.error('Failed to update campaign')
  }
}

function editCampaign(campaign: any) {
  toast.info(`Edit functionality for "${campaign.name}" coming soon`)
}

function confirmDelete(campaign: any) { deleteTarget.value = campaign }

async function doDelete() {
  try {
    await campaignApi.delete(deleteTarget.value.id)
    campaigns.value = campaigns.value.filter(c => c.id !== deleteTarget.value.id)
    toast.success(`"${deleteTarget.value.name}" deleted`)
    deleteTarget.value = null
  } catch {
    toast.error('Failed to delete campaign')
  }
}

const difficultyColor = (d: string) => (d === 'easy' ? 'green' : d === 'medium' ? 'amber' : 'red') as any
const statusColor = (s: string) => (s === 'active' ? 'blue' : s === 'draft' ? 'grey' : s === 'completed' ? 'green' : 'amber') as any
const capitalize = (s: string) => s.charAt(0).toUpperCase() + s.slice(1)
</script>
