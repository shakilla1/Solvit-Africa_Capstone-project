<template>
  <div class="min-h-full bg-[#f8fafc] flex items-center justify-center p-8">
    <div class="text-center max-w-sm">
      <div class="w-20 h-20 rounded-full bg-[#fee2e2] flex items-center justify-center mx-auto mb-4">
        <AlertCircle :size="40" class="text-[#dc2626]" />
      </div>
      <h1 class="text-7xl font-serif text-[#0f172a] leading-none mb-3" :style="{ fontFamily: 'var(--font-serif)' }">404</h1>
      <h2 class="text-lg font-semibold text-[#0f172a] mb-2">Page Not Found</h2>
      <p class="text-sm text-[#475569] mb-6">This page doesn't exist or has been moved.</p>
      <div class="flex gap-3 justify-center">
        <button @click="router.back()" class="flex items-center gap-1.5 px-4 py-2 text-sm border border-[#e2e8f0] rounded-lg text-[#475569] hover:bg-[#f8fafc] transition-colors">
          <ArrowLeft :size="15" /> Go Back
        </button>
        <RouterLink to="/">
          <Button><template #icon><Home :size="15" /></template>Go Home</Button>
        </RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { AlertCircle, ArrowLeft, Home } from '@lucide/vue'
import { useRouter } from 'vue-router'
import Button from '../components/Button.vue'
const router = useRouter()
</script>
