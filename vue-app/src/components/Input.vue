<template>
  <div class="flex flex-col gap-1.5">
    <label v-if="label" class="text-sm font-medium text-[#0f172a]">{{ label }}</label>
    <div class="relative">
      <input
        v-bind="$attrs"
        :type="isPassword && showPassword ? 'text' : type"
        :value="modelValue"
        @input="$emit('update:modelValue', ($event.target as HTMLInputElement).value)"
        :class="`w-full px-4 py-3 bg-white border rounded-lg transition-all duration-200 outline-none placeholder:text-[#94a3b8] ${error ? 'border-[#dc2626] focus:ring-2 focus:ring-[#dc2626]/20' : 'border-[#e2e8f0] focus:border-[#2563eb] focus:ring-2 focus:ring-[#2563eb]/20'}`"
      />
      <button
        v-if="isPassword"
        type="button"
        @click="showPassword = !showPassword"
        class="absolute right-3 top-1/2 -translate-y-1/2 text-[#94a3b8] hover:text-[#475569]"
      >
        <EyeOff v-if="showPassword" :size="20" />
        <Eye v-else :size="20" />
      </button>
    </div>
    <p v-if="error" class="text-sm text-[#dc2626]">{{ error }}</p>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Eye, EyeOff } from '@lucide/vue'

const props = defineProps<{ label?: string; error?: string; type?: string; modelValue?: string }>()
defineEmits(['update:modelValue'])

const showPassword = ref(false)
const isPassword = computed(() => props.type === 'password')
</script>
