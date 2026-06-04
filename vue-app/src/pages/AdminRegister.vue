<template>
  <div class="min-h-screen flex">
    <div class="w-1/2 bg-[#0f172a] text-white flex flex-col justify-between p-12">
      <div class="flex items-center gap-2">
        <Shield :size="28" />
        <span class="font-serif text-2xl" :style="{ fontFamily: 'var(--font-serif)' }">PhishGuard Pro</span>
      </div>
      <div class="text-center">
        <div class="text-4xl font-serif mb-8" :style="{ fontFamily: 'var(--font-serif)' }">Your first line of defence starts here.</div>
        <div class="w-32 h-32 mx-auto bg-[#2563eb]/20 rounded-full flex items-center justify-center">
          <Shield :size="64" class="text-[#2563eb]" />
        </div>
      </div>
      <div class="flex justify-center gap-8 text-sm">
        <div class="flex items-center gap-2"><Lock :size="16" /><span>256-bit encrypted</span></div>
        <div class="flex items-center gap-2"><Shield :size="16" /><span>CSRF protected</span></div>
        <div class="flex items-center gap-2"><Lock :size="16" /><span>JWT secured</span></div>
      </div>
    </div>

    <div class="w-1/2 bg-white flex items-center justify-center p-12 overflow-y-auto">
      <div class="w-full max-w-md">
        <div class="mb-8">
          <h1 class="text-3xl font-serif text-[#0f172a] mb-2" :style="{ fontFamily: 'var(--font-serif)' }">Create Account</h1>
          <p class="text-[#475569]">Start your free trial today</p>
        </div>
        <form @submit.prevent="handleSubmit" class="space-y-5">
          <Input label="Full Name" placeholder="John Doe" v-model="form.fullName" :error="errors.fullName" />
          <Input label="Organisation Name" placeholder="Acme Inc" v-model="form.orgName" :error="errors.orgName" />
          <Input label="Email Address" type="email" placeholder="admin@company.com" v-model="form.email" :error="errors.email" />
          <div>
            <Input label="Password" type="password" placeholder="••••••••" v-model="form.password" :error="errors.password" />
            <div v-if="form.password" class="mt-2">
              <div class="flex gap-1 mb-1">
                <div v-for="i in 4" :key="i" class="h-1 flex-1 rounded" :style="{ backgroundColor: i <= form.password.length / 2 ? strength.color : '#e2e8f0' }"></div>
              </div>
              <span class="text-xs" :style="{ color: strength.color }">{{ strength.label }}</span>
            </div>
          </div>
          <Input label="Confirm Password" type="password" placeholder="••••••••" v-model="form.confirmPassword" :error="errors.confirmPassword" />
          <div class="flex items-start gap-2">
            <input type="checkbox" id="terms" v-model="form.agreedToTerms" class="mt-1" />
            <label for="terms" class="text-sm text-[#475569]">
              I agree to the <a href="#" class="text-[#2563eb] hover:underline">Terms of Service</a> and <a href="#" class="text-[#2563eb] hover:underline">Privacy Policy</a>
            </label>
          </div>
          <p v-if="errors.terms" class="text-sm text-[#dc2626]">{{ errors.terms }}</p>
          <Button type="submit" class="w-full" :disabled="loading">
            <span v-if="loading" class="flex items-center justify-center gap-2">
              <svg class="animate-spin w-4 h-4" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/></svg>
              Creating...
            </span>
            <span v-else>Create Account</span>
          </Button>
          <div class="text-center text-sm text-[#475569]">
            Already have an account? <RouterLink to="/admin/login" class="text-[#2563eb] hover:underline">Sign in</RouterLink>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Shield, Lock } from '@lucide/vue'
import { toast } from 'vue-sonner'
import Input from '../components/Input.vue'
import Button from '../components/Button.vue'
import { authApi } from '../api'

const router = useRouter()
const form = ref({ fullName: '', orgName: '', email: '', password: '', confirmPassword: '', agreedToTerms: false })
const errors = ref<Record<string, string>>({})
const loading = ref(false)

const strength = computed(() => {
  const len = form.value.password.length
  if (len === 0) return { label: '', color: '' }
  if (len < 4) return { label: 'Weak', color: '#dc2626' }
  if (len < 6) return { label: 'Fair', color: '#d97706' }
  if (len < 8) return { label: 'Good', color: '#2563eb' }
  return { label: 'Strong', color: '#16a34a' }
})

async function handleSubmit() {
  const e: Record<string, string> = {}
  if (!form.value.fullName) e.fullName = 'This field is required'
  if (!form.value.orgName) e.orgName = 'This field is required'
  if (!form.value.email) e.email = 'This field is required'
  if (!form.value.password) e.password = 'This field is required'
  if (form.value.password !== form.value.confirmPassword) e.confirmPassword = 'Passwords do not match'
  if (!form.value.agreedToTerms) e.terms = 'You must agree to the terms'
  if (Object.keys(e).length > 0) { errors.value = e; return }

  loading.value = true
  try {
    const { data } = await authApi.register(form.value.fullName, form.value.orgName, form.value.email, form.value.password)
    const { message, email, fullName, role, plan } = data
    localStorage.setItem('accessToken', 'temp-' + email)
    localStorage.setItem('refreshToken', 'temp-refresh-' + email)
    localStorage.setItem('currentUser', JSON.stringify({ email, fullName, role: role || 'ROLE_ADMIN', plan: plan || 'ENTERPRISE' }))
    loading.value = false
    toast.success(message || 'Account created successfully!')
    router.push('/admin/dashboard')
  } catch (err: any) {
    loading.value = false
    const msg = err.response?.data?.message || err.response?.data?.error || 'Registration failed. Please try again.'
    toast.error(msg)
  }
}
</script>
