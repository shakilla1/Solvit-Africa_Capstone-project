<template>
  <button :class="classes" v-bind="$attrs">
    <slot name="icon" />
    <slot />
  </button>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(defineProps<{
  variant?: 'primary' | 'secondary' | 'danger'
  size?: 'sm' | 'md' | 'lg'
}>(), { variant: 'primary', size: 'md' })

const variantStyles = {
  primary: 'bg-[#2563eb] text-white hover:bg-[#1d4ed8] hover:scale-[1.01] hover:shadow-md',
  secondary: 'bg-white text-[#0f172a] border border-[#e2e8f0] hover:border-[#cbd5e1] hover:shadow-sm',
  danger: 'bg-[#dc2626] text-white hover:bg-[#b91c1c] hover:scale-[1.01]',
}
const sizeStyles = { sm: 'px-3 py-1.5 text-sm', md: 'px-6 py-3', lg: 'px-8 py-4 text-lg' }

const classes = computed(() =>
  `inline-flex items-center justify-center gap-2 rounded-lg transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed ${variantStyles[props.variant]} ${sizeStyles[props.size]}`
)
</script>
