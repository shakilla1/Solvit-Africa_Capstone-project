<template>
  <div class="flex bg-[#f8fafc]" style="height: 680px;">
    <!-- Sidebar -->
    <aside class="w-56 bg-[#0f172a] text-white flex flex-col flex-shrink-0" style="height: 680px;">
      <!-- Logo → goes to landing / -->
      <div class="p-5 border-b border-[#1e293b]">
        <RouterLink to="/" class="flex items-center gap-2 hover:opacity-80 transition-opacity">
          <Shield :size="22" />
          <span class="font-serif text-lg" :style="{ fontFamily: 'var(--font-serif)' }">
            PhishGuard <span class="text-[#2563eb]">Pro</span>
          </span>
        </RouterLink>
      </div>

      <nav class="flex-1 overflow-y-auto py-3 px-2">
        <div v-for="section in navItems" :key="section.section" class="mb-4">
          <div class="px-3 mb-1.5 text-[10px] font-semibold text-[#64748b] uppercase tracking-wider">{{ section.section }}</div>
          <div class="space-y-0.5">
            <RouterLink
              v-for="item in section.items"
              :key="item.path"
              :to="item.path"
              :class="['flex items-center gap-2.5 px-3 py-2 rounded-lg transition-colors text-sm',
                isActive(item.path) ? 'bg-[#2563eb] text-white' : 'text-[#cbd5e1] hover:bg-[rgba(37,99,235,0.15)] hover:text-white']"
            >
              <component :is="item.icon" :size="17" />
              <span>{{ item.label }}</span>
            </RouterLink>
          </div>
        </div>
      </nav>

      <div class="p-3 border-t border-[#1e293b]">
        <div class="flex items-center gap-2.5 mb-2.5">
          <div class="w-8 h-8 rounded-full bg-[#2563eb] flex items-center justify-center text-xs font-semibold">AD</div>
          <div class="flex-1 min-w-0">
            <div class="text-sm font-medium truncate">Admin User</div>
            <div class="text-xs text-[#64748b]">Administrator</div>
          </div>
        </div>
        <RouterLink to="/admin/settings" class="w-full flex items-center gap-2 px-3 py-1.5 text-sm text-[#cbd5e1] hover:bg-[rgba(37,99,235,0.15)] rounded-lg transition-colors mb-1">
          <Settings :size="15" /> Settings
        </RouterLink>
        <button
          @click="handleLogout"
          class="w-full flex items-center gap-2 px-3 py-1.5 text-sm text-[#cbd5e1] hover:bg-[rgba(37,99,235,0.15)] rounded-lg transition-colors"
        >
          <LogOut :size="15" /> Logout
        </button>
      </div>
    </aside>

    <!-- Main Content -->
    <div class="flex-1 flex flex-col min-w-0" style="height: 680px; overflow: hidden;">
      <!-- Top Header -->
      <header class="bg-white border-b border-[#e2e8f0] flex items-center justify-between px-6 flex-shrink-0" style="height: 52px;">
        <div class="flex items-center gap-3">
          <!-- Back button on sub-pages -->
          <button
            v-if="showBackButton"
            @click="router.back()"
            class="flex items-center gap-1.5 text-sm text-[#475569] hover:text-[#0f172a] transition-colors mr-1"
          >
            <ArrowLeft :size="16" />
            <span class="text-xs">Back</span>
          </button>
          <!-- Breadcrumb -->
          <div class="flex items-center gap-1.5 text-sm">
            <span v-for="(crumb, idx) in breadcrumbs" :key="idx" class="flex items-center gap-1.5">
              <RouterLink v-if="crumb.path" :to="crumb.path" class="text-[#475569] hover:text-[#2563eb] transition-colors text-xs">{{ crumb.label }}</RouterLink>
              <span v-else class="text-[#0f172a] font-semibold text-xs">{{ crumb.label }}</span>
              <ChevronRight v-if="idx < breadcrumbs.length - 1" :size="12" class="text-[#94a3b8]" />
            </span>
          </div>
        </div>

        <div class="flex items-center gap-2">
          <!-- Search -->
          <div class="relative">
            <button
              @click="showSearch = !showSearch"
              class="p-1.5 hover:bg-[#f8fafc] rounded-lg transition-colors"
              title="Search"
            >
              <Search :size="17" class="text-[#475569]" />
            </button>
            <div v-if="showSearch" class="absolute right-0 top-full mt-1 w-64 bg-white border border-[#e2e8f0] rounded-lg shadow-lg p-2 z-50">
              <input
                v-model="searchQuery"
                placeholder="Search pages..."
                class="w-full px-3 py-2 text-sm border border-[#e2e8f0] rounded-lg focus:outline-none focus:border-[#2563eb]"
                autofocus
              />
              <div v-if="searchResults.length > 0" class="mt-1">
                <RouterLink
                  v-for="result in searchResults"
                  :key="result.path"
                  :to="result.path"
                  @click="showSearch = false; searchQuery = ''"
                  class="flex items-center gap-2 px-3 py-2 text-sm text-[#475569] hover:bg-[#f8fafc] rounded-lg"
                >
                  <component :is="result.icon" :size="14" class="text-[#94a3b8]" />
                  {{ result.label }}
                </RouterLink>
              </div>
              <p v-else-if="searchQuery" class="text-xs text-[#94a3b8] px-3 py-2">No results found</p>
            </div>
          </div>

          <!-- Notifications -->
          <div class="relative">
            <button
              @click="showNotifications = !showNotifications; showSearch = false"
              class="p-1.5 hover:bg-[#f8fafc] rounded-lg transition-colors relative"
              title="Notifications"
            >
              <Bell :size="17" class="text-[#475569]" />
              <span v-if="unreadCount > 0" class="absolute top-0.5 right-0.5 w-4 h-4 bg-[#dc2626] rounded-full text-white text-[9px] flex items-center justify-center font-bold">
                {{ unreadCount }}
              </span>
            </button>

            <!-- Notification Panel -->
            <div v-if="showNotifications" class="absolute right-0 top-full mt-1 w-80 bg-white border border-[#e2e8f0] rounded-xl shadow-xl z-50 overflow-hidden">
              <div class="flex items-center justify-between px-4 py-3 border-b border-[#e2e8f0]">
                <h3 class="text-sm font-semibold text-[#0f172a]">Notifications</h3>
                <button @click="markAllRead" class="text-xs text-[#2563eb] hover:underline">Mark all read</button>
              </div>
              <div class="max-h-72 overflow-y-auto">
                <div
                  v-for="(notif, idx) in notifications"
                  :key="idx"
                  :class="['flex items-start gap-3 px-4 py-3 border-b border-[#f1f5f9] last:border-0 cursor-pointer hover:bg-[#f8fafc] transition-colors', !notif.read ? 'bg-[#f0f7ff]' : '']"
                  @click="notif.read = true"
                >
                  <div class="w-7 h-7 rounded-full flex items-center justify-center flex-shrink-0 mt-0.5" :style="{ backgroundColor: notif.color + '20' }">
                    <component :is="notif.icon" :size="14" :style="{ color: notif.color }" />
                  </div>
                  <div class="flex-1 min-w-0">
                    <p class="text-xs font-medium text-[#0f172a]">{{ notif.title }}</p>
                    <p class="text-xs text-[#475569] mt-0.5">{{ notif.message }}</p>
                    <p class="text-[10px] text-[#94a3b8] mt-1">{{ notif.time }}</p>
                  </div>
                  <div v-if="!notif.read" class="w-2 h-2 rounded-full bg-[#2563eb] flex-shrink-0 mt-1.5"></div>
                </div>
              </div>
              <div class="px-4 py-2 border-t border-[#e2e8f0]">
                <RouterLink to="/admin/logs" @click="showNotifications = false" class="text-xs text-[#2563eb] hover:underline">View all event logs →</RouterLink>
              </div>
            </div>
          </div>

          <!-- Avatar -->
          <div class="relative">
            <button @click="showUserMenu = !showUserMenu; showNotifications = false; showSearch = false"
              class="flex items-center gap-1.5 hover:bg-[#f8fafc] px-2 py-1 rounded-lg transition-colors">
              <div class="w-7 h-7 rounded-full bg-[#2563eb] flex items-center justify-center text-xs font-semibold text-white">AD</div>
              <ChevronDown :size="13" class="text-[#94a3b8]" />
            </button>
            <div v-if="showUserMenu" class="absolute right-0 top-full mt-1 w-44 bg-white border border-[#e2e8f0] rounded-lg shadow-lg py-1 z-50">
              <div class="px-4 py-2 border-b border-[#f1f5f9]">
                <p class="text-xs font-semibold text-[#0f172a]">Admin User</p>
                <p class="text-xs text-[#94a3b8]">admin@company.com</p>
              </div>
              <RouterLink to="/admin/settings" class="flex items-center gap-2 px-4 py-2 text-sm text-[#475569] hover:bg-[#f8fafc]" @click="showUserMenu = false">
                <Settings :size="14" /> Settings
              </RouterLink>
              <button @click="handleLogout" class="flex items-center gap-2 w-full px-4 py-2 text-sm text-[#dc2626] hover:bg-[#fee2e2]">
                <LogOut :size="14" /> Logout
              </button>
            </div>
          </div>
        </div>
      </header>

      <!-- Page Content -->
      <main class="flex-1 overflow-y-auto p-5" @click="closeAllPanels">
        <RouterView />
      </main>
    </div>

    <!-- Overlay to close panels -->
    <div v-if="showNotifications || showSearch || showUserMenu" class="fixed inset-0 z-40" @click="closeAllPanels"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  Shield, Bell, Search, LogOut, Settings, ArrowLeft, ChevronRight, ChevronDown,
  LayoutDashboard, Target, Mail, Monitor,
  BarChart2, Users, Activity,
  GraduationCap, Trophy, FileText,
  CreditCard, MousePointer, AlertTriangle, CheckCircle
} from '@lucide/vue'

