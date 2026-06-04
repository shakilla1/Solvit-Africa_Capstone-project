<template>
  <div class="space-y-6">
    <Card>
      <div class="flex items-start gap-6">
        <div class="w-20 h-20 rounded-full bg-[#2563eb] flex items-center justify-center text-2xl text-white font-medium">JD</div>
        <div class="flex-1">
          <h2 class="text-2xl font-serif text-[#0f172a] mb-1" :style="{ fontFamily: 'var(--font-serif)' }">John Doe</h2>
          <p class="text-[#475569] mb-2">john@company.com • Sales Department</p>
          <div class="flex items-center gap-3">
            <span class="text-sm text-[#94a3b8]">Member since May 2024</span>
            <div class="flex items-center gap-2">
              <span class="text-sm text-[#94a3b8]">Risk Score:</span>
              <span class="text-lg font-medium text-[#dc2626]">85</span>
            </div>
          </div>
        </div>
      </div>
    </Card>

    <div class="grid grid-cols-4 gap-6">
      <StatCard label="Campaigns Targeted" value="5" borderColor="#2563eb"><template #icon><Target :size="24" /></template></StatCard>
      <StatCard label="Times Clicked" value="4" borderColor="#d97706"><template #icon><MousePointer :size="24" /></template></StatCard>
      <StatCard label="Times Submitted" value="3" borderColor="#dc2626"><template #icon><LogIn :size="24" /></template></StatCard>
      <StatCard label="Education Sessions" value="3" borderColor="#16a34a"><template #icon><GraduationCap :size="24" /></template></StatCard>
    </div>

    <Card>
      <h3 class="text-lg font-medium text-[#0f172a] mb-4">Campaign History</h3>
      <div class="space-y-3">
        <div v-for="(item, idx) in history" :key="idx" class="flex items-center justify-between p-4 bg-[#f8fafc] rounded-lg">
          <div>
            <div class="font-medium text-[#0f172a] mb-1">{{ item.campaign }}</div>
            <div class="text-sm text-[#94a3b8]">{{ item.date }}</div>
          </div>
          <div class="flex items-center gap-4">
            <div class="text-right">
              <div class="text-xs text-[#94a3b8] mb-1">Result</div>
              <div class="flex gap-2">
                <Badge :variant="item.clicked ? 'red' : 'green'">{{ item.clicked ? 'Clicked' : 'Safe' }}</Badge>
                <Badge v-if="item.submitted" variant="red">Submitted</Badge>
              </div>
            </div>
            <div class="text-right">
              <div class="text-xs text-[#94a3b8] mb-1">Score</div>
              <div class="text-lg font-medium text-[#0f172a]">{{ item.score }}</div>
            </div>
          </div>
        </div>
      </div>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { Target, MousePointer, LogIn, GraduationCap } from '@lucide/vue'
import Card from '../../components/Card.vue'
import StatCard from '../../components/StatCard.vue'
import Badge from '../../components/Badge.vue'

const history = [
  { campaign: 'Q2 Security Awareness', date: 'May 15, 2025', clicked: true, submitted: true, score: 65 },
  { campaign: 'IT Department Test', date: 'Apr 10, 2025', clicked: true, submitted: false, score: 85 },
  { campaign: 'New Hire Training', date: 'Mar 5, 2025', clicked: false, submitted: false, score: 100 },
]
</script>
