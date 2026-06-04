<template>
  <div class="space-y-5">
    <div class="flex justify-between items-start">
      <div>
        <h2 class="text-xl font-semibold text-[#0f172a]">Security Training</h2>
        <p class="text-sm text-[#475569]">Complete all topics then take the Final Assessment</p>
      </div>
      <div class="text-right">
        <div class="text-xs text-[#94a3b8] mb-1">Overall Progress</div>
        <div class="flex items-center gap-2">
          <div class="w-32 h-2 bg-[#e2e8f0] rounded-full overflow-hidden">
            <div class="h-full bg-[#2563eb] rounded-full transition-all" :style="{ width: overallProgress + '%' }"></div>
          </div>
          <span class="text-xs font-semibold text-[#0f172a]">{{ completedCount }}/{{ topics.length }}</span>
        </div>
      </div>
    </div>

    <!-- Active session banner -->
    <Card v-if="activeSession" class="border-2 border-[#2563eb] bg-[#eff6ff]">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-full bg-[#dbeafe] flex items-center justify-center">
            <PlayCircle :size="20" class="text-[#2563eb]" />
          </div>
          <div>
            <p class="text-sm font-semibold text-[#0f172a]">Continue where you left off</p>
            <p class="text-xs text-[#475569]">{{ activeSession.topicTitle }} — in progress</p>
          </div>
        </div>
        <RouterLink :to="topicLink(activeSession.topicId)">
          <Button size="sm">Continue →</Button>
        </RouterLink>
      </div>
    </Card>

    <!-- Topics -->
    <div class="grid grid-cols-1 gap-3">
      <div v-for="(topic, idx) in topics" :key="topic.id"
        class="bg-white border border-[#e2e8f0] rounded-xl p-4 hover:shadow-sm transition-shadow">
        <div class="flex items-center gap-4">
          <div class="w-10 h-10 rounded-full flex items-center justify-center flex-shrink-0 text-sm font-bold"
            :class="getTopicStatusClass(topic.id)">
            <Check v-if="getProgress(topic.id)?.status === 'completed'" :size="18" />
            <span v-else>{{ idx + 1 }}</span>
          </div>
          <div class="w-9 h-9 rounded-lg flex items-center justify-center flex-shrink-0"
            :style="{ backgroundColor: topic.color + '15' }">
            <component :is="topic.icon" :size="18" :style="{ color: topic.color }" />
          </div>
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-2 mb-0.5">
              <h3 class="text-sm font-semibold text-[#0f172a]">{{ topic.title }}</h3>
              <Badge v-if="getProgress(topic.id)?.status === 'COMPLETED'" variant="green" class="text-xs">
                ✓ {{ getProgress(topic.id)?.score }}%
              </Badge>
              <Badge v-else-if="getProgress(topic.id)?.status === 'IN_PROGRESS'" variant="amber" class="text-xs">
                In Progress
              </Badge>
            </div>
            <p class="text-xs text-[#475569]">{{ topic.desc }}</p>
            <div class="flex items-center gap-3 mt-1">
              <span class="text-xs text-[#94a3b8]">{{ topic.duration }}</span>
              <span class="text-xs text-[#94a3b8]">·</span>
              <span class="text-xs text-[#94a3b8]">{{ topic.questions }} quiz questions</span>
            </div>
          </div>
          <RouterLink :to="topicLink(topic.id)">
            <Button :variant="getProgress(topic.id)?.status === 'completed' ? 'secondary' : 'primary'" size="sm">
              {{ getProgress(topic.id)?.status === 'completed' ? 'Review' : getProgress(topic.id)?.status === 'in_progress' ? 'Continue' : 'Start' }}
            </Button>
          </RouterLink>
        </div>
      </div>
    </div>

    <!-- Final Assessment Card -->
    <div class="rounded-2xl border-2 overflow-hidden transition-all"
      :class="allTopicsDone ? 'border-[#7c3aed]' : 'border-[#e2e8f0] opacity-60'">

      <!-- Locked state -->
      <div v-if="!allTopicsDone" class="bg-[#f8fafc] p-5 flex items-center gap-4">
        <div class="w-12 h-12 rounded-full bg-[#e2e8f0] flex items-center justify-center flex-shrink-0">
          <Lock :size="22" class="text-[#94a3b8]" />
        </div>
        <div class="flex-1">
          <h3 class="font-semibold text-[#94a3b8]">Final Assessment — Locked</h3>
          <p class="text-xs text-[#94a3b8] mt-0.5">Complete all {{ topics.length }} topics to unlock the Final Assessment</p>
          <div class="flex items-center gap-2 mt-2">
            <div class="flex-1 h-1.5 bg-[#e2e8f0] rounded-full overflow-hidden">
              <div class="h-full bg-[#7c3aed] rounded-full transition-all" :style="{ width: overallProgress + '%' }"></div>
            </div>
            <span class="text-xs text-[#94a3b8]">{{ completedCount }}/{{ topics.length }}</span>
          </div>
        </div>
      </div>

      <!-- Unlocked state -->
      <div v-else class="bg-gradient-to-r from-[#7c3aed] to-[#4f46e5] p-5">
        <div class="flex items-center gap-4">
          <div class="w-12 h-12 rounded-full bg-white/20 flex items-center justify-center flex-shrink-0">
            <Trophy :size="24" class="text-white" />
          </div>
          <div class="flex-1">
            <div class="flex items-center gap-2 mb-0.5">
              <h3 class="font-bold text-white">Final Assessment</h3>
              <span v-if="finalDone" class="px-2 py-0.5 bg-white/20 text-white rounded text-xs font-medium">
                ✓ {{ finalScore }}%
              </span>
              <span v-else class="px-2 py-0.5 bg-yellow-400/80 text-yellow-900 rounded text-xs font-medium">Unlocked!</span>
            </div>
            <p class="text-xs text-white/80">15 questions across all topics · Earn the Phishing Detective badge</p>
          </div>
          <RouterLink :to="topicLink('final-assessment')">
            <button class="px-4 py-2 bg-white text-[#7c3aed] rounded-lg text-sm font-bold hover:bg-white/90 transition-all">
              {{ finalDone ? 'Retake' : 'Start Now' }}
            </button>
          </RouterLink>
        </div>

        <!-- Badge earned -->
        <div v-if="finalDone && finalScore >= 70" class="mt-4 bg-white/10 rounded-xl p-3 flex items-center gap-3">
          <div class="w-10 h-10 rounded-full bg-yellow-400/20 flex items-center justify-center">
            <Award :size="20" class="text-yellow-300" />
          </div>
          <div>
            <p class="text-sm font-bold text-white">🏆 Phishing Detective Badge Earned!</p>
            <p class="text-xs text-white/70">You have mastered phishing awareness</p>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { PlayCircle, Check, Lock, Trophy, Award } from '@lucide/vue'
import Card from '../../components/Card.vue'
import Button from '../../components/Button.vue'
import Badge from '../../components/Badge.vue'
import { api } from '../../api'
import { topics } from '../../stores/topics'

const route = useRoute()
const token = route.query.token as string || ''
const progressList = ref<any[]>([])
const loading = ref(true)

const totalTopics = topics.length

function getProgress(topicId: string) {
  return progressList.value.find((p: any) => p.topicId === topicId) || {}
}

const completedCount = computed(() =>
  topics.filter(t => getProgress(t.id)?.status === 'COMPLETED').length
)

const overallProgress = computed(() =>
  Math.round((completedCount.value / totalTopics) * 100)
)

const allTopicsDone = computed(() => completedCount.value >= totalTopics)

const finalProgress = computed(() => getProgress('final-assessment'))
const finalDone = computed(() => finalProgress.value?.status === 'COMPLETED')
const finalScore = computed(() => finalProgress.value?.score ?? 0)

const activeSession = computed(() =>
  progressList.value.find((p: any) => p.status === 'IN_PROGRESS' && p.topicId !== 'final-assessment')
)

function getTopicStatusClass(topicId: string) {
  const p = getProgress(topicId)
  if (p?.status === 'COMPLETED') return 'bg-[#16a34a] text-white'
  if (p?.status === 'IN_PROGRESS') return 'bg-[#d97706] text-white'
  return 'bg-[#e2e8f0] text-[#94a3b8]'
}

function topicLink(topicId: string) {
  return `/training/${topicId}${token ? '?token=' + token : ''}`
}

onMounted(async () => {
  try {
    const { data } = await api.get('/api/user/training/me')
    progressList.value = data.progress || []
  } catch {
    progressList.value = []
  } finally {
    loading.value = false
  }
})
</script>
