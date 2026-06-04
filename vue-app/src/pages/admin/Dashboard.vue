<template>
  <div class="space-y-8">
    <div class="grid grid-cols-4 gap-6">
      <StatCard label="Total Campaigns" :value="String(stats.totalCampaigns)" borderColor="#2563eb">
        <template #icon><BarChart2 :size="24" /></template>
      </StatCard>
      <StatCard label="Emails Sent" :value="String(stats.emailsSent)" borderColor="#0f172a">
        <template #icon><Mail :size="24" /></template>
      </StatCard>
      <StatCard label="Click Rate" :value="`${stats.clickRate}%`" borderColor="#d97706">
        <template #icon><MousePointer :size="24" /></template>
      </StatCard>
      <StatCard label="Submission Rate" :value="`${stats.submissionRate}%`" borderColor="#dc2626">
        <template #icon><LogIn :size="24" /></template>
      </StatCard>
    </div>

    <div class="grid grid-cols-5 gap-6">
      <Card class="col-span-3">
        <h3 class="text-lg font-medium text-[#0f172a] mb-4">Campaign Click Rate Over Time</h3>
        <div v-if="stats.trend?.length" class="relative h-[300px] flex items-end gap-6 px-4">
          <div v-for="d in stats.trend" :key="d.name" class="flex-1 flex flex-col items-center gap-1">
            <div class="w-full flex flex-col items-center gap-1">
              <div class="w-full bg-[#2563eb] rounded-t" :style="{ height: d.clickRate * 2.5 + 'px' }"></div>
              <div class="w-full bg-[#dc2626] rounded-t" :style="{ height: d.submissionRate * 2.5 + 'px' }"></div>
            </div>
            <span class="text-xs text-[#94a3b8] truncate w-full text-center">{{ d.name }}</span>
          </div>
        </div>
        <div v-else class="h-[300px] flex items-center justify-center text-sm text-[#94a3b8]">No campaign data yet</div>
        <div class="flex gap-4 mt-2 text-xs">
          <div class="flex items-center gap-1"><div class="w-3 h-3 bg-[#2563eb] rounded"></div> Click Rate %</div>
          <div class="flex items-center gap-1"><div class="w-3 h-3 bg-[#dc2626] rounded"></div> Submission Rate %</div>
        </div>
      </Card>

      <Card class="col-span-2">
        <h3 class="text-lg font-medium text-[#0f172a] mb-4">Recent Activity</h3>
        <div class="space-y-3 max-h-[300px] overflow-y-auto">
          <div v-if="stats.recentActivity?.length === 0" class="text-sm text-[#94a3b8]">No activity yet</div>
          <div v-for="(a, idx) in stats.recentActivity" :key="idx" class="flex items-start gap-3">
            <div class="w-2 h-2 rounded-full mt-2 flex-shrink-0"
              :style="{ backgroundColor: a.eventType === 'CREDENTIALS_SUBMITTED' ? '#dc2626' : a.eventType === 'LINK_CLICKED' ? '#2563eb' : '#16a34a' }">
            </div>
            <div class="flex-1 min-w-0">
              <p class="text-sm text-[#0f172a]"><strong>{{ a.user }}</strong> {{ formatEventType(a.eventType) }}</p>
              <p class="text-xs text-[#94a3b8]">{{ timeAgo(a.occurredAt) }}</p>
            </div>
          </div>
        </div>
        <RouterLink to="/admin/logs" class="text-sm text-[#2563eb] hover:underline mt-3 inline-block">View all logs →</RouterLink>
      </Card>
    </div>

    <div class="grid grid-cols-10 gap-6">
      <Card class="col-span-4">
        <h3 class="text-lg font-medium text-[#0f172a] mb-4">Campaign Status Summary</h3>
        <div class="grid grid-cols-2 gap-3 mt-4">
          <div v-for="(count, status) in stats.statusCounts" :key="status" class="flex items-center gap-2">
            <div class="w-3 h-3 rounded-full" :style="{ backgroundColor: statusColor(String(status)) }"></div>
            <span class="text-sm text-[#475569]">{{ status }}: {{ count }}</span>
          </div>
        </div>
        <div v-if="Object.keys(stats.statusCounts).length" class="flex gap-2 mt-6 h-8">
          <div v-for="(count, status) in stats.statusCounts" :key="status"
            :style="{ backgroundColor: statusColor(String(status)), flex: count }" class="rounded"></div>
        </div>
      </Card>

      <Card class="col-span-3">
        <h3 class="text-lg font-medium text-[#0f172a] mb-4">Top At-Risk Users</h3>
        <div class="space-y-3">
          <div v-if="!stats.atRiskUsers?.length" class="text-sm text-[#94a3b8]">No users yet</div>
          <div v-for="user in stats.atRiskUsers" :key="user.id">
            <div class="flex justify-between items-center mb-1">
              <div class="text-sm text-[#0f172a]">{{ user.fullName }}</div>
              <div class="text-xs text-[#94a3b8]">{{ user.department }}</div>
            </div>
            <div class="flex items-center gap-2">
              <div class="flex-1 h-2 bg-[#f1f5f9] rounded-full overflow-hidden">
                <div class="h-full bg-[#dc2626] rounded-full" :style="{ width: user.riskScore + '%' }"></div>
              </div>
              <div class="text-sm font-medium text-[#dc2626]">{{ user.riskScore }}</div>
            </div>
          </div>
        </div>
        <RouterLink to="/admin/users" class="text-sm text-[#2563eb] hover:underline mt-3 inline-block">View all users →</RouterLink>
      </Card>

      <Card class="col-span-3">
        <h3 class="text-lg font-medium text-[#0f172a] mb-4">Campaign Status</h3>
        <div class="space-y-3">
          <div v-for="(count, status) in stats.statusCounts" :key="status"
            class="flex justify-between items-center p-3 bg-[#f8fafc] rounded-lg">
            <span class="text-sm font-medium text-[#0f172a]">{{ status }}</span>
            <span class="text-lg font-serif" :style="{ color: statusColor(String(status)) }">{{ count }}</span>
          </div>
          <div v-if="!Object.keys(stats.statusCounts).length" class="text-sm text-[#94a3b8]">No campaigns yet</div>
        </div>
      </Card>
    </div>

    <Card>
      <h3 class="text-lg font-medium text-[#0f172a] mb-4">Quick Actions</h3>
      <div class="flex gap-4">
        <RouterLink to="/admin/campaigns/new">
          <Button><template #icon><Target :size="18" /></template>New Campaign</Button>
        </RouterLink>
        <RouterLink to="/admin/reports">
          <Button variant="secondary"><template #icon><FileText :size="18" /></template>Generate Report</Button>
        </RouterLink>
        <RouterLink to="/admin/leaderboard">
          <Button variant="secondary"><template #icon><Trophy :size="18" /></template>View Leaderboard</Button>
        </RouterLink>
      </div>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { BarChart2, Mail, MousePointer, LogIn, Target, FileText, Trophy } from '@lucide/vue'
