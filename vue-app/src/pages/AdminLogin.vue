<template>
  <div class="min-h-screen flex">
    <!-- Left Panel -->
    <div class="w-1/2 bg-[#0f172a] text-white flex flex-col justify-between p-10">
      <RouterLink to="/" class="flex items-center gap-2 hover:opacity-80 transition-opacity">
        <Shield :size="26" />
        <span class="font-serif text-xl" :style="{ fontFamily: 'var(--font-serif)' }">PhishGuard Pro</span>
      </RouterLink>
      <div class="text-center">
        <div class="text-3xl font-serif mb-6" :style="{ fontFamily: 'var(--font-serif)' }">
          Your first line of defence starts here.
        </div>
        <div class="w-28 h-28 mx-auto bg-[#2563eb]/20 rounded-full flex items-center justify-center">
          <Shield :size="56" class="text-[#2563eb]" />
        </div>
      </div>
      <div class="flex justify-center gap-6 text-xs text-[#64748b]">
        <div class="flex items-center gap-1.5"><Lock :size="13" /><span>256-bit encrypted</span></div>
        <div class="flex items-center gap-1.5"><Shield :size="13" /><span>CSRF protected</span></div>
        <div class="flex items-center gap-1.5"><Lock :size="13" /><span>JWT + MFA secured</span></div>
      </div>
    </div>

    <!-- Right Panel -->
    <div class="w-1/2 bg-white flex items-center justify-center p-10">
      <div class="w-full max-w-md">

        <!-- ── Phase 1: Login form ── -->
        <template v-if="phase === 'login'">
          <div class="mb-6">
            <h1 class="text-2xl font-serif text-[#0f172a] mb-1" :style="{ fontFamily: 'var(--font-serif)' }">Admin Portal</h1>
            <p class="text-sm text-[#475569]">Sign in to manage your phishing campaigns</p>
          </div>
          <form @submit.prevent="handleLogin" class="space-y-5">
            <Input label="Email address" type="email" placeholder="admin@company.com" v-model="email" :error="errors.email" />
            <Input label="Password" type="password" placeholder="••••••••" v-model="password" :error="errors.password" />
            <div class="text-right">
              <RouterLink to="/forgot-password" class="text-xs text-[#2563eb] hover:underline">Forgot password?</RouterLink>
            </div>
            <Button type="submit" class="w-full" :disabled="loading">
              <span v-if="loading" class="flex items-center justify-center gap-2">
                <svg class="animate-spin w-4 h-4" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/>
                </svg>
                Signing in...
              </span>
              <span v-else>Sign In</span>
            </Button>
          </form>
        </template>

        <!-- ── Phase 2: MFA Setup (scan QR) ── -->
        <template v-if="phase === 'mfa-setup'">
          <div class="mb-6">
            <div class="w-10 h-10 rounded-full bg-[#dbeafe] flex items-center justify-center mb-3">
              <Smartphone :size="20" class="text-[#2563eb]" />
            </div>
            <h1 class="text-xl font-semibold text-[#0f172a] mb-1">Set Up Two-Factor Authentication</h1>
            <p class="text-sm text-[#475569]">Scan this QR code with Google Authenticator or Authy, then enter the 6-digit code to activate MFA.</p>
          </div>
          <div class="flex justify-center mb-4">
            <img v-if="mfaQrUri" :src="mfaQrUri" alt="MFA QR Code" class="w-40 h-40 border border-[#e2e8f0] rounded-lg" />
            <div v-else class="w-40 h-40 bg-[#f8fafc] border border-[#e2e8f0] rounded-lg flex items-center justify-center text-xs text-[#94a3b8]">
              Loading QR...
            </div>
          </div>
          <div class="bg-[#f8fafc] rounded-lg p-3 mb-4 text-xs text-[#475569]">
            <strong>Manual entry key:</strong><br>
            <code class="text-[#0f172a] break-all">{{ mfaSecret }}</code>
          </div>
          <form @submit.prevent="handleMfaEnable" class="space-y-4">
            <Input label="6-digit verification code" placeholder="000000" v-model="totpCode"
              maxlength="6" :error="errors.totp" />
            <Button type="submit" class="w-full" :disabled="loading">
              {{ loading ? 'Verifying...' : 'Activate MFA & Sign In' }}
            </Button>
          </form>
        </template>

        <!-- ── Phase 3: MFA Verify (already set up) ── -->
        <template v-if="phase === 'mfa-verify'">
          <div class="mb-6">
            <div class="w-10 h-10 rounded-full bg-[#dbeafe] flex items-center justify-center mb-3">
              <Smartphone :size="20" class="text-[#2563eb]" />
            </div>
            <h1 class="text-xl font-semibold text-[#0f172a] mb-1">Two-Factor Authentication</h1>
            <p class="text-sm text-[#475569]">Enter the 6-digit code from your authenticator app to complete sign in.</p>
          </div>
          <form @submit.prevent="handleMfaVerify" class="space-y-4">
            <Input label="Authentication code" placeholder="000000" v-model="totpCode"
              maxlength="6" :error="errors.totp" />
            <Button type="submit" class="w-full" :disabled="loading">
              {{ loading ? 'Verifying...' : 'Verify & Sign In' }}
            </Button>
            <button type="button" @click="phase = 'login'" class="w-full text-sm text-[#475569] hover:text-[#0f172a]">
              ← Back to login
            </button>
          </form>
        </template>

      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Shield, Lock, Smartphone } from '@lucide/vue'
