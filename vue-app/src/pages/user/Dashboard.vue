<template>
  <div class="space-y-6">
    <div v-if="loading" class="flex items-center justify-center h-40 text-[#94a3b8]">Loading...</div>
    <template v-else>
      <Card leftBorderColor="#2563eb">
        <h1 class="text-2xl font-serif text-[#0f172a] mb-1" :style="{ fontFamily: 'var(--font-serif)' }">
          Welcome back, {{ me.fullName }} 👋
        </h1>
        <p class="text-[#475569]">Here's your security awareness progress.</p>
      </Card>

      <div class="grid grid-cols-4 gap-6">
        <StatCard label="Total Points" :value="String(me.totalPoints)" borderColor="#2563eb"><template #icon><Trophy :size="24" /></template></StatCard>
        <StatCard label="Current Badge" :value="me.currentBadge || 'None yet'" borderColor="#d97706"><template #icon><Award :size="24" /></template></StatCard>
        <StatCard label="Topics Completed" :value="`${completedTopics}/${totalTopics}`" borderColor="#16a34a"><template #icon><BookOpen :size="24" /></template></StatCard>
        <StatCard label="Risk Score" :value="String(me.riskScore)" :borderColor="me.riskScore > 66 ? '#dc2626' : me.riskScore > 33 ? '#d97706' : '#16a34a'"><template #icon><AlertTriangle :size="24" /></template></StatCard>
      </div>

      <div class="grid grid-cols-5 gap-6">
        <!-- Training progress -->
        <Card class="col-span-3">
          <h3 class="text-lg font-medium text-[#0f172a] mb-4">Training Progress</h3>
          <div v-if="me.progress?.length" class="space-y-3">
            <div v-for="p in me.progress" :key="p.topicId" class="flex items-center gap-3">
              <div class="flex-1">
                <div class="flex justify-between text-xs mb-1">
                  <span class="text-[#0f172a] font-medium">{{ p.topicTitle }}</span>
                  <span :class="p.status === 'COMPLETED' ? 'text-[#16a34a]' : 'text-[#d97706]'">
                    {{ p.status === 'COMPLETED' ? `${p.score}%` : 'In Progress' }}
                  </span>
                </div>
                <div class="h-2 bg-[#e2e8f0] rounded-full overflow-hidden">
                  <div class="h-full rounded-full transition-all"
                    :style="{ width: p.status === 'COMPLETED' ? p.score + '%' : '40%', backgroundColor: p.status === 'COMPLETED' ? '#16a34a' : '#d97706' }">
                  </div>
                </div>
              </div>
              <span v-if="p.status === 'COMPLETED'" class="text-[#16a34a] text-sm">✓</span>
            </div>
            <div v-if="!me.progress?.length" class="text-sm text-[#94a3b8]">No training started yet.</div>
          </div>
          <RouterLink to="/training" class="text-sm text-[#2563eb] hover:underline mt-4 inline-block">
            Go to Training →
          </RouterLink>
        </Card>

        <!-- Phishing simulation history -->
        <Card class="col-span-2">
          <h3 class="text-lg font-medium text-[#0f172a] mb-4">Simulation History</h3>
          <div v-if="simHistory.length" class="space-y-3">
            <div v-for="ev in simHistory" :key="ev.id" class="flex items-start gap-3">
              <div class="w-2 h-2 rounded-full mt-2 flex-shrink-0"
                :style="{ backgroundColor: ev.eventType === 'CREDENTIALS_SUBMITTED' ? '#dc2626' : ev.eventType === 'LINK_CLICKED' ? '#d97706' : '#16a34a' }">
              </div>
              <div class="flex-1 min-w-0">
                <p class="text-sm text-[#0f172a]">{{ formatEvent(ev.eventType) }}</p>
                <p class="text-xs text-[#94a3b8]">{{ formatDate(ev.occurredAt) }}</p>
              </div>
            </div>
          </div>
          <p v-else class="text-sm text-[#94a3b8]">No simulation events yet.</p>
        </Card>
      </div>

      <!-- Risk score indicator -->
      <Card>
        <h3 class="text-lg font-medium text-[#0f172a] mb-3">Your Risk Level</h3>
        <div class="flex items-center gap-4">
          <div class="flex-1 h-4 bg-[#e2e8f0] rounded-full overflow-hidden">
            <div class="h-full rounded-full transition-all"
              :style="{ width: me.riskScore + '%', backgroundColor: me.riskScore > 66 ? '#dc2626' : me.riskScore > 33 ? '#d97706' : '#16a34a' }">
            </div>
          </div>
          <span class="text-lg font-bold w-12"
            :style="{ color: me.riskScore > 66 ? '#dc2626' : me.riskScore > 33 ? '#d97706' : '#16a34a' }">
            {{ me.riskScore }}
          </span>
        </div>
        <p class="text-xs text-[#475569] mt-2">
          {{ me.riskScore > 66 ? 'High risk — complete your training to reduce this score.' : me.riskScore > 33 ? 'Moderate risk — keep completing training modules.' : 'Low risk — great job staying security aware!' }}
        </p>
      </Card>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Trophy, Award, AlertTriangle, BookOpen } from '@lucide/vue'
import Card from '../../components/Card.vue'
import StatCard from '../../components/StatCard.vue'
import { api } from '../../api'

const loading = ref(true)
const me = ref<any>({ fullName: '', totalPoints: 0, currentBadge: '', riskScore: 0, progress: [] })
const simHistory = ref<any[]>([])

const totalTopics = 5
const completedTopics = computed(() =>
  (me.value.progress || []).filter((p: any) => p.status === 'COMPLETED').length
)

const formatEvent = (e: string) => {
  if (e === 'CREDENTIALS_SUBMITTED') return '⚠️ Submitted credentials on fake page'
  if (e === 'LINK_CLICKED') return '🔗 Clicked a phishing link'
  if (e === 'EMAIL_OPENED') return '📧 Opened a phishing email'
  return e
}

const formatDate = (d: string) => d ? new Date(d).toLocaleString() : ''

async function load() {
  try {
    const [meRes, evRes] = await Promise.all([
      api.get('/api/user/training/me'),
      api.get('/api/user/events'),
    ])
    me.value = meRes.data
    simHistory.value = evRes.data.slice(0, 5)
  } catch {} finally {
    loading.value = false
  }
}

onMounted(load)
</script>
