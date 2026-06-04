<template>
  <div class="min-h-screen bg-[#f8fafc] flex items-center justify-center p-8">
    <Card class="w-full max-w-md">
      <div class="flex justify-center mb-6">
        <div class="w-16 h-16 rounded-full bg-[#dbeafe] flex items-center justify-center">
          <Shield :size="32" class="text-[#2563eb]" />
        </div>
      </div>
      <h1 class="text-2xl font-serif text-center text-[#0f172a] mb-2" :style="{ fontFamily: 'var(--font-serif)' }">Create Account</h1>
      <p class="text-center text-[#475569] mb-8">Join your organization's security training</p>
      <form @submit.prevent="handleSubmit" class="space-y-4">
        <Input label="Full Name" placeholder="John Doe" v-model="form.fullName" />
        <Input label="Email" type="email" placeholder="you@example.com" v-model="form.email" />
        <Input label="Password" type="password" placeholder="••••••••" v-model="form.password" />
        <Input label="Confirm Password" type="password" placeholder="••••••••" v-model="form.confirmPassword" />
        <div class="flex items-start gap-2">
          <input type="checkbox" id="terms" v-model="form.agreedToTerms" class="mt-1" />
          <label for="terms" class="text-sm text-[#475569]">I agree to the Terms of Service and Privacy Policy</label>
        </div>
        <Button type="submit" class="w-full">Create Account</Button>
        <div class="text-center text-sm">
          <RouterLink to="/login" class="text-[#2563eb] hover:underline">Already have an account? Sign in</RouterLink>
        </div>
      </form>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Shield } from '@lucide/vue'
import { toast } from 'vue-sonner'
import { authApi } from '../api'
import { useAuth } from '../stores/authStore'
import Card from '../components/Card.vue'
import Input from '../components/Input.vue'
import Button from '../components/Button.vue'

const router = useRouter()
const { saveSession } = useAuth() as any
const form = ref({ fullName: '', email: '', password: '', confirmPassword: '', agreedToTerms: false })
const loading = ref(false)

async function handleSubmit() {
  if (!form.value.fullName || !form.value.email || !form.value.password) {
    toast.error('Please fill in all fields.')
    return
  }
  if (form.value.password !== form.value.confirmPassword) {
    toast.error('Passwords do not match.')
    return
  }
  if (!form.value.agreedToTerms) {
    toast.error('Please agree to the Terms of Service.')
    return
  }
  loading.value = true
  try {
    const { data } = await authApi.userRegister(form.value.fullName, form.value.email, form.value.password)
    localStorage.setItem('accessToken', data.accessToken)
    localStorage.setItem('refreshToken', data.refreshToken)
    localStorage.setItem('currentUser', JSON.stringify({
      email: data.email,
      fullName: data.fullName,
      role: data.role,
      plan: data.plan ?? 'FREE',
    }))
    toast.success('Account created successfully!')
    router.push('/dashboard')
  } catch (err: any) {
    toast.error(err.response?.data?.message || 'Registration failed. Please try again.')
  } finally {
    loading.value = false
  }
}
</script>
