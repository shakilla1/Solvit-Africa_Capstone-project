<template>
  <div class="min-h-screen bg-[#f8fafc] flex items-center justify-center p-8">
    <Card class="w-full max-w-md">
      <div class="flex justify-center mb-6">
        <div class="w-16 h-16 rounded-full bg-[#dbeafe] flex items-center justify-center">
          <Shield :size="32" class="text-[#2563eb]" />
        </div>
      </div>
      <h1 class="text-2xl font-serif text-center text-[#0f172a] mb-2" :style="{ fontFamily: 'var(--font-serif)' }">Welcome Back</h1>
      <p class="text-center text-[#475569] mb-6">Sign in to view your training progress and badges</p>

      <!-- First time? Set password notice -->
      <div class="bg-[#fef3c7] border border-[#fde68a] rounded-xl p-3 mb-5 text-sm text-[#92400e]">
        <strong>First time?</strong> Use the temporary password sent to you, or ask your administrator to set one for you.
      </div>

      <form @submit.prevent="handleSubmit" class="space-y-5">
        <Input label="Email" type="email" placeholder="you@example.com" v-model="email" :error="errors.email" />
        <Input label="Password" type="password" placeholder="••••••••" v-model="password" :error="errors.password" />
        <p v-if="errors.general" class="text-sm text-[#dc2626] text-center">{{ errors.general }}</p>
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
    </Card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Shield } from '@lucide/vue'
import { toast } from 'vue-sonner'
import Card from '../components/Card.vue'
import Input from '../components/Input.vue'
import Button from '../components/Button.vue'
import { api } from '../api'

const router = useRouter()
const email = ref('')
const password = ref('')
const loading = ref(false)
const errors = ref<Record<string, string>>({})

async function handleSubmit() {
  errors.value = {}
  if (!email.value) { errors.value.email = 'Email is required'; return }
  if (!password.value) { errors.value.password = 'Password is required'; return }

  loading.value = true
  try {
    const { data } = await api.post('/api/auth/user/login', { email: email.value, password: password.value })

    localStorage.setItem('accessToken', data.accessToken)
    localStorage.setItem('refreshToken', data.refreshToken)
    localStorage.setItem('currentUser', JSON.stringify({
      email: data.email,
      fullName: data.fullName,
      role: data.role,
      userId: data.userId,
    }))

    // Load training progress from backend into localStorage so Training.vue can use it
    try {
      const meRes = await api.get('/api/user/training/me')
      const progress = meRes.data.progress || []
      const mapped = progress.map((p: any) => ({
        topicId: p.topicId,
        topicTitle: p.topicTitle,
        status: p.status === 'COMPLETED' ? 'completed' : 'in_progress',
        score: p.score,
        completedAt: p.completedAt,
        answers: {},
      }))
      localStorage.setItem('phishguard_training_progress', JSON.stringify(mapped))
    } catch {}

    toast.success(`Welcome back, ${data.fullName}!`)
    router.push('/dashboard')
  } catch (err: any) {
    errors.value.general = err.response?.data?.error || 'Invalid email or password'
  } finally {
    loading.value = false
  }
}
</script>