import StatCard from '../../components/StatCard.vue'
import Card from '../../components/Card.vue'
import Button from '../../components/Button.vue'
import { api } from '../../api'

const stats = ref<any>({
  totalCampaigns: 0, emailsSent: 0, clickRate: 0, submissionRate: 0,
  statusCounts: {}, atRiskUsers: [], recentActivity: [], trend: []
})

async function load() {
  try {
    const res = await api.get('/api/admin/dashboard/stats')
    stats.value = res.data
  } catch {}
}

const formatEventType = (e: string) => {
  if (e === 'CREDENTIALS_SUBMITTED') return 'submitted credentials'
  if (e === 'LINK_CLICKED') return 'clicked a phishing link'
  if (e === 'EMAIL_OPENED') return 'opened a phishing email'
  return e?.toLowerCase().replace(/_/g, ' ')
}

const timeAgo = (d: string) => {
  if (!d) return ''
  const diff = Date.now() - new Date(d).getTime()
  const mins = Math.floor(diff / 60000)
  if (mins < 1) return 'just now'
  if (mins < 60) return `${mins} min${mins > 1 ? 's' : ''} ago`
  const hrs = Math.floor(mins / 60)
  if (hrs < 24) return `${hrs} hr${hrs > 1 ? 's' : ''} ago`
  return `${Math.floor(hrs / 24)}d ago`
}

const statusColor = (s: string) => {
  if (s === 'ACTIVE') return '#2563eb'
  if (s === 'COMPLETED') return '#16a34a'
  if (s === 'PAUSED') return '#d97706'
  return '#94a3b8'
}

let interval: any
onMounted(() => { load(); interval = setInterval(load, 30000) })
onUnmounted(() => clearInterval(interval))
</script>
