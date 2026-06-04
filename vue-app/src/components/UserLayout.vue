<template>
  <div class="bg-[#f8fafc]" style="min-height: 680px;">
    <nav class="bg-white border-b border-[#e2e8f0] sticky top-0 z-50">
      <div class="px-6 flex items-center justify-between" style="height: 52px;">
        <!-- Logo → home -->
        <RouterLink to="/" class="flex items-center gap-2 hover:opacity-80 transition-opacity">
          <Shield :size="20" class="text-[#2563eb]" />
          <span class="font-serif text-base text-[#0f172a]" :style="{ fontFamily: 'var(--font-serif)' }">
            PhishGuard <span class="text-[#2563eb]">Pro</span>
          </span>
        </RouterLink>

        <div class="flex items-center gap-0.5">
          <RouterLink
            v-for="item in navItems"
            :key="item.path"
            :to="item.path"
            :class="['px-3 py-1.5 rounded-lg text-xs font-medium transition-colors',
              route.path === item.path ? 'text-[#2563eb] bg-[#dbeafe]' : 'text-[#475569] hover:text-[#0f172a] hover:bg-[#f1f5f9]']"
          >
            {{ item.label }}
          </RouterLink>
        </div>

        <div class="flex items-center gap-2">
          <!-- Notifications -->
          <div class="relative">
            <button
              @click="showNotifications = !showNotifications; showUserMenu = false"
              class="p-1.5 hover:bg-[#f8fafc] rounded-lg transition-colors relative"
            >
              <Bell :size="17" class="text-[#475569]" />
              <span v-if="unreadCount > 0" class="absolute top-0.5 right-0.5 w-3.5 h-3.5 bg-[#dc2626] rounded-full text-white text-[8px] flex items-center justify-center font-bold">
                {{ unreadCount }}
              </span>
            </button>

            <div v-if="showNotifications" class="absolute right-0 top-full mt-1 w-72 bg-white border border-[#e2e8f0] rounded-xl shadow-xl z-50 overflow-hidden">
              <div class="flex items-center justify-between px-4 py-2.5 border-b border-[#e2e8f0]">
                <h3 class="text-sm font-semibold text-[#0f172a]">Notifications</h3>
                <button @click="markAllRead" class="text-xs text-[#2563eb] hover:underline">Mark all read</button>
              </div>
              <div class="max-h-60 overflow-y-auto">
                <div
                  v-for="(notif, idx) in notifications"
                  :key="idx"
                  :class="['flex items-start gap-3 px-4 py-2.5 border-b border-[#f1f5f9] last:border-0 cursor-pointer hover:bg-[#f8fafc]', !notif.read ? 'bg-[#f0f7ff]' : '']"
                  @click="notif.read = true"
                >
                  <div class="w-6 h-6 rounded-full flex items-center justify-center flex-shrink-0 mt-0.5" :style="{ backgroundColor: notif.color + '20' }">
                    <component :is="notif.icon" :size="12" :style="{ color: notif.color }" />
                  </div>
                  <div class="flex-1 min-w-0">
                    <p class="text-xs font-medium text-[#0f172a]">{{ notif.title }}</p>
                    <p class="text-xs text-[#475569]">{{ notif.message }}</p>
                    <p class="text-[10px] text-[#94a3b8] mt-0.5">{{ notif.time }}</p>
                  </div>
                  <div v-if="!notif.read" class="w-1.5 h-1.5 rounded-full bg-[#2563eb] flex-shrink-0 mt-2"></div>
                </div>
              </div>
            </div>
          </div>

          <!-- User menu -->
          <div class="relative">
            <button
              @click="showUserMenu = !showUserMenu; showNotifications = false"
              class="flex items-center gap-1.5 hover:bg-[#f8fafc] px-2 py-1.5 rounded-lg transition-colors"
            >
              <div class="w-7 h-7 rounded-full bg-[#2563eb] flex items-center justify-center text-xs font-semibold text-white">{{ userInitials }}</div>
              <span class="text-xs font-medium text-[#0f172a]">{{ userName }}</span>
              <ChevronDown :size="13" class="text-[#94a3b8]" />
            </button>
            <div v-if="showUserMenu" class="absolute right-0 top-full mt-1 w-44 bg-white border border-[#e2e8f0] rounded-lg shadow-lg py-1 z-50">
              <div class="px-4 py-2 border-b border-[#f1f5f9]">
                <p class="text-xs font-semibold text-[#0f172a]">{{ userName }}</p>
                <p class="text-xs text-[#94a3b8]">{{ currentUser.email }}</p>
              </div>
              <RouterLink to="/profile" class="flex items-center gap-2 px-4 py-2 text-sm text-[#475569] hover:bg-[#f8fafc]" @click="showUserMenu = false">
                <User :size="14" /> My Profile
              </RouterLink>
              <RouterLink to="/badges" class="flex items-center gap-2 px-4 py-2 text-sm text-[#475569] hover:bg-[#f8fafc]" @click="showUserMenu = false">
                <Award :size="14" /> My Badges
              </RouterLink>
              <button @click="handleLogout" class="flex items-center gap-2 w-full px-4 py-2 text-sm text-[#dc2626] hover:bg-[#fee2e2]">
                <LogOut :size="14" /> Logout
              </button>
            </div>
          </div>
        </div>
      </div>
    </nav>

    <!-- Back button bar for sub-pages -->
    <div v-if="showBackButton" class="bg-white border-b border-[#f1f5f9] px-6 py-2 flex items-center gap-2">
      <button @click="router.back()" class="flex items-center gap-1.5 text-xs text-[#475569] hover:text-[#2563eb] transition-colors">
        <ArrowLeft :size="14" /> Back
      </button>
      <span class="text-[#e2e8f0]">|</span>
      <span class="text-xs text-[#94a3b8]">{{ pageTitle }}</span>
    </div>

    <main class="px-6 py-5">
      <RouterView />
    </main>

    <!-- Overlay -->
    <div v-if="showNotifications || showUserMenu" class="fixed inset-0 z-40" @click="showNotifications = false; showUserMenu = false"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Shield, Bell, ChevronDown, LogOut, User, Award, ArrowLeft, Trophy, Target, BookOpen } from '@lucide/vue'

