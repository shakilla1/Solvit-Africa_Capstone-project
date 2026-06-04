<template>
  <div class="min-h-screen bg-gradient-to-br from-[#0f172a] via-[#1e293b] to-[#334155] flex flex-col items-center justify-center p-6 overflow-auto">
    <!-- Laptop -->
    <div class="w-full" style="max-width: 1100px;">
      <!-- Screen bezel -->
      <div class="bg-[#1a1a1a] rounded-t-2xl pt-4 px-4 pb-0 shadow-2xl border border-[#333]">
        <!-- Camera dot -->
        <div class="flex justify-center mb-2">
          <div class="w-2 h-2 rounded-full bg-[#333]"></div>
        </div>
        <!-- Screen -->
        <div class="bg-white rounded-t-lg overflow-hidden" style="height: 680px;">
          <div class="w-full h-full overflow-auto" style="height: 680px;">
            <RouterView />
          </div>
        </div>
      </div>
      <!-- Hinge -->
      <div class="h-1.5 bg-gradient-to-b from-[#2a2a2a] to-[#3a3a3a]"></div>
      <!-- Base -->
      <div class="bg-gradient-to-b from-[#d1d5db] to-[#9ca3af] rounded-b-xl h-5 shadow-lg"></div>
      <!-- Trackpad notch -->
      <div class="flex justify-center">
        <div class="w-48 h-1 bg-[#9ca3af] rounded-b-full"></div>
      </div>
    </div>

    <Toaster position="bottom-right" />
  </div>
</template>

<script setup lang="ts">
import { Toaster } from 'vue-sonner'
</script>
