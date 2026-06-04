<template>
  <div class="space-y-6">
    <div>
      <h2 class="text-2xl font-serif text-[#0f172a]" :style="{ fontFamily: 'var(--font-serif)' }">Target Users</h2>
      <p class="text-[#475569]">Manage and monitor all target users</p>
    </div>
    <Card>
      <div class="flex gap-4 mb-6">
        <Input placeholder="Search by name or email..." class="flex-1" v-model="search" />
        <select v-model="deptFilter" class="px-4 py-2 border border-[#e2e8f0] rounded-lg text-sm">
          <option value="">All Departments</option>
          <option v-for="d in departments" :key="d" :value="d">{{ d }}</option>
        </select>
        <select v-model="riskFilter" class="px-4 py-2 border border-[#e2e8f0] rounded-lg text-sm">
          <option value="">All Risk Levels</option>
          <option value="high">High (&gt;66)</option>
          <option value="medium">Medium (34–66)</option>
          <option value="low">Low (&lt;34)</option>
        </select>
      </div>
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-[#f8fafc] border-b border-[#e2e8f0]">
            <tr>
              <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">Name</th>
              <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">Email</th>
              <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">Department</th>
              <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">Campaigns</th>
              <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">Clicks</th>
              <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">Submissions</th>
              <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">Risk Score</th>
              <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">Actions</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-[#e2e8f0]">
            <tr v-if="loading">
              <td colspan="8" class="px-6 py-8 text-center text-sm text-[#94a3b8]">Loading...</td>
            </tr>
            <tr v-else-if="filtered.length === 0">
              <td colspan="8" class="px-6 py-8 text-center text-sm text-[#94a3b8]">No users found</td>
            </tr>
            <tr v-for="user in filtered" :key="user.id" class="hover:bg-[#f8fafc]"
              :class="{ 'bg-[#fee2e2]/20': user.riskScore > 66 }">
              <td class="px-6 py-4 text-[#0f172a] font-medium">{{ user.fullName }}</td>
              <td class="px-6 py-4 text-[#475569]">{{ user.email }}</td>
              <td class="px-6 py-4 text-[#475569]">{{ user.department || '—' }}</td>
              <td class="px-6 py-4 text-[#0f172a]">{{ user.campaigns }}</td>
              <td class="px-6 py-4">
                <span v-if="user.clicks > 0" class="px-2 py-0.5 bg-[#dbeafe] text-[#2563eb] rounded text-xs font-medium">{{ user.clicks }}</span>
                <span v-else class="text-[#94a3b8]">0</span>
              </td>
              <td class="px-6 py-4">
                <span v-if="user.submissions > 0" class="px-2 py-0.5 bg-[#fee2e2] text-[#dc2626] rounded text-xs font-medium">{{ user.submissions }}</span>
                <span v-else class="text-[#94a3b8]">0</span>
              </td>
              <td class="px-6 py-4">
                <div class="flex items-center gap-2">
                  <div class="flex-1 h-2 w-16 bg-[#f1f5f9] rounded-full overflow-hidden">
                    <div class="h-full rounded-full"
                      :style="{ width: user.riskScore + '%', backgroundColor: user.riskScore > 66 ? '#dc2626' : user.riskScore > 33 ? '#d97706' : '#16a34a' }">
                    </div>
                  </div>
                  <span class="text-sm font-medium"
                    :style="{ color: user.riskScore > 66 ? '#dc2626' : user.riskScore > 33 ? '#d97706' : '#16a34a' }">
                    {{ user.riskScore }}
                  </span>
                </div>
              </td>
              <td class="px-6 py-4">
                <RouterLink :to="`/admin/users/${user.id}`">
                  <Button variant="secondary" size="sm"><template #icon><Eye :size="16" /></template>View</Button>
                </RouterLink>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { Eye } from '@lucide/vue'
import Card from '../../components/Card.vue'
import Button from '../../components/Button.vue'
import Input from '../../components/Input.vue'
import { api } from '../../api'

const users = ref<any[]>([])
const loading = ref(true)
const search = ref('')
const deptFilter = ref('')
const riskFilter = ref('')

async function load() {
  try {
    const res = await api.get('/api/admin/users')
    users.value = res.data
  } catch {} finally {
    loading.value = false
  }
}

const departments = computed(() => [...new Set(users.value.map(u => u.department).filter(Boolean))])

const filtered = computed(() => users.value.filter(u => {
  const q = search.value.toLowerCase()
  if (q && !u.fullName?.toLowerCase().includes(q) && !u.email?.toLowerCase().includes(q)) return false
  if (deptFilter.value && u.department !== deptFilter.value) return false
  if (riskFilter.value === 'high' && u.riskScore <= 66) return false
  if (riskFilter.value === 'medium' && (u.riskScore < 34 || u.riskScore > 66)) return false
  if (riskFilter.value === 'low' && u.riskScore >= 34) return false
  return true
}))

let interval: any
onMounted(() => { load(); interval = setInterval(load, 30000) })
onUnmounted(() => clearInterval(interval))
</script>
