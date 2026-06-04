<template>
  <div class="space-y-6">
    <div v-if="loading" class="flex items-center justify-center h-40 text-[#94a3b8]">Loading...</div>
    <template v-else-if="campaign">
      <div class="flex justify-between items-start">
        <div>
          <div class="flex items-center gap-3 mb-2">
            <h2 class="text-2xl font-serif text-[#0f172a]" :style="{ fontFamily: 'var(--font-serif)' }">{{ campaign.name }}</h2>
            <Badge :variant="campaign.status === 'ACTIVE' ? 'blue' : campaign.status === 'COMPLETED' ? 'green' : 'amber'">{{ campaign.status }}</Badge>
            <Badge variant="amber">{{ campaign.difficulty }}</Badge>
          </div>
          <p class="text-[#475569]">Campaign #{{ campaign.id }} • Launched {{ formatDate(campaign.launchedAt) }}</p>
        </div>
        <div class="flex gap-3">
          <Button variant="secondary" @click="togglePause">
            <template #icon><Pause v-if="campaign.status === 'ACTIVE'" :size="18" /><Play v-else :size="18" /></template>
            {{ campaign.status === 'ACTIVE' ? 'Pause' : 'Resume' }}
          </Button>
          <Button variant="secondary" @click="downloadCsv">
            <template #icon><Download :size="18" /></template>Download Report
          </Button>
        </div>
      </div>

      <div class="grid grid-cols-4 gap-6">
        <StatCard label="Emails Sent" :value="String(campaign.emailsSent)" borderColor="#2563eb"><template #icon><Mail :size="24" /></template></StatCard>
        <StatCard label="Opened" :value="`${campaign.emailsOpened} (${pct(campaign.emailsOpened, campaign.emailsSent)}%)`" borderColor="#16a34a"><template #icon><Eye :size="24" /></template></StatCard>
        <StatCard label="Clicked" :value="`${campaign.linksClicked} (${pct(campaign.linksClicked, campaign.emailsSent)}%)`" borderColor="#d97706"><template #icon><MousePointer :size="24" /></template></StatCard>
        <StatCard label="Submitted" :value="`${campaign.credentialsSubmitted} (${pct(campaign.credentialsSubmitted, campaign.emailsSent)}%)`" borderColor="#dc2626"><template #icon><LogIn :size="24" /></template></StatCard>
      </div>

      <!-- Tabs -->
      <div class="border-b border-[#e2e8f0]">
        <div class="flex gap-6">
          <button v-for="tab in tabs" :key="tab" @click="activeTab = tab"
            :class="`pb-3 px-1 text-sm font-medium border-b-2 transition-colors ${activeTab === tab ? 'border-[#2563eb] text-[#2563eb]' : 'border-transparent text-[#475569] hover:text-[#0f172a]'}`">
            {{ capitalize(tab) }}
          </button>
        </div>
      </div>

      <!-- Targets Tab -->
      <Card v-if="activeTab === 'targets'" class="p-0 overflow-hidden">
        <div class="p-6 border-b border-[#e2e8f0] flex justify-between items-center">
          <h3 class="text-lg font-medium text-[#0f172a]">Target Users</h3>
          <Button variant="secondary" size="sm" @click="downloadCsv"><template #icon><Download :size="15" /></template>Export CSV</Button>
        </div>
        <table class="w-full">
          <thead class="bg-[#f8fafc] border-b border-[#e2e8f0]">
            <tr>
              <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">Name</th>
              <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">Email</th>
              <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">Department</th>
              <th class="text-center px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">Sent</th>
              <th class="text-center px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">Clicked</th>
              <th class="text-center px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">Submitted</th>
              <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">Risk</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-[#e2e8f0]">
            <tr v-for="t in targets" :key="t.id" class="hover:bg-[#f8fafc]">
              <td class="px-6 py-4 text-sm text-[#0f172a]">{{ t.targetUser.fullName }}</td>
              <td class="px-6 py-4 text-sm text-[#475569]">{{ t.targetUser.email }}</td>
              <td class="px-6 py-4 text-sm text-[#475569]">{{ t.targetUser.department || '—' }}</td>
              <td class="px-6 py-4 text-center text-sm">{{ t.emailSent ? '✓' : '—' }}</td>
              <td class="px-6 py-4 text-center">
                <span v-if="t.linkClicked" class="px-2 py-0.5 bg-[#dbeafe] text-[#2563eb] rounded text-xs font-medium">Clicked</span>
                <span v-else class="text-sm text-[#94a3b8]">—</span>
              </td>
              <td class="px-6 py-4 text-center">
                <span v-if="t.credentialsSubmitted" class="px-2 py-0.5 bg-[#fee2e2] text-[#dc2626] rounded text-xs font-medium">Submitted</span>
                <span v-else class="text-sm text-[#94a3b8]">—</span>
              </td>
              <td class="px-6 py-4">
                <Badge :variant="t.credentialsSubmitted ? 'red' : t.linkClicked ? 'amber' : 'green'">
                  {{ t.credentialsSubmitted ? 'High' : t.linkClicked ? 'Medium' : 'Low' }}
                </Badge>
              </td>
            </tr>
            <tr v-if="targets.length === 0">
              <td colspan="7" class="px-6 py-8 text-center text-sm text-[#94a3b8]">No targets found</td>
            </tr>
          </tbody>
        </table>
      </Card>

      <!-- Events Tab -->
      <Card v-if="activeTab === 'events'" class="p-0 overflow-hidden">
        <div class="p-5 border-b border-[#e2e8f0]">
          <h3 class="text-lg font-medium text-[#0f172a]">Event Log</h3>
        </div>
        <table class="w-full">
          <thead class="bg-[#f8fafc] border-b border-[#e2e8f0]">
            <tr>
              <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">Timestamp</th>
              <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">User</th>
              <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">Event</th>
              <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">IP Address</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-[#e2e8f0]">
            <tr v-for="ev in events" :key="ev.id"
              :class="`hover:bg-[#f8fafc] ${ev.eventType === 'LINK_CLICKED' ? 'bg-[#dbeafe]/20' : ev.eventType === 'CREDENTIALS_SUBMITTED' ? 'bg-[#fee2e2]/20' : ''}`">
              <td class="px-6 py-3 text-xs text-[#475569]">{{ formatDate(ev.occurredAt) }}</td>
              <td class="px-6 py-3 text-sm text-[#0f172a]">{{ ev.targetUser?.fullName }}</td>
              <td class="px-6 py-3">
                <span :class="`px-2 py-1 rounded text-xs font-medium ${
                  ev.eventType === 'LINK_CLICKED' ? 'bg-[#dbeafe] text-[#2563eb]' :
                  ev.eventType === 'CREDENTIALS_SUBMITTED' ? 'bg-[#fee2e2] text-[#dc2626]' :
                  'bg-[#f1f5f9] text-[#475569]'}`">
                  {{ formatEvent(ev.eventType) }}
                </span>
              </td>
              <td class="px-6 py-3 text-xs text-[#475569]">{{ ev.ipAddress || '—' }}</td>
            </tr>
            <tr v-if="events.length === 0">
              <td colspan="4" class="px-6 py-8 text-center text-sm text-[#94a3b8]">No events recorded yet</td>
            </tr>
          </tbody>
        </table>
      </Card>

      <!-- Report Tab -->
      <Card v-if="activeTab === 'report'">
        <div class="flex justify-between items-center mb-6">
          <h3 class="text-lg font-medium text-[#0f172a]">Campaign Report</h3>
          <Button @click="downloadCsv"><template #icon><Download :size="15" /></template>Download CSV</Button>
        </div>
        <div class="space-y-4">
          <div class="grid grid-cols-4 gap-4">
            <div class="p-4 bg-[#f8fafc] rounded-lg text-center">
              <div class="text-2xl font-serif text-[#0f172a] mb-1">{{ campaign.emailsSent }}</div>
              <div class="text-xs text-[#94a3b8]">Emails Sent</div>
            </div>
            <div class="p-4 bg-[#f8fafc] rounded-lg text-center">
              <div class="text-2xl font-serif text-[#d97706] mb-1">{{ pct(campaign.linksClicked, campaign.emailsSent) }}%</div>
              <div class="text-xs text-[#94a3b8]">Click Rate</div>
            </div>
            <div class="p-4 bg-[#f8fafc] rounded-lg text-center">
              <div class="text-2xl font-serif text-[#dc2626] mb-1">{{ pct(campaign.credentialsSubmitted, campaign.emailsSent) }}%</div>
              <div class="text-xs text-[#94a3b8]">Submission Rate</div>
            </div>
            <div class="p-4 bg-[#f8fafc] rounded-lg text-center">
              <div class="text-2xl font-serif text-[#16a34a] mb-1">{{ campaign.totalTargets }}</div>
              <div class="text-xs text-[#94a3b8]">Total Targets</div>
            </div>
          </div>
          <div class="p-4 bg-[#f8fafc] rounded-lg">
            <h4 class="text-sm font-medium text-[#0f172a] mb-3">Phished Users</h4>
            <table class="w-full">
              <thead>
                <tr>
                  <th class="text-left text-xs text-[#94a3b8] pb-2">Name</th>
                  <th class="text-left text-xs text-[#94a3b8] pb-2">Email</th>
                  <th class="text-left text-xs text-[#94a3b8] pb-2">Action</th>
                  <th class="text-left text-xs text-[#94a3b8] pb-2">Time</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-[#e2e8f0]">
                <tr v-for="t in phishedTargets" :key="t.id">
                  <td class="py-2 text-sm text-[#0f172a]">{{ t.targetUser.fullName }}</td>
                  <td class="py-2 text-sm text-[#475569]">{{ t.targetUser.email }}</td>
                  <td class="py-2">
                    <span v-if="t.credentialsSubmitted" class="px-2 py-0.5 bg-[#fee2e2] text-[#dc2626] rounded text-xs">Submitted Credentials</span>
                    <span v-else class="px-2 py-0.5 bg-[#dbeafe] text-[#2563eb] rounded text-xs">Clicked Link</span>
                  </td>
                  <td class="py-2 text-xs text-[#475569]">{{ formatDate(t.submittedAt || t.clickedAt) }}</td>
                </tr>
                <tr v-if="phishedTargets.length === 0">
                  <td colspan="4" class="py-4 text-center text-sm text-[#94a3b8]">No users phished yet</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </Card>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { Pause, Play, Mail, Eye, MousePointer, LogIn, Download } from '@lucide/vue'
import { toast } from 'vue-sonner'
import Card from '../../components/Card.vue'
import Button from '../../components/Button.vue'
import Badge from '../../components/Badge.vue'
import StatCard from '../../components/StatCard.vue'
import { campaignApi, api } from '../../api'

const route = useRoute()
const id = Number(route.params.id)
const loading = ref(true)
const campaign = ref<any>(null)
const targets = ref<any[]>([])
const events = ref<any[]>([])
const activeTab = ref('targets')
const tabs = ['targets', 'events', 'report']

const capitalize = (s: string) => s.charAt(0).toUpperCase() + s.slice(1)
const pct = (a: number, b: number) => b === 0 ? 0 : Math.round(a / b * 100)
const formatDate = (d: string) => d ? new Date(d).toLocaleString() : '—'
const formatEvent = (e: string) => e?.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase())

const phishedTargets = computed(() => targets.value.filter(t => t.linkClicked || t.credentialsSubmitted))

async function load() {
  try {
    const [cRes, tRes, eRes] = await Promise.all([
      campaignApi.get(id),
      api.get(`/api/admin/campaigns/${id}/targets`),
      api.get(`/api/admin/campaigns/${id}/events`),
    ])
    campaign.value = cRes.data
    targets.value = tRes.data
    events.value = eRes.data
  } catch {
    toast.error('Failed to load campaign data')
  } finally {
    loading.value = false
  }
}

async function togglePause() {
  try {
    const res = await campaignApi.togglePause(id)
    campaign.value = res.data
    toast.success(`Campaign ${res.data.status === 'PAUSED' ? 'paused' : 'resumed'}`)
  } catch {
    toast.error('Failed to update campaign')
  }
}

async function downloadCsv() {
  try {
    const res = await api.get(`/api/admin/campaigns/${id}/report/csv`, { responseType: 'blob' })
    const url = URL.createObjectURL(res.data)
    const a = document.createElement('a')
    a.href = url
    a.download = `campaign-${id}-report.csv`
    a.click()
    URL.revokeObjectURL(url)
    toast.success('Report downloaded')
  } catch {
    toast.error('Failed to download report')
  }
}

onMounted(load)
</script>
