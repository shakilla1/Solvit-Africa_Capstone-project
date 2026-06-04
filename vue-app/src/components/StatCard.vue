<template>
  <Card :leftBorderColor="borderColor" class="relative">
    <div class="absolute top-6 right-6 text-[#94a3b8]">
      <slot name="icon" />
    </div>
    <div class="space-y-1">
      <div class="text-3xl font-serif text-[#0f172a]" :style="{ fontFamily: 'var(--font-serif)' }">{{ value }}</div>
      <div class="text-sm text-[#94a3b8]">{{ label }}</div>
    </div>
  </Card>
</template>

<script setup lang="ts">
import Card from './Card.vue'
defineProps<{ label: string; value: string | number; borderColor: string }>()
</script>
