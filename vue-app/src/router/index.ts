import { createRouter, createWebHistory } from 'vue-router'

function isAdminLoggedIn() {
  return !!localStorage.getItem('accessToken') && (() => {
    try { const u = JSON.parse(localStorage.getItem('currentUser') || 'null'); return u?.role === 'ROLE_ADMIN' } catch { return false }
  })()
}

function isUserLoggedIn() {
  return !!localStorage.getItem('accessToken') && (() => {
    try { const u = JSON.parse(localStorage.getItem('currentUser') || 'null'); return u?.role !== 'ROLE_ADMIN' } catch { return true }
  })()
}

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: () => import('../pages/Landing.vue') },
    { path: '/admin/login', component: () => import('../pages/AdminLogin.vue') },
    { path: '/login', component: () => import('../pages/UserLogin.vue') },
    { path: '/register', component: () => import('../pages/UserRegister.vue') },
    { path: '/forgot-password', component: () => import('../pages/ForgotPassword.vue') },
    {
      path: '/admin',
      component: () => import('../components/AdminLayout.vue'),
      meta: { requiresAdmin: true },
      children: [
        { path: 'dashboard', component: () => import('../pages/admin/Dashboard.vue') },
        { path: 'campaigns', component: () => import('../pages/admin/Campaigns.vue') },
        { path: 'campaigns/new', component: () => import('../pages/admin/NewCampaign.vue') },
        { path: 'campaigns/:id', component: () => import('../pages/admin/CampaignDetail.vue') },
        { path: 'templates', component: () => import('../pages/admin/EmailTemplates.vue') },
        { path: 'landing-pages', component: () => import('../pages/admin/LandingPages.vue') },
        { path: 'analytics', component: () => import('../pages/admin/Analytics.vue') },
        { path: 'users', component: () => import('../pages/admin/TargetUsers.vue') },
        { path: 'users/:id', component: () => import('../pages/admin/UserProfile.vue') },
        { path: 'logs', component: () => import('../pages/admin/EventLogs.vue') },
        { path: 'reports', component: () => import('../pages/admin/Reports.vue') },
        { path: 'education', component: () => import('../pages/admin/EducationMonitor.vue') },
        { path: 'leaderboard', component: () => import('../pages/admin/AdminLeaderboard.vue') },
        { path: 'subscription', component: () => import('../pages/admin/Subscription.vue') },
        { path: 'settings', component: () => import('../pages/admin/Settings.vue') },
      ],
    },
    {
      path: '/',
      component: () => import('../components/UserLayout.vue'),
      meta: { requiresUser: true },
      children: [
        { path: 'dashboard', component: () => import('../pages/user/Dashboard.vue') },
        { path: 'training', component: () => import('../pages/user/Training.vue') },
        { path: 'training/intro', component: () => import('../pages/user/TrainingIntro.vue') },
        { path: 'training/:id', component: () => import('../pages/user/TrainingSession.vue') },
        { path: 'leaderboard', component: () => import('../pages/user/Leaderboard.vue') },
        { path: 'badges', component: () => import('../pages/user/Badges.vue') },
        { path: 'profile', component: () => import('../pages/user/Profile.vue') },
      ],
    },
    { path: '/track/:token', component: () => import('../pages/ClickAwareness.vue') },
    { path: '/sim/:token', component: () => import('../pages/FakeLanding.vue') },
    { path: '/403', component: () => import('../pages/Forbidden.vue') },
    { path: '/500', component: () => import('../pages/ServerError.vue') },
    { path: '/session-expired', component: () => import('../pages/SessionExpired.vue') },
    { path: '/:pathMatch(.*)*', component: () => import('../pages/NotFound.vue') },
  ],
})

router.beforeEach((to) => {
  if (to.meta.requiresAdmin && !isAdminLoggedIn()) return '/admin/login'
  if (to.meta.requiresUser && !isUserLoggedIn()) return '/login'
})

export default router
