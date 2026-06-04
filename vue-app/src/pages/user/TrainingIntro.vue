<template>
  <div class="min-h-full bg-gradient-to-b from-[#0f172a] to-[#1e293b] flex items-center justify-center p-6">
    <div class="w-full max-w-2xl">
      <!-- Logo -->
      <div class="text-center mb-6">
        <div class="w-16 h-16 rounded-2xl bg-[#2563eb] flex items-center justify-center mx-auto mb-3">
          <Shield :size="32" class="text-white" />
        </div>
        <h1 class="text-2xl font-serif text-white" :style="{ fontFamily: 'var(--font-serif)' }">
          PhishGuard <span class="text-[#60a5fa]">Security Training</span>
        </h1>
        <p class="text-sm text-[#94a3b8] mt-1">You were targeted in a phishing simulation. Let's turn that into a learning moment.</p>
      </div>

      <!-- What happened card -->
      <div class="bg-[#1e293b] border border-[#334155] rounded-2xl p-5 mb-4">
        <div class="flex items-center gap-2 mb-3">
          <div class="w-6 h-6 rounded-full bg-[#dc2626]/20 flex items-center justify-center">
            <AlertTriangle :size="13" class="text-[#dc2626]" />
          </div>
          <h2 class="text-sm font-semibold text-white">What just happened?</h2>
        </div>
        <p class="text-xs text-[#94a3b8] leading-relaxed">
          You clicked on a simulated phishing link sent by your organisation's security team.
          This is a <span class="text-white font-medium">controlled exercise</span> — no real harm was done.
          The goal is to help you recognise and avoid real phishing attacks in the future.
        </p>
      </div>

      <!-- How training works -->
      <div class="bg-[#1e293b] border border-[#334155] rounded-2xl p-5 mb-5">
        <h2 class="text-sm font-semibold text-white mb-4">How this training works</h2>
        <div class="space-y-3">
          <div v-for="(step, idx) in trainingSteps" :key="idx" class="flex items-start gap-3">
            <div class="w-7 h-7 rounded-full flex items-center justify-center flex-shrink-0 text-xs font-bold text-white"
              :style="{ backgroundColor: step.color }">
              {{ idx + 1 }}
            </div>
            <div>
              <p class="text-xs font-semibold text-white">{{ step.title }}</p>
              <p class="text-xs text-[#64748b]">{{ step.desc }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Topics preview -->
      <div class="bg-[#1e293b] border border-[#334155] rounded-2xl p-5 mb-5">
        <h2 class="text-sm font-semibold text-white mb-3">What you'll learn</h2>
        <div class="grid grid-cols-3 gap-2">
          <div v-for="topic in topics" :key="topic.id"
            class="bg-[#0f172a] rounded-lg p-3 border border-[#334155]">
            <div class="w-7 h-7 rounded-lg flex items-center justify-center mb-2" :style="{ backgroundColor: topic.color + '20' }">
              <component :is="topic.icon" :size="14" :style="{ color: topic.color }" />
            </div>
            <p class="text-xs font-medium text-white">{{ topic.title }}</p>
            <p class="text-[10px] text-[#64748b] mt-0.5">{{ topic.duration }}</p>
          </div>
        </div>
      </div>

      <!-- Stats -->
      <div class="grid grid-cols-3 gap-3 mb-5">
        <div class="bg-[#1e293b] border border-[#334155] rounded-xl p-3 text-center">
          <div class="text-xl font-serif text-[#60a5fa]" :style="{ fontFamily: 'var(--font-serif)' }">~15 min</div>
          <div class="text-[10px] text-[#64748b]">Total time</div>
        </div>
        <div class="bg-[#1e293b] border border-[#334155] rounded-xl p-3 text-center">
          <div class="text-xl font-serif text-[#60a5fa]" :style="{ fontFamily: 'var(--font-serif)' }">{{ topics.length }} topics</div>
          <div class="text-[10px] text-[#64748b]">With quizzes</div>
        </div>
        <div class="bg-[#1e293b] border border-[#334155] rounded-xl p-3 text-center">
          <div class="text-xl font-serif text-[#60a5fa]" :style="{ fontFamily: 'var(--font-serif)' }">1 badge</div>
          <div class="text-[10px] text-[#64748b]">On completion</div>
        </div>
      </div>

      <Button class="w-full" size="lg" @click="router.push('/training' + (token ? '?token=' + token : ''))">
        <template #icon><GraduationCap :size="18" /></template>
        Get Started — Begin Training
      </Button>
      <div class="text-center mt-3">
        <RouterLink to="/dashboard" class="text-xs text-[#475569] hover:text-[#94a3b8]">
          I'll do this later
        </RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'
import { Shield, AlertTriangle, GraduationCap } from '@lucide/vue'
import Button from '../../components/Button.vue'
import { topics } from '../../stores/topics'

const router = useRouter()
const route = useRoute()
const token = route.query.token as string || ''

const trainingSteps = [
  { title: 'Read the lesson', desc: 'Short, focused content on one phishing concept at a time', color: '#2563eb' },
  { title: 'Take the quiz', desc: 'Test your understanding with scenario-based questions', color: '#d97706' },
  { title: 'Get your score', desc: 'Instant feedback with explanations for every answer', color: '#16a34a' },
  { title: 'Earn your badge', desc: 'Complete all topics to earn your Security Awareness badge', color: '#7c3aed' },
]
</script>