const route = useRoute()
const router = useRouter()

const showNotifications = ref(false)
const showSearch = ref(false)
const showUserMenu = ref(false)
const searchQuery = ref('')

function closeAllPanels() {
  showNotifications.value = false
  showSearch.value = false
  showUserMenu.value = false
}

function handleLogout() {
  localStorage.removeItem('accessToken')
  localStorage.removeItem('refreshToken')
  localStorage.removeItem('currentUser')
  router.push('/')
}

// ── Nav items ──────────────────────────────────────────────
const navItems = [
  { section: 'MAIN', items: [
    { icon: LayoutDashboard, label: 'Overview', path: '/admin/dashboard' },
    { icon: Target, label: 'Campaigns', path: '/admin/campaigns' },
    { icon: Mail, label: 'Email Templates', path: '/admin/templates' },
    { icon: Monitor, label: 'Landing Pages', path: '/admin/landing-pages' },
  ]},
  { section: 'ANALYTICS', items: [
    { icon: BarChart2, label: 'Analytics', path: '/admin/analytics' },
    { icon: Users, label: 'Target Users', path: '/admin/users' },
    { icon: Activity, label: 'Event Logs', path: '/admin/logs' },
  ]},
  { section: 'PLATFORM', items: [
    { icon: GraduationCap, label: 'Education Monitor', path: '/admin/education' },
    { icon: Trophy, label: 'Leaderboard', path: '/admin/leaderboard' },
    { icon: FileText, label: 'Reports', path: '/admin/reports' },
  ]},
  { section: 'ACCOUNT', items: [
    { icon: CreditCard, label: 'Subscription', path: '/admin/subscription' },
    { icon: Settings, label: 'Settings', path: '/admin/settings' },
  ]},
]

