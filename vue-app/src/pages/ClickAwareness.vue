<template>
  <div class="min-h-full bg-white flex items-center justify-center p-5">
    <div class="w-full max-w-lg">
      <div class="flex items-center justify-between mb-4">
        <div class="flex items-center gap-1.5">
          <Shield :size="16" class="text-[#2563eb]" />
          <span class="text-xs font-semibold text-[#475569]">PhishGuard Pro — Security Simulation</span>
        </div>
        <span class="text-xs text-[#94a3b8]">{{ new Date().toLocaleTimeString() }}</span>
      </div>

      <div class="bg-white border-2 border-[#dc2626] rounded-2xl overflow-hidden shadow-lg">
        <div class="bg-[#dc2626] px-6 py-4 text-white text-center">
          <div class="w-12 h-12 rounded-full bg-white/20 flex items-center justify-center mx-auto mb-2">
            <AlertTriangle :size="24" class="text-white" />
          </div>
          <h1 class="text-lg font-bold">⚠️ You Were Just Phished</h1>
          <p class="text-xs text-red-100 mt-1">This was a controlled security simulation by your organisation</p>
        </div>

        <div class="p-5 space-y-4">
          <!-- Credentials captured -->
          <div v-if="pendingCreds" class="bg-[#fef2f2] border border-[#fecaca] rounded-xl p-4">
            <div class="flex items-center gap-2 mb-3">
              <div class="w-6 h-6 rounded-full bg-[#dc2626] flex items-center justify-center">
                <span class="text-white text-xs font-bold">!</span>
              </div>
              <h3 class="text-sm font-bold text-[#dc2626]">Your credentials were just captured</h3>
            </div>
            <p class="text-xs text-[#475569] mb-3">
              You submitted your login details to a fake <strong>{{ pendingCreds.landingType }}</strong> page.
              In a real attack, a hacker would now have access to your account.
            </p>
            <div class="bg-white border border-[#fecaca] rounded-lg p-3 font-mono text-xs space-y-2">
              <div class="flex items-center justify-between">
                <span class="text-[#94a3b8]">Username / Email:</span>
                <span class="text-[#dc2626] font-semibold">{{ pendingCreds.username }}</span>
              </div>
              <div class="flex items-center justify-between">
                <span class="text-[#94a3b8]">Password:</span>
                <div class="flex items-center gap-2">
                  <span class="text-[#dc2626] font-semibold">{{ showPassword ? pendingCreds.password : '•'.repeat(pendingCreds.password?.length || 8) }}</span>
                  <button @click="showPassword = !showPassword" class="text-[#94a3b8] hover:text-[#475569]">
                    <Eye v-if="!showPassword" :size="12" /><EyeOff v-else :size="12" />
                  </button>
                </div>
              </div>
            </div>
            <p class="text-xs text-[#dc2626] mt-2 font-medium">
              ⚠️ If this were a real attack, your {{ pendingCreds.landingType }} account would now be compromised.
            </p>
          </div>

          <!-- Click only -->
          <div v-else class="bg-[#fef3c7] border border-[#fde68a] rounded-xl p-4">
            <div class="flex items-center gap-2 mb-2">
              <AlertTriangle :size="16" class="text-[#d97706]" />
              <h3 class="text-sm font-bold text-[#d97706]">You clicked a suspicious link</h3>
            </div>
            <p class="text-xs text-[#475569]">
              In a real attack, clicking this link could have silently installed malware or stolen your session — before you even saw a login page.
            </p>
          </div>

          <!-- Red flags -->
          <div class="bg-[#f8fafc] rounded-xl p-4">
            <h3 class="text-xs font-bold text-[#0f172a] mb-2 uppercase tracking-wide">What you missed</h3>
            <div class="space-y-2">
              <div v-for="(flag, idx) in redFlags" :key="idx" class="flex items-start gap-2">
                <div class="w-5 h-5 rounded-full bg-[#fee2e2] flex items-center justify-center flex-shrink-0 mt-0.5">
                  <X :size="10" class="text-[#dc2626]" />
                </div>
                <div>
                  <p class="text-xs font-medium text-[#0f172a]">{{ flag.title }}</p>
                  <p class="text-xs text-[#475569]">{{ flag.desc }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- CTA -->
          <div class="bg-[#dbeafe] rounded-xl p-4 text-center">
            <p class="text-xs font-semibold text-[#1e40af] mb-1">Turn this into a learning moment</p>
            <p class="text-xs text-[#3b82f6] mb-3">Complete a 5-minute training to learn how to spot phishing attacks</p>
            <Button class="w-full" @click="goToTraining">
              <template #icon><GraduationCap :size="15" /></template>
              Start Security Training Now
            </Button>
          </div>

          <div class="text-center">
            <button @click="skipTraining" class="text-xs text-[#94a3b8] hover:text-[#475569] hover:underline">
              Skip for now
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { AlertTriangle, X, Shield, Eye, EyeOff, GraduationCap } from '@lucide/vue'
import Button from '../components/Button.vue'
import { getPendingCreds, clearPendingCreds, recordClick } from '../stores/simStore'

const route = useRoute()
const router = useRouter()
const token = route.params.token as string

const pendingCreds = ref<any>(null)
const showPassword = ref(false)

onMounted(async () => {
  const creds = getPendingCreds()
  if (creds && creds.token === token) {
    pendingCreds.value = creds
    clearPendingCreds()
  } else {
    await recordClick(token)
  }
})

const redFlags = [
  { title: 'Suspicious sender domain', desc: 'The email came from a domain that looked real but wasn\'t — e.g. "bankk-alert.com" instead of "bank.com"' },
  { title: 'Urgency language', desc: 'Phrases like "Your account will be locked" are designed to panic you into acting without thinking' },
  { title: 'Unexpected request', desc: 'Legitimate services never ask for your password via an email link' },
]

function goToTraining() {
  // Pass token so training session can report back against the right target user
  router.push(`/training/intro?token=${token}`)
}

function skipTraining() {
  router.push(`/training/intro?token=${token}`)
}
</script>
