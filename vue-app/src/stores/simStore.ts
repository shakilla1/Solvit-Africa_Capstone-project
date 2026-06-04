import { trackingApi } from '../api/index'

// Lightweight reactive store using localStorage — no Pinia needed
import { ref, watch } from 'vue'

export interface SimEvent {
  id: string
  type: 'click' | 'credential_submit'
  token: string
  campaignId: number
  campaignName: string
  targetEmail: string
  targetName: string
  timestamp: string
  ip: string
  device: string
  credentials?: { username: string; password: string }
  landingType?: string
}

export interface TrainingProgress {
  topicId: string
  topicTitle: string
  status: 'not_started' | 'in_progress' | 'completed'
  score: number | null
  completedAt: string | null
  answers: Record<number, number>
}

const EVENTS_KEY = 'phishguard_sim_events'
const PROGRESS_KEY = 'phishguard_training_progress'
const PENDING_CREDS_KEY = 'phishguard_pending_creds'

// ── Simulation Events ──────────────────────────────────────
function loadEvents(): SimEvent[] {
  try { return JSON.parse(localStorage.getItem(EVENTS_KEY) || '[]') } catch { return [] }
}

export const simEvents = ref<SimEvent[]>(loadEvents())

watch(simEvents, (val) => localStorage.setItem(EVENTS_KEY, JSON.stringify(val)), { deep: true })

export async function recordClick(token: string) {
   try {
     await trackingApi.recordClick(token)
   } catch (e) {
     console.warn('Could not record click on backend:', e)
   }
   const event: SimEvent = {
     id: crypto.randomUUID(),
     type: 'click',
     token,
     campaignId: 1,
     campaignName: 'Q2 Security Awareness',
     targetEmail: 'john@company.com',
     targetName: 'John Doe',
     timestamp: new Date().toISOString(),
     ip: '192.168.1.' + Math.floor(Math.random() * 254 + 1),
     device: getDevice(),
   }
   simEvents.value.unshift(event)
   return event
}

export async function recordCredentials(token: string, username: string, password: string, landingType: string) {
   try {
     await trackingApi.submitCredentials(token, { username, password, landingType })
   } catch (e) {
     console.warn('Could not submit credentials to backend:', e)
   }
   const event: SimEvent = {
     id: crypto.randomUUID(),
     type: 'credential_submit',
     token,
     campaignId: 1,
     campaignName: 'Q2 Security Awareness',
     targetEmail: 'john@company.com',
     targetName: 'John Doe',
     timestamp: new Date().toISOString(),
     ip: '192.168.1.' + Math.floor(Math.random() * 254 + 1),
     device: getDevice(),
     credentials: { username, password },
     landingType,
   }
   simEvents.value.unshift(event)
   // Store pending creds so ClickAwareness can show them
   localStorage.setItem(PENDING_CREDS_KEY, JSON.stringify({ username, password, landingType, token }))
   return event
}

export function getPendingCreds() {
  try { return JSON.parse(localStorage.getItem(PENDING_CREDS_KEY) || 'null') } catch { return null }
}

export function clearPendingCreds() {
  localStorage.removeItem(PENDING_CREDS_KEY)
}

// ── Training Progress ──────────────────────────────────────
function loadProgress(): TrainingProgress[] {
  try { return JSON.parse(localStorage.getItem(PROGRESS_KEY) || '[]') } catch { return [] }
}

export const trainingProgress = ref<TrainingProgress[]>(loadProgress())

watch(trainingProgress, (val) => localStorage.setItem(PROGRESS_KEY, JSON.stringify(val)), { deep: true })

export function getTopicProgress(topicId: string): TrainingProgress | undefined {
  return trainingProgress.value.find(p => p.topicId === topicId)
}

export function startTopic(topicId: string, topicTitle: string) {
  const existing = trainingProgress.value.find(p => p.topicId === topicId)
  if (!existing) {
    trainingProgress.value.push({ topicId, topicTitle, status: 'in_progress', score: null, completedAt: null, answers: {} })
  } else if (existing.status === 'not_started') {
    existing.status = 'in_progress'
  }
}

export function recordAnswer(topicId: string, questionIndex: number, answerIndex: number) {
  const p = trainingProgress.value.find(p => p.topicId === topicId)
  if (p) p.answers[questionIndex] = answerIndex
}

export function completeTopic(topicId: string, score: number) {
  const p = trainingProgress.value.find(p => p.topicId === topicId)
  if (p) {
    p.status = 'completed'
    p.score = score
    p.completedAt = new Date().toISOString()
  }
}

// ── Helpers ────────────────────────────────────────────────
function getDevice(): string {
  const ua = navigator.userAgent
  if (ua.includes('Chrome')) return 'Chrome on ' + (ua.includes('Windows') ? 'Windows' : ua.includes('Mac') ? 'macOS' : 'Linux')
  if (ua.includes('Firefox')) return 'Firefox on ' + (ua.includes('Windows') ? 'Windows' : 'Linux')
  if (ua.includes('Safari')) return 'Safari on macOS'
  return 'Unknown Browser'
}
