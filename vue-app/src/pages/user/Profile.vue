<template>
  <div class="space-y-6 max-w-4xl">
    <div>
      <h2 class="text-2xl font-serif text-[#0f172a]" :style="{ fontFamily: 'var(--font-serif)' }">My Profile</h2>
      <p class="text-[#475569]">Manage your account and view your security history</p>
    </div>

    <div v-if="loading" class="flex items-center justify-center h-40 text-[#94a3b8]">Loading...</div>
    <template v-else>

      <!-- Profile card -->
      <Card>
        <div class="flex items-start gap-6">
          <div class="w-20 h-20 rounded-full bg-[#2563eb] flex items-center justify-center text-2xl text-white font-medium">
            {{ initials }}
          </div>
          <div class="flex-1">
            <h3 class="text-2xl font-serif text-[#0f172a] mb-1" :style="{ fontFamily: 'var(--font-serif)' }">{{ me.fullName }}</h3>
            <p class="text-[#475569] mb-1">{{ me.email }} {{ me.department ? '• ' + me.department : '' }}</p>
            <div class="flex items-center gap-2 mt-2">
              <span v-if="me.currentBadge" class="px-3 py-1 bg-[#fef3c7] text-[#92400e] rounded-full text-xs font-medium">
                🏅 {{ me.currentBadge }}
              </span>
              <span class="px-3 py-1 rounded-full text-xs font-medium"
                :style="{ backgroundColor: me.riskScore > 66 ? '#fee2e2' : me.riskScore > 33 ? '#fef3c7' : '#dcfce7', color: me.riskScore > 66 ? '#dc2626' : me.riskScore > 33 ? '#d97706' : '#16a34a' }">
                Risk: {{ me.riskScore }}
              </span>
            </div>
          </div>
        </div>
      </Card>

      <!-- Stats -->
      <div class="grid grid-cols-4 gap-4">
        <StatCard label="Total Points" :value="String(me.totalPoints)" borderColor="#2563eb"><template #icon><Trophy :size="22" /></template></StatCard>
        <StatCard label="Topics Done" :value="`${completedTopics}/${totalTopics}`" borderColor="#16a34a"><template #icon><BookOpen :size="22" /></template></StatCard>
        <StatCard label="Times Clicked" :value="String(clickCount)" borderColor="#d97706"><template #icon><MousePointer :size="22" /></template></StatCard>
        <StatCard label="Credentials Given" :value="String(submitCount)" borderColor="#dc2626"><template #icon><LogIn :size="22" /></template></StatCard>
      </div>

      <!-- Phishing simulation history — show what was captured -->
      <Card v-if="simEvents.length">
        <h3 class="text-lg font-medium text-[#0f172a] mb-4">Your Phishing Simulation History</h3>
        <p class="text-sm text-[#475569] mb-4">This shows every time you were targeted and what happened — so you can see how serious real attacks can be.</p>
        <div class="space-y-3">
          <div v-for="ev in simEvents" :key="ev.id"
            :class="`p-4 rounded-xl border ${ev.eventType === 'CREDENTIALS_SUBMITTED' ? 'bg-[#fef2f2] border-[#fecaca]' : ev.eventType === 'LINK_CLICKED' ? 'bg-[#fef3c7] border-[#fde68a]' : 'bg-[#f8fafc] border-[#e2e8f0]'}`">
            <div class="flex items-start justify-between">
              <div>
                <span class="text-sm font-semibold"
                  :style="{ color: ev.eventType === 'CREDENTIALS_SUBMITTED' ? '#dc2626' : ev.eventType === 'LINK_CLICKED' ? '#d97706' : '#475569' }">
                  {{ formatEvent(ev.eventType) }}
                </span>
                <p class="text-xs text-[#475569] mt-1">Campaign: {{ ev.campaign?.name || 'Unknown' }}</p>
              </div>
              <span class="text-xs text-[#94a3b8]">{{ formatDate(ev.occurredAt) }}</span>
            </div>
            <div v-if="ev.eventType === 'CREDENTIALS_SUBMITTED'" class="mt-3 bg-white rounded-lg p-3 border border-[#fecaca] text-xs font-mono space-y-1">
              <p class="text-[#94a3b8]">What was captured on the fake page:</p>
              <p class="text-[#dc2626]">Username: {{ ev.capturedUsername || '—' }}</p>
              <p class="text-[#dc2626]">Password: {{ ev.capturedPasswordMasked || '—' }}</p>
              <p class="text-[#475569]">Landing page: {{ ev.landingPageType || '—' }}</p>
              <p class="text-[#475569]">IP address: {{ ev.ipAddress || '—' }}</p>
            </div>
          </div>
        </div>
      </Card>

      <!-- Change Password -->
      <Card>
        <h3 class="text-lg font-medium text-[#0f172a] mb-2">Change Password</h3>
        <p class="text-sm text-[#475569] mb-5">Update your login password for this training platform.</p>
        <div class="space-y-4 max-w-md">
          <Input label="Current Password" type="password" placeholder="••••••••" v-model="pwForm.current" :error="pwErrors.current" />
          <Input label="New Password" type="password" placeholder="••••••••" v-model="pwForm.newPw" :error="pwErrors.newPw" />
          <Input label="Confirm New Password" type="password" placeholder="••••••••" v-model="pwForm.confirm" :error="pwErrors.confirm" />
          <p v-if="pwSuccess" class="text-sm text-[#16a34a]">✓ Password updated successfully</p>
          <Button @click="changePassword" :disabled="pwLoading">
            {{ pwLoading ? 'Updating...' : 'Update Password' }}
          </Button>
        </div>
      </Card>

    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Trophy, MousePointer, LogIn, BookOpen } from '@lucide/vue'
