<template>
  <div class="min-h-full bg-[#f8fafc] flex items-center justify-center p-8">
    <div class="text-center max-w-sm">
      <div class="w-20 h-20 rounded-full bg-[#fef3c7] flex items-center justify-center mx-auto mb-4">
        <Lock :size="40" class="text-[#d97706]" />
      </div>
      <h1 class="text-5xl font-serif text-[#0f172a] mb-3" :style="{ fontFamily: 'var(--font-serif)' }">403</h1>
      <h2 class="text-lg font-semibold text-[#0f172a] mb-2">Access Restricted</h2>
      <p class="text-sm text-[#475569] mb-2">This feature requires a Starter plan or above.</p>
      <div class="mb-6"><Badge variant="grey">Current Plan: Free</Badge></div>
      <div class="flex gap-3 justify-center">
        <button @click="router.back()" class="flex items-center gap-1.5 px-4 py-2 text-sm border border-[#e2e8f0] rounded-lg text-[#475569] hover:bg-[#f8fafc] transition-colors">
          <ArrowLeft :size="15" /> Go Back
        </button>
        <RouterLink to="/admin/subscription"><Button>Upgrade Now</Button></RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Lock, ArrowLeft } from '@lucide/vue'
import { useRouter } from 'vue-router'
import Button from '../components/Button.vue'
import Badge from '../components/Badge.vue'
const router = useRouter()
</script>