import { toast } from 'vue-sonner'
import Input from '../components/Input.vue'
import Button from '../components/Button.vue'
import { useAuth } from '../stores/authStore'

const router = useRouter()
const { login, verifyMfa, enableMfa } = useAuth()

const phase = ref<'login' | 'mfa-setup' | 'mfa-verify'>('login')
const email = ref('')
const password = ref('')
const totpCode = ref('')
const mfaQrUri = ref('')
const mfaSecret = ref('')
const loading = ref(false)
const errors = ref<Record<string, string>>({})

async function handleLogin() {
  errors.value = {}
  if (!email.value) { errors.value.email = 'Email is required'; return }
  if (!password.value) { errors.value.password = 'Password is required'; return }

  loading.value = true
  try {
    const result = await login(email.value, password.value)

    if (result.mfaRequired) {
      phase.value = 'mfa-verify'
    } else if (result.mfaSetupRequired) {
      mfaQrUri.value = result.mfaQrUri || ''
      mfaSecret.value = result.mfaSecret || ''
      phase.value = 'mfa-setup'
    } else {
      toast.success('Welcome back!')
      router.push('/admin/dashboard')
    }
  } catch (err: any) {
    const msg = err.response?.data?.error || 'Login failed. Please try again.'
    if (msg.toLowerCase().includes('locked')) {
      errors.value.password = msg
    } else {
      errors.value.password = msg
    }
  } finally {
    loading.value = false
  }
}

async function handleMfaEnable() {
  errors.value = {}
  if (!totpCode.value || totpCode.value.length !== 6) {
    errors.value.totp = 'Enter the 6-digit code from your authenticator app'
    return
  }
  loading.value = true
  try {
    await enableMfa(email.value, totpCode.value)
    toast.success('MFA activated! You are now signed in.')
    router.push('/admin/dashboard')
  } catch (err: any) {
    errors.value.totp = err.response?.data?.error || 'Invalid code. Please try again.'
  } finally {
    loading.value = false
  }
}

async function handleMfaVerify() {
  errors.value = {}
  if (!totpCode.value || totpCode.value.length !== 6) {
    errors.value.totp = 'Enter the 6-digit code from your authenticator app'
    return
  }
  loading.value = true
  try {
    await verifyMfa(email.value, totpCode.value)
    toast.success('Welcome back!')
    router.push('/admin/dashboard')
  } catch (err: any) {
    errors.value.totp = err.response?.data?.error || 'Invalid code. Please try again.'
  } finally {
    loading.value = false
  }
}
</script>