// All searchable pages
const allPages = navItems.flatMap(s => s.items)
const searchResults = computed(() => {
  if (!searchQuery.value.trim()) return []
  return allPages.filter(p => p.label.toLowerCase().includes(searchQuery.value.toLowerCase())).slice(0, 6)
})

// ── Active link ────────────────────────────────────────────
const isActive = (path: string) => {
  if (path === '/admin/dashboard') return route.path === path
  return route.path.startsWith(path)
}

// ── Breadcrumbs ────────────────────────────────────────────
const breadcrumbs = computed(() => {
  const path = route.path
  const crumbs: { label: string; path?: string }[] = [{ label: 'Admin', path: '/admin/dashboard' }]
  if (path.includes('/campaigns/new')) {
    crumbs.push({ label: 'Campaigns', path: '/admin/campaigns' }, { label: 'New Campaign' })
  } else if (path.match(/\/campaigns\/\d+/)) {
    crumbs.push({ label: 'Campaigns', path: '/admin/campaigns' }, { label: 'Campaign Details' })
  } else if (path.includes('/campaigns')) {
    crumbs.push({ label: 'Campaigns' })
  } else if (path.includes('/templates')) {
    crumbs.push({ label: 'Email Templates' })
  } else if (path.includes('/landing-pages')) {
    crumbs.push({ label: 'Landing Pages' })
  } else if (path.includes('/analytics')) {
    crumbs.push({ label: 'Analytics' })
  } else if (path.match(/\/users\/\d+/)) {
    crumbs.push({ label: 'Target Users', path: '/admin/users' }, { label: 'User Profile' })
  } else if (path.includes('/users')) {
    crumbs.push({ label: 'Target Users' })
  } else if (path.includes('/logs')) {
    crumbs.push({ label: 'Event Logs' })
  } else if (path.includes('/reports')) {
    crumbs.push({ label: 'Reports' })
  } else if (path.includes('/subscription')) {
    crumbs.push({ label: 'Subscription' })
  } else if (path.includes('/settings')) {
    crumbs.push({ label: 'Settings' })
  } else if (path.includes('/education')) {
    crumbs.push({ label: 'Education Monitor' })
  } else if (path.includes('/leaderboard')) {
    crumbs.push({ label: 'Leaderboard' })
  } else {
    crumbs.push({ label: 'Overview' })
  }
  return crumbs
})

// Show back button on detail/sub pages
const showBackButton = computed(() =>
  route.path.match(/\/campaigns\/\d+/) ||
  route.path.match(/\/users\/\d+/) ||
  route.path.includes('/campaigns/new')
)

// ── Notifications ──────────────────────────────────────────
const notifications = ref([
  { icon: MousePointer, color: '#2563eb', title: 'Link Clicked', message: 'John Doe clicked a link in Q2 Awareness', time: '2 mins ago', read: false },
  { icon: AlertTriangle, color: '#dc2626', title: 'Credentials Submitted', message: 'Jane Smith submitted credentials', time: '5 mins ago', read: false },
  { icon: CheckCircle, color: '#16a34a', title: 'Training Completed', message: 'Bob Wilson completed education module', time: '12 mins ago', read: false },
  { icon: Target, color: '#d97706', title: 'Campaign Launched', message: 'Q2 Security Awareness is now active', time: '1 hour ago', read: true },
  { icon: Users, color: '#2563eb', title: 'New Target Added', message: '3 new users added to target list', time: '2 hours ago', read: true },
])

const unreadCount = computed(() => notifications.value.filter(n => !n.read).length)
function markAllRead() { notifications.value.forEach(n => n.read = true) }
</script>