const route = useRoute()
const router = useRouter()
const showNotifications = ref(false)
const showUserMenu = ref(false)

const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}')
const userName = currentUser.fullName || 'User'
const userInitials = userName.split(' ').map((n: string) => n[0]).join('').toUpperCase().slice(0, 2)

const navItems = [
  { label: 'Dashboard', path: '/dashboard' },
  { label: 'Training', path: '/training' },
  { label: 'Leaderboard', path: '/leaderboard' },
  { label: 'My Badges', path: '/badges' },
  { label: 'Profile', path: '/profile' },
]

const showBackButton = computed(() =>
  route.path.startsWith('/training/') && route.params.id
)

const pageTitle = computed(() => {
  if (route.path.startsWith('/training/')) return 'Training Session'
  return ''
})

const notifications = ref([
  { icon: Target, color: '#d97706', title: 'New Campaign', message: 'You have been targeted in a new simulation', time: '1 hour ago', read: false },
  { icon: Trophy, color: '#2563eb', title: 'Badge Earned!', message: 'You earned the Security Aware badge', time: '1 day ago', read: false },
  { icon: BookOpen, color: '#16a34a', title: 'Training Available', message: 'New training module is ready for you', time: '2 days ago', read: true },
])

const unreadCount = computed(() => notifications.value.filter(n => !n.read).length)
function markAllRead() { notifications.value.forEach(n => n.read = true) }

function handleLogout() {
  localStorage.removeItem('accessToken')
  localStorage.removeItem('refreshToken')
  localStorage.removeItem('currentUser')
  router.push('/')
}
</script>
