<template>
  <div class="space-y-6">
    <div>
      <h2 class="text-2xl font-serif text-[#0f172a]" :style="{ fontFamily: 'var(--font-serif)' }">My Achievements</h2>
      <p class="text-[#475569]">Track your security awareness badges</p>
    </div>

    <div>
      <h3 class="text-lg font-medium text-[#0f172a] mb-4">Earned Badges</h3>
      <div class="grid grid-cols-3 gap-6">
        <Card v-for="(badge, idx) in earnedBadges" :key="idx">
          <div class="w-24 h-24 rounded-full flex items-center justify-center mx-auto mb-4" :style="{ backgroundColor: badge.color + '20' }">
            <Award :size="48" :style="{ color: badge.color }" />
          </div>
          <h3 class="text-lg font-medium text-[#0f172a] text-center mb-1">{{ badge.name }}</h3>
          <p class="text-sm text-[#475569] text-center mb-3">{{ badge.desc }}</p>
          <div class="text-xs text-[#94a3b8] text-center mb-2">Earned on {{ badge.date }}</div>
          <div class="text-xs text-[#94a3b8] text-center mb-3">Campaign: {{ badge.campaign }}</div>
          <div class="text-center mb-4"><Badge variant="blue">Score: {{ badge.score }}</Badge></div>
          <Button variant="secondary" size="sm" class="w-full"><template #icon><Download :size="14" /></template>Share</Button>
        </Card>
      </div>
    </div>

    <div>
      <h3 class="text-lg font-medium text-[#0f172a] mb-4">Locked Badges</h3>
      <div class="grid grid-cols-3 gap-6">
        <Card v-for="(badge, idx) in lockedBadges" :key="idx" class="opacity-60">
          <div class="relative w-24 h-24 rounded-full flex items-center justify-center mx-auto mb-4" :style="{ backgroundColor: badge.color + '20' }">
            <Award :size="48" :style="{ color: badge.color }" />
            <div class="absolute inset-0 flex items-center justify-center bg-black/50 rounded-full">
              <Lock :size="32" class="text-white" />
            </div>
          </div>
          <h3 class="text-lg font-medium text-[#0f172a] text-center mb-1">{{ badge.name }}</h3>
          <p class="text-sm text-[#475569] text-center mb-3">{{ badge.desc }}</p>
          <div class="text-center"><Badge variant="grey">Progress: {{ badge.progress }}</Badge></div>
        </Card>
      </div>
    </div>

    <Card>
      <div class="flex items-center gap-6">
        <div class="w-32 h-32 bg-[#f8fafc] rounded-lg flex items-center justify-center relative">
          <Award :size="48" class="text-[#94a3b8]" />
          <div class="absolute inset-0 flex items-center justify-center bg-black/50 rounded-lg">
            <Lock :size="32" class="text-white" />
          </div>
        </div>
        <div class="flex-1">
          <h3 class="text-xl font-medium text-[#0f172a] mb-2">Security Awareness Certificate</h3>
          <p class="text-[#475569] mb-4">Pass 3 campaigns without clicking any link to earn your certificate.</p>
          <Badge variant="grey">Progress: 1/3 campaigns</Badge>
        </div>
      </div>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { Award, Lock, Download } from '@lucide/vue'
import Card from '../../components/Card.vue'
import Button from '../../components/Button.vue'
import Badge from '../../components/Badge.vue'

const earnedBadges = [
  { name: 'Phishing Novice', desc: 'First training completed', date: 'Apr 15, 2025', campaign: 'IT Department Test', score: 65, color: '#92400e' },
  { name: 'Security Aware', desc: 'Scored above 70%', date: 'May 20, 2025', campaign: 'Q2 Security Awareness', score: 85, color: '#cbd5e1' },
]

const lockedBadges = [
  { name: 'Phishing Detective', desc: 'Scored above 90%, passed 3 campaigns', progress: '2/3', color: '#d97706' },
  { name: 'Perfect Score', desc: 'Score 100% in a session', progress: 'Not yet achieved', color: '#2563eb' },
  { name: 'Streak Master', desc: 'Pass 5 campaigns without falling', progress: '0/5', color: '#16a34a' },
]
</script>
