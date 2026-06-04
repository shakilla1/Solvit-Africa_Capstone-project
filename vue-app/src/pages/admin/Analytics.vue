<template>
  <div class="space-y-6">
    <div class="grid grid-cols-4 gap-6">
      <StatCard label="Total Simulations" :value="String(stats.totalCampaigns)" borderColor="#2563eb"><template #icon><BarChart2 :size="24" /></template></StatCard>
      <StatCard label="Overall Click Rate" :value="`${stats.clickRate}%`" borderColor="#d97706"><template #icon><TrendingDown :size="24" /></template></StatCard>
      <StatCard label="Overall Submission Rate" :value="`${stats.submissionRate}%`" borderColor="#dc2626"><template #icon><TrendingDown :size="24" /></template></StatCard>
      <StatCard label="Emails Sent" :value="String(stats.emailsSent)" borderColor="#16a34a"><template #icon><TrendingUp :size="24" /></template></StatCard>
    </div>

    <Card>
      <h3 class="text-lg font-medium text-[#0f172a] mb-4">Click & Submission Rate Per Campaign</h3>
      <div v-if="stats.trend?.length" class="flex items-end gap-6 h-[200px] px-4">
        <div v-for="d in stats.trend" :key="d.name" class="flex-1 flex flex-col items-center gap-1">
          <div class="w-full flex gap-1">
            <div class="flex-1 bg-[#2563eb] rounded-t" :style="{ height: d.clickRate * 2 + 'px' }"></div>
            <div class="flex-1 bg-[#dc2626] rounded-t" :style="{ height: d.submissionRate * 2 + 'px' }"></div>
          </div>
          <span class="text-xs text-[#94a3b8] truncate w-full text-center">{{ d.name }}</span>
        </div>
      </div>
      <div v-else class="h-[200px] flex items-center justify-center text-sm text-[#94a3b8]">No campaign data yet</div>
      <div class="flex gap-4 mt-2 text-xs">
        <div class="flex items-center gap-1"><div class="w-3 h-3 bg-[#2563eb] rounded"></div> Click Rate %</div>
        <div class="flex items-center gap-1"><div class="w-3 h-3 bg-[#dc2626] rounded"></div> Submission Rate %</div>
      </div>
    </Card>

    <div class="grid grid-cols-2 gap-6">
      <Card>
        <h3 class="text-lg font-medium text-[#0f172a] mb-4">Department Vulnerability</h3>
        <div v-if="deptStats.length" class="grid grid-cols-3 gap-3">
          <div v-for="d in deptStats" :key="d.dept" class="p-4 rounded-lg text-center"
            :style="{ backgroundColor: d.rate > 60 ? '#fee2e2' : d.rate > 40 ? '#fef3c7' : '#dcfce7' }">
            <div class="text-sm font-medium text-[#0f172a] mb-1">{{ d.dept || 'Unknown' }}</div>
            <div class="text-xl font-serif">{{ d.rate }}%</div>
          </div>
        </div>
        <div v-else class="text-sm text-[#94a3b8]">No data yet</div>
      </Card>

      <Card>
        <h3 class="text-lg font-medium text-[#0f172a] mb-4">Risk Score Distribution</h3>
        <div v-if="riskDist.some(r => r.users > 0)" class="flex items-end gap-4 h-[200px] px-4">
          <div v-for="d in riskDist" :key="d.range" class="flex-1 flex flex-col items-center gap-1">
            <div class="w-full bg-[#2563eb] rounded-t" :style="{ height: Math.max(d.users * 10, d.users > 0 ? 4 : 0) + 'px' }"></div>
            <span class="text-xs text-[#94a3b8]">{{ d.range }}</span>
            <span class="text-xs text-[#0f172a]">{{ d.users }}</span>
          </div>
        </div>
        <div v-else class="h-[200px] flex items-center justify-center text-sm text-[#94a3b8]">No user data yet</div>
      </Card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { BarChart2, TrendingUp, TrendingDown } from '@lucide/vue'
import Card from '../../components/Card.vue'
import StatCard from '../../components/StatCard.vue'
import { api } from '../../api'

const stats = ref<any>({ totalCampaigns: 0, emailsSent: 0, clickRate: 0, submissionRate: 0, trend: [], atRiskUsers: [] })
const users = ref<any[]>([])

async function load() {
  try {
    const [sRes, uRes] = await Promise.all([
      api.get('/api/admin/dashboard/stats'),
      api.get('/api/admin/users'),
    ])
    stats.value = sRes.data
    users.value = uRes.data
  } catch {}
}

const deptStats = computed(() => {
  const map: Record<string, { clicks: number; total: number }> = {}
  users.value.forEach(u => {
    const d = u.department || 'Unknown'
    if (!map[d]) map[d] = { clicks: 0, total: 0 }
    map[d].total++
    if (u.clicks > 0) map[d].clicks++
  })
  return Object.entries(map).map(([dept, v]) => ({
    dept,
    rate: v.total === 0 ? 0 : Math.round(v.clicks / v.total * 100)
  }))
})

const riskDist = computed(() => {
  const ranges = [
    { range: '0-20', min: 0, max: 20 },
    { range: '21-40', min: 21, max: 40 },
    { range: '41-60', min: 41, max: 60 },
    { range: '61-80', min: 61, max: 80 },
    { range: '81-100', min: 81, max: 100 },
  ]
  return ranges.map(r => ({
    range: r.range,
    users: users.value.filter(u => u.riskScore >= r.min && u.riskScore <= r.max).length
  }))
})

let interval: any
onMounted(() => { load(); interval = setInterval(load, 30000) })
onUnmounted(() => clearInterval(interval))
</script>