import Card from '../../components/Card.vue'
import StatCard from '../../components/StatCard.vue'
import Input from '../../components/Input.vue'
import Button from '../../components/Button.vue'
import { api } from '../../api'
import { toast } from 'vue-sonner'

const loading = ref(true)
const me = ref<any>({ fullName: '', email: '', department: '', riskScore: 0, totalPoints: 0, currentBadge: '', progress: [] })
const simEvents = ref<any[]>([])

const totalTopics = 5
const completedTopics = computed(() => (me.value.progress || []).filter((p: any) => p.status === 'COMPLETED').length)
const clickCount = computed(() => simEvents.value.filter(e => e.eventType === 'LINK_CLICKED').length)
const submitCount = computed(() => simEvents.value.filter(e => e.eventType === 'CREDENTIALS_SUBMITTED').length)
const initials = computed(() => me.value.fullName?.split(' ').map((n: string) => n[0]).join('').toUpperCase().slice(0, 2) || 'U')

const pwForm = ref({ current: '', newPw: '', confirm: '' })
const pwErrors = ref<Record<string, string>>({})
const pwLoading = ref(false)
const pwSuccess = ref(false)

const formatEvent = (e: string) => {
  if (e === 'CREDENTIALS_SUBMITTED') return '⚠️ You submitted credentials on a fake login page'
  if (e === 'LINK_CLICKED') return '🔗 You clicked a phishing link'
  if (e === 'EMAIL_OPENED') return '📧 You opened a phishing email'
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
    simEvents.value = evRes.data
  } catch {} finally {
    loading.value = false
  }
}

async function changePassword() {
  pwErrors.value = {}
  pwSuccess.value = false
  if (!pwForm.value.current) { pwErrors.value.current = 'Required'; return }
  if (!pwForm.value.newPw || pwForm.value.newPw.length < 8) { pwErrors.value.newPw = 'Minimum 8 characters'; return }
  if (pwForm.value.newPw !== pwForm.value.confirm) { pwErrors.value.confirm = 'Passwords do not match'; return }

  pwLoading.value = true
  try {
    await api.post('/api/user/change-password', {
      currentPassword: pwForm.value.current,
      newPassword: pwForm.value.newPw,
    })
    pwSuccess.value = true
    pwForm.value = { current: '', newPw: '', confirm: '' }
    toast.success('Password updated successfully')
  } catch (err: any) {
    pwErrors.value.current = err.response?.data?.error || 'Current password is incorrect'
  } finally {
    pwLoading.value = false
  }
}

onMounted(load)
</script>
