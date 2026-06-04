<template>
  <div :class="cardClass" :style="leftBorderColor ? { borderLeftColor: leftBorderColor } : {}">
    <slot />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{ leftBorderColor?: string; class?: string }>()

const cardClass = computed(() => {
  const base = 'bg-white border border-[#e2e8f0] rounded-[10px] p-6 shadow-[0_1px_3px_rgba(0,0,0,0.08),0_4px_16px_rgba(0,0,0,0.04)]'
  const border = props.leftBorderColor ? 'border-l-4' : ''
  const extra = props.class || ''
  return [base, border, extra].filter(Boolean).join(' ')
})
</script>
