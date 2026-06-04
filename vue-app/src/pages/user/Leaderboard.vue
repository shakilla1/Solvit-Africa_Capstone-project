<template>
  <div class="space-y-6">
    <div>
      <div class="flex items-center gap-3 mb-2">
        <h2 class="text-2xl font-serif text-[#0f172a]" :style="{ fontFamily: 'var(--font-serif)' }">Organisation Leaderboard</h2>
        <Badge variant="blue">Updated in real-time</Badge>
      </div>
      <p class="text-[#475569]">See how you rank against your colleagues</p>
    </div>

    <!-- Podium -->
    <div class="flex items-end justify-center gap-4 mb-8">
      <Card class="w-48 text-center pb-8">
        <div class="w-20 h-20 rounded-full bg-[#cbd5e1] flex items-center justify-center text-2xl font-medium text-white mx-auto mb-3">{{ leaderboard[1].avatar }}</div>
        <Medal :size="32" class="text-[#cbd5e1] mx-auto mb-2" />
        <div class="text-lg font-medium text-[#0f172a] mb-1">{{ leaderboard[1].name }}</div>
        <div class="text-2xl font-serif text-[#2563eb] mb-1" :style="{ fontFamily: 'var(--font-serif)' }">{{ leaderboard[1].points }}</div>
        <Badge variant="grey">{{ leaderboard[1].badge }}</Badge>
      </Card>
      <Card class="w-48 text-center pb-8 -mt-8">
        <div class="w-24 h-24 rounded-full bg-[#d97706] flex items-center justify-center text-2xl font-medium text-white mx-auto mb-3">{{ leaderboard[0].avatar }}</div>
        <Trophy :size="40" class="text-[#d97706] mx-auto mb-2" />
        <div class="text-lg font-medium text-[#0f172a] mb-1">{{ leaderboard[0].name }}</div>
        <div class="text-3xl font-serif text-[#2563eb] mb-1" :style="{ fontFamily: 'var(--font-serif)' }">{{ leaderboard[0].points }}</div>
        <Badge variant="blue">{{ leaderboard[0].badge }}</Badge>
      </Card>
      <Card class="w-48 text-center pb-8">
        <div class="w-20 h-20 rounded-full bg-[#92400e] flex items-center justify-center text-2xl font-medium text-white mx-auto mb-3">{{ leaderboard[2].avatar }}</div>
        <Award :size="32" class="text-[#92400e] mx-auto mb-2" />
        <div class="text-lg font-medium text-[#0f172a] mb-1">{{ leaderboard[2].name }}</div>
        <div class="text-2xl font-serif text-[#2563eb] mb-1" :style="{ fontFamily: 'var(--font-serif)' }">{{ leaderboard[2].points }}</div>
        <Badge variant="grey">{{ leaderboard[2].badge }}</Badge>
      </Card>
    </div>

    <!-- Full Rankings -->
    <Card class="p-0 overflow-hidden">
      <div class="p-6 border-b border-[#e2e8f0]"><h3 class="text-lg font-medium text-[#0f172a]">Full Rankings</h3></div>
      <table class="w-full">
        <thead class="bg-[#f8fafc] border-b border-[#e2e8f0]">
          <tr>
            <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">Rank</th>
            <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">User</th>
            <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">Department</th>
            <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">Total Points</th>
            <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">Badge</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-[#e2e8f0]">
          <tr v-for="user in leaderboard" :key="user.id" :class="user.current ? 'bg-[#dbeafe]/30' : 'hover:bg-[#f8fafc]'">
            <td class="px-6 py-4">
              <div class="flex items-center gap-2">
                <span class="text-lg font-medium text-[#0f172a]">#{{ user.rank }}</span>
                <Trophy v-if="user.rank === 1" :size="16" class="text-[#d97706]" />
                <Medal v-else-if="user.rank === 2" :size="16" class="text-[#cbd5e1]" />
                <Award v-else-if="user.rank === 3" :size="16" class="text-[#92400e]" />
              </div>
            </td>
            <td class="px-6 py-4">
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-full bg-[#2563eb] flex items-center justify-center text-sm font-medium text-white">{{ user.avatar }}</div>
                <div class="font-medium text-[#0f172a]">{{ user.name }} <span v-if="user.current" class="text-[#2563eb]">(You)</span></div>
              </div>
            </td>
            <td class="px-6 py-4 text-[#475569]">{{ user.dept }}</td>
            <td class="px-6 py-4 text-lg font-medium text-[#0f172a]">{{ user.points }}</td>
            <td class="px-6 py-4"><Badge :variant="user.badge === 'Phishing Detective' ? 'blue' : 'grey'">{{ user.badge }}</Badge></td>
          </tr>
        </tbody>
      </table>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Trophy, Medal, Award } from '@lucide/vue'
import Card from '../../components/Card.vue'
import Badge from '../../components/Badge.vue'
import { userApi } from '../../api'

const leaderboard = ref<any[]>([])
const loading = ref(true)

onMounted(async () => {
  try {
    const { data } = await userApi.leaderboard()
    leaderboard.value = data
  } catch {
    leaderboard.value = []
  } finally {
    loading.value = false
  }
})
</script>
