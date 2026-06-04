<template>
  <div class="min-h-full flex items-center justify-center p-6" :style="{ backgroundColor: landing.bg }">
    <div class="w-full max-w-sm">
      <!-- Realistic fake login card -->
      <div class="bg-white rounded-2xl shadow-xl overflow-hidden">
        <!-- Brand header -->
        <div class="px-8 pt-8 pb-4 text-center">
          <div class="w-14 h-14 rounded-full flex items-center justify-center text-2xl font-bold text-white mx-auto mb-3"
            :style="{ backgroundColor: landing.color }">
            {{ landing.logo }}
          </div>
          <h1 class="text-lg font-semibold text-[#0f172a]">Sign in to {{ landing.name }}</h1>
          <p class="text-xs text-[#475569] mt-1">{{ landing.subtitle }}</p>
        </div>

        <!-- Form -->
        <div class="px-8 pb-8">
          <form @submit.prevent="handleSubmit" class="space-y-4">
            <div>
              <label class="block text-xs font-medium text-[#374151] mb-1">{{ landing.emailLabel }}</label>
              <input
                v-model="username"
                :type="landing.id === 'whatsapp' ? 'tel' : 'email'"
                :placeholder="landing.emailPlaceholder"
                class="w-full px-3 py-2.5 border border-[#d1d5db] rounded-lg text-sm focus:outline-none focus:border-[#2563eb] focus:ring-2 focus:ring-[#2563eb]/20"
                required
              />
            </div>
            <div>
              <label class="block text-xs font-medium text-[#374151] mb-1">Password</label>
              <input
                v-model="password"
                type="password"
                placeholder="••••••••"
                class="w-full px-3 py-2.5 border border-[#d1d5db] rounded-lg text-sm focus:outline-none focus:border-[#2563eb] focus:ring-2 focus:ring-[#2563eb]/20"
                required
              />
            </div>

            <div v-if="landing.id === 'bank'" class="flex items-center gap-2">
              <input type="checkbox" id="remember" class="rounded" />
              <label for="remember" class="text-xs text-[#475569]">Remember this device for 30 days</label>
            </div>

            <!-- Loading state -->
            <button
              type="submit"
              :disabled="submitting"
              class="w-full py-2.5 rounded-lg text-sm font-semibold text-white transition-all"
              :style="{ backgroundColor: submitting ? '#94a3b8' : landing.color }"
            >
              <span v-if="submitting" class="flex items-center justify-center gap-2">
                <svg class="animate-spin w-4 h-4" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path>
                </svg>
                Signing in...
              </span>
              <span v-else>Sign In</span>
            </button>

            <div class="text-center">
              <a href="#" class="text-xs text-[#2563eb] hover:underline">Forgot password?</a>
            </div>
          </form>

          <!-- Security badge (fake) -->
          <div class="mt-5 pt-4 border-t border-[#f1f5f9] flex items-center justify-center gap-1.5">
            <svg class="w-3 h-3 text-[#16a34a]" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clip-rule="evenodd"/>
            </svg>
            <span class="text-[10px] text-[#475569]">256-bit SSL encrypted · Secure connection</span>
          </div>
        </div>
      </div>

      <!-- Tiny simulation badge — barely visible -->
      <div class="text-center mt-3">
        <span class="text-[9px] text-[#94a3b8] opacity-30">PhishGuard Simulation</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { recordCredentials } from '../stores/simStore'

const route = useRoute()
const router = useRouter()
const token = route.params.token as string

const username = ref('')
const password = ref('')
const submitting = ref(false)

const landingTypes = [
  {
    id: 'gmail', name: 'Google', logo: 'G', color: '#4285f4', bg: '#f8fafc',
    subtitle: 'Use your Google Account', emailLabel: 'Email or phone', emailPlaceholder: 'you@gmail.com',
  },
  {
    id: 'mtn', name: 'MTN Customer Portal', logo: 'M', color: '#ffcb05', bg: '#fffbeb',
    subtitle: 'Access your MTN account', emailLabel: 'Phone number or email', emailPlaceholder: '080xxxxxxxx',
  },
  {
    id: 'university', name: 'Student Portal', logo: 'U', color: '#1e40af', bg: '#eff6ff',
    subtitle: 'University of Technology', emailLabel: 'Student email', emailPlaceholder: 'student@university.edu',
  },
  {
    id: 'bank', name: 'SecureBank Online', logo: 'B', color: '#059669', bg: '#f0fdf4',
    subtitle: 'Internet Banking', emailLabel: 'Account email', emailPlaceholder: 'you@email.com',
  },
  {
    id: 'whatsapp', name: 'WhatsApp Web', logo: 'W', color: '#25d366', bg: '#f0fdf4',
    subtitle: 'Connect your phone', emailLabel: 'Phone number', emailPlaceholder: '+27 80 000 0000',
  },
  {
    id: 'microsoft', name: 'Microsoft', logo: 'M', color: '#0078d4', bg: '#f0f9ff',
    subtitle: 'Sign in to your Microsoft account', emailLabel: 'Email, phone, or Skype', emailPlaceholder: 'you@outlook.com',
  },
]

// Pick landing type from token or random
const tokenIndex = token ? token.charCodeAt(0) % landingTypes.length : Math.floor(Math.random() * landingTypes.length)
const landing = landingTypes[tokenIndex]

async function handleSubmit() {
  if (!username.value || !password.value) return
  submitting.value = true

  // Simulate network delay (makes it feel real)
  await new Promise(r => setTimeout(r, 1800))

  // Record the credential submission
  await recordCredentials(token || 'demo', username.value, password.value, landing.id)

  // Redirect to awareness page
  router.push(`/track/${token || 'demo'}`)
}
</script>
