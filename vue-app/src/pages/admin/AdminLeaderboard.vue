<template>
  <div class="space-y-6">
    <div class="flex justify-between items-center">
      <div>
        <h2 class="text-2xl font-serif text-[#0f172a]" :style="{ fontFamily: 'var(--font-serif)' }">Organisation Leaderboard</h2>
        <p class="text-[#475569]">Monitor user rankings and security awareness scores</p>
      </div>
      <Button variant="secondary"><template #icon><Download :size="16" /></template>Export Rankings</Button>
    </div>

    <div class="grid grid-cols-3 gap-4">
      <StatCard label="Top Scorer" value="Sarah W." borderColor="#d97706">
        <template #icon><Trophy :size="22" /></template>
      </StatCard>
      <StatCard label="Avg Points" value="285" borderColor="#2563eb">
        <template #icon><BarChart2 :size="22" /></template>
      </StatCard>
      <StatCard label="Active Users" value="45" borderColor="#16a34a">
        <template #icon><Users :size="22" /></template>
      </StatCard>
    </div>

    <!-- Podium -->
    <Card>
      <h3 class="text-base font-medium text-[#0f172a] mb-4">Top 3 Performers</h3>
      <div class="flex items-end justify-center gap-6">
        <div class="text-center">
          <div class="w-14 h-14 rounded-full bg-[#cbd5e1] flex items-center justify-center text-lg font-semibold text-white mx-auto mb-2">MC</div>
          <Medal :size="24" class="text-[#cbd5e1] mx-auto mb-1" />
          <div class="text-sm font-medium text-[#0f172a]">Mike Chen</div>
          <div class="text-lg font-serif text-[#2563eb]" :style="{ fontFamily: 'var(--font-serif)' }">385</div>
          <Badge variant="grey" class="text-xs">Security Aware</Badge>
        </div>
        <div class="text-center -mt-4">
          <div class="w-16 h-16 rounded-full bg-[#d97706] flex items-center justify-center text-xl font-semibold text-white mx-auto mb-2">SW</div>
          <Trophy :size="28" class="text-[#d97706] mx-auto mb-1" />
          <div class="text-sm font-medium text-[#0f172a]">Sarah Wilson</div>
          <div class="text-xl font-serif text-[#2563eb]" :style="{ fontFamily: 'var(--font-serif)' }">420</div>
          <Badge variant="blue" class="text-xs">Phishing Detective</Badge>
        </div>
        <div class="text-center">
          <div class="w-14 h-14 rounded-full bg-[#92400e] flex items-center justify-center text-lg font-semibold text-white mx-auto mb-2">ED</div>
          <Award :size="24" class="text-[#92400e] mx-auto mb-1" />
          <div class="text-sm font-medium text-[#0f172a]">Emma Davis</div>
          <div class="text-lg font-serif text-[#2563eb]" :style="{ fontFamily: 'var(--font-serif)' }">310</div>
          <Badge variant="grey" class="text-xs">Security Aware</Badge>
        </div>
      </div>
    </Card>

    <!-- Full table -->
    <Card class="p-0 overflow-hidden">
      <div class="p-4 border-b border-[#e2e8f0] flex gap-3">
        <Input placeholder="Search users..." v-model="search" class="flex-1" />
        <select v-model="filterDept" class="px-3 py-2 border border-[#e2e8f0] rounded-lg text-sm">
          <option value="">All Departments</option>
          <option>IT</option><option>Sales</option><option>Marketing</option><option>HR</option><option>Finance</option>
        </select>
      </div>
      <table class="w-full">
        <thead class="bg-[#f8fafc] border-b border-[#e2e8f0]">
          <tr>
            <th class="text-left px-5 py-3 text-xs font-medium text-[#94a3b8] uppercase">Rank</th>
            <th class="text-left px-5 py-3 text-xs font-medium text-[#94a3b8] uppercase">User</th>
            <th class="text-left px-5 py-3 text-xs font-medium text-[#94a3b8] uppercase">Department</th>
            <th class="text-left px-5 py-3 text-xs font-medium text-[#94a3b8] uppercase">Points</th>
            <th class="text-left px-5 py-3 text-xs font-medium text-[#94a3b8] uppercase">Campaigns</th>
            <th class="text-left px-5 py-3 text-xs font-medium text-[#94a3b8] uppercase">Avg Score</th>
            <th class="text-left px-5 py-3 text-xs font-medium text-[#94a3b8] uppercase">Badge</th>
            <th class="text-left px-5 py-3 text-xs font-medium text-[#94a3b8] uppercase">Trend</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-[#e2e8f0]">
          <tr v-for="user in filteredUsers" :key="user.rank" class="hover:bg-[#f8fafc]">
            <td class="px-5 py-3">
              <div class="flex items-center gap-1.5">
                <span class="text-sm font-semibold text-[#0f172a]">#{{ user.rank }}</span>
                <Trophy v-if="user.rank === 1" :size="14" class="text-[#d97706]" />
                <Medal v-else-if="user.rank === 2" :size="14" class="text-[#cbd5e1]" />
                <Award v-else-if="user.rank === 3" :size="14" class="text-[#92400e]" />
              </div>
            </td>
            <td class="px-5 py-3">
              <div class="flex items-center gap-2">
                <div class="w-7 h-7 rounded-full bg-[#2563eb] flex items-center justify-center text-xs text-white font-medium">{{ user.avatar }}</div>
                <span class="text-sm font-medium text-[#0f172a]">{{ user.name }}</span>
              </div>
            </td>
            <td class="px-5 py-3 text-sm text-[#475569]">{{ user.dept }}</td>
            <td class="px-5 py-3 text-sm font-semibold text-[#0f172a]">{{ user.points }}</td>
            <td class="px-5 py-3 text-sm text-[#475569]">{{ user.campaigns }}</td>
            <td class="px-5 py-3">
              <span class="text-sm font-medium" :style="{ color: user.avgScore >= 80 ? '#16a34a' : user.avgScore >= 60 ? '#d97706' : '#dc2626' }">
                {{ user.avgScore }}%
              </span>
            </td>
            <td class="px-5 py-3">
              <Badge :variant="user.badge === 'Phishing Detective' ? 'blue' : user.badge === 'Security Aware' ? 'green' : 'grey'">
                {{ user.badge }}
              </Badge>
            </td>
            <td class="px-5 py-3">
              <div v-if="user.change > 0" class="flex items-center gap-1 text-[#16a34a] text-xs"><TrendingUp :size="14" />+{{ user.change }}</div>
              <div v-else-if="user.change < 0" class="flex items-center gap-1 text-[#dc2626] text-xs"><TrendingDown :size="14" />{{ user.change }}</div>
              <span v-else class="text-xs text-[#94a3b8]">—</span>
            </td>
          </tr>
        </tbody>
      </table>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import Card from '../../components/Card.vue'
import Badge from '../../components/Badge.vue'
import Button from '../../components/Button.vue'
import Input from '../../components/Input.vue'
import { userApi } from '../../api'

const search = ref('')
const filterDept = ref('')
const users = ref<any[]>([])
const loading = ref(true)

const filteredUsers = computed(() =>
  users.value.filter(u => {
    const matchSearch = !search.value || u.name.toLowerCase().includes(search.value.toLowerCase())
    const matchDept = !filterDept.value || u.dept === filterDept.value
    return matchSearch && matchDept
  })
)

onMounted(async () => {
  try {
    const { data } = await userApi.leaderboard()
    users.value = data.map((u: any) => ({ ...u, change: 0, avgScore: 0 }))
  } catch {
    users.value = []
  } finally {
    loading.value = false
  }
})
</script>
