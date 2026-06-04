<template>
  <div class="space-y-6">
    <div>
      <h2 class="text-2xl font-serif text-[#0f172a]" :style="{ fontFamily: 'var(--font-serif)' }">Education Monitor</h2>
      <p class="text-[#475569]">Track all users' training progress and quiz performance in real time</p>
    </div>

    <div class="grid grid-cols-4 gap-4">
      <StatCard label="Total Sessions" :value="String(sessions.length)" borderColor="#2563eb">
        <template #icon><BookOpen :size="22" /></template>
      </StatCard>
      <StatCard label="Avg Score" :value="avgScore + '%'" borderColor="#16a34a">
        <template #icon><TrendingUp :size="22" /></template>
      </StatCard>
      <StatCard label="Completed" :value="String(completedCount)" borderColor="#16a34a">
        <template #icon><CheckCircle :size="22" /></template>
      </StatCard>
      <StatCard label="In Progress" :value="String(inProgressCount)" borderColor="#d97706">
        <template #icon><Clock :size="22" /></template>
      </StatCard>
    </div>

    <Card>
      <div class="flex gap-3">
        <Input placeholder="Search by user or topic..." v-model="search" class="flex-1" />
        <select v-model="filterStatus" class="px-3 py-2 border border-[#e2e8f0] rounded-lg text-sm">
          <option value="">All Status</option>
          <option value="COMPLETED">Completed</option>
          <option value="IN_PROGRESS">In Progress</option>
        </select>
      </div>
    </Card>

    <Card class="p-0 overflow-hidden">
      <table class="w-full">
        <thead class="bg-[#f8fafc] border-b border-[#e2e8f0]">
          <tr>
            <th class="text-left px-5 py-3 text-xs font-medium text-[#94a3b8] uppercase">User</th>
            <th class="text-left px-5 py-3 text-xs font-medium text-[#94a3b8] uppercase">Topic</th>
            <th class="text-left px-5 py-3 text-xs font-medium text-[#94a3b8] uppercase">Status</th>
            <th class="text-left px-5 py-3 text-xs font-medium text-[#94a3b8] uppercase">Score</th>
            <th class="text-left px-5 py-3 text-xs font-medium text-[#94a3b8] uppercase">Attempts</th>
            <th class="text-left px-5 py-3 text-xs font-medium text-[#94a3b8] uppercase">Completed At</th>
            <th class="text-left px-5 py-3 text-xs font-medium text-[#94a3b8] uppercase">Badge</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-[#e2e8f0]">
          <tr v-if="loading">
            <td colspan="7" class="px-5 py-8 text-center text-sm text-[#94a3b8]">Loading...</td>
          </tr>
          <tr v-else-if="filtered.length === 0">
            <td colspan="7" class="px-5 py-8 text-center text-sm text-[#94a3b8]">No training records yet</td>
          </tr>
          <tr v-for="row in filtered" :key="row.id" class="hover:bg-[#f8fafc]">
            <td class="px-5 py-3">
              <div class="flex items-center gap-2">
                <div class="w-7 h-7 rounded-full bg-[#2563eb] flex items-center justify-center text-xs text-white font-medium">
                  {{ row.targetUser?.fullName?.split(' ').map((n: string) => n[0]).join('').slice(0,2) || 'U' }}
                </div>
                <div>
                  <div class="text-sm font-medium text-[#0f172a]">{{ row.targetUser?.fullName }}</div>
                  <div class="text-xs text-[#94a3b8]">{{ row.targetUser?.email }}</div>
                </div>
              </div>
            </td>
            <td class="px-5 py-3 text-sm text-[#475569]">{{ row.topicTitle }}</td>
            <td class="px-5 py-3">
              <Badge :variant="row.status === 'COMPLETED' ? 'green' : 'amber'">
                {{ row.status === 'COMPLETED' ? 'Completed' : 'In Progress' }}
              </Badge>
            </td>
            <td class="px-5 py-3">
              <span v-if="row.score !== null" class="text-sm font-medium"
                :style="{ color: row.score >= 70 ? '#16a34a' : row.score >= 50 ? '#d97706' : '#dc2626' }">
                {{ row.score }}%
              </span>
              <span v-else class="text-sm text-[#94a3b8]">—</span>
            </td>
            <td class="px-5 py-3 text-sm text-[#475569]">{{ row.attempts }}</td>
            <td class="px-5 py-3 text-sm text-[#475569]">{{ row.completedAt ? formatDate(row.completedAt) : '—' }}</td>
            <td class="px-5 py-3">
              <Badge v-if="row.score >= 70 && row.status === 'COMPLETED'" variant="blue">
                {{ row.topicId === 'final-assessment' ? 'Phishing Detective' : 'Security Aware' }}
              </Badge>
              <span v-else class="text-sm text-[#94a3b8]">—</span>
            </td>
          </tr>
        </tbody>
      </table>
      <div class="px-5 py-3 border-t border-[#e2e8f0] text-sm text-[#475569]">
        Showing {{ filtered.length }} of {{ sessions.length }} records
      </div>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { BookOpen, TrendingUp, CheckCircle, Clock } from '@lucide/vue'
import Card from '../../components/Card.vue'
import StatCard from '../../components/StatCard.vue'
import Badge from '../../components/Badge.vue'
import Input from '../../components/Input.vue'
import { api } from '../../api'

const sessions = ref<any[]>([])
const loading = ref(true)
const search = ref('')
const filterStatus = ref('')

const completedCount = computed(() => sessions.value.filter(s => s.status === 'COMPLETED').length)
const inProgressCount = computed(() => sessions.value.filter(s => s.status === 'IN_PROGRESS').length)
const avgScore = computed(() => {
  const done = sessions.value.filter(s => s.score !== null)
  if (!done.length) return 0
  return Math.round(done.reduce((sum, s) => sum + s.score, 0) / done.length)
})

const filtered = computed(() => sessions.value.filter(s => {
  const q = search.value.toLowerCase()
  if (q && !s.targetUser?.fullName?.toLowerCase().includes(q) && !s.topicTitle?.toLowerCase().includes(q)) return false
  if (filterStatus.value && s.status !== filterStatus.value) return false
  return true
}))

const formatDate = (d: string) => new Date(d).toLocaleString()

async function load() {
  try {
    const res = await api.get('/api/admin/education/progress')
    sessions.value = res.data
  } catch {} finally {
    loading.value = false
  }
}

let interval: any
onMounted(() => { load(); interval = setInterval(load, 30000) })
onUnmounted(() => clearInterval(interval))
</script>
