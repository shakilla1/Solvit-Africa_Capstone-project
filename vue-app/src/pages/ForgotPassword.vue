<template>
  <div class="min-h-screen bg-[#f8fafc] flex items-center justify-center p-8">
    <Card class="w-full max-w-md">
      <div class="flex justify-center mb-6">
        <div class="w-16 h-16 rounded-full bg-[#dbeafe] flex items-center justify-center">
          <Shield :size="32" class="text-[#2563eb]" />
        </div>
      </div>
      <template v-if="!submitted">
        <h1 class="text-2xl font-serif text-center text-[#0f172a] mb-2" :style="{ fontFamily: 'var(--font-serif)' }">Reset your password</h1>
        <p class="text-center text-[#475569] mb-8">Enter your email and we'll send you a reset link</p>
        <form @submit.prevent="handleSubmit" class="space-y-5">
          <Input label="Email" type="email" placeholder="you@example.com" v-model="email" />
          <Button type="submit" class="w-full">Send Reset Link</Button>
          <div class="text-center text-sm">
            <RouterLink to="/admin/login" class="text-[#2563eb] hover:underline">Back to login</RouterLink>
          </div>
        </form>
      </template>
      <div v-else class="text-center">
        <div class="w-16 h-16 rounded-full bg-[#dcfce7] flex items-center justify-center mx-auto mb-4">
          <CheckCircle :size="32" class="text-[#16a34a]" />
        </div>
        <h2 class="text-xl font-medium text-[#0f172a] mb-2">Check your inbox</h2>
        <p class="text-[#475569] mb-6">We've sent a reset link to <strong>{{ email }}</strong></p>
        <RouterLink to="/admin/login"><Button variant="secondary">Back to login</Button></RouterLink>
      </div>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Shield, CheckCircle } from '@lucide/vue'
import Card from '../components/Card.vue'
import Input from '../components/Input.vue'
import Button from '../components/Button.vue'

const email = ref('')
const submitted = ref(false)

function handleSubmit() { submitted.value = true }
</script>
