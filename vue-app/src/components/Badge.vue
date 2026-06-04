<template>
  <span :class="badgeClass"><slot /></span>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(defineProps<{
  variant?: 'blue' | 'green' | 'amber' | 'red' | 'grey'
  class?: string
}>(), { variant: 'grey' })

const variantStyles: Record<string, string> = {
  blue: 'bg-[#dbeafe] text-[#2563eb]',
  green: 'bg-[#dcfce7] text-[#16a34a]',
  amber: 'bg-[#fef3c7] text-[#d97706]',
  red: 'bg-[#fee2e2] text-[#dc2626]',
  grey: 'bg-[#f1f5f9] text-[#475569]',
}

const badgeClass = computed(() => {
  const base = 'inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium'
  return [base, variantStyles[props.variant], props.class || ''].filter(Boolean).join(' ')
})
</script>
