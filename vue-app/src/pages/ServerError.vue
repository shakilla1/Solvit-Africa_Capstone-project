<template>
  <div class="min-h-full bg-[#f8fafc] flex items-center justify-center p-8">
    <div class="text-center max-w-sm">
      <div class="w-20 h-20 rounded-full bg-[#fee2e2] flex items-center justify-center mx-auto mb-4">
        <AlertTriangle :size="40" class="text-[#dc2626]" />
      </div>
      <h1 class="text-5xl font-serif text-[#0f172a] mb-3" :style="{ fontFamily: 'var(--font-serif)' }">500</h1>
      <h2 class="text-lg font-semibold text-[#0f172a] mb-2">Something Went Wrong</h2>
      <p class="text-sm text-[#475569] mb-6">Our team has been notified. Please try again in a moment.</p>
      <div class="flex gap-3 justify-center">
        <button @click="router.back()" class="flex items-center gap-1.5 px-4 py-2 text-sm border border-[#e2e8f0] rounded-lg text-[#475569] hover:bg-[#f8fafc] transition-colors">
          <ArrowLeft :size="15" /> Go Back
        </button>
        <Button @click="() => window.location.reload()">
          <template #icon><RefreshCw :size="15" /></template>
          Retry
        </Button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { AlertTriangle, ArrowLeft, RefreshCw } from '@lucide/vue'
import { useRouter } from 'vue-router'
import Button from '../components/Button.vue'
const router = useRouter()
</script>
