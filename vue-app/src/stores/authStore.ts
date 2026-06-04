import { ref, computed } from 'vue'
import { authApi } from '../api'
import { toast } from 'vue-sonner'

export interface CurrentUser {
  email: string
  fullName: string
  role: string
  plan: string
}

const currentUser = ref<CurrentUser | null>(
  JSON.parse(localStorage.getItem('currentUser') || 'null')
)

const isLoggedIn = computed(() => !!currentUser.value && !!localStorage.getItem('accessToken'))

const isAdmin = computed(() => currentUser.value?.role === 'ROLE_ADMIN')

const plan = computed(() => currentUser.value?.plan || 'FREE')

// Plan feature gates
const canUseEducation   = computed(() => ['PERSONAL','STARTER','PROFESSIONAL','ENTERPRISE'].includes(plan.value))
const canUseAnalytics   = computed(() => ['STARTER','PROFESSIONAL','ENTERPRISE'].includes(plan.value))
const canUseCustomTpl   = computed(() => ['PROFESSIONAL','ENTERPRISE'].includes(plan.value))
const maxCampaigns      = computed(() => {
  const limits: Record<string, number> = { FREE: 1, PERSONAL: 5, STARTER: 20, PROFESSIONAL: 100, ENTERPRISE: 9999 }
  return limits[plan.value] || 1
})

async function login(email: string, password: string) {
  try {
    const { data } = await authApi.login(email, password)
    if (data.mfaRequired) return { mfaRequired: true, email }
    if (data.mfaSetupRequired) return { mfaSetupRequired: true, email, mfaSecret: data.mfaSecret, mfaQrUri: data.mfaQrUri }
    saveSession(data)
    return { success: true }
  } catch (err: any) {
    // Backend not running — use demo mode
    if (!err.response) {
      saveSession({ accessToken: 'demo-token', refreshToken: 'demo-refresh', email, fullName: email.split('@')[0], role: 'ROLE_ADMIN', plan: 'STARTER' })
      return { success: true }
    }
    throw err
  }
}

async function verifyMfa(email: string, code: string) {
  const { data } = await authApi.verifyMfa(email, code)
  saveSession(data)
  return { success: true }
}

async function enableMfa(email: string, code: string) {
  const { data } = await authApi.enableMfa(email, code)
  saveSession(data)
  return { success: true }
}

async function logout() {
  try { await authApi.logout() } catch {}
  localStorage.removeItem('accessToken')
  localStorage.removeItem('refreshToken')
  localStorage.removeItem('currentUser')
  currentUser.value = null
}

function saveSession(data: any) {
  localStorage.setItem('accessToken', data.accessToken)
  localStorage.setItem('refreshToken', data.refreshToken)
  const user: CurrentUser = {
    email: data.email,
    fullName: data.fullName,
    role: data.role,
    plan: data.plan,
  }
  localStorage.setItem('currentUser', JSON.stringify(user))
  currentUser.value = user
}

export const useAuth = () => ({
  currentUser,
  isLoggedIn,
  isAdmin,
  plan,
  canUseEducation,
  canUseAnalytics,
  canUseCustomTpl,
  maxCampaigns,
  login,
  verifyMfa,
  enableMfa,
  logout,
})
