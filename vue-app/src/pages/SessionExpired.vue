<template>
  <div class="min-h-full bg-[#f8fafc] flex items-center justify-center p-8">
    <div class="text-center max-w-sm">
      <div class="w-20 h-20 rounded-full bg-[#dbeafe] flex items-center justify-center mx-auto mb-4 relative">
        <Lock :size="36" class="text-[#2563eb]" />
        <Clock :size="18" class="text-[#2563eb] absolute bottom-3 right-2" />
      </div>
      <h2 class="text-lg font-semibold text-[#0f172a] mb-2">Session Expired</h2>
      <p class="text-sm text-[#475569] mb-6">For your security, sessions expire after 30 minutes of inactivity.</p>
      <div class="flex gap-3 justify-center">
        <RouterLink to="/">
          <button class="flex items-center gap-1.5 px-4 py-2 text-sm border border-[#e2e8f0] rounded-lg text-[#475569] hover:bg-[#f8fafc] transition-colors">
            <Home :size="15" /> Go Home
          </button>
        </RouterLink>
        <RouterLink to="/admin/login"><Button>Sign In Again</Button></RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Lock, Clock, Home } from '@lucide/vue'
import Button from '../components/Button.vue'
</script>
