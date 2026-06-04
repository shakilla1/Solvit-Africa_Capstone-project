import axios from 'axios'
import { toast } from 'vue-sonner'

const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

export const api = axios.create({
  baseURL: BASE_URL,
  headers: { 'Content-Type': 'application/json' },
  timeout: 15000,
})

// ── Request interceptor: attach JWT ───────────────────────
api.interceptors.request.use(config => {
  const token = localStorage.getItem('accessToken')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// ── Response interceptor: handle errors + token refresh ───
let isRefreshing = false
let failedQueue: any[] = []

function processQueue(error: any, token: string | null = null) {
  failedQueue.forEach(p => error ? p.reject(error) : p.resolve(token))
  failedQueue = []
}

api.interceptors.response.use(
  res => res,
  async err => {
    const original = err.config

    // 402 = plan upgrade required
    if (err.response?.status === 402) {
      toast.error(err.response.data?.error || 'Upgrade your plan to access this feature', {
        action: { label: 'Upgrade', onClick: () => window.location.href = '/admin/subscription' }
      })
      return Promise.reject(err)
    }

    // 401 = try refresh token
    if (err.response?.status === 401 && !original._retry) {
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject })
        }).then(token => {
          original.headers.Authorization = `Bearer ${token}`
          return api(original)
        })
      }

      original._retry = true
      isRefreshing = true

      const refreshToken = localStorage.getItem('refreshToken')
      if (!refreshToken) {
        clearAuth()
        return Promise.reject(err)
      }

      try {
        const { data } = await axios.post(`${BASE_URL}/api/auth/refresh`, { refreshToken })
        localStorage.setItem('accessToken', data.accessToken)
        localStorage.setItem('refreshToken', data.refreshToken)
        api.defaults.headers.common.Authorization = `Bearer ${data.accessToken}`
        processQueue(null, data.accessToken)
        original.headers.Authorization = `Bearer ${data.accessToken}`
        return api(original)
      } catch (refreshErr) {
        processQueue(refreshErr, null)
        clearAuth()
        return Promise.reject(refreshErr)
      } finally {
        isRefreshing = false
      }
    }

    return Promise.reject(err)
  }
)

function clearAuth() {
  localStorage.removeItem('accessToken')
  localStorage.removeItem('refreshToken')
  localStorage.removeItem('currentUser')
  window.location.href = '/admin/login'
}

// ── Auth API ───────────────────────────────────────────────
export const authApi = {
  login: (email: string, password: string) =>
    api.post('/api/auth/admin/login', { email, password }),

  userLogin: (email: string, password: string) =>
    api.post('/api/auth/user/login', { email, password }),

  userRegister: (fullName: string, email: string, password: string) =>
    api.post('/api/auth/user/register', { fullName, email, password }),

  enableMfa: (email: string, code: string) =>
    api.post('/api/auth/admin/mfa/enable', { email, code }),

  verifyMfa: (email: string, code: string) =>
    api.post('/api/auth/admin/mfa/verify', { email, code }),

  logout: () => api.post('/api/auth/logout'),
}

// ── Campaign API ───────────────────────────────────────────
export const campaignApi = {
  list: ()                    => api.get('/api/admin/campaigns'),
  get: (id: number)           => api.get(`/api/admin/campaigns/${id}`),
  targets: (id: number)       => api.get(`/api/admin/campaigns/${id}/targets`),
  create: (data: any)         => api.post('/api/admin/campaigns', data),
  launch: (id: number)        => api.post(`/api/admin/campaigns/${id}/launch`),
  togglePause: (id: number)   => api.post(`/api/admin/campaigns/${id}/toggle-pause`),
  delete: (id: number)        => api.delete(`/api/admin/campaigns/${id}`),
}

// ── Template API ───────────────────────────────────────────
export const templateApi = {
  list: ()    => api.get('/api/admin/templates'),
  public: ()  => api.get('/api/templates/public'),
}

// ── Notification API ───────────────────────────────────────
export const notifApi = {
  adminList: (category?: string) =>
    api.get('/api/admin/notifications', { params: category ? { category } : {} }),
  adminCounts: ()              => api.get('/api/admin/notifications/counts'),
  adminMarkAllRead: ()         => api.post('/api/admin/notifications/mark-all-read'),
  adminMarkOneRead: (id: number) => api.post(`/api/admin/notifications/${id}/read`),

  userList: ()                 => api.get('/api/user/notifications'),
  userMarkAllRead: ()          => api.post('/api/user/notifications/mark-all-read'),
}

// ── Training API ───────────────────────────────────────────
export const trainingApi = {
  getProgress: ()                                    => api.get('/api/user/training/progress'),
  startTopic: (topicId: string, topicTitle: string)  =>
    api.post(`/api/user/training/topics/${topicId}/start`, { topicTitle }),
  submitQuiz: (topicId: string, answers: Record<number, number>) =>
    api.post(`/api/user/training/topics/${topicId}/submit`, { answers }),
}

// ── Tracking API (public — no auth) ───────────────────────
export const trackingApi = {
  recordClick: (token: string) =>
    axios.get(`${BASE_URL}/api/track/${token}`),
  submitCredentials: (token: string, data: { username: string; password: string; landingType: string }) =>
    axios.post(`${BASE_URL}/api/sim/${token}/submit`, data),
}

// ── Education Monitor (admin) ──────────────────────────────
export const educationApi = {
  getProgress: () => api.get('/api/admin/education/progress'),
}

// ── User API ───────────────────────────────────────────────
export const userApi = {
  me: ()                                              => api.get('/api/user/training/me'),
  leaderboard: ()                                     => api.get('/api/user/leaderboard'),
  events: ()                                          => api.get('/api/user/events'),
  changePassword: (currentPassword: string, newPassword: string) =>
    api.post('/api/user/change-password', { currentPassword, newPassword }),
}
